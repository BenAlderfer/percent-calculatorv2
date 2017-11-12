package com.alderferstudios.percentcalculatorv2.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.activity.BaseActivity
import com.alderferstudios.percentcalculatorv2.activity.OneItemActivity
import com.alderferstudios.percentcalculatorv2.util.PercentCalculator
import java.text.DecimalFormat

/**
 * Results screen
 */
class ResultsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        makeResults()
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_cost, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        /////////////////////////// finish this /////////////////////////////////////////
        /*switch (item.getItemId())
        {
            case R.id.settings:
                Intent settingsActivity = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;

            default:
                onBackPressed();
                return true;
        }*/
        return true
    }

    /**
     * Makes the results text
     * Puts all the values together with text
     */
    private fun makeResults() {
        var orientation = "portrait"
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = "landscape"
        }

        val pc = PercentCalculator(activity ?: OneItemActivity(), getString(R.string.money_separator))    //TODO: decide if OneItemActivity is correct here
        pc.calculate()    //calculates the numbers

        /**
         * Construction of text section
         */
        val pattern = "0" + getString(R.string.money_separator) + "00"
        val decimalFormat = DecimalFormat(pattern)    //formats numbers to 2 decimals

        val cost = decimalFormat.format(java.lang.Double.parseDouble((activity as BaseActivity).shared?.getString("cost", "0.00")))
        val subtotal = decimalFormat.format(java.lang.Double.parseDouble((activity as BaseActivity).shared?.getString("subtotal", "0.00")))

        var didAdd = false
        if ((activity as BaseActivity).shared?.getString("button", null) != null) {
            didAdd = (activity as BaseActivity).shared?.getString("button", null).equals("add")
        }
        val total = (activity as BaseActivity).shared?.getString("total", "")

        /**
         * Header Section
         */
        val totalText = activity?.findViewById<TextView>(R.id.total)
        var spacing: String
        spacing = if (orientation == "portrait") {
            String.format("%n")
        } else {    //(orientation.equals("landscape"))
            String.format("%n%n")    //extra space in landscape
        }

        val change = (activity as BaseActivity).shared?.getString("percentAmount", "")
        val eachTip = (activity as BaseActivity).shared?.getString("eachTip", "")

        val didSplit = (activity as BaseActivity).shared?.getBoolean("didSplit", false)

        val willSplitTip: Boolean
        val willSplitTotal: Boolean
        when ((activity as BaseActivity).shared?.getString("splitList", "Split tip")) {
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

        if (didAdd) {
            if (didSplit == true && willSplitTip) {    //if they split and chose to split the tip, tip amount + split cost
                totalText?.text = String.format("Tip: " + getString(R.string.money_sign) + change + spacing +
                        getString(R.string.money_sign) + eachTip + " each")

            } else {    //tip, tip amount + final cost
                totalText?.text = String.format("Tip: " + getString(R.string.money_sign) + change + spacing +
                        "Final cost: " + getString(R.string.money_sign) + total)
            }

        } else {    //discount, discount amount + final cost
            totalText?.text = String.format("Discount: " + getString(R.string.money_sign) + change + spacing +
                    "Final cost: " + getString(R.string.money_sign) + total)
        }

        totalText?.gravity = Gravity.CENTER

        /**
         * Body Section
         */
        val resultsText = activity?.findViewById<TextView>(R.id.results)    //where results are displayed
        spacing = String.format("%n")
        val willTaxFirst = (activity as BaseActivity).shared?.getBoolean("afterBox", false)    //if the tax will be added before or after the tip is calculated
        val willTax = (activity as BaseActivity).shared?.getBoolean("taxBox", false)    //if the tax will be added
        val percent = (activity as BaseActivity).shared?.getInt("percent", 0)
        val taxAmount = (activity as BaseActivity).shared?.getString("taxAmount", "")
        val eachTotal = (activity as BaseActivity).shared?.getString("eachTotal", "")

        var results = String.format("The original cost is: " + getString(R.string.money_sign) + cost + spacing)    //adds cost

        if (didAdd) {
            if (willTax == true && willTaxFirst == true) {
                results += String.format("The tax is: " + getString(R.string.money_sign) + taxAmount + spacing +    //adds the tax first if needed

                        "The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing)    //adds the subtotal
            }

            results += String.format("The tip percent is: " + percent + "%%" + spacing +    //adds tip percent

                    "The tip is: " + getString(R.string.money_sign) + change + spacing)    //adds tip amount $

            if (didSplit == true && willSplitTip) {    //if they split and chose to split the tip
                results += String.format("Each person tips: " +
                        getString(R.string.money_sign) + eachTip + spacing)    //add line for each person tips
            }

            if (willTax == true && willTaxFirst == false) {
                results += String.format("The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing +    //adds the subtotal

                        "The tax is: " + getString(R.string.money_sign) + taxAmount + spacing)    //adds the tax afterwards if needed
            }
        } else {
            if (willTax == true && willTaxFirst == true) {
                results += String.format("The tax is: " + getString(R.string.money_sign) + taxAmount + spacing +    //adds the tax first if needed

                        "The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing)    //adds the subtotal
            }

            results += String.format("The discount percent is: " + percent + "%%" + spacing +    //adds discount percent

                    "The discount is: " + getString(R.string.money_sign) + change + spacing)    //adds discount amount $

            if (willTax == true && willTaxFirst == false) {
                results += String.format("The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing +    //adds the subtotal

                        "The tax is: " + getString(R.string.money_sign) + taxAmount + spacing)    //adds the tax afterwards if needed
            }

        }

        results += String.format("The final cost is: " + getString(R.string.money_sign) + total)    //adds line for total

        if (didSplit == true && willSplitTotal) {    //if they split and chose to split the total
            results += String.format(spacing + "Each person pays: " + getString(R.string.money_sign) + eachTotal)    //add line for each person pays (of total)
        }

        resultsText?.text = results
        resultsText?.gravity = Gravity.CENTER
    }
}