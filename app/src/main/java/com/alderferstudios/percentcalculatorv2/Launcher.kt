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
import com.alderferstudios.percentcalculatorv2.util.PrefConstants

/**
 * Launcher, determines which activity to launch and does it
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
        return context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK >=
                Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val shared = PreferenceManager.getDefaultSharedPreferences(this)
        editor = shared.edit()

        saveScreenSize()
        editor?.putBoolean(PrefConstants.DID_JUST_GO_BACK, false)    //used later to determine animations
        editor?.apply()

        val firstActivity = if (shared.getBoolean(PrefConstants.COMBINED_BOX, false)) {
            Intent(this, CombinedActivity::class.java)
        } else {
            Intent(this, OneItemActivity::class.java)
        }

        startActivity(firstActivity)
    }

    /**
     * Gets the screen size and saves it for later
     */
    private fun saveScreenSize() {
        editor?.putString(PrefConstants.SCREEN_SIZE,
                if (isTablet(this)) PrefConstants.TABLET
                else PrefConstants.PHONE)
    }
}
