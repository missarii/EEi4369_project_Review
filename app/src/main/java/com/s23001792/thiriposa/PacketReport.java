package com.s23001792.thiriposa;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PacketReport extends AppCompatActivity {

    private TableLayout reportTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_report);

        reportTable = findViewById(R.id.reportTable);

        String motherName = getIntent().getStringExtra("motherName");
        String motherBmi = getIntent().getStringExtra("motherBmi");
        String babyBmi = getIntent().getStringExtra("babyBmi");
        String packetCount = getIntent().getStringExtra("packetCount");
        String measuredMonth = getIntent().getStringExtra("measuredMonth");

        addRow("Mother Name", motherName);
        addRow("Mother BMI", motherBmi);
        addRow("Baby BMI", babyBmi);
        addRow("Thiriposa Packet Count", packetCount);
        addRow("Measured Month", measuredMonth);
    }

    private void addRow(String label, String value) {
        TableRow row = new TableRow(this);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setPadding(16, 16, 16, 16);
        labelView.setTextSize(20f);   // increased size
        labelView.setTypeface(null, android.graphics.Typeface.BOLD); // make label bold

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setPadding(16, 16, 16, 16);
        valueView.setTextSize(18f);   // slightly smaller than label

        row.addView(labelView);
        row.addView(valueView);

        reportTable.addView(row);
    }
}
