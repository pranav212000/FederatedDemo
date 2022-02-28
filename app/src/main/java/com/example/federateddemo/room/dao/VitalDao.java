package com.example.federateddemo.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.federateddemo.room.entities.Vital;

import java.util.Date;
import java.util.List;

@Dao
public interface VitalDao {
    @Query("SELECT * FROM vitals")
    LiveData<List<Vital>> getAllVitals();

    @Query("SELECT * FROM vitals where userId = (:userId)")
    LiveData<List<Vital>> getUserVitals(String userId);

    @Query("SELECT * FROM vitals where userId = (:userId) AND (date BETWEEN :from and :to)")
    LiveData<List<Vital> >getUserVitalsBetween(String userId, Date from, Date to);

    @Insert
    void insert(Vital vital);

    @Delete
    void delete(Vital vital);
}
