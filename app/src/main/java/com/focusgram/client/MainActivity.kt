package com.focusgram.client

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusgram.client.domain.InstagramUrlPolicy
import com.focusgram.client.ui.FocusGramViewModel
import com.focusgram.client.ui.screens.MainScreen
import com.focusgram.client.ui.theme.FocusGramTheme
import com.focusgram.client.ui.theme.resolveDarkTheme
import com.focusgram.client.webview.WebViewController

class MainActivity : ComponentActivity() {

    private val app: FocusGramApp
        get() = application as FocusGramApp

    private val viewModel: FocusGramViewModel by viewModels {
        FocusGramViewModel.Factory(
            preferencesRepository = app.preferencesRepository,
            networkMonitor = app.networkMonitor
        )
    }

    private val webViewController = WebViewController()

    // File upload callback for Instagram Posts / Stories / DM media picker
    private var pendingFilePathCallback: ValueCallback<Array<Uri>>? = null

    // Fullscreen video state
    private var fullscreenViewState by mutableStateOf<View?>(null)
    private var fullscreenCustomViewCallback: WebChromeClient.CustomViewCallback? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val callback = pendingFilePathCallback
        pendingFilePathCallback = null
        if (callback == null) return@registerForActivityResult

        val data = result.data
        val uris: Array<Uri>? = when {
            result.resultCode != RESULT_OK || data == null -> null
            data.clipData != null -> {
                val clip = data.clipData!!
                Array(clip.itemCount) { index -> clip.getItemAt(index).uri }
            }
            data.data != null -> arrayOf(data.data!!)
            else -> WebChromeClient.FileChooserParams.parseResult(result.resultCode, data)
        }
        callback.onReceiveValue(uris)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val startUrl = InstagramUrlPolicy.normalizeDeepLink(intent?.dataString)

        setContent {
            val preferences by viewModel.preferences.collectAsStateWithLifecycle()
            val isDark = resolveDarkTheme(preferences.themeMode)

            LaunchedEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    },
                    navigationBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
            }

            FocusGramTheme(themeMode = preferences.themeMode) {
                MainScreen(
                    initialUrl = startUrl,
                    viewModel = viewModel,
                    webViewController = webViewController,
                    fullscreenView = fullscreenViewState,
                    onExitFullscreen = ::exitFullscreenVideo,
                    onOpenExternalUrl = ::openExternalUrl,
                    onOpenExternalUri = ::openExternalUri,
                    onCopyUrl = ::copyToClipboard,
                    onShareUrl = ::shareUrl,
                    onDownloadRequested = ::enqueueDownload,
                    onFileChooserRequested = ::launchFileChooser,
                    onEnterFullscreen = ::enterFullscreenVideo,
                    onFinishActivity = { finish() }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val incomingUrl = intent.dataString
        if (!incomingUrl.isNullOrBlank()) {
            val targetUrl = InstagramUrlPolicy.normalizeDeepLink(incomingUrl)
            webViewController.loadUrl(targetUrl)
        }
    }

    override fun onResume() {
        super.onResume()
        app.networkMonitor.start()
        viewModel.setForeground(true)
        webViewController.onResume()
    }

    override fun onPause() {
        viewModel.setForeground(false)
        webViewController.onPause()
        app.networkMonitor.stop()
        super.onPause()
    }

    override fun onDestroy() {
        pendingFilePathCallback?.onReceiveValue(null)
        pendingFilePathCallback = null
        exitFullscreenVideo()
        webViewController.destroy()
        super.onDestroy()
    }

    private fun launchFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>,
        params: WebChromeClient.FileChooserParams
    ): Boolean {
        pendingFilePathCallback?.onReceiveValue(null)
        pendingFilePathCallback = filePathCallback

        return runCatching {
            val intent = params.createIntent().apply {
                if (params.mode == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
            }
            fileChooserLauncher.launch(intent)
            true
        }.getOrElse {
            pendingFilePathCallback = null
            false
        }
    }

    private fun enterFullscreenVideo(view: View, callback: WebChromeClient.CustomViewCallback) {
        if (fullscreenViewState != null) {
            callback.onCustomViewHidden()
            return
        }
        fullscreenViewState = view
        fullscreenCustomViewCallback = callback
        setSystemBarsVisible(false)
    }

    private fun exitFullscreenVideo() {
        if (fullscreenViewState == null) return
        fullscreenViewState = null
        fullscreenCustomViewCallback?.onCustomViewHidden()
        fullscreenCustomViewCallback = null
        setSystemBarsVisible(true)
    }

    private fun setSystemBarsVisible(visible: Boolean) {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (visible) {
            controller.show(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun openExternalUrl(url: String, useCustomTabs: Boolean) {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return
        if (useCustomTabs) {
            val launched = runCatching {
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                    .build()
                customTabsIntent.launchUrl(this, uri)
                true
            }.getOrDefault(false)
            if (launched) return
        }
        openExternalUri(uri)
    }

    private fun openExternalUri(uri: Uri) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }.onFailure {
            Toast.makeText(this, "Tidak ada aplikasi untuk membuka tautan ini", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Instagram URL", text))
    }

    private fun shareUrl(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan tautan Instagram"))
    }

    private fun enqueueDownload(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimeType: String
    ) {
        if (!InstagramUrlPolicy.isHttpOrHttps(url)) return
        runCatching {
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeType)
                val cookies = CookieManager.getInstance().getCookie(url)
                if (!cookies.isNullOrBlank()) {
                    addRequestHeader("Cookie", cookies)
                }
                addRequestHeader("User-Agent", userAgent)
                setTitle(fileName)
                setDescription("Diunduh melalui FocusGram")
                setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }
            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }.onFailure {
            Toast.makeText(this, "Gagal memulai unduhan", Toast.LENGTH_SHORT).show()
        }
    }
}
