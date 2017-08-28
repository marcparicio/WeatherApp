package com.paricio.weatherapp.ForecastRecyclerView;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.paricio.weatherapp.Model.Forecast;
import com.paricio.weatherapp.Model.ForecastDay;
import com.paricio.weatherapp.R;
import com.paricio.weatherapp.WeatherFragment;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastHolder>  {

    private static final String TAG = "ForecastAdapter";
    private List<ForecastDay> forecastDays;
    private WeatherFragment fragment;
    private Typeface weatherFont;

    public ForecastAdapter(Forecast forecast, WeatherFragment fragment) {
        this.forecastDays = forecast.getForecastDays();
        this.fragment = fragment;
        weatherFont = Typeface.createFromAsset(fragment.getActivity().getAssets(), "fonts/weather.ttf");
    }

    @Override
    public ForecastHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(fragment.getActivity());
        View view = layoutInflater.inflate(R.layout.list_item_forecast, parent, false);
        return new ForecastHolder(view,weatherFont);
    }

    @Override
    public void onBindViewHolder(final ForecastHolder holder, int position) {
        ForecastDay forecastDay = forecastDays.get(position);
        String temperature = forecastDay.getTemperature();
        String iconId = forecastDay.getIconId();
        String date = forecastDay.getDate();
        holder.bindForecast(temperature,iconId,date);
    }

    @Override
    public int getItemCount() {
        return forecastDays.size();
    }
}
