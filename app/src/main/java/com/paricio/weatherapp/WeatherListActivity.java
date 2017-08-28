package com.paricio.weatherapp;

import android.support.v4.app.Fragment;


public class WeatherListActivity extends BaseActivity {

    protected Fragment createFragment() { return new WeatherListFragment(); }

}
