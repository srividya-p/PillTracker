package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ImageToTextActivity extends AppCompatActivity {

    ImageView capturedImageView;
    ImageButton captureAgainButton, proceedButton;
    TextView extractedText;
    public static final int CAMERA_REQUEST_CODE = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_text);

        capturedImageView = findViewById(R.id.capturedImage);
        captureAgainButton = findViewById(R.id.captureAgainButton);
        proceedButton = findViewById(R.id.proceedButton);
        extractedText = findViewById(R.id.extractedText);

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