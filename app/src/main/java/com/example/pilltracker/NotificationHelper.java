package com.example.pilltracker;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.DebugUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotificationHelper {
    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public static final String TAG = "NotificationHelper";
    public static final String ACTION_YES = "action_yes";
    public static final String ACTION_NO = "action_no";
    public static final int NOTIFICATION_ID = 1;
    public static String mDosageId;

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    NotificationHelper(Context context) {
        mContext = context;
    }

    void createNotification(String title, String content, String caller, String mDoseId) {

        mDosageId = mDoseId;

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        if (caller.equals("showNotif")) {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title)
                    .setAutoCancel(false)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent);
        } else {
            Intent yesIntent = new Intent(mContext, NotificationActionService.class).setAction(ACTION_YES);
            PendingIntent yesPendingIntent = PendingIntent.getService(mContext, 10, yesIntent, PendingIntent.FLAG_ONE_SHOT);

            Intent noIntent = new Intent(mContext, NotificationActionService.class).setAction(ACTION_NO);
            PendingIntent noPendingIntent = PendingIntent.getService(mContext, 11, noIntent, PendingIntent.FLAG_ONE_SHOT);

            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title)
                    .setAutoCancel(false)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(resultPendingIntent)
                    .addAction(R.drawable.ic_baseline_check_circle_24, "YES", yesPendingIntent)
                    .addAction(R.drawable.ic_baseline_check_circle_24, "NO", noPendingIntent);
        }

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "PILL_TRACKER_REMINDER", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_ID/* Request Code */, mBuilder.build());
    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            System.out.println("Received notification action: " + action);
            if (ACTION_YES.equals(action)) {
                // TODO: handle action Yes.
                System.out.println("YES!");
                System.out.println(mDosageId);

                db.collection("medicineDosage").document(mDosageId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                Map<String, Object> data = document.getData();
                                List<Integer> dosesTaken = (List<Integer>)data.get("dosesTaken");

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                LocalDate st = LocalDate.parse((String) document.get("startDate"), formatter);

                                LocalDate curr  = st;
                                for(int i=0; i<dosesTaken.size(); i++) {
                                    if(curr.isEqual(LocalDate.now())){
                                        int val = Integer.parseInt(String.valueOf(dosesTaken.get(i)));
                                        val = val+1;
                                        dosesTaken.set(i, val);
                                        break;
                                    }
                                    curr.plusDays(1);
                                }

                                String uid = (String) document.get("uid");
                                String name = (String) document.get("name");
                                String desc = (String) document.get("desc");
                                boolean isGeneric = (boolean) document.get("isGeneric");
                                String expDate = (String) document.get("expDate");
                                String startDate = (String) document.get("startDate");
                                String endDate = (String) document.get("endDate");
                                List<Integer> timings = (List<Integer>) document.get("timings");
                                int totalDosage = Integer.parseInt(String.valueOf(document.get("totalDosage")));

                                MedicineDosage newMD = new MedicineDosage(uid, name, desc, isGeneric, expDate, startDate, endDate, timings, totalDosage, dosesTaken);

                                db.collection("medicineDosage").document(mDosageId).set(newMD).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d(TAG, "Stats updated successfully!");
                                        } else {
                                            Log.e(TAG, "Error updating stats!");
                                        }
                                    }
                                });

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            } else {
                // TODO: handle action No.
                System.out.println("NO!");
                NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
            }
        }
    }
}
