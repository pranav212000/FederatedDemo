package com.example.federateddemo.room;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.federateddemo.Constants;
import com.example.federateddemo.room.dao.VitalDao;
import com.example.federateddemo.room.database.CoviCareDatabase;
import com.example.federateddemo.room.entities.Vital;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VitalRepository {

    private VitalDao vitalDao;

    private LiveData<List<Vital>> allVitals;
    private LiveData<List<Vital>> userVitalsWeek;
    private LiveData<List<Vital>> userVitalsMonth;
    private LiveData<List<Vital>> userVitalsYear;


    public VitalRepository(Application application) {
        CoviCareDatabase coviCareDatabase = CoviCareDatabase.getInstance(application);
        vitalDao = coviCareDatabase.vitalDao();
        allVitals = vitalDao.getAllVitals();

        SharedPreferences preferences = application.getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
        String userId = preferences.getString(Constants.USER_ID, "abc");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        userVitalsWeek = getUserVitalsWeek(userId);
        userVitalsMonth = getUserVitalsMonth(userId);
        userVitalsYear = getUserVitalsYear(userId);
    }

    public void insert(Vital vital) {

        new InsertVitalAsyncTask(vitalDao).execute(vital);
    }

    public void delete(Vital vital) {

    }

    public LiveData<List<Vital>> getAllVitals() {
        return allVitals;
    }

    public LiveData<List<Vital>> getUserVitalsWeek() {
        return userVitalsWeek;
    }

    public LiveData<List<Vital>> getUserVitalsWeek(String userId) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        userVitalsWeek = vitalDao.getUserVitalsBetween(userId, calendar.getTime(), Calendar.getInstance().getTime());
        return userVitalsWeek;
    }

    public LiveData<List<Vital>> getUserVitalsWeek(String userId, Date from, Date to) {
        userVitalsWeek = vitalDao.getUserVitalsBetween(userId, from, to);
        return userVitalsWeek;
    }

    public LiveData<List<Vital>> getUserVitalsMonth() {
        return userVitalsMonth;
    }

    public LiveData<List<Vital>> getUserVitalsMonth(String userId) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -30);
        userVitalsMonth = vitalDao.getUserVitalsBetween(userId, calendar.getTime(), Calendar.getInstance().getTime());
        return userVitalsMonth;
    }

    public LiveData<List<Vital>> getUserVitalsMonth(String userId, Date from, Date to) {
        userVitalsMonth = vitalDao.getUserVitalsBetween(userId, from, to);
        return userVitalsMonth;
    }

    public LiveData<List<Vital>> getUserVitalsYear() {
        return userVitalsYear;
    }

    public LiveData<List<Vital>> getUserVitalsYear(String userId) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -12);
        userVitalsYear = vitalDao.getUserVitalsBetween(userId, calendar.getTime(), Calendar.getInstance().getTime());
        return userVitalsYear;
    }

    public LiveData<List<Vital>> getUserVitalsYear(String userId, Date from, Date to) {
        userVitalsYear = vitalDao.getUserVitalsBetween(userId, from, to);
        return userVitalsYear;
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
