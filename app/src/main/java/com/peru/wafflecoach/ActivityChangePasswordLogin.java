package com.peru.wafflecoach;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityChangePasswordLogin extends AppCompatActivity {

    EditText txtEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_login);
        txtEmail = findViewById(R.id.txtEmail);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        initBtnEnviar();
    }

    private void initBtnEnviar() {
        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(ActivityChangePasswordLogin.this, "Debe ingresar su correo electrónico", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ActivityChangePasswordLogin.this, "Correo no válido", Toast.LENGTH_SHORT).show();
                    return;
                }
                final la.neurometrics.cobi.ServiceRest serviceRest = new la.neurometrics.cobi.ServiceRest(ActivityChangePasswordLogin.this);
                serviceRest.showDialog("Enviando contraseña temporal...");
                JSONObject body = new JSONObject();
                try {

                    body.put("email", email);
                    serviceRest.post("password", body, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            serviceRest.hideDialog();
                            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityChangePasswordLogin.this, "Contraseña temporal enviada");
                            onBackPressed();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
