# Audit dan revisi soal EDUYASA Kelas 3 — 2026-11-11

**Cakupan eksklusif:** IPAS, Bahasa Inggris, PAI, PJOK, Seni Rupa. BIN, MTK, PKN tidak disentuh.

**STATUS KESELURUHAN: BELUM LULUS / BELUM FINAL.** Audit struktur dan pola berulang telah dilakukan. Revisi akademik menyeluruh, validasi visual, dan verifikasi per-butir terhadap buku resmi **belum selesai**. Jangan konversi atau rilis sebagai bank final.

| Mapel | Bab | Soal | Temuan yang terverifikasi | Revisi selesai | Sisa pekerjaan |
|---|---:|---:|---|---|---|
| IPAS | 8 | 560 | Pola berulang, opsi ambigu, gambar membocorkan definisi, penyederhanaan konsep diperbaiki | 560 diperiksa; 546 diubah; 16 modul dan media delapan bab diselaraskan | Putaran isi/media selesai; belum konversi atau uji siswa |
| ENG | 10 | 500 | Soal teks tiga pola dominan per unit; gambar dan listening satu pola per unit | Belum | Variasi kompetensi bahasa, stimulus gambar, audio TTS dan uji WebView |
| PAI | 10 | 300 | Soal berulang dan sebagian identik; ID tanpa awalan K03 | Awalan ID diperbaiki; Bab 1–4 diperiksa per butir dan delapan modul diselaraskan | Bab 5–10 dan pratinjau HP Bab 1 |
| PJOK | 8 | 360 | Soal berulang; ilustrasi hanya simbol; berkas soal lama dengan ID lain masih ada | Belum | Revisi teknik gerak dan keselamatan, ilustrasi, pilih bank resmi, modul belum ada |
| SBD | 5 | 225 | Pengulangan kompetensi, ilustrasi generik; bab internal belum diverifikasi terhadap buku | Belum | Soal pengamatan visual, distraktor, ilustrasi, verifikasi bab resmi |

## Standar wajib sebelum status FINAL
1. Empat pilihan yang berbeda, tepat satu benar; jawaban identik dengan salah satu opsi.
2. Tidak ada pengulangan konsep yang hanya mengganti kalimat; variasikan pemahaman, penerapan, pengamatan, dan analisis sederhana.
3. Pengecoh masuk akal, tidak mengandung instruksi tak relevan atau jawaban yang terlalu jelas.
4. Pembahasan menerangkan **mengapa** jawaban benar, bukan mengulang kunci.
5. Soal bergambar memerlukan gambar untuk menjawab; gambar akurat dan tidak membocorkan kunci.
6. Materi selaras dengan buku resmi yang tepat edisi dan jenjangnya; periksa keselamatan PJOK dan keakuratan dalil PAI.
7. ID unik dengan awalan K03; pertahankan ID yang valid dan jumlah per bab.
8. Audit JSON, aset, tampilan HP/WebView, TTS, dan alur soal setelah konversi.

## Riwayat perubahan
- `68d2c45`: IPAS redaksi dan pembahasan 8 berkas teks; **tahap pertama saja**.
- `b5f387c`: perbaikan awalan K03 untuk 300 ID soal PAI.
- `bc7e622`: perbaikan awalan K03 untuk 20 ID modul PAI.

**Catatan integritas:** Perubahan redaksi otomatis IPAS tidak otomatis memperbaiki kualitas pedagogis; butuh penulisan ulang manual yang terarah. Jangan menyatakan revisi menyeluruh telah selesai.

## Kemajuan tambahan (commit f9c98e2)
- IPAS B01 soal teks nomor 001–020: ditulis ulang secara substantif dengan konteks, distraktor, dan pembahasan baru.
- PAI B01 soal nomor 001–010: ditulis ulang secara substantif dengan konteks, distraktor, dan pembahasan baru.
- Pemeriksaan pasca-commit: berkas IPAS B01 tetap memuat 50 nomor dan PAI B01 tetap 30 nomor; ID awal dan akhir masih tersedia.
- **Belum selesai:** 1.915 butir lain belum mendapatkan penulisan ulang substantif dalam putaran ini. Audit kunci satu per satu, kesesuaian buku resmi, dan inspeksi semua gambar belum selesai. Status keseluruhan tetap BELUM FINAL.

