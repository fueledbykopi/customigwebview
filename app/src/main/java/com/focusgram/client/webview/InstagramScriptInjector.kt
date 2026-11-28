package com.focusgram.client.webview

import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.domain.InstagramUrlPolicy
import com.focusgram.client.ui.theme.buildThemeTokens
import java.util.Locale

/**
 * Generates clean, idempotent CSS + JavaScript payloads injected into Instagram Web
 * to provide a fast, native-feeling, accurately themed, Ghost-Mode-enabled experience
 * with reliable custom Chat (DM) & Timeline photo backgrounds.
 */
object InstagramScriptInjector {

    const val BRIDGE_NAME = "FocusGramBridge"

    /**
     * Generates a lightweight script executed at DOCUMENT_START (before any Instagram scripts load)
     * so Ghost Mode hooks on fetch, XMLHttpRequest, sendBeacon, and WebSocket are active from
     * the very first network call.
     */
    fun buildDocumentStartScript(prefs: FocusPreferences): String {
        return """
            (function() {
                try {
                    window.__focusGramGhost = {
                        story: ${prefs.isGhostStoryActive},
                        dm: ${prefs.isGhostDmActive}
                    };
                    ${buildGhostInterceptorBody()}
                } catch (e) {}
            })();
        """.trimIndent()
    }

