package com.s23001792.thiriposa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PacketUpdate extends AppCompatActivity {

    private EditText motherNameInput, motherBmiInput, babyBmiInput, packetCountInput, measuredMonthInput;
    private Button generateReportBtn;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_update);

        motherNameInput = findViewById(R.id.motherNameInput);
        motherBmiInput = findViewById(R.id.motherBmiInput);
        babyBmiInput = findViewById(R.id.babyBmiInput);
        packetCountInput = findViewById(R.id.packetCountInput);
        measuredMonthInput = findViewById(R.id.measuredMonthInput);
        generateReportBtn = findViewById(R.id.generateReportBtn);

        dbRef = FirebaseDatabase.getInstance().getReference("packetReports");

        generateReportBtn.setOnClickListener(v -> {
            String motherName = motherNameInput.getText().toString();
            String motherBmi = motherBmiInput.getText().toString();
            String babyBmi = babyBmiInput.getText().toString();
            String packetCount = packetCountInput.getText().toString();
            String measuredMonth = measuredMonthInput.getText().toString();

            // Save to Firebase
            PacketReportData reportData = new PacketReportData(motherName, motherBmi, babyBmi, packetCount, measuredMonth);
            dbRef.setValue(reportData);

            // Open Report Page
            Intent intent = new Intent(PacketUpdate.this, PacketReport.class);
            intent.putExtra("motherName", motherName);
            intent.putExtra("motherBmi", motherBmi);
            intent.putExtra("babyBmi", babyBmi);
            intent.putExtra("packetCount", packetCount);
            intent.putExtra("measuredMonth", measuredMonth);
            startActivity(intent);
        });
    }
}
