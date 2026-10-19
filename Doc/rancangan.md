# LexiLadder — Master Plan Pengembangan
**Dokumen:** `rancangan.md`  
**Produk:** LexiLadder — English Vocabulary Builder  
**Platform:** Android  
**Tooling:** VS Code + Antigravity  
**Bahasa implementasi:** Kotlin  
**UI:** Jetpack Compose  
**Database lokal:** Room / SQLite  
**Repository konten:** GitHub  
**Repository aplikasi:** https://github.com/arton-soul/ladder.git (GitHub: arton.79@gmail.com)  
**Developer Play Store:** dearyoti (Google Play Console: made.sudi@gmail.com)  
**Model produk:** Offline-first, freemium (AdMob + Premium)
**Strategi pasar:** Global-first; satu aplikasi Android untuk banyak negara dan bahasa
**Status nama:** LexiLadder adalah nama kerja; verifikasi merek, Play Store, domain, dan package name sebelum final

---

## 1. Visi Produk

LexiLadder membantu pengguna global membangun kosakata bahasa Inggris melalui sesi singkat, materi offline, latihan adaptif, dan jalur level yang terstruktur.

### Nilai utama
1. **Daily learning:** sesi singkat yang mudah diselesaikan, dengan target awal sekitar 5 menit.
2. **Offline-first:** materi yang sudah tersedia di perangkat tetap bisa dipelajari tanpa internet.
3. **Adaptive practice:** kata yang belum dikuasai dijadwalkan untuk lebih sering ditinjau.
4. **Structured levels:** level dibuka setelah pengguna memenuhi syarat dan lulus ujian khusus.
5. **Content updates:** konten gratis dapat diperbarui dari repository GitHub tanpa harus merilis APK baru.
6. **Low operating cost:** tidak memerlukan backend khusus untuk fitur belajar inti.

### Prinsip produk
- Jangan mewajibkan akun.
- Jangan mewajibkan koneksi internet untuk belajar.
- Jangan membuat iklan mengganggu sesi atau ujian.
- Jangan menghapus progres karena paket konten diperbarui.
- Jangan mengunci fitur inti pembelajaran di balik premium.
- Utamakan konten akurat dan berlisensi jelas daripada mengejar jumlah kata semata.

---

## 2. Target Pengguna dan Positioning

### Target awal
- Pembelajar bahasa Inggris pemula hingga menengah.
- Orang yang ingin belajar dalam sesi singkat.
- Pengguna yang membutuhkan akses offline.
- Pengguna yang menyukai jalur belajar dengan target dan ujian yang jelas.

### Positioning
> Learn English words in 5 minutes a day — offline, one level at a time.

Ini adalah pesan pemasaran awal, bukan klaim hasil belajar yang dijamin.

### Bahasa
- UI awal: English.
- Data konten harus mendukung beberapa bahasa terjemahan.
- Mulai dengan satu bahasa terjemahan yang ditinjau secara konsisten, lalu perluas berdasarkan data pengguna.
- Jangan mengasumsikan satu terjemahan selalu cukup; kata dapat memiliki beberapa makna berdasarkan konteks.

---

## 3. Scope MVP

### Wajib ada sebelum rilis awal
- Onboarding sederhana: pilihan level awal atau tes penempatan opsional, bahasa penjelasan, target harian.
- Home: target harian, sesi berikutnya, kata untuk review, progres.
- Learn: flashcard, definisi, contoh kalimat, terjemahan, favorit.
- Practice: kuis pilihan ganda dan latihan mengingat arti.
- Level map: status Locked, Available, Learning, Mastered.
- Mastery test khusus untuk setiap level yang dirilis.
- Room database untuk konten dan progres lokal.
- Spaced repetition sederhana.
- Paket Level 1 tersedia di instalasi awal.
- Pemeriksaan pembaruan konten GitHub dan unduhan paket opsional.
- Validasi paket sebelum mengaktifkan konten.
- Pengaturan, kebijakan privasi, dan penanganan kesalahan.
- Pengujian mode pesawat.

### Ditunda sampai setelah MVP tervalidasi
- AI generatif atau AI cloud.
- Akun wajib dan sinkronisasi cloud.
- Multiplayer, leaderboard online, dan komunitas.
- Speech recognition kompleks.
- Paket audio besar yang wajib diunduh.
- Banyak bahasa UI sekaligus.
- Konten premium yang perlu dilindungi dari akses publik.
- Gamifikasi kompleks yang tidak membantu retensi belajar.

---

## 4. Navigasi dan Layar

Tab navigasi yang disarankan:
1. **Home** — target, review, streak, mulai sesi.
2. **Learn** — level, unit, kategori, daftar kata, favorit.
3. **Practice** — kuis dan review kata sulit.
4. **Progress** — kata dipelajari, akurasi, hasil ujian, riwayat.
5. **Settings** — bahasa, target, pengingat, tema, konten, backup, premium, privasi.

### Layar inti
- `OnboardingScreen`
- `HomeScreen`
- `LevelMapScreen`
- `LevelDetailScreen`
- `UnitScreen`
- `FlashcardScreen`
- `PracticeScreen`
- `MasteryTestIntroScreen`
- `MasteryTestScreen`
- `MasteryResultScreen`
- `ProgressScreen`
- `ContentUpdatesScreen`
- `SettingsScreen`
- `PremiumScreen`

### Alur belajar harian
1. Tinjau kata yang jatuh tempo.
2. Pelajari beberapa kata baru.
3. Jawab latihan singkat.
4. Tampilkan hasil dan rekomendasi review berikutnya.

