package com.paricio.weatherapp.utils;


import android.content.res.Resources;

import com.paricio.weatherapp.R;

public class WeatherIconConverter {

    public static String fromCodeToIcon(String iconCode, Resources res) {
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
            case '-':
                return res.getString(R.string.wi_refresh);
        }
        return res.getString(R.string.wi_na);
    }
}
