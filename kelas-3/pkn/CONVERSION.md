# Konversi Pendidikan Pancasila Kelas 3

Rilis aplikasi: `20261111-k03-pkn-1`.

## Ruang lingkup

Konversi empat bab PKN dari sumber Markdown yang diunggah pemilik proyek.
Tidak membuat soal baru. Sumber Markdown dan 48 SVG dipertahankan tanpa
perubahan. Matematika tidak dikonversi, diubah, atau diaktifkan pada rilis ini
karena masih dikerjakan secara terpisah, sesuai instruksi terbaru pemilik.

| Bab | Judul | Modul | Soal biasa | Soal bergambar |
| --- | --- | --- | --- | --- |
| 1 | Aku Anak Indonesia | 2 | 50 | 20 |
| 2 | Aku Patuh Aturan | 2 | 50 | 20 |
| 3 | Berbeda Itu Indah | 2 | 50 | 20 |
| 4 | Ayo Mengenal Pancasila | 2 | 50 | 20 |
| Total | | 8 | 200 | 80 |

Ada 13 JSON baru: empat materi, empat bank biasa, empat bank gambar, serta
`question-packs.json`. Teks materi, nomor ID, urutan opsi dalam file, jawaban,
pembahasan, URL dan teks alternatif gambar dipertahankan dari sumber. Hanya
judul pembungkus dokumen, label kolom, dan garis pemisah antarrekaman yang
menjadi struktur JSON. Setiap file konten menyimpan lokasi dan SHA-256 sumber
pada `source_markdown` untuk pemeriksaan ulang.

## Integrasi dan tombol

`HTML/grade-config.js` menambahkan PKN hanya pada Kelas 3, dengan empat judul
bab. Aplikasi mengambil `kelas-3/pkn/`, ID berawalan `K03-PKN-`, dan kedua
paket yang tercatat di manifest. URL aplikasi tetap memakai `?kelas=3`.
Informasi ketersediaan mapel pada halaman awal mengikuti konfigurasi kelas.
Kelas 3 dan 6 tetap aktif, kelas lain tetap abu-abu.

Tombol NEXT/KEMBALI menggunakan mapel dan bab yang dipilih. Setiap sesi tetap
**20 soal: 15 biasa dan 5 bergambar**, dari gabungan bab pilihan. Urutan soal
serta opsi A–D diacak; kunci tetap berupa teks jawaban. Hasil dan laporan
WhatsApp mencantumkan Kelas 3, Pendidikan Pancasila, serta bab yang diujikan.
Sesi tidak diteruskan jika paket gambar gagal diambil; pengguna dapat mencoba
lagi tanpa diam-diam mendapatkan komposisi soal yang berbeda.

## Verifikasi yang dapat diulang

Dari akar repository:

```sh
python scripts/convert-k03-pkn.py --check
node --test tests/*.test.cjs
```

Konverter hanya menangani PKN. Gunakan `--write` untuk membuat ulang JSON
sesudah sumber PKN diperbarui. Seluruh bab divalidasi sebelum berkas ditulis.

Pengujian otomatis memeriksa kesetiaan teks terhadap Markdown, hash sumber,
jumlah dan ID, empat opsi unik, kecocokan kunci, kedua modul, seluruh referensi
aset, kuota acak 5/15 sebanyak 100 seed per bab, multi-bab, gabungan BIN+PKN,
kegagalan paket A dan retry, serta kompatibilitas bank lama.

**Hasil sebelum rilis:** 47 pengujian otomatis lulus (termasuk seluruh pengujian
lama), konversi ulang `--check` sesuai sumber, dan 2.990 ID soal dalam seluruh
bank JSON repository unik.

Browser Chromium pada lebar layar 390 piksel menyelesaikan delapan versi
materi dan empat kuis lengkap (80 jawaban), masing-masing tepat lima soal
bergambar dengan urutan dan pilihan jawaban yang berubah. Tombol NEXT,
KEMBALI dari pemilihan bab, KEMBALI dari materi, dan KEMBALI dari persiapan
kuis mempertahankan pilihan Kelas 3/PKN/bab. Gabungan dua mapel BIN+PKN dan
empat bab tetap menjadi satu sesi 20 soal dengan kuota 5/15. Kegagalan paket A
menghentikan kuis; percobaan ulang berhasil.

Seluruh 48 SVG dimuat, XML dan batas teks diperiksa, serta ditinjau melalui
lembar gambar. Pembesaran klik, Enter, tambah/kurang, dan penutupan gambar
berfungsi. Formulir laporan menolak nomor salah; tautan WhatsApp yang ditangkap
memuat nomor Indonesia yang dinormalisasi, Kelas 3, Pendidikan Pancasila, bab,
dan nilai. Tidak ada pesan yang dikirim. Kembali ke awal mereset pilihan kelas;
pembatalannya tetap berada di hasil.

Pemeriksaan ulang Bahasa Indonesia Kelas 3 serta BIN, MTK, PJK, dan SBD Kelas 6
mengikuti lokasi datanya dan label kelas yang benar. Tidak ada kesalahan
JavaScript yang tidak tertangani. Verifikasi versi publik dilakukan setelah
penerbitan; hasilnya disampaikan bersama tautan rilis.

## Batas audit

Konversi menjaga konten yang diunggah, bukan mengesahkan seluruh kunci dan
pernyataan akademiknya. Identitas buku mengikuti README sumber. Audit
akademik menyeluruh oleh pendidik tetap belum dilakukan; pemeriksaan visual
menilai keterbacaan dan keterhubungan aset, bukan penilaian pedagogis penuh.
Ikon dan label pada SVG asli tetap digunakan sesuai unggahan. Pengujian
WhatsApp hanya memeriksa tautan dan teks laporan, tanpa mengirim pesan.
