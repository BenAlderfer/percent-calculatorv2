package com.alderferstudios.percentcalculatorv2.widget

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.PrefConstants

/**
 * pop up for choosing the split mode
 */
class SplitPopUp : BasePopUpPreference() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pref_pop_up)

        fragmentManager.beginTransaction()
                .replace(R.id.prefFrame, PopUpFragment()).commit()
    }

    /**
     * The pop up fragment
     */
    class PopUpFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
        private val shared = PreferenceManager.getDefaultSharedPreferences(activity)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            PreferenceManager.setDefaultValues(activity, R.xml.split_pref_pop_up, false)
            addPreferencesFromResource(R.xml.split_pref_pop_up)

            setSummary()
            shared.registerOnSharedPreferenceChangeListener(this)
        }

        /**
         * Sets the summaries on start
         */
        private fun setSummary() {
            setSplitSummary()  //Split list
        }

        /**
         * Sets the summaries on change
         */
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                PrefConstants.SPLIT_LIST -> setSplitSummary()
            }
        }

        /**
         * Sets the summary for the split list
         */
        private fun setSplitSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.SPLIT_LIST)
                if (p != null) {
                    p.summary = (resources.getString(R.string.splitDesc, shared.getString(PrefConstants.SPLIT_LIST, PrefConstants.SPLIT_TIP)))
                }
            }
        }
    }
}
