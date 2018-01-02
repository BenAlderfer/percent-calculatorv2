package com.alderferstudios.percentcalculatorv2.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.view.MenuItem
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.activity.OneItemActivity.Companion.editor
import com.alderferstudios.percentcalculatorv2.activity.OneItemActivity.Companion.shared
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import com.alderferstudios.percentcalculatorv2.util.PrefConstants

/**
 * Settings screen
 */
class SettingsActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        shared = PreferenceManager.getDefaultSharedPreferences(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        try {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        needsActRestart = shared?.getBoolean(PrefConstants.NEEDS_ACTIVITY_RESTART, false) == true
        needsFullRestart = shared?.getBoolean(PrefConstants.NEEDS_FULL_RESTART, false) == true

        if (MiscUtil.isLandscape(this)) {
            /* getFragmentManager().beginTransaction()
                    .replace(R.id.frame1, new LandscapePrefsFragment1()).commit();

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame2, new LandscapePrefsFragment2()).commit();*/
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.framePort, PrefsFragment()).commit()
        }

        shared?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Restarts the activity if the theme or color was changed
     * Removes leading zeros from percent limit and tax
     * Cannot be done in fragment b/c fragments are static
     * @param sharedPreferences the shared prefs
     * @param key the name of the pref
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == PrefConstants.THEME_LIST || key == PrefConstants.COLOR_LIST) {
            onRestart()
        }

        if (key == PrefConstants.PERCENT_INPUT) {    //removes leading zeros and puts it back, remakes edit for use later
            editor?.putString(PrefConstants.PERCENT_INPUT, removeLeadingZeroes(shared?.getString(PrefConstants.PERCENT_INPUT, "") ?: ""))
            editor?.apply()
        }

        if (key == PrefConstants.TAX_INPUT) {    //removes leading zeros and puts it back, remakes edit for use later
            editor?.putString(PrefConstants.TAX_INPUT, removeLeadingZeroes(shared?.getString(PrefConstants.TAX_INPUT, "") ?: ""))
            editor?.apply()
        }
    }

    /**
     * Overridden to apply activity specific themes
     * Applies the correct colors based on the theme
     * Makes some changes on Lollipop
     */
    override fun applyTheme() {
        themeChoice = shared?.getString(PrefConstants.THEME_LIST, "Light")
        colorChoice = shared?.getString(PrefConstants.COLOR_LIST, "Green")

        if (colorChoice == "Dynamic") {
            when (themeChoice) {
                "Dark" -> setTheme(R.style.GreenDark)
                "Black and White" -> setTheme(R.style.BlackAndWhite)
                else -> setTheme(R.style.GreenLight)
            }
        } else {
            super.applyTheme()
        }
    }

    /**
     * Removes any leading zeros
     *
     * @param string the String to edit
     * @return the String without leading zeros
     */
    private fun removeLeadingZeroes(string: String): String {
        var s = string
        while (s.substring(0, 1) == "0") {
            s = s.substring(1)
        }

        return s
    }

    override fun onRestart() {
        editor?.putBoolean(PrefConstants.NEEDS_ACTIVITY_RESTART, needsActRestart)    //saves variables in case it needs to restart for color/theme change
        editor?.putBoolean(PrefConstants.NEEDS_FULL_RESTART, needsFullRestart)
        editor?.apply()

        super.onRestart()
    }

    open class PCFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

        /**
         * Sets the summaries on start
         */
        protected open fun setSummaries() {
            setPercentStartSummary()    //starting percent
            setPercentMaxSummary()    //max percent
            setTaxBoxSummary()    //Tax box
            setTaxSummary()    //Tax number
            setAfterBoxSummary()    //Calculate after tax box
            setSaveBoxSummary()    //Save box
            setSplitSummary()    //Split list
            setCombinedBoxSummary()    //Combined box
            setThemeListSummary()    //Theme list
            setColorListSummary()    //Color box
        }

        /**
         * Sets the summaries on change
         */
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                PrefConstants.PERCENT_START -> setPercentStartSummary()
                PrefConstants.PERCENT_MAX -> setPercentMaxSummary()
                PrefConstants.TAX_BOX -> setTaxBoxSummary()
                PrefConstants.TAX_INPUT -> setTaxSummary()
                PrefConstants.AFTER_BOX -> setAfterBoxSummary()
                PrefConstants.SAVE_BOX -> {
                    setSaveBoxSummary()
                    needsActRestart = true
                }
                PrefConstants.SPLIT_LIST -> setSplitSummary()
                PrefConstants.COMBINED_BOX -> {
                    setCombinedBoxSummary()
                    needsFullRestart = true
                }
                PrefConstants.THEME_LIST -> setThemeListSummary()
                PrefConstants.COLOR_LIST -> setColorListSummary()
            }
        }

        /**
         * Sets the summary for the starting percent
         * String needs to be acquired differently since its being added
         */
        protected fun setPercentStartSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.PERCENT_START)
                if (p != null) {
                    val percentStart = shared?.getString(PrefConstants.PERCENT_START, "0")
                    val percentMax = shared?.getString(PrefConstants.PERCENT_MAX, "0")
                    if (percentStart == null || percentMax == null) {
                        return
                    }
                    when {
                        percentStart == "" -> MiscUtil.showToast(activity, getString(R.string.start_percent_incorrect))
                        percentStart.toInt() >= percentMax.toInt() -> {
                            MiscUtil.showToast(activity, getString(R.string.start_greater_than_max))
                            var newPercentStart = percentMax.toInt() - 1
                                if (newPercentStart < 0) {    //percent start cannot be below 0
                                newPercentStart = 0
                            }
                            editor?.putString(PrefConstants.PERCENT_START, newPercentStart.toString())
                            editor?.apply()
                        }
                        else -> p.summary = (resources.getString(R.string.percentStartDesc, percentStart))
                    }
                }
            }
        }

        /**
         * Sets the summary for the max percent
         * String needs to be acquired differently since its being added
         */
        protected fun setPercentMaxSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.PERCENT_MAX)
                if (p != null) {
                    val percentStart = shared?.getString(PrefConstants.PERCENT_START, "0")
                    val percentMax = shared?.getString(PrefConstants.PERCENT_MAX, "0")
                    if (percentStart == null || percentMax == null) {
                        return
                    }
                    when {
                        percentStart == "" -> MiscUtil.showToast(activity, getString(R.string.max_percent_incorrect))
                        percentMax.toInt() <= percentStart.toInt() -> {
                            MiscUtil.showToast(activity, getString(R.string.max_less_than_start))
                            var newPercentMax = percentStart.toInt() + 1
                            if (newPercentMax < 1) {    //percent max cannot be below 1
                                newPercentMax = 1
                            }
                            editor?.putString(PrefConstants.PERCENT_MAX, newPercentMax.toString())
                            editor?.apply()
                        }
                        else -> p.summary = (resources.getString(R.string.percentLimitDesc, percentMax))
                    }
                }
            }
        }

        /**
         * Sets the summary for the tax box
         */
        protected fun setTaxBoxSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.TAX_BOX)
                if (p != null) {
                    if (shared?.getBoolean(PrefConstants.TAX_BOX, false) == true) {
                        p.setSummary(R.string.enabledTaxDesc)
                    } else {
                        p.setSummary(R.string.disabledTaxDesc)
                    }
                }
            }
        }

        /**
         * Sets the summary for the tax
         * String needs to be acquired differently since its being added
         */
        protected fun setTaxSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.TAX_INPUT)
                if (p != null) {
                    p.summary = (resources.getString(R.string.taxDesc, shared?.getString(PrefConstants.TAX_INPUT, "6")))
                }
            }
        }

        /**
         * Sets the summary for the after box
         */
        protected fun setAfterBoxSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.AFTER_BOX)
                if (p != null) {
                    if (shared?.getBoolean(PrefConstants.AFTER_BOX, false) == true) {
                        p.setSummary(R.string.enabledAfterTaxDesc)
                    } else {
                        p.setSummary(R.string.disabledAfterTaxDesc)
                    }
                }
            }
        }

        /**
         * Sets the summary for the save box
         */
        protected fun setSaveBoxSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.SAVE_BOX)
                if (p != null) {
                    if (shared?.getBoolean(PrefConstants.SAVE_BOX, false) == true) {
                        p.setSummary(R.string.enabledSaveDesc)
                    } else {
                        p.setSummary(R.string.disabledSaveDesc)
                    }
                }
            }
        }

        /**
         * Sets the summary for the split list
         */
        protected fun setSplitSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.SPLIT_LIST)
                if (p != null) {
                    p.summary = (resources.getString(R.string.splitDesc, shared?.getString(PrefConstants.SPLIT_LIST, PrefConstants.SPLIT_TIP)))
                }
            }
        }

        /**
         * Sets the summary for the combined box
         */
        protected fun setCombinedBoxSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.COMBINED_BOX)
                if (p != null) {
                    if (shared?.getBoolean(PrefConstants.COMBINED_BOX, false) == true) {
                        p.setSummary(R.string.enabledCombinedDesc)
                    } else {
                        p.setSummary(R.string.disabledCombinedDesc)
                    }
                }
            }
        }

        /**
         * Sets the summary for the theme list
         * String needs to be acquired differently since its being added
         */
        protected fun setThemeListSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.THEME_LIST)
                if (p != null) {
                    p.summary = (resources.getString(R.string.themeDesc, shared?.getString(PrefConstants.THEME_LIST, "Light")))
                }
            }
        }


        /**
         * Sets the summary for the color list
         * String needs to be acquired differently since its being added
         */
        protected fun setColorListSummary() {
            if (isAdded) {    //must check if the fragment is added to the activity
                val p = findPreference(PrefConstants.COLOR_LIST)
                if (p != null) {
                    p.summary = (resources.getString(R.string.colorDesc, shared?.getString(PrefConstants.COLOR_LIST, "Dynamic")))
                }
            }
        }
    }

    /**
     * The portrait fragment, contains everything
     */
    class PrefsFragment : PCFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            PreferenceManager.setDefaultValues(activity, R.xml.prefs, false)
            addPreferencesFromResource(R.xml.prefs)

            setSummaries()
            shared?.registerOnSharedPreferenceChangeListener(this)
        }
    }

    /**
     * Left landscape fragment, contains functionality tweaks
     */
    class LandscapePrefsFragment1 : PCFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            PreferenceManager.setDefaultValues(activity, R.xml.prefs, false)
            addPreferencesFromResource(R.xml.land_prefs1)

            setSummaries()
            shared?.registerOnSharedPreferenceChangeListener(this)
        }

        /**
         * Sets the summaries on start
         */
        override fun setSummaries() {
            setPercentStartSummary()    //starting percent
            setPercentMaxSummary()    //max percent
            setTaxBoxSummary()    //Tax box
            setTaxSummary()    //Tax number
            setAfterBoxSummary()    //Calculate after tax box
            setSaveBoxSummary()    //Save box
        }

        /**
         * Sets the summaries on change
         */
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                PrefConstants.PERCENT_START -> setPercentStartSummary()
                PrefConstants.PERCENT_MAX -> setPercentMaxSummary()
                PrefConstants.TAX_BOX -> setTaxBoxSummary()
                PrefConstants.TAX_INPUT -> setTaxSummary()
                PrefConstants.AFTER_BOX -> setAfterBoxSummary()
                PrefConstants.SAVE_BOX -> {
                    setSaveBoxSummary()
                    needsActRestart = true
                }
            }
        }
    }

    /*@Override
    public void onBackPressed()
    {
        editor.putBoolean("didJustGoBack", true);    //saves back action
        editor.apply();

        if (needsFullRestart)    //if combined pref was changed, restart app
        {
            editor.putBoolean("needsFullRestart", false);    //resets variable
            editor.apply();

            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(i);
        }
        else if (needsActRestart)
        {
            Intent last = new Intent(this, CostActivity.class);	    //remakes last activity, defaults to Cost
            switch (caller)
            {
                case "percent": last = new Intent(this, PercentActivity.class); break;
                case "split": last = new Intent(this, SplitActivity.class); break;
                case "results": last = new Intent(this, ResultsActivity.class); break;
                case "combined": last = new Intent(this, CombinedActivity.class); break;
            }

            editor.putBoolean("needsActRestart", false);    //resets variable
            editor.apply();

            last.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(last);
        }
        else
            super.onBackPressed();
    }*/

    /**
     * Right landscape fragment, contains split and design tweaks
     */
    class LandscapePrefsFragment2 : PCFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            PreferenceManager.setDefaultValues(activity, R.xml.prefs, false)
            addPreferencesFromResource(R.xml.land_prefs2)

            setSummaries()
            shared?.registerOnSharedPreferenceChangeListener(this)
        }

        /**
         * Sets the summaries
         */
        override fun setSummaries() {
            setSplitSummary()    //Split list
            setCombinedBoxSummary()    //Combined box
            setThemeListSummary()    //Theme list
            setColorListSummary()    //Color box
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            when (key) {
                PrefConstants.SPLIT_LIST -> setSplitSummary()
                PrefConstants.COMBINED_BOX -> {
                    setCombinedBoxSummary()
                    needsFullRestart = true
                }
                PrefConstants.THEME_LIST -> setThemeListSummary()
                PrefConstants.COLOR_LIST -> setColorListSummary()
            }
        }
    }

    companion object {

        private var needsActRestart: Boolean = false
        private var needsFullRestart: Boolean = false
    }
}