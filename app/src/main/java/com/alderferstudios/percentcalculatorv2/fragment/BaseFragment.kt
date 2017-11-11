package com.alderferstudios.percentcalculatorv2.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alderferstudios.percentcalculatorv2.activity.BaseCalcActivity

/**
 * Base class for all Fragments
 */
abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.alderferstudios.percentcalculatorv2.R.layout.activity_one_item, container, false)
    }

    protected fun getBaseActivity(): BaseCalcActivity {
        return activity as BaseCalcActivity
    }
}
