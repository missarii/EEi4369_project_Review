// File: app/src/main/java/com/s23001792/thiriposa/MotherHomePage.java
package com.s23001792.thiriposa;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MotherHomePage extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView leftArrow, rightArrow;
    private ImageView contentImage;
    private TextView contentLabel;
    private int currentIndex = 0;

    private final int[] imageResIds = {
            R.drawable.mother_baby_mother, // Index 0: “Mom & B Baby Health Report”
            R.drawable.appoint_send,       // Index 1: “Appointment Booking”
            R.drawable.bicycle_ride        // Index 2: “Bicycle Ride”
    };

    private final String[] imageLabels = {
            "Mom & B Baby Health Report",
            "Appointment Booking",
            "Locations"
    };

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mother_home);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        welcomeText  = findViewById(R.id.welcomeText);
        leftArrow    = findViewById(R.id.leftArrow);
        rightArrow   = findViewById(R.id.rightArrow);
        contentImage = findViewById(R.id.contentImage);
        contentLabel = findViewById(R.id.contentLabel);

        // Arrow-pulse animation
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.arrow_pulse);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        leftArrow.startAnimation(pulse);
        rightArrow.startAnimation(pulse);

        // Load user name from Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            dbRef.child(uid).child("username")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.getValue(String.class);
                            if (username != null && !username.trim().isEmpty()) {
                                welcomeText.setText("Welcome, " + username);
                            } else {
                                welcomeText.setText("Welcome, Mother");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            welcomeText.setText("Welcome, Mother");
                        }
                    });
        } else {
            welcomeText.setText("Welcome, Mother");
        }

        // Initialize slider content
        updateContentDisplay();

        leftArrow.setOnClickListener(v -> {
            currentIndex = (currentIndex - 1 + imageResIds.length) % imageResIds.length;
            updateContentDisplay();
        });

        rightArrow.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % imageResIds.length;
            updateContentDisplay();
        });

        // When user taps the central image:
        contentImage.setOnClickListener(v -> {
            if (currentIndex == 0) {
                // Index 0: “Mom & B Baby Health Report” -> HealthReportActivity
                Intent intent = new Intent(MotherHomePage.this, HealthReportActivity.class);
                startActivity(intent);

            } else if (currentIndex == 1) {
                // Index 1: Appointment Booking -> AppointmentSendActivity
                Intent intent = new Intent(MotherHomePage.this, AppointmentSendActivity.class);
                startActivity(intent);

            } else if (currentIndex == 2) {
                // Index 2: “Bicycle Ride” -> MapActivity
                Intent intent = new Intent(MotherHomePage.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateContentDisplay() {
        contentImage.setImageResource(imageResIds[currentIndex]);
        contentLabel.setText(imageLabels[currentIndex]);
    }
}
