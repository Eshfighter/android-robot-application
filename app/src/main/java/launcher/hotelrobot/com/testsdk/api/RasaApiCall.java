package launcher.hotelrobot.com.testsdk.api;

import java.io.BufferedReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.robot.performlib.action.SpeakAction;

import org.json.JSONObject;

import launcher.hotelrobot.com.testsdk.model.RasaObject;
import launcher.hotelrobot.com.testsdk.interfaces.AsyncResponse;

import com.google.gson.Gson;

public class RasaApiCall extends AsyncTask<String, String, String> {

    private Activity context;

    public AsyncResponse callback = null;

    @Override
    protected void onPostExecute(String result) {
        Gson gson = new Gson();
        RasaObject[] rasaArray = gson.fromJson(result, RasaObject[].class);
        String text = rasaArray[0].getText();
        callback.processResult(text);
    }

    @Override
    protected String doInBackground(String... params) {

        URL url;
        String response = "";
        try {
            url = new URL(params[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");

            JSONObject json = new JSONObject();
            json.put("message",params[1]);
            json.put("sender",params[2]);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(json.toString());
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
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}
