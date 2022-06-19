package com.peru.wafflecoach;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPassword extends AppCompatActivity {

    EditText txtCurrentPass;
    EditText txtNewPass;
    EditText txtConfirmedPass;
    Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cambiar contraseña");
        txtCurrentPass = findViewById(R.id.txt_current_pass);
        txtNewPass = findViewById(R.id.txt_new_pass);
        txtConfirmedPass = findViewById(R.id.txt_confirmed_pass);
        btnChange = findViewById(R.id.btn_change);

        initBtnChange();

    }

    private void initBtnChange() {
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtNewPass.getText().toString().equals("") && txtNewPass.getText().toString().equals(txtConfirmedPass.getText().toString())) {

                    try {
                        final ServiceRest serviceRest = new ServiceRest(ActivityPassword.this);
                        serviceRest.showDialog("Actualizando contraseña...");
                        JSONObject bodyPut = new JSONObject();
                        bodyPut.put("password", txtCurrentPass.getText().toString());
                        bodyPut.put("new_password", txtNewPass.getText().toString());
                        serviceRest.put("user", bodyPut, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                la.neurometrics.cobi.ServiceGlobal.alerta(ActivityPassword.this, "Contraseña actualizada");
                                serviceRest.hideDialog();
                                onBackPressed();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    la.neurometrics.cobi.ServiceGlobal.alerta(ActivityPassword.this, "Confirme correctamente la nueva contraseña.");
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
