package com.islamdidarmd.adblockerwebviewsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.islamdidarmd.adblockerwebview.AdBlockerWebView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView = findViewById<AdBlockerWebView>(R.id.webview)
        webView.setAdBlockEnabled(true)
        webView.loadUrl("https://google.com")
    }
}
