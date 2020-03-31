package launcher.hotelrobot.com.testsdk.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import launcher.hotelrobot.com.testsdk.MainActivity;
import tech.gusavila92.websocketclient.WebSocketClient;

public class WebSocket {

    public static WebSocketClient webSocketClient;

    public static WebSocketClient createWebSocketClient() {
        URI uri;
        try {
            // Connect to local host
            uri = new URI("ws://192.168.10.201:5000");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
            }
            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received");
                final String message = s;
                Log.d("response", s);
                try{
                    JSONObject obj = new JSONObject(s);
                    JSONArray faces = (JSONArray) obj.get("faces-detected");
                    List<String> face_list = new ArrayList<String>();
                    for (int i=0; i<faces.length(); i++) {
                        face_list.add( faces.getString(i) );
                    }
                    MainActivity.processFaces(face_list);
                    Log.d("hi","hi");
                } catch (Exception e){

                }
//                callback.processFaces();
            }
            @Override
            public void onBinaryReceived(byte[] data) {
            }
            @Override
            public void onPingReceived(byte[] data) {
            }
            @Override
            public void onPongReceived(byte[] data) {
            }
            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }
            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
            }
        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(500);
        webSocketClient.connect();
        return webSocketClient;
    }
}
