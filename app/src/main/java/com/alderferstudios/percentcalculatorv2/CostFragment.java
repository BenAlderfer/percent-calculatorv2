package com.alderferstudios.percentcalculatorv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Percent Calculator
 * The cost screen
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
public class CostFragment extends PCFragment {
    private EditText costText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.activity_cost, container, false);
        buttons.add((Button) layout.findViewById(R.id.next));
        adjustButtons();

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
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsActivity = new Intent(getActivity(), PrefsActivity.class);
                startActivity(settingsActivity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Applies preference settings
     */
    private void applyPrefs() {
        if (shared.getBoolean("saveBox", false))                                                  //fills in last value if save is enabled
        {
            EditText costBox = ((EditText) layout.findViewById(R.id.cost));
            costBox.setText(Double.toString(PreferenceDoubles.getDouble(shared, "cost", 0.00)));
            costBox.setSelection(costBox.getText().length());                                     //puts focus at end of cost text
        }
    }

    /**
     * Advances
     *
     * @param b
     */
    @Override
    protected void performAction(Button b) {
        next(b);
    }

    /**
     * Checks for input
     * Saves data
     * Moves to percent activity
     *
     * @param view the View
     */
    public void next(View view) {
        costText = (EditText) layout.findViewById(R.id.cost);
        String costString = costText.getText().toString();
        if (containsAnError()) {
            showToast("The cost has not been entered");
            costText.requestFocus();
        } else {
            double cost = PercentCalculator.round(Double.parseDouble(costString));                  //saves the cost
            editor = PreferenceDoubles.putDouble(editor, "cost", cost);
            editor.putBoolean("didJustGoBack", false);                                            //clears back action and remakes editor for later use
            editor.apply();

            //////////////////////// implement new changes //////////////////////////////////////////////////////////////////////////////////////////////
           /* Intent percentActivity = new Intent(getActivity(), PercentFragment.class);	                  //moves to percent activity
            startActivity(percentActivity);*/
        }
    }

    /**
     * Overridden to apply activity specific button
     * Lollipop gets ripple button
     * Others get regular button using setBackgroundDrawable
     * To prevent having to raise the min api
     */
    protected void adjustButtons() {
        if (colorChoice.equals("Dynamic"))
            for (Button b : buttons) {
                if (isLollipop())
                    b.setBackgroundResource(R.drawable.ripple_green_button);
                else
                    b.setBackgroundResource(R.drawable.green_button);
            }
        else
            super.adjustButtons();
    }

    /**
     * Checks if the input contains an error
     *
     * @return true if there is an error; false otherwise
     */
    private boolean containsAnError() {
        String text = costText.getText().toString();

        return text.equals("") || text.equals("0");
    }
}