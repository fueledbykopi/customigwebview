package com.focusgram.client.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.focusgram.client.data.AppThemeStyle
import com.focusgram.client.data.ThemeMode
import kotlin.math.roundToInt

@Immutable
data class FocusThemeTokens(
    val style: AppThemeStyle,
    val isDark: Boolean,
    val glassSurface: Color,
    val glassElevated: Color,
    val glassSheet: Color,
    val glassBorderStart: Color,
    val glassBorderMid: Color,
    val glassBorderEnd: Color,
    val specularSheenTop: Color,
    val ambientOrbs: List<Color>,
    val activeSwitchTrack: Color,
    val cardCornerRadius: Dp,
    val dockCornerRadius: Dp,
    val previewGradient: List<Color>,
    val cssAccentHex: String,
    val cssSecondaryHex: String,
    val cssBgHex: String,
    val cssSurfaceHex: String,
    val cssTextPrimaryHex: String,
    val cssTextSecondaryHex: String,
    val cssBgRgb: String,
    val cssSurfaceRgb: String,
    val cssSurfaceVariantRgb: String,
    val cssTextPrimaryRgb: String,
    val cssTextSecondaryRgb: String,
    val cssAccentRgb: String,
    val cssBorderRgb: String,
    val cssGlassSurfaceRgba: String,
    val cssBorderRgba: String
) {
    val specularBorderBrush: Brush
        get() = Brush.linearGradient(
            colors = listOf(glassBorderStart, glassBorderMid, glassBorderEnd)
        )

    val topSheenBrush: Brush
        get() = Brush.verticalGradient(
            colors = listOf(specularSheenTop, Color.Transparent)
        )
}

val LocalFocusThemeTokens = staticCompositionLocalOf {
    buildThemeTokens(AppThemeStyle.LIQUID_GLASS, isDark = false, isAmoled = false)
}

private fun Color.toRgbTriplet(): String {
    val r = (red * 255f).roundToInt().coerceIn(0, 255)
    val g = (green * 255f).roundToInt().coerceIn(0, 255)
    val b = (blue * 255f).roundToInt().coerceIn(0, 255)
    return "$r, $g, $b"
}

// ---------------------------------------------------------------------------
// Color Schemes (Light & Dark variants for ALL 6 themes so ThemeMode always works!)
// ---------------------------------------------------------------------------

private val LiquidGlassLightScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    error = IosRed,
    onError = OnPrimaryLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerLow = BackgroundLight,
    surfaceContainerHigh = SurfaceLight
)

private val LiquidGlassDarkScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    error = IosRedDark,
    onError = OnPrimaryDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerLow = BackgroundDark,
    surfaceContainerHigh = SurfaceVariantDark
)

private val CyberpunkScheme = darkColorScheme(
    primary = CyberYellow,
    onPrimary = Color(0xFF090910),
    primaryContainer = Color(0xFF332F06),
    onPrimaryContainer = CyberYellow,
    secondary = CyberCyan,
    onSecondary = Color(0xFF03171A),
    secondaryContainer = Color(0xFF062C33),
    onSecondaryContainer = CyberCyan,
    tertiary = CyberMagenta,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3D061A),
    onTertiaryContainer = Color(0xFFFF85AE),
    background = CyberBg,
    onBackground = CyberOnBg,
    surface = CyberSurface,
    onSurface = CyberOnBg,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberOnSurfaceVariant,
    outline = CyberCyan,
    error = CyberMagenta,
    onError = Color.White,
    surfaceContainer = CyberSurface,
    surfaceContainerLow = CyberBg,
    surfaceContainerHigh = CyberSurfaceVariant
)

private val CyberpunkLightScheme = lightColorScheme(
    primary = Color(0xFFB39B00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF7B2),
    onPrimaryContainer = Color(0xFF3B3200),
    secondary = Color(0xFF0099A8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1F8FC),
    onSecondaryContainer = Color(0xFF003840),
    tertiary = CyberMagenta,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD6E5),
    onTertiaryContainer = Color(0xFF660024),
    background = Color(0xFFF4F6FC),
    onBackground = Color(0xFF101324),
    surface = Color.White,
    onSurface = Color(0xFF101324),
    surfaceVariant = Color(0xFFE6EAF5),
    onSurfaceVariant = Color(0xFF4F5673),
    outline = Color(0xFF0099A8),
    error = CyberMagenta,
    onError = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerLow = Color(0xFFF4F6FC),
    surfaceContainerHigh = Color.White
)

