package com.focusgram.client.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.focusgram.client.BuildConfig
import com.focusgram.client.R
import com.focusgram.client.data.FocusPreferences
import com.focusgram.client.data.ThemeMode

private val sessionTimerOptions = listOf(0, 10, 15, 30, 45, 60)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    preferences: FocusPreferences,
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Appearance & Navigation
            SettingsCard(
                title = "Tampilan & Navigasi",
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.DarkMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Text(
                    text = "Mode Tema",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = preferences.themeMode == mode,
                            onClick = { onThemeModeChange(mode) },
                            label = { Text(mode.label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))

                SettingSwitchRow(
                    title = "Bilah Navigasi Bawah Native",
                    subtitle = "Gunakan navigasi Material 3 untuk Beranda, Cari, Pesan, Aktivitas & Profil",
                    checked = preferences.useNativeBottomBar,
                    onCheckedChange = onToggleNativeBottomBar
                )
                SettingSwitchRow(
                    title = "Tampilkan Bilah Atas",
                    subtitle = "Matikan untuk layar penuh (tombol Fokus mengambang tetap tersedia)",
                    checked = preferences.showTopBar,
                    onCheckedChange = onToggleShowTopBar
                )
                SettingSwitchRow(
                    title = "Tarik untuk Muat Ulang",
                    subtitle = "Aktifkan gestur pull-to-refresh saat berada di bagian atas halaman",
                    checked = preferences.enablePullToRefresh,
                    onCheckedChange = onTogglePullToRefresh
                )
            }

            // Section 2: Mindful Session Timer
            SettingsCard(
                title = "Pengingat Waktu Fokus",
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Text(
                    text = "Tampilkan pengingat istirahat otomatis setelah durasi sesi tertentu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    sessionTimerOptions.forEach { minutes ->
                        FilterChip(
                            selected = preferences.sessionLimitMinutes == minutes,
                            onClick = { onSessionLimitChange(minutes) },
                            label = {
                                Text(if (minutes == 0) "Nonaktif" else "$minutes mnt")
                            }
                        )
                    }
                }
            }

            // Section 3: Privacy & Storage
            SettingsCard(
                title = "Privasi & Penyimpanan",
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                SettingSwitchRow(
                    title = "Buka Tautan Luar di Custom Tabs",
                    subtitle = "Tautan di luar Instagram dibuka cepat & aman dengan Chrome Custom Tabs",
                    checked = preferences.openExternalInCustomTabs,
                    onCheckedChange = onToggleCustomTabs
                )
                SettingSwitchRow(
                    title = "Blokir Pelacak & Telemetri",
                    subtitle = "Cegah permintaan analitik latar belakang untuk menghemat RAM & baterai",
                    checked = preferences.blockTrackers,
                    onCheckedChange = onToggleBlockTrackers
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.16f))
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onClearCacheKeepSession,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CleaningServices,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bersihkan Cache (Tetap Login)")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showClearSessionDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Semua Data & Keluar Akun")
                }
            }

            // Section 4: About
            SettingsCard(
                title = "Tentang FocusGram",
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Text(
                    text = "FocusGram v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Klien Android native ringan berbasis WebView, Kotlin, dan Jetpack Compose Material 3 " +
                        "yang membungkus instagram.com dengan pengalaman bersih, cepat, dan bebas distraksi. " +
                        "Tidak ada data login atau aktivitas yang dikirim ke server pihak ketiga.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showClearSessionDialog) {
        AlertDialog(
            onDismissRequest = { showClearSessionDialog = false },
            title = { Text("Hapus Semua Data Sesi?") },
            text = {
                Text(
                    "Seluruh cookie, cache, dan penyimpanan lokal Instagram akan dihapus. " +
                        "Anda perlu login ulang setelah tindakan ini."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearSessionDialog = false
                        onClearAllSessionData()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Hapus & Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearSessionDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(
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
