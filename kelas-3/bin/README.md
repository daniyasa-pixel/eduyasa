# Bahasa Indonesia — Kelas 3

Paket awal Kelas 3 untuk menguji pemetaan kelas tanpa memindahkan data Kelas 6.

## Acuan dan cakupan

Buku pemerintah **Bahasa Indonesia: Kawan Seiring untuk SD/MI Kelas III
(Edisi Revisi)**, 2025. Penulis: Gantino Habibi dan S. Nailul Muna Aljamaliah.
ISBN PDF: 978-634-00-3315-1.
[Salinan buku yang dibaca untuk mencocokkan identitas, bab, dan kompetensi](https://fliphtml5.com/Yenny_73/Bahasa-Indonesia_BS_KLS_III_Rev/).
Tautan tersebut merupakan pembaca salinan buku, bukan situs penerbit resmi.

Empat judul bab dan urutannya mengikuti buku ini, bukan buku edisi 2022 yang
mempunyai pembagian bab berbeda. Bacaan, soal, dan ilustrasi dibuat khusus
untuk EDUYASA; bukan salinan teks cerita atau latihan buku.

| Bab | Judul | Fokus pembelajaran |
| --- | --- | --- |
| 1 | Kawan Seiring | Informasi bacaan, gagasan utama/pendukung, kosakata, kapital, tanda baca, cerita runtut |
| 2 | Yuk, Berpendapat! | Pendapat dan alasan, tanggapan santun, perasaan, kamus kecil dan abjad |
| 3 | Pribadi yang Hebat | Tokoh/latar, tujuan–masalah–usaha–hasil, sifat berdasarkan bukti, pesan, pengalaman dan diskusi |
| 4 | Menyelamatkan Lingkungan | Dongeng dan khayalan, sebab-akibat, perubahan tokoh, akhir cerita, ajakan menjaga lingkungan |

Pembagian keterampilan dalam aplikasi merupakan adaptasi ringkas untuk
latihan, bukan pemindahan seluruh kegiatan buku ke aplikasi. Daftar tujuan
tersimpan pada setiap materi dan pada kedua versi modulnya.

## Isi paket

Setiap bab mempunyai:

- `bin-bNN.json`: 50 soal biasa tanpa gambar.
- `bin-bNNa.json`: 20 soal bergambar tambahan, nomor ID 051–070.
- `bin-bNN-materi.json`: MOD-01 dan MOD-02 dengan tujuan setara dan teks berbeda;
  masing-masing menyisipkan dua ilustrasi di antara penjelasan.
- 12 SVG: 10 stimulus soal dan dua ilustrasi materi. Setiap stimulus mendukung
  dua pertanyaan berbeda. Artinya ada 20 soal bergambar, bukan 20 gambar unik.

Total: **200 soal biasa + 80 soal bergambar, 8 modul, 48 SVG**. SVG berisi judul,
deskripsi alternatif, bentuk, dan teks; tanpa skrip, font eksternal, atau raster.
Komik, denah, kartu kata, dan poster memberikan informasi untuk menjawab soal.
Denah mempertahankan posisi kiri–tengah–kanan yang digunakan dalam pertanyaan.
Gambar dapat diperbesar melalui klik, Enter, serta tombol tambah/kurang.

Setiap sesi mengambil **20 soal: 15 biasa + 5 bergambar** dari gabungan bab
pilihan, kemudian mengacak urutan soal serta opsi A–D. Kuota berlaku untuk sesi,
bukan 20 soal untuk setiap bab yang dipilih. Manifest membaca kedua paket; jika
paket tambahan gagal dimuat, sesi dihentikan dengan pesan dan dapat dicoba lagi.

## Pemisahan kelas

Bank ini berada di `kelas-3/bin/` dengan awalan ID `K03-`. Materi, koneksi,
manifest, soal, dan label laporan mengikuti kelas pilihan. Bank BIN Kelas 6
berada di `bin/` dengan ID lama. Tidak ada file data Kelas 6 yang dipindahkan
atau ditulis ulang dalam perubahan ini.

## Pemeriksaan rilis 20261111-k03-bin-1

Perintah pengujian otomatis: `node --test tests/*.test.cjs` — **39 pengujian
lulus**, termasuk 31 pengujian lama. Pengujian baru mencakup pemetaan kelas,
pembacaan kedua paket, ID dan media, kuota 5/15 dengan 100 seed per bab,
gabungan empat bab, penolakan data salah kelas/bab, manifest tidak sah, dan
kegagalan paket tambahan. Setiap modul diperiksa tujuan, versi, panjang ringkas,
serta gambar yang disisipkan di tengah teks.

Pemeriksaan seluruh bank repository menemukan 2.710 soal dengan ID unik.
Seluruh 280 soal baru mempunyai empat opsi berbeda, jawaban sesuai teks opsi,
dan pembahasan. Posisi kunci dalam JSON juga bervariasi; aplikasi mengacaknya
kembali saat menampilkan soal.

Pengujian browser lokal pada lebar layar 390 piksel menyelesaikan empat kuis
(80 jawaban), delapan versi materi, pembesaran gambar dengan klik/keyboard,
gabungan empat bab, kegagalan paket A dan retry, serta hasil dan laporan Kelas 3.
Setiap sesi menampilkan tepat lima gambar pada posisi yang berubah-ubah.
Seluruh 48 SVG dirender dan diperiksa batas teksnya serta ditinjau secara visual.
Pemeriksaan ulang BIN, MTK, PJK, dan SBD Kelas 6 tetap membaca data akar dengan
label laporan Kelas 6. Tidak ditemukan kesalahan JavaScript yang tidak tertangani.

Formulir laporan menolak nomor yang tidak valid, membentuk tautan WhatsApp
dengan nomor Indonesia yang dinormalisasi dan label Kelas 3, lalu mereset pilihan
kelas ketika kembali ke halaman awal. Tautan ditangkap oleh pengujian; tidak
membuka WhatsApp atau mengirim pesan. Pembatalan kembali ke halaman awal juga
diuji. Kelas yang belum tersedia tetap menonaktifkan tombol Mulai Belajar.

[Rincian hasil lokal untuk audit](VALIDATION.json). Verifikasi publik dilakukan
sesudah deployment; rincian tersebut mencatat pengujian lokal sebelum rilis.

## Batas audit

Pemeriksaan ini memeriksa kelengkapan, konsistensi dasar, dan fungsi aplikasi.
Audit akademik menyeluruh oleh guru belum dilakukan, sesuai keputusan pemilik
untuk menunda full audit. Kesetaraan dua versi dinilai berdasarkan daftar tujuan
dan cakupan yang sama; belum melalui uji hasil belajar siswa. Pelafalan/audio
belum ditambahkan karena latihan ini menggunakan bacaan dan stimulus visual.
Pengiriman WhatsApp hanya diuji sampai pembentukan tautan; tidak ada pesan yang
dikirim ke penerima sungguhan. Pengujian siswa perlu mencakup keterbacaan bahasa,
kejelasan gambar/pengecoh, dan kesesuaian dengan buku yang dipakai sekolah.
