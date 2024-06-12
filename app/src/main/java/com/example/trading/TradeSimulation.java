package com.example.trading;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


public class TradeSimulation extends AppCompatActivity implements SocketClientCallback{

    double buyTrigger;
    double sellTrigger;
    String chosenPair;
    ArrayList<ArrayList<String>> prices = new ArrayList<>();
    LineChart lineChart;

    @SuppressLint("MissingInflatedId")
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
        buyTrigger = i.getDoubleExtra("buyTrigger", 0);
        sellTrigger = i.getDoubleExtra("sellTrigger", 0);
        chosenPair = i.getStringExtra("pair");
        prices = (ArrayList<ArrayList<String>>) i.getSerializableExtra("historyPrices");

        System.out.println(buyTrigger);
        System.out.println(sellTrigger);

        ((TextView)findViewById(R.id.displayPair2)).setText(chosenPair);

        new SocketClient(this, chosenPair, this);

        lineChart = findViewById(R.id.lineChart);
        setupChart();
        updateChart();
    }

    @Override
    // Realtime fetching data from socket using interface callback
    public void onMessageReceived(JSONObject message) {
        runOnUiThread(() -> {
            try {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(message.getString("c"));
                temp.add(message.getString("E"));
                prices.add(temp);
                //Log.d("value", ""+prices);
                updateChart();

                if(Double.parseDouble(message.getString("c")) > sellTrigger){
                    payloadGenerate("sell",message.getString("c"));
                } else if(Double.parseDouble(message.getString("c")) <= buyTrigger){
                    payloadGenerate("buy",message.getString("c"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        addLimitLine(buyTrigger, "Buy Trigger");
        addLimitLine(sellTrigger, "Sell Trigger");

        // Set the text and axis marks to white
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setValueFormatter(new DateValueFormatter()); // Format the axis value to date format MM/dd HH:mm to understand

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGridColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setGridColor(Color.WHITE);
        rightAxis.setAxisLineColor(Color.WHITE);

        lineChart.getLegend().setTextColor(Color.WHITE);
    }

    // Update everytime we receive updated price through sockets
    public void updateChart(){
        List<Entry> entries = new ArrayList<>();

        // prices data to entries
        for (ArrayList<String> priceData : prices) {
            float price = Float.parseFloat(priceData.get(0));
            long time = Long.parseLong(priceData.get(1));
            entries.add(new Entry(time, price));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Price");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setValueTextColor(Color.WHITE);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.invalidate();
    }

    // Trigger Line definition for Buy And Sell
    public void addLimitLine(double triggerValue, String label) {
        LimitLine limitLine = new LimitLine((float) triggerValue, label);
        limitLine.setLineWidth(1.5f);
        limitLine.enableDashedLine(10f, 10f, 0f);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLine.setTextSize(10f);
        limitLine.setLineColor(Color.GREEN);
        limitLine.setTextColor(Color.WHITE);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.addLimitLine(limitLine);

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
    }

    public void cancelOrder(View v) throws JSONException {
        payloadGenerate("cancel", prices.get(0).get(0));        // Passess the recent price where cancel got called

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