package com.peru.wafflecoach;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Response;
import com.google.android.material.textfield.TextInputLayout;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;


public class ActivityLogin extends AppCompatActivity {

    private la.neurometrics.cobi.ServiceRest serviceRest;
    boolean login=false;
    MixpanelAPI mixpanel;
    int i ;
    public static final String MIXPANEL_TOKEN = "d49503cda477a5088678264293bbf55b";
    boolean pass=true;


    @Override
    protected void onResume() {
        super.onResume();
        final la.neurometrics.cobi.Version version = la.neurometrics.cobi.ClassSingleton.getInstance(getApplicationContext()).getVersion();
        if(version!=null) {
            if (!version.itsUpdate()) {
                new AlertDialog.Builder(ActivityLogin.this).setCancelable(false)
                        .setTitle(version.getTittleVersion()).setMessage(version.getDescriptionVersion())
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=la.neurometrics.cobi");
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent1);
                                i++;

                            }
                        }).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final la.neurometrics.cobi.Version version = la.neurometrics.cobi.ClassSingleton.getInstance(getApplicationContext()).getVersion();
        if(version != null) {
            if (!version.itsUpdate()) {
                new AlertDialog.Builder(ActivityLogin.this).setCancelable(false)
                        .setTitle(version.getTittleVersion()).setMessage(version.getDescriptionVersion())
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=la.neurometrics.cobi");
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent1);
                                i++;

                            }
                        }).show();
            }
        }
        ActionBar actionBar  = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

   
        initLogin();
        initBtnRegister();
        initBtnPassword();
    }

    private void initLogin() {
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitLogin();
            }
        });
    }

    private void submitLogin() {
        TextInputLayout txtEmail = findViewById(R.id.edi_email);
        TextInputLayout txtPass = findViewById(R.id.edi_pass);


        TextView lvlemail= findViewById(R.id.lbl_email);
        TextView lvlpass= findViewById(R.id.lbl_pass);


        String email = txtEmail.getEditText().getText().toString().trim();
        String password = txtPass.getEditText().getText().toString().trim();

        if(email.isEmpty()){
            //tvError1.setText("Debe ingresar su correo");
            lvlemail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));
            login=false;
        }else {
            lvlemail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
              //  tvError1.setText("Correo no válido");
                lvlemail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));

                login=false;
            }else{
                lvlemail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                login=true;
            }
        }

        if(password.isEmpty()){
        //    tvError2.setText("Debe ingresar su contraseña");
            lvlpass.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));

            return;
        }else {
            lvlpass.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
        if(!login){
            return;
        }
        serviceRest = new la.neurometrics.cobi.ServiceRest(this,this);
        serviceRest.showDialog("Iniciando...");
            try {
                JSONObject body = new JSONObject();
                body.put("email",email);
                body.put("pass", password);

                serviceRest.post("session", body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("token") && !response.getString("token").equals("")) {
                                String token = response.getString("token");
                                la.neurometrics.cobi.ServiceSession session = new la.neurometrics.cobi.ServiceSession(ActivityLogin.this);
                                session.login(token);
                                serviceRest.hideDialog();
                            }else{
                                la.neurometrics.cobi.ServiceGlobal.alerta(ActivityLogin.this, "No se recibió la sesión.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityLogin.this, "No se pudo iniciar sesión.");
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    private void initBtnRegister() {
        TextView btnOpenRegister = findViewById(R.id.txt_register);
        String sourceString = "Aún no tengo cuenta. <b>Registrarme</b>";
        btnOpenRegister.setText(Html.fromHtml(sourceString));
        btnOpenRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(intent);
            }
        });
    }

    private void initBtnPassword(){
        TextView btnPassowrd = findViewById(R.id.btnPassword);
        btnPassowrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ActivityChangePasswordLogin.class));
            }
        });
    }
}
