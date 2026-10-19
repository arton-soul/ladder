# AGENTS.md — Petunjuk Coding Agent untuk LexiLadder

## 1. Urutan membaca

Sebelum mengubah kode:
1. Baca `rancangan.md` untuk visi, scope, arsitektur, dan keputusan produk.
2. Baca `progress.md` untuk fase aktif, status aktual, dan acceptance criteria.
3. Baca `content_system.md` sebelum mengubah schema, manifest, terjemahan, atau proses update konten.
4. Periksa kode yang benar-benar ada di repository. Jangan menganggap dokumentasi berarti fitur telah dibuat.

## 2. Aturan kerja

- Kerjakan satu fase atau satu task kecil pada satu waktu.
- Sebelum mengedit, ringkas file yang akan diubah dan tujuan perubahan.
- Jangan menandai task selesai sebelum build/test dan acceptance criteria relevan diperiksa.
- Jangan membuat klaim bahwa build, test, emulator, Play Console, atau GitHub berhasil jika tidak benar-benar dijalankan.
- Jika ada keputusan yang bertentangan, jangan memilih diam-diam. Catat konflik dan minta persetujuan.
- Pertahankan perubahan pengguna yang tidak terkait.
- Jangan mengganti package name, application ID, skema data, nama file paket terpublikasi, atau ID konten stabil tanpa rencana migrasi.
- Perbarui `progress.md` setelah pekerjaan nyata selesai, dengan bukti seperti perintah dan hasil test.
- Jika dokumentasi berubah, perbarui dokumen yang relevan secara konsisten.

## 3. Prinsip arsitektur yang tidak boleh dilanggar

- Kotlin dan Jetpack Compose mengikuti pola arsitektur yang sudah dipilih dalam `rancangan.md`.
- Offline-first: fitur belajar inti tidak boleh memerlukan jaringan.
- Data konten yang diunduh adalah data tidak tepercaya; validasi schema, checksum, ukuran, ID, dan referensi.
- Jangan pernah mengunduh atau mengeksekusi kode dari repository konten.
- Progres belajar dipisahkan dari konten dan tidak boleh hilang karena update terjemahan atau paket.
- Gunakan ID stabil; jangan gunakan teks terjemahan sebagai primary key.
- Update konten harus atomik atau memakai strategi staging dan aktivasi agar paket lama tetap aman.
- UI language, explanation language, dan target language adalah pengaturan terpisah.
- Bahasa UI berbasis katalog data memungkinkan bahasa baru ditambahkan tanpa APK hanya untuk key yang sudah didukung aplikasi. Fitur, layar, logic, permission, dan SDK baru tetap membutuhkan update aplikasi.
- Paket dasar dan fallback harus tersedia offline.
- Jangan menyimpan token/rahasia di source code atau repository publik.
- Jangan mengandalkan repository GitHub publik untuk melindungi konten premium.

## 4. Kualitas dan keamanan

- Jangan memasukkan data pengguna nyata ke file contoh atau repository.
- Jangan menambahkan dependency baru tanpa menjelaskan alasan dan memeriksa kompatibilitas.
- Hindari TODO/placeholder yang terlihat oleh pengguna di fitur yang diklaim selesai.
- Tambahkan unit test untuk aturan domain dan validasi konten.
- Uji error state, mode pesawat, unduhan gagal, checksum salah, schema tidak cocok, dan rollback.
- Gunakan HTTPS untuk pengunduhan.
- Jangan mengubah kebijakan privasi atau perilaku iklan tanpa meninjau dampak kepatuhan.
- Jangan menaruh iklan yang mengganggu kuis atau ujian.

## 5. Workflow task

Untuk setiap task:
1. Identifikasi fase dan acceptance criteria di `progress.md`.
2. Periksa file dan implementasi terkait.
3. Ajukan rencana singkat jika task menyentuh banyak file atau kontrak data.
4. Implementasikan perubahan terkecil yang menyelesaikan task.
5. Jalankan build/test yang relevan jika environment mendukung.
6. Laporkan file berubah, hasil perintah, test yang lulus/gagal, dan blocker.
7. Perbarui `progress.md` hanya berdasarkan bukti.
8. Jangan memulai fase berikutnya jika ada blocker atau keputusan arsitektur belum disetujui.

## 6. Laporan akhir setiap task

Gunakan format:
- **Selesai:** ringkasan implementasi nyata.
- **File berubah:** daftar file.
- **Verifikasi:** perintah build/test dan hasil aktual.
- **Belum selesai:** item atau keterbatasan.
- **Progress tracker:** bagian yang diperbarui.
- **Langkah berikutnya:** satu task berikut yang paling tepat.

## 7. Scope awal

Mulai dari Phase 0 dalam `progress.md`. Jangan langsung membangun semua fitur sekaligus. Pertama periksa repository aktual, tetapkan package name dan konfigurasi Android, lalu finalisasi skema konten dan contoh validasi.
