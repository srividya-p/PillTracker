package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MedicineAddedActivity extends AppCompatActivity {
    TextView mNameView, mDescView, mDurationView, userName, userEmail;
    Button backButton;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private Toolbar toolbar;
    private ImageView profilePic;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_added);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigationview);
        headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.username);
        profilePic = headerView.findViewById(R.id.profilePic);
        userEmail = headerView.findViewById(R.id.usermailid);
        
        mNameView = findViewById(R.id.mNameView);
        mDescView = findViewById(R.id.mDescView);
        mDurationView = findViewById(R.id.mDurationView);
        
        backButton = findViewById(R.id.backButton);
        
        Bundle extras = getIntent().getExtras();
        
        mNameView.setText(extras.getString("mName"));
        mDescView.setText(extras.getString("mDesc"));
        mDurationView.setText(extras.getString("mDuration"));

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.addMedicine);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MedicineAddedActivity.this, LoginActivity.class));
                        break;
                    case R.id.home_menu:
                        startActivity(new Intent(MedicineAddedActivity.this, DashboardActivity.class));
                        break;
                    case R.id.viewMedicine:
                        startActivity(new Intent(MedicineAddedActivity.this, DisplayMedicineActivity.class));
                        break;
                    case R.id.viewStats:
                        startActivity(new Intent(MedicineAddedActivity.this, StatsActivity.class));
                        break;
                    case R.id.searchMedicine:
                        startActivity(new Intent(MedicineAddedActivity.this, APISearchActivity.class));
                        break;
                    case R.id.imagetoText:
                        startActivity(new Intent(MedicineAddedActivity.this, ImageToTextActivity.class));
                        break;
                    case R.id.location:
                        String url = "http://maps.google.co.uk/maps?q=Pharmacy&hl=en";
                        Intent locIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        locIntent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                        startActivity(locIntent);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            userName.setText(name);
            userEmail.setText(email);

            Picasso.get().load(photoUrl).transform(new CircleTransform()).into(profilePic);
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedicineAddedActivity.this.startActivity(new Intent(MedicineAddedActivity.this, DashboardActivity.class));
                MedicineAddedActivity.this.finish();
            }
        });
    }
}