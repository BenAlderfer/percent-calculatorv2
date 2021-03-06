package com.alderferstudios.percentcalculatorv2.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import com.alderferstudios.percentcalculatorv2.R
import com.alderferstudios.percentcalculatorv2.fragment.*
import com.alderferstudios.percentcalculatorv2.util.MiscUtil
import com.alderferstudios.percentcalculatorv2.util.PrefConstants

/**
 * Main activity for 1 item at a time
 */
class OneItemActivity : BaseCalcActivity() {

    private var viewPager: ViewPager? = null
    private var adapter: PagerAdapter? = null

    /**
     * Creates the Activity and gets all the variables/listeners ready
     *
     * @param savedInstanceState the savedInstanceState from before
     */
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_item)

        if (!MiscUtil.isTablet(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }

        shared = PreferenceManager.getDefaultSharedPreferences(this)
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false)    //sets default values if the preferences have not yet been used
        editor = shared?.edit()

        viewPager = findViewById<View>(R.id.pager) as ViewPager
        adapter = PagerAdapter(supportFragmentManager)
        viewPager?.adapter = adapter
        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val current = viewPager?.currentItem ?: 0
                try {
                    supportActionBar?.setDisplayHomeAsUpEnabled(current > 0)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                title = adapter?.getPageTitle(current) ?: getString(R.string.app_name)
            }
        })
        title = adapter?.getPageTitle(0) ?: getString(R.string.app_name)
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
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Checks for input
     * Saves data
     * Moves to percent activity
     *
     * @param v the View
     */
    override fun advanceFromCost(@Suppress("UNUSED_PARAMETER") v: View) {
        super.advanceFromCost(v)
        if (!containsAnError(findViewById(R.id.cost))) {
            viewPager?.currentItem = 1
        }
    }

    override fun tip(@Suppress("UNUSED_PARAMETER") v: View) {
        super.tip(v)
        savePercent()
        viewPager?.currentItem = 3
    }

    override fun discount(@Suppress("UNUSED_PARAMETER") v: View) {
        super.discount(v)
        savePercent()
        viewPager?.currentItem = 3
    }

    override fun advanceToSplit(@Suppress("UNUSED_PARAMETER") v: View) {
        super.advanceToSplit(v)
        savePercent()
        viewPager?.currentItem = 2
    }

    /**
     * Checks for percent input
     * Saves data
     */
    private fun savePercent() {
        val percentText = findViewById<View>(R.id.percentNum) as EditText
        if (containsAnError(percentText)) {
            MiscUtil.showToast(this, getString(R.string.percentError))
            percentText.requestFocus()
        } else {
            percent = percentText.text.toString().toInt()
        }
    }

    override fun split(@Suppress("UNUSED_PARAMETER") v: View) {
        super.split(v)
        split = findViewById<NumberPicker>(R.id.numPicker).value
        viewPager?.currentItem = 3
        //remake results with new split value
        ((viewPager?.adapter as PagerAdapter).getItem(3) as ResultsFragment).makeResults()
    }

    /**
     * Applies the correct colors based on the theme
     */
    override fun applyTheme() {
        themeChoice = "Light"
        try {
            themeChoice = shared?.getString(PrefConstants.THEME_LIST, "Light")
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to get theme")
            e.printStackTrace()
        }

        colorChoice = "Green"
        try {
            colorChoice = shared?.getString(PrefConstants.COLOR_LIST, "Green")
        } catch (e: NullPointerException) {
            Log.e("failure", "failed to get color")
            e.printStackTrace()
        }

        if (colorChoice == "Dynamic") {
            when (themeChoice) {
                "Dark" -> when (viewPager?.currentItem) {
                    0 -> setTheme(R.style.GreenDark)
                    1 -> setTheme(R.style.OrangeDark)
                    2 -> setTheme(R.style.RedDark)
                    3 -> setTheme(R.style.BlueDark)
                }
                "Black and White" -> setTheme(R.style.BlackAndWhite)
                else -> when (viewPager?.currentItem) {
                    0 -> setTheme(R.style.GreenLight)
                    1 -> setTheme(R.style.OrangeLight)
                    2 -> setTheme(R.style.RedLight)
                    3 -> setTheme(R.style.BlueLight)
                }
            }
        } else {
            super.applyTheme()
        }
    }

    override fun onBackPressed() {
        val current = viewPager?.currentItem ?: 0
        if (current <= 0) {
            finish()
        } else {
            //if results page and last action was not split
            if (viewPager?.currentItem == 3 &&
                    shared?.getString(PrefConstants.LAST_ACTION,
                            PrefConstants.TIP) != PrefConstants.SPLIT) {
                viewPager?.currentItem = current - 2
            } else {
                viewPager?.currentItem = current - 1
            }
        }
    }

    /**
     * Custom FragmentPagerAdapter
     */
    inner class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
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

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> getString(R.string.title_cost)
                1 -> getString(R.string.title_percent)
                2 -> getString(R.string.title_split)
                3 -> getString(R.string.title_results)
                else -> getString(R.string.app_name)
            }
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
    }
}
