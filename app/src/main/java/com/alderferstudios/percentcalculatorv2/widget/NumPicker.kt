package com.alderferstudios.percentcalculatorv2.widget

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.NumberPicker

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
}
