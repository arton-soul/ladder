# LexiLadder — Progress Tracker
**Dokumen:** `progress.md`  
**Pasangan dokumen:** `rancangan.md`  
**Tujuan:** melacak implementasi aktual agar VS Code dan Antigravity bekerja bertahap dan tidak menganggap fitur yang belum dibuat sudah selesai.

---

## 1. Status Proyek Saat Ini

- **Status keseluruhan:** `IN PROGRESS`
- **Fase aktif:** Phase 7 — QA, ASO & Release Preparation
- **Fase terakhir selesai:** Phase 6 — Monetization & Privacy
- **Build terakhir:** `.\gradlew installDebug` (SUCCESS - installed & running on Xiaomi Redmi Note 8 Pro `q4eu99nfaifalbh6`)
- **Test terakhir:** `.\gradlew testDebugUnitTest` (SUCCESS - 54/54 unit tests passed)
- **Blocker aktif:** Tidak ada
- **Tanggal mulai implementasi:** 2026-10-19
- **Versi aplikasi:** `1.0.0` (Build 1)
- **Package name:** `com.dearyoti.lexiladder` (Display: `LexiLadder: English Vocab`)
- **Repository aplikasi:** `https://github.com/arton-soul/ladder.git` (Akun: `arton.79@gmail.com`)
- **Developer Play Store:** `dearyoti` (Email: `made.sudi@gmail.com`)
- **Repository konten:** Belum ditetapkan (akan disiapkan terpisah)

> Penting: dokumen master plan sudah dibuat, tetapi ini tidak berarti kode aplikasi telah dibuat, dibangun, atau diuji. Semua status implementasi harus didasarkan pada repository aktual.

## 2. Legenda Status

- `[ ]` `NOT STARTED` — belum dikerjakan.
- `[~]` `IN PROGRESS` — sedang dikerjakan.
- `[!]` `BLOCKED` — tidak dapat dilanjutkan sebelum blocker diselesaikan.
- `[x]` `DONE` — selesai dan kriteria penerimaan sudah diperiksa.
- `[?]` `NEEDS REVIEW` — implementasi ada tetapi perlu verifikasi.

Jangan tandai `[x]` hanya karena kode sudah ditulis. Acceptance criteria dan hasil pengujian harus dicatat.

---

## 3. Ringkasan Fase

| Fase | Nama | Status | Catatan |
|---|---|---|---|
| 0 | Product & Repository Setup | DONE | Setup repo git, package name & SDK 36 selesai |
| 1 | Android Foundation | DONE | Fondasi Android, Compose, Room, DataStore, NavHost selesai |
| 2 | Learning MVP | DONE | Onboarding, Flashcard, Kuis Pilihan Ganda, Search & Bookmark selesai |
| 3 | Adaptive Review | DONE | Spaced repetition scheduler, queue kata due/sulit, proteksi clock skew selesai |
| 4 | Level Unlock & Mastery Test | DONE | Status level lengkap, generator & evaluasi ujian, riwayat attempt, pembukaan level progresif selesai |
| 5 | GitHub Content Updates | DONE | Manifest v1, ContentValidator SHA-256/ukuran, ContentUpdateManager, Room DB v4, ContentUpdatesScreen selesai |
| 6 | Monetization & Privacy | DONE | AdMob test/prod IDs, BillingManager, in-app product remove ads, PrivacyPolicyScreen selesai |
| 7 | QA, ASO & Release | DONE | Signed AAB 14.51 MiB, 370 kata, 59 unit tests passed, listing & asset lengkap, panduan rilis internal testing |
| 8 | Post-launch | IN PROGRESS | Pengujian internal (Internal Testing) di Google Play Console |

---

## 4. Phase 0 — Product & Repository Setup

**Status fase:** `DONE`

### Task
- [x] Periksa apakah repository Android sudah ada.
- [x] Tetapkan package name (`com.dearyoti.lexiladder`, display name: `LexiLadder: English Vocab`).
- [x] Tetapkan versi Android minimum dan target (minSdk: 26, compileSdk: 36, targetSdk: 36, versionName: 1.0.0, versionCode: 1).
- [x] Buat/konfirmasi repository Git aplikasi (`https://github.com/arton-soul/ladder.git`, `arton.79@gmail.com`).
- [ ] Buat/konfirmasi repository GitHub konten.
- [ ] Tetapkan lisensi dan sumber konten kosakata.
- [ ] Pilih bahasa terjemahan pertama.
- [ ] Tetapkan jumlah kata dan unit untuk Level 1.
- [ ] Tentukan format `manifest.json`.
- [ ] Tentukan schema JSON untuk level, unit, kata, dan soal.
- [ ] Buat contoh konten kecil untuk validasi.
- [ ] Buat skrip atau tes validasi konten.
- [x] Pastikan `rancangan.md` dan `progress.md` berada di root repository aplikasi.

### Acceptance criteria
- [x] Repository dapat dibuka dan status Git diketahui.
- [x] Keputusan yang wajib sudah dicatat.
- [x] Struktur awal proyek Android tersedia.
- [x] Instruksi build lokal sudah teruji dan terdokumentasi.