private val Gta6Scheme = darkColorScheme(
    primary = GtaPink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF42102C),
    onPrimaryContainer = Color(0xFFFFA3CC),
    secondary = GtaTeal,
    onSecondary = Color(0xFF041D1B),
    secondaryContainer = Color(0xFF07322E),
    onSecondaryContainer = GtaTeal,
    tertiary = GtaOrange,
    onTertiary = Color(0xFF261202),
    tertiaryContainer = Color(0xFF40230A),
    onTertiaryContainer = Color(0xFFFFC78F),
    background = GtaBg,
    onBackground = GtaOnBg,
    surface = GtaSurface,
    onSurface = GtaOnBg,
    surfaceVariant = GtaSurfaceVariant,
    onSurfaceVariant = GtaOnSurfaceVariant,
    outline = GtaPink,
    error = Color(0xFFFF4D6D),
    onError = Color.White,
    surfaceContainer = GtaSurface,
    surfaceContainerLow = GtaBg,
    surfaceContainerHigh = GtaSurfaceVariant
)

private val Gta6LightScheme = lightColorScheme(
    primary = Color(0xFFD9267B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8EB),
    onPrimaryContainer = Color(0xFF5C0A31),
    secondary = Color(0xFF00968A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCF7F3),
    onSecondaryContainer = Color(0xFF003B36),
    tertiary = Color(0xFFE07519),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE5CC),
    onTertiaryContainer = Color(0xFF4D2400),
    background = Color(0xFFFFF4F9),
    onBackground = Color(0xFF220E2E),
    surface = Color.White,
    onSurface = Color(0xFF220E2E),
    surfaceVariant = Color(0xFFF4E4F0),
    onSurfaceVariant = Color(0xFF6B4C7A),
    outline = Color(0xFFD9267B),
    error = Color(0xFFD9264B),
    onError = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerLow = Color(0xFFFFF4F9),
    surfaceContainerHigh = Color.White
)

private val SpiderManScheme = darkColorScheme(
    primary = SpiderRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF420A13),
    onPrimaryContainer = Color(0xFFFF8FA0),
    secondary = SpiderBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0C264D),
    onSecondaryContainer = Color(0xFF8CC0FF),
    tertiary = SpiderViolet,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF2A1142),
    onTertiaryContainer = Color(0xFFD5A6FF),
    background = SpiderBg,
    onBackground = SpiderOnBg,
    surface = SpiderSurface,
    onSurface = SpiderOnBg,
    surfaceVariant = SpiderSurfaceVariant,
    onSurfaceVariant = SpiderOnSurfaceVariant,
    outline = SpiderBlue,
    error = SpiderRed,
    onError = Color.White,
    surfaceContainer = SpiderSurface,
    surfaceContainerLow = SpiderBg,
    surfaceContainerHigh = SpiderSurfaceVariant
)

private val SpiderManLightScheme = lightColorScheme(
    primary = Color(0xFFD61A31),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9DF),
    onPrimaryContainer = Color(0xFF5C0510),
    secondary = Color(0xFF1565C0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E8FF),
    onSecondaryContainer = Color(0xFF082B59),
    tertiary = SpiderViolet,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEBD8FF),
    onTertiaryContainer = Color(0xFF330F57),
    background = Color(0xFFF3F6FC),
    onBackground = Color(0xFF0E1529),
    surface = Color.White,
    onSurface = Color(0xFF0E1529),
    surfaceVariant = Color(0xFFE4EAF5),
    onSurfaceVariant = Color(0xFF4F5D7D),
    outline = Color(0xFF1565C0),
    error = SpiderRed,
    onError = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerLow = Color(0xFFF3F6FC),
    surfaceContainerHigh = Color.White
)

private val TechScheme = darkColorScheme(
    primary = TechEmerald,
    onPrimary = Color(0xFF03190D),
    primaryContainer = Color(0xFF07331D),
    onPrimaryContainer = TechEmerald,
    secondary = TechBlue,
    onSecondary = Color(0xFF021622),
    secondaryContainer = Color(0xFF072B3F),
    onSecondaryContainer = TechBlue,
    tertiary = TechTeal,
    onTertiary = Color(0xFF031C17),
    tertiaryContainer = Color(0xFF08352C),
    onTertiaryContainer = TechTeal,
    background = TechBg,
    onBackground = TechOnBg,
    surface = TechSurface,
    onSurface = TechOnBg,
    surfaceVariant = TechSurfaceVariant,
    onSurfaceVariant = TechOnSurfaceVariant,
    outline = TechEmerald,
    error = Color(0xFFFF5252),
    onError = Color.White,
    surfaceContainer = TechSurface,
    surfaceContainerLow = TechBg,
    surfaceContainerHigh = TechSurfaceVariant
)

private val TechLightScheme = lightColorScheme(
    primary = Color(0xFF00994D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCBF5DF),
    onPrimaryContainer = Color(0xFF003B1D),
    secondary = Color(0xFF007EB3),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCEFFF),
    onSecondaryContainer = Color(0xFF003247),
    tertiary = Color(0xFF008C74),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCF5EE),
    onTertiaryContainer = Color(0xFF00382E),
    background = Color(0xFFF0F8F6),
    onBackground = Color(0xFF081F1A),
    surface = Color.White,
    onSurface = Color(0xFF081F1A),
    surfaceVariant = Color(0xFFDFEEEC),
    onSurfaceVariant = Color(0xFF426661),
    outline = Color(0xFF00994D),
    error = IosRed,
    onError = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerLow = Color(0xFFF0F8F6),
    surfaceContainerHigh = Color.White
)

