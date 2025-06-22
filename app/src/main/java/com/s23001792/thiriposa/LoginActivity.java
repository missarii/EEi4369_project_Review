package com.s23001792.thiriposa;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {
    private EditText          emailEt, passwordEt;
    private ImageView         togglePassword;
    private Spinner           typeSpinner;
    private Button            loginBtn;
    private TextView          forgotText, signupText;
    private ProgressBar       progressBar;

    private FirebaseAuth      mAuth;
    private DatabaseReference dbRef;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth & Database
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Bind UI
        emailEt        = findViewById(R.id.emailEditText);
        passwordEt     = findViewById(R.id.passwordEditText);
        togglePassword = findViewById(R.id.togglePassword);
        typeSpinner    = findViewById(R.id.userTypeSpinnerLogin);
        loginBtn       = findViewById(R.id.loginButton);
        forgotText     = findViewById(R.id.forgotPasswordText);
        signupText     = findViewById(R.id.signupText);
        progressBar    = findViewById(R.id.loginProgressBar);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.user_roles,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        // Toggle password visibility
        togglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordEt.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                togglePassword.setImageResource(R.drawable.ic_eye_on);
            } else {
                passwordEt.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                togglePassword.setImageResource(R.drawable.ic_eye_off);
            }
            passwordEt.setSelection(passwordEt.getText().length());
        });

        // Button listeners
        loginBtn.setOnClickListener(v -> attemptLogin());
        forgotText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );
        signupText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class))
        );
    }

    private void attemptLogin() {
        final String email = emailEt.getText().toString().trim();
        final String pass  = passwordEt.getText().toString();
        final String role  = typeSpinner.getSelectedItem().toString();

        // 1. Basic validation
        if (email.isEmpty()) {
            emailEt.requestFocus();
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty()) {
            passwordEt.requestFocus();
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if ("Select Role".equals(role)) {
            typeSpinner.requestFocus();
            Toast.makeText(this, "Please select your role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable UI while authenticating
        loginBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);

                    if (!task.isSuccessful()) {
                        handleAuthError(task.getException());
                        return;
                    }

                    // Auth succeeded → verify role in RTDB
                    String uid = mAuth.getCurrentUser().getUid();
                    dbRef.child(uid)
                            .child("role")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) {
                                        Toast.makeText(LoginActivity.this,
                                                "User data missing. Try signing up again.",
                                                Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                        return;
                                    }
                                    String storedRole = snapshot.getValue(String.class);
                                    if (storedRole == null) {
                                        Toast.makeText(LoginActivity.this,
                                                "Role data corrupted. Contact support.",
                                                Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                        return;
                                    }

                                    if (role.equals(storedRole)) {
                                        goToHomePage(role);
                                    } else {
                                        Toast.makeText(LoginActivity.this,
                                                "Role mismatch! Registered as “" + storedRole + "”",
                                                Toast.LENGTH_LONG).show();
                                        typeSpinner.setSelection(0);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this,
                                            "Database error: " + error.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                });
    }

    private void handleAuthError(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, "Email not registered", Toast.LENGTH_LONG).show();
            emailEt.setText("");
            passwordEt.setText("");
        }
        else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_LONG).show();
            passwordEt.setText("");
        }
        else {
            Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goToHomePage(String role) {
        Intent intent;
        switch (role) {
            case "Mother":
                intent = new Intent(LoginActivity.this, MotherHomePage.class);
                break;
            case "Home Nurse":
                intent = new Intent(LoginActivity.this, NurseHomePage.class);
                break;
            case "MOH Officer":
                intent = new Intent(LoginActivity.this, MOHHomePage.class);
                break;
            default:
                Toast.makeText(this,
                        "Unknown role: " + role, Toast.LENGTH_LONG).show();
                return;
        }
        // Clear form
        emailEt.setText("");
        passwordEt.setText("");
        typeSpinner.setSelection(0);

        // Launch and finish
        startActivity(intent);
        finish();
    }
}
