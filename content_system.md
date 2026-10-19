# LexiLadder — Content System Specification

**Status:** Rancangan teknis; belum diimplementasikan  
**Pemilik keputusan:** Pemilik proyek  
**Terkait:** `rancangan.md`, `progress.md`, `AGENTS.md`

## 1. Tujuan

Dokumen ini menentukan cara aplikasi mengelola materi belajar dan bahasa melalui repository GitHub yang terpisah dari kode Android. Tujuan utamanya:
- Menambahkan kosakata, soal, terjemahan, dan bahasa UI tanpa merilis APK baru jika format data yang dibutuhkan sudah didukung aplikasi.
- Mempertahankan fungsi offline-first.
- Menjaga progres pengguna ketika konten berubah.
- Menolak paket rusak, tidak kompatibel, atau tidak tervalidasi.
- Memisahkan konten gratis publik dari materi yang memerlukan perlindungan akses.

## 2. Jawaban arsitektur: bisakah bahasa baru ditambahkan tanpa update APK?

**Ya, dengan syarat aplikasi dirancang sejak awal untuk itu.** UI harus membaca katalog string berbasis data, misalnya `ui/id.json`, bukan seluruhnya bergantung pada string resource yang dikompilasi ke APK. Paket bahasa baru yang hanya menerjemahkan key yang sudah dikenal dapat ditambahkan di GitHub, dimuat ke perangkat, lalu digunakan tanpa merilis APK.

Hal yang dapat ditambahkan melalui paket data:
- Terjemahan string UI untuk key yang sudah didukung.
- Penjelasan/terjemahan kata.
- Contoh kalimat dan catatan editorial.
- Paket kosakata, unit, kategori, dan soal yang mengikuti schema.
- Metadata bahasa seperti nama lokal, locale, dan arah teks jika jenis layout terkait sudah didukung.

Hal yang tidak bisa ditambahkan hanya dengan paket data:
- Kode Kotlin/Java baru.
- Layar, navigasi, atau alur UI baru yang belum diimplementasikan.
- Permission Android atau integrasi SDK baru.
- Aturan interaksi atau tipe latihan baru yang belum dikenali aplikasi.
- Dukungan RTL kompleks jika belum disiapkan dan diuji di kode.
- Perubahan aplikasi yang diwajibkan kebijakan platform atau perubahan SDK.

Prinsip keamanan: repository mengirim **data saja**, tidak mengirim kode yang dieksekusi.

## 3. Konsep bahasa

Pisahkan setidaknya tiga pengaturan:

| Field | Contoh | Fungsi |
|---|---|---|
| `uiLanguage` | `id` | Bahasa menu, tombol, navigasi, pesan aplikasi |
| `explanationLanguage` | `id` | Bahasa terjemahan/penjelasan materi |
| `targetLanguage` | `en` | Bahasa yang sedang dipelajari |

Pengguna bisa memakai UI Indonesia, belajar bahasa Inggris, dan kemudian memilih penjelasan Portugis. Jangan menganggap ketiga preferensi harus sama.

Gunakan tag bahasa BCP 47. Simpan locale kanonis seperti `pt-BR`; normalisasikan input saat membaca, tetapi jangan mengubah ID paket yang sudah dipublikasikan tanpa migrasi.

## 4. Struktur repository yang disarankan

```text
lexiladder-content/
├── manifest.json
├── schemas/
│   ├── manifest.schema.json
│   ├── ui-catalog.schema.json
│   ├── learning-package.schema.json
│   └── translation-package.schema.json
├── ui/
│   ├── en.json
│   └── id.json
├── learning/
│   └── en/
│       ├── A1/
│       └── A2/
├── translations/
│   ├── id/en/
│   └── pt-BR/en/
├── quizzes/
│   └── en/
├── changelog/
│   └── content-changelog.json
└── tools/
    ├── validate_content.py
    └── README.md
```

Repositori kode Android dan repositori konten sebaiknya terpisah. Ini mengurangi risiko konten publik tanpa sengaja membuka source code atau konfigurasi internal aplikasi.

## 5. Contoh manifest

Contoh konseptual; URL dan checksum harus diisi dengan nilai nyata saat paket dipublikasikan.

