package com.example.trading;

import org.json.JSONObject;

public interface SocketClientCallback {
    void onMessageReceived(JSONObject message);
}
