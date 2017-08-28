package com.paricio.weatherapp;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.util.Log;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.RoomDB.AppDatabase;
import com.paricio.weatherapp.RoomDB.LocationDAO;
import com.paricio.weatherapp.Services.WeatherDataDownloader;

import java.util.List;

public class WeatherService extends IntentService{
    private static final String TAG = "WeatherService";
    private static final String SERVICE_TO_ACTIVITY_ACTION = "ServiceToActivityAction";
    private static final String DATA_UPDATED = "Data updated";
    private static final String DATA_NOT_UPDATED = "Data not updated";
    private static final String SERVICE_TO_ACTIVITY_KEY = "ServiceToActivityKey";

    private static AppDatabase database;
    private WeatherDataDownloader dataDownloader;

    public static Intent newIntent(Context context) {
        return new Intent(context, WeatherService.class);
    }

    public WeatherService() {
        super(TAG);
        dataDownloader = new WeatherDataDownloader();
        dataDownloader.setDataDownloadListener(new WeatherDataDownloader.DataDownloadListener() {
            @Override
            public void onWeatherDataDownloaded(Location location) {
                updateLocation(location);
            }
        });
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
                dataDownloader.updateLocationDownload(location,getApplicationContext());
            }
            Log.i(TAG, "sendMessageToActivity");

            sendMessageToActivity(DATA_UPDATED);
        }
        else sendMessageToActivity(DATA_NOT_UPDATED);
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

    public void updateLocation(final Location location) {
        Thread updateThread = new Thread() {
            @Override
            public void run() {
                database.locationDAO().updateLocation(location);
            }
        };
        updateThread.start();
    }
}
