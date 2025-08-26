// File: app/src/main/java/com/s23001792/thiriposa/NurseHomePage.java
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
import com.google.firebase.database.*;

public class NurseHomePage extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView leftArrow, rightArrow;
    private ImageView contentImage;
    private TextView contentLabel;
    private int currentIndex = 0;

    private final int[] imageResIds = {
            R.drawable.mom_boy1,         // Index 0: taps -> HealthUpdateActivity
            R.drawable.appoint_receive,   // Index 1: taps -> AppointmentReceiveActivity
            R.drawable.thiriposa_packet
    };

    private final String[] imageLabels = {
            "Mom & Baby Health Update",
            "Appointment Pending",
            "Thiriposa Packet Update"
    };

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nurse_home);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        welcomeText  = findViewById(R.id.welcomeText);
        leftArrow    = findViewById(R.id.leftArrow);
        rightArrow   = findViewById(R.id.rightArrow);
        contentImage = findViewById(R.id.contentImage);
        contentLabel = findViewById(R.id.contentLabel);

        // Start arrow-pulse animation
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
                                welcomeText.setText("Welcome, Home Nurse");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            welcomeText.setText("Welcome, Home Nurse");
                        }
                    });
        } else {
            welcomeText.setText("Welcome, Home Nurse");
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

        // If user taps the central image:
        contentImage.setOnClickListener(v -> {
            if (currentIndex == 0) {
                Intent intent = new Intent(NurseHomePage.this, HealthUpdateActivity.class);
                startActivity(intent);
            } else if (currentIndex == 1) {
                Intent intent = new Intent(NurseHomePage.this, AppointmentReceiveActivity.class);
                startActivity(intent);
            } else if (currentIndex == 2) {
                Intent intent = new Intent(NurseHomePage.this, PacketUpdate.class);
                startActivity(intent);
            }
        });

    }

    private void updateContentDisplay() {
        contentImage.setImageResource(imageResIds[currentIndex]);
        contentLabel.setText(imageLabels[currentIndex]);
    }
}
