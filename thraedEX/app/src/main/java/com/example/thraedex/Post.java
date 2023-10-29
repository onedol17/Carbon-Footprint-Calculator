package com.example.thraedex;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Post extends AsyncTask<String, Void, String>{
    String path;
    JSONObject response;

    public Post(String path) {
        this.path = path;
        this.response = new JSONObject();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(this.path);
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");
            client.setDoOutput(true);

            try (OutputStream os = client.getOutputStream()) {
                byte[] input = strings[0].getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), "utf-8"))) {
                StringBuilder r = new StringBuilder();
                String responseLine = null;

                while ((responseLine = br.readLine()) != null) {
                    r.append(responseLine.trim());
                }

                response = new JSONObject(r.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("POST ERR", "Post Failed");
        }
        return null;
    }

    public JSONObject getResponse() {
        return response;
    }
}