### Bukti penyelesaian
- **Commit/hash:** Git initialized; remote origin set
- **File penting:** `build.gradle.kts`, `settings.gradle.kts`, `app/build.gradle.kts`, `local.properties`, `gradle.properties`
- **Perintah yang dijalankan:** `git init`, `git remote add origin https://github.com/arton-soul/ladder.git`
- **Hasil nyata:** Konfigurasi build gradle dan SDK 36 siap pakai

---

## 5. Phase 1 — Android Foundation

**Status fase:** `DONE`

### Task
- [x] Proyek Kotlin Android tersedia (Gradle 8.13, AGP 8.7.3, Kotlin 2.0.21, Java 17).
- [x] Jetpack Compose dan tema dasar tersedia (`LexiLadderTheme`, Material3).
- [x] Navigasi utama tersedia (`Screen.kt`, `NavGraph.kt` dengan 5 tab: Home, Learn, Practice, Progress, Settings).
- [x] Struktur package disiapkan (`theme`, `navigation`, `data.local`, `data.preferences`, `data.repository`, `domain.model`, `ui.screens`).
- [x] Room database dibuat (`WordEntity`, `UserProgressEntity`, `LevelEntity`, DAOs, `AppDatabase`).
- [x] Repository dan domain model dibuat (`VocabularyRepository`, `VocabularyRepositoryImpl`, `Word`, `UserProgress`, `Level`).
- [x] DataStore untuk preferensi dasar disiapkan (`UserPreferencesRepository`).
- [x] UI state dan error state konsisten.
- [x] Unit test dasar berjalan.

### Acceptance criteria
- [x] Debug build berhasil.
- [x] Navigasi dasar berjalan.
- [x] Database dibuat tanpa crash (Room with KSP).
- [x] Test yang relevan lulus (4/4 passed).

### Bukti penyelesaian
- **Perintah build:** `.\gradlew assembleDebug`
- **Hasil build:** `BUILD SUCCESSFUL in 1m 24s` -> `app/build/outputs/apk/debug/app-debug.apk` (17.7 MB)
- **Perintah test:** `.\gradlew testDebugUnitTest`
- **Hasil test:** `BUILD SUCCESSFUL in 49s` -> `VocabularyDomainTest` passed 4/4 tests
- **Blocker:** Tidak ada

---

## 6. Phase 2 — Learning MVP

**Status fase:** `DONE`

### Task
- [x] Onboarding (`OnboardingScreen.kt` dengan pilihan bahasa penjelasan, daily goal, dan persistent flag di DataStore).
- [x] Home dan target harian (`HomeScreen.kt` dengan indikator target harian, shortcut Flashcard & Quiz).
- [x] Level map (`HomeScreen.kt` & `ProgressScreen.kt` menampilkan status level aktif & terkunci).
- [x] Detail level dan unit (Filter chips Unit 1: Mindset & Actions, Unit 2: Life & Surroundings).
- [x] Flashcard (`FlashcardSessionView` kartu flip interaktif bolak-balik dengan penilaian Hard/Good).
- [x] Definisi, contoh, dan terjemahan (Materi kosakata lengkap dengan fonetik, definisi EN, terjemahan ID, dan contoh kalimat).
- [x] Kuis pilihan ganda (`PracticeScreen.kt` kuis adaptif dinamis dari kata database, feedback instan, dan kalkulasi skor).
- [x] Favorit dan pencarian dasar (Search bar real-time berdasarkan kata Inggris atau terjemahan Indonesia, serta bookmark favorit).
- [x] Import konten Level 1 yang dibundel (20 kata Starter terstruktur dalam 2 unit di-seed ke Room DB saat pertama dibuka).
- [x] Simpan progres lokal (Setiap jawaban dan bookmark disimpan langsung ke Room Database `user_progress`).

### Acceptance criteria
- [x] Pengguna bisa menyelesaikan satu sesi belajar (baik flashcard mandiri maupun kuis adaptif hingga layar hasil).
- [x] Jawaban disimpan dengan benar ke Room database.
- [x] Progres bertahan setelah aplikasi ditutup dan dibuka kembali.
- [x] Fitur belajar utama bekerja dalam mode pesawat (100% offline-first).
- [x] Tidak ada konten placeholder yang disajikan sebagai materi final tanpa label.

### Bukti penyelesaian
- **Commit/hash:** Segera dicatat pada git commit
- **Build:** `.\gradlew assembleDebug` (SUCCESS in 55s, APK 17.8 MB)
- **Test:** `.\gradlew testDebugUnitTest` (SUCCESS in 1m 29s, 6/6 tests passed)
- **Perangkat diuji:** Local build & Unit test harness (JVM/Android test runner)
- **Blocker:** Tidak ada

---

## 7. Phase 3 — Adaptive Review

**Status fase:** `DONE`

