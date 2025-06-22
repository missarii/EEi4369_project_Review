package com.s23001792.thiriposa;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText  emailEt;
    private Button    resetBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth    = FirebaseAuth.getInstance();
        emailEt  = findViewById(R.id.emailEditText);
        resetBtn = findViewById(R.id.resetButton);

        resetBtn.setOnClickListener(v -> attemptReset());
    }

    private void attemptReset() {
        String email = emailEt.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this,
                    "Please enter your email", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
