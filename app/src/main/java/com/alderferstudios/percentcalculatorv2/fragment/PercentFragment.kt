package com.alderferstudios.percentcalculatorv2.fragment

import android.content.Context
import android.content.SharedPreferences
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
import com.alderferstudios.percentcalculatorv2.widget.NumPicker

/**
 * percent screen
 */
class PercentFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener {

    private var percentage: EditText? = null
    private var percent: Int = 0
    private val percentStart: Int = 0
    private val percentMax: Int = 0
    private val editor: SharedPreferences.Editor? = null
    private var numPick: NumPicker? = null
    private var bar: SeekBar? = null
    private val needsToRestart: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getBaseActivity().buttons.add(activity?.findViewById(R.id.add))
        getBaseActivity().buttons.add(activity?.findViewById(R.id.split))
        getBaseActivity().buttons.add(activity?.findViewById(R.id.subtract))

        bar = activity?.findViewById(R.id.percentBar)
        bar?.setOnSeekBarChangeListener(this)

        percentage = activity?.findViewById(R.id.percentNum)
        percentage?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val percentText = percentage?.text.toString()
                if (percentText != "" &&
                        Integer.parseInt(percentText) <= percentMax &&
                        Integer.parseInt(percentText) >= percentStart) {                          //only update bar if its within the limits

                    if (percentText == "") {
                        bar?.progress = percentStart
                    } else {
                        bar?.progress = Integer.parseInt(percentage?.text.toString()) - percentStart
                    }

                    percentage?.setSelection(percentage?.text?.length ?: 0)                       //returns focus to end of text
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        //shared.registerOnSharedPreferenceChangeListener(this);
        applyPrefs()

        return inflater.inflate(R.layout.fragment_percent, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_percent, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //////////////////////////// finish this ////////////////////////////////////////////////////
        /*switch (item.getItemId())
        {
            case R.id.settings:
                Intent settingsActivity = new Intent(this, PrefsActivity.class);
                settingsActivity.putExtra("caller", "percent");
                startActivity(settingsActivity);
                return true;

            case R.id.bar_limits:
                Intent popUp = new Intent(this, PercentLimitPopUp.class);
                needsToRestart = true;
                startActivity(popUp);
                return true;

            default:
                onBackPressed();
                return true;
        }*/
        return true
    }

    /**
     * Applies preference settings
     */
    private fun applyPrefs() {
        var percentStart = 0
        try {
            percentStart = Integer.parseInt((activity as BaseActivity).shared?.getString("percentStart", "0"))
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to get start percent")
            e.printStackTrace()
        }

        var percentMax = 30
        try {
            percentMax = Integer.parseInt((activity as BaseActivity).shared?.getString("percentMax", "30"))
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to get max percent")
            e.printStackTrace()
        }

        if (bar?.max != percentMax) {
            bar?.max = percentMax - percentStart
        }

        if (activity?.findViewById<View>(R.id.numPicker) != null) { //fills in last or default value for split picker if it is there
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
    private fun percentIsWrong(): Boolean {
        var percentText = percentage?.text.toString()
        if (percentText == "") {                                                             //updates bar if nothing was entered
            bar?.progress = percentStart
            percentage?.setText(percentStart)
            percentText = percentage?.text.toString()
        }

        if (Integer.parseInt(percentText) > percentMax) {
            percentage?.setText(percentMax)
            MiscUtil.showToast(activity as Context, "The percent cannot be greater than the max percent")
            return true
        } else if (Integer.parseInt(percentText) < percentStart) {
            percentage?.setText(percentStart)
            MiscUtil.showToast(activity as Context, "The percent cannot be less than the start percent")
            return true
        }
        return false
    }

    /**
     * Checks if the user changed the percentage
     *
     * @return true if it was changed, false if its unchanged
     */
    private fun didChangePercent(): Boolean {
        if (percent == 0 && (percentage?.text.toString() == "" || percentage?.text.toString() == "0")) {                       //if the percent is untouched
            MiscUtil.showToast(activity as Context, "The percent has not been entered")
            return false
        }

        return true
    }

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
        var percentText = percentage?.text.toString()
        if (percentText == "") {                                                             //updates bar if nothing was entered
            bar?.progress = percentStart
            percentage?.setText(percentStart)
            percentText = percentage?.text.toString()
        }

        if (Integer.parseInt(percentText) > percentMax) {
            percentage?.setText(percentMax)
        } else if (Integer.parseInt(percentText) < percentStart) {
            percentage?.setText(percentStart)
        }
    }
}