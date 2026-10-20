# Catatan Rilis Internal Testing — LexiLadder v1.0.1 (Build 2)

Dokumen ini memuat detail teknis, catatan rilis resmi dwibahasa (*Release Notes*), dan panduan lengkap pengunggahan ke **Google Play Console (Internal Testing)**.

---

## 1. Spesifikasi Berkas Bundel Rilis

| Parameter | Nilai Aktual |
|---|---|
| **Nama Aplikasi** | LexiLadder: English Vocab |
| **Package Name / Application ID** | `com.dearyoti.lexiladder` |
| **Version Name** | `1.0.1` |
| **Version Code** | `2` |
| **Google Play Billing Library** | `8.0.0` *(Memenuhi syarat kebijakan monetisasi terbaru Google Play)* |
| **Tipe Berkas** | Android App Bundle (`.aab`) |
| **Ukuran Berkas** | **14.51 MiB** (15,215,368 bytes) *(Batas Play Console: 150 MB)* |
| **Lokasi Berkas di Proyek** | `app/build/outputs/bundle/release/app-release.aab` |
| **Minimum SDK** | Android 8.0 (API Level 26) |
| **Target SDK / Compile SDK** | Android 16 (API Level 36) |
| **Keystore Alias** | `lexy` |
| **Penandatangan (Signer)** | `CN=dearyoti, OU=dearyoti, O=dearyoti, C=ID` |
| **Masa Berlaku Sertifikat** | Hingga **06 Maret 2054** |
| **SHA-256 Fingerprint Keystore** | `71:EC:E4:AC:E0:01:A1:24:C0:5A:0B:E2:09:D6:B6:7F:3F:C2:CE:B3:D5:34:55:BF:31:AD:B8:2A:7B:91:C8:AF` |
| **SHA-1 Fingerprint Keystore** | `5A:A5:DF:98:F9:79:EB:FC:75:FA:44:AB:09:5D:FF:61:02:7F:7F:56` |

---

## 2. Keterangan Rilis untuk Google Play Console (*Release Notes*)

Salin teks berikut ke kolom **Catatan rilis / Release notes** di Google Play Console:

### 🇮🇩 Bahasa Indonesia (`id-ID`):
```text
Rilis Pembaruan LexiLadder (v1.0.1 Build 2):
- Pembaruan pustaka Google Play Billing Library ke versi 8.0.0 sesuai standar monetisasi terbaru Google Play.
- 370 Kosakata Terstruktur: 150 kata Level 1 (5 Unit) & 200 kata Level 2 (7 Unit) lengkap dengan transkripsi fonetik IPA, definisi CEFR, kalimat contoh, dan terjemahan.
- 100% Offline-First: Belajar dan berlatih kapan saja tanpa koneksi internet.
- Pengucapan Suara Asli: Menggunakan Text-to-Speech (TTS) bawaan Android.
- Sistem Pengulangan Spasi (SRS): Algoritma pintar untuk memperkuat daya ingat jangka panjang.
- Kuis Pilihan Ganda & Ujian Level: Uji pemahaman kosakata dengan umpan balik langsung.
- Pilihan Bahasa Antarmuka & Penjelasan: Dukungan penuh Bahasa Indonesia dan English Immersion.
- Pembaruan Konten Otomatis: Pemeriksaan pembaruan materi harian dari GitHub.
```

