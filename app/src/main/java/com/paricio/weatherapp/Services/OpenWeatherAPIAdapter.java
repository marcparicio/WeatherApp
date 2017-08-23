package com.paricio.weatherapp.Services;



import android.content.Context;

import com.google.gson.JsonObject;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.R;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenWeatherAPIAdapter {

    public Call<JsonObject> getServiceCall(final Location location, Context context) {

        final String latitude = location.getLatitude();
        final String longitude = location.getLongitude();
        final String API_KEY = context.getString(R.string.openweather_key);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OpenWeatherAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeatherAPI service = retrofit.create(OpenWeatherAPI.class);

        return service.getLocation(latitude, longitude, "metric",  API_KEY);


    }

}
