package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.squareup.picasso.Picasso;

public class ImageToTextActivity extends AppCompatActivity {

    ImageView capturedImageView;
    ImageButton captureAgainButton, proceedButton;
    TextView extractedText;

    TextView userName, userEmail;
    private ImageView profilePic;
    private FirebaseUser currentUser;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private Toolbar toolbar;

    public static final int CAMERA_REQUEST_CODE = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigationview);
        headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.username);
        profilePic = headerView.findViewById(R.id.profilePic);
        userEmail = headerView.findViewById(R.id.usermailid);

        capturedImageView = findViewById(R.id.capturedImage);
        captureAgainButton = findViewById(R.id.captureAgainButton);
        proceedButton = findViewById(R.id.proceedButton);
        extractedText = findViewById(R.id.extractedText);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.imagetoText);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ImageToTextActivity.this, LoginActivity.class));
                        break;
                    case R.id.addMedicine:
                        startActivity(new Intent(ImageToTextActivity.this,AddMedicineActivity.class));
                        break;
                    case R.id.viewMedicine:
                        startActivity(new Intent(ImageToTextActivity.this,DisplayMedicineActivity.class));
                        break;
                    case R.id.viewStats:
                        startActivity(new Intent(ImageToTextActivity.this,StatsActivity.class));
                        break;
                    case R.id.searchMedicine:
                        startActivity(new Intent(ImageToTextActivity.this, APISearchActivity.class));
                        break;
                    case R.id.home_menu:
                        startActivity(new Intent(ImageToTextActivity.this, DashboardActivity.class));
                        break;
                    case R.id.location:
                        String url = "http://maps.google.co.uk/maps?q=Pharmacy&hl=en";
                        Intent locIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        locIntent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                        startActivity(locIntent);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            userName.setText(name);
            userEmail.setText(email);

            Picasso.get().load(photoUrl).transform(new CircleTransform()).into(profilePic);
        }

        captureAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        chooseImage();
    }

    private void chooseImage() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
        }
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK && data.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        capturedImageView.setImageBitmap(bitmap);
                        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                        int rotationDegree = 0;
                        InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);

                        Task<Text> result =
                                recognizer.process(image)
                                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                                            @Override
                                            public void onSuccess(Text visionText) {
                                                Log.d("TAG", "Fetched Text Successfully");
                                                StringBuilder sb = new StringBuilder();
                                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                                    String text = block.getText();
                                                    sb.append(text);
                                                    sb.append(" ");
                                                }
                                                extractedText.setText(sb.toString());

                                                proceedButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Intent addMedicineIntent = new Intent(ImageToTextActivity.this, AddMedicineActivity.class);
                                                        addMedicineIntent.putExtra("name", sb.toString());
                                                        addMedicineIntent.putExtra("desc", sb.toString());
                                                        startActivity(addMedicineIntent);
                                                    }
                                                });
                                            }
                                        })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                });

                    }
                }
                break;
        }

//            super.onActivityResult(requestCode, resultCode, data);
//            //Extract the image
//            Bundle bundle = data.getExtras();
//            Bitmap bitmap = (Bitmap) bundle.get("data");
//            mImageView.setImageBitmap(bitmap);
//

    }
}