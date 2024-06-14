package com.example.trading;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;



public class TradeSimulation extends AppCompatActivity implements SocketClientCallback{

    float buyTrigger;
    float sellTrigger;
    String chosenPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.trade_simulation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent i = getIntent();
        chosenPair = i.getStringExtra("pair");

        ((TextView)findViewById(R.id.displayPair2)).setText(chosenPair);

        new SocketClient(this, chosenPair, this);
    }

    @Override
    // Realtime fetching data from socket using interface callback
    public void onMessageReceived(JSONObject message) {
        runOnUiThread(() -> {
            try {
                JSONArray selling = message.getJSONArray("a");
                JSONArray buyings = message.getJSONArray("b");
                float sell = Float.parseFloat(selling.getJSONArray(0).getString(0));
                float buy = Float.parseFloat(buyings.getJSONArray(0).getString(0));
                for(int i = 1; i<17; i++){
                    if (Float.parseFloat(selling.getJSONArray(i).getString(0)) > sell){
                        sell = Float.parseFloat(selling.getJSONArray(i).getString(0));
                    }
                    if (Float.parseFloat(buyings.getJSONArray(i).getString(0)) > buy){
                        buy = Float.parseFloat(buyings.getJSONArray(i).getString(0));
                    }
                }
                Log.d("valueSell", ""+sell);
                Log.d("valueBuy", ""+buy);
                ((TextView)findViewById(R.id.sell_price)).setText(String.valueOf(sell));
                ((TextView)findViewById(R.id.buy_price)).setText(String.valueOf(buy));

                if(sellTrigger != 0.0 && buyTrigger != 0.0){
                    if(sell >= sellTrigger){
                        payloadGenerate("sell",Float.toString(sell));
                    }
                    if(buy <= buyTrigger){
                        payloadGenerate("buy",Float.toString(buy));
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setTrigger(View v){
        String st = ((EditText)findViewById(R.id.sell_trigger)).getText().toString();
        String bt = ((EditText)findViewById(R.id.buy_trigger)).getText().toString();
        if(st.equals("") || bt.equals("")){
            Toast.makeText(this, "Please Enter the trigger", Toast.LENGTH_LONG).show();
        } else {
            sellTrigger = Float.parseFloat(st);
            buyTrigger = Float.parseFloat(bt);
        }
    }

    // Payload generator buy/sell/cancel
    public void payloadGenerate(String action, String price) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("type", action);
        payload.put("symbol", chosenPair);
        payload.put("price", price);

        float unixTimestamp = (float) System.currentTimeMillis() / 1000L;
        payload.put("time", String.valueOf(unixTimestamp));

        Toast.makeText(this, payload.toString(), Toast.LENGTH_LONG).show();
        Log.d("payload", payload.toString());
    }

    public void payloadGenerate(String action, String price, String price2) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("type", action);
        payload.put("symbol", chosenPair);
        payload.put("sell price", price);
        payload.put("buy price", price2);

        float unixTimestamp = (float) System.currentTimeMillis() / 1000L;
        payload.put("time", String.valueOf(unixTimestamp));

        Toast.makeText(this, payload.toString(), Toast.LENGTH_LONG).show();
        Log.d("payload", payload.toString());
    }

    public void cancelOrder(View v) throws JSONException {
        String sell = ((TextView)findViewById(R.id.buy_price)).getText().toString();
        String buy = ((TextView)findViewById(R.id.sell_price)).getText().toString();
        payloadGenerate("cancel", sell, buy);        // Passess the recent price where cancel got called

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent page = new Intent(TradeSimulation.this, MainActivity.class);
                startActivity(page);
            }
        };

        timer.schedule(task, 2000);     //  Redirect to main page after 2 seconds after cancellation
    }


}