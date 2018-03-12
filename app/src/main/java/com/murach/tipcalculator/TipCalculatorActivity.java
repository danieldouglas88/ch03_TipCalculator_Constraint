package com.murach.tipcalculator;

import java.text.NumberFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TipCalculatorActivity extends Activity
        implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    private TextView textViewResult;

    // define the SharedPreferences object
    private SharedPreferences savedValues;

    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;

    //define rounding constants
    private final int ROUND_NONE = 0;
    private final int ROUND_TIP = 1;
    private final int ROUND_TOTAL = 2;

    //setup the preferences
    private SharedPreferences prefs;
    private boolean rememberTipPercent = true;
    private int rouding = ROUND_NONE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        //set the default values for the prefs
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //set textview with shared pref obj
        textViewResult.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", null));
    }

    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);

        // calculate and display
        calculateAndDisplay();

        //set textview as chosen name in pref
        textViewResult.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", null));
    }

    public void calculateAndDisplay() {

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }

        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;

        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));

        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.percentDownButton:
                tipPercent = tipPercent - .01f;
                calculateAndDisplay();
                break;
            case R.id.percentUpButton:
                tipPercent = tipPercent + .01f;
                calculateAndDisplay();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tip_cal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.calc_menu:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}