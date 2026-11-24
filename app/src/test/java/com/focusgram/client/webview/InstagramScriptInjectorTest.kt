package com.focusgram.client.webview

import com.focusgram.client.data.FocusPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InstagramScriptInjectorTest {

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
    fun `buildInjectionScript wires JS bridge and current flags`() {
        val prefs = FocusPreferences(hideReels = true, autoMuteVideos = true)
        val script = InstagramScriptInjector.buildInjectionScript(
            prefs = prefs,
            isDarkTheme = true,
            isAmoled = false
        )

        assertTrue(script.contains(InstagramScriptInjector.BRIDGE_NAME))
        assertTrue(script.contains("hideReels: true"))
        assertTrue(script.contains("autoMute: true"))
        assertTrue(script.contains("MutationObserver"))
    }
}
