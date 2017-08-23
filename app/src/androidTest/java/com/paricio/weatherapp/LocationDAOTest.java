package com.paricio.weatherapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.paricio.weatherapp.Model.Location;

import org.junit.After;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LocationDAOTest {

    private LocationDAO locationDAO;
    private AppDatabase database;

    private String id;
    private String name;
    private String timezone;
    private String latitude;
    private String longitude;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();

        locationDAO = database.locationDAO();

        id = "01234567890";
        name = "Barcelona";
        timezone = "GMT0";
        latitude = "51";
        longitude = "345";
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testDatabaseCreated() {
        assertNotNull(database);
    }

    @Test
    public void testDAOCreated() {
        assertNotNull(locationDAO);
    }

    @Test
    public void testInsertLocation() {
        Location newLocation = new Location();
        newLocation.setId(id);
        newLocation.setName(name);
        newLocation.setTimezone(timezone);
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        locationDAO.insertLocation(newLocation);
        List<Location> locationList = locationDAO.getAll();

        assertEquals(1, locationList.size());
        Location location = locationList.get(0);
        assertEquals(id,location.getId());
        assertEquals(name,location.getName());
        assertEquals(timezone,location.getTimezone());
        assertEquals(latitude,location.getLatitude());
        assertEquals(longitude,location.getLongitude());

    }

    @Test
    public void testDeleteLocation() {
        Location newLocation = new Location();
        newLocation.setId(id);
        newLocation.setName(name);
        newLocation.setTimezone(timezone);
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);
        locationDAO.insertLocation(newLocation);
        List<Location> locationList = locationDAO.getAll();

        assertEquals(1, locationList.size());
        Location location = locationList.get(0);
        locationDAO.deleteLocation(location);
        locationList = locationDAO.getAll();
        assertEquals(0,locationList.size());
    }
}
