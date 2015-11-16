package com.alderferstudios.percentcalculatorv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;

/**
 * Percent Calculator
 * The internal calculator
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class PercentCalculator
{
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private double taxNum;
    private String moneySeparator;

    /**
     * Constructs a Percent Calculator
     * Initializes the shared preference object and editor
     * @param c the Context of where to get the preferences
     * @param separator the money separator, must be given from an ActionBarActivity
     */
    public PercentCalculator(Context c, String separator)
    {
        shared = PreferenceManager.getDefaultSharedPreferences(c);
        editor = shared.edit();
        moneySeparator = separator;
    }

    /**
     * Rounds the value
     * @param value the thing to round
     * @return the rounded value
     */
    public static double round(double value)
    {
        return Math.ceil(value * 100) / 100;
    }

    /**
     * Calculates and saves the numbers formatted as Strings
     */
    protected void calculate()
    {
        double costNum = PreferenceDoubles.getDouble(shared, "cost", 0.00);
        int percent = shared.getInt("percent", 0);
        String button = shared.getString("button", null);
        int split = shared.getInt("split", 1);
        double totalNum = costNum;

        boolean willTaxFirst = shared.getBoolean("afterBox", false);                              //if the tax will be added before or after the tip is calculated
        boolean willTax = shared.getBoolean("taxBox", false);                                     //if the tax will be added

        if (willTax && willTaxFirst)                                                              //if will tax and tax comes before the tip, NOT the standard
        {
            totalNum += getTax(costNum);
            PreferenceDoubles.putDouble(editor, "subtotal", totalNum);
        }

        double percentNum = round(totalNum * (percent / 100.0));

        if (button != null && button.equals("add"))
        {
            totalNum = round(totalNum + percentNum);

            if (willTax && !willTaxFirst)                                                         //if will tax and tax comes after the tip
            {
                PreferenceDoubles.putDouble(editor, "subtotal", totalNum);
                totalNum += getTax(costNum);                                                      //only original cost is taxed, tip is not taxed
            }
        }

        else	//button.equals("subtract")
        {
            totalNum = round(totalNum - percentNum);

            if (willTax && !willTaxFirst)                                                         //if will tax and tax comes after the discount
            {
                PreferenceDoubles.putDouble(editor, "subtotal", totalNum);
                totalNum += getTax(totalNum);                                                     //tax is applied to discounted cost, not original
            }
        }



        String pattern = "0" + moneySeparator + "00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        String percentAmount = decimalFormat.format(percentNum);
        String total = decimalFormat.format(totalNum);

        String eachTip = percentAmount;                                                           //if only 1 person, they pay all tip
        String eachTotal = total;                                                                 //if only 1 person, they pay all total
        boolean didSplit = shared.getBoolean("didSplit", false);
        boolean willSplitTip, willSplitTotal;
        switch (shared.getString("splitList", "Split tip"))
        {
            case "Split total": willSplitTip = false; willSplitTotal = true; break;
            case "Split both": willSplitTip = true; willSplitTotal = true; break;
            default: willSplitTip = true; willSplitTotal = false; break;
        }
        if (didSplit)
        {
            if (willSplitTip)                                                                     //if they chose to split the tip
                eachTip = decimalFormat.format(round(percentNum / split));                        //figures out the tip for each person
            if (willSplitTotal)                                                                   //if they chose to split the total
                eachTotal = decimalFormat.format(round(totalNum / split));                        //figures out the total for each person
        }

        editor.putString("percentAmount", percentAmount);
        editor.putString("taxAmount", decimalFormat.format(taxNum));
        editor.putString("percentAmount", percentAmount);
        editor.putString("taxAmount", decimalFormat.format(taxNum));
        editor.putString("total", total);
        editor.putString("eachTip", eachTip);
        editor.putString("eachTotal", eachTotal);

        editor.apply();                                                                           //finalizes changes
    }

    /**
     * Calculates how much the tax will be
     * Adds it to the total
     * @param price the price of the item
     * @return the total + the tax
     */
    private double getTax(double price)
    {
        double tax = Double.parseDouble(shared.getString("taxInput", "6.00"));
        taxNum = (tax / 100.0) * price;

        return taxNum;
    }
}