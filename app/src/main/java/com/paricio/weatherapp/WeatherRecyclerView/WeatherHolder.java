package com.paricio.weatherapp.WeatherRecyclerView;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.R;
import com.paricio.weatherapp.WeatherIconTranslator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.list_item_weather_city_name)
    TextView cityNameTextView;
    @BindView(R.id.list_item_weather_time)
    TextClock timeTextView;
    @BindView(R.id.list_item_weather_temperature) TextView temperatureTextView;
    @BindView(R.id.list_item_weather_icon) TextView iconTextView;
    public View view;
    private Typeface weatherFont;

    public WeatherHolder(View itemView, Typeface weatherFont) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        iconTextView.setTypeface(weatherFont);
        view = itemView;
    }

    public void bindWeather(Location location) {
        cityNameTextView.setText(location.getName());
        timeTextView.setTimeZone(location.getTimezone());
        temperatureTextView.setText(location.getTemperature() + " °C");

        Resources res = view.getResources();
        iconTextView.setText(WeatherIconTranslator.translate(location.getIconId(), res));
    }
}

