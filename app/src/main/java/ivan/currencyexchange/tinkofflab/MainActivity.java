package ivan.currencyexchange.tinkofflab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final String VALUE_FROM = "valueFrom";
    static final String VALUE_TO = "valueTo";
    static final String CURRENCY_FROM = "currencyFrom";
    static final String CURRENCY_TO = "currencyTo";
    private Spinner spinnerFrom, spinnerTo;
    private EditText editBoxFrom, editBoxTo;
    private boolean lastEditValueCurrencyFrom = true;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency_array,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom = findViewById(R.id.spinCurFrom);
        spinnerFrom.setAdapter(adapter);

        spinnerTo = findViewById(R.id.spinCurTo);
        spinnerTo.setAdapter(adapter);

        if(adapter.getCount() > 0)
            spinnerTo.setSelection(1);

        editBoxFrom = findViewById(R.id.editBoxFrom);
        editBoxTo = findViewById(R.id.editBoxTo);

        adapter.notifyDataSetChanged();

        AddSpinnerHandler(spinnerFrom);
        AddSpinnerHandler(spinnerTo);

        AddEditTextHandler(editBoxFrom);
        AddEditTextHandler(editBoxTo);
    }

    private void Convert(boolean directConvert) {

        final TextView sourceEdit, targetEdit;
        Spinner sourceSpin, targetSpin;

        if (directConvert) {
            sourceEdit = editBoxFrom;
            targetEdit = editBoxTo;

            sourceSpin = spinnerFrom;
            targetSpin = spinnerTo;
        } else {
            sourceEdit = editBoxTo;
            targetEdit = editBoxFrom;

            sourceSpin = spinnerTo;
            targetSpin = spinnerFrom;
        }

        final String currencyFrom = sourceSpin.getSelectedItem().toString();
        final String currencyTo = targetSpin.getSelectedItem().toString();
        String valueFromString = sourceEdit.getText().toString();
        valueFromString = valueFromString.equals("") ? "0" : valueFromString;
        final Float valueFrom = Float.parseFloat(valueFromString);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CurrencyConverter.ConvertAndUpdate(currencyFrom, currencyTo, valueFrom, targetEdit);
            }
        });

        thread.start();

        // ---


//        Float valueTo = 0.0f;
//
//        if (currencyFrom.equals(currencyTo)) {
//            valueTo = valueFrom;
//        } else {
//            valueTo = 100.81f;
//        }
//
//        String valueToString;
//        if(valueTo == Math.round(valueTo)){
//            valueToString = String.valueOf(Math.round(valueTo));
//        } else
//        {
//            valueToString = String.valueOf(valueTo);
//        }
//
//        targetEdit.setText(valueToString);
    }

    private void AddSpinnerHandler(Spinner spin){
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(view != null && parent != null) {
                    Convert(parent.equals(spinnerFrom));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void AddEditTextHandler(final EditText editText){
        //final boolean directionConvertation = editText.equals(editBoxFrom);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getCurrentFocus() == editText) {
                    lastEditValueCurrencyFrom = editText.equals(editBoxFrom);
                    Convert(lastEditValueCurrencyFrom);
                }
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        super.onRestoreInstanceState(savedInstanceState);

        String valFrom = savedInstanceState.getString(VALUE_FROM);
        String valTo = savedInstanceState.getString(VALUE_TO);

        String curFrom = savedInstanceState.getString(CURRENCY_FROM);
        String curTo = savedInstanceState.getString(CURRENCY_TO);

        spinnerFrom.setSelection(adapter.getPosition(curFrom));
        spinnerTo.setSelection(adapter.getPosition(curTo));

        editBoxFrom.setText(valFrom);
        editBoxTo.setText(valTo);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        String valFrom = editBoxFrom.getText().toString();
        String valTo = editBoxTo.getText().toString();

        String curFrom = spinnerFrom.getSelectedItem().toString();
        String curTo = spinnerTo.getSelectedItem().toString();

        savedInstanceState.putString(CURRENCY_FROM, curFrom);
        savedInstanceState.putString(CURRENCY_TO, curTo);

        savedInstanceState.putString(VALUE_FROM, valFrom);
        savedInstanceState.putString(VALUE_TO, valTo);

        super.onSaveInstanceState(savedInstanceState);
    }
}