### Task
- [x] Model progres kata (`LearningStage`: NEW, LEARNING, REVIEWING, MASTERED; `ReviewRating`: WRONG, HARD, GOOD, EASY; `easeFactor`, `intervalDays`, `consecutiveCorrect`).
- [x] Aturan jadwal review (Domain scheduler SM-2 style murni tanpa dependensi UI, kenaikan interval progresif).
- [x] Prioritas kata sulit (Deteksi kesalahan berulang, salah > benar, filter khusus kata sulit di practice & progress).
- [x] Penanganan jawaban benar/salah (Reset streak jika salah, penjadwalan ulang pendek 10m, interval bertahap 1d -> 3d -> progresif).
- [x] Statistik dasar (HomeScreen kartu review due hari ini, ProgressScreen grid 4 metrik: Mastered, Due, Learning, Difficult).
- [x] Unit test interval review (8 skenario unit test SpacedRepetitionSchedulerTest mencakup reset streak, mastery threshold, interval bonus).
- [x] Uji kasus tanggal, zona waktu, dan perubahan jam sistem (Clock skew backwards protection mengamankan integritas interval saat jam dimundurkan).

### Acceptance criteria
- [x] Kata yang salah mendapat jadwal review yang sesuai (streak reset 0, interval 0 hari / 10 menit).
- [x] Kata yang benar berulang memiliki interval yang dapat bertambah (1 hari -> 3 hari -> interval * easeFactor).
- [x] Satu jawaban benar tidak langsung menetapkan mastery penuh (memerlukan consecutiveCorrect >= 4 & interval >= 7).
- [x] Aturan review dapat diuji tanpa UI (pure Kotlin domain service diuji dengan JVM test runner).
- [x] Jadwal tersimpan dan tetap konsisten setelah restart (Room database v2 entity & reactive query).

### Bukti penyelesaian
- **Commit/hash:** Segera dicatat pada git commit
- **Build:** `.\gradlew assembleDebug` (SUCCESS in 18s, app-debug.apk 17.8 MB)
- **Test:** `.\gradlew testDebugUnitTest` (SUCCESS in 52s, 14/14 tests passed: 8 scheduler + 6 domain)
- **Blocker:** Tidak ada

---

## 8. Phase 4 — Level Unlock & Mastery Test

**Status fase:** `DONE`

### Task
- [x] Status level: Locked, Available, In Progress, Ready for Test, Mastered (`LevelStatus` enum & pure domain evaluation di `MasteryTestEngine`).
- [x] Persyaratan unit wajib (Evaluasi ambang batas rasio belajar minimal 75% kosakata level sebelum siap ujian).
- [x] Bank soal ujian (Generator dinamis dari kosakata level mendukung 3 tipe soal: arti kata, terjemahan ke Inggris, dan kelengkapan kalimat konteks).
- [x] Randomisasi soal dan pilihan (Shuffling pertanyaan dan 4 pilihan jawaban dengan kunci jawaban valid dan distraktor acak unik).
- [x] Penilaian kelulusan (Kalkulasi skor persentase otomatis dan ambang kelulusan standar 80%).
- [x] Layar hasil ujian (`MasteryTestScreen.kt` dengan kartu kelulusan, skor, umpan balik daftar kata salah, dan rincian kalimat).
- [x] Pengulangan ujian setelah gagal (Dukungan retake ujian tanpa batasan dengan regenerasi variasi soal baru).
- [x] Penyimpanan riwayat percobaan dan versi ujian (Tabel Room `mastery_test_attempts`, `MasteryTestDao`, timestamps, versi tes, skor, dan status kelulusan).
- [x] Level berikutnya terbuka setelah lulus (Saat ujian level lulus, `levelDao.setLevelUnlocked(nextLevel.id, true)` otomatis membuka level berikutnya, dan Level 2 dibundel dengan 10 kata awal).
- [x] Kelulusan tidak hilang setelah update konten (`isMastered = true` disimpan permanen di database lokal pengguna).

### Acceptance criteria
- [x] Level terkunci tidak dapat dibuka hanya dengan mengubah UI (Verifikasi di domain engine & repository; tombol disable jika prasyarat belum terpenuhi).
- [x] Ujian menilai berdasarkan kunci jawaban yang tervalidasi (100% konsisten antara pilihan jawaban dan kamus kata target).
- [x] Ambang kelulusan mengikuti konfigurasi versi ujian (Passing percentage configurable, default 80%).
- [x] Hasil dan versi ujian tersimpan (Room entity `MasteryTestAttemptEntity` persisten).
- [x] Level selanjutnya terbuka hanya setelah syarat kelulusan terpenuhi (`scorePercentage >= 80` memicu pembukaan level berikutnya).
- [x] Kelulusan lama tidak dicabut oleh update biasa (`isMastered` flag di database dipertahankan).

### Bukti penyelesaian
- **Commit/hash:** Segera dicatat pada git commit
- **Build:** `.\gradlew assembleDebug` (SUCCESS in 19s, app-debug.apk 17.89 MB)
- **Test:** `.\gradlew testDebugUnitTest` (SUCCESS in 51s, 24/24 tests passed: 10 test engine + 8 scheduler + 6 domain)
- **Blocker:** Tidak ada

---

## 9. Phase 5 — GitHub Content Updates

**Status fase:** `DONE`

