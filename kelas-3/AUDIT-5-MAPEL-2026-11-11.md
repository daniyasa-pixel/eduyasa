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
