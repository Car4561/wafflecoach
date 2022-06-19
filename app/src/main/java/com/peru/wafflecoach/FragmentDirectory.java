package com.peru.wafflecoach;


import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class  FragmentDirectory extends Fragment {

    private View root;
    private RecyclerView recyclerView;
    private ServiceRest serviceRest;

    private String min = "";
    private String max = "";
    private String days = "";
    private String languages = "";
    private String specialties = "";
    Long  mLastClickTime=0l;

    private Toolbar toolbar;
    private Toolbar toolbarSearch;
    private EditText txtSearch;
    Context context;
    private String reference = "";
    private String filEdad="";
    ServiceSession serviceSession;
    public FragmentDirectory(Context context) {
      this.context = context;
        serviceSession = new ServiceSession(context);

    }
    public FragmentDirectory() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_directory, container, false);
        getActivity().setTitle("Directorio");
        if(context!=null) {
            if (!serviceSession.intro2()) {
                if(serviceSession.getFromSession("profile").toString().equals("patient")
                        && serviceSession.getFromSession("state").toString().equals("1")){
                        TextView txtIntroFilter = root.findViewById(R.id.txtIntroFilter);
                        ImageView back = root.findViewById(R.id.background);
                        ImageView imgFlecha = root.findViewById(R.id.imgFlech);
                        txtIntroFilter.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        imgFlecha.setVisibility(View.VISIBLE);
                        back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView txtIntroFilter = root.findViewById(R.id.txtIntroFilter);

                            ImageView back = root.findViewById(R.id.background);
                            ImageView imgFlecha = root.findViewById(R.id.imgFlech);
                            txtIntroFilter.setVisibility(View.GONE);
                            back.setVisibility(View.GONE);
                            imgFlecha.setVisibility(View.GONE);
                            serviceSession.setToSession("intro2",true);

                        }
                    });
                }

            }
        }

        toolbar = getActivity().findViewById(R.id.toolbar);
        toolbarSearch = getActivity().findViewById(R.id.toolbar_search);
        txtSearch = toolbarSearch.findViewById(R.id.txt_search);

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });


        initRVPsychotherapists();
        initBtnFilter();
        setHasOptionsMenu(true);

        return root;
    }

    private void search() {
        reference= txtSearch.getText().toString();
        getPsychotherapists();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_directory, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search :
                toolbar.setVisibility(View.GONE);
                txtSearch.requestFocus();
                toolbarSearch.setVisibility(View.VISIBLE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initBtnFilter() {
        FloatingActionButton btnFilter = root.findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(getActivity(), la.neurometrics.cobi.ActivityFilter.class);
                startActivityForResult(intent, 1);
                //startActivity(intent);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                min = data.getStringExtra("min");
                max = data.getStringExtra("max");
                days = data.getStringExtra("days");
                languages = data.getStringExtra("languages");
                specialties = data.getStringExtra("specialties");
                filEdad = data.getStringExtra("filEdad");
                getPsychotherapists();
            }
        }
    }

    private void initRVPsychotherapists() {
        recyclerView = root.findViewById(R.id.rv_psychotherapists);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVisibility(View.GONE);
        getPsychotherapists();
    }
    public  int getEdad(Date fechaNacimiento, Date fechaActual) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int dIni = Integer.parseInt(formatter.format(fechaNacimiento));
        int dEnd = Integer.parseInt(formatter.format(fechaActual));
        int age = (dEnd-dIni)/10000;
        return age;
    }
    private void getPsychotherapists() {
        serviceRest = new ServiceRest(getContext());
        serviceRest.showDialog("Cargando especialistas...");
        ServiceSession serviceSession = new ServiceSession(getContext());
        String url = "psychotherapist?state=1&user=" + serviceSession.getFromSession("id") + "&reference=" + reference;
        if(!min.equals("")) url = url + "&min=" + min;
        if(!max.equals("")) url = url + "&max=" + max;
        if(!days.equals("")) url = url + "&days=" + days;
        if(!languages.equals("")) url = url + "&languages=" + languages;
        if(!specialties.equals("")) url = url + "&specialties=" + specialties;
        la.neurometrics.cobi.ServiceGlobal.console(url);
        Log.d("TAG66",url);
        serviceRest.getJSONArray(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                TextView txtEmpty = root.findViewById(R.id.txt_empty);

                JSONArray responseNew = new JSONArray();

                if(response.length() > 0) {
                    if(!filEdad.equals("")) {
                        try {

                            for (int i = 0; i < response.length(); i++) {
                                if(response.getJSONObject(i).getJSONObject("attributes").has("fecha_nacimiento")) {
                                    DateFormat dateFormat = dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    Date fechaNacimiento = null;
                                    fechaNacimiento = dateFormat.parse(response.getJSONObject(i).getJSONObject("attributes").getString("fecha_nacimiento"));

                                    Calendar cal = Calendar.getInstance();
                                    Date fechaActual = cal.getTime();

                                    System.out.println("Edad : " + String.valueOf(getEdad(fechaNacimiento, fechaActual)));
                                    int edad = getEdad(fechaNacimiento, fechaActual);
                                    if (filEdad.equals("30")) {
                                        if (edad <= 30) {
                                            responseNew.put(response.getJSONObject(i));
                                        }
                                    } else if (filEdad.equals("3035")) {
                                        if (edad >= 31 && edad <= 45) {
                                            responseNew.put(response.getJSONObject(i));
                                        }

                                    } else if (filEdad.equals("40")) {
                                        if (edad >= 46) {
                                           responseNew.put(response.getJSONObject(i));
                                        }
                                    }
                                }
                            }
                            response = responseNew;

                       } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                        }
                    }

                    if(response.length()<1){
                        recyclerView.setVisibility(View.GONE);
                  //      txtEmpty.setVisibility(View.VISIBLE);
                        ImageView img = root.findViewById(R.id.fondo);
                        img.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                    }else{
                        la.neurometrics.cobi.AdapterPsychotherapist adapterPsychotherapist = new la.neurometrics.cobi.AdapterPsychotherapist(getContext(), response);
                        recyclerView.setAdapter(adapterPsychotherapist);
                        recyclerView.setVisibility(View.VISIBLE);
                  //      txtEmpty.setVisibility(View.GONE);
                        ImageView img = root.findViewById(R.id.fondo);
                        img.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.GONE);

                    }

                }else{
                    ImageView img = root.findViewById(R.id.fondo);
                    img.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.VISIBLE);
                }
                serviceRest.hideDialog();
            }
        });
    }

}
