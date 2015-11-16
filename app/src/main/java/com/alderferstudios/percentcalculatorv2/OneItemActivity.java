package com.alderferstudios.percentcalculatorv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Percent Calculator
 * The main activity for 1 item at a time
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class OneItemActivity extends AppCompatActivity {

    protected static SharedPreferences shared;
    protected static SharedPreferences.Editor editor;

    private int pageNum = 0;
    private ViewPager viewPager;
    private PagerAdapter adapter;

    /**
     * Checks if the device is a tablet
     *
     * @param context the Context
     * @return true if device is a tablet
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Creates the Activity and gets all the variables/listeners ready
     *
     * @param savedInstanceState the savedInstanceState from before
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_item);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (!isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        shared = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);                             //sets default values if the preferences have not yet been used
        editor = shared.edit();

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    /**
     * Creates the options menu
     *
     * @param menu the Menu
     * @return always true (useless)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_one_item, menu);
        return true;
    }

    /**
     * Handles toolbar/overflow options
     *
     * @param item the option tapped
     * @return true if it matches one of the options, otherwise goes to super
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_clear_all:
                if (ets[tabNum] == null) {
                    saveEditTexts();
                }
                clearAll();
                return true;

            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                return true;

            case R.id.action_instructions:
                Intent instructionActivity = new Intent(this, InstructionActivity.class);
                startActivity(instructionActivity);
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Custom FragmentPagerAdapter
     */
    public class PagerAdapter extends FragmentPagerAdapter {
        private PCFragment[] pages = new PCFragment[4];

        /**
         * Constructs the PagerAdapter
         *
         * @param fm the FragmentManager
         */
        public PagerAdapter(FragmentManager fm) {
            super(fm);

            pages[0] = new CostFragment();
            pages[1] = new PercentFragment();
            pages[2] = new SplitFragment();
            pages[3] = new ResultsFragment();
        }

        /**
         * Gets the fragment at the position
         *
         * @param position the position of the fragment
         * @return the fragment if it exists
         */
        @Override
        public Fragment getItem(int position) {
            return pages[position];
        }

        /**
         * Gets the number of pages
         *
         * @return the number of pages
         */
        @Override
        public int getCount() {
            return pages.length;
        }
    }
}
