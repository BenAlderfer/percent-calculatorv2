package com.alderferstudios.percentcalculatorv2.activity

import android.view.View
import android.widget.Button
import android.widget.EditText
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import com.alderferstudios.percentcalculatorv2.util.PercentCalculator
import com.alderferstudios.percentcalculatorv2.util.PrefConstants
import java.util.*

/**
 * Base for all Activities that calculate (all but settings)
 */
abstract class BaseCalcActivity : BaseActivity() {

    val buttons = ArrayList<Button?>()    //Stores the buttons for restyling
    var cost = 0.00
    var percent = 0
    var split = 1

    /**
     * Checks for input
     * Saves data
     * Moves to percent activity
     *
     * @param v the View
     */
    open fun advanceFromCost(@Suppress("UNUSED_PARAMETER") v: View) {
        val costText = findViewById<View>(R.id.cost) as EditText
        if (containsAnError(costText)) {
            MiscUtil.showToast(this, getString(R.string.costError))
            costText.requestFocus()
        } else {
            cost = PercentCalculator.roundDouble(costText.text.toString().toDouble())
        }
    }

    /**
     * 
     *
     * @param v the View
     */
    open fun tip(@Suppress("UNUSED_PARAMETER") v: View) {
        //TODO: review
        editor?.putString("button", "add")
        editor?.putBoolean(PrefConstants.DID_SPLIT, false)
        editor?.putString(PrefConstants.LAST_ACTION, PrefConstants.TIP)
        editor?.apply()
    }

    /**
     *
     *
     * @param v the View
     */
    open fun discount(@Suppress("UNUSED_PARAMETER") v: View) {
        //TODO: review
        editor?.putString("button", "subtract")
        editor?.putBoolean(PrefConstants.DID_SPLIT, false)
        editor?.putString(PrefConstants.LAST_ACTION, PrefConstants.DISCOUNT)
        editor?.apply()
    }

    /**
     *
     *
     * @param v the View
     */
    open fun advanceToSplit(@Suppress("UNUSED_PARAMETER") v: View) {
        //TODO: review
        editor?.putString("button", "add")
        editor?.putBoolean(PrefConstants.DID_SPLIT, false)
        editor?.putString(PrefConstants.LAST_ACTION, PrefConstants.SPLIT)
        editor?.apply()
    }

    /**
     *
     *
     * @param v the View
     */
    open fun split(@Suppress("UNUSED_PARAMETER") v: View) {
        //TODO: review
        editor?.putBoolean(PrefConstants.DID_SPLIT, true)
        editor?.putBoolean(PrefConstants.DID_JUST_GO_BACK, false)
        editor?.apply()
    }

    /**
     * Checks if the input contains an error
     *
     * @return true if there is an error; false otherwise
     */
    protected fun containsAnError(et: EditText): Boolean {
        val text = et.text.toString()

        return text == "" || text.toDouble() == 0.00
    }
}
