package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ActivityCode extends AppCompatActivity {

    Button btnVerifyCode;
    EditText txtCode;
    ServiceRest serviceRest;
    ServiceSession serviceSession;
    String priceLightrail;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        priceLightrail = Objects.requireNonNull(getIntent().getExtras()).getString("price");
        priceLightrail = priceLightrail + "00";
        serviceRest = new ServiceRest(ActivityCode.this);
        serviceSession = new ServiceSession(ActivityCode.this);

        txtCode = findViewById(R.id.txtCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);
        initBtnVerifyCode();

    }

    private void initBtnVerifyCode() {
        btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final String code = txtCode.getText().toString().trim();
                    if(code.equals("")){
                        Toast.makeText(ActivityCode.this, "Introduce un código", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject body = new JSONObject();
                    body.put("id",ServiceGlobal.getAlphaNumericString(10) +  serviceSession.get("id").toString());;
                    JSONArray sources = new JSONArray();
                    JSONObject source1 = new JSONObject();
                    source1.put("rail","lightrail");
                    source1.put("code",code);
                    JSONObject source2 = new JSONObject();
                    source2.put("rail","lightrail");
                    source2.put("contactId",serviceSession.get("id"));
                    sources.put(source1).put(source2);
                    body.put("sources",sources);
                    JSONArray lineItems = new JSONArray();
                    JSONObject item1 = new JSONObject();
                    item1.put("productId","appointment");
                    item1.put("unitPrice",Integer.valueOf(priceLightrail ));
                    item1.put("quantity",1);
                    item1.put("taxrate",0);
                    lineItems.put(item1);
                    body.put("lineItems",lineItems);
                    body.put("currency","PEN");
                    body.put("allowRemainder",true);
                        body.put("simulate",
                                true);

                    serviceRest.showDialog("Verificando código...");
                    serviceRest.postLightrail("transactions/checkout", body, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            serviceRest.hideDialog();
                            Log.d("lightrail",response.toString());
                            try {
                                JSONObject totals = response.getJSONObject("totals");
                                int discount = totals.getInt("discount")/100;
                                JSONArray steps = response.getJSONArray("steps");
                                JSONObject balanceRule = new JSONObject();

                                if(steps.length() > 0){
                                    balanceRule = steps.getJSONObject(0).getJSONObject("balanceRule");
                                     description = balanceRule.getString("explanation");
                                }
                                Log.d("lightrail", String.valueOf(discount));
                                if(discount == 0){
                                    Toast.makeText(ActivityCode.this, "Código no válido", Toast.LENGTH_SHORT).show();
                                }else{
                                    serviceSession.setToSession("descriptionDiscount",description);
                                    serviceSession.setToSession("discount",discount);
                                    serviceSession.setToSession("isDiscount",true);
                                    JSONObject bodyPut = new JSONObject();
                                    JSONObject attributes = new JSONObject();
                                    attributes.put("discountCode",code);
                                 //   attributes.put("descriptionDiscount",description);
                                    bodyPut.put("attributes", attributes);
                                    ServiceGlobal.console(bodyPut.toString());
                                    ServiceUser serviceUser = new ServiceUser(ActivityCode.this);
                                    serviceUser.putNoMessage(bodyPut, false);
                                    Intent intent = new Intent();
                                    intent.putExtra("discount",discount);
                                    intent.putExtra("descriptionDiscount",description);
                                    String nameCoupon = response.getJSONArray("paymentSources").getJSONObject(0).getString("code");

                                    intent.putExtra("nameCoupon",nameCoupon);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
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