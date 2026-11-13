# PJOK Kelas 3 — EDUYASA

Acuan perencanaan: Buku Panduan Guru PJOK SD/MI Kelas III (Muhajir dan Umi Hariyani, 2022), delapan unit pembelajaran.

1. Aktivitas Pola Gerak Dasar Lokomotor
2. Aktivitas Pola Gerak Dasar Nonlokomotor
3. Aktivitas Pola Gerak Dasar Manipulatif
4. Aktivitas Gerak Dominan Senam
5. Aktivitas Gerak Berirama
6. Aktivitas Permainan dan Olahraga di Air
7. Aktivitas Kebugaran untuk Kesehatan
8. Aktivitas Fisik dan Memilih Makanan Bergizi

**Paket sumber:** 280 soal teks + 80 soal bergambar = **360 soal**, delapan unit @45 soal. ID dipertahankan `K03-PJK-B##-M01-001` s.d. `045`.

**Checkpoint 2026-11-12:** Seluruh **360 sumber soal sudah mengalami revisi redaksi**. Unit 1: 45 soal; Unit 2: 45 soal (5 direvisi pada batch sebelumnya, 40 dilanjutkan); Unit 3–8: masing-masing 45 soal. Soal teks digeser dari pengulangan definisi menjadi situasi aktivitas, prinsip gerak dan keselamatan, walaupun beberapa butir tetap menguji konsep yang sama dengan variasi konteks. Seluruh 80 SVG soal diperbarui agar tidak memuat label jawaban langsung; visual 70 SVG Unit 2–8 masih menggunakan simbol sederhana dan **tidak boleh dianggap gambar gerak yang memadai tanpa audit visual/peningkatan ilustrasi**.

Pemeriksaan sumber setelah revisi: **360 soal, 360 ID unik, 360 stem unik, empat opsi berbeda dan satu kunci identik dengan opsi, pembahasan ada, 0 kesalahan pemeriksaan struktur lokal**.

**STATUS: REVISI SUMBER SELESAI, AUDIT FINAL BELUM.** Belum dilaksanakan verifikasi bab demi bab terhadap buku resmi, validasi jawaban semantik dan pengecoh, rendering/peninjauan visual penuh pada HP 390 px, uji pembelajaran oleh guru atau siswa, maupun validator Python keseluruhan `scripts/check-k03-question-sources.py`. Perlu pemeriksaan rinci khusus unit senam dan air; jangan langsung konversi JSON.

**Modul pembelajaran:** Unit 1–7 sudah masing-masing memiliki MOD-01 dan MOD-02. Unit 8 masih belum memiliki modul dan tetap menjadi kekurangan yang harus diselesaikan.

Catatan serah-terima dan progres lihat `../PROGRES-REVISI-1945.md` dan `../HANDOVER-KONVERSI-JSON.md`.

## Checkpoint konversi JSON — 12 November 2026

**Bank soal seluruh Unit 1–8 sudah dikonversi:** 16 JSON bank (`pjk-bNN.json` 35 teks + `pjk-bNNa.json` 10 bergambar per unit), 360/360 soal. Manifest `question-packs.json` dibuat. Audit JSON vs sumber Markdown langsung: 360 ID unik berurutan dan semua field/opsi/kunci/pembahasan/path+alt gambar cocok, **0 mismatch**. Keterangan historis `jangan langsung konversi JSON` pada bagian sebelumnya digantikan instruksi terbaru `../HANDOVER-KONVERSI-JSON.md`: audit **hasil JSON**, bukan konten akademik pra-konversi. **Modul PJOK belum tersedia, status NOT_AVAILABLE_IN_SOURCE**, sehingga belum dibuat berkas materi JSON. Menu belum diaktifkan untuk Kelas 3; perangkat HP/WebView belum diuji.


## Audit final Unit 1 — 13 November 2026

Unit 1 selesai diaudit terhadap buku guru resmi: 45 soal diperiksa, 10 soal
bergambar dan 10 SVG direvisi, serta dua modul baru dibuat. Bank teks lama
dipertahankan karena sudah sesuai setelah pemeriksaan isi. JSON
`pjk-b01a.json` berstatus **STALE_SOURCE_CHANGED**; `pjk-b01-materi.json`
belum ada dan perlu dibuat pada tahap konversi terpisah. Uji praktik siswa dan
render HP/WebView tetap diperlukan.


## Audit final Unit 2 — 13 November 2026

