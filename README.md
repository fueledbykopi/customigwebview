# FocusGram — Custom Instagram Client (Android Native WebView)

**FocusGram** adalah aplikasi Android native ringan berbasis **Kotlin**, **Jetpack Compose**, dan **Material 3** yang membungkus `https://www.instagram.com/` dalam `WebView` modern untuk menghadirkan pengalaman Instagram yang bersih, cepat, hemat baterai, dan bebas distraksi.

---

## Fitur Utama

### 1. Mesin Bebas Distraksi Real-Time (Tanpa Muat Ulang)
Dapat diaktifkan/nonaktifkan langsung melalui **Kontrol Mode Fokus** (ikon perisai di bilah atas) dan diterapkan secara *live* lewat injeksi CSS + `MutationObserver` ringan:
- **Preset Cepat**: *Mode Zen*, *Seimbang (Rekomendasi)*, dan *Minimalis*.
- **Opsi Tampilkan / Sembunyikan Reels**: Aktifkan *Tampilkan Reels* kapan saja lewat Pengaturan atau Mode Fokus untuk menampilkan tab & konten Reels, atau matikan untuk memblokir rute `/reels/`.
- **Sembunyikan Grid Explore**: Menghilangkan grid konten viral di halaman Explore sambil tetap mempertahankan kolom pencarian akun.
- **Sembunyikan Saran & Iklan Feed**: Menyaring kartu *"Disarankan untuk Anda / Suggested for you"* dan *"Bersponsor / Sponsored"*.
- **Hapus Banner "Buka di Aplikasi"**: Membersihkan banner/popup ajakan mengunduh aplikasi resmi dan mencegah redirect `intent://`.
- **Sembunyikan Stories** *(opsional)*: Fokus hanya pada feed utama dan pesan langsung (DM).
- **Sembunyikan Jumlah Like** *(opsional)*: Mengurangi pemicu validasi sosial.
- **Auto-Mute Video** *(opsional)*: Membisukan video secara otomatis.
- **Mode Monokrom (Grayscale)** *(opsional)*: Filter abu-abu untuk mengurangi adiksi visual.
- **Pengingat Waktu Fokus (Mindful Timer)**: Pengingat istirahat otomatis (10 / 15 / 30 / 45 / 60 menit).

### 2. Tema Liquid Glass iOS Terbaru, 5 Tema Kustom & Kustomisasi Foto
- **6 Gaya Tema Premium**:
  - **Liquid Glass (Default)**: Desain *Frosted Blur & Liquid Glass* ala iOS terbaru lengkap dengan ambient mesh blur (`RenderEffect` Android 12+), bingkai specular, dan permukaan kaca transparan.
  - **5 Tema Tambahan**: **Cyberpunk**, **GTA VI**, **Spider-Man**, **Tech**, dan **Barbie**, lengkap dengan mode *Sistem*, *Terang*, *Gelap*, dan **AMOLED Hitam Pekat (`#000000`)**.
- **Background Chat (DM), Background Timeline & Ikon Aplikasi Pakai Foto Sendiri**:
  - Pilih foto dari galeri dengan editor **Pinch-to-Zoom & Geser (Pan)** interaktif + slider zoom untuk mengepaskan foto ke layar chat, timeline, atau bingkai ikon squircle.
  - Atur tingkat redup/frost lapisan foto agar teks tetap jelas, dan pasang ikon foto kustom ke Layar Utama (Home Screen) via *Pinned Shortcut*.
- **Bilah Navigasi Bawah Native (Opsional, Bukan Default)**: Nonaktif secara bawaan agar menggunakan navigasi asli Instagram; dapat diaktifkan kapan saja di Pengaturan sebagai floating dock Liquid Glass (otomatis menampilkan tab Reels jika Reels aktif).
- **Mode Layar Penuh (Immersive)**: Bilah atas dapat disembunyikan, menyisakan tombol perisai Fokus yang ringkas.
- **Pull-to-Refresh Cerdas**: Menggunakan `SwipeRefreshLayout` yang hanya aktif saat halaman berada di posisi scroll paling atas (`scrollY == 0`).
- **Navigasi Kembali Cerdas**: Mendukung penutupan video fullscreen, bottom sheet, riwayat WebView, dan konfirmasi tekan dua kali untuk keluar.
- **Tampilan Offline Native**: Menggantikan halaman error bawaan browser dengan layar Material 3 dan muat ulang otomatis saat internet kembali aktif.

