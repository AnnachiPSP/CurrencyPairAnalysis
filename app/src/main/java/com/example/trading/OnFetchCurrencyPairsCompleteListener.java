package com.example.trading;

import java.util.ArrayList;
import java.util.HashMap;

public interface OnFetchCurrencyPairsCompleteListener {
    void onFetchCurrencyPairsComplete(HashMap<String, ArrayList<String>> pairs);
}