Durasi 5 menit adalah target UX, bukan batas wajib. Pengguna boleh menyelesaikan sesi lebih cepat atau melanjutkan belajar.

---

## 5. Sistem Level dan Penguasaan

### Contoh peta level
| ID | Nama contoh | Fokus | Status awal |
|---|---|---|---|
| `level-a1` | Foundation | kata dasar dan situasi sehari-hari | Terbuka |
| `level-a2` | Everyday English | komunikasi dan aktivitas umum | Terkunci |
| `level-b1` | Intermediate | konteks, frasa, phrasal verbs dasar | Terkunci |
| `level-b2` | Upper Intermediate | collocations dan nuansa makna | Terkunci |
| `level-c1` | Advanced | bahasa profesional dan akademik | Terkunci |
| `level-c2` | Proficiency | kosakata tingkat lanjut dan konteks kompleks | Terkunci |

Nama level produk dapat berbeda, tetapi pemetaan ke CEFR harus ditinjau secara editorial. Jangan menyebut suatu kata sebagai level CEFR tertentu tanpa dasar yang memadai.

### Status level
- `LOCKED`: prasyarat belum terpenuhi.
- `AVAILABLE`: paket tersedia dan level dapat dimulai.
- `IN_PROGRESS`: pengguna sudah mulai belajar.
- `READY_FOR_TEST`: persyaratan materi wajib terpenuhi.
- `MASTERED`: ujian lulus dan kelulusan tersimpan.
- `UPDATE_AVAILABLE`: versi konten baru tersedia; ini status pembaruan, bukan status penguasaan.

Status penguasaan dan status paket konten harus disimpan terpisah.

### Aturan kelulusan awal
- Ambang awal: 80%, dapat dikonfigurasi melalui versi konten.
- Contoh ujian: 25–30 soal dari beberapa tipe dan kategori.
- Pertanyaan dan urutan jawaban diacak.
- Gunakan bank soal yang memadai agar ujian ulang tidak hanya menghafal urutan.
- Uji arti, pengenalan kata, dan penggunaan kata dalam konteks.
- Jika gagal, berikan umpan balik berdasarkan topik yang perlu dipelajari kembali.
- Jumlah percobaan tidak dibatasi pada MVP.
- Simpan skor, waktu, versi ujian, dan hasil setiap percobaan.
- Kelulusan lama tidak dicabut hanya karena konten diperbarui.
- Ujian yang sedang berjalan harus menyelesaikan dengan snapshot versi konten yang sama.

### Persyaratan sebelum ujian
Persyaratan dapat mencakup:
- semua unit wajib telah diselesaikan;
- persentase minimum kata sudah pernah dipelajari;
- review wajib telah diselesaikan jika ditetapkan oleh desain level.

Hindari memaksa pengguna mencapai 100% mastery untuk setiap kata sebelum mengikuti ujian, karena dapat membuat progres terasa terlalu berat.

### Integritas ujian
Karena aplikasi offline, hasil ujian disimpan di perangkat dan dapat dimodifikasi oleh pengguna yang memiliki akses teknis ke perangkat. Untuk MVP, ini dapat diterima karena tidak ada hadiah bernilai tinggi atau sertifikasi resmi. Jangan mengklaim hasil ujian offline sebagai sertifikasi yang tahan manipulasi.

---

## 6. Spaced Repetition dan Latihan Adaptif

### Data progres per kata
- `wordId`
- `correctCount`
- `wrongCount`
- `lastReviewedAt`
- `nextReviewAt`
- `learningStage`
- `easeScore` atau parameter kesulitan
- `masteryStatus`
- `lastAnswerQuality`

### Aturan awal yang sederhana
- Jawaban salah: jadwalkan ulang dalam sesi atau interval pendek.
- Jawaban benar tetapi terasa sulit: interval pendek.
- Jawaban benar dan mudah: interval lebih panjang.
- Kata yang sering salah mendapat prioritas lebih tinggi.
- Kata yang telah dikuasai tetap bisa masuk review berkala.
- Jawaban benar satu kali tidak langsung menandakan penguasaan penuh.

Simpan algoritma dalam satu komponen/domain service yang bisa diuji unit, bukan tersebar di UI. Jangan memerlukan machine learning.

### Jenis latihan MVP
- Pilihan ganda: kata → arti.
- Pilihan ganda: arti → kata.
- Flashcard dengan penilaian mandiri.
- Melengkapi kalimat jika kualitas distraktor dan jawaban dapat divalidasi.

Mengetik kata, audio, dan latihan lanjutan dapat ditambahkan setelah model konten mendukungnya dengan baik.

---

## 7. Arsitektur Teknis Android

### Stack
- Kotlin.
- Jetpack Compose + Material 3.
- MVVM atau pola UI state yang konsisten.
- Repository pattern.
- Coroutines dan Flow.
- Room / SQLite.
- DataStore untuk preferensi sederhana.
- WorkManager untuk pekerjaan latar yang sesuai.
- Android TextToSpeech sebagai opsi pengucapan dinamis.
- Google Play Billing untuk produk digital.
- Google Mobile Ads SDK untuk AdMob.
- Firebase Analytics opsional, dengan konfigurasi privasi yang sesuai.

Verifikasi versi dependency yang kompatibel saat implementasi; jangan menyalin versi library lama tanpa pemeriksaan.

### Struktur package yang disarankan
```text
app/src/main/java/<package>/
├── app/
│   ├── LexiLadderApp.kt
│   └── navigation/
├── core/
│   ├── common/
│   ├── database/
│   ├── content/
│   ├── network/
│   ├── billing/
│   └── ads/
├── data/
│   ├── local/
│   ├── remote/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── feature/
│   ├── onboarding/
│   ├── home/
│   ├── levels/
│   ├── learn/
│   ├── practice/
│   ├── masterytest/
│   ├── progress/
│   ├── contentupdates/
│   ├── settings/
│   └── premium/
└── ui/
    ├── components/
    └── theme/
```

