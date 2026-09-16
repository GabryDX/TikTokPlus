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
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import com.gabrydx.tiktok.databinding.ActivityWebBrowserBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBrowserBinding
    private var browser: WebView? = null
    
    // For File Uploads
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    
    // For Fullscreen Video
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

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
            val uri = url.toUri()
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

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress < 100) {
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = newProgress
            } else {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            super.onShowCustomView(view, callback)
            if (customView != null) {
                callback?.onCustomViewHidden()
                return
            }
            customView = view
            customViewCallback = callback
            binding.fullscreenContainer.addView(view)
            binding.fullscreenContainer.visibility = View.VISIBLE
            binding.swipeRefreshLayout.visibility = View.GONE
        }

        override fun onHideCustomView() {
            super.onHideCustomView()
            if (customView == null) return
            binding.fullscreenContainer.visibility = View.GONE
            binding.fullscreenContainer.removeView(customView)
            customView = null
            customViewCallback?.onCustomViewHidden()
            customViewCallback = null
            binding.swipeRefreshLayout.visibility = View.VISIBLE
        }
        
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            fileUploadCallback?.onReceiveValue(null)
            fileUploadCallback = filePathCallback
            
            val intent = fileChooserParams?.createIntent()
            try {
                if (intent != null) {
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE)
                }
            } catch (e: Exception) {
                fileUploadCallback = null
                return false
            }
            return true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWebBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        browser = binding.webView1
        browser?.webViewClient = MyWebViewClient()
        browser?.webChromeClient = MyWebChromeClient()
        val webSettings = browser?.settings

        // needed for viewing videos
        webSettings?.javaScriptEnabled = true
        webSettings?.domStorageEnabled = true
        // to handle your cache
        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT

        CookieManager.getInstance().setAcceptThirdPartyCookies(browser, true)

        binding.swipeRefreshLayout.setOnRefreshListener {
            browser?.reload()
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (fileUploadCallback == null) return
            val result = if (data == null || resultCode != RESULT_OK) null else data.data
            val results = if (result != null) arrayOf(result) else null
            fileUploadCallback?.onReceiveValue(results)
            fileUploadCallback = null
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val TIKTOK_URL = "https://www.tiktok.com/foryou"
        private const val FILE_CHOOSER_REQUEST_CODE = 100
    }
}
