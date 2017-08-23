package com.paricio.weatherapp;

import android.support.v4.app.Fragment;

public class WeatherActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return new WeatherFragment();
    }

}

