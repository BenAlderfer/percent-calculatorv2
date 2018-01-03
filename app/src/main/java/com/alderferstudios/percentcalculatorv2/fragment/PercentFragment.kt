package com.alderferstudios.percentcalculatorv2.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.SeekBar
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.activity.BaseActivity
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import com.alderferstudios.percentcalculatorv2.util.PrefConstants
import com.alderferstudios.percentcalculatorv2.widget.NumPicker
import com.alderferstudios.percentcalculatorv2.widget.PercentLimitPopUp

/**
 * Percent screen
 */
class PercentFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener {

    private var percentage: EditText? = null
    private var percent: Int = 0
    private val percentStart: Int = 0
    private val percentMax: Int = 0
    private var numPick: NumPicker? = null
    private var bar: SeekBar? = null
    private val needsToRestart: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bar = activity?.findViewById(R.id.percentBar)
        bar?.setOnSeekBarChangeListener(this)

        percentage = activity?.findViewById(R.id.percentNum)
        percentage?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val percentText = percentage?.text.toString()
                if (percentText != "" &&
                        percentText.toInt() <= percentMax &&
                        percentText.toInt() >= percentStart) {    //only update bar if its within the limits

                    if (percentText == "") {
                        bar?.progress = percentStart
                    } else {
                        bar?.progress = percentage?.text.toString().toInt() - percentStart
                    }

                    percentage?.setSelection(percentage?.text?.length ?: 0)    //returns focus to end of text
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        //shared.registerOnSharedPreferenceChangeListener(this);
        applyPrefs()

        return inflater.inflate(R.layout.fragment_percent, container, false)
    }

    override fun onStart() {
        super.onStart()
        val base = getBaseActivity()
        base.buttons.add(activity?.findViewById(R.id.tip))
        base.buttons.add(activity?.findViewById(R.id.split))
        base.buttons.add(activity?.findViewById(R.id.discount))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_percent, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.split_options -> startActivity(Intent(activity, PercentLimitPopUp::class.java))
        }
        return true
    }

    /**
     * Applies preference settings
     */
    private fun applyPrefs() {
        var percentStart = 0
        try {
            percentStart = (activity as BaseActivity).shared?.getString(PrefConstants.PERCENT_START, "0")?.toInt() ?: 0
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to get start percent")
            e.printStackTrace()
        }

        var percentMax = 30
        try {
            percentMax = (activity as BaseActivity).shared?.getString(PrefConstants.PERCENT_MAX, "30")?.toInt() ?: 30
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to get max percent")
            e.printStackTrace()
        }

        if (bar?.max != percentMax) {
            bar?.max = percentMax - percentStart
        }

        if (activity?.findViewById<View>(R.id.numPicker) != null) {    //fills in last or default value for split picker if it is there
            activity?.findViewById<NumPicker>(R.id.numPicker)?.value = 4
        }
    }

    /**
     * Restarts the activity if one of the limits was changed
     * @param sharedPreferences the shared prefs
     * @param key the name of the pref
     */
    /*@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("percentStart") || key.equals("percentMax"))
            needsToRestart = true;
    }*/

    /**
     * Checks if the percent input is wrong
     * Looks to see if it is greater or less than a limit
     *
     * @return true if it exceeds a limit; false otherwise
     */
//    private fun percentIsWrong(): Boolean {
//        var percentText = percentage?.text.toString()
//        if (percentText == "") {    //updates bar if nothing was entered
//            bar?.progress = percentStart
//            percentage?.setText(percentStart)
//            percentText = percentage?.text.toString()
//        }
//
//        if (percentText.toInt() > percentMax) {
//            percentage?.setText(percentMax)
//            MiscUtil.showToast(activity as Context, "The percent cannot be greater than the max percent")
//            return true
//        } else if (percentText.toInt() < percentStart) {
//            percentage?.setText(percentStart)
//            MiscUtil.showToast(activity as Context, "The percent cannot be less than the start percent")
//            return true
//        }
//        return false
//    }

    /**
     * Updates the percent text as the bar changes
     */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        percent = progress + percentStart
        if (percent > percentMax) {
            percent = percentMax
        }

        percentage?.setText(percent.toString())
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    /**
     * Checks if the percent input is wrong
     * Looks to see if it is greater or less than a limit
     *
     * @return true if it exceeds a limit; false otherwise
     */
    private fun resetAfterPrefChange() {
        //TODO: check this
        var percentText = percentage?.text.toString()
        if (percentText == "") {    //updates bar if nothing was entered
            bar?.progress = percentStart
            percentage?.setText(percentStart)
            percentText = percentage?.text.toString()
        }

        if (percentText.toInt() > percentMax) {
            percentage?.setText(percentMax)
        } else if (percentText.toInt() < percentStart) {
            percentage?.setText(percentStart)
        }
    }

    override fun fieldsAreValid(): Boolean {
        return percent > 0
    }

    override fun showErrorMessage() {
        MiscUtil.showToast(activity as Context, getString(R.string.percentError))
    }
}