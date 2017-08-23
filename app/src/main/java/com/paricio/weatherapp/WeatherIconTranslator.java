package com.paricio.weatherapp;


import android.content.res.Resources;

public class WeatherIconTranslator {

    public static String translate(String iconCode, Resources res) {
        char group = iconCode.charAt(0);

        switch (group) {
            case '2':
                return res.getString(R.string.wi_thunderstorm);
            case '3':
                return res.getString(R.string.wi_rain);
            case '5':
                if (iconCode.charAt(1) == '0') return res.getString(R.string.wi_day_rain);
                else return res.getString(R.string.wi_rain);
            case '6':
                return res.getString(R.string.wi_snow);
            case '7':
                return res.getString(R.string.wi_fog);
            case '8':
                if (iconCode.equals("800")) {
                    return res.getString(R.string.wi_day_sunny);
                }
                else if (iconCode.equals("801")) {
                    return res.getString(R.string.wi_day_cloudy);
                }
                else if (iconCode.equals("802")) {
                    return res.getString(R.string.wi_cloud);
                }
                else {
                    return res.getString(R.string.wi_cloudy);
                }
            case '9':
                return res.getString(R.string.wi_strong_wind);
        }
        return res.getString(R.string.wi_alien);
    }
}
