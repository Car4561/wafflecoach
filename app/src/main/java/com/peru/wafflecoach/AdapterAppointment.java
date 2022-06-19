package com.peru.wafflecoach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AdapterAppointment extends RecyclerView.Adapter<AdapterAppointment.viewHolder> {

    private JSONArray items;
    JSONObject appointment;
    private Context context;
    private int countPatients;
    private String validity;
    private int countPasts;
    Long  mLastClickTime=0l;
    boolean c = true;
    private  int position2=-1;
    private  int position3=-1;

    public static class viewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout lytAppointment;
        public TextView txtType;
        public TextView txtName;
        public TextView txtLine;
        public TextView txtSchedule;
        public ImageView imgSpecialist;
        public ImageView imgPatient;
        public LinearLayout lytFavorites;
        public ImageView imgFavorites;
        public TextView txtFavorites;

        public viewHolder(LinearLayout view) {
            super(view);
            this.lytAppointment = view.findViewById(R.id.lyt_item_appointment);
            this.txtType = view.findViewById(R.id.txt_type);
            this.txtName = view.findViewById(R.id.txt_name);
            this.txtLine = view.findViewById(R.id.txt_line);
            this.txtSchedule = view.findViewById(R.id.txt_schedule);
            this.imgSpecialist = view.findViewById(R.id.img_specialist);
            this.imgPatient = view.findViewById(R.id.img_patient);
            this.lytFavorites = view.findViewById(R.id.lyt_favorites);
            this.imgFavorites = view.findViewById(R.id.img_favorites);
            this.txtFavorites = view.findViewById(R.id.txt_favorites);
        }

    }

    public AdapterAppointment(Context _context, JSONArray _items) {

        items = _items;
        context = _context;
        countPatients = 0;
        validity = "valid";
        countPasts = 0;

    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new viewHolder((LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_psychotherapist, parent, false));
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {

        if (position == position3) {
            countPatients = 0;
        }
        if(position == position2){

            countPasts=0;

        }

        if(position > 0){
            holder.txtType.setVisibility(View.GONE);
        }

        String id = "";
        String state = "";
        String valid = "";
        String type = "";
        String schedule = "";
        String description = "";
        String name = "";
        String photo = "";
        String line = "";
        String hour="";
        String day="";
        String country = "";
        String dateSchedule2="";

        JSONObject psychotherapist = null;
        String idPsychotherapist = "";
        String favorites = "";
        boolean isFavorite = false;
        Date dateSchedule = null;
        JSONObject userAttributes = null;
        try {
             appointment = items.getJSONObject(position);
             hour = appointment.getString("hour");
             day = appointment.getString("day");
            id = appointment.getString("id");
            state = appointment.getString("state");
            valid = appointment.getString("validity");
            type = appointment.getString("type");

            dateSchedule = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(appointment.getString("schedule"));
            dateSchedule2 = (appointment.getString("schedule"));

            schedule = ServiceGlobal.convertFormat(appointment.getString("schedule"));

            JSONObject attributes = appointment.getJSONObject("attributes");
            description = attributes.getString("description");

            psychotherapist = appointment.getJSONObject("psychotherapist");
            idPsychotherapist = psychotherapist.getString("id");
            favorites = psychotherapist.getString("favorites");
            if(psychotherapist.has("isFavorite") && psychotherapist.getString("isFavorite").equals("1")){
                isFavorite = true;
            }
            JSONObject patient = appointment.getJSONObject("user");
            if(patient.has("country"))
                country = patient.getString("country");

            if(type.equals("out")) {
                userAttributes = patient.getJSONObject("attributes");
            }else{
                userAttributes = psychotherapist.getJSONObject("attributes");
            }

            name = userAttributes.getString("name") + " " + userAttributes.getString("surname");


            if(userAttributes.has("photo")){
                photo = userAttributes.getString("photo");
            }else{
                photo = "https://www.cobiapp.com/assets/images/blankprofile.png";
            }
            if(userAttributes.has("line"))
                line = userAttributes.getString("line");

        }catch (JSONException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txtName.setText(name);
        if(state.equals("2")){
            holder.txtSchedule.setText("Cancelada");
            holder.txtSchedule.setTextColor(context.getResources().getColor(R.color.colorPink));
        }else{
            holder.txtSchedule.setText(schedule);
        }

        if(type.equals("out")){
            holder.txtLine.setText(country);
            ServiceGlobal.loadCircleImage(photo, holder.imgPatient);

            holder.imgSpecialist.setVisibility(View.GONE);
            holder.lytFavorites.setVisibility(View.GONE);
            holder.imgPatient.setVisibility(View.VISIBLE);
            if(countPatients == 0 && valid.equals("valid") && position  == 0) {
                holder.txtType.setVisibility(View.VISIBLE);
                position3 = position;
            }
            holder.txtType.setText("Próximas citas con pacientes");
            if(countPatients == 0 && valid.equals("valid")) {
                holder.txtType.setVisibility(View.VISIBLE);
                position3 = position;
            }
            Log.d("TAG1", String.valueOf(position3));
            countPatients++;


        }else{
            holder.txtLine.setText(line);
            ServiceGlobal.loadCircleImage(photo, holder.imgSpecialist);
            holder.txtFavorites.setText(favorites);
            if(isFavorite){
                holder.imgFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favorite));
            }else{
                holder.imgFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favorite_off));
            }

            final String finalIdPsychotherapist = idPsychotherapist;
            holder.lytFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 400) {
                        return;
                    }
                    ServiceRest serviceRest = new ServiceRest(context);
                    try {
                        Integer countFavorites = Integer.valueOf(holder.txtFavorites.getText().toString());
                        if(context.getResources().getDrawable(R.drawable.icon_favorite).getConstantState() == holder.imgFavorites.getDrawable().getConstantState()){
                            holder.imgFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favorite_off));
                            countFavorites = countFavorites - 1;
                            refreshFavorite(finalIdPsychotherapist, "0", countFavorites);
                            serviceRest.delete("favorite?psychotherapist=" + finalIdPsychotherapist, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) { }
                            });
                        }else{
                            holder.imgFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_favorite));
                            countFavorites = countFavorites + 1;
                            JSONObject bodyPost = new JSONObject();
                            refreshFavorite(finalIdPsychotherapist, "1", countFavorites);
                            bodyPost.put("idPsychotherapist", finalIdPsychotherapist);
                            serviceRest.post("favorite", bodyPost, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) { }
                            });
                        }
                        holder.txtFavorites.setText(String.valueOf(countFavorites));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });

        }

        if(valid.equals("past") && countPasts == 0) {
            holder.txtType.setText("Mi historial");
            holder.txtType.setVisibility(View.VISIBLE);
            position2 = position;
            countPasts++;
        }
        if(valid.equals("past") && countPasts == 0 && position == 0 ){
            holder.txtType.setText("Mi historial");
            holder.txtType.setVisibility(View.VISIBLE);

        }

        final String finalId = id;
        final String finalName = name;
        final String finalDescription = description;
        final String finalPhoto = photo;
        final String finalValid = valid;
        final String finalType = type;
        final String finalHour = hour;
        final String finalDay = day;
        final String finalSchedule2 =dateSchedule2 ;
        final JSONObject finalPsychotherapist = psychotherapist;
        final String finalSchedule = schedule;
        final String finalState = state;
        final Date finalDateSchedule = dateSchedule;
        final JSONObject finalAttributes = userAttributes;
        holder.lytAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    initPopUpDescription(finalId, finalName, finalSchedule, finalDescription, finalPhoto, finalValid, finalType, finalPsychotherapist, finalState, finalDateSchedule, finalAttributes,finalDay,finalHour,finalSchedule2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void refreshFavorite(String idPsychotherapist, String state, Integer count) {
        for(int i = 0; i < items.length(); i++) {
            try {
                JSONObject appointment = items.getJSONObject(i);
                JSONObject psychotherapist = appointment.getJSONObject("psychotherapist");
                String id = psychotherapist.getString("id");
                if(idPsychotherapist.equals(id)) {
                    psychotherapist.put("isFavorite", state);
                    psychotherapist.put("favorites", String.valueOf(count));
                    appointment.put("psychotherapist", psychotherapist);
                    items.put(i, appointment);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                countPatients = 0;
                validity = "valid";
                countPasts = 0;
                notifyItemRangeChanged(0, items.length());
            }
        }).start();
    }

    private void initPopUpDescription(final String id, final String fullName, final String schedule, String description, String photo, String valid, String type, final JSONObject psychotherapist, String state, final Date dateSchedule, final JSONObject attributes, final String finalDay, final String finalHour,final String finalSchedule2) throws ParseException {

        View layoutView = ((Activity)context).getLayoutInflater().inflate(R.layout.layout_description, null);

        ImageView imgPic = layoutView.findViewById(R.id.img_pic);
        TextView txtName = layoutView.findViewById(R.id.txt_name);
        TextView txtDescription = layoutView.findViewById(R.id.txt_description);
        Button btnCall = layoutView.findViewById(R.id.btn_call);
        Button btnChange = layoutView.findViewById(R.id.btn_change);
        Button btnCancel = layoutView.findViewById(R.id.btn_cancel);
        ImageView imgClose = layoutView.findViewById(R.id.img_close);

        ServiceGlobal.loadCircleImage(photo, imgPic);
        txtName.setText(fullName);
        txtDescription.setText(description);

        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Lima"));
        String dateStr = dateFormat.format(date);

        Date currentDate = dateFormat.parse(dateStr);

        long diff = dateSchedule.getTime() - currentDate.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        if(valid.equals("past")){
            btnCall.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnChange.setVisibility(View.GONE);
        }else{
            if(type.equals("in")){
                btnCall.setVisibility(View.GONE);

                btnCancel.setVisibility(View.GONE);
                if(hours >= 12) {
                    btnChange.setOnClickListener(   new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ActivityScheduleAppointment.class);
                            intent.putExtra("user", psychotherapist.toString());
                            intent.putExtra("change", true);
                            String dateAnte[] = finalSchedule2.split(" ");
                            intent.putExtra("idAppointment", id);
                            intent.putExtra("dateAnterior",dateAnte[0]);

                            System.out.println(schedule);
                            intent.putExtra("dayAnte",finalDay);
                            intent.putExtra("houtAnte",finalHour);
                            System.out.println(finalDay+finalHour);

                            context.startActivity(intent);
                        }
                    });
                }else{
                    btnChange.setVisibility(View.GONE);
                }
                if(state.equals("2") || minutes < -15){
                    btnCall.setVisibility(View.GONE);
                }else{
                    if(minutes > 10) {
                        btnCall.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorLightGray)));
                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ServiceGlobal.alerta(context, "Aún no puede iniciar la sesión");
                            }
                        });
                    }else{
                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if(attributes.has("phone")){
                                        String phone = attributes.getString("phone");
                                        String url = "https://api.whatsapp.com/send?phone=" + phone;
                                        try {
                                            PackageManager pm = context.getPackageManager();
                                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(url));
                                            context.startActivity(i);
                                        } catch (PackageManager.NameNotFoundException e) {
                                            ServiceGlobal.alerta(context, "Necesita instalar Whatsapp");
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }else{
                btnChange.setVisibility(View.GONE);
                if(state.equals("1")){
                    if(hours >= 12) {
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, ActivityCancelAppointment.class);
                                intent.putExtra("schedule", schedule);
                                intent.putExtra("name", fullName);
                                try {
                                    intent.putExtra("idPsychotherapist", psychotherapist.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(finalDay+finalHour);

                                String dateAnte[] = finalSchedule2.split(" ");
                                intent.putExtra("dayAnte",finalDay);
                                intent.putExtra("houtAnte",finalHour);
                                intent.putExtra("dateAnterior",dateAnte[0]);
                                intent.putExtra("id", id);
                                context.startActivity(intent);
                            }
                        });
                    }else{
                        btnCancel.setVisibility(View.GONE);
                    }
                    if(minutes > 10) {
                        btnCall.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorLightGray)));
                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ServiceGlobal.alerta(context, "Aún no puede iniciar la sesión");
                            }
                        });
                    }else{
                        btnCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    if(attributes.has("phone")){
                                        String phone = attributes.getString("phone");
                                        String url = "https://api.whatsapp.com/send?phone=" + phone;
                                        try {
                                            PackageManager pm = context.getPackageManager();
                                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(url));
                                            context.startActivity(i);
                                        } catch (PackageManager.NameNotFoundException e) {
                                            ServiceGlobal.alerta(context, "Necesita instalar Whatsapp");
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else{
                    btnCall.setVisibility(View.GONE);
                    btnCancel.setText("Cita cancelada");
                }
            }
        }
    try {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(layoutView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }catch (Exception e){
        e.printStackTrace();
    }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.length();
    }
}