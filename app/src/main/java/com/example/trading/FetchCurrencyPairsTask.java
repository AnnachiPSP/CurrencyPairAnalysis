package com.example.trading;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchCurrencyPairsTask extends AsyncTask<Void, Void, HashMap<String, ArrayList<String>>> {

    private OnFetchCurrencyPairsCompleteListener listener;

    public FetchCurrencyPairsTask(OnFetchCurrencyPairsCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected HashMap<String, ArrayList<String>> doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.wazirx.com/api/v2/market-status";
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            JSONObject json = new JSONObject(result);
            int n = json.getJSONArray("markets").length();
            HashMap<String, ArrayList<String>> pairs = new HashMap<>();

            for (int i = 0; i < n; i++) {
                JSONObject obj = json.getJSONArray("markets").getJSONObject(i);
                String base = obj.getString("baseMarket");
                String quote = obj.getString("quoteMarket");
                if (pairs.containsKey(base)) {
                    pairs.get(base).add(quote);
                } else {
                    ArrayList<String> quotes = new ArrayList<>();
                    quotes.add(quote);
                    pairs.put(base, quotes);
                }
            }
            return pairs;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(HashMap<String, ArrayList<String>> result) {
        if (result != null) {
            listener.onFetchCurrencyPairsComplete(result);
        } else {
            Log.e("Currency Pair", "Failed to fetch data!");
        }
    }
}