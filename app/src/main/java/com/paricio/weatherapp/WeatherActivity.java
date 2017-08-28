package com.paricio.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.utils.GsonSerializer;

public class WeatherActivity extends BaseActivity {

    private static final String EXTRA_LOCATION = "com.paricio.weatherapp.location";

    public static Intent newIntent(Context packageContext, Location location) {
        Intent intent = new Intent(packageContext,WeatherActivity.class);
        intent.putExtra(EXTRA_LOCATION, GsonSerializer.serializeLocationToJson(location));
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        Bundle bundle = getIntent().getExtras();
        Fragment fragment = new WeatherFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}

