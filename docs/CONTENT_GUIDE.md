# Ketentuan konten EDUYASA

Aturan berikut merangkum keputusan pemilik proyek untuk paket selanjutnya.

- Tetapkan cabang, kelas, kurikulum, edisi buku, dan bab sebelum produksi.
  Bedakan judul bab utama dengan kegiatan atau submateri. Catat sumbernya.
- Setiap bab memiliki MOD-01 dan MOD-02 dengan cakupan kompetensi setara.
  Keduanya ringkas dan memakai bahasa yang mudah dipahami siswa pada kelas sasaran.
- **Mulai paket Seni Rupa ini dan untuk modul baru selanjutnya, gabungkan
  penjelasan dengan gambar yang membantu pemahaman.** Jangan menjadikan
  seluruh materi berupa gambar. Sertakan teks alternatif yang bermakna.
- Utamakan SVG untuk diagram dan ilustrasi yang cocok dibuat sebagai vektor.
  Gambar harus akurat dan relevan; gambar raster boleh dipilih bila lebih sesuai.
  SVG adalah format gambar; CSV adalah format data tabel.
- Bank dasar berisi 50 soal biasa per bab, dengan pengecualian PAI 30 sesuai
  keputusan sebelumnya. Paket yang meminta variasi gambar mempunyai tambahan
  20 soal bergambar pada file `kode-bNNa.json`, bukan menulis ulang bank dasar.
- Pertahankan ID bab dan semua ID lama. Gunakan ID soal baru yang unik lintas
  paket. Jawaban disimpan sebagai teks opsi, bukan posisi sebelum pengacakan.
- Soal pilihan ganda memiliki tepat empat opsi berbeda, satu jawaban yang jelas,
  serta pembahasan beralasan. Variasikan pengamatan, pemahaman, penerapan,
  analisis sederhana, dan evaluasi proses. Jangan menilai selera sebagai fakta.
- Audio digunakan ketika mendukung kompetensi. Bedakan simulasi nada/irama
  dengan rekaman alat musik asli dan jangan memberi label yang menyesatkan.
- Penerbitan perlu disertai pemeriksaan JSON, ID, media, pengacakan, alur siswa,
  dan laporan tentang batas pengujian. Audit teknis dan akademik dipisahkan.

Ketentuan gambar berlaku pada konten baru; materi mata pelajaran lama tetap
memerlukan audit tersendiri sebelum diperbarui.

## Kelas tambahan

Kelas 6 tetap memakai folder mapel di akar dan ID lama. Kelas baru memakai
`kelas-N/mapel/` serta awalan ID `KNN-`, misalnya `K03-BIN-B01`. Nama file
tetap `bin-b01.json`, `bin-b01a.json`, dan `bin-b01-materi.json`.
Konfigurasi `HTML/grade-config.js` menentukan lokasi, mapel tersedia,
manifest paket tambahan, dan kuota gambar. Parameter `?kelas=3` memilih
Kelas 3; URL lama tanpa parameter tetap memilih Kelas 6. Kelas yang belum
tersedia tidak boleh diam-diam diarahkan ke bank kelas lain.

Materi, bank, ID laporan, dan pemeriksaan koneksi harus mengikuti kelas
pilihan. Kelas 3 menggunakan 20 soal per sesi: 5 bergambar dan 15 biasa;
soal dan opsi jawaban diacak. Jumlah ini berbeda dari bank 70 soal per bab.
