package com.s23001792.thiriposa;

public class PacketReportData {

    public String motherName;
    public String motherBmi;
    public String babyBmi;
    public String packetCount;
    public String measuredMonth;

    public PacketReportData() {
        // Default constructor required for Firebase
    }

    public PacketReportData(String motherName, String motherBmi, String babyBmi, String packetCount, String measuredMonth) {
        this.motherName = motherName;
        this.motherBmi = motherBmi;
        this.babyBmi = babyBmi;
        this.packetCount = packetCount;
        this.measuredMonth = measuredMonth;
    }
}
