package com.paricio.weatherapp.utils;


import com.google.gson.Gson;
import com.paricio.weatherapp.Model.Location;

public class GsonSerializer {

    public static String serializeLocationToJson(Location location) {
        Gson gson = new Gson();
        String serialized = gson.toJson(location);
        return serialized;
    }

    public static Location deserializeLocationFromJson(String jsonString) {
        Gson gson = new Gson();
        Location location = gson.fromJson(jsonString, Location.class);
        return location;
    }
}
