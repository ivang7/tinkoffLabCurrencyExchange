package ivan.currencyexchange.tinkofflab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final String VALUE_FROM = "valueFrom";
    static final String VALUE_TO = "valueTo";
    static final String CURRENCY_FROM = "currencyFrom";
    static final String CURRENCY_TO = "currencyTo";
    private Spinner spinnerFrom, spinnerTo;
    private TextView editBoxFrom, editBoxTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency_array,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFrom = findViewById(R.id.spinCurFrom);
        spinnerFrom.setAdapter(adapter);

        spinnerTo = findViewById(R.id.spinCurTo);
        spinnerTo.setAdapter(adapter);

        editBoxFrom = findViewById(R.id.editBoxFrom);
        editBoxTo = findViewById(R.id.editBoxTo);

        adapter.notifyDataSetChanged();

        AddSpinnerHandler(spinnerFrom);
        AddSpinnerHandler(spinnerTo);
    }

    private void Convert(boolean directConvert) {

        TextView sourceEdit, targetEdit;
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

        String currencyFrom = sourceSpin.getSelectedItem().toString();
        String currencyTo = targetSpin.getSelectedItem().toString();
        String valueFromString = sourceEdit.getText().toString();
        valueFromString = valueFromString.equals("") ? "0" : valueFromString;
        Float valueFrom = Float.parseFloat(valueFromString);

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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        super.onRestoreInstanceState(savedInstanceState);

        String valFrom = savedInstanceState.getString(VALUE_FROM);
        String valTo = savedInstanceState.getString(VALUE_TO);

        editBoxFrom.setText(valFrom);
        editBoxTo.setText(valTo);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        String valFrom = editBoxFrom.getText().toString();
        String valTo = editBoxTo.getText().toString();

        savedInstanceState.putString(VALUE_FROM, valFrom);
        savedInstanceState.putString(VALUE_TO, valTo);

        super.onSaveInstanceState(savedInstanceState);
    }
}



//                        Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                CurrencyConverter cur =  new CurrencyConverter();
//                                cur.Parse(cur.TestLoad(MainActivity.this));
//                            }
//                        });
//
//                        thread.start();
