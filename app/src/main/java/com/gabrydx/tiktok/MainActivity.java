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
import android.util.Base64;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.webkit.WebView;
import android.widget.Button;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.io.UnsupportedEncodingException;


public class MainActivity extends Activity
{
    private final String MY_PREFS_NAME = "Preferences";
    private WebView Browser;
    private String tikTokUrl = "https://www.tiktok.com/foryou";

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            saveCookies(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url){
            saveCookies(url);
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);

        Browser = (WebView) findViewById(R.id.webView1);
        Browser.setWebViewClient(new WebViewClient());
        WebSettings webSettings = Browser.getSettings();
        // needed for viewing videos
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        //to handle your cache
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        //webSettings.setAppCachePath(cacheDir.path);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(Browser, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

        loadCookies();

        Browser.loadUrl(tikTokUrl);

    }

    @Override
    public void onBackPressed() {
        if(Browser.canGoBack()) {
            Browser.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void loadCookies() {
        try {
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String name = prefs.getString("session", "No session found"); //"No session found" is the default value.

            byte[] data = Base64.decode(name, Base64.DEFAULT);
            String text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void saveCookies(String url) {
        try {
            String cookies = CookieManager.getInstance().getCookie(url);
            byte[] data = cookies.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("session", base64);
            editor.apply();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
