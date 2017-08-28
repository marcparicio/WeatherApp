package com.paricio.weatherapp.Services;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paricio.weatherapp.Model.Forecast;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.utils.OpenWeatherForecastOffset;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastDataDownloader {

    private static final String TAG = "ForecastDataDownloader";
    private DataDownloadListener dataDownloadListener;

    public interface DataDownloadListener {
        void onForecastDataDownloaded(Forecast forecast);
    }

    public void setDataDownloadListener(DataDownloadListener dataDownloadListener) {
        this.dataDownloadListener = dataDownloadListener;
    }

    public void newForecastDownload(Location location, Context context) {
        createForecast(location,context);
    }

    private void createForecast(final Location location, Context context) {
        final OpenWeatherAPIAdapter openWeatherAPIAdapter = new OpenWeatherAPIAdapter();

        Call<JsonObject> call = openWeatherAPIAdapter.getForecastCall(location,context);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                OpenWeatherForecastOffset forecastOffset = gson.fromJson(response.body(),OpenWeatherForecastOffset.class);
                Forecast forecast = forecastOffset.to5DayForecast(location.getId());
                dataDownloadListener.onForecastDataDownloaded(forecast);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}
