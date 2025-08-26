package com.s23001792.thiriposa;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppointmentSendActivity extends AppCompatActivity {

    private Spinner centreSpinner;
    private Spinner nurseSpinner;
    private EditText motherNameEt, babyNameEt;
    private Button submitButton;

    private DatabaseReference usersRef;
    private DatabaseReference appointmentRef;
    private FirebaseAuth mAuth;

    // Lists for populating the nurseSpinner
    private ArrayList<String> nurseUsernames = new ArrayList<>();
    private ArrayList<String> nurseUids      = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_send);

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        appointmentRef = FirebaseDatabase.getInstance().getReference("appointment_send");

        // Bind UI elements
        centreSpinner = findViewById(R.id.centreSpinner);
        nurseSpinner  = findViewById(R.id.nurseSpinner);
        motherNameEt  = findViewById(R.id.motherNameEt);
        babyNameEt    = findViewById(R.id.babyNameEt);
        submitButton  = findViewById(R.id.submitButton);

        // 1) Setup the “Booking Centre” Spinner (static list)
        String[] centres = {
                "Ampara",
                "Anuradhapura",
                "Badulla",
                "Batticaloa",
                "Colombo",
                "Galle",
                "Gampaha",
                "Hambantota",
                "Jaffna",
                "Kalutara",
                "Kandy",
                "Kegalle",
                "Kilinochchi",
                "Kurunegala",
                "Mannar",
                "Matale",
                "Matara",
                "Monaragala",
                "Mullaitivu",
                "Nuwara Eliya",
                "Polonnaruwa",
                "Puttalam",
                "Ratnapura",
                "Trincomalee",
                "Vavuniya"
        };

        ArrayAdapter<String> centreAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                centres
        );
        centreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        centreSpinner.setAdapter(centreAdapter);

        // 2) Load all Home Nurses into nurseSpinner (display their USERNAMEs)
        loadHomeNurses();

        // 3) Handle Submit button click
        submitButton.setOnClickListener(v -> sendAppointmentData());
    }

    /**
     * Query the "users" node for all children where role == "Home Nurse",
     * then populate nurseSpinner with their usernames (falling back to email if needed).
     */
    private void loadHomeNurses() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nurseUsernames.clear();
                nurseUids.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String role = userSnap.child("role").getValue(String.class);
                    if ("Home Nurse".equals(role)) {
                        String uid = userSnap.getKey();

                        // 1) Try to read “username” field
                        String username = userSnap.child("username").getValue(String.class);
                        if (username == null || username.trim().isEmpty()) {
                            // 2) If “username” is missing, fall back to “email”
                            String email = userSnap.child("email").getValue(String.class);
                            if (email != null && !email.trim().isEmpty()) {
                                username = email;
                            } else {
                                // 3) If even “email” is missing, use a placeholder
                                username = "nurse_" + uid.substring(0, 6);
                            }
                        }

                        nurseUsernames.add(username);
                        nurseUids.add(uid);
                    }
                }

                if (nurseUsernames.isEmpty()) {
                    // No home nurses found → show a placeholder entry
                    nurseUsernames.add("No Home Nurses Found");
                    nurseUids.add("");
                }

                ArrayAdapter<String> nurseAdapter = new ArrayAdapter<>(
                        AppointmentSendActivity.this,
                        android.R.layout.simple_spinner_item,
                        nurseUsernames
                );
                nurseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                nurseSpinner.setAdapter(nurseAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        AppointmentSendActivity.this,
                        "Failed to load nurses: " + error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    /**
     * Gather all form fields and push a new child under "appointment_send".
     */
    private void sendAppointmentData() {
        String selectedCentre = centreSpinner.getSelectedItem().toString();
        int    nurseIndex     = nurseSpinner.getSelectedItemPosition();

        String selectedNurseUid      = "";
        String selectedNurseUsername = "";

        // If a valid nurse was selected, retrieve its UID & username
        if (nurseIndex >= 0 && nurseIndex < nurseUids.size()) {
            selectedNurseUid      = nurseUids.get(nurseIndex);
            selectedNurseUsername = nurseUsernames.get(nurseIndex);
        }

        String motherFullName = motherNameEt.getText().toString().trim();
        String babyFullName   = babyNameEt.getText().toString().trim();

        // Basic validation
        if (motherFullName.isEmpty() || babyFullName.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please fill in both Mother and Baby full names.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if (selectedNurseUid.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please select a valid Home Nurse (or check if any exist).",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Prepare appointment data
        String motherUid = mAuth.getCurrentUser() != null
                ? mAuth.getCurrentUser().getUid()
                : "";

        Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("centre",       selectedCentre);
        appointmentData.put("nurseUid",     selectedNurseUid);
        appointmentData.put("nurseUsername", selectedNurseUsername);
        appointmentData.put("motherUid",    motherUid);
        appointmentData.put("motherName",   motherFullName);
        appointmentData.put("babyName",     babyFullName);

        // Push a new child under "appointment_send"
        appointmentRef.push()
                .setValue(appointmentData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                AppointmentSendActivity.this,
                                "Appointment request sent successfully!",
                                Toast.LENGTH_LONG
                        ).show();
                        // Clear form for convenience
                        motherNameEt.setText("");
                        babyNameEt.setText("");
                        centreSpinner.setSelection(0);
                        nurseSpinner.setSelection(0);
                    } else {
                        Toast.makeText(
                                AppointmentSendActivity.this,
                                "Failed to send appointment: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}
