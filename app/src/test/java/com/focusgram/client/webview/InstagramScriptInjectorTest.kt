package com.focusgram.client.webview

import com.focusgram.client.data.AppThemeStyle
import com.focusgram.client.data.FocusPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InstagramScriptInjectorTest {

    @Test
    fun `default preferences keep Explore visible and disable native bottom nav bar`() {
        val defaults = FocusPreferences()
        assertFalse(defaults.useNativeBottomBar)
        assertFalse(defaults.hideExploreFeed)
        assertFalse(defaults.ghostModeEnabled)
        assertTrue(defaults.ghostViewStories)
        assertTrue(defaults.ghostReadDm)
        assertTrue(defaults.appThemeStyle == AppThemeStyle.LIQUID_GLASS)
    }

    @Test
    fun `buildCss includes grayscale, AMOLED, and Explore-safe Reels rules`() {
        val prefs = FocusPreferences(
            grayscaleMode = true,
            hideReels = true,
            useNativeBottomBar = true
        )
        val css = InstagramScriptInjector.buildCss(prefs, isDarkTheme = true, isAmoled = true)

        assertTrue(css.contains("filter: grayscale(100%)"))
        assertTrue(css.contains("#000000"))
        assertTrue(css.contains("""a[href^="/reels/"]"""))
        assertTrue(css.contains("""html:not(.focusgram-on-explore) a[href^="/reel/"]"""))
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
    fun `buildCss emits same-origin custom background layers and transparency rules when photo set`() {
        val prefs = FocusPreferences(
            chatBackgroundPath = "/data/user/0/com.focusgram.client/files/custom_media/chat_bg_1.jpg",
            timelineBackgroundPath = "/data/user/0/com.focusgram.client/files/custom_media/timeline_bg_1.jpg"
        )
        val css = InstagramScriptInjector.buildCss(prefs, isDarkTheme = true, isAmoled = false)

        assertTrue(css.contains("focusgram-custom-bg-active"))
        assertTrue(css.contains("focusgram-custom-timeline-bg"))
        assertTrue(css.contains("focusgram-custom-chat-bg"))
        assertTrue(css.contains("/__focusgram_media__/chat_bg"))
        assertTrue(css.contains("/__focusgram_media__/timeline_bg"))
        assertTrue(css.contains("backdrop-filter: blur"))
    }

    @Test
    fun `buildCss applies Instagram CSS variable palette overrides for all 6 themes`() {
        for (style in AppThemeStyle.entries) {
            val prefs = FocusPreferences(appThemeStyle = style)
            val cssDark = InstagramScriptInjector.buildCss(prefs, isDarkTheme = true, isAmoled = false)
            val cssLight = InstagramScriptInjector.buildCss(prefs, isDarkTheme = false, isAmoled = false)

            assertTrue("Missing --ig-primary-background for $style (dark)", cssDark.contains("--ig-primary-background:"))
            assertTrue("Missing --ig-primary-text for $style (dark)", cssDark.contains("--ig-primary-text:"))
            assertTrue("Missing color-scheme dark for $style", cssDark.contains("color-scheme: dark"))
            assertTrue("Missing color-scheme light for $style", cssLight.contains("color-scheme: light"))
            assertTrue("Missing accent-color for $style", cssDark.contains("accent-color:"))
        }
    }

    @Test
    fun `buildInjectionScript removes Instagram Lite prompts safely and wires Ghost Mode flags`() {
        val prefs = FocusPreferences(
            hideReels = false,
            autoMuteVideos = true,
            ghostModeEnabled = true,
            ghostViewStories = true,
            ghostReadDm = true,
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
        assertTrue(script.contains("ghostStory: true"))
        assertTrue(script.contains("ghostDm: true"))
        assertTrue(script.contains("use instagram lite"))
        assertTrue(script.contains("MutationObserver"))
    }

    @Test
    fun `buildDocumentStartScript hooks fetch, XHR, sendBeacon, and WebSocket for Ghost Mode`() {
        val prefs = FocusPreferences(
            ghostModeEnabled = true,
            ghostViewStories = true,
            ghostReadDm = true
        )
        val docStart = InstagramScriptInjector.buildDocumentStartScript(prefs)

        assertTrue(docStart.contains("window.__focusGramGhost"))
        assertTrue(docStart.contains("story: true"))
        assertTrue(docStart.contains("dm: true"))
        assertTrue(docStart.contains("polarisstoriesv3seenmutation"))
        assertTrue(docStart.contains("mark_thread_read"))
        assertTrue(docStart.contains("WebSocket.prototype.send"))
        assertTrue(docStart.contains("navigator.sendBeacon"))
    }
}
