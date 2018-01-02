package com.alderferstudios.percentcalculatorv2.util

import android.content.Context
import android.preference.PreferenceManager
import java.text.DecimalFormat

/**
 * The internal calculator
 */
class PercentCalculator(c: Context) {

    private val shared = PreferenceManager.getDefaultSharedPreferences(c)

    /**
     * Calculates and saves the numbers formatted as Strings
     */
    fun calculate(fields: CalcFields, moneySeparator: String): CalcResults {
        val results = CalcResults()
        var taxNum = 0.00
        var total = fields.cost

        val willTaxFirst = shared.getBoolean("afterBox", false)    //if the tax will be added before or after the tip is calculated
        val willTax = shared.getBoolean("taxBox", false)    //if the tax will be added

        if (willTax && willTaxFirst) {    //tax comes before the tip, NOT the standard
            taxNum = getTax(fields.cost)
            total += taxNum
            results.subtotal = total
        }

        val percentNum = roundDouble(total * (fields.percent / 100.0))

        if (fields.button == MiscUtil.TIP) {
            total = roundDouble(total + percentNum)

            if (willTax && !willTaxFirst) { //tax comes after the tip
                results.subtotal = total
                taxNum = getTax(fields.cost)
                total += taxNum //only original cost is taxed, tip is not taxed
            }
        } else {    //discount
            total = roundDouble(total - percentNum)

            if (willTax && !willTaxFirst) { //if will tax and tax comes after the discount
                results.subtotal = total
                taxNum = getTax(total)
                total += taxNum    //tax is applied to discounted cost, not original
            }
        }

        val pattern = "0" + moneySeparator + "00"
        val decimalFormat = DecimalFormat(pattern)
        val percentAmount = decimalFormat.format(percentNum)

        results.taxAmount = decimalFormat.format(taxNum)
        results.total = decimalFormat.format(total)

        var eachTip = percentAmount //if only 1 person, they pay all tip
        var eachTotal = results.total   //if only 1 person, they pay all total
        val didSplit = shared.getBoolean("didSplit", false)
        val willSplitTip: Boolean
        val willSplitTotal: Boolean
        when (shared.getString(PrefKeys.SPLIT_LIST, "Split tip")) {
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
            if (willSplitTip) { //if they chose to split the tip
                eachTip = decimalFormat.format(roundDouble(percentNum / fields.split))   //figures out the tip for each person
            }
            if (willSplitTotal) {   //if they chose to split the total
                eachTotal = decimalFormat.format(roundDouble(total / fields.split))   //figures out the total for each person
            }
        }

        results.percent = percentAmount
        results.eachTip = eachTip
        results.eachTotal = eachTotal

        return results
    }

    /**
     * Calculates how much the tax will be
     * Adds it to the total
     * @param price the price of the item
     * @return the total + the tax
     */
    private fun getTax(price: Double): Double {
        val tax = shared.getString(PrefKeys.TAX_INPUT, "6.00").toDouble()
        return tax / 100.0 * price
    }

    companion object {
        /**
         * Rounds the value
         * @param value the thing to round
         * @return the rounded value
         */
        fun roundDouble(value: Double): Double = Math.ceil(value * 100) / 100
    }
}