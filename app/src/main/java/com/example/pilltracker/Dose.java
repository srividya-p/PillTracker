package com.example.pilltracker;

public class Dose {
    private String date;
    private int doseTaken;
    private String medicineId;

    public Dose () { }

    public Dose (String date, int doseTaken, String medicineId) {
        this.date = date;
        this.doseTaken = doseTaken;
        this.medicineId = medicineId;
    }

    public String getDate() {
        return this.date;
    }

    public int getDoseTaken() {
        return this.doseTaken;
    }

    public String getMedicineId() {
        return this.medicineId;
    }
}