```json
{
  "manifestSchemaVersion": 1,
  "manifestVersion": 1,
  "appContent": {
    "fallbackUiLocale": "en",
    "targetLanguage": "en"
  },
  "packages": [
    {
      "packageId": "ui.en",
      "type": "ui-catalog",
      "locale": "en",
      "contentVersion": 1,
      "schemaVersion": 1,
      "minimumAppVersion": 1,
      "status": "published",
      "url": "https://example.invalid/ui/en.json",
      "sha256": "REPLACE_WITH_REAL_SHA256",
      "sizeBytes": 12000
    },
    {
      "packageId": "ui.id",
      "type": "ui-catalog",
      "locale": "id",
      "contentVersion": 1,
      "schemaVersion": 1,
      "minimumAppVersion": 1,
      "status": "published",
      "url": "https://example.invalid/ui/id.json",
      "sha256": "REPLACE_WITH_REAL_SHA256",
      "sizeBytes": 13000
    }
  ]
}
```

Jangan menggunakan `example.invalid` dalam rilis produksi. Gunakan URL HTTPS yang benar-benar diuji.

## 6. Contoh katalog UI

Key adalah kontrak antara aplikasi dan paket terjemahan. Paket bahasa baru menerjemahkan key yang sudah dipakai kode aplikasi.

```json
{
  "schemaVersion": 1,
  "locale": "id",
  "contentVersion": 1,
  "strings": {
    "app_name": "LexiLadder",
    "home_title": "Beranda",
    "learn_title": "Belajar",
    "practice_title": "Latihan",
    "progress_title": "Progres",
    "settings_title": "Pengaturan",
    "content_update_available": "Pembaruan konten tersedia",
    "offline_mode_label": "Anda sedang offline"
  }
}
```

Aturan:
- Jangan membuat key berdasarkan kalimat yang diterjemahkan.
- Key tidak boleh berubah hanya karena teks terjemahan diperbaiki.
- Placeholder harus identik antarbahasa, misalnya `Hello, {name}`.
- Gunakan format plural yang eksplisit jika ada hitungan.
- Tetapkan fallback jika key tidak ditemukan.
- String harus diuji untuk teks panjang, font besar, dan layout sempit.

## 7. Contoh paket terjemahan materi

```json
{
  "schemaVersion": 1,
  "packageId": "translation.id.en.a1",
  "contentVersion": 1,
  "locale": "id",
  "targetLanguage": "en",
  "levelId": "level-a1",
  "entries": [
    {
      "wordId": "en-a1-000001",
      "translation": "apel",
      "definition": "Buah yang biasanya berbentuk bulat.",
      "exampleTranslation": "Saya makan sebuah apel."
    }
  ]
}
```

Paket terjemahan harus mereferensikan `wordId` yang stabil. Teks tidak boleh menjadi primary key. Jika kata berubah atau dipindahkan antarunit, identitasnya tetap konsisten kecuali memang merupakan entri leksikal berbeda.

## 8. Contoh paket belajar

```json
{
  "schemaVersion": 1,
  "packageId": "learning.en.a1",
  "contentVersion": 1,
  "targetLanguage": "en",
  "levelId": "level-a1",
  "units": [
    {
      "unitId": "a1-greetings",
      "title": "Greetings",
      "wordIds": ["en-a1-000001"]
    }
  ],
  "words": [
    {
      "wordId": "en-a1-000001",
      "term": "apple",
      "partOfSpeech": "noun",
      "definition": "A round fruit that grows on a tree.",
      "example": "I eat an apple.",
      "audio": null,
      "tags": ["food", "beginner"]
    }
  ]
}
```

Contoh ini hanya menunjukkan bentuk data, bukan konten final yang sudah divalidasi secara editorial.

## 9. Alur publikasi

1. Editor membuat atau memperbarui konten pada branch kerja.
2. Jalankan validasi schema dan pemeriksaan referensi.
3. Periksa ID duplikat, key hilang, placeholder, soal tanpa jawaban benar, dan distraktor yang sama.
4. Review manusia untuk grammar, arti, contoh, tingkat kesulitan, hak cipta, dan terjemahan.
5. Uji paket di build internal dengan versi aplikasi yang sesuai.
6. Tandai paket `testing` dan lakukan uji unduh, import, offline, dan rollback.
7. Publikasikan file paket immutable dan perbarui manifest.
8. Catat perubahan pada changelog.
9. Pantau error unduh dan feedback pengguna; rollback manifest ke versi paket sebelumnya jika perlu.

