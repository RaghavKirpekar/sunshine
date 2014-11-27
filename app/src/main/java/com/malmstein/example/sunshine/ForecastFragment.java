package com.malmstein.example.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.malmstein.example.sunshine.parser.WeatherDataParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;

public class ForecastFragment extends Fragment {

    public static final String EXTRA_WEATHER = "ff.weather";
    private ArrayAdapter<String> forecastAdapter;
    private ListView weatherList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        forecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview);
        weatherList = (ListView) rootView.findViewById(R.id.listview_forecast);
        weatherList.setAdapter(forecastAdapter);
        weatherList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetail(forecastAdapter.getItem(position));
            }

        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWeather();
    }

    private void showDetail(String weather) {
        Intent weatherIntent = new Intent(getActivity(), DetailActivity.class);
        weatherIntent.putExtra(EXTRA_WEATHER, weather);
        startActivity(weatherIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.fragment_forecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        new FetchWeatherTask().execute(
                preferences.getString(getString(R.string.pref_key_location), getString(R.string.pref_location_default)),
                preferences.getString(getString(R.string.pref_key_units), getString(R.string.pref_units_default)));
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0){
                return null;
            }

            int days = 7;

            // These two need to be declared outside the try/catch so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                String queryParam = "q";
                String formatParam = "mode";
                String unitsParam = "units";
                String daysParam = "cnt";

                Uri built = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(queryParam, params[0])
                        .appendQueryParameter(formatParam, "json")
                        .appendQueryParameter(unitsParam, params[1])
                        .appendQueryParameter(daysParam, Integer.toString(days))
                        .build();

                URL url = new URL(built.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                WeatherDataParser parser = new WeatherDataParser();
                return parser.getWeatherDataFromJson(forecastJsonStr, days);
            } catch (JSONException e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            forecastAdapter.addAll(strings);
        }
    }

}
