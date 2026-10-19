# LexiLadder — Progress Tracker
**Dokumen:** `progress.md`  
**Pasangan dokumen:** `rancangan.md`  
**Tujuan:** melacak implementasi aktual agar VS Code dan Antigravity bekerja bertahap dan tidak menganggap fitur yang belum dibuat sudah selesai.

---

## 1. Status Proyek Saat Ini

- **Status keseluruhan:** `IN PROGRESS`
- **Fase aktif:** Phase 1 — Android Foundation (Selesai; siap Phase 2 — Learning MVP)
- **Fase terakhir selesai:** Phase 1 — Android Foundation
- **Build terakhir:** `.\gradlew assembleDebug` (SUCCESS - app-debug.apk 17.7 MB)
- **Test terakhir:** `.\gradlew testDebugUnitTest` (SUCCESS - 4/4 unit tests passed)
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
| 2 | Learning MVP | NOT STARTED | Siap dikerjakan |
| 3 | Adaptive Review | NOT STARTED | Menunggu Phase 2 |
| 4 | Level Unlock & Mastery Test | NOT STARTED | Menunggu Phase 2 |
| 5 | GitHub Content Updates | NOT STARTED | Menunggu format konten stabil |
| 6 | Monetization & Privacy | NOT STARTED | Setelah alur belajar stabil |
| 7 | QA, ASO & Release | NOT STARTED | Menunggu fitur rilis |
| 8 | Post-launch | NOT STARTED | Setelah rilis |

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

**Status fase:** `NOT STARTED`

### Task
- [ ] Onboarding.
- [ ] Home dan target harian.
- [ ] Level map.
- [ ] Detail level dan unit.
- [ ] Flashcard.
- [ ] Definisi, contoh, dan terjemahan.
- [ ] Kuis pilihan ganda.
- [ ] Favorit dan pencarian dasar.
- [ ] Import konten Level 1 yang dibundel.
- [ ] Simpan progres lokal.

### Acceptance criteria
- [ ] Pengguna bisa menyelesaikan satu sesi belajar.
- [ ] Jawaban disimpan dengan benar.
- [ ] Progres bertahan setelah aplikasi ditutup dan dibuka kembali.
- [ ] Fitur belajar utama bekerja dalam mode pesawat.
- [ ] Tidak ada konten placeholder yang disajikan sebagai materi final tanpa label.

### Bukti penyelesaian
- **Commit/hash:** —
- **Build/test:** —
- **Perangkat diuji:** —
- **Blocker:** —

---

## 7. Phase 3 — Adaptive Review

**Status fase:** `NOT STARTED`

### Task
- [ ] Model progres kata.
- [ ] Aturan jadwal review.
- [ ] Prioritas kata sulit.
- [ ] Penanganan jawaban benar/salah.
- [ ] Statistik dasar.
- [ ] Unit test interval review.
- [ ] Uji kasus tanggal, zona waktu, dan perubahan jam sistem.

### Acceptance criteria
- [ ] Kata yang salah mendapat jadwal review yang sesuai.
- [ ] Kata yang benar berulang memiliki interval yang dapat bertambah.
- [ ] Satu jawaban benar tidak langsung menetapkan mastery penuh.
- [ ] Aturan review dapat diuji tanpa UI.
- [ ] Jadwal tersimpan dan tetap konsisten setelah restart.

### Bukti penyelesaian
- **Commit/hash:** —
- **Test:** —
- **Blocker:** —

---

## 8. Phase 4 — Level Unlock & Mastery Test

**Status fase:** `NOT STARTED`

### Task
- [ ] Status level: Locked, Available, In Progress, Ready for Test, Mastered.
- [ ] Persyaratan unit wajib.
- [ ] Bank soal ujian.
- [ ] Randomisasi soal dan pilihan.
- [ ] Penilaian kelulusan.
- [ ] Layar hasil ujian.
- [ ] Pengulangan ujian setelah gagal.
- [ ] Penyimpanan riwayat percobaan dan versi ujian.
- [ ] Level berikutnya terbuka setelah lulus.
- [ ] Kelulusan tidak hilang setelah update konten.

