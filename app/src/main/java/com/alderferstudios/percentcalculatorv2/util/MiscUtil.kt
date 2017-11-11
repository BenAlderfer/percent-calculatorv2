package com.alderferstudios.percentcalculatorv2.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast

class MiscUtil {
    companion object {
        private var toast: Toast? = null

        /**
         * Checks if the current api is Lollipop or greater
         *
         * @return true if is Lollipop
         */
        fun isLollipop(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        }

        /**
         * Checks if the device is in landscape
         *
         * @return true if in landscape
         */
        fun isLandscape(c: Context): Boolean {
            return c.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }


        /**
         * Displays a toast on the screen
         * Uses Toast t to save last toast
         * Checks if a toast is currently visible
         * If so it sets the new text
         * Else it makes the new text
         *
         * @param s the string to be toasted
         */
        @SuppressLint("ShowToast")
        fun showToast(c: Context, s: String) {
            if (toast == null || toast?.view?.isShown == false) {
                toast = Toast.makeText(c, s, Toast.LENGTH_SHORT)
            } else {
                toast?.setText(s)
            }

            toast?.show()
        }
    }
}
