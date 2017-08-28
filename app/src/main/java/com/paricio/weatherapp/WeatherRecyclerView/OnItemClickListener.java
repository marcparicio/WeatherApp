package com.paricio.weatherapp.WeatherRecyclerView;


import android.view.View;

public interface OnItemClickListener {
    void onItemClick(View view, int position);

    void onLongItemClick(View view, int position);
}


