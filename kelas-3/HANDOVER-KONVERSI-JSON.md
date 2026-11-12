# HANDOVER CODEX — ATURAN TERBARU (12 NOVEMBER 2026)

**Prioritas tertinggi: instruksi pengguna terbaru menggantikan prosedur audit isi pada dokumen lama.** Codex bertugas **mengonversi sumber Markdown EDUYASA Kelas 3 menjadi JSON dan melakukan AUDIT HASIL KONVERSI JSON**, **BUKAN** mengaudit ulang atau menulis ulang mutu pedagogis/akademik soal dan modul. Tidak perlu audit kecocokan buku, ulang soal, penilaian distraktor, atau revisi isi sumber sebelum konversi. Ambil Markdown versi terakhir di branch `main` apa adanya. Jika sumber berubah saat pekerjaan, baca SHA terbaru dan konversi ulang paket terdampak.

## Apa saja yang WAJIB diaudit setelah konversi

1. **Kelengkapan & jumlah:** bandingkan setiap ID Markdown vs JSON satu per satu. Target paket: IPAS 8 x 70 = 560; PAI 10 x 30 = 300; Inggris 10 x 50 = 500; PJOK 8 x 45 = 360; Seni Rupa/SBD 5 x 45 = 225. Total **1.945 soal**. Kecocokan jumlah saja tidak cukup: **himpunan ID wajib identik**, tak boleh nomor hilang, ganda, bertambah, bergeser atau teracak.
2. **Urutan nomor:** urutan soal di JSON mengikuti urutan Markdown secara persis, bukan sort alfabet/string yang merusak urutan angka. Jangan mengacak urutan bank sewaktu konversi. Jika aplikasi membutuhkan pengacakan tampilan soal atau pilihan, lakukan **hanya pada runtime**, bukan dengan mengubah susunan data kanonis atau ID. Audit nomor 001 hingga nomor terakhir setiap bab, termasuk file teks dan gambar (contoh 001–035 lalu 036–045, listening 046–050).
3. **Kesetiaan isi antarsumber:** bandingkan teks pertanyaan, pilihan jawaban (tepat empat, **urutannya persis dan tanpa awalan A/B/C/D**), kunci jawaban berupa **teks lengkap dan persis sama** salah satu opsi, serta pembahasan. Tidak boleh dipotong, dirangkum, diparafrase, salah kutip, atau tertukar antar-ID; pemeriksaan ini adalah **audit kesetiaan transfer**, bukan audit apakah isi pelajaran benar.
4. **Metadata dan tipe:** `subject_id`, `subject_name`, `grade=3`, `curriculum`, `chapter_id`, `chapter_title`, `type=PG`, jalur media. Pertahankan ID `K03-<MAPEL>-B##-M01-###`; modul `K03-<MAPEL>-B##-MOD-01/02`. Jangan mengganti kode `SBD` menjadi `SRP` atau `PJK` menjadi `PJOK` pada ID sumber. Jangan salah tempatkan bab atau mencampur kelas 3 dengan kelas 6.
5. **Media:** referensi gambar harus menunjuk aset yang benar dan dapat diakses; metadata TTS Inggris `type/lang/text/rate` tidak berubah; transkrip produksi tidak tampil kepada siswa. Jagalah teks Arab, harakat, karakter khusus dan LaTeX agar tidak rusak oleh JSON escaping. Bila nama path harus diubah untuk aplikasi, buat pemetaan yang dapat diuji satu-ke-satu.
6. **Modul:** jumlah dan urutan dua versi MOD-01/MOD-02 per bab yang memang tersedia, judul, teks utuh termasuk Markdown/LaTeX dan gambar harus sama dengan Markdown. **PJOK saat ini tidak memiliki modul**; jangan mengarang, jangan klaim selesai, dan catat sebagai keterbatasan sumber.
7. **Teknis:** semua JSON dapat di-parse, sesuai skema aplikasi, manifest mengarah ke file benar, path aset valid, dan bank dapat dimuat. Audit otomatis difokuskan pada perbandingan Markdown–JSON per-ID dan per-kolom. Tampilkan laporan PASS/FAIL dengan daftar **path + ID + field** yang berbeda, jumlah expected/actual, dan SHA asal; lalu **perbaiki JSON/konverter** dan jalankan kembali audit sampai lulus.

