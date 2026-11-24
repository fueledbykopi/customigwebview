package com.focusgram.client.ui.screens

import android.net.Uri
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusgram.client.data.ThemeMode
import com.focusgram.client.ui.FocusGramViewModel
import com.focusgram.client.ui.components.FocusBottomNavBar
import com.focusgram.client.ui.components.FocusControlSheet
import com.focusgram.client.ui.components.FocusTopBar
import com.focusgram.client.ui.components.MindfulBreakDialog
import com.focusgram.client.ui.components.OfflineErrorOverlay
import com.focusgram.client.ui.components.WebViewContainer
import com.focusgram.client.ui.theme.resolveDarkTheme
import com.focusgram.client.webview.WebViewController
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    initialUrl: String,
    viewModel: FocusGramViewModel,
    webViewController: WebViewController,
    fullscreenView: View?,
    onExitFullscreen: () -> Unit,
    onOpenExternalUrl: (url: String, useCustomTabs: Boolean) -> Unit,
    onOpenExternalUri: (Uri) -> Unit,
    onCopyUrl: (String) -> Unit,
    onShareUrl: (String) -> Unit,
    onDownloadRequested: (url: String, userAgent: String, contentDisposition: String, mimeType: String) -> Unit,
    onFileChooserRequested: (ValueCallback<Array<Uri>>, WebChromeClient.FileChooserParams) -> Boolean,
    onEnterFullscreen: (View, WebChromeClient.CustomViewCallback) -> Unit,
    onFinishActivity: () -> Unit
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = resolveDarkTheme(preferences.themeMode)
    val isAmoled = preferences.themeMode == ThemeMode.AMOLED

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Auto-reload when connectivity is restored after an offline error
    LaunchedEffect(uiState.isOnline) {
        if (uiState.isOnline && uiState.errorMessage != null) {
            viewModel.clearError()
            webViewController.reload()
        }
    }

    // Predictive & hierarchical back navigation
    BackHandler {
        when {
            fullscreenView != null -> onExitFullscreen()
            uiState.showSettingsScreen -> viewModel.setSettingsScreenVisible(false)
            uiState.showFocusSheet -> viewModel.setFocusSheetVisible(false)
            webViewController.goBack() -> Unit
            else -> {
                val now = System.currentTimeMillis()
                if (now - lastBackPressTime <= 2000L) {
                    onFinishActivity()
                } else {
                    lastBackPressTime = now
                    showMessage("Tekan kembali sekali lagi untuk keluar")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                if (preferences.showTopBar) {
                    FocusTopBar(
                        section = uiState.currentSection,
                        canGoBack = uiState.canGoBack,
                        isLoading = uiState.isLoading,
                        loadingProgress = uiState.loadingProgress,
                        activeFilterCount = preferences.activeFilterCount,
                        onBackClick = { webViewController.goBack() },
                        onTitleClick = { webViewController.scrollToTop() },
                        onFocusControlsClick = { viewModel.setFocusSheetVisible(true) },
                        onRefreshClick = {
                            viewModel.clearError()
                            webViewController.reload()
                        },
                        onCopyLinkClick = {
                            onCopyUrl(uiState.currentUrl)
                            showMessage("Tautan disalin ke papan klip")
                        },
                        onShareLinkClick = { onShareUrl(uiState.currentUrl) },
                        onOpenInBrowserClick = {
                            onOpenExternalUrl(uiState.currentUrl, false)
                        },
                        onOpenSettingsClick = { viewModel.setSettingsScreenVisible(true) }
                    )
                }
            },
            bottomBar = {
                if (preferences.useNativeBottomBar && !uiState.isAuthRoute) {
                    FocusBottomNavBar(
                        currentSection = uiState.currentSection,
                        onSelectSection = { section ->
                            viewModel.clearError()
                            webViewController.navigateToSection(section, uiState.currentSection)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .then(
                        if (!preferences.showTopBar) Modifier.statusBarsPadding() else Modifier
                    )
                    .then(
                        if (!preferences.useNativeBottomBar || uiState.isAuthRoute) {
                            Modifier.navigationBarsPadding()
                        } else {
                            Modifier
                        }
                    )
                    .imePadding()
            ) {
                WebViewContainer(
                    initialUrl = initialUrl,
                    controller = webViewController,
                    preferences = preferences,
                    isDarkTheme = isDarkTheme,
                    isAmoled = isAmoled,
                    onPageStarted = { url -> viewModel.onPageStarted(url) },
                    onPageProgress = { progress -> viewModel.onPageProgress(progress) },
                    onPageFinished = { url, canGoBack -> viewModel.onPageFinished(url, canGoBack) },
                    onSpaUrlChanged = { url -> viewModel.onSpaUrlChanged(url) },
                    onMainFrameError = { code, desc, _ -> viewModel.onMainFrameError(code, desc) },
                    onOpenExternalUrl = { url ->
                        onOpenExternalUrl(url, preferences.openExternalInCustomTabs)
                    },
                    onOpenExternalUri = onOpenExternalUri,
                    onDownloadRequested = { url, ua, cd, mime ->
                        onDownloadRequested(url, ua, cd, mime)
                        showMessage("Mengunduh media...")
                    },
                    onFileChooserRequested = onFileChooserRequested,
                    onEnterFullscreen = onEnterFullscreen,
                    onExitFullscreen = onExitFullscreen,
                    onTrackerBlocked = { viewModel.onTrackerBlocked() },
                    onDistractionBlocked = { count -> viewModel.onDistractionBlocked(count) },
                    onReelsBlocked = {
                        showMessage("Reels disembunyikan oleh Mode Fokus")
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Slim progress bar & subtle Focus pill when TopBar is hidden (Immersive mode)
                if (!preferences.showTopBar) {
                    if (uiState.isLoading && uiState.loadingProgress in 1..99) {
                        LinearProgressIndicator(
                            progress = { uiState.loadingProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .align(Alignment.TopCenter),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Surface(
                        onClick = { viewModel.setFocusSheetVisible(true) },
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        tonalElevation = 4.dp,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = "Mode Fokus",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = preferences.activeFilterCount.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Offline / Main-Frame Error Overlay
                if (uiState.errorMessage != null) {
                    OfflineErrorOverlay(
                        message = uiState.errorMessage.orEmpty(),
                        isOnline = uiState.isOnline,
                        onRetry = {
                            viewModel.clearError()
                            webViewController.reload()
                        }
                    )
                }
            }
        }

        // Quick Focus Control Bottom Sheet
        if (uiState.showFocusSheet) {
            FocusControlSheet(
                preferences = preferences,
                sessionElapsedSeconds = uiState.sessionElapsedSeconds,
                blockedTrackersCount = uiState.blockedTrackersCount,
                blockedDistractionsCount = uiState.blockedDistractionsCount,
                onDismiss = { viewModel.setFocusSheetVisible(false) },
                onApplyPreset = { preset ->
                    viewModel.applyPreset(preset)
                    showMessage("Preset '${preset.label}' diterapkan")
                },
                onToggleHideReels = viewModel::toggleHideReels,
                onToggleHideExploreFeed = viewModel::toggleHideExploreFeed,
                onToggleHideSuggestedAndAds = viewModel::toggleHideSuggestedAndAds,
                onToggleHideOpenInApp = viewModel::toggleHideOpenInApp,
                onToggleHideStories = viewModel::toggleHideStories,
                onToggleHideEngagementCounts = viewModel::toggleHideEngagementCounts,
                onToggleAutoMuteVideos = viewModel::toggleAutoMuteVideos,
                onToggleGrayscaleMode = viewModel::toggleGrayscaleMode,
                onToggleBlockTrackers = viewModel::toggleBlockTrackers,
                onOpenFullSettings = { viewModel.setSettingsScreenVisible(true) }
            )
        }

        // Mindful Session Break Reminder Dialog
        if (uiState.showMindfulBreakDialog) {
            MindfulBreakDialog(
                elapsedMinutes = (uiState.sessionElapsedSeconds / 60).coerceAtLeast(1),
                onCloseApp = onFinishActivity,
                onExtendFiveMinutes = { viewModel.extendSession(5) },
                onDismiss = { viewModel.dismissMindfulBreakDialog() }
            )
        }

        // Full Settings Overlay (keeps WebView alive in the background with zero reload)
        AnimatedVisibility(
            visible = uiState.showSettingsScreen,
            enter = slideInHorizontally(initialOffsetX = { it / 3 }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it / 3 }) + fadeOut()
        ) {
            SettingsScreen(
                preferences = preferences,
                onNavigateBack = { viewModel.setSettingsScreenVisible(false) },
                onThemeModeChange = viewModel::updateThemeMode,
                onToggleNativeBottomBar = viewModel::toggleUseNativeBottomBar,
                onToggleShowTopBar = viewModel::toggleShowTopBar,
                onTogglePullToRefresh = viewModel::togglePullToRefresh,
                onToggleCustomTabs = viewModel::toggleOpenExternalInCustomTabs,
                onToggleBlockTrackers = viewModel::toggleBlockTrackers,
                onSessionLimitChange = viewModel::updateSessionLimitMinutes,
                onClearCacheKeepSession = {
                    webViewController.clearCacheKeepSession()
                    showMessage("Cache berhasil dibersihkan")
                },
                onClearAllSessionData = {
                    webViewController.clearAllSessionData {
                        viewModel.setSettingsScreenVisible(false)
                        showMessage("Data sesi & cookie berhasil dihapus")
                    }
                }
            )
        }

        // Fullscreen Video Playback Overlay
        if (fullscreenView != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { fullscreenView },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
