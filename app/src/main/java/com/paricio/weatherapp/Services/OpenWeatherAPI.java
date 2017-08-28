package com.paricio.weatherapp.Services;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherAPI {
    String BASE_URL = "https://api.openweathermap.org";

    @GET("/data/2.5/weather")
    Call<JsonObject> getLocation(@Query("lat") String lat, @Query("lon") String lon,
                                   @Query("units") String units, @Query("appid") String appid);

    @GET("/data/2.5/forecast")
    Call<JsonObject> getForecast(@Query("lat") String lat, @Query("lon") String lon,
                                    @Query("units") String units, @Query("appid") String appid);

}
