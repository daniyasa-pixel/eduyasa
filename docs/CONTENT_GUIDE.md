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

## Penulisan matematika (EDUYASA kelas 3 dan seterusnya)

**Wajib** menggunakan teks LaTeX/MathJax untuk notasi matematika, bukan teks operasi biasa. Ini adalah standar yang sama seperti Matematika Kelas 6.

- Inline: `$28+15=43$`, `$4\\times5=20$`, `$20\\div4=5$`, `$\\frac{3}{5}$`, `$90^{\\circ}$`.
- Display (bila rumus perlu berdiri sendiri): `$$\\frac{1}{2}+\\frac{1}{4}=\\frac{3}{4}$$`.
- Satuan dalam ekspresi: `$2\\ \\text{m}=200\\ \\text{cm}$`, `$1\\ \\text{kg}=1000\\ \\text{g}$`.
- Terapkan konsisten pada **modul, pertanyaan, opsi, kunci jawaban, pembahasan**, dan contoh.
- Sumber Markdown **menyimpan backslash LaTeX tunggal** seperti `\\frac`. Jangan mengubahnya menjadi JSON pada tahap produksi. Saat Codex mengonversi, escape backslash sesuai aturan JSON (contoh `\\frac` di Markdown menjadi `\\\\frac` di teks sumber JSON). Setelah JSON diparse harus kembali identik.
- Jangan hilangkan delimiter `$...$` dan `$$...$$`, kurung kurawal, atau perintah LaTeX. Pastikan tidak ada ekspresi templat `${...}` tersisa.
- Teks di dalam gambar SVG adalah label gambar, sedangkan semua rumus yang muncul di **konten teks** disimpan sebagai MathJax.

