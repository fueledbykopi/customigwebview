package com.focusgram.client.webview

import android.graphics.Color
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.data.InstagramSection
import com.focusgram.client.domain.InstagramUrlPolicy
import java.lang.ref.WeakReference

class WebViewController {

    private var webViewRef: WeakReference<WebView>? = null
    private var swipeRefreshRef: WeakReference<SwipeRefreshLayout>? = null

    val webView: WebView?
        get() = webViewRef?.get()

    fun attach(webView: WebView, swipeRefreshLayout: SwipeRefreshLayout) {
        webViewRef = WeakReference(webView)
        swipeRefreshRef = WeakReference(swipeRefreshLayout)
    }

    fun detach() {
        webViewRef = null
        swipeRefreshRef = null
    }

    fun loadUrl(url: String) {
        webView?.loadUrl(url)
    }

    /**
     * Uses fast in-DOM SPA navigation for instant tab switches (zero full-page reload)
     * with automatic fallback to direct URL loading if the SPA link isn't present.
     */
    fun navigateToSection(section: InstagramSection, currentSection: InstagramSection) {
        val wv = webView ?: return
        if (section == currentSection && section == InstagramSection.HOME) {
            scrollToTop()
            return
        }
        when (section) {
            InstagramSection.HOME -> wv.evaluateJavascript(
                InstagramScriptInjector.buildSpaNavigateScript("/", InstagramUrlPolicy.HOME_URL),
                null
            )
            InstagramSection.SEARCH -> wv.evaluateJavascript(
                InstagramScriptInjector.buildSpaNavigateScript("/explore/", InstagramUrlPolicy.EXPLORE_URL),
                null
            )
            InstagramSection.REELS -> wv.evaluateJavascript(
                InstagramScriptInjector.buildSpaNavigateScript("/reels/", InstagramUrlPolicy.REELS_URL),
                null
            )
            InstagramSection.DIRECT -> wv.evaluateJavascript(
                InstagramScriptInjector.buildSpaNavigateScript("/direct/inbox/", InstagramUrlPolicy.DIRECT_URL),
                null
            )
            InstagramSection.ACTIVITY -> wv.evaluateJavascript(
                InstagramScriptInjector.buildSpaNavigateScript("/accounts/activity/", InstagramUrlPolicy.ACTIVITY_URL),
                null
            )
            InstagramSection.PROFILE -> {
                wv.evaluateJavascript(InstagramScriptInjector.buildNavigateToProfileScript(), null)
            }
            InstagramSection.OTHER -> wv.loadUrl(InstagramUrlPolicy.HOME_URL)
        }
    }

    fun reload() {
        webView?.reload()
    }

    fun goBack(): Boolean {
        val wv = webView ?: return false
        return if (wv.canGoBack()) {
            wv.goBack()
            true
        } else {
            false
        }
    }

    fun scrollToTop() {
        webView?.evaluateJavascript(
            "window.scrollTo({ top: 0, behavior: 'smooth' });",
            null
        )
    }

    fun finishRefreshing() {
        swipeRefreshRef?.get()?.isRefreshing = false
    }

    fun setPullToRefreshEnabled(enabled: Boolean) {
        swipeRefreshRef?.get()?.isEnabled = enabled
    }

    fun applyInjections(
        prefs: FocusPreferences,
        isDarkTheme: Boolean,
        isAmoled: Boolean
    ) {
        val wv = webView ?: return
        val script = InstagramScriptInjector.buildInjectionScript(
            prefs = prefs,
            isDarkTheme = isDarkTheme,
            isAmoled = isAmoled
        )
        wv.evaluateJavascript(script, null)
    }

    /**
     * Disables WebView's algorithmic auto-darkening because FocusGram applies exact CSS variables
     * and Instagram's native __fb-dark-mode / __fb-light-mode classes for every theme without color inversion.
     */
    fun applyDarkening() {
        val wv = webView ?: return
        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(wv.settings, false)
        }
    }

    fun applyBackgroundTransparency(isTransparent: Boolean, fallbackColor: Int) {
        val color = if (isTransparent) Color.TRANSPARENT else fallbackColor
        webView?.let { wv ->
            if (isTransparent) {
                wv.background = null
            }
            wv.setBackgroundColor(color)
        }
        swipeRefreshRef?.get()?.let { sr ->
            if (isTransparent) {
                sr.background = null
            }
            sr.setBackgroundColor(color)
        }
    }

    fun clearCacheKeepSession() {
        webView?.clearCache(true)
    }

    fun clearAllSessionData(onCleared: () -> Unit) {
        val wv = webView
        wv?.clearCache(true)
        wv?.clearHistory()
        wv?.clearFormData()
        WebStorage.getInstance().deleteAllData()
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies {
            cookieManager.flush()
            wv?.loadUrl(InstagramUrlPolicy.HOME_URL)
            onCleared()
        }
    }

    fun trimMemory() {
        // Clears in-memory WebView caches only (false = keep disk cache & login session intact)
        webView?.clearCache(false)
    }

    fun onPause() {
        Thread {
            runCatching { CookieManager.getInstance().flush() }
        }.start()
        webView?.onPause()
    }

    fun onResume() {
        webView?.onResume()
    }

    fun destroy() {
        webView?.let { wv ->
            wv.stopLoading()
            wv.removeJavascriptInterface(InstagramScriptInjector.BRIDGE_NAME)
            wv.destroy()
        }
        detach()
    }

    companion object {
        // Modern mobile Chrome User-Agent without the "; wv)" token that degrades web features
        const val MOBILE_USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36"
    }
}
