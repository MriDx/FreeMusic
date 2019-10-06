package com.mridx.freemusic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AuthHandler {

    public String sendPostRequest(String requestURL) {
        URL url;

        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
           // conn.setRequestProperty("authority", "www.jiosaavn.com");
            //conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0");
            //conn.setRequestProperty("enctype", "multipart/form-data");
            //conn.setRequestProperty("Content-Type", "application/opensearchdescription+xml");
            List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
            for (String cookie : cookies) {
                conn.setRequestProperty("Cookie", cookie);
            }

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

}
