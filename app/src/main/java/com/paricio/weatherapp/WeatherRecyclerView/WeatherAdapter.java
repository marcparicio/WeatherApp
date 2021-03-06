package com.paricio.weatherapp.WeatherRecyclerView;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.paricio.weatherapp.ItemTouchHelper.ItemTouchHelperAdapter;
import com.paricio.weatherapp.Model.Location;
import com.paricio.weatherapp.R;
import com.paricio.weatherapp.WeatherListFragment;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> implements ItemTouchHelperAdapter {

    private static final String TAG = "WeatherAdapter";
    private List<Location> locations;
    private WeatherListFragment fragment;
    private Typeface weatherFont;

    public WeatherAdapter(List<Location> locations, WeatherListFragment fragment) {
        this.locations = locations;
        this.fragment = fragment;
        weatherFont = Typeface.createFromAsset(fragment.getActivity().getAssets(), "fonts/weather.ttf");
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(fragment.getActivity());
        View view = layoutInflater.inflate(R.layout.list_item_weather, parent, false);
        return new WeatherHolder(view,weatherFont);
    }

    @Override
    public void onBindViewHolder(final WeatherHolder holder, int position) {
        Location location = locations.get(position);
        holder.bindWeather(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void addItem(Location location) {
        if (locationExists(location)) {
            Toast.makeText(fragment.getActivity(), R.string.location_already_exists, Toast.LENGTH_SHORT).show();
        }
        else {
            locations.add(location);
            notifyItemInserted(getItemCount());
            notifyDataSetChanged();
            fragment.insertLocation(location);
            Toast.makeText(fragment.getActivity(), R.string.location_added, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateLocationsList(List<Location> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    @Override
    public void onItemDismiss(int position) {
        Location location = locations.get(position);
        locations.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        fragment.deleteLocation(location);

    }

    private boolean locationExists (Location newLocation) {
        for (Location location : locations) {
            if (location.getId().equals(newLocation.getId())) return true;
        }
        return false;
    }

    public Location getLocationAtPosition(int position) {
        return locations.get(position);
    }
}

