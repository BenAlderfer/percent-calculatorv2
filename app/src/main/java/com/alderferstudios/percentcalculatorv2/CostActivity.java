package com.alderferstudios.percentcalculatorv2;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Ben Alderfer
 * Alderfer Studios
 * Percent Calculator
 * The cost screen
 */
public class CostActivity extends PCActivity
{
    private EditText costText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        shared = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);                             //sets default values if the preferences have not yet been used
        editor = shared.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cost);

        buttons.add((Button) findViewById(R.id.next));
        adjustButtons();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        applyPrefs();

        findViewById(R.id.mainView).setOnTouchListener(new OnSwipeTouchListener(this){            //swipe listeners for activity movement
            public void onSwipeTop() {
                next(findViewById(R.id.next));
            }

            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_cost, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.settings:
                Intent settingsActivity = new Intent(this, PrefsActivity.class);
                settingsActivity.putExtra("caller", "cost");
                startActivity(settingsActivity);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Applies preference settings
     */
    private void applyPrefs(){
        if (shared.getBoolean("saveBox", false))                                                  //fills in last value if save is enabled
        {
            EditText costBox = ((EditText) findViewById(R.id.cost));
            costBox.setText(Double.toString(PreferenceDoubles.getDouble(shared, "cost", 0.00)));
            costBox.setSelection(costBox.getText().length());                                     //puts focus at end of cost text
        }
    }

    /**
      * Checks for input
      * Saves data
      * Moves to percent activity
      * @param view the View
      */
    public void next(View view){
        costText = (EditText) findViewById(R.id.cost);
        String costString = costText.getText().toString();
        if (containsAnError())
        {
            showToast("The cost has not been entered");
            costText.requestFocus();
        }
        else
        {
            double cost = PercentCalculator.round(Double.parseDouble(costString));                  //saves the cost
            editor = PreferenceDoubles.putDouble(editor, "cost", cost);
            editor.putBoolean("didJustGoBack", false);                                            //clears back action and remakes editor for later use
            editor.apply();

            Intent percentActivity = new Intent(this, PercentActivity.class);	                  //moves to percent activity
            startActivity(percentActivity);
        }
    }

    /**
     * Overridden to apply activity specific themes
     * Applies the correct colors based on the theme
     * Makes some changes on Lollipop
     */
    @Override
    protected void applyTheme(){
        themeChoice = shared.getString("themeList", "Light");
        colorChoice = shared.getString("colorList", "Green");

        if (colorChoice.equals("Dynamic"))
            switch (themeChoice)
            {
                case "Dark": setTheme(R.style.GreenDark); break;
                case "Black and White": setTheme(R.style.BlackAndWhite); break;
                default: setTheme(R.style.GreenLight); break;
            }
        else
            super.applyTheme();
    }

    /**
     * Overridden to apply activity specific button
     * Lollipop gets ripple button
     * Others get regular button using setBackgroundDrawable
     * To prevent having to raise the min api
     */
    protected void adjustButtons(){
        if (colorChoice.equals("Dynamic"))
            for (Button b: buttons)
            {
                if (isLollipop())
                    b.setBackgroundResource(R.drawable.ripple_green_button);
                else
                    b.setBackgroundResource(R.drawable.green_button);
            }
        else
            super.adjustButtons();
    }

    /**
     * Checks if the input contains an error
     * @return true if there is an error; false otherwise
     */
    private boolean containsAnError(){
        String text = costText.getText().toString();
        return text.equals("") || text.equals("0");

    }
}