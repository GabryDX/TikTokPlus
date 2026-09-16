package com.gabrydx.tiktok

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private var browser: WebView? = null

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            view.loadUrl(url)
            saveCookies(url)
            return true
        }

        @Suppress("DEPRECATION")
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            saveCookies(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            saveCookies(url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_browser)

        browser = findViewById(R.id.webView1)
        browser?.webViewClient = MyWebViewClient()
        val webSettings = browser?.settings

        // needed for viewing videos
        webSettings?.javaScriptEnabled = true
        webSettings?.domStorageEnabled = true
        // to handle your cache
        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT

        CookieManager.getInstance().setAcceptThirdPartyCookies(browser, true)

        loadCookies()

        browser?.loadUrl(TIKTOK_URL)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val b = browser
                if (b != null && b.canGoBack()) {
                    b.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun loadCookies() {
        try {
            val prefs: SharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
            val name = prefs.getString("session", "No session found") // "No session found" is the default value.

            if (name != null && name != "No session found") {
                val data = Base64.decode(name, Base64.DEFAULT)
                val text = String(data, StandardCharsets.UTF_8)
                Log.d("MainActivity", "Loaded cookies: $text")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading cookies", e)
        }
    }

    private fun saveCookies(url: String) {
        try {
            val cookies = CookieManager.getInstance().getCookie(url)
            if (cookies != null) {
                val data = cookies.toByteArray(StandardCharsets.UTF_8)
                val base64 = Base64.encodeToString(data, Base64.DEFAULT)

                val editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
                editor.putString("session", base64)
                editor.apply()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error saving cookies", e)
        }
    }

    companion object {
        private const val MY_PREFS_NAME = "Preferences"
        private const val TIKTOK_URL = "https://www.tiktok.com/foryou"
    }
}
