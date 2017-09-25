package com.paricio.weatherapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.RoomDB.AppDatabase;
import com.paricio.weatherapp.RoomDB.LocationDAO;
import com.paricio.weatherapp.utils.WeatherIconConverter;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static java.security.AccessController.getContext;


public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    private AtomicInteger atomicInteger = new AtomicInteger();
    private int MID = atomicInteger.incrementAndGet();

    private AppDatabase database;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Notification Alarm Received");
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, WeatherListActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.notification_file_key), Context.MODE_PRIVATE);
        String checkedLocation = settings.getString(context.getString(R.string.checked_location), null);

        database = Room.databaseBuilder(context,AppDatabase.class, "weather-database").build();
        Location location = null;
        try {
            location = new GetLocationTask().execute(checkedLocation).get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.i(TAG, "Popping up notification...");

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.weather_icon_launcher)
                .setSound(alarmSound)
                .setContentTitle("Current weather in " + location.getName() + ":")
                .setContentText(location.getTemperature() + " °C,  " + location.getDescription() + "  -  Tap for more info")
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent);
        Log.i(TAG, "Notification sent");
        notificationManager.notify(MID, notifyBuilder.build());
        MID = atomicInteger.incrementAndGet();
    }

    public class GetLocationTask extends AsyncTask<String,Void,Location> {

        @Override
        protected Location doInBackground(String... strings) {
            return database.locationDAO().getLocationById(strings[0]);
        }
    }

}
