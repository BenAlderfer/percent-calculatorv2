package com.alderferstudios.percentcalculatorv2.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.TextView
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.activity.BaseActivity
import com.alderferstudios.percentcalculatorv2.util.CalcFields
import com.alderferstudios.percentcalculatorv2.util.PercentCalculator
import com.alderferstudios.percentcalculatorv2.util.PrefConstants
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

    /**
     * Makes the results text
     * Puts all the values together with text
     */
    private fun makeResults() {
        var orientation = "portrait"
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = "landscape"
        }

        val pc = PercentCalculator(getBaseActivity())
        val calcFields = CalcFields(getBaseActivity().cost, getBaseActivity().percent, getBaseActivity().split, "")    //TODO: add button string
        val results = pc.calculate(calcFields, getString(R.string.money_separator))

        /**
         * Construction of text section
         */
        val pattern = "0" + getString(R.string.money_separator) + "00"
        val decimalFormat = DecimalFormat(pattern)    //formats numbers to 2 decimals

        val cost = decimalFormat.format(getBaseActivity().cost)
        val subtotal = decimalFormat.format(results.subtotal)

        var didAdd = false
        if ((activity as BaseActivity).shared?.getString(PrefConstants.BUTTON, null) != null) {
            didAdd = (activity as BaseActivity).shared?.getString(PrefConstants.BUTTON, null).equals("add")
        }

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

        val didSplit = (activity as BaseActivity).shared?.getBoolean(PrefConstants.DID_SPLIT, false)

        val willSplitTip: Boolean
        val willSplitTotal: Boolean
        when ((activity as BaseActivity).shared?.getString(PrefConstants.SPLIT_LIST, PrefConstants.SPLIT_TIP)) {
            PrefConstants.SPLIT_TIP -> {
                willSplitTip = true
                willSplitTotal = false
            }
            PrefConstants.SPLIT_TOTAL -> {
                willSplitTip = false
                willSplitTotal = true
            }
            else -> {   //split both
                willSplitTip = true
                willSplitTotal = true
            }
        }

        if (didAdd) {
            if (didSplit == true && willSplitTip) {    //if they split and chose to split the tip, tip amount + split cost
                totalText?.text = String.format("Tip: " + getString(R.string.money_sign) + results.percent + spacing +
                        getString(R.string.money_sign) + results.eachTip + " each")

            } else {    //tip, tip amount + final cost
                totalText?.text = String.format("Tip: " + getString(R.string.money_sign) + results.percent + spacing +
                        "Final cost: " + getString(R.string.money_sign) + results.total)
            }

        } else {    //discount, discount amount + final cost
            totalText?.text = String.format("Discount: " + getString(R.string.money_sign) + results.percent + spacing +
                    "Final cost: " + getString(R.string.money_sign) + results.total)
        }

        /**
         * Body Section
         */
        val resultsView = activity?.findViewById<TextView>(R.id.results)    //where results are displayed
        spacing = String.format("%n")
        val willTaxFirst = (activity as BaseActivity).shared?.getBoolean(PrefConstants.AFTER_BOX, false)    //if the tax will be added before or after the tip is calculated
        val willTax = (activity as BaseActivity).shared?.getBoolean(PrefConstants.TAX_BOX, false)    //if the tax will be added

        var resultsText = String.format("The original cost is: " + getString(R.string.money_sign) + cost + spacing)    //adds cost

        if (didAdd) {
            if (willTax == true && willTaxFirst == true) {
                resultsText += String.format("The tax is: " + getString(R.string.money_sign) + results.taxAmount + spacing +    //adds the tax first if needed

                        "The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing)    //adds the subtotal
            }

            resultsText += String.format("The tip percent is: " + getBaseActivity().percent + "%%" + spacing +    //adds tip percent

                    "The tip is: " + getString(R.string.money_sign) + results.percent + spacing)    //adds tip amount $

            if (didSplit == true && willSplitTip) {    //if they split and chose to split the tip
                resultsText += String.format("Each person tips: " +
                        getString(R.string.money_sign) + results.eachTip + spacing)    //add line for each person tips
            }

            if (willTax == true && willTaxFirst == false) {
                resultsText += String.format("The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing +    //adds the subtotal

                        "The tax is: " + getString(R.string.money_sign) + results.taxAmount + spacing)    //adds the tax afterwards if needed
            }
        } else {
            if (willTax == true && willTaxFirst == true) {
                resultsText += String.format("The tax is: " + getString(R.string.money_sign) + results.taxAmount + spacing +    //adds the tax first if needed

                        "The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing)    //adds the subtotal
            }

            resultsText += String.format("The discount percent is: " + getBaseActivity().percent + "%%" + spacing +    //adds discount percent

                    "The discount is: " + getString(R.string.money_sign) + results.percent + spacing)    //adds discount amount $

            if (willTax == true && willTaxFirst == false) {
                resultsText += String.format("The subtotal is: " + getString(R.string.money_sign) + subtotal + spacing +    //adds the subtotal

                        "The tax is: " + getString(R.string.money_sign) + results.taxAmount + spacing)    //adds the tax afterwards if needed
            }

        }

        resultsText += String.format("The final cost is: " + getString(R.string.money_sign) + results.total)    //adds line for total

        if (didSplit == true && willSplitTotal) {    //if they split and chose to split the total
            resultsText += String.format(spacing + "Each person pays: " + getString(R.string.money_sign) + results.eachTotal)    //add line for each person pays (of total)
        }

        resultsView?.text = resultsText
    }

    /**
     * Nothing to validate here
     */
    override fun fieldsAreValid(): Boolean = true

    /**
     * No error message for this
     */
    override fun showErrorMessage() {}
}