# Audit Kemandirian Soal Kelas 6 — Pengerjaan 5

Tanggal: 23 November 2026 (Asia/Jakarta). Repository: `daniyasa-pixel/eduyasa`; cabang operasional `main`. Baseline sebelum pekerjaan: `f311eac1ad69a1c380301db1cdef3ec6d73286f7`.

## Cakupan

Pengerjaan kelima mencakup **lima bab**, yaitu Matematika Bab 4 (Peluang) serta PPKn Bab 1–4. Soal lama dipertahankan: tidak ada soal dihapus, ditambah, atau diubah ID-nya. Struktur JSON lama digunakan.

| Bank JSON | Soal diperiksa | Butir berubah |
|---|---:|---:|
| `mtk/mtk-b04.json` | 50 | 14 |
| `mtk/mtk-b04a.json` | 20 | 16 |
| `pkn/pkn-b01.json` | 50 | 32 |
| `pkn/pkn-b02.json` | 50 | 30 |
| `pkn/pkn-b03.json` | 50 | 40 |
| `pkn/pkn-b04.json` | 50 | 30 |
| **Total** | **270** | **162** |

Satu butir dihitung berubah meskipun hanya media, pembahasan, atau pemformatan jawaban yang berubah. **162 butir berubah tidak berarti 162 butir semula kehilangan cerita**; sebagian revisi memperjelas redaksi, format rumus, atau memperbaiki ambiguitas.

## Perubahan akademik dan kemandirian

### Matematika Bab 4 — Peluang

- Audit 50 soal biasa dan 20 soal bergambar. Sebanyak 14 butir utama diperjelas agar kondisi peluang, seperti dadu seimbang, jumlah kartu, dan kesempatan memilih, tegas dalam stem soal.
- Pada 8 soal bergambar, stem diubah agar siswa langsung diminta melihat kotak/kartu/roda **pada gambar yang melekat pada soal itu sendiri**.
- Bentuk pecahan pada opsi, kunci, dan pembahasan yang memerlukannya diformat sebagai LaTeX/MathJax, dengan kesamaan teks kunci dan salah satu opsi tetap terjaga.
- Ke-20 SVG `mtk/assets/mtk-b04a-001.svg` sampai `mtk-b04a-020.svg` diperiksa isi label dan deskripsinya: jumlah bola, kartu, sektor roda, atau nilai tabel sesuai dengan representasi data setiap soal. Tidak ada gambar yang diambil dari MOD-01/MOD-02 atau soal lain.

### PPKn Bab 1–4

Audit menemukan pola produksi yang berulang: pertanyaan jenis “sikap atau pemahaman yang perlu dihindari” justru menyimpan **peringatan yang benar** (misalnya larangan memaksakan kehendak) sebagai kunci, sehingga batang soal dan jawaban tidak selaras. Perbaikannya:

- 10 soal per bab (40 seluruhnya) diubah menjadi pertanyaan tentang **peringatan atau batasan yang benar**. Dengan demikian, kunci yang berbentuk larangan tidak lagi disajikan sebagai tindakan buruk yang harus dipilih siswa.
- 10 soal per bab (40 seluruhnya) yang sebelumnya meminta “situasi” tanpa memberikan situasi, sekarang mempunyai kasus lengkap di stem masing-masing. Tidak bergantung pada cerita dalam soal lain.
- 10 soal per bab (40 seluruhnya) yang pembahasannya berbunyi “Materi bab menjelaskan bahwa…” mendapat penjelasan yang berdiri sendiri, tanpa merujuk ke salah satu versi materi.
- PPKn Bab 1 memperjelas pedoman menolong tanpa pamrih. PPKn Bab 2 dan 4 masing-masing memperbaiki sebuah pengecoh yang terlalu mirip dengan jawaban benar.
- PPKn Bab 3 mengoreksi pernyataan tentang norma hukum: aturan resmi ditetapkan lembaga berwenang dan berlaku bagi setiap orang dalam cakupan yurisdiksinya, bukan hanya warga negara atau semata dibuat bersama lembaga legislatif. Penjelasan MOD-01 dalam `pkn/pkn-b03-materi.json` diselaraskan. Rumusan peringatan tentang hak identitas, bermain, dan pendidikan juga diperjelas agar tidak bermakna ganda.

Semua soal PPKn yang ditinjau berbentuk teks tanpa ketergantungan wajib pada gambar/audio eksternal. ID lama `PKN-BNN-NNN` sengaja dipertahankan (tidak dikonversi ke skema ID baru).

## Perbandingan ID butir yang berubah

