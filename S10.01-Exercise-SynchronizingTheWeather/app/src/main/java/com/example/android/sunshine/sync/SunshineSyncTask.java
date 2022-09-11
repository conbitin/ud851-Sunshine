//  DONE (1) Create a class called SunshineSyncTask
//  DONE (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
//      TODO (3) Within syncWeather, fetch new weather data
//      TODO (4) If we have valid results, delete the old data and insert the new

package com.example.android.sunshine.sync;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.data.WeatherProvider;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import org.json.JSONException;

import java.io.IOException;

public class SunshineSyncTask {
    synchronized public static void syncWeather(Context context) {
        String jsonResult = null;
        try {
            jsonResult = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.getUrl(context));
            ContentValues[] contentValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, jsonResult);

            if (contentValues != null && contentValues.length > 0) {
                context.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null);
                context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, contentValues);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

    }
}