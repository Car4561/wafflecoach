package com.peru.wafflecoach;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityAvailability extends AppCompatActivity {

    int activeDay = 1;
    private List<String> availability = new ArrayList<>();
    private ServiceRest serviceRest;
    MixpanelAPI mixpanel;

    public static final String MIXPANEL_TOKEN = "d49503cda477a5088678264293bbf55b";
    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mixpanel =
                MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
        mixpanel.optOutTracking();

        JSONObject props = new JSONObject();
        try {
            props.put("Gender", "Female");
            props.put("Plan", "Premium");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Plan Selected", props);

        Boolean hasOptedOutTracking = mixpanel.hasOptedOutTracking();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Mi horario");

        serviceRest = new ServiceRest(ActivityAvailability.this);

        getAvailability();
        initSwVacations();
    }

    private void initSwVacations() {

        Switch swVacations = findViewById(R.id.sw_vacations);

        ServiceSession serviceSession = new ServiceSession(ActivityAvailability.this);
        String vacations = serviceSession.getFromSession("vacations");
        if(vacations.equals("1")){
            swVacations.setChecked(true);
        }

        swVacations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String stateVacations = "0";
                if(b){
                    stateVacations = "1";
                }
                try {
                    JSONObject bodyPut = new JSONObject();
                    bodyPut.put("vacations", stateVacations);
                    ServiceUser serviceUser = new ServiceUser(ActivityAvailability.this);
                    serviceUser.put(bodyPut);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void getAvailability() {
        serviceRest.showDialog("Cargando disponibilidad...");
        ServiceSession serviceSession = new ServiceSession(ActivityAvailability.this) ;
        serviceRest.getJSONArray("availability/"+serviceSession.getFromSession("id"), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {


                    Log.d("TAG1",response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject dayAndHour = response.getJSONObject(i);
                        availability.add(dayAndHour.getString("day")+ "_" + dayAndHour.getString("hour"));
                    }
                    initBtnDays();
                    initTxtDays();
                    loadDailyAvailability();
                    serviceRest.hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

      /* serviceRest.getString("availability", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    availability = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(response);

                    Log.d("TAG1",jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        availability.add(jsonArray.getString(i));
                    }
                    initBtnDays();
                    initTxtDays();
                    loadDailyAvailability();
                    serviceRest.hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/

    private void initTxtDays() {
        for(int i=6; i<24; i++) {
            String txtID = "txt_hour_" + i;
            int resID = getResources().getIdentifier(txtID, "id", getPackageName());
            final TextView txtHour = findViewById(resID);
            final int finalI = i;
            txtHour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String dayAndHour = activeDay + "_" + finalI;
                    if (availability.contains(dayAndHour)) {
                        deleteAvailability(dayAndHour);
                        txtHour.setTextColor(getResources().getColor(R.color.colorGray));
                        txtHour.setBackground(null);
                    }else{
                        postAvailability(dayAndHour);
                        txtHour.setTextColor(getResources().getColor(R.color.colorWhite));
                        txtHour.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                    }
                }
            });
        }
    }

    private void deleteAvailability(final String dayAndHour) {
        String[] arrDayAndHour = dayAndHour.split("_");
        serviceRest.delete("availability?day=" + arrDayAndHour[0] + "&hour=" + arrDayAndHour[1], new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                availability.remove(dayAndHour);
            }
        });
    }

    private void postAvailability(final String dayAndHour) {
        try {
            JSONObject bodyPost = new JSONObject();
            String[] arrDayAndHour = dayAndHour.split("_");
            bodyPost.put("day", arrDayAndHour[0]);
            bodyPost.put("hour", arrDayAndHour[1]);

            serviceRest.post("availability", bodyPost, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    availability.add(dayAndHour);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initBtnDays() {
        LinearLayout linearLayout = findViewById(R.id.lyt_days);
        int count = linearLayout.getChildCount();
        for(int i=0; i<count; i++) {
            Button btn = (Button) linearLayout.getChildAt(i);
            initOpenAvailability(btn, i + 1);
            if(i==0){
                btn.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                btn.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }
    }

    public void initOpenAvailability(final Button btnDay, final int day) {
        btnDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOffDays();
                activeDay = day;
                Button btn = (Button) view;
                btnDay.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                btnDay.setTextColor(getResources().getColor(R.color.colorWhite));
                if(day==1){
                    TextView tv = findViewById(R.id.txtDaydate);
                    tv.setText("lunes");
                }else if(day==2){
                    TextView tv = findViewById(R.id.txtDaydate);
                    tv.setText("martes");
                }else if(day==3){
                    TextView tv = findViewById(R.id.txtDaydate);
                    tv.setText("miércoles");
                }else if(day==4){
                    TextView tv = findViewById(R.id.txtDaydate);
                    tv.setText("jueves");
                }else if(day==5){
                    TextView tv = findViewById(R.id.txtDaydate);
                    tv.setText("viernes");
                }else if(day==6){
                    TextView tv = findViewById(R.id.txtDaydate);
                    tv.setText("sábado");
                }else if(day==7){
                    TextView tv = findViewById(R.id.txtDaydate);
                    tv.setText("domingo");
                }
                loadDailyAvailability();
            }
        });
    }

    private void loadDailyAvailability() {
        for(int i=6; i<24; i++) {
            String txtID = "txt_hour_" + i;
            int resID = getResources().getIdentifier(txtID, "id", getPackageName());
            TextView txtHour = findViewById(resID);
            txtHour.setTextColor(getResources().getColor(R.color.colorGray));
            txtHour.setBackground(null);
            if (availability.contains(activeDay + "_" + i)) {
                txtHour.setTextColor(getResources().getColor(R.color.colorWhite));
                txtHour.setBackground(getResources().getDrawable(R.drawable.btn_accent));
            }
        }
    }

    private void turnOffDays() {
        LinearLayout linearLayout = findViewById(R.id.lyt_days);
        int count = linearLayout.getChildCount();
        for(int i=0; i<count; i++) {
            Button btn = (Button) linearLayout.getChildAt(i);
            btn.setBackground(getResources().getDrawable(R.drawable.btn_white));
            btn.setTextColor(getResources().getColor(R.color.colorGray));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
