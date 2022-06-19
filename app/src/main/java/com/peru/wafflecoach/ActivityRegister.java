package com.peru.wafflecoach;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Response;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityRegister extends AppCompatActivity {

    private String[] nameCountries;
    private String[] idCountries;
    private String country = "0";
    boolean login=false,login1=false;
    EditText txtFechNaci;
    Calendar myCalendar = Calendar.getInstance();
    private int edad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        la.neurometrics.cobi.ServiceSession session = new la.neurometrics.cobi.ServiceSession(this);
        session.clearSession();

        ActionBar actionBar  = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();

        initCountryList();
        initTerms();
    }
    private void actualizarInput() {
        String formatoDeFecha = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(formatoDeFecha);

        txtFechNaci.setText(sdf.format(myCalendar.getTime()));
    }
    private void initTerms() {
        String Car = " d";
        TextView txtTerms = findViewById(R.id.txt_terms);
        String sourceString = "Al iniciar sesión acepto los <a href= https://www.cobiapp.com/Terminos-y-condiciones.html><u>Términos y Condiciones</u></a> y la <a href= https://www.cobiapp.com/Politica-de-privacidad.html ><u>Política de Privacidad.</u></a>";
        txtTerms.setText(Html.fromHtml(sourceString));
        txtTerms.setMovementMethod(LinkMovementMethod.getInstance());

       /* txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(ActivityRegister.this, ActivityTerms.class);
                //startActivity(intent);
                Uri uri = Uri.parse("https://www.cobiapp.com/Terminos-y-condiciones.html");
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent1);
            }
        });*/
    }

    private void initCountryList() {
        final la.neurometrics.cobi.ServiceRest serviceRest = new la.neurometrics.cobi.ServiceRest(ActivityRegister.this,ActivityRegister.this);
        serviceRest.showDialog();
        serviceRest.getJSONArray("country", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                nameCountries = new String[response.length()];
                idCountries = new String[response.length()];
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject country = response.getJSONObject(i);
                        idCountries[i] = country.getString("id");
                        nameCountries[i] = country.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                serviceRest.hideDialog();
                initCountrySpinner();
                initRegister();
            }
        });
    }

    private void initCountrySpinner() {
        Spinner spiCountry = findViewById(R.id.spi_country);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityRegister.this,
                R.layout.item_spinner, nameCountries);

        adapter.setDropDownViewResource(R.layout.item_spinner);
        spiCountry.setAdapter(adapter);
        spiCountry.setSelection(178);
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

    private void initRegister() {
        txtFechNaci   = findViewById(R.id.edi_fech);
        txtFechNaci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                         showDatePickerDialog();

            }
        });
        Button btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRegister();
            }
        });
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarInput();
        }

    };

    private void showDatePickerDialog() {
        la.neurometrics.cobi.DatePickerFragment newFragment = la.neurometrics.cobi.DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                txtFechNaci.setEnabled(true);
                System.out.println("entro pe conchatumare "+ selectedDate);
                txtFechNaci.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");




    }
    public  int getEdad(Date fechaNacimiento, Date fechaActual) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int dIni = Integer.parseInt(formatter.format(fechaNacimiento));
        int dEnd = Integer.parseInt(formatter.format(fechaActual));
        int age = (dEnd-dIni)/10000;
        return age;
    }

    private void submitRegister() {

        EditText txtName = findViewById(R.id.edi_name);
        EditText txtSurname = findViewById(R.id.edi_surname);
        EditText txtEmail = findViewById(R.id.edi_email);
        EditText txtPass = findViewById(R.id.edi_pass);
        EditText txtPhone = findViewById(R.id.edi_phone);
        CountryCodePicker txtCodeNumber = findViewById(R.id.pickerCountry);

        EditText txtPass2 = findViewById(R.id.edi_pass2);

        String email = txtEmail.getText().toString().trim();
        String password = txtPass.getText().toString().trim();
        String name = txtName.getText().toString().trim();
        String surname = txtSurname.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String password2 = txtPass2.getText().toString().trim();

        TextView lvlemail= findViewById(R.id.lbl_email);
        TextView lvlpass= findViewById(R.id.lbl_pass);
        TextView lvlname= findViewById(R.id.lbl_name);
        TextView lvlsurname = findViewById(R.id.lbl_surname);
        TextView lvlphone = findViewById(R.id.lbl_phone);
        TextView lvlpass2 = findViewById(R.id.lbl_pass2);

        Log.d("countrycode",  txtCodeNumber.getFullNumber() + phone);
        CheckBox chkTerms = findViewById(R.id.chkTerms);
        if(email.isEmpty()){
            login=false;
            lvlemail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));

        }else{
            lvlemail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                //  tvError1.setText("Correo no válido");
                lvlemail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));

                login=false;
            }else{
                lvlemail.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                login=true;
            }
        }
        if(phone.isEmpty()){
            login=false;
            lvlphone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));

        }else{
            lvlphone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

            if(!Patterns.PHONE.matcher(phone).matches() || phone.length()<6){
                //  tvError1.setText("Correo no válido");
                lvlphone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));

                login1=false;
            }else{

                lvlphone.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                login1=true;
            }
        }
        if(name.isEmpty()){
            lvlname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));
            login=false;
        }else{
            lvlname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        }

        if(surname.isEmpty()){
            lvlsurname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));
            login=false;
        }else{

            lvlsurname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        }


        if(password.isEmpty()){
            //    tvError2.setText("Debe ingresar su contraseña");
            lvlpass.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));
            login=false;
        }else {
            lvlpass.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        }
        TextView lvlFecha = findViewById(R.id.lbl_fech);

        if(txtFechNaci.getText().toString().trim().isEmpty()){

            lvlFecha.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));
            login= false;
        }else{
            lvlFecha.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            try {
                DateFormat dateFormat = dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date fechaNacimiento = dateFormat.parse(txtFechNaci.getText().toString().trim());
                Calendar cal = Calendar.getInstance();
                Date fechaActual = cal.getTime();

                System.out.println("Edad : " + String.valueOf(getEdad(fechaNacimiento, fechaActual)));
                edad = getEdad(fechaNacimiento,fechaActual);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(edad<18 && login){
                Toast.makeText(this, "Lo sentimos, solo aceptamos a personas mayores de 18 años.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), la.neurometrics.cobi.ActivityLogin.class));
                login=false;
            }else {
                login=true;
            }
        }

        if(password2.isEmpty()){

            lvlpass2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));
            login= false;

        }else{

            if(password.equals(password2)){
                lvlpass2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                lvlpass.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                login = true;

            }else{
                lvlpass2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));
                lvlpass.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorREd));

                login=false;
            }

        }
        if(name.isEmpty()){
            return;
        }
        if(password.isEmpty()){
            return;
        }
        if(phone.isEmpty()){
            return;
        }
        if(txtFechNaci.getText().toString().trim().isEmpty()){
            return;
        }
        if(edad<18){
            return;
        }
        if(email.isEmpty()){
            return;
        }
        if(password2.isEmpty()){
            return;
        }
        if(country.isEmpty()){
            return;
        }

        if(!login || !login1){
            return;
        }

        if(!chkTerms.isChecked()){
            Toast.makeText(this, "Debe aceptar los Términos & Condiciones y la Política de Privacidad.", Toast.LENGTH_SHORT).show();
            return;
        }

            try {

                JSONObject body = new JSONObject();
                body.put("source", "cobi");
                body.put("email", txtEmail.getText().toString().trim());
                body.put("pass", txtPass.getText().toString().trim());
                body.put("country", country);
                JSONObject attributes = new JSONObject();
                attributes.put("name", txtName.getText().toString().trim());
                attributes.put("surname", txtSurname.getText().toString().trim());
                attributes.put("fecha_nacimiento", txtFechNaci.getText().toString().trim());
                attributes.put("phone",txtPhone.getText().toString().trim());
                attributes.put("edited_by", null);
                attributes.put("photo", "");
                attributes.put("name", txtName.getText().toString().trim());
                attributes.put("surname", txtSurname.getText().toString().trim());
                attributes.put("line", null);
                attributes.put("description", null);
                attributes.put("year", null);
                attributes.put("certification", null);
                attributes.put("price", null);
                attributes.put("video", null);
                body.put("attributes", attributes);
                body.put("created_by", "app");
                ServiceUser serviceUser = new ServiceUser(ActivityRegister.this);
                serviceUser.post(body);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

}
