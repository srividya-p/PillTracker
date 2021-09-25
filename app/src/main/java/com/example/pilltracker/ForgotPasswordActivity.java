package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button resetPasswordButton;
    Button loginPageButton;

    EditText emailText;

    private ProgressBar forgotPswdProgress;

    private FirebaseAuth fireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        loginPageButton = findViewById(R.id.loginPageButton);

        emailText = findViewById(R.id.email);

        forgotPswdProgress = findViewById(R.id.forgotPswdProgress);
        forgotPswdProgress.setVisibility(View.GONE);

        fireBaseAuth = FirebaseAuth.getInstance();

        loginPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_login_page = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(open_login_page);
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    public void resetPassword() {
        String email = emailText.getText().toString();

        //Validation
        if(!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(ForgotPasswordActivity.this.getApplicationContext(), "Please enter a valid Email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        forgotPswdProgress.setVisibility(View.VISIBLE);
        //Sending Reset Email
        fireBaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Reset Password email sent successfully
                    forgotPswdProgress.setVisibility(View.GONE);
                    Toast.makeText(ForgotPasswordActivity.this, "Reset password mail sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                    ForgotPasswordActivity.this.startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    ForgotPasswordActivity.this.finish();
                } else {
                    forgotPswdProgress.setVisibility(View.GONE);
                    Toast.makeText(ForgotPasswordActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}