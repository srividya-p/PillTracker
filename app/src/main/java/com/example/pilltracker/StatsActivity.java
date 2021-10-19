package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private final String TAG="StatsActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;

    TextView userName, userEmail;
    private ImageView profilePic;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private Toolbar toolbar;

    private static int MAX_X_VALUE = 0;

    private static final String STACK_1_LABEL = "Doses Completed";
    private static final String STACK_2_LABEL = "Doses Pending";
    private static final String SET_LABEL = "";

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigationview);
        headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.username);
        profilePic = headerView.findViewById(R.id.profilePic);
        userEmail = headerView.findViewById(R.id.usermailid);

        barChart = findViewById(R.id.dosageStackedBarChart);
        ArrayList<String> xLabel = new ArrayList<>();
        ArrayList<Integer> totalDosages = new ArrayList<>();
        ArrayList<Integer> dosesTaken = new ArrayList<>();

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.viewStats);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(StatsActivity.this, LoginActivity.class));
                        break;
                    case R.id.addMedicine:
                        startActivity(new Intent(StatsActivity.this,AddMedicineActivity.class));
                        break;
                    case R.id.viewMedicine:
                        startActivity(new Intent(StatsActivity.this,DisplayMedicineActivity.class));
                        break;
                    case R.id.home_menu:
                        startActivity(new Intent(StatsActivity.this,DashboardActivity.class));
                        break;
                    case R.id.searchMedicine:
                        startActivity(new Intent(StatsActivity.this, APISearchActivity.class));
                        break;
                    case R.id.imagetoText:
                        startActivity(new Intent(StatsActivity.this, ImageToTextActivity.class));
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

        db.collection("medicineDosage").whereEqualTo("uid", uid).orderBy("endDate").limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    int count=0;
                    for(QueryDocumentSnapshot document : task.getResult()){
                        xLabel.add(document.getString("name"));
                        count++;

                        int doseTaken=0;
                        List<Integer> dosesTakenDocValue = (List<Integer>)document.get("dosesTaken");
                        for(int i=0; i<dosesTakenDocValue.size(); i++) {
                            doseTaken+=Integer.parseInt(String.valueOf(dosesTakenDocValue.get(i)));
                        }
                        dosesTaken.add(doseTaken);
                        totalDosages.add(Integer.parseInt(String.valueOf(document.get("totalDosage"))));
                    }

                    MAX_X_VALUE = count;

                    if(count==0){
                        barChart.setNoDataText("Add Dosages to see statistics.");
                        Paint p = barChart.getPaint(Chart.PAINT_INFO);
                        p.setColor(Color.BLACK);
                        p.setTextSize(50f);
                        barChart.invalidate();
                    } else {
                        BarData data = createChartData(dosesTaken, totalDosages);
                        configureChartAppearance(xLabel);
                        prepareChartData(data);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void configureChartAppearance(ArrayList<String> xLabel) {
        barChart.setDrawGridBackground(false);
        barChart.setDrawValueAboveBar(false);

        barChart.getDescription().setEnabled(false);

        barChart.animateY(1000);
        barChart.animateX(1000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int)value);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);

        Legend l = barChart.getLegend();
        l.setTextSize(12f);
        l.setTextColor(Color.BLACK);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
    }


    private void prepareChartData(BarData data) {
        data.setValueTextSize(12f);
        barChart.setData(data);
        barChart.invalidate();
    }

    private BarData createChartData(ArrayList<Integer> doseTaken, ArrayList<Integer> totalDosages) {
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < MAX_X_VALUE; i++) {
            int value1 = doseTaken.get(i);
            int value2 = totalDosages.get(i) - doseTaken.get(i);
            values.add(new BarEntry(i, new float[]{value1, value2}));
        }

        BarDataSet setY = new BarDataSet(values, SET_LABEL);

        setY.setColors(new int[] {ColorTemplate.MATERIAL_COLORS[0], ColorTemplate.MATERIAL_COLORS[1]});
        setY.setStackLabels(new String[] {STACK_1_LABEL, STACK_2_LABEL});

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(setY);

        BarData data = new BarData(dataSets);
        return data;
    }
}



