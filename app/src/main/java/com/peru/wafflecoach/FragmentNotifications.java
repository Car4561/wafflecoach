package com.peru.wafflecoach;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONArray;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotifications extends Fragment {

    View root;
    private RecyclerView recyclerView;
    private la.neurometrics.cobi.ServiceRest serviceRest;

    public FragmentNotifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        getActivity().setTitle("Notificaciones");
        initRVNotifications();

        // Inflate the layout for this fragment
        return root;
    }

    private void initRVNotifications() {
        recyclerView = root.findViewById(R.id.rv_notifications);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        getNotifications();
    }

    private void getNotifications() {
        final TextView txtEmpty = root.findViewById(R.id.txt_empty);

        final ImageView img = root.findViewById(R.id.fondo);

        serviceRest = new la.neurometrics.cobi.ServiceRest(getContext());
        serviceRest.showDialog("Cargando notificaciones...");
        serviceRest.getJSONArray("notification", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                LinearLayout lytEmpty = root.findViewById(R.id.lyt_empty);
                if(response.length() > 0) {
                    la.neurometrics.cobi.AdapterNotification adapterNotification = new la.neurometrics.cobi.AdapterNotification(getContext(), response);
                    recyclerView.setAdapter(adapterNotification);
                    //lytEmpty.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);

                    txtEmpty.setVisibility(View.GONE);
                }else{
                   // lytEmpty.setVisibility(View.VISIBLE);
                    img.setVisibility(View.VISIBLE);

                    txtEmpty.setVisibility(View.VISIBLE);
                }
                serviceRest.hideDialog();
            }
        });
    }

}
