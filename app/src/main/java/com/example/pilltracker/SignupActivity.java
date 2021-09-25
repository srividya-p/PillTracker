package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignupActivity extends AppCompatActivity{

    private static final String TAG = "SignupActivity";

    private Button signUpButton;
    private Button signUpPageButton;

    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPasswordText;

    private ProgressBar signUpProgress;

    private FirebaseAuth fireBaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpButton = findViewById(R.id.signUpButton);
        signUpPageButton = findViewById(R.id.loginPageButton);

        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        confirmPasswordText = findViewById(R.id.confirmPassword);

        signUpProgress = findViewById(R.id.signUpProgress);
        signUpProgress.setVisibility(View.GONE);

        fireBaseAuth = FirebaseAuth.getInstance();

        //Setting Event Listeners
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        signUpPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_login_page = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(open_login_page);
            }
        });
    }

    public void signUp() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();

        //Validation
        if(!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(SignupActivity.this.getApplicationContext(), "Please enter a valid Email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(SignupActivity.this.getApplicationContext(), "Please enter a Password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6) {
            Toast.makeText(SignupActivity.this.getApplicationContext(), "Password must have at least 6 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!confirmPassword.equals(password)) {
            Toast.makeText(SignupActivity.this.getApplicationContext(), "Entered passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        signUpProgress.setVisibility(View.VISIBLE);

        //Creating new User
        fireBaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmailAndPassword:onComplete"+task.isSuccessful());
                if(!task.isSuccessful()){
                    signUpProgress.setVisibility(View.GONE);
                    //Sign in failed
                    Toast.makeText(SignupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    //Sign in successful, AuthStateChange will occur
                    System.out.println("Signup Successful!");
                    sendVerificationEmail();
                }
            }
        });

    }

    public void sendVerificationEmail() {
        FirebaseUser user = fireBaseAuth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Set User's Name and Photo
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String name = user.getEmail().split("@")[0];
                    name = name.replace('.', ' ');
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(Uri.parse("https://t4.ftcdn.net/jpg/00/64/67/63/360_F_64676383_LdbmhiNM6Ypzb3FM4PPuFP9rHe7ri8Ju.jpg"))
                            .build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "User Profile Updated");
                            }
                        }
                    });

                    //Sign out User
                    FirebaseAuth.getInstance().signOut();

                    signUpProgress.setVisibility(View.GONE);

                    //Mail was sent successfully
                    Toast.makeText(SignupActivity.this.getApplicationContext(), "A Verification email has been sent to the entered email address.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                } else {
                    //Print exception and restart activity
                    signUpProgress.setVisibility(View.GONE);
                    System.out.println(task.getException());
                    overridePendingTransition(0,0);
                    finish();
                    overridePendingTransition(0,0);
                    startActivity(getIntent());
                }
            }
        });
    }
}