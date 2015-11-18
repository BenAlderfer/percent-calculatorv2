package com.alderferstudios.percentcalculatorv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Percent Calculator
 * The combined screen
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class CombinedActivity extends PCActivity implements SeekBar.OnSeekBarChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView resultsText, resultsText1, resultsText2;                                     //where results are displayed
    private EditText costBox, percentage, splitBox;
    private String text, text1, text2;
    private int percent, percentStart, percentMax;
    private boolean willTax, willSplit, didJustUpdate, didJustTypePercent, needsToRestart;
    private SeekBar bar;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shared = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);                             //sets default values if the preferences have not yet been used
        editor = shared.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combined);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        buttons.add((Button) findViewById(R.id.add));
        buttons.add((Button) findViewById(R.id.split));
        buttons.add((Button) findViewById(R.id.subtract));
        adjustButtons();

        willTax = shared.getBoolean("taxBox", false);                                             //if the tax will be added
        costBox = ((EditText) findViewById(R.id.cost));
        percentage = (EditText) findViewById(R.id.percentNum);
        splitBox = ((EditText) findViewById(R.id.splitNum));
        bar = (SeekBar)findViewById(R.id.percentBar);
        willSplit = false;

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {//hides portrait results
            resultsText = (TextView) findViewById(R.id.results);
            if (resultsText != null) {
                resultsText.setVisibility(View.INVISIBLE);
            }
        } else { //if (orientation == Configuration.ORIENTATION_LANDSCAPE)                          //hides landscape results
            resultsText1 = (TextView) findViewById(R.id.landResults1);
            resultsText2 = (TextView) findViewById(R.id.landResults2);
            if (resultsText1 != null) {
                resultsText1.setVisibility(View.INVISIBLE);
            }
            if (resultsText2 != null) {
                resultsText2.setVisibility(View.INVISIBLE);
            }
        }

        applyPrefs();
        shared.registerOnSharedPreferenceChangeListener(this);
        addListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_combined, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsActivity = new Intent(this, PrefsActivity.class);
                settingsActivity.putExtra("caller", "combined");
                startActivity(settingsActivity);
                return true;

            case R.id.bar_limits:
                Intent popUp = new Intent(this, PercentLimitPopUp.class);
                startActivity(popUp);
                return true;

            case R.id.split_options:
                Intent popUp2 = new Intent(this, SplitPopUp.class);
                startActivity(popUp2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Applies preference settings
     */
    private void applyPrefs() {
        percentStart = Integer.parseInt(shared.getString("percentStart", "0"));
        percentMax = Integer.parseInt(shared.getString("percentMax", "30"));
        if (bar.getMax() != percentMax) {                                                         //refreshes the activity if the current max is not right
            bar.setMax(percentMax - percentStart);
        }

        if (shared.getBoolean("saveBox", false)) {                                                //if they want to save values
            costBox.setText(Double.toString(PreferenceDoubles.getDouble(shared, "cost", 0.00)));  //fill in cost

            percent = shared.getInt("percent", 0);
            percentage.setText(Integer.toString(percent));                                        //fill in percent
            bar.setProgress(percent);                                                             //sets progress bar at right spot

            splitBox.setText(Integer.toString(shared.getInt("split", 4)));                        //fill in split
            //willSplit = shared.getBoolean("didSplit", false);                                     //remembers if they split or not

            costBox.setSelection(costBox.getText().length());                                     //puts focus at end of cost text

            updateResultsWithChanges();                                                           //updates results
        }

        if (shared.getString("button", "").equals("")) {                                          //if no button is currently saved, initializes it to be add
            editor.putString("button", "add");
            editor.apply();
            editor = shared.edit();                                                               //editor must be reinitialized after apply
        }
    }

    /**
     * Restarts the activity if one of the limits was changed
     * @param sharedPreferences the shared prefs
     * @param key the name of the pref
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("percentStart") || key.equals("percentMax") || key.equals("splitList")) {
            needsToRestart = true;
        }
    }

    /**
     * Adds listeners to all items for auto-updating
     */
    private void addListeners() {
        costBox.addTextChangedListener(new TextWatcher() {                                        //watches cost to update
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateResultsWithChanges();                                                       //updates the results text if possible as numbers change
            }
        });

        bar.setOnSeekBarChangeListener(this);
        percentage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                didJustTypePercent = true;

                if (percentage.getText().toString().equals("")) {
                    bar.setProgress(0);
                } else {
                    bar.setProgress(Integer.parseInt((percentage.getText().toString())) - percentStart);
                }

                updateResultsWithChanges();                                                       //updates the results text if possible as numbers change

                percentage.setSelection(percentage.getText().length());                           //returns focus to end of text
            }
        });

        splitBox.addTextChangedListener(new TextWatcher() {                                       //watches split to update
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                willSplit = didEnterSplit(false);                                                 //makes sure the split was entered, false prevents toasts

                updateResultsWithChanges();                                                       //updates the results text if possible as numbers change
            }
        });
    }

    /**
     * Hides the keyboard when a button is clicked
     * Saves the numbers
     */
    private void processValues() {
        if (!didJustUpdate) {                                                                     //makes sure it was not an auto update to avoid breaks in typing
            imm.hideSoftInputFromWindow(splitBox.getWindowToken(), 0);
        }

        didJustUpdate = false;                                                                    //resets variable

        //cost value
        String costString = costBox.getText().toString();
        double cost = PercentCalculator.round(Double.parseDouble(costString));
        editor = PreferenceDoubles.putDouble(editor, "cost", cost);

        //percent value
        if (percent == 0) {                                                                       //if they didn't use the seekbar, the percent will be the default value
            percent = Integer.parseInt(percentage.getText().toString());
        }

        editor.putInt("percent", percent);

        //split value
        int split;
        if (splitBox.getText().toString().equals("")) {                                          //default split is 1
            split = 1;
        } else {
            split = Integer.parseInt(splitBox.getText().toString());
        }

        editor.putInt("split", split);                                                            //saves split amount
        //editor.putBoolean("didSplit", willSplit);                                                 //saves whether they will split or not

        editor.apply();

        PercentCalculator pc = new PercentCalculator(this, getString(R.string.money_separator));
        pc.calculate();                                                                           //calculates the numbers
    }

    /**
     * Makes the text results
     */
    private void makeText() {
        String change = shared.getString("percentAmount", "");                                    //makes the first part of the text, tip is add, else discount
        String eachTip = shared.getString("eachTip", "");                                         //how much each person will tip
        String eachTotal = shared.getString("eachTotal", "");                                     //how much of the total each person pays

        boolean willSplitTip, willSplitTotal;
        switch (shared.getString("splitList", "Split tip")) {
            case "Split total":
                willSplitTip = false;
                willSplitTotal = true;
                break;
            case "Split both":
                willSplitTip = true;
                willSplitTotal = true;
                break;
            default:
                willSplitTip = true;
                willSplitTotal = false;
                break;
        }

        String spacing = "%n";

        /**
         * portrait setup
         */
        if (!isLandscape()) {
            text = "";                                                                            //clears previous results

            if (shared.getString("button", null) != null &&
                    (shared.getString("button", null)).equals("add")) {
                text = "Tip: " + getString(R.string.money_sign) + change + spacing;

                if (willSplit && didEnterSplit(false) && willSplitTip) {                          //if split was clicked, number is entered, and split tip is selected in prefs
                    text += "Each tips: " + getString(R.string.money_sign) + eachTip + spacing;   //adds the split tip part of the text
                }
            } else {
                text = "Discount: " + getString(R.string.money_sign) + change + spacing;
            }

            if (willTax) {                                                                        //makes the tax part of the text
                text += "Tax: " + getString(R.string.money_sign) + shared.getString("taxAmount", "") + spacing;
            }

            text += "Final cost: " + getString(R.string.money_sign) + shared.getString("total", "");//makes the cost total part of the text

            if (willSplit && didEnterSplit(false) && willSplitTotal) {                            //if split was clicked, number is entered, and split total is selected in prefs
                text += spacing + "Each pays: " + getString(R.string.money_sign) + eachTotal;     //adds the split tip part of the text
            }

            setResults();

        } else {                                                                                  //landscape setup
            text1 = "";                                                                           //clears previous results
            text2 = "";

            if (!willSplit || !didEnterSplit(false)) {                                            //if not splitting, use both sides
                if ((shared.getString("button", null) != null &&
                        (shared.getString("button", null)).equals("add"))) {
                    text1 = "Tip: " + getString(R.string.money_sign) + change;
                } else {
                    text1 = "Discount: " + getString(R.string.money_sign) + change;
                }

                if (willTax) {                                                                    //makes the tax part of the text
                    text1 += spacing + "Tax: " + getString(R.string.money_sign) + shared.getString("taxAmount", "");
                }

                text2 = "Final cost: " + getString(R.string.money_sign) + shared.getString("total", "");//makes the cost total part of the text
            } else {                                                                                 //otherwise, splits are on right
                if ((shared.getString("button", null) != null &&
                        (shared.getString("button", null)).equals("add"))) {
                    text1 = "Tip: " + getString(R.string.money_sign) + change + spacing;
                } else {
                    text1 = "Discount: " + getString(R.string.money_sign) + change + spacing;
                }

                if (willTax) {                                                                    //makes the tax part of the text
                    text1 += "Tax: " + getString(R.string.money_sign) + shared.getString("taxAmount", "") + spacing;
                }

                text1 += "Final cost: " + getString(R.string.money_sign) + shared.getString("total", "");//makes the cost total part of the text

                if (willSplit && didEnterSplit(false) && willSplitTip) {                          //if split was clicked, number is entered, and split tip is checked in prefs
                    text2 = "Each tips: " + getString(R.string.money_sign) + eachTip;             //adds the split tip part of the text
                }

                if (willSplit && didEnterSplit(false) && willSplitTotal) {                        //if split was clicked, number is entered, and split total is checked in prefs
                    text2 += spacing + "Each pays: " + getString(R.string.money_sign) + eachTotal;//adds the split tip part of the text
                }
            }

            setLandscapeResults();
        }
    }

    /**
     * Checks for no input first
     * Adds the percent
     * Used for tip
     */
    public void add(View view) {
        willSplit = false;
        if (didEnterValues()) {
            editor.putString("button", "add");
            editor.putString("lastAction", "tip");
            processValues();
            makeText();
        }
    }

    /**
     * Checks for no input first
     * Subtracts the percent
     * Used for discount
     */
    public void subtract(View view) {
        willSplit = false;
        if (didEnterValues()) {
            editor.putString("button", "subtract");
            editor.putString("lastAction", "discount");
            processValues();
            makeText();
        }
    }

    /**
     * Checks for no input first
     * Splits the tip
     */
    public void split(View view) {
        willSplit = true;
        if (didEnterSplit(true) && didEnterValues()) {
            editor.putString("button", "add");
            editor.putString("lastAction", "split");
            processValues();
            makeText();
        }
    }

    /**
     * Sets the text in results
     * Makes the text visible
     * Centers the text
     *
     * While the warning says String.format is not needed, it is
     */
    private void setResults() {
        resultsText.setText(String.format(text));
        resultsText.setVisibility(View.VISIBLE);                                                  //shows results text
        resultsText.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    /**
     * Landscape variation of the results
     * Sets the text in results
     * Makes the text visible
     * Centers the text
     *
     * While the warning says String.format is not needed, it is
     */
    private void setLandscapeResults() {
        resultsText1.setText(String.format(text1));
        resultsText1.setVisibility(View.VISIBLE);                                                 //shows results text #1
        resultsText1.setGravity(Gravity.CENTER_HORIZONTAL);

        resultsText2.setText(String.format(text2));
        resultsText2.setVisibility(View.VISIBLE);                                                 //shows results text #2
        resultsText2.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    /**
     * Checks to make sure all the info is entered
     * Updates the results as the numbers are changed
     */
    private void updateResultsWithChanges() {
        if (fieldsAreCurrentlyFilled()) {                                                         //if the info is already filled in, updates the results
            didJustUpdate = true;

            switch (shared.getString("lastAction", "tip")) {                                       //performs last action, default = tip
                case "discount": subtract(findViewById(R.id.subtract)); break;
                case "split": split(findViewById(R.id.split)); break;
                default: add(findViewById(R.id.add)); break;
            }
        }
    }

    /**
     * Checks if the user entered the cost and percentage
     * @return true if it was entered, false if its unchanged
     */
    private boolean didEnterValues() {
        if (!costIsEntered()) {
            showToast("The cost has not been entered");
            costBox.requestFocus();
            return false;
        } else if (!percentIsEntered()) {                                                         //if the percent is untouched
            showToast("The percent has not been entered");
            return false;
        } else if (willSplit) {
            return didEnterSplit(false);
        }

        return true;
    }

    /**
     * Checks if they entered the split (if needed)
     * @param willToast If it should toast
     * @return true if entered, false and toast otherwise
     */
    private boolean didEnterSplit(boolean willToast) {
        if (splitIsEmpty()) {
            if (willToast) {
                showToast("The split has not been entered");
            }
            return false;
        } else if (splitIsOne()) {
            if (willToast) {
                showToast("One person cannot split the bill");
            }
            return false;
        }

        return true;
    }

    /**
     * Checks if all the necessary fields are filled
     * Used to check for blank fields before auto-updating results
     * @return true if everything is filled in
     */
    private boolean fieldsAreCurrentlyFilled() {
        if (!costIsEntered()) {                                                                   //false if cost is not entered
            return false;
        } else if (!percentIsEntered()) {                                                         //false if percent is not entered
            return false;
        }

        return true;                                                                              //if it passes the other checks, its good
    }

    /**
     * Checks if the cost was entered
     * Checks for the most common forms of 0
     * @return true if the cost was entered
     */
    private boolean costIsEntered() {
        return (!costBox.getText().toString().equals("") &&
                !costBox.getText().toString().equals("0") &&
                !costBox.getText().toString().equals("0.0") &&
                !costBox.getText().toString().equals("0.00") &&
                !costBox.getText().toString().equals("00.00"));
    }

    /**
     * Checks if the percent was entered
     * @return true if the percent was entered
     */
    private boolean percentIsEntered() {
        return (percent != 0 &&
               !percentage.getText().toString().equals("") &&
               !percentage.getText().toString().equals("0"));
    }

    /**
     * Checks if nothing was entered into the split
     * @return true if its empty
     */
    private boolean splitIsEmpty() {
        return (splitBox.getText().toString()).equals("");
    }

    /**
     * Checks if they only put 1 in the split
     * @return true if 1 was entered
     */
    private boolean splitIsOne() {
        return (splitBox.getText().toString()).equals("1");
    }

    /**
     * Updates the percent text as the bar changes
     * Updates the results if possible
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (didJustTypePercent) {
            didJustTypePercent = false;
        } else {
            imm.hideSoftInputFromWindow(splitBox.getWindowToken(), 0);                            //hides keyboard if not typing
        }

        percent = progress + percentStart;
        if (percent > percentMax) {
            percent = percentMax;
        }

        percentage.setText(String.valueOf(percent));
        updateResultsWithChanges();
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
    protected void applyTheme() {
        themeChoice = shared.getString("themeList", "Light");
        colorChoice = shared.getString("colorList", "Green");

        if (colorChoice.equals("Dynamic")) {
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
        } else {
            super.applyTheme();
        }
    }

    /**
     * Overridden to apply activity specific buttons
     * Lollipop gets ripple buttons
     * Others get regular buttons using setBackgroundDrawable
     * To prevent having to raise the min api
     */
    protected void adjustButtons() {
        if (colorChoice.equals("Dynamic")) {
            for (Button b : buttons) {
                if (isLollipop()) {
                    b.setBackgroundResource(R.drawable.ripple_green_button);
                } else {
                    b.setBackgroundResource(R.drawable.green_button);
                }
            }
        } else {
            super.adjustButtons();
        }
    }

    public void onResume() {
        super.onResume();

        if (needsToRestart) {
            needsToRestart = false;
            onRestart();
        }
    }
}
