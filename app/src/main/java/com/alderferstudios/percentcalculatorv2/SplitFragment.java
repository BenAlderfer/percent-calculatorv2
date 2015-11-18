package com.alderferstudios.percentcalculatorv2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Percent Calculator
 * The split screen
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class SplitFragment extends PCFragment {

    private NumPicker numPick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.activity_split, container, false);
        buttons.add((Button) layout.findViewById(R.id.add));
        adjustButtons();

        numPick = (NumPicker) layout.findViewById(R.id.numPicker);
        applyPrefs();

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cost, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /////////////////////////// finish this /////////////////////////////////////////
        /*switch (item.getItemId())
        {
            case R.id.settings:
                Intent settingsActivity = new Intent(getActivity(), PrefsActivity.class);
                startActivity(settingsActivity);
                return true;

            case R.id.split_options:
                Intent popUp = new Intent(this, SplitPopUp.class);
                startActivity(popUp);
                return true;

            default:
                onBackPressed();
                return true;
        }*/
        return true;
    }

    /**
     * Applies preference settings
     */
    private void applyPrefs() {
        if (shared.getBoolean("saveBox", false)) {                                                //fills in last value if save is enabled
            if (shared.getInt("split", 4) >= 2 && shared.getInt("split", 4) <= 100) {             //makes sure the number is in the correct range
                numPick.setValue(shared.getInt("split", 4));
            } else {
                numPick.setValue(4);
            }
        } else {                                              //default value is 4
            numPick.setValue(4);
        }
    }

    @Override
    protected void performAction(Button b) {
        add(b);
    }

    /**
     * Saves the value of the number picker
     * Switches to results
     *
     * @param View the View
     */
    public void add(View View) {
        editor.putInt("split", numPick.getValue());                                               //saves the value of the number picker
        editor.putBoolean("didSplit", true);                                                      //lets the results know that they want to split
        editor.putBoolean("didJustGoBack", false);                                                //clears back action and remakes editor for later use
        editor.apply();
        ////////////////////// implement new switch /////////////////////////////////////
        /*Intent results = new Intent(this, ResultsActivity.class);					              //switches to results
        startActivity(results);*/
    }

    /**
     * Overridden to apply activity specific button
     * Lollipop gets ripple button
     * Others get regular button using setBackgroundDrawable
     * To prevent having to raise the min api
     */
    protected void adjustButtons() {
        if (colorChoice.equals("Dynamic")) {
            for (Button b : buttons) {
                if (isLollipop()) {
                    b.setBackgroundResource(R.drawable.ripple_red_button);
                } else {
                    b.setBackgroundResource(R.drawable.red_button);
                }
            }
        } else {
            super.adjustButtons();
        }
    }
}