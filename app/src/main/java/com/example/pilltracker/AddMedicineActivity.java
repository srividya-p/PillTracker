package com.example.pilltracker;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class AddMedicineActivity extends AppCompatActivity {

    TextInputEditText mName, mDesc, mStartDate, mEndDate, mExpDate;
    CheckBox isGeneric, isBreakfast, isLunch, isEvening, isDinner;
    TextView selectLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        mName = findViewById(R.id.mName);
        mDesc = findViewById(R.id.mDesc);
        mStartDate = findViewById(R.id.mStartDate);
        mEndDate = findViewById(R.id.mEndDate);
        mExpDate = findViewById(R.id.mExpDate);

        isGeneric = findViewById(R.id.isGeneric);
        isBreakfast = findViewById(R.id.isBreakfast);
        isLunch = findViewById(R.id.isLunch);
        isEvening = findViewById(R.id.isEvening);
        isDinner = findViewById(R.id.isDinner);

        selectLabel = findViewById(R.id.selectLabel);

        mExpDate.setVisibility(View.GONE);

        isGeneric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isGeneric.isChecked()){
                    mStartDate.setVisibility(View.GONE);
                    mEndDate.setVisibility(View.GONE);
                    isBreakfast.setVisibility(View.GONE);
                    isLunch.setVisibility(View.GONE);
                    isEvening.setVisibility(View.GONE);
                    isDinner.setVisibility(View.GONE);
                    mExpDate.setVisibility(View.VISIBLE);
                    selectLabel.setVisibility(View.GONE);
                } else{
                    mStartDate.setVisibility(View.VISIBLE);
                    mEndDate.setVisibility(View.VISIBLE);
                    isBreakfast.setVisibility(View.VISIBLE);
                    isLunch.setVisibility(View.VISIBLE);
                    isEvening.setVisibility(View.VISIBLE);
                    isDinner.setVisibility(View.VISIBLE);
                    mExpDate.setVisibility(View.GONE);
                }
            }
        });

    }
}