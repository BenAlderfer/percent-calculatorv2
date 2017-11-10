package com.alderferstudios.percentcalculatorv2.activity

import android.view.View
import android.widget.Button
import android.widget.EditText
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import com.alderferstudios.percentcalculatorv2.util.PercentCalculator
import java.util.*

abstract class BaseCalcActivity : BaseActivity() {

    val buttons = ArrayList<Button?>()    //Stores the buttons for restyling
    protected var cost = 0.00
    protected var percent = 0
    protected var split = 0

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
            MiscUtil.showToast(this, "The cost has not been entered")
            costText.requestFocus()
        } else {
            cost = PercentCalculator.round(java.lang.Double.parseDouble(costText.text.toString()))
        }
    }

    /**
     * When the add button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     *
     * @param v the View
     */
    open fun tip(@Suppress("UNUSED_PARAMETER") v: View) {
        editor?.putString("button", "add")
        editor?.putBoolean("didSplit", false)
        editor?.putString("lastAction", "tip")
    }

    /**
     * When the subtract button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     *
     * @param v the View
     */
    open fun discount(@Suppress("UNUSED_PARAMETER") v: View) {
        editor?.putString("button", "subtract")
        editor?.putBoolean("didSplit", false)
        editor?.putString("lastAction", "discount")
    }

    /**
     * When the split button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to split activity
     *
     * @param v the View
     */
    open fun switchToSplit(@Suppress("UNUSED_PARAMETER") v: View) {
        editor?.putString("button", "add")
        editor?.putBoolean("didSplit", false)
        editor?.putString("lastAction", "split")
    }

    /**
     * Saves the value of the number picker
     * Switches to results
     *
     * @param v the View
     */
    open fun split(@Suppress("UNUSED_PARAMETER") v: View) {
//        editor?.putInt("split", numPick?.value ?: 0) //saves the value of the number picker
        editor?.putBoolean("didSplit", true) //lets the results know that they want to split
        editor?.putBoolean("didJustGoBack", false)   //clears back action and remakes editor for later use
        editor?.apply()
        ////////////////////// implement new switch /////////////////////////////////////
        /*Intent results = new Intent(this, ResultsActivity.class);					              //switches to results
        startActivity(results);*/
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

    /**
     * Changes the button backgrounds based on color and api
     * Lollipop gets ripple buttons
     * Others get regular buttons using setBackgroundDrawable
     * To prevent having to raise the min api
     * Will be overridden if no color is chosen
     */
    protected open fun adjustButtons() {
        for (b in buttons) {
            if (themeChoice == "Black and White") {
                if (MiscUtil.isLollipop()) {
                    b?.background = getDrawable(R.drawable.ripple_black_button)
                } else {
                    b?.setBackgroundResource(R.drawable.black_button)
                }
            } else {
                when (colorChoice) {
                    "Green" -> if (MiscUtil.isLollipop()) {
                        b?.background = getDrawable(R.drawable.ripple_green_button)
                    } else {
                        b?.setBackgroundResource(R.drawable.green_button)
                    }

                    "Orange" -> if (MiscUtil.isLollipop()) {
                        b?.background = getDrawable(R.drawable.ripple_orange_button)
                    } else {
                        b?.setBackgroundResource(R.drawable.orange_button)
                    }

                    "Red" -> if (MiscUtil.isLollipop()) {
                        b?.background = getDrawable(R.drawable.ripple_red_button)
                    } else {
                        b?.setBackgroundResource(R.drawable.red_button)
                    }

                    "Blue" -> if (MiscUtil.isLollipop()) {
                        b?.background = getDrawable(R.drawable.ripple_blue_button)
                    } else {
                        b?.setBackgroundResource(R.drawable.blue_button)
                    }
                }
            }
        }
    }
}
