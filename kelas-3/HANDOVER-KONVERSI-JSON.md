# HANDOVER CODEX — KONVERSI JSON EDUYASA KELAS 3

**Keputusan terbaru pengguna — 12 November 2026 (menggantikan instruksi audit isi sebelumnya):** Mulai sekarang tugas Codex adalah **konversi sumber Markdown ke JSON**, lalu **AUDIT HASIL JSON TERHADAP SUMBER MARKDOWN**, **BUKAN audit ulang isi pelajaran/akademik**. Jangan mengulang pemeriksaan kesesuaian buku, mengevaluasi kualitas materi, menulis ulang soal, menyunting pembahasan, atau meminta persetujuan audit konten sebelum konversi. Sumber Markdown terakhir pada `main` adalah **source of truth**. Tidak ada izin mengacak atau menomori ulang konten sumber.

**Repository:** https://github.com/daniyasa-pixel/eduyasa — branch `main`; kerja Kelas 3 seluruhnya di `kelas-3/`.

## Paket sumber untuk dikonversi

| Mata pelajaran | Folder | Paket | Jumlah soal | Pola nomor per bab |
| --- | --- | --- | ---: | --- |
| IPAS | `kelas-3/ipas/` | Bab 1–8 | 560 | 001–050 teks, 051–070 gambar |
| PAI | `kelas-3/pai/` | Bab 1–10 | 300 | 001–030 |
| Bahasa Inggris | `kelas-3/eng/` | Unit 1–10 | 500 | 001–035 teks, 036–045 gambar, 046–050 listening |
| PJOK | `kelas-3/pjk/` | Unit 1–8 | 360 | 001–035 teks, 036–045 gambar |
| Seni Rupa (SBD) | `kelas-3/sbd/` | Bab 1–5 | 225 | 001–035 teks, 036–045 gambar |
| **TOTAL** | | **41 bab/unit** | **1.945** | |

Semua **225 soal Seni Rupa Bab 1–5** sudah diperbarui dalam sumber Markdown pada 12 November, berikut **50 SVG soal** pengganti generik. Seluruh sumber soal lima mapel telah melewati tahap produksi/revisi sebelumnya, tetapi **status tersebut bukan jaminan audit akademik final atau kesiapan rilis**. Codex tidak perlu menyelesaikan audit akademik untuk mulai konversi.

### Berkas sumber

- IPAS: `ipas-bNN-soal-biasa.md`, `ipas-bNN-soal-bergambar.md`, `ipas-bNN-materi.md`.
- PAI: `pai-bNN-soal.md`, `pai-bNN-materi.md`.
- Bahasa Inggris: `eng-bNN-soal-biasa.md`, `eng-bNN-soal-bergambar.md`, `eng-bNN-soal-listening.md`, `eng-bNN-materi.md`.
- PJOK: `pjk-bNN-soal-biasa.md`, `pjk-bNN-soal-bergambar.md`; **modul PJOK belum tersedia**. Jangan ciptakan modul otomatis sebagai bagian dari konversi; laporkan `NOT_AVAILABLE_IN_SOURCE`.
- Seni Rupa: `sbd-bNN-soal-biasa.md`, `sbd-bNN-soal-bergambar.md`, `sbd-bNN-materi.md` (dua modul per bab).
- Aset SVG berada pada folder `assets/` masing-masing mapel. Jangan mengganti gambar atau nama ID ketika mengonversi.

### Bentuk JSON

