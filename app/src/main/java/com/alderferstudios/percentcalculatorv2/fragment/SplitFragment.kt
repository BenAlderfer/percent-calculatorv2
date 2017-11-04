package com.alderferstudios.percentcalculatorv2.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.activity.BaseActivity
import com.alderferstudios.percentcalculatorv2.widget.NumPicker

/**
 * split screen
 */
class SplitFragment : BaseFragment() {

    private var numPick: NumPicker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        buttons.add(activity?.findViewById(R.id.add))

        numPick = activity?.findViewById(R.id.numPicker)
        applyPrefs()

        return inflater.inflate(R.layout.activity_split, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_cost, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
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
        return true
    }

    /**
     * Applies preference settings
     */
    private fun applyPrefs() {
        var didSave = false
        try {
            didSave = (activity as BaseActivity).shared?.getBoolean("saveBox", false) == true
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to check if saved")
            e.printStackTrace()
        }

        if (activity?.findViewById<View>(R.id.numPicker) != null) { //fills in last or default value for split picker if it is there
            numPick = activity?.findViewById(R.id.numPicker)
            if (didSave) {  //fills in last value if save is enabled
                var split = 4
                try {
                    split = (activity as BaseActivity).shared?.getInt("split", 4) ?: 4
                } catch (e: NullPointerException) {
                    Log.e("failure", "failed to get split")
                    e.printStackTrace()
                }

                if (split in 2..100) {  //makes sure the number is in the correct range
                    numPick?.value = (activity as BaseActivity).shared?.getInt("split", 4) ?: 4
                } else {
                    numPick?.value = 4
                }
            } else {    //default value is 4
                numPick?.value = 4
            }
        }
    }

    /**
     * Saves the value of the number picker
     * Switches to results
     *
     * @param v the View
     */
    fun add(@Suppress("UNUSED_PARAMETER") v: View) {
        (activity as BaseActivity).editor?.putInt("split", numPick?.value ?: 0) //saves the value of the number picker
        (activity as BaseActivity).editor?.putBoolean("didSplit", true) //lets the results know that they want to split
        (activity as BaseActivity).editor?.putBoolean("didJustGoBack", false)   //clears back action and remakes editor for later use
        (activity as BaseActivity).editor?.apply()
        ////////////////////// implement new switch /////////////////////////////////////
        /*Intent results = new Intent(this, ResultsActivity.class);					              //switches to results
        startActivity(results);*/
    }
}