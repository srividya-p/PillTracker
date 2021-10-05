package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

public class AddMedicineActivity extends AppCompatActivity {

    TextInputEditText mName, mDesc, mStartDate, mEndDate, mExpDate;
    CheckBox isGeneric, isBreakfast, isLunch, isEvening, isDinner;
    TextView selectLabel;
    Button addButton;

    Calendar calendar = Calendar.getInstance();

    String currentDateField = "";
    String TAG = "AddMedicineActivity";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        mName = findViewById(R.id.mName);
        mDesc = findViewById(R.id.mDesc);
        mStartDate = findViewById(R.id.mStartDate);
        mEndDate = findViewById(R.id.mEndDate);
        mExpDate = findViewById(R.id.mExpDate);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        isGeneric = findViewById(R.id.isGeneric);
        isBreakfast = findViewById(R.id.isBreakfast);
        isLunch = findViewById(R.id.isLunch);
        isEvening = findViewById(R.id.isEvening);
        isDinner = findViewById(R.id.isDinner);

        selectLabel = findViewById(R.id.selectLabel);

        addButton = findViewById(R.id.addButton);

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

        mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddMedicineActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                currentDateField = "start";
            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddMedicineActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                currentDateField = "end";
            }
        });

        mExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddMedicineActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                currentDateField = "exp";
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMedicine();
            }
        });
    }

    public void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        if(currentDateField == "start"){
            mStartDate.setText(sdf.format(calendar.getTime()));
        } else if(currentDateField == "end") {
            mEndDate.setText(sdf.format(calendar.getTime()));
        } else {
            mExpDate.setText(sdf.format(calendar.getTime()));
        }
    }

    public void addMedicine() {
        String name = mName.getText().toString();
        String desc = mDesc.getText().toString();
        boolean generic = isGeneric.isChecked();
        String start = mStartDate.getText().toString();
        String end = mEndDate.getText().toString();
        String exp = mExpDate.getText().toString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate st = LocalDate.parse(start, formatter);
        LocalDate en = LocalDate.parse(end, formatter);

        if(st.isBefore(LocalDate.now()) || en.isBefore(LocalDate.now())){
            Toast.makeText(AddMedicineActivity.this.getApplicationContext(), "Input Dates are in the past!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(! st.isBefore(en)){
            Toast.makeText(AddMedicineActivity.this.getApplicationContext(), "Start Date is after End Date!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean b = isBreakfast.isChecked();
        boolean l = isLunch.isChecked();
        boolean e = isEvening.isChecked();
        boolean d = isDinner.isChecked();
        int[] timings = {0, 0, 0, 0};
        if(b) timings[0] = 1;
        if(l) timings[1] = 1;
        if(e) timings[2] = 1;
        if(d) timings[3] = 1;

        long days = ChronoUnit.DAYS.between(st, en);
        int dayDose = 0;
        for(int i:timings) {
            if(i == 1) dayDose++;
        }
        int totalDosage = (int)days * dayDose;

        MedicineDosage newMD = new MedicineDosage(name, desc, generic, exp, start, end, Arrays.stream(timings).boxed().collect(Collectors.toList()), totalDosage);
        System.out.println(totalDosage);
        db.collection("medicineDosage").add(newMD).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                Intent mAdded = new Intent(AddMedicineActivity.this, MedicineAddedActivity.class);
                mAdded.putExtra("mName", name);
                mAdded.putExtra("mDesc", desc);
                mAdded.putExtra("mDuration", generic ? "Expires - "+exp : start+" - "+end );
                AddMedicineActivity.this.startActivity(mAdded);
                AddMedicineActivity.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }
}