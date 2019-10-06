package com.mridx.freemusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CookieManager.getInstance().setAcceptCookie(true);

        WebView webView = findViewById(R.id.webview);
        WebViewClient webViewClient = new WebViewClient();
        webView.getSettings().setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0");
        webView.loadUrl("https://www.jiosaavn.com/api.php?__call=song.generateAuthToken&_marker=0&_format=json&bitrate=320&url=NMKyboFo/Fij1ArI3T/z/LmnO3dJSpyqWVxAgcZxmVepg8yngMCjIq98fSzvaHVx");


        final String coded = "ID2ieOjCrwfgWvL5sXl4B1ImC5QfbsDyG21LbRvd2D8Q+GhhwSV5XQXeiN4vyOLkJ2Snb3w11rMSMHqVDAWAgxw7tS9a8Gtq";

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, MainUI.class));
                some();
            }
        });


    }

    private void decode() {

        //Base64.decode("ID2ieOjCrwfgWvL5sXl4B1ImC5QfbsDyG21LbRvd2D8Q+GhhwSV5XQXeiN4vyOLkJ2Snb3w11rMSMHqVDAWAgxw7tS9a8Gtq", null, null)

    }
    public void  some() {

        String finString;
        String finUrl = "https://www.jiosaavn.com/api.php?__call=song.generateAuthToken&_marker=0&_format=json&bitrate=320&url=NMKyboFo/Fij1ArI3T/z/LmnO3dJSpyqWVxAgcZxmVepg8yngMCjIq98fSzvaHVx";

        StrictMode.setThreadPolicy(new Builder().permitAll().build());
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(finUrl);
        List<String> cookie = (List<String>) httpclient.getCookieStore();
        httpget.setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:49.0) Gecko/20100101 Firefox/49.0");
        for (String coo :  cookie) {
            httpget.setHeader("Cookie", coo);
        }
        try {
            InputStream is = httpclient.execute(httpget).getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(line);
                stringBuilder.append("\n");
                sb.append(stringBuilder.toString());
            }
            finString = sb.toString();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private String decodeBase64(String coded){
        byte[] valueDecoded= new byte[0];
        try {
            valueDecoded = Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
        }

        Toast.makeText(MainActivity.this, new String(valueDecoded), Toast.LENGTH_LONG).show();
        return new String(valueDecoded);


    }
}
