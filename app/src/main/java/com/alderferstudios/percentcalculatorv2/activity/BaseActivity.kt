package com.alderferstudios.percentcalculatorv2.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.MiscUtil

/**
 * Base class for all Activities
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var themeChoice: String? = null
    protected var colorChoice: String? = null

    var shared: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shared = PreferenceManager.getDefaultSharedPreferences(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)    //sets default values if the preferences have not yet been used
        editor = shared?.edit()

        applyTheme()    //sets the theme based on the preference
        changeLollipopIcon()    //changes the Lollipop overview icon

        if (shared?.getString("screenSize", "phone") == "phone") {  //lock orientation if its a phone
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }

    /**
     * Restarts the activity when it gets back from settings
     */
    override fun onRestart() {
        super.onRestart()
        recreate()
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
        }
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

    override fun onBackPressed() {
        editor?.putBoolean("didJustGoBack", true)                                                 //saves back action
        editor?.apply()

        super.onBackPressed()
    }
}