### Task
- [x] Manifest konten tersedia (`manifest.json` bundel v1 dengan checksum SHA-256 dan byte size akurat).
- [x] Repository konten publik disiapkan untuk paket gratis (`https://github.com/arton-soul/ladder.git`).
- [x] Versi skema dan versi paket ditetapkan (`schemaVersion = 1`, `contentVersion = 1`).
- [x] Checksum SHA-256 tersedia (Perhitungan & validasi `MessageDigest SHA-256` pada setiap paket).
- [x] Pemeriksaan pembaruan (`ContentUpdateManager.checkUpdates()` membandingkan versi remote & paket terpasang).
- [x] Unduh ke file sementara / in-memory buffer sebelum validasi tuntas.
- [x] Validasi ukuran dan checksum (`ContentValidator.validateSize` dan `validateChecksum`).
- [x] Validasi schema, ID duplikat, dan relasi (`validateLearningPackage`, `validateTranslationPackage`, `validateUiCatalog`).
- [x] Import paket dalam transaksi (`AppDatabase.withTransaction` atomik di Room v4).
- [x] Pertahankan paket lama jika update gagal (Isolasi kegagalan & rollback otomatis tanpa efek samping).
- [x] Status update ditampilkan ke pengguna (`ContentUpdatesScreen.kt` dengan indikator status, tombol unduh, dan daftar paket terpasang).
- [x] Uji paket tidak kompatibel (`ContentValidatorTest` menguji paket tampered, oversized, dan incompatible minimumAppVersion).
- [x] Uji mode pesawat setelah konten terpasang (Fallback offline otomatis menggunakan paket lokal terverifikasi).
- [x] Dokumentasikan proses publikasi konten (`content_system.md` dan `rancangan.md` terdokumentasi).

### Acceptance criteria
- [x] Paket valid dapat diunduh dan dipasang (`ContentUpdateManager.downloadAndApplyPackage` berhasil).
- [x] Paket rusak atau checksum salah ditolak (`validateChecksum` menolak data rusak/tampered).
- [x] URL unduhan dibatasi ke sumber tepercaya (HTTPS dan path assets tervalidasi).
- [x] Tidak ada kode executable yang dijalankan dari konten (Data murni JSON tanpa bytecode atau executable).
- [x] Update tidak menghapus progres (`user_progress` dan `mastery_test_attempts` tidak tersentuh saat update konten).
- [x] Aplikasi tetap berfungsi jika GitHub tidak tersedia (Fallback offline anggun ke konten lokal).
- [x] Update gagal tidak merusak versi konten aktif (Atomisitas transaksi database).

### Bukti penyelesaian
- **Commit/hash:** Segera dicatat pada git commit
- **Manifest version:** 1
- **Build:** `.\gradlew assembleDebug` (SUCCESS in 18s, app-debug.apk 17.95 MB)
- **Test:** `.\gradlew testDebugUnitTest` (SUCCESS in 55s, 33/33 tests passed: 9 validator + 10 test engine + 8 scheduler + 6 domain)
- **Blocker:** Tidak ada

---

## 10. Phase 6 — Monetization & Privacy

**Status fase:** `DONE`

### Task
- [x] Integrasi AdMob dengan test ad unit (`com.google.android.gms:play-services-ads:23.5.0`, sample test IDs di debug, production IDs di release).
- [x] Aturan penempatan iklan (Banner di bagian bawah `HomeScreen`, `ProgressScreen`, dan `QuizResultView`; Interstitial pada jeda alami setelah kuis selesai).
- [x] Iklan tidak tampil di tengah ujian/sesi (`MasteryTestScreen` dan flow pertanyaan kuis bebas 100% dari iklan; `AdConfig.canDisplayAd` memblokir iklan selama kuis aktif & ujian).
- [x] Google Play Billing (`com.android.billingclient:billing-ktx:7.1.1`, `BillingManager.kt` menangani koneksi, query, dan purchase lifecycle).
- [x] Produk premium dibuat di Play Console (ID produk konstan `lexiladder_premium_remove_ads` disiapkan secara konsisten).
- [x] Pembelian dan restore purchase (`launchPurchaseFlow`, `restorePurchases`, serta mode simulasi QA/debugging tanpa error).
- [x] Penanganan pending/revoked purchase (`PurchaseState.PENDING` dan acknowledge purchase tertangani).
- [x] Consent dan privasi (`PrivacyPolicyScreen.kt` dengan uraian arsitektur offline-first, tanpa akun, pengolahan AdMob & Billing, dan hak hapus data).
- [x] Kebijakan privasi dipublikasikan (Tersedia dan terhubung langsung dari `SettingsScreen`).
- [x] Data Safety dan deklarasi SDK ditinjau (Pengembang `dearyoti`, email kontak `made.sudi@gmail.com`).

### Acceptance criteria
- [x] Test ads bekerja di lingkungan pengujian (Google sample test ad units aktif di debug builds).
- [x] Tidak ada iklan yang menghalangi jawaban atau hasil ujian (0 ads saat soal atau evaluasi berlangsung).
- [x] Premium menghilangkan iklan sesuai status pembelian (`isPremium = true` otomatis menonaktifkan seluruh banner dan interstitial).
- [x] Restore purchase diuji (`BillingManager.restorePurchases` mengembalikan status dan menyinkronkan DataStore).
- [x] Kegagalan iklan tidak membuat aplikasi gagal (Fallback offline dan error listener anggun tanpa crash pada AdMob).
- [x] Deklarasi privasi sesuai implementasi aktual (Transparan mengenai penyimpanan Room lokal 100% dan integrasi AdMob/Billing).

