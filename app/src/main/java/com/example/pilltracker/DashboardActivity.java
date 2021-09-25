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
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    switchOnGPS();
                } else {
                    getLocation();
                }
            }
        });
    }

    public void switchOnGPS() {
        System.out.println("switchOnFunction");
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setMessage("GPS is not enabled. Enable it from settings?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(locationGPS != null){
                latitude = String.valueOf(locationGPS.getLatitude());
                longitude = String.valueOf(locationGPS.getLongitude());
                System.out.println(latitude+"\n"+longitude);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}