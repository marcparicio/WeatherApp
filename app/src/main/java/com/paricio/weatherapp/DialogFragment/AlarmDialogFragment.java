package com.paricio.weatherapp.DialogFragment;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.paricio.weatherapp.NotificationReceiver;
import com.paricio.weatherapp.R;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.ALARM_SERVICE;


public class AlarmDialogFragment extends DialogFragment {

    private static final String TAG = "AlarmDialogFragment";
    private static final int DEFAULT_HOUR = 10;
    private static final int DEFAULT_MINUTE = 0;

    @BindView(R.id.alarm_timepicker)
    TimePicker timePicker;
    @BindView(R.id.alarm_ok)
    Button okButton;
    @BindView(R.id.alarm_cancel)
    Button cancelButton;
    @BindView(R.id.alarm_disable)
    ImageButton disableButton;

    public AlarmDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_dialog, container, false);
        ButterKnife.bind(this,view);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNewNotificationAlarm();
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        disableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableNotificationAlarm();
                dismiss();
            }
        });
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.notification_file_key),Context.MODE_PRIVATE);
        int currentHour = sharedPreferences.getInt(getString(R.string.notification_hour), DEFAULT_HOUR);
        int currentMinute = sharedPreferences.getInt(getString(R.string.notification_minute), DEFAULT_MINUTE);
        setTimePickerHour(currentHour);
        setTimePickerMinute(currentMinute);
        return view;
    }

    private void setTimePickerHour(int currentHour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(currentHour);
        }
        else {
            timePicker.setCurrentHour(currentHour);
        }
    }

    private void setTimePickerMinute(int currentMinute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setMinute(currentMinute);
        }
        else {
            timePicker.setCurrentMinute(currentMinute);
        }
    }

    private int getTimePickerHour() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getHour();
        }
        else {
            return timePicker.getCurrentHour();
        }
    }

    private int getTimePickerMinute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return timePicker.getMinute();
        }
        else {
            return timePicker.getCurrentMinute();
        }
    }

    private void setNewNotificationAlarm() {
        int hour = getTimePickerHour();
        int minute = getTimePickerMinute();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.notification_file_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.notification_hour), hour);
        editor.putInt(getString(R.string.notification_minute), minute);
        editor.commit();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Log.i(TAG, "Setting alarm at " + hour + " : " + minute);
        Context applicationContext = getContext().getApplicationContext();
        Intent intent = new Intent(applicationContext, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(applicationContext,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)
                applicationContext.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(getContext(),"Alarm set at " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
    }

    private void disableNotificationAlarm() {
        Context applicationContext = getContext().getApplicationContext();
        Intent intent = new Intent(applicationContext, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(applicationContext,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)
                applicationContext.getSystemService(this.getActivity().ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getContext(),"Alarm disabled", Toast.LENGTH_SHORT).show();
    }
}
