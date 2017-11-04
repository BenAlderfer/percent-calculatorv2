package com.alderferstudios.percentcalculatorv2.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.fragment.*
import com.alderferstudios.percentcalculatorv2.util.PercentCalculator
import com.alderferstudios.percentcalculatorv2.util.PreferenceDoubles

/**
 * The main activity for 1 item at a time
 */
class OneItemActivity : BaseActivity() {

    private val pageNum = 0
    private var viewPager: ViewPager? = null
    private var adapter: PagerAdapter? = null

    /**
     * Creates the Activity and gets all the variables/listeners ready
     *
     * @param savedInstanceState the savedInstanceState from before
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_item)
        try {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        if (!isTablet(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }

        shared = PreferenceManager.getDefaultSharedPreferences(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)                             //sets default values if the preferences have not yet been used
        editor = shared?.edit()

        viewPager = findViewById<View>(R.id.pager) as ViewPager
        adapter = PagerAdapter(supportFragmentManager)
        viewPager!!.adapter = adapter
    }

    /**
     * Creates the options menu
     *
     * @param menu the Menu
     * @return always true (useless)
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_one_item, menu)
        return true
    }

    /**
     * Handles toolbar/overflow options
     *
     * @param item the option tapped
     * @return true if it matches one of the options, otherwise goes to super
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }/*case R.id.action_clear_all:
                if (ets[tabNum] == null) {
                    saveEditTexts();
                }
                clearAll();
                return true;

            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                return true;

            case R.id.action_instructions:
                Intent instructionActivity = new Intent(this, InstructionActivity.class);
                startActivity(instructionActivity);
                return true;*/
        return super.onOptionsItemSelected(item)
    }

    /**
     * Checks for input
     * Saves data
     * Moves to percent activity
     *
     * @param v the View
     */
    fun next(@Suppress("UNUSED_PARAMETER") v: View) {
        val costText = findViewById<View>(R.id.cost) as EditText
        val costString = costText.text.toString()
        if (containsAnError()) {
            BaseActivity.showToast("The cost has not been entered")
            costText.requestFocus()
        } else {
            val cost = PercentCalculator.round(java.lang.Double.parseDouble(costString))                  //saves the cost
            editor = PreferenceDoubles.putDouble(editor ?: PreferenceManager.getDefaultSharedPreferences(this).edit(), "cost", cost)
            editor?.putBoolean("didJustGoBack", false)                                            //clears back action and remakes editor for later use
            editor?.apply()

            viewPager!!.currentItem = 1
        }
    }

    /**
     * Checks if the input contains an error
     *
     * @return true if there is an error; false otherwise
     */
    private fun containsAnError(): Boolean {
        val text = (findViewById<View>(R.id.cost) as EditText).text.toString()

        return text == "" || text == "0"
    }

    /**
     * Overridden to apply activity specific themes
     * Applies the correct colors based on the theme
     * Makes some changes on Lollipop
     */
    override fun applyTheme() {
        themeChoice = "Light"
        try {
            themeChoice = shared?.getString("themeList", "Light")
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to get theme")
            e.printStackTrace()
        }

        colorChoice = "Green"
        try {
            colorChoice = shared?.getString("colorList", "Green")
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to get color")
            e.printStackTrace()
        }

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
     * Overridden to apply activity specific button
     * Lollipop gets ripple button
     * Others get regular button using setBackgroundDrawable
     * To prevent having to raise the min api
     */
    override fun adjustButtons() {
        buttons = (adapter!!.getItem(pageNum) as BaseFragment).buttons
        if (colorChoice == "Dynamic") {
            when (pageNum) {
                0 -> for (b in buttons) {
                    if (isLollipop()) {
                        b.setBackgroundResource(R.drawable.ripple_green_button)
                    } else {
                        b.setBackgroundResource(R.drawable.green_button)
                    }
                }

                1 -> for (b in buttons) {
                    if (isLollipop()) {
                        b.setBackgroundResource(R.drawable.ripple_orange_button)
                    } else {
                        b.setBackgroundResource(R.drawable.orange_button)
                    }
                }

                2 -> for (b in buttons) {
                    if (isLollipop()) {
                        b.setBackgroundResource(R.drawable.ripple_red_button)
                    } else {
                        b.setBackgroundResource(R.drawable.red_button)
                    }
                }
            }
        } else {
            super.adjustButtons()
        }
    }

    /**
     * Custom FragmentPagerAdapter
     */
    inner class PagerAdapter
    /**
     * Constructs the PagerAdapter
     *
     * @param fm the FragmentManager
     */
    (fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val pages = arrayOfNulls<BaseFragment>(4)

        init {

            pages[0] = CostFragment()
            pages[1] = PercentFragment()
            pages[2] = SplitFragment()
            pages[3] = ResultsFragment()
        }

        /**
         * Gets the fragment at the position
         *
         * @param position the position of the fragment
         * @return the fragment if it exists
         */
        override fun getItem(position: Int): Fragment {
            return pages[position] ?: CostFragment()
        }

        /**
         * Gets the number of pages
         *
         * @return the number of pages
         */
        override fun getCount(): Int {
            return pages.size
        }
    }

    companion object {

        var shared: SharedPreferences? = null
        var editor: SharedPreferences.Editor? = null

        /**
         * Checks if the device is a tablet
         *
         * @param context the Context
         * @return true if device is a tablet
         */
        fun isTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
        }
    }
}
