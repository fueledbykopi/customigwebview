# Keep JavascriptInterface bridge methods exposed to WebView
-keepclassmembers class com.focusgram.client.webview.InstagramJsBridge {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
-keepattributes *Annotation*

# Keep AndroidX WebKit boundary interfaces
-keep class org.chromium.support_lib_boundary.** { *; }