## Checkpoint revisi lanjutan

Lihat [PROGRES-REVISI-1945.md](PROGRES-REVISI-1945.md): IPAS Bab 1 seluruh
70 butir diperiksa ulang, 56 diubah, sepuluh stimulus gambar diganti, dua modul
dilengkapi. **Keseluruhan tetap BELUM SELESAI**; 1.875 soal pada 40 bab/unit
belum diaudit ulang. Tidak ada konversi JSON pada tahap ini.

### Kelanjutan: IPAS Bab 2

Seluruh 70 butir Bab 2 diperiksa ulang dan direvisi; dua modul serta 12 SVG
diselaraskan. Kumulatif putaran ini 140 diperiksa, 126 diubah. Sisa 1.805 soal
pada 39 bab/unit; status keseluruhan **BELUM SELESAI**. Titik lanjut Bab 3.

### Kelanjutan: IPAS Bab 3

Seluruh 70 butir Bab 3 ditulis ulang dan diperiksa terhadap cakupan pertumbuhan,
perkembangan, tahapan manusia, dan berbagi peran keluarga. Dua modul serta 12
SVG diganti dan diperiksa. Kumulatif 210 diperiksa, 196 diubah; sisa 1.735 soal
pada 38 bab/unit. Status tetap **BELUM SELESAI**. Titik lanjut: IPAS Bab 4.

### Kelanjutan: IPAS Bab 4 — verifikasi visual belum selesai

Semua 70 butir dan kedua modul Bab 4 ditulis ulang. Dua belas SVG diganti;
70 ID lama dipertahankan. Kumulatif pemeriksaan isi 280, berubah 266;
sisa isi 1.665 soal pada 37 bab/unit. Bab 4 belum ditutup karena uji render
dan peninjauan visual tertunda akibat lingkungan eksekusi tidak tersedia.

Validasi JavaScript terhadap 82 bank kanonis melalui konektor GitHub:
1.945 soal, ID unik, empat opsi berbeda, kunci cocok, path gambar tersedia,
0 kesalahan struktur. Tag/entity dan atribut dasar 12 SVG Bab 4 diperiksa;
ini bukan sertifikasi XML penuh maupun verifikasi tampilan HP.
Kunci dan isi tidak dinyatakan benar hanya karena validasi struktur.

**BELUM SELESAI.** Langkah pertama berikutnya: sinkronkan main, uji serta
tinjau 12 SVG Bab 4 pada Chromium lebar 390 piksel, lalu lanjut Bab 5.
Lihat rincian dan batas pemeriksaan dalam PROGRES-REVISI-1945.md.

### Pemulihan pemeriksaan media IPAS Bab 4

Lingkungan tersedia kembali. Dua belas SVG Bab 4 lolos parser XML,
rendering Chromium lebar 390 piksel dan pemeriksaan batas teks; lembar
gambar ditinjau secara visual. Validator Python: 1.945 soal, 0 kesalahan
struktur. Hambatan media Bab 4 ditutup. Bab 1–4 berjumlah 280 soal diperiksa,
266 berubah; sisa 1.665 soal/37 bab. Titik lanjut Bab 5. Status keseluruhan
BELUM SELESAI, tanpa konversi JSON.

### Kelanjutan: IPAS Bab 5

Semua 70 butir Bab 5 ditulis ulang dan diperiksa; seluruh ID dan jumlah tetap.
Dua modul serta 12 SVG diganti. Nilai uang, barter, kebutuhan sesuai konteks,
anggaran, nilai guna dan keputusan belanja dicakup. XML, render 390 piksel,
batas teks dan tinjauan visual berhasil; 46 ekspresi unik sumber berhasil
dirender MathJax tanpa kesalahan. Validasi bank kanonis: 1.945, 0 kesalahan
struktur. Kumulatif 350 diperiksa, 336 diubah; sisa 1.595 soal/36 bab.
Status **BELUM SELESAI**. Titik lanjut IPAS Bab 6. Tidak ada konversi JSON.

### Kelanjutan: IPAS Bab 6