Sesuaikan package name dan struktur modul dengan proyek nyata. Jangan membuat semua modul Gradle terpisah sejak awal jika belum diperlukan; struktur package sederhana cukup untuk MVP.

### Prinsip arsitektur
- UI hanya menampilkan state dan mengirim intent/event.
- Logika kelulusan, review, validasi paket, dan premium berada di layer domain/repository.
- Repository menyediakan data lokal sebagai sumber utama saat offline.
- Konten remote tidak langsung ditampilkan sebelum validasi dan transaksi lokal berhasil.
- Migrasi Room harus diuji dan tidak boleh menghapus progres.
- Hindari `GlobalScope`; gunakan lifecycle-aware coroutine.
- Tangani loading, error, empty state, dan retry dengan konsisten.

---

## 8. Desain Database Room

Skema awal berikut adalah panduan konseptual. Finalisasi tipe kolom, indeks, foreign key, converter, dan migrasi saat implementasi.

### Tabel konten
- `ContentPackageEntity`
  - `packageId`, `version`, `schemaVersion`, `checksum`, `installedAt`, `status`
- `LevelEntity`
  - `levelId`, `title`, `description`, `cefrLevel`, `contentVersion`, `sortOrder`, `isActive`
- `UnitEntity`
  - `unitId`, `levelId`, `title`, `sortOrder`, `isRequired`
- `WordEntity`
  - `wordId`, `headword`, `partOfSpeech`, `definitionEn`, `pronunciationIpa`, `exampleSentence`, `exampleTranslation`, `levelId`, `isActive`
- `WordTranslationEntity`
  - `wordId`, `locale`, `meaning`, `exampleTranslation`
- `TopicEntity` dan `WordTopicCrossRef`
- `QuestionEntity`
  - `questionId`, `levelId`, `unitId`, `questionType`, `prompt`, `explanation`, `contentVersion`
- `QuestionOptionEntity`
  - `optionId`, `questionId`, `label`, `optionOrder`, `isCorrect`

### Tabel progres
- `WordProgressEntity`
  - `wordId`, `correctCount`, `wrongCount`, `learningStage`, `nextReviewAt`, `lastReviewedAt`, `masteryStatus`
- `UnitProgressEntity`
  - `unitId`, `completedAt`, `completionState`
- `LevelProgressEntity`
  - `levelId`, `status`, `unlockedAt`, `startedAt`, `masteredAt`, `bestScore`
- `MasteryAttemptEntity`
  - `attemptId`, `levelId`, `examVersion`, `score`, `passed`, `startedAt`, `completedAt`
- `MasteryAnswerEntity`
  - `attemptId`, `questionId`, `selectedOptionId`, `isCorrect`
- `UserSettingsEntity`
  - bahasa, target harian, pengingat, preferensi UI.

### Aturan integritas
- Gunakan ID konten stabil, tidak berdasarkan urutan tampilan.
- Gunakan transaksi database saat mengaktifkan paket baru.
- Pertahankan progres untuk konten yang diperbarui.
- Jika kata dihapus dari katalog, tandai tidak aktif; jangan langsung menghapus riwayatnya.
- Jika sebuah soal diubah secara substansial, gunakan ID atau versi soal baru agar riwayat ujian tetap dapat ditafsirkan.
- Tambahkan foreign key dan indeks yang relevan.
- Uji migrasi database dengan data progres yang sudah ada.

---

## 9. Sistem Konten GitHub

### Tujuan
GitHub menyimpan konten statis dan manifest rilis. GitHub bukan database progres pengguna dan tidak diperlukan saat pengguna belajar.

### Struktur repository konten
```text
wordsprint-content/
├── manifest.json
├── catalog/
│   └── levels.json
├── levels/
│   ├── level-a1/
│   │   ├── level.json
│   │   ├── units/
│   │   │   ├── unit-001.json
│   │   │   └── unit-002.json
│   │   └── mastery-test.json
│   └── level-a2/
│       ├── level.json
│       ├── units/
│       └── mastery-test.json
├── schemas/
│   ├── manifest.schema.json
│   ├── level.schema.json
│   ├── word.schema.json
│   └── question.schema.json
├── scripts/
│   └── validate-content.*
└── CHANGELOG.md
```

### Manifest
Manifest minimal perlu memuat:
- `schemaVersion`
- `contentVersion`
- `minimumAppVersion`
- daftar paket dan versi tiap paket
- URL relatif/absolut yang tervalidasi
- checksum SHA-256
- ukuran paket dan jumlah entri
- bahasa yang didukung
- status rilis paket

Gunakan HTTPS dan URL yang dibatasi ke sumber tepercaya. Jangan menerima URL arbitrer dari konten yang diunduh.

### Alur pembaruan
1. Pengguna membuka halaman pembaruan atau aplikasi memeriksa secara jarang ketika online.
2. Ambil manifest dengan timeout dan penanganan error.
3. Bandingkan versi manifest dan paket lokal.
4. Tampilkan ukuran paket serta opsi unduh jika relevan.
5. Unduh ke file sementara.
6. Periksa ukuran, checksum, versi skema, ID duplikat, referensi, dan struktur konten.
7. Validasi konten dan soal.
8. Import menggunakan transaksi; jangan menghapus paket aktif terlebih dahulu.
9. Tandai versi baru aktif hanya setelah semua pemeriksaan berhasil.
10. Jika gagal, hapus file sementara dan pertahankan paket lama.
11. Catat versi paket yang terpasang dan tampilkan hasil kepada pengguna.