    private fun buildGhostInterceptorBody(): String {
        return """
            if (!window.__focusGramGhostHooked) {
                window.__focusGramGhostHooked = true;

                function fgNotifyGhost(kind) {
                    try {
                        if (window.$BRIDGE_NAME && typeof window.$BRIDGE_NAME.onGhostActionBlocked === 'function') {
                            window.$BRIDGE_NAME.onGhostActionBlocked(kind);
                        }
                    } catch (e) {}
                }

                function fgExtractBody(body) {
                    if (!body) return '';
                    try {
                        if (typeof body === 'string') return body.slice(0, 6000).toLowerCase();
                        if (typeof URLSearchParams !== 'undefined' && body instanceof URLSearchParams) {
                            return body.toString().slice(0, 6000).toLowerCase();
                        }
                        if (typeof FormData !== 'undefined' && body instanceof FormData) {
                            var parts = [];
                            body.forEach(function(v, k) {
                                if (typeof v === 'string' && parts.length < 40) {
                                    parts.push(k + '=' + v.slice(0, 300));
                                }
                            });
                            return parts.join('&').slice(0, 6000).toLowerCase();
                        }
                        if (body instanceof ArrayBuffer || ArrayBuffer.isView(body)) {
                            var bytes = body instanceof ArrayBuffer
                                ? new Uint8Array(body, 0, Math.min(body.byteLength, 6000))
                                : new Uint8Array(body.buffer, body.byteOffset, Math.min(body.byteLength, 6000));
                            var out = '';
                            for (var i = 0; i < bytes.length; i++) {
                                var ch = bytes[i];
                                if (ch >= 32 && ch <= 126) out += String.fromCharCode(ch);
                            }
                            return out.toLowerCase();
                        }
                    } catch (e) {}
                    return '';
                }

                function fgIsStorySeen(url, bodyStr) {
                    var u = (url || '').toLowerCase();
                    if (u.indexOf('/stories/reel/seen') !== -1 ||
                        u.indexOf('/media/seen') !== -1 ||
                        u.indexOf('/reel/seen') !== -1) {
                        return true;
                    }
                    if (!bodyStr) return false;
                    return bodyStr.indexOf('polarisstoriesv3seenmutation') !== -1 ||
                           bodyStr.indexOf('storiesv3seenmutation') !== -1 ||
                           bodyStr.indexOf('markstoryseen') !== -1 ||
                           bodyStr.indexOf('storyseen') !== -1 ||
                           bodyStr.indexOf('reelseen') !== -1 ||
                           (bodyStr.indexOf('viewseenat') !== -1 && bodyStr.indexOf('reel') !== -1);
                }

                function fgIsDmSeen(url, bodyStr) {
                    var u = (url || '').toLowerCase();
                    if ((u.indexOf('/direct_v2/') !== -1 || u.indexOf('/direct/') !== -1) &&
                        (u.indexOf('/seen') !== -1 ||
                         u.indexOf('/mark_seen') !== -1 ||
                         u.indexOf('/mark_thread_read') !== -1 ||
                         u.indexOf('/indicate_activity') !== -1 ||
                         u.indexOf('/send_read_receipt') !== -1)) {
                        return true;
                    }
                    if (!bodyStr) return false;
                    return bodyStr.indexOf('markthreadasread') !== -1 ||
                           bodyStr.indexOf('mark_thread_read') !== -1 ||
                           bodyStr.indexOf('directmarkseen') !== -1 ||
                           bodyStr.indexOf('sendreadreceipt') !== -1 ||
                           bodyStr.indexOf('send_read_receipt') !== -1 ||
                           bodyStr.indexOf('indicate_activity') !== -1 ||
                           (bodyStr.indexOf('mark_seen') !== -1 && bodyStr.indexOf('direct') !== -1);
                }

                function fgShouldBlockHttp(url, body) {
                    var g = window.__focusGramGhost;
                    if (!g || (!g.story && !g.dm)) return null;
                    var bodyStr = fgExtractBody(body);
                    if (g.story && fgIsStorySeen(url, bodyStr)) return 'story';
                    if (g.dm && fgIsDmSeen(url, bodyStr)) return 'dm';
                    return null;
                }

                // 1. Intercept fetch()
                if (typeof window.fetch === 'function') {
                    var origFetch = window.fetch;
                    window.fetch = function(input, init) {
                        try {
                            var reqUrl = typeof input === 'string'
                                ? input
                                : (input && input.url ? input.url : '');
                            var reqBody = init && init.body ? init.body : null;
                            var blocked = fgShouldBlockHttp(reqUrl, reqBody);
                            if (blocked) {
                                fgNotifyGhost(blocked);
                                return Promise.resolve(
                                    new Response(
                                        JSON.stringify({ data: {}, status: 'ok' }),
                                        {
                                            status: 200,
                                            statusText: 'OK',
                                            headers: { 'Content-Type': 'application/json' }
                                        }
                                    )
                                );
                            }
                        } catch (e) {}
                        return origFetch.apply(this, arguments);
                    };
                }

                // 2. Intercept XMLHttpRequest
                if (typeof XMLHttpRequest !== 'undefined' && XMLHttpRequest.prototype) {
                    var origOpen = XMLHttpRequest.prototype.open;
                    var origSend = XMLHttpRequest.prototype.send;

                    XMLHttpRequest.prototype.open = function(method, url) {
                        this.__fgUrl = typeof url === 'string' ? url : String(url || '');
                        return origOpen.apply(this, arguments);
                    };

                    XMLHttpRequest.prototype.send = function(body) {
                        try {
                            var blocked = fgShouldBlockHttp(this.__fgUrl || '', body);
                            if (blocked) {
                                fgNotifyGhost(blocked);
                                var xhr = this;
                                var fakeBody = '{"data":{},"status":"ok"}';
                                try {
                                    Object.defineProperty(xhr, 'readyState', { configurable: true, value: 4 });
                                    Object.defineProperty(xhr, 'status', { configurable: true, value: 200 });
                                    Object.defineProperty(xhr, 'statusText', { configurable: true, value: 'OK' });
                                    Object.defineProperty(xhr, 'responseText', { configurable: true, value: fakeBody });
                                    Object.defineProperty(xhr, 'response', { configurable: true, value: fakeBody });
                                } catch (defErr) {}
                                setTimeout(function() {
                                    try {
                                        if (typeof xhr.onreadystatechange === 'function') xhr.onreadystatechange();
                                        xhr.dispatchEvent(new Event('readystatechange'));
                                        if (typeof xhr.onload === 'function') xhr.onload();
                                        xhr.dispatchEvent(new Event('load'));
                                        xhr.dispatchEvent(new Event('loadend'));
                                    } catch (evErr) {}
                                }, 8);
                                return;
                            }
                        } catch (e) {}
                        return origSend.apply(this, arguments);
                    };
                }

                // 3. Intercept navigator.sendBeacon
                if (navigator && typeof navigator.sendBeacon === 'function') {
                    var origBeacon = navigator.sendBeacon.bind(navigator);
                    navigator.sendBeacon = function(url, data) {
                        try {
                            var blocked = fgShouldBlockHttp(url || '', data);
                            if (blocked) {
                                fgNotifyGhost(blocked);
                                return true;
                            }
                        } catch (e) {}
                        return origBeacon(url, data);
                    };
                }

                // 4. Intercept WebSocket outgoing Lightspeed MQTT read-receipt / typing frames
                if (typeof WebSocket !== 'undefined' && WebSocket.prototype && typeof WebSocket.prototype.send === 'function') {
                    var origWsSend = WebSocket.prototype.send;
                    WebSocket.prototype.send = function(data) {
                        try {
                            var g = window.__focusGramGhost;
                            if (g && g.dm && data) {
                                var frame = fgExtractBody(data);
                                if (frame) {
                                    var isMessageSend =
                                        frame.indexOf('"label":"46"') !== -1 ||
                                        frame.indexOf('\\"label\\":\\"46\\"') !== -1 ||
                                        frame.indexOf('"label":46') !== -1 ||
                                        frame.indexOf('send_message') !== -1 ||
                                        frame.indexOf('send_item') !== -1 ||
                                        frame.indexOf('broadcast_') !== -1 ||
                                        frame.indexOf('send_reaction') !== -1;

                                    var isReadOrTyping =
                                        frame.indexOf('mark_thread_read') !== -1 ||
                                        frame.indexOf('mark_seen') !== -1 ||
                                        frame.indexOf('send_read_receipt') !== -1 ||
                                        frame.indexOf('read_receipt') !== -1 ||
                                        frame.indexOf('last_read_watermark_ts') !== -1 ||
                                        frame.indexOf('indicate_activity') !== -1 ||
                                        frame.indexOf('"label":"21"') !== -1 ||
                                        frame.indexOf('\\"label\\":\\"21\\"') !== -1 ||
                                        frame.indexOf('"label":21') !== -1 ||
                                        frame.indexOf('"label":"3"') !== -1 ||
                                        frame.indexOf('\\"label\\":\\"3\\"') !== -1;

                                    if (isReadOrTyping && !isMessageSend) {
                                        fgNotifyGhost('dm');
                                        return;
                                    }
                                }
                            }
                        } catch (e) {}
                        return origWsSend.apply(this, arguments);
                    };
                }
            }
        """.trimIndent()
    }

