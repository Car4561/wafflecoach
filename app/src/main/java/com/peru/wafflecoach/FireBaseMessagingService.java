package com.peru.wafflecoach;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static int count = 0;

    @Override
    public void onNewToken(final String token) {
        //Global.console("token::" + s);
        super.onNewToken(token);
        ServiceSession session = new ServiceSession(this);
        if(session.exist()) {
            new Handler(Looper.getMainLooper());

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    ServiceUser user = new ServiceUser(getApplicationContext());
                    user.updateFirebaseToken(token);
                }
            });
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> row = remoteMessage.getData();
        RemoteMessage.Notification notificaiton = remoteMessage.getNotification();

        Log.d(TAG, String.valueOf(row.get("title")));
        if(row.get("title")!=null) {
          /*  if (row.get("type").toString().equals("cancel")) {
                mostrarNotificacion(row.get("title"), row.get("body"));
            }*/
            mostrarNotificacion(row.get("title"), row.get("body"));
            Log.d(TAG,row.get("title")+row.get("body"));
        }else if(notificaiton!=null){
            mostrarNotificacion(notificaiton.getTitle(), notificaiton.getBody());
        }



    }

    private void mostrarNotificacion(String title, String body) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "Cobi",
                    NotificationManager.IMPORTANCE_MAX);
            channel.setDescription("Cobi notificaciones");
            mNotificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, ActivityWrapper.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent  = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri souUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this,"YOUR_CHANNEL_ID")
                .setSmallIcon(R.drawable.icon_appointments)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(false)
                .setSound(souUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notBuilder.build());

    }

}
