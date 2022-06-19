package com.peru.wafflecoach;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


public class ActivityScheduleAppointment extends AppCompatActivity {

    JSONObject psychotherapist = new JSONObject();
    ServiceRest serviceRest;

    MaterialCalendarView calDate;
    LinearLayout lytHours;
    Button btnNext;
    Button btnUpdate;

    List<Integer> availableDays = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();

    String date = "";
    String hour = "";
    String dateAnterior = "";
    int c1 = 0;

    String idAppointment = "";

    JSONArray availability;
    JSONObject user;
    int countDay= 0;
    int dayOfWeek;
    String dayAnte="";
    int contCatch = 0 ;
    String hourAnte ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getBooleanExtra("change", false) && getIntent().hasExtra("idAppointment")) {
            setContentView(R.layout.activity_updatenly);
            setTitle("Cambiar fecha");
        }else{
            setContentView(R.layout.activity_schedule_appointment);
            setTitle("Reservar");

        }

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calDate = findViewById(R.id.cal_date);
        lytHours = findViewById(R.id.lyt_hours);
        btnNext = findViewById(R.id.btn_next);
        btnUpdate = findViewById(R.id.btn_update);
        try {
            String extra1 = getIntent().getStringExtra("user");
            user = new JSONObject(extra1);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        serviceRest = new ServiceRest(ActivityScheduleAppointment.this);
        serviceRest.showDialog("Cargando...");
        try {
            serviceRest.getJSONArray("/availability/"+ user.getString("id"), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    availability = response;
                    serviceRest.hideDialog();
                    if(getIntent().hasExtra("user")) {
                        try {
                            String extra = getIntent().getStringExtra("user");
                            psychotherapist = new JSONObject(extra);
                            ServiceGlobal.console(psychotherapist.toString());
                          //  initAvailableDays();
                            initCalendarDate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //sacar de la vista
                        }

                        if(getIntent().getBooleanExtra("change", false) && getIntent().hasExtra("idAppointment")) {
                            idAppointment = getIntent().getStringExtra("idAppointment");
                            initBntUpdate();
                            btnUpdate.setVisibility(View.VISIBLE);
                            btnNext.setVisibility(View.GONE);
                        }
                    }else{

                        //sacar de la vista

                    }
                    initBtnNext();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void initBntUpdate() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!date.equals("") && !hour.equals("")) {
                    serviceRest.showDialog("Agendar nueva fecha...");
                    JSONObject bodyPut = new JSONObject();
                    try {
                        dateAnterior = getIntent().getStringExtra("dateAnterior");
                        hourAnte = getIntent().getStringExtra("houtAnte");
                        dayAnte = getIntent().getStringExtra("dayAnte");
                        Log.d("TAGdateanterior",dateAnterior);
                        bodyPut.put("schedule", date + " " + hour + ":00:00");
                        bodyPut.put("dateAnterior",dateAnterior);
                        bodyPut.put("date", date);
                        bodyPut.put("hour", hour);
                        if(dayOfWeek == 1){
                            dayOfWeek = 7;
                            bodyPut.put("day", 7);

                        }else{
                            bodyPut.put("day", dayOfWeek-1);
                        }
                        bodyPut.put("idPsychotherapist",  user.getString("id"));
                        bodyPut.put("dayAnt",dayAnte);
                        bodyPut.put("houtAnt",hourAnte);
                        Log.d("TAG4",bodyPut.toString() + idAppointment );
                        serviceRest.put("appointment/" + idAppointment, bodyPut, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                serviceRest.hideDialog();
                                Intent intent = new Intent(ActivityScheduleAppointment.this, ActivityWrapper.class);
                                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                                startActivity(intent);
                                finish();
                            }
                        });
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    ServiceGlobal.alerta(ActivityScheduleAppointment.this, "Seleccione una fecha y una hora disponible");
                }
            }
        });
    }

    private void initBtnNext() {

        Date today = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("America/Lima"));
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR,1);
        Date date1 = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Lima"));
        String dayDate = dateFormat.format(date1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int daynew = calendar.get(Calendar.DAY_OF_MONTH);
        String[] daydate1 = dayDate.split("-");

        Log.d("TAG43525435433453545431",String.valueOf(daydate1[2]));


        CalendarDay calendarToday = CalendarDay.from(year,month, daynew);
        date = calendarToday.getYear() + "-" + calendarToday.getMonth() + "-" + calendarToday.getDay();
        Log.d("TAGDATE",date);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) + 1;
        if(dayOfWeek==0){
            dayOfWeek=7;
        }
        initAvailableHours(calendarToday);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder( ActivityScheduleAppointment.this)
                        .setMessage("Atendemos a pacientes de 18 años a más. Recomendamos estar en un lugar sin interrupciones para la sesión, recuerda poner tu celular en modo avión y conectarte a una buena red de Wi-Fi. Le llamaremos por WhatsApp")
                        .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!date.equals("") && !hour.equals("")) {
                                    Intent intent = new Intent(ActivityScheduleAppointment.this, ActivityInfoPatient.class);
                                    intent.putExtra("user", psychotherapist.toString());
                                    intent.putExtra("date", date);
                                    intent.putExtra("hour", hour);
                                    Log.d("day1234123", String.valueOf(dayOfWeek));
                                    intent.putExtra("day", dayOfWeek-1);
                                    startActivity(intent);
                                }else{
                                    ServiceGlobal.alerta(ActivityScheduleAppointment.this, "Seleccione una fecha y una hora disponible");
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        })
                        .show();

            }
        });
    }

    private void initAvailableDays() {
        try {

            JSONArray availability = psychotherapist.getJSONArray("availability");
            for(int i = 0; i < availability.length(); i++) {
                String[] available = availability.getString(i).split("_");
                int availableDay =  Integer.valueOf(available[0]);
                availableDay++;
                if(availableDay > 7)
                    availableDay = 0;
                if(!availableDays.contains(availableDay))
                    availableDays.add(availableDay);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initCalendarDate() {
    //   String[] months = new String[]{"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
     //   calDate.setTitleMonths(months);
        calDate.setDynamicHeightEnabled(true);
        calDate.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                calendar.set(day.getYear(), day.getMonth() - 1, day.getDay());

                int dayOfWeek1 = calendar.get(Calendar.DAY_OF_WEEK);
                dayOfWeek1-- ;
                if(dayOfWeek1 == 0) {
                    dayOfWeek1   = 7;
                }
                try {
                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("America/Lima"));

                    Date dayDate = dateFormat.parse(day.getYear() + "-" + day.getMonth() + "-" + day.getDay());
                    if(date.compareTo(dayDate) > 0) {
                        return  true;
                    }else if (countDay == 0){
                        countDay++;
                        return true;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                for(int i = 0; i<availability.length() ; i++){
                    try {
                        if(availability.getJSONObject(i).getString("day").equals(dayOfWeek1)){
                            return  false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
            @Override
            public void decorate(DayViewFacade view) {
                   view.setDaysDisabled(true);
                   view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGray)));
            }
        });
        calDate.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
                dateFormat.setTimeZone(TimeZone.getTimeZone("America/Lima"));
                if(dateFormat.format(date).equals(day.getYear() + "-" + day.getMonth() + "-" + day.getDay())){
                    c1 = 1;
                }
                if(c1==1) {
                    c1 = 2;
                    return false;

                }else if(c1==2){
                    c1=0;

                    return  true;
                }else{
                    return  false;
                }

            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(getDrawable(R.drawable.circle));
                view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorWhite)));

                view.setSelectionDrawable(getDrawable(R.drawable.rounded_white));
            }
        });
        calDate.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay day, boolean selected) {
                date = day.getYear() + "-" + day.getMonth() + "-" + day.getDay();
                calendar.set(day.getYear(), day.getMonth() - 1, day.getDay());
                dayOfWeek= calendar.get(Calendar.DAY_OF_WEEK);
                Log.d("TAG1", String.valueOf(day.getMonth()));
                if(dayOfWeek==0){
                    dayOfWeek=7;
                }
                initAvailableHours(day);

                TextView txtAv = findViewById(R.id.lbl_availability);
                TextView txtNote = findViewById(R.id.lbl_note);
                HorizontalScrollView text = findViewById(R.id.hsv_hour);

                txtAv.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                txtNote.setVisibility(View.GONE);
            }
        });
    }

    private void initAvailableHours(CalendarDay day) {
        contCatch = 0;
        hour = "";
        lytHours.removeAllViews();
        calendar.set(day.getYear(), day.getMonth() - 1, day.getDay());
        int daysOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1  ;
        if(daysOfWeek == 0)
            daysOfWeek = 7;
        try {
            int added = 0;
           // JSONArray availability = psychotherapist.getJSONArray("availability");
            boolean pass= true;
            List<String> availableHours = new ArrayList<>();

            for(int i = 0; i<availability.length();i++) {
                if (availability.getJSONObject(i).getString("date_day").length() > 2 && availability.getJSONObject(i).getString("date_month").length() > 2 && availability.getJSONObject(i).getString("date_year").length() > 4) {

                    String[] date_day = availability.getJSONObject(i).getString("date_day").split(",");
                    String[] date_month = availability.getJSONObject(i).getString("date_month").split(",");
                    String[] date_year = availability.getJSONObject(i).getString("date_year").split(",");

                    if (availability.getJSONObject(i).getString("day").equals(String.valueOf(daysOfWeek))) {
                         try {
                            for (int j = 0; j < date_day.length; j++) {
                                if (!date_day[j].equals("") && !date_day[j].equals("") && !date_day[j].equals("")) {

                                    Log.d("TAG54", date_year[j] + " cal:" + day.getYear());

                                        if (Integer.parseInt(date_day[j]) != ((day.getDay())) || Integer.parseInt(date_month[j]) != ((day.getMonth())) || Integer.parseInt(date_year[j]) != ((day.getYear()))) {


                                        } else {

                                            pass = false;
                                        }

                                }

                            }
                        }catch (Exception e){
                            lytHours.removeAllViews();
                             contCatch++;
                            added = 0;
                            e.printStackTrace();
                        }

                        if (pass) {
                            added++;
                            addHourButton(availability.getJSONObject(i).getString("hour"));
                        }
                        pass = true;

                    }
               }else {

                    if (availability.getJSONObject(i).getString("day").equals(String.valueOf(daysOfWeek))) {
                        try {
                            String date_day = availability.getJSONObject(i).getString("date_day");
                            String date_month = availability.getJSONObject(i).getString("date_month");
                            String date_year = availability.getJSONObject(i).getString("date_year");
                            Log.d("TAG1","xd "+date_day + " "+ date_month + " " + date_year);
                            if(!date_day.equals("null") && !date_month.equals("null") && !date_year.equals("null") ) {

                                    if (Integer.parseInt(date_day) != ((day.getDay())) || Integer.parseInt(date_month) != ((day.getMonth())) || Integer.parseInt(date_year) != ((day.getYear()))) {
                                        addHourButton(availability.getJSONObject(i).getString("hour"));
                                        added++;

                                    }


                            }else{
                                addHourButton(availability.getJSONObject(i).getString("hour"));
                                added++;
                            }
                        }catch (Exception e){
                            lytHours.removeAllViews();
                            contCatch++;
                            e.printStackTrace();
                            added = 0;

                        }
                    }

                }

            }
            if(contCatch > 0){
                lytHours.removeAllViews();

                added = 0;

            }







         /*   for(int i = 0; i<availability.length();i++){
                if(availability.getJSONObject(i).getString("date_day").length()>2 && availability.getJSONObject(i).getString("date_month").length()>2 && availability.getJSONObject(i).getString("date_year").length()>4) {

                    String[] date_day = availability.getJSONObject(i).getString("date_day").split(",");
                    String[] date_month = availability.getJSONObject(i).getString("date_month").split(",");
                    String[] date_year = availability.getJSONObject(i).getString("date_year").split(",");
                    if(availability.getJSONObject(i).getString("day").equals(String.valueOf(daysOfWeek))) {
                        for (int j = 0; j < date_day.length; j++) {
                            if (!date_day[j].equals(day.getDay()) || !date_month[j].equals(day.getMonth()) || !date_year[j].equals(day.getYear())) {
                                pass = true;
                            } else {
                                pass = false;
                            }

                        }
                        if (pass) {
                            addHourButton(availability.getJSONObject(i).getString("hour"));
                            added++;
                        }
                    }
                }else{
                    String date_day = availability.getJSONObject(i).getString("date_day");
                    String date_month = availability.getJSONObject(i).getString("date_month");
                    String date_year = availability.getJSONObject(i).getString("date_year");
                    if(date_day.equals(day.getDay()) || date_month.equals(day.getMonth()))
                }

            }

/*

               /* for(int i = 0; i<availability.length();i++){
                    if(availability.getJSONObject(i).getString("day").equals(String.valueOf(daysOfWeek))){
                        if (!availability.getJSONObject(i).getString("date_day").contains(String
                                .valueOf(day.getDay())) && !availability.getJSONObject(i).getString("date_month").contains(String.valueOf(day.getDay())) &&
                                !availability.getJSONObject(i).getString("date_year").contains(String.valueOf(day.getYear()))  ) {


                            addHourButton(availability.getJSONObject(i).getString("hour"));
                            added++;


                        }
                    }
                /*
              /*  int availableDay =  Integer.valueOf(available[0]);
                availableDay++;
                if(availableDay > 7)
                    availableDay = 0;
                if(availableDay == daysOfWeek){
                    addHourButton(available[1]);
                    added++;
                }*/

            if(added == 0) {
                ContextThemeWrapper newContext = new ContextThemeWrapper(ActivityScheduleAppointment.this, R.style.btnSmWhite);
                Button btnHour = new Button(newContext);
                btnHour.setText("No hay horarios disponibles");
                btnHour.setBackground(getResources().getDrawable(R.drawable.btn_white));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.btn_sm));
                params.setMarginEnd(16);
                btnHour.setTextSize(14);
                btnHour.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                btnHour.setAllCaps(false);
                btnHour.setLayoutParams(params);
         //       btnHour.setPadding((int) getResources().getDimension(R.dimen.space),0,(int) getResources().getDimension(R.dimen.space),0);
                lytHours.addView(btnHour);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void addHourButton(final String availableHour) {
        ContextThemeWrapper newContext = new ContextThemeWrapper(ActivityScheduleAppointment.this, R.style.btnSmWhite);
        Button btnHour = new Button(newContext);
        btnHour.setText(availableHour + ":00");
        btnHour.setBackground(getResources().getDrawable(R.drawable.btn_white));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.btn_sm));
        params.setMarginEnd(16);
        btnHour.setLayoutParams(params);
        btnHour.setPadding((int) getResources().getDimension(R.dimen.space),0,(int) getResources().getDimension(R.dimen.space),0);
        btnHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView btn;
                for(int i = 0; i < lytHours.getChildCount(); i++) {
                    btn = (TextView) lytHours.getChildAt(i);
                    btn.setBackground(getResources().getDrawable(R.drawable.btn_white));
                    btn.setTextColor(getResources().getColor(R.color.colorGray));
                }
                btn = (TextView) view;
                btn.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                btn.setTextColor(getResources().getColor(R.color.colorWhite));
                hour = availableHour;
            }
        });
        lytHours.addView(btnHour);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
