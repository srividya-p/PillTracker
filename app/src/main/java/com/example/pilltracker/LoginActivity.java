package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shaishavgandhi.loginbuttons.GooglePlusButton;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    private Button loginButton;
    private Button signUpPageButton;
    private Button forgotPasswordButton;
    private Button googlePlusButton;

    private EditText emailText;
    private EditText passwordText;

    private ProgressBar loginProgress;

    private FirebaseAuth fireBaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //View Initialisation
        loginButton = findViewById(R.id.loginButton);
        signUpPageButton = findViewById(R.id.signUpPageButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        googlePlusButton = findViewById(R.id.googlePlusButton);

        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);

        loginProgress = findViewById(R.id.loginProgress);

        loginProgress.setVisibility(View.GONE);

        //Firebase and Google Auth
        fireBaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Check if user is already Logged in
        if(fireBaseAuth.getCurrentUser() != null){
            Intent open_dashboard = new Intent(getBaseContext(), DashboardActivity.class);
            startActivity(open_dashboard);
            finish();
        }

        //Setting Event Listeners
        signUpPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_signup_page = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(open_signup_page);
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgot_page = new Intent(getBaseContext(), ForgotPasswordActivity.class);
                startActivity(forgot_page);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });

        googlePlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
    }

    public void logIn() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        //Validation
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this.getApplicationContext(), "Please enter an Email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this.getApplicationContext(), "Please enter a Password.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Logging In User
        loginProgress.setVisibility(View.VISIBLE);
        fireBaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWitEmailAndPassword"+task.isSuccessful());

                if(!task.isSuccessful()){
                    //Authorization Failed
                    loginProgress.setVisibility(View.GONE);
                    System.out.println(task.getException());
                    Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    //Authorization successful
                    checkIfEmailVerified();
                }
            }
        });
    }

    public void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user.isEmailVerified()) {
            //Email was verified
            finish();
            loginProgress.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
            updateUI();
        }
        else {
            //Email was not Verified, Sign out user and Restart activity
            loginProgress.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "This Email is not verified", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            overridePendingTransition(0,0);
            finish();
            overridePendingTransition(0,0);
            startActivity(getIntent());
        }
    }

    public void googleSignIn() {
        loginProgress.setVisibility(View.VISIBLE);
        Intent gSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(gSignInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching Intent
        if(requestCode == RC_SIGN_IN){
            Task <GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //Google SignIn Successful
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "fireBaseAuthWithGoogle:"+account.getId());
                fireBaseAuthWithGoogle(account.getIdToken());
            } catch(ApiException e) {

            }
        }
    }

    private void fireBaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fireBaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loginProgress.setVisibility(View.GONE);
                    Log.d(TAG, "signInWithCredential:success");
//                    FirebaseUser user = fireBaseAuth.getCurrentUser();
                    updateUI();
                } else {
                    loginProgress.setVisibility(View.GONE);
                    Log.d(TAG, "signInWithCredential:failure");
                }
            }
        });
    }

    public void updateUI() {
        //Open Dashboard upon successful Auth
        LoginActivity.this.startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        LoginActivity.this.finish();
    }
}