# Panduan Signing & Rilis Google Play Console (LexiLadder)

Dokumen ini menjelaskan langkah-langkah menandatangani (*signing*) Android App Bundle (`.aab`) untuk diunggah ke Google Play Console.

---

## 1. Lokasi dan Data Keystore Rilis

Keystore telah dibuat dan disimpan di folder privat khusus:
- **Lokasi Keystore:** `D:\Android\Keystore\LexyLadder\lexyladder.jks`
- **File Referensi Pengaturan:** `D:\Android\Keystore\LexyLadder\Pengaturan.txt`
- **Alias:** `lexy`
- **Password Keystore:** `lexyladderkey`
- **Password Alias:** `lexyladderkey`
- **Nama Pengembang:** `dearyoti`
- **Validity:** `10000` hari

> [!IMPORTANT]
> **Aturan Rilis:** JANGAN membuat signed bundle / release build sebelum diminta secara eksplisit oleh pengguna.

---

## 2. Konfigurasi Signing di `app/build.gradle.kts` (Digunakan Saat Diminta)

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("D:/Android/Keystore/LexyLadder/lexyladder.jks")
            storePassword = "lexyladderkey"
            keyAlias = "lexy"
            keyPassword = "lexyladderkey"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ...
        }
    }
}
```

---

## 3. Perintah Build Rilis

Setelah keystore siap:

```powershell
.\gradlew bundleRelease
```

File output bundle rilis akan berada di:  
`app/build/outputs/bundle/release/app-release.aab`

---

## 4. Unggah ke Google Play Console (Closed Testing)

1. Buka [Google Play Console](https://play.google.com/console).
2. Pilih Akun Pengembang: **dearyoti** (`made.sudi@gmail.com`).
3. Buat aplikasi baru:
   - Nama aplikasi: **LexiLadder: English Vocab**
   - Bahasa default: **English (United States) - en-US**
   - Tipe aplikasi: **Aplikasi**
   - Gratis / Berbayar: **Gratis**
4. Buka menu **Testing > Closed testing** (Pengujian tertutup).
5. Buat track baru (misal: *Alpha* atau *Closed Testing Track*).
6. Unggah `app-release.aab` (atau `app-debug.aab` jika menguji tanda tangan internal).
7. Ikuti kuis kepatuhan kebijakan:
   - **Data Safety:** Nyatakan tidak ada data pengguna yang dikirim ke server backend mandiri.
   - **Ads:** Centang "Ya, aplikasi saya memuat iklan" (AdMob terintegrasi).
   - **Target Audience:** Pilih usia 13 ke atas (atau sesuai kebijakan lokal).
8. Tambahkan email penguji tertutup (20 penguji sesuai ketentuan Google Play akun personal baru).
9. Rilis ke pengujian tertutup!
