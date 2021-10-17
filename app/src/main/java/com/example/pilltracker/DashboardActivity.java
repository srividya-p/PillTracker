package com.example.pilltracker;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    String latitude, longitude;

    private ImageButton logoutButton;
    private ImageView locationTab, addMedicineTab, viewMedicinesTab, viewStatsTab, imageToTextTab, apiTab;
    private TextView userGreet;

    private FirebaseUser currentUser;
    private LocationManager locationManager;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        logoutButton = findViewById(R.id.logoutImageButton);
        locationTab = findViewById(R.id.locationTab);
        addMedicineTab = findViewById(R.id.addMedicineTab);
        viewMedicinesTab = findViewById(R.id.viewMedicinesTab);
        viewStatsTab = findViewById(R.id.viewStatsTab);
        imageToTextTab = findViewById(R.id.imageToTextTab);
        apiTab = findViewById(R.id.apiTab);
        userGreet = findViewById(R.id.userGreet);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_open,R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
//          Log.d("WHAT?", currentUser.getDisplayName());
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            userGreet.setText(name);
        }

        //String url = "https://www.pinclipart.com/picdir/middle/182-1821638_logout-icon-png-red-clipart.png";
        //Picasso.get().load(url).into(logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseAuth.getInstance().signOut();
            DashboardActivity.this.startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            DashboardActivity.this.finish();
        }
        });

        locationTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://maps.google.co.uk/maps?q=Pharmacy&hl=en";
                Intent locIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                locIntent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                startActivity(locIntent);
            }
        });

        addMedicineTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMedicineIntent = new Intent(getBaseContext(), AddMedicineActivity.class);
                startActivity(addMedicineIntent);
            }
        });

        viewMedicinesTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent displayMedicineIntent = new Intent(getBaseContext(), DisplayMedicineActivity.class);
                startActivity(displayMedicineIntent);
            }
        });

        viewStatsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewStatsIntent = new Intent(getBaseContext(), StatsActivity.class);
                startActivity(viewStatsIntent);
            }
        });

        imageToTextTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageToTextIntent = new Intent(getBaseContext(), ImageToTextActivity.class);
                startActivity(imageToTextIntent);
            }
        });

        apiTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent apiIntent = new Intent(getBaseContext(), APISearchActivity.class);
                startActivity(apiIntent);
            }
        });
    }
}