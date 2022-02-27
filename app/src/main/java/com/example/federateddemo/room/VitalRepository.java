package com.example.federateddemo.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.federateddemo.room.dao.VitalDao;
import com.example.federateddemo.room.database.CoviCareDatabase;
import com.example.federateddemo.room.entities.Vital;

import java.util.List;

public class VitalRepository {

    private VitalDao vitalDao;

    private LiveData<List<Vital>> allVitals;

    public VitalRepository(Application application) {
        CoviCareDatabase coviCareDatabase = CoviCareDatabase.getInstance(application);
        vitalDao = coviCareDatabase.vitalDao();
        allVitals = vitalDao.getAllVitals();
    }

    public void insert(Vital vital) {

        new InsertVitalAsyncTask(vitalDao).execute(vital);
    }

    public void delete(Vital vital) {

    }

    public LiveData<List<Vital>> getAllVitals() {
        return allVitals;
    }


    private static class InsertVitalAsyncTask extends AsyncTask<Vital, Void, Void> {

        private final VitalDao vitalDao;

        public InsertVitalAsyncTask(VitalDao vitalDao) {
            this.vitalDao = vitalDao;
        }

        @Override
        protected Void doInBackground(Vital... vitals) {
            vitalDao.insert(vitals[0]);
            return null;
        }
    }


}
