/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherProvider;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
//      DONE (21) Implement LoaderManager.LoaderCallbacks<Cursor>

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

//  DONE (18) Create a String array containing the names of the desired data columns from our ContentProvider
//  DONE (19) Create constant int values representing each column name's position above
//  DONE (20) Create a constant int to identify our loader used in DetailActivity
    String[] PROJECTION_DATA = {WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES};

    static final int COLUMN_DATE = 0;
    static final int COLUMN_WEATHER_ID = 1;
    static final int COLUMN_MIN_TEMP = 2;
    static final int COLUMN_MAX_TEMP = 3;
    static final int COLUMN_HUMIDITY = 4;
    static final int COLUMN_PRESSURE = 5;
    static final int COLUMN_WIND_SPEED = 6;
    static final int COLUMN_DEGREES = 7;

    static final int DETAIL_LOADER_ID = 222;




    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

//  DONE (15) Declare a private Uri field called mUri
    private Uri mUri;

//  DONE (10) Remove the mWeatherDisplay TextView declaration

//  DONE (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure
    TextView mTvDate, mTvDescription, mTvHighTemp, mTvLowTemp, mTvHumidity, mTvWind, mTvPressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      DONE (12) Remove mWeatherDisplay TextView
//      DONE (13) Find each of the TextViews by ID
        mTvDate = (TextView) findViewById(R.id.tv_day_date);
        mTvDescription = (TextView) findViewById(R.id.tv_day_desc);
        mTvHighTemp = (TextView) findViewById(R.id.tv_high_temp);
        mTvLowTemp = (TextView) findViewById(R.id.tv_low_temp);
        mTvHumidity = (TextView) findViewById(R.id.tv_day_humidity);
        mTvWind = (TextView) findViewById(R.id.tv_day_wind);
        mTvPressure = (TextView) findViewById(R.id.tv_day_pressure);

//      DONE (14) Remove the code that checks for extra text
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            mUri = intentThatStartedThisActivity.getData();
            if (mUri == null) throw new NullPointerException("mURI must be not null");

        }
//      DONE (16) Use getData to get a reference to the URI passed with this Activity's Intent
//      DONE (17) Throw a NullPointerException if that URI is null
//      DONE (35) Initialize the loader for DetailActivity
        getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

//  DONE (22) Override onCreateLoader
//          DONE (23) If the loader requested is our detail loader, return the appropriate CursorLoader

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        switch (i) {
            case DETAIL_LOADER_ID:
                return new CursorLoader(DetailActivity.this, mUri, PROJECTION_DATA, null, null, null);
            default:
                throw new RuntimeException("Loader cannot be implemented: " + i);
        }
    }


//  DONE (24) Override onLoadFinished
//      DONE (25) Check before doing anything that the Cursor has valid data
//      DONE (26) Display a readable data string
//      DONE (27) Display the weather description (using SunshineWeatherUtils)
//      DONE (28) Display the high temperature
//      DONE (29) Display the low temperature
//      DONE (30) Display the humidity
//      DONE (31) Display the wind speed and direction
//      DONE (32) Display the pressure
//      DONE (33) Store a forecast summary in mForecastSummary

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) return;
        if (!cursor.moveToFirst()) return;
        String date = SunshineDateUtils.getFriendlyDateString(this, cursor.getLong(COLUMN_DATE), false);
        mTvDate.setText(date);

        String description = SunshineWeatherUtils.getStringForWeatherCondition(this, cursor.getInt(COLUMN_WEATHER_ID));
        mTvDescription.setText(description);

        double highTemp = cursor.getDouble(COLUMN_MAX_TEMP);
        double lowTemp = cursor.getDouble(COLUMN_MIN_TEMP);
        mTvHighTemp.setText(SunshineWeatherUtils.formatTemperature(this, highTemp));
        mTvLowTemp.setText(SunshineWeatherUtils.formatTemperature(this, lowTemp));

        mTvHumidity.setText(getString(R.string.format_humidity,cursor.getFloat(COLUMN_HUMIDITY)));
        mTvWind.setText(SunshineWeatherUtils.getFormattedWind(this, cursor.getFloat(COLUMN_WIND_SPEED),cursor.getFloat(COLUMN_DEGREES)));
        mTvPressure.setText(getString(R.string.format_pressure,cursor.getFloat(COLUMN_PRESSURE)));

        mForecastSummary = date + " - " + description + " - " + SunshineWeatherUtils.formatHighLows(this, highTemp, lowTemp);

    }


//  DONE (34) Override onLoaderReset, but don't do anything in it yet


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}