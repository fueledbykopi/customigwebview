package com.focusgram.client.webview

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.domain.InstagramUrlPolicy
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.Executors

class FocusWebViewClient(
    private val preferencesProvider: () -> FocusPreferences,
    private val isDarkThemeProvider: () -> Boolean,
    private val isAmoledProvider: () -> Boolean,
    private val onPageStartedCallback: (url: String) -> Unit,
    private val onPageFinishedCallback: (url: String, canGoBack: Boolean) -> Unit,
    private val onMainFrameErrorCallback: (errorCode: Int, description: String, failingUrl: String) -> Unit,
    private val onOpenExternalUrl: (url: String) -> Unit,
    private val onOpenExternalUri: (uri: Uri) -> Unit,
    private val onTrackerBlocked: () -> Unit,
    private val onGhostActionBlocked: (kind: String) -> Unit,
    private val onReelsBlocked: () -> Unit,
    private val onRenderProcessGoneCallback: () -> Unit
) : WebViewClient() {

    private val cookieFlushExecutor = Executors.newSingleThreadExecutor()

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url?.toString() ?: return super.shouldInterceptRequest(view, request)
        val prefs = preferencesProvider()

        // 1. Serve user's cropped Chat (DM) & Timeline photo backgrounds from local storage on same-origin virtual URLs
        if (InstagramUrlPolicy.isCustomMediaAssetUrl(url)) {
            val path = request.url?.path.orEmpty()
            val filePath = when {
                path.startsWith(InstagramUrlPolicy.CHAT_BG_VIRTUAL_PATH) -> prefs.chatBackgroundPath
                path.startsWith(InstagramUrlPolicy.TIMELINE_BG_VIRTUAL_PATH) -> prefs.timelineBackgroundPath
                else -> ""
            }
            if (filePath.isNotBlank()) {
                val file = File(filePath)
                if (file.exists() && file.canRead()) {
                    val mime = if (file.name.endsWith(".png", ignoreCase = true)) "image/png" else "image/jpeg"
                    val headers = mapOf(
                        "Cache-Control" to "no-store, no-cache, must-revalidate",
                        "Access-Control-Allow-Origin" to "*"
                    )
                    return runCatching {
                        WebResourceResponse(mime, null, 200, "OK", headers, FileInputStream(file))
                    }.getOrNull()
                }
            }
            return WebResourceResponse("text/plain", "utf-8", 404, "Not Found", emptyMap(), ByteArrayInputStream(ByteArray(0)))
        }

        // 2. Ghost Mode network-layer protection (Story seen & DM read-receipt / typing endpoints)
        if (prefs.isGhostStoryActive && InstagramUrlPolicy.isStorySeenUrl(url)) {
            onGhostActionBlocked("story")
            return syntheticOkJsonResponse()
        }
        if (prefs.isGhostDmActive && InstagramUrlPolicy.isDmSeenOrTypingUrl(url)) {
            onGhostActionBlocked("dm")
            return syntheticOkJsonResponse()
        }

        // 3. Tracker & telemetry blocker
        if (prefs.blockTrackers && InstagramUrlPolicy.isTrackerOrAdUrl(url)) {
            onTrackerBlocked()
            return WebResourceResponse(
                "text/plain",
                "utf-8",
                ByteArrayInputStream(ByteArray(0))
            )
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val uri = request?.url ?: return false
        val rawUrl = uri.toString()

        // Handle non-HTTP schemes cleanly (intent://, mailto:, tel:, etc.)
        if (!InstagramUrlPolicy.isHttpOrHttps(rawUrl)) {
            if (preferencesProvider().hideOpenInAppPrompts && rawUrl.startsWith("intent://")) {
                return true
            }
            onOpenExternalUri(uri)
            return true
        }

        val resolvedUrl = InstagramUrlPolicy.unwrapRedirectUrl(rawUrl)

        // Block Reels route when Hide Reels is enabled (allow individual /reel/ post taps coming from Explore)
        val currentUrl = view?.url.orEmpty()
        val isFromExplore = InstagramUrlPolicy.isExploreRoute(currentUrl)
        if (preferencesProvider().hideReels && InstagramUrlPolicy.isReelsRoute(resolvedUrl) && !isFromExplore) {
            onReelsBlocked()
            return true
        }

        if (InstagramUrlPolicy.isInternalUrl(resolvedUrl)) {
            if (resolvedUrl != rawUrl) {
                view?.loadUrl(resolvedUrl)
                return true
            }
            return false
        }

        // Open external URLs in Chrome Custom Tabs / external browser
        onOpenExternalUrl(resolvedUrl)
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        if (!url.isNullOrBlank()) {
            onPageStartedCallback(url)
        }
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
        if (view != null) {
            injectCleaners(view)
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (view != null) {
            injectCleaners(view)
            cookieFlushExecutor.execute {
                runCatching { CookieManager.getInstance().flush() }
            }
            onPageFinishedCallback(url ?: view.url.orEmpty(), view.canGoBack())
        }
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        if (request?.isForMainFrame == true) {
            val code = error?.errorCode ?: ERROR_UNKNOWN
            val desc = error?.description?.toString().orEmpty()
            val failingUrl = request.url?.toString().orEmpty()
            onMainFrameErrorCallback(code, desc, failingUrl)
        }
    }

    override fun onRenderProcessGone(
        view: WebView?,
        detail: RenderProcessGoneDetail?
    ): Boolean {
        onRenderProcessGoneCallback()
        return true
    }

    private fun injectCleaners(webView: WebView) {
        val script = InstagramScriptInjector.buildInjectionScript(
            prefs = preferencesProvider(),
            isDarkTheme = isDarkThemeProvider(),
            isAmoled = isAmoledProvider()
        )
        webView.evaluateJavascript(script, null)
    }

    private fun syntheticOkJsonResponse(): WebResourceResponse {
        val payload = """{"data":{},"status":"ok"}""".toByteArray(Charsets.UTF_8)
        return WebResourceResponse(
            "application/json",
            "utf-8",
            200,
            "OK",
            mapOf("Cache-Control" to "no-store"),
            ByteArrayInputStream(payload)
        )
    }
}
