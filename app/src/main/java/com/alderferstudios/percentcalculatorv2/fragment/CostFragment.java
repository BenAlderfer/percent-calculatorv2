package com.alderferstudios.percentcalculatorv2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.alderferstudios.percentcalculatorv2.R;
import com.alderferstudios.percentcalculatorv2.activity.PrefsActivity;
import com.alderferstudios.percentcalculatorv2.util.PreferenceDoubles;

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
        boolean didSave = false;
        try {
            didSave = shared.getBoolean("saveBox", false);
        } catch (NullPointerException e) {
            Log.e("failure", "failed to check if saved");
            e.printStackTrace();
        }
        if (didSave) {                                                                            //fills in last value if save is enabled
            EditText costBox = ((EditText) layout.findViewById(R.id.cost));
            costBox.setText(Double.toString(PreferenceDoubles.INSTANCE.getDouble(shared, "cost", 0.00)));
            costBox.setSelection(costBox.getText().length());                                     //puts focus at end of cost text
        }
    }
}