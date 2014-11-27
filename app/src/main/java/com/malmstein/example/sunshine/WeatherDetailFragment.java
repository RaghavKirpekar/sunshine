package com.malmstein.example.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WeatherDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView weatherText = (TextView) rootView.findViewById(R.id.weather_text);
        weatherText.setText(getActivity().getIntent().getStringExtra(ForecastFragment.EXTRA_WEATHER));

        return rootView;
    }

}