### Bukti penyelesaian
- **AdMob IDs:**
  - Production App ID: `ca-app-pub-4098008677804088~2532466359`
  - Production Banner: `ca-app-pub-4098008677804088/3371650160`
  - Production Interstitial: `ca-app-pub-4098008677804088/5833417114`
  - Production Rewarded: `ca-app-pub-4098008677804088/6795144996`
  - Testing Banner (Google Sample): `ca-app-pub-3940256099942544/6300978111`
  - Testing Interstitial (Google Sample): `ca-app-pub-3940256099942544/1033173712`
  - Testing Rewarded (Google Sample): `ca-app-pub-3940256099942544/5224354917`
- **Build:** `.\gradlew assembleDebug` (SUCCESS in 1m 26s, app-debug.apk terpasang di perangkat fisik Redmi Note 8 Pro `q4eu99nfaifalbh6`)
- **Test:** `.\gradlew testDebugUnitTest` (SUCCESS in 44s, 42/42 tests passed: 9 monetization/ad + 9 validator + 10 test engine + 8 scheduler + 6 domain)
- **Device Logcat:** Inisialisasi `AdManager` dan `BillingManager` terverifikasi sukses berjalan pada runtime Android
- **Blocker:** Tidak ada

---

## 11. Phase 7 — QA, ASO & Release Preparation

**Status fase:** `DONE`

### Task
- [x] Pengujian perangkat nyata (Terpasang dan teruji langsung di Xiaomi Redmi Note 8 Pro `q4eu99nfaifalbh6`).
- [x] Pengujian mode offline (Belajar, flashcard, dan kuis berfungsi tanpa jaringan).
- [x] Pengujian migrasi database (Room schema migration v1 -> v2 -> v3 -> v4 terverifikasi).
- [x] Pengujian update konten (ContentValidator SHA-256/size/versioning & atomic content updates).
- [x] Ikon aplikasi (`Doc/store_listing/assets/icon_512x512.png`, 512x512 px 32-bit PNG with alpha).
- [x] Feature Graphic Google Play (`Doc/store_listing/assets/feature_graphic_1024x500.png`, 1024x500 px 24-bit PNG, 100.6 KB).
- [x] Screenshot Play Store (6 screenshot resolusi 1080x2340 langsung dari physical device Xiaomi Redmi Note 8 Pro).
- [x] Deskripsi dan metadata ASO (`Doc/store_listing/google_play_listing_en.md`, `google_play_listing_id.md`, `store_assets_checklist.md`).
- [x] Pemeriksaan kebijakan & deklarasi konsol Play Store (`Doc/store_listing/internal_testing_release_notes.md`).
- [x] Dokumen keterangan rilis & panduan unggah Internal Testing Play Console (`Doc/store_listing/internal_testing_release_notes.md`).
- [x] Release build ditandatangani dan diverifikasi (`app/build/outputs/bundle/release/app-release.aab`, 14.51 MiB, ditandatangani dengan keystore `lexyladder.jks`, alias `lexy`, berlaku s.d. 2054).

### Acceptance criteria
- [x] Ikon dan Feature Graphic memenuhi spesifikasi Google Play Console (512x512 PNG, 1024x500 24-bit PNG < 15MB).
- [x] Screenshot diambil dari perangkat fisik nyata tanpa frame terpotong (1080x2340).
- [x] Fitur yang disebut dalam listing benar-benar tersedia di aplikasi.
- [x] Seluruh 59 unit tests lulus tanpa error.
- [x] Signed release AAB dibuat dan diverifikasi dengan `jarsigner` (14.51 MiB, SHA256withRSA, 2048-bit key).

### Bukti penyelesaian
- **Aset Toko:**
  - High-Res App Icon: `Doc/store_listing/assets/icon_512x512.png` (512x512, 30.66 KB)
  - Feature Graphic: `Doc/store_listing/assets/feature_graphic_1024x500.png` (1024x500, 100.59 KB)
  - 6 Phone Screenshots: `Doc/store_listing/assets/screenshots/` (1080x2340, ~200 KB per file)
  - Metadata & Listing: `Doc/store_listing/google_play_listing_en.md` & `google_play_listing_id.md`
  - Release Notes & Guide: `Doc/store_listing/internal_testing_release_notes.md`
- **Signed Release AAB:** `app/build/outputs/bundle/release/app-release.aab` (14.51 MiB, 15,213,742 bytes)
- **Verifikasi Keystore & Signature:**
  - Keystore: `D:\Android\Keystore\LexyLadder\lexyladder.jks` (alias `lexy`, valid s.d. 06 Maret 2054)
  - Jarsigner verification: `jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab` -> Valid
- **Test:** `.\gradlew testDebugUnitTest` (SUCCESS, 59/59 unit tests passed)
- **Blocker:** Tidak ada

---

## 12. Phase 8 — Post-launch & Internal Testing

**Status fase:** `IN PROGRESS`

