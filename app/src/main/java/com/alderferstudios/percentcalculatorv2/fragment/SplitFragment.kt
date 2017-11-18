package com.alderferstudios.percentcalculatorv2.fragment

import android.os.Bundle
import android.view.*
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.widget.NumPicker

/**
 * Split screen
 */
class SplitFragment : BaseFragment() {

    private var numPick: NumPicker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        numPick = activity?.findViewById(R.id.numPicker)
        applyPrefs()

        return inflater.inflate(R.layout.fragment_split, container, false)
    }

    override fun onStart() {
        super.onStart()
        getBaseActivity().buttons.add(activity?.findViewById(R.id.add))
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
                Intent settingsActivity = new Intent(getActivity(), SettingsActivity.class);
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
        if (activity?.findViewById<View>(R.id.numPicker) != null) {    //fills in last or default value for split picker if it is there
            activity?.findViewById<NumPicker>(R.id.numPicker)?.value = 4
        }
    }

    override fun fieldsAreValid(): Boolean {
        return true
        //TODO: fix functionality
    }
}