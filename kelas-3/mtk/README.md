# Matematika — EDUYASA Kelas 3

**Status:** SUMBER KONTEN MARKDOWN SELESAI. Belum dikonversi ke JSON dan belum dihubungkan ke aplikasi; konversi dan integrasi dilakukan Codex nanti.

## Dasar materi

Buku siswa resmi **Matematika untuk SD/MI Kelas III**, Pusat Perbukuan, 2022, edisi 1; penulis Susanto, Arika Indah Kristiana, Arif Fatahillah, Eko Waluyo, Ridho Alfarisi, Hobri. ISBN PDF **978-602-427-935-6**.

- [Katalog buku pemerintah](https://buku.kemendikdasmen.go.id/katalog/matematika-untuk-sdmi-kelas-iii)
- [Buku siswa PDF untuk acuan judul dan kompetensi](https://static-sc.cloudapp.web.id/content/pdf/bukuteks/kurikulum21/Matematika_BS_KLS_III.pdf)

Buku memuat lima bab. Konten EDUYASA ditulis sendiri dengan bahasa anak Kelas 3, tidak menyalin soal dan penjelasan buku. Materi menyesuaikan cakupan bab, termasuk operasi sampai 100 di Bab 1.

## Paket yang diunggah

| Bab | Materi (2 versi) | Soal biasa (50) | Soal gambar (20) |
|---|---|---|---|
| 1. Bilangan Cacah sampai 1.000 | [Materi](mtk-b01-materi.md) | [50 soal](mtk-b01-soal-biasa.md) | [20 gambar](mtk-b01-soal-bergambar.md) |
| 2. Kalimat Matematika | [Materi](mtk-b02-materi.md) | [50 soal](mtk-b02-soal-biasa.md) | [20 gambar](mtk-b02-soal-bergambar.md) |
| 3. Pengukuran Panjang dan Berat | [Materi](mtk-b03-materi.md) | [50 soal](mtk-b03-soal-biasa.md) | [20 gambar](mtk-b03-soal-bergambar.md) |
| 4. Unsur-Unsur Bangun Datar | [Materi](mtk-b04-materi.md) | [50 soal](mtk-b04-soal-biasa.md) | [20 gambar](mtk-b04-soal-bergambar.md) |
| 5. Penyajian Data dalam Tabel | [Materi](mtk-b05-materi.md) | [50 soal](mtk-b05-soal-biasa.md) | [20 gambar](mtk-b05-soal-bergambar.md) |

**Total: 10 modul (MOD-01 dan MOD-02), 250 soal tanpa gambar, 100 soal bergambar, 60 aset SVG.** Ada 10 gambar stimulus soal per bab (satu gambar digunakan untuk dua soal berbeda), dan 2 ilustrasi materi per bab (dipakai dalam kedua versi modul). Sumber gambar ada di [assets/](assets/).

## Aturan matematika (PENTING)

Seluruh operasi, nilai, rumus, pecahan, satuan matematis, simbol perbandingan, dan besar sudut dalam konten teks menggunakan LaTeX/MathJax.

Contoh sumber Markdown (bukan JSON):

- Inline: `$28+15=43$`
- Perkalian: `$4\\times5=20$`
- Pembagian: `$20\\div4=5$`
- Pecahan: `$\\frac{1}{2}$`
- Satuan: `$2\\ \\text{m}=200\\ \\text{cm}$`
- Sudut: `$90^{\\circ}$`
- Blok: `$$\\frac{1}{2}+\\frac{1}{4}=\\frac{3}{4}$$`

**Jangan menghapus atau mengganti delimiter `$...$`, `$$...$$`, tanda backslash, `\text{}`, kurung kurawal, atau simbol saat konversi.** MathJax di aplikasi akan menampilkannya; Codex wajib menangani escape backslash di JSON.

## Status pemeriksaan

- 5 bab dan 10 modul tersedia; setiap versi memuat dua sisipan gambar di antara penjelasan.
- Semua bank berisi 50 soal biasa dan 20 bergambar per bab.
- 350 ID unik `K03-MTK-BNN-M01-001` sampai `070` per bab.
- Tiap soal mempunyai tepat 4 opsi berbeda, 1 kunci identik dengan salah satu opsi, dan pembahasan.
- 50 gambar stimulus unik untuk 100 soal bergambar; 10 SVG ilustrasi modul; seluruh 60 aset ada dan setiap tautan sumber ditemukan.
- Semua ekspresi MathJax diperiksa terhadap pembatas `$` yang ganjil, kurung kurawal, dan ekspresi templat tertinggal: tidak ditemukan kesalahan struktur.
- **Batasan:** Belum ada pemeriksaan visual seluruh SVG pada browser, belum pengujian MathJax pada frontend, dan belum review akademik menyeluruh oleh pendidik. Lakukan sebelum merilisnya untuk siswa.

**Catatan:** Tidak mengubah aplikasi, konfigurasi kelas, JSON kelas lain, atau paket lama.

[Handover untuk Codex](HANDOFF-CODEX.md)
