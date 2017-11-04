package com.alderferstudios.percentcalculatorv2.fragment

import android.content.Context
import android.content.Intent
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
        buttons.add(activity?.findViewById(R.id.add))
        buttons.add(activity?.findViewById(R.id.split))
        buttons.add(activity?.findViewById(R.id.subtract))

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

        return inflater.inflate(R.layout.activity_percent, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_cost, menu)
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

        var didSave = false
        try {
            didSave = (activity as BaseActivity).shared?.getBoolean("saveBox", false) == true
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to check if saved")
            e.printStackTrace()
        }

        if (didSave) {                                                                            //fills in last value if save is enabled
            var lastPercent = 0
            try {
                lastPercent = (activity as BaseActivity).shared?.getInt("percent", 0) ?: 0
            } catch (e: NullPointerException) {
                Log.e("failure", "failed to get last percent")
                e.printStackTrace()
            }

            if (Integer.parseInt(percentage?.text.toString()) > percentMax) {
                percentage?.setText(percentMax)
            } else {
                percentage?.setText(lastPercent)
            }

            percentage?.setSelection(percentage?.text?.length ?: 0)                               //puts focus at end of percent text
        }

        if (activity?.findViewById<View>(R.id.numPicker) != null) {                                        //fills in last or default value for split picker if it is there
            numPick = activity?.findViewById(R.id.numPicker)
            if (didSave) {                                                                        //fills in last value if save is enabled
                var split = 4
                try {
                    split = (activity as BaseActivity).shared?.getInt("split", 4) ?: 4
                } catch (e: NullPointerException) {
                    Log.e("failure", "failed to get split")
                    e.printStackTrace()
                }

                if (split in 2..100) {                                                 //makes sure the number is in the correct range
                    numPick?.value = (activity as BaseActivity).shared?.getInt("split", 4) ?: 4
                } else {
                    numPick?.value = 4
                }
            } else {                                                                              //default value is 4
                numPick?.value = 4
            }
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
     * When the add button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     *
     * @param v the View
     */
    fun add(@Suppress("UNUSED_PARAMETER") v: View) {
        if (!percentIsWrong()) {
            editor?.putString("button", "add")
            editor?.putBoolean("didSplit", false)
            editor?.putString("lastAction", "tip")
            ////////////////////// implement new switch /////////////////////////////////////
            /*Intent results = new Intent(this, ResultsActivity.class);
            saveAndSwitch(results);*/
        }
    }

    /**
     * When the subtract button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     *
     * @param v the View
     */
    fun subtract(@Suppress("UNUSED_PARAMETER") v: View) {
        if (!percentIsWrong()) {
            editor?.putString("button", "subtract")
            editor?.putBoolean("didSplit", false)
            editor?.putString("lastAction", "discount")
            ////////////////////// implement new switch /////////////////////////////////////
            /*Intent results = new Intent(this, ResultsActivity.class);
            saveAndSwitch(results);*/
        }
    }

    /**
     * When the split button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to split activity
     *
     * @param v the View
     */
    fun split(@Suppress("UNUSED_PARAMETER") v: View) {
        if (!percentIsWrong()) {
            editor?.putString("button", "add")
            editor?.putBoolean("didSplit", false)
            editor?.putString("lastAction", "split")
            ////////////////////// implement new switch /////////////////////////////////////
            /*Intent split = new Intent(this, SplitActivity.class);
            saveAndSwitch(split);*/
        }
    }

    /**
     * When the split button is hit
     * Saves button type and
     * Moves on to save data and
     * Switch to results activity
     * ***Does not go to split screen since the number
     * ***picker is on the same screen in landscape
     *
     * @param v the View
     */
    fun splitLandscape(@Suppress("UNUSED_PARAMETER") v: View) {
        editor?.putString("button", "add")
        editor?.putBoolean("didSplit", true)
        editor?.putString("lastAction", "split")
        ////////////////////// implement new switch /////////////////////////////////////
        /*Intent results = new Intent(this, ResultsActivity.class);
        saveAndSwitch(results);*/
    }

    /**
     * Saves the data
     * Switches to the given Intent
     *
     * @param nextIntent the Intent to switch to
     */
    private fun saveAndSwitch(nextIntent: Intent) {
        if (didChangePercent()) {
            editor?.putInt("percent", percent)

            if (activity?.findViewById<View>(R.id.numPicker) != null) {                                    //if there is a numpicker, save the value
                editor?.putInt("split", (activity?.findViewById<NumPicker>(R.id.numPicker))?.value ?: 0)
            }

            editor?.putBoolean("didJustGoBack", false)                                            //clears back action and remakes editor for later use
            editor?.apply()
            startActivity(nextIntent)
        }
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