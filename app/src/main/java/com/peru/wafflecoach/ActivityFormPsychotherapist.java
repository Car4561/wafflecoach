package com.peru.wafflecoach;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityFormPsychotherapist extends AppCompatActivity {

    EditText txtName;
    EditText txtSurname;
    EditText txtLine;
    EditText txtPhone;
    ImageView btnSpecialties;
    TextView txtSpecialties;
    Button btnRequest;
    la.neurometrics.cobi.ServiceSession serviceSession;
    la.neurometrics.cobi.ServiceRest serviceRest;
    JSONArray categories;
    String[] stringSpecialties;
    boolean[] checkedSpecialties;
    JSONArray specialties = new JSONArray();
    ArrayList<Integer> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_psychotherapist);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Modo especialista");
        serviceSession = new la.neurometrics.cobi.ServiceSession(ActivityFormPsychotherapist.this);
        serviceRest = new la.neurometrics.cobi.ServiceRest(ActivityFormPsychotherapist.this);

        txtName = findViewById(R.id.edi_name);
        txtSurname = findViewById(R.id.edi_surname);
        txtLine = findViewById(R.id.txt_line);
        txtPhone = findViewById(R.id.edi_phone);
        btnRequest = findViewById(R.id.btn_request);
        btnSpecialties = findViewById(R.id.btn_specialties);
        txtSpecialties = findViewById(R.id.txt_specialties);
      initForm();

     //   getCategories();


    }

    private void getCategories() {
        serviceRest.showDialog("Cargando especialidades disponibles...");
        serviceRest.getJSONArray("category", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                serviceRest.hideDialog();
                categories = response;
                initForm();
                setSpecialties();
            }
        });
    }

    private void setSpecialties() {
        try {
            for(int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                if(category.has("specialties")){
                    for(int j = 0; j < category.getJSONArray("specialties").length(); j++) {
                        specialties.put(category.getJSONArray("specialties").get(j));
                    }
                }
            }

            stringSpecialties = new String[specialties.length()];
            for(int n = 0; n < specialties.length(); n++) {
                JSONObject specialty = specialties.getJSONObject(n);
                stringSpecialties[n] = specialty.getString("name");
            }

            checkedSpecialties= new boolean [specialties.length()];

        } catch (JSONException e) {
            e.printStackTrace();
        }
        initSpecialtiesFromUser();
    }

    private void initSpecialtiesFromUser() {
        ArrayList<HashMap> userSpecialties = serviceSession.getFromSession("specialties");
        for(int i = 0; i < userSpecialties.size(); i++){
            HashMap specialty = userSpecialties.get(i);
            selectedItems.add(Integer.parseInt(specialty.get("id").toString()));
        }
        setTextSpecialtiesSelected();
    }

    private void initForm() {

        txtName.setText(serviceSession.getUserAttribute("name"));
        txtSurname.setText(serviceSession.getUserAttribute("surname"));
        txtPhone.setText(serviceSession.getUserAttribute("phone"));
        txtLine.setText(serviceSession.getUserAttribute("line"));

        String profile = serviceSession.getFromSession("profile");
        String state = serviceSession.getFromSession("state");

        if(profile.equals("psychotherapist") && state.equals("2")){
            txtName.setEnabled(false);
            txtSurname.setEnabled(false);
            txtPhone.setEnabled(false);
            txtLine.setEnabled(false);

            btnRequest.setBackground(getResources().getDrawable(R.drawable.btn_accent));
            btnRequest.setText("Esperando validación");
            btnRequest.setEnabled(false);

        }else{
           initSpecialtyPopup();
           initBtnRequest();
        }
    }

    private void initSpecialtyPopup() {

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityFormPsychotherapist.this);
                builder.setTitle("Seleccionar especialidades");
                    builder.setMultiChoiceItems(stringSpecialties, checkedSpecialties, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked) {
                            if(!selectedItems.contains(position)) {
                                selectedItems.add(position);
                            }
                        }else if(selectedItems.contains(position)) {
                                selectedItems.remove(selectedItems.indexOf(position));
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setTextSpecialtiesSelected();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
        btnSpecialties.setOnClickListener(onClickListener);
        txtSpecialties.setOnClickListener(onClickListener);
    }

    private void setTextSpecialtiesSelected() {
        String selected = "";
        String coma = "";
        List<String> print;
        print = new ArrayList<>();
        for (int i = 0 ;i<stringSpecialties.length;i++){
            print.add(stringSpecialties[i]);
        }

        Log.d("TAG", String.valueOf(print + "size: "+ print.size()) );

        Log.d("TAG", String.valueOf(selectedItems+"size: "+ selectedItems.size()));
        for(int m = 0; m < selectedItems.size(); m++) {
            try {
                selected = selected + coma + stringSpecialties[selectedItems.get(m)];
                coma = ", ";
                Log.d("TAG", String.valueOf(selectedItems.get(m)));
            }catch (Exception e){
                e.printStackTrace();
                Log.d("TAG", String.valueOf(selected));

                Log.d("TAG", String.valueOf(selectedItems.get(m)));
            }
        }
        txtSpecialties.setText(selected);
    }

    private void initBtnRequest() {
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtName.getText().toString().equals("") &&
                        !txtSurname.getText().toString().equals("") &&
                        !txtPhone.getText().toString().equals("")) {
                    try {
                        JSONObject bodyPost = new JSONObject();
                        bodyPost.put("name", txtName.getText());
                        bodyPost.put("surname", txtSurname.getText());
                        bodyPost.put("phone", txtPhone.getText());
                        bodyPost.put("line", txtLine.getText());

                        String selected = "";
                        String coma = "";
                     /*   for(int i = 0; i < selectedItems.size(); i++) {
                            JSONObject specialty = specialties.getJSONObject(selectedItems.get(i));
                            selected = selected + coma + specialty.getString("id");
                            coma = ",";
                        }
                        bodyPost.put("specialties", selected);*/
                        serviceRest.showDialog("Registrando...");
                        serviceRest.post("psychotherapist", bodyPost, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    serviceRest.hideDialog();
                                    String token = response.getString("token");
                                    serviceSession.login(token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    la.neurometrics.cobi.ServiceGlobal.alerta(ActivityFormPsychotherapist.this, "Error solicitando validación.");
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    la.neurometrics.cobi.ServiceGlobal.alerta(ActivityFormPsychotherapist.this, "Completar todos los campos");
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
