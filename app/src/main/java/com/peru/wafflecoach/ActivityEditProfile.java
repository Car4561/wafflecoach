package com.peru.wafflecoach;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ActivityEditProfile extends AppCompatActivity {

    la.neurometrics.cobi.ServiceSession serviceSession;
    la.neurometrics.cobi.ServiceRest serviceRest;
    la.neurometrics.cobi.ServiceUser serviceUser;
    static final int GALLERY_REQUEST_CODE = 1;

    ImageView imgPic;
    ImageView btnPic;
    EditText txtName;
    EditText txtSurname;
    EditText txtEmail;
    EditText txtPhone;
    Spinner spiCountry;
    ConstraintLayout lytPsychotherapist;
    EditText txtLine;
    EditText txtDescription;
    TextView txtSpecialties;
    TextView txtDelete;
    TextView txtDeletePatient;

    ImageView btnSpecialties;
    Spinner spiYear;
    EditText txtCertification;
    TextView txtLanguage;
    ImageView btnLanguage;
    EditText txtPrice;
    EditText txtVideo;

    Button btnSave;

    private String[] nameCountries;
    private String[] idCountries;
    JSONArray categories;
    private String[] stringSpecialties;
    private boolean[] checkedSpecialties;
    JSONArray specialties = new JSONArray();
    ArrayList<Integer> specialtiesSelected = new ArrayList<>();

    private String[] stringLanguages;
    private boolean[] checkedLanguages;
    JSONArray languages = new JSONArray();
    ArrayList<Integer> languagesSelected = new ArrayList<>();

    private String country = "";
    private String encodedPhoto = "";
    private String year = "";
    private ArrayList<Integer> years;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Editar perfil");

        imgPic = findViewById(R.id.img_pic);
        btnPic = findViewById(R.id.btn_pic);
        txtName = findViewById(R.id.txt_name);
        txtSurname = findViewById(R.id.txt_surname);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);
        spiCountry = findViewById(R.id.spi_country);
        lytPsychotherapist = findViewById(R.id.lyt_psychotherapist);
        txtLine = findViewById(R.id.txt_line);
        txtDescription = findViewById(R.id.txt_description);
        txtSpecialties = findViewById(R.id.txt_specialties);
        btnSpecialties = findViewById(R.id.btn_specialties);
        spiYear = findViewById(R.id.spi_year);
        txtCertification = findViewById(R.id.txt_certification);
        txtLanguage = findViewById(R.id.txt_language);
        btnLanguage = findViewById(R.id.btn_language);
        txtPrice = findViewById(R.id.txt_price);
        txtVideo = findViewById(R.id.txt_video);
        txtDelete = findViewById(R.id.lbl_delete);
        txtDeletePatient = findViewById(R.id.lbl_deletePatient);

        btnSave = findViewById(R.id.btn_save);

        serviceSession = new la.neurometrics.cobi.ServiceSession(ActivityEditProfile.this);
        serviceRest = new la.neurometrics.cobi.ServiceRest(ActivityEditProfile.this);
        serviceUser = new la.neurometrics.cobi.ServiceUser(ActivityEditProfile.this);

        serviceRest.showDialog("Cargando valores...");

        if(serviceSession.getUserAttribute("photo") != null)
            la.neurometrics.cobi.ServiceGlobal.loadImage(serviceSession.getUserAttribute("photo"), imgPic);

        initBtnPic();
        initBtnDelete();
        initYearSpinner();
        initCountryList();

    }

    private void initBtnDelete() {
        View.OnClickListener delete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ActivityEditProfile.this)
                        .setTitle("¿Seguro que desea eliminar su cuenta?")
                        .setPositiveButton("Eliminar cuenta", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject body = new JSONObject();
                                try {
                                    body.put("state",0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                serviceRest.delete("user",new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        serviceRest.hideDialog();
                                        Toast.makeText(ActivityEditProfile.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                        serviceSession.logout();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        };
        txtDelete.setOnClickListener(delete);
        txtDeletePatient.setOnClickListener(delete);

    }

    private void initBtnPic() {
        btnPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(ActivityEditProfile.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityEditProfile.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
           //     ServiceGlobal.alerta(ActivityEditProfile.this, "Una explicación");
                ActivityCompat.requestPermissions(ActivityEditProfile.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(ActivityEditProfile.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            pickFromGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GALLERY_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    pickFromGallery();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "No se puede elegir una foto sin su permiso.");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void pickFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    //imgPic.setImageURI(selectedImage);
                    try {
                        InputStream is = getContentResolver().openInputStream(selectedImage);

                        if(is != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                            if (bitmap.getHeight() > 800 || bitmap.getWidth() > 800) {
                                final int THUMBNAIL_HEIGHT = 800;//48
                                Float width = new Float(bitmap.getWidth());
                                Float height = new Float(bitmap.getHeight());
                                Float ratio = width / height;
                                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (THUMBNAIL_HEIGHT * ratio), THUMBNAIL_HEIGHT, false);
                            }
                            imgPic.setImageBitmap(bitmap);
                            ByteArrayOutputStream baosPhoto = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baosPhoto);
                            byte[] imageBytesPhoto = baosPhoto.toByteArray();
                            encodedPhoto = Base64.encodeToString(imageBytesPhoto, Base64.DEFAULT);

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
    }

    private void initValuesFromSession() {

        txtEmail.setText(serviceSession.getFromSession("email").toString());
        txtName.setText(serviceSession.getUserAttribute("name"));
        txtSurname.setText(serviceSession.getUserAttribute("surname"));
        txtPhone.setText(serviceSession.getUserAttribute("phone"));
        country = serviceSession.getFromSession("idCountry");
        spiCountry.setSelection(Arrays.asList(idCountries).indexOf(country));

        if(serviceSession.getFromSession("profile").equals("patient")) {
            lytPsychotherapist.setVisibility(View.GONE);

            initBtnSavePatient();
        }else{

            TextView lblPhone = findViewById(R.id.lbl_phone);
            lblPhone.setText("Teléfono: *");
            txtLine.setText(serviceSession.getUserAttribute("line"));
            txtDescription.setText(serviceSession.getUserAttribute("description"));
            if(serviceSession.getUserAttribute("year") != null)
                spiYear.setSelection(years.indexOf(Integer.valueOf(serviceSession.getUserAttribute("year"))));
            txtCertification.setText(serviceSession.getUserAttribute("certification"));
            txtPrice.setText(serviceSession.getUserAttribute("price"));
            txtVideo.setText(serviceSession.getUserAttribute("video"));
            txtDeletePatient.setVisibility(View.GONE);
            ArrayList<HashMap> userSpecialties = serviceSession.getFromSession("specialties");
            if (userSpecialties != null) {
                for (int i = 0; i < userSpecialties.size(); i++) {
                    HashMap hashSpecialty = userSpecialties.get(i);
                    for (int j = 0; j < specialties.length(); j++) {
                        try {
                            JSONObject jsonSpecialty = specialties.getJSONObject(j);
                            if (jsonSpecialty.getString("id").equals(hashSpecialty.get("id"))) {
                                specialtiesSelected.add(j);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setTextSpecialtiesSelected();
            }

            ArrayList<HashMap> userLanguages = serviceSession.getFromSession("languages");
            if (userLanguages != null) {
                for (int i = 0; i < userLanguages.size(); i++) {
                    HashMap hashLanguage = userLanguages.get(i);
                    for (int j = 0; j < languages.length(); j++) {
                        try {
                            JSONObject jsonLanguage = languages.getJSONObject(j);
                            if (jsonLanguage.getString("id").equals(hashLanguage.get("id"))) {
                                languagesSelected.add(j);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                setTextLanguagesSelected();
            }
            initBtnSavePsychotherapist();
        }

        serviceRest.hideDialog();
    }

    private boolean validateInfoPatient() {
        if(txtName.getText().toString().equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar su nombre");
            txtName.requestFocus();
            return false;
        }
        if(txtSurname.getText().toString().equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar su apellido");
            txtSurname.requestFocus();
            return false;
        }
        if(txtEmail.getText().toString().equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar su correo electrónico");
            txtEmail.requestFocus();
            return false;
        }
        if(country.equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe seleccionar su país");
            spiCountry.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateInfoPsychotherapist() {
        if(txtPhone.getText().toString().equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar su teléfono");
            txtPhone.requestFocus();
            return false;
        }
        if(txtLine.getText().toString().equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar su descripción corta");
            txtLine.requestFocus();
            return false;
        }
        if(txtDescription.getText().toString().equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar su descripción");
            txtDescription.requestFocus();
            return false;
        }
        if(specialtiesSelected.size() == 0) {
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe seleccionar sus especialidades");
            openPopupSpecialties();
            return false;
        }
        if(year.equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar el año desde que ejerce");
            spiYear.requestFocus();
            return false;
        }
        if(txtCertification.getText().toString().equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar su certificación");
            txtCertification.requestFocus();
            return false;
        }
        if(languagesSelected.size() == 0) {
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe seleccionar los idiomas que habla");
            openPopupLanguages();
            return false;
        }
        if(txtPrice.getText().toString().equals("")){
            la.neurometrics.cobi.ServiceGlobal.alerta(ActivityEditProfile.this, "Debe ingresar su precio por sesión");
            txtPrice.requestFocus();
            return false;
        }
        return true;
    }

    private void initBtnSavePatient() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInfoPatient()){
                    //HACER EL PUT
                    try {
                        JSONObject bodyPut = new JSONObject();
                        bodyPut.put("email", txtEmail.getText().toString().trim());
                        bodyPut.put("country", country);

                        JSONObject attributes = new JSONObject();
                        attributes.put("name", txtName.getText().toString().trim());
                        attributes.put("surname", txtSurname.getText().toString().trim());
                        attributes.put("phone", txtPhone.getText().toString().trim());
                        attributes.put("edited_by", "app");


                        if(!encodedPhoto.equals("")){
                            attributes.put("photo", encodedPhoto);
                        }
                        bodyPut.put("attributes", attributes);
                        la.neurometrics.cobi.ServiceGlobal.console(bodyPut.toString());
                        serviceUser.put(bodyPut, true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initBtnSavePsychotherapist() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInfoPatient() && validateInfoPsychotherapist()){
                    //HACER EL PUT
                    try {
                        JSONObject bodyPut = new JSONObject();
                        bodyPut.put("email", txtEmail.getText().toString().trim());
                        bodyPut.put("country", country);

                        JSONObject attributes = new JSONObject();
                        if(!encodedPhoto.equals("")){
                            attributes.put("photo", encodedPhoto);
                        }
                        attributes.put("name", txtName.getText().toString().trim());
                        attributes.put("surname", txtSurname.getText().toString().trim());
                        attributes.put("phone", txtPhone.getText().toString().trim());
                        attributes.put("line", txtLine.getText().toString().trim());
                        attributes.put("description", txtDescription.getText().toString().trim());
                        attributes.put("year", year);
                        attributes.put("certification", txtCertification.getText().toString().trim());
                        attributes.put("price", txtPrice.getText().toString().trim());
                        attributes.put("video", txtVideo.getText().toString().trim());
                        attributes.put("edited_by", "app");

                        bodyPut.put("attributes", attributes);

                        //specialties
                        String ids = "";
                        String coma = "";
                        for(int i = 0; i < specialtiesSelected.size(); i++) {
                            JSONObject specialty = specialties.getJSONObject(specialtiesSelected.get(i));
                            ids = ids + coma + specialty.getString("id");
                            coma = ",";
                        }
                        bodyPut.put("specialties", ids);

                        //languages
                        ids = "";
                        coma = "";
                        for(int i = 0; i < languagesSelected.size(); i++) {
                            JSONObject language = languages.getJSONObject(languagesSelected.get(i));
                            ids = ids + coma + language.getString("id");
                            coma = ",";
                        }
                        bodyPut.put("languages", ids);

                        la.neurometrics.cobi.ServiceGlobal.console(bodyPut.toString());
                        serviceUser.put(bodyPut, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initLanguages() {
        serviceRest.getJSONArray("language", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    languages = response;
                    try {
                        stringLanguages = new String[languages.length()];
                        for (int n = 0; n < languages.length(); n++) {
                            JSONObject language = languages.getJSONObject(n);
                            stringLanguages[n] = language.getString("name");

                        }
                        checkedLanguages = new boolean[languages.length()];
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    initLanguagePopup();
                    initValuesFromSession();
                }
            }
        });
    }

    private void initLanguagePopup() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopupLanguages();
            }
        };
        btnLanguage.setOnClickListener(onClickListener);
        txtLanguage.setOnClickListener(onClickListener);
    }

    private void openPopupLanguages() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEditProfile.this);
        builder.setTitle("Seleccionar idiomas");
        builder.setMultiChoiceItems(stringLanguages, checkedLanguages, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!languagesSelected.contains(position)) {
                        languagesSelected.add(position);
                    }
                } else if (languagesSelected.contains(position)) {
                    languagesSelected.remove(languagesSelected.indexOf(position));
                }
            }
        });
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setTextLanguagesSelected();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setTextLanguagesSelected() {
        String selected = "";
        String coma = "";
        for (int m = 0; m < languagesSelected.size(); m++) {
            selected = selected + coma + stringLanguages[languagesSelected.get(m)];
            coma = ", ";
            checkedLanguages[languagesSelected.get(m)] = true;
        }
        txtLanguage.setText(selected);
    }

    private void initSpecialties() {
        serviceRest.getJSONArray("category", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                serviceRest.hideDialog();
                categories = response;
                setSpecialties();
                initLanguages();
            }
        });
    }

    private void setSpecialties() {
        try {
            for (int i = 0; i < categories.length(); i++) {
                JSONObject category = categories.getJSONObject(i);
                if (category.has("specialties"))
                    for (int j = 0; j < category.getJSONArray("specialties").length(); j++)
                        specialties.put(category.getJSONArray("specialties").get(j));
            }
            stringSpecialties = new String[specialties.length()];
            for (int n = 0; n < specialties.length(); n++) {
                JSONObject specialty = specialties.getJSONObject(n);
                stringSpecialties[n] = specialty.getString("name");
            }
            checkedSpecialties = new boolean[specialties.length()];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initSpecialtyPopup();
    }

    private void initSpecialtyPopup() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPopupSpecialties();
            }
        };
        btnSpecialties.setOnClickListener(onClickListener);
        txtSpecialties.setOnClickListener(onClickListener);
    }

    private void openPopupSpecialties() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEditProfile.this);
        builder.setTitle("Seleccionar especialidades");
        builder.setMultiChoiceItems(stringSpecialties, checkedSpecialties, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    if (!specialtiesSelected.contains(position)) {
                        specialtiesSelected.add(position);
                    }
                } else if (specialtiesSelected.contains(position)) {
                    specialtiesSelected.remove(specialtiesSelected.indexOf(position));
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

    private void setTextSpecialtiesSelected() {
        String selected = "";
        String coma = "";
        for (int m = 0; m < specialtiesSelected.size(); m++) {
            selected = selected + coma + stringSpecialties[specialtiesSelected.get(m)];
            coma = ", ";
            checkedSpecialties[specialtiesSelected.get(m)] = true;
        }
        txtSpecialties.setText(selected);
    }

    private void initCountryList() {
        serviceRest.getJSONArray("country", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                nameCountries = new String[response.length()];
                idCountries = new String[response.length()];
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        idCountries[i] = item.getString("id");
                        nameCountries[i] = item.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                initCountrySpinner();
                initSpecialties();
            }
        });
    }

    private void initCountrySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityEditProfile.this,
                R.layout.item_spinner, nameCountries);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spiCountry.setAdapter(adapter);
        spiCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                country = idCountries[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initYearSpinner() {
        years = new ArrayList<>();
        for(int i = 2019; i >= 1960; i--){
            years.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(ActivityEditProfile.this,
                R.layout.item_spinner, years);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spiYear.setAdapter(adapter);
        spiYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                year = String.valueOf(years.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}