### Ketahanan dan keamanan
- Konten gratis dari GitHub publik dapat diakses dan disalin siapa saja.
- SHA-256 mendeteksi kerusakan file, tetapi tidak membuktikan siapa penerbitnya jika penyerang juga dapat mengubah manifest.
- Untuk integritas yang lebih kuat, pertimbangkan tanda tangan digital manifest/paket dan verifikasi public key yang ditanam di aplikasi.
- Jangan mengunduh dan menjalankan kode executable dari repository konten.
- Jangan menyimpan rahasia atau token tulis GitHub di aplikasi.
- Gunakan GitHub Releases atau berkas statis versi tetap jika lebih stabil untuk kebutuhan distribusi.
- Sediakan konten dasar di APK sehingga instalasi baru tetap berguna tanpa koneksi.

### Konten premium
Jangan menganggap pemeriksaan premium lokal atau URL yang tidak ditampilkan sebagai perlindungan kuat. Konten dalam repository publik tidak dapat dianggap rahasia. Untuk MVP, gunakan GitHub publik untuk materi gratis; desain distribusi premium terpisah jika perlindungan konten menjadi kebutuhan bisnis.

---

## 10. Format Konten dan Contoh

Contoh konseptual `level.json`:
```json
{
  "schemaVersion": 1,
  "levelId": "level-a1",
  "version": "1.0.0",
  "title": "Foundation",
  "cefrLevel": "A1",
  "prerequisiteLevelIds": [],
  "requiredUnitIds": ["unit-001", "unit-002"],
  "masteryTestId": "test-level-a1",
  "passPercentage": 80,
  "questionCount": 25
}
```

Contoh konseptual kata:
```json
{
  "wordId": "word_example_0001",
  "headword": "reliable",
  "partOfSpeech": "adjective",
  "definitionEn": "Able to be trusted or depended on.",
  "pronunciationIpa": "/rɪˈlaɪəbl/",
  "translations": {
    "id": "dapat diandalkan"
  },
  "exampleSentence": "She is a reliable friend.",
  "exampleTranslations": {
    "id": "Dia adalah teman yang dapat diandalkan."
  },
  "levelId": "level-a2",
  "topicIds": ["personality"]
}
```

Jangan memasukkan field JSON yang tidak dikenal tanpa aturan kompatibilitas. Skema harus memvalidasi konten, tetapi aplikasi tetap perlu menangani field opsional dengan aman.

---

## 11. AdMob dan Premium

### Free
- Materi dasar offline.
- Flashcard, kuis, review, dan ujian level.
- Progres dasar.
- Iklan pada lokasi nonintrusif.

### Premium
- Tanpa iklan.
- Paket topik khusus atau fitur tambahan yang jelas nilainya.
- Statistik lanjutan dan opsi kenyamanan.
- Fitur tambahan yang tidak merusak manfaat inti pengguna gratis.

### Aturan iklan
- Jangan menampilkan iklan di tengah soal, saat pengguna menjawab, atau sebelum hasil ujian.
- Pertimbangkan banner hanya di layar yang tidak mengganggu belajar.
- Interstitial hanya pada jeda alami dan dengan frekuensi rendah; jangan menghalangi alur belajar.
- Rewarded ad harus opsional dan manfaatnya jelas.
- Gunakan test ad unit selama pengembangan.
- Siapkan consent/privacy flow yang sesuai dengan wilayah distribusi dan kebijakan yang berlaku.
- Periksa kebijakan Google Play dan AdMob terbaru sebelum rilis.

### Billing
- Gunakan Google Play Billing untuk pembelian digital di aplikasi jika diwajibkan kebijakan.
- Tangani pending purchase, restore/query purchases, refund/revocation, dan status premium.
- Jangan menjadikan flag premium yang mudah diedit sebagai satu-satunya sumber kebenaran tanpa desain yang sesuai.
- Untuk MVP offline-first, jelaskan bahwa status pembelian dan pemulihan mungkin memerlukan koneksi.

---

## 12. Privasi, Aksesibilitas, dan Kualitas

- Jangan mewajibkan nama, email, atau akun untuk belajar.
- Minimalkan data analitik.
- Sediakan kebijakan privasi yang menjelaskan SDK iklan/analitik dan data yang dikumpulkan.
- Gunakan kontras warna memadai, ukuran teks yang dapat dibaca, dukungan TalkBack, dan target sentuh yang cukup.
- Jangan hanya menggunakan warna untuk menyampaikan benar/salah.
- Tangani rotasi layar, proses aplikasi dihentikan, dan pemulihan sesi ujian.
- Uji perangkat dengan memori rendah dan versi Android yang didukung.
- Sediakan status loading, empty, offline, error, dan retry yang jelas.

---

## 13. Pengujian dan Acceptance Criteria

### Fungsional
- Pengguna dapat memulai dan menyelesaikan sesi belajar.
- Jawaban kuis memperbarui progres dengan benar.
- Review menampilkan kata yang jatuh tempo.
- Level terkunci tidak bisa dibuka hanya dengan navigasi UI.
- Level terbuka setelah kelulusan tersimpan.
- Ujian menyimpan skor dan versi konten.
- Perubahan konten tidak menghapus progres.
- Pengguna dapat mengunduh paket dan belajar dari paket tersebut tanpa internet.

