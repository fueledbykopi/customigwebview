package com.focusgram.client.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusgram.client.data.CustomMediaManager
import com.focusgram.client.data.CustomMediaTarget
import com.focusgram.client.data.InstagramSection
import com.focusgram.client.data.ThemeMode
import com.focusgram.client.ui.FocusGramViewModel
import com.focusgram.client.ui.components.FocusBottomNavBar
import com.focusgram.client.ui.components.FocusControlSheet
import com.focusgram.client.ui.components.FocusIcons
import com.focusgram.client.ui.components.FocusTopBar
import com.focusgram.client.ui.components.MindfulBreakDialog
import com.focusgram.client.ui.components.OfflineErrorOverlay
import com.focusgram.client.ui.components.PhotoCropperOverlay
import com.focusgram.client.ui.components.WebViewContainer
import com.focusgram.client.ui.theme.IosPurple
import com.focusgram.client.ui.theme.LocalFocusThemeTokens
import com.focusgram.client.ui.theme.resolveDarkTheme
import com.focusgram.client.webview.WebViewController
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    initialUrl: String,
    viewModel: FocusGramViewModel,
    webViewController: WebViewController,
    fullscreenView: View?,
    cropTarget: CustomMediaTarget?,
    cropSourceBitmap: Bitmap?,
    isSavingCrop: Boolean,
    onPickCustomMedia: (CustomMediaTarget) -> Unit,
    onCancelCrop: () -> Unit,
    onConfirmCrop: (
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        frameWidth: Float,
        frameHeight: Float
    ) -> Unit,
    onPinCustomAppIcon: (Bitmap) -> Unit,
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
    val isDarkTheme = resolveDarkTheme(preferences.themeMode, preferences.appThemeStyle)
    val isAmoled = preferences.themeMode == ThemeMode.AMOLED
    val tokens = LocalFocusThemeTokens.current

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    // Reactive custom photo bitmaps loaded off the main thread
    var chatBgBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var timelineBgBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var customAppIconBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(preferences.chatBackgroundPath) {
        chatBgBitmap = CustomMediaManager.loadBitmapFromPath(preferences.chatBackgroundPath)
    }
    LaunchedEffect(preferences.timelineBackgroundPath) {
        timelineBgBitmap = CustomMediaManager.loadBitmapFromPath(preferences.timelineBackgroundPath)
    }
    LaunchedEffect(preferences.customAppIconPath) {
        customAppIconBitmap = CustomMediaManager.loadBitmapFromPath(preferences.customAppIconPath)
    }

    val isChatSection = uiState.currentSection == InstagramSection.DIRECT
    val isTimelineSection = !isChatSection &&
        !uiState.isAuthRoute &&
        uiState.currentSection != InstagramSection.REELS

    val activeCustomBg: Bitmap? = when {
        isChatSection && chatBgBitmap != null -> chatBgBitmap
        isTimelineSection && timelineBgBitmap != null -> timelineBgBitmap
        else -> null
    }
    val activeBgImage = remember(activeCustomBg) { activeCustomBg?.asImageBitmap() }
    val fallbackBgColorInt = MaterialTheme.colorScheme.background.toArgb()

    // Preserve the active URL when recovering from an OS WebView renderer kill after long inactivity
    val activeInitialUrl = remember(uiState.webViewGeneration) {
        if (uiState.webViewGeneration == 0) initialUrl else uiState.currentUrl
    }

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
            cropSourceBitmap != null && cropTarget != null -> {
                if (!isSavingCrop) onCancelCrop()
            }
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
        // Active custom photo canvas behind edge-to-edge Liquid Glass chrome & WebView
        if (activeBgImage != null) {
            Image(
                bitmap = activeBgImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            val scrimBase = if (isDarkTheme) Color.Black else Color.White
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(scrimBase.copy(alpha = preferences.backgroundDimOpacity))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
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
                        ghostModeEnabled = preferences.ghostModeEnabled,
                        isDarkTheme = isDarkTheme,
                        customAppIcon = customAppIconBitmap,
                        onBackClick = { webViewController.goBack() },
                        onTitleClick = { webViewController.scrollToTop() },
                        onToggleGhostMode = {
                            val next = !preferences.ghostModeEnabled
                            viewModel.toggleGhostMode(next)
                            showMessage(
                                if (next) {
                                    "Ghost Mode aktif: Story tanpa jejak & DM tanpa seen"
                                } else {
                                    "Ghost Mode dimatikan"
                                }
                            )
                        },
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
                        isDarkTheme = isDarkTheme,
                        showReels = preferences.showReels,
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
                    initialUrl = activeInitialUrl,
                    webViewGeneration = uiState.webViewGeneration,
                    controller = webViewController,
                    preferences = preferences,
                    isDarkTheme = isDarkTheme,
                    isAmoled = isAmoled,
                    isBackgroundTransparent = activeBgImage != null,
                    fallbackBackgroundColor = fallbackBgColorInt,
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
                    onGhostActionBlocked = { kind -> viewModel.onGhostActionBlocked(kind) },
                    onReelsBlocked = {
                        showMessage("Reels disembunyikan. Aktifkan 'Tampilkan Reels' di Pengaturan atau Mode Fokus.")
                    },
                    onRenderProcessGone = {
                        viewModel.onWebViewRenderProcessGone()
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Slim progress bar & persistent floating Focus + Ghost + Settings capsule when TopBar is hidden
                if (!preferences.showTopBar) {
                    if (uiState.isLoading && uiState.loadingProgress in 1..99) {
                        LinearProgressIndicator(
                            progress = { uiState.loadingProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .align(Alignment.TopCenter)
                                .zIndex(20f),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color.Transparent,
                            strokeCap = StrokeCap.Round
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = tokens.glassElevated,
                        border = BorderStroke(
                            width = 0.85.dp,
                            brush = tokens.specularBorderBrush
                        ),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .zIndex(20f)
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        val next = !preferences.ghostModeEnabled
                                        viewModel.toggleGhostMode(next)
                                        showMessage(
                                            if (next) "Ghost Mode aktif" else "Ghost Mode dimatikan"
                                        )
                                    }
                            ) {
                                Icon(
                                    imageVector = FocusIcons.Ghost,
                                    contentDescription = "Mode Hantu",
                                    tint = if (preferences.ghostModeEnabled) IosPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { viewModel.setFocusSheetVisible(true) }
                                    .padding(horizontal = 5.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = FocusIcons.ShieldFilled,
                                    contentDescription = "Mode Fokus",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = preferences.activeFilterCount.toString(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .clickable { viewModel.setSettingsScreenVisible(true) }
                            ) {
                                Icon(
                                    imageVector = FocusIcons.Gear,
                                    contentDescription = "Pengaturan",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
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

        // Quick Focus & Ghost Control Bottom Sheet
        if (uiState.showFocusSheet) {
            FocusControlSheet(
                preferences = preferences,
                sessionElapsedSeconds = uiState.sessionElapsedSeconds,
                blockedTrackersCount = uiState.blockedTrackersCount,
                blockedDistractionsCount = uiState.blockedDistractionsCount,
                blockedGhostTracesCount = uiState.blockedGhostTracesCount,
                isDarkTheme = isDarkTheme,
                onDismiss = { viewModel.setFocusSheetVisible(false) },
                onApplyPreset = { preset ->
                    viewModel.applyPreset(preset)
                    showMessage("Preset '${preset.label}' diterapkan")
                },
                onToggleGhostMode = { enabled ->
                    viewModel.toggleGhostMode(enabled)
                    showMessage(
                        if (enabled) "Ghost Mode aktif" else "Ghost Mode dimatikan"
                    )
                },
                onToggleGhostViewStories = viewModel::toggleGhostViewStories,
                onToggleGhostReadDm = viewModel::toggleGhostReadDm,
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
                isDarkTheme = isDarkTheme,
                blockedGhostTracesCount = uiState.blockedGhostTracesCount,
                chatBgPreview = chatBgBitmap,
                timelineBgPreview = timelineBgBitmap,
                customAppIconPreview = customAppIconBitmap,
                onNavigateBack = { viewModel.setSettingsScreenVisible(false) },
                onAppThemeStyleChange = { style ->
                    viewModel.updateAppThemeStyle(style)
                    showMessage("Tema '${style.label}' diterapkan")
                },
                onThemeModeChange = viewModel::updateThemeMode,
                onToggleGhostMode = { enabled ->
                    viewModel.toggleGhostMode(enabled)
                    showMessage(
                        if (enabled) {
                            "Ghost Mode aktif: Story tanpa jejak & DM tanpa seen"
                        } else {
                            "Ghost Mode dimatikan"
                        }
                    )
                },
                onToggleGhostViewStories = viewModel::toggleGhostViewStories,
                onToggleGhostReadDm = viewModel::toggleGhostReadDm,
                onPickCustomMedia = onPickCustomMedia,
                onClearCustomMedia = { target ->
                    viewModel.clearCustomMedia(target)
                    showMessage("${target.title} direset ke bawaan")
                },
                onPinCustomAppIcon = {
                    val iconBmp = customAppIconBitmap
                    if (iconBmp != null) {
                        onPinCustomAppIcon(iconBmp)
                    } else {
                        showMessage("Pilih foto ikon terlebih dahulu")
                    }
                },
                onBackgroundDimChange = viewModel::updateBackgroundDimOpacity,
                onToggleShowReels = { show ->
                    viewModel.toggleHideReels(!show)
                    showMessage(if (show) "Reels ditampilkan" else "Reels disembunyikan")
                },
                onToggleShowExplore = { show ->
                    viewModel.toggleHideExploreFeed(!show)
                    showMessage(if (show) "Explore ditampilkan" else "Grid Explore disembunyikan")
                },
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

        // Full-Screen Pinch-to-Zoom Photo Cropper Overlay
        if (cropSourceBitmap != null && cropTarget != null) {
            PhotoCropperOverlay(
                bitmap = cropSourceBitmap,
                target = cropTarget,
                isSaving = isSavingCrop,
                onCancel = onCancelCrop,
                onConfirmCrop = onConfirmCrop
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
