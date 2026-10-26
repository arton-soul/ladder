# Catatan Rilis Internal Testing — LexiLadder v1.0.3 (Build 4)

Dokumen ini memuat detail teknis, catatan rilis resmi dwibahasa (*Release Notes*), dan panduan lengkap pengunggahan ke **Google Play Console (Internal Testing)**.

---

## 1. Spesifikasi Berkas Bundel Rilis

| Parameter | Nilai Aktual |
|---|---|
| **Nama Aplikasi** | LexiLadder: English Vocab |
| **Package Name / Application ID** | `com.dearyoti.lexiladder` |
| **Version Name** | `1.0.3` |
| **Version Code** | `4` |
| **Google Play Billing Library** | `8.0.0` *(Memenuhi syarat kebijakan monetisasi terbaru Google Play)* |
| **Tipe Berkas** | Android App Bundle (`.aab`) |
| **Ukuran Berkas** | **14.62 MiB** (15,328,949 bytes) *(Batas Play Console: 150 MB)* |
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
Pembaruan Fitur & Perbaikan Stabilitas LexiLadder (v1.0.3 Build 4):
- Perbaikan Mode Latihan "Perlu Diulang": Evaluasi penilaian kuis kini 100% akurat, stabil, dan bebas dari anomali pembatalan atau regenerasi soal mendadak saat menjawab kuis.
- Penyesuaian Syarat Ujian Tingkat Level: Akses ujian sertifikasi kini terbuka lebih cepat dan alami setelah mempelajari minimal 80% materi kosakata di level bersangkutan (120 kata di Level 1, 160 kata di Level 2, 200 kata di Level 3).
- Perbaikan UI Mode Kartu Kilat (Flashcard): Tombol navigasi kartu selanjutnya kini menampilkan ikon panah kanan (ArrowForward) dengan tepat dan konsisten.
- Peningkatan Algoritma Pilihan Jawaban Kuis: Sanitasi opsi pengecoh untuk menjamin semua pilihan jawaban unik tanpa duplikasi.
- Pembaruan Notifikasi Milestone: Peringatan kelayakan ujian disesuaikan dengan pencapaian 80% dan 100% materi.
- 100% Offline-First: Belajar, latihan, dan ujian kosakata kapan saja tanpa kuota internet.
```

### 🇺🇸 Bahasa Inggris (`en-US`):
```text
LexiLadder Feature Update & Bug Fixes (v1.0.3 Build 4):
- Fixed Practice "Due Review" Mode: Quiz answer evaluations are now 100% accurate, stable, and immune to mid-session question reshuffling or unexpected resets.
- Optimized Level Exam Prerequisites: Level certification exams now unlock naturally upon completing at least 80% of lesson material in that level (120 words for Level 1, 160 words for Level 2, 200 words for Level 3).
- Fixed Flashcard Navigation UI: The next-card button now correctly displays the forward arrow icon.
- Enhanced Quiz Distractor Algorithm: Stricter distractor sanitization to guarantee 4 distinct options per question.
- Smart Milestone Alerts Updated: In-app celebrations and notifications now trigger accurately at 80% and 100% study milestones.
- 100% Offline-First: Learn, practice, and take exams anytime without an internet connection.
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
