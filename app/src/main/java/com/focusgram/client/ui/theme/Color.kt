package com.focusgram.client.ui.theme

import androidx.compose.ui.graphics.Color

// iOS System Accent Colors (HIG)
val IosBlue = Color(0xFF007AFF)
val IosBlueDark = Color(0xFF0A84FF)
val IosPink = Color(0xFFFF2D55)
val IosPinkDark = Color(0xFFFF375F)
val IosGreen = Color(0xFF34C759)
val IosIndigo = Color(0xFF5856D6)
val IosOrange = Color(0xFFFF9500)
val IosPurple = Color(0xFFAF52DE)
val IosTeal = Color(0xFF30B0C7)
val IosRed = Color(0xFFFF3B30)
val IosRedDark = Color(0xFFFF453A)
val IosGray = Color(0xFF8E8E93)

// Switch tracks (iOS HIG)
val IosSwitchTrackOffLight = Color(0xFFE9E9EA)
val IosSwitchTrackOffDark = Color(0xFF39393D)

// Light palette — crisp iOS System Grouped Background & pure white elevated cards
val PrimaryLight = IosPink
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFFFE5EB)
val OnPrimaryContainerLight = Color(0xFFB0003A)

val SecondaryLight = IosBlue
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFE5F1FF)
val OnSecondaryContainerLight = Color(0xFF004A99)

val TertiaryLight = IosIndigo
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFECEBFF)
val OnTertiaryContainerLight = Color(0xFF2B2A7A)

val BackgroundLight = Color(0xFFF2F2F7)      // systemGroupedBackground
val OnBackgroundLight = Color(0xFF000000)    // label
val SurfaceLight = Color(0xFFFFFFFF)         // secondarySystemGroupedBackground
val OnSurfaceLight = Color(0xFF000000)       // label
val SurfaceVariantLight = Color(0xFFE5E5EA)  // systemGray5
val OnSurfaceVariantLight = Color(0xFF6C6C70) // secondaryLabel
val OutlineLight = Color(0xFF3C3C43)         // separator base (used with alpha)
val SurfaceContainerLight = Color(0xFFFFFFFF)

// Dark palette — iOS Dark Elevated System Surfaces
val PrimaryDark = IosPinkDark
val OnPrimaryDark = Color(0xFFFFFFFF)
val PrimaryContainerDark = Color(0xFF3A121D)
val OnPrimaryContainerDark = Color(0xFFFF85A1)

val SecondaryDark = IosBlueDark
val OnSecondaryDark = Color(0xFFFFFFFF)
val SecondaryContainerDark = Color(0xFF122742)
val OnSecondaryContainerDark = Color(0xFF64B0FF)

val TertiaryDark = Color(0xFF7D7AFF)
val OnTertiaryDark = Color(0xFFFFFFFF)
val TertiaryContainerDark = Color(0xFF23224A)
val OnTertiaryContainerDark = Color(0xFFB5B4FF)

val BackgroundDark = Color(0xFF000000)       // systemGroupedBackground (dark)
val OnBackgroundDark = Color(0xFFFFFFFF)     // label (dark)
val SurfaceDark = Color(0xFF1C1C1E)          // systemGray6 / elevated chrome
val OnSurfaceDark = Color(0xFFFFFFFF)
val SurfaceVariantDark = Color(0xFF2C2C2E)   // systemGray5 (dark)
val OnSurfaceVariantDark = Color(0xFF98989F) // secondaryLabel (dark)
val OutlineDark = Color(0xFF545458)          // separator base (dark)
val SurfaceContainerDark = Color(0xFF1C1C1E)

// True AMOLED blacks with subtle luminous glass separation
val AmoledBlack = Color(0xFF000000)
val AmoledSurfaceContainer = Color(0xFF101012)
