package com.alderferstudios.percentcalculatorv2.widget

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.PrefKeys

/**
 * Like BaseActivity, but for preferences
 */
abstract class BasePopUpPreference : AppCompatActivity() {
    private var themeChoice: String? = null
    private var colorChoice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shared = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        themeChoice = shared.getString(PrefKeys.THEME_LIST, "Light")
        colorChoice = shared.getString(PrefKeys.COLOR_LIST, "Dynamic")
        applyTheme()                                                                             //sets the theme based on the preference
    }

    /**
     * Applies the correct colors based on the theme
     */
    private fun applyTheme() {
        /**
         * This section assumes they chose a color
         * Will be overridden in class if not
         */
        when (themeChoice) {
            "Black and White" -> setTheme(R.style.BlackAndWhitePopUp)

            "Dark" -> when (colorChoice) {
                "Green" -> setTheme(R.style.GreenDarkPopUp)
                "Orange" -> setTheme(R.style.OrangeDarkPopUp)
                "Red" -> setTheme(R.style.RedDarkPopUp)
                "Blue" -> setTheme(R.style.BlueDarkPopUp)
            }

            else -> when (colorChoice) { //light
                "Green" -> setTheme(R.style.GreenLightPopUp)
                "Orange" -> setTheme(R.style.OrangeLightPopUp)
                "Red" -> setTheme(R.style.RedLightPopUp)
                "Blue" -> setTheme(R.style.BlueLightPopUp)
            }
        }
    }
}
