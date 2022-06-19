package com.peru.wafflecoach;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFormPsychotherapist extends Fragment {


    EditText txtName;
    EditText txtSurname;
    EditText txtLine;
    EditText txtPhone;
    ImageView btnSpecialties;
    TextView txtSpecialties;
    Button btnRequest;
    ServiceSession serviceSession;
    ServiceRest serviceRest;
    JSONArray categories;
    String[] stringSpecialties;
    boolean[] checkedSpecialties;
    JSONArray specialties = new JSONArray();
    ArrayList<Integer> selectedItems = new ArrayList<>();
    View root;

    public FragmentFormPsychotherapist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       root = inflater.inflate(R.layout.fragment_fragment_form_psychotherapist, container, false);

        getActivity().setTitle("Modo especialista");
        serviceSession = new ServiceSession(getContext());
        serviceRest = new ServiceRest(getContext());

        txtName = root.findViewById(R.id.edi_name);
        txtSurname = root.findViewById(R.id.edi_surname);
        txtLine = root.findViewById(R.id.txt_line);
        txtPhone = root.findViewById(R.id.edi_phone);
        btnRequest = root.findViewById(R.id.btn_request);
        btnSpecialties = root.findViewById(R.id.btn_specialties);
        txtSpecialties = root.findViewById(R.id.txt_specialties);

        getCategories();
        return root;
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
            stringSpecialties = new String[specialties.length()+1];
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        for(int m = 0; m < selectedItems.size(); m++) {
            selected = selected + coma + stringSpecialties[selectedItems.get(m)];
            coma = ", ";
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
                                    ServiceGlobal.alerta(getContext(), "Error solicitando validación.");
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    ServiceGlobal.alerta(getContext(), "Completar todos los campos");
                }
            }
        });
    }

}
