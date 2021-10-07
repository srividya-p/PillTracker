package com.example.pilltracker;

import java.util.List;
import java.util.Map;

public class MedicineDosage {

    private String uid;
    private String name;
    private String desc;
    private boolean isGeneric;
    private String expDate;
    private String startDate;
    private String endDate;
    private List<Integer> timings;
    private int totalDosage;
    private List<Integer> dosesTaken;

    public MedicineDosage() { }

    public MedicineDosage(String uid, String name, String desc, boolean isGeneric, String expDate, String startDate, String endDate, List<Integer> timings, int totalDosage, List<Integer> dosesTaken) {
        this.uid = uid;
        this.name = name;
        this.desc = desc;
        this.isGeneric = isGeneric;
        this.expDate = expDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timings = timings;
        this.totalDosage = totalDosage;
        this.dosesTaken = dosesTaken;
    }

    public String getUid() { return this.uid; }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean getIsGeneric() {
        return this.isGeneric;
    }

    public String getExpDate() {
        return this.expDate;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public List<Integer> getTimings() {
        return this.timings;
    }

    public int getTotalDosage() {
        return this.totalDosage;
    }

    public List<Integer> getDosesTaken() { return this.dosesTaken; }
}
