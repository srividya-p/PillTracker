package com.example.pilltracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DisplayMedicineActivity extends AppCompatActivity {

    private final String TAG="DisplayMedicineActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;

    List<String> names = new ArrayList<>();
    List<String> descriptions = new ArrayList<>();
    List<String> statuses = new ArrayList<>();

    ListView dosageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_medicines);

        String uid="";
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            uid = currentUser.getUid();
        }

        db.collection("medicineDosage").whereEqualTo("uid", uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        names.add((String) document.get("name"));
                        descriptions.add((String) document.get("desc"));

                        boolean isGeneric = (boolean) document.get("isGeneric");
                        if(isGeneric){
                            statuses.add("Generic");
                        } else {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            LocalDate st = LocalDate.parse((String) document.get("startDate"), formatter);
                            LocalDate en = LocalDate.parse((String) document.get("endDate"), formatter);

                            if(isBetweenInclusive(st, en, LocalDate.now())){
                                statuses.add("Ongoing");
                            } else {
                                statuses.add("Completed");
                            }
                        }
                    }

                    ViewDosageElementAdapter adapter = new ViewDosageElementAdapter(DisplayMedicineActivity.this, names, descriptions, statuses);
                    dosageList = findViewById(R.id.dosageList);
                    dosageList.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    boolean isBetweenInclusive(LocalDate start, LocalDate end, LocalDate target) {
        return !target.isBefore(start) && !target.isAfter(end);
    }
}