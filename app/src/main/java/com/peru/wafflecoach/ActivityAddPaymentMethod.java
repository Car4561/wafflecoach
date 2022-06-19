package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


public class ActivityAddPaymentMethod extends AppCompatActivity {

    TextView kind_card;
    EditText txtNumber;
    EditText txtExpMonth;
    EditText txtExpYear;
    EditText txtCVV;
    Button btnSave;

    ServiceRest rest;
    Validation validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Agregar método de pago");

        kind_card = findViewById(R.id.textKind);
        txtNumber = findViewById(R.id.txtNumber);
        txtExpMonth = findViewById(R.id.txtExpMonth);
        txtExpYear = findViewById(R.id.txtExpYear);
        txtCVV = findViewById(R.id.txtCVV);
        btnSave = findViewById(R.id.btnSave);

        rest = new ServiceRest(this);
        validation = new Validation();

        initFields();
        initSubmit();
    }

    private void initFields() {
        txtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    txtCVV.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = txtNumber.getText().toString();
                if(s.length() == 0) {
                    //txtNumber.setBackgroundResource(R.drawable.border_error);
                }

                if(validation.luhn(text)) {
                    //txtNumber.setBackgroundResource(R.drawable.border_sucess);
                } else {
                    //txtNumber.setBackgroundResource(R.drawable.border_error);
                }

                int cvv = validation.bin(text, kind_card);
                if(cvv > 0) {
                    txtCVV.setEnabled(true);
                } else {
                    txtCVV.setEnabled(true);
                    txtCVV.setText("");
                }
            }
        });

        txtExpYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = txtExpYear.getText().toString();
                if(validation.year(text)){
                    //txtExpYear.setBackgroundResource(R.drawable.border_error);
                } else {
                    //txtExpYear.setBackgroundResource(R.drawable.border_sucess);
                }
            }
        });

        txtExpMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = txtExpMonth.getText().toString();
                if(validation.month(text)){
                    //txtExpMonth.setBackgroundResource(R.drawable.border_error);
                } else {
                    //txtExpMonth.setBackgroundResource(R.drawable.border_sucess);
                }
            }
        });
    }

    private void initSubmit() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtNumber.getText().toString().isEmpty() || txtExpMonth.getText().toString().isEmpty() || txtExpYear.getText().toString().isEmpty() || txtCVV.getText().toString().isEmpty() ){
                    Toast.makeText(ActivityAddPaymentMethod.this, "Debe ingresar todos los campos ", Toast.LENGTH_SHORT).show();
                    return;
                }
                ServiceSession session = new ServiceSession(ActivityAddPaymentMethod.this);
                rest.showDialog("Generando token...");
                Card card = new Card(txtNumber.getText().toString(), txtCVV.getText().toString(), Integer.parseInt(txtExpMonth.getText().toString()),Integer.parseInt("20" + txtExpYear.getText().toString()), session.getEmail());
                //Token token = new Token("sk_test_yXVtdMtTz76KR4VH");
                Token token = new Token("pk_live_MOiqT8OpqJC5WOFE");
                token.createToken(getApplicationContext(), card, new TokenCallback() {
                    @Override
                    public void onSuccess(JSONObject token) {
                        try {
                            submitCard(token);
                        } catch (Exception ex){
                            rest.hideDialog();
                        }

                    }

                    @Override
                    public void onError(VolleyError error) {
                        if (error instanceof ServerError) {
                            rest.hideDialog();
                            if(error.networkResponse.data != null) {
                                try {
                                    String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                    JSONObject objectError = new JSONObject(URLDecoder.decode(body,"UTF-8"));
                                    //  "user_message": "Su tarjeta no tiene fondos suficientes. Para realizar la compra, verifica tus fondos disponibles con el banco emisor o inténta nuevamente con otra tarjeta."
                                    ServiceGlobal.alerta(ActivityAddPaymentMethod.this,  new JSONObject(objectError.toString()).getString("user_message"));


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }                        rest.hideDialog();
                    }
                });

            }
        });
    }

    private void submitCard(JSONObject tokenCard) {
        JSONObject body = new JSONObject();
        try {
            body.put("tokenCard", tokenCard);
            rest.post("card", body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    rest.hideDialog();
                    Intent intent = new Intent();
                    intent.putExtra("done", true);
                    ServiceSession serviceSession = new ServiceSession(ActivityAddPaymentMethod.this);
                    try {
                        serviceSession.setSession(response.getString("token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    rest.hideDialog();
                    if(error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject objectError = new JSONObject(URLDecoder.decode(body,"UTF-8"));
                            //  "user_message": "Su tarjeta no tiene fondos suficientes. Para realizar la compra, verifica tus fondos disponibles con el banco emisor o inténta nuevamente con otra tarjeta."
                            ServiceGlobal.alerta(ActivityAddPaymentMethod.this,  new JSONObject(objectError.getString("message").toString()).getString("user_message"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
