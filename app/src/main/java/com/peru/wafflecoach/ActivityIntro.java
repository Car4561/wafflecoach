package com.peru.wafflecoach;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class ActivityIntro extends AppCompatActivity {

    private Button btnSkip;
    private Button btnNext;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        btnNext = findViewById(R.id.btn_next);
        initBtnSkip();
        initPagerIntro();

    }

    private void initBtnSkip() {
        btnSkip = findViewById(R.id.btn_skip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceSession serviceSession = new ServiceSession(ActivityIntro.this);
                serviceSession.setToSession("intro", true);
                Intent intent = new Intent(ActivityIntro.this, ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initPagerIntro() {
        final ViewPager2 pagerIntro = findViewById(R.id.pager_intro);
        pagerIntro.setAdapter(new RecyclerView.Adapter<viewHolder>() {

            int[] imgs = {R.drawable.slide_01_, R.drawable.slide_02_, R.drawable.slide_03_};
            String[] txts = {"Encuentra en línea a los\n" +
                    "mejores psicoterapeutas y\n" +
                    "coaches de forma inmediata.",
            "Mediante sesiones oportunas te\n" +
                    "brindamos la ayuda que\n" +
                    "necesitas.",
            "Tendrás una sesión con mayor flexibilidad.\n" +
                    "El tiempo ya no es una\n" +
                    "excusa."};

            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(ActivityIntro.this);
                return new viewHolder((RelativeLayout) inflater.inflate(R.layout.layout_intro, parent, false));
            }

            @Override
            public void onBindViewHolder(viewHolder holder, int position) {
                holder.imgIntro.setImageResource(imgs[position]);
                holder.txtIntro.setText(txts[position]);

            }

            @Override
            public int getItemCount() {
                return imgs.length;
            }
        });
        pagerIntro.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                LinearLayout lytControl = findViewById(R.id.lyt_control);
                ImageView control;
                for(int i = 0; i < lytControl.getChildCount(); i++){
                    control = (ImageView) lytControl.getChildAt(i);
                    control.setColorFilter(getResources().getColor(R.color.colorWhite));
                }
                control = (ImageView) lytControl.getChildAt(position);
                control.setColorFilter(getResources().getColor(R.color.colorAccent));

                if(position == 2){
                    //btnSkip.setBackground(getResources().getDrawable(R.drawable.btn_accent));
                    btnNext.setText("Continuar");
                }else{
                    btnNext.setBackgroundColor(getResources().getColor(R.color.transparent));
                    btnNext.setText("Siguiente");
                }
                pos = position;
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos < 2){
                    pagerIntro.setCurrentItem(pos + 1);
                }else{
                    ServiceSession serviceSession = new ServiceSession(ActivityIntro.this);
                    serviceSession.setToSession("intro", true);
                    Intent intent = new Intent(ActivityIntro.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    class viewHolder extends RecyclerView.ViewHolder {

        ImageView imgIntro;
        TextView txtIntro;

        public viewHolder(RelativeLayout layout) {
            super(layout);
            imgIntro = layout.findViewById(R.id.img_intro);
            txtIntro = layout.findViewById(R.id.txt_intro);
        }
    }
}
