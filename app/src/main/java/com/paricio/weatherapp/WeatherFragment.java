package com.paricio.weatherapp;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paricio.weatherapp.ForecastRecyclerView.ForecastAdapter;
import com.paricio.weatherapp.Model.Forecast;
import com.paricio.weatherapp.Model.ForecastDay;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.RoomDB.AppDatabase;
import com.paricio.weatherapp.Services.ForecastDataDownloader;
import com.paricio.weatherapp.utils.GsonSerializer;
import com.paricio.weatherapp.utils.OpenWeatherForecastOffset;
import com.paricio.weatherapp.utils.WeatherIconConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WeatherFragment extends Fragment {

    private static final String TAG = "WeatherFragment";
    private static final String EXTRA_LOCATION = "com.paricio.weatherapp.location";

    @BindView(R.id.weather_city_name)
    TextView name;
    @BindView(R.id.weather_day)
    TextView day;
    @BindView(R.id.weather_temperature)
    TextView temperature;
    @BindView(R.id.weather_icon)
    TextView icon;
    @BindView(R.id.weather_description)
    TextView description;
    @BindView(R.id.weather_humidity)
    TextView humidity;
    @BindView(R.id.weather_pressure)
    TextView pressure;
    @BindView(R.id.weather_min_temp)
    TextView minTemp;
    @BindView(R.id.weather_max_temp)
    TextView maxTemp;
    @BindView(R.id.forecast_recycler_view)
    RecyclerView forecastRecyclerView;
    private Unbinder unbinder;
    private ForecastAdapter forecastAdapter;
    private Location location;
    private ForecastDataDownloader dataDownloader;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dataDownloader = new ForecastDataDownloader();
        dataDownloader.setDataDownloadListener(new ForecastDataDownloader.DataDownloadListener() {
            @Override
            public void onForecastDataDownloaded(Forecast forecast) {
                forecastAdapter = new ForecastAdapter(forecast,WeatherFragment.this);
                forecastRecyclerView.setAdapter(forecastAdapter);
            }
        });
        Bundle activityBundle = this.getArguments();
        String locationJsonObject = activityBundle.getString(EXTRA_LOCATION);
        Location location = GsonSerializer.deserializeLocationFromJson(locationJsonObject);
        Log.i(TAG, location.getName() + " " + location.getTemperature());
        dataDownloader.newForecastDownload(location,getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        unbinder = ButterKnife.bind(this, view);
        writeTextViews();
        setupUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_weather, menu);
        SharedPreferences settings = getContext().getSharedPreferences(getString(R.string.notification_file_key), Context.MODE_PRIVATE);
        String checkedLocation = settings.getString(getString(R.string.checked_location), null);
        MenuItem item = menu.findItem(R.id.action_check);
        if (checkedLocation !=null && checkedLocation.equals(location.getId())) {
            item.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_check) {
            SharedPreferences settings = getContext().getSharedPreferences(getString(R.string.notification_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            item.setChecked(!item.isChecked());
            if (item.isChecked()) {
                editor.putString(getString(R.string.checked_location), location.getId());
                editor.commit();
                Toast.makeText(getContext(),"Notifications activated on: " + location.getName(),Toast.LENGTH_SHORT).show();
                return true;
            }
            else {
                editor.remove(getString(R.string.checked_location));
                editor.commit();
                Toast.makeText(getContext(),"Notifications disabled on: " + location.getName(),Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void writeTextViews() {
        Typeface weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        Bundle extras = getArguments();
        location = GsonSerializer.deserializeLocationFromJson(extras.getString(EXTRA_LOCATION));
        name.setText(location.getName());
        TimeZone timezone = TimeZone.getTimeZone(location.getTimezone());
        day.setText(OpenWeatherForecastOffset.getDayOfWeek(timezone));
        temperature.setText(location.getTemperature() + " °C");
        icon.setTypeface(weatherFont);
        icon.setText(WeatherIconConverter.fromCodeToIcon(location.getIconId(), getResources()));
        description.setText(location.getDescription());
        humidity.setText(location.getHumidity());
        pressure.setText(location.getPresure());
        minTemp.setText(location.getMinTemp());
        maxTemp.setText(location.getMaxTemp());
    }

    private void setupUI() {
        int orientation;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            orientation = LinearLayoutManager.HORIZONTAL;
        }
        else orientation = LinearLayoutManager.VERTICAL;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),orientation,false);
        forecastRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                forecastRecyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );
        forecastRecyclerView.addItemDecoration(dividerItemDecoration);
        Forecast loadingForecast = new Forecast();
        loadingForecast.setId(UUID.randomUUID().toString());
        loadingForecast.setForecastDays(getLoadingForecastList());
        forecastAdapter = new ForecastAdapter(loadingForecast,WeatherFragment.this);
        forecastRecyclerView.setAdapter(forecastAdapter);
    }

    private List<ForecastDay> getLoadingForecastList() {
        List<ForecastDay> forecastDays = new ArrayList<>();
        TimeZone timezone = TimeZone.getTimeZone(location.getTimezone());
        Calendar calendar = Calendar.getInstance(timezone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i=0; i<5; ++i) {
            ForecastDay forecastDay = new ForecastDay();
            if (i != 0) calendar.add(Calendar.DAY_OF_YEAR, 1);
            String dateText = simpleDateFormat.format(calendar.getTime());
            forecastDay.setDate(dateText);
            forecastDay.setTemperature(getString(R.string.loading));
            forecastDay.setIconId("-1");
            forecastDays.add(forecastDay);
        }
        return forecastDays;
    }
}
