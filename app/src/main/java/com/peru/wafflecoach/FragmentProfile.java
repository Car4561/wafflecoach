package com.peru.wafflecoach;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends Fragment {

    View root;

    JSONObject user = new JSONObject();
    CollapsingToolbarLayout lytToolbar;
    ViewPager2 viewPager;

    ImageView imgPhoto;
    TextView txtLine;
    FrameLayout lytOverlay;
    LinearLayout lytControl;

    TextView txtDescription;
    TextView txtExperience;
    TextView txtCertification;
    TextView txtSpecialties;
    TextView txtPrice;
    Long  mLastClickTime=0l;

    TextView txtPass;

    LinearLayout lytProfile;
    LinearLayout lytPsychotherapist;

    String fullName = "";
    la.neurometrics.cobi.ServiceSession serviceSession;

    public FragmentProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        root = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Mi perfil");

        lytToolbar = root.findViewById(R.id.toolbar_layout);
        imgPhoto = root.findViewById(R.id.img_specialist);
        txtLine = root.findViewById(R.id.txt_line);
        viewPager = root.findViewById(R.id.pager_photo);
        lytOverlay = root.findViewById(R.id.lyt_overlay);
        lytControl = root.findViewById(R.id.lyt_control);

        txtDescription = root.findViewById(R.id.txt_description);
        txtExperience = root.findViewById(R.id.txt_experience);
        txtCertification = root.findViewById(R.id.txt_certification);
        txtSpecialties = root.findViewById(R.id.txt_specialties);
        txtPrice = root.findViewById(R.id.txt_price);

        lytPsychotherapist = root.findViewById(R.id.lyt_psychotherapist);
        lytProfile = root.findViewById(R.id.lyt_profile);
        lytProfile.setVisibility(View.VISIBLE);

        txtPass = root.findViewById(R.id.txt_pass);

        serviceSession = new la.neurometrics.cobi.ServiceSession(getContext());

        initPyschotherapistData();
        initPhotoPyschotherapist();
        setHasOptionsMenu(true);
        initBtnPass();

        // Inflate the layout for this fragment
        return root;
    }

    private void initBtnPass() {
        txtPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), la.neurometrics.cobi.ActivityPassword.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_edit :
                if (SystemClock.elapsedRealtime() - mLastClickTime > 2000) {
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent intent = new Intent(getActivity(), ActivityEditProfile.class);
                    getActivity().startActivity(intent);                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initPyschotherapistData() {
        fullName = serviceSession.getFullName();
    //    fullName = serviceSession.getFirstNameAndFirstSurname();

        lytToolbar.setTitle(fullName);
        setInfoProfile();
        if(serviceSession.getFromSession("profile").equals("psychotherapist")){
            setInfoPsychotherapist();
        }else{
            FrameLayout lytSeparator = (FrameLayout) lytProfile.getChildAt(0);
            lytSeparator.setVisibility(View.GONE);
            lytPsychotherapist.setVisibility(View.GONE);
        }
    }

    private void setInfoProfile() {
        TextView txtCountry = root.findViewById(R.id.txt_country);
        TextView txtPhone = root.findViewById(R.id.txt_phone);
        TextView txtEmail = root.findViewById(R.id.txt_email);

        if(serviceSession.getFromSession("country") != null)
            txtCountry.setText(serviceSession.getFromSession("country").toString());
        else
            txtCountry.setText("País no registrado");

        if(serviceSession.getUserAttribute("phone") != null)
            txtPhone.setText(serviceSession.getUserAttribute("phone"));
        else
            txtPhone.setText("Teléfono no registrado");

        if(serviceSession.getFromSession("email") != null)
            txtEmail.setText(serviceSession.getFromSession("email").toString());
        else
            txtEmail.setText("País no registrado");

    }

    private void setInfoPsychotherapist() {
        txtLine.setText(serviceSession.getUserAttribute("line"));
        if(serviceSession.getUserAttribute("description") != null){
            txtDescription.setText(serviceSession.getUserAttribute("description"));
        }else{
            txtDescription.setText("No ha registrado una descripción de su perfil.");
        }
        if(serviceSession.getUserAttribute("year") != null){
            int year = Integer.valueOf(serviceSession.getUserAttribute("year"));
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int dif = currentYear - year;
            String s = "";
            if(dif > 0) {
                if(dif > 1)
                    s = "s";
                txtExperience.setText(dif + " año" + s + " de experiencia");
            }else{
                txtExperience.setText("Menos de un año de experiencia");
            }
        }else{
            txtExperience.setText("Sin años de experiencia");
        }
        if(serviceSession.getUserAttribute("certification") != null){
            txtCertification.setText(serviceSession.getUserAttribute("certification"));
        }else{
            txtCertification.setText("Sin certificaciones");
        }
        if(serviceSession.getUserAttribute("price") != null){
            txtPrice.setText("S/ " + serviceSession.getUserAttribute("price") + " por sesión");
        }else{
            txtPrice.setText("Sin precio registrado");
        }

        ArrayList<HashMap> specialties = serviceSession.getFromSession("specialties");
        if(specialties != null){
            String strSpecialties = "";
            String separator = "";

            for(int i = 0; i < specialties.size(); i++) {
                HashMap specialty = specialties.get(i);
                strSpecialties = strSpecialties + separator + specialty.get("name");
                separator = "\n";
            }
            if(!strSpecialties.equals("")){
                txtSpecialties.setText(strSpecialties);
            }else{
                txtSpecialties.setText("Sin especialidades registradas");
            }
        }
    }

    private void initPhotoPyschotherapist() {
        if(serviceSession.getUserAttribute("video") != null) {
            initPagerWithVideo();
         /*   AppBarLayout appBarLayout  = root.findViewById(R.id.app_bar);
            ViewGroup.LayoutParams params = appBarLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            appBarLayout.setLayoutParams(params);*/
        }else{
            initPhoto();
        }
    }

    private void initPhoto() {
        viewPager.setVisibility(View.GONE);
        if(serviceSession.getUserAttribute("photo") != null)
            la.neurometrics.cobi.ServiceGlobal.loadImage(serviceSession.getUserAttribute("photo"), imgPhoto);
    }

    private void initPagerWithVideo() {
        imgPhoto.setVisibility(View.GONE);
        lytControl.setVisibility(View.VISIBLE);
        ArrayList<String> urls = new ArrayList<>();
        urls.add(serviceSession.getUserAttribute("photo"));
        urls.add(serviceSession.getUserAttribute("video"));
        viewPager.setAdapter(new la.neurometrics.cobi.AdapterPagerProfile(getContext(), urls));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                ImageView img01 = (ImageView) lytControl.getChildAt(0);
                ImageView img02 = (ImageView) lytControl.getChildAt(1);
                if(position == 1){
                    txtLine.setVisibility(View.GONE);
                    lytOverlay.setVisibility(View.GONE);
                    img01.setColorFilter(getResources().getColor(R.color.colorGray));
                    img02.setColorFilter(getResources().getColor(R.color.colorPrimary));
                }else{
                    txtLine.setVisibility(View.VISIBLE);
                    lytOverlay.setVisibility(View.VISIBLE);
                    //getSupportActionBar().setTitle(fullName);
                    img01.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    img02.setColorFilter(getResources().getColor(R.color.colorGray));
                }
            }
        });
    }

}
