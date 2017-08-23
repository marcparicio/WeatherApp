package com.paricio.weatherapp;


import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.solver.SolverVariable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.paricio.weatherapp.ItemTouchHelper.SimpleItemTouchHelperCallback;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.Model.OpenWeatherOffset;
import com.paricio.weatherapp.Services.OpenWeatherAPIAdapter;
import com.paricio.weatherapp.Services.TimeZoneAPIAdapter;
import com.paricio.weatherapp.WeatherRecyclerView.WeatherAdapter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class WeatherListFragment extends Fragment {

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 0;
    private static final String TAG = "WeatherListFragment";
    private static final String SERVICE_TO_ACTIVITY_ACTION = "ServiceToActivityAction";
    private static final String LIST_IS_FULL = "The list of locations if full.";
    private static final String SERVICE_TO_ACTIVITY_KEY = "ServiceToActivityKey";
    private static final String DATA_UPDATED = "Data updated";

    @BindView(R.id.weather_recycler_view) RecyclerView weatherRecyclerView;
    private Unbinder unbinder;
    private WeatherAdapter weatherAdapter;
    private AppDatabase database;
    private ServiceToActivity serviceReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_weather_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupUI();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_weather_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_new_location:
                callPlaceAutocompleteActivityIntent();
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (weatherAdapter.getItemCount() >= 10) Toast.makeText(getActivity(),LIST_IS_FULL,Toast.LENGTH_SHORT);
                else {
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    createLocationFromPlace(place);
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.i(ContentValues.TAG, status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED) {

            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        database = Room.databaseBuilder(context,AppDatabase.class, "weather-database").build();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        getActivity().unregisterReceiver(serviceReceiver);
    }

    private void setupUI() {

        List<Location> locations = null;
        try {
            locations = new GetAllLocationsTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        weatherAdapter = new WeatherAdapter(locations, WeatherListFragment.this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(weatherAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        weatherRecyclerView.setAdapter(weatherAdapter);
        itemTouchHelper.attachToRecyclerView(weatherRecyclerView);

        serviceReceiver = new ServiceToActivity();
        IntentFilter intentSFilter = new IntentFilter(SERVICE_TO_ACTIVITY_ACTION);
        getActivity().registerReceiver(serviceReceiver, intentSFilter);
    }

    private void callPlaceAutocompleteActivityIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }


    public class ServiceToActivity extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle notificationData = intent.getExtras();
            String newData = notificationData.getString(SERVICE_TO_ACTIVITY_KEY);
            if (newData.equals(DATA_UPDATED)) {
                Log.i(TAG, "Correct action reveiced");
                List<Location> locations = null;
                try {
                    locations = new GetAllLocationsTask().execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                weatherAdapter.updateLocationsList(locations);
            }
            Log.i(TAG, "onReceive ServiceToActivity");
        }
    }

    //TODO refactor: database tasks (asynctasks) + api calls
    public class GetAllLocationsTask extends AsyncTask<Void,Void,List<Location>> {

        @Override
        protected List<Location> doInBackground(Void... voids) {
            return database.locationDAO().getAll();
        }

    }

    public class InsertLocationTask extends AsyncTask<Location,Void,Void> {

        @Override
        protected Void doInBackground(Location... locations) {
            database.locationDAO().insertLocation(locations);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }


    public class DeleteLocationTask extends AsyncTask<Location,Void,Void> {

        @Override
        protected Void doInBackground(Location... locations) {
            database.locationDAO().deleteLocation(locations);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }

    private void createLocationFromPlace(final Place locationPlace) {

        TimeZoneAPIAdapter timeZoneAPIAdapter = new TimeZoneAPIAdapter();

        Call<JsonObject> call = timeZoneAPIAdapter.getServiceCall(locationPlace, getContext());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "No Success");
                }

                Gson gson = new Gson();
                String zoneId = gson.fromJson(response.body(),TimeZoneOffset.class).getTimeZoneId();

                LatLng placeCoords = locationPlace.getLatLng();
                String latitude = String.valueOf(placeCoords.latitude);
                String longitude = String.valueOf(placeCoords.longitude);

                Location location = new Location();
                location.setId(locationPlace.getId());
                location.setName(locationPlace.getName().toString());
                location.setTimezone(zoneId);
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                createLocationWithWeather(location);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure on service execution, createLocaiton");
            }
        });

    }

    private void createLocationWithWeather(final Location location) {
        final OpenWeatherAPIAdapter openWeatherAPIAdapter = new OpenWeatherAPIAdapter();

        Call<JsonObject> call = openWeatherAPIAdapter.getServiceCall(location,this.getContext());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "No Success");
                }

                Gson gson = new Gson();
                OpenWeatherOffset openWeatherOffset = gson.fromJson(response.body(),OpenWeatherOffset.class);

                String iconId = openWeatherOffset.getIconId();
                String temperature = openWeatherOffset.getTemperature();

                Location newLocation = new Location();
                newLocation.setId(location.getId());
                newLocation.setName(location.getName());
                newLocation.setLatitude(location.getLatitude());
                newLocation.setLongitude(location.getLongitude());
                newLocation.setTimezone(location.getTimezone());
                newLocation.setTemperature(temperature);
                newLocation.setIconId(iconId);

                Log.i(TAG, newLocation.getName() + " , " + iconId + " , " + temperature);
                weatherAdapter.addItem(newLocation);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure on service execution, updateLocation");
            }
        });
    }



    private class TimeZoneOffset {
        private String timeZoneId;
        private String getTimeZoneId() { return timeZoneId; }
    }

    public void insertLocation(Location location) {
        InsertLocationTask i = new InsertLocationTask();
        i.execute(location);
    }

    public void deleteLocation(Location location) {
        DeleteLocationTask d = new DeleteLocationTask();
        d.execute(location);
    }

}
