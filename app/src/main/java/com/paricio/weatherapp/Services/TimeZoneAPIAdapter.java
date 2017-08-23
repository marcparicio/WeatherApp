package com.paricio.weatherapp.Services;


import android.content.Context;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.paricio.weatherapp.R;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimeZoneAPIAdapter {

    public Call<JsonObject> getServiceCall(final Place locationPlace, Context context) {

        final String API_KEY = context.getString(R.string.time_zone_key);
        final Place place = locationPlace;
        LatLng placeCoords = place.getLatLng();
        final String latitude = String.valueOf(placeCoords.latitude);
        final String longitude = String.valueOf(placeCoords.longitude);
        Long tsLong = System.currentTimeMillis() / 1000;
        String timestamp = tsLong.toString();
        String location = latitude + "," + longitude;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TimeZoneAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TimeZoneAPI service = retrofit.create(TimeZoneAPI.class);

        return service.getTimeZoneId(location, timestamp, API_KEY);


    }

}
