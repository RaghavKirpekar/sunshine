package com.malmstein.example.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malmstein.example.sunshine.data.WeatherContract;

public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE = 1;
    private static final int VIEW_TYPES = 2;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPES;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        return LayoutInflater.from(context).inflate(viewType == VIEW_TYPE_TODAY ? R.layout.list_item_forecast_today :
                R.layout.list_item_forecast, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(WeatherContract.COL_WEATHER_ID);
        // Use placeholder image for now
        ImageView iconView = (ImageView) view.findViewById(R.id.weather_image);
        iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        String dateString = cursor.getString(WeatherContract.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        TextView dateView = (TextView) view.findViewById(R.id.weather_date);
        dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(WeatherContract.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        TextView descriptionView = (TextView) view.findViewById(R.id.weather_forecast);
        descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(WeatherContract.COL_WEATHER_MAX_TEMP);
        TextView highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
        highTempView.setText(Utility.formatTemperature(high, isMetric));

        // Read low temperature from cursor
        float low = cursor.getFloat(WeatherContract.COL_WEATHER_MIN_TEMP);
        TextView lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        lowTempView.setText(Utility.formatTemperature(low, isMetric));

    }
}