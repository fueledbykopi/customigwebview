package com.focusgram.client.data

import androidx.compose.runtime.Immutable

enum class ThemeMode(val label: String) {
    SYSTEM("Sistem"),
    LIGHT("Terang"),
    DARK("Gelap"),
    AMOLED("AMOLED")
}

enum class AppThemeStyle(val label: String, val tagline: String) {
    LIQUID_GLASS("Liquid Glass", "iOS Frosted Blur"),
    CYBERPUNK("Cyberpunk", "Neon Night City"),
    GTA6("GTA VI", "Vice City Sunset"),
    SPIDERMAN("Spider-Man", "Crimson Web-Verse"),
    TECH("Tech", "Obsidian Matrix"),
    BARBIE("Barbie", "Dreamhouse Gloss")
}

enum class CustomMediaTarget(
    val title: String,
    val subtitle: String,
    val filePrefix: String,
    val isSquareIcon: Boolean
) {
    CHAT_BACKGROUND(
        title = "Background Chat (DM)",
        subtitle = "Cubit & geser foto untuk mengepaskan layar chat",
        filePrefix = "chat_bg_",
        isSquareIcon = false
    ),
    TIMELINE_BACKGROUND(
        title = "Background Timeline",
        subtitle = "Cubit & geser foto untuk mengepaskan layar beranda",
        filePrefix = "timeline_bg_",
        isSquareIcon = false
    ),
    APP_ICON(
        title = "Ikon Aplikasi",
        subtitle = "Cubit & geser foto agar pas di bingkai ikon",
        filePrefix = "app_icon_",
        isSquareIcon = true
    )
}

enum class FocusPreset(val label: String, val description: String) {
    ZEN("Mode Zen", "Bebas distraksi maksimal: tanpa Reels, Explore, Stories, & metrik like"),
    BALANCED("Seimbang (Rekomendasi)", "Hapus Reels, iklan/saran, dan banner aplikasi; pertahankan Feed & DM"),
    CLASSIC("Minimalis", "Hanya bersihkan banner aplikasi & blokir pelacak")
}

enum class InstagramSection(val title: String, val url: String) {
    HOME("Beranda", "https://www.instagram.com/"),
    SEARCH("Cari", "https://www.instagram.com/explore/"),
    REELS("Reels", "https://www.instagram.com/reels/"),
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
    val useNativeBottomBar: Boolean = false,
    val showTopBar: Boolean = true,
    val enablePullToRefresh: Boolean = true,
    val openExternalInCustomTabs: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appThemeStyle: AppThemeStyle = AppThemeStyle.LIQUID_GLASS,
    val sessionLimitMinutes: Int = 0,
    val chatBackgroundPath: String = "",
    val timelineBackgroundPath: String = "",
    val customAppIconPath: String = "",
    val backgroundDimOpacity: Float = 0.34f
) {
    val showReels: Boolean
        get() = !hideReels

    val hasChatBackground: Boolean
        get() = chatBackgroundPath.isNotBlank()

    val hasTimelineBackground: Boolean
        get() = timelineBackgroundPath.isNotBlank()

    val hasCustomAppIcon: Boolean
        get() = customAppIconPath.isNotBlank()

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
