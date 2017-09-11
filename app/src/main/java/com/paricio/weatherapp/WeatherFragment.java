package com.paricio.weatherapp;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.widget.TextClock;
import android.widget.TextView;

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
    private static final String EXTRA_FORECAST = "com.paricio.weatherapp.forecast";
    private static final int COLOR_YELLOW = 0;
    private static final int COLOR_WHITE = 1;



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
    @BindView(R.id.sun)
    View sunView;
    @BindView(R.id.sky)
    View skyView;
    View sceneView;
    private Unbinder unbinder;
    private ForecastAdapter forecastAdapter;
    private Location location;
    private ForecastDataDownloader dataDownloader;
    @BindColor(R.color.blue_sky)
    int blueSkyColor;
    @BindColor(R.color.sunset_sky)
    int sunsetSkyColor;
    @BindColor(R.color.night_sky)
    int nightSkyColor;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        sceneView = view;
        unbinder = ButterKnife.bind(this, view);
        writeTextViews();
        setupUI();

        sceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void startAnimation() {
        int locationTime = getLocationTime();
        int sunColor = getSunColor(locationTime);
        setSunColor(sunColor); //TODO
        float sunYStart = getSunYStartPosition();
        float sunXStart = getSunXStartPosition();
        boolean isGoingUp = checkSunIsGoingUp(locationTime);
        float sunYEnd = getSunYEndPosition(locationTime, isGoingUp);
        float sunXEnd = getSunXEndPosition(locationTime);
        ObjectAnimator widthAnimator = ObjectAnimator
                .ofFloat(sunView, "x", sunXStart, sunXEnd)
                .setDuration(3000);
        widthAnimator.setInterpolator(new AccelerateInterpolator());

        if (isGoingUp) {
            ObjectAnimator heightAnimator = ObjectAnimator
                    .ofFloat(sunView, "y", sunYStart, sunYEnd)
                    .setDuration(3000);
            heightAnimator.setInterpolator(new AccelerateInterpolator());

            heightAnimator.start();
            widthAnimator.start();
        }
        else {
            ObjectAnimator heightAnimator = ObjectAnimator
                    .ofFloat(sunView, "y", sunYStart, getSunYTop())
                    .setDuration(1500);
            heightAnimator.setInterpolator(new AccelerateInterpolator());
            ObjectAnimator heightDownAnimator = ObjectAnimator
                    .ofFloat(sunView, "y", getSunYTop(), sunYEnd)
                    .setDuration(1500);
            widthAnimator.setInterpolator(new AccelerateInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet
                    .play(heightAnimator)
                    .with(widthAnimator);
            animatorSet.start();
        }
    }

    private int getSunYTop() {
        return skyView.getHeight()/2;
    }

    private boolean checkSunIsGoingUp(int time) {
        return time <= 12;
    }

    private float getSunYStartPosition() {
        return skyView.getBottom();
    }

    private float getSunXStartPosition() {
        return skyView.getLeft();
    }

    private float getSunYEndPosition(int time, boolean isGoingUp) {
        float hourValue = getSunYTop()/12;
        if (isGoingUp) return  (skyView.getBottom()+(hourValue*time));
        else return (skyView.getBottom()+(getSunYTop()-(hourValue*time)));
    }

    private float getSunXEndPosition(int time) {
        float hourValue = skyView.getWidth()/24;
        return (skyView.getLeft()+(hourValue*time));
    }

    private int getSunColor(int locationTime) {
        if (locationTime >= 5 && locationTime < 22) return COLOR_YELLOW;
        else return COLOR_WHITE;
    }
    private int getLocationTime() {
        TimeZone timezone = TimeZone.getTimeZone(location.getTimezone());
        Calendar locationCalendar = Calendar.getInstance(timezone);
        return locationCalendar.get(Calendar.HOUR_OF_DAY);
    }

    private void setSunColor(int colorCode) {
        int color;
        if (colorCode == COLOR_WHITE) color = ContextCompat.getColor(getContext(),R.color.bright_sun);
        else color = ContextCompat.getColor(getContext(),R.color.bright_moon);
        Drawable myIcon = ContextCompat.getDrawable(getContext(), R.drawable.sun);
        ColorFilter filter = new LightingColorFilter(color, color);
        myIcon.setColorFilter(filter);
        sunView.setBackground(myIcon);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
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
