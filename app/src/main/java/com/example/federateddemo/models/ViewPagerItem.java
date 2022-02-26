package com.example.federateddemo.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ViewPagerItem implements Serializable {

    int id;

    ArrayList<Vital> vitals;

    public ViewPagerItem(ArrayList<Vital> vitals) {
        this.vitals = vitals;
    }

    public ViewPagerItem(int id, ArrayList<Vital> vitals) {
        this.id = id;
        this.vitals = vitals;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Vital> getVitals() {
        return vitals;
    }

    public void setVitals(ArrayList<Vital> vitals) {
        this.vitals = vitals;
    }
}
