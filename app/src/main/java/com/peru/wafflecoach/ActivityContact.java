package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityContact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Contactar a Cobi");
        TextView txtIntro = findViewById(R.id.txt_intro);
        String sourceString = "¿Necesitas ayuda? Contáctanos a través de <b><a href='mailto:hola@cobiapp.com' target='_top'>hola@cobiapp.com</a></b> para recibir soporte y absolver tus dudas y/o consultas. Te responderemos lo antes posible.";
        txtIntro.setText(Html.fromHtml(sourceString));
        txtIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "contacto@cobiapp.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
