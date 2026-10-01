# PPID BPMPSUMSEL — Android WebView App

Aplikasi Android wrapper (WebView) untuk website **https://ppid-bpmpsumsel.id**.
Ketika dibuka, aplikasi langsung menampilkan website tersebut.

## Fitur
- Load website ppid-bpmpsumsel.id penuh (JavaScript, cookie, login)
- Tombol back navigasi WebView (tekan 2x untuk keluar)
- Pull-to-refresh (tarik ke bawah untuk reload)
- Progress bar loading
- Link keluar domain otomatis dibuka di browser
- Halaman offline jika tidak ada koneksi
- Icon adaptive

---

## Cara Build APK (via GitHub Actions — gratis, tanpa install apa-apa)

### 1. Buat repository baru di GitHub
Buka https://github.com/new → beri nama misalnya `ppid-app` → **Create repository**
(jangan centang "Add README")

### 2. Push project ini ke GitHub
Jalankan di folder ini:

```bash
cd ~/ppid-app
git init
git add .
git commit -m "Initial commit: PPID BPMPSUMSEL WebView app"
git branch -M main
git remote add origin https://github.com/USERNAME/ppid-app.git
git push -u origin main
```

> Ganti `USERNAME` dengan username GitHub Anda. Kalau diminta password, gunakan **Personal Access Token** (GitHub → Settings → Developer settings → Personal access tokens), bukan password akun.

### 3. Tunggu build otomatis
Buka tab **Actions** di repo GitHub. Build berjalan otomatis (~3-5 menit).

### 4. Download APK
Ada 2 cara:
- **Artifacts** (selalu ada): di halaman run yang selesai → scroll bawah → `PPID-BPMPSUMSEL-apk`
- **Releases**: jalankan manual via **Actions → Build APK → Run workflow**, APK muncul di tab **Releases**

APK `PPID-BPMPSUMSEL-release.apk` siap dibagikan / dipasang di HP.

---

## Instalasi di HP
1. Kirim file `.apk` ke HP (WhatsApp/Google Drive/usb)
2. Buka file → izinkan **"Install unknown apps"** untuk aplikasi pembuka
3. App terpasang dengan nama **PPID BPMPSUMSEL**

> APK ini ditandatangani dengan debug key. Untuk publish ke Play Store,
> perlu signing key produksi (bisa ditambahkan nanti).

---

## Kalau domain berubah
Edit `app/src/main/java/id/ppid/bpmpsumsel/MainActivity.java`:

```java
private static final String START_URL = "https://ppid-bpmpsumsel.id";
private static final String HOST = "ppid-bpmpsumsel.id";
```

## Ganti nama app / icon
- Nama: `app/src/main/res/values/strings.xml`
- Warna brand: `app/src/main/res/values/colors.xml`
- Icon: ganti `app/src/main/res/mipmap-*/ic_launcher.png` (atau generate ulang)

---

## Struktur
```
ppid-app/
├── .github/workflows/build-apk.yml   # GitHub Actions auto-build
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/id/ppid/bpmpsumsel/MainActivity.java
│       ├── assets/offline.html
│       └── res/ (layout, values, mipmap icons)
├── build.gradle
├── settings.gradle
└── gradle/ (wrapper)
```

## Build lokal (opsional, butuh Android SDK + JDK 17)
```bash
./gradlew assembleRelease
# hasil: app/build/outputs/apk/release/app-release.apk
```

---

## Status Build

✅ **Build berhasil** — APK tersedia.

- Run terakhir: https://github.com/DedekAgung/ppid-app/actions
- Download APK: tab **Actions** → pilih run terbaru → **Artifacts** → `PPID-BPMPSUMSEL-apk`
- Ukuran: release **2.5 MB**, debug 3.2 MB
- `applicationId`: `id.ppid.bpmpsumsel`

### Riwayat perbaikan build
1. `android-actions/setup-android@v3` gagal (`Failed to find package 'tools'`) → dihapus, karena runner `ubuntu-latest` sudah punya Android SDK.
2. `checkDebugDuplicateClasses` gagal (konflik kotlin-stdlib 1.8.22 vs jdk7/jdk8 1.6.21) → diperbaiki dengan `resolutionStrategy.force`.
