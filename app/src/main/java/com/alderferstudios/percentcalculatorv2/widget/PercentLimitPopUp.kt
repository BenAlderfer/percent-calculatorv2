package com.alderferstudios.percentcalculatorv2.widget

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.widget.Toast

import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.PrefKeys

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
                PrefKeys.PERCENT_START -> setPercentStartSummary()
                PrefKeys.PERCENT_MAX -> setPercentMaxSummary()
            }
        }

        /**
         * Sets the summary for the starting percent
         * String needs to be acquired differently since its being added
         */
        private fun setPercentStartSummary() {
            if (isAdded) {  //must check if the fragment is added to the activity
                val p = findPreference(PrefKeys.PERCENT_START)
                if (p != null) {
                    val percentStart = shared.getString(PrefKeys.PERCENT_START, "0")
                    val percentMax = shared.getString(PrefKeys.PERCENT_MAX, "0")
                    when {
                        percentStart == "" -> Toast.makeText(activity, getString(R.string.start_percent_incorrect), Toast.LENGTH_SHORT).show()
                        percentStart.toInt() >= percentMax.toInt() -> {
                            Toast.makeText(activity, getString(R.string.start_greater_than_max), Toast.LENGTH_SHORT).show()
                            var newPercentStart = percentMax.toInt() - 1
                            if (newPercentStart < 0) {  //percent start cannot be below 0
                                newPercentStart = 0
                            }
                            editor.putString(PrefKeys.PERCENT_START, newPercentStart.toString() + "")
                            editor.apply()
                        }
                        else -> p.summary = (resources.getString(R.string.percentStartDesc, shared.getString(PrefKeys.PERCENT_START, "0")))
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
                val p = findPreference(PrefKeys.PERCENT_MAX)
                if (p != null) {
                    val percentStart = shared.getString(PrefKeys.PERCENT_START, "0")
                    val percentMax = shared.getString(PrefKeys.PERCENT_MAX, "0")
                    when {
                        percentStart == "" -> Toast.makeText(activity, getString(R.string.max_percent_incorrect), Toast.LENGTH_SHORT).show()
                        percentMax.toInt() <= percentStart.toInt() -> {
                            Toast.makeText(activity, getString(R.string.max_less_than_start), Toast.LENGTH_SHORT).show()
                            var newPercentMax = percentStart.toInt() + 1
                            if (newPercentMax < 1) {    //percent max cannot be below 1
                                newPercentMax = 1
                            }
                            editor.putString(PrefKeys.PERCENT_MAX, newPercentMax.toString() + "")
                            editor.apply()
                        }
                        else -> p.summary = (resources.getString(R.string.percentLimitDesc, shared.getString(PrefKeys.PERCENT_MAX, "30")))
                    }
                }
            }
        }
    }
}
