package com.example.federateddemo.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.federateddemo.room.entities.Vital;

import java.util.Date;
import java.util.List;

public class VitalViewModel extends AndroidViewModel {

    private VitalRepository repository;
    private LiveData<List<Vital>> allVitals;
    private List<Vital> userVitalsWeek;

    public VitalViewModel(@NonNull Application application) {
        super(application);

        repository = new VitalRepository(application);
        allVitals = repository.getAllVitals();
    }


    public void insert(Vital vital) {
        repository.insert(vital);
    }

    public LiveData<List<Vital>> getUserVitalWeek() {
        return repository.getUserVitalsWeek();
    }

    public LiveData<List<Vital>> getUserVitalWeek(String userId) {
        return repository.getUserVitalsWeek(userId);
    }

    public LiveData<List<Vital>> getUserVitalsMonth() {
        return repository.getUserVitalsMonth();
    }

    public LiveData<List<Vital>> getUserVitalsMonth(String userId) {
        return repository.getUserVitalsMonth(userId);
    }


    public LiveData<List<Vital>> getUserVitalsMonth(String userId, Date from, Date to) {
        return repository.getUserVitalsMonth(userId, from, to);
    }

    public LiveData<List<Vital>> getUserVitalsYear() {
        return repository.getUserVitalsYear();
    }

    public LiveData<List<Vital>> getUserVitalsYear(String userId) {
        return repository.getUserVitalsYear(userId);
    }


    public LiveData<List<Vital>> getUserVitalsYear(String userId, Date from, Date to) {
        return repository.getUserVitalsYear(userId, from, to);
    }


    public LiveData<List<Vital>> getAllVitals() {
        return allVitals;
    }

}
