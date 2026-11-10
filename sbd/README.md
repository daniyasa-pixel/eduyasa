# Seni Budaya — Seni Rupa Kelas 6

Cakupan produksi: **Seni Rupa**, lima bab berdasarkan *Panduan Guru Seni Rupa
untuk SD/MI Kelas VI (Edisi Revisi)*, Taufik Hidayatulloh dan Taibatus Solehah,
2025, ISBN PDF **978-634-00-3153-9**. Musik, tari, dan teater tidak dimasukkan
ke cakupan ini. Dua belas unit dalam buku 2022 tidak digabungkan dengan bab edisi ini.

Acuan:
- [Katalog resmi SIBI](https://buku.kemendikdasmen.go.id/katalog/panduan-guru-seni-rupa-untuk-sdmi-kelas-vi-edisi-revisi).
- [Salinan buku untuk memeriksa daftar isi dan kegiatan](https://fliphtml5.com/pakta01/Seni_Rupa_BG_KLS_VI_Rev/), daftar isi halaman v–vi; bab pada halaman 27, 69, 105, 145, dan 181.
- [Panduan mata pelajaran Seni Rupa, Puskurjar 2025](https://kurikulum.kemendikdasmen.go.id/file/panduan/dokumen/18.%20Panduan%20Mata%20Pelajaran%20Seni%20Rupa.pdf), acuan kompetensi Fase C.

Materi, soal, dan gambar EDUYASA dibuat sebagai konten pembelajaran asli,
bukan salinan soal atau ilustrasi buku pemerintah. Buku digunakan sebagai
acuan cakupan. Aplikasi mendukung belajar konsep dan refleksi; keterampilan
praktik tetap perlu diamati melalui kegiatan berkarya.

| Bab | Judul | MOD-01 | MOD-02 | Soal biasa | Soal bergambar |
| --- | --- | --- | --- | --- | --- |
| 1 | Menemukan Keindahan di Sekitar Kita | 295 kata | 293 kata | 50 | 20 |
| 2 | Belajar dari Karya Orang Lain | 309 kata | 291 kata | 50 | 20 |
| 3 | Ideku, Gagasanku | 309 kata | 313 kata | 50 | 20 |
| 4 | Karya yang Mewakili Diriku | 309 kata | 305 kata | 50 | 20 |
| 5 | Aku Seniman Cilik | 293 kata | 303 kata | 50 | 20 |

Hitungan kata tidak memasukkan tautan/teks alternatif gambar. Setiap modul
memiliki dua ilustrasi yang menyelingi penjelasan. MOD-01 dan MOD-02 membahas
tujuan belajar yang sama dengan contoh dan alur penyampaian berbeda.

## File dan identitas

- `sbd-bNN-materi.json`: dua modul, identitas `SBD-BNN-MOD-01` dan `MOD-02`.
- `sbd-bNN.json`: 50 soal biasa, ID `SBD-BNN-M01-001`–`050`.
- `sbd-bNNa.json`: 20 soal bergambar, ID `SBD-BNN-M01-051`–`070`.
- `question-packs.json`: daftar file bank untuk setiap bab.
- `assets/`: 68 SVG asli, sekitar **200 KiB** total (204.733 byte pada produksi awal).
  Sepuluh ilustrasi materi digunakan pada dua versi; 58 diagram soal mendukung
  100 pertanyaan bergambar. Penggunaan ulang untuk fokus pengamatan berbeda
  mengurangi ukuran unduhan. SVG dapat diperbesar tanpa kehilangan ketajaman.

Setiap soal mempunyai empat opsi unik, satu kunci berupa teks opsi persis, dan
pembahasan. Pilihan yang dinilai benar didasarkan pada pengamatan, tujuan
tertentu, atau prosedur, bukan selera atau dugaan sifat pribadi pembuat karya.
Pertanyaan tentang warna membedakan model pigmen dasar dan kesan warna;
tidak menyatakan warna selalu mempunyai satu makna emosi yang sama.

## Perilaku aplikasi

SBD menampilkan lima bab pada pemilihan bab. Setiap bab dipilihkan satu versi
materi acak. Latihan tetap **20 soal total per sesi**. Jika SBD atau MTK dipilih,
termasuk sesi campuran, aplikasi mengambil **5 soal bergambar dan 15 tanpa
gambar** dari gabungan bank bab terpilih, tanpa pengulangan. Urutan soal dan
opsi A–D diacak dengan Fisher–Yates. Tidak ada kuota per bab.

Gambar pada materi dan kuis dapat diketuk atau difokuskan lalu ditekan Enter
untuk membuka pembesaran. Tombol +/− mengatur ukuran; gambar dapat digeser.
Dialog dapat ditutup dengan tombol Tutup atau Escape. Audio tidak ditambahkan
karena kompetensi paket ini berfokus pada seni rupa, bukan pengenalan bunyi.

Paket gagal dimuat menghentikan persiapan latihan dengan pesan yang dapat
dicoba lagi. Manifest dan bank soal dimuat tanpa cache browser. URL aplikasi
serta skrip memakai versi rilis `20261110-sbd-1`.

## Menambah bank selanjutnya

Tambahkan file seperti `sbd-b01b.json` dengan `chapter_id` tetap `SBD-B01`, ID
baru yang unik (misalnya mulai `SBD-B01-M01-071`), lalu daftarkan pada manifest.
Tidak perlu membuka atau mengubah bank 50 soal sebelumnya.

## Audit manual pemilik proyek

1. Cocokkan fokus kelima bab dengan buku acuan yang dipakai sekolah.
2. Baca dua modul berdampingan: pastikan konsep inti dan kedalamannya setara.
3. Periksa kejelasan soal, ketunggalan jawaban, kualitas pengecoh, dan pembahasan.
4. Cocokkan diagram dengan pertanyaan serta teks alternatifnya. Perhatikan bahwa
   diagram merupakan ilustrasi: tekstur nyata tetap memerlukan bahan asli.
5. Uji materi dan kuis pada ponsel, pembesaran gambar, hasil, dan laporan.
6. Tinjau keterampilan praktik secara terpisah dari nilai pilihan ganda.

Validasi teknis dan pengujian browser tidak menggantikan audit akademik/manual.
Perintah pengujian: `node --test tests/quiz-bank.test.cjs`.

## Hasil pemeriksaan sebelum penerbitan

- 22 pengujian Node lulus, termasuk regresi MTK, lima bab SBD, beberapa bab,
  sesi campuran, paket tambahan gagal, dan pemeriksaan integritas konten.
- 700 percobaan pengambilan SBD (lima bab tunggal, beberapa bab, dan SBD+MTK)
  menghasilkan tepat lima gambar dan lima belas soal biasa tanpa duplikasi.
- Chromium mobile 390 × 844: kedua versi materi kelima bab dimuat, semua gambar
  materi tampil, pembesaran melalui sentuhan/keyboard dan tombol +/− bekerja.
- Lima sesi kuis penuh (satu per bab), masing-masing 20 soal, selesai dengan nilai
  100 saat semua jawaban benar. Gambar kuis tampil dan laporan memuat nama mapel/bab.
- Beberapa bab, SBD+MTK, SBD+BIN, kegagalan paket 503, dan coba lagi berhasil diuji.
- Seluruh 68 SVG dimuat serta diperiksa secara visual melalui lembar pratinjau.
  Tidak ada error JavaScript tak tertangani atau overflow horizontal pada alur mobile.

Pengujian di atas memakai checkout lokal untuk respons data GitHub, agar dapat
memeriksa seluruh variasi secara terkontrol. Verifikasi versi publik dilakukan
terpisah sesudah GitHub Pages terbit. Android WebView belum diuji.
