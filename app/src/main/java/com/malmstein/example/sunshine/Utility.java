package com.malmstein.example.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.malmstein.example.sunshine.data.WeatherContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_location),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_units),
                context.getString(R.string.pref_units_default))
                .equals(context.getString(R.string.pref_units_default));
    }

    static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    static String formatDate(String dateString) {
        Date date = null;
        try {
            date = WeatherContract.getDateFromDb(dateString);
        } catch (ParseException e) {
            return "";
        }
        return DateFormat.getDateInstance().format(date);
    }
}