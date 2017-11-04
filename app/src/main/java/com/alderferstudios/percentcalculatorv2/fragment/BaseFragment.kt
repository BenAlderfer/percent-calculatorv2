package com.alderferstudios.percentcalculatorv2.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import java.util.*

/**
 * base class for all Fragments in this app
 */
abstract class BaseFragment : Fragment() {
    protected var themeChoice: String? = null
    protected var colorChoice: String? = null
    var buttons = ArrayList<Button?>()   //Stores the buttons for restyling

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.alderferstudios.percentcalculatorv2.R.layout.activity_one_item, container, false)
    }

    /**
     * Changes the button backgrounds based on color and api
     * Lollipop gets ripple buttons
     * Others get regular buttons using setBackgroundDrawable
     * To prevent having to raise the min api
     * Will be overridden if no color is chosen
     */
    protected fun adjustButtons() {
        for (b in buttons) {
            if (themeChoice == "Black and White") {
                if (MiscUtil.isLollipop()) {
                    b?.background = ContextCompat.getDrawable(activity as Context, R.drawable.ripple_black_button)
                } else {
                    b?.setBackgroundResource(R.drawable.black_button)
                }
            } else {
                when (colorChoice) {
                    "Green" -> {
                        if (MiscUtil.isLollipop()) {
                            b?.background = ContextCompat.getDrawable(activity as Context, R.drawable.ripple_green_button)
                        } else {
                            b?.setBackgroundResource(R.drawable.green_button)
                        }
                    }
                    "Orange" -> {
                        if (MiscUtil.isLollipop()) {
                            b?.background = ContextCompat.getDrawable(activity as Context, R.drawable.ripple_orange_button)
                        } else {
                            b?.setBackgroundResource(R.drawable.orange_button)
                        }
                    }
                    "Red" -> {
                        if (MiscUtil.isLollipop()) {
                            b?.background = ContextCompat.getDrawable(activity as Context, R.drawable.ripple_red_button)
                        } else {
                            b?.setBackgroundResource(R.drawable.red_button)
                        }
                    }
                    "Blue" -> {
                        if (MiscUtil.isLollipop()) {
                            b?.background = ContextCompat.getDrawable(activity as Context, R.drawable.ripple_blue_button)
                        } else {
                            b?.setBackgroundResource(R.drawable.blue_button)
                        }
                    }
                }
            }
        }
    }
}