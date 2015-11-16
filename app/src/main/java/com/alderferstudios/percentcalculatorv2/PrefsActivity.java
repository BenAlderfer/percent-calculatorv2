package com.alderferstudios.percentcalculatorv2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

/**
 * Percent Calculator
 * The settings screen
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class PrefsActivity extends PCActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static boolean needsActRestart, needsFullRestart;
    private String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        shared = PreferenceManager.getDefaultSharedPreferences(this);
        editor = shared.edit();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        needsActRestart = shared.getBoolean("needsActRestart", false);
        needsFullRestart = shared.getBoolean("needsFullRestart", false);
        caller = getIntent().getStringExtra("caller");

        if (isLandscape())
        {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame1, new LandscapePrefsFragment1()).commit();

            getFragmentManager().beginTransaction()
                    .replace(R.id.frame2, new LandscapePrefsFragment2()).commit();
        }

        else
           getFragmentManager().beginTransaction()
                    .replace(R.id.framePort, new PrefsFragment()).commit();

        shared.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        onBackPressed();
        return true;
    }

    /**
     * Restarts the activity if the theme or color was changed
     * Removes leading zeros from percent limit and tax
     * Cannot be done in fragment b/c fragments are static
     * @param sharedPreferences the shared prefs
     * @param key the name of the pref
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals("themeList") || key.equals("colorList"))
            onRestart();

        if (key.equals("percentInput"))                                                           //removes leading zeros and puts it back, remakes edit for use later
        {
            editor.putString("percentInput", removeLeadingZeroes(shared.getString("percentInput", "")));
            editor.apply();
            editor = shared.edit();
        }

        if (key.equals("taxInput"))                                                               //removes leading zeros and puts it back, remakes edit for use later
        {
            editor.putString("taxInput", removeLeadingZeroes(shared.getString("taxInput", "")));
            editor.apply();
            editor = shared.edit();
        }
    }

    /**
     * Overridden to apply activity specific themes
     * Applies the correct colors based on the theme
     * Makes some changes on Lollipop
     */
    @Override
    protected void applyTheme() {
        themeChoice = shared.getString("themeList", "Light");
        colorChoice = shared.getString("colorList", "Green");

        if (colorChoice.equals("Dynamic"))
            switch (themeChoice) {
                case "Dark":
                    setTheme(R.style.GreenDark);
                    break;
                case "Black and White":
                    setTheme(R.style.BlackAndWhite);
                    break;
                default:
                    setTheme(R.style.GreenLight);
                    break;
            }
        else
            super.applyTheme();
    }

    /**
     * Removes any leading zeros
     *
     * @param s the String to edit
     * @return s the String without leading zeros
     */
    private String removeLeadingZeroes(String s) {
        while (s.substring(0, 1).equals("0"))
            s = s.substring(1);

        return s;
    }

    @Override
    protected void onRestart() {
        editor.putBoolean("needsActRestart", needsActRestart);                                    //saves variables in case it needs to restart for color/theme change
        editor.putBoolean("needsFullRestart", needsFullRestart);
        editor.apply();

        super.onRestart();
    }

    public static class PCFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
        }

        /**
         * Sets the summaries on start
         */
        protected void setSummaries()
        {
            setPercentStartSummary();                                                             //starting percent
            setPercentMaxSummary();                                                               //max percent
            setTaxBoxSummary();                                                                   //Tax box
            setTaxSummary();                                                                      //Tax number
            setAfterBoxSummary();                                                                 //Calculate after tax box
            setSaveBoxSummary();                                                                  //Save box
            setSplitSummary();                                                                    //Split list
            setCombinedBoxSummary();                                                              //Combined box
            setThemeListSummary();                                                                //Theme list
            setColorListSummary();                                                                //Color box
        }

        /**
         * Sets the summaries on change
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            switch (key)
            {
                case "percentStart": setPercentStartSummary(); break;                             //starting percent
                case "percentMax": setPercentMaxSummary(); break;                                 //max percent
                case "taxBox": setTaxBoxSummary(); break;                                         //Tax box
                case "taxInput": setTaxSummary(); break;                                          //Tax number
                case "afterBox": setAfterBoxSummary(); break;                                     //Calculate after tax box
                case "saveBox": setSaveBoxSummary(); needsActRestart = true; break;               //Save box
                case "splitList": setSplitSummary(); break;                                       //Split list
                case "combinedBox": setCombinedBoxSummary(); needsFullRestart = true; break;      //Combined box
                case "themeList": setThemeListSummary(); break;                                   //Theme list
                case "colorList": setColorListSummary(); break;                                   //Color box
            }
        }

        /**
         * Sets the summary for the starting percent
         * String needs to be acquired differently since its being added
         */
        protected void setPercentStartSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("percentStart");
                if (p != null) {
                    String percentStart = shared.getString("percentStart", "0");
                    String percentMax = shared.getString("percentMax", "0");
                    if (percentStart.equals("")) {
                        showToast("The start percent was not input correctly");
                    }
                    else if (Integer.parseInt(percentStart) >= Integer.parseInt(percentMax)) {
                        showToast("The start percent cannot be more than the max percent");
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
        protected void setPercentMaxSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("percentMax");
                if (p != null){
                    String percentStart = shared.getString("percentStart", "0");
                    String percentMax = shared.getString("percentMax", "0");
                    if (percentStart.equals("")) {
                        showToast("The max percent was not input correctly");
                    }
                    else if (Integer.parseInt(percentMax) <= Integer.parseInt(percentStart)) {
                        showToast("The max percent cannot be less than the start percent");
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

        /**
         * Sets the summary for the tax box
         */
        protected void setTaxBoxSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("taxBox");
                if (p != null)
                    if (shared.getBoolean("taxBox", false))
                        p.setSummary(R.string.enabledTaxDesc);
                    else
                        p.setSummary(R.string.disabledTaxDesc);
            }
        }

        /**
         * Sets the summary for the tax
         * String needs to be acquired differently since its being added
         */
        protected void setTaxSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("taxInput");
                if (p != null)
                    p.setSummary(getResources().getString(R.string.taxDesc)
                            + " " + shared.getString("taxInput", "6") + "%");
            }
        }

        /**
         * Sets the summary for the after box
         */
        protected void setAfterBoxSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("afterBox");
                if (p != null)
                    if (shared.getBoolean("afterBox", false))
                        p.setSummary(R.string.enabledAfterTaxDesc);
                    else
                        p.setSummary(R.string.disabledAfterTaxDesc);
            }
        }

        /**
         * Sets the summary for the save box
         */
        protected void setSaveBoxSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("saveBox");
                if (p != null)
                    if (shared.getBoolean("saveBox", false))
                        p.setSummary(R.string.enabledSaveDesc);
                    else
                        p.setSummary(R.string.disabledSaveDesc);
            }
        }

        /**
         * Sets the summary for the split list
         */
        protected void setSplitSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("splitList");
                if (p != null)
                    p.setSummary(getResources().getString(R.string.splitDesc)
                            + " " + shared.getString("splitList", "Split tip"));
            }
        }

        /**
         * Sets the summary for the combined box
         */
        protected void setCombinedBoxSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("combinedBox");
                if (p != null)
                    if (shared.getBoolean("combinedBox", false))
                        p.setSummary(R.string.enabledCombinedDesc);
                    else
                        p.setSummary(R.string.disabledCombinedDesc);
            }
        }

        /**
         * Sets the summary for the theme list
         * String needs to be acquired differently since its being added
         */
        protected void setThemeListSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("themeList");
                if (p != null)
                    p.setSummary(getResources().getString(R.string.themeDesc)
                            + " " + shared.getString("themeList", "Light"));
            }
        }


        /**
         * Sets the summary for the color list
         * String needs to be acquired differently since its being added
         */
        protected void setColorListSummary()
        {
            if (isAdded())                                                                        //must check if the fragment is added to the activity
            {
                Preference p = findPreference("colorList");
                if (p != null)
                    p.setSummary(getResources().getString(R.string.colorDesc)
                            + " " + shared.getString("colorList", "Dynamic"));
            }
        }
    }

    /**
     * The portrait fragment, contains everything
     */
    public static class PrefsFragment extends PCFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, false);
            addPreferencesFromResource(R.xml.prefs);

            setSummaries();
            shared.registerOnSharedPreferenceChangeListener(this);
        }
    }

    /**
     * Left landscape fragment, contains functionality tweaks
     */
    public static class LandscapePrefsFragment1 extends PCFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, false);
            addPreferencesFromResource(R.xml.land_prefs1);

            setSummaries();
            shared.registerOnSharedPreferenceChangeListener(this);
        }

        /**
         * Sets the summaries on start
         */
        @Override
        protected void setSummaries()
        {
            setPercentStartSummary();                                                             //starting percent
            setPercentMaxSummary();                                                               //max percent
            setTaxBoxSummary();                                                                   //Tax box
            setTaxSummary();                                                                      //Tax number
            setAfterBoxSummary();                                                                 //Calculate after tax box
            setSaveBoxSummary();                                                                  //Save box
        }

        /**
         * Sets the summaries on change
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            switch (key)
            {
                case "percentStart": setPercentStartSummary(); break;                             //starting percent
                case "percentMax": setPercentMaxSummary(); break;                                 //max percent
                case "taxBox": setTaxBoxSummary(); break;                                         //Tax box
                case "taxInput": setTaxSummary(); break;                                          //Tax number
                case "afterBox": setAfterBoxSummary(); break;                                     //Calculate after tax box
                case "saveBox": setSaveBoxSummary(); needsActRestart = true; break;               //Save box
            }
        }
    }

    /*@Override
    public void onBackPressed()
    {
        editor.putBoolean("didJustGoBack", true);                                                 //saves back action
        editor.apply();

        if (needsFullRestart)                                                                     //if combined pref was changed, restart app
        {
            editor.putBoolean("needsFullRestart", false);                                         //resets variable
            editor.apply();

            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(i);
        }
        else if (needsActRestart)
        {
            Intent last = new Intent(this, CostActivity.class);	                                  //remakes last activity, defaults to Cost
            switch (caller)
            {
                case "percent": last = new Intent(this, PercentActivity.class); break;
                case "split": last = new Intent(this, SplitActivity.class); break;
                case "results": last = new Intent(this, ResultsActivity.class); break;
                case "combined": last = new Intent(this, CombinedActivity.class); break;
            }

            editor.putBoolean("needsActRestart", false);                                          //resets variable
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
    public static class LandscapePrefsFragment2 extends PCFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager.setDefaultValues(getActivity(), R.xml.prefs, false);
            addPreferencesFromResource(R.xml.land_prefs2);

            setSummaries();
            shared.registerOnSharedPreferenceChangeListener(this);
        }

        /**
         * Sets the summaries
         */
        @Override
        protected void setSummaries()
        {
            setSplitSummary();                                                                    //Split list
            setCombinedBoxSummary();                                                              //Combined box
            setThemeListSummary();                                                                //Theme list
            setColorListSummary();                                                                //Color box
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            switch (key)
            {
                case "splitList": setSplitSummary();  break;                                      //Split list
                case "combinedBox": setCombinedBoxSummary(); needsFullRestart = true; break;      //Combined box
                case "themeList": setThemeListSummary(); break;                                   //Theme list
                case "colorList": setColorListSummary(); break;                                   //Color box
            }
        }
    }
}