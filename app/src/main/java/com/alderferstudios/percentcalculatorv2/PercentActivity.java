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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class PercentActivity extends PCActivity implements SeekBar.OnSeekBarChangeListener
{
    private EditText percentage;
    private int percent, percentStart, percentMax;
    private SharedPreferences.Editor editor;
    private NumPicker numPick;
    private SeekBar bar;
    private boolean needsToRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        shared = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);                             //sets default values if the preferences have not yet been used
        editor = shared.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percent);

        buttons.add((Button) findViewById(R.id.add));
        buttons.add((Button) findViewById(R.id.split));
        buttons.add((Button) findViewById(R.id.subtract));
        adjustButtons();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bar = (SeekBar)findViewById(R.id.percentBar);
        bar.setOnSeekBarChangeListener(this);

        percentage = (EditText)findViewById(R.id.percentNum);
        percentage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String percentText = percentage.getText().toString();
                if (!percentText.equals("") &&
                        Integer.parseInt(percentText) <= percentMax &&
                        Integer.parseInt(percentText) >= percentStart) {                              //only update bar if its within the limits

                    if (percentText.equals("")) {
                        bar.setProgress(percentStart);
                    } else {
                        bar.setProgress(Integer.parseInt((percentage.getText().toString())) - percentStart);
                    }

                    percentage.setSelection(percentage.getText().length());                           //returns focus to end of text
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //shared.registerOnSharedPreferenceChangeListener(this);
        applyPrefs();

        findViewById(R.id.percentView).setOnTouchListener(new OnSwipeTouchListener(this)          //swipe listeners for activity movement
        {
            public void onSwipeTop()
            {
                if (!shared.getBoolean("saveBox", false))                                         //if they don't want to save, always assume tip
                    add(findViewById(R.id.add));
                else
                    switch(shared.getString("lastAction", "tip"))                                 //performs last action, default = tip
                    {
                        case "discount": subtract(findViewById(R.id.subtract)); break;

                        case "split":
                            if (isLandscape())
                                splitLandscape(findViewById(R.id.split));
                            else
                                split(findViewById(R.id.split));

                            break;

                        default: add(findViewById(R.id.add)); break;
                    }
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
        getMenuInflater().inflate(R.menu.menu_percent, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent settingsActivity = new Intent(this, PrefsActivity.class);
                settingsActivity.putExtra("caller", "percent");
                startActivity(settingsActivity);
                return true;

            case R.id.bar_limits:
                Intent popUp = new Intent(this, PercentLimitPopUp.class);
                needsToRestart = true;
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
        percentStart = Integer.parseInt(shared.getString("percentStart", "0"));
        percentMax = Integer.parseInt(shared.getString("percentMax", "30"));
        if (bar.getMax() != percentMax)
            bar.setMax(percentMax - percentStart);

        if (shared.getBoolean("saveBox", false))                                                  //fills in last value if save is enabled
        {
            int lastPercent = shared.getInt("percent", 0);
            if (Integer.parseInt(percentage.getText().toString()) > percentMax)
                percentage.setText(Integer.toString(percentMax));
            else
                percentage.setText(Integer.toString(lastPercent));

            percentage.setSelection(percentage.getText().length());                               //puts focus at end of percent text
        }

        if (findViewById(R.id.numPicker) != null)                                                 //fills in last or default value for split picker if it is there
        {
            numPick = (NumPicker) findViewById(R.id.numPicker);
            if (shared.getBoolean("saveBox", false))                                              //fills in last value if save is enabled
                if (shared.getInt("split", 4) >= 2 && shared.getInt("split", 4) <= 100)           //makes sure the number is in the correct range
                    numPick.setValue(shared.getInt("split", 4));
                else
                    numPick.setValue(4);
            else                                                                                  //default value is 4
                numPick.setValue(4);
        }
    }

    /**
     * Restarts the activity if one of the limits was changed
     * @param sharedPreferences the shared prefs
     * @param key the name of the pref
     */
    /*@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals("percentStart") || key.equals("percentMax"))
            needsToRestart = true;
    }*/

    /**
     * Checks if the percent input is wrong
     * Looks to see if it is greater or less than a limit
     * @return true if it exceeds a limit; false otherwise
     */
    private boolean percentIsWrong(){
        String percentText = percentage.getText().toString();
        if (percentText.equals("")) {                                                             //updates bar if nothing was entered
            bar.setProgress(percentStart);
            percentage.setText(percentStart + "");
            percentText = percentage.getText().toString();
        }

        if(Integer.parseInt(percentText) > percentMax) {
            percentage.setText(percentMax + "");
            showToast("The percent cannot be greater than the max percent");
            return true;
        }
        else if(Integer.parseInt(percentText) < percentStart) {
            percentage.setText(percentStart + "");
            showToast("The percent cannot be less than the start percent");
            return true;
        }
        return false;
    }

    /**
     * When the add button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     * @param view the View
     */
    public void add(View view)
    {
        if (!percentIsWrong()) {
            editor.putString("button", "add");
            editor.putBoolean("didSplit", false);
            editor.putString("lastAction", "tip");
            Intent results = new Intent(this, ResultsActivity.class);
            saveAndSwitch(results);
        }
    }

    /**
     * When the subtract button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     * @param view the View
     */
    public void subtract(View view)
    {
        if (!percentIsWrong()) {
            editor.putString("button", "subtract");
            editor.putBoolean("didSplit", false);
            editor.putString("lastAction", "discount");
            Intent results = new Intent(this, ResultsActivity.class);
            saveAndSwitch(results);
        }
    }

    /**
     * When the split button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to split activity
     * @param view the View
     */
    public void split(View view)
    {
        if (!percentIsWrong()) {
            editor.putString("button", "add");
            editor.putBoolean("didSplit", false);
            editor.putString("lastAction", "split");
            Intent split = new Intent(this, SplitActivity.class);
            saveAndSwitch(split);
        }
    }

    /**
     * When the split button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     * ***Does not go to split screen since the number
     * ***picker is on the same screen in landscape
     * @param view the View
     */
    public void splitLandscape(View view)
    {
        editor.putString("button", "add");
        editor.putBoolean("didSplit", true);
        editor.putString("lastAction", "split");
        Intent results = new Intent(this, ResultsActivity.class);
        saveAndSwitch(results);
    }

    /**
     * Saves the data
     * Switches to the given Intent
     * @param nextIntent the Intent to switch to
     */
    private void saveAndSwitch(Intent nextIntent)
    {
        if (didChangePercent())
        {
            editor.putInt("percent", percent);

            if (findViewById(R.id.numPicker) != null)                                             //if there is a numpicker, save the value
                editor.putInt("split", ((NumPicker) findViewById(R.id.numPicker)).getValue());

            editor.putBoolean("didJustGoBack", false);                                            //clears back action and remakes editor for later use
            editor.apply();
            startActivity(nextIntent);
        }
    }

    /**
     * Checks if the user changed the percentage
     * @return true if it was changed, false if its unchanged
     */
    private boolean didChangePercent()
    {
        if (percent == 0 &&
                (((percentage.getText().toString()).equals("")) ||
                ((percentage.getText().toString()).equals("0"))))	                              //if the percent is untouched
        {
            showToast("The percent has not been entered");
            return false;
        }

        return true;
    }

    /**
     * Updates the percent text as the bar changes
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        percent = progress + percentStart;
        if (percent > percentMax)
            percent = percentMax;

        percentage.setText(String.valueOf(percent));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

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
                case "Dark": setTheme(R.style.OrangeDark); break;
                case "Black and White": setTheme(R.style.BlackAndWhite); break;
                default: setTheme(R.style.OrangeLight); break;
            }
        else
            super.applyTheme();
    }

    /**
     * Overridden to apply activity specific buttons
     * Lollipop gets ripple buttons
     * Others get regular buttons using setBackgroundDrawable
     * To prevent having to raise the min api
     */
    protected void adjustButtons()
    {
        if (colorChoice.equals("Dynamic"))
            for (Button b: buttons)
            {
                if (isLollipop())
                    b.setBackgroundResource(R.drawable.ripple_orange_button);
                else
                    b.setBackgroundResource(R.drawable.orange_button);
            }
        else
            super.adjustButtons();
    }

    public void onResume()
    {
        super.onResume();

        if (needsToRestart)
        {
            needsToRestart = false;
            resetAfterPrefChange();
            onRestart();
        }
    }

    /**
     * Checks if the percent input is wrong
     * Looks to see if it is greater or less than a limit
     * @return true if it exceeds a limit; false otherwise
     */
    private void resetAfterPrefChange(){
        String percentText = percentage.getText().toString();
        if (percentText.equals("")) {                                                             //updates bar if nothing was entered
            bar.setProgress(percentStart);
            percentage.setText(percentStart + "");
            percentText = percentage.getText().toString();
        }

        if(Integer.parseInt(percentText) > percentMax) {
            percentage.setText(percentMax + "");
        }
        else if(Integer.parseInt(percentText) < percentStart) {
            percentage.setText(percentStart + "");
        }
    }
}