package com.alderferstudios.percentcalculatorv2;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Percent Calculator
 * The results screen
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class ResultsFragment extends PCFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.activity_results, container, false);

        makeResults();

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cost, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /////////////////////////// finish this /////////////////////////////////////////
        /*switch (item.getItemId())
        {
            case R.id.settings:
                Intent settingsActivity = new Intent(getActivity(), PrefsActivity.class);
                startActivity(settingsActivity);
                return true;

            default:
                onBackPressed();
                return true;
        }*/
        return true;
    }

    /**
     * Makes the results text
     * Puts all the values together with text
     */
    private void makeResults() {
        String layout = "portrait";
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            layout = "landscape";

        PercentCalculator pc = new PercentCalculator(getActivity(), getString(R.string.money_separator));
        pc.calculate();                                                                           //calculates the numbers

        /**
         * Construction of text section
         */
        String pattern = "0" + getString(R.string.money_separator) + "00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);                                 //formats numbers to 2 decimals

        String cost = decimalFormat.format(PreferenceDoubles.getDouble(shared, "cost", 0.00));
        String subtotal = decimalFormat.format(PreferenceDoubles.getDouble(shared, "subtotal", 0.00));

        boolean didAdd = false;
        if (shared.getString("button", null) != null) {
            didAdd = (shared.getString("button", null)).equals("add");
        }
        String total = shared.getString("total", "");

        /**
         * Header Section
         */
        TextView totalText = (TextView) layout.findViewById(R.id.total);
        String spacing;
        if (layout.equals("portrait"))
            spacing = String.format("%n");
        else//(layout.equals("landscape"))
            spacing = String.format("%n%n");                                                      //extra space in landscape

        String change = shared.getString("percentAmount", "");
        String eachTip = shared.getString("eachTip", "");

        boolean didSplit = shared.getBoolean("didSplit", false);

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

        if (didAdd)
            if (didSplit && willSplitTip)                              //if they split and chose to split the tip, tip amount + split cost
                totalText.setText(String.format("Tip: " + getString(R.string.money_sign) + change + spacing +
                        getString(R.string.money_sign) + eachTip + " each"));

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
        TextView resultsText = (TextView) layout.findViewById(R.id.results);                              //where results are displayed
        spacing = String.format("%n");
        boolean willTaxFirst = shared.getBoolean("afterBox", false);                              //if the tax will be added before or after the tip is calculated
        boolean willTax = shared.getBoolean("taxBox", false);                                     //if the tax will be added
        int percent = shared.getInt("percent", 0);
        String taxAmount = shared.getString("taxAmount", "");
        String eachTotal = shared.getString("eachTotal", "");

        String results = String.format("The original cost is: " + getString(R.string.money_sign) + cost + spacing); //adds cost

        if (didAdd) {
            if (willTax && willTaxFirst)
                results += String.format("The tax is: " + getString(R.string.money_sign) + taxAmount + spacing +             //adds the tax first if needed
                        "The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing);         //adds the subtotal

            results += String.format("The tip percent is: " + percent + "%%" + spacing +          //adds tip percent
                    "The tip is: " + getString(R.string.money_sign) + change + spacing);                    //adds tip amount $

            if (didSplit && willSplitTip)                                                         //if they split and chose to split the tip
                results += String.format("Each person tips: " +
                        getString(R.string.money_sign) + eachTip + spacing);//add line for each person tips

            if (willTax && !willTaxFirst)
                results += String.format("The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing +         //adds the subtotal
                        "The tax is: " + getString(R.string.money_sign) + taxAmount + spacing);             //adds the tax afterwards if needed
        } else {
            if (willTax && willTaxFirst)
                results += String.format("The tax is: " + getString(R.string.money_sign) + taxAmount + spacing +             //adds the tax first if needed
                        "The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing);          //adds the subtotal

            results += String.format("The discount percent is: " + percent + "%%" + spacing +     //adds discount percent
                    "The discount is: " + getString(R.string.money_sign) + change + spacing);               //adds discount amount $

            if (willTax && !willTaxFirst)
                results += String.format("The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing +         //adds the subtotal
                        "The tax is: " + getString(R.string.money_sign) + taxAmount + spacing);             //adds the tax afterwards if needed

        }

        results += String.format("The final cost is: " + getString(R.string.money_sign) + total);                              //adds line for total

        if (didSplit && willSplitTotal)                                                           //if they split and chose to split the total
            results += String.format(spacing + "Each person pays: " + getString(R.string.money_sign) + eachTotal);           //add line for each person pays (of total)

        resultsText.setText(results);
        resultsText.setGravity(Gravity.CENTER);
    }

    @Override
    protected void performAction(Button b) {
        //no action needed
    }
}