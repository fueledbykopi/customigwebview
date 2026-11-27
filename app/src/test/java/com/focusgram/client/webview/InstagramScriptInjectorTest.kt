package com.focusgram.client.webview

import com.focusgram.client.data.AppThemeStyle
import com.focusgram.client.data.FocusPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InstagramScriptInjectorTest {

    @Test
    fun `default preferences disable native bottom nav bar`() {
        val defaults = FocusPreferences()
        assertFalse(defaults.useNativeBottomBar)
        assertTrue(defaults.appThemeStyle == AppThemeStyle.LIQUID_GLASS)
    }

    @Test
    fun `buildCss includes grayscale and AMOLED rules only when enabled`() {
        val prefs = FocusPreferences(
            grayscaleMode = true,
            hideReels = true,
            useNativeBottomBar = true
        )
        val css = InstagramScriptInjector.buildCss(prefs, isDarkTheme = true, isAmoled = true)

        assertTrue(css.contains("filter: grayscale(100%)"))
        assertTrue(css.contains("#000000"))
        assertTrue(css.contains("""a[href^="/reels/"]"""))
        assertTrue(css.contains("data-focusgram-web-bottom-nav"))
    }

    @Test
    fun `buildCss omits disabled distraction rules`() {
        val prefs = FocusPreferences(
            hideOpenInAppPrompts = false,
            hideReels = false,
            hideExploreFeed = false,
            hideStories = false,
            hideSuggestedAndAds = false,
            hideEngagementCounts = false,
            grayscaleMode = false,
            useNativeBottomBar = false
        )
        val css = InstagramScriptInjector.buildCss(prefs, isDarkTheme = false, isAmoled = false)

        assertFalse(css.contains("grayscale"))
        assertFalse(css.contains("data-focusgram-reels"))
        assertFalse(css.contains("data-focusgram-stories"))
        assertFalse(css.contains("data-focusgram-web-bottom-nav"))
    }

    @Test
    fun `buildCss emits custom background transparency and frosted glass rules when photo set`() {
        val prefs = FocusPreferences(
            chatBackgroundPath = "/data/user/0/com.focusgram.client/files/custom_media/chat_bg_1.jpg",
            timelineBackgroundPath = "/data/user/0/com.focusgram.client/files/custom_media/timeline_bg_1.jpg"
        )
        val css = InstagramScriptInjector.buildCss(prefs, isDarkTheme = true, isAmoled = false)

        assertTrue(css.contains("focusgram-custom-bg-active"))
        assertTrue(css.contains("focusgram-custom-timeline-bg"))
        assertTrue(css.contains("focusgram-custom-chat-bg"))
        assertTrue(css.contains("backdrop-filter: blur"))
    }

    @Test
    fun `buildCss applies theme palette accents for all 6 themes`() {
        for (style in AppThemeStyle.entries) {
            val prefs = FocusPreferences(appThemeStyle = style)
            val css = InstagramScriptInjector.buildCss(prefs, isDarkTheme = true, isAmoled = false)
            assertTrue("Missing backdrop-filter for $style", css.contains("backdrop-filter: blur"))
            assertTrue("Missing accent-color for $style", css.contains("accent-color:"))
        }
    }

    @Test
    fun `buildInjectionScript wires JS bridge and current flags`() {
        val prefs = FocusPreferences(
            hideReels = false,
            autoMuteVideos = true,
            chatBackgroundPath = "/tmp/chat.jpg"
        )
        val script = InstagramScriptInjector.buildInjectionScript(
            prefs = prefs,
            isDarkTheme = true,
            isAmoled = false
        )

        assertTrue(script.contains(InstagramScriptInjector.BRIDGE_NAME))
        assertTrue(script.contains("hideReels: false"))
        assertTrue(script.contains("autoMute: true"))
        assertTrue(script.contains("hasChatBg: true"))
        assertTrue(script.contains("hasTimelineBg: false"))
        assertTrue(script.contains("MutationObserver"))
    }
}
