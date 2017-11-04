package com.alderferstudios.percentcalculatorv2.activity

import android.app.ActivityManager
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.widget.Button
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import java.util.*

/**
 * base class for all Activities in this app
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var themeChoice: String? = null
    protected var colorChoice: String? = null
    protected var buttons = ArrayList<Button?>() //Stores the buttons for restyling

    var shared: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        shared = PreferenceManager.getDefaultSharedPreferences(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)                             //sets default values if the preferences have not yet been used
        editor = shared?.edit()

        applyTheme()                                                                             //sets the theme based on the preference
        changeLollipopIcon()                                                                     //changes the Lollipop overview icon

        super.onCreate(savedInstanceState)
        if (shared?.getString("screenSize", "phone") == "phone") {                          //lock orientation if its a phone
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }

    /**
     * Applies the correct colors based on the theme
     * Makes some changes on Lollipop
     */
    protected open fun applyTheme() {
        /**
         * This section assumes they chose a color
         * Will be overridden in class if not
         */
        when (themeChoice) {
            "Black and White" -> setTheme(R.style.BlackAndWhite)

            "Dark" -> when (colorChoice) {
                "Green" -> setTheme(R.style.GreenDark)
                "Orange" -> setTheme(R.style.OrangeDark)
                "Red" -> setTheme(R.style.RedDark)
                "Blue" -> setTheme(R.style.BlueDark)
            }

            else -> when (colorChoice) {
                "Green" -> setTheme(R.style.GreenLight)
                "Orange" -> setTheme(R.style.OrangeLight)
                "Red" -> setTheme(R.style.RedLight)
                "Blue" -> setTheme(R.style.BlueLight)
            }
        }//everything looks the same in Black and White
    }

    /**
     * Restarts the activity when it gets back from settings
     */
    override fun onRestart() {
        super.onRestart()
        recreate()
    }

    /**
     * Changes the overview icon in Lollipop
     * Must be called from child class to work
     */
    private fun changeLollipopIcon() {
        if (MiscUtil.isLollipop()) {                                                                       //sets an alternate icon for the overview (recent apps)
            val typedValue = TypedValue()
            val theme = theme
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val color = typedValue.data

            val bm = BitmapFactory.decodeResource(resources, R.drawable.overview_icon)
            val td = ActivityManager.TaskDescription(null, bm, color)

            setTaskDescription(td)
            bm.recycle()
        }
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

    override fun onBackPressed() {
        editor?.putBoolean("didJustGoBack", true)                                                 //saves back action
        editor?.apply()

        super.onBackPressed()
    }
}
