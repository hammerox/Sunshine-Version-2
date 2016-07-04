package com.example.android.sunshine.app;

        import android.content.Context;
        import android.database.Cursor;
        import android.support.v4.widget.CursorAdapter;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.example.android.sunshine.app.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final static int VIEW_TYPE_TODAY = 0;
    private final static int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseTodayLayout;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public int getViewTypeCount() {
        if (mUseTodayLayout) {
            return 2;
        } else {
            return 1;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mUseTodayLayout) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }

    }

    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = -1;



        if (getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else {
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        boolean isMetric = Utility.isMetric(context);

        int weatherId;
        if (getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) {
            weatherId = Utility.getArtResourceForWeatherCondition(
                    cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID));
        } else {
            weatherId = Utility.getIconResourceForWeatherCondition(
                    cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID));
        }
        String date = Utility.getFriendlyDayString(
                context,
                cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        String max = Utility.formatTemperature(
                context,
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                isMetric);
        String min = Utility.formatTemperature(
                context,
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP),
                isMetric);

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.iconView.setImageResource(weatherId);
        viewHolder.dateView.setText(date);
        viewHolder.forecastView.setText(forecast);
        viewHolder.maxView.setText(max);
        viewHolder.minView.setText(min);

    }


    public class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView forecastView;
        public final TextView maxView;
        public final TextView minView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            maxView = (TextView) view.findViewById(R.id.list_item_high_textview);
            minView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }


    public boolean useTodayLayout() {
        return mUseTodayLayout;
    }

    public void setUseTodayLayout(boolean mUseTodayLayout) {
        this.mUseTodayLayout = mUseTodayLayout;
    }
}
