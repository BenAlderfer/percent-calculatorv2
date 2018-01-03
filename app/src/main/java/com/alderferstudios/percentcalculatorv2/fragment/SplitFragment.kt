package com.alderferstudios.percentcalculatorv2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.widget.NumPicker
import com.alderferstudios.percentcalculatorv2.widget.SplitPopUp

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
        when (item?.itemId) {
            R.id.split_options -> startActivity(Intent(activity, SplitPopUp::class.java))
        }
        return true
    }

    /**
     * Applies preference settings
     * Fills in last or default value
     */
    private fun applyPrefs() {
        if (numPick == null) {
            numPick = activity?.findViewById(R.id.numPicker)
        }
        if (numPick != null) {  //make sure it was found
            numPick?.value = 4
        }
    }

    /**
     * Cannot pick a bad value with the number picker
     */
    override fun fieldsAreValid(): Boolean {
        return true
    }

    /**
     * No possible error since no bad values possible
     */
    override fun showErrorMessage() {}
}