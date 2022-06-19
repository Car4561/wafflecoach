package com.peru.wafflecoach;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Response;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class ActivityWrapper extends AppCompatActivity {

    private Toolbar toolbar;
    private Toolbar toolbarSearch;
    private NavigationView navigationView;
    private BottomNavigationView navViewBottom;
    private ServiceSession serviceSession;
    ServiceRest serviceRest;
    MixpanelAPI mixpanel;
   /* public static final String MIXPANEL_TOKEN = "d49503cda477a5088678264293bbf55b";
    @Override
    protected void onDestroy() {
        mixpanel.flush();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mixpanel =
                MixpanelAPI.getInstance(this, MIXPANEL_TOKEN,false);
        mixpanel.optOutTracking();

        JSONObject props = new JSONObject();

        try {

            props.put("genre", "hip-hop");
            props.put("duration in seconds", 42);

            mixpanel.track("Video play", props);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Ensure all future events sent from
// the device will have the distinct_id 13793
        mixpanel.identify("13793");


// Ensure all future user profile properties sent from
// the device will have the distinct_id 13793
        mixpanel.getPeople().identify("13793");
        mixpanel.getPeople().set("Sign up date", "13793");


        Boolean hasOptedOutTracking = mixpanel.hasOptedOutTracking();
        Log.d("tagmix", String.valueOf(hasOptedOutTracking));
    }
*/

   public  void resetToken(){
       ServiceRest serviceRest = new ServiceRest(this);
       serviceRest.getJSONObject("session", new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               String token = null;
               try {
                   token = response.getString("token");
               } catch (JSONException e) {
                   e.printStackTrace();
               }
               ServiceSession serviceSession = new ServiceSession(getApplicationContext());
               serviceSession.setSession(token);
            }
       });
   }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper);
      
        serviceSession = new ServiceSession(this);
        Version version = la.neurometrics.cobi.ClassSingleton.getInstance(getApplicationContext()).getVersion();
        if(version != null) {
            if (!version.itsUpdate()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityWrapper.this).setCancelable(false)
                        .setTitle(version.getTittleVersion()).setMessage(version.getDescriptionVersion())
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=la.neurometrics.cobi");
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent1);

                            }
                        });

                alert.show();

            }
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }



        toolbarSearch = findViewById(R.id.toolbar_search);
        ImageView btnCloseSearch  = toolbarSearch.findViewById(R.id.img_clear);
        btnCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtSearch = toolbarSearch.findViewById(R.id.txt_search);
                if(txtSearch.getText().toString().equals("")) {
                    toolbarSearch.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                }else{
                    txtSearch.setText("");
                }
            }
        });
        initNavBottom();

        String defaultTap = "";
        if(getIntent().hasExtra("default")){
            defaultTap = getIntent().getStringExtra("default");
        }
        switch (defaultTap) {
            case "appointment":
                openFragment(new FragmentAppointments());

                navViewBottom.setSelectedItemId(R.id.item_appointments);

                break;

            case "profile" :
                openFragment(new FragmentProfile());

                navViewBottom.setSelectedItemId(R.id.item_profile);
                break;

            default:
                openFragment(new FragmentDirectory());
                navViewBottom.setSelectedItemId(R.id.item_directory);
                break;
        }
        initDrawer();

        serviceRest= new ServiceRest(this);
        serviceRest.getJSONObject("session", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String token = null;
                try {
                    token = response.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ServiceSession serviceSession = new ServiceSession(getApplicationContext());
                serviceSession.setSession(token);
                ServiceGlobal.console(token);
                serviceSession.setToSession("intro2",true);


                serviceSession = new ServiceSession(ActivityWrapper.this);
                initNavDrawer();

            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(ActivityWrapper.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String tokenFirebase = instanceIdResult.getToken();
                Log.d("FIREBASEE","KQKQWDKKDWOPQKDPQWKPQWDK");
                if(serviceSession.getFromSession("tokenFirebase") == null ||
                        !serviceSession.getFromSession("tokenFirebase").equals(tokenFirebase)){
                    ServiceUser serviceUser = new ServiceUser(ActivityWrapper.this);
                    serviceUser.updateFirebaseToken(tokenFirebase);
                }
            }
        });
        try {
            registerLightrail();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void registerLightrail() {

        try {
            JSONObject body = new JSONObject();
            body.put("id",serviceSession.get("id"));
            body.put("firstName",serviceSession.getUserAttribute("name"));
            body.put("lastName",serviceSession.getUserAttribute("surname"));
            body.put("email",serviceSession.get("email"));
            Log.d("lightrail",body.toString());

            serviceRest.postLightrail("contacts", body, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    ServiceGlobal.console("Usuario Registrado en lightrail");
                }
            });



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void initDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    private void initNavDrawer() {

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_payments) {
                    ArrayList<HashMap> paymentMethod = serviceSession.getFromSession("paymentMethod");
                    Intent intent = new Intent(ActivityWrapper.this, ActivityAddPaymentMethod.class);

                    if(serviceSession.getUserAttribute("phone")  == null || serviceSession.getUserAttribute("phone").isEmpty()){
                        Toast.makeText(ActivityWrapper.this, "Debe ingresar su número de celular para ingresar métodos de pago", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),ActivityEditProfile.class));
                        finish();
                    }else {
                        if (paymentMethod != null && paymentMethod.size() > 0) {
                            intent = new Intent(ActivityWrapper.this, ActivityPaymentMethods.class);
                            startActivity(intent);
                        } else {
                            startActivityForResult(intent, 1);
                        }
                    }

                } else if (id == R.id.nav_psychotherapist) {
                    Intent intent = new Intent(ActivityWrapper.this, ActivityFormPsychotherapist.class);
                    startActivity(intent);
                } else if (id == R.id.nav_availability) {
                    Intent intent = new Intent(ActivityWrapper.this, ActivityAvailability.class);
                    startActivity(intent);
                } else if (id == R.id.nav_faqs) {
                    Intent intent = new Intent(ActivityWrapper.this, ActivityFaqs.class);
                    startActivity(intent);
                } else if (id == R.id.nav_terms) {
                    //Intent intent = new Intent(ActivityWrapper.this, ActivityTerms.class);
                    //startActivity(intent);
                    Uri uri = Uri.parse("https://www.cobiapp.com/Terminos-y-condiciones.html");
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent1);
                } else if (id == R.id.nav_contact) {
                    Intent intent = new Intent(ActivityWrapper.this, ActivityContact.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {

                    new AlertDialog.Builder(ActivityWrapper.this)
                            .setTitle("¿Seguro que desea cerrar su sesión?")
                            .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            // Remove InstanceID initiate to unsubscribe all topic
                                            try {
                                                FirebaseInstanceId.getInstance().deleteInstanceId();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    serviceSession.logout();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            })
                            .show();

                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        View headerView = navigationView.getHeaderView(0);
        TextView navTxtName = headerView.findViewById(R.id.nav_txt_name);
        TextView navTxtEmail = headerView.findViewById(R.id.nav_txt_email);

        String name = serviceSession.getFullName();
        String email = serviceSession.getEmail();
        navTxtName.setText(name);
        navTxtEmail.setText(email);

        String urlPhoto = serviceSession.getUserAttribute("photo");
        if(urlPhoto != null) {
            ImageView photo = headerView.findViewById(R.id.nav_img_photo);
            ServiceGlobal.loadCircleImage(urlPhoto, photo);
        }else{
            ImageView photo = headerView.findViewById(R.id.nav_img_photo);

            ServiceGlobal.loadCircleImage("https://churchpluggedin.com/wp-content/uploads/2017/08/blank-profile-picture-e1504135905221.png", photo);

        }
        if(serviceSession.getFromSession("profile").toString().equals("psychotherapist")
                && serviceSession.getFromSession("state").toString().equals("1")){
            Menu menu = navigationView.getMenu();
            MenuItem menuItemAvailability = menu.findItem(R.id.nav_availability);
            MenuItem menuItemPsychotherapist = menu.findItem(R.id.nav_psychotherapist);
            menuItemPsychotherapist.setVisible(false);
            menuItemAvailability.setVisible(true);
        }
        Menu menu = navigationView.getMenu();



    }

    private void initNavBottom() {
        navViewBottom = findViewById(R.id.nav_view_bottom);
        navViewBottom.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                //
            }
        });
        navViewBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.item_appointments:
                        fragment = new FragmentAppointments();
                        openFragment(fragment);
                        return true;
                    case R.id.item_directory:
                        fragment = new FragmentDirectory(ActivityWrapper.this);
                        openFragment(fragment);
                        return true;
                    case R. id.item_notifications:
                        fragment = new FragmentNotifications();
                        openFragment(fragment);
                        return true;
                    case R.id.item_profile:
                        fragment = new FragmentProfile();
                        openFragment(fragment);
                        return true;
                }
                return false;
            }
        });
    }

    public void openFragment(Fragment fragment) {

        toolbar.setVisibility(View.VISIBLE);
        toolbarSearch.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(navViewBottom.getSelectedItemId() == R.id.item_appointments){
                super.onBackPressed();
            }else{
                navViewBottom.setSelectedItemId(R.id.item_appointments);
            }
        }
    }

    @Override
    protected void onResume() {
        if(navigationView != null && navigationView.getCheckedItem() != null)
            navigationView.getCheckedItem().setChecked(false);

        Version version = la.neurometrics.cobi.ClassSingleton.getInstance(getApplicationContext()).getVersion();
        if(version!=null) {
            if (!version.itsUpdate()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ActivityWrapper.this).setCancelable(false)
                        .setTitle(version.getTittleVersion()).setMessage(version.getDescriptionVersion())
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=la.neurometrics.cobi");
                                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent1);

                            }
                        });

                alert.show();

            }
        }
        super.onResume();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Intent intent = new Intent(ActivityWrapper.this, ActivityPaymentMethods.class);
                startActivity(intent);
            }
        }
    }
}
