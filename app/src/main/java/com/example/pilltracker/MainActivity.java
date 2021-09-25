package com.example.pilltracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginPageButton = findViewById(R.id.openLoginPageButton);
        loginPageButton.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        Intent open_login_page = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(open_login_page);
    }
}