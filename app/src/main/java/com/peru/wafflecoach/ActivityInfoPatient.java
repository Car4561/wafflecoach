package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityInfoPatient extends AppCompatActivity {

    EditText txtName;
    EditText txtDescription;
    Button btnNext;

    ServiceSession serviceSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_patient);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Información adicional");

        txtName = findViewById(R.id.txt_name);
        txtDescription = findViewById(R.id.txt_description);
        btnNext = findViewById(R.id.btn_next);

        serviceSession = new ServiceSession(ActivityInfoPatient.this);

        if(!getIntent().hasExtra("user")
                || !getIntent().hasExtra("date")
                || !getIntent().hasExtra("hour")) {
            //sácalo de aquí
            ServiceGlobal.alerta(ActivityInfoPatient.this, "Datos insuficientes");
            onBackPressed();
        }

        initName();
        initBtnNext();
    }

    private void initBtnNext() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtDescription.getText().toString().equals("")){
                    if(txtDescription.getText().toString().trim().length()>30) {


                        String name = serviceSession.getFullName();
                        if (!txtName.getText().toString().equals("")) {
                            name = txtName.getText().toString();
                        }

                        String description = txtDescription.getText().toString();
                        String psychotherapist = getIntent().getStringExtra("user");
                        String date = getIntent().getStringExtra("date");
                        String hour = getIntent().getStringExtra("hour");
                        Intent intent = new Intent(ActivityInfoPatient.this, ActivityPayment.class);
                        int dayOfWeek = getIntent().getExtras().getInt("day");
                        intent.putExtra("user", psychotherapist);
                        intent.putExtra("date", date);
                        intent.putExtra("hour", hour);
                        intent.putExtra("name", name);
                        intent.putExtra("description", description);
                        intent.putExtra("day", dayOfWeek);
                        startActivity(intent);
                    }
                    else {
                        ServiceGlobal.alerta(ActivityInfoPatient.this, "La descripción debe tener como mínimo 30 caracteres");

                    }
                }else{
                    ServiceGlobal.alerta(ActivityInfoPatient.this, "Ingrese una descripción del caso a tratar");
                }

            }
        });
    }

    private void initName() {
        txtName.setHint(serviceSession.getFullName());
        txtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                EditText editText = (EditText) view;
                if (b)
                    editText.setHint("");
                else
                    editText.setHint(serviceSession.getFullName());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
