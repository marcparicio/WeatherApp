package com.paricio.weatherapp;


import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
import com.paricio.weatherapp.DialogFragment.AboutDialogFragment;
import com.paricio.weatherapp.ItemTouchHelper.SimpleItemTouchHelperCallback;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.Services.WeatherDataDownloader;
import com.paricio.weatherapp.RoomDB.AppDatabase;
import com.paricio.weatherapp.WeatherRecyclerView.OnItemClickListener;
import com.paricio.weatherapp.WeatherRecyclerView.WeatherAdapter;
import com.paricio.weatherapp.WeatherRecyclerView.WeatherItemClickListener;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    private WeatherDataDownloader dataDownloader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataDownloader = new WeatherDataDownloader();
        dataDownloader.setDataDownloadListener(new WeatherDataDownloader.DataDownloadListener() {
            @Override
            public void onWeatherDataDownloaded(Location location) {
                weatherAdapter.addItem(location);
            }
        });
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_weather_list, container, false);
        unbinder = ButterKnife.bind(this, view);
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
                return true;
            case R.id.menu_item_alarm_time:
                alarmNotificationSettings();
                return true;
            case R.id.menu_item_about:
                showAboutDialog();
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
                    dataDownloader.newLocationDownload(place,getContext());
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.i(ContentValues.TAG, status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED) {
                Log.i(TAG, "PlaceAutocomplete canceled");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Background thread stopped");
    }

    private void setupUI() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        weatherRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                weatherRecyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );
        weatherRecyclerView.addItemDecoration(dividerItemDecoration);
        weatherRecyclerView.addOnItemTouchListener(
                new WeatherItemClickListener(getActivity(), weatherRecyclerView ,new OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Location locationClicked = weatherAdapter.getLocationAtPosition(position);
                        Intent intent = WeatherActivity.newIntent(getActivity(),locationClicked);
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(getActivity(), "onLongClick", Toast.LENGTH_SHORT).show();
                    }
                })
        );

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

    private void alarmNotificationSettings() {

    }

    private void showAboutDialog() {
        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
        aboutDialogFragment.show(getFragmentManager(),"dialog");
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

    public class GetAllLocationsTask extends AsyncTask<Void,Void,List<Location>> {

        @Override
        protected List<Location> doInBackground(Void... voids) {
            return database.locationDAO().getAll();
        }

    }

    public void insertLocation(final Location location) {
        Thread insertThread = new Thread() {
            @Override
            public void run() {
                database.locationDAO().insertLocation(location);
            }
        };
        insertThread.start();
    }

    public void deleteLocation(final Location location) {
        Thread deleteThread = new Thread() {
            @Override
            public void run() {
                database.locationDAO().deleteLocation(location);
            }
        };
        deleteThread.start();
    }
}
