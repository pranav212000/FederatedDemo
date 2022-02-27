package com.example.federateddemo.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.federateddemo.room.typeconverter.DateTimeConverter;

import java.util.Date;

@Entity(tableName = "vitals", primaryKeys = {"userId", "date"})
public class Vital {

    @NonNull
    private String userId;

    @ColumnInfo(name = "temperature")
    private Double temperature;

    @ColumnInfo(name = "spo2")
    private Double spo2;

    @ColumnInfo(name = "pulse")
    private Double pulse;

    @ColumnInfo(name = "date")
    @TypeConverters({DateTimeConverter.class})
    @NonNull
    private Date date;

    @ColumnInfo(name = "cough")
    private Boolean cough;

    public Vital(String userId, Double temperature, Double spo2, Double pulse, Date date, Boolean cough) {
        this.userId = userId;
        this.temperature = temperature;
        this.spo2 = spo2;
        this.pulse = pulse;
        this.date = date;
        this.cough = cough;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
                "userId='" + userId + '\'' +
                ", temperature=" + temperature +
                ", spo2=" + spo2 +
                ", pulse=" + pulse +
                ", date=" + date +
                ", cough=" + cough +
                '}';
    }
}
