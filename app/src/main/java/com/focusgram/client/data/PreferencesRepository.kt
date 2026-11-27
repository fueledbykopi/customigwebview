package com.focusgram.client.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.focusDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "focusgram_preferences"
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val HIDE_OPEN_IN_APP = booleanPreferencesKey("hide_open_in_app")
        val HIDE_REELS = booleanPreferencesKey("hide_reels")
        val HIDE_EXPLORE_FEED = booleanPreferencesKey("hide_explore_feed")
        val HIDE_STORIES = booleanPreferencesKey("hide_stories")
        val HIDE_SUGGESTED_AND_ADS = booleanPreferencesKey("hide_suggested_and_ads")
        val HIDE_ENGAGEMENT_COUNTS = booleanPreferencesKey("hide_engagement_counts")
        val AUTO_MUTE_VIDEOS = booleanPreferencesKey("auto_mute_videos")
        val GRAYSCALE_MODE = booleanPreferencesKey("grayscale_mode")
        val BLOCK_TRACKERS = booleanPreferencesKey("block_trackers")
        val USE_NATIVE_BOTTOM_BAR = booleanPreferencesKey("use_native_bottom_bar")
        val SHOW_TOP_BAR = booleanPreferencesKey("show_top_bar")
        val ENABLE_PULL_TO_REFRESH = booleanPreferencesKey("enable_pull_to_refresh")
        val OPEN_EXTERNAL_CUSTOM_TABS = booleanPreferencesKey("open_external_custom_tabs")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_THEME_STYLE = stringPreferencesKey("app_theme_style")
        val SESSION_LIMIT_MINUTES = intPreferencesKey("session_limit_minutes")
        val CHAT_BACKGROUND_PATH = stringPreferencesKey("chat_background_path")
        val TIMELINE_BACKGROUND_PATH = stringPreferencesKey("timeline_background_path")
        val CUSTOM_APP_ICON_PATH = stringPreferencesKey("custom_app_icon_path")
        val BACKGROUND_DIM_OPACITY = floatPreferencesKey("background_dim_opacity")
    }

    val preferencesFlow: Flow<FocusPreferences> = context.focusDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val themeName = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = runCatching { ThemeMode.valueOf(themeName) }
                .getOrDefault(ThemeMode.SYSTEM)

            val styleName = prefs[Keys.APP_THEME_STYLE] ?: AppThemeStyle.LIQUID_GLASS.name
            val appThemeStyle = runCatching { AppThemeStyle.valueOf(styleName) }
                .getOrDefault(AppThemeStyle.LIQUID_GLASS)

            FocusPreferences(
                hideOpenInAppPrompts = prefs[Keys.HIDE_OPEN_IN_APP] ?: true,
                hideReels = prefs[Keys.HIDE_REELS] ?: true,
                hideExploreFeed = prefs[Keys.HIDE_EXPLORE_FEED] ?: true,
                hideStories = prefs[Keys.HIDE_STORIES] ?: false,
                hideSuggestedAndAds = prefs[Keys.HIDE_SUGGESTED_AND_ADS] ?: true,
                hideEngagementCounts = prefs[Keys.HIDE_ENGAGEMENT_COUNTS] ?: false,
                autoMuteVideos = prefs[Keys.AUTO_MUTE_VIDEOS] ?: false,
                grayscaleMode = prefs[Keys.GRAYSCALE_MODE] ?: false,
                blockTrackers = prefs[Keys.BLOCK_TRACKERS] ?: true,
                useNativeBottomBar = prefs[Keys.USE_NATIVE_BOTTOM_BAR] ?: false,
                showTopBar = prefs[Keys.SHOW_TOP_BAR] ?: true,
                enablePullToRefresh = prefs[Keys.ENABLE_PULL_TO_REFRESH] ?: true,
                openExternalInCustomTabs = prefs[Keys.OPEN_EXTERNAL_CUSTOM_TABS] ?: true,
                themeMode = themeMode,
                appThemeStyle = appThemeStyle,
                sessionLimitMinutes = prefs[Keys.SESSION_LIMIT_MINUTES] ?: 0,
                chatBackgroundPath = prefs[Keys.CHAT_BACKGROUND_PATH] ?: "",
                timelineBackgroundPath = prefs[Keys.TIMELINE_BACKGROUND_PATH] ?: "",
                customAppIconPath = prefs[Keys.CUSTOM_APP_ICON_PATH] ?: "",
                backgroundDimOpacity = (prefs[Keys.BACKGROUND_DIM_OPACITY] ?: 0.34f)
                    .coerceIn(0f, 0.78f)
            )
        }

    suspend fun updateHideOpenInAppPrompts(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.HIDE_OPEN_IN_APP] = enabled }
    }

    suspend fun updateHideReels(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.HIDE_REELS] = enabled }
    }

    suspend fun updateHideExploreFeed(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.HIDE_EXPLORE_FEED] = enabled }
    }

    suspend fun updateHideStories(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.HIDE_STORIES] = enabled }
    }

    suspend fun updateHideSuggestedAndAds(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.HIDE_SUGGESTED_AND_ADS] = enabled }
    }

    suspend fun updateHideEngagementCounts(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.HIDE_ENGAGEMENT_COUNTS] = enabled }
    }

    suspend fun updateAutoMuteVideos(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.AUTO_MUTE_VIDEOS] = enabled }
    }

    suspend fun updateGrayscaleMode(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.GRAYSCALE_MODE] = enabled }
    }

    suspend fun updateBlockTrackers(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.BLOCK_TRACKERS] = enabled }
    }

    suspend fun updateUseNativeBottomBar(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.USE_NATIVE_BOTTOM_BAR] = enabled }
    }

    suspend fun updateShowTopBar(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.SHOW_TOP_BAR] = enabled }
    }

    suspend fun updateEnablePullToRefresh(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.ENABLE_PULL_TO_REFRESH] = enabled }
    }

    suspend fun updateOpenExternalInCustomTabs(enabled: Boolean) {
        context.focusDataStore.edit { it[Keys.OPEN_EXTERNAL_CUSTOM_TABS] = enabled }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.focusDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun updateAppThemeStyle(style: AppThemeStyle) {
        context.focusDataStore.edit { it[Keys.APP_THEME_STYLE] = style.name }
    }

    suspend fun updateSessionLimitMinutes(minutes: Int) {
        context.focusDataStore.edit { it[Keys.SESSION_LIMIT_MINUTES] = minutes.coerceAtLeast(0) }
    }

    suspend fun updateChatBackgroundPath(path: String) {
        context.focusDataStore.edit { it[Keys.CHAT_BACKGROUND_PATH] = path }
    }

    suspend fun updateTimelineBackgroundPath(path: String) {
        context.focusDataStore.edit { it[Keys.TIMELINE_BACKGROUND_PATH] = path }
    }

    suspend fun updateCustomAppIconPath(path: String) {
        context.focusDataStore.edit { it[Keys.CUSTOM_APP_ICON_PATH] = path }
    }

    suspend fun updateBackgroundDimOpacity(opacity: Float) {
        context.focusDataStore.edit { it[Keys.BACKGROUND_DIM_OPACITY] = opacity.coerceIn(0f, 0.78f) }
    }

    suspend fun applyPreset(preset: FocusPreset) {
        context.focusDataStore.edit { prefs ->
            when (preset) {
                FocusPreset.ZEN -> {
                    prefs[Keys.HIDE_OPEN_IN_APP] = true
                    prefs[Keys.HIDE_REELS] = true
                    prefs[Keys.HIDE_EXPLORE_FEED] = true
                    prefs[Keys.HIDE_STORIES] = true
                    prefs[Keys.HIDE_SUGGESTED_AND_ADS] = true
                    prefs[Keys.HIDE_ENGAGEMENT_COUNTS] = true
                    prefs[Keys.AUTO_MUTE_VIDEOS] = true
                    prefs[Keys.GRAYSCALE_MODE] = true
                    prefs[Keys.BLOCK_TRACKERS] = true
                }
                FocusPreset.BALANCED -> {
                    prefs[Keys.HIDE_OPEN_IN_APP] = true
                    prefs[Keys.HIDE_REELS] = true
                    prefs[Keys.HIDE_EXPLORE_FEED] = true
                    prefs[Keys.HIDE_STORIES] = false
                    prefs[Keys.HIDE_SUGGESTED_AND_ADS] = true
                    prefs[Keys.HIDE_ENGAGEMENT_COUNTS] = false
                    prefs[Keys.AUTO_MUTE_VIDEOS] = false
                    prefs[Keys.GRAYSCALE_MODE] = false
                    prefs[Keys.BLOCK_TRACKERS] = true
                }
                FocusPreset.CLASSIC -> {
                    prefs[Keys.HIDE_OPEN_IN_APP] = true
                    prefs[Keys.HIDE_REELS] = false
                    prefs[Keys.HIDE_EXPLORE_FEED] = false
                    prefs[Keys.HIDE_STORIES] = false
                    prefs[Keys.HIDE_SUGGESTED_AND_ADS] = false
                    prefs[Keys.HIDE_ENGAGEMENT_COUNTS] = false
                    prefs[Keys.AUTO_MUTE_VIDEOS] = false
                    prefs[Keys.GRAYSCALE_MODE] = false
                    prefs[Keys.BLOCK_TRACKERS] = true
                }
            }
        }
    }
}
