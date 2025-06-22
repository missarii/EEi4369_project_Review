// File: app/src/main/java/com/s23001792/thiriposa/HealthUpdateActivity.java
package com.s23001792.thiriposa;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HealthUpdateActivity extends AppCompatActivity {

    private EditText etMotherName, etMotherWeight, etMotherBMI;
    private EditText etBabyName, etBabyHeight, etBabyWeight;
    private Button btnSubmit;

    // We will write to “healthUpdates/latest” in Firebase
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_update);

        // Initialize Firebase DB reference
        dbRef = FirebaseDatabase.getInstance()
                .getReference("healthUpdates")
                .child("latest");

        // Find all form fields
        etMotherName   = findViewById(R.id.etMotherName);
        etMotherWeight = findViewById(R.id.etMotherWeight);
        etMotherBMI    = findViewById(R.id.etMotherBMI);

        etBabyName   = findViewById(R.id.etBabyName);
        etBabyHeight = findViewById(R.id.etBabyHeight);
        etBabyWeight = findViewById(R.id.etBabyWeight);

        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String motherName   = etMotherName.getText().toString().trim();
            String motherWeight = etMotherWeight.getText().toString().trim();
            String motherBMI    = etMotherBMI.getText().toString().trim();

            String babyName   = etBabyName.getText().toString().trim();
            String babyHeight = etBabyHeight.getText().toString().trim();
            String babyWeight = etBabyWeight.getText().toString().trim();

            // Basic validation: ensure none of the fields are empty
            if (TextUtils.isEmpty(motherName) ||
                    TextUtils.isEmpty(motherWeight) ||
                    TextUtils.isEmpty(motherBMI) ||
                    TextUtils.isEmpty(babyName) ||
                    TextUtils.isEmpty(babyHeight) ||
                    TextUtils.isEmpty(babyWeight)) {

                Toast.makeText(HealthUpdateActivity.this,
                        "Please fill in all fields before submitting.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Build a HealthData object
            HealthData healthData = new HealthData(
                    motherName, motherWeight, motherBMI,
                    babyName, babyHeight, babyWeight
            );

            // Push to Firebase under “healthUpdates/latest”
            dbRef.setValue(healthData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(HealthUpdateActivity.this,
                                "Health data submitted successfully.",
                                Toast.LENGTH_SHORT).show();
                        // Optionally, clear the form
                        etMotherName.setText("");
                        etMotherWeight.setText("");
                        etMotherBMI.setText("");
                        etBabyName.setText("");
                        etBabyHeight.setText("");
                        etBabyWeight.setText("");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HealthUpdateActivity.this,
                                "Failed to submit data: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        });
    }
}
