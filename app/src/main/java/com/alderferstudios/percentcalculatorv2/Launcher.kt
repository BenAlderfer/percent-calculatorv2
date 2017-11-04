package com.alderferstudios.percentcalculatorv2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager

import com.alderferstudios.percentcalculatorv2.activity.CombinedActivity
import com.alderferstudios.percentcalculatorv2.activity.OneItemActivity

/**
 * launcher
 */
class Launcher : Activity() {

    private var editor: SharedPreferences.Editor? = null

    /**
     * Checks if the device is a tablet
     *
     * @param context the Context
     * @return true if a tablet
     */
    private fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val shared = PreferenceManager.getDefaultSharedPreferences(this)
        editor = shared.edit()

        saveScreenSize()
        editor?.putBoolean("didJustGoBack", false)  //used later to determine animations
        editor?.apply()

        val firstActivity: Intent
        if (shared.getBoolean("combinedBox", false)) {
            firstActivity = Intent(this, CombinedActivity::class.java)
        } else {
            firstActivity = Intent(this, OneItemActivity::class.java)
        }

        startActivity(firstActivity)
    }

    /**
     * Gets the screen size and saves it for later
     */
    private fun saveScreenSize() {
        val size: String
        if (isTablet(this)) {
            size = "tablet"
        } else {
            size = "phone"
        }

        editor?.putString("screenSize", size)
    }
}
