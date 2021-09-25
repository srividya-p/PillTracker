package com.example.pilltracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    String latitude, longitude;

    private ImageButton logoutButton;
    private ImageView locationTab;
    private TextView userGreet;

    private FirebaseUser currentUser;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        logoutButton = findViewById(R.id.logoutImageButton);
        locationTab = findViewById(R.id.locationTab);
        userGreet = findViewById(R.id.userGreet);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            userGreet.setText("Hello "+currentUser.getDisplayName()+"!");

            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();
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
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
    }
}