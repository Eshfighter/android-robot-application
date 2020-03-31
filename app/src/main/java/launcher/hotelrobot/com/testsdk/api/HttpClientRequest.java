package launcher.hotelrobot.com.testsdk.api;

import launcher.hotelrobot.com.testsdk.model.RasaObject;
import launcher.hotelrobot.com.testsdk.model.faceDetect;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import launcher.hotelrobot.com.testsdk.interfaces.AsyncResponse;

public class HttpClientRequest extends AsyncTask<String, String, String> {

    public AsyncResponse callback = null;

    // one instance, reuse
    private final OkHttpClient httpClient = new OkHttpClient();

    public String sendGet(String url) throws Exception {

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body

            System.out.println(response.body().string());
            return response.body().string();
        }

    }

    @Override
    protected void onPostExecute(String result) {
//        Gson gson = new Gson();
//        faceDetect[] faceArray = gson.fromJson(result, faceDetect[].class);
//        String[] faces = faceArray[0].getFaces();
        List<String> list = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray face = jsonObject.getJSONArray("faces-detected");
            list = new ArrayList<String>();
            for (int i = 0; i < face.length(); i++) {
                list.add((String) face.get(i));
            }
            Log.d("hi", "hi");
//            callback.processFaces(list);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String response = "";
        try{
            URL url = new URL(params[0]);
            String Base64String = params[1];
//            JSONObject faceObject = new JSONObject();
//            faceObject.put("base64String",Base64String);
//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            RequestBody body = RequestBody.create(JSON, faceObject.toString());
//            Request request = new Request.Builder()
//                    .url(url)
//                    .addHeader("Content-Type", "application/json")
//                    .post(body)
//                    .build();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");

            JSONObject faceObject = new JSONObject();
            faceObject.put("base64String",Base64String);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(faceObject.toString());
            os.flush();

            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        } catch (Exception e) {
            response="";
        }
        return response;
    }

    public String sendPost(String url, JSONObject formBody) throws Exception {

        // form parameters
//        RequestBody formBody = new FormBody.Builder()
//                .add("username", "abc")
//                .add("password", "123")
//                .add("custom", "secret")
//                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, formBody.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            System.out.println(response.body().string());
            return response.body().string();
        } catch (Exception e){
            Log.d("error",e.toString());
            return e.toString();
        }

    }

}
