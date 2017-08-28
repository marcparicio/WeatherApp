package com.paricio.weatherapp.Model;

import java.util.List;

public class Forecast {

    private String id;

    private List<ForecastDay> forecastDays = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ForecastDay> getForecastDays() {
        return forecastDays;
    }

    public void setForecastDays(List<ForecastDay> forecastDays) {
        this.forecastDays = forecastDays;
    }

}
