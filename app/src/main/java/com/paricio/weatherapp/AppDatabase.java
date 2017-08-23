package com.paricio.weatherapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.paricio.weatherapp.Model.Location;

@Database(entities = {Location.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract LocationDAO locationDAO();
}
