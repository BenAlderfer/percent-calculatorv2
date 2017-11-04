package com.alderferstudios.percentcalculatorv2.widget

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.widget.Toast

import com.alderferstudios.percentcalculatorv2.R

/**
 * pop up for choosing percent limits
 */
class PercentLimitPopUp : BasePopUpPreference() {

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
        private val editor = shared.edit()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            PreferenceManager.setDefaultValues(activity, R.xml.percent_limit_pref_pop_up, false)
            addPreferencesFromResource(R.xml.percent_limit_pref_pop_up)

            setSummaries()
            shared.registerOnSharedPreferenceChangeListener(this)
        }

        /**
         * Sets the summaries on start
         */
        private fun setSummaries() {
            setPercentStartSummary()    //starting percent
            setPercentMaxSummary()  //max percent
        }

        /**
         * Sets the summaries on change
         */
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                "percentStart" -> setPercentStartSummary()
                "percentMax" -> setPercentMaxSummary()
            }
        }

        /**
         * Sets the summary for the starting percent
         * String needs to be acquired differently since its being added
         */
        private fun setPercentStartSummary() {
            if (isAdded) {  //must check if the fragment is added to the activity
                val p = findPreference("percentStart")
                if (p != null) {
                    val percentStart = shared.getString("percentStart", "0")
                    val percentMax = shared.getString("percentMax", "0")
                    if (percentStart == "") {
                        Toast.makeText(activity, "The start percent was not input correctly", Toast.LENGTH_SHORT).show()
                    } else if (Integer.parseInt(percentStart) >= Integer.parseInt(percentMax)) {
                        Toast.makeText(activity, "The start percent cannot be more than the max percent", Toast.LENGTH_SHORT).show()
                        var newPercentStart = Integer.parseInt(percentMax) - 1
                        if (newPercentStart < 0) {  //percent start cannot be below 0
                            newPercentStart = 0
                        }
                        editor.putString("percentStart", newPercentStart.toString() + "")
                        editor.apply()
                    } else {
                        p.summary = (resources.getString(R.string.percentStartDesc)
                                + " " + shared.getString("percentStart", "0") + "%")
                    }
                }
            }
        }

        /**
         * Sets the summary for the max percent
         * String needs to be acquired differently since its being added
         */
        private fun setPercentMaxSummary() {
            if (isAdded) {  //must check if the fragment is added to the activity
                val p = findPreference("percentMax")
                if (p != null) {
                    val percentStart = shared.getString("percentStart", "0")
                    val percentMax = shared.getString("percentMax", "0")
                    if (percentStart == "") {
                        Toast.makeText(activity, "The max percent was not input correctly", Toast.LENGTH_SHORT).show()
                    } else if (Integer.parseInt(percentMax) <= Integer.parseInt(percentStart)) {
                        Toast.makeText(activity, "The max percent cannot be less than the start percent", Toast.LENGTH_SHORT).show()
                        var newPercentMax = Integer.parseInt(percentStart) + 1
                        if (newPercentMax < 1) {    //percent max cannot be below 1
                            newPercentMax = 1
                        }
                        editor.putString("percentMax", newPercentMax.toString() + "")
                        editor.apply()
                    } else {
                        p.summary = (resources.getString(R.string.percentLimitDesc)
                                + " " + shared.getString("percentMax", "30") + "%")
                    }
                }
            }
        }
    }
}
