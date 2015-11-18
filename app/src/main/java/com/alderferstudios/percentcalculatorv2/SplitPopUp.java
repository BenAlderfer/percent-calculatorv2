package com.alderferstudios.percentcalculatorv2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Percent Calculator
 * The pop up for choosing the split mode
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class SplitPopUp extends PopUpPreference {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref_pop_up);

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

            PreferenceManager.setDefaultValues(getActivity(), R.xml.split_pref_pop_up, false);
            addPreferencesFromResource(R.xml.split_pref_pop_up);

            setSummary();
            shared.registerOnSharedPreferenceChangeListener(this);
        }

        /**
         * Sets the summaries on start
         */
        protected void setSummary() {
            setSplitSummary();                                                                    //Split list
        }

        /**
         * Sets the summaries on change
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "splitList": setSplitSummary(); break;                                       //Split list
            }
        }

        /**
         * Sets the summary for the split list
         */
        protected void setSplitSummary() {
            if (isAdded()) {                                                                      //must check if the fragment is added to the activity
                Preference p = findPreference("splitList");
                if (p != null) {
                    p.setSummary(getResources().getString(R.string.splitDesc)
                            + " " + shared.getString("splitList", "Split tip"));
                }
            }
        }
    }
}
