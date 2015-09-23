package com.alderferstudios.percentcalculatorv2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Like PCActivity, but for preferences
 * The most generic preference class
 */
public abstract class PopUpPreference extends AppCompatActivity
{
    protected static SharedPreferences shared;
    protected String themeChoice, colorChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        shared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        themeChoice = shared.getString("themeList", "Light");
        colorChoice = shared.getString("colorList", "Dynamic");
        applyTheme();                                                                             //sets the theme based on the preference
    }

    /**
     * Applies the correct colors based on the theme
     */
    protected void applyTheme()
    {
        /**
         * This section assumes they chose a color
         * Will be overridden in class if not
         */
        switch (themeChoice)
        {
            case "Black and White": setTheme(R.style.BlackAndWhitePopUp); break;                  //everything looks the same in Black and White

            case "Dark":
            {
                switch (colorChoice)
                {
                    case "Green": setTheme(R.style.GreenDarkPopUp); break;
                    case "Orange": setTheme(R.style.OrangeDarkPopUp); break;
                    case "Red": setTheme(R.style.RedDarkPopUp); break;
                    case "Blue": setTheme(R.style.BlueDarkPopUp); break;
                }
            } break;

            default: //light
            {
                switch (colorChoice)
                {
                    case "Green": setTheme(R.style.GreenLightPopUp); break;
                    case "Orange": setTheme(R.style.OrangeLightPopUp); break;
                    case "Red": setTheme(R.style.RedLightPopUp); break;
                    case "Blue": setTheme(R.style.BlueLightPopUp); break;
                }
            } break;
        }
    }
}
