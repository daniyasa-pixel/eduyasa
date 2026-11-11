# Revisi 1.945 soal — checkpoint 2026-11-11

**STATUS: BELUM SELESAI. Bukan persetujuan rilis atau konversi JSON.**

| Mata pelajaran | Revisi putaran ini | Audit isi putaran ini | Sisa |
| --- | --- | --- | --- |
| IPAS | Bab 1–3: 196 butir berubah | 210 butir diperiksa ulang | Bab 4–8: 350 soal |
| PAI | Belum dikerjakan | Belum diperiksa ulang | 300 soal/10 bab |
| Bahasa Inggris | Belum dikerjakan | Belum diperiksa ulang | 500 soal/10 unit |
| PJOK | Belum dikerjakan | Belum diperiksa ulang | 360 soal/8 unit |
| Seni Rupa | Belum dikerjakan | Belum diperiksa ulang | 225 soal/5 bab |

Jumlah diperiksa secara isi: **210/1.945**. Jumlah diubah: **196**.
Sisa audit isi: **1.735 soal pada 38 bab/unit**. Revisi terdahulu pada mapel
lain belum dihitung lulus karena belum diperiksa ulang dalam putaran ini.

## IPAS Bab 1

Seluruh nomor 001–070 dibaca ulang. Nomor 001–007, 010–016 dipertahankan
setelah penilaian ulang, bukan karena pernah direvisi. Nomor 008, 009, 017–020,
021–070 berubah secara substantif. Semua ID tetap.

- Keterangan tidur tidak lagi menyatakan otak berhenti bekerja; pembahasan
  menekankan pemulihan dan konsentrasi.
- Nomor 020 yang membahas petir diganti agar selaras dengan ciri tubuh dan
  penghargaan terhadap perbedaan, bukan keselamatan cuaca di luar fokus bab.
- Nomor 021–050 mengganti pengulangan definisi dengan bentuk–fungsi indra,
  batas informasi dari satu indra, bukti pengamatan, perbandingan terarah,
  akses belajar, dan keselamatan pengamatan.
- Nomor 051–070 memakai sepuluh stimulus baru. Gambar tidak lagi berupa
  daftar pasangan istilah–jawaban. Stimulus mencakup pola bentuk/warna, data
  ketukan, catatan tekstur, denah, urutan kegiatan, serta bukti dan selera.
  Jawaban memerlukan informasi pada stimulus. Informasi data pengamatan
  memang ditampilkan; label jawaban benar dan penjelasan kunci tidak dicetak.
- Sepuluh SVG soal diganti; ikon kain, lampu, gelas, buah, dan cuci tangan
  disesuaikan dengan objek yang dijelaskan. Dua ilustrasi materi dipertahankan.
- Kedua modul ditambah penjelasan bentuk–fungsi serta batas kesimpulan
  pengamatan. Acuan: buku pemerintah IPAS III revisi 2025, Bab 1 halaman 1–24,
  khususnya hubungan bentuk tubuh dan fungsinya. Referensi buku:
  https://static.sc.cloudapp.web.id/content/pdf/bukuteks/kurikulum21/IPAS_BS_KLS_III.pdf

## IPAS Bab 2

Semua 70 butir diperiksa ulang dan direvisi pada putaran ini; seluruh ID tetap.
Pengecoh diganti agar terkait sumber sejarah, urutan kejadian, dan penafsiran
bukti. Istilah instruksi “Tantangan” dikeluarkan dari bank. Pernyataan terlalu
umum tentang perkembangan alat diganti dengan konteks dan batas kesimpulan.
Soal mencakup sumber lisan/tertulis, izin wawancara, pengecekan silang,
kronologi, perubahan/kesinambungan, ketidakpastian, dan privasi.

Sepuluh stimulus soal dan dua ilustrasi materi diganti. Data perbandingan taman
benar-benar menggambar tiga pohon dengan satu/tiga bangku. Kendaraan sesuai
catatan sepeda/bus/kereta; jadwal disajikan sebagai tabel. Stimulus lain berupa
garis waktu dan kartu bukti yang harus dibandingkan. Kedua modul ditulis ulang
untuk mengajarkan kompetensi yang diujikan, dengan pilihan tugas sejarah sekolah
bagi siswa yang tidak nyaman membahas data keluarga. Acuan: Bab 2 buku revisi
2025 halaman 25–48, mencakup sejarah keluarga dan perubahan sosial-budaya.

