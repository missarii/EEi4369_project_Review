package com.s23001792.thiriposa;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppointmentReceiveActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private DatabaseReference appointmentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_receive);

        tableLayout = findViewById(R.id.tableLayout);
        appointmentRef = FirebaseDatabase.getInstance().getReference("appointment_send");

        addTableHeader();
        loadAllAppointments();
    }

    // Header row
    private void addTableHeader() {
        TableRow headerRow = new TableRow(this);
        headerRow.setPadding(0, 8, 0, 8);
        headerRow.setBackgroundColor(Color.parseColor("#3F51B5")); // Indigo header

        String[] headers = {"Centre", "Nurse", "Mother", "Baby"};
        float[] columnWeights = {1f, 1.5f, 1f, 1f}; // Nurse slightly wider

        for (int i = 0; i < headers.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(headers[i]);
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setPadding(24, 12, 24, 12);
            tv.setGravity(Gravity.CENTER);

            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, columnWeights[i]);
            tv.setLayoutParams(params);

            headerRow.addView(tv);
        }

        tableLayout.addView(headerRow);
    }

    // Load appointment rows
    private void loadAllAppointments() {
        appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    Toast.makeText(AppointmentReceiveActivity.this,
                            "No appointment requests found.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                int rowIndex = 0;
                float[] columnWeights = {1f, 1.5f, 1f, 1f}; // Same as header

                for (DataSnapshot appointmentSnap : snapshot.getChildren()) {
                    String centre = appointmentSnap.child("centre").getValue(String.class);
                    if (centre == null) centre = "—";

                    String nurseName = appointmentSnap.child("nurseUsername").getValue(String.class);
                    if (nurseName == null || nurseName.trim().isEmpty()) {
                        nurseName = appointmentSnap.child("nurseName").getValue(String.class);
                        if (nurseName == null || nurseName.trim().isEmpty()) {
                            nurseName = appointmentSnap.child("nurseEmail").getValue(String.class);
                            if (nurseName == null || nurseName.trim().isEmpty()) {
                                nurseName = "—";
                            }
                        }
                    }

                    String motherName = appointmentSnap.child("motherName").getValue(String.class);
                    if (motherName == null) motherName = "—";

                    String babyName = appointmentSnap.child("babyName").getValue(String.class);
                    if (babyName == null) babyName = "—";

                    TableRow row = new TableRow(AppointmentReceiveActivity.this);
                    row.setPadding(0, 4, 0, 4);

                    int backgroundColor = (rowIndex % 2 == 0)
                            ? Color.parseColor("#FFFFFF")
                            : Color.parseColor("#F0F0F0");
                    row.setBackgroundColor(backgroundColor);

                    String[] values = {centre, nurseName, motherName, babyName};

                    for (int i = 0; i < values.length; i++) {
                        TextView tv = new TextView(AppointmentReceiveActivity.this);
                        tv.setText(values[i]);
                        tv.setTextColor(Color.BLACK);
                        tv.setPadding(24, 12, 24, 12);
                        tv.setGravity(Gravity.CENTER);

                        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, columnWeights[i]);
                        tv.setLayoutParams(params);

                        row.addView(tv);
                    }

                    tableLayout.addView(row);
                    rowIndex++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AppointmentReceiveActivity.this,
                        "Failed to load appointments: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
