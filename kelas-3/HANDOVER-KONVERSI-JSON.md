# Handover Codex — konversi JSON EDUYASA Kelas 3

Repositori [daniyasa-pixel/Eduyasa](https://github.com/daniyasa-pixel/eduyasa), branch `main`. Inventaris per 12 November 2026 berdasarkan audit isi sampai Bahasa Inggris Unit 5. Baca [catatan progres](PROGRES-REVISI-1945.md) dan [laporan audit](AUDIT-5-MAPEL-2026-11-11.md) di `main` sebelum mulai. Gunakan versi Markdown terbaru dan catat SHA sumber yang diperiksa.

## Paket yang dapat diambil Codex

Lakukan **satu audit ulang per bab atau unit sebelum konversi**. Audit terdahulu sudah memeriksa paket dalam tabel ini. Codex boleh langsung membetulkan temuan pada sumber Markdown dan aset terkait, memeriksa kembali bagian yang berubah, lalu mengonversi paket tersebut setelah lolos.

| Paket | Soal per bab/unit | Jumlah diaudit | Modul | Sumber |
| --- | --- | ---: | ---: | --- |
| IPAS Bab 1–8 | 001–050 teks, 051–070 bergambar | 560 | 16 | [`ipas/`](ipas/README.md) |
| PAI Bab 1–10 | 001–030 | 300 | 20 | [`pai/`](pai/README.md) |
| Bahasa Inggris Unit 1–5 | 001–035 teks, 036–045 bergambar, 046–050 listening | 250 | 10 | [`eng/`](eng/README.md) |
| **Total pada checkpoint** | 23 bab atau unit | **1.110** | **46** | [Rincian per bab](PROGRES-REVISI-1945.md) |

- **IPAS.** Ambil `ipas-bNN-soal-biasa.md`, `ipas-bNN-soal-bergambar.md`, dan `ipas-bNN-materi.md`. Audit isi dan ilustrasi selesai untuk delapan bab. Pertahankan notasi LaTeX dan periksa kembali setelah JSON diparse.
- **PAI.** Ambil `pai-bNN-soal.md` dan `pai-bNN-materi.md`. Audit mencakup dalil, tajwid, dan konsistensi kunci. Pertahankan teks Arab, harakat, dan arah baca, khususnya Bab 1, 6, dan 8. Uji tampilan pada aplikasi masih tertunda.
- **Bahasa Inggris.** Ambil `eng-bNN-soal-biasa.md`, `eng-bNN-soal-bergambar.md`, `eng-bNN-soal-listening.md`, dan `eng-bNN-materi.md`. Pertahankan metadata `Audio TTS` (`type`, `lang`, `text`, `rate`). Transkrip produksi dipakai saat audit. Soal listening harus bisa menyembunyikan transkrip dari siswa. Konfirmasi format pemuatan listening dengan kode aplikasi sebelum menetapkan penamaan file atau bentuk metadata baru. Uji pemutaran di HP dan WebView masih tertunda.

## Audit ulang, revisi, lalu konversi

1. Cocokkan judul, edisi buku, cakupan, dan dua versi modul. Periksa setiap soal untuk memastikan empat opsi berbeda, tepat satu jawaban benar, kunci identik dengan salah satu opsi, pembahasan menjelaskan alasan, serta gambar atau audio sesuai dengan pertanyaan. Cek jumlah dan ID terhadap sumber.
2. Jika ada temuan, **revisi Markdown dan media yang terkait**, pertahankan ID dan jumlah yang sah, lalu uji ulang bagian yang diperbaiki. Catat ID soal atau modul yang diubah beserta alasannya. Konversi dari sumber hasil revisi. Jika ada ambiguitas yang belum dapat diputuskan dari rujukan, catat dan tahan paket yang terdampak sambil melanjutkan paket lain.
3. Ikuti [panduan konten](../docs/CONTENT_GUIDE.md) dan contoh Kelas 3 yang sudah berfungsi. Bank soal memakai `subject_id`, `subject_name`, `grade`, `curriculum`, dan `chapters[]` dengan `chapter_id`, `chapter_title`, dan `questions[]`. Setiap soal memuat `id`, `type` (`PG`), `text`, `options`, `answer`, dan `discussion`, beserta media yang didukung. Simpan opsi tanpa huruf A–D sesuai urutan Markdown. Pertahankan ID `K03-<MAPEL>-B##-M01-###` dan `K03-<MAPEL>-B##-MOD-01/02`.
4. Gunakan pola `<mapel>-bNN.json`, `<mapel>-bNNa.json` untuk paket bergambar, dan `<mapel>-bNN-materi.json` untuk dua versi modul. Modul memakai metadata mapel/kelas/kurikulum, `chapter_id`, `chapter_title`, dan `modules[]` dengan `id`, `version`, `title`, dan `content`. Pertahankan seluruh materi dan gunakan struktur isi yang didukung aplikasi. [Bank teks](bin/bin-b01.json) · [Bank gambar](bin/bin-b01a.json) · [Modul](bin/bin-b01-materi.json) · [Contoh manifest](bin/question-packs.json)
5. Parse setiap JSON dan bandingkan jumlah, ID, urutan opsi, kunci, pembahasan, dua modul, serta media dengan Markdown terakhir. Jalankan [validator sumber](../scripts/check-k03-question-sources.py). Catat file hasil, jumlah per paket, SHA sumber, dan pengujian yang masih tertunda. Aktivasi paket di aplikasi mengikuti tahap integrasi.

## Audit sumber yang masih berjalan

Pada checkpoint ini, sisa audit isi **835 soal** di Bahasa Inggris Unit 6–10 (250), PJOK Unit 1–8 (360), dan Seni Rupa Bab 1–5 (225). Gunakan catatan progres untuk mengambil tambahan bab atau unit setelah audit isi tercatat selesai. Bahasa Indonesia, Pendidikan Pancasila, dan Matematika sudah tersedia sebagai JSON di Kelas 3.
