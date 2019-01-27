package ivan.currencyexchange.tinkofflab;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CurrencyConverter {

    private static HashMap<String, Float> currencies = new HashMap();

    public static void ConvertAndUpdate(String currencyFrom, String currencyTo, float valueFrom, final TextView editTextTo) {
        Float valueTo;

        if (currencyFrom.equals(currencyTo)) {
            valueTo = valueFrom;
        } else {
            valueTo = Convert(currencyFrom, currencyTo, valueFrom, editTextTo.getContext());
        }

        final String valueToString;
        if (valueTo == Math.round(valueTo)) {
            if (valueTo == 0)
                valueToString = "";
            else
                valueToString = String.valueOf(Math.round(valueTo));
        } else {
            valueToString = String.valueOf(valueTo);
        }

        editTextTo.post(new Runnable() {
            @Override
            public void run() {
                editTextTo.setText(valueToString);
            }
        });
    }

    public static float Convert(String from, String to, float value, Context context) {
        boolean online = isOnline(context);
        Float result = value;

        SharedPreferences prefs = context.getSharedPreferences("defaultPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = null;

        Map<String, Float> rates = null;


        //if value get in current session work
        if (currencies.containsKey(from) && currencies.containsKey(to)) {
            rates = new HashMap<String, Float>();
            rates.put(from, currencies.get(from));
            rates.put(to, currencies.get(to));
        } else { //update value from internet

            if (online) {
                editor = prefs.edit();
                String exchangeRates = GetCurrentExchangeRate(from, to);

                if (exchangeRates != null)
                    rates = ParseExchangeRate(exchangeRates);
            } else if (!online || rates == null) {
                rates = new HashMap<String, Float>();

                float fromPref = prefs.getFloat(from, -1.0f);
                float toPref = prefs.getFloat(to, -1.0f);

                if (fromPref == -1.0f || toPref == -1.0f) {
                    Toast.makeText(context, "Нет интернет соединения и нет сохранненых курсов, невозможно конвертировать.", Toast.LENGTH_LONG).show();
                    return 0.0f;
                }

                rates.put(from, fromPref);
                rates.put(to, toPref);
            }
        }

        Iterator<String> iterator = rates.keySet().iterator();

        for (int i = 0; iterator.hasNext(); ) {
            String key = iterator.next();
            Float rate = rates.get(key);
            key = key.replace("_USD", "");

            if (editor != null) editor.putFloat(key, rate);

            if (i++ == 0)
                result /= rate;
            else
                result *= rate;
            }

        if (editor != null) editor.apply();


        return result;
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String GetCurrentExchangeRate(String currencyFrom, String currencyTo) {

        String result = "";
        HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://free.currencyconverterapi.com/api/v6/convert?q=" + currencyFrom + "_USD," + currencyTo + "_USD" + "&compact=ultra"); // USD_USD,USD_USD
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream is = new BufferedInputStream(connection.getInputStream());

            int bytesRead = 0;
            byte[] str = new byte[1024];

            while((bytesRead = is.read(str)) != -1) {
                result += new String(str, 0, bytesRead);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return result;
    }

    public static Map<String, Float> ParseExchangeRate(String str) {
        Map<String, Float> list = new HashMap<String, Float>();

        try {
            JSONObject currenciesRate = new JSONObject(str);
            Iterator<String> iterator = currenciesRate.keys();

            for (; iterator.hasNext(); ) {
                String key = iterator.next();
                Float rate = Float.parseFloat(currenciesRate.getString(key));
                key = key.replace("_USD", "");
                list.put(key, rate);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            list = null;
        }

        return list;
    }

    public String GetAllCurrencies(String currencyFrom, String currencyTo) {

        String result = "";
        HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://free.currencyconverterapi.com/api/v6/currencies");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream is = new BufferedInputStream(connection.getInputStream());

            int bytesRead = 0;
            byte[] str = new byte[15000];

            while ((bytesRead = is.read(str)) != -1) {
                result += new String(str, 0, bytesRead);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    public List<String> ParseJsonAllCurrencies(String str) {
        List<String> list = new ArrayList<>();

        try {
            Iterator<String> iterator = ((JSONObject) new JSONObject(str).get("results")).keys();

            for (; iterator.hasNext(); ) {
                list.add(iterator.next());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void UpdateSpinners(List<String> currencies, final AppCompatActivity activity) {

        String[] data = currencies.toArray(new String[0]);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                activity,
                android.R.layout.simple_spinner_item,
                data);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Spinner spinnerFrom = (Spinner) activity.findViewById(R.id.spinCurFrom);
                spinnerFrom.setAdapter(adapter);

                Spinner spinnerTo = (Spinner) activity.findViewById(R.id.spinCurTo);
                spinnerTo.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        });
    }
}
