package com.example.android.v3newsappbiddlecom;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //we are using the findPreference method to get the Preference object and we are using the
            //bindPreferenceSummaryToValue so that it can be seen in the Preference Summary
            Preference numberOfStories = findPreference(getString(R.string.settings_number_of_stories_key));
            bindPreferenceSummaryToValue(numberOfStories);

            //we are using the findPreference method to get the Preference object and we are using the
            //bindPreferenceSummaryToValue so that it can be seen in the Preference Summary
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference fromDate = findPreference(getString(R.string.settings_sort_by_from_date_key));
            bindPreferenceSummaryToValue(fromDate);

            Preference toDate = findPreference(getString(R.string.settings_sort_by_to_date_key));
            bindPreferenceSummaryToValue(toDate);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            //the code in this method takes care of updating the displayed preference summary after
            //it has been changed
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String sharedPreferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, sharedPreferenceString);
        }
    }
}