private val BarbieLightScheme = lightColorScheme(
    primary = BarbiePink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD6EC),
    onPrimaryContainer = Color(0xFF8C004B),
    secondary = BarbieMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE3F3),
    onSecondaryContainer = Color(0xFF7D004C),
    tertiary = BarbieGold,
    onTertiary = Color(0xFF2B1A00),
    tertiaryContainer = Color(0xFFFFF0CC),
    onTertiaryContainer = Color(0xFF6E4700),
    background = BarbieLightBg,
    onBackground = BarbieLightOnBg,
    surface = BarbieLightSurface,
    onSurface = BarbieLightOnBg,
    surfaceVariant = BarbieLightSurfaceVariant,
    onSurfaceVariant = BarbieLightOnSurfaceVariant,
    outline = BarbiePink,
    error = IosRed,
    onError = Color.White,
    surfaceContainer = BarbieLightSurface,
    surfaceContainerLow = BarbieLightBg,
    surfaceContainerHigh = BarbieLightSurface
)

private val BarbieDarkScheme = darkColorScheme(
    primary = BarbiePinkBright,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4C0D2E),
    onPrimaryContainer = Color(0xFFFF9BD0),
    secondary = BarbieMagenta,
    onSecondary = Color(0xFF280518),
    secondaryContainer = Color(0xFF421230),
    onSecondaryContainer = Color(0xFFFFB2DF),
    tertiary = BarbieGold,
    onTertiary = Color(0xFF241500),
    tertiaryContainer = Color(0xFF3E2904),
    onTertiaryContainer = Color(0xFFFFD580),
    background = BarbieDarkBg,
    onBackground = BarbieDarkOnBg,
    surface = BarbieDarkSurface,
    onSurface = BarbieDarkOnBg,
    surfaceVariant = BarbieDarkSurfaceVariant,
    onSurfaceVariant = BarbieDarkOnSurfaceVariant,
    outline = BarbiePinkBright,
    error = IosRedDark,
    onError = Color.White,
    surfaceContainer = BarbieDarkSurface,
    surfaceContainerLow = BarbieDarkBg,
    surfaceContainerHigh = BarbieDarkSurfaceVariant
)

fun themePreviewGradient(style: AppThemeStyle): List<Color> = when (style) {
    AppThemeStyle.LIQUID_GLASS -> listOf(Color(0xFF70C4FF), Color(0xFF9C8CFF), Color(0xFFFF9ED2))
    AppThemeStyle.CYBERPUNK -> listOf(CyberYellow, CyberCyan, CyberMagenta)
    AppThemeStyle.GTA6 -> listOf(GtaPink, GtaOrange, GtaTeal)
    AppThemeStyle.SPIDERMAN -> listOf(SpiderRed, SpiderBlue, SpiderViolet)
    AppThemeStyle.TECH -> listOf(TechEmerald, TechBlue, Color(0xFF0A2429))
    AppThemeStyle.BARBIE -> listOf(BarbiePink, BarbieMagenta, Color(0xFFFFC1E3))
}

@Composable
fun resolveDarkTheme(
    themeMode: ThemeMode,
    appThemeStyle: AppThemeStyle = AppThemeStyle.LIQUID_GLASS
): Boolean {
    return when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
        ThemeMode.SYSTEM -> when (appThemeStyle) {
            AppThemeStyle.CYBERPUNK,
            AppThemeStyle.GTA6,
            AppThemeStyle.SPIDERMAN,
            AppThemeStyle.TECH -> true
            AppThemeStyle.LIQUID_GLASS,
            AppThemeStyle.BARBIE -> isSystemInDarkTheme()
        }
    }
}

