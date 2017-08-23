package com.paricio.weatherapp.Model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpenWeatherOffset {

    List<Weather> weather;
    Main main;

    public String getTemperature() {
        return String.valueOf(main.temp);
    }

    public String getIconId() {
        return String.valueOf(weather.get(0).id);
    }

    static class Weather {
        float id;
    }

    static class Main {
        float temp;
    }
}