**Aturan penanganan masalah:** Jika JSON tidak cocok dengan sumber, perbaiki proses konversi atau JSON—**bukan mengubah isi Markdown** agar cocok dengan JSON. Jika sumber Markdown ternyata ambigu secara sintaks sehingga tidak dapat dipetakan dengan pasti, laporkan ID/path sebagai `SOURCE_BLOCKER`, jangan menebak atau diam-diam mengubah soal. Kesalahan akademik yang tidak berkaitan dengan transfer **di luar ruang lingkup tugas Codex ini**. Jangan memblokir konversi karena status audit isi dari handover lama; cukup catat batasan tersebut terpisah. Lakukan pekerjaan dan lapor setelah semua paket yang sumbernya ada terkonversi serta audit JSON lulus. Jangan mengklaim seluruh aplikasi siap rilis hanya karena JSON lulus.

## Format dan rute output

Gunakan JSON soal berstruktur `subject_id`, `subject_name`, `grade`, `curriculum`, `chapters[]` dengan setiap soal `id`, `type`, `text`, `options`, `answer`, `discussion`. Gunakan nama `<kode>-bNN.json` (teks), `<kode>-bNNa.json` (bergambar), `<kode>-bNN-materi.json` (modul). Untuk sumber Inggris, file listening terpisah atau metadata khusus yang kompatibel aplikasi boleh digunakan, **asalkan** seluruh 5 listening/Unit utuh dan tercatat manifestnya. Gunakan pola yang memang dipakai oleh aplikasi—jangan mengubah skema secara spekulatif. Setelah konversi, perbarui status paket, manifest dan dokumentasi sesuai kesiapan teknisnya.

---

## ARSIP HANDOVER LAMA — aturan audit konten sebelum konversi di bawah ini SUDAH DIGANTIKAN

# Handover Codex — konversi JSON EDUYASA Kelas 3

Repositori [daniyasa-pixel/Eduyasa](https://github.com/daniyasa-pixel/eduyasa), branch `main`. Inventaris per 12 November 2026 berdasarkan audit isi sampai Bahasa Inggris Unit 5. Baca [catatan progres](PROGRES-REVISI-1945.md) dan [laporan audit](AUDIT-5-MAPEL-2026-11-11.md) di `main` sebelum mulai. Gunakan versi Markdown terbaru dan catat SHA sumber yang diperiksa.

## Paket yang dapat diambil Codex

Lakukan **satu audit ulang per bab atau unit sebelum konversi**. Audit terdahulu sudah memeriksa paket dalam tabel ini. Codex boleh langsung membetulkan temuan pada sumber Markdown dan aset terkait, memeriksa kembali bagian yang berubah, lalu mengonversi paket tersebut setelah lolos.

| Paket | Soal per bab/unit | Jumlah diaudit | Modul | Sumber |
| --- | --- | ---: | ---: | --- |
| IPAS Bab 1–8 | 001–050 teks, 051–070 bergambar | 560 | 16 | [`ipas/`](ipas/README.md) |
| PAI Bab 1–10 | 001–030 | 300 | 20 | [`pai/`](pai/README.md) |
| Bahasa Inggris Unit 1–6 | 001–035 teks, 036–045 bergambar, 046–050 listening | 300 | 12 | [`eng/`](eng/README.md) |
| **Total pada checkpoint** | 24 bab atau unit | **1.160** | **48** | [Rincian per bab](PROGRES-REVISI-1945.md) |

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

Pada checkpoint ini, sisa audit isi **785 soal** yang belum lulus audit final: Bahasa Inggris Unit 7–10 (200), PJOK Unit 1–8 (360), dan Seni Rupa Bab 1–5 (225). Sumber PJOK sudah direvisi, tetapi masih memerlukan audit final buku, keselamatan, modul, dan media. Gunakan catatan progres untuk mengambil tambahan bab atau unit setelah audit isi tercatat selesai. Bahasa Indonesia, Pendidikan Pancasila, dan Matematika sudah tersedia sebagai JSON di Kelas 3.