fun buildThemeTokens(
    style: AppThemeStyle,
    isDark: Boolean,
    isAmoled: Boolean
): FocusThemeTokens {
    return when (style) {
        AppThemeStyle.LIQUID_GLASS -> if (isDark) {
            val bgColor = if (isAmoled) AmoledBlack else BackgroundDark
            val surfColor = if (isAmoled) AmoledSurfaceContainer else SurfaceDark
            FocusThemeTokens(
                style = style,
                isDark = true,
                glassSurface = (if (isAmoled) AmoledSurfaceContainer else Color(0xFF1A2033)).copy(alpha = 0.66f),
                glassElevated = (if (isAmoled) Color(0xFF12141E) else Color(0xFF1C2338)).copy(alpha = 0.74f),
                glassSheet = (if (isAmoled) Color(0xFF0B0D14) else Color(0xFF121726)).copy(alpha = 0.88f),
                glassBorderStart = Color.White.copy(alpha = 0.34f),
                glassBorderMid = Color.White.copy(alpha = 0.08f),
                glassBorderEnd = Color(0xFF80C8FF).copy(alpha = 0.24f),
                specularSheenTop = Color.White.copy(alpha = 0.14f),
                ambientOrbs = listOf(Color(0xFF1A6DFF), Color(0xFF7B4DFF), Color(0xFF00C2D1)),
                activeSwitchTrack = IosGreen,
                cardCornerRadius = 22.dp,
                dockCornerRadius = 32.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#0A84FF",
                cssSecondaryHex = "#FF375F",
                cssBgHex = if (isAmoled) "#000000" else "#070912",
                cssSurfaceHex = if (isAmoled) "#10121A" else "#151926",
                cssTextPrimaryHex = "#F5F7FF",
                cssTextSecondaryHex = "#A0A7BD",
                cssBgRgb = bgColor.toRgbTriplet(),
                cssSurfaceRgb = surfColor.toRgbTriplet(),
                cssSurfaceVariantRgb = SurfaceVariantDark.toRgbTriplet(),
                cssTextPrimaryRgb = OnBackgroundDark.toRgbTriplet(),
                cssTextSecondaryRgb = OnSurfaceVariantDark.toRgbTriplet(),
                cssAccentRgb = PrimaryDark.toRgbTriplet(),
                cssBorderRgb = Color(0xFF2B3247).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(22, 27, 42, 0.72)",
                cssBorderRgba = "rgba(255, 255, 255, 0.16)"
            )
        } else {
            FocusThemeTokens(
                style = style,
                isDark = false,
                glassSurface = Color.White.copy(alpha = 0.68f),
                glassElevated = Color.White.copy(alpha = 0.76f),
                glassSheet = Color(0xFFF7F9FE).copy(alpha = 0.88f),
                glassBorderStart = Color.White.copy(alpha = 0.95f),
                glassBorderMid = Color.White.copy(alpha = 0.32f),
                glassBorderEnd = Color(0xFF9AC5FF).copy(alpha = 0.42f),
                specularSheenTop = Color.White.copy(alpha = 0.72f),
                ambientOrbs = listOf(Color(0xFF7CC4FF), Color(0xFFC3B5FF), Color(0xFFFFB8DE)),
                activeSwitchTrack = IosGreen,
                cardCornerRadius = 22.dp,
                dockCornerRadius = 32.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#007AFF",
                cssSecondaryHex = "#FF2D55",
                cssBgHex = "#FFFFFF",
                cssSurfaceHex = "#F4F6FB",
                cssTextPrimaryHex = "#0B0D14",
                cssTextSecondaryHex = "#5B6170",
                cssBgRgb = Color.White.toRgbTriplet(),
                cssSurfaceRgb = BackgroundLight.toRgbTriplet(),
                cssSurfaceVariantRgb = SurfaceVariantLight.toRgbTriplet(),
                cssTextPrimaryRgb = OnBackgroundLight.toRgbTriplet(),
                cssTextSecondaryRgb = OnSurfaceVariantLight.toRgbTriplet(),
                cssAccentRgb = PrimaryLight.toRgbTriplet(),
                cssBorderRgb = Color(0xFFD8DFEC).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(255, 255, 255, 0.78)",
                cssBorderRgba = "rgba(180, 196, 220, 0.55)"
            )
        }

        AppThemeStyle.CYBERPUNK -> if (isDark) {
            val bgColor = if (isAmoled) AmoledBlack else CyberBg
            FocusThemeTokens(
                style = style,
                isDark = true,
                glassSurface = (if (isAmoled) Color(0xFF0B0C16) else CyberSurface).copy(alpha = 0.78f),
                glassElevated = Color(0xFF14182E).copy(alpha = 0.84f),
                glassSheet = Color(0xFF0C0E1C).copy(alpha = 0.92f),
                glassBorderStart = CyberCyan.copy(alpha = 0.68f),
                glassBorderMid = CyberYellow.copy(alpha = 0.32f),
                glassBorderEnd = CyberMagenta.copy(alpha = 0.56f),
                specularSheenTop = CyberCyan.copy(alpha = 0.14f),
                ambientOrbs = listOf(CyberCyan, CyberMagenta, CyberYellow),
                activeSwitchTrack = CyberYellow,
                cardCornerRadius = 14.dp,
                dockCornerRadius = 18.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#FCEE0A",
                cssSecondaryHex = "#00F0FF",
                cssBgHex = if (isAmoled) "#000000" else "#090912",
                cssSurfaceHex = "#131629",
                cssTextPrimaryHex = "#F4F6FF",
                cssTextSecondaryHex = "#A2A8C9",
                cssBgRgb = bgColor.toRgbTriplet(),
                cssSurfaceRgb = CyberSurface.toRgbTriplet(),
                cssSurfaceVariantRgb = CyberSurfaceVariant.toRgbTriplet(),
                cssTextPrimaryRgb = CyberOnBg.toRgbTriplet(),
                cssTextSecondaryRgb = CyberOnSurfaceVariant.toRgbTriplet(),
                cssAccentRgb = CyberYellow.toRgbTriplet(),
                cssBorderRgb = Color(0xFF1D4E5F).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(19, 22, 41, 0.78)",
                cssBorderRgba = "rgba(0, 240, 255, 0.34)"
            )
        } else {
            FocusThemeTokens(
                style = style,
                isDark = false,
                glassSurface = Color.White.copy(alpha = 0.78f),
                glassElevated = Color.White.copy(alpha = 0.86f),
                glassSheet = Color(0xFFF6F8FD).copy(alpha = 0.92f),
                glassBorderStart = Color(0xFF0099A8).copy(alpha = 0.55f),
                glassBorderMid = Color(0xFFB39B00).copy(alpha = 0.32f),
                glassBorderEnd = CyberMagenta.copy(alpha = 0.42f),
                specularSheenTop = Color.White.copy(alpha = 0.78f),
                ambientOrbs = listOf(CyberCyan, Color(0xFFFFD84D), CyberMagenta),
                activeSwitchTrack = Color(0xFF0099A8),
                cardCornerRadius = 14.dp,
                dockCornerRadius = 18.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#0099A8",
                cssSecondaryHex = "#FF0055",
                cssBgHex = "#F4F6FC",
                cssSurfaceHex = "#FFFFFF",
                cssTextPrimaryHex = "#101324",
                cssTextSecondaryHex = "#4F5673",
                cssBgRgb = Color(0xFFF4F6FC).toRgbTriplet(),
                cssSurfaceRgb = Color.White.toRgbTriplet(),
                cssSurfaceVariantRgb = Color(0xFFE6EAF5).toRgbTriplet(),
                cssTextPrimaryRgb = Color(0xFF101324).toRgbTriplet(),
                cssTextSecondaryRgb = Color(0xFF4F5673).toRgbTriplet(),
                cssAccentRgb = Color(0xFF0099A8).toRgbTriplet(),
                cssBorderRgb = Color(0xFFBADDE2).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(255, 255, 255, 0.80)",
                cssBorderRgba = "rgba(0, 153, 168, 0.32)"
            )
        }

        AppThemeStyle.GTA6 -> if (isDark) {
            val bgColor = if (isAmoled) AmoledBlack else GtaBg
            FocusThemeTokens(
                style = style,
                isDark = true,
                glassSurface = (if (isAmoled) Color(0xFF10071B) else GtaSurface).copy(alpha = 0.75f),
                glassElevated = Color(0xFF25133F).copy(alpha = 0.82f),
                glassSheet = Color(0xFF160B27).copy(alpha = 0.91f),
                glassBorderStart = GtaPink.copy(alpha = 0.64f),
                glassBorderMid = GtaOrange.copy(alpha = 0.30f),
                glassBorderEnd = GtaTeal.copy(alpha = 0.54f),
                specularSheenTop = GtaPink.copy(alpha = 0.16f),
                ambientOrbs = listOf(GtaPink, GtaOrange, GtaTeal),
                activeSwitchTrack = GtaPink,
                cardCornerRadius = 20.dp,
                dockCornerRadius = 28.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#FF4D9D",
                cssSecondaryHex = "#00E5D4",
                cssBgHex = if (isAmoled) "#000000" else "#12081F",
                cssSurfaceHex = "#201136",
                cssTextPrimaryHex = "#FFF4FA",
                cssTextSecondaryHex = "#C8B2DD",
                cssBgRgb = bgColor.toRgbTriplet(),
                cssSurfaceRgb = GtaSurface.toRgbTriplet(),
                cssSurfaceVariantRgb = GtaSurfaceVariant.toRgbTriplet(),
                cssTextPrimaryRgb = GtaOnBg.toRgbTriplet(),
                cssTextSecondaryRgb = GtaOnSurfaceVariant.toRgbTriplet(),
                cssAccentRgb = GtaPink.toRgbTriplet(),
                cssBorderRgb = Color(0xFF5A2B5E).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(32, 17, 54, 0.78)",
                cssBorderRgba = "rgba(255, 77, 157, 0.34)"
            )
        } else {
            FocusThemeTokens(
                style = style,
                isDark = false,
                glassSurface = Color.White.copy(alpha = 0.78f),
                glassElevated = Color.White.copy(alpha = 0.86f),
                glassSheet = Color(0xFFFFF6FA).copy(alpha = 0.92f),
                glassBorderStart = Color(0xFFD9267B).copy(alpha = 0.48f),
                glassBorderMid = GtaOrange.copy(alpha = 0.35f),
                glassBorderEnd = Color(0xFF00968A).copy(alpha = 0.44f),
                specularSheenTop = Color.White.copy(alpha = 0.78f),
                ambientOrbs = listOf(GtaPink, GtaOrange, GtaTeal),
                activeSwitchTrack = Color(0xFFD9267B),
                cardCornerRadius = 20.dp,
                dockCornerRadius = 28.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#D9267B",
                cssSecondaryHex = "#00968A",
                cssBgHex = "#FFF4F9",
                cssSurfaceHex = "#FFFFFF",
                cssTextPrimaryHex = "#220E2E",
                cssTextSecondaryHex = "#6B4C7A",
                cssBgRgb = Color(0xFFFFF4F9).toRgbTriplet(),
                cssSurfaceRgb = Color.White.toRgbTriplet(),
                cssSurfaceVariantRgb = Color(0xFFF4E4F0).toRgbTriplet(),
                cssTextPrimaryRgb = Color(0xFF220E2E).toRgbTriplet(),
                cssTextSecondaryRgb = Color(0xFF6B4C7A).toRgbTriplet(),
                cssAccentRgb = Color(0xFFD9267B).toRgbTriplet(),
                cssBorderRgb = Color(0xFFEBC6DE).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(255, 244, 249, 0.82)",
                cssBorderRgba = "rgba(217, 38, 123, 0.28)"
            )
        }

        AppThemeStyle.SPIDERMAN -> if (isDark) {
            val bgColor = if (isAmoled) AmoledBlack else SpiderBg
            FocusThemeTokens(
                style = style,
                isDark = true,
                glassSurface = (if (isAmoled) Color(0xFF0A0E1B) else SpiderSurface).copy(alpha = 0.78f),
                glassElevated = Color(0xFF162140).copy(alpha = 0.84f),
                glassSheet = Color(0xFF0E1429).copy(alpha = 0.92f),
                glassBorderStart = SpiderRed.copy(alpha = 0.66f),
                glassBorderMid = Color.White.copy(alpha = 0.22f),
                glassBorderEnd = SpiderBlue.copy(alpha = 0.58f),
                specularSheenTop = SpiderRed.copy(alpha = 0.14f),
                ambientOrbs = listOf(SpiderRed, SpiderBlue, SpiderViolet),
                activeSwitchTrack = SpiderRed,
                cardCornerRadius = 18.dp,
                dockCornerRadius = 26.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#EF233C",
                cssSecondaryHex = "#2589FF",
                cssBgHex = if (isAmoled) "#000000" else "#0A0F1F",
                cssSurfaceHex = "#131C36",
                cssTextPrimaryHex = "#F5F8FF",
                cssTextSecondaryHex = "#A6B4D6",
                cssBgRgb = bgColor.toRgbTriplet(),
                cssSurfaceRgb = SpiderSurface.toRgbTriplet(),
                cssSurfaceVariantRgb = SpiderSurfaceVariant.toRgbTriplet(),
                cssTextPrimaryRgb = SpiderOnBg.toRgbTriplet(),
                cssTextSecondaryRgb = SpiderOnSurfaceVariant.toRgbTriplet(),
                cssAccentRgb = SpiderRed.toRgbTriplet(),
                cssBorderRgb = Color(0xFF33436E).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(19, 28, 54, 0.78)",
                cssBorderRgba = "rgba(239, 35, 60, 0.34)"
            )
        } else {
            FocusThemeTokens(
                style = style,
                isDark = false,
                glassSurface = Color.White.copy(alpha = 0.78f),
                glassElevated = Color.White.copy(alpha = 0.86f),
                glassSheet = Color(0xFFF5F8FE).copy(alpha = 0.92f),
                glassBorderStart = Color(0xFFD61A31).copy(alpha = 0.48f),
                glassBorderMid = Color(0xFF1565C0).copy(alpha = 0.28f),
                glassBorderEnd = SpiderViolet.copy(alpha = 0.40f),
                specularSheenTop = Color.White.copy(alpha = 0.78f),
                ambientOrbs = listOf(SpiderRed, SpiderBlue, SpiderViolet),
                activeSwitchTrack = Color(0xFFD61A31),
                cardCornerRadius = 18.dp,
                dockCornerRadius = 26.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#D61A31",
                cssSecondaryHex = "#1565C0",
                cssBgHex = "#F3F6FC",
                cssSurfaceHex = "#FFFFFF",
                cssTextPrimaryHex = "#0E1529",
                cssTextSecondaryHex = "#4F5D7D",
                cssBgRgb = Color(0xFFF3F6FC).toRgbTriplet(),
                cssSurfaceRgb = Color.White.toRgbTriplet(),
                cssSurfaceVariantRgb = Color(0xFFE4EAF5).toRgbTriplet(),
                cssTextPrimaryRgb = Color(0xFF0E1529).toRgbTriplet(),
                cssTextSecondaryRgb = Color(0xFF4F5D7D).toRgbTriplet(),
                cssAccentRgb = Color(0xFFD61A31).toRgbTriplet(),
                cssBorderRgb = Color(0xFFC8D4EB).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(255, 255, 255, 0.82)",
                cssBorderRgba = "rgba(214, 26, 49, 0.28)"
            )
        }

        AppThemeStyle.TECH -> if (isDark) {
            val bgColor = if (isAmoled) AmoledBlack else TechBg
            FocusThemeTokens(
                style = style,
                isDark = true,
                glassSurface = (if (isAmoled) Color(0xFF071113) else TechSurface).copy(alpha = 0.80f),
                glassElevated = Color(0xFF102328).copy(alpha = 0.85f),
                glassSheet = Color(0xFF081417).copy(alpha = 0.92f),
                glassBorderStart = TechEmerald.copy(alpha = 0.58f),
                glassBorderMid = TechBlue.copy(alpha = 0.24f),
                glassBorderEnd = TechTeal.copy(alpha = 0.46f),
                specularSheenTop = TechEmerald.copy(alpha = 0.12f),
                ambientOrbs = listOf(TechEmerald, TechBlue, Color(0xFF00796B)),
                activeSwitchTrack = TechEmerald,
                cardCornerRadius = 14.dp,
                dockCornerRadius = 18.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#00E676",
                cssSecondaryHex = "#00B8FF",
                cssBgHex = if (isAmoled) "#000000" else "#060D0E",
                cssSurfaceHex = "#0D1B1F",
                cssTextPrimaryHex = "#EBFFF5",
                cssTextSecondaryHex = "#8CB4B8",
                cssBgRgb = bgColor.toRgbTriplet(),
                cssSurfaceRgb = TechSurface.toRgbTriplet(),
                cssSurfaceVariantRgb = TechSurfaceVariant.toRgbTriplet(),
                cssTextPrimaryRgb = TechOnBg.toRgbTriplet(),
                cssTextSecondaryRgb = TechOnSurfaceVariant.toRgbTriplet(),
                cssAccentRgb = TechEmerald.toRgbTriplet(),
                cssBorderRgb = Color(0xFF1D4245).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(13, 27, 31, 0.80)",
                cssBorderRgba = "rgba(0, 230, 118, 0.30)"
            )
        } else {
            FocusThemeTokens(
                style = style,
                isDark = false,
                glassSurface = Color.White.copy(alpha = 0.78f),
                glassElevated = Color.White.copy(alpha = 0.86f),
                glassSheet = Color(0xFFF2FAF8).copy(alpha = 0.92f),
                glassBorderStart = Color(0xFF00994D).copy(alpha = 0.48f),
                glassBorderMid = Color(0xFF007EB3).copy(alpha = 0.28f),
                glassBorderEnd = Color(0xFF008C74).copy(alpha = 0.42f),
                specularSheenTop = Color.White.copy(alpha = 0.78f),
                ambientOrbs = listOf(TechEmerald, TechBlue, TechTeal),
                activeSwitchTrack = Color(0xFF00994D),
                cardCornerRadius = 14.dp,
                dockCornerRadius = 18.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#00994D",
                cssSecondaryHex = "#007EB3",
                cssBgHex = "#F0F8F6",
                cssSurfaceHex = "#FFFFFF",
                cssTextPrimaryHex = "#081F1A",
                cssTextSecondaryHex = "#426661",
                cssBgRgb = Color(0xFFF0F8F6).toRgbTriplet(),
                cssSurfaceRgb = Color.White.toRgbTriplet(),
                cssSurfaceVariantRgb = Color(0xFFDFEEEC).toRgbTriplet(),
                cssTextPrimaryRgb = Color(0xFF081F1A).toRgbTriplet(),
                cssTextSecondaryRgb = Color(0xFF426661).toRgbTriplet(),
                cssAccentRgb = Color(0xFF00994D).toRgbTriplet(),
                cssBorderRgb = Color(0xFFB8DBD5).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(255, 255, 255, 0.82)",
                cssBorderRgba = "rgba(0, 153, 77, 0.28)"
            )
        }

        AppThemeStyle.BARBIE -> if (isDark) {
            val bgColor = if (isAmoled) AmoledBlack else BarbieDarkBg
            FocusThemeTokens(
                style = style,
                isDark = true,
                glassSurface = (if (isAmoled) Color(0xFF160510) else BarbieDarkSurface).copy(alpha = 0.76f),
                glassElevated = Color(0xFF341029).copy(alpha = 0.82f),
                glassSheet = Color(0xFF21091A).copy(alpha = 0.91f),
                glassBorderStart = Color(0xFFFFA3D6).copy(alpha = 0.62f),
                glassBorderMid = BarbiePinkBright.copy(alpha = 0.28f),
                glassBorderEnd = BarbieGold.copy(alpha = 0.42f),
                specularSheenTop = Color(0xFFFFB2DF).copy(alpha = 0.18f),
                ambientOrbs = listOf(BarbiePinkBright, BarbieMagenta, BarbieGold),
                activeSwitchTrack = BarbiePinkBright,
                cardCornerRadius = 24.dp,
                dockCornerRadius = 32.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#FF3B9A",
                cssSecondaryHex = "#FF8FD1",
                cssBgHex = if (isAmoled) "#000000" else "#1A0714",
                cssSurfaceHex = "#2B0D22",
                cssTextPrimaryHex = "#FFF0F8",
                cssTextSecondaryHex = "#D99FC1",
                cssBgRgb = bgColor.toRgbTriplet(),
                cssSurfaceRgb = BarbieDarkSurface.toRgbTriplet(),
                cssSurfaceVariantRgb = BarbieDarkSurfaceVariant.toRgbTriplet(),
                cssTextPrimaryRgb = BarbieDarkOnBg.toRgbTriplet(),
                cssTextSecondaryRgb = BarbieDarkOnSurfaceVariant.toRgbTriplet(),
                cssAccentRgb = BarbiePinkBright.toRgbTriplet(),
                cssBorderRgb = Color(0xFF63284F).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(43, 13, 34, 0.78)",
                cssBorderRgba = "rgba(255, 59, 154, 0.36)"
            )
        } else {
            FocusThemeTokens(
                style = style,
                isDark = false,
                glassSurface = Color.White.copy(alpha = 0.74f),
                glassElevated = Color.White.copy(alpha = 0.82f),
                glassSheet = Color(0xFFFFF5FA).copy(alpha = 0.90f),
                glassBorderStart = Color.White.copy(alpha = 0.96f),
                glassBorderMid = Color(0xFFFF9AD2).copy(alpha = 0.45f),
                glassBorderEnd = BarbiePink.copy(alpha = 0.36f),
                specularSheenTop = Color.White.copy(alpha = 0.80f),
                ambientOrbs = listOf(BarbieMagenta, Color(0xFFFFB3DE), Color(0xFFFFD166)),
                activeSwitchTrack = BarbiePink,
                cardCornerRadius = 24.dp,
                dockCornerRadius = 32.dp,
                previewGradient = themePreviewGradient(style),
                cssAccentHex = "#E91E8C",
                cssSecondaryHex = "#FF59B6",
                cssBgHex = "#FFF0F7",
                cssSurfaceHex = "#FFFFFF",
                cssTextPrimaryHex = "#2E081D",
                cssTextSecondaryHex = "#8A456A",
                cssBgRgb = BarbieLightBg.toRgbTriplet(),
                cssSurfaceRgb = BarbieLightSurface.toRgbTriplet(),
                cssSurfaceVariantRgb = BarbieLightSurfaceVariant.toRgbTriplet(),
                cssTextPrimaryRgb = BarbieLightOnBg.toRgbTriplet(),
                cssTextSecondaryRgb = BarbieLightOnSurfaceVariant.toRgbTriplet(),
                cssAccentRgb = BarbiePink.toRgbTriplet(),
                cssBorderRgb = Color(0xFFF5BCD9).toRgbTriplet(),
                cssGlassSurfaceRgba = "rgba(255, 240, 247, 0.82)",
                cssBorderRgba = "rgba(233, 30, 140, 0.28)"
            )
        }
    }
}

