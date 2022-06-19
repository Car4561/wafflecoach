package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ActivityCheckout extends AppCompatActivity {

    JSONObject attributes;

    TextView txtPsychotherapist;
    TextView txtPromotionDescrip;

    TextView txtDate;
    TextView txtPatient;
    TextView txtPrice;
    TextView txtCard;
    Button btnPay;
    Button btnInputCode;
    LinearLayout layoutDiscount;
    LinearLayout layoutCodeInput;
    la.neurometrics.cobi.ServiceSession serviceSession;
    String idPsychotherapist;
    String idCard;
    String schedule;
    int discountVer = 0;

    String amount;
    String name;
    String description;
    String strPromotionDescription = "Disfruta el descuento!";
    String nameCoupon  = "";
    int dayOfWeek;
    private la.neurometrics.cobi.ServiceRest serviceRest;
    boolean isAutomaticDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Pagar cita");

        txtPsychotherapist = findViewById(R.id.txt_psychotherapist);
        txtDate = findViewById(R.id.txt_date);
        txtPatient = findViewById(R.id.txt_patient);
        txtPrice = findViewById(R.id.txt_price);
        txtCard = findViewById(R.id.txt_card);
        btnPay = findViewById(R.id.btn_pay);
        btnInputCode = findViewById(R.id.btnInputCode);
        layoutDiscount = findViewById(R.id.layoutDiscount);
        layoutCodeInput = findViewById(R.id.layoutInputCode);
        txtPromotionDescrip = findViewById(R.id.txtPromotionDescrip);

        serviceSession = new la.neurometrics.cobi.ServiceSession(ActivityCheckout.this);
        serviceRest = new la.neurometrics.cobi.ServiceRest(ActivityCheckout.this);
        if(!getIntent().hasExtra("user")
                || !getIntent().hasExtra("date")
                || !getIntent().hasExtra("hour")
                || !getIntent().hasExtra("name")
                || !getIntent().hasExtra("description")
                || !getIntent().hasExtra("card")) {
            //sácalo de aquí
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityCheckout.this, "Datos insuficientes");
            onBackPressed();
        }


        initAppointmentDetails();
        initBtnPay();
        initBtnInputCode();


        verifyAutomaticDiscount(true);
    }

    private void verifyAutomaticDiscount(final Boolean simulated) {
        try {
            JSONObject body = new JSONObject();
            body.put("id", la.neurometrics.cobi.ServiceGlobal.getAlphaNumericString(10) +  serviceSession.get("id").toString());;
            JSONArray sources = new JSONArray();
            JSONObject source1 = new JSONObject();
            source1.put("rail","lightrail");
            source1.put("contactId",serviceSession.get("id"));
            sources.put(source1);
            body.put("sources",sources);
            JSONArray lineItems = new JSONArray();
            JSONObject item1 = new JSONObject();
            item1.put("productId","appointment");
            item1.put("unitPrice",Integer.parseInt(amount+"00"));
            item1.put("quantity",1);
            item1.put("taxrate",0);
            lineItems.put(item1);
            body.put("lineItems",lineItems);
            body.put("currency","PEN");
            body.put("allowRemainder",true);
            body.put("simulate",simulated);

            serviceRest.showDialog("Verificando código...");
            serviceRest.postLightrail("transactions/checkout", body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("lightrailResponseAuto",response.toString());
                    try {

                        JSONObject totals = response.getJSONObject("totals");
                        JSONArray steps = response.getJSONArray("steps");
                        JSONObject balanceRule = new JSONObject();
                        int discount = totals.getInt("discount")/100;
                        if(steps.length() > 0){
                            balanceRule = steps.getJSONObject(0).getJSONObject("balanceRule");
                            strPromotionDescription = balanceRule.getString("explanation");
                            nameCoupon = strPromotionDescription;
                        }
                        Log.d("description",strPromotionDescription);

                        Log.d("lightrailD", String.valueOf(discount));

                        if(discount > 0){
                            if(simulated) {
                                isAutomaticDiscount = true;
                                serviceRest.hideDialog();

                                setDiscount(discount,strPromotionDescription);
                                Log.d("lightrail", "Si hay descuento");
                            }else{
                                Log.d("lightrail", "Si hay descuento , transacción hecha ");
                            }
                        }else{
                            Log.d("lightrailS", String.valueOf(discount));

                            if(simulated) {
                                verifyCodeDiscount(true);
                                Log.d("lightrailS", String.valueOf(discount));
                                Log.d("lightrail", "No hay descuento");
                            }else{
                                Log.d("lightrail", "No hay descuento , transacción hecha");
                            }
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

    private void verifyCodeDiscount(final Boolean simulated) {
        if(serviceSession.getUserAttribute("discountCode") != null){
           if(!serviceSession.getUserAttribute("discountCode").equals("") ){
               Log.d("lightrail","entro pe gil");
             //  strPromotionDescription = serviceSession.getUserAttribute("descriptionDiscount");
              //
               try {
                   final String code =serviceSession.getUserAttribute("discountCode").trim();
                   if(code.equals("")){
                       serviceRest.hideDialog();
                       return;
                   }
                   JSONObject body = new JSONObject();
                   body.put("id", la.neurometrics.cobi.ServiceGlobal.getAlphaNumericString(10) +  serviceSession.get("id").toString());;
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
                   item1.put("unitPrice",Integer.parseInt(amount+"00"));
                   item1.put("quantity",1);
                   item1.put("taxrate",0);
                   lineItems.put(item1);
                   body.put("lineItems",lineItems);
                   body.put("currency","PEN");
                   body.put("allowRemainder",true);
                   body.put("simulate",simulated);

                   serviceRest.showDialog("Verificando código...");
                   serviceRest.postLightrail("transactions/checkout", body, new Response.Listener<JSONObject>() {
                       @Override
                       public void onResponse(JSONObject response) {
                           serviceRest.hideDialog();
                           Log.d("lightrailResponseCode",response.toString());
                           try {
                               JSONObject totals = response.getJSONObject("totals");
                               int discount = totals.getInt("discount")/100;
                               if (discount > 0) {
                                   if(simulated) {
                                           JSONArray steps = response.getJSONArray("steps");
                                           JSONObject balanceRule = new JSONObject();
                                           if (steps.length() > 0) {
                                               balanceRule = steps.getJSONObject(0).getJSONObject("balanceRule");
                                               strPromotionDescription = balanceRule.getString("explanation");
                                           }
                                           Log.d("lightrail", String.valueOf(discount));
                                           nameCoupon = response.getJSONArray("paymentSources").getJSONObject(0).getString("code");
                                           setDiscount(discount, strPromotionDescription);
                                       }else{
                                           JSONObject bodyPut = new JSONObject();
                                           JSONObject attributes1 = new JSONObject();
                                           attributes1.put("discountCode", "");
                                           //   at tributes1.put("descriptionDiscount","Disfruta del descuento!");
                                           bodyPut.put("attributes", attributes1);
                                           Log.d("lightrail", bodyPut.toString());
                                           ServiceUser serviceUser = new ServiceUser(ActivityCheckout.this);
                                           serviceUser.putNoMessage(bodyPut, false);
                                   }
                               }

                           } catch (JSONException e) {
                               e.printStackTrace();
                           }

                       }
                   });
               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }else{
                serviceRest.hideDialog();

           }

        }else{
            serviceRest.hideDialog();
        }
    }

    private void initBtnPay() {
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAppointment();
            }
        });
    }

    private void initBtnInputCode() {
        btnInputCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Ga");
                startActivityForResult(new Intent(getApplicationContext(), la.neurometrics.cobi.ActivityCode.class).putExtra("price",amount),1);
            }
        });
    }

    private void submitAppointment() {
        //continuar aquí

        final la.neurometrics.cobi.ServiceRest serviceRest = new la.neurometrics.cobi.ServiceRest(ActivityCheckout.this);
        serviceRest.showDialog("Reservando cita...");
        JSONObject bodyPost = new JSONObject();
        try {
            bodyPost.put("idPsychotherapist", idPsychotherapist);
            bodyPost.put("idCard", idCard);
            bodyPost.put("schedule", schedule);
            bodyPost.put("amount", amount);
            bodyPost.put("name", name);
            if(discountVer > 0){
                bodyPost.put("discount",discountVer);
            }

            if(!nameCoupon.equals("")){
                bodyPost.put("nameCoupon",nameCoupon);
            }
            JSONObject attributes = new JSONObject();
            attributes.put("name", name);
            attributes.put("description", description);

            bodyPost.put("attributes", attributes);
            if(dayOfWeek==0){
                dayOfWeek=7;
            }

            bodyPost.put("day",String.valueOf(dayOfWeek));
            bodyPost.put("backupday",String.valueOf(dayOfWeek));
            bodyPost.put("date", getIntent().getStringExtra("date"));
            bodyPost.put("hour", getIntent().getStringExtra("hour"));
            la.neurometrics.cobi.ServiceGlobal.console(bodyPost.toString());
            Log.d("day1234123", bodyPost.toString());

            serviceRest.post("appointment", bodyPost, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent intent = new Intent(ActivityCheckout.this, la.neurometrics.cobi.ActivityWrapper.class);
                    intent.putExtra("default", "appointment");
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                    serviceSession.setToSession("discount","0");

                    if(!isAutomaticDiscount) {

                        verifyCodeDiscount(false);
                    }else{
                        if(discountVer > 0) {
                            verifyAutomaticDiscount(false);
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof ServerError) {
                        serviceRest.hideDialog();
                        if(error.networkResponse.data != null) {
                            try {
                                String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                JSONObject objectError = new JSONObject(URLDecoder.decode(body,"UTF-8"));
                                //  "user_message": "Su tarjeta no tiene fondos suficientes. Para realizar la compra, verifica tus fondos disponibles con el banco emisor o inténta nuevamente con otra tarjeta."
                                la.neurometrics.cobi.ServiceGlobal.alerta(ActivityCheckout.this, new JSONObject(objectError.getString("message")).getString("user_message"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initAppointmentDetails() {

        idCard = getIntent().getStringExtra("card");
        schedule = getIntent().getStringExtra("date") + " " + getIntent().getStringExtra("hour") + ":00:00";
        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");
        dayOfWeek = getIntent().getExtras().getInt("day");
        if(dayOfWeek==0){
            dayOfWeek=7;
        }
        Log.d("day1234123", String.valueOf(dayOfWeek));

        String fullName = "";
        try {
            JSONObject psychotherapist = new JSONObject(getIntent().getStringExtra("user"));
            attributes = psychotherapist.getJSONObject("attributes");
            idPsychotherapist = psychotherapist.getString("id");
            amount = attributes.getString("price");
            fullName = attributes.getString("name") + " " + attributes.getString("surname");
         //   fullName = attributes.get("name").toString().split(" ")[0] + " " + attributes.get("surname").toString().split(" ")[0];

        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtPsychotherapist.setText(fullName);
        txtPrice.setText("S/ " + amount);
        btnPay.setText("Pagar S/ " + amount);
        txtDate.setText(la.neurometrics.cobi.ServiceGlobal.convertFormat(schedule));
        txtPatient.setText(getIntent().getStringExtra("name"));
        txtCard.setText(getIntent().getStringExtra("nameCard"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            int discount = data.getIntExtra("discount",0);
            strPromotionDescription = data.getStringExtra("descriptionDiscount");
            nameCoupon = data.getStringExtra("nameCoupon");
            Log.d("lightrail", String.valueOf(discount));
            if(discount != 0){
                setDiscount(discount, strPromotionDescription);
            }

        }
    }

    private void setDiscount(int discount, String description) {
        discountVer = discount;
        layoutCodeInput.setVisibility(View.VISIBLE);
        btnInputCode.setVisibility(View.GONE);
        layoutDiscount.setVisibility(View.VISIBLE);
        txtPromotionDescrip.setText(description);
        TextView lblCita = (TextView) layoutDiscount.getChildAt(1);
        TextView lblDiscount = (TextView) layoutDiscount.getChildAt(2);
        TextView lblTotal = (TextView) layoutDiscount.getChildAt(4);
        lblCita.setText( "Cita S/ "+ amount);
        lblDiscount.setText("Monto descontado S/ "+String.valueOf(discount));
        int rest = Integer.parseInt(amount) - discount;
        if(rest < 0){
            rest = 0;
        }
        lblTotal.setText("Total S/ "+String.valueOf(rest));
        this.amount = String.valueOf(rest);
        btnPay.setText("Pagar S/ " + amount);
        Log.d("lightrail",amount);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