### Pembaruan konten
- Manifest tidak valid ditolak.
- Checksum salah ditolak.
- ID duplikat atau relasi rusak ditolak.
- Kegagalan unduhan tidak merusak paket lama.
- Import bersifat transaksional.
- Konten yang sudah diunduh tetap tersedia dalam mode pesawat.
- Paket yang tidak kompatibel dengan versi aplikasi tidak diaktifkan.

### Database
- Migrasi mempertahankan progres.
- Foreign key dan indeks diuji.
- Import paket tidak membuat duplikasi.
- Menghapus atau menonaktifkan kata tidak menghapus riwayat tanpa aturan eksplisit.

### Monetisasi
- Premium menghilangkan iklan sesuai status pembelian.
- Restore purchase berfungsi.
- Iklan tidak tampil pada layar ujian atau di tengah sesi.
- Aplikasi tetap dapat digunakan ketika iklan gagal dimuat.

### Rilis
- Tidak ada crash blocker.
- Kebijakan privasi dan deklarasi Data safety diperiksa.
- Listing dan screenshot sesuai fitur yang benar-benar ada.
- Build release diuji pada perangkat nyata.
- Backup/restore diuji jika fitur tersebut dirilis.

---

## 14. Roadmap Tahapan

### Phase 0 — Product & Repository Setup
- Tetapkan package name, min/target SDK, struktur repository.
- Buat dokumen arsitektur dan aturan coding.
- Siapkan repository aplikasi dan repository konten.
- Definisikan schema JSON dan contoh konten.
- Tentukan lisensi sumber kosakata dan proses editorial.

**Selesai jika:** proyek dapat dibangun, struktur repo tersedia, dan contoh konten lolos validasi.

### Phase 1 — Android Foundation
- Siapkan Kotlin, Compose, tema, navigasi.
- Implementasikan Room, DataStore, repository, dan dependency injection bila diperlukan.
- Buat UI state dan penanganan error dasar.

**Selesai jika:** aplikasi berjalan, navigasi berfungsi, database dapat dibuat dan diuji.

### Phase 2 — Learning MVP
- Implementasikan Home, level map, unit, flashcard, dan kuis.
- Import konten bawaan Level 1.
- Simpan progres dan favorit.

**Selesai jika:** alur belajar inti berjalan offline dan progres bertahan setelah aplikasi ditutup.

### Phase 3 — Adaptive Review
- Implementasikan penjadwalan review.
- Buat kata sulit dan statistik dasar.
- Tambahkan pengujian unit untuk aturan interval.

**Selesai jika:** hasil jawaban memengaruhi jadwal review secara konsisten.

### Phase 4 — Level Unlock & Mastery Test
- Implementasikan prasyarat level.
- Buat bank soal, randomisasi, hasil ujian, dan percobaan ulang.
- Simpan versi ujian serta kelulusan.

**Selesai jika:** kelulusan membuka level berikutnya dan tetap tersimpan setelah restart.

### Phase 5 — GitHub Content Updates
- Buat manifest, repository konten, dan validator.
- Implementasikan cek versi, unduh, checksum, validasi, dan import transaksional.
- Uji rollback dan mode offline.

**Selesai jika:** konten baru dapat dipasang tanpa APK baru dan kegagalan update tidak merusak data lama.

### Phase 6 — Monetization & Privacy
- Integrasikan AdMob menggunakan test ads.
- Implementasikan Billing dan status premium.
- Siapkan consent, privasi, dan pengaturan iklan.

**Selesai jika:** iklan dan pembelian diuji di jalur test yang benar dan tidak menghalangi belajar.

### Phase 7 — QA, ASO & Release
- Uji perangkat nyata, performa, aksesibilitas, dan migrasi.
- Siapkan ikon, screenshot, deskripsi, dan listing.
- Uji closed testing sesuai persyaratan Google Play yang berlaku.

**Selesai jika:** acceptance criteria terpenuhi dan tidak ada blocker rilis.

### Phase 8 — Post-launch
- Pantau crash, retensi, penyelesaian sesi, dan konversi listing.
- Perbaiki konten berdasarkan temuan.
- Tambahkan level dan bahasa secara bertahap.
- Prioritaskan fitur berdasarkan data dan umpan balik.

---

## 15. Aturan Kerja VS Code + Antigravity

### Sebelum memulai setiap fase
1. Baca `rancangan.md` secara menyeluruh.
2. Baca `progress.md` dan catatan keputusan terakhir.
3. Periksa kode yang benar-benar ada; jangan mengasumsikan fase sebelumnya selesai.
4. Tentukan satu milestone kecil dan acceptance criteria.
5. Buat daftar file yang akan diubah sebelum mengedit.

### Saat implementasi
- Kerjakan satu milestone dalam satu waktu.
- Jangan menulis ulang seluruh proyek jika perubahan kecil cukup.
- Pertahankan nama ID, schema, dan perilaku yang sudah stabil.
- Jangan menambahkan dependency tanpa alasan dan verifikasi kompatibilitas.
- Jangan memasukkan API key, token, atau rahasia ke repository.
- Tambahkan unit test untuk domain logic dan content validator.
- Jalankan build/test yang relevan setelah perubahan.
- Periksa diff sebelum menerima perubahan.
- Jangan mengklaim build atau test berhasil jika belum dijalankan.

### Setelah setiap milestone
1. Catat file yang dibuat/diubah.
2. Catat perintah build dan test yang dijalankan beserta hasil nyata.
3. Tandai acceptance criteria satu per satu.
4. Catat bug yang belum selesai.
5. Perbarui `progress.md`.
6. Jika ada perubahan desain, perbarui bagian terkait di `rancangan.md`.
7. Jangan lanjut ke fase berikutnya jika ada blocker pada fase saat ini.

