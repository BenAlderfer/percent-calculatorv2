package com.alderferstudios.percentcalculatorv2.activity

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.widget.Button
import android.widget.Toast
import com.alderferstudios.percentcalculatorv2.R
import java.util.*

/**
 * The generic class for all Activities in this app
 */
abstract class PCActivity : AppCompatActivity() {
    protected var themeChoice: String? = null
    protected var colorChoice: String? = null
    protected var buttons = ArrayList<Button>() //Stores the buttons for restyling

    protected var shared: SharedPreferences? = null
    protected var editor: SharedPreferences.Editor? = null

    /**
     * Checks if the device is in landscape
     * @return true if in landscape
     */
    protected val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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

        c = this
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
     * **Must be called from child class to work
     */
    private fun changeLollipopIcon() {
        if (isLollipop()) {                                                                       //sets an alternate icon for the overview (recent apps)
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
                if (isLollipop()) {
                    b.background = getDrawable(R.drawable.ripple_black_button)
                } else {
                    b.setBackgroundResource(R.drawable.black_button)
                }
            } else {
                when (colorChoice) {
                    "Green" -> if (isLollipop()) {
                        b.background = getDrawable(R.drawable.ripple_green_button)
                    } else {
                        b.setBackgroundResource(R.drawable.green_button)
                    }

                    "Orange" -> if (isLollipop()) {
                        b.background = getDrawable(R.drawable.ripple_orange_button)
                    } else {
                        b.setBackgroundResource(R.drawable.orange_button)
                    }

                    "Red" -> if (isLollipop()) {
                        b.background = getDrawable(R.drawable.ripple_red_button)
                    } else {
                        b.setBackgroundResource(R.drawable.red_button)
                    }

                    "Blue" -> if (isLollipop()) {
                        b.background = getDrawable(R.drawable.ripple_blue_button)
                    } else {
                        b.setBackgroundResource(R.drawable.blue_button)
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

    protected fun isLollipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    companion object {

        protected var t: Toast? = null
        private var c: Context? = null

        /**
         * Displays a toast on the screen
         * Uses Toast t to save last toast
         * Checks if a toast is currently visible
         * If so it sets the new text
         * Else it makes the new text
         *
         * @param s the string to be toasted
         */
        fun showToast(s: String) {
            if (t == null) {
                t = Toast.makeText(c, s, Toast.LENGTH_SHORT)
            } else if (t?.view?.isShown == true) {
                t?.setText(s)
            } else {
                t = Toast.makeText(c, s, Toast.LENGTH_SHORT)
            }

            t?.show()
        }
    }
}