Ikuti format aplikasi EDUYASA yang sudah berfungsi di `kelas-3/bin/`:
- Bank soal: top-level `subject_id`, `subject_name`, `grade`, `curriculum`, `chapters[]`. Per bab: `chapter_id`, `chapter_title`, `questions[]`. Per soal: `id`, `type` (PG), `text`, `options` (array 4 teks **tanpa awalan A/B/C/D**), `answer` (**teks penuh**, sama persis dengan salah satu item `options`), `discussion`, dan media yang didukung.
- Modul: top-level metadata mapel/kelas/kurikulum, `chapter_id`, `chapter_title`, `modules[]`; dua versi MOD-01/02 jika tersedia; masing-masing `id`, `version`, `title`, `content`. Konten modul harus **utuh**, tidak disingkat.
- Gunakan pola keluaran kanonis `<kode>-bNN.json` (bank biasa), `<kode>-bNNa.json` (bank bergambar), `<kode>-bNN-materi.json` (modul). Khusus Inggris, 5 listening tiap unit wajib disimpan sesuai dukungan aplikasi/manifest; bila perlu file listening tambahan, dokumentasikan tanpa kehilangan satupun soal. Lihat pola contoh `kelas-3/bin/bin-b01.json`, `bin-b01a.json`, `bin-b01-materi.json` dan manifest `bin/question-packs.json`.
- **ID tidak boleh diubah**: `K03-<KODE>-B##-M01-###`, modul `K03-<KODE>-B##-MOD-01/02`. Kode yang benar: IPAS, PAI, ENG, PJK, SBD. Jangan ubah PJK menjadi PJOK atau SBD menjadi SEN. Jangan salah kelas.

## Satu-satunya audit Codex yang diminta: audit integritas JSON pascakonversi

Setelah mengonversi **setiap** paket, tulis atau jalankan pemeriksaan otomatis:

1. **Parse dan schema JSON:** semua JSON dapat dibaca oleh parser, field wajib benar, `grade=3`, jenis data sesuai, dan format sesuai frontend/manifest.
2. **Hitung sumber vs output:** jumlah per file, per bab, per jenis, dan total. Seluruh 1.945 ID harus ada **tepat satu kali**. Jangan hanya cocokkan jumlah: bandingkan **array ID lengkap** dari sumber Markdown dengan output.
3. **Urutan tidak boleh diacak:** bandingkan urutan ID persis, contoh `001,002,...,035`, lalu `036–045`, listening `046–050`. `questions[i].id` harus sama dengan ID urutan sumber, tanpa sort/string-order yang keliru. **Tidak boleh mengacak pertanyaan atau opsi di JSON.** Randomisasi untuk siswa hanya ketika kuis berjalan (runtime), tidak mengubah data kanonis. Jika frontend mengacak pilihan di runtime, kunci tetap harus merujuk teks opsi yang benar.
4. **Audit field per-ID:** bandingkan teks pertanyaan, seluruh 4 opsi secara berurutan, teks kunci utuh, pembahasan, tipe, dan metadata. Deteksi pertanyaan yang pindah ke ID salah, isi tertukar, teks terpotong, spasi/karakter penting hilang, atau pilihan tersusun ulang.
5. **Media:** path gambar di JSON ada dan mengarah ke SVG yang tepat; metadata listening Inggris `type/lang/text/rate` sama persis; transkrip produksi tidak terpapar di tampilan siswa. Pertahankan harakat Arab dan string LaTeX/escape aslinya. Periksa path dan manifest terhadap basis lokasi yang benar, tanpa mengubah gambar sumber.
6. **Modul:** bandingkan ID, urutan MOD-01/MOD-02, judul dan seluruh `content` serta rujukan gambar terhadap Markdown. Laporkan modul PJOK yang belum tersedia sebagai kekurangan sumber, bukan kesalahan konversi.
7. **Laporan dan perbaikan:** catat `PASS/FAIL` masing-masing paket, jumlah expected/actual, SHA sumber, file output, daftar perbedaan `path + ID + nama field + expected + actual`. **Perbaiki JSON atau script konversi**, lalu ulang tes sampai cocok. Jangan menyunting isi Markdown untuk menyesuaikan JSON.

Jika Markdown tidak dapat diparse dengan pasti, laporkan `SOURCE_BLOCKER` dengan path/ID dan penyebabnya, **jangan menebak atau mengarang**. Pengecekan apakah jawaban benar secara akademik, soal terlalu repetitif, teori tepat, dll. **bukan tugas Codex pada tahap ini**. Jangan menahan konversi karena catatan audit isi lama.

## Urutan pelaksanaan yang disarankan

