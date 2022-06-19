package com.peru.wafflecoach;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServiceRest {

    private static Context context;
    private Response.ErrorListener callBackErrorListener;
    private Response.ErrorListener callBackErrorListenerLightrail;

    public ProgressDialog progressDialog;
    public boolean cancelable = false;
    private ServiceSession serviceSession;

    public ServiceRest(final Context _context,final Activity activity){
        context = _context;
        serviceSession = new ServiceSession(context);


        progressDialog = new ProgressDialog(_context);
        progressDialog.setCancelable(cancelable);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Procesando...");
        callBackErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.e("CUSTOM LOG:::::", "Esta tardando demasiado");
                } else if (error instanceof AuthFailureError) {
                    Log.e("CUSTOM LOG:::::", "Error de autenticación");
                    serviceSession.logout();
                } else if (error instanceof ServerError) {
                    if(error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            la.neurometrics.cobi.ServiceGlobal.console(body);
                            JSONObject objectError = new JSONObject(URLDecoder.decode(body,"UTF-8"));
                            Log.e("CUSTOM LOG:::::", "Error :: 3 :: " + objectError.getString("message"));
                            la.neurometrics.cobi.ServiceGlobal.console(objectError.getString("message"));

                            TextView lvlemail= activity.findViewById(R.id.lbl_email);
                            TextView lvlpass= activity.findViewById(R.id.lbl_pass);
                            if(objectError.getString("message").equals("Contraseña incorrecta")){
                                lvlpass.setTextColor(ContextCompat.getColor(_context, R.color.colorREd));

                            }
                            if(objectError.getString("message").equals("Usuario no encontrado")){
                                lvlemail.setTextColor(ContextCompat.getColor(_context, R.color.colorREd));

                            }
                            if(objectError.getString("message").equals("Ya existe otro usuario usando el mismo correo electrónico")){
                                la.neurometrics.cobi.ServiceGlobal.alerta(context,"Ya existe otro usuario usando el mismo correo electrónico");
                                lvlemail.setTextColor(Color.parseColor("#FAFF0404"));

                            }
                            if(objectError.getString("message").equals("Ha tardado demasiado, vuelve a intentarlo")){
                                la.neurometrics.cobi.ServiceGlobal.alerta(context,"Ya existe otro usuario usando el mismo correo electrónico");

                                lvlemail.setTextColor(Color.parseColor("#FAFF0404"));

                            }

                            la.neurometrics.cobi.ServiceGlobal.console(objectError.getString("message"));
                            la.neurometrics.cobi.ServiceGlobal.alerta(context, objectError.getString("message"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    hideDialog();
                } else if (error instanceof NetworkError) {
                    error.printStackTrace();
                    Log.e("CUSTOM LOG:::::", "Error de conexión: "+error.toString());
                } else if (error instanceof ParseError) {
                    error.printStackTrace();
                    Log.e("CUSTOM LOG:::::", "Error: "+error.toString());
                }
            }
        };

    }


    public ServiceRest(Context _context){
        context = _context;
        serviceSession = new ServiceSession(context);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(cancelable);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Procesando...");

        callBackErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.e("COBI LOG:::::", "Error :: 1 :: Ha tardado demasiado, vuelve a intentarlo");
                } else if (error instanceof AuthFailureError) {
                    Log.e("COBI LOG:::::", "Error :: 2 :: Auth Error");
                    serviceSession.logout();
                } else if (error instanceof ServerError) {
                    if(error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject objectError = new JSONObject(URLDecoder.decode(body,"UTF-8"));
                            Log.e("COBI LOG:::::", "Error :: 3 :: " + objectError.getString("message"));
                            la.neurometrics.cobi.ServiceGlobal.console(objectError.getString("message"));
                            la.neurometrics.cobi.ServiceGlobal.alerta(context, objectError.getString("message"));
                            if(objectError.getString("message").equals("Ha tardado demasiado, vuelve a intentarlo")){
                               hideDialog();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    hideDialog();
                } else if (error instanceof NetworkError) {
                    error.printStackTrace();
                    Log.e("COBI LOG:::::", "Error :: 4 :: "+error.toString());
                } else if (error instanceof ParseError) {
                    error.printStackTrace();
                    Log.e("COBI LOG:::::", "Error :: 5 :: "+error.toString());
                }
            }
        };

        callBackErrorListenerLightrail = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Log.e("COBI LOG:::::", "Error :: 1 :: Ha tardado demasiado, vuelve a intentarlo");
                    } else if (error instanceof AuthFailureError) {
                        Log.e("COBI LOG:::::", "Error :: 2 :: Auth Error");
                        serviceSession.logout();
                    } else if (error instanceof ServerError) {
                        if (error.networkResponse.data != null) {
                            try {
                                String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                JSONObject objectError = new JSONObject(URLDecoder.decode(body, "UTF-8"));
                                Log.e("COBI LOG:::::", "Error :: 3 :: " + objectError.getString("message"));
                                Log.d("lightrail", objectError.getString("message"));
                                if (objectError.getString("message").equals("Ha tardado demasiado, vuelve a intentarlo")) {
                                    hideDialog();


                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        hideDialog();
                    } else if (error instanceof NetworkError) {
                        error.printStackTrace();
                        Log.e("COBI LOG:::::", "Error :: 4 :: " + error.toString());
                    } else if (error instanceof ParseError) {
                        error.printStackTrace();
                        Log.e("COBI LOG:::::", "Error :: 5 :: " + error.toString());
                    }
                }catch (Exception e) {
                  e.getMessage();
                }
            }
        };
    }

    public void showDialog() {
        progressDialog.show();
    }

    public void showDialog(String title) {
        progressDialog.setMessage(title);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.dismiss();
    }

    private HashMap customHeaders() {
        HashMap headers = new HashMap();
        headers.put("Content-Type", "application/json");
        if(serviceSession.getFromSession("token") != null) {
            headers.put("Authorization", "Bearer " + serviceSession.getFromSession("token"));
        }
        return headers;
    }
    private HashMap lightrailHeaders() {
        HashMap headers = new HashMap();
        headers.put("Content-Type", "application/json");
        if(serviceSession.getFromSession("token") != null) {
            headers.put("Authorization", "Bearer " + la.neurometrics.cobi.ServiceGlobal.TOKEN_LIGHTRAIL_LIVE);
        }
        return headers;
    }


    public void getJSONObject(final String URL, final Response.Listener<JSONObject> listener){
        JsonObjectRequest object = new JsonObjectRequest(
                Request.Method.GET,
                la.neurometrics.cobi.ServiceGlobal.URL_API + URL,
                null,
                listener,
                this.callBackErrorListener
        ){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                return customHeaders();
            }
        };
        la.neurometrics.cobi.ClassSingleton.getInstance(context).addToRequestQueue(object);
    }

    public void getJSONArray(final String URL, final Response.Listener<JSONArray> listener){
        JsonArrayRequest list = new JsonArrayRequest(
                Request.Method.GET,
                la.neurometrics.cobi.ServiceGlobal.URL_API + URL,
                null,
                listener,
                this.callBackErrorListener
        ){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                return customHeaders();
            }
        };
        la.neurometrics.cobi.ClassSingleton.getInstance(context).addToRequestQueue(list);
    }

    public void getString(final String URL, final Response.Listener<String> listener){
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                la.neurometrics.cobi.ServiceGlobal.URL_API + URL,
                listener,
                null
        ){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                return customHeaders();
            }
        };
        la.neurometrics.cobi.ClassSingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void post(final String URL, final JSONObject object, final Response.Listener<JSONObject> listener){
        this.post(URL, object, listener, this.callBackErrorListener);
    }

    public void post(final String URL, final JSONObject object, final Response.Listener<JSONObject> listener, Response.ErrorListener callBackErrorListener){
        la.neurometrics.cobi.ServiceGlobal.console(object.toString());
        JsonObjectRequest response = new JsonObjectRequest(
                Request.Method.POST,
                la.neurometrics.cobi.ServiceGlobal.URL_API + URL,
                object,
                listener,
                callBackErrorListener
        ){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                return customHeaders();
            }
        };
        la.neurometrics.cobi.ClassSingleton.getInstance(context).addToRequestQueue(response);
    }



    public void postLightrail(final String URL, final JSONObject object, final Response.Listener<JSONObject> listener ){

        this.postLightrail(URL, object, listener, this.callBackErrorListenerLightrail);

    }
    public void postLightrail(final String URL, final JSONObject object, final Response.Listener<JSONObject> listener,Response.ErrorListener callBackErrorListener ){
        la.neurometrics.cobi.ServiceGlobal.console(object.toString());
        JsonObjectRequest response = new JsonObjectRequest(
                Request.Method.POST,
                la.neurometrics.cobi.ServiceGlobal.URL_API_LIGHTRAIL + URL,
                object,
                listener,
                callBackErrorListener
        ){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                return lightrailHeaders();
            }
        };

        la.neurometrics.cobi.ClassSingleton.getInstance(context).addToRequestQueue(response);
    }
    public void put(final String URL, final JSONObject object, final Response.Listener<JSONObject> listener){
        this.put(URL, object, listener, this.callBackErrorListener);
    }

    public void put(final String URL, final JSONObject object, final Response.Listener<JSONObject> listener, Response.ErrorListener callBackErrorListener){
        JsonObjectRequest response = new JsonObjectRequest(
                Request.Method.PUT,
                la.neurometrics.cobi.ServiceGlobal.URL_API + URL,
                object,
                listener,
                callBackErrorListener
        ){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                return customHeaders();
            }
        };
        la.neurometrics.cobi.ClassSingleton.getInstance(context).addToRequestQueue(response);
    }

    public void patch(final String URL, final JSONObject object, final Response.Listener<JSONObject> listener){
        this.patch(URL, object, listener, this.callBackErrorListener);
    }

    public void patch(final String URL, final JSONObject object, final Response.Listener<JSONObject> listener, Response.ErrorListener callBackErrorListener){
        JsonObjectRequest response = new JsonObjectRequest(
                Request.Method.OPTIONS,
                la.neurometrics.cobi.ServiceGlobal.URL_API + URL,
                object,
                listener,
                callBackErrorListener
        ){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                return customHeaders();
            }
        };
        la.neurometrics.cobi.ClassSingleton.getInstance(context).addToRequestQueue(response);
    }
    public void delete(final String URL, final Response.Listener<JSONObject> listener){
        JsonObjectRequest response = new JsonObjectRequest(
                Request.Method.DELETE,
                la.neurometrics.cobi.ServiceGlobal.URL_API + URL,
                null,
                listener,
                this.callBackErrorListener
        ){
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                return customHeaders();
            }
        };
        la.neurometrics.cobi.ClassSingleton.getInstance(context).addToRequestQueue(response);
    }

}
