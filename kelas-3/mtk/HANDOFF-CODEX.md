# HANDOVER CODEX — EDUYASA Kelas 3 Matematika

## Tugas saat integrasi

Konversi konten sumber Markdown dan SVG dari folder `kelas-3/mtk/` menjadi JSON, sambungkan ke konfigurasi Kelas 3 tanpa mengganggu Kelas 6, dan lakukan pemeriksaan menyeluruh. **Belum dikerjakan pada tahap produksi konten ini.**

## Sumber

Untuk N = 01, 02, 03, 04, 05:

- `mtk-bNN-materi.md`: 2 modul `K03-MTK-BNN-MOD-01` dan `MOD-02`.
- `mtk-bNN-soal-biasa.md`: 50 soal, ID `K03-MTK-BNN-M01-001` – `050`.
- `mtk-bNN-soal-bergambar.md`: 20 soal, ID `051` – `070`.
- `assets/mtk-bNN-gambar-01.svg` – `10.svg`: 10 stimulus unik, dua soal per stimulus.
- `assets/mtk-bNN-materi-01.svg` dan `02.svg`: ilustrasi materi yang harus dipertahankan di posisi asal dalam kedua modul.

## Standar wajib MathJax (prioritas tinggi)

Sumber sudah menggunakan delimiter inline `$...$` atau display `$$...$$` dengan LaTeX seperti `\\frac`, `\\times`, `\\div`, `\\text{}`, `\\square`, `\\circ`. Biarkan rumus tetap utuh. Dalam JSON, escape backslash sesuai sintaks JSON agar setelah parse muncul kembali sebagai teks LaTeX semula; jangan double-escape teks yang sudah diparse.

Contoh dalam Markdown `$4\\times5=20$` harus diterima MathJax setelah parsing JSON dalam bentuk yang **identik**, bukan berubah menjadi tab (`\\t`) atau hilang garis miringnya.

Pasang/cek MathJax pada **materi, stem pertanyaan, semua pilihan, kunci, dan pembahasan**, termasuk bila konten dimasukkan dinamis. Ikuti `docs/CONTENT_GUIDE.md`.

## Target JSON (belum dibuat)

- `mtk-bNN-materi.json`: `subject_id` = `MTK`; `subject_name` = `Matematika`; `grade` = 3; `curriculum` = `Kurikulum Merdeka`; `chapter_id` = `K03-MTK-BNN`, `chapter_title`, `modules[]` (2 modul; isi `content` asli Markdown termasuk gambar).
- `mtk-bNN.json`: 50 soal tanpa gambar.
- `mtk-bNNa.json`: 20 soal bergambar; `image.src` URL raw SVG dan `image.alt` deskripsi. Wajib satu jawaban yang identik dengan teks salah satu dari empat `options`.
- `question-packs.json` dan `HTML/grade-config.js` sesuai pola `kelas-3/bin/` untuk aktivasi mata pelajaran setelah data valid.

Pilihan jawaban tidak memakai label A/B/C/D dalam JSON; label ditambahkan frontend. ID soal unik antar kelas dan bab. Jangan menyalin JSON Kelas 6 ke Kelas 3.

## Mekanisme kuis

Per sesi EDUYASA Kelas 3 mengambil **20 soal acak: 15 biasa + 5 soal gambar**, secara keseluruhan dari bab-bab terpilih. Acak juga opsi pilihan, tetapi jawaban benar tetap berdasarkan **teks**. Pilihan versi modul 01/02 juga acak, namun cakupan kompetensi setara.

## QA yang perlu dilakukan Codex

1. Uji seluruh JSON parseable, 10 modul/350 soal/60 SVG valid, ID dan jawaban valid.
2. Uji render MathJax setelah HTML dinamis, pada semua halaman yang memuat rumus; pastikan pecahan, operasi, satuan, simbol perbandingan, kotak kosong, dan derajat terbaca.
3. Uji konversi string matematika *round trip*: isi Markdown sebelum konversi harus sama dengan isi setelah parse JSON.
4. Uji gambar tiap materi dan soal, khususnya diagram tempat bilangan, diagram pita, skala, sudut dan bangun datar, serta tabel.
5. Uji randomisasi soal dan opsi dengan kuota 15/5, termasuk gabungan bab dan kegagalan media.
6. Uji regresi Bahasa Indonesia Kelas 3 dan seluruh mata pelajaran Kelas 6; jangan mengubah paket yang telah dibuat.
7. Lakukan audit pedagogis konten/angka serta penilaian visual sebelum rilis.

Rujukan: https://buku.kemendikdasmen.go.id/katalog/matematika-untuk-sdmi-kelas-iii

**Batas rilis:** Sumber siap untuk proses konversi, belum dinyatakan siap tayang bagi siswa sampai browser, MathJax, dan audit pendidikan lulus.
