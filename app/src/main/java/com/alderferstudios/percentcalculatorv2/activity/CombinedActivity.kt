package com.alderferstudios.percentcalculatorv2.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.util.CalcFields
import com.alderferstudios.percentcalculatorv2.util.CalcResults
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import com.alderferstudios.percentcalculatorv2.util.PercentCalculator
import com.alderferstudios.percentcalculatorv2.widget.PercentLimitPopUp
import com.alderferstudios.percentcalculatorv2.widget.SplitPopUp

/**
 * Combined screen, all parts on 1 screen
 */
class CombinedActivity : BaseCalcActivity(), SeekBar.OnSeekBarChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private var resultsText: TextView? = null
    private val resultsText1: TextView? = null
    private val resultsText2: TextView? = null    //where results are displayed
    private var costBox: EditText? = null
    private var percentage: EditText? = null
    private var splitBox: EditText? = null
    private var text: String? = null
    private var text1: String? = null
    private var text2: String? = null
    private var percentStart: Int = 0
    private var percentMax: Int = 0
    private var willTax: Boolean = false
    private var willSplit: Boolean = false
    private var didJustUpdate: Boolean = false
    private var didJustTypePercent: Boolean = false
    private var needsToRestart: Boolean = false
    private var bar: SeekBar? = null
    private var imm: InputMethodManager? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        shared = PreferenceManager.getDefaultSharedPreferences(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)    //sets default values if the preferences have not yet been used
        editor = shared?.edit()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combined)

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        buttons.add(findViewById<View>(R.id.tip) as Button)
        buttons.add(findViewById<View>(R.id.split) as Button)
        buttons.add(findViewById<View>(R.id.discount) as Button)
        adjustButtons()

        willTax = shared?.getBoolean("taxBox", false) ?: false    //if the tax will be added
        costBox = findViewById<View>(R.id.cost) as EditText
        percentage = findViewById<View>(R.id.percentNum) as EditText
        splitBox = findViewById<View>(R.id.splitNum) as EditText
        bar = findViewById<View>(R.id.percentBar) as SeekBar
        willSplit = false

        if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE) {    //hides portrait results
            resultsText = findViewById<View>(R.id.results) as TextView
            if (resultsText != null) {
                resultsText?.visibility = View.INVISIBLE
            }
        } else { //if (orientation == Configuration.ORIENTATION_LANDSCAPE)  //hides landscape results
            /* resultsText1 = (TextView) findViewById(R.id.landResults1);
            resultsText2 = (TextView) findViewById(R.id.landResults2);
            if (resultsText1 != null) {
                resultsText1.setVisibility(View.INVISIBLE);
            }
            if (resultsText2 != null) {
                resultsText2.setVisibility(View.INVISIBLE);
            }*/
        }

        applyPrefs()
        shared?.registerOnSharedPreferenceChangeListener(this)
        addListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_combined, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val settingsActivity = Intent(this, SettingsActivity::class.java)
                settingsActivity.putExtra("caller", "combined")
                startActivity(settingsActivity)
                return true
            }

            R.id.bar_limits -> {
                val popUp = Intent(this, PercentLimitPopUp::class.java)
                startActivity(popUp)
                return true
            }

            R.id.split_options -> {
                val popUp2 = Intent(this, SplitPopUp::class.java)
                startActivity(popUp2)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Applies preference settings
     */
    private fun applyPrefs() {
        percentStart = Integer.parseInt(shared?.getString("percentStart", "0"))
        percentMax = Integer.parseInt(shared?.getString("percentMax", "30"))
        if (bar?.max != percentMax) {   //refreshes the activity if the current max is not right
            bar?.max = percentMax - percentStart
        }

        if (shared?.getString("button", "") == "") {    //if no button is currently saved, initializes it to be add
            editor?.putString("button", "add")
            editor?.apply()
        }
    }

    /**
     * Restarts the activity if one of the limits was changed
     * @param sharedPreferences the shared prefs
     * @param key the name of the pref
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "percentStart" || key == "percentMax" || key == "splitList") {
            needsToRestart = true
        }
    }

    /**
     * Adds listeners to all items for auto-updating
     */
    private fun addListeners() {
        costBox?.addTextChangedListener(object : TextWatcher {    //watches cost to update
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateResultsWithChanges()    //updates the results text if possible as numbers change
            }
        })

        bar?.setOnSeekBarChangeListener(this)
        percentage?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                didJustTypePercent = true

                if (percentage?.text.toString() == "") {
                    bar?.progress = 0
                } else {
                    bar?.progress = Integer.parseInt(percentage?.text.toString()) - percentStart
                }

                updateResultsWithChanges()    //updates the results text if possible as numbers change

                percentage?.setSelection(percentage?.text?.length ?: 0)    //returns focus to end of text
            }
        })

        splitBox?.addTextChangedListener(object : TextWatcher {    //watches split to update
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                willSplit = didEnterSplit(false)    //makes sure the split was entered, false prevents toasts

                updateResultsWithChanges()    //updates the results text if possible as numbers change
            }
        })
    }

    /**
     * Hides the keyboard when a button is clicked
     * Saves the numbers
     */
    private fun processValues() {
        if (!didJustUpdate) {    //makes sure it was not an auto update to avoid breaks in typing
            imm?.hideSoftInputFromWindow(splitBox?.windowToken, 0)
        }

        didJustUpdate = false    //resets variable

        val costString = costBox?.text.toString()
        cost = PercentCalculator.roundDouble(java.lang.Double.parseDouble(costString))

        if (percent == 0) {    //if they didn't use the seekbar, the percent will be the default value
            percent = Integer.parseInt(percentage?.text.toString())
        }

        if (splitBox?.text.toString() != "") {
            split = Integer.parseInt(splitBox?.text.toString())
        }

        //editor.putBoolean("didSplit", willSplit);    //saves whether they will split or not

        val pc = PercentCalculator(this)
        val calcFields = CalcFields(cost, percent, split, "")   //TODO: add button string
        makeResultsText(pc.calculate(calcFields, getString(R.string.money_separator)))
    }

    /**
     * Makes the text results
     */
    private fun makeResultsText(results: CalcResults) {
        val change = shared?.getString("percentAmount", "")    //makes the first part of the text, tip is add, else discount
        val eachTip = shared?.getString("eachTip", "")    //how much each person will tip
        val eachTotal = shared?.getString("eachTotal", "")    //how much of the total each person pays

        val willSplitTip: Boolean
        val willSplitTotal: Boolean
        when (shared?.getString("splitList", "Split tip")) {
            "Split total" -> {
                willSplitTip = false
                willSplitTotal = true
            }
            "Split both" -> {
                willSplitTip = true
                willSplitTotal = true
            }
            else -> {
                willSplitTip = true
                willSplitTotal = false
            }
        }

        val spacing = "%n"

        // portrait setup
        if (!MiscUtil.isLandscape(this)) {
            text = ""    //clears previous results

            if (shared?.getString("button", null) != null && shared?.getString("button", null) == "add") {
                text = "Tip: " + getString(R.string.money_sign) + change + spacing

                if (willSplit && didEnterSplit(false) && willSplitTip) {    //if split was clicked, number is entered, and split tip is selected in prefs
                    text += "Each tips: " + getString(R.string.money_sign) + eachTip + spacing    //adds the split tip part of the text
                }
            } else {
                text = "Discount: " + getString(R.string.money_sign) + change + spacing
            }

            if (willTax) {    //makes the tax part of the text
                text += "Tax: " + getString(R.string.money_sign) + shared?.getString("taxAmount", "") + spacing
            }

            text += "Final cost: " + getString(R.string.money_sign) + shared?.getString("total", "")    //makes the cost total part of the text

            if (willSplit && didEnterSplit(false) && willSplitTotal) {    //if split was clicked, number is entered, and split total is selected in prefs
                text += spacing + "Each pays: " + getString(R.string.money_sign) + eachTotal    //adds the split tip part of the text
            }

            setResults()

        } else {    //landscape setup
            //clears previous results
            text1 = ""
            text2 = ""

            if (!willSplit || !didEnterSplit(false)) {    //if not splitting, use both sides
                text1 = if (shared?.getString("button", null) != null && shared?.getString("button", null) == "add") {
                    "Tip: " + getString(R.string.money_sign) + change
                } else {
                    "Discount: " + getString(R.string.money_sign) + change
                }

                if (willTax) {    //makes the tax part of the text
                    text1 += spacing + "Tax: " + getString(R.string.money_sign) + shared?.getString("taxAmount", "")
                }

                text2 = "Final cost: " + getString(R.string.money_sign) + shared?.getString("total", "")    //makes the cost total part of the text
            } else {    //otherwise, splits are on right
                text1 = if (shared?.getString("button", null) != null && shared?.getString("button", null) == "add") {
                    "Tip: " + getString(R.string.money_sign) + change + spacing
                } else {
                    "Discount: " + getString(R.string.money_sign) + change + spacing
                }

                if (willTax) {    //makes the tax part of the text
                    text1 += "Tax: " + getString(R.string.money_sign) + shared?.getString("taxAmount", "") + spacing
                }

                text1 += "Final cost: " + getString(R.string.money_sign) + shared?.getString("total", "")    //makes the cost total part of the text

                if (willSplit && didEnterSplit(false) && willSplitTip) {    //if split was clicked, number is entered, and split tip is checked in prefs
                    text2 = "Each tips: " + getString(R.string.money_sign) + eachTip    //adds the split tip part of the text
                }

                if (willSplit && didEnterSplit(false) && willSplitTotal) {    //if split was clicked, number is entered, and split total is checked in prefs
                    text2 += spacing + "Each pays: " + getString(R.string.money_sign) + eachTotal    //adds the split tip part of the text
                }
            }

            setLandscapeResults()
        }
    }

    /**
     * Checks for no input first
     * Adds the percent
     * Used for tip
     */
    override fun tip(@Suppress("UNUSED_PARAMETER") v: View) {
//        willSplit = false
//        if (didEnterValues()) {
//            editor?.putString("button", "add")
//            editor?.putString("lastAction", "tip")
//            processValues()
//            makeText()
//        }
    }

    /**
     * Checks for no input first
     * Subtracts the percent
     * Used for discount
     */
    override fun discount(@Suppress("UNUSED_PARAMETER") v: View) {
//        willSplit = false
//        if (didEnterValues()) {
//            editor?.putString("button", "subtract")
//            editor?.putString("lastAction", "discount")
//            processValues()
//            makeText()
//        }
    }

    /**
     * Checks for no input first
     * Splits the tip
     */
    override fun split(@Suppress("UNUSED_PARAMETER") v: View) {
//        willSplit = true
//        if (didEnterSplit(true) && didEnterValues()) {
//            editor?.putString("button", "add")
//            editor?.putString("lastAction", "split")
//            processValues()
//            makeText()
//        }
    }

    /**
     * Sets the text in results
     * Makes the text visible
     * Centers the text
     *
     * While the warning says String.format is not needed, it is
     */
    private fun setResults() {
        resultsText?.text = String.format(text ?: "")
        resultsText?.visibility = View.VISIBLE    //shows results text
        resultsText?.gravity = Gravity.CENTER_HORIZONTAL
    }

    /**
     * Landscape variation of the results
     * Sets the text in results
     * Makes the text visible
     * Centers the text
     *
     * While the warning says String.format is not needed, it is
     */
    private fun setLandscapeResults() {
        resultsText1?.text = String.format(text1 ?: "")
        resultsText1?.visibility = View.VISIBLE    //shows results text #1
        resultsText1?.gravity = Gravity.CENTER_HORIZONTAL

        resultsText2?.text = String.format(text2 ?: "")
        resultsText2?.visibility = View.VISIBLE    //shows results text #2
        resultsText2?.gravity = Gravity.CENTER_HORIZONTAL
    }

    /**
     * Checks to make sure all the info is entered
     * Updates the results as the numbers are changed
     */
    private fun updateResultsWithChanges() {
        if (fieldsAreCurrentlyFilled()) {    //if the info is already filled in, updates the results
            didJustUpdate = true

            //performs last action, default = tip
            when (shared?.getString("lastAction", "tip")) {
                "discount" -> discount(findViewById(R.id.discount))
                "split" -> split(findViewById(R.id.split))
                else -> tip(findViewById(R.id.tip))
            }
        }
    }

    /**
     * Checks if the user entered the cost and percentage
     * @return true if it was entered, false if its unchanged
     */
    private fun didEnterValues(): Boolean {
        if (!costIsEntered()) {
            MiscUtil.showToast(this, "The cost has not been entered")
            costBox?.requestFocus()
            return false
        } else if (!percentIsEntered()) {    //if the percent is untouched
            MiscUtil.showToast(this, "The percent has not been entered")
            return false
        } else if (willSplit) {
            return didEnterSplit(false)
        }

        return true
    }

    /**
     * Checks if they entered the split (if needed)
     * @param willToast If it should toast
     * @return true if entered, false and toast otherwise
     */
    private fun didEnterSplit(willToast: Boolean): Boolean {
        if (splitIsEmpty()) {
            if (willToast) {
                MiscUtil.showToast(this, "The split has not been entered")
            }
            return false
        } else if (splitIsOne()) {
            if (willToast) {
                MiscUtil.showToast(this, "One person cannot split the bill")
            }
            return false
        }

        return true
    }

    /**
     * Checks if all the necessary fields are filled
     * Used to check for blank fields before auto-updating results
     * @return true if everything is filled in
     */
    private fun fieldsAreCurrentlyFilled(): Boolean {
        if (!costIsEntered()) {    //false if cost is not entered
            return false
        } else if (!percentIsEntered()) {    //false if percent is not entered
            return false
        }

        return true    //if it passes the other checks, its good
    }

    /**
     * Checks if the cost was entered
     * Checks for the most common forms of 0
     * @return true if the cost was entered
     */
    private fun costIsEntered(): Boolean {
        return costBox?.text.toString() != "" &&
                costBox?.text.toString() != "0" &&
                costBox?.text.toString() != "0.0" &&
                costBox?.text.toString() != "0.00" &&
                costBox?.text.toString() != "00.00"
    }

    /**
     * Checks if the percent was entered
     * @return true if the percent was entered
     */
    private fun percentIsEntered(): Boolean {
        return percent != 0 &&
                percentage?.text.toString() != "" &&
                percentage?.text.toString() != "0"
    }

    /**
     * Checks if nothing was entered into the split
     * @return true if its empty
     */
    private fun splitIsEmpty(): Boolean = splitBox?.text.toString() == ""

    /**
     * Checks if they only put 1 in the split
     * @return true if 1 was entered
     */
    private fun splitIsOne(): Boolean = splitBox?.text.toString() == "1"

    /**
     * Updates the percent text as the bar changes
     * Updates the results if possible
     */
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (didJustTypePercent) {
            didJustTypePercent = false
        } else {
            imm?.hideSoftInputFromWindow(splitBox?.windowToken, 0)    //hides keyboard if not typing
        }

        percent = progress + percentStart
        if (percent > percentMax) {
            percent = percentMax
        }

        percentage?.setText(percent.toString())
        updateResultsWithChanges()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    /**
     * Overridden to apply activity specific themes
     * Applies the correct colors based on the theme
     * Makes some changes on Lollipop
     */
    override fun applyTheme() {
        themeChoice = shared?.getString("themeList", "Light")
        colorChoice = shared?.getString("colorList", "Green")

        if (colorChoice == "Dynamic") {
            when (themeChoice) {
                "Dark" -> setTheme(R.style.GreenDark)
                "Black and White" -> setTheme(R.style.BlackAndWhite)
                else -> setTheme(R.style.GreenLight)
            }
        } else {
            super.applyTheme()
        }
    }

    /**
     * Applies activity specific button
     * If not dynamic, defer to super to set color from theme
     */
    override fun adjustButtons() {
        if (colorChoice == "Dynamic") {
            for (b in buttons) {
                b?.setBackgroundResource(R.drawable.btn_green)
            }
        } else {
            super.adjustButtons()
        }
    }

    public override fun onResume() {
        super.onResume()

        if (needsToRestart) {
            needsToRestart = false
            onRestart()
        }
    }
}
