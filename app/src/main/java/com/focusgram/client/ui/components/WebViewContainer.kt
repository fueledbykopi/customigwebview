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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.webview.FocusWebChromeClient
import com.focusgram.client.webview.FocusWebViewClient
import com.focusgram.client.webview.InstagramJsBridge
import com.focusgram.client.webview.InstagramScriptInjector
import com.focusgram.client.webview.WebViewController

private data class WebInjectionSignature(
    val hideOpenInAppPrompts: Boolean,
    val hideReels: Boolean,
    val hideExploreFeed: Boolean,
    val hideStories: Boolean,
    val hideSuggestedAndAds: Boolean,
    val hideEngagementCounts: Boolean,
    val autoMuteVideos: Boolean,
    val grayscaleMode: Boolean,
    val ghostModeEnabled: Boolean,
    val ghostViewStories: Boolean,
    val ghostReadDm: Boolean,
    val useNativeBottomBar: Boolean,
    val appThemeStyle: com.focusgram.client.data.AppThemeStyle,
    val themeMode: com.focusgram.client.data.ThemeMode,
    val chatBackgroundPath: String,
    val timelineBackgroundPath: String,
    val backgroundDimOpacity: Float,
    val isDarkTheme: Boolean,
    val isAmoled: Boolean
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    initialUrl: String,
    webViewGeneration: Int,
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
    onGhostActionBlocked: (String) -> Unit,
    onReelsBlocked: () -> Unit,
    onRenderProcessGone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latestPreferences by rememberUpdatedState(preferences)
    val latestIsDark by rememberUpdatedState(isDarkTheme)
    val latestIsAmoled by rememberUpdatedState(isAmoled)
    val latestTransparent by rememberUpdatedState(isBackgroundTransparent)
    val latestFallbackColor by rememberUpdatedState(fallbackBackgroundColor)
    var canPullToRefreshFromJs by remember { mutableStateOf(true) }

    val injectionSignature = remember(preferences, isDarkTheme, isAmoled) {
        WebInjectionSignature(
            hideOpenInAppPrompts = preferences.hideOpenInAppPrompts,
            hideReels = preferences.hideReels,
            hideExploreFeed = preferences.hideExploreFeed,
            hideStories = preferences.hideStories,
            hideSuggestedAndAds = preferences.hideSuggestedAndAds,
            hideEngagementCounts = preferences.hideEngagementCounts,
            autoMuteVideos = preferences.autoMuteVideos,
            grayscaleMode = preferences.grayscaleMode,
            ghostModeEnabled = preferences.ghostModeEnabled,
            ghostViewStories = preferences.ghostViewStories,
            ghostReadDm = preferences.ghostReadDm,
            useNativeBottomBar = preferences.useNativeBottomBar,
            appThemeStyle = preferences.appThemeStyle,
            themeMode = preferences.themeMode,
            chatBackgroundPath = preferences.chatBackgroundPath,
            timelineBackgroundPath = preferences.timelineBackgroundPath,
            backgroundDimOpacity = preferences.backgroundDimOpacity,
            isDarkTheme = isDarkTheme,
            isAmoled = isAmoled
        )
    }

    // Only re-inject JS/CSS when web-affecting preferences actually change (avoids unnecessary JS stalls)
    LaunchedEffect(injectionSignature, webViewGeneration) {
        controller.applyDarkening()
        controller.applyInjections(preferences, isDarkTheme, isAmoled)
    }

    LaunchedEffect(isBackgroundTransparent, fallbackBackgroundColor, webViewGeneration) {
        controller.applyBackgroundTransparency(isBackgroundTransparent, fallbackBackgroundColor)
    }

    LaunchedEffect(preferences.enablePullToRefresh, webViewGeneration) {
        controller.setPullToRefreshEnabled(preferences.enablePullToRefresh)
    }

    key(webViewGeneration) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val initialBg = if (latestTransparent) Color.TRANSPARENT else latestFallbackColor
                val swipeRefresh = SwipeRefreshLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    if (latestTransparent) background = null
                    setBackgroundColor(initialBg)
                    isEnabled = latestPreferences.enablePullToRefresh
                }

                val webView = WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    if (latestTransparent) background = null
                    setBackgroundColor(initialBg)
                    overScrollMode = View.OVER_SCROLL_NEVER
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)

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
                        WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, false)
                    }
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
                        runCatching {
                            WebViewCompat.addDocumentStartJavaScript(
                                this,
                                InstagramScriptInjector.buildDocumentStartScript(latestPreferences),
                                setOf(
                                    "https://www.instagram.com",
                                    "https://instagram.com",
                                    "https://m.instagram.com"
                                )
                            )
                        }
                    }

                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    addJavascriptInterface(
                        InstagramJsBridge(
                            onUrlChangedCallback = onSpaUrlChanged,
                            onDistractionBlockedCallback = onDistractionBlocked,
                            onGhostActionBlockedCallback = onGhostActionBlocked,
                            onPullToRefreshAllowedCallback = { allowed ->
                                canPullToRefreshFromJs = allowed
                            }
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
                        onGhostActionBlocked = onGhostActionBlocked,
                        onReelsBlocked = onReelsBlocked,
                        onRenderProcessGoneCallback = onRenderProcessGone
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
                    webView.scrollY > 0 || !canPullToRefreshFromJs
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
}
