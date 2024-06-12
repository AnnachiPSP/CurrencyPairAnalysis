package com.example.trading;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnFetchCurrencyPairsCompleteListener {

    HashMap<String, ArrayList<String>> pairs = new HashMap<>();
    Spinner s1, s2;
    String selectedKey;
    String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize spinners
        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);

        // Fetch currency pairs
        new FetchCurrencyPairsTask(this).execute();
    }

    @Override
    public void onFetchCurrencyPairsComplete(HashMap<String, ArrayList<String>> pairs) {
        this.pairs = pairs;

        // Sorting for comfort
        ArrayList<String> sortedKeys = new ArrayList<>(this.pairs.keySet());
        Collections.sort(sortedKeys);

        // First spinner
        ArrayAdapter<String> adapterKeys = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sortedKeys);
        adapterKeys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapterKeys);
        s1.setOnItemSelectedListener(this);

        // second spinner
        ArrayAdapter<String> adapterValues = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>());
        adapterValues.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(adapterValues);
        s2.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.s1) {

            selectedKey = (String) parent.getItemAtPosition(position);
            ArrayList<String> selectedPairs = pairs.get(selectedKey);

            // Update the second spinner with the selected pairs
            ArrayAdapter<String> adapterValues = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    selectedPairs);
            adapterValues.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s2.setAdapter(adapterValues);
        } else if (parent.getId() == R.id.s2) {

            String selectedValue = (String) parent.getItemAtPosition(position);
            selected = selectedKey + selectedValue;

            // Debug
            // Toast.makeText(getApplicationContext(), selectedKey + selectedValue, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void analyzePair(View v){
        if(selected == null || selected.isEmpty()){
            Log.d("value", selected);
            Toast.makeText(this, "Please select the pairs", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent page = new Intent(this, AnalysisPage.class);
        page.putExtra("pair", selected);
        startActivity(page);
    }
}


