package kgk.beacon.monitoring.network;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import kgk.beacon.networking.VolleyHttpClient;
import kgk.beacon.util.AppController;

public class WebSocket implements VolleyHttpClient.WebSocketKeyRequestListener {

    private static WebSocket instance;

    private VolleyHttpClient httpClient;
    private String webSocketKey;
    private WebSocketClient webSocketClient;

    ////

    private WebSocket() {
        httpClient = VolleyHttpClient.getInstance(AppController.getInstance());
    }

    public static WebSocket getInstance() {
        if (instance == null) instance = new WebSocket();
        return instance;
    }

    ////

    public void connect() {
        httpClient.setWebSocketKeyRequestListener(this);
        httpClient.sendWebSocketKeyRequest();
    }

    public void disconnect() {
        webSocketClient.close();
    }

    ////

    private void connectWebSocket() {
        URI uri;
        try { uri = new URI("ws://rcv1.kgk-global.com:8876/sock"); }
        catch (URISyntaxException e) { e.printStackTrace(); return; }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                try {
                    // TODO Delete test code
                    Log.d("Websocket", "Opened with: " + prepareAuthMessage().toString());
                    webSocketClient.send(prepareAuthMessage().toString()); }
                catch (JSONException e) { e.printStackTrace(); }
            }

            @Override
            public void onMessage(String message) {

                // TODO Delete test code
                Log.d("Websocket", "Message - " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

                // TODO Delete test code
                Log.d("Websocket", "Closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {

                // TODO Delete test code
                Log.d("Websocket", "Error: " + ex.getMessage());
            }
        };

        webSocketClient.connect();
    }

    private JSONObject prepareAuthMessage() throws JSONException {
        JSONObject authMessage = new JSONObject();
        authMessage.put("type", "auth");
        JSONObject key = new JSONObject();
        key.put("key", webSocketKey);
        authMessage.put("data", key);
        return authMessage;
    }

    private String parseWebSocketKey(String rawMessage) throws JSONException {
        JSONObject message = new JSONObject(rawMessage);
        JSONObject data = message.getJSONObject("data");
        return data.getString("webSocketKey");
    }

    ////

    @Override
    public void onWebSocketKeyReceived(String rawMessage) {

        // TODO Delete test code
        Log.d("Websocket", "Web Socket Key:  " + rawMessage);

        try {
            webSocketKey = parseWebSocketKey(rawMessage);
            httpClient.removeWebSocketKeyRequestListener();
            connectWebSocket();
        } catch (JSONException e) { e.printStackTrace(); }
    }
}