Unit 2 selesai diaudit terhadap buku guru resmi. Seluruh 45 soal ditulis ulang
karena sumber lama mengulang pola yang sama; 10 SVG diganti dan dua modul baru
dibuat. JSON `pjk-b02.json` dan `pjk-b02a.json` berstatus
**STALE_SOURCE_CHANGED**; `pjk-b02-materi.json` belum ada dan perlu dibuat
pada tahap konversi terpisah. Uji praktik siswa dan HP/WebView tetap diperlukan.


## Audit final Unit 3 — 13 November 2026

Unit 3 selesai diaudit terhadap buku guru resmi. Seluruh 45 soal ditulis ulang untuk menghapus pengulangan pola; cakupan dipusatkan pada variasi melempar, menangkap, menendang, dan menahan bola. Sepuluh SVG diganti dengan stimulus fungsional dan dua modul baru dibuat. JSON `pjk-b03.json` dan `pjk-b03a.json` berstatus **STALE_SOURCE_CHANGED**; `pjk-b03-materi.json` belum ada dan perlu dibuat pada tahap konversi terpisah. Uji praktik siswa serta HP/WebView tetap diperlukan.


## Audit final Unit 4 — 13 November 2026

Unit 4 selesai diaudit terhadap buku guru resmi. Seluruh 45 soal ditulis ulang karena sumber lama mengulang pola dan belum memusatkan tujuan resmi keseimbangan, sikap lilin, serta lompat kangkang. Sepuluh SVG diganti dengan stimulus fungsional dan dua modul baru dibuat. Instruksi latihan berdiri dengan kepala tidak diberikan sebagai latihan mandiri; sumber menegaskan pengawasan, matras, alat stabil, progresi rendah, area pendaratan kosong, dan penghentian saat nyeri. JSON `pjk-b04.json` dan `pjk-b04a.json` berstatus **STALE_SOURCE_CHANGED**; `pjk-b04-materi.json` perlu dibuat pada tahap konversi terpisah. Uji praktik siswa dan HP/WebView tetap diperlukan.


## Audit final Unit 5 — 13 November 2026

Unit 5 selesai diaudit terhadap buku guru resmi. Seluruh 45 soal ditulis ulang untuk menghapus pengulangan definisi dan memperluas cakupan langkah kaki, ayunan lengan, koordinasi, tempo, kreativitas, refleksi, dan keselamatan. Sepuluh SVG diganti dengan stimulus fungsional dan dua modul baru dibuat. Permainan kelompok disajikan tanpa hukuman atau tindakan yang mempermalukan anak. JSON `pjk-b05.json` dan `pjk-b05a.json` berstatus **STALE_SOURCE_CHANGED**; `pjk-b05-materi.json` perlu dibuat pada tahap konversi terpisah. Uji praktik siswa dan HP/WebView tetap diperlukan.


## Audit final Unit 6 — 13 November 2026

Unit 6 selesai diaudit terhadap Unit Pembelajaran 6 buku guru resmi. Seluruh 45 soal ditulis ulang karena sumber lama berulang dan belum mencakup posisi tubuh, gerak kaki, gerak lengan, pernapasan, pengawasan, serta respons keadaan darurat secara memadai. Sepuluh SVG diganti dengan stimulus fungsional dan dua modul baru dibuat. Keselamatan menegaskan rasio acuan paling banyak 10 siswa per guru, pemeriksaan kesehatan, zona dangkal, larangan mendorong/berlari, alat bantu bukan pengganti pengawasan, tanpa lomba menahan napas, dan panggilan kepada petugas terlatih bila ada kesulitan. JSON `pjk-b06.json` dan `pjk-b06a.json` berstatus **STALE_SOURCE_CHANGED**; `pjk-b06-materi.json` perlu dibuat pada tahap konversi terpisah. Uji praktik siswa dan HP/WebView tetap diperlukan.


## Audit final Unit 7 — 13 November 2026

Unit 7 selesai diaudit terhadap Unit Pembelajaran 7 buku guru resmi. Seluruh 45 soal ditulis ulang agar fokus pada kekuatan otot dan daya tahan otot; 10 SVG diganti menjadi stimulus fungsional dan dua modul baru dibuat. Keselamatan menegaskan tahanan sesuai usia, teknik lebih penting daripada jumlah, variasi pemula, pasangan seimbang, tanpa instruksi “sekuatnya”, tanpa beban berat, tanpa tarikan kepala/leher, tanpa melilit tali, dan tanpa melepaskan tali mendadak. JSON `pjk-b07.json` dan `pjk-b07a.json` berstatus **STALE_SOURCE_CHANGED**; `pjk-b07-materi.json` perlu dibuat pada tahap konversi terpisah. Uji praktik siswa dan HP/WebView tetap diperlukan.
