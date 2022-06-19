package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityPaymentMethods extends AppCompatActivity {

    TextView textSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        textSub = findViewById(R.id.textSub);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Métodos de pago");

        initList();
        initBtnAdd();
    }

    private void initBtnAdd() {
        FloatingActionButton btnAdd = findViewById(R.id.btnAddCard);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityPaymentMethods.this, la.neurometrics.cobi.ActivityAddPaymentMethod.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                LinearLayout layout = findViewById(R.id.layoutPaymentMethod);
                layout.removeAllViews();
                initList();
                la.neurometrics.cobi.ServiceGlobal.alerta(ActivityPaymentMethods.this, "Método de pago agregado");
            }
        }
    }

    private void initList() {
        LinearLayout layout = findViewById(R.id.layoutPaymentMethod);

        la.neurometrics.cobi.ServiceSession session = new la.neurometrics.cobi.ServiceSession(this);
        ArrayList<HashMap> paymentMethod = session.getFromSession("paymentMethod");

        if(paymentMethod != null && paymentMethod.size() > 0){
            textSub.setText("Elige cómo quieres pagar");
            for(int i = 0; i < paymentMethod.size(); i++) {
                ConstraintLayout itemLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.item_payment_method, null);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                params.setMargins(0, 0, 0, 24);
                itemLayout.setLayoutParams(params);
                TextView txtName = itemLayout.findViewById(R.id.txtName);
                ImageView imgDefault = itemLayout.findViewById(R.id.imgDefault);
                ImageView imgDelete = itemLayout.findViewById(R.id.imgDelete);
                ImageView imgCard = itemLayout.findViewById(R.id.imageCard);

                HashMap card = paymentMethod.get(i);
                txtName.setText(card.get("name") + "");

                if(txtName.getText().toString().trim().contains("MasterCard")){
                    imgCard.setImageResource((R.mipmap.mastercard2));
                }
                else if(txtName.getText().toString().trim().contains("Visa")){
                    imgCard.setImageResource((R.mipmap.visa));
                }else if (txtName.getText().toString().trim().contains("Diners")){
                    imgCard.setImageResource((R.mipmap.diners));


                }else {
                    imgCard.setColorFilter(getResources().getColor(R.color.colorGray));
                }
                if(card.get("principal").equals("0")) {
                    imgDefault.setColorFilter(getResources().getColor(R.color.colorLightGray));
                    imgDefault.setAlpha(0.0f);
                    imgDelete.setVisibility(View.VISIBLE);
                }
                else{
                    imgDefault.setColorFilter(getResources().getColor(R.color.colorAccent));
                   // imgDelete.setVisibility(View.GONE);
                }
                final int position = i;

                itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        defaultCard(position);
                    }
                });

                imgDelete.setAlpha(0.5f);
                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeCard(position);
                    }
                });
                layout.addView(itemLayout);
            }
        }else{
            textSub.setText("Para hacer una reserva, Ud. debe de agregar al menos una tarjeta.");

        }
    }

    private void defaultCard(int position) {
        LinearLayout layout = findViewById(R.id.layoutPaymentMethod);
        for(int i = 0; i < layout.getChildCount(); i++) {
            ConstraintLayout itemLayout = (ConstraintLayout) layout.getChildAt(i);
            ImageView imgDefault = itemLayout.findViewById(R.id.imgDefault);
            ImageView imgDelete = itemLayout.findViewById(R.id.imgDelete);
            if(i == position) {
                imgDefault.setColorFilter(getResources().getColor(R.color.colorAccent));
                imgDefault.setAlpha(1.0f);
                imgDelete.setVisibility(View.GONE);
            }else{
                imgDefault.setColorFilter(getResources().getColor(R.color.colorLightGray));
                imgDefault.setAlpha(0.0f);
                imgDelete.setVisibility(View.VISIBLE);
            }
        }

        la.neurometrics.cobi.ServiceRest rest = new la.neurometrics.cobi.ServiceRest(this);
        JSONObject body = new JSONObject();
        try {
            la.neurometrics.cobi.ServiceSession session = new la.neurometrics.cobi.ServiceSession(this);
            ArrayList<HashMap> paymentMethod = session.getFromSession("paymentMethod");

            body.put("id", paymentMethod.get(position).get("id"));
            body.put("principal", "1");
            rest.put("card", body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    la.neurometrics.cobi.ServiceSession serviceSession = new la.neurometrics.cobi.ServiceSession(ActivityPaymentMethods.this);
                    try {
                        serviceSession.setSession(response.getString("token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeCard(int position) {

        final LinearLayout layout = findViewById(R.id.layoutPaymentMethod);
        layout.removeViewAt(position);

        la.neurometrics.cobi.ServiceRest rest = new la.neurometrics.cobi.ServiceRest(this);
        la.neurometrics.cobi.ServiceSession session = new la.neurometrics.cobi.ServiceSession(this);
        ArrayList<HashMap> paymentMethod = session.getFromSession("paymentMethod");

        rest.delete("card/" + paymentMethod.get(position).get("id"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                la.neurometrics.cobi.ServiceSession serviceSession = new la.neurometrics.cobi.ServiceSession(ActivityPaymentMethods.this);
                try {
                    serviceSession.setSession(response.getString("token"));
                    layout.removeAllViews();

                    initList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
