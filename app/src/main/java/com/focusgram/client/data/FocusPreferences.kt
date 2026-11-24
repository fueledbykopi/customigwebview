package com.focusgram.client.data

import androidx.compose.runtime.Immutable

enum class ThemeMode(val label: String) {
    SYSTEM("Sistem"),
    LIGHT("Terang"),
    DARK("Gelap"),
    AMOLED("AMOLED")
}

enum class FocusPreset(val label: String, val description: String) {
    ZEN("Mode Zen", "Bebas distraksi maksimal: tanpa Reels, Explore, Stories, & metrik like"),
    BALANCED("Seimbang (Rekomendasi)", "Hapus Reels, iklan/saran, dan banner aplikasi; pertahankan Feed & DM"),
    CLASSIC("Minimalis", "Hanya bersihkan banner aplikasi & blokir pelacak")
}

enum class InstagramSection(val title: String, val url: String) {
    HOME("Beranda", "https://www.instagram.com/"),
    SEARCH("Cari", "https://www.instagram.com/explore/"),
    DIRECT("Pesan", "https://www.instagram.com/direct/inbox/"),
    ACTIVITY("Aktivitas", "https://www.instagram.com/accounts/activity/"),
    PROFILE("Profil", "https://www.instagram.com/accounts/edit/"),
    OTHER("Instagram", "https://www.instagram.com/")
}

@Immutable
data class FocusPreferences(
    val hideOpenInAppPrompts: Boolean = true,
    val hideReels: Boolean = true,
    val hideExploreFeed: Boolean = true,
    val hideStories: Boolean = false,
    val hideSuggestedAndAds: Boolean = true,
    val hideEngagementCounts: Boolean = false,
    val autoMuteVideos: Boolean = false,
    val grayscaleMode: Boolean = false,
    val blockTrackers: Boolean = true,
    val useNativeBottomBar: Boolean = true,
    val showTopBar: Boolean = true,
    val enablePullToRefresh: Boolean = true,
    val openExternalInCustomTabs: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val sessionLimitMinutes: Int = 0
) {
    val activeFilterCount: Int
        get() = listOf(
            hideOpenInAppPrompts,
            hideReels,
            hideExploreFeed,
            hideStories,
            hideSuggestedAndAds,
            hideEngagementCounts,
            autoMuteVideos,
            grayscaleMode,
            blockTrackers
        ).count { it }

    val matchingPreset: FocusPreset?
        get() = when {
            hideOpenInAppPrompts && hideReels && hideExploreFeed && hideStories &&
                hideSuggestedAndAds && hideEngagementCounts && autoMuteVideos &&
                grayscaleMode && blockTrackers -> FocusPreset.ZEN

            hideOpenInAppPrompts && hideReels && hideExploreFeed && !hideStories &&
                hideSuggestedAndAds && !hideEngagementCounts && !autoMuteVideos &&
                !grayscaleMode && blockTrackers -> FocusPreset.BALANCED

            hideOpenInAppPrompts && !hideReels && !hideExploreFeed && !hideStories &&
                !hideSuggestedAndAds && !hideEngagementCounts && !autoMuteVideos &&
                !grayscaleMode && blockTrackers -> FocusPreset.CLASSIC

            else -> null
        }
}
