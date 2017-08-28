package com.paricio.weatherapp.ForecastRecyclerView;


import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.paricio.weatherapp.R;
import com.paricio.weatherapp.utils.OpenWeatherForecastOffset;
import com.paricio.weatherapp.utils.WeatherIconConverter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForecastHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.list_item_forecast_temperature)
    TextView temperatureTextView;
    @BindView(R.id.list_item_forecast_icon)
    TextView iconTextView;
    @BindView(R.id.list_item_forecast_date)
    TextView dateTextView;
    public View view;

    public ForecastHolder(View itemView, Typeface weatherFont) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        iconTextView.setTypeface(weatherFont);
        view = itemView;
    }

    public void bindForecast(String temperature, String iconId, String date) {
        temperatureTextView.setText(temperature + " °C");
        dateTextView.setText(OpenWeatherForecastOffset.getDayOfWeek(date));
        Resources res = view.getResources();
        iconTextView.setText(WeatherIconConverter.fromCodeToIcon(iconId, res));
    }

}
