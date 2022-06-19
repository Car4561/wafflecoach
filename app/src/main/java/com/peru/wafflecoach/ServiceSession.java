package com.peru.wafflecoach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.login.LoginManager;
import com.orhanobut.hawk.Hawk;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ServiceSession {

    private Context context;

    public ServiceSession(Context context) {
        this.context = context;
        Hawk.init(this.context).build();
    }

    public <T> T getFromSession(String key) {
        Hawk.init(this.context).build();
        return Hawk.get(key, null);
    }
    public <T> T get(String key) {
        return Hawk.get(key, null);
    }


    public <T>  void setToSession(String key, T value) {
        Hawk.put(key, value);
    }

    public boolean contains(String key) {
        return Hawk.contains(key);
    }

    public void deleteFromSession(String key) {
        Hawk.delete(key);
    }

    public boolean exist() {
        return Hawk.contains("token");
    }

    public void clearSession() {
        Hawk.deleteAll();
        Hawk.put("intro", true);
        Hawk.put("intro2", true);

    }

    public void setSession(String token) {
        clearSession();
        ServiceSession session= new ServiceSession(context);
        String data = la.neurometrics.cobi.ServiceGlobal.bin2hex(la.neurometrics.cobi.ServiceGlobal.getHash("cobiapi"));
        Key key = new SecretKeySpec(data.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        Map<String, Object> user = (Map<String, Object>) claims.get("user");

        this.setToSession("id", claims.getSubject());
        this.setToSession("profile", user.get("profile"));
        this.setToSession("source", user.get("source"));
        this.setToSession("uid", user.get("uid"));
        this.setToSession("tokenFirebase", user.get("tokenFirebase"));
        this.setToSession("email", user.get("email"));
        this.setToSession("idCountry", user.get("tbl_country_id"));
        this.setToSession("country", user.get("country"));
        this.setToSession("vacations", user.get("vacations"));
        this.setToSession("state", user.get("state"));
        this.setToSession("attributes", user.get("attributes"));
        this.setToSession("specialties", user.get("specialties"));
        this.setToSession("languages", user.get("languages"));
        this.setToSession("paymentMethod", user.get("paymentMethod"));
        this.setToSession("token", token);

    }

    public void login(String token) {
        setSession(token);
        Intent intent = new Intent(context, la.neurometrics.cobi.ActivityWrapper.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        clearSession();
        Intent intent = new Intent(context, ActivityLogin.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public String getFullName() {
        String fullname = "";
        HashMap attributes = getFromSession("attributes");
     /*   if (attributes.containsKey("name")) {

            fullname = fullname + attributes.get("name").toString().split(" ")[0] + " ";
        }
        if (attributes.containsKey("surname")) {
            fullname = fullname + attributes.get("surname").toString().split(" ")[0] + " ";
        }*/
      if (attributes.containsKey("name")) {
            fullname = fullname + attributes.get("name") + " ";
        }
        if (attributes.containsKey("surname")) {
            fullname = fullname + attributes.get("surname");
        }
        return fullname.trim();
    }

    public String getFirstNameAndFirstSurname() {
        String fullname = "";
        HashMap attributes = getFromSession("attributes");
        if (attributes.containsKey("name")) {

            fullname = fullname + attributes.get("name").toString().split(" ")[0] + " ";
        }
        if (attributes.containsKey("surname")) {
            fullname = fullname + attributes.get("surname").toString().split(" ")[0] ;
        }

        return fullname.trim();
    }
    public String getEmail() {
        return getFromSession("email").toString().trim();
    }

    public String getUserAttribute(String key) {
        HashMap attributes = getFromSession("attributes");
        if (attributes.containsKey(key)) {
            return attributes.get(key).toString();
        }
        return null;
    }




    public boolean intro() {
        if(Hawk.contains("intro"))
            return Hawk.get("intro");
        return false;
    }
    public boolean intro2() {
        if(Hawk.contains("intro2"))
            return Hawk.get("intro2");
        return false;
    }
}