### Format permintaan kerja yang disarankan untuk Antigravity
```text
Baca rancangan.md dan progress.md terlebih dahulu.
Periksa repository aktual dan tentukan milestone aktif yang belum selesai.
Kerjakan hanya milestone berikut: [NAMA MILESTONE].
Jangan mengerjakan fase lain atau menambahkan fitur di luar scope.

Sebelum mengubah kode:
1. Ringkas kondisi implementasi saat ini.
2. Daftar file yang akan dibuat/diubah.
3. Jelaskan pendekatan dan risiko migrasi/kompatibilitas.

Setelah implementasi:
1. Jalankan build dan test yang relevan jika tool tersedia.
2. Laporkan perintah dan hasil aktual, jangan mengarang hasil.
3. Periksa diff.
4. Perbarui progress.md dengan status nyata.
5. Catat blocker dan langkah berikutnya.
Jangan menandai milestone selesai jika acceptance criteria belum terbukti.
```

---

## 16. Definisi Selesai untuk Setiap Task

Task hanya dapat ditandai `[x]` jika:
- Implementasi benar-benar ada di repository.
- Acceptance criteria terkait terpenuhi.
- Test yang relevan dijalankan, atau keterbatasan menjalankan test dicatat dengan jelas.
- Tidak ada error blocker yang diketahui.
- Dokumentasi dan `progress.md` sudah diperbarui.

Status yang digunakan:
- `NOT STARTED`
- `IN PROGRESS`
- `BLOCKED`
- `DONE`
- `NEEDS REVIEW`

Jangan menyamakan “kode sudah ditulis” dengan “fitur sudah selesai”.

---

## 17. Risiko dan Mitigasi

| Risiko | Mitigasi |
|---|---|
| Konten tidak akurat | proses editorial dan validasi contoh |
| Konten hasil salinan tanpa izin | gunakan sumber berlisensi dan catat atribusi |
| Update rusak | checksum, validasi, staging, transaksi, rollback |
| Progres hilang | ID stabil, migrasi teruji, backup bila tersedia |
| Level terlalu sulit | uji coba dengan pengguna dan evaluasi kelulusan |
| Ujian mudah dihafal | bank soal dan variasi tipe soal |
| Repository GitHub tidak tersedia | materi lokal tetap bisa digunakan |
| Biaya iklan tidak sebanding | tempatkan iklan nonintrusif dan pantau retensi |
| Konten premium tersebar | jangan simpan rahasia dalam repo publik; desain distribusi terpisah |
| Scope membengkak | MVP ketat dan roadmap berbasis bukti |

---

## 18. Keputusan yang Belum Final

Catat keputusan berikut sebelum atau selama Phase 0:
- Package name Android.
- `minSdk` dan target SDK yang sesuai saat rilis.
- Nama repository GitHub aplikasi dan konten.
- Bahasa terjemahan pertama.
- Jumlah kata dan unit untuk Level 1.
- Sumber konten dan lisensi.
- Apakah Level 1 dibundel penuh atau sebagian.
- Kebijakan pembaruan otomatis versus manual.
- Pilihan produk premium: sekali beli, langganan, atau keduanya.
- Kebijakan backup dan pemulihan progres.

Jangan menghambat fondasi teknis karena keputusan yang tidak mendesak. Catat asumsi dan pilih default yang dapat diubah.

---

## 19. Prioritas Pertama

Mulai dari **Phase 0 — Product & Repository Setup**. Jangan langsung mengintegrasikan AdMob atau Billing sebelum alur belajar lokal dan struktur konten terbukti berfungsi.

Urutan implementasi yang disarankan:
1. Proyek Android dapat dibangun.
2. Database dan model konten.
3. Level 1 lokal.
4. Flashcard dan kuis.
5. Progres dan review.
6. Ujian serta level unlock.
7. Update konten GitHub.
8. Monetisasi.
9. QA dan rilis.

`progress.md` adalah catatan status aktual. `rancangan.md` adalah sumber keputusan produk dan teknis. Perbarui keduanya saat ada perubahan yang disepakati.


---

## 15. Strategi Global dan Manajemen Bahasa

### 15.1 Prinsip produk global
- Produk dirancang untuk satu aplikasi Android yang dapat dipasarkan di banyak negara; jangan membuat APK terpisah per negara kecuali ada kebutuhan bisnis atau regulasi yang jelas.
- Bahasa antarmuka (UI language) dipisahkan dari bahasa target belajar (target language) dan bahasa penjelasan/terjemahan (support language).
- Contoh: pengguna dapat belajar bahasa Inggris dengan penjelasan bahasa Indonesia, Portugis, Vietnam, atau bahasa lain yang didukung.
- Gunakan kode bahasa standar BCP 47, misalnya `en`, `id`, `pt-BR`, `vi`, dan `hi`.
- Tetapkan bahasa fallback yang stabil (English) dan fallback berlapis: bahasa pilihan pengguna → bahasa fallback yang didukung → English → key yang aman untuk debugging, bukan layar kosong.
- Satu bahasa baru tidak boleh mengharuskan perubahan pada struktur database atau logika belajar jika hanya berupa data terjemahan yang sudah didukung oleh aplikasi.
- Jangan mengklaim semua bahasa sudah didukung sebelum paket, QA, dan listing terkait selesai.

### 15.2 Menambahkan bahasa melalui GitHub tanpa update aplikasi
**Persyaratan arsitektur sejak awal:** aplikasi harus dirancang menggunakan katalog string UI dan paket terjemahan berbasis data, bukan hanya mengandalkan Android `strings.xml` yang dikompilasi ke APK.

