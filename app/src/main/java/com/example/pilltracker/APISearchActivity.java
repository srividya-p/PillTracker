package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APISearchActivity extends AppCompatActivity {

    ImageButton medSearchButton, medClearButton;
    SearchView medSearchBar;
    TextView mName, mPurpose, mDrugType, mRoute, mSubstanceName, mIndication, mDosage, mWhenUsing, mAskDoctor, mActiveIng, mStorage, emptyText;
    ProgressBar progressBar;
    MaterialCardView infoCard;
    TextView userName, userEmail;
    private ImageView profilePic;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private Toolbar toolbar;

    private static final String TAG = "APISearchActivity";

    String uri;
    String purpose;
    String indications_and_usage;
    String dosage_and_administration;
    String when_using;
    String ask_doctor;
    String active_ingredient;
    String storage_and_handling;

    String route;
    String substance_name;
    String product_type;
    String generic_name;

    Boolean internet_flag = false;

    boolean flag = false;
    private FirebaseUser currentUser;
    private static final String BASE_URI = "https://api.fda.gov/drug/label.json?search=openfda.generic_name:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apisearch);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigationview);
        headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.username);
        profilePic = headerView.findViewById(R.id.profilePic);
        userEmail = headerView.findViewById(R.id.usermailid);

        medSearchButton = findViewById(R.id.medSearchButton);
        medClearButton = findViewById(R.id.medClearButton);
        medSearchBar = findViewById(R.id.medSearchBar);
        infoCard = findViewById(R.id.infoCard);

        mName = findViewById(R.id.mName);
        mPurpose = findViewById(R.id.mPurpose);
        mDrugType = findViewById(R.id.mDrugType);
        mRoute = findViewById(R.id.mRoute);
        mSubstanceName = findViewById(R.id.mSubstanceName);
        mIndication = findViewById(R.id.mIndication);
        mDosage = findViewById(R.id.mDosage);
        mWhenUsing = findViewById(R.id.mWhenUsing);
        mAskDoctor = findViewById(R.id.mAskDoctor);
        mActiveIng = findViewById(R.id.mActiveIng);
        mStorage = findViewById(R.id.mStorage);

        emptyText = findViewById(R.id.emptyText);

        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.searchMedicine);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(APISearchActivity.this, LoginActivity.class));
                        break;
                    case R.id.addMedicine:
                        startActivity(new Intent(APISearchActivity.this,AddMedicineActivity.class));
                        break;
                    case R.id.viewMedicine:
                        startActivity(new Intent(APISearchActivity.this,DisplayMedicineActivity.class));
                        break;
                    case R.id.viewStats:
                        startActivity(new Intent(APISearchActivity.this,StatsActivity.class));
                        break;
                    case R.id.home_menu:
                        startActivity(new Intent(APISearchActivity.this, DashboardActivity.class));
                        break;
                    case R.id.imagetoText:
                        startActivity(new Intent(APISearchActivity.this, ImageToTextActivity.class));
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

        medClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        medSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDrugInfo();
            }
        });

    }

    private void refresh() {
        emptyText.setVisibility(View.VISIBLE);
        infoCard.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        medSearchBar.setQuery("", false);
    }

    @SuppressLint("RtlHardcoded")
    private void getDrugInfo() {
        String drugName = medSearchBar.getQuery().toString();
        if(drugName.equals("")) {
            Toast.makeText(APISearchActivity.this.getApplicationContext(), "Please enter a Drug Name.", Toast.LENGTH_SHORT).show();
            return;

        } else {
            uri = BASE_URI+drugName+"&limit=1";
            Log.d(TAG,"uri: " + uri);
            new RetrieveDataTask().execute();
        }


    }

    class RetrieveDataTask extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
            emptyText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            infoCard.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(uri);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                internet_flag=true;
                return null;
            }
        }

        protected void onPostExecute(String response) {
            emptyText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            if(internet_flag){
                Toast.makeText(APISearchActivity.this.getApplicationContext(), "There is no internet connection! Please switch on WiFi/Data and try again!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(response == null) {
                Toast.makeText(APISearchActivity.this.getApplicationContext(), "The drug was not found! Please try another keyword.", Toast.LENGTH_SHORT).show();
                return;
            }

            parseJSONData(response);
        }
    }

    private void parseJSONData(String response) {
        try {
            JSONObject jObject = new JSONObject(response);
            JSONArray resArray = jObject.getJSONArray("results");
            JSONObject res = resArray.getJSONObject(0);

            purpose = JSONArrayToString(res.optJSONArray("purpose"));
            indications_and_usage = JSONArrayToString(res.optJSONArray("indications_and_usage"));
            dosage_and_administration = JSONArrayToString(res.optJSONArray("dosage_and_administration"));
            when_using = JSONArrayToString(res.optJSONArray("when_using"));
            ask_doctor = JSONArrayToString(res.optJSONArray("ask_doctor"));
            active_ingredient = JSONArrayToString(res.optJSONArray("active_ingredient"));
            storage_and_handling = JSONArrayToString(res.optJSONArray("storage_and_handling"));

            JSONObject openFDA = res.getJSONObject("openfda");
            route = JSONArrayToString(openFDA.optJSONArray("route"));
            substance_name = JSONArrayToString(openFDA.optJSONArray("substance_name"));
            product_type = JSONArrayToString(openFDA.optJSONArray("product_type"));
            generic_name = JSONArrayToString(openFDA.optJSONArray("generic_name"));

            infoCard.setVisibility(View.VISIBLE);
            mName.setText(generic_name);
            mRoute.setText(route);
            mSubstanceName.setText(substance_name);
            mDrugType.setText(product_type);
            mPurpose.setText(purpose);
            mIndication.setText(indications_and_usage);
            mDosage.setText(dosage_and_administration);
            mWhenUsing.setText(when_using);
            mAskDoctor.setText(ask_doctor);
            mActiveIng.setText(active_ingredient);
            mStorage.setText(storage_and_handling);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String JSONArrayToString(JSONArray inpArray) {
        String result = "";
        try {
            for (int i = 0; i < inpArray.length(); i++) {
                result += inpArray.get(i);
                result+=" ";
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        if(inpArray == null){
            return "Not Found";
        }
        return result;
    }
}

//Examples -
//naloxone hydrochloride
//tolnaftate
//benadryl
//paracetamol
//caffine
//ibuprofen