package com.alderferstudios.percentcalculatorv2.widget;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.alderferstudios.percentcalculatorv2.R;

/**
 * Percent Calculator
 * The pop up for choosing percent limits
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class PercentLimitPopUp extends PopUpPreference {

    protected static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref_pop_up);

        editor = shared.edit();

        getFragmentManager().beginTransaction()
                .replace(R.id.prefFrame, new PopUpFragment()).commit();
    }

    /**
     * The pop up fragment
     */
    public static class PopUpFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.percent_limit_pref_pop_up, false);
            addPreferencesFromResource(R.xml.percent_limit_pref_pop_up);

            setSummaries();
            shared.registerOnSharedPreferenceChangeListener(this);
        }

        /**
         * Sets the summaries on start
         */
        protected void setSummaries() {
            setPercentStartSummary();                                                             //starting percent
            setPercentMaxSummary();                                                               //max percent
        }

        /**
         * Sets the summaries on change
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "percentStart": setPercentStartSummary(); break;                             //starting percent
                case "percentMax": setPercentMaxSummary(); break;                                 //max percent
            }
        }

        /**
         * Sets the summary for the starting percent
         * String needs to be acquired differently since its being added
         */
        protected void setPercentStartSummary() {
            if (isAdded()) {                                                                       //must check if the fragment is added to the activity
                Preference p = findPreference("percentStart");
                if (p != null) {
                    String percentStart = shared.getString("percentStart", "0");
                    String percentMax = shared.getString("percentMax", "0");
                    if (percentStart.equals("")) {
                        Toast.makeText(getActivity(), "The start percent was not input correctly", Toast.LENGTH_SHORT).show();
                    }
                    else if (Integer.parseInt(percentStart) >= Integer.parseInt(percentMax)) {
                        Toast.makeText(getActivity(), "The start percent cannot be more than the max percent", Toast.LENGTH_SHORT).show();
                        int newPercentStart = Integer.parseInt(percentMax) - 1;
                        if (newPercentStart < 0) {                                                //percent start cannot be below 0
                            newPercentStart = 0;
                        }
                        editor.putString("percentStart", newPercentStart + "");
                        editor.apply();
                    }
                    else {
                        p.setSummary(getResources().getString(R.string.percentStartDesc)
                                + " " + shared.getString("percentStart", "0") + "%");
                    }
                }
            }
        }

        /**
         * Sets the summary for the max percent
         * String needs to be acquired differently since its being added
         */
        protected void setPercentMaxSummary() {
            if (isAdded()) {                                                                       //must check if the fragment is added to the activity
                Preference p = findPreference("percentMax");
                if (p != null) {
                    String percentStart = shared.getString("percentStart", "0");
                    String percentMax = shared.getString("percentMax", "0");
                    if (percentStart.equals("")) {
                        Toast.makeText(getActivity(), "The max percent was not input correctly", Toast.LENGTH_SHORT).show();
                    }
                    else if (Integer.parseInt(percentMax) <= Integer.parseInt(percentStart)) {
                        Toast.makeText(getActivity(), "The max percent cannot be less than the start percent", Toast.LENGTH_SHORT).show();
                        int newPercentMax = Integer.parseInt(percentStart) + 1;
                        if (newPercentMax < 1) {                                                  //percent max cannot be below 1
                            newPercentMax = 1;
                        }
                        editor.putString("percentMax", newPercentMax + "");
                        editor.apply();
                    }
                    else {
                        p.setSummary(getResources().getString(R.string.percentLimitDesc)
                                + " " + shared.getString("percentMax", "30") + "%");
                    }
                }
            }
        }
    }
}
