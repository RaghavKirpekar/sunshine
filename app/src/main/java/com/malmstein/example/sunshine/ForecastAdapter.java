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
        View view = LayoutInflater.from(context).inflate(viewType == VIEW_TYPE_TODAY ? R.layout.list_item_forecast_today :
                R.layout.list_item_forecast, parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        int weatherId = cursor.getInt(WeatherContract.COL_WEATHER_ID);
        holder.iconView.setImageResource(R.drawable.ic_launcher);

        String dateString = cursor.getString(WeatherContract.COL_WEATHER_DATE);
        holder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        String description = cursor.getString(WeatherContract.COL_WEATHER_DESC);
        holder.descriptionView.setText(description);

        boolean isMetric = Utility.isMetric(context);

        float high = cursor.getFloat(WeatherContract.COL_WEATHER_MAX_TEMP);
        holder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        float low = cursor.getFloat(WeatherContract.COL_WEATHER_MIN_TEMP);
        holder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.weather_image);
            dateView = (TextView) view.findViewById(R.id.weather_date);
            descriptionView = (TextView) view.findViewById(R.id.weather_forecast);
            highTempView = (TextView) view.findViewById(R.id.weather_high_temp);
            lowTempView = (TextView) view.findViewById(R.id.weather_low_temp);
        }
    }
}