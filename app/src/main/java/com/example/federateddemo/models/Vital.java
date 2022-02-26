package com.example.federateddemo.models;

import com.google.type.DateTime;

import java.util.Date;

public class Vital {

    private Date date;
    private Double temperature;
    private Double spo2;
    private Double pulse;
    private Boolean cough;


    public Vital(Date date, Double temperature, Double spo2, Double pulse, Boolean cough) {
        this.date = date;
        this.temperature = temperature;
        this.spo2 = spo2;
        this.pulse = pulse;
        this.cough = cough;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getSpo2() {
        return spo2;
    }

    public void setSpo2(Double spo2) {
        this.spo2 = spo2;
    }

    public Double getPulse() {
        return pulse;
    }

    public void setPulse(Double pulse) {
        this.pulse = pulse;
    }

    public Boolean getCough() {
        return cough;
    }

    public void setCough(Boolean cough) {
        this.cough = cough;
    }

    @Override
    public String toString() {
        return "Vital{" +
                "date=" + date +
                ", temperature=" + temperature +
                ", spo2=" + spo2 +
                ", pulse=" + pulse +
                ", cough=" + cough +
                '}';
    }
}
