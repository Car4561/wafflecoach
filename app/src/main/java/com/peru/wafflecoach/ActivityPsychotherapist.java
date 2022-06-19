package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityPsychotherapist extends AppCompatActivity {

    JSONObject psychotherapist = new JSONObject();
    ViewPager2 viewPager;
    JSONObject attributes;

    ImageView imgPhoto;
    TextView txtLine;
    FrameLayout lytOverlay;
    LinearLayout lytControl;
    Long  mLastClickTime=0l;

    TextView txtDescription;
    TextView txtExperience;
    TextView txtCertification;
    TextView txtSpecialties;
    TextView txtPrice;

    String fullName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychotherapist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imgPhoto = findViewById(R.id.img_specialist);
        txtLine = findViewById(R.id.txt_line);
        viewPager = findViewById(R.id.pager_photo);
        lytOverlay = findViewById(R.id.lyt_overlay);
        lytControl = findViewById(R.id.lyt_control);

        txtDescription = findViewById(R.id.txt_description);
        txtExperience = findViewById(R.id.txt_experience);
        txtCertification = findViewById(R.id.txt_certification);
        txtSpecialties = findViewById(R.id.txt_specialties);
        txtPrice = findViewById(R.id.txt_price);



        if(getIntent().hasExtra("psychotherapist")) {
            try {
                String extra = getIntent().getStringExtra("psychotherapist");
                psychotherapist = new JSONObject(extra);
                attributes = psychotherapist.getJSONObject("attributes");
                initPyschotherapistData();
                initPhotoPyschotherapist();
                initBtnReserve();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initBtnReserve() {
        Button btnReserve = findViewById(R.id.btn_reserve);
        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                ServiceSession session = new ServiceSession(ActivityPsychotherapist.this);

                if(session.getUserAttribute("phone")  == null || session.getUserAttribute("phone").isEmpty()){
                    Toast.makeText(ActivityPsychotherapist.this, "Debe ingresar su número de celular para hacer reservas", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ActivityEditProfile.class));
                    finish();
                }else {
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent(ActivityPsychotherapist.this, la.neurometrics.cobi.ActivityScheduleAppointment.class);
                    intent.putExtra("user", psychotherapist.toString());
                    startActivity(intent);
                }
            }
        });
    }

    private void initPyschotherapistData() {
        try {
          //  fullName = attributes.get("name").toString().split(" ")[0] + " " + attributes.get("surname").toString().split(" ")[0];
            fullName = attributes.getString("name") + " " + attributes.getString("surname");
            getSupportActionBar().setTitle(fullName);
            txtLine.setText(attributes.getString("line"));
            TextView txtCountry = findViewById(R.id.txt_country);
            ServiceSession serviceSession = new ServiceSession(ActivityPsychotherapist.this);
            if(psychotherapist.has("country"))
                txtCountry.setText(psychotherapist.getString("country"));
            if(attributes.has("description")){
                txtDescription.setText(attributes.getString("description"));
            }
            if(attributes.has("year")){
                int year = Integer.valueOf(attributes.getString("year"));
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int dif = currentYear - year;
                String s = "";
                if(dif > 0) {
                    if(dif > 1)
                        s = "s";
                    txtExperience.setText(dif + " año" + s + " de experiencia");
                }else{
                    txtExperience.setText("Menos de un año de experiencia");
                }
            }
            if(attributes.has("certification")){
                txtCertification.setText(attributes.getString("certification"));
            }
            if(attributes.has("price")){
                txtPrice.setText("S/ " + attributes.getString("price") + " por sesión");
            }
            if(psychotherapist.has("specialties")){
                String strSpecialties = "";
                String separator = "";
                JSONArray specialties = psychotherapist.getJSONArray("specialties");

                for(int i = 0; i < specialties.length(); i++) {
                    JSONObject specialty = (JSONObject) specialties.get(i);
                    strSpecialties = strSpecialties + separator + specialty.getString("name");
                    separator = "\n";
                }

                txtSpecialties.setText(strSpecialties);
            }
            ServiceRest serviceRest = new ServiceRest(this);
            JSONObject bodyPut = new JSONObject();
            bodyPut.put("view",1);
            bodyPut.put("idPsychotherapist",psychotherapist.getString("id"));
            serviceRest.put("psychotherapist", bodyPut, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    la.neurometrics.cobi.ServiceGlobal.console(response.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initPhotoPyschotherapist() {
        if(attributes.has("video")) {
            initPagerWithVideo();
          /*  AppBarLayout appBarLayout  = findViewById(R.id.app_bar);
            ViewGroup.LayoutParams params = appBarLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            appBarLayout.setLayoutParams(params);*/
        }else{
            initPhoto();

        }
    }

    private void initPhoto() {
        viewPager.setVisibility(View.GONE);
        try {
            la.neurometrics.cobi.ServiceGlobal.loadImage(attributes.getString("photo"), imgPhoto);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initPagerWithVideo() {
        imgPhoto.setVisibility(View.GONE);
        lytControl.setVisibility(View.VISIBLE);
        try {
            ArrayList<String> urls = new ArrayList<>();
            urls.add(attributes.getString("photo"));
            urls.add(attributes.getString("video"));
            viewPager.setAdapter(new AdapterPagerProfile(ActivityPsychotherapist.this, urls,true));
            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    ImageView img01 = (ImageView) lytControl.getChildAt(0);
                    ImageView img02 = (ImageView) lytControl.getChildAt(1);
                    if(position == 1){
                        txtLine.setVisibility(View.GONE);
                        lytOverlay.setVisibility(View.GONE);
                        img01.setColorFilter(getResources().getColor(R.color.colorGray));
                        img02.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    }else{
                        txtLine.setVisibility(View.VISIBLE);
                        lytOverlay.setVisibility(View.VISIBLE);
                        getSupportActionBar().setTitle(fullName);
                        img01.setColorFilter(getResources().getColor(R.color.colorPrimary));
                        img02.setColorFilter(getResources().getColor(R.color.colorGray));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
