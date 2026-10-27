# Catatan Rilis Internal Testing — LexiLadder v1.0.4 (Build 5)

Dokumen ini memuat detail teknis, catatan rilis resmi dwibahasa (*Release Notes*), dan panduan lengkap pengunggahan ke **Google Play Console (Internal Testing)**.

---

## 1. Spesifikasi Berkas Bundel Rilis

| Parameter | Nilai Aktual |
|---|---|
| **Nama Aplikasi** | LexiLadder: English Vocab |
| **Package Name / Application ID** | `com.dearyoti.lexiladder` |
| **Version Name** | `1.0.4` |
| **Version Code** | `5` |
| **Google Play Billing Library** | `8.0.0` *(Memenuhi syarat kebijakan monetisasi terbaru Google Play)* |
| **Tipe Berkas** | Android App Bundle (`.aab`) |
| **Ukuran Berkas** | **14.63 MiB** (15,344,537 bytes) *(Batas Play Console: 150 MB)* |
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
Pembaruan Fitur & Konten Baru LexiLadder (v1.0.4 Build 5):
- Peluncuran Paket Ujian TOEFL & IELTS (Exam Booster): Hadir 60 kosakata inti Academic Word List (AWL) terstruktur dalam 3 unit (Analisis & Metodologi, Evaluasi Kritis, serta Inovasi Teknologi & Struktur Sosial).
- Kuis Diagnostik & Simulasi Ujian Akademik: Latihan konteks kalimat, tebak definisi, dan simulasi kelulusan ujian level AWL dengan passing grade 80%.
- Opsi Pembelian Baru: Dukungan produk in-app purchase "Exam Booster Pack" dan "All-Access Lifetime (PRO)" untuk membuka semua fitur dan bebas iklan selamanya.
- Tampilan Baru Halaman Premium: Antarmuka kartu bertingkat yang lebih modern dengan status kepemilikan lencana "Unlocked & Active".
- Peningkatan Sinkronisasi Manifest Konten: Manifest konten versi 3 kini terintegrasi penuh secara offline.
- 100% Offline-First: Belajar, latihan, dan simulasi ujian tanpa memerlukan koneksi internet.
```

### 🇺🇸 Bahasa Inggris (`en-US`):
```text
LexiLadder New Features & Content Update (v1.0.4 Build 5):
- TOEFL & IELTS Exam Booster Pack: Added 60 essential Academic Word List (AWL) core words across 3 academic units (Research & Methodology, Critical Evaluation, and Technological & Societal Structure).
- Diagnostic Quizzes & Academic Exam Simulation: Context sentences, definition matching, and automated timed mastery test simulations with an 80% passing grade.
- New Purchase Options: Added "Exam Booster Pack" and "All-Access Lifetime (PRO)" in-app products to unlock all modules and remove all ads permanently.
- Modernized Premium Screen: Redesigned multi-tier product cards with clear ownership badges ("Unlocked & Active").
- Content Manifest v3 Sync: Bundled offline content packages updated with verified SHA-256 integrity.
- 100% Offline-First: Learn, practice, and test vocabulary anytime without an internet connection.
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
