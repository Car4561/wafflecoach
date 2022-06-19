package com.peru.wafflecoach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class ServiceUser {

    private static Context context;
    private la.neurometrics.cobi.ServiceRest serviceRest;

    public ServiceUser(Context _context) {
        context = _context;
        serviceRest = new la.neurometrics.cobi.ServiceRest(context);
    }

    public void registerWithFacebook(AccessToken accessToken) {
        serviceRest.showDialog("Solicitando información...");
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    JSONObject body = new JSONObject();
                    body.put("source", "facebook");
                    body.put("uid", object.getString("id"));
                    body.put("email", object.getString("email"));
                    JSONObject attributes = new JSONObject();
                    attributes.put("name", object.getString("first_name"));
                    attributes.put("surname", object.getString("last_name"));
                    if(object.has("picture")
                            && object.getJSONObject("picture").has("data")
                            && object.getJSONObject("picture").getJSONObject("data").has("url"))
                //        attributes.put("photo", object.getJSONObject("picture").getJSONObject("data").getString("url"));
                    body.put("attributes", attributes);
                    body.put("created_by", "app");
                    body.put("edited_by", "app");

                    post(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,picture.type(large),first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void post(JSONObject body) {
        serviceRest.showDialog("Registrando...");
        serviceRest.post("user", body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has("token") && !response.getString("token").equals("")) {
                        String token = response.getString("token");
                        la.neurometrics.cobi.ServiceSession session = new la.neurometrics.cobi.ServiceSession(context);
                        session.login(token);
                        serviceRest.hideDialog();
                    }else{
                        la.neurometrics.cobi.ServiceGlobal.alerta(context, "No se reconoce el token");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void put(JSONObject body) {
        put(body, false);
    }

    public void put(JSONObject body, final boolean waiting) {
        if(waiting) {
            serviceRest.showDialog("Actualizando...");
        }
        serviceRest.put("user", body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has("token") && !response.getString("token").equals("")) {
                        String token = response.getString("token");
                        la.neurometrics.cobi.ServiceSession session = new la.neurometrics.cobi.ServiceSession(context);
                        session.setSession(token);
                        serviceRest.hideDialog();
                        la.neurometrics.cobi.ServiceGlobal.alerta(context, "Perfil actualizado");
                        if(waiting){
                            Intent intent = new Intent(context, la.neurometrics.cobi.ActivityWrapper.class);
                            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("default", "profile");
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }else{
                        la.neurometrics.cobi.ServiceGlobal.alerta(context, "No se reconoce el token actualizado");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateFirebaseToken(String token) {
        try {
            JSONObject bodyPut = new JSONObject();
            bodyPut.put("tokenFirebase", token);
            Log.d("TAG1", String.valueOf(bodyPut));

            serviceRest.put("user", bodyPut, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String token = response.getString("token");
                        la.neurometrics.cobi.ServiceSession serviceSession = new la.neurometrics.cobi.ServiceSession(context);
                        serviceSession.setSession(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void putNoMessage(JSONObject body, final boolean waiting) {
        if(waiting) {
            serviceRest.showDialog("Actualizando...");
        }
        serviceRest.put("user", body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has("token") && !response.getString("token").equals("")) {
                        String token = response.getString("token");
                        la.neurometrics.cobi.ServiceSession session = new la.neurometrics.cobi.ServiceSession(context);
                        session.setSession(token);
                        serviceRest.hideDialog();
                        if(waiting){
                            Intent intent = new Intent(context, la.neurometrics.cobi.ActivityWrapper.class);
                            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("default", "profile");
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }else{
                        la.neurometrics.cobi.ServiceGlobal.alerta(context, "No se reconoce el token actualizado");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
