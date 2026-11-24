package com.focusgram.client.webview

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class InstagramJsBridge(
    private val onUrlChangedCallback: (String) -> Unit,
    private val onDistractionBlockedCallback: (Int) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onUrlChanged(url: String) {
        if (url.isBlank()) return
        mainHandler.post {
            onUrlChangedCallback(url)
        }
    }

    @JavascriptInterface
    fun onDistractionBlocked(count: Int) {
        if (count <= 0) return
        mainHandler.post {
            onDistractionBlockedCallback(count)
        }
    }
}
