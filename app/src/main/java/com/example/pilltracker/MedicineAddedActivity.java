package com.example.pilltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MedicineAddedActivity extends AppCompatActivity {
    TextView mNameView, mDescView, mDurationView;
    Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_added);
        
        mNameView = findViewById(R.id.mNameView);
        mDescView = findViewById(R.id.mDescView);
        mDurationView = findViewById(R.id.mDurationView);
        
        backButton = findViewById(R.id.backButton);
        
        Bundle extras = getIntent().getExtras();
        
        mNameView.setText(extras.getString("mName"));
        mDescView.setText(extras.getString("mDesc"));
        mDurationView.setText(extras.getString("mDuration"));
        
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MedicineAddedActivity.this.startActivity(new Intent(MedicineAddedActivity.this, DashboardActivity.class));
                MedicineAddedActivity.this.finish();
            }
        });
    }
}