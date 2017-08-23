package com.paricio.weatherapp;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class WeatherListActivityTest {

    @Rule
    public ActivityTestRule<WeatherListActivity> rule  = new ActivityTestRule<>(WeatherListActivity.class);

    @Test
    public void testFragmentIsPresent() {
        WeatherListActivity weatherListActivity = rule.getActivity();
        Fragment fragment = weatherListActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        assertThat(fragment, notNullValue());
        assertThat(fragment, instanceOf(WeatherListFragment.class));
    }
}
