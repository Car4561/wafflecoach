package com.peru.wafflecoach;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceGlobal {

    public static String URL_API = "https://api.cobiapp.com/1.0/";
    public static String URL_API_LIGHTRAIL= "https://api.lightrail.com/v2/";

    public static String URL_DMZ = "https://api.cobiapp.com/dmz/";
    public static String YOUTUBE_API_KEY = "AIzaSyC08aUy0ibVJA5vULvFpTYnVajhQMjPsqU";
    public static String TOKEN_LIGHTRAIL_TEST = "eyJ2ZXIiOjMsInZhdiI6MSwiYWxnIjoiSFMyNTYiLCJ0eXAiOiJKV1QifQ.eyJnIjp7Imd1aSI6InVzZXItYTIxMDg4ZmU3MjdhNGQ5YzgwNjY5NzMxMmYxODRjNTctVEVTVCIsImdtaSI6InVzZXItYTIxMDg4ZmU3MjdhNGQ5YzgwNjY5NzMxMmYxODRjNTctVEVTVCIsInRtaSI6InVzZXItYTIxMDg4ZmU3MjdhNGQ5YzgwNjY5NzMxMmYxODRjNTctVEVTVCJ9LCJhdWQiOiJBUElfS0VZIiwiaXNzIjoiU0VSVklDRVNfVjEiLCJpYXQiOjE1OTQ3NDYxMjYuNzI0LCJqdGkiOiJiYWRnZS05ZmUwMjJjMDJjNmM0NmJiODE4YWI5NWU5NTdhYTI0MyIsInBhcmVudEp0aSI6ImJhZGdlLTQ3OTIxMDY1NTYzMTRhYzBiZjRiOTZiNDI2NzlkMDhjIiwic2NvcGVzIjpbXSwicm9sZXMiOlsiYWNjb3VudE1hbmFnZXIiLCJjb250YWN0TWFuYWdlciIsImN1c3RvbWVyU2VydmljZU1hbmFnZXIiLCJjdXN0b21lclNlcnZpY2VSZXByZXNlbnRhdGl2ZSIsInBvaW50T2ZTYWxlIiwicHJvZ3JhbU1hbmFnZXIiLCJwcm9tb3RlciIsInJlcG9ydGVyIiwic2VjdXJpdHlNYW5hZ2VyIiwidGVhbUFkbWluIiwid2ViUG9ydGFsIl19.TOmUTWPX6y07MP5Y5gZE7bviUT8wMLnlpg8p0W5R3dE";
    public static String TOKEN_LIGHTRAIL_LIVE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsInZlciI6MiwidmF2IjoxfQ.eyJnIjp7Imd1aSI6InVzZXItYTIxMDg4ZmU3MjdhNGQ5YzgwNjY5NzMxMmYxODRjNTciLCJ0bWkiOiJ1c2VyLWEyMTA4OGZlNzI3YTRkOWM4MDY2OTczMTJmMTg0YzU3In0sImF1ZCI6IkFQSSIsImlzcyI6IkVESEkiLCJyb2xlcyI6WyJwcm9tb3RlciIsInJlcG9ydGVyIiwidGVhbUFkbWluIiwid2ViUG9ydGFsIiwicG9pbnRPZlNhbGUiLCJhY2NvdW50TWFuYWdlciIsImNvbnRhY3RNYW5hZ2VyIiwicHJvZ3JhbU1hbmFnZXIiLCJzZWN1cml0eU1hbmFnZXIiLCJjdXN0b21lclNlcnZpY2VNYW5hZ2VyIiwiY3VzdG9tZXJTZXJ2aWNlUmVwcmVzZW50YXRpdmUiXSwic2NvcGVzIjpbXSwianRpIjoidG9rLTYwOTFjYjJiNmUyNTQyNzU5ZTZiZGRkOWQwODcwZDhhIiwiaWF0IjoxNTk1NjA2MTI4Ljc2OX0.kekUhMQIuxd7_Z_kqYRPBgaJgGJfmjKTqbgBWDnnNj4";

    public static void alerta(Context contexto, String mensaje){
        Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show();
    }

    public static void console(String log) {
        Log.i("COBI LOG:::::", log);
    }

    public static String convertFormat(String inputDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("es","ES")).parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("dd",new Locale("es","ES")).format(date)
                + " de "
                + new SimpleDateFormat("MMMM | HH:mm",new Locale("es","ES")).format(date)
                + " - "
                + new SimpleDateFormat("HH:45",new Locale("es","ES")).format(date);
    }

    public static void loadImageString (String urlPhoto, ImageView photo) {
        if(urlPhoto != null && !urlPhoto.equals("")) {
            Picasso.get()
                    .load(urlPhoto)
                    .placeholder(R.drawable.bg_trama)
                    .error(R.drawable.blankprofile)
                    .into(photo);
        }
    }
    public static void circlee(String urlPhoto, CircleImageView photo) {
        if(urlPhoto != null && !urlPhoto.equals("")) {
            Picasso.get()
                    .load(urlPhoto)
                    .placeholder(R.drawable.bg_trama)
                    .error(R.drawable.bg_trama)
                    .into(photo);
        }
    }
    public static void loadImage(String idResource, ImageView photo) {
        Picasso.get()
                .load(idResource)
                .placeholder(R.drawable.circle)
                .error(R.drawable.circle)
                .into(photo);
    }

    public static void loadCircleImage(String urlPhoto, ImageView photo) {
        if(!urlPhoto.equals("")) {
            Picasso.get()
                    .load(urlPhoto)
                    .transform(new la.neurometrics.cobi.ClassCircleTransform())
                    .placeholder(R.drawable.circle)
                    .error(R.drawable.blankprofile)
                    .into(photo);
        }
    }

    public static void loadCircleImage(int idResource, ImageView photo) {
        Picasso.get()
                .load(idResource)
                .transform(new la.neurometrics.cobi.ClassCircleTransform())
                .placeholder(R.drawable.circle)
                .error(R.drawable.circle)
                .into(photo);
    }

    public static String extractYoutubeVideoId(String ytUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }

    public static byte[] getHash(String password) {
        MessageDigest digest=null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }
    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length*2) + "X", new BigInteger(1, data));
    }

    public static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