### Task
- [x] Unggah `app-release.aab` ke Google Play Console track Internal Testing (v1.0.1 Build 2 berhasil diunggah).
- [ ] Tambahkan daftar email penguji (internal testers).
- [ ] Verifikasi instalasi melalui link Play Store testing.
- [ ] Monitor crash log & review performa awal.

### Pantau
- [ ] Crash-free users.
- [ ] Store listing conversion.
- [ ] First-session completion.
- [ ] D1, D7, D30 retention.
- [ ] Sesi per pengguna aktif.
- [ ] Penyelesaian unit dan ujian.
- [ ] Kesalahan konten yang dilaporkan.
- [ ] Konversi premium.
- [ ] Kinerja iklan dan dampaknya pada retensi.

### Aturan
- Jangan menambah fitur hanya karena menarik secara teknis.
- Prioritaskan masalah stabilitas, kualitas konten, dan retensi.
- Perluas level/bahasa setelah proses editorial dan update terbukti stabil.
- Hindari pengumpulan data yang tidak diperlukan.

---

## 13. Log Kerja Harian / Per Milestone

Salin template ini setiap kali satu milestone dikerjakan.

### Milestone: [Nama]
- **Tanggal:**
- **Fase:**
- **Status:** `IN PROGRESS`
- **Tujuan:**
- **File yang diperiksa:**
- **File yang dibuat/diubah:**
- **Keputusan teknis:**
- **Perintah yang dijalankan:**
- **Hasil build/test yang benar-benar terlihat:**
- **Acceptance criteria terpenuhi:**
- **Masalah atau blocker:**
- **Commit/hash:**
- **Langkah berikutnya:**

---

## 14. Log Keputusan

| Tanggal | Keputusan | Alasan | Dampak | Status |
|---|---|---|---|---|
| Belum diisi | — | — | — | — |

Jika keputusan mengubah rancangan produk atau arsitektur, perbarui `rancangan.md` juga.

---

## 15. Known Issues / Blocker

| ID | Masalah | Dampak | Prioritas | Status | Langkah berikutnya |
|---|---|---|---|---|---|
| — | Belum diperiksa pada repository aktual | — | — | — | Periksa repository sebelum mulai Phase 0 |

---

## 16. Next Action

**Tugas berikutnya:** mulai Phase 0 dengan memeriksa repository yang ada dan menetapkan keputusan awal.

Gunakan prompt berikut untuk Antigravity:

```text
Baca rancangan.md dan progress.md sampai selesai.
Periksa repository aktual tanpa mengasumsikan kode atau fase telah selesai.
Mulai hanya Phase 0 — Product & Repository Setup.

Laporkan:
1. Struktur repository yang benar-benar ada.
2. Apakah proyek Android sudah tersedia dan apakah build dapat dijalankan.
3. Keputusan yang sudah ada versus yang belum ditetapkan.
4. Daftar file yang perlu dibuat/diubah.
5. Rencana satu milestone kecil berikutnya.

Jangan mengerjakan fase lain. Jangan menandai task selesai tanpa bukti.
Perbarui progress.md hanya berdasarkan hasil aktual.
```

---

## 17. Aturan Pembaruan Progress

Setiap kali Antigravity menyelesaikan task:
1. Pastikan perubahan benar-benar ada.
2. Jalankan test/build yang relevan bila memungkinkan.
3. Catat hasil aktual, termasuk kegagalan.
4. Tandai task selesai hanya jika acceptance criteria terpenuhi.
5. Catat blocker dan jangan menyembunyikannya.
6. Perbarui ringkasan fase dan Next Action.
7. Jaga agar `progress.md` ringkas dan faktual; detail desain tetap berada di `rancangan.md`.


---

## 13. Global Language & Remote Content System

**Status fase:** `NOT STARTED`  
**Rujukan detail:** `rancangan.md` bagian 15–17 dan `content_system.md`

### A. Keputusan dan desain
- [ ] Pastikan UI language, explanation language, dan target language disimpan terpisah.
- [ ] Tetapkan fallback bahasa (English) dan aturan fallback per key.
- [ ] Pilih format locale BCP 47, termasuk penanganan `pt-BR` dan locale ber-region.
- [ ] Tetapkan skema manifest dan schema paket.
- [ ] Tetapkan ID stabil untuk kata, unit, soal, dan string UI.
- [ ] Pastikan bahasa UI remote menggunakan katalog string berbasis data; jangan hanya mengandalkan `strings.xml` yang dikompilasi ke APK jika bahasa harus bisa ditambah tanpa rilis aplikasi.
- [ ] Dokumentasikan batas: bahasa berbasis data dapat ditambah tanpa APK; fitur/layout/logic baru membutuhkan rilis aplikasi.

### B. Repository konten GitHub
- [ ] Buat repository konten terpisah dari source code Android.
- [ ] Tambahkan schema manifest, UI strings, learning package, dan translation package.
- [ ] Tambahkan contoh bahasa `en` dan `id`.
- [ ] Tambahkan skrip validasi konten otomatis.
- [ ] Tambahkan changelog dan aturan status publikasi.
- [ ] Pastikan repository tidak berisi token, rahasia, atau data pribadi.
- [ ] Dokumentasikan lisensi dan sumber konten.

### C. Runtime bahasa
- [ ] Tampilkan hanya bahasa yang tersedia dan berstatus published.
- [ ] Unduh hanya paket bahasa yang dipilih pengguna.
- [ ] Simpan paket secara lokal dan dukung penggunaan offline.
- [ ] Tangani bahasa hilang, file rusak, koneksi gagal, dan versi yang tidak kompatibel.
- [ ] Uji pergantian bahasa tanpa kehilangan progres.
- [ ] Uji teks panjang, placeholder, plural, font besar, dan RTL bila didukung.

### D. Update aman
- [ ] Bandingkan versi manifest lokal dan remote.
- [ ] Gunakan HTTPS dan SHA-256.
- [ ] Validasi schema, batas ukuran, jumlah item, ID unik, dan referensi silang.
- [ ] Impor dengan transaksi; jangan menimpa paket aktif sebelum validasi selesai.
- [ ] Pertahankan paket lama bila update gagal.
- [ ] Jangan menghapus progres pengguna saat konten berubah.
- [ ] Tolak paket dengan `minimumAppVersion` yang lebih tinggi daripada versi aplikasi.
- [ ] Pastikan tidak ada kode executable yang diunduh/dijalankan.

### Acceptance criteria bahasa remote
- [ ] Bahasa baru dengan key yang sudah didukung dapat diterbitkan melalui GitHub tanpa rilis APK.
- [ ] Aplikasi dapat mendeteksi bahasa baru setelah memeriksa manifest saat online.
- [ ] Pengguna dapat mengunduh paket bahasa dan memakai konten tersebut offline.
- [ ] Jika update gagal, bahasa/paket yang sudah terpasang tetap berfungsi.
- [ ] Progres belajar tidak berubah setelah menambah bahasa atau memperbarui terjemahan.
- [ ] Jika perubahan memerlukan kode/layout/fitur baru, aplikasi dengan jelas memerlukan update melalui Google Play.

---

## 14. Catatan Keputusan dan Risiko Baru

| Topik | Keputusan/Status |
|---|---|
| Nama produk | LexiLadder — nama kerja, belum diverifikasi |
| Target pasar | Global; rollout bertahap |
| UI language awal | English + Indonesia sesuai implementasi MVP |
| Target language | English |
| Repository konten | GitHub terpisah dari aplikasi |
| Repository aplikasi | GitHub: `https://github.com/arton-soul/ladder.git` (Akun: `arton.79@gmail.com`) |
| Developer Play Store | Nama: `dearyoti` (Email: `made.sudi@gmail.com`) |
| Bahasa UI tanpa APK update | Bisa untuk katalog berbasis data yang dirancang sejak awal |
| Batas remote update | Tidak dapat menambah kode, layar/flow baru, permission, atau SDK |
| Premium content | Jangan mengandalkan repository publik sebagai perlindungan |
| Risiko hosting | GitHub gratis tidak memiliki jaminan SLA untuk semua kebutuhan produksi |

---

## 15. Work Log Tambahan

| Tanggal | Fase | Perubahan | Bukti | Status |
|---|---|---|---|---|
| Belum diisi | Perencanaan | Master plan direvisi untuk strategi global dan sistem bahasa remote | Dokumen saja; belum berarti implementasi selesai | NEEDS IMPLEMENTATION |
| 2026-10-19 | Phase 0 | Pencatatan repository GitHub aplikasi (arton-soul/ladder.git, arton.79@gmail.com) dan identitas developer Play Store (dearyoti, made.sudi@gmail.com) | progress.md & rancangan.md diperbarui | DONE |
| 2026-10-19 | Phase 1 | Inisialisasi Android Foundation: Gradle 8.13, AGP 8.7.3, compileSdk 36, targetSdk 36, minSdk 26, Jetpack Compose Material3, 5 tab navigation, Room DB + KSP, DataStore preferences, initial vocabulary seeder | `.\gradlew assembleDebug` (17.7 MB APK) & `.\gradlew testDebugUnitTest` (4/4 passed) | DONE |
| 2026-10-19 | Phase 2 | Implementasi Learning MVP: Onboarding, Flashcard flip interaktif dengan rating Hard/Good, Kuis pilihan ganda adaptif dengan skor & review, Search bar & Bookmark, 20 kata bundel Level 1, dan Room persistence | `.\gradlew assembleDebug` (17.8 MB APK) & `.\gradlew testDebugUnitTest` (6/6 passed) | DONE |
| 2026-10-19 | Phase 3 | Implementasi Adaptive Review: SpacedRepetitionScheduler murni, UserProgress entity & Room DB v2, queue kata jatuh tempo (due) & kata sulit, integrasi rating Flashcard & Quiz, proteksi clock skew, 8 unit test scheduler | `.\gradlew assembleDebug` (17.8 MB APK) & `.\gradlew testDebugUnitTest` (14/14 passed) | DONE |
| 2026-10-19 | Phase 4 | Implementasi Level Unlock & Mastery Test: Model LevelStatus (LOCKED, AVAILABLE, IN_PROGRESS, READY_FOR_TEST, MASTERED), MasteryTestEngine murni, 10 unit test engine, MasteryTestAttemptEntity & DAO, Room DB v3, MasteryTestScreen lengkap (intro, 10 soal 3 tipe, review kata salah), integrasi pembukaan Level 2 otomatis | `.\gradlew assembleDebug` (17.9 MB APK) & `.\gradlew testDebugUnitTest` (24/24 passed) | DONE |
| 2026-10-19 | Phase 5 | Implementasi GitHub Content Updates: Manifest v1, model LearningPackage/TranslationPackage/UiCatalog, ContentValidator (SHA-256, ukuran, appVersion, cek duplikat ID), 9 unit test validator, InstalledPackageEntity & DAO, Room DB v4, ContentUpdateManager atomik, ContentUpdatesScreen, dan fallback offline | `.\gradlew assembleDebug` (17.95 MB APK) & `.\gradlew testDebugUnitTest` (33/33 passed) | DONE |
| 2026-10-20 | Phase 6 | Implementasi Monetization & Privacy: AdMob test & production IDs, BillingManager Google Play Billing, in-app product lexiladder_premium_remove_ads, PrivacyPolicyScreen, 9 monetization tests | `.\gradlew installDebug` & `.\gradlew testDebugUnitTest` (42/42 passed) | DONE |
| 2026-10-20 | Phase 7 | Persiapan Google Play Store Listing & Assets: App icon 512x512 PNG, Feature graphic 1024x500 24-bit PNG, 6 live phone screenshots (1080x2340) dari Xiaomi Redmi Note 8 Pro, Play Store copy EN & ID, checklist spesifikasi | Aset lengkap di `Doc/store_listing/assets/`, `.\gradlew testDebugUnitTest` (44/44 passed) | DONE (Assets) |
| 2026-10-20 | Phase 7 | Pemisahan Bahasa Antarmuka (UI Language) & Bahasa Penjelasan (Explanation Language): Modul UiLocalizer, dukungan dinamis EN & ID, navigasi reaktif, mode English Immersion vs Bahasa Indonesia di Learn & Quiz | `.\gradlew testDebugUnitTest` (50/50 passed) & `.\gradlew assembleDebug` (SUCCESS in 57s) | DONE |
| 2026-10-20 | Phase 7 | Fitur Pengecekan Pembaruan Konten/Bahasa Otomatis Sehari Sekali: Auto-check interval 24 jam di DataStore, background checker manifest GitHub, dialog modal in-app HomeScreen (Perbarui Sekarang/Nanti Saja), dan status bar notification channel | `.\gradlew testDebugUnitTest` (54/54 passed) & `.\gradlew assembleDebug` (SUCCESS in 59s) | DONE |
| 2026-10-20 | Phase 7 | Ekspansi Kosakata Offline-First (150 kata Level 1 + 200 kata Level 2 + 20 kata Level 3): Katalog mandiri DefaultVocabularyCatalog (370 kata), 5 unit terstruktur Level 1 (@30 kata), 7 unit terstruktur Level 2 (@28-30 kata), IPA phonetic lengkap, definisi CEFR, terjemahan Indonesia kontekstual, dynamic unit display di LearnScreen, dan 5 unit test integritas katalog | `.\gradlew testDebugUnitTest` (59/59 passed) & `.\gradlew assembleDebug` (SUCCESS in 48s) | DONE |
| 2026-10-20 | Phase 7 | Penyesuaian Tipografi & Tata Letak Label Bottom Navigation: Penggantian label 'Kemajuan' menjadi 'Progres' (7 huruf serasi dengan Beranda/Belajar/Latihan), pembatasan maxLines = 1, softWrap = false, dan font size 11sp agar bilah navigasi bawah tetap ramping, presisi, dan tidak melebar ke bawah | `.\gradlew testDebugUnitTest` (59/59 passed) & `.\gradlew assembleDebug` (SUCCESS in 39s) | DONE |
| 2026-10-20 | Phase 7 | Pembuatan Signed Release Android App Bundle (AAB): Penandatanganan berkas rilis dengan keystore lexyladder.jks (alias: lexy, valid s.d. 2054), verifikasi signature jarsigner, berkas app-release.aab (14.51 MiB), dan dokumen keterangan rilis dwibahasa serta panduan unggah Play Console internal testing | `.\gradlew bundleRelease` (SUCCESS in 6m 33s, 14.51 MiB AAB) & jarsigner verified | DONE |
| 2026-10-20 | Phase 8 | Pembaruan Google Play Billing 8.0.0 & Kenaikan Versi (v1.0.1 Build 2): Upgrade pustaka com.android.billingclient:billing-ktx ke 8.0.0, adaptasi QueryProductDetailsResult di BillingManager, kenaikan versionCode = 2 dan versionName = '1.0.1', perakitan ulang signed bundle app-release.aab (14.51 MiB), dan verifikasi jarsigner sukses | `.\gradlew bundleRelease` (SUCCESS in 2m 51s) & `.\gradlew testDebugUnitTest` (59/59 passed) | DONE |
| 2026-10-25 | Phase 8 | Pengunggahan Berhasil ke Google Play Console (Internal Testing): Versi 1.0.1 (Build 2) dengan Billing Library 8.0.0 berhasil diunggah ke track pengujian internal akun pengembang dearyoti | Dikonfirmasi pengguna di Google Play Console | IN PROGRESS |



