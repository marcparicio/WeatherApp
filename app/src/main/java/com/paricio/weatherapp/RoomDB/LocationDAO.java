package com.paricio.weatherapp.RoomDB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.paricio.weatherapp.Model.Location;

import java.util.List;


@Dao
public interface LocationDAO {

    @Query("Select * FROM location")
    List<Location> getAll();

    @Query("Select * FROM location WHERE id LIKE :locationId")
    Location getLocationById(String locationId);

    @Insert
    void insertLocation(Location... locations);

    @Delete
    void deleteLocation(Location... locations);

    @Update
    void updateLocation(Location... locations);
}
