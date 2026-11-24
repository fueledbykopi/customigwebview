package com.focusgram.client.webview

import com.focusgram.client.data.FocusPreferences

/**
 * Generates clean, idempotent CSS + JavaScript payloads injected into Instagram Web
 * to provide a native-like, lightweight, distraction-free experience.
 */
object InstagramScriptInjector {

    const val BRIDGE_NAME = "FocusGramBridge"

    fun buildCss(prefs: FocusPreferences, isDarkTheme: Boolean, isAmoled: Boolean): String {
        val sb = StringBuilder()

        // Base native-feel polish
        sb.append(
            """
            * {
                -webkit-tap-highlight-color: transparent !important;
            }
            html, body {
                overscroll-behavior-y: contain !important;
                scroll-behavior: smooth;
                -webkit-font-smoothing: antialiased;
            }
            ::-webkit-scrollbar {
                width: 0px !important;
                height: 0px !important;
                display: none !important;
            }
            """.trimIndent()
        )

        if (isDarkTheme && isAmoled) {
            sb.append(
                """
                html, body, main, section, header, nav {
                    background-color: #000000 !important;
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
                [data-testid="app-upsell-banner"],
                [data-focusgram-upsell="true"] {
                    display: none !important;
                }
                """.trimIndent()
            )
        }

        if (prefs.hideReels) {
            sb.append(
                """
                a[href="/reels/"],
                a[href^="/reels/"],
                a[href^="/reel/"],
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
     * Produces a self-contained JavaScript snippet that updates the injected stylesheet,
     * configures the live MutationObserver, hooks SPA navigation events, and cleans DOM distractions.
     */
    fun buildInjectionScript(
        prefs: FocusPreferences,
        isDarkTheme: Boolean,
        isAmoled: Boolean
    ): String {
        val cssEscaped = escapeForJsTemplate(buildCss(prefs, isDarkTheme, isAmoled))
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
                        isDark: $isDarkTheme
                    };
                    window.__focusGramCfg = cfg;

                    // 1. Apply or update dynamic stylesheet
                    var styleId = 'focusgram-custom-styles';
                    var styleEl = document.getElementById(styleId);
                    if (!styleEl) {
                        styleEl = document.createElement('style');
                        styleEl.id = styleId;
                        (document.head || document.documentElement).appendChild(styleEl);
                    }
                    styleEl.textContent = "$cssEscaped";

                    // 2. Helper to notify Android bridge on SPA URL changes
                    function notifyUrl() {
                        if (window.$BRIDGE_NAME && typeof window.$BRIDGE_NAME.onUrlChanged === 'function') {
                            window.$BRIDGE_NAME.onUrlChanged(window.location.href);
                        }
                    }

                    // 3. DOM distraction cleaner (idempotent & lightweight)
                    function cleanDom() {
                        var c = window.__focusGramCfg;
                        if (!c) return;
                        var blockedNow = 0;
                        var path = window.location.pathname || '/';

                        // A. Hide "Open in Instagram app" banners & modals
                        if (c.hideOpenInApp) {
                            var upsellKeywords = [
                                'open app', 'open in app', 'use the instagram app',
                                'use the app', 'get the instagram app', 'get the app',
                                'buka aplikasi', 'buka di aplikasi', 'gunakan aplikasi'
                            ];
                            var buttons = document.querySelectorAll('a, button, div[role="button"]');
                            for (var i = 0; i < buttons.length; i++) {
                                var btn = buttons[i];
                                var text = (btn.innerText || '').trim().toLowerCase();
                                if (!text || text.length > 48) continue;
                                for (var k = 0; k < upsellKeywords.length; k++) {
                                    if (text === upsellKeywords[k] || text.indexOf(upsellKeywords[k]) !== -1) {
                                        var container = btn.closest('div[role="dialog"]') ||
                                                        btn.closest('header') ||
                                                        btn.parentElement;
                                        if (container && container.tagName !== 'BODY' && container.tagName !== 'MAIN') {
                                            if (container.getAttribute('data-focusgram-upsell') !== 'true') {
                                                // Avoid hiding full login forms
                                                if (!container.querySelector('input[type="password"]')) {
                                                    container.setAttribute('data-focusgram-upsell', 'true');
                                                    blockedNow++;
                                                }
                                            }
                                        }
                                        break;
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
                                var el = storyLists[st];
                                if (el.querySelector('canvas') || el.querySelector('button[aria-label*="Story"]') || el.querySelector('button[aria-label*="Cerita"]')) {
                                    var tray = el.closest('div[role="presentation"]') || el.parentElement;
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

                        // D. Hide Explore recommendations grid while keeping search bar usable
                        if (c.hideExploreFeed && path.indexOf('/explore') === 0) {
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

                        // E. Hide Reels containers & redirect guard
                        if (c.hideReels) {
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

                        // G. Hide Instagram's web bottom nav when Native Bottom Bar is enabled
                        var isAuthPage = path.indexOf('/accounts/login') === 0 ||
                                         path.indexOf('/accounts/emailsignup') === 0 ||
                                         path.indexOf('/challenge') === 0;
                        if (c.useNativeBottomBar && !isAuthPage) {
                            var navCandidates = document.querySelectorAll('nav, div[role="tablist"], footer');
                            for (var n = 0; n < navCandidates.length; n++) {
                                var nav = navCandidates[n];
                                var rect = nav.getBoundingClientRect();
                                var isAtBottom = rect.height > 36 && rect.height < 96 &&
                                                 rect.bottom >= window.innerHeight - 16 &&
                                                 rect.top >= window.innerHeight - 110;
                                if (isAtBottom && nav.querySelector('a[href="/"]')) {
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

                    // 4. Install SPA history hooks & MutationObserver once
                    if (!window.__focusGramInstalled) {
                        window.__focusGramInstalled = true;

                        var origPush = history.pushState;
                        history.pushState = function() {
                            var res = origPush.apply(this, arguments);
                            notifyUrl();
                            setTimeout(cleanDom, 150);
                            return res;
                        };

                        var origReplace = history.replaceState;
                        history.replaceState = function() {
                            var res = origReplace.apply(this, arguments);
                            notifyUrl();
                            setTimeout(cleanDom, 150);
                            return res;
                        };

                        window.addEventListener('popstate', function() {
                            notifyUrl();
                            setTimeout(cleanDom, 150);
                        });

                        var debounceTimer = null;
                        var scheduleClean = function() {
                            if (debounceTimer) return;
                            debounceTimer = setTimeout(function() {
                                debounceTimer = null;
                                if (typeof window.requestIdleCallback === 'function') {
                                    window.requestIdleCallback(cleanDom, { timeout: 350 });
                                } else {
                                    cleanDom();
                                }
                            }, 280);
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
     * Attempts to locate the logged-in user's profile link inside the Instagram DOM
     * and navigate directly to it; falls back to /accounts/edit/ if unavailable.
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
                                window.location.href = 'https://www.instagram.com/' + seg + '/';
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
