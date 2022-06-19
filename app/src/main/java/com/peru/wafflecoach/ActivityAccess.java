package com.peru.wafflecoach;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class ActivityAccess extends AppCompatActivity {

    private CallbackManager callbackManager;
    private ServiceRest serviceRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);
        final String versionApp;
        final Version version = la.neurometrics.cobi.ClassSingleton.getInstance(getApplicationContext()).getVersion();
        if(version != null) {
            if (!version.itsUpdate()) {

                new AlertDialog.Builder(ActivityAccess.this).setCancelable(false)
                        .setTitle(version.getTittleVersion()).setMessage(version.getDescriptionVersion())
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=la.neurometrics.cobi");
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent1);

                            }
                        }).show();

            }
        }
        serviceRest = new ServiceRest(ActivityAccess.this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        initLoginFacebook();
        initBtnLogin();
        initTerms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Version version = la.neurometrics.cobi.ClassSingleton.getInstance(getApplicationContext()).getVersion();
        if(version!=null) {
            if (!version.itsUpdate()) {

                new AlertDialog.Builder(ActivityAccess.this).setCancelable(false)
                        .setTitle(version.getTittleVersion()).setMessage(version.getDescriptionVersion())
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=la.neurometrics.cobi");
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent1);

                            }
                        }).show();

            }
        }


    }

    private void initTerms() {
        TextView txtTerms = findViewById(R.id.txt_terms);
        String sourceString = "Al iniciar sesión acepto los <b><u>Términos y condiciones.</u></b> ";
        txtTerms.setText(Html.fromHtml(sourceString));
        txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //     Intent intent = new Intent(ActivityAccess.this, ActivityTerms.class);
             //   startActivity(intent);
                Uri uri = Uri.parse("https://www.cobiapp.com/Terminos-y-condiciones.html");
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent1);

            }
        });

    }

    private void initLoginFacebook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        ServiceUser serviceUser = new ServiceUser(ActivityAccess.this);
                        serviceUser.registerWithFacebook(accessToken);
                    }
                    @Override
                    public void onCancel() {
                        serviceRest.hideDialog();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        serviceRest.hideDialog();
                    }
                });
        Button btnLoginFacebook = findViewById(R.id.btn_login_facebook);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(ActivityAccess.this, Arrays.asList("public_profile", "email", "user_birthday"));
            }
        });
    }

    private void initBtnLogin() {
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityAccess.this, ActivityLogin.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
