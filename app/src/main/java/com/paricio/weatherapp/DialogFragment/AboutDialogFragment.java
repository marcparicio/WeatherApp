package com.paricio.weatherapp.DialogFragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.paricio.weatherapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutDialogFragment extends DialogFragment {

    @BindView(R.id.about_button)
    Button okButton;


    public AboutDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_dialog, container, false);
        ButterKnife.bind(this,view);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }
}