Bahasa baru yang hanya berisi teks yang sudah memiliki key dan format data kompatibel dapat ditambahkan lewat repository konten GitHub:
1. Tambahkan file katalog bahasa baru, misalnya `ui/pt-BR.json`.
2. Tambahkan paket penjelasan kosakata, misalnya `translations/pt-BR/`.
3. Validasi semua key wajib, placeholder, panjang teks, plural, dan format.
4. Jalankan pemeriksaan kualitas terjemahan dan review manusia.
5. Tambahkan entri bahasa ke `manifest.json` dengan versi, status, ukuran, URL, dan checksum.
6. Uji paket di build lokal atau channel internal menggunakan data yang sama.
7. Publikasikan manifest/paket setelah validasi lulus.
8. Aplikasi mendeteksi bahasa baru ketika online, menampilkan bahasa tersebut di Settings, lalu mengunduh paket pilihan pengguna.
9. Simpan paket di database/file lokal agar antarmuka dan materi yang sudah diunduh dapat digunakan offline.

**Batasan penting:** metode ini memungkinkan penambahan teks UI dan materi belajar yang telah didukung oleh kerangka aplikasi tanpa update APK. Tidak dapat menambahkan fitur baru, layar baru yang memerlukan kode, aturan navigasi baru, permission Android, SDK, atau perubahan perilaku yang belum diimplementasikan tanpa merilis versi aplikasi baru. Perubahan listing Google Play juga dikelola terpisah di Play Console. Untuk bahasa RTL seperti Arab, dukungan layout RTL harus disiapkan dan diuji di kode aplikasi terlebih dahulu.

### 15.3 Pemisahan tiga jenis bahasa
- `uiLanguage`: bahasa menu, tombol, pesan sistem dalam aplikasi.
- `explanationLanguage`: bahasa penjelasan/terjemahan kata bahasa Inggris.
- `targetLanguage`: bahasa yang sedang dipelajari; MVP awal adalah English (`en`).

Jangan menyatukan ketiganya ke satu preferensi karena pengguna bisa ingin UI bahasa Indonesia tetapi belajar bahasa Inggris dengan penjelasan bahasa Portugis, misalnya.

### 15.4 Struktur repository konten yang disarankan
Repository konten dipisahkan dari repository kode Android.

```text
lexiladder-content/
├── manifest.json
├── schemas/
│   ├── manifest.schema.json
│   ├── ui-strings.schema.json
│   └── learning-package.schema.json
├── ui/
│   ├── en.json
│   └── id.json
├── learning/
│   └── en/
│       ├── A1/
│       └── A2/
├── translations/
│   ├── id/
│   │   └── en/
│   │       ├── A1.json
│   │       └── A2.json
│   └── pt-BR/
│       └── en/
├── quizzes/
│   └── en/
├── changelog/
│   └── content-changelog.json
└── tools/
    ├── validate_content.py
    └── README.md
```

Nama direktori di atas adalah rekomendasi. Format final harus dibakukan sebelum membuat banyak konten.

### 15.5 Ketentuan manifest dan paket
Setiap paket minimal menyimpan:
- `schemaVersion`: versi format skema.
- `contentVersion`: versi konten paket.
- `packageId`: ID stabil.
- `language` atau `locale`.
- `targetLanguage`.
- `minimumAppVersion` bila fitur aplikasi tertentu diperlukan.
- `packageUrl` atau path paket.
- `sha256`.
- `sizeBytes`.
- `updatedAt` dan catatan perubahan yang aman ditampilkan.
- Status publikasi seperti `draft`, `testing`, `published`, `deprecated`.

Manifest tidak boleh mengeksekusi kode. Konten yang diunduh diperlakukan sebagai data tidak tepercaya dan harus divalidasi.

### 15.6 Ketahanan update
- Sertakan paket dasar A1 dan bahasa fallback di instalasi awal agar aplikasi langsung bisa digunakan offline.
- Jangan meminta unduhan seluruh bahasa saat onboarding; unduh hanya bahasa yang dipilih pengguna.
- Periksa manifest dengan koneksi yang hemat dan cache; jangan mengunduh ulang jika versi tidak berubah.
- Gunakan HTTPS, checksum SHA-256, validasi schema, batas ukuran, batas jumlah item, dan pemeriksaan ID duplikat.
- Impor menggunakan transaksi database; jangan mengaktifkan paket sebelum validasi selesai.
- Jika unduhan, checksum, atau import gagal, pertahankan paket aktif sebelumnya.
- Jangan menghapus progres belajar ketika konten diperbarui atau bahasa baru ditambahkan.
- Gunakan ID kata/soal yang stabil; jangan menggunakan teks terjemahan sebagai primary key.
- Jika paket memerlukan `minimumAppVersion` yang lebih tinggi daripada versi aplikasi, jangan mengaktifkannya; tampilkan pesan bahwa update aplikasi diperlukan.
- Untuk aplikasi yang masih offline, pertahankan paket terpasang. Jangan membuat fitur belajar inti bergantung pada GitHub yang selalu tersedia.

### 15.7 GitHub, biaya, dan keamanan
- GitHub dapat digunakan sebagai host awal untuk konten publik dengan biaya rendah, tetapi ketersediaan, bandwidth, rate limit, dan URL harus dipantau; jangan menganggap layanan gratis memiliki SLA produksi.
- Gunakan URL rilis atau raw file yang stabil dan hindari mengganti nama file paket yang sudah dipublikasikan.
- Publikasikan paket baru secara bertahap: draft → validasi otomatis → review → testing → published.
- Jangan menyimpan rahasia, token, atau data pribadi pengguna di repository konten.
- Konten di repository publik dapat diunduh dan disalin; jangan mengandalkannya untuk melindungi materi premium.
- Untuk premium, pertimbangkan distribusi paket yang sesuai dan verifikasi hak akses. Pemeriksaan premium hanya di sisi klien bukan perlindungan kuat.
- Jangan pernah mengunduh atau menjalankan executable code dari repository konten. Hanya data sesuai schema yang diperbolehkan.

