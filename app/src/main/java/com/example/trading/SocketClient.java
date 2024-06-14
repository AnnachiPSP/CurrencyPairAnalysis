package com.example.trading;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import okhttp3.*;
import okio.ByteString;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketClient extends WebSocketListener {

    private static final Logger logger = Logger.getLogger(SocketClient.class.getName());
    private WebSocket ws;
    private Context context;
    private String pair;
    private SocketClientCallback callback;

    public SocketClient(Context context, String pair, SocketClientCallback callback) {

        this.context = context;
        OkHttpClient client = new OkHttpClient();

        this.pair = pair;
        this.callback = callback;

        Request req = new Request.Builder().url("wss://stream.wazirx.com/stream").build();
        ws = client.newWebSocket(req, this);
        // logger.log(Level.INFO, "WebSocket connected");           //Debug
    }

    @Override
    public void onOpen(WebSocket ws, Response res) {
        String msg = "{\"event\": \"subscribe\", \"streams\": [\""+pair+"@depth\"]}";
        ws.send(msg);
    }

    @Override
    public void onMessage(WebSocket ws, String text) {
        try{
            JSONObject json = new JSONObject(text);

            // Notifying connection
            if(json.has("event") && json.length()==2){
                if(json.getString("event").equals("connected")){
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(this.context, "Successfully Connected", Toast.LENGTH_LONG).show());
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(this.context, "Connection Failure", Toast.LENGTH_LONG).show());
                }
            }

            if(!json.has("event")){
                JSONObject data = json.getJSONObject("data");
                if (callback != null) {
                    callback.onMessageReceived(data);
                }
            }


        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing message", e);
        }
    }


    @Override
    public void onClosed(WebSocket ws, int code, String reason) {
        logger.log(Level.INFO, "Closed WebSocket: " + code + " / " + reason);
    }

    @Override
    public void onFailure(WebSocket ws, Throwable t, Response response) {
        logger.log(Level.SEVERE, "Error in WebSocket", t);
    }
}