Pemeriksaan Bab 2: 70 ID dan teks pertanyaan unik, 50+20 butir, empat opsi,
kunci cocok; 12 SVG lolos XML, rendering lebar 390 piksel, batas teks, dan
peninjauan lembar gambar. Lulus struktur tidak dipakai untuk mengklaim semua
mapel lulus akademik. Belum ada uji kesulitan empiris pada siswa.

## IPAS Bab 3

Semua 70 butir diperiksa dan ditulis ulang; ID tetap. Cakupan lama yang hampir
seluruhnya berupa istilah perilaku sosial diganti dengan dua bagian sesuai buku
revisi 2025 halaman 49–66: pertumbuhan/perkembangan manusia dan berbagi peran
keluarga. Bank biasa memuat 25 butir tentang perubahan ukuran/kemampuan,
tahapan, bukti pengamatan, kebutuhan dukungan; 25 butir berikutnya tentang
pembagian tanggung jawab, kesepakatan, kondisi yang berubah, dan keselamatan.

Keenam nama tahap pada buku dipertahankan tanpa memaksakan batas usia atau
kemampuan yang sama bagi semua orang. Diagram linear tidak menyatakan bahwa
individu lanjut usia kembali menjadi bayi. Grafik pertumbuhan memakai dua
hasil contoh dari anak yang sama dan sumbu dimulai dari nol; penyebab pasti
perubahan serta kondisi kesehatan tidak disimpulkan dari grafik itu saja.

Sepuluh gambar soal dan dua ilustrasi materi diganti, kemudian ditinjau melalui
lembar gambar. Dua modul ditulis ulang dan berisi dua gambar masing-masing.
Pembagian peran tidak dibatasi berdasarkan jenis kelamin; keselamatan tugas,
hak meminta bantuan, dan penyesuaian kondisi keluarga diterangkan.

Pemeriksaan: 50+20 butir; 70 ID dan teks pertanyaan unik; kunci cocok dengan
empat opsi; tidak ada label instruksi Tantangan sebagai konsep. Dua belas SVG
lolos XML dan uji batas teks pada lebar 390 piksel. Perintah LaTeX satuan pada
sumber Markdown diperiksa agar tidak berubah menjadi tab atau double escape.
Tidak ada konversi JSON atau perubahan pada materi yang dilarang.

## Pemeriksaan

`python scripts/check-k03-question-sources.py` memeriksa paket kanonis lima
mapel: jumlah 1.945, ID unik, empat opsi berbeda, kunci ada di opsi, serta
referensi gambar soal tersedia. Hasil: **0 kesalahan struktur**.

Itu **bukan** bukti seluruh kunci benar secara akademik. Butir di luar Bab 1–3
belum memperoleh pemeriksaan isi putaran ini. Script juga tidak menyimpulkan
ketunggalan jawaban, kesesuaian buku, kualitas pengecoh, atau kompetensi.

Dua belas SVG Bab 1 dirender pada Chromium dan diperiksa batas teksnya;
lembar gambar ditinjau secara visual. Perubahan akhir ikon diuji ulang.
Pemeriksaan ini bukan uji coba kepada siswa atau pengukuran kesulitan empiris.

## Posisi lanjut yang tepat

Lanjut **IPAS Bab 4, nomor 001–070**, dengan memeriksa dua modul dan 12 SVG
terkait, lalu Bab 5 sampai Bab 8. Setelah IPAS tuntas, lanjut PAI, ENG, PJOK,
dan Seni Rupa sesuai instruksi. Pertahankan pemeriksaan terhadap buku revisi 2025. Instruksi kegiatan yang
sebelumnya dijadikan istilah pelajaran juga harus diperiksa pada bab lain.

Dilarang menyimpulkan keseluruhan selesai dari checkpoint ini. Jangan membuat
JSON, mengaktifkan draft pada aplikasi, atau mengubah BIN, MTK, dan PKN.
