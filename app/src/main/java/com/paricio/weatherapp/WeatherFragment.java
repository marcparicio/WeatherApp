package com.paricio.weatherapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.paricio.weatherapp.Model.Location;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WeatherFragment extends Fragment {

    @BindView(R.id.weather_city_name)
    TextView cityName;
    @BindView(R.id.weather_time)
    TextView time;
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
    @BindView(R.id.weather_recycler_view)
    RecyclerView forecastRecyclerView;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO turn the layout(xml) into the view Fragment for the recycler view; no edittext and more info to show
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        unbinder = ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        forecastRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                forecastRecyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );
        forecastRecyclerView.addItemDecoration(dividerItemDecoration);
        setupUI();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupUI() {
        //TODO setupUI, setText to textviews and create recyclerview adapter...
    }
}
