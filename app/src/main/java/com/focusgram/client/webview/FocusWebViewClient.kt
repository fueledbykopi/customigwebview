package com.focusgram.client.webview

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.domain.InstagramUrlPolicy
import java.io.ByteArrayInputStream

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
    private val onReelsBlocked: () -> Unit
) : WebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url?.toString() ?: return super.shouldInterceptRequest(view, request)
        val prefs = preferencesProvider()
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
            // Suppress Instagram app-launch intent:// links when hideOpenInAppPrompts is active
            if (preferencesProvider().hideOpenInAppPrompts && rawUrl.startsWith("intent://")) {
                return true
            }
            onOpenExternalUri(uri)
            return true
        }

        val resolvedUrl = InstagramUrlPolicy.unwrapRedirectUrl(rawUrl)

        // Block Reels route when Hide Reels is enabled
        if (preferencesProvider().hideReels && InstagramUrlPolicy.isReelsRoute(resolvedUrl)) {
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
            runCatching { CookieManager.getInstance().flush() }
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

    private fun injectCleaners(webView: WebView) {
        val script = InstagramScriptInjector.buildInjectionScript(
            prefs = preferencesProvider(),
            isDarkTheme = isDarkThemeProvider(),
            isAmoled = isAmoledProvider()
        )
        webView.evaluateJavascript(script, null)
    }
}
