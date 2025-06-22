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

        // 1) Build and add the header row
        addTableHeader();

        // 2) Load all appointment_send entries and add them as colored rows
        loadAllAppointments();
    }

    /**
     * Add a header row to the TableLayout with bolded column titles
     * and a solid background color.
     */
    private void addTableHeader() {
        TableRow headerRow = new TableRow(this);
        headerRow.setPadding(0, 8, 0, 8);
        headerRow.setBackgroundColor(Color.parseColor("#3F51B5")); // Indigo header

        String[] headers = {"Centre", "Nurse", "Mother", "Baby"};
        for (String header : headers) {
            TextView tv = new TextView(this);
            tv.setText(header);
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setPadding(24, 12, 24, 12);
            tv.setGravity(Gravity.CENTER);
            headerRow.addView(tv);
        }

        tableLayout.addView(headerRow);
    }

    /**
     * Query Firebase, iterate all children under "appointment_send", and add each
     * as a new TableRow with alternating background colors.
     */
    private void loadAllAppointments() {
        appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || !snapshot.hasChildren()) {
                    Toast.makeText(
                            AppointmentReceiveActivity.this,
                            "No appointment requests found.",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                int rowIndex = 0;
                for (DataSnapshot appointmentSnap : snapshot.getChildren()) {
                    // 1) Read each field (falling back if null)
                    String centre = appointmentSnap.child("centre").getValue(String.class);
                    if (centre == null) centre = "—";

                    // Try "nurseUsername" first. If absent, fall back to "nurseName", then "nurseEmail".
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

                    // 2) Create a new row for this appointment
                    TableRow row = new TableRow(AppointmentReceiveActivity.this);
                    row.setPadding(0, 4, 0, 4);

                    // Alternate row color: even rows = white, odd rows = light gray
                    int backgroundColor = (rowIndex % 2 == 0)
                            ? Color.parseColor("#FFFFFF")   // white
                            : Color.parseColor("#F0F0F0");  // light gray
                    row.setBackgroundColor(backgroundColor);

                    // Centre cell
                    TextView centreTv = new TextView(AppointmentReceiveActivity.this);
                    centreTv.setText(centre);
                    centreTv.setTextColor(Color.BLACK);
                    centreTv.setPadding(24, 12, 24, 12);
                    centreTv.setGravity(Gravity.CENTER);
                    row.addView(centreTv);

                    // Nurse cell
                    TextView nurseTv = new TextView(AppointmentReceiveActivity.this);
                    nurseTv.setText(nurseName);
                    nurseTv.setTextColor(Color.BLACK);
                    nurseTv.setPadding(24, 12, 24, 12);
                    nurseTv.setGravity(Gravity.CENTER);
                    row.addView(nurseTv);

                    // Mother cell
                    TextView motherTv = new TextView(AppointmentReceiveActivity.this);
                    motherTv.setText(motherName);
                    motherTv.setTextColor(Color.BLACK);
                    motherTv.setPadding(24, 12, 24, 12);
                    motherTv.setGravity(Gravity.CENTER);
                    row.addView(motherTv);

                    // Baby cell
                    TextView babyTv = new TextView(AppointmentReceiveActivity.this);
                    babyTv.setText(babyName);
                    babyTv.setTextColor(Color.BLACK);
                    babyTv.setPadding(24, 12, 24, 12);
                    babyTv.setGravity(Gravity.CENTER);
                    row.addView(babyTv);

                    tableLayout.addView(row);
                    rowIndex++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        AppointmentReceiveActivity.this,
                        "Failed to load appointments: " + error.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