    fun buildCss(prefs: FocusPreferences, isDarkTheme: Boolean, isAmoled: Boolean): String {
        val sb = StringBuilder()
        val tokens = buildThemeTokens(
            style = prefs.appThemeStyle,
            isDark = isDarkTheme,
            isAmoled = isAmoled
        )
        val colorSchemeValue = if (isDarkTheme) "dark" else "light"

        // 1. Native-app touch feel, zero scrollbar clutter, and full CSS custom property theming
        //    Overrides Instagram Web's StyleX --ig-* & Meta web variables so ALL 6 themes & Light/Dark/AMOLED work accurately.
        sb.append(
            """
            * {
                -webkit-tap-highlight-color: transparent !important;
            }
            :root, html, body, .__fb-dark-mode, .__fb-light-mode {
                color-scheme: $colorSchemeValue !important;
                --ig-primary-background: ${tokens.cssBgRgb} !important;
                --ig-secondary-background: ${tokens.cssSurfaceRgb} !important;
                --ig-elevated-background: ${tokens.cssSurfaceRgb} !important;
                --ig-banner-background: ${tokens.cssSurfaceRgb} !important;
                --ig-highlight-background: ${tokens.cssSurfaceVariantRgb} !important;
                --ig-temporary-floating-background: ${tokens.cssSurfaceRgb} !important;
                --ig-toast-background: ${tokens.cssSurfaceVariantRgb} !important;
                --ig-bubble-background: ${tokens.cssSurfaceVariantRgb} !important;
                --ig-primary-text: ${tokens.cssTextPrimaryRgb} !important;
                --ig-secondary-text: ${tokens.cssTextSecondaryRgb} !important;
                --ig-tertiary-text: ${tokens.cssTextSecondaryRgb} !important;
                --ig-link: ${tokens.cssAccentRgb} !important;
                --ig-primary-button: ${tokens.cssAccentRgb} !important;
                --ig-primary-button-hover: ${tokens.cssAccentRgb} !important;
                --ig-stroke: ${tokens.cssBorderRgb} !important;
                --ig-separator: ${tokens.cssBorderRgb} !important;
                --ig-elevated-separator: ${tokens.cssBorderRgb} !important;
                --ig-focus-stroke: ${tokens.cssAccentRgb} !important;
                --ig-badge: ${tokens.cssAccentRgb} !important;
                --bg-primary: ${tokens.cssBgHex} !important;
                --surface-background: ${tokens.cssSurfaceHex} !important;
                --web-wash: ${tokens.cssBgHex} !important;
                --card-background: ${tokens.cssSurfaceHex} !important;
                --nav-bar-background: ${tokens.cssSurfaceHex} !important;
                --messenger-card-background: ${tokens.cssBgHex} !important;
                --primary-text: ${tokens.cssTextPrimaryHex} !important;
                --secondary-text: ${tokens.cssTextSecondaryHex} !important;
                --accent: ${tokens.cssAccentHex} !important;
                --divider: rgb(${tokens.cssBorderRgb}) !important;
            }
            html, body {
                overscroll-behavior-y: contain !important;
                -webkit-font-smoothing: antialiased;
                text-rendering: optimizeSpeed;
                accent-color: ${tokens.cssAccentHex} !important;
            }
            body, a, button, img, svg, header, nav {
                -webkit-touch-callout: none;
                -webkit-user-select: none;
                user-select: none;
            }
            input, textarea, [contenteditable="true"], [role="textbox"], p, span[dir="auto"], h1, h2, h3 {
                -webkit-user-select: text;
                user-select: text;
            }
            ::-webkit-scrollbar {
                width: 0px !important;
                height: 0px !important;
                display: none !important;
            }
            html:not(.focusgram-custom-bg-active),
            html:not(.focusgram-custom-bg-active) body,
            html:not(.focusgram-custom-bg-active) main,
            html:not(.focusgram-custom-bg-active) section {
                background-color: ${tokens.cssBgHex} !important;
                color: ${tokens.cssTextPrimaryHex} !important;
            }
            header, nav {
                background-color: ${tokens.cssGlassSurfaceRgba} !important;
                border-color: ${tokens.cssBorderRgba} !important;
                -webkit-backdrop-filter: blur(14px) saturate(160%);
                backdrop-filter: blur(14px) saturate(160%);
            }
            a:active, button:active {
                opacity: 0.78;
            }
            """.trimIndent()
        )

        // 2. Bulletproof Custom Photo Backgrounds (Chat DM & Timeline)
        //    Renders directly via same-origin virtual URLs served by FocusWebViewClient + deep transparency sweep.
        if (prefs.hasChatBackground || prefs.hasTimelineBackground) {
            val scrimRgb = if (isDarkTheme) "0, 0, 0" else "255, 255, 255"
            val scrimAlpha = String.format(Locale.US, "%.2f", prefs.backgroundDimOpacity.coerceIn(0f, 0.78f))
            val chatBgUrl = "https://www.instagram.com${InstagramUrlPolicy.CHAT_BG_VIRTUAL_PATH}?v=${prefs.chatBackgroundPath.hashCode().toUInt()}"
            val timelineBgUrl = "https://www.instagram.com${InstagramUrlPolicy.TIMELINE_BG_VIRTUAL_PATH}?v=${prefs.timelineBackgroundPath.hashCode().toUInt()}"

            sb.append(
                """
                html.focusgram-custom-bg-active,
                html.focusgram-custom-bg-active body,
                html.focusgram-custom-bg-active #mount_0_0,
                html.focusgram-custom-bg-active [id^="mount_0_0"],
                html.focusgram-custom-bg-active main,
                html.focusgram-custom-bg-active section,
                html.focusgram-custom-bg-active [role="main"],
                html.focusgram-custom-bg-active [role="grid"],
                html.focusgram-custom-bg-active [data-focusgram-transparent="1"] {
                    background-color: transparent !important;
                    background: transparent !important;
                }
                html.focusgram-custom-bg-active body {
                    position: relative !important;
                    z-index: 1 !important;
                }
                html.focusgram-custom-chat-bg::before {
                    content: "" !important;
                    position: fixed !important;
                    top: 0 !important;
                    left: 0 !important;
                    right: 0 !important;
                    bottom: 0 !important;
                    z-index: 0 !important;
                    pointer-events: none !important;
                    background-image: linear-gradient(rgba($scrimRgb, $scrimAlpha), rgba($scrimRgb, $scrimAlpha)), url("$chatBgUrl") !important;
                    background-size: cover !important;
                    background-position: center center !important;
                    background-repeat: no-repeat !important;
                }
                html.focusgram-custom-timeline-bg::before {
                    content: "" !important;
                    position: fixed !important;
                    top: 0 !important;
                    left: 0 !important;
                    right: 0 !important;
                    bottom: 0 !important;
                    z-index: 0 !important;
                    pointer-events: none !important;
                    background-image: linear-gradient(rgba($scrimRgb, $scrimAlpha), rgba($scrimRgb, $scrimAlpha)), url("$timelineBgUrl") !important;
                    background-size: cover !important;
                    background-position: center center !important;
                    background-repeat: no-repeat !important;
                }
                html.focusgram-custom-timeline-bg article {
                    background-color: ${tokens.cssGlassSurfaceRgba} !important;
                    -webkit-backdrop-filter: blur(12px) saturate(150%) !important;
                    backdrop-filter: blur(12px) saturate(150%) !important;
                    border: 1px solid ${tokens.cssBorderRgba} !important;
                    border-radius: 18px !important;
                    margin: 8px 6px !important;
                    overflow: hidden !important;
                }
                html.focusgram-custom-bg-active header,
                html.focusgram-custom-bg-active nav {
                    background-color: ${tokens.cssGlassSurfaceRgba} !important;
                    -webkit-backdrop-filter: blur(16px) saturate(165%) !important;
                    backdrop-filter: blur(16px) saturate(165%) !important;
                    border-color: ${tokens.cssBorderRgba} !important;
                }
                html.focusgram-custom-chat-bg [role="textbox"],
                html.focusgram-custom-chat-bg textarea,
                html.focusgram-custom-chat-bg form {
                    background-color: ${tokens.cssGlassSurfaceRgba} !important;
                    -webkit-backdrop-filter: blur(14px) saturate(155%) !important;
                    backdrop-filter: blur(14px) saturate(155%) !important;
                    border-radius: 22px !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.grayscaleMode) {
            sb.append(
                """
                html {
                    filter: grayscale(100%) !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.hideOpenInAppPrompts) {
            sb.append(
                """
                a[href*="intent://"],
                a[href*="play.google.com"],
                a[href*="apps.apple.com"],
                a[href*="com.instagram.lite"],
                a[href*="instagram.lite"],
                a[href*="instagram.com/download"],
                a[href*="applink"],
                [data-testid="app-upsell-banner"],
                [data-focusgram-upsell="true"] {
                    display: none !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.hideReels) {
            // Keep Explore grid items (/reel/CODE/) visible on /explore/ so Explore never appears empty
            sb.append(
                """
                a[href="/reels/"],
                a[href^="/reels/"],
                html:not(.focusgram-on-explore) a[href^="/reel/"],
                [data-focusgram-reels="true"] {
                    display: none !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.hideExploreFeed) {
            sb.append(
                """
                [data-focusgram-explore-grid="true"] {
                    display: none !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.hideStories) {
            sb.append(
                """
                [data-focusgram-stories="true"] {
                    display: none !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.hideSuggestedAndAds) {
            sb.append(
                """
                [data-focusgram-distraction="true"] {
                    display: none !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.hideEngagementCounts) {
            sb.append(
                """
                a[href*="/liked_by/"],
                a[href*="/c/"],
                [data-focusgram-metrics="true"] {
                    display: none !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.useNativeBottomBar) {
            sb.append(
                """
                [data-focusgram-web-bottom-nav="true"] {
                    display: none !important;
                }
                body {
                    padding-bottom: 12px !important;
                }
                """.trimIndent()
            )
        }

        return sb.toString()
    }

    /**
     * Produces a self-contained JavaScript snippet that updates Ghost Mode flags,
     * dynamic theme stylesheet & dark/light class, SPA navigation hooks,
     * custom DM/Timeline background transparency, Explore stability, and upsell removal.
     */
    fun buildInjectionScript(
        prefs: FocusPreferences,
        isDarkTheme: Boolean,
        isAmoled: Boolean
    ): String {
        val cssEscaped = escapeForJsTemplate(buildCss(prefs, isDarkTheme, isAmoled))
        val tokens = buildThemeTokens(
            style = prefs.appThemeStyle,
            isDark = isDarkTheme,
            isAmoled = isAmoled
        )
        return """
            (function() {
                try {
                    var cfg = {
                        hideOpenInApp: ${prefs.hideOpenInAppPrompts},
                        hideReels: ${prefs.hideReels},
                        hideExploreFeed: ${prefs.hideExploreFeed},
                        hideStories: ${prefs.hideStories},
                        hideSuggested: ${prefs.hideSuggestedAndAds},
                        hideMetrics: ${prefs.hideEngagementCounts},
                        autoMute: ${prefs.autoMuteVideos},
                        useNativeBottomBar: ${prefs.useNativeBottomBar},
                        hasChatBg: ${prefs.hasChatBackground},
                        hasTimelineBg: ${prefs.hasTimelineBackground},
                        ghostStory: ${prefs.isGhostStoryActive},
                        ghostDm: ${prefs.isGhostDmActive},
                        isDark: $isDarkTheme,
                        themeBgRgb: "${tokens.cssBgRgb}"
                    };
                    window.__focusGramCfg = cfg;
                    window.__focusGramGhost = {
                        story: cfg.ghostStory,
                        dm: cfg.ghostDm
                    };

                    // Ensure Ghost Mode hooks are also installed if DOCUMENT_START_SCRIPT wasn't supported
                    ${buildGhostInterceptorBody()}

                    // 1. Sync Instagram's native dark/light root class so icons & text match the chosen theme
                    var rootEl = document.documentElement;
                    if (rootEl) {
                        rootEl.classList.toggle('__fb-dark-mode', !!cfg.isDark);
                        rootEl.classList.toggle('__fb-light-mode', !cfg.isDark);
                    }

                    // 2. Apply or update dynamic stylesheet
                    var styleId = 'focusgram-custom-styles';
                    var styleEl = document.getElementById(styleId);
                    if (!styleEl) {
                        styleEl = document.createElement('style');
                        styleEl.id = styleId;
                        (document.head || document.documentElement).appendChild(styleEl);
                    }
                    styleEl.textContent = "$cssEscaped";

                    // 3. Helper to notify Android bridge on SPA URL changes
                    function notifyUrl() {
                        var href = window.location.href;
                        if (href === window.__focusGramLastHref) return;
                        window.__focusGramLastHref = href;
                        if (window.$BRIDGE_NAME && typeof window.$BRIDGE_NAME.onUrlChanged === 'function') {
                            window.$BRIDGE_NAME.onUrlChanged(href);
                        }
                    }

                    function isOpaquePageBg(bgStr, themeRgb) {
                        if (!bgStr || bgStr === 'transparent' || bgStr === 'rgba(0, 0, 0, 0)') return false;
                        if (bgStr.indexOf('rgba(') === 0) return false;
                        if (bgStr === 'rgb(255, 255, 255)' ||
                            bgStr === 'rgb(0, 0, 0)' ||
                            bgStr === 'rgb(12, 16, 20)' ||
                            bgStr === 'rgb(18, 18, 18)' ||
                            bgStr === 'rgb(24, 24, 24)' ||
                            bgStr === 'rgb(38, 38, 38)' ||
                            bgStr === 'rgb(250, 250, 250)' ||
                            bgStr === 'rgb(' + themeRgb + ')') {
                            return true;
                        }
                        return false;
                    }

                    // 4. DOM distraction cleaner, Explore stabilizer & custom background surface sync
                    function cleanDom() {
                        var c = window.__focusGramCfg;
                        if (!c) return;
                        notifyUrl();

                        var blockedNow = 0;
                        var path = window.location.pathname || '/';
                        var isAuthPage = path.indexOf('/accounts/login') === 0 ||
                                         path.indexOf('/accounts/emailsignup') === 0 ||
                                         path.indexOf('/challenge') === 0;
                        var isDirectPage = path.indexOf('/direct') === 0;
                        var isExplorePage = path === '/explore' || path.indexOf('/explore/') === 0;
                        var isReelsPage = path.indexOf('/reels') === 0 || path.indexOf('/reel/') === 0;
                        var isStoriesPage = path.indexOf('/stories') === 0;

                        var root = document.documentElement;
                        root.classList.toggle('focusgram-on-explore', isExplorePage);

                        // Trigger virtualized grid layout on Explore if it just opened
                        if (isExplorePage && window.__fgLastExplorePath !== path) {
                            window.__fgLastExplorePath = path;
                            setTimeout(function() {
                                try { window.dispatchEvent(new Event('resize')); } catch (e) {}
                            }, 160);
                        } else if (!isExplorePage) {
                            window.__fgLastExplorePath = null;
                        }

                        // 0. Sync custom photo background classes on <html> & sweep opaque wrappers
                        var useChatBg = c.hasChatBg && isDirectPage;
                        var useTimelineBg = c.hasTimelineBg && !isDirectPage && !isAuthPage && !isReelsPage && !isStoriesPage;
                        var anyCustomBg = useChatBg || useTimelineBg;

                        root.classList.toggle('focusgram-custom-bg-active', anyCustomBg);
                        root.classList.toggle('focusgram-custom-chat-bg', useChatBg);
                        root.classList.toggle('focusgram-custom-timeline-bg', useTimelineBg);

                        if (anyCustomBg) {
                            // Walk ALL ancestors up to BODY (no 8-level depth cap)
                            var anchors = document.querySelectorAll('main, [role="main"], [role="grid"], [role="list"], section');
                            for (var an = 0; an < anchors.length; an++) {
                                var cur = anchors[an];
                                while (cur && cur.tagName !== 'BODY' && cur.tagName !== 'HTML') {
                                    if (cur.getAttribute('data-focusgram-transparent') !== '1') {
                                        cur.setAttribute('data-focusgram-transparent', '1');
                                    }
                                    cur = cur.parentElement;
                                }
                            }
                            // Sweep inner full-width opaque backing divs while preserving message bubbles, cards & controls
                            var sweepRoot = document.querySelector('[role="main"]') ||
                                            document.querySelector('main') ||
                                            document.querySelector('[id^="mount_0_0"]') ||
                                            document.body;
                            if (sweepRoot) {
                                var divs = sweepRoot.querySelectorAll('div, section');
                                var maxScan = Math.min(divs.length, 320);
                                var minWidth = Math.floor(window.innerWidth * 0.52);
                                for (var d = 0; d < maxScan; d++) {
                                    var el = divs[d];
                                    if (el.getAttribute('data-focusgram-transparent') === '1') continue;
                                    if (el.closest('header, nav, [role="dialog"], [role="button"], button, a, form')) continue;
                                    if (useTimelineBg && el.closest('article')) continue;
                                    var role = el.getAttribute('role') || '';
                                    if (role === 'button' || role === 'textbox' || role === 'dialog') continue;
                                    if (!el.firstElementChild) continue;
                                    var cs = window.getComputedStyle(el);
                                    if (isOpaquePageBg(cs.backgroundColor, c.themeBgRgb)) {
                                        var rect = el.getBoundingClientRect();
                                        if (rect.width >= minWidth && rect.height >= 40) {
                                            el.setAttribute('data-focusgram-transparent', '1');
                                        }
                                    }
                                }
                            }
                        } else {
                            var prevTrans = document.querySelectorAll('[data-focusgram-transparent="1"]');
                            for (var pt = 0; pt < prevTrans.length; pt++) {
                                prevTrans[pt].removeAttribute('data-focusgram-transparent');
                            }
                        }

                        // A. Hide "Use Instagram Lite" & "Open in Instagram app" banners/prompts SAFELY
                        //    NEVER hides the entire <header> or Instagram's Settings menu dialog!
                        if (c.hideOpenInApp) {
                            var upsellKeywords = [
                                'use instagram lite', 'instagram lite', 'gunakan instagram lite',
                                'pakai instagram lite', 'unduh instagram lite', 'download instagram lite',
                                'get instagram lite', 'open app', 'open in app', 'use the instagram app',
                                'use the app', 'get the instagram app', 'get the app',
                                'switch to the app', 'continue in app', 'open in instagram',
                                'buka aplikasi', 'buka di aplikasi', 'gunakan aplikasi',
                                'dapatkan aplikasi', 'buka di instagram'
                            ];
                            var candidates = document.querySelectorAll('a, button, div[role="button"], div[role="banner"], span');
                            for (var i = 0; i < candidates.length; i++) {
                                var node = candidates[i];
                                if (node.getAttribute('data-focusgram-upsell') === 'true') continue;
                                if (node.getAttribute('data-fg-u-ok') === '1') continue;
                                // Skip containers with many children so we inspect leaf labels/buttons cleanly
                                if (node.tagName === 'SPAN' && node.childElementCount > 2) continue;
                                var text = (node.textContent || '').trim().toLowerCase();
                                if (!text || text.length > 56) {
                                    if (node.tagName !== 'DIV') node.setAttribute('data-fg-u-ok', '1');
                                    continue;
                                }
                                var matched = false;
                                for (var k = 0; k < upsellKeywords.length; k++) {
                                    if (text === upsellKeywords[k] || text.indexOf(upsellKeywords[k]) !== -1) {
                                        matched = true;
                                        break;
                                    }
                                }
                                if (!matched) {
                                    if (node.tagName !== 'DIV') node.setAttribute('data-fg-u-ok', '1');
                                    continue;
                                }

                                // Determine the smallest safe container to hide without ever hiding Settings/Header
                                var target = node.closest('a, button, div[role="button"], div[role="banner"]') || node;
                                var dlg = target.closest('div[role="dialog"]');
                                if (dlg) {
                                    // ONLY hide the entire dialog if it's a small app-upsell modal (NOT Settings menu!)
                                    var dlgBtns = dlg.querySelectorAll('a, button, div[role="button"]').length;
                                    var isSettingsOrAuth = !!dlg.querySelector('input[type="password"], a[href*="/accounts/"], a[href*="/settings"]');
                                    if (!isSettingsOrAuth && dlgBtns <= 4) {
                                        target = dlg;
                                    }
                                } else {
                                    // Walk up at most 3 levels ONLY if wrapper is a dedicated upsell strip (no header nav/logo/main)
                                    var wrap = target.parentElement;
                                    var steps = 0;
                                    while (wrap && steps < 3) {
                                        var tag = wrap.tagName;
                                        if (tag === 'BODY' || tag === 'MAIN' || tag === 'HEADER' || tag === 'NAV' || tag === 'ARTICLE' || tag === 'SECTION') {
                                            break;
                                        }
                                        var hasProtected = !!wrap.querySelector('input, main, article, nav, a[href="/"], a[href*="/direct/"], a[href*="/accounts/"]');
                                        var wrapTextLen = (wrap.textContent || '').trim().length;
                                        if (hasProtected || wrapTextLen > 140) break;
                                        target = wrap;
                                        wrap = wrap.parentElement;
                                        steps++;
                                    }
                                }

                                if (target && target.tagName !== 'BODY' && target.tagName !== 'MAIN' && target.tagName !== 'HEADER') {
                                    if (target.getAttribute('data-focusgram-upsell') !== 'true') {
                                        target.setAttribute('data-focusgram-upsell', 'true');
                                        blockedNow++;
                                    }
                                }
                            }
                        } else {
                            document.querySelectorAll('[data-focusgram-upsell="true"]').forEach(function(el) {
                                el.removeAttribute('data-focusgram-upsell');
                            });
                        }

                        // B. Hide Suggested / Sponsored feed items
                        if (c.hideSuggested) {
                            var feedKeywords = [
                                'sponsored', 'bersponsor',
                                'suggested for you', 'disarankan untuk anda',
                                'suggested posts', 'postingan yang disarankan',
                                'suggested reels', 'reels yang disarankan',
                                'threads', 'because you watched'
                            ];
                            var articles = document.querySelectorAll('article:not([data-focusgram-checked="1"])');
                            for (var a = 0; a < articles.length; a++) {
                                var art = articles[a];
                                art.setAttribute('data-focusgram-checked', '1');
                                if (art.getAttribute('data-focusgram-distraction') === 'true') continue;
                                var spans = art.querySelectorAll('span, div[dir="auto"]');
                                var limit = Math.min(spans.length, 18);
                                for (var s = 0; s < limit; s++) {
                                    var t = (spans[s].textContent || '').trim().toLowerCase();
                                    if (t.length > 0 && t.length < 40) {
                                        for (var fk = 0; fk < feedKeywords.length; fk++) {
                                            if (t === feedKeywords[fk]) {
                                                art.setAttribute('data-focusgram-distraction', 'true');
                                                blockedNow++;
                                                break;
                                            }
                                        }
                                    }
                                    if (art.getAttribute('data-focusgram-distraction') === 'true') break;
                                }
                            }
                        } else {
                            document.querySelectorAll('[data-focusgram-distraction="true"]').forEach(function(el) {
                                el.removeAttribute('data-focusgram-distraction');
                            });
                            document.querySelectorAll('[data-focusgram-checked="1"]').forEach(function(el) {
                                el.removeAttribute('data-focusgram-checked');
                            });
                        }

                        // C. Hide Stories tray on Home
                        if (c.hideStories && (path === '/' || path === '')) {
                            var storyLists = document.querySelectorAll('div[role="menu"], ul');
                            for (var st = 0; st < storyLists.length; st++) {
                                var stEl = storyLists[st];
                                if (stEl.querySelector('canvas') || stEl.querySelector('button[aria-label*="Story"]') || stEl.querySelector('button[aria-label*="Cerita"]')) {
                                    var tray = stEl.closest('div[role="presentation"]') || stEl.parentElement;
                                    if (tray && tray.tagName !== 'MAIN' && tray.tagName !== 'BODY') {
                                        tray.setAttribute('data-focusgram-stories', 'true');
                                    }
                                }
                            }
                        } else {
                            document.querySelectorAll('[data-focusgram-stories="true"]').forEach(function(el) {
                                el.removeAttribute('data-focusgram-stories');
                            });
                        }

                        // D. Hide Explore recommendations grid ONLY when user explicitly enables hideExploreFeed
                        if (c.hideExploreFeed && isExplorePage) {
                            var exploreLinks = document.querySelectorAll('main a[href^="/p/"], main a[href^="/reel/"]');
                            for (var ex = 0; ex < exploreLinks.length; ex++) {
                                var link = exploreLinks[ex];
                                if (link.getAttribute('data-focusgram-explore-grid') !== 'true') {
                                    link.setAttribute('data-focusgram-explore-grid', 'true');
                                    blockedNow++;
                                }
                            }
                        } else {
                            document.querySelectorAll('[data-focusgram-explore-grid="true"]').forEach(function(el) {
                                el.removeAttribute('data-focusgram-explore-grid');
                            });
                        }

                        // E. Hide Reels feed containers when Hide Reels is enabled (never break Explore grid!)
                        if (c.hideReels && !isExplorePage) {
                            var reelLinks = document.querySelectorAll('a[href^="/reel/"], a[href^="/reels"]');
                            for (var rl = 0; rl < reelLinks.length; rl++) {
                                var rParent = reelLinks[rl].closest('article');
                                if (rParent && rParent.getAttribute('data-focusgram-reels') !== 'true') {
                                    rParent.setAttribute('data-focusgram-reels', 'true');
                                    blockedNow++;
                                }
                            }
                        } else {
                            document.querySelectorAll('[data-focusgram-reels="true"]').forEach(function(el) {
                                el.removeAttribute('data-focusgram-reels');
                            });
                        }

                        // F. Auto-mute videos
                        if (c.autoMute) {
                            var videos = document.querySelectorAll('video');
                            for (var v = 0; v < videos.length; v++) {
                                if (!videos[v].muted) {
                                    videos[v].muted = true;
                                }
                            }
                        }

                        // G. Hide Instagram's web bottom nav ONLY when Native Bottom Bar is explicitly enabled
                        if (c.useNativeBottomBar && !isAuthPage) {
                            var navCandidates = document.querySelectorAll('nav:not([data-focusgram-web-bottom-nav="true"]), div[role="tablist"]:not([data-focusgram-web-bottom-nav="true"]), footer:not([data-focusgram-web-bottom-nav="true"])');
                            for (var n = 0; n < navCandidates.length; n++) {
                                var nav = navCandidates[n];
                                if (!nav.querySelector('a[href="/"]')) continue;
                                var nRect = nav.getBoundingClientRect();
                                var isAtBottom = nRect.height > 36 && nRect.height < 96 &&
                                                 nRect.bottom >= window.innerHeight - 16 &&
                                                 nRect.top >= window.innerHeight - 110;
                                if (isAtBottom) {
                                    nav.setAttribute('data-focusgram-web-bottom-nav', 'true');
                                }
                            }
                        } else {
                            document.querySelectorAll('[data-focusgram-web-bottom-nav="true"]').forEach(function(el) {
                                el.removeAttribute('data-focusgram-web-bottom-nav');
                            });
                        }

                        if (blockedNow > 0 && window.$BRIDGE_NAME && typeof window.$BRIDGE_NAME.onDistractionBlocked === 'function') {
                            window.$BRIDGE_NAME.onDistractionBlocked(blockedNow);
                        }
                    }

                    window.__focusGramClean = cleanDom;

                    // 5. Install SPA history hooks, smart pull-to-refresh guard & MutationObserver once
                    if (!window.__focusGramInstalled) {
                        window.__focusGramInstalled = true;

                        var origPush = history.pushState;
                        history.pushState = function() {
                            var res = origPush.apply(this, arguments);
                            notifyUrl();
                            setTimeout(cleanDom, 120);
                            return res;
                        };

                        var origReplace = history.replaceState;
                        history.replaceState = function() {
                            var res = origReplace.apply(this, arguments);
                            notifyUrl();
                            setTimeout(cleanDom, 120);
                            return res;
                        };

                        window.addEventListener('popstate', function() {
                            notifyUrl();
                            setTimeout(cleanDom, 120);
                        });

                        // Smart Pull-to-Refresh detection so swiping down in DM/Stories/Dialogs never reloads the page
                        document.addEventListener('touchstart', function(ev) {
                            try {
                                if (!window.$BRIDGE_NAME || typeof window.$BRIDGE_NAME.onPullToRefreshAllowed !== 'function') return;
                                var p = window.location.pathname || '/';
                                var blockedRoute = p.indexOf('/stories') === 0 ||
                                                   p.indexOf('/direct/t/') === 0 ||
                                                   p.indexOf('/reels') === 0 ||
                                                   p.indexOf('/reel/') === 0;
                                if (blockedRoute || window.scrollY > 2 || document.querySelector('div[role="dialog"]')) {
                                    window.$BRIDGE_NAME.onPullToRefreshAllowed(false);
                                    return;
                                }
                                var el = ev.target;
                                var canPull = true;
                                while (el && el !== document.body && el !== document.documentElement) {
                                    if (el.scrollTop && el.scrollTop > 2) {
                                        canPull = false;
                                        break;
                                    }
                                    el = el.parentElement;
                                }
                                window.$BRIDGE_NAME.onPullToRefreshAllowed(canPull);
                            } catch (e) {}
                        }, { passive: true });

                        var debounceTimer = null;
                        var scheduleClean = function() {
                            if (debounceTimer) return;
                            debounceTimer = setTimeout(function() {
                                debounceTimer = null;
                                if (typeof window.requestIdleCallback === 'function') {
                                    window.requestIdleCallback(cleanDom, { timeout: 320 });
                                } else {
                                    cleanDom();
                                }
                            }, 220);
                        };
                        var observer = new MutationObserver(scheduleClean);

                        if (document.body) {
                            observer.observe(document.body, { childList: true, subtree: true });
                        }
                    }

                    notifyUrl();
                    cleanDom();
                } catch (e) {
                    // Fail silently so Instagram Web remains stable
                }
            })();
        """.trimIndent()
    }

    /**
     * Instant SPA navigation that clicks Instagram's in-DOM React Router link when available
     * (sub-50ms tab switch without full page reload), falling back to direct URL load if needed.
     */
    fun buildSpaNavigateScript(targetPath: String, fallbackUrl: String): String {
        val safePath = escapeForJsTemplate(targetPath)
        val safeFallback = escapeForJsTemplate(fallbackUrl)
        return """
            (function() {
                try {
                    var link = document.querySelector('a[href="$safePath"]');
                    if (link) {
                        link.click();
                        return;
                    }
                    window.location.href = "$safeFallback";
                } catch (e) {
                    window.location.href = "$safeFallback";
                }
            })();
        """.trimIndent()
    }

    /**
     * Attempts to locate the logged-in user's profile link inside the Instagram DOM
     * and navigate directly via SPA click; falls back to /accounts/edit/ if unavailable.
     */
    fun buildNavigateToProfileScript(): String {
        return """
            (function() {
                try {
                    var reserved = ['', 'explore', 'direct', 'reels', 'reel', 'accounts', 'p', 'stories'];
                    var avatarImgs = document.querySelectorAll('a[href^="/"] img[alt*="profile"], a[href^="/"] img[alt*="profil"]');
                    for (var i = 0; i < avatarImgs.length; i++) {
                        var anchor = avatarImgs[i].closest('a[href^="/"]');
                        if (anchor) {
                            var href = anchor.getAttribute('href') || '';
                            var seg = href.replace(/^\//, '').split('/')[0].toLowerCase();
                            if (seg && reserved.indexOf(seg) === -1) {
                                anchor.click();
                                return;
                            }
                        }
                    }
                    window.location.href = 'https://www.instagram.com/accounts/edit/';
                } catch (e) {
                    window.location.href = 'https://www.instagram.com/accounts/edit/';
                }
            })();
        """.trimIndent()
    }

    private fun escapeForJsTemplate(input: String): String {
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "")
            .replace("\n", "\\n")
    }
}
