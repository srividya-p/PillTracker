package com.example.pilltracker;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class DashboardActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    String latitude, longitude;

    private ImageButton logoutButton;
    private ImageView locationTab, addMedicineTab, viewMedicinesTab, viewStatsTab, imageToTextTab, apiTab, profilePic;
    private TextView userGreet, userName, userEmail;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationview);
        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.username);
        profilePic = headerView.findViewById(R.id.profilePic);
        userEmail = headerView.findViewById(R.id.usermailid);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_open,R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            userGreet.setText(name);
            userName.setText(name);
            userEmail.setText(email);

            Picasso.get().load(photoUrl).transform(new CircleTransform()).into(profilePic);
        }

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

class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}