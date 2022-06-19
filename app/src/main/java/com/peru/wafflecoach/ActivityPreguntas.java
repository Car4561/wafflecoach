package com.peru.wafflecoach;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityPreguntas extends AppCompatActivity {

    String[] preguntas = new String[]{"¿Qué es COBI?","¿La terapia online es efectiva?","¿Cuánto cuesta el servicio?","¿Puedo ver quiénes son los especialistas?","¿Cómo puedo ver los horarios disponibles de los especialistas?"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas2);

    }

}