1. Tarik `main` terbaru, baca `kelas-3/PROGRES-REVISI-1945.md` dan ambil SHA seluruh sumber agar perubahan paralel terdeteksi.
2. Konversi per mapel/bab, gunakan parser yang menjaga ID, urutan dan semua konten 1:1; jangan rekonstruksi soal dari pengetahuan model.
3. Audit JSON secara otomatis tepat setelah membuatnya; fix mismatch JSON/konverter hingga PASS, tanpa mengubah Markdown.
4. Perbarui manifest dan konfigurasi pemuatan kelas 3 hanya untuk paket yang lulus integritas dan kompatibilitas frontend; jangan menyentuh paket Kelas 6.
5. Tambahkan laporan konversi `kelas-3/LAPORAN-AUDIT-JSON.md` (atau nama sepadan) berisi status seluruh 41 paket dan setiap mismatch yang ditemukan/diselesaikan; update checkpoint GitHub.
6. Laporkan jumlah paket berhasil, jumlah file JSON, jumlah ID, hasil pengecekan urutan/opsi, kendala sumber, serta pengujian aplikasi yang belum dilakukan.

**Definisi selesai tugas Codex ini:** sumber tersedia telah dikonversi tanpa kehilangan/mengganti data dan **audit integritas Markdown↔JSON lulus**. Ini **bukan** pernyataan bahwa konten telah dinilai secara akademik atau aplikasi sudah diuji di HP/WebView.

## Riwayat

Aturan sebelumnya meminta audit isi sebelum JSON; aturan itu **dicabut atas instruksi pengguna pada 12 November 2026**. Gunakan ketentuan terbaru dalam dokumen ini. Catatan revisi akademik terdahulu tetap ada pada progres sebagai riwayat, bukan prasyarat Codex saat ini.


## Perubahan sumber setelah konversi — 13 November 2026

Bahasa Inggris Unit 8 direvisi setelah konversi 12 November. Empat JSON Unit 8
berstatus **STALE_SOURCE_CHANGED**. Regenerasi dari Markdown `main` terbaru,
bandingkan ulang seluruh 50 ID, urutan opsi, TTS, media, dan dua modul. Jangan
menyalin kembali isi JSON lama ke Markdown.


## Perubahan sumber Unit 9 — 13 November 2026

Bahasa Inggris Unit 9 direvisi setelah konversi. Empat JSON Unit 9 berstatus
**STALE_SOURCE_CHANGED**. Regenerasi dari Markdown `main` terbaru dan audit
ulang 50 ID, opsi, TTS, media, serta dua modul.


## Perubahan sumber Unit 10 — 13 November 2026

Bahasa Inggris Unit 10 direvisi setelah konversi. Empat JSON Unit 10 berstatus
**STALE_SOURCE_CHANGED**. Audit ulang isi Markdown sudah dilakukan; pada tahap
JSON, regenerasi dari `main` terbaru lalu bandingkan 50 ID, opsi, metadata TTS,
sepuluh media, dan dua modul. Jika Markdown kembali berubah, audit perubahan itu
satu kali sebelum konversi; revisi sumber yang diperlukan boleh dilakukan lalu
konversi ulang.


## Perubahan sumber PJOK Unit 1 — 13 November 2026

Audit isi Unit 1 selesai. Markdown gambar berubah dan file materi dua versi baru
ditambahkan. Regenerasi `pjk-b01a.json`, buat `pjk-b01-materi.json`, lalu
audit keduanya terhadap sumber terbaru. `pjk-b01.json` teks tidak berubah dan
tidak perlu dibuat ulang kecuali SHA sumbernya kemudian berubah. Jika ada
perubahan baru, audit perubahan itu satu kali sebelum konversi.


## Perubahan sumber PJOK Unit 2 — 13 November 2026

Seluruh 45 soal Unit 2, 10 SVG, dan dua modul berubah/baru setelah konversi.
Regenerasi `pjk-b02.json`, `pjk-b02a.json`, buat
`pjk-b02-materi.json`, lalu audit per-ID, opsi, media, dan konten dua modul
terhadap Markdown `main` terbaru. Jika sumber berubah lagi, audit perubahan
itu satu kali sebelum konversi.


