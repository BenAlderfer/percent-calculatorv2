package com.alderferstudios.percentcalculatorv2.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import com.alderferstudios.percentcalculatorv2.R


/**
 * custom number picker
 * allows for setting min and max from xml
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class NumPicker : NumberPicker {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        processAttributeSet(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        processAttributeSet(attrs)
    }

    /**
     * Reads the xml, sets the properties
     */
    private fun processAttributeSet(attrs: AttributeSet) {
        minValue = attrs.getAttributeIntValue(null, "min", 0)
        maxValue = attrs.getAttributeIntValue(null, "max", 0)
    }

    override fun addView(child: View) {
        super.addView(child)
        updateView(child)
    }

    override fun addView(child: View, index: Int, params: android.view.ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        updateView(child)
    }

    override fun addView(child: View, params: android.view.ViewGroup.LayoutParams) {
        super.addView(child, params)
        updateView(child)
    }

    /**
     * Set text size of numberpicker to match normal text size
     */
    private fun updateView(view: View) {
        if (view is EditText) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_tx_size))
        }
    }
}
