# HANDOVER CODEX — PKN KELAS 3 EDUYASA

## Misi

Konversi sumber Markdown empat bab Pendidikan Pancasila Kelas 3 ke JSON sesuai `docs/CONTENT_GUIDE.md` dan pola `kelas-3/bin/`; sambungkan datanya ke aplikasi, **tanpa mengubah teks pengajaran, kunci, opsi, penjelasan, atau aset secara diam-diam**. Gunakan branch `main` repo `daniyasa-pixel/eduyasa` dan periksa status sebelum menulis.

## Sumber lengkap

Masing-masing `pkn-b01`, `pkn-b02`, `pkn-b03`, `pkn-b04` mempunyai:

1. `pkn-bNN-materi.md`: dua versi materi ber-ID `K03-PKN-BNN-MOD-01` / `MOD-02`; gambar telah disisipkan di tengah konten. Kedua versi setara cakupan dan aplikasi memilih 1 secara acak.
2. `pkn-bNN-soal-biasa.md`: 50 soal tanpa gambar, ID `K03-PKN-BNN-M01-001` hingga `050`.
3. `pkn-bNN-soal-bergambar.md`: 20 soal terpisah, ID `051` hingga `070`, 10 gambar berbeda yang masing-masing dipakai 2 soal.
4. `assets/pkn-bNN-gambar-01.svg` sampai `10.svg` dan `assets/pkn-bNN-materi-01.svg` sampai `02.svg`.

Catatan: File yang berisi pilihan jawaban memuat 4 bullet tanpa label A/B/C/D. Kunci ditulis sebagai **teks jawaban persis**, bukan huruf. Pertahankan teks pembahasan. Link gambar Markdown mengarah ke URL raw yang dapat dipakai sebagai `image.src`; alt-nya dipindahkan ke `image.alt`.

## Target konversi, belum dibuat saat handover ini

- `pkn-bNN-materi.json` -> top-level `subject_id: PKN`, `subject_name: Pendidikan Pancasila`, `grade: 3`, `curriculum: Kurikulum Merdeka`, `chapter_id: K03-PKN-BNN`, `chapter_title`, `modules[]` berisi 2 entri dan properti `content` Markdown.
- `pkn-bNN.json` -> 50 soal tanpa gambar.
- `pkn-bNNa.json` -> 20 soal dengan `image: {src,alt}`.
- `question-packs.json` bila diperlukan -> manifest kedua paket untuk setiap bab. Ikuti pemetaan relatif seperti `kelas-3/bin/question-packs.json`.

## Integrasi aplikasi

Periksa `HTML/grade-config.js` dan pola untuk BIN Kelas 3. Tambahkan PKN Kelas 3 setelah JSON tervalidasi tanpa memengaruhi pemetaan kelas lain. Kuota **20 soal per sesi: 15 biasa + 5 bergambar**, dari gabungan bab yang dipilih, dan pilihan jawaban diacak. Jangan mengambil 20 + 20 atau memperlakukan 70 soal sebagai yang harus tampil semuanya. Soal dengan gambar tetap dapat diacak dalam kumpulan 20.

## Pengujian minimal

1. Semua jumlah, ID unik, empat opsi berbeda, kunci cocok salah satu opsi.
2. Dua modul tiap bab dan ketercapaian tujuan, gambar tetap di tengah materi (bukan dihilangkan).
3. Semua 48 SVG berhasil dimuat; diagram yang diuji dalam soal tetap sesuai arah/label; cek kesesuaian alternatif teks.
4. Kelas 3 PKN dapat dipilih; hasil dan label WhatsApp menyebut kelas dan mapel benar.
5. Sesi 20 soal tepat 15/5 secara acak; multi-bab dan opsi acak berfungsi; saat gagal memuat paket gambar ada pesan gagal yang jelas.
6. Regresi Kelas 6 serta Bahasa Indonesia Kelas 3 tidak rusak.

**Sumber resmi buku:** https://buku.kemendikdasmen.go.id/katalog/pendidikan-pancasila-untuk-sdmi-kelas-iii

**Batas:** Sumber ini telah melalui audit format dan keterhubungan nama aset; audit visual dan pedagogis menyeluruh tetap diperlukan sebelum dipakai siswa.
