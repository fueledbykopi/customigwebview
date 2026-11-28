package com.focusgram.client.domain

import com.focusgram.client.data.InstagramSection
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.Locale

object InstagramUrlPolicy {

    const val HOME_URL = "https://www.instagram.com/"
    const val EXPLORE_URL = "https://www.instagram.com/explore/"
    const val REELS_URL = "https://www.instagram.com/reels/"
    const val DIRECT_URL = "https://www.instagram.com/direct/inbox/"
    const val ACTIVITY_URL = "https://www.instagram.com/accounts/activity/"

    const val CUSTOM_MEDIA_PATH_PREFIX = "/__focusgram_media__/"
    const val CHAT_BG_VIRTUAL_PATH = "/__focusgram_media__/chat_bg"
    const val TIMELINE_BG_VIRTUAL_PATH = "/__focusgram_media__/timeline_bg"

    private val internalExactHosts = setOf(
        "instagram.com",
        "www.instagram.com",
        "m.instagram.com",
        "i.instagram.com",
        "facebook.com",
        "www.facebook.com",
        "m.facebook.com",
        "mbasic.facebook.com",
        "meta.com",
        "www.meta.com",
        "accounts.meta.com",
        "accountscenter.instagram.com",
        "accountscenter.meta.com"
    )

    private val internalSuffixes = listOf(
        ".instagram.com",
        ".cdninstagram.com",
        ".fbcdn.net"
    )

    private val redirectBounceHosts = setOf(
        "l.instagram.com",
        "lm.instagram.com",
        "l.facebook.com",
        "lm.facebook.com"
    )

    private val blockedTrackerHosts = setOf(
        "doubleclick.net",
        "googleadservices.com",
        "googlesyndication.com",
        "google-analytics.com",
        "googletagmanager.com",
        "connect.facebook.net",
        "pixel.facebook.com",
        "analytics.facebook.com",
        "an.facebook.com",
        "ads.instagram.com",
        "adservice.google.com",
        "crashlytics.com",
        "app-measurement.com",
        "branch.io",
        "app.link"
    )

    private val blockedTrackerPathMarkers = listOf(
        "/logging_client_events",
        "/ajax/bz",
        "/tr/",
        "/qp/batch_fetch_web",
        "/api/v1/ads/",
        "/client_event"
    )

    private val storySeenPathMarkers = listOf(
        "/stories/reel/seen",
        "/api/v1/stories/reel/seen",
        "/api/v1/media/seen",
        "/media/seen",
        "/reel/seen"
    )

    private val dmSeenPathMarkers = listOf(
        "/mark_seen",
        "/mark_thread_read",
        "/indicate_activity",
        "/send_read_receipt"
    )

    private val reservedTopLevelPaths = setOf(
        "",
        "explore",
        "direct",
        "reels",
        "reel",
        "stories",
        "accounts",
        "p",
        "tv",
        "about",
        "legal",
        "developer",
        "challenge",
        "consent",
        "oauth",
        "two_factor",
        "session"
    )

    fun isHttpOrHttps(url: String): Boolean {
        val lower = url.trim().lowercase(Locale.US)
        return lower.startsWith("https://") || lower.startsWith("http://")
    }

    fun isCustomMediaAssetUrl(url: String): Boolean {
        val path = parsePath(url)
        return path.startsWith(CUSTOM_MEDIA_PATH_PREFIX)
    }

    fun isStorySeenUrl(url: String): Boolean {
        val path = parsePath(url)
        if (path.isEmpty()) return false
        return storySeenPathMarkers.any { marker -> path.contains(marker) }
    }

    fun isDmSeenOrTypingUrl(url: String): Boolean {
        val path = parsePath(url)
        if (path.isEmpty()) return false
        if (path.contains("/direct_v2/") && path.contains("/seen")) return true
        if (path.contains("/direct/") && path.contains("/seen")) return true
        return dmSeenPathMarkers.any { marker -> path.contains(marker) }
    }

    fun isRedirectBounceUrl(url: String): Boolean {
        val host = parseHost(url) ?: return false
        return host in redirectBounceHosts
    }

