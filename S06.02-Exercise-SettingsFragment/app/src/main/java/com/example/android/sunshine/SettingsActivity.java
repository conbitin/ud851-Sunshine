/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.view.MenuItem;

/**
 * Loads the SettingsFragment and handles the proper behavior of the up button.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // DONE (2) Create an xml resource directory
        // DONE (3) Add a PreferenceScreen with an EditTextPreference and ListPreference within the newly created xml resource directory

        // DONE (4) Create SettingsFragment and extend PreferenceFragmentCompat

        // Do steps 5 - 11 within SettingsFragment
        // DONE (10) Implement OnSharedPreferenceChangeListener from SettingsFragment

        // DONE (8) Create a method called setPreferenceSummary that accepts a Preference and an Object and sets the summary of the preference

        // DONE (5) Override onCreatePreferences and add the preference xml file using addPreferencesFromResource

        // Do step 9 within onCreatePreference
        // DONE (9) Set the preference summary on each preference that isn't a CheckBoxPreference

        // DONE (13) Unregister SettingsFragment (this) as a SharedPreferenceChangedListener in onStop

        // DONE (12) Register SettingsFragment (this) as a SharedPreferenceChangedListener in onStart

        // DONE (11) Override onSharedPreferenceChanged to update non CheckBoxPreferences when they are changed
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onStart() {
            super.onStart();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.settings_preference);
            PreferenceScreen pref = getPreferenceScreen();
            int count = pref.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference preference = pref.getPreference(i);
                if (preference instanceof CheckBoxPreference == false) {
                    setPreferenceSummary(preference, pref.getSharedPreferences().getString(preference.getKey(), ""));
                }
            }

        }

        @Override
        public void onStop() {
            super.onStop();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        public void setPreferenceSummary(Preference pref, Object object) {
            String value = object.toString();
            if (pref instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) pref;
                int index = listPreference.findIndexOfValue(value);
                if (index != -1) {
                    String entryValue = listPreference.getEntryValues()[index].toString();
                    pref.setSummary(entryValue);
                }
            } else {
                pref.setSummary(value);
            }

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = getPreferenceScreen().findPreference(key);
            if (pref instanceof CheckBoxPreference == false)
                pref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}