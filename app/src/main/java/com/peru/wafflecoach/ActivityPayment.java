package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityPayment extends AppCompatActivity {

    Button btnNext;
    String idCard = "";
    String nameCard = "";
    TextView textSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        textSub = findViewById(R.id.textSub);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Método de pago");

        btnNext = findViewById(R.id.btn_next);

        if(!getIntent().hasExtra("user")
                || !getIntent().hasExtra("date")
                || !getIntent().hasExtra("hour")
                || !getIntent().hasExtra("name")
                || !getIntent().hasExtra("description")) {
            //sácalo de aquí
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityPayment.this, "Datos insuficientes");
            onBackPressed();
        }

        initList();
        initBtnAdd();
        initBtnNext();

    }

    private void initBtnAdd() {
        FloatingActionButton btnAdd = findViewById(R.id.btnAddCard);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityPayment.this, la.neurometrics.cobi.ActivityAddPaymentMethod.class);
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
            }
        }
    }

    private void initList() {
        LinearLayout layout = findViewById(R.id.layoutPaymentMethod);

        ServiceSession session = new ServiceSession(this);
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
                ImageView imgCard = itemLayout.findViewById(R.id.imageCard);
                ImageView imgDelete = itemLayout.findViewById(R.id.imgDelete);
                imgDelete.setVisibility(View.GONE);

                HashMap card = paymentMethod.get(i);
                txtName.setText(card.get("name") + "");
                if(card.get("principal").equals("0")) {
                    imgDefault.setColorFilter(getResources().getColor(R.color.colorLightGray));
                    imgDefault.setAlpha(0.0f);
                }else{
                    imgDefault.setColorFilter(getResources().getColor(R.color.colorAccent));
                    idCard = (String) paymentMethod.get(i).get("id");
                    nameCard = (String) paymentMethod.get(i).get("name");
                }
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
                final int position = i;

                itemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectCard(position);
                    }
                });

                layout.addView(itemLayout);
            }
        }else{
            textSub.setText("Para hacer una reserva, Ud. debe de agregar al menos una tarjeta.");

        }
    }

    private void selectCard(int position) {
        LinearLayout layout = findViewById(R.id.layoutPaymentMethod);
        for(int i = 0; i < layout.getChildCount(); i++) {
            ConstraintLayout itemLayout = (ConstraintLayout) layout.getChildAt(i);
            ImageView imgDefault = itemLayout.findViewById(R.id.imgDefault);
            if(i == position) {
                imgDefault.setColorFilter(getResources().getColor(R.color.colorAccent));
                imgDefault.setAlpha(1.0f);
            }else{
                imgDefault.setColorFilter(getResources().getColor(R.color.colorLightGray));
                imgDefault.setAlpha(0.0f);
            }
        }

        ServiceSession session = new ServiceSession(this);
        ArrayList<HashMap> paymentMethod = session.getFromSession("paymentMethod");
        idCard = (String) paymentMethod.get(position).get("id");
        nameCard = (String) paymentMethod.get(position).get("name");

    }

    private void initBtnNext() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(idCard.equals("") || nameCard.equals("")) {
                    la.neurometrics.cobi.ServiceGlobal.alerta(ActivityPayment.this, "Debe seleccionar un método de pago válido");
                    return;
                }

                Intent intent = new Intent(ActivityPayment.this, ActivityCheckout.class);

                intent.putExtra("user", getIntent().getStringExtra("user"));
                intent.putExtra("date", getIntent().getStringExtra("date"));
                intent.putExtra("hour", getIntent().getStringExtra("hour"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("description", getIntent().getStringExtra("description"));
                intent.putExtra("card", idCard);
                intent.putExtra("nameCard", nameCard);
                
                int   dayOfWeek =  getIntent().getExtras().getInt("day");
                intent.putExtra("day",dayOfWeek);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
