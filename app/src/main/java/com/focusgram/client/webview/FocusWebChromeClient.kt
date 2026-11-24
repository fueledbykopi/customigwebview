package com.focusgram.client.webview

import android.net.Uri
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class FocusWebChromeClient(
    private val onProgressChangedCallback: (Int) -> Unit,
    private val onFileChooserRequested: (
        filePathCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ) -> Boolean,
    private val onEnterFullscreen: (view: View, callback: CustomViewCallback) -> Unit,
    private val onExitFullscreen: () -> Unit
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onProgressChangedCallback(newProgress.coerceIn(0, 100))
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        if (filePathCallback == null || fileChooserParams == null) {
            return false
        }
        return onFileChooserRequested(filePathCallback, fileChooserParams)
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (view != null && callback != null) {
            onEnterFullscreen(view, callback)
        } else {
            super.onShowCustomView(view, callback)
        }
    }

    override fun onHideCustomView() {
        onExitFullscreen()
        super.onHideCustomView()
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        // Deny unsolicited web camera/microphone/DRM permissions by default for privacy
        request?.deny()
    }
}
