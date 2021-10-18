package com.example.pilltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    ImageView mrPill;

    Animation fadeIn;
    Animation fadeOut;
    int fadeOutTime=6000;
    int timeBetween=2000;
    int images[]={R.mipmap.alarm_clock_foreground,R.mipmap.search_foreground,R.mipmap.ic_launcher_foreground};
    static  int index=0;
    private AnimationSet animation;
    private Handler mHandler;

    private Runnable mCountUpdater = new Runnable() {
        private int mCount = 0;
        @Override
        public void run() {
            if(mCount>2)
                return;
            else
            {
                func();
                mCount++;
                mHandler.postDelayed(this, 3000);
            }
        }
    };

    public void func()
    {
        if(index<=2)
        {
            mrPill=(ImageView)findViewById(R.id.mrPill);

            mrPill.setImageResource(images[index]);

            fadeIn = new AlphaAnimation(0.00f, 1);
            fadeIn.setInterpolator(new LinearInterpolator());
            fadeIn.setDuration(1000);

            fadeOut = new AlphaAnimation(1, 0f);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setStartOffset(timeBetween);
            fadeOut.setDuration(fadeOutTime);

            animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);
            if(index != 2){
                animation.addAnimation(fadeOut);
            }
            animation.setRepeatCount(1);
            animation.setFillAfter(true);
            mrPill.setAnimation(animation);
            index=index+1;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginPageButton = findViewById(R.id.openLoginPageButton);
        loginPageButton.setOnClickListener(this::onClick);

        mrPill = findViewById(R.id.mrPill);

        mHandler = new Handler();
        mHandler.post(mCountUpdater);
    }

    @Override
    public void onClick(View view) {
        Intent open_login_page = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(open_login_page);
    }

}