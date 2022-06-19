package com.peru.wafflecoach;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityMain extends AppCompatActivity {

    ServiceRest serviceRest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        serviceRest = new ServiceRest(getApplicationContext());
        ActionBar actionBar  = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

        serviceRest.getJSONObject("version", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String versionApp = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    Log.d("gaaa", versionApp) ;
                    la.neurometrics.cobi.ClassSingleton.getInstance(ActivityMain.this).setVersion(new Version(response.getString("newVersion"),versionApp,response.getString("tittleVersion"),response.getString("descriptionVersion")));

                    Intent intent = new Intent(ActivityMain.this, ActivityLogin.class);
                    final ServiceSession serviceSession = new ServiceSession(ActivityMain.this);
                    if(serviceSession.exist()) {
                        intent = new Intent(ActivityMain.this, la.neurometrics.cobi.ActivityWrapper.class);
                    }else{
                        if(!serviceSession.intro()){
                            intent = new Intent(ActivityMain.this, la.neurometrics.cobi.ActivityIntro.class);
                        }
                        serviceSession.clearSession();
                        LoginManager.getInstance().logOut();
                    }
                    startActivity(intent);
                    finish();
                } catch (JSONException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
