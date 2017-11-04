package com.alderferstudios.percentcalculatorv2.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alderferstudios.percentcalculatorv2.R;

import java.util.ArrayList;

/**
 * Percent Calculator
 * The generic class for all Fragments in this app
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public abstract class BaseFragment extends Fragment {

    protected static SharedPreferences shared;
    protected static SharedPreferences.Editor editor;
    protected static Toast t;
    protected static Context c;
    protected String themeChoice, colorChoice;
    public ArrayList<Button> buttons = new ArrayList<>();   //Stores the buttons for restyling
    protected RelativeLayout layout;

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
        if (t == null)
            t = Toast.makeText(c, s, Toast.LENGTH_SHORT);
        else if (t.getView().isShown())
            t.setText(s);
        else
            t = Toast.makeText(c, s, Toast.LENGTH_SHORT);

        t.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        c = getContext();
        shared = PreferenceManager.getDefaultSharedPreferences(c);
        PreferenceManager.setDefaultValues(c, R.xml.prefs, false);                             //sets default values if the preferences have not yet been used
        editor = shared.edit();

        layout = (RelativeLayout) inflater.inflate(R.layout.activity_one_item, container, false);
        return layout;
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
                    case "Green": {
                        if (isLollipop()) {
                            b.setBackground(getResources().getDrawable(R.drawable.ripple_green_button));
                        } else {
                            b.setBackgroundDrawable(getResources().getDrawable(R.drawable.green_button));
                        }
                        break;
                    }
                    case "Orange": {
                        if (isLollipop()) {
                            b.setBackground(getResources().getDrawable(R.drawable.ripple_orange_button));
                        } else {
                            b.setBackgroundDrawable(getResources().getDrawable(R.drawable.orange_button));
                        }
                        break;
                    }
                    case "Red": {
                        if (isLollipop()) {
                            b.setBackground(getResources().getDrawable(R.drawable.ripple_red_button));
                        } else {
                            b.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
                        }
                        break;
                    }
                    case "Blue": {
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
    }

    /**
     * Checks if the device is in landscape
     *
     * @return true if in landscape
     */
    protected boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