    /**
     * Unwraps Instagram/Facebook outbound redirect links like:
     * https://l.instagram.com/?u=https%3A%2F%2Fexample.com&e=...
     */
    fun unwrapRedirectUrl(url: String): String {
        val uri = parseUri(url) ?: return url
        val host = uri.host?.lowercase(Locale.US) ?: return url
        if (host !in redirectBounceHosts) return url

        val query = uri.rawQuery ?: return url
        for (pair in query.split("&")) {
            val idx = pair.indexOf('=')
            if (idx <= 0) continue
            val key = pair.substring(0, idx)
            if (key == "u") {
                val encodedTarget = pair.substring(idx + 1)
                val decoded = runCatching {
                    URLDecoder.decode(encodedTarget, StandardCharsets.UTF_8.name())
                }.getOrNull()
                if (!decoded.isNullOrBlank() && isHttpOrHttps(decoded)) {
                    return decoded
                }
            }
        }
        return url
    }

    fun isInternalUrl(url: String): Boolean {
        val host = parseHost(url) ?: return false
        if (host in redirectBounceHosts) return false
        if (host in internalExactHosts) return true
        return internalSuffixes.any { suffix -> host.endsWith(suffix) }
    }

    fun isTrackerOrAdUrl(url: String): Boolean {
        val uri = parseUri(url) ?: return false
        val host = uri.host?.lowercase(Locale.US) ?: return false
        if (blockedTrackerHosts.any { blocked -> host == blocked || host.endsWith(".$blocked") }) {
            return true
        }
        val path = uri.path?.lowercase(Locale.US) ?: ""
        return blockedTrackerPathMarkers.any { marker -> path.contains(marker) }
    }

    fun isReelsRoute(url: String): Boolean {
        val path = parsePath(url)
        return path == "/reels" ||
            path.startsWith("/reels/") ||
            path.startsWith("/reel/")
    }

    fun isExploreRoute(url: String): Boolean {
        val path = parsePath(url)
        return path == "/explore" || path.startsWith("/explore/")
    }

    fun isStoryRoute(url: String): Boolean {
        val path = parsePath(url)
        return path == "/stories" || path.startsWith("/stories/")
    }

    fun isDirectThreadRoute(url: String): Boolean {
        val path = parsePath(url)
        return path.startsWith("/direct/t/")
    }

    fun isAuthOrChallengeRoute(url: String): Boolean {
        val uri = parseUri(url) ?: return false
        val host = uri.host?.lowercase(Locale.US) ?: return false
        if (host.endsWith("facebook.com") || host.endsWith("meta.com")) {
            return true
        }
        val path = uri.path?.lowercase(Locale.US) ?: ""
        return path.startsWith("/accounts/login") ||
            path.startsWith("/accounts/emailsignup") ||
            path.startsWith("/accounts/onetap") ||
            path.startsWith("/accounts/password") ||
            path.startsWith("/challenge") ||
            path.startsWith("/consent") ||
            path.startsWith("/two_factor")
    }

    fun detectSection(url: String): InstagramSection {
        val uri = parseUri(url) ?: return InstagramSection.HOME
        val host = uri.host?.lowercase(Locale.US) ?: return InstagramSection.HOME
        if (!host.endsWith("instagram.com")) return InstagramSection.OTHER

        val path = (uri.path ?: "/").lowercase(Locale.US).trimEnd('/')
        if (path.isEmpty()) return InstagramSection.HOME

        val firstSegment = path.removePrefix("/").substringBefore("/")
        return when {
            firstSegment == "explore" -> InstagramSection.SEARCH
            firstSegment == "reels" || firstSegment == "reel" -> InstagramSection.REELS
            firstSegment == "direct" -> InstagramSection.DIRECT
            path.startsWith("/accounts/activity") -> InstagramSection.ACTIVITY
            path.startsWith("/accounts/edit") -> InstagramSection.PROFILE
            firstSegment !in reservedTopLevelPaths -> InstagramSection.PROFILE
            else -> InstagramSection.OTHER
        }
    }

    fun normalizeDeepLink(rawUrl: String?): String {
        if (rawUrl.isNullOrBlank()) return HOME_URL
        val unwrapped = unwrapRedirectUrl(rawUrl)
        return if (isHttpOrHttps(unwrapped) && isInternalUrl(unwrapped)) {
            unwrapped
        } else {
            HOME_URL
        }
    }

    private fun parseUri(url: String): URI? = runCatching { URI(url.trim()) }.getOrNull()

    private fun parseHost(url: String): String? = parseUri(url)?.host?.lowercase(Locale.US)

    private fun parsePath(url: String): String =
        parseUri(url)?.path?.lowercase(Locale.US) ?: ""
}
