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
                val showBack = (viewPager?.currentItem ?: 0) > 0
                try {
                    supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        })
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
        viewPager?.currentItem = 3
    }

    override fun discount(@Suppress("UNUSED_PARAMETER") v: View) {
        super.discount(v)
        viewPager?.currentItem = 3
    }

    override fun switchToSplit(@Suppress("UNUSED_PARAMETER") v: View) {
        super.switchToSplit(v)
        viewPager?.currentItem = 2
    }

    override fun split(@Suppress("UNUSED_PARAMETER") v: View) {
        super.split(v)
        viewPager?.currentItem = 3
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
            when (viewPager?.currentItem) {
                0 -> for (b in buttons) {
                    b?.setBackgroundResource(R.drawable.btn_green)
                }

                1 -> for (b in buttons) {
                    b?.setBackgroundResource(R.drawable.btn_orange)
                }

                2 -> for (b in buttons) {
                    b?.setBackgroundResource(R.drawable.btn_red)
                }
            }
        } else {
            super.adjustButtons()
        }
    }

    override fun onBackPressed() {
        val current = viewPager?.currentItem ?: 0
        if (current <= 0) {
            finish()
        } else {
            viewPager?.currentItem = current - 1
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
