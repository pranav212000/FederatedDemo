package com.example.federateddemo.room.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.federateddemo.Constants;
import com.example.federateddemo.room.dao.VitalDao;
import com.example.federateddemo.room.entities.Vital;
import com.example.federateddemo.room.typeconverter.DateTimeConverter;

import java.util.Calendar;

@Database(entities = {Vital.class}, version = 1, exportSchema = false)
@TypeConverters({DateTimeConverter.class})
public abstract class CoviCareDatabase extends RoomDatabase {

    private static CoviCareDatabase instance;
    //TODO REMOVE CALLBACK
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    public static synchronized CoviCareDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CoviCareDatabase.class, Constants.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;

    }

    public abstract VitalDao vitalDao();

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private VitalDao vitalDao;

        private PopulateDbAsyncTask(CoviCareDatabase db) {
            vitalDao = db.vitalDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d("DATABASE", "doInBackground: CREATING ENTRIES");

            vitalDao.insert(new Vital("abc", 38.0, 97.1, 34.0, Calendar.getInstance().getTime(), false));

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, -12);

            vitalDao.insert(new Vital("abc", 40.0, 90.1, 74.3, cal.getTime(), false));

            cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY, -4);

            vitalDao.insert(new Vital("abc", 32.0, 97.1, 88.1, cal.getTime(), false));
            return null;
        }
    }
}