private fun applyAmoledIfNeeded(scheme: ColorScheme, isAmoled: Boolean): ColorScheme {
    if (!isAmoled) return scheme
    return scheme.copy(
        background = AmoledBlack,
        surface = AmoledBlack,
        surfaceContainer = AmoledSurfaceContainer,
        surfaceContainerLow = AmoledBlack,
        surfaceContainerHigh = AmoledSurfaceContainer
    )
}

@Composable
fun FocusGramTheme(
    themeMode: ThemeMode,
    appThemeStyle: AppThemeStyle = AppThemeStyle.LIQUID_GLASS,
    content: @Composable () -> Unit
) {
    val isDark = resolveDarkTheme(themeMode, appThemeStyle)
    val isAmoled = themeMode == ThemeMode.AMOLED

    val baseScheme = when (appThemeStyle) {
        AppThemeStyle.LIQUID_GLASS -> if (isDark) LiquidGlassDarkScheme else LiquidGlassLightScheme
        AppThemeStyle.CYBERPUNK -> if (isDark) CyberpunkScheme else CyberpunkLightScheme
        AppThemeStyle.GTA6 -> if (isDark) Gta6Scheme else Gta6LightScheme
        AppThemeStyle.SPIDERMAN -> if (isDark) SpiderManScheme else SpiderManLightScheme
        AppThemeStyle.TECH -> if (isDark) TechScheme else TechLightScheme
        AppThemeStyle.BARBIE -> if (isDark) BarbieDarkScheme else BarbieLightScheme
    }

    val colorScheme = if (isDark) applyAmoledIfNeeded(baseScheme, isAmoled) else baseScheme
    val tokens = buildThemeTokens(style = appThemeStyle, isDark = isDark, isAmoled = isAmoled)

    CompositionLocalProvider(LocalFocusThemeTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FocusGramTypography,
            content = content
        )
    }
}
