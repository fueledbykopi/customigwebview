package com.focusgram.client.webview

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class InstagramJsBridge(
    private val onUrlChangedCallback: (String) -> Unit,
    private val onDistractionBlockedCallback: (Int) -> Unit,
    private val onGhostActionBlockedCallback: (String) -> Unit = {},
    private val onPullToRefreshAllowedCallback: (Boolean) -> Unit = {}
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

    @JavascriptInterface
    fun onGhostActionBlocked(kind: String) {
        if (kind.isBlank()) return
        mainHandler.post {
            onGhostActionBlockedCallback(kind)
        }
    }

    @JavascriptInterface
    fun onPullToRefreshAllowed(allowed: Boolean) {
        mainHandler.post {
            onPullToRefreshAllowedCallback(allowed)
        }
    }
}
