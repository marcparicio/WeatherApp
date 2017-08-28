package com.paricio.weatherapp.utils;


import com.paricio.weatherapp.Model.Forecast;
import com.paricio.weatherapp.Model.ForecastDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class OpenWeatherForecastOffset {

    private List<ForecastOffset> list;


    public List<ForecastOffset> getList() {
        return list;
    }

    public static class ForecastOffset {
        private List<Weather> weather;
        private Main main;
        private String dt_txt;


        public String getTemperature() {
            return String.valueOf(main.temp);
        }

        public String getIconId() {
            return String.valueOf(weather.get(0).id);
        }

        public String getDtTxt() {
            return dt_txt;
        }

        private static class Weather {
            float id;
        }

        private static class Main {
            float temp;
        }
    }

    public Forecast to5DayForecast(String id) {
        Forecast forecast = new Forecast();
        List<ForecastDay> forecastDays = new ArrayList<>();
        for (ForecastOffset forecastOffset: list) {
            String dateText = forecastOffset.getDtTxt();
            String temperature = forecastOffset.getTemperature();
            String iconId = forecastOffset.getIconId();
            if (correctHourInterval(dateText) && !dayAlreadyInTheList(forecastDays,dateText)) {
                ForecastDay forecastDay = new ForecastDay();
                forecastDay.setDate(dateText);
                forecastDay.setIconId(iconId);
                forecastDay.setTemperature(temperature);
                forecastDays.add(forecastDay);
            }
        }
        forecast.setId(id);
        forecast.setForecastDays(forecastDays);
        return forecast;
    }

    private boolean dayAlreadyInTheList(List<ForecastDay> forecastDays, String dateText) {
        for (ForecastDay forecastDayAdded : forecastDays) {
            String dayAdded = forecastDayAdded.getDate();
            if (getValueOfDate(dateText,Calendar.DAY_OF_MONTH) == (getValueOfDate(dayAdded, Calendar.DAY_OF_MONTH))) return true;
        }
        return false;
    }

    private boolean correctHourInterval(String dateText) {
        int hour = getValueOfDate(dateText,Calendar.HOUR_OF_DAY);
        if (hour > 10 && hour < 20 ) return true;
        else return false;
    }

    private int getValueOfDate(String dateText, int calendarValue) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendarValue);
    }

    public static String getDayOfWeek(String dateText) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
        };
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(date);
    }

    public static String getDayOfWeek(TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(calendar.getTime());
    }

}
