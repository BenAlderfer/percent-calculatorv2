package com.alderferstudios.percentcalculatorv2.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.activity.SettingsActivity
import com.alderferstudios.percentcalculatorv2.util.MiscUtil

/**
 * Cost screen
 */
class CostFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_cost, container, false)

    override fun onStart() {
        super.onStart()
        getBaseActivity().buttons.add(activity?.findViewById(R.id.next))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_cost, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                R.id.settings -> {
                    val settingsActivity = Intent(activity, SettingsActivity::class.java)
                    startActivity(settingsActivity)
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }

    override fun fieldsAreValid(): Boolean {
        val costBox = activity?.findViewById<EditText>(R.id.cost)
        return costBox?.text.toString() != "" &&
                costBox?.text.toString().toDouble() > 0.0
    }

    fun showErrorMessage() {
        MiscUtil.showToast(activity as Context, getString(R.string.costError))
    }
}