### 3. Privasi, Keamanan & Integrasi Android Penuh
- **Blokir Pelacak & Telemetri**: Memblokir permintaan analitik/iklan (`logging_client_events`, `ajax/bz`, `doubleclick.net`, dll.) di level `shouldInterceptRequest`.
- **Unwrap Link Shim**: Membuka tautan keluar `l.instagram.com/?u=...` langsung ke tujuan aslinya lewat **Chrome Custom Tabs** tanpa melacak hop redirect.
- **Dukungan Upload Media & Video Fullscreen**: Mendukung pemilihan foto/video dari galeri (`onShowFileChooser`), pemutaran video layar penuh (`onShowCustomView`), serta pengunduhan media via `DownloadManager`.
- **Sesi Login Persisten**: Sinkronisasi `CookieManager.flush()` otomatis, dilengkapi tombol *Bersihkan Cache (Tetap Login)* dan *Hapus Semua Data & Keluar*.
- **Deep Link Handler**: Mendukung pembukaan tautan `https://www.instagram.com/...` langsung ke dalam FocusGram.

---

## Struktur Proyek

```text
custom-instagram-client/
├── .github/workflows/build-apk.yml        # CI GitHub Actions (Test + Build Debug APK)
├── gradle/libs.versions.toml              # Version Catalog (AGP 8.5.2, Kotlin 2.0.20, Compose BOM)
└── app/src/main/java/com/focusgram/client/
    ├── FocusGramApp.kt                    # Application container (DataStore + NetworkMonitor)
    ├── MainActivity.kt                    # Edge-to-edge, SplashScreen, FileChooser, CustomTabs, DownloadManager
    ├── data/
    │   ├── FocusPreferences.kt            # Model preferensi, preset, tema & section
    │   └── PreferencesRepository.kt       # Persistensi AndroidX DataStore Preferences
    ├── domain/
    │   ├── InstagramUrlPolicy.kt          # Policy routing internal/eksternal, tracker blocker, link unwrapper
    │   └── NetworkMonitor.kt              # Pemantau konektivitas real-time
    ├── webview/
    │   ├── InstagramScriptInjector.kt     # Generator CSS + JS MutationObserver & SPA hook
    │   ├── InstagramJsBridge.kt           # Bridge JS -> Android (@JavascriptInterface)
    │   ├── FocusWebViewClient.kt          # Intersepsi tracker, routing URL, injeksi & cookie flush
    │   ├── FocusWebChromeClient.kt        # Progress, file upload & fullscreen video
    │   └── WebViewController.kt           # Kontroler aman lifecycle untuk WebView & SwipeRefresh
    └── ui/
        ├── FocusGramViewModel.kt          # State UI, timer fokus, statistik blokir
        ├── theme/                         # Tema Material 3 (Light, Dark, AMOLED, Dynamic Color)
        ├── components/                    # TopBar, BottomNavBar, FocusControlSheet, WebViewContainer, dll.
        └── screens/                       # MainScreen & SettingsScreen
```

---

## Cara Build & Menjalankan

### Melalui Android Studio (Direkomendasikan)
1. Buka folder `custom-instagram-client` di **Android Studio Jellyfish / Koala / Ladybug** atau lebih baru.
2. Pastikan **JDK 17** dan **Android SDK API 34** terpasang.
3. Jalankan Gradle Sync, lalu klik **Run** pada emulator atau perangkat Android (minimal Android 8.0 / API 26).

### Melalui Terminal (Gradle CLI)
```bash
# Menjalankan unit test
./gradlew testDebugUnitTest

# Build Debug APK
./gradlew assembleDebug
```
Hasil APK tersedia di: `app/build/outputs/apk/debug/app-debug.apk`

### Melalui GitHub Actions
Workflow [.github/workflows/build-apk.yml](.github/workflows/build-apk.yml) sudah disertakan untuk otomatis menjalankan unit test dan menghasilkan artefak Debug APK saat push ke GitHub.
