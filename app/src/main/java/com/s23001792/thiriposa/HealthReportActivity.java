// File: app/src/main/java/com/s23001792/thiriposa/HealthReportActivity.java
package com.s23001792.thiriposa;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;             // already present
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;           // already present
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HealthReportActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_report);

        tableLayout = findViewById(R.id.tableLayout);
        // Reference the same “healthUpdates/latest” node we wrote to
        dbRef = FirebaseDatabase.getInstance()
                .getReference("healthUpdates")
                .child("latest");

        // Listen for data once and populate the table
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HealthData data = snapshot.getValue(HealthData.class);
                if (data == null) {
                    Toast.makeText(HealthReportActivity.this,
                            "No health data available.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Clear any existing rows
                tableLayout.removeAllViews();

                // Add a header row (optional)
                TableRow headerRow = new TableRow(HealthReportActivity.this);
                TextView header1 = new TextView(HealthReportActivity.this);
                header1.setText("Field");
                header1.setTextAppearance(android.R.style.TextAppearance_Medium);
                header1.setPadding(8, 8, 8, 8);
                TextView header2 = new TextView(HealthReportActivity.this);
                header2.setText("Value");
                header2.setTextAppearance(android.R.style.TextAppearance_Medium);
                header2.setPadding(8, 8, 8, 8);
                headerRow.addView(header1);
                headerRow.addView(header2);
                tableLayout.addView(headerRow);

                // Add a simple divider (optional)
                // Changed from "View divider" to "TextView divider" so we don't need to import android.view.View
                TextView divider = new TextView(HealthReportActivity.this);
                divider.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 2));
                divider.setBackgroundColor(0xFFCCCCCC);
                tableLayout.addView(divider);

                // Create rows for each piece of data
                addRow("Mother Name", data.getMotherName());
                addRow("Mother Weight", data.getMotherWeight());
                addRow("Mother BMI", data.getMotherBMI());
                addRow("Baby Name", data.getBabyName());
                addRow("Baby Height", data.getBabyHeight());
                addRow("Baby Weight", data.getBabyWeight());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HealthReportActivity.this,
                        "Failed to load data: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Helper to create and add a row with two TextViews (field name + value)
     */
    private void addRow(String fieldName, String value) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(lp);

        TextView tvField = new TextView(this);
        tvField.setText(fieldName);
        tvField.setPadding(8, 16, 16, 16);
        tvField.setGravity(Gravity.START);

        TextView tvValue = new TextView(this);
        tvValue.setText(value);
        tvValue.setPadding(8, 16, 16, 16);
        tvValue.setGravity(Gravity.START);

        row.addView(tvField);
        row.addView(tvValue);
        tableLayout.addView(row);
    }
}
