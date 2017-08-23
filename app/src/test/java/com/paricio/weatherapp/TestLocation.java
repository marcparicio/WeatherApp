package com.paricio.weatherapp;

import com.paricio.weatherapp.Model.Location;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import static junit.framework.Assert.assertEquals;

public class TestLocation {

    Location location;

    @Before
    public void setupWeather() {
        location = new Location();
    }

    @Test
    public void testLocationId() {
        String randomId = UUID.randomUUID().toString();
        location.setId(randomId);
        assertEquals(randomId,location.getId());
    }

    @Test
    public void testLocationName() {
        location.setName("loc");
        assertEquals("loc",location.getName());
    }

    @Test
    public void testLocationTime() {
        location.setTimezone("0");
        assertEquals("0",location.getTimezone());
    }

    @Test
    public void testLocationCoordinates() {
        location.setLatitude("3.5");
        location.setLongitude("200.003");
        assertEquals("3.5", location.getLatitude());
        assertEquals("200.003", location.getLongitude());
    }


}
