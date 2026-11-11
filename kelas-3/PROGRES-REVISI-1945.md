# Revisi 1.945 soal — checkpoint 2026-11-11

**STATUS: BELUM SELESAI. Bukan persetujuan rilis atau konversi JSON.**

| Mata pelajaran | Revisi putaran ini | Audit isi putaran ini | Sisa |
| --- | --- | --- | --- |
| IPAS | Bab 1: 56 dari 70 butir berubah | 70 butir Bab 1 diperiksa ulang | Bab 2–8: 490 soal |
| PAI | Belum dikerjakan | Belum diperiksa ulang | 300 soal/10 bab |
| Bahasa Inggris | Belum dikerjakan | Belum diperiksa ulang | 500 soal/10 unit |
| PJOK | Belum dikerjakan | Belum diperiksa ulang | 360 soal/8 unit |
| Seni Rupa | Belum dikerjakan | Belum diperiksa ulang | 225 soal/5 bab |

Jumlah diperiksa secara isi: **70/1.945**. Jumlah diubah: **56**.
Sisa audit isi: **1.875 soal pada 40 bab/unit**. Revisi terdahulu pada mapel
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

## Pemeriksaan

`python scripts/check-k03-question-sources.py` memeriksa paket kanonis lima
mapel: jumlah 1.945, ID unik, empat opsi berbeda, kunci ada di opsi, serta
referensi gambar soal tersedia. Hasil: **0 kesalahan struktur**.

Itu **bukan** bukti seluruh kunci benar secara akademik. Butir di luar Bab 1
belum memperoleh pemeriksaan isi putaran ini. Script juga tidak menyimpulkan
ketunggalan jawaban, kesesuaian buku, kualitas pengecoh, atau kompetensi.

Dua belas SVG Bab 1 dirender pada Chromium dan diperiksa batas teksnya;
lembar gambar ditinjau secara visual. Perubahan akhir ikon diuji ulang.
Pemeriksaan ini bukan uji coba kepada siswa atau pengukuran kesulitan empiris.

## Posisi lanjut yang tepat

Lanjut **IPAS Bab 2, nomor 001–070**, dengan memeriksa dua modul dan 12 SVG
terkait, lalu Bab 3 sampai Bab 8. Setelah IPAS tuntas, lanjut PAI, ENG, PJOK,
dan Seni Rupa sesuai instruksi. Pada Bab 3 IPAS, cocokkan cakupan pertumbuhan
manusia dan pembagian peran keluarga dengan buku revisi 2025; jangan hanya
mempertahankan soal norma sosial. Instruksi kegiatan yang sebelumnya dijadikan
istilah pelajaran juga harus diperiksa pada bab lain.

Dilarang menyimpulkan keseluruhan selesai dari checkpoint ini. Jangan membuat
JSON, mengaktifkan draft pada aplikasi, atau mengubah BIN, MTK, dan PKN.
