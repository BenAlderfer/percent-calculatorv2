package com.alderferstudios.percentcalculatorv2;

import android.content.SharedPreferences;

/**
 * Percent Calculator
 * Utility class for putting and getting doubles in SharedPreferences
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class PreferenceDoubles {

    /**
     * Converts a Double to a Long and puts it into the editor
     * @param edit the editor
     * @param key the key for future reference
     * @param value the double to put
     * @return the editor with the Double added
     */
    public static SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    /**
     * Used to get the Double from a saved Long
     * @param prefs the preference object
     * @param key the key for future reference
     * @param defaultValue the defaultValue if nothing is there
     * @return the double value
     */
    public static double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        if (!prefs.contains(key)) {
            return defaultValue;
        }

        return Double.longBitsToDouble(prefs.getLong(key, 0));
    }
}
