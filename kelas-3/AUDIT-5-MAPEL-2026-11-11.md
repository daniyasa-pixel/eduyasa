# Audit dan revisi soal EDUYASA Kelas 3 — 2026-11-11

**Cakupan eksklusif:** IPAS, Bahasa Inggris, PAI, PJOK, Seni Rupa. BIN, MTK, PKN tidak disentuh.

**STATUS KESELURUHAN: BELUM LULUS / BELUM FINAL.** Audit struktur dan pola berulang telah dilakukan. Revisi akademik menyeluruh, validasi visual, dan verifikasi per-butir terhadap buku resmi **belum selesai**. Jangan konversi atau rilis sebagai bank final.

| Mapel | Bab | Soal | Temuan yang terverifikasi | Revisi selesai | Sisa pekerjaan |
|---|---:|---:|---|---|---|
| IPAS | 8 | 560 | Soal teks menggunakan tiga pola dominan; pembahasan formulaik; pilihan jawaban gambar ada yang tidak relevan | Redaksi dan pembahasan soal teks 8 bab diperbarui tahap pertama | Tulis ulang substantif 560 butir, distraktor, audit konsep, gambar dan pembahasan |
| ENG | 10 | 500 | Soal teks tiga pola dominan per unit; gambar dan listening satu pola per unit | Belum | Variasi kompetensi bahasa, stimulus gambar, audio TTS dan uji WebView |
| PAI | 10 | 300 | Soal berulang dan sebagian identik; ID tanpa awalan K03 | Awalan ID soal dan modul 10 bab sudah diperbaiki | Tulis ulang duplikasi, audit ayat/hadis/tajwid, ketepatan materi dan pembahasan |
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
