package com.paricio.weatherapp.Services;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TimeZoneAPI {

    String BASE_URL = "https://maps.googleapis.com";

    @GET ("/maps/api/timezone/json")
    Call<JsonObject> getTimeZoneId(@Query("location") String location, @Query("timestamp") String timestamp, @Query("key") String key);

}
