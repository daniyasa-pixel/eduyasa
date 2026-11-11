# Pendidikan Pancasila — EDUYASA Kelas 3

**Status:** Empat bab sudah dikonversi ke JSON dan dihubungkan ke pilihan Pendidikan Pancasila pada **Kelas 3**. Seluruh sumber Markdown dan SVG asli dipertahankan. [Laporan konversi dan pemeriksaan](CONVERSION.md).

## Acuan resmi

Buku Siswa *Pendidikan Pancasila untuk SD/MI Kelas III* (Ressi Kartika Dewi, Kamala Rahayu Candra Sary, Hani Hanifah), Pusat Perbukuan, terbit 2023, ISBN PDF **978-623-194-631-7**. Berlaku sebagai acuan Kurikulum Merdeka tahun ajaran 2025/2026.

- Katalog pemerintah: https://buku.kemendikdasmen.go.id/katalog/pendidikan-pancasila-untuk-sdmi-kelas-iii
- Buku sumber yang dirujuk: https://static-sc.cloudapp.web.id/content/pdf/bukuteks/kurikulum21/Pendidikan-Pancasila-BS-KLS-III.pdf

Bab mengikuti buku: (1) Aku Anak Indonesia; (2) Aku Patuh Aturan; (3) Berbeda Itu Indah; (4) Ayo Mengenal Pancasila. Ringkasan serta pertanyaan adalah konten baru EDUYASA, bukan menyalin soal buku.

## Paket konten dalam folder ini

| Bab | Materi dua modul | 50 soal tanpa gambar | 20 soal bergambar |
|---|---|---|---|
| 1 Aku Anak Indonesia | [Materi](pkn-b01-materi.md) | [Soal biasa](pkn-b01-soal-biasa.md) | [Soal gambar](pkn-b01-soal-bergambar.md) |
| 2 Aku Patuh Aturan | [Materi](pkn-b02-materi.md) | [Soal biasa](pkn-b02-soal-biasa.md) | [Soal gambar](pkn-b02-soal-bergambar.md) |
| 3 Berbeda Itu Indah | [Materi](pkn-b03-materi.md) | [Soal biasa](pkn-b03-soal-biasa.md) | [Soal gambar](pkn-b03-soal-bergambar.md) |
| 4 Ayo Mengenal Pancasila | [Materi](pkn-b04-materi.md) | [Soal biasa](pkn-b04-soal-biasa.md) | [Soal gambar](pkn-b04-soal-bergambar.md) |

Total: **8 modul, 200 soal biasa, 80 soal bergambar, 48 aset SVG.** Terdapat **10 ilustrasi stimulus bergambar per bab**, dan masing-masing dipakai oleh **2 soal berbeda** (20 soal gambar). Ditambah **2 SVG ilustrasi materi per bab**, disisipkan dalam kedua versi modul pada bab tersebut. Seluruhnya terletak di [assets/](assets/).

## Status audit sumber

- Jumlah soal per bab: 50 + 20; diperiksa 280 nomor.
- ID soal: 280 ID unik lintas empat bab; tiap bab memiliki 001–070, berawalan `K03-PKN-BNN-M01-`.
- Tiap soal: 4 opsi yang berbeda, 1 kunci berupa teks opsi persis, penjelasan tersedia.
- Tiap bab: dua modul dengan penjelasan, kegiatan, dan gambar. Gambar berupa SVG orisinal dengan `title`, `desc`, dan teks alternatif pada Markdown.
- Referensi gambar pada soal: 80; 40 stimulus unik; aset 48 terdaftar pada folder, tidak ada nama aset hilang.
- **Batas audit:** struktur dan konsistensi berkas telah diperiksa; masih diperlukan inspeksi visual/browser serta pemeriksaan pedagogis oleh pendidik sebelum rilis siswa. SVG adalah ilustrasi statis; latihan yang mengajak anak menjawab/mengamati ada pada teks modul. Jika ingin interaksi klik animasi, itu tugas frontend Codex.

## File yang dibaca aplikasi

Setiap bab menggunakan `pkn-bNN-materi.json` (dua versi materi),
`pkn-bNN.json` (50 soal biasa), dan `pkn-bNNa.json` (20 soal bergambar).
`question-packs.json` mendaftarkan kedua bank untuk tiap bab. Sesi tetap
20 soal: 15 biasa + 5 bergambar, kemudian urutan soal dan opsi diacak.

Untuk memverifikasi kesesuaian JSON dengan sumber:
`python scripts/convert-k03-pkn.py --check` dari akar repository.
Konverter hanya membaca/menulis PKN, bukan Matematika. Gunakan `--write`
untuk membuat ulang JSON setelah sumber PKN diperbarui dan diperiksa.

Sumber Markdown tetap dipertahankan sebagai bahan penyuntingan dan audit.
Bank Bahasa Indonesia Kelas 3 dan data Kelas 6 tidak ditulis ulang.

[Handover konversi khusus Codex](HANDOFF-CODEX.md)
