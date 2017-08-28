package com.paricio.weatherapp.utils;


import java.util.List;

public class OpenWeatherOffset {

    private List<Weather> weather;
    private Main main;

    public String getTemperature() {
        return String.valueOf(main.temp);
    }

    public String getIconId() {
        return String.valueOf(weather.get(0).id);
    }

    public String getDescription() {
        return weather.get(0).description;
    }

    public String getHumidity() {
        return String.valueOf(main.humidity);
    }

    public String getPressure() {
        return String.valueOf(main.pressure);
    }

    public String getTempMin() {
        return String.valueOf(main.temp_min);
    }

    public String getTempMax() {
        return String.valueOf(main.temp_max);
    }

    private static class Weather {
        float id;
        String description;
    }

    private static class Main {
        float temp;
        float humidity;
        float pressure;
        float temp_min;
        float temp_max;
    }
}
