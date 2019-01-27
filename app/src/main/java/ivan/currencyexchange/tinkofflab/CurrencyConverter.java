package ivan.currencyexchange.tinkofflab;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class CurrencyConverter{
    CurrencyConverter(){}
    public float Convert(String from, String to, float value){
        return 0.0f;
    }

    public void UpdateSpinners(List<String> currencies, final AppCompatActivity activity){
        String[] data = currencies.toArray(new String[0]);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                activity,
                android.R.layout.simple_spinner_item,
                data);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                        Spinner spinnerFrom = (Spinner)activity.findViewById(R.id.spinCurFrom);
                        spinnerFrom.setAdapter(adapter);

                        Spinner spinnerTo = (Spinner)activity.findViewById(R.id.spinCurTo);
                        spinnerTo.setAdapter(adapter);

                        adapter.notifyDataSetChanged();
            }
        });
    }

    public List<String> Parse(String str)
    {
        List<String> list = new ArrayList<>();

        try {
            Iterator<String> iterator = ((JSONObject)new JSONObject(str).get("results")).keys();

            for (; iterator.hasNext(); ) {
                list.add(iterator.next());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }


    public String TestLoad(){

        String result = "";
        HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://free.currencyconverterapi.com/api/v6/currencies");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream is = new BufferedInputStream(connection.getInputStream());

            int bytesRead = 0;
            byte[] str = new byte[15000];

            while((bytesRead = is.read(str)) != -1) {
                result += new String(str, 0, bytesRead);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }

        return result;
    }
}
