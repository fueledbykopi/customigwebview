package com.focusgram.client.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.focusgram.client.BuildConfig
import com.focusgram.client.R
import com.focusgram.client.data.AppThemeStyle
import com.focusgram.client.data.CustomMediaTarget
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.data.ThemeMode
import com.focusgram.client.ui.components.FocusIcons
import com.focusgram.client.ui.components.IosGroupedCard
import com.focusgram.client.ui.components.IosHairlineDivider
import com.focusgram.client.ui.components.IosIconBadge
import com.focusgram.client.ui.components.IosSectionHeader
import com.focusgram.client.ui.components.IosSegmentedControl
import com.focusgram.client.ui.components.IosToggleRow
import com.focusgram.client.ui.components.LiquidAmbientBackdrop
import com.focusgram.client.ui.theme.IosBlue
import com.focusgram.client.ui.theme.IosGreen
import com.focusgram.client.ui.theme.IosIndigo
import com.focusgram.client.ui.theme.IosOrange
import com.focusgram.client.ui.theme.IosPink
import com.focusgram.client.ui.theme.IosPurple
import com.focusgram.client.ui.theme.IosTeal
import com.focusgram.client.ui.theme.LocalFocusThemeTokens
import com.focusgram.client.ui.theme.themePreviewGradient
import kotlin.math.roundToInt

private val sessionTimerOptions = listOf(0, 10, 15, 30, 45, 60)

