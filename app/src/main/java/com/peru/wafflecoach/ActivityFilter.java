package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityFilter extends AppCompatActivity {

    TextView txtMin;
    TextView txtMax;
    List<String> daySelected;
    List<String> langSelected;
    List<String> speSelected;
    JSONArray speList;

    int min = 0;
    int max = 400;

    JSONArray languages;
    JSONArray categories;
    ServiceRest serviceRest;
    String filEdad="";
    JSONArray speArray;
    Spinner spiSpecial1;
    Spinner spiSpecial2;
    LinearLayout lytSpecialties;
    boolean b1=true,b2=true,b3=true;
    LinearLayout lytLanguage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        spiSpecial1 = findViewById(R.id.spinner_spe1);
        spiSpecial2 = findViewById(R.id.spinner_spe2);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serviceRest = new ServiceRest(ActivityFilter.this);
        langSelected = new ArrayList<>();
        daySelected = new ArrayList<>();
        speSelected = new ArrayList<>();
        getLanguages();
    }

    private void getLanguages() {
        serviceRest.showDialog("Cargando filtros...");
        serviceRest.getJSONArray("language", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length()>0){
                    languages = response;
                }else{
                    //ocultar filtro idioma
                }
                getCategories();
            }
        });
    }

    private void getCategories() {
        serviceRest.getJSONArray("category", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                serviceRest.hideDialog();
                categories = response;
                try {
                    initFilters();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initFilters() throws JSONException {
        initRangeBar();
        initBtnDays();
        initBtnLang();
        initSpecialties();
        initBtnFilter();
        initBtnEdad();
    }

    private void initBtnEdad() {
        final Button btn30,btn3035,btn40;
        btn40=findViewById(R.id.btn40);
        btn3035 = findViewById(R.id.btn3035);
        btn30 = findViewById(R.id.btn30);
        btn30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn3035.setBackground(getResources().getDrawable(R.drawable.btn_white));
                btn3035.setTextColor(getResources().getColor(R.color.colorGray));
                btn40.setBackground(getResources().getDrawable(R.drawable.btn_white));
                btn40.setTextColor(getResources().getColor(R.color.colorGray));
                btn30.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                btn30.setTextColor(getResources().getColor(R.color.colorWhite));
                filEdad="30";

            }
        });
        btn3035.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(b1){
                    btn40.setBackground(getResources().getDrawable(R.drawable.btn_white));
                    btn40.setTextColor(getResources().getColor(R.color.colorGray));
                    btn30.setBackground(getResources().getDrawable(R.drawable.btn_white));
                    btn30.setTextColor(getResources().getColor(R.color.colorGray));

                    btn3035.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                    btn3035.setTextColor(getResources().getColor(R.color.colorWhite));
                    filEdad="3035";
                }
            }
        });
        btn40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     btn3035.setBackground(getResources().getDrawable(R.drawable.btn_white));
                     btn3035.setTextColor(getResources().getColor(R.color.colorGray));
                      btn30.setBackground(getResources().getDrawable(R.drawable.btn_white));
                      btn30.setTextColor(getResources().getColor(R.color.colorGray));

                     btn40.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                     btn40.setTextColor(getResources().getColor(R.color.colorWhite));
                filEdad="40";

            }
        });

    }
    public  int getEdad(Date fechaNacimiento, Date fechaActual) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int dIni = Integer.parseInt(formatter.format(fechaNacimiento));
        int dEnd = Integer.parseInt(formatter.format(fechaActual));
        int age = (dEnd-dIni)/10000;
        return age;
    }
    private void initSpecialties() throws JSONException {
       /* spiSpecial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                speList = new JSONArray();
                try {
                for(int i=0;i<categories.length();i++){
                    for (int j = 0;j < (categories.getJSONObject(i).getJSONArray("specialties").length());j++){

                            speList.put(categories.getJSONObject(i).getJSONArray("specialties").getJSONObject(j));


                    }
                }
                    String[] names = new String[speList.length()];
                    for (int  i = 0 ; i<speList.length();i++){
                        names[i] = speList.getJSONObject(i).getString("name");
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFilter.this);
                    builder.setTitle("Seleccionar especialidades");
                    boolean[] check = new boolean [speList.length()];
                    builder.setSingleChoiceItems(names, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                speSelected.add(speList.getJSONObject(which).getString("id"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
*/
           speList = new JSONArray();
           final JSONArray speList2 = new JSONArray();


           for (int j = 0;j < (categories.getJSONObject(0).getJSONArray("specialties").length());j++) {

                    speList.put(categories.getJSONObject(0).getJSONArray("specialties").getJSONObject(j));

           }
           for (int j = 0;j < (categories.getJSONObject(1).getJSONArray("specialties").length());j++) {

                  speList2.put(categories.getJSONObject(1).getJSONArray("specialties").getJSONObject(j));

           }
        Log.d("TAG66",speList2.toString());
            String[] names1 = new String[speList.length()+1];
            String[] names2 = new String[speList2.length()+1];

             names1[0] = "Seleccione especialidad de psicoterapia";
             names2[0] = "Seleccione especialidad de coaching";
            for (int  i = 1 ; i<speList.length()+1;i++){
                names1[i] = speList.getJSONObject(i-1).getString("name");
            }

             for (int  i = 1 ; i<speList2.length()+1;i++){
                  names2[i] = speList2.getJSONObject(i-1).getString("name");
             }
        Log.d("TAG66",names2[1]);

               lytSpecialties = findViewById(R.id.lyt_specialties);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(ActivityFilter.this,
                R.layout.item_spinner, names1);

        adapter.setDropDownViewResource(R.layout.item_spinner);

         ArrayAdapter<String> adapter2 = new ArrayAdapter<>(ActivityFilter.this,
                  R.layout.item_spinner, names2);

         adapter2.setDropDownViewResource(R.layout.item_spinner);
        spiSpecial1.setAdapter(adapter);
        spiSpecial2.setAdapter(adapter2);


        spiSpecial1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if(position != 0) {
                        spiSpecial2.setSelection(0,true);

                        speSelected.clear();
                        speSelected.add(speList.getJSONObject(position-1).getString("id"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spiSpecial2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if(position != 0) {

                        spiSpecial1.setSelection(0,true);

                        speSelected.clear();

                        speSelected.add(speList2.getJSONObject(position-1).getString("id"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spiSpecial1);

            // Set popupWindow height to 500px
            popupWindow.setHeight(500);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
      /*  for(int i = 0; i < categories.length(); i++){
            LinearLayout layoutSpecialty = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_filter_specialty, null);
            FrameLayout lytSeparator = layoutSpecialty.findViewById(R.id.lyt_separator);
            TextView txtCategory = layoutSpecialty.findViewById(R.id.txt_category);
            GridLayout gridSpecialties = layoutSpecialty.findViewById(R.id.grid_specialties);

            try {
                JSONObject category = categories.getJSONObject(i);
                txtCategory.setText(category.getString("name"));

                JSONArray specialties = category.getJSONArray("specialties");
                for(int j = 0; j < specialties.length(); j++){

                    if(i == 0){
                        lytSeparator.setVisibility(View.GONE);
                    }

                    TextView btnSpecialty = new TextView(ActivityFilter.this);
                    btnSpecialty.setTextSize(12);
                    btnSpecialty.setGravity(Gravity.CENTER);
                    GridLayout.LayoutParams params= new GridLayout.LayoutParams(GridLayout.spec(
                            GridLayout.UNDEFINED,GridLayout.FILL,1f),
                            GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f));
                    params.height = (int) getResources().getDimension(R.dimen.btn);
                    params.width  = 0;
                    params.setMargins(0,0,0,(int) getResources().getDimension(R.dimen.space) / 2);
                    btnSpecialty.setLayoutParams(params);
                    final JSONObject specialty = (JSONObject) specialties.get(j);
                    btnSpecialty.setText(specialty.getString("name"));

                    btnSpecialty.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            turnOffSpecialties();
                            TextView item = (TextView) view;
                            item.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                            item.setTextColor(getResources().getColor(R.color.colorWhite));
                            try {
                                speSelected.add(specialty.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    gridSpecialties.addView(btnSpecialty);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0, (int) getResources().getDimension(R.dimen.space));
            layoutSpecialty.setLayoutParams(params);
            lytSpecialties.addView(layoutSpecialty);*/

    }

    private void turnOffSpecialties() {
        for(int i = 0; i < lytSpecialties.getChildCount(); i++) {
            LinearLayout layoutSpecialty = (LinearLayout) lytSpecialties.getChildAt(i);
            GridLayout gridSpecialties = layoutSpecialty.findViewById(R.id.grid_specialties);
            for(int j = 0; j < gridSpecialties.getChildCount(); j++) {
                TextView specialty = (TextView) gridSpecialties.getChildAt(j);
                specialty.setBackground(null);
                specialty.setTextColor(getResources().getColor(R.color.colorGray));
                speSelected.clear();
            }
        }
    }

    private void initBtnFilter() {
        Button btnFilter = findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFilter();
            }
        });
    }

    private void submitFilter() {
        Intent intent = new Intent();
        intent.putExtra("min", String.valueOf(min));
        intent.putExtra("max", String.valueOf(max));
        intent.putExtra("days", TextUtils.join(",", daySelected));
        intent.putExtra("languages", TextUtils.join(",", langSelected));
        intent.putExtra("specialties", TextUtils.join(",", speSelected));
        intent.putExtra("filEdad",filEdad);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initBtnLang() {
        lytLanguage = findViewById(R.id.lyt_language);
        for(int i = 0; i < languages.length(); i++) {
            try {
                ContextThemeWrapper newContext = new ContextThemeWrapper(ActivityFilter.this, R.style.btnSmWhite);
                Button btnLanguage = new Button(newContext);
                JSONObject language = languages.getJSONObject(i);

                btnLanguage.setText(language.getString("name"));
                btnLanguage.setBackground(getResources().getDrawable(R.drawable.btn_white));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( (int) getResources().getDimension(R.dimen.len), (int) getResources().getDimension(R.dimen.btn_sm));
                params.setMarginEnd(16);
                btnLanguage.setLayoutParams(params);
                btnLanguage.setAllCaps(false);
                btnLanguage.setTextSize(12);
                final String idLanguage = language.getString("id");
                btnLanguage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button btn = (Button) view;
                        turnOfLen();

                        if(langSelected.contains(idLanguage)){
                            btn.setBackground(getResources().getDrawable(R.drawable.btn_white));
                            btn.setTextColor(getResources().getColor(R.color.colorGray));
                            langSelected.remove(idLanguage);
                        }else{
                            btn.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                            btn.setTextColor(getResources().getColor(R.color.colorWhite));
                            langSelected.add(idLanguage);
                        }
                    }
                });

                lytLanguage.addView(btnLanguage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void turnOfLen() {
        for(int j = 0; j < lytLanguage.getChildCount(); j++) {
            Button specialty = (Button) lytLanguage.getChildAt(j);
            specialty.setBackground((getResources().getDrawable(R.drawable.btn_white)));
            specialty.setTextColor(getResources().getColor(R.color.colorGray));
            langSelected.clear();
        }
    }

    private void initBtnDays() {
        LinearLayout lytDays = findViewById(R.id.lyt_days);
        for(int i = 0; i < lytDays.getChildCount(); i++) {
            final int position = i;
            Button btnDay = (Button) lytDays.getChildAt(i);
            btnDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button btn = (Button) view;
                    String day = String.valueOf(position+1);
                    if(daySelected.contains(day)){
                        btn.setBackground(getResources().getDrawable(R.drawable.btn_white));
                        btn.setTextColor(getResources().getColor(R.color.colorGray));
                        daySelected.remove(day);
                    }else{
                        btn.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                        btn.setTextColor(getResources().getColor(R.color.colorWhite));
                        daySelected.add(day);
                    }
                }
            });
        }
    }

    private void initRangeBar() {
        txtMin = findViewById(R.id.txt_min);
        txtMax = findViewById(R.id.txt_max);
        CrystalRangeSeekbar rangePrice = findViewById(R.id.range_price);
        rangePrice.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                min = minValue.intValue();
                max = maxValue.intValue();
                txtMin.setText("min: S/ " + minValue);
                txtMax.setText("max: S/ " + maxValue);
            }
        });
        rangePrice.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