## Perubahan sumber PJOK Unit 3 — 13 November 2026

Seluruh 45 soal Unit 3, 10 SVG, dan dua modul berubah/baru setelah konversi. Regenerasi `pjk-b03.json`, `pjk-b03a.json`, buat `pjk-b03-materi.json`, lalu audit per-ID, opsi, media, dan konten dua modul terhadap Markdown `main` terbaru. Jika sumber berubah lagi, audit perubahan itu satu kali sebelum konversi; bila audit menemukan revisi yang memang diperlukan, revisi Markdown terlebih dahulu lalu konversi ulang.


## Perubahan sumber PJOK Unit 4 — 13 November 2026

Seluruh 45 soal Unit 4, 10 SVG, dan dua modul berubah/baru setelah konversi. Regenerasi `pjk-b04.json`, `pjk-b04a.json`, buat `pjk-b04-materi.json`, lalu audit per-ID, opsi, media, dan konten dua modul terhadap Markdown `main` terbaru. Jika sumber berubah lagi, audit perubahan itu satu kali sebelum konversi; bila audit menemukan revisi yang diperlukan, revisi Markdown terlebih dahulu lalu konversi ulang.


## Perubahan sumber PJOK Unit 5 — 13 November 2026

Seluruh 45 soal Unit 5, 10 SVG, dan dua modul berubah/baru setelah konversi. Regenerasi `pjk-b05.json`, `pjk-b05a.json`, buat `pjk-b05-materi.json`, lalu audit per-ID, opsi, media, dan konten dua modul terhadap Markdown `main` terbaru. Jika sumber berubah lagi, audit perubahan itu satu kali sebelum konversi; bila audit menemukan revisi yang diperlukan, revisi Markdown terlebih dahulu lalu konversi ulang.


## Perubahan sumber PJOK Unit 6 — 13 November 2026

Seluruh 45 soal Unit 6, 10 SVG, dan dua modul berubah/baru setelah konversi. Regenerasi `pjk-b06.json`, `pjk-b06a.json`, buat `pjk-b06-materi.json`, lalu audit per-ID, opsi, media, dan konten dua modul terhadap Markdown `main` terbaru. Jika sumber berubah lagi, audit perubahan itu satu kali sebelum konversi; bila audit menemukan revisi yang diperlukan, revisi Markdown terlebih dahulu lalu konversi ulang. Uji praktik air, suara/TTS bila digunakan aplikasi, dan HP/WebView tetap merupakan tahap aplikasi.


## Perubahan sumber PJOK Unit 7 — 13 November 2026

Seluruh 45 soal Unit 7, 10 SVG, dan dua modul berubah/baru setelah konversi. Regenerasi `pjk-b07.json`, `pjk-b07a.json`, buat `pjk-b07-materi.json`, lalu audit per-ID, opsi, media, dan konten dua modul terhadap Markdown `main` terbaru. Jika sumber berubah lagi, audit perubahan itu satu kali sebelum konversi; bila audit menemukan revisi yang diperlukan, revisi Markdown terlebih dahulu lalu konversi ulang. Uji praktik kekuatan/daya tahan dan HP/WebView tetap merupakan tahap aplikasi.


## Perubahan sumber PJOK Unit 8 — 13 November 2026

Seluruh 45 soal Unit 8, 10 SVG, dan dua modul berubah/baru setelah konversi. Regenerasi `pjk-b08.json`, `pjk-b08a.json`, buat `pjk-b08-materi.json`, lalu audit per-ID, opsi, media, dan konten dua modul terhadap Markdown `main` terbaru. Dengan Unit 8, semua sumber PJOK Unit 1–8 telah diaudit; 16 modul PJOK kini tersedia. Jika sumber berubah lagi, audit perubahan itu satu kali sebelum konversi; bila perlu revisi Markdown dahulu lalu konversi ulang. Uji praktik, aksesibilitas, dan HP/WebView tetap tahap aplikasi.
