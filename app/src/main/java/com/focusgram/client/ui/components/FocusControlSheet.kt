package com.focusgram.client.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.ExploreOff
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.VideocamOff
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.data.FocusPreset
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusControlSheet(
    preferences: FocusPreferences,
    sessionElapsedSeconds: Int,
    blockedTrackersCount: Int,
    blockedDistractionsCount: Int,
    onDismiss: () -> Unit,
    onApplyPreset: (FocusPreset) -> Unit,
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kontrol Mode Fokus",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Perubahan diterapkan langsung tanpa muat ulang",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Session & Privacy Stats Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp)
                ) {
                    StatColumn(
                        value = formatSessionTime(sessionElapsedSeconds),
                        label = "Durasi Sesi"
                    )
                    StatColumn(
                        value = blockedDistractionsCount.toString(),
                        label = "Distraksi Dihapus"
                    )
                    StatColumn(
                        value = blockedTrackersCount.toString(),
                        label = "Pelacak Diblokir"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Preset Cepat",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FocusPreset.entries.forEach { preset ->
                    FilterChip(
                        selected = false,
                        onClick = { onApplyPreset(preset) },
                        label = {
                            Text(
                                text = when (preset) {
                                    FocusPreset.ZEN -> "Zen"
                                    FocusPreset.BALANCED -> "Seimbang"
                                    FocusPreset.CLASSIC -> "Minimalis"
                                }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            FocusToggleRow(
                icon = Icons.Rounded.VideocamOff,
                title = "Sembunyikan Reels",
                subtitle = "Hapus tab Reels & blokir rute /reels",
                checked = preferences.hideReels,
                onCheckedChange = onToggleHideReels
            )
            FocusToggleRow(
                icon = Icons.Rounded.ExploreOff,
                title = "Sembunyikan Grid Explore",
                subtitle = "Tetap bisa mencari akun tanpa konten viral",
                checked = preferences.hideExploreFeed,
                onCheckedChange = onToggleHideExploreFeed
            )
            FocusToggleRow(
                icon = Icons.Rounded.VisibilityOff,
                title = "Sembunyikan Saran & Iklan",
                subtitle = "Filter postingan 'Disarankan' dan 'Bersponsor'",
                checked = preferences.hideSuggestedAndAds,
                onCheckedChange = onToggleHideSuggestedAndAds
            )
            FocusToggleRow(
                icon = Icons.Rounded.Smartphone,
                title = "Hapus Banner 'Buka Aplikasi'",
                subtitle = "Bersihkan ajakan unduh/buka aplikasi resmi",
                checked = preferences.hideOpenInAppPrompts,
                onCheckedChange = onToggleHideOpenInApp
            )
            FocusToggleRow(
                icon = Icons.Rounded.AutoStories,
                title = "Sembunyikan Stories",
                subtitle = "Fokus hanya pada postingan feed & pesan",
                checked = preferences.hideStories,
                onCheckedChange = onToggleHideStories
            )
            FocusToggleRow(
                icon = Icons.Rounded.FavoriteBorder,
                title = "Sembunyikan Jumlah Like",
                subtitle = "Kurangi pemicu validasi sosial",
                checked = preferences.hideEngagementCounts,
                onCheckedChange = onToggleHideEngagementCounts
            )
            FocusToggleRow(
                icon = Icons.Rounded.VolumeOff,
                title = "Auto-Mute Video",
                subtitle = "Bisukan suara video secara otomatis",
                checked = preferences.autoMuteVideos,
                onCheckedChange = onToggleAutoMuteVideos
            )
            FocusToggleRow(
                icon = Icons.Rounded.Contrast,
                title = "Mode Monokrom (Grayscale)",
                subtitle = "Tampilan hitam-putih untuk mengurangi adiksi visual",
                checked = preferences.grayscaleMode,
                onCheckedChange = onToggleGrayscaleMode
            )
            FocusToggleRow(
                icon = Icons.Rounded.Block,
                title = "Blokir Pelacak & Telemetri",
                subtitle = "Hemat kuota & RAM dengan memblokir skrip analitik",
                checked = preferences.blockTrackers,
                onCheckedChange = onToggleBlockTrackers
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onOpenFullSettings,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pengaturan Lengkap (Tema, Timer, Privasi)")
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
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

@Composable
private fun FocusToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun formatSessionTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
