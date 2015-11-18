package com.alderferstudios.percentcalculatorv2;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Percent Calculator
 * The generic class for all Activities in this app
 *
 * Alderfer Studios
 * @author Ben Alderfer
 */
public abstract class PCActivity extends AppCompatActivity {

    protected static SharedPreferences shared;
    protected static SharedPreferences.Editor editor;
    protected static Toast t;
    private static Context c;
    protected String themeChoice, colorChoice;
    protected ArrayList<Button> buttons = new ArrayList<>();                                      //Stores the buttons for restyling

    /**
     * Checks if the current api is Lollipop or greater
     *
     * @return true if is Lollipop
     */
    protected static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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
    protected static void showToast(String s) {
        if (t == null) {
            t = Toast.makeText(c, s, Toast.LENGTH_SHORT);
        } else if (t.getView().isShown()) {
            t.setText(s);
        } else {
            t = Toast.makeText(c, s, Toast.LENGTH_SHORT);
        }

        t.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shared = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);                             //sets default values if the preferences have not yet been used
        editor = shared.edit();

        applyTheme();                                                                             //sets the theme based on the preference
        changeLollipopIcon();                                                                     //changes the Lollipop overview icon

        super.onCreate(savedInstanceState);
        if ((shared.getString("screenSize", "phone")).equals("phone")) {                          //lock orientation if its a phone
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        c = this;
    }

    /**
     * Applies the correct colors based on the theme
     * Makes some changes on Lollipop
     */
    protected void applyTheme() {
        /**
         * This section assumes they chose a color
         * Will be overridden in class if not
         */
        switch (themeChoice) {
            case "Black and White": setTheme(R.style.BlackAndWhite); break;                               //everything looks the same in Black and White

            case "Dark":
                switch (colorChoice) {
                    case "Green": setTheme(R.style.GreenDark); break;
                    case "Orange": setTheme(R.style.OrangeDark); break;
                    case "Red": setTheme(R.style.RedDark); break;
                    case "Blue": setTheme(R.style.BlueDark); break;
                }
                break;

            default:
                switch (colorChoice) {
                    case "Green": setTheme(R.style.GreenLight); break;
                    case "Orange": setTheme(R.style.OrangeLight); break;
                    case "Red": setTheme(R.style.RedLight); break;
                    case "Blue": setTheme(R.style.BlueLight); break;
                }
                break;
        }
    }

    /**
     * Restarts the activity when it gets back from settings
     */
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    /**
     * Changes the overview icon in Lollipop
     * **Must be called from child class to work
     */
    protected void changeLollipopIcon() {
        if (isLollipop()) {                                                                       //sets an alternate icon for the overview (recent apps)
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.overview_icon);
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(null, bm, color);

            setTaskDescription(td);
            bm.recycle();
        }
    }

    /**
     * Changes the button backgrounds based on color and api
     * Lollipop gets ripple buttons
     * Others get regular buttons using setBackgroundDrawable
     * To prevent having to raise the min api
     * Will be overridden if no color is chosen
     */
    protected void adjustButtons() {
        for (Button b : buttons) {
            if (themeChoice.equals("Black and White")) {
                if (isLollipop()) {
                   b.setBackground(getResources().getDrawable(R.drawable.ripple_black_button));
                } else {
                   b.setBackgroundDrawable(getResources().getDrawable(R.drawable.black_button));
                }
            } else {
               switch (colorChoice) {
                   case "Green":
                       if (isLollipop()) {
                           b.setBackground(getResources().getDrawable(R.drawable.ripple_green_button));
                       } else {
                           b.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
                       }
                       break;

                   case "Orange":
                       if (isLollipop()) {
                           b.setBackground(getResources().getDrawable(R.drawable.ripple_orange_button));
                       } else {
                           b.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_button));
                       }
                       break;

                   case "Red":
                       if (isLollipop()) {
                           b.setBackground(getResources().getDrawable(R.drawable.ripple_red_button));
                       } else {
                           b.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
                       }
                       break;

                   case "Blue":
                       if (isLollipop()) {
                           b.setBackground(getResources().getDrawable(R.drawable.ripple_blue_button));
                       } else {
                           b.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_button));
                       }
                       break;
               }
           }
       }
   }

    /**
     * Checks if the device is in landscape
     * @return true if in landscape
     */
    protected boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onBackPressed() {
        editor.putBoolean("didJustGoBack", true);                                                 //saves back action
        editor.apply();

        super.onBackPressed();
    }
}
