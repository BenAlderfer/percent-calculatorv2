package com.alderferstudios.percentcalculatorv2;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;

/**
 * Percent Calculator
 * The custom number picker
 * <p>
 * Alderfer Studios
 *
 * @author Ben Alderfer
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NumPicker extends NumberPicker
{
    public NumPicker(Context context)
    {
        super(context);
    }

    public NumPicker(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        processAttributeSet(attrs);
    }

    public NumPicker(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        processAttributeSet(attrs);
    }

    /**
     * Reads the xml, sets the properties
     */
    private void processAttributeSet(AttributeSet attrs)
    {
        setMinValue(attrs.getAttributeIntValue(null, "min", 0));
        setMaxValue(attrs.getAttributeIntValue(null, "max", 0));
    }
}
