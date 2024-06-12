package com.example.trading;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnalysisPage extends AppCompatActivity{

    String chosenPair;
    double buyTrigger;
    double sellTrigger;
    String latest;
    ArrayList<ArrayList<String>> prices = new ArrayList<>();
    final OkHttpClient customClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES) // connect timeout
            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
            .readTimeout(5, TimeUnit.MINUTES) // read timeout
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.analysis_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = getIntent();
        chosenPair = i.getStringExtra("pair");
        ((TextView)findViewById(R.id.displayPair2)).setText(chosenPair);
        getHistoryData();
        // System.out.println(chosenPair);          // Debug
    }

    public void onSimulate(View view){
        buyTrigger = Double.parseDouble(((EditText) findViewById(R.id.buy_trigger)).getText().toString());
        sellTrigger = Double.parseDouble(((EditText) findViewById(R.id.sell_trigger)).getText().toString());

        String buyTriggerStr = ((EditText) findViewById(R.id.buy_trigger)).getText().toString();
        String sellTriggerStr = ((EditText) findViewById(R.id.sell_trigger)).getText().toString();

        // Error handling
        if (buyTriggerStr.isEmpty() || sellTriggerStr.isEmpty()) {
            Toast.makeText(this, "Please enter both buy and sell trigger prices", Toast.LENGTH_SHORT).show();
            return; // Stop execution here
        }

        Intent page = new Intent(this, TradeSimulation.class);
        page.putExtra("buyTrigger", buyTrigger);
        page.putExtra("sellTrigger", sellTrigger);
        page.putExtra("pair", chosenPair);
        page.putExtra("historyPrices",prices);
        startActivity(page);
    }

    public void getHistoryData(){
        String url = "https://api.wazirx.com/sapi/v1/trades?symbol="+chosenPair+"&limit=20";

        // Create GET request
        Request request = new Request.Builder()
                .url(url)
                .build();

        customClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Handle request failure
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    // Read the response and convert it to a string
                    String responseData = response.body().string();
                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(responseData);
                        // System.out.println(jsonArray.length());
                        for(int i = jsonArray.length()-1; i>=0; i--) {
                            if(i == 0) latest = jsonArray.getJSONObject(i).getString("price");
                            ArrayList<String> temp = new ArrayList<>();
                            temp.add(jsonArray.getJSONObject(i).getString("price"));
                            temp.add(jsonArray.getJSONObject(i).getString("time"));
                            prices.add(temp);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    ((TextView)findViewById(R.id.pairPrice)).setText(latest);
                    System.out.println(prices.size()+" "+ prices);
                }
            }
        });
    }

}