### 15.8 Konten, hak cipta, dan QA bahasa
- Catat sumber dan lisensi untuk definisi, contoh, gambar, audio, dan dataset.
- Hindari menyalin kamus komersial atau kumpulan soal berhak cipta tanpa izin.
- Validasi grammar, arti kontekstual, contoh kalimat, jawaban kuis, distraktor, dan level kesulitan.
- Gunakan review manusia untuk terjemahan sebelum publikasi; terjemahan otomatis boleh membantu draf tetapi bukan satu-satunya gerbang kualitas.
- Jangan menerbitkan bahasa baru hanya karena file JSON tersedia; uji UI, pemotongan teks, plural, placeholder, font, dan aksesibilitas.
- Simpan changelog paket untuk memudahkan rollback dan audit.

---

## 16. Rencana Rilis dan Ekspansi Negara

### Tahap A — MVP internal
- Satu aplikasi; UI English dan Indonesia (sesuai prioritas implementasi).
- Target belajar English.
- Paket A1 bundled agar langsung offline.
- Paket bahasa/terjemahan pertama diuji menyeluruh.
- Tidak ada ekspansi negara besar sebelum alur belajar dan update stabil.

### Tahap B — Rilis awal terbatas
- Rilis melalui testing track.
- Uji install baru, upgrade dari versi lama, mode pesawat, update konten gagal, rollback, perubahan bahasa, dan pemulihan progres.
- Verifikasi listing, kebijakan privasi, consent iklan yang berlaku, Billing, dan SDK yang digunakan.
- Gunakan satu package name dan satu aplikasi, dengan ketersediaan negara yang diatur di Play Console.

### Tahap C — Ekspansi konten
- Tambah A2 dan paket berikutnya setelah QA.
- Tambahkan bahasa UI/penjelasan berdasarkan permintaan, hasil unduhan, retensi, dan kemampuan review.
- Bahasa baru ditambahkan melalui GitHub bila hanya memerlukan string dan materi yang sudah didukung aplikasi.
- Catat metrik per negara/bahasa tanpa mengumpulkan data yang tidak diperlukan.

### Tahap D — Optimasi global
- Terjemahkan listing Google Play dan aset pemasaran per pasar prioritas.
- Uji konversi halaman, retensi, crash rate, penyelesaian sesi, dan monetisasi.
- Periksa kualitas konten serta feedback pengguna sebelum menambah bahasa berikutnya.

### Kriteria sebelum menambah bahasa
- Semua key wajib tersedia atau punya fallback yang ditetapkan.
- Placeholder dan plural cocok dengan sumber.
- Tidak ada teks kosong atau key mentah yang tampil ke pengguna.
- Tampilan diuji di layar kecil dan ukuran font besar.
- RTL diuji bila relevan dan sudah didukung aplikasi.
- Terjemahan materi dan jawaban kuis ditinjau manusia.
- Paket dapat diunduh, divalidasi, dipasang, digunakan offline, dan di-rollback.

---

## 17. Keputusan Arsitektur yang Harus Dijaga

1. Satu aplikasi Android, banyak bahasa melalui katalog data.
2. UI language, explanation language, dan target language adalah preferensi terpisah.
3. Konten dasar tersedia offline dari instalasi awal.
4. Konten publik dapat diperbarui lewat GitHub tanpa APK update jika schema kompatibel.
5. String UI baru yang memakai key yang sudah ada dapat ditambahkan lewat paket bahasa remote; key atau UI flow baru yang belum didukung memerlukan update aplikasi.
6. Kode aplikasi tidak boleh dikirim melalui sistem konten.
7. ID kata dan soal stabil, progres pengguna terpisah dari versi konten.
8. Setiap paket harus divalidasi, diberi versi, memiliki checksum, dan dapat ditolak jika tidak kompatibel.
9. Jangan mengunci nama aplikasi, package name, harga, atau cakupan negara sebelum diverifikasi.
10. Jangan menandai fase selesai tanpa bukti implementasi dan pengujian.

---

## 18. Catatan Nama dan ASO

- Nama kerja saat ini: **LexiLadder**.
- Sebelum mengunci nama, cek hasil Play Store, merek dagang pada pasar sasaran, domain, akun media sosial, dan ketersediaan package name.
- Akun Developer Google Play Store: **dearyoti** (email: `made.sudi@gmail.com`).
- Akun & Repository GitHub Aplikasi: `https://github.com/arton-soul/ladder.git` (email: `arton.79@gmail.com`).
- ASO per bahasa mencakup judul, short description, deskripsi lengkap, screenshot, dan lokalisasi listing; jangan menjejalkan kata kunci.
- Nama kerja dalam dokumen bukan bukti bahwa merek atau nama paket tersedia.


---

## 19. Peta Dokumen Proyek

- `rancangan.md`: sumber utama keputusan produk dan roadmap.
- `progress.md`: status implementasi dan bukti pengujian.
- `content_system.md`: kontrak teknis konten, bahasa, manifest, validasi, dan update GitHub.
- `AGENTS.md`: cara coding agent bekerja dan aturan yang harus dijaga.

Jika terjadi konflik, jangan mengubah keputusan secara diam-diam. Catat konflik, tentukan dokumen mana yang perlu diperbarui, dan pastikan semua dokumen konsisten.
