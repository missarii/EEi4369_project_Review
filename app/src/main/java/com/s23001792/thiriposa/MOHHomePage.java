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

public class MOHHomePage extends AppCompatActivity {

    private TextView welcomeText;
    private ImageView leftArrow, rightArrow;
    private ImageView contentImage;
    private TextView contentLabel;
    private int currentIndex = 0;

    private final int[] imageResIds = {
            R.drawable.bmi_cycle,
            R.drawable.thiriposa_delivery
    };

    private final String[] imageLabels = {
            "Clinical BMI Details",
            "Thiriposa Packet Delivery"
    };

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moh_home);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        welcomeText  = findViewById(R.id.welcomeText);
        leftArrow    = findViewById(R.id.leftArrow);
        rightArrow   = findViewById(R.id.rightArrow);
        contentImage = findViewById(R.id.contentImage);
        contentLabel = findViewById(R.id.contentLabel);

        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.arrow_pulse);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        leftArrow.startAnimation(pulse);
        rightArrow.startAnimation(pulse);

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
                            welcomeText.setText("Welcome, MOH Officer");
                        }
                    });
        } else {
            welcomeText.setText("Welcome, MOH Officer");
        }

        updateContentDisplay();

        leftArrow.setOnClickListener(v -> {
            currentIndex = (currentIndex - 1 + imageResIds.length) % imageResIds.length;
            updateContentDisplay();
        });

        rightArrow.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % imageResIds.length;
            updateContentDisplay();
        });

        contentImage.setOnClickListener(v -> {
            if (currentIndex == 0) {
                Intent intent = new Intent(MOHHomePage.this, BmiData.class);
                startActivity(intent);
            } else if (currentIndex == 1) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("packetReports");
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String motherName = snapshot.child("motherName").getValue(String.class);
                        String motherBmi = snapshot.child("motherBmi").getValue(String.class);
                        String babyBmi = snapshot.child("babyBmi").getValue(String.class);
                        String packetCount = snapshot.child("packetCount").getValue(String.class);
                        String measuredMonth = snapshot.child("measuredMonth").getValue(String.class);

                        Intent intent = new Intent(MOHHomePage.this, PacketReport.class);
                        intent.putExtra("motherName", motherName != null ? motherName : "N/A");
                        intent.putExtra("motherBmi", motherBmi != null ? motherBmi : "N/A");
                        intent.putExtra("babyBmi", babyBmi != null ? babyBmi : "N/A");
                        intent.putExtra("packetCount", packetCount != null ? packetCount : "N/A");
                        intent.putExtra("measuredMonth", measuredMonth != null ? measuredMonth : "N/A");
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error handling if needed
                    }
                });
            }
        });
    }

    private void updateContentDisplay() {
        contentImage.setImageResource(imageResIds[currentIndex]);
        contentLabel.setText(imageLabels[currentIndex]);
    }
}
