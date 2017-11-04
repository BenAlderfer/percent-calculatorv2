package com.alderferstudios.percentcalculatorv2.util

import android.content.Context
import android.preference.PreferenceManager
import java.text.DecimalFormat

/**
 * The internal calculator
 */
class PercentCalculator
/**
 * Constructs a Percent Calculator
 * Initializes the shared preference object and editor
 * @param c the Context of where to get the preferences
 * @param moneySeparator the money separator, must be given from an ActionBarActivity
 */
(c: Context, private val moneySeparator: String) {

    private val shared = PreferenceManager.getDefaultSharedPreferences(c)
    private val editor = shared.edit()
    private var taxNum: Double = 0.toDouble()

    /**
     * Calculates and saves the numbers formatted as Strings
     */
    fun calculate() {
        val costNum = PreferenceDoubles.getDouble(shared, "cost", 0.00)
        val percent = shared.getInt("percent", 0)
        val button = shared.getString("button", null)
        val split = shared.getInt("split", 1)
        var totalNum = costNum

        val willTaxFirst = shared.getBoolean("afterBox", false)                              //if the tax will be added before or after the tip is calculated
        val willTax = shared.getBoolean("taxBox", false)                                     //if the tax will be added

        if (willTax && willTaxFirst) {                                                            //if will tax and tax comes before the tip, NOT the standard
            totalNum += getTax(costNum)
            PreferenceDoubles.putDouble(editor, "subtotal", totalNum)
        }

        val percentNum = round(totalNum * (percent / 100.0))

        if (button != null && button == "add") {
            totalNum = round(totalNum + percentNum)

            if (willTax && !willTaxFirst) {                                                       //if will tax and tax comes after the tip
                PreferenceDoubles.putDouble(editor, "subtotal", totalNum)
                totalNum += getTax(costNum)                                                      //only original cost is taxed, tip is not taxed
            }
        } else {    //button.equals("subtract")
            totalNum = round(totalNum - percentNum)

            if (willTax && !willTaxFirst) {                                                       //if will tax and tax comes after the discount
                PreferenceDoubles.putDouble(editor, "subtotal", totalNum)
                totalNum += getTax(totalNum)                                                     //tax is applied to discounted cost, not original
            }
        }


        val pattern = "0" + moneySeparator + "00"
        val decimalFormat = DecimalFormat(pattern)
        val percentAmount = decimalFormat.format(percentNum)
        val total = decimalFormat.format(totalNum)

        var eachTip = percentAmount                                                           //if only 1 person, they pay all tip
        var eachTotal = total                                                                 //if only 1 person, they pay all total
        val didSplit = shared.getBoolean("didSplit", false)
        val willSplitTip: Boolean
        val willSplitTotal: Boolean
        when (shared.getString("splitList", "Split tip")) {
            "Split total" -> {
                willSplitTip = false
                willSplitTotal = true
            }
            "Split both" -> {
                willSplitTip = true
                willSplitTotal = true
            }
            else -> {
                willSplitTip = true
                willSplitTotal = false
            }
        }
        if (didSplit) {
            if (willSplitTip) {                                                                   //if they chose to split the tip
                eachTip = decimalFormat.format(round(percentNum / split))                        //figures out the tip for each person
            }
            if (willSplitTotal) {                                                                 //if they chose to split the total
                eachTotal = decimalFormat.format(round(totalNum / split))                        //figures out the total for each person
            }
        }

        editor.putString("percentAmount", percentAmount)
        editor.putString("taxAmount", decimalFormat.format(taxNum))
        editor.putString("percentAmount", percentAmount)
        editor.putString("taxAmount", decimalFormat.format(taxNum))
        editor.putString("total", total)
        editor.putString("eachTip", eachTip)
        editor.putString("eachTotal", eachTotal)

        editor.apply()                                                                           //finalizes changes
    }

    /**
     * Calculates how much the tax will be
     * Adds it to the total
     * @param price the price of the item
     * @return the total + the tax
     */
    private fun getTax(price: Double): Double {
        val tax = java.lang.Double.parseDouble(shared.getString("taxInput", "6.00"))
        taxNum = tax / 100.0 * price

        return taxNum
    }

    companion object {

        /**
         * Rounds the value
         * @param value the thing to round
         * @return the rounded value
         */
        fun round(value: Double): Double {
            return Math.ceil(value * 100) / 100
        }
    }
}