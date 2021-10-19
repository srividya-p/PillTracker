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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DisplayMedicineActivity extends AppCompatActivity {

    TextView userName, userEmail;
    private ImageView profilePic;

    private final String TAG="DisplayMedicineActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private Toolbar toolbar;

    List<String> names = new ArrayList<>();
    List<String> descriptions = new ArrayList<>();
    List<String> statuses = new ArrayList<>();

    ListView dosageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_medicines);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigationview);
        headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.username);
        profilePic = headerView.findViewById(R.id.profilePic);
        userEmail = headerView.findViewById(R.id.usermailid);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.viewMedicine);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(DisplayMedicineActivity.this, LoginActivity.class));
                        break;
                    case R.id.addMedicine:
                        startActivity(new Intent(DisplayMedicineActivity.this,AddMedicineActivity.class));
                        break;
                    case R.id.searchMedicine:
                        startActivity(new Intent(DisplayMedicineActivity.this, APISearchActivity.class));
                        break;
                    case R.id.viewStats:
                        startActivity(new Intent(DisplayMedicineActivity.this,StatsActivity.class));
                        break;
                    case R.id.home_menu:
                        startActivity(new Intent(DisplayMedicineActivity.this, DashboardActivity.class));
                        break;
                    case R.id.imagetoText:
                        startActivity(new Intent(DisplayMedicineActivity.this, ImageToTextActivity.class));
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


        String uid="";
        if(currentUser != null) {
            uid = currentUser.getUid();
        }

        db.collection("medicineDosage").whereEqualTo("uid", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int count=0;
                    for(QueryDocumentSnapshot document : task.getResult()){
                        count++;
                        names.add((String) document.get("name"));
                        descriptions.add((String) document.get("desc"));

                        boolean isGeneric = (boolean) document.get("isGeneric");
                        if(isGeneric){
                            statuses.add("Generic");
                        } else {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            LocalDate st = LocalDate.parse((String) document.get("startDate"), formatter);
                            LocalDate en = LocalDate.parse((String) document.get("endDate"), formatter);

                            if(isBetweenInclusive(st, en, LocalDate.now())){
                                statuses.add("Ongoing");
                            } else {
                                statuses.add("Completed");
                            }
                        }
                    }

                    if(count == 0){
                        names.add("No Dosages to show!");
                        descriptions.add("Add a dosage to view it's status");
                        statuses.add("");
                    }

                    ViewDosageElementAdapter adapter = new ViewDosageElementAdapter(DisplayMedicineActivity.this, names, descriptions, statuses);
                    dosageList = findViewById(R.id.dosageList);
                    dosageList.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    boolean isBetweenInclusive(LocalDate start, LocalDate end, LocalDate target) {
        return !target.isBefore(start) && !target.isAfter(end);
    }
}