package com.example.federateddemo.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.federateddemo.room.entities.Vital;

import java.util.List;

public class VitalViewModel extends AndroidViewModel {

    private VitalRepository repository;
    private LiveData<List<Vital>> allVitals;

    public VitalViewModel(@NonNull Application application) {
        super(application);

        repository = new VitalRepository(application);
        allVitals = repository.getAllVitals();
    }


    public void insert(Vital vital) {
        repository.insert(vital);
    }

    public LiveData<List<Vital>> getAllVitals () {
        return allVitals;
    }

}
