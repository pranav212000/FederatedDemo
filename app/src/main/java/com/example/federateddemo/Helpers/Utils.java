package com.example.federateddemo.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static long getDateInMilliSeconds(String givenDateString, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        long timeInMilliseconds = 1;
        try {
            Date mDate = sdf.parse(givenDateString);
            assert mDate != null;
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }
}
