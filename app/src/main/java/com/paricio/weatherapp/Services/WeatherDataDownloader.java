package com.paricio.weatherapp.Services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.utils.OpenWeatherOffset;
import com.paricio.weatherapp.utils.TimeZoneOffset;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataDownloader {
    private static final String TAG = "WeatherDataDownloader";
    private DataDownloadListener dataDownloadListener;

    public interface DataDownloadListener {
        void onWeatherDataDownloaded(Location location);
    }

    public void setDataDownloadListener(DataDownloadListener dataDownloadListener) {
        this.dataDownloadListener = dataDownloadListener;
    }

    public void newLocationDownload(Place place, Context context) {
        createLocationFromPlace(place, context);
    }

    public void updateLocationDownload(Location location, Context context) {
        createWeatherFromLocation(location,context);
    }

    private void createLocationFromPlace(final Place locationPlace, final Context context) {

        TimeZoneAPIAdapter timeZoneAPIAdapter = new TimeZoneAPIAdapter();

        Call<JsonObject> call = timeZoneAPIAdapter.getServiceCall(locationPlace, context);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "No Success");
                }
                Gson gson = new Gson();
                String zoneId = gson.fromJson(response.body(),TimeZoneOffset.class).getTimeZoneId();

                LatLng placeCoords = locationPlace.getLatLng();
                String latitude = String.valueOf(placeCoords.latitude);
                String longitude = String.valueOf(placeCoords.longitude);

                Location location = new Location();
                location.setId(locationPlace.getId());
                location.setName(locationPlace.getName().toString());
                location.setTimezone(zoneId);
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                createWeatherFromLocation(location,context);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure on service execution, createLocaiton");
            }
        });

    }

    private void createWeatherFromLocation(final Location location, Context context) {
        final OpenWeatherAPIAdapter openWeatherAPIAdapter = new OpenWeatherAPIAdapter();

        Call<JsonObject> call = openWeatherAPIAdapter.getCurrentWeatherCall(location,context);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "No Success");
                }

                Gson gson = new Gson();
                OpenWeatherOffset openWeatherOffset = gson.fromJson(response.body(),OpenWeatherOffset.class);

                Location newLocation = new Location();
                newLocation.setId(location.getId());
                newLocation.setName(location.getName());
                newLocation.setLatitude(location.getLatitude());
                newLocation.setLongitude(location.getLongitude());
                newLocation.setTimezone(location.getTimezone());
                newLocation.setTemperature(openWeatherOffset.getTemperature());
                newLocation.setIconId(openWeatherOffset.getIconId());
                newLocation.setDescription(openWeatherOffset.getDescription());
                newLocation.setHumidity(openWeatherOffset.getHumidity());
                newLocation.setPresure(openWeatherOffset.getPressure());
                newLocation.setMinTemp(openWeatherOffset.getTempMin());
                newLocation.setMaxTemp(openWeatherOffset.getTempMax());
                onWeatherDataDownloaded(newLocation);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure on service execution, updateLocation");
            }
        });
    }



    private void onWeatherDataDownloaded(Location location) {
                dataDownloadListener.onWeatherDataDownloaded(location);
    }



}
