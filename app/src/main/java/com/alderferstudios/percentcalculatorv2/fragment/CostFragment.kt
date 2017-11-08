package com.alderferstudios.percentcalculatorv2.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.activity.PrefsActivity

/**
 * cost screen
 */
class CostFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getBaseActivity().buttons.add(activity?.findViewById(R.id.next))
        return inflater.inflate(R.layout.fragment_cost, container, false)
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
}