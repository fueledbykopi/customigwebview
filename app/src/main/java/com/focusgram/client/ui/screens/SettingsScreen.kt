package com.focusgram.client.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.focusgram.client.BuildConfig
import com.focusgram.client.R
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.data.ThemeMode
import com.focusgram.client.ui.components.FocusIcons
import com.focusgram.client.ui.components.IosGroupedCard
import com.focusgram.client.ui.components.IosHairlineDivider
import com.focusgram.client.ui.components.IosIconBadge
import com.focusgram.client.ui.components.IosSectionHeader
import com.focusgram.client.ui.components.IosSegmentedControl
import com.focusgram.client.ui.components.IosToggleRow
import com.focusgram.client.ui.theme.IosBlue
import com.focusgram.client.ui.theme.IosGreen
import com.focusgram.client.ui.theme.IosIndigo
import com.focusgram.client.ui.theme.IosOrange
import com.focusgram.client.ui.theme.IosTeal

private val sessionTimerOptions = listOf(0, 10, 15, 30, 45, 60)

@Composable
fun SettingsScreen(
    preferences: FocusPreferences,
    isDarkTheme: Boolean,
    onNavigateBack: () -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Slim iOS Navigation Bar with centered title & chevron back button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDarkTheme) {
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                    } else {
                        Color.White.copy(alpha = 0.94f)
                    }
                )
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
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onNavigateBack)
                        .padding(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = FocusIcons.ChevronLeft,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Kembali",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Text(
                    text = "Pengaturan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            IosHairlineDivider()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            // Section 1: Appearance & Navigation
            IosSectionHeader(title = "Tampilan & Navigasi")
            IosGroupedCard {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IosIconBadge(icon = FocusIcons.Moon, tint = IosIndigo)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Mode Tema",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    IosSegmentedControl(
                        items = ThemeMode.entries,
                        selectedItem = preferences.themeMode,
                        labelProvider = { it.label },
                        onSelect = onThemeModeChange
                    )
                }

                IosHairlineDivider(startIndent = 14.dp)

                IosToggleRow(
                    title = "Dock Navigasi Bawah Native",
                    subtitle = "Gunakan floating dock iOS untuk Beranda, Cari, Pesan, Aktivitas & Profil",
                    checked = preferences.useNativeBottomBar,
                    onCheckedChange = onToggleNativeBottomBar,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 14.dp)
                IosToggleRow(
                    title = "Tampilkan Bilah Atas",
                    subtitle = "Matikan untuk layar penuh (tombol Fokus mengambang tetap tersedia)",
                    checked = preferences.showTopBar,
                    onCheckedChange = onToggleShowTopBar,
                    isDark = isDarkTheme
                )
                IosHairlineDivider(startIndent = 14.dp)
                IosToggleRow(
                    title = "Tarik untuk Muat Ulang",
                    subtitle = "Aktifkan pull-to-refresh saat berada di bagian atas halaman",
                    checked = preferences.enablePullToRefresh,
                    onCheckedChange = onTogglePullToRefresh,
                    isDark = isDarkTheme
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section 2: Mindful Session Timer
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
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
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

            // Section 3: Privacy & Storage
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

            // Classic iOS Grouped Action Rows
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
                        color = MaterialTheme.colorScheme.secondary
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

            // Section 4: About
            IosSectionHeader(title = "Tentang")
            IosGroupedCard {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IosIconBadge(
                            icon = FocusIcons.InfoCircle,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "FocusGram",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Versi ${BuildConfig.VERSION_NAME}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Klien Instagram Android ultra-ringan dengan bahasa desain modern, hemat RAM, dan bebas distraksi. Tidak ada data login atau aktivitas yang dikirim ke pihak ketiga.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showClearSessionDialog) {
        AlertDialog(
            onDismissRequest = { showClearSessionDialog = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
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
                    Text("Batal", color = MaterialTheme.colorScheme.secondary)
                }
            }
        )
    }
}
