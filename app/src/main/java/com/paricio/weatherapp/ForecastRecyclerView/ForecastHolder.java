package com.paricio.weatherapp.ForecastRecyclerView;


import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
        Resources res = view.getResources();
        if (temperature.equals(res.getString(R.string.loading))) temperatureTextView.setText(res.getString(R.string.loading));
        else temperatureTextView.setText(temperature + " °C");
        dateTextView.setText(OpenWeatherForecastOffset.getDayOfWeek(date));
        String icon = WeatherIconConverter.fromCodeToIcon(iconId, res);
        iconTextView.setText(icon);

        if (icon.equals(res.getString(R.string.wi_refresh))) {
            Animation rotation = AnimationUtils.loadAnimation(view.getContext(), R.anim.rotate);
            rotation.setFillAfter(true);
            iconTextView.startAnimation(rotation);
        }
    }

}
