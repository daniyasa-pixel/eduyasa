# Matematika Kelas 3 — konversi dan tampilan rumus

Rilis aplikasi: `20261111-k03-mtk-1`.

Lima bab sumber Markdown dikonversi menjadi 16 JSON dan dihubungkan ke pilihan
**Matematika, Kelas 3**. Tidak membuat bank atau teks pengajaran baru. Seluruh
ID, teks materi, pertanyaan, urutan opsi dalam file, kunci, pembahasan, dan
notasi LaTeX dipertahankan. Aplikasi memilih versi modul dan mengacak soal serta
pilihan A–D saat ditayangkan.

| Bab | Judul | Modul | Soal biasa | Soal bergambar |
| --- | --- | --- | --- | --- |
| 1 | Bilangan Cacah sampai 1.000 | 2 | 50 | 20 |
| 2 | Kalimat Matematika | 2 | 50 | 20 |
| 3 | Pengukuran Panjang dan Berat | 2 | 50 | 20 |
| 4 | Unsur-Unsur Bangun Datar | 2 | 50 | 20 |
| 5 | Penyajian Data dalam Tabel | 2 | 50 | 20 |
| Total | | 10 | 250 | 100 |

Masing-masing bab mempunyai `mtk-bNN-materi.json`, `mtk-bNN.json`, dan
`mtk-bNNa.json`. `question-packs.json` mendaftarkan kedua bank untuk setiap bab.
Ada 60 SVG: 50 stimulus soal (dua pertanyaan per stimulus) dan 10 ilustrasi
materi. Setiap versi materi tetap mempunyai dua sisipan gambar pada posisi
asal. Metadata tujuan belajar dirangkum dari cakupan sumber; teks modul tidak
ditulis ulang. Setiap JSON konten menyimpan lokasi dan SHA-256 sumber Markdown.

## LaTeX disimpan, MathJax menampilkan

LaTeX adalah notasi teks matematikanya. MathJax adalah mesin yang merendernya
di aplikasi. Konversi memakai encoder JSON standar sehingga backslash tidak
berubah menjadi tab atau karakter kontrol.

```text
Sumber Markdown:       $4\times5=20$
Teks di berkas JSON:   "$4\\times5=20$"
Setelah JSON dibaca:   $4\times5=20$
```

Delimiter `$...$`/`$$...$$`, `\square`, `\times`, `\div`, `\text{}`, `\circ`,
kurung kurawal, dan perbandingan tetap utuh. Pilihan dan kunci dibandingkan
sebagai teks sumber; perubahan tampilan MathJax tidak mengubah penilaian.

`HTML/math-renderer.js` menunggu script dan startup MathJax, kemudian
menjalankan typesetting dinamis secara berurutan. Materi, soal/opsi, dan
pembahasan dirender setelah teksnya dimasukkan. Hanya elemen yang akan diganti
yang dibersihkan; materi lain tidak dihapus dari pencatatan MathJax.
Pembahasan dimasukkan sebagai teks agar tanda `<`/`>` tidak dianggap HTML.
Jika mesin rumus benar-benar gagal dimuat, ada pesan untuk memeriksa koneksi
dan memuat ulang. Pelajaran tanpa rumus tetap dapat digunakan.

## Koreksi gambar yang dicatat

Lima SVG Bab 4 diperbaiki tanpa mengubah pertanyaan atau kunci:

- `mtk-b04-gambar-02.svg` dan `mtk-b04-materi-01.svg`: gambar persegi sebelumnya
  berukuran 260 × 230; sekarang keempat sisi sama panjang (230 × 230), dan
  penanda titik sudut mengikuti koordinat baru.
- `mtk-b04-gambar-01.svg`, `04.svg`, `05.svg`: warna isi segitiga, segi lima,
  serta segi enam disesuaikan dengan judul ungu, jingga, dan hijau.

Aplikasi menambahkan versi rilis pada URL gambar MTK Kelas 3 saat ditampilkan,
agar koreksi tidak tertahan cache gambar lama. URL asli dalam Markdown dan JSON
serta teks alternatif tetap dipertahankan.

## Verifikasi sebelum rilis

Dari akar repository:

```sh
python scripts/convert-k03-mtk.py --check
node --test tests/*.test.cjs
```

Konverter hanya menangani Matematika Kelas 3. Gunakan `--write` untuk membuat
ulang JSON setelah sumber diperbarui dan diperiksa. Seluruh sumber divalidasi
sebelum berkas ditulis. `--check` membandingkan keluaran terhadap sumber dan
memeriksa round trip JSON/LaTeX tanpa menulis ulang berkas.

- 62 pengujian otomatis lulus, termasuk seluruh pengujian bank lama,
  pemisahan kelas, kesetiaan konversi, kuota, dan pemuatan MathJax yang tertunda,
  gagal, atau pulih. Gambar persegi mempunyai uji kesamaan panjang sisi dan
  sudut siku-siku.
- Seluruh 732 ekspresi LaTeX unik dari JSON berhasil diproses MathJax pada
  Chromium tanpa kesalahan parser. Uji tambahan mencakup pecahan `\frac`,
  display math, operasi, satuan, derajat, perbandingan, dan kotak kosong.
- 203 persamaan aritmetika/konversi satuan tertutup pada materi dan pembahasan
  diperiksa otomatis tanpa menemukan perbedaan nilai. Ini tidak mencakup semua
  penalaran soal atau menggantikan audit akademik.
- Browser lokal menyelesaikan sepuluh versi materi dan lima kuis (100 jawaban).
  Notasi pada materi, pertanyaan, opsi, dan pembahasan tampil sebagai rumus;
  tidak tertinggal delimiter dolar pada teks yang ditampilkan.
- Setiap kuis tetap 20 soal: 15 biasa + 5 bergambar, dengan urutan soal dan opsi
  diacak. Gabungan BIN+MTK dan empat bab tetap menjadi satu sesi. Kegagalan
  paket A menghentikan kuis dan percobaan ulang berhasil.
- Seluruh 60 SVG dimuat, XML/batas teks diperiksa, dan ditinjau secara visual.
  Pembesaran gambar, tombol maju/kembali, hasil, laporan, serta reset halaman
  awal berfungsi. Tautan WhatsApp hanya ditangkap oleh pengujian; tidak ada
  pesan yang dikirim.
- Pemuatan script MathJax sengaja ditahan sampai materi terbuka; setelah
  dilepas, rumus tetap dirender. Saat script diblokir, pesan muncul untuk
  Matematika dan Bahasa Indonesia masih dapat dipakai.

Verifikasi aplikasi publik dilakukan setelah penerbitan; hasilnya disampaikan
bersama tautan rilis. Data/ID Kelas 6, BIN Kelas 3, dan PKN Kelas 3 tidak ditulis
ulang. Proses rendering bersama tetap diperiksa terhadap pelajaran lama.

## Batas audit

Pemeriksaan ini memverifikasi format, angka yang dapat diperiksa, gambar,
kesetiaan sumber, dan perilaku aplikasi. Review akademik menyeluruh oleh guru
belum dilakukan, sesuai keputusan untuk menunda full audit. Kejelasan bahasa,
pengecoh, kecocokan dengan buku sekolah, dan hasil belajar siswa tetap perlu
dinilai melalui audit manual. Metadata acuan buku mengikuti README unggahan.
