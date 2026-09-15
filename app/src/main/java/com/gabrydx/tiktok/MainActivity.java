/*
 * browser.java
 *
 *  Copyleft (C) 2015  Sun Dro
 *
 * Simple web browser source code for android.
 */

package com.gabrydx.tiktok;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private final String MY_PREFS_NAME = "Preferences";
    private WebView Browser;
    private final String tikTokUrl = "https://www.tiktok.com/foryou";

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            view.loadUrl(url);
            saveCookies(url);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            saveCookies(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            saveCookies(url);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);

        Browser = findViewById(R.id.webView1);
        Browser.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = Browser.getSettings();

        // needed for viewing videos
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        // to handle your cache
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(Browser, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        loadCookies();

        Browser.loadUrl(tikTokUrl);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (Browser != null && Browser.canGoBack()) {
                    Browser.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void loadCookies() {
        try {
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String name = prefs.getString("session", "No session found"); // "No session found" is the default value.

            byte[] data = Base64.decode(name, Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveCookies(String url) {
        try {
            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies != null) {
                byte[] data = cookies.getBytes(StandardCharsets.UTF_8);
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("session", base64);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
