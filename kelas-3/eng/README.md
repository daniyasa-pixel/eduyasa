# Bahasa Inggris Kelas 3 — EDUYASA

**Target terbaru per unit:** 2 modul, 35 soal teks, 10 soal bergambar, 5 soal listening. Total 10 unit: **20 modul dan 500 soal**.

- Unit 1: I Like Mi Aceh
- Unit 2: I Don't Like Rice
- Unit 3: I Have Fried Chicken for Breakfast
- Unit 4: Do You Like Swimming?
- Unit 5: I Like Riding a Bike on Sunday
- Unit 6: Is It the Canteen?
- Unit 7: My Class Is Behind the Office
- Unit 8: I Drink Orange Juice in the Canteen
- Unit 9: My Classroom Is Clean
- Unit 10: There Are Twenty Books on the Shelf

**STATUS: DRAF BELUM FINAL.** Semua unit memiliki berkas Markdown dan SVG pendukung. **Audio menggunakan Web Speech API**, tanpa berkas MP3. Berkas listening sudah memuat data TTS, tetapi integrasi ke halaman kuis dan uji perangkat belum selesai. Unit 1–7 telah diaudit terhadap buku siswa dan panduan guru serta memakai stimulus fungsional. Unit 8–10 masih menunggu audit isi dan ilustrasi. Jangan menganggap paket siap rilis.

**ID:** `K03-ENG-B##-MOD-01/02`; soal `K03-ENG-B##-M01-001`–`050`. Soal 1–35 teks, 36–45 bergambar, 46–50 listening.

## Checkpoint JSON — 12 November 2026

**Seluruh 10 unit telah dikonversi** ke `eng-bNN.json` (35 teks), `eng-bNNa.json` (10 bergambar), `eng-bNNl.json` (5 listening), dan `eng-bNN-materi.json` (dua modul), total **500 soal, 20 modul, 40 file JSON**. Manifest `question-packs.json` mencantumkan tiga bank per unit. Pemeriksaan data Markdown ↔ JSON per-ID menyatakan 0 mismatch; transkrip produksi tidak ditampilkan sebagai stem, audio `type/lang/text/rate` dipertahankan, modul hanya mengubah tautan gambar relatif menjadi URL GitHub raw. Instruksi historis `DRAF BELUM FINAL` di atas tidak menghalangi konversi sesuai keputusan terbaru pengguna. **Belum diaktifkan dalam menu Kelas 3** dan Web Speech API belum diuji di HP/WebView; pemuatan file listening baru masih perlu penyesuaian aplikasi apabila loader tidak mendukung bank terpisah.
