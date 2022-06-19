package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityCancelAppointment extends AppCompatActivity {

    String id = "";
    String name = "";
    String schedule = "";
    String description = "";
    String dateAnte = "";
    String dayAnt = "";
    String houtAnt = "";
    String idPys = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_appointment);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("id") &&
                getIntent().hasExtra("name") &&
                getIntent().hasExtra("schedule")) {
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            schedule = getIntent().getStringExtra("schedule");
            dateAnte = getIntent().getStringExtra("dateAnterior");
            dayAnt = getIntent().getStringExtra("dayAnte");
            houtAnt = getIntent().getStringExtra("houtAnte");
            idPys = getIntent().getStringExtra("idPsychotherapist");
            TextView txtIntro = findViewById(R.id.txt_intro);
            String sourceString = "Indique el motivo por el cual esta cancelando la cita programada para <b>" + schedule + "</b> con <b>" + name + "</b>";
            txtIntro.setText(Html.fromHtml(sourceString));

            initCancelForm();
        }else{
            onBackPressed();
        }
    }

    private void initCancelForm() {
        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtDescription = findViewById(R.id.txt_description);
                description = txtDescription.getText().toString();
                if(!id.equals("") && !description.equals("")) {
                    final la.neurometrics.cobi.ServiceRest serviceRest = new la.neurometrics.cobi.ServiceRest(ActivityCancelAppointment.this);
                    serviceRest.showDialog("Cancelando cita...");
                    JSONObject bodyPut = new JSONObject();
                    try {
                        bodyPut.put("description", description);
                        bodyPut.put("state", "2");
                        bodyPut.put("cancel","1 ");
                        bodyPut.put("dayAnt",dayAnt);
                        bodyPut.put("houtAnt",houtAnt);
                        bodyPut.put("dateAnterior",dateAnte);
                        bodyPut.put("idPsychotherapist", idPys);

                        Log.d("tag1",bodyPut.toString());
                        serviceRest.put("appointment/" + id, bodyPut, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                serviceRest.hideDialog();
                                Intent intent = new Intent(ActivityCancelAppointment.this, la.neurometrics.cobi.ActivityWrapper.class);
                                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                                startActivity(intent);
                                finish();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    la.neurometrics.cobi.ServiceGlobal.alerta(ActivityCancelAppointment.this, "Debe indicar el motivo");
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
