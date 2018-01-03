package com.alderferstudios.percentcalculatorv2.util

import android.content.Context
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import com.alderferstudios.percentcalculatorv2.fragment.BaseFragment


class ValidatingViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    /**
     * Intercepts touches and blocks swiping if fields not valid
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val baseFrag = (adapter as FragmentPagerAdapter).getItem(currentItem) as BaseFragment
        val valid = baseFrag.fieldsAreValid()
        if (!valid) {
            baseFrag.showErrorMessage()
        }
        return valid
    }
}