Seluruh 70 soal dan kedua modul ditulis ulang, 12 SVG diganti. ID dan jumlah
tetap. Koreksi sumber/bentuk energi, lampu sebagai pengguna listrik, panel
fotovoltaik, energi makanan versus air putih, keselamatan dan penghematan
sesuai kebutuhan. Pemeriksaan XML, render 390 piksel, batas teks dan tinjauan
visual berhasil; 7 ekspresi sumber berhasil dirender MathJax. Validator:
1.945 soal, 0 kesalahan struktur. Kumulatif 420 diperiksa, 406 diubah;
sisa 1.525 soal/35 bab. **BELUM SELESAI**; titik lanjut IPAS Bab 7.

### Kelanjutan: IPAS Bab 7

70 butir, kedua modul dan 12 SVG ditulis ulang. ID/jumlah tetap. Arah,
orientasi denah, legenda, rute pulang, kenampakan, serta akses dan keselamatan
diperiksa. Semua SVG lolos XML, render 390 piksel, batas teks dan peninjauan
visual setelah koreksi ikon. Validator: 1.945 soal, 0 kesalahan struktur.
Kumulatif 490 diperiksa, 476 diubah; sisa 1.455 soal/34 bab.
Status **BELUM SELESAI**, titik lanjut IPAS Bab 8.

### Kelanjutan: IPAS Bab 8 — putaran IPAS selesai

70 soal dan kedua modul ditulis ulang; ID/jumlah tetap; 12 SVG diganti dan
lolos XML, render 390 piksel, batas teks serta tinjauan visual. Dua ekspresi
satuan sumber berhasil dirender MathJax. Koreksi membedakan uap/tetes cair,
massa/volume, perubahan wujud/pelarutan, partikel bergetar, dan perilaku oobleck.

IPAS 560/560 diperiksa, 546 berubah. Semua 16 ID modul unik, path ilustrasi
tersedia, 96 SVG valid XML; tidak ada badan soal lengkap identik dalam 560
butir. Validator kanonis lima mapel: 1.945 soal, 0 kesalahan struktur.
Keseluruhan tetap **BELUM SELESAI**: PAI, ENG, PJOK, Seni Rupa masih 1.385
soal/33 bab. Titik lanjut PAI Bab 1, tanpa konversi JSON atau aktivasi aplikasi.

### Kelanjutan: PAI Bab 1

30 butir diperiksa, 23 diubah, kedua modul direvisi berdasarkan buku siswa
dan panduan guru. ID dan jumlah tetap. Audit makna ayat, qalqalah, penyalinan,
hafalan dan penerapan selesai untuk Bab 1. Pratinjau HP teks Arab tertunda
karena Chromium lokal tidak dapat membuat soket. Kumulatif 590 diperiksa;
1.355 soal/32 bab atau unit tersisa. Lanjut PAI Bab 2. Status keseluruhan
tetap **BELUM SELESAI**. Tidak ada konversi JSON atau aktivasi aplikasi.

### Kelanjutan: PAI Bab 2

30 butir diperiksa dan ditulis ulang, serta kedua modul diselaraskan. Sifat
jaiz, pasangan sifat wajib dan mustahil, serta penghayatan Al-Wahhab dan
Al-Kabir diperiksa terhadap Bab II buku siswa dan panduan guru. ID dan jumlah
tetap. Kumulatif 620 diperiksa; 1.325 soal pada 31 bab atau unit tersisa.
Lanjut PAI Bab 3. Pratinjau HP Bab 1 masih tertunda.

### Kelanjutan: PAI Bab 3

30 soal diperiksa dan ditulis ulang. Kedua modul mencakup bakti kepada orang
tua dan guru serta menghormati orang lain. Semua ID tetap. Kumulatif 650
soal diperiksa; 1.295 soal pada 30 bab atau unit tersisa. Lanjut PAI Bab 4.

### Kelanjutan: PAI Bab 4

30 soal diperiksa dan ditulis ulang. Kedua modul mencakup ketentuan dan
hikmah puasa. Semua ID tetap. Kumulatif 680 soal diperiksa; 1.265 soal
pada 29 bab atau unit tersisa. Lanjut PAI Bab 5.
