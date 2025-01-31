package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    TextView mWeatherDataDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mWeatherDataDetail = (TextView) findViewById(R.id.tv_weather_data_detail);

        Intent startIntent = getIntent();
        if (startIntent != null && startIntent.hasExtra(Intent.EXTRA_TEXT)) {
            mWeatherDataDetail.setText(startIntent.getStringExtra(Intent.EXTRA_TEXT));

        }

        // DONE (2) Display the weather forecast that was passed from MainActivity
    }
}