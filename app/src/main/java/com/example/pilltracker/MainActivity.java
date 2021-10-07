package com.example.pilltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginPageButton = findViewById(R.id.openLoginPageButton);
        loginPageButton.setOnClickListener(this::onClick);

//        myAlarm();
    }

    @Override
    public void onClick(View view) {
        Intent open_login_page = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(open_login_page);
    }

//    public void myAlarm() {
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.MONTH, 9);
//        calendar.set(Calendar.DAY_OF_MONTH, 6);
//        calendar.set(Calendar.YEAR, 2021);
//        calendar.set(Calendar.HOUR_OF_DAY, 12);
//        calendar.set(Calendar.MINUTE, 53);
//        calendar.set(Calendar.SECOND, 0);
//
//        if (calendar.getTime().compareTo(new Date()) < 0)
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//
//        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//        if (alarmManager != null) {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//            System.out.println("Alarm set!");
//        }
//
//    }


    //https://developer.android.com/training/notify-user/build-notification
    //https://stackoverflow.com/questions/33055129/how-to-show-a-notification-everyday-at-a-certain-time-even-when-the-app-is-close
}