Hindari mengganti isi file versi lama tanpa menaikkan `contentVersion`. Idealnya paket yang sudah diterbitkan bersifat immutable.

## 10. Prosedur update di aplikasi

1. Saat online dan pada kondisi yang sesuai, baca manifest dengan cache.
2. Validasi versi manifest dan batas ukuran.
3. Tampilkan bahasa/paket baru yang kompatibel.
4. Unduh hanya paket yang dipilih atau dibutuhkan.
5. Verifikasi SHA-256 dan schema.
6. Validasi semua referensi silang.
7. Import dalam transaksi atau siapkan salinan baru sebelum aktivasi.
8. Aktifkan paket hanya setelah semua pemeriksaan lolos.
9. Pertahankan paket lama sampai paket baru berhasil dipakai.
10. Catat hasil lokal yang tidak sensitif untuk debugging.

Jika jaringan gagal, aplikasi tetap menggunakan konten lokal. Update tidak boleh memblokir Home, Learn, Practice, atau review.

## 11. Database dan progres

- Gunakan ID stabil untuk `wordId`, `unitId`, `questionId`, `levelId`, dan string key.
- Simpan progres terpisah dari isi konten.
- Simpan versi paket yang dipakai pada percobaan mastery test.
- Jangan mengubah status kelulusan lama hanya karena definisi atau terjemahan diperbarui.
- Bila sebuah kata ditandai deprecated, pertahankan riwayat progres dan sediakan pemetaan jika ID diganti.
- Migrasi schema database dilakukan oleh aplikasi, bukan dengan menjalankan skrip dari repository konten.

## 12. Fallback dan UX bahasa

- Default awal dapat memakai bahasa perangkat jika paket yang cocok tersedia; jika tidak, gunakan English.
- Pengguna harus bisa memilih UI language dan explanation language secara terpisah.
- Bahasa baru yang terdeteksi dari manifest tidak otomatis mengubah bahasa pengguna; tampilkan pilihan yang jelas.
- Jangan menghapus bahasa yang sedang aktif sebelum paket baru tervalidasi.
- Jika paket UI hilang/rusak, gunakan katalog fallback yang sudah dibundel.
- Pertahankan paket bahasa yang sudah terpasang untuk penggunaan offline.
- Aplikasi harus tetap bisa dipakai jika GitHub tidak tersedia sementara.

## 13. Kualitas, hak cipta, dan privasi

- Catat sumber dan lisensi materi.
- Jangan menyalin kamus atau dataset komersial tanpa izin.
- Terjemahan mesin harus ditinjau manusia sebelum publikasi.
- Jangan menyimpan data pribadi pengguna di GitHub.
- Jangan menganggap konten di repository publik sebagai materi rahasia.
- Jangan mengirim token GitHub di APK.
- Jangan mengeksekusi script, HTML, atau kode yang diambil dari konten.
- Jangan mengumpulkan bahasa pilihan atau analitik yang tidak diperlukan; jelaskan pengolahan data di kebijakan privasi.

## 14. Acceptance criteria

Sistem konten dapat dianggap siap setelah:
- [ ] Paket konten awal tersedia di instalasi baru dan dapat digunakan offline.
- [ ] Bahasa baru yang hanya menerjemahkan key yang sudah didukung dapat ditambahkan melalui GitHub tanpa APK update.
- [ ] Manifest dan paket diverifikasi.
- [ ] Paket invalid ditolak dan paket lama tetap berfungsi.
- [ ] Perubahan bahasa tidak menghapus progres.
- [ ] Bahasa yang belum terpasang tidak diklaim bisa digunakan offline.
- [ ] Paket yang membutuhkan versi aplikasi lebih tinggi ditolak dengan pesan yang jelas.
- [ ] Tidak ada kode executable yang diunduh.
- [ ] Proses rollback telah diuji.
- [ ] Dokumen ini diperbarui jika format atau kontrak data berubah.
