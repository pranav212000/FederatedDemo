package com.example.federateddemo.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Date;
import java.util.List;

public class XAxisValueFormatter extends ValueFormatter {

    List<String> datesList;


    public XAxisValueFormatter() {
    }


    public XAxisValueFormatter(List<String> arrayOfDates) {
        this.datesList = arrayOfDates;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {

        if (value == 0.0f) {
            return "January";
        } else if (value == 1.0f) {
            return "February";
        } else if (value == 2.0f) {
            return "March";
        } else if (value == 3.0f) {
            return "April";
        } else if (value == 4.0f) {
            return "May";
        } else if (value == 5.0f) {
            return "June";
        } else if (value == 6.0f) {
            return "July";
        } else if (value == 7.0f) {
            return "August";
        } else if (value == 8.0f) {
            return "September";
        } else if (value == 9.0f) {
            return "October";
        } else if (value == 10.0f) {
            return "November";
        } else if (value == 11.0f) {
            return "December";
        }

        return "";

    }
}