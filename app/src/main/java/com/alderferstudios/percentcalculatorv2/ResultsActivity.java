/**
 * @author Ben Alderfer
 * Alderfer Studios
 * Percent Calculator
 */

package com.alderferstudios.percentcalculatorv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ResultsActivity extends PCActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private SharedPreferences shared;
    private boolean needsToRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        shared = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);                             //sets default values if the preferences have not yet been used

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shared.registerOnSharedPreferenceChangeListener(this);

        findViewById(R.id.resultsView).setOnTouchListener(new OnSwipeTouchListener(this)          //swipe listeners for activity movement
        {
            public void onSwipeBottom()
            {
                onBackPressed();
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        makeResults();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_results, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent settingsActivity = new Intent(this, PrefsActivity.class);
                settingsActivity.putExtra("caller", "results");
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
     * Restarts the activity if one of the limits was changed
     * @param sharedPreferences the shared prefs
     * @param key the name of the pref
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals("splitList"))
            needsToRestart = true;
    }

    /**
     * Makes the results text
     * Puts all the values together with text
     */
    private void makeResults()
    {
        String layout = "portrait";
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            layout = "landscape";

        PercentCalculator pc = new PercentCalculator(this, getString(R.string.money_separator));
        pc.calculate();                                                                           //calculates the numbers

        /**
         * Construction of text section
         */
        String pattern = "0" + getString(R.string.money_separator) + "00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);                                 //formats numbers to 2 decimals

        String cost = decimalFormat.format(PreferenceDoubles.getDouble(shared, "cost", 0.00));
        String subtotal = decimalFormat.format(PreferenceDoubles.getDouble(shared, "subtotal", 0.00));

        boolean didAdd = (shared.getString("button", null)).equals("add");
        String total = shared.getString("total", "");

        /**
         * Header Section
         */
        TextView totalText = (TextView) findViewById(R.id.total);
        String spacing;
        if (layout.equals("portrait"))
            spacing = String.format("%n");
        else//(layout.equals("landscape"))
            spacing = String.format("%n%n");                                                      //extra space in landscape

        String change = shared.getString("percentAmount", "");
        String eachTip = shared.getString("eachTip", "");
        if (eachTip.equals("0.00")){
            eachTip = "<" + getString(R.string.money_sign) + "0.00";
        }
        else{
            eachTip = getString(R.string.money_sign) + "0.00";
        }

        boolean didSplit = shared.getBoolean("didSplit", false);

        boolean willSplitTip, willSplitTotal;
        switch (shared.getString("splitList", "Split tip"))
        {
            case "Split total": willSplitTip = false; willSplitTotal = true; break;
            case "Split both": willSplitTip = true; willSplitTotal = true; break;
            default: willSplitTip = true; willSplitTotal = false; break;
        }

        if (didAdd)
            if (didSplit && willSplitTip)                              //if they split and chose to split the tip, tip amount + split cost
                totalText.setText(String.format("Tip: " + getString(R.string.money_sign) + change + spacing +
                                                 eachTip + " each"));

            else                                                                                  //tip, tip amount + final cost
                totalText.setText(String.format("Tip: " + getString(R.string.money_sign) + change + spacing +
                                                "Final cost: " + getString(R.string.money_sign) + total));

        else                                                                                      //discount, discount amount + final cost
            totalText.setText(String.format("Discount: " + getString(R.string.money_sign) + change + spacing +
                                            "Final cost: " + getString(R.string.money_sign) + total));

        totalText.setGravity(Gravity.CENTER);

        /**
         * Body Section
         */
        TextView resultsText = (TextView) findViewById(R.id.results);				    	      //where results are displayed
        spacing = String.format("%n");
        boolean willTaxFirst = shared.getBoolean("afterBox", false);                              //if the tax will be added before or after the tip is calculated
        boolean willTax = shared.getBoolean("taxBox", false);                                     //if the tax will be added
        int percent = shared.getInt("percent", 0);
        String taxAmount = shared.getString("taxAmount", "");
        String eachTotal = shared.getString("eachTotal", "");

        String results = String.format("The original cost is: " + getString(R.string.money_sign) + cost + spacing); //adds cost

        if (didAdd)
        {
            if (willTax && willTaxFirst)
                results += String.format("The tax is: " + getString(R.string.money_sign) + taxAmount + spacing +             //adds the tax first if needed
                                         "The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing);         //adds the subtotal

            results += String.format("The tip percent is: " + percent + "%%" + spacing +          //adds tip percent
                                     "The tip is: " + getString(R.string.money_sign) + change + spacing);                    //adds tip amount $

            if (didSplit && willSplitTip)                                                         //if they split and chose to split the tip
                    results += String.format("Each person tips: " + eachTip + spacing);     //add line for each person tips

            if (willTax && !willTaxFirst)
                results += String.format("The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing +         //adds the subtotal
                                         "The tax is: " + getString(R.string.money_sign) + taxAmount + spacing);             //adds the tax afterwards if needed
        }
        else
        {
            if (willTax && willTaxFirst)
                results += String.format("The tax is: " + getString(R.string.money_sign) + taxAmount + spacing +             //adds the tax first if needed
                                        "The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing);          //adds the subtotal

            results += String.format("The discount percent is: " + percent + "%%" + spacing +     //adds discount percent
                                     "The discount is: " + getString(R.string.money_sign) + change + spacing);               //adds discount amount $

            if (willTax && !willTaxFirst)
                results += String.format("The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing +         //adds the subtotal
                                         "The tax is: " + getString(R.string.money_sign) + taxAmount + spacing);             //adds the tax afterwards if needed

        }

        results += String.format("The final cost is: " + getString(R.string.money_sign) + total);			                  //adds line for total

        if (didSplit && willSplitTotal)                                                           //if they split and chose to split the total
            results += String.format(spacing + "Each person pays: " + getString(R.string.money_sign) + eachTotal);           //add line for each person pays (of total)

        resultsText.setText(results);
        resultsText.setGravity(Gravity.CENTER);
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
                case "Dark": setTheme(R.style.BlueDark); break;
                case "Black and White": setTheme(R.style.BlackAndWhite); break;
                default: setTheme(R.style.BlueLight); break;
            }
        else
            super.applyTheme();

    }

    public void onResume()
    {
        super.onResume();

        if (needsToRestart)
        {
            needsToRestart = false;
            onRestart();
        }
    }
}