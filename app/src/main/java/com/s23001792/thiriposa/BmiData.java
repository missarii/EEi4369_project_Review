package com.s23001792.thiriposa;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BmiData extends AppCompatActivity {

    private TableLayout bmiTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_data);

        bmiTable = findViewById(R.id.bmiTable);

        // Hardcoded stable BMI data: categories with entries
        BmiCategory[] categories = new BmiCategory[] {
                new BmiCategory("General BMI by Age", new BmiEntry[] {
                        new BmiEntry("18-24 years", "18.5", "24.9"),
                        new BmiEntry("25-34 years", "19.0", "25.9"),
                        new BmiEntry("35-44 years", "19.5", "26.4"),
                        new BmiEntry("45-54 years", "20.0", "27.4"),
                        new BmiEntry("55-64 years", "20.5", "28.9"),
                        new BmiEntry("65+ years",   "21.0", "29.9")
                }),
                new BmiCategory("Pregnant Mothers BMI Ranges", new BmiEntry[] {
                        new BmiEntry("Underweight", "Below 18.5", "N/A"),
                        new BmiEntry("Normal weight", "18.5", "24.9"),
                        new BmiEntry("Overweight", "25.0", "29.9"),
                        new BmiEntry("Obese", "30.0", "Above")
                }),
                new BmiCategory("Newborn Baby BMI", new BmiEntry[] {
                        new BmiEntry("0-3 months", "13.5", "18.0"),
                        new BmiEntry("3-6 months", "14.0", "19.0"),
                        new BmiEntry("6-12 months", "14.5", "19.5")
                })
        };

        // Populate the table layout with data
        for (BmiCategory category : categories) {
            addCategoryTitle(category.name);
            addTableHeader();
            for (BmiEntry entry : category.entries) {
                addEntryRow(entry);
            }
        }
    }

    private void addCategoryTitle(String title) {
        TableRow titleRow = new TableRow(this);
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(22f); // slightly larger than before
        titleView.setPadding(10, 30, 10, 10);
        titleRow.addView(titleView);
        bmiTable.addView(titleRow);
    }

    private void addTableHeader() {
        TableRow headerRow = new TableRow(this);
        String[] headers = {"Age/Type", "BMI       Min", "BMI Max"};
        for (String header : headers) {
            TextView headerView = new TextView(this);
            headerView.setText(header);
            headerView.setPadding(10, 10, 10, 10);
            headerView.setTextAppearance(android.R.style.TextAppearance_Large); // slightly larger header
            headerRow.addView(headerView);
        }
        bmiTable.addView(headerRow);
    }

    private void addEntryRow(BmiEntry entry) {
        TableRow row = new TableRow(this);

        TextView ageView = new TextView(this);
        ageView.setText(entry.ageRangeOrType);
        ageView.setPadding(10, 10, 10, 10);
        ageView.setTextSize(18f); // increased from default

        TextView minView = new TextView(this);
        minView.setText(entry.bmiMin);
        minView.setPadding(10, 10, 10, 10);
        minView.setTextSize(18f);

        TextView maxView = new TextView(this);
        maxView.setText(entry.bmiMax);
        maxView.setPadding(10, 10, 10, 10);
        maxView.setTextSize(18f);

        row.addView(ageView);
        row.addView(minView);
        row.addView(maxView);

        bmiTable.addView(row);
    }

    // Helper classes for data representation
    static class BmiCategory {
        String name;
        BmiEntry[] entries;
        BmiCategory(String name, BmiEntry[] entries) {
            this.name = name;
            this.entries = entries;
        }
    }

    static class BmiEntry {
        String ageRangeOrType;
        String bmiMin;
        String bmiMax;
        BmiEntry(String ageRangeOrType, String bmiMin, String bmiMax) {
            this.ageRangeOrType = ageRangeOrType;
            this.bmiMin = bmiMin;
            this.bmiMax = bmiMax;
        }
    }
}
