package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

import java.text.SimpleDateFormat;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DETAIL_LOADER_ID = 2;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_WIND_DEGREES = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;


    private ShareActionProvider mShareActionProvider;

    private ImageView iconView;
    private TextView dayView;
    private TextView dateView;
    private TextView maxView;
    private TextView minView;
    private TextView forecastView;
    private TextView humidityView;
    private TextView windView;
    private TextView pressureView;

    public DetailFragment() {
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        iconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        dayView = (TextView) rootView.findViewById(R.id.detail_day);
        dateView = (TextView) rootView.findViewById(R.id.detail_date);
        maxView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        minView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        forecastView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        humidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        windView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        pressureView  = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details_fragment, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        if (dateView != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v("onLoadFinished", "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        boolean isMetric = Utility.isMetric(getActivity());
        int weatherIcon = Utility.getArtResourceForWeatherCondition(
                        data.getInt(COL_WEATHER_CONDITION_ID));
        String day = Utility.getDayName(getActivity(),
                data.getLong(COL_WEATHER_DATE));
        String dateString = new SimpleDateFormat("MMMM dd").format(
                data.getLong(COL_WEATHER_DATE));
        String weatherDescription =
                data.getString(COL_WEATHER_DESC);
        String high = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        String humidity = getActivity().getString(R.string.format_humidity,
                data.getDouble(COL_WEATHER_HUMIDITY));
        String wind = Utility.getFormattedWind(
                getActivity(),
                data.getFloat(COL_WEATHER_WIND_SPEED),
                data.getFloat(COL_WEATHER_WIND_DEGREES));
        String pressure = getActivity().getString(R.string.format_pressure,
                data.getDouble(COL_WEATHER_PRESSURE));

        iconView.setImageResource(weatherIcon);
        dayView.setText(day);
        dateView.setText(dateString);
        forecastView.setText(weatherDescription);
        maxView.setText(high);
        minView.setText(low);
        humidityView.setText(humidity);
        windView.setText(wind);
        pressureView.setText(pressure);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri dataUri = null;

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            dataUri = intent.getData();
        } else {
            return null;
        }

        return new CursorLoader(getActivity(), dataUri, FORECAST_COLUMNS, null, null, null);
    }


    public Intent createShareForecastIntent() {
        String forecastString = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        forecastString = forecastString + " #SunshineApp";

        Intent mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_STREAM, forecastString);

        return mShareIntent;
    }

}
