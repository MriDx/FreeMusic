package com.mridx.freemusic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class JsonRequestHandler {

    public String sendPostRequest(String requestURL) {
        URL url;

        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("enctype", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Type", "application/opensearchdescription+xml");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;

                while ((response = br.readLine()) != null) {
                    sb.append(response);
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("error", "false");
                jsonObject.put("title", "Server offline");
                jsonObject.put("message", "Sorry! Currently our server is offline.\nPlease come back later!");
                jsonObject.put("button", "OK");
                jsonObject.put("if", "off");
            } catch (JSONException k) {
                k.printStackTrace();
            }
            return String.valueOf(jsonObject);

        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));

        }
        return result.toString();
    }

}
