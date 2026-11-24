package com.focusgram.client.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.data.FocusPreset
import com.focusgram.client.data.InstagramSection
import com.focusgram.client.data.PreferencesRepository
import com.focusgram.client.data.ThemeMode
import com.focusgram.client.domain.InstagramUrlPolicy
import com.focusgram.client.domain.NetworkMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Immutable
data class MainUiState(
    val currentUrl: String = InstagramUrlPolicy.HOME_URL,
    val currentSection: InstagramSection = InstagramSection.HOME,
    val isAuthRoute: Boolean = false,
    val loadingProgress: Int = 0,
    val isLoading: Boolean = true,
    val canGoBack: Boolean = false,
    val isOnline: Boolean = true,
    val errorMessage: String? = null,
    val blockedTrackersCount: Int = 0,
    val blockedDistractionsCount: Int = 0,
    val sessionElapsedSeconds: Int = 0,
    val showMindfulBreakDialog: Boolean = false,
    val showFocusSheet: Boolean = false,
    val showSettingsScreen: Boolean = false
)

class FocusGramViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    val preferences: StateFlow<FocusPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = FocusPreferences()
        )

    private val _uiState = MutableStateFlow(MainUiState(isOnline = networkMonitor.isOnline.value))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var nextBreakThresholdSeconds: Int? = null
    private var isForeground: Boolean = false

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _uiState.update { state ->
                    state.copy(isOnline = online)
                }
            }
        }

        viewModelScope.launch {
            preferences.collect { prefs ->
                recalculateBreakThreshold(prefs.sessionLimitMinutes)
            }
        }
    }

    fun setForeground(active: Boolean) {
        isForeground = active
        if (active) {
            startSessionTimer()
        } else {
            timerJob?.cancel()
            timerJob = null
        }
    }

    private fun startSessionTimer() {
        if (timerJob?.isActive == true) return
        timerJob = viewModelScope.launch {
            while (isActive && isForeground) {
                delay(1000L)
                _uiState.update { state ->
                    val nextSeconds = state.sessionElapsedSeconds + 1
                    val threshold = nextBreakThresholdSeconds
                    val shouldShowBreak = threshold != null && nextSeconds >= threshold
                    state.copy(
                        sessionElapsedSeconds = nextSeconds,
                        showMindfulBreakDialog = state.showMindfulBreakDialog || shouldShowBreak
                    )
                }
            }
        }
    }

    private fun recalculateBreakThreshold(limitMinutes: Int) {
        nextBreakThresholdSeconds = if (limitMinutes <= 0) {
            null
        } else {
            val current = _uiState.value.sessionElapsedSeconds
            val target = limitMinutes * 60
            if (current >= target) current + target else target
        }
    }

    fun extendSession(extraMinutes: Int) {
        val current = _uiState.value.sessionElapsedSeconds
        nextBreakThresholdSeconds = current + (extraMinutes * 60)
        _uiState.update { it.copy(showMindfulBreakDialog = false) }
    }

    fun dismissMindfulBreakDialog() {
        val limit = preferences.value.sessionLimitMinutes
        nextBreakThresholdSeconds = if (limit > 0) {
            _uiState.value.sessionElapsedSeconds + (limit * 60)
        } else {
            null
        }
        _uiState.update { it.copy(showMindfulBreakDialog = false) }
    }

    fun onPageStarted(url: String) {
        _uiState.update {
            it.copy(
                currentUrl = url,
                currentSection = InstagramUrlPolicy.detectSection(url),
                isAuthRoute = InstagramUrlPolicy.isAuthOrChallengeRoute(url),
                isLoading = true,
                errorMessage = null
            )
        }
    }

    fun onPageProgress(progress: Int) {
        _uiState.update {
            it.copy(
                loadingProgress = progress,
                isLoading = progress < 100
            )
        }
    }

    fun onPageFinished(url: String, canGoBack: Boolean) {
        _uiState.update {
            it.copy(
                currentUrl = url.ifBlank { it.currentUrl },
                currentSection = InstagramUrlPolicy.detectSection(url.ifBlank { it.currentUrl }),
                isAuthRoute = InstagramUrlPolicy.isAuthOrChallengeRoute(url.ifBlank { it.currentUrl }),
                loadingProgress = 100,
                isLoading = false,
                canGoBack = canGoBack
            )
        }
    }

    fun onSpaUrlChanged(url: String) {
        _uiState.update {
            it.copy(
                currentUrl = url,
                currentSection = InstagramUrlPolicy.detectSection(url),
                isAuthRoute = InstagramUrlPolicy.isAuthOrChallengeRoute(url)
            )
        }
    }

    fun onMainFrameError(errorCode: Int, description: String) {
        val message = when {
            !_uiState.value.isOnline -> "Tidak ada koneksi internet. Periksa Wi-Fi atau data seluler Anda."
            description.isNotBlank() -> "Halaman gagal dimuat ($description)."
            else -> "Terjadi kesalahan jaringan (kode $errorCode)."
        }
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onTrackerBlocked() {
        _uiState.update { it.copy(blockedTrackersCount = it.blockedTrackersCount + 1) }
    }

    fun onDistractionBlocked(count: Int) {
        _uiState.update { it.copy(blockedDistractionsCount = it.blockedDistractionsCount + count) }
    }

    fun setFocusSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showFocusSheet = visible) }
    }

    fun setSettingsScreenVisible(visible: Boolean) {
        _uiState.update {
            it.copy(
                showSettingsScreen = visible,
                showFocusSheet = if (visible) false else it.showFocusSheet
            )
        }
    }

    // Preference mutations
    fun toggleHideOpenInApp(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateHideOpenInAppPrompts(enabled)
    }

    fun toggleHideReels(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateHideReels(enabled)
    }

    fun toggleHideExploreFeed(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateHideExploreFeed(enabled)
    }

    fun toggleHideStories(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateHideStories(enabled)
    }

    fun toggleHideSuggestedAndAds(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateHideSuggestedAndAds(enabled)
    }

    fun toggleHideEngagementCounts(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateHideEngagementCounts(enabled)
    }

    fun toggleAutoMuteVideos(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateAutoMuteVideos(enabled)
    }

    fun toggleGrayscaleMode(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateGrayscaleMode(enabled)
    }

    fun toggleBlockTrackers(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateBlockTrackers(enabled)
    }

    fun toggleUseNativeBottomBar(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateUseNativeBottomBar(enabled)
    }

    fun toggleShowTopBar(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateShowTopBar(enabled)
    }

    fun togglePullToRefresh(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateEnablePullToRefresh(enabled)
    }

    fun toggleOpenExternalInCustomTabs(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.updateOpenExternalInCustomTabs(enabled)
    }

    fun updateThemeMode(mode: ThemeMode) = viewModelScope.launch {
        preferencesRepository.updateThemeMode(mode)
    }

    fun updateSessionLimitMinutes(minutes: Int) = viewModelScope.launch {
        preferencesRepository.updateSessionLimitMinutes(minutes)
    }

    fun applyPreset(preset: FocusPreset) = viewModelScope.launch {
        preferencesRepository.applyPreset(preset)
    }

    class Factory(
        private val preferencesRepository: PreferencesRepository,
        private val networkMonitor: NetworkMonitor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FocusGramViewModel(preferencesRepository, networkMonitor) as T
        }
    }
}
