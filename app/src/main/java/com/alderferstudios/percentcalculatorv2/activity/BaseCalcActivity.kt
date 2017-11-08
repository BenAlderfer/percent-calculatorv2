package com.alderferstudios.percentcalculatorv2.activity

import android.widget.Button
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import java.util.*

abstract class BaseCalcActivity : BaseActivity() {

    protected var buttons = ArrayList<Button?>() //Stores the buttons for restyling

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
