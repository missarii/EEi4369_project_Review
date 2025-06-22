// File: app/src/main/java/com/s23001792/thiriposa/HealthData.java
package com.s23001792.thiriposa;

/**
 * Simple POJO to hold both mother and baby details.
 * Will be used to push/pull from Firebase.
 */
public class HealthData {
    private String motherName;
    private String motherWeight;
    private String motherBMI;
    private String babyName;
    private String babyHeight;
    private String babyWeight;

    // Default constructor required for calls to DataSnapshot.getValue(HealthData.class)
    public HealthData() { }

    public HealthData(String motherName, String motherWeight, String motherBMI,
                      String babyName, String babyHeight, String babyWeight) {
        this.motherName   = motherName;
        this.motherWeight = motherWeight;
        this.motherBMI    = motherBMI;
        this.babyName     = babyName;
        this.babyHeight   = babyHeight;
        this.babyWeight   = babyWeight;
    }

    // Getters and setters for Firebase serialization

    public String getMotherName() {
        return motherName;
    }
    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherWeight() {
        return motherWeight;
    }
    public void setMotherWeight(String motherWeight) {
        this.motherWeight = motherWeight;
    }

    public String getMotherBMI() {
        return motherBMI;
    }
    public void setMotherBMI(String motherBMI) {
        this.motherBMI = motherBMI;
    }

    public String getBabyName() {
        return babyName;
    }
    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public String getBabyHeight() {
        return babyHeight;
    }
    public void setBabyHeight(String babyHeight) {
        this.babyHeight = babyHeight;
    }

    public String getBabyWeight() {
        return babyWeight;
    }
    public void setBabyWeight(String babyWeight) {
        this.babyWeight = babyWeight;
    }
}
