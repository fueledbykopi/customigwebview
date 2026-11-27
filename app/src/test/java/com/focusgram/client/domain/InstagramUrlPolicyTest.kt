package com.focusgram.client.domain

import com.focusgram.client.data.InstagramSection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InstagramUrlPolicyTest {

    @Test
    fun `internal Instagram and Meta auth URLs stay inside WebView`() {
        assertTrue(InstagramUrlPolicy.isInternalUrl("https://www.instagram.com/"))
        assertTrue(InstagramUrlPolicy.isInternalUrl("https://www.instagram.com/direct/inbox/"))
        assertTrue(InstagramUrlPolicy.isInternalUrl("https://m.instagram.com/explore/"))
        assertTrue(InstagramUrlPolicy.isInternalUrl("https://accounts.meta.com/"))
        assertTrue(InstagramUrlPolicy.isInternalUrl("https://m.facebook.com/login.php"))
    }

    @Test
    fun `external and redirect bounce URLs are not treated as internal`() {
        assertFalse(InstagramUrlPolicy.isInternalUrl("https://example.com/article"))
        assertFalse(InstagramUrlPolicy.isInternalUrl("https://youtube.com/watch?v=123"))
        assertFalse(InstagramUrlPolicy.isInternalUrl("https://l.instagram.com/?u=https%3A%2F%2Fexample.com"))
    }

    @Test
    fun `unwrapRedirectUrl extracts real destination from Instagram link shim`() {
        val shim = "https://l.instagram.com/?u=https%3A%2F%2Fdeveloper.android.com%2Fcompose&e=AT123"
        assertEquals(
            "https://developer.android.com/compose",
            InstagramUrlPolicy.unwrapRedirectUrl(shim)
        )
    }

    @Test
    fun `tracker blocker flags telemetry and ad domains while preserving core Instagram APIs`() {
        assertTrue(InstagramUrlPolicy.isTrackerOrAdUrl("https://www.instagram.com/logging_client_events"))
        assertTrue(InstagramUrlPolicy.isTrackerOrAdUrl("https://www.instagram.com/ajax/bz"))
        assertTrue(InstagramUrlPolicy.isTrackerOrAdUrl("https://googleads.g.doubleclick.net/pagead/id"))
        assertTrue(InstagramUrlPolicy.isTrackerOrAdUrl("https://connect.facebook.net/en_US/fbevents.js"))

        assertFalse(InstagramUrlPolicy.isTrackerOrAdUrl("https://www.instagram.com/"))
        assertFalse(InstagramUrlPolicy.isTrackerOrAdUrl("https://www.instagram.com/api/v1/feed/timeline/"))
        assertFalse(InstagramUrlPolicy.isTrackerOrAdUrl("https://static.cdninstagram.com/rsrc.php/bundle.js"))
    }

    @Test
    fun `detects Reels routes accurately`() {
        assertTrue(InstagramUrlPolicy.isReelsRoute("https://www.instagram.com/reels/"))
        assertTrue(InstagramUrlPolicy.isReelsRoute("https://www.instagram.com/reel/CxYz123/"))
        assertFalse(InstagramUrlPolicy.isReelsRoute("https://www.instagram.com/p/CxYz123/"))
        assertFalse(InstagramUrlPolicy.isReelsRoute("https://www.instagram.com/direct/inbox/"))
    }

    @Test
    fun `detectSection maps routes to native navigation tabs`() {
        assertEquals(InstagramSection.HOME, InstagramUrlPolicy.detectSection("https://www.instagram.com/"))
        assertEquals(InstagramSection.SEARCH, InstagramUrlPolicy.detectSection("https://www.instagram.com/explore/"))
        assertEquals(InstagramSection.REELS, InstagramUrlPolicy.detectSection("https://www.instagram.com/reels/"))
        assertEquals(InstagramSection.REELS, InstagramUrlPolicy.detectSection("https://www.instagram.com/reel/CxYz123/"))
        assertEquals(InstagramSection.DIRECT, InstagramUrlPolicy.detectSection("https://www.instagram.com/direct/inbox/"))
        assertEquals(InstagramSection.ACTIVITY, InstagramUrlPolicy.detectSection("https://www.instagram.com/accounts/activity/"))
        assertEquals(InstagramSection.PROFILE, InstagramUrlPolicy.detectSection("https://www.instagram.com/androiddev/"))
        assertEquals(InstagramSection.OTHER, InstagramUrlPolicy.detectSection("https://www.instagram.com/p/ABC123/"))
    }

    @Test
    fun `normalizeDeepLink accepts valid Instagram links and falls back safely`() {
        assertEquals(
            "https://www.instagram.com/p/XYZ123/",
            InstagramUrlPolicy.normalizeDeepLink("https://www.instagram.com/p/XYZ123/")
        )
        assertEquals(
            InstagramUrlPolicy.HOME_URL,
            InstagramUrlPolicy.normalizeDeepLink("https://evil.example.com/phishing")
        )
        assertEquals(
            InstagramUrlPolicy.HOME_URL,
            InstagramUrlPolicy.normalizeDeepLink(null)
        )
    }
}