### 🇺🇸 Bahasa Inggris (`en-US`):
```text
LexiLadder Update Release (v1.0.1 Build 2):
- Upgraded Google Play Billing Library to version 8.0.0 complying with the latest Google Play monetization policies.
- 370 Curated Vocabulary Words: 150 Level 1 words (5 Units) & 200 Level 2 words (7 Units) with IPA phonetics, CEFR definitions, context sentences, and translations.
- 100% Offline-First: Learn and review anytime without needing an active internet connection.
- Native Pronunciation: Fast, zero-data audio pronunciation powered by Android TTS.
- Spaced Repetition System (SRS): Scientific review scheduling for rock-solid vocabulary retention.
- Adaptive Quizzes & Mastery Tests: Interactive challenges and unit mastery milestones.
- Dual Language Experience: Choose between English Immersion and Indonesian explanations.
- Automatic Daily Content Updates: Daily background checks for new lessons from GitHub.
```
- 370 Curated Vocabulary Words: 150 Level 1 words (5 Units) & 200 Level 2 words (7 Units) with IPA phonetics, CEFR definitions, context sentences, and translations.
- 100% Offline-First: Learn and review anytime without needing an active internet connection.
- Native Pronunciation: Fast, zero-data audio pronunciation powered by Android TTS.
- Spaced Repetition System (SRS): Scientific review scheduling for rock-solid vocabulary retention.
- Adaptive Quizzes & Mastery Tests: Interactive challenges and unit mastery milestones.
- Dual Language Experience: Choose between English Immersion and Indonesian explanations.
- Automatic Daily Content Updates: Daily background checks for new lessons from GitHub.
```

---

## 3. Panduan Langkah Demi Langkah Mengunggah ke Play Console

### Langkah 1: Buka Track Pengujian Internal
1. Masuk ke [Google Play Console](https://play.google.com/console).
2. Pilih aplikasi **LexiLadder: English Vocab** pada dashboard pengembang `dearyoti`.
3. Pada menu navigasi sebelah kiri, gulir ke bagian **Pengujian (Test)** > klik **Pengujian internal (Internal testing)**.

### Langkah 2: Buat Rilis Baru
1. Di pojok kanan atas, klik tombol **Buat rilis baru (Create new release)**.
2. Di bagian **Tanda tangan aplikasi Google Play (Play App Signing)**, izinkan Google mengelola kunci penandatanganan (keystore `lexyladder.jks` yang Anda gunakan bertindak sebagai kunci upload / *upload key*).
3. Di bagian **App Bundle**, klik **Upload** (Unggah).
4. Pilih berkas dari komputer Anda:
   `D:\Android\LexiLadder\app\build\outputs\bundle\release\app-release.aab`
5. Tunggu proses verifikasi hingga muncul nama versi `1.0.0 (1)`.

### Langkah 3: Masukkan Keterangan Rilis
1. Di kolom **Nama rilis (Release name)**, biarkan default `1.0.0 (1)`.
2. Di kolom **Catatan rilis (Release notes)**, tempelkan teks dari **Bagian 2** di atas untuk bahasa Inggris dan bahasa Indonesia.
3. Klik **Simpan (Save)** di bagian bawah halaman.

### Langkah 4: Tambahkan Daftar Penguji (Testers)
1. Pindah ke tab **Penguji (Testers)** pada halaman Pengujian Internal.
2. Buat daftar email penguji (misal: masukkan email Anda `arton.79@gmail.com` dan tester lainnya).
3. Simpan daftar penguji.
4. Salin **Link bergabung penguji (How testers join your test)**:
   - Link web (*Join on the web*)
   - Link aplikasi Google Play (*Join on Android*)
5. Bagikan link tersebut ke penguji internal agar dapat langsung mengunduh aplikasi dari Google Play Store.

### Langkah 5: Tinjau dan Luncurkan
1. Klik **Tinjau rilis (Review release)**.
2. Periksa apakah ada peringatan merah (*Errors*). Jika hanya peringatan informasi (*Warnings* seperti missing mapping file/proguard jika ada), itu normal.
3. Klik **Mulai peluncuran ke Pengujian internal (Start rollout to Internal testing)**.

---

## 4. Checklist Formulir Kebijakan Play Console (Sebelum Publikasi Penuh)

Pastikan checklist berikut telah diisi pada menu **Konten aplikasi (App content)**:
- [x] **Akses Aplikasi:** Semua fitur dapat diakses tanpa login khusus.
- [x] **Iklan:** Pilih "Ya, aplikasi saya memuat iklan" (AdMob terintegrasi untuk versi gratis).
- [x] **Rating Konten:** Isi kuesioner IARC (kategori Pendidikan/Referensi, aman untuk semua umur / 3+).
- [x] **Audiens Target:** Pilih usia 13 tahun ke atas.
- [x] **Keamanan Data (Data Safety):** Aplikasi tidak mengumpulkan data pribadi di luar SDK Google Mobile Ads / Play Billing. Progres belajar sepenuhnya offline di perangkat.
- [x] **Kebijakan Privasi:** Cantumkan URL kebijakan privasi resmi yang sudah disiapkan.
