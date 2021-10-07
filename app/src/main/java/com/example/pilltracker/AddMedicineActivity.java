package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AddMedicineActivity extends AppCompatActivity {

    TextInputEditText mName, mDesc, mStartDate, mEndDate, mExpDate;
    CheckBox isGeneric, isBreakfast, isLunch, isEvening, isDinner;
    TextView selectLabel;
    Button addButton;

    Calendar calendar = Calendar.getInstance();

    String currentDateField = "";
    String TAG = "AddMedicineActivity";

    private FirebaseUser currentUser;
    String username="";
    String uid = "";
    String dosageId;

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

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            username = currentUser.getDisplayName();
            uid = currentUser.getUid();
        }

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

        int[] timings = {0, 0, 0, 0};
        int totalDosage = 0;
        List<Integer> dosesTaken = new ArrayList<Integer>();;

        if(!generic) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate st = LocalDate.parse(start, formatter);
            LocalDate en = LocalDate.parse(end, formatter);

            if (st.isBefore(LocalDate.now()) || en.isBefore(LocalDate.now())) {
                Toast.makeText(AddMedicineActivity.this.getApplicationContext(), "Input Dates are in the past!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!st.isBefore(en)) {
                Toast.makeText(AddMedicineActivity.this.getApplicationContext(), "Start Date is after End Date!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean b = isBreakfast.isChecked();
            boolean l = isLunch.isChecked();
            boolean e = isEvening.isChecked();
            boolean d = isDinner.isChecked();
            if(b) timings[0] = 1;
            if(l) timings[1] = 1;
            if(e) timings[2] = 1;
            if(d) timings[3] = 1;

            long days = ChronoUnit.DAYS.between(st, en);
            int dayDose = 0;
            for(int i:timings) {
                if(i == 1) dayDose++;
            }
            totalDosage = (int)days * dayDose;

            LocalDate cur = st;
            while(isBetweenInclusive(st, en, cur)){
                dosesTaken.add(0);
                cur = cur.plusDays(1);
            }
        }

        MedicineDosage newMD = new MedicineDosage(uid, name, desc, generic, exp, start, end, Arrays.stream(timings).boxed().collect(Collectors.toList()), totalDosage, dosesTaken);

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

                dosageId = documentReference.getId();

                setAlarms(newMD);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
    }

    public void setAlarms(MedicineDosage mDosage) {
        if(!mDosage.getIsGeneric()){
            List<Integer> timings = mDosage.getTimings();
            int[] actualTimings = {9, 12, 16, 22};
            String[] mealNames = {"Breakfast", "Lunch", "Snacks", "Dinner"};

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate st = LocalDate.parse(mDosage.getStartDate(), formatter);
            LocalDate en = LocalDate.parse(mDosage.getEndDate(), formatter);

            LocalDate currentDate = st;
            while(isBetweenInclusive(st, en, currentDate)){
                int day = currentDate.getDayOfMonth();
                int month = currentDate.getMonthValue() - 1;
                int year = currentDate.getYear();

                for(int i = 0; i<timings.size(); i++) {
                    if(timings.get(i) == 1){
//                        ACTUAL
//                        setIndividualAlarm(month, day, year, actualTimings[i], 0, "showNotif", mDosage.getName(), mealNames[i], "", "");
//                        setIndividualAlarm(month, day, year, actualTimings[i]+1, 0, "askNotif", mDosage.getName(), mealNames[i], "", "");

//                        TEST
                        setIndividualAlarm(month, day, year, 19, 50, "showNotif", mDosage.getName(), mealNames[i], "", "");
                        setIndividualAlarm(month, day, year, 9, 46, "askNotif", mDosage.getName(), mealNames[i], "", "");
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        } else {
            //Code for Generic.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate exp = LocalDate.parse(mDosage.getExpDate(), formatter);
            LocalDate expRem = exp.minusDays(7);

            //Setting First Reminder
            int day = expRem.getDayOfMonth();
            int month = expRem.getMonthValue() - 1;
            int year = expRem.getYear();
//            ACTUAL
//            setIndividualAlarm(month, day, year, 9, 0, "showNotifExp", mDosage.getName(), "", mDosage.getExpDate(), "rem1");

//            TEST
            setIndividualAlarm(month, day, year, 16, 5, "showNotifExp", mDosage.getName(), "", mDosage.getExpDate(), "rem2");


            //Setting Second Reminder
            day = exp.getDayOfMonth();
            month = exp.getMonthValue() - 1;
            year = exp.getYear();
//            ACTUAL
//            setIndividualAlarm(month, day, year, 9, 0, "showNotifExp", mDosage.getName(), "", mDosage.getExpDate(), "rem2");

//            TEST
            setIndividualAlarm(month, day, year, 16, 5, "showNotifExp", mDosage.getName(), "", mDosage.getExpDate(), "rem2");
        }
    }

    public void setIndividualAlarm(int M, int d, int y, int h, int m, String notifType, String mName, String meal, String expDate, String remNo) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, M);
        calendar.set(Calendar.DAY_OF_MONTH, d);
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent;
        if(notifType.equals("showNotif")) {
            intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("caller", "showNotif");
            intent.putExtra("title", "Dose Reminder - "+mName);
            intent.putExtra("content", "Hey " + username+ "! It is time to take your dosage of "+mName + " with " + meal);
        } else if(notifType.equals("askNotif")) {
            intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("caller", "askNotif");
            intent.putExtra("title", "Dose Reminder - "+mName);
            intent.putExtra("content", "Hey " + username+ "! Did you take your dosage of "+mName + " with " + meal+"?");
            intent.putExtra("mDosageId", dosageId);
        } else if (remNo.equals("rem1")){
            intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("caller", "showNotif");
            intent.putExtra("title", "Expiry Reminder - "+mName);
            intent.putExtra("content", "Hey " + username+ "!" +mName + "expires on "+ expDate +"! Remember to restock.");
        } else {
            intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("caller", "showNotif");
            intent.putExtra("title", "Expiry Reminder - "+mName);
            intent.putExtra("content", "Hey " + username+ "!" +mName + "expires today! Remember to restock.");
        }

        final int id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            System.out.println("Alarm set!");
//          System.out.println(id);System.out.println(M);System.out.println(d);System.out.println(y);System.out.println(h);System.out.println(m);
        }
    }

    boolean isBetweenInclusive(LocalDate start, LocalDate end, LocalDate target) {
        return !target.isBefore(start) && !target.isAfter(end);
    }
}