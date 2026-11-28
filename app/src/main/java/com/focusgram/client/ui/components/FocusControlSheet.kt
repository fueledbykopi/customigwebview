package com.focusgram.client.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.data.FocusPreset
import com.focusgram.client.ui.theme.IosBlue
import com.focusgram.client.ui.theme.IosGray
import com.focusgram.client.ui.theme.IosGreen
import com.focusgram.client.ui.theme.IosIndigo
import com.focusgram.client.ui.theme.IosOrange
import com.focusgram.client.ui.theme.IosPink
import com.focusgram.client.ui.theme.IosPurple
import com.focusgram.client.ui.theme.IosRed
import com.focusgram.client.ui.theme.IosTeal
import com.focusgram.client.ui.theme.LocalFocusThemeTokens
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusControlSheet(
    preferences: FocusPreferences,
    sessionElapsedSeconds: Int,
    blockedTrackersCount: Int,
    blockedDistractionsCount: Int,
    blockedGhostTracesCount: Int,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onApplyPreset: (FocusPreset) -> Unit,
    onToggleGhostMode: (Boolean) -> Unit,
    onToggleGhostViewStories: (Boolean) -> Unit,
    onToggleGhostReadDm: (Boolean) -> Unit,
    onToggleHideReels: (Boolean) -> Unit,
    onToggleHideExploreFeed: (Boolean) -> Unit,
    onToggleHideSuggestedAndAds: (Boolean) -> Unit,
    onToggleHideOpenInApp: (Boolean) -> Unit,
    onToggleHideStories: (Boolean) -> Unit,
    onToggleHideEngagementCounts: (Boolean) -> Unit,
    onToggleAutoMuteVideos: (Boolean) -> Unit,
    onToggleGrayscaleMode: (Boolean) -> Unit,
    onToggleBlockTrackers: (Boolean) -> Unit,
    onOpenFullSettings: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val tokens = LocalFocusThemeTokens.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        containerColor = tokens.glassSheet,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 38.dp, height = 5.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.42f),
                        shape = CircleShape
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            // Sheet Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                IosIconBadge(
                    icon = FocusIcons.ShieldFilled,
                    tint = MaterialTheme.colorScheme.primary,
                    iconTint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mode Fokus & Ghost",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Diterapkan seketika tanpa muat ulang",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inset Grouped Session Stats
            IosGroupedCard {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 12.dp)
                ) {
                    StatColumn(
                        value = formatSessionTime(sessionElapsedSeconds),
                        label = "Sesi",
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 4.dp)
                    )
                    StatColumn(
                        value = blockedDistractionsCount.toString(),
                        label = "Distraksi",
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 4.dp)
                    )
                    StatColumn(
                        value = blockedTrackersCount.toString(),
                        label = "Pelacak",
                        modifier = Modifier.weight(1f)
                    )
                    VerticalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 4.dp)
                    )
                    StatColumn(
                        value = blockedGhostTracesCount.toString(),
                        label = "Ghost",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ghost Mode Quick Controls
            IosSectionHeader(title = "Mode Hantu (Ghost Mode)")
            IosGroupedCard {
                IosToggleRow(
                    icon = FocusIcons.Ghost,
                    iconTint = IosPurple,
                    title = "Aktifkan Ghost Mode",
                    subtitle = "Tonton Story tanpa jejak & baca full DM tanpa status seen",
                    checked = preferences.ghostModeEnabled,
                    onCheckedChange = onToggleGhostMode,
                    isDark = isDarkTheme
                )
                if (preferences.ghostModeEnabled) {
                    IosHairlineDivider(startIndent = 56.dp)
                    IosToggleRow(
                        icon = FocusIcons.Stories,
                        iconTint = IosIndigo,
                        title = "Lihat Story Tanpa Jejak",
                        subtitle = "Akunmu tidak masuk ke daftar penonton Story",
                        checked = preferences.ghostViewStories,
                        onCheckedChange = onToggleGhostViewStories,
                        isDark = isDarkTheme
                    )
                    IosHairlineDivider(startIndent = 56.dp)
                    IosToggleRow(
                        icon = FocusIcons.ChatBubble,
                        iconTint = IosBlue,
                        title = "Baca Full DM Tanpa Seen",
                        subtitle = "Blokir tanda terbaca & indikator mengetik di DM",
                        checked = preferences.ghostReadDm,
                        onCheckedChange = onToggleGhostReadDm,
                        isDark = isDarkTheme
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            IosSectionHeader(title = "Preset Cepat")
            IosSegmentedControl(
                items = FocusPreset.entries,
                selectedItem = preferences.matchingPreset,
                labelProvider = { preset ->
                    when (preset) {
                        FocusPreset.ZEN -> "Zen"
                        FocusPreset.BALANCED -> "Seimbang"
                        FocusPreset.CLASSIC -> "Minimalis"
                    }
                },
                onSelect = onApplyPreset
            )

            Spacer(modifier = Modifier.height(18.dp))

            IosSectionHeader(title = "Filter Konten & Privasi")
            IosGroupedCard {
                IosToggleRow(
                    icon = FocusIcons.ReelsPlayOutline,
                    iconTint = IosPink,
                    title = "Tampilkan Reels",
                    subtitle = "Aktifkan untuk menampilkan tab & video Reels di Instagram",
                    checked = preferences.showReels,
                    onCheckedChange = { show -> onToggleHideReels(!show) },
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 56.dp)
                IosToggleRow(
                    icon = FocusIcons.ExploreOff,
                    iconTint = IosIndigo,
                    title = "Sembunyikan Grid Explore",
                    subtitle = "Matikan agar halaman Explore Instagram tampil penuh",
                    checked = preferences.hideExploreFeed,
                    onCheckedChange = onToggleHideExploreFeed,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 56.dp)
                IosToggleRow(
                    icon = FocusIcons.EyeSlash,
                    iconTint = IosOrange,
                    title = "Sembunyikan Saran & Iklan",
                    subtitle = "Filter postingan 'Disarankan' dan 'Bersponsor'",
                    checked = preferences.hideSuggestedAndAds,
                    onCheckedChange = onToggleHideSuggestedAndAds,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 56.dp)
                IosToggleRow(
                    icon = FocusIcons.Phone,
                    iconTint = IosBlue,
                    title = "Hapus Banner 'Instagram Lite / Aplikasi'",
                    subtitle = "Hilangkan tulisan 'Use Instagram Lite' & ajakan unduh aplikasi",
                    checked = preferences.hideOpenInAppPrompts,
                    onCheckedChange = onToggleHideOpenInApp,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 56.dp)
                IosToggleRow(
                    icon = FocusIcons.Stories,
                    iconTint = IosPurple,
                    title = "Sembunyikan Stories",
                    subtitle = "Fokus hanya pada feed & pesan langsung",
                    checked = preferences.hideStories,
                    onCheckedChange = onToggleHideStories,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 56.dp)
                IosToggleRow(
                    icon = FocusIcons.HeartOutline,
                    iconTint = IosRed,
                    title = "Sembunyikan Jumlah Like",
                    subtitle = "Kurangi pemicu validasi sosial",
                    checked = preferences.hideEngagementCounts,
                    onCheckedChange = onToggleHideEngagementCounts,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 56.dp)
                IosToggleRow(
                    icon = FocusIcons.SpeakerSlash,
                    iconTint = IosTeal,
                    title = "Auto-Mute Video",
                    subtitle = "Bisukan suara video secara otomatis",
                    checked = preferences.autoMuteVideos,
                    onCheckedChange = onToggleAutoMuteVideos,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 56.dp)
                IosToggleRow(
                    icon = FocusIcons.Contrast,
                    iconTint = IosGray,
                    title = "Mode Monokrom (Grayscale)",
                    subtitle = "Tampilan hitam-putih untuk kurangi adiksi visual",
                    checked = preferences.grayscaleMode,
                    onCheckedChange = onToggleGrayscaleMode,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 56.dp)
                IosToggleRow(
                    icon = FocusIcons.Block,
                    iconTint = IosGreen,
                    title = "Blokir Pelacak & Telemetri",
                    subtitle = "Hemat kuota & RAM dari skrip analitik",
                    checked = preferences.blockTrackers,
                    onCheckedChange = onToggleBlockTrackers,
                    isDark = isDarkTheme
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Liquid Glass Full-Width Settings Action Button
            Surface(
                onClick = onOpenFullSettings,
                shape = RoundedCornerShape(16.dp),
                color = tokens.glassSurface,
                contentColor = MaterialTheme.colorScheme.primary,
                border = BorderStroke(
                    width = 0.85.dp,
                    brush = tokens.specularBorderBrush
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 13.dp, horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = FocusIcons.Gear,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pengaturan, Tema & Background",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatColumn(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatSessionTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
