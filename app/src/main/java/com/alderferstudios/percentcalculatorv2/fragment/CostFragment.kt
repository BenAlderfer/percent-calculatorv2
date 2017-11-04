package com.alderferstudios.percentcalculatorv2.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.activity.BaseActivity
import com.alderferstudios.percentcalculatorv2.activity.PrefsActivity
import com.alderferstudios.percentcalculatorv2.util.PreferenceDoubles

/**
 * cost screen
 */
class CostFragment : BaseFragment() {

    private val costText: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        buttons.add(activity?.findViewById<View>(R.id.next) as Button)
        applyPrefs()

        return inflater.inflate(R.layout.activity_cost, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_cost, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settings -> {
                val settingsActivity = Intent(activity, PrefsActivity::class.java)
                startActivity(settingsActivity)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Applies preference settings
     */
    private fun applyPrefs() {
        var didSave = false
        try {
            didSave = (activity as BaseActivity).shared?.getBoolean("saveBox", false) ?: false
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to check if saved")
            e.printStackTrace()
        }

        if (didSave) {                                                                            //fills in last value if save is enabled
            val costBox = activity?.findViewById<EditText>(R.id.cost)
            costBox?.setText(java.lang.Double.toString(PreferenceDoubles.getDouble((activity as BaseActivity).shared, "cost", 0.00)))
            costBox?.setSelection(costBox.text.length)                                     //puts focus at end of cost text
        }
    }
}