/**
 * @author Ben Alderfer
 * Alderfer Studios
 * Percent Calculator
 * Updated: 2/5/15
 */

package com.alderferstudios.percentcalculatorv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class SplitActivity extends PCActivity
{
    private SharedPreferences.Editor editor;
    private  NumPicker numPick;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        shared = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);                             //sets default values if the preferences have not yet been used
        editor = shared.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        buttons.add((Button) findViewById(R.id.add));
        adjustButtons();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        numPick = (NumPicker) findViewById(R.id.numPicker);
        applyPrefs();

        findViewById(R.id.splitView).setOnTouchListener(new OnSwipeTouchListener(this)            //swipe listeners for activity movement
        {
            public void onSwipeTop()
            {
                add(findViewById(R.id.add));
            }


            public void onSwipeBottom()
            {
                onBackPressed();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_split, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent settingsActivity = new Intent(this, PrefsActivity.class);
                settingsActivity.putExtra("caller", "split");
                startActivity(settingsActivity);
                return true;

            case R.id.split_options:
                Intent popUp = new Intent(this, SplitPopUp.class);
                startActivity(popUp);
                return true;

            default:
                onBackPressed();
                return true;
        }
    }

    /**
     * Applies preference settings
     */
    private void applyPrefs()
    {
        if (shared.getBoolean("saveBox", false))                                                  //fills in last value if save is enabled
            if (shared.getInt("split", 4) >= 2 && shared.getInt("split", 4) <= 100)               //makes sure the number is in the correct range
                numPick.setValue(shared.getInt("split", 4));
            else
                numPick.setValue(4);
        else                                               //default value is 4
            numPick.setValue(4);
    }

    /**
     * Saves the value of the number picker
     * Switches to results
     * @param View the View
     */
    public void add(View View)
    {
        editor.putInt("split", numPick.getValue());                                               //saves the value of the number picker
        editor.putBoolean("didSplit", true);                                                      //lets the results know that they want to split
        editor.putBoolean("didJustGoBack", false);                                                //clears back action and remakes editor for later use
        editor.apply();

        Intent results = new Intent(this, ResultsActivity.class);					              //switches to results
        startActivity(results);
    }

    /**
     * Overridden to apply activity specific themes
     * Applies the correct colors based on the theme
     * Makes some changes on Lollipop
     */
    @Override
    protected void applyTheme()
    {
        themeChoice = shared.getString("themeList", "Light");
        colorChoice = shared.getString("colorList", "Green");

        if (colorChoice.equals("Dynamic"))
            switch (themeChoice)
            {
                case "Dark": setTheme(R.style.RedDark); break;
                case "Black and White": setTheme(R.style.BlackAndWhite); break;
                default: setTheme(R.style.RedLight); break;
            }
        else
            super.applyTheme();
    }

    /**
     * Overridden to apply activity specific button
     * Lollipop gets ripple button
     * Others get regular button using setBackgroundDrawable
     * To prevent having to raise the min api
     */
    protected void adjustButtons()
    {
        if (colorChoice.equals("Dynamic"))
            for (Button b: buttons)
            {
                if (isLollipop())
                    b.setBackgroundResource(R.drawable.ripple_red_button);
                else
                    b.setBackgroundResource(R.drawable.red_button);
            }
        else
            super.adjustButtons();
    }
}