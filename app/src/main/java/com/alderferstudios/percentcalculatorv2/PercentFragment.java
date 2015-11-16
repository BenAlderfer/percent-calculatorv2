package com.alderferstudios.percentcalculatorv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

/**
 * Percent Calculator
 * The percent screen
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class PercentFragment extends PCFragment implements SeekBar.OnSeekBarChangeListener {
    private EditText percentage;
    private int percent, percentStart, percentMax;
    private SharedPreferences.Editor editor;
    private NumPicker numPick;
    private SeekBar bar;
    private boolean needsToRestart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.activity_percent, container, false);

        buttons.add((Button) layout.findViewById(R.id.add));
        buttons.add((Button) layout.findViewById(R.id.split));
        buttons.add((Button) layout.findViewById(R.id.subtract));
        adjustButtons();

        bar = (SeekBar) layout.findViewById(R.id.percentBar);
        bar.setOnSeekBarChangeListener(this);

        percentage = (EditText) layout.findViewById(R.id.percentNum);
        percentage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String percentText = percentage.getText().toString();
                if (!percentText.equals("") &&
                        Integer.parseInt(percentText) <= percentMax &&
                        Integer.parseInt(percentText) >= percentStart) {                          //only update bar if its within the limits

                    if (percentText.equals("")) {
                        bar.setProgress(percentStart);
                    } else {
                        bar.setProgress(Integer.parseInt((percentage.getText().toString())) - percentStart);
                    }

                    percentage.setSelection(percentage.getText().length());                       //returns focus to end of text
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //shared.registerOnSharedPreferenceChangeListener(this);
        applyPrefs();

        ////////////////////////////// move this to the activity /////////////////////////////////////////////////////////
        /*findViewById(R.id.percentView).setOnTouchListener(new OnSwipeTouchListener(this)        //swipe listeners for activity movement
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
        });*/

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cost, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //////////////////////////// finish this ////////////////////////////////////////////////////
        /*switch (item.getItemId())
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
        }*/
        return true;
    }

    /**
     * Applies preference settings
     */
    private void applyPrefs() {
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

        if (layout.findViewById(R.id.numPicker) != null)                                          //fills in last or default value for split picker if it is there
        {
            numPick = (NumPicker) layout.findViewById(R.id.numPicker);
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
     *
     * @return true if it exceeds a limit; false otherwise
     */
    private boolean percentIsWrong() {
        String percentText = percentage.getText().toString();
        if (percentText.equals("")) {                                                             //updates bar if nothing was entered
            bar.setProgress(percentStart);
            percentage.setText(percentStart + "");
            percentText = percentage.getText().toString();
        }

        if (Integer.parseInt(percentText) > percentMax) {
            percentage.setText(percentMax + "");
            showToast("The percent cannot be greater than the max percent");
            return true;
        } else if (Integer.parseInt(percentText) < percentStart) {
            percentage.setText(percentStart + "");
            showToast("The percent cannot be less than the start percent");
            return true;
        }
        return false;
    }

    @Override
    protected void performAction(Button b) {
        switch (b.getId()) {
            case R.id.split:
                split(b);
                break;
            case R.id.subtract:
                subtract(b);
                break;
            default:   //portrait add
                add(b);
                break;
        }
    }

    /**
     * When the add button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     *
     * @param view the View
     */
    public void add(View view) {
        if (!percentIsWrong()) {
            editor.putString("button", "add");
            editor.putBoolean("didSplit", false);
            editor.putString("lastAction", "tip");
            ////////////////////// implement new switch /////////////////////////////////////
            /*Intent results = new Intent(this, ResultsActivity.class);
            saveAndSwitch(results);*/
        }
    }

    /**
     * When the subtract button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     *
     * @param view the View
     */
    public void subtract(View view) {
        if (!percentIsWrong()) {
            editor.putString("button", "subtract");
            editor.putBoolean("didSplit", false);
            editor.putString("lastAction", "discount");
            ////////////////////// implement new switch /////////////////////////////////////
            /*Intent results = new Intent(this, ResultsActivity.class);
            saveAndSwitch(results);*/
        }
    }

    /**
     * When the split button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to split activity
     *
     * @param view the View
     */
    public void split(View view) {
        if (!percentIsWrong()) {
            editor.putString("button", "add");
            editor.putBoolean("didSplit", false);
            editor.putString("lastAction", "split");
            ////////////////////// implement new switch /////////////////////////////////////
            /*Intent split = new Intent(this, SplitActivity.class);
            saveAndSwitch(split);*/
        }
    }

    /**
     * When the split button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     * ***Does not go to split screen since the number
     * ***picker is on the same screen in landscape
     *
     * @param view the View
     */
    public void splitLandscape(View view) {
        editor.putString("button", "add");
        editor.putBoolean("didSplit", true);
        editor.putString("lastAction", "split");
        ////////////////////// implement new switch /////////////////////////////////////
        /*Intent results = new Intent(this, ResultsActivity.class);
        saveAndSwitch(results);*/
    }

    /**
     * Saves the data
     * Switches to the given Intent
     *
     * @param nextIntent the Intent to switch to
     */
    private void saveAndSwitch(Intent nextIntent) {
        if (didChangePercent()) {
            editor.putInt("percent", percent);

            if (layout.findViewById(R.id.numPicker) != null)                                             //if there is a numpicker, save the value
                editor.putInt("split", ((NumPicker) layout.findViewById(R.id.numPicker)).getValue());

            editor.putBoolean("didJustGoBack", false);                                            //clears back action and remakes editor for later use
            editor.apply();
            startActivity(nextIntent);
        }
    }

    /**
     * Checks if the user changed the percentage
     *
     * @return true if it was changed, false if its unchanged
     */
    private boolean didChangePercent() {
        if (percent == 0 &&
                (((percentage.getText().toString()).equals("")) ||
                        ((percentage.getText().toString()).equals("0"))))                                  //if the percent is untouched
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        percent = progress + percentStart;
        if (percent > percentMax)
            percent = percentMax;

        percentage.setText(String.valueOf(percent));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /**
     * Overridden to apply activity specific buttons
     * Lollipop gets ripple buttons
     * Others get regular buttons using setBackgroundDrawable
     * To prevent having to raise the min api
     */
    protected void adjustButtons() {
        if (colorChoice.equals("Dynamic"))
            for (Button b : buttons) {
                if (isLollipop())
                    b.setBackgroundResource(R.drawable.ripple_orange_button);
                else
                    b.setBackgroundResource(R.drawable.orange_button);
            }
        else
            super.adjustButtons();
    }

    /**
     * Checks if the percent input is wrong
     * Looks to see if it is greater or less than a limit
     *
     * @return true if it exceeds a limit; false otherwise
     */
    private void resetAfterPrefChange() {
        String percentText = percentage.getText().toString();
        if (percentText.equals("")) {                                                             //updates bar if nothing was entered
            bar.setProgress(percentStart);
            percentage.setText(percentStart + "");
            percentText = percentage.getText().toString();
        }

        if (Integer.parseInt(percentText) > percentMax) {
            percentage.setText(percentMax + "");
        } else if (Integer.parseInt(percentText) < percentStart) {
            percentage.setText(percentStart + "");
        }
    }
}