package com.focusgram.client.ui.components

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.webview.FocusWebChromeClient
import com.focusgram.client.webview.FocusWebViewClient
import com.focusgram.client.webview.InstagramJsBridge
import com.focusgram.client.webview.InstagramScriptInjector
import com.focusgram.client.webview.WebViewController

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    initialUrl: String,
    controller: WebViewController,
    preferences: FocusPreferences,
    isDarkTheme: Boolean,
    isAmoled: Boolean,
    isBackgroundTransparent: Boolean,
    fallbackBackgroundColor: Int,
    onPageStarted: (String) -> Unit,
    onPageProgress: (Int) -> Unit,
    onPageFinished: (String, Boolean) -> Unit,
    onSpaUrlChanged: (String) -> Unit,
    onMainFrameError: (Int, String, String) -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    onOpenExternalUri: (Uri) -> Unit,
    onDownloadRequested: (url: String, userAgent: String, contentDisposition: String, mimeType: String) -> Unit,
    onFileChooserRequested: (
        ValueCallback<Array<Uri>>,
        WebChromeClient.FileChooserParams
    ) -> Boolean,
    onEnterFullscreen: (View, WebChromeClient.CustomViewCallback) -> Unit,
    onExitFullscreen: () -> Unit,
    onTrackerBlocked: () -> Unit,
    onDistractionBlocked: (Int) -> Unit,
    onReelsBlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latestPreferences by rememberUpdatedState(preferences)
    val latestIsDark by rememberUpdatedState(isDarkTheme)
    val latestIsAmoled by rememberUpdatedState(isAmoled)
    val latestTransparent by rememberUpdatedState(isBackgroundTransparent)
    val latestFallbackColor by rememberUpdatedState(fallbackBackgroundColor)

    // Live-update injected CSS/JS, dark mode, transparency, and pull-to-refresh without recreating WebView
    LaunchedEffect(preferences, isDarkTheme, isAmoled, isBackgroundTransparent, fallbackBackgroundColor) {
        controller.applyDarkening(isDarkTheme)
        controller.applyBackgroundTransparency(isBackgroundTransparent, fallbackBackgroundColor)
        controller.setPullToRefreshEnabled(preferences.enablePullToRefresh)
        controller.applyInjections(preferences, isDarkTheme, isAmoled)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val initialBg = if (latestTransparent) Color.TRANSPARENT else latestFallbackColor
            val swipeRefresh = SwipeRefreshLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(initialBg)
                isEnabled = latestPreferences.enablePullToRefresh
            }

            val webView = WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(initialBg)
                overScrollMode = View.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    loadsImagesAutomatically = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    builtInZoomControls = false
                    displayZoomControls = false
                    setSupportZoom(false)
                    mediaPlaybackRequiresUserGesture = false
                    allowFileAccess = false
                    allowContentAccess = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    cacheMode = WebSettings.LOAD_DEFAULT
                    userAgentString = WebViewController.MOBILE_USER_AGENT
                }

                if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
                    WebSettingsCompat.setSafeBrowsingEnabled(settings, true)
                }
                if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, latestIsDark)
                }

                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)

                addJavascriptInterface(
                    InstagramJsBridge(
                        onUrlChangedCallback = onSpaUrlChanged,
                        onDistractionBlockedCallback = onDistractionBlocked
                    ),
                    InstagramScriptInjector.BRIDGE_NAME
                )

                webViewClient = FocusWebViewClient(
                    preferencesProvider = { latestPreferences },
                    isDarkThemeProvider = { latestIsDark },
                    isAmoledProvider = { latestIsAmoled },
                    onPageStartedCallback = onPageStarted,
                    onPageFinishedCallback = { url, canGoBack ->
                        swipeRefresh.isRefreshing = false
                        onPageFinished(url, canGoBack)
                    },
                    onMainFrameErrorCallback = { code, desc, url ->
                        swipeRefresh.isRefreshing = false
                        onMainFrameError(code, desc, url)
                    },
                    onOpenExternalUrl = onOpenExternalUrl,
                    onOpenExternalUri = onOpenExternalUri,
                    onTrackerBlocked = onTrackerBlocked,
                    onReelsBlocked = onReelsBlocked
                )

                webChromeClient = FocusWebChromeClient(
                    onProgressChangedCallback = onPageProgress,
                    onFileChooserRequested = onFileChooserRequested,
                    onEnterFullscreen = onEnterFullscreen,
                    onExitFullscreen = onExitFullscreen
                )

                setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
                    onDownloadRequested(url, userAgent, contentDisposition, mimeType)
                }

                loadUrl(initialUrl)
            }

            swipeRefresh.addView(webView)
            swipeRefresh.setOnChildScrollUpCallback { _, _ ->
                webView.scrollY > 0
            }
            swipeRefresh.setOnRefreshListener {
                webView.reload()
            }

            controller.attach(webView, swipeRefresh)
            swipeRefresh
        },
        update = { swipeRefresh ->
            swipeRefresh.isEnabled = preferences.enablePullToRefresh
        }
    )
}
