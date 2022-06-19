package com.peru.wafflecoach;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.viewHolder> {

    private JSONArray items;
    private Context context;
    NotificationCompat.Builder notificationCompat ;
    private  static  final  int idUnico =51623;





    public static class viewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout lytNotification;
        public TextView txtMessage;
        public TextView txtTime;
        public ImageView imgBgIcon;
        public ImageView imgIcon;

        public viewHolder(LinearLayout view) {
            super(view);
            this.lytNotification = view.findViewById(R.id.lyt_notification);
            this.txtMessage = view.findViewById(R.id.txt_message);
            this.txtTime = view.findViewById(R.id.txt_time);
            this.imgBgIcon = view.findViewById(R.id.img_bg_icon);
            this.imgIcon = view.findViewById(R.id.img_icon);
        }
    }

    public AdapterNotification(Context context, JSONArray items) {
        this.items = items;
        this.context = context;
        notificationCompat = new NotificationCompat.Builder(context);
        notificationCompat.setAutoCancel(true);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder((LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        try {

            final JSONObject notification = items.getJSONObject(position);
            holder.txtMessage.setText(notification.getString("message"));
            String type = notification.getString("type");
            switch (type) {
                case "change" :
                    holder.imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_notification_cancel));
                    holder.imgBgIcon.setColorFilter(context.getResources().getColor(R.color.icon));

                    holder.lytNotification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, la.neurometrics.cobi.ActivityWrapper.class);
                            context.startActivity(intent);
                        }
                    });
                    break;
                case "cancel":
                    holder.imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel));
                    holder.imgBgIcon.setColorFilter(context.getResources().getColor(R.color.icon));
                    holder.lytNotification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, la.neurometrics.cobi.ActivityWrapper.class);
                            context.startActivity(intent);
                        }
                    });
                    break;
                case "cita":
                    holder.imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_appointments));
                    holder.imgBgIcon.setColorFilter(context.getResources().getColor(R.color.icon));
                    holder.lytNotification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, la.neurometrics.cobi.ActivityWrapper.class);
                            context.startActivity(intent);
                        }
                    });
                    break;
                case "validation" :
                    holder.imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_notification_validation));
                    holder.imgBgIcon.setColorFilter(context.getResources().getColor(R.color.icon));
                    notificationCompat.setSmallIcon(R.drawable.logo);
                    notificationCompat.setPriority(1);
                    notificationCompat.setWhen(System.currentTimeMillis());
                    notificationCompat.setContentTitle("Felicidades,Ha sido validado como especialista!");
                    notificationCompat.setContentTitle(notification.getString("message"));

                  //  PendingIntent pd = PendingIntent.getActivity(context,0,)

                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(idUnico,notificationCompat.build());

                    holder.lytNotification.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ActivityEditProfile.class);
                            context.startActivity(intent);
                        }
                    });
                    break;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Lima"));
            SimpleDateFormat now1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            now1.setTimeZone(TimeZone.getTimeZone("America/Lima"));
            long time = sdf.parse(notification.getString("time")).getTime();
            long now   =  now1.parse(now1.format(new Date())).getTime() ;

            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.txtTime.setText(sdf.format(sdf.parse(notification.getString("time"))));


            if(type.equals("cancel")) {
                holder.lytNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initPopupCancellation(notification);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void initPopupCancellation(JSONObject notification) {
        View layoutView = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_cancellation, null);
        TextView txtCancellation = layoutView.findViewById(R.id.txt_cancellation);


    }
   
    @Override
    public int getItemCount() {
        return items.length();
    }
}
