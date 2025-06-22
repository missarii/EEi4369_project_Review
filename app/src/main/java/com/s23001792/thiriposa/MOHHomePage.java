package com.s23001792.thiriposa;

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

public class MOHHomePage extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView leftArrow, rightArrow;
    private ImageView contentImage;
    private TextView contentLabel;
    private int currentIndex = 0;

    // Re-use exactly the same images and labels as NurseHomePage
    private final int[] imageResIds = {
            R.drawable.mother_baby_mother,
            R.drawable.bmi_update,
            R.drawable.bicycle_ride
    };

    private final String[] imageLabels = {
            "Mom & B Baby Report",
            "Clinical Report",
            "Baby Name 3"
    };

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moh_home);

        // Initialize Firebase Auth & DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Bind UI elements
        welcomeText  = findViewById(R.id.welcomeText);
        leftArrow    = findViewById(R.id.leftArrow);
        rightArrow   = findViewById(R.id.rightArrow);
        contentImage = findViewById(R.id.contentImage);
        contentLabel = findViewById(R.id.contentLabel);

        // Start the same arrow‐pulse animation as NurseHomePage
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.arrow_pulse);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        leftArrow.startAnimation(pulse);
        rightArrow.startAnimation(pulse);

        // Load the logged‐in user's “username” from Firebase (if available)
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
                                welcomeText.setText("Welcome, MOH Officer");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // On error, fall back to a static greeting
                            welcomeText.setText("Welcome, MOH Officer");
                        }
                    });
        } else {
            // No user logged in (fallback)
            welcomeText.setText("Welcome, MOH Officer");
        }

        // Initialize slider content
        updateContentDisplay();

        // Left‐arrow click: go to previous image
        leftArrow.setOnClickListener(v -> {
            currentIndex = (currentIndex - 1 + imageResIds.length) % imageResIds.length;
            updateContentDisplay();
        });

        // Right‐arrow click: go to next image
        rightArrow.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % imageResIds.length;
            updateContentDisplay();
        });
    }

    // Helper method to update the ImageView and Label
    private void updateContentDisplay() {
        contentImage.setImageResource(imageResIds[currentIndex]);
        contentLabel.setText(imageLabels[currentIndex]);
    }
}
