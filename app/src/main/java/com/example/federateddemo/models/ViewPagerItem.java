package com.example.federateddemo.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ViewPagerItem implements Serializable {

    int id;


    ArrayList<Integer> temperature;
    ArrayList<Integer> spo2;
    ArrayList<Integer> pulse;
    ArrayList<Boolean> cough;

    public ViewPagerItem(int id, ArrayList<Integer> temperature, ArrayList<Integer> spo2, ArrayList<Integer> pulse, ArrayList<Boolean> cough) {
        this.id = id;
        this.temperature = temperature;
        this.spo2 = spo2;
        this.pulse = pulse;
        this.cough = cough;
    }

    public ViewPagerItem(ArrayList<Integer> temperature, ArrayList<Integer> spo2, ArrayList<Integer> pulse, ArrayList<Boolean> cough) {
        this.temperature = temperature;
        this.spo2 = spo2;
        this.pulse = pulse;
        this.cough = cough;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Integer> getTemperature() {
        return temperature;
    }

    public void setTemperature(ArrayList<Integer> temperature) {
        this.temperature = temperature;
    }

    public ArrayList<Integer> getSpo2() {
        return spo2;
    }

    public void setSpo2(ArrayList<Integer> spo2) {
        this.spo2 = spo2;
    }

    public ArrayList<Integer> getPulse() {
        return pulse;
    }

    public void setPulse(ArrayList<Integer> pulse) {
        this.pulse = pulse;
    }

    public ArrayList<Boolean> getCough() {
        return cough;
    }

    public void setCough(ArrayList<Boolean> cough) {
        this.cough = cough;
    }
}
