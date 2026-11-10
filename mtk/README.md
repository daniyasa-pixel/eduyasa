# Paket soal tambahan Matematika

Setiap bab sekarang mempunyai dua file terpisah:

| Bab | Bank awal (50 soal) | Paket A (20 soal bergambar) |
| --- | --- | --- |
| Pecahan dan Desimal | `mtk-b01.json` | `mtk-b01a.json` |
| Rasio | `mtk-b02.json` | `mtk-b02a.json` |
| Kubus dan Balok | `mtk-b03.json` | `mtk-b03a.json` |
| Peluang | `mtk-b04.json` | `mtk-b04a.json` |

Semua paket pada baris yang sama memakai `chapter_id` yang sama. ID soal awal
`MTK-BNN-M01-001`–`050` dipertahankan. Paket A menggunakan ID unik
`MTK-BNN-M01-051`–`070`. Huruf pada **nama file** menunjukkan paket tambahan;
`M01` dalam ID tetap merupakan grup identitas historis, bukan versi materi.
Gambar SVG asli dibuat untuk soal ini dan disimpan dalam `mtk/assets/`.

## Menambah paket B atau berikutnya

1. Buat file baru, misalnya `mtk-b01b.json`, dengan struktur `chapters/questions`
   yang sama dan `chapter_id: "MTK-B01"`.
2. Gunakan ID soal baru yang belum dipakai di paket mana pun untuk bab itu
   (misalnya mulai `MTK-B01-M01-071`). Jangan mengubah ID atau isi bank lama.
3. Simpan aset gambar dan gunakan field `image: { "src": "URL", "alt": "deskripsi" }`
   jika soal memang memerlukan gambar. Pastikan URL dapat dibuka setelah deployment.
4. Tambahkan `"mtk/mtk-b01b.json"` ke daftar `MTK-B01` dalam
   `question-packs.json`. Frontend membaca daftar ini; nama file baru tidak
   otomatis ditemukan hanya dengan mengunggahnya.
5. Jalankan `node --test tests/quiz-bank.test.cjs` dari root repository.

## Aturan pengambilan soal

Jumlah latihan tetap **20 soal total per sesi**. Jika Matematika dipilih,
termasuk saat dipasangkan dengan mapel lain, aplikasi menggabungkan seluruh
paket dari bab/mapel terpilih, memisahkan soal bergambar dan tanpa gambar,
lalu mengambil tepat **5 soal bergambar (25%) dan 15 soal tanpa gambar (75%)**
tanpa pengulangan. Dalam sesi campuran, gambar dari mapel pasangan juga masuk
kelompok bergambar. Kedua kelompok dan urutan akhirnya diacak dengan
Fisher–Yates. Tidak ada kuota per bab; setiap soal selalu berasal dari bab terpilih.

Sesi yang tidak memilih Matematika tetap mengambil 20 soal acak seperti sebelumnya.
Jika paket gagal dimuat atau kuota gambar/teks kurang, aplikasi menampilkan pesan
dan tidak memulai latihan dengan komposisi yang salah. Tombol dapat dicoba lagi.
Proporsi bank (50:20) berbeda dari proporsi pengambilan latihan (15:5).
