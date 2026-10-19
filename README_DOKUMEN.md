# LexiLadder — Paket Dokumen Proyek

Dokumen perencanaan untuk VS Code + Antigravity.

## Isi
- `rancangan.md` — master plan revisi; sumber utama keputusan produk.
- `progress.md` — progress tracker revisi; semua status implementasi tetap belum selesai sampai dibuktikan.
- `content_system.md` — spesifikasi sistem konten dan penambahan bahasa via GitHub.
- `AGENTS.md` — instruksi kerja untuk coding agent.

## Cara memakai
1. Unduh dan ekstrak paket.
2. Salin empat file Markdown ke root repository aplikasi Android.
3. Periksa status Git dan backup perubahan yang sudah ada sebelum menimpa file lama.
4. Minta Antigravity membaca `AGENTS.md`, `rancangan.md`, `progress.md`, dan `content_system.md`.
5. Mulai dari Phase 0 di `progress.md`; jangan menganggap fitur sudah terimplementasi hanya karena rancangan tertulis.

## Catatan penting
- Nama `LexiLadder` masih nama kerja sampai pemeriksaan ketersediaan selesai.
- Menambahkan bahasa melalui GitHub tanpa update APK memungkinkan jika aplikasi sejak awal memakai katalog UI berbasis data dan paket bahasa sesuai schema. Fitur baru atau perubahan kode tetap membutuhkan update aplikasi.
- URL dan checksum contoh dalam spesifikasi bukan URL/hash produksi.