@Composable
fun SettingsScreen(
    preferences: FocusPreferences,
    isDarkTheme: Boolean,
    blockedGhostTracesCount: Int,
    chatBgPreview: Bitmap?,
    timelineBgPreview: Bitmap?,
    customAppIconPreview: Bitmap?,
    onNavigateBack: () -> Unit,
    onAppThemeStyleChange: (AppThemeStyle) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onToggleGhostMode: (Boolean) -> Unit,
    onToggleGhostViewStories: (Boolean) -> Unit,
    onToggleGhostReadDm: (Boolean) -> Unit,
    onPickCustomMedia: (CustomMediaTarget) -> Unit,
    onClearCustomMedia: (CustomMediaTarget) -> Unit,
    onPinCustomAppIcon: () -> Unit,
    onBackgroundDimChange: (Float) -> Unit,
    onToggleShowReels: (Boolean) -> Unit,
    onToggleShowExplore: (Boolean) -> Unit,
    onToggleNativeBottomBar: (Boolean) -> Unit,
    onToggleShowTopBar: (Boolean) -> Unit,
    onTogglePullToRefresh: (Boolean) -> Unit,
    onToggleCustomTabs: (Boolean) -> Unit,
    onToggleBlockTrackers: (Boolean) -> Unit,
    onSessionLimitChange: (Int) -> Unit,
    onClearCacheKeepSession: () -> Unit,
    onClearAllSessionData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearSessionDialog by remember { mutableStateOf(false) }
    val tokens = LocalFocusThemeTokens.current
    val customIconImg = remember(customAppIconPreview) { customAppIconPreview?.asImageBitmap() }

    LiquidAmbientBackdrop(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Frosted Liquid Glass iOS Navigation Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tokens.glassElevated)
                    .background(tokens.topSheenBrush)
                    .statusBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onNavigateBack)
                            .padding(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = FocusIcons.ChevronLeft,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Kembali",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Pengaturan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.6.dp)
                        .background(tokens.specularBorderBrush)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .navigationBarsPadding()
            ) {
                // Section 1: Ghost Mode (Invisible Story Viewer & Stealth Full DM Reader)
                IosSectionHeader(title = "Mode Hantu (Ghost Mode)")
                IosGroupedCard {
                    IosToggleRow(
                        icon = FocusIcons.Ghost,
                        iconTint = IosPurple,
                        title = "Aktifkan Ghost Mode",
                        subtitle = if (preferences.ghostModeEnabled) {
                            "Aktif • $blockedGhostTracesCount jejak diblokir sesi ini"
                        } else {
                            "Lihat Story tanpa jejak & baca full DM tanpa ketahuan seen"
                        },
                        checked = preferences.ghostModeEnabled,
                        onCheckedChange = onToggleGhostMode,
                        isDark = isDarkTheme
                    )
                    IosHairlineDivider(startIndent = 56.dp)
                    IosToggleRow(
                        icon = FocusIcons.Stories,
                        iconTint = IosIndigo,
                        title = "Lihat Story Tanpa Jejak",
                        subtitle = "Tonton Story orang lain tanpa masuk daftar viewer",
                        checked = preferences.ghostViewStories,
                        onCheckedChange = onToggleGhostViewStories,
                        isDark = isDarkTheme
                    )
                    IosHairlineDivider(startIndent = 56.dp)
                    IosToggleRow(
                        icon = FocusIcons.ChatBubble,
                        iconTint = IosBlue,
                        title = "Baca Full DM Tanpa Seen",
                        subtitle = "Buka & baca seluruh isi chat DM tanpa tanda terbaca & mengetik",
                        checked = preferences.ghostReadDm,
                        onCheckedChange = onToggleGhostReadDm,
                        isDark = isDarkTheme
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 2: 6 Visual Themes (iOS Liquid Glass + 5 Custom Themes)
                IosSectionHeader(title = "Tema Aplikasi (Liquid Glass & 5 Tema)")
                IosGroupedCard {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IosIconBadge(icon = FocusIcons.Palette, tint = MaterialTheme.colorScheme.primary, iconTint = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Gaya Tema",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Diterapkan ke aplikasi & halaman Instagram secara langsung",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val themePairs = remember { AppThemeStyle.entries.chunked(2) }
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            themePairs.forEach { rowItems ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    rowItems.forEach { style ->
                                        ThemeStyleCard(
                                            style = style,
                                            selected = preferences.appThemeStyle == style,
                                            onClick = { onAppThemeStyleChange(style) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        IosHairlineDivider()
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IosIconBadge(icon = FocusIcons.Moon, tint = IosIndigo)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Mode Kecerahan",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        IosSegmentedControl(
                            items = ThemeMode.entries,
                            selectedItem = preferences.themeMode,
                            labelProvider = { it.label },
                            onSelect = onThemeModeChange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 3: Custom Backgrounds & App Icon (with Pinch-to-Zoom cropper)
                IosSectionHeader(title = "Background Foto & Ikon Aplikasi")
                IosGroupedCard {
                    CustomMediaRow(
                        icon = FocusIcons.ChatBubble,
                        iconTint = IosBlue,
                        title = "Background Chat (DM)",
                        subtitle = if (preferences.hasChatBackground) {
                            "Foto khusus aktif • Ketuk untuk ganti & cubit (pinch)"
                        } else {
                            "Ganti latar belakang pesan DM pakai foto sendiri"
                        },
                        previewBitmap = chatBgPreview,
                        hasCustomMedia = preferences.hasChatBackground,
                        onPick = { onPickCustomMedia(CustomMediaTarget.CHAT_BACKGROUND) },
                        onReset = { onClearCustomMedia(CustomMediaTarget.CHAT_BACKGROUND) }
                    )

                    IosHairlineDivider(startIndent = 56.dp)

                    CustomMediaRow(
                        icon = FocusIcons.Photo,
                        iconTint = IosPurple,
                        title = "Background Timeline",
                        subtitle = if (preferences.hasTimelineBackground) {
                            "Foto khusus aktif • Ketuk untuk ganti & cubit (pinch)"
                        } else {
                            "Ganti latar belakang beranda/feed pakai foto sendiri"
                        },
                        previewBitmap = timelineBgPreview,
                        hasCustomMedia = preferences.hasTimelineBackground,
                        onPick = { onPickCustomMedia(CustomMediaTarget.TIMELINE_BACKGROUND) },
                        onReset = { onClearCustomMedia(CustomMediaTarget.TIMELINE_BACKGROUND) }
                    )

                    if (preferences.hasChatBackground || preferences.hasTimelineBackground) {
                        IosHairlineDivider(startIndent = 14.dp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Redup / Frost Lapisan Foto",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${(preferences.backgroundDimOpacity * 100f).roundToInt()}%",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = preferences.backgroundDimOpacity,
                                onValueChange = onBackgroundDimChange,
                                valueRange = 0f..0.78f,
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    IosHairlineDivider(startIndent = 56.dp)

                    CustomMediaRow(
                        icon = FocusIcons.AppGrid,
                        iconTint = IosPink,
                        title = "Ikon Aplikasi (Foto Sendiri)",
                        subtitle = if (preferences.hasCustomAppIcon) {
                            "Ikon kustom aktif • Ketuk untuk ganti & cubit (pinch)"
                        } else {
                            "Pilih foto sendiri & pasang ikon ke Layar Utama"
                        },
                        previewBitmap = customAppIconPreview,
                        hasCustomMedia = preferences.hasCustomAppIcon,
                        onPick = { onPickCustomMedia(CustomMediaTarget.APP_ICON) },
                        onReset = { onClearCustomMedia(CustomMediaTarget.APP_ICON) }
                    )

                    if (preferences.hasCustomAppIcon) {
                        IosHairlineDivider(startIndent = 14.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Button(
                                onClick = onPinCustomAppIcon,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = FocusIcons.AppGrid,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pasang Ikon Foto ke Layar Utama",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 4: Konten & Navigasi
                IosSectionHeader(title = "Konten & Navigasi")
                IosGroupedCard {
                    IosToggleRow(
                        icon = FocusIcons.ReelsPlayOutline,
                        iconTint = IosPink,
                        title = "Tampilkan Reels",
                        subtitle = "Tampilkan tab & konten Reels di Instagram (matikan untuk Mode Fokus)",
                        checked = preferences.showReels,
                        onCheckedChange = onToggleShowReels,
                        isDark = isDarkTheme
                    )
                    IosHairlineDivider(startIndent = 56.dp)
                    IosToggleRow(
                        icon = FocusIcons.SearchOutline,
                        iconTint = IosIndigo,
                        title = "Tampilkan Grid Explore",
                        subtitle = "Tampilkan konten penemuan & pencarian di tab Explore Instagram",
                        checked = !preferences.hideExploreFeed,
                        onCheckedChange = onToggleShowExplore,
                        isDark = isDarkTheme
                    )
                    IosHairlineDivider(startIndent = 56.dp)
                    IosToggleRow(
                        title = "Bilah Navigasi Bawah Native",
                        subtitle = "Opsional (bukan default): floating dock Liquid Glass di bagian bawah",
                        checked = preferences.useNativeBottomBar,
                        onCheckedChange = onToggleNativeBottomBar,
                        isDark = isDarkTheme
                    )
                    IosHairlineDivider(startIndent = 14.dp)
                    IosToggleRow(
                        title = "Tampilkan Bilah Atas",
                        subtitle = "Matikan untuk layar penuh (tombol Fokus & Pengaturan mengambang tetap tersedia)",
                        checked = preferences.showTopBar,
                        onCheckedChange = onToggleShowTopBar,
                        isDark = isDarkTheme
                    )
                    IosHairlineDivider(startIndent = 14.dp)
                    IosToggleRow(
                        title = "Tarik untuk Muat Ulang",
                        subtitle = "Pull-to-refresh pintar hanya saat di puncak halaman (tidak mengganggu DM/Story)",
                        checked = preferences.enablePullToRefresh,
                        onCheckedChange = onTogglePullToRefresh,
                        isDark = isDarkTheme
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 5: Mindful Session Timer
                IosSectionHeader(title = "Pengingat Waktu Fokus")
                IosGroupedCard {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IosIconBadge(icon = FocusIcons.Timer, tint = IosOrange)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Batas Durasi Sesi",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Pengingat istirahat otomatis agar tetap mindful",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            sessionTimerOptions.forEach { minutes ->
                                val selected = preferences.sessionLimitMinutes == minutes
                                Surface(
                                    onClick = { onSessionLimitChange(minutes) },
                                    shape = CircleShape,
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                                    },
                                    contentColor = if (selected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = if (minutes == 0) "Off" else "${minutes}m",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 6: Privacy & Storage
                IosSectionHeader(title = "Privasi & Performa")
                IosGroupedCard {
                    IosToggleRow(
                        icon = FocusIcons.Globe,
                        iconTint = IosBlue,
                        title = "Buka Tautan Luar di Custom Tabs",
                        subtitle = "Tautan eksternal dibuka cepat & aman dengan Chrome Custom Tabs",
                        checked = preferences.openExternalInCustomTabs,
                        onCheckedChange = onToggleCustomTabs,
                        isDark = isDarkTheme
                    )
                    IosHairlineDivider(startIndent = 56.dp)
                    IosToggleRow(
                        icon = FocusIcons.Block,
                        iconTint = IosGreen,
                        title = "Blokir Pelacak & Telemetri",
                        subtitle = "Cegah analitik latar belakang untuk menghemat RAM & baterai",
                        checked = preferences.blockTrackers,
                        onCheckedChange = onToggleBlockTrackers,
                        isDark = isDarkTheme
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Grouped Action Rows
                IosGroupedCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onClearCacheKeepSession)
                            .padding(horizontal = 14.dp, vertical = 13.dp)
                    ) {
                        IosIconBadge(icon = FocusIcons.Sparkles, tint = IosTeal)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Bersihkan Cache (Tetap Login)",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IosHairlineDivider(startIndent = 56.dp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showClearSessionDialog = true }
                            .padding(horizontal = 14.dp, vertical = 13.dp)
                    ) {
                        IosIconBadge(icon = FocusIcons.Trash, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Hapus Semua Data & Keluar Akun",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 7: About
                IosSectionHeader(title = "Tentang")
                IosGroupedCard {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (customIconImg != null) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(0.7.dp, tokens.specularBorderBrush),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Image(
                                        bitmap = customIconImg,
                                        contentDescription = "Ikon FocusGram",
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                IosIconBadge(
                                    icon = FocusIcons.InfoCircle,
                                    tint = MaterialTheme.colorScheme.primary,
                                    iconTint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "FocusGram",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Versi ${BuildConfig.VERSION_NAME} • Tema ${preferences.appThemeStyle.label}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Klien Instagram Android cepat & native-feel dengan Ghost Mode (Story tanpa jejak & DM tanpa seen), desain iOS Liquid Glass, 6 tema penuh, latar belakang foto DM/Timeline, serta kontrol fokus tanpa distraksi.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showClearSessionDialog) {
        AlertDialog(
            onDismissRequest = { showClearSessionDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = tokens.glassSheet,
            title = {
                Text(
                    text = "Hapus Semua Data Sesi?",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Seluruh cookie, cache, dan penyimpanan lokal Instagram akan dihapus. Anda perlu login ulang setelah tindakan ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearSessionDialog = false
                        onClearAllSessionData()
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Hapus & Keluar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearSessionDialog = false },
                    shape = CircleShape
                ) {
                    Text("Batal", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Composable
private fun ThemeStyleCard(
    style: AppThemeStyle,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = remember(style) { themePreviewGradient(style) }
    val borderStroke = if (selected) {
        BorderStroke(1.8.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(0.7.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.28f))
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (selected) 0.72f else 0.40f),
        border = borderStroke,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Theme miniature glass banner preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Brush.linearGradient(gradient))
            ) {
                // Mini frosted glass bar inside preview
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .height(14.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.36f))
                )

                if (selected) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(5.dp)
                            .size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = FocusIcons.Check,
                                contentDescription = "Aktif",
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = style.label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = style.tagline,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CustomMediaRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    previewBitmap: Bitmap?,
    hasCustomMedia: Boolean,
    onPick: () -> Unit,
    onReset: () -> Unit
) {
    val tokens = LocalFocusThemeTokens.current
    val previewImage = remember(previewBitmap) { previewBitmap?.asImageBitmap() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        if (previewImage != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(0.8.dp, tokens.specularBorderBrush),
                modifier = Modifier.size(38.dp)
            ) {
                Image(
                    bitmap = previewImage,
                    contentDescription = title,
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            IosIconBadge(icon = icon, tint = iconTint)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (hasCustomMedia) {
            Surface(
                onClick = onReset,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
                contentColor = MaterialTheme.colorScheme.error
            ) {
                Text(
                    text = "Reset",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Surface(
            onClick = onPick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = if (hasCustomMedia) "Ubah" else "Pilih",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
            )
        }
    }
}
