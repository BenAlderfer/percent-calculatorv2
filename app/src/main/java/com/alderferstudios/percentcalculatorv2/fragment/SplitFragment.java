package com.alderferstudios.percentcalculatorv2.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.alderferstudios.percentcalculatorv2.R;
import com.alderferstudios.percentcalculatorv2.widget.NumPicker;

/**
 * Percent Calculator
 * The split screen
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class SplitFragment extends BaseFragment {

    private NumPicker numPick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.activity_split, container, false);
        buttons.add((Button) layout.findViewById(R.id.add));

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
        boolean didSave = false;
        try {
            didSave = shared.getBoolean("saveBox", false);
        } catch (NullPointerException e) {
            Log.e("failure", "failed to check if saved");
            e.printStackTrace();
        }

        if (layout.findViewById(R.id.numPicker) != null) {                                        //fills in last or default value for split picker if it is there
            numPick = (NumPicker) layout.findViewById(R.id.numPicker);
            if (didSave) {                                                                        //fills in last value if save is enabled
                int split = 4;
                try {
                    split = shared.getInt("split", 4);
                } catch (NullPointerException e) {
                    Log.e("failure", "failed to get split");
                    e.printStackTrace();
                }
                if (split >= 2 && split <= 100) {                                                 //makes sure the number is in the correct range
                    numPick.setValue(shared.getInt("split", 4));
                } else {
                    numPick.setValue(4);
                }
            } else {                                                                              //default value is 4
                numPick.setValue(4);
            }
        }
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
}