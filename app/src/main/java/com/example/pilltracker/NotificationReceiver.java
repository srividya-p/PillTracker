package com.example.pilltracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Bundle extras = intent.getExtras();
        String caller = extras.getString("caller");
        String title = extras.getString("title");
        String content = extras.getString("content");

        if(caller.equals("showNotif")){
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.createNotification(title, content, caller, "");
        } else {
            String dosageId = extras.getString("mDosageId");
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.createNotification(title, content, caller, dosageId);
        }
    }
}