package com.alderferstudios.percentcalculatorv2.fragment

import android.os.Bundle
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
        getBaseActivity().buttons.add(activity?.findViewById(R.id.add))

        numPick = activity?.findViewById(R.id.numPicker)
        applyPrefs()

        return inflater.inflate(R.layout.fragment_split, container, false)
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
        if (activity?.findViewById<View>(R.id.numPicker) != null) { //fills in last or default value for split picker if it is there
            activity?.findViewById<NumPicker>(R.id.numPicker)?.value = 4
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