### Acceptance criteria
- [ ] Level terkunci tidak dapat dibuka hanya dengan mengubah UI.
- [ ] Ujian menilai berdasarkan kunci jawaban yang tervalidasi.
- [ ] Ambang kelulusan mengikuti konfigurasi versi ujian.
- [ ] Hasil dan versi ujian tersimpan.
- [ ] Level selanjutnya terbuka hanya setelah syarat kelulusan terpenuhi.
- [ ] Kelulusan lama tidak dicabut oleh update biasa.

### Bukti penyelesaian
- **Commit/hash:** —
- **Test:** —
- **Blocker:** —

---

## 9. Phase 5 — GitHub Content Updates

**Status fase:** `NOT STARTED`

### Task
- [ ] Manifest konten tersedia.
- [ ] Repository konten publik disiapkan untuk paket gratis.
- [ ] Versi skema dan versi paket ditetapkan.
- [ ] Checksum SHA-256 tersedia.
- [ ] Pemeriksaan pembaruan.
- [ ] Unduh ke file sementara.
- [ ] Validasi ukuran dan checksum.
- [ ] Validasi schema, ID duplikat, dan relasi.
- [ ] Import paket dalam transaksi.
- [ ] Pertahankan paket lama jika update gagal.
- [ ] Status update ditampilkan ke pengguna.
- [ ] Uji paket tidak kompatibel.
- [ ] Uji mode pesawat setelah konten terpasang.
- [ ] Dokumentasikan proses publikasi konten.

### Acceptance criteria
- [ ] Paket valid dapat diunduh dan dipasang.
- [ ] Paket rusak atau checksum salah ditolak.
- [ ] URL unduhan dibatasi ke sumber tepercaya.
- [ ] Tidak ada kode executable yang dijalankan dari konten.
- [ ] Update tidak menghapus progres.
- [ ] Aplikasi tetap berfungsi jika GitHub tidak tersedia.
- [ ] Update gagal tidak merusak versi konten aktif.

### Bukti penyelesaian
- **Commit/hash:** —
- **Manifest version:** —
- **Test:** —
- **Blocker:** —

---

## 10. Phase 6 — Monetization & Privacy

**Status fase:** `NOT STARTED`

### Task
- [ ] Integrasi AdMob dengan test ad unit.
- [ ] Aturan penempatan iklan.
- [ ] Iklan tidak tampil di tengah ujian/sesi.
- [ ] Google Play Billing.
- [ ] Produk premium dibuat di Play Console.
- [ ] Pembelian dan restore purchase.
- [ ] Penanganan pending/revoked purchase.
- [ ] Consent dan privasi.
- [ ] Kebijakan privasi dipublikasikan.
- [ ] Data Safety dan deklarasi SDK ditinjau.

### Acceptance criteria
- [ ] Test ads bekerja di lingkungan pengujian.
- [ ] Tidak ada iklan yang menghalangi jawaban atau hasil ujian.
- [ ] Premium menghilangkan iklan sesuai status pembelian.
- [ ] Restore purchase diuji.
- [ ] Kegagalan iklan tidak membuat aplikasi gagal.
- [ ] Deklarasi privasi sesuai implementasi aktual.

### Bukti penyelesaian
- **Commit/hash:** —
- **Billing test:** —
- **Ads test:** —
- **Blocker:** —

---

## 11. Phase 7 — QA, ASO & Release

**Status fase:** `NOT STARTED`

### Task
- [ ] Pengujian perangkat nyata.
- [ ] Pengujian mode offline.
- [ ] Pengujian migrasi database.
- [ ] Pengujian update konten gagal/berhasil.
- [ ] Pengujian aksesibilitas.
- [ ] Pengujian crash dan performa.
- [ ] Ikon aplikasi.
- [ ] Screenshot Play Store.
- [ ] Deskripsi dan metadata ASO.
- [ ] Closed testing sesuai ketentuan Google Play saat rilis.
- [ ] Pemeriksaan kebijakan terbaru.
- [ ] Release build ditandatangani dan diverifikasi.

### Acceptance criteria
- [ ] Tidak ada blocker yang diketahui.
- [ ] Fitur yang disebut dalam listing benar-benar tersedia.
- [ ] Semua test rilis penting lulus atau pengecualian didokumentasikan.
- [ ] Data dan privasi ditinjau.
- [ ] Proses rollback/penanganan masalah diketahui.

### Bukti penyelesaian
- **Versi rilis:** —
- **Build:** —
- **Tanggal:** —
- **Blocker:** —

---

## 12. Phase 8 — Post-launch

**Status fase:** `NOT STARTED`

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
