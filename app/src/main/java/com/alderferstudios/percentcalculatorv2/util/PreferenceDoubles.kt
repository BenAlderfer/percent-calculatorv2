package com.alderferstudios.percentcalculatorv2.util

import android.content.SharedPreferences

/**
 * Utility class for putting and getting doubles in SharedPreferences
 */
object PreferenceDoubles {

    /**
     * Converts a Double to a Long and puts it into the editor
     * @param edit the editor
     * @param key the key for future reference
     * @param value the double to put
     * @return the editor with the Double added
     */
    fun putDouble(edit: SharedPreferences.Editor, key: String, value: Double): SharedPreferences.Editor {
        return edit.putLong(key, java.lang.Double.doubleToRawLongBits(value))
    }

    /**
     * Used to get the Double from a saved Long
     * @param prefs the preference object
     * @param key the key for future reference
     * @param defaultValue the defaultValue if nothing is there
     * @return the double value
     */
    fun getDouble(prefs: SharedPreferences?, key: String?, defaultValue: Double): Double {
        return if (prefs == null || key == null || !prefs.contains(key)) {
            defaultValue
        } else java.lang.Double.longBitsToDouble(prefs.getLong(key, 0))

    }
}
