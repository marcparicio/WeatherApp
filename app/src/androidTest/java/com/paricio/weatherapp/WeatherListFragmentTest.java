package com.paricio.weatherapp;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class WeatherListFragmentTest {

    @Rule
    public ActivityTestRule<TestActivity> rule = new ActivityTestRule<>(TestActivity.class);

    private TestActivity testActivity;
    private WeatherListFragment fragment;

    @Before
    public void setUp() throws Exception {
        testActivity = rule.getActivity();

        View container = testActivity.findViewById(R.id.fragment_container);

        fragment = new WeatherListFragment();
        testActivity.getSupportFragmentManager().beginTransaction().add(container.getId(),fragment).commitAllowingStateLoss();

        getInstrumentation().waitForIdleSync();

    }

    @After
    public void tearDown() throws Exception {
        testActivity = null;
    }

    @Test
    public void testLaunch() {
        View container = testActivity.findViewById(R.id.fragment_container);
        assertNotNull(container);

        View view = fragment.getView().findViewById(R.id.weather_recycler_view);
        assertNotNull(view);
    }

    @Test
    public void testAddLocation() {

    }


    @Test
    public void createLocation() throws Exception {

    }

}