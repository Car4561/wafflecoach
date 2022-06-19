package com.peru.wafflecoach;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAppointments extends Fragment {

    private View root;
    private RecyclerView recyclerView;
    private la.neurometrics.cobi.ServiceRest serviceRest;

    public FragmentAppointments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_appointments, container, false);
        getActivity().setTitle("Citas");

        // Inflate the layout for this fragment
        getAppointments();
        initBtnEmpty();
        return root;
    }

    private void initBtnEmpty() {
        Button btnEmpty = root.findViewById(R.id.btn_empty);
        btnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomNavigationView navViewBottom;
                navViewBottom = getActivity().findViewById(R.id.nav_view_bottom);
                navViewBottom.setSelectedItemId(R.id.item_directory);

                Fragment fragment = new la.neurometrics.cobi.FragmentDirectory(getContext());
                FragmentTransaction fragmentTransaction  = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, fragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            }
        });
    }

    public void initRVAppointments(JSONArray response) {
        LinearLayout lytEmpty = root.findViewById(R.id.lyt_empty);
        if(response.length() > 0) {
            recyclerView = root.findViewById(R.id.rv_appointments);
            recyclerView.setHasFixedSize(true);
            int itemViewType = 0;
            recyclerView.getRecycledViewPool().setMaxRecycledViews(itemViewType, 0);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            la.neurometrics.cobi.AdapterAppointment adapterAppointment = new la.neurometrics.cobi.AdapterAppointment(getContext(), response);
            recyclerView.setAdapter(adapterAppointment);
            lytEmpty.setVisibility(View.GONE);
            TextView txtEmpty = root.findViewById(R.id.txt_empty);

            ImageView img = root.findViewById(R.id.fondo);
            img.setVisibility(View.GONE);
            txtEmpty.setVisibility(View.GONE);
            lytEmpty.setVisibility(View.GONE);
        }else{
            TextView txtEmpty = root.findViewById(R.id.txt_empty);


            ImageView img = root.findViewById(R.id.fondo);
            img.setVisibility(View.VISIBLE);
            lytEmpty.setVisibility(View.VISIBLE);

            txtEmpty.setVisibility(View.VISIBLE);

        }
    }

    private void getAppointments() {
        serviceRest = new la.neurometrics.cobi.ServiceRest(getContext());
        serviceRest.showDialog("Cargando citas...");
        Log.d("asd", String.valueOf(new ServiceSession(getContext()).get("token")));

        serviceRest.getJSONArray("appointment", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                serviceRest.hideDialog();
                Log.d("TAG1", String.valueOf(response));
                initRVAppointments(response);
            }
        });
    }

}
