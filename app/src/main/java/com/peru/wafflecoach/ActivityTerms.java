package com.peru.wafflecoach;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityTerms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Términos y condiciones");
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return super.onSupportNavigateUp();

    }
}
