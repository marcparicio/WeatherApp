package com.paricio.weatherapp;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.Model.OpenWeatherOffset;
import com.paricio.weatherapp.RoomDB.AppDatabase;
import com.paricio.weatherapp.RoomDB.LocationDAO;
import com.paricio.weatherapp.Services.OpenWeatherAPIAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherService extends IntentService{
    private static final String TAG = "WeatherService";
    private static final String SERVICE_TO_ACTIVITY_ACTION = "ServiceToActivityAction";
    private static final String DATA_UPDATED = "Data updated";
    private static final String DATA_NOT_UPDATED = "Data not updated";
    private static final String SERVICE_TO_ACTIVITY_KEY = "ServiceToActivityKey";

    private static AppDatabase database;

    public static Intent newIntent(Context context) {
        return new Intent(context, WeatherService.class);
    }

    public WeatherService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailebleAndConnected()) {
            return;
        }
        Log.i(TAG, "onHandleIntent");
        database = Room.databaseBuilder(this,AppDatabase.class, "weather-database").build();

        LocationDAO locationDAO = database.locationDAO();
        List<Location> locations = locationDAO.getAll();
        if (locations.size() > 0) {
            for (Location location : locations) {
                Log.i(TAG, "onHandleIntent to update " + location.getName() );
                updateLocation(location);
            }
            Log.i(TAG, "sendMessageToActivity");

            sendMessageToActivity(DATA_UPDATED);
        }
        else sendMessageToActivity(DATA_NOT_UPDATED);
    }

    private void updateLocation(final Location location) {
        final OpenWeatherAPIAdapter openWeatherAPIAdapter = new OpenWeatherAPIAdapter();

        Call<JsonObject> call = openWeatherAPIAdapter.getServiceCall(location, getApplicationContext());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "No Success");
                }
                Log.i(TAG, "Updating " + location.getName() );

                Gson gson = new Gson();
                OpenWeatherOffset openWeatherOffset = gson.fromJson(response.body(),OpenWeatherOffset.class);

                String iconId = openWeatherOffset.getIconId();
                String temperature = openWeatherOffset.getTemperature();

                Location updatedLocation = new Location();
                updatedLocation.setId(location.getId());
                updatedLocation.setName(location.getName());
                updatedLocation.setLatitude(location.getLatitude());
                updatedLocation.setLongitude(location.getLongitude());
                updatedLocation.setTimezone(location.getTimezone());
                updatedLocation.setTemperature(temperature);
                updatedLocation.setIconId(iconId);

                UpdateLocationTask updateLocationTask = new UpdateLocationTask();
                updateLocationTask.execute(updatedLocation);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure on service execution, updateLocation");
            }
        });
    }


    private boolean isNetworkAvailebleAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    public static void setServiceAlarm(Context context) {
        Log.i(TAG, "BootBroadcast received, weatherservice alarm set");
        Intent intent = WeatherService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context,0,intent,0);
        database = Room.databaseBuilder(context,AppDatabase.class, "weather-database").build();
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_HALF_HOUR,
                    pendingIntent);

    }

    private void sendMessageToActivity(String newData){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SERVICE_TO_ACTIVITY_ACTION);
        broadcastIntent.putExtra(SERVICE_TO_ACTIVITY_KEY, newData);
        sendBroadcast(broadcastIntent);
    }

    public class UpdateLocationTask extends AsyncTask<Location,Void,Void> {

        @Override
        protected Void doInBackground(Location... locations) {
            database.locationDAO().updateLocation(locations);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }
}
