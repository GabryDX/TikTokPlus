/*
 * Copyright 2024 TikTok+ Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gabrydx.tiktok

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var browser: WebView? = null

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            return handleUrlLoading(view, url)
        }

        @Suppress("DEPRECATION")
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return handleUrlLoading(view, url)
        }

        private fun handleUrlLoading(view: WebView, url: String): Boolean {
            val uri = Uri.parse(url)
            val host = uri.host
            if (host != null && (host.endsWith("tiktok.com") || host.endsWith("tiktokcdn.com"))) {
                // Allow WebView to load the URL
                return false
            }
            // Block or open external links in the default browser to prevent malicious redirects
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                view.context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to open URL", e)
            }
            return true
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

    override fun onDestroy() {
        // Ensure cookies are flushed to persistent storage securely
        CookieManager.getInstance().flush()
        super.onDestroy()
    }

    companion object {
        private const val TIKTOK_URL = "https://www.tiktok.com/foryou"
    }
}