- **mtk/mtk-b04.json**: MTK-B04-M01-010, MTK-B04-M01-012, MTK-B04-M01-017, MTK-B04-M01-020, MTK-B04-M01-026, MTK-B04-M01-027, MTK-B04-M01-029, MTK-B04-M01-034, MTK-B04-M01-036, MTK-B04-M01-038, MTK-B04-M01-046, MTK-B04-M01-047, MTK-B04-M01-049, MTK-B04-M01-050.
- **mtk/mtk-b04a.json**: MTK-B04-M01-051, MTK-B04-M01-054, MTK-B04-M01-055, MTK-B04-M01-056, MTK-B04-M01-057, MTK-B04-M01-058, MTK-B04-M01-059, MTK-B04-M01-060, MTK-B04-M01-061, MTK-B04-M01-062, MTK-B04-M01-063, MTK-B04-M01-064, MTK-B04-M01-065, MTK-B04-M01-067, MTK-B04-M01-069, MTK-B04-M01-070.
- **pkn/pkn-b01.json**: PKN-B01-001, PKN-B01-003, PKN-B01-005, PKN-B01-006, PKN-B01-008, PKN-B01-010, PKN-B01-011, PKN-B01-013, PKN-B01-015, PKN-B01-016, PKN-B01-018, PKN-B01-020, PKN-B01-021, PKN-B01-022, PKN-B01-023, PKN-B01-024, PKN-B01-025, PKN-B01-026, PKN-B01-028, PKN-B01-030, PKN-B01-031, PKN-B01-033, PKN-B01-035, PKN-B01-036, PKN-B01-038, PKN-B01-040, PKN-B01-041, PKN-B01-043, PKN-B01-045, PKN-B01-046, PKN-B01-048, PKN-B01-050.
- **pkn/pkn-b02.json**: PKN-B02-001, PKN-B02-003, PKN-B02-005, PKN-B02-006, PKN-B02-008, PKN-B02-010, PKN-B02-011, PKN-B02-013, PKN-B02-015, PKN-B02-016, PKN-B02-018, PKN-B02-020, PKN-B02-021, PKN-B02-023, PKN-B02-025, PKN-B02-026, PKN-B02-028, PKN-B02-030, PKN-B02-031, PKN-B02-033, PKN-B02-035, PKN-B02-036, PKN-B02-038, PKN-B02-040, PKN-B02-041, PKN-B02-043, PKN-B02-045, PKN-B02-046, PKN-B02-048, PKN-B02-050.
- **pkn/pkn-b03.json**: PKN-B03-001, PKN-B03-002, PKN-B03-003, PKN-B03-004, PKN-B03-005, PKN-B03-006, PKN-B03-008, PKN-B03-010, PKN-B03-011, PKN-B03-013, PKN-B03-015, PKN-B03-016, PKN-B03-018, PKN-B03-020, PKN-B03-021, PKN-B03-022, PKN-B03-023, PKN-B03-024, PKN-B03-025, PKN-B03-026, PKN-B03-028, PKN-B03-030, PKN-B03-031, PKN-B03-032, PKN-B03-033, PKN-B03-034, PKN-B03-035, PKN-B03-036, PKN-B03-037, PKN-B03-038, PKN-B03-039, PKN-B03-040, PKN-B03-041, PKN-B03-042, PKN-B03-043, PKN-B03-044, PKN-B03-045, PKN-B03-046, PKN-B03-048, PKN-B03-050.
- **pkn/pkn-b04.json**: PKN-B04-001, PKN-B04-003, PKN-B04-005, PKN-B04-006, PKN-B04-008, PKN-B04-010, PKN-B04-011, PKN-B04-013, PKN-B04-015, PKN-B04-016, PKN-B04-018, PKN-B04-020, PKN-B04-021, PKN-B04-023, PKN-B04-025, PKN-B04-026, PKN-B04-028, PKN-B04-030, PKN-B04-031, PKN-B04-033, PKN-B04-035, PKN-B04-036, PKN-B04-038, PKN-B04-040, PKN-B04-041, PKN-B04-043, PKN-B04-045, PKN-B04-046, PKN-B04-048, PKN-B04-050.

## Validasi

- Enam bank JSON pada `main` dibaca kembali setelah commit: **270 soal** dengan **270 ID unik**.
- Semua ID dan jumlah soal dibandingkan dengan baseline sebelum pekerjaan: tetap sama.
- Semua soal mempunyai empat opsi berbeda; teks jawaban cocok secara persis dengan salah satu opsi.
- Tidak ada ketergantungan wajib pada gambar, soal sebelumnya, atau versi MOD yang terdeteksi oleh pemeriksaan pola teks; 20 gambar pada paket peluang berada pada butir masing-masing.
- **250 sesi simulasi sederhana**: untuk setiap sesi dipilih 5 soal bergambar dan 15 teks dari bank yang diaudit. Hasil: 20 ID unik/sesi, 5 media/sesi, dan jawaban tetap cocok setelah pengacakan opsi, tanpa anomali pada simulasi.
- Simulasi di atas menguji **struktur dan pengacakan buatan**, bukan eksekusi langsung `HTML/quiz-bank.js` atau uji di HP. Masih diperlukan pengujian visual serta MathJax dan gambar pada browser/WebView asli.
- Pengujian tidak menggantikan review pedagogis mendalam atas kualitas seluruh distraktor; beberapa butir PPKn masih mempunyai pola tema sejenis dan dapat dipertajam di audit berikutnya.

## Riwayat perubahan utama

- Matematika Bab 4: `086bff587a76a2ab86d0750d4dfa425c3b927288` dan `6f4c5a6cc1e00243a0b1f5a8f148a8af368b08a7`.
- PPKn Bab 1: `2ba8e1b92bd95b122651efc5d186d0eab02ace18`; redaksi lanjutan `c88e04f8c5f3703622e8783177e80db67858cc58`.
- PPKn Bab 2: `66a7a4ac4ff9a9dd1b65da24ebd39e813ca93b20`; perbaikan pengecoh `5c7f0098edc3b3077c1326ca1a1558419fe9c310`.
- PPKn Bab 3: `9bcbed340d50ffadf12c6d8d2889afcfda9841b1`; redaksi lanjutan `645be497e20da2e4941350aad2f04b05ccf5edea`; penyelarasan modul `288cf2cefbe79c643d372f46a0c009b6c3cf6c2b`.
- PPKn Bab 4: `b138e1aac0250894facee494280bde14a0658438`; perbaikan pengecoh `c2bc7e66eddbd534d054ff9afbf87eabc3e1b4f0`.

## Pekerjaan berikutnya

Lanjut ke **Pengerjaan 6**, memprioritaskan PPKn Kelas 6 Bab 5–7, lalu mapel lain yang belum memperoleh audit soal mandiri khusus. Tetap gunakan urutan audit satu bab, revisi, dan validasi sebelum bergeser ke bab berikutnya. Jangan mengubah jumlah soal atau ID.
