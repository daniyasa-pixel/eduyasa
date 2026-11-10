# PJOK Kelas 6 — paket lima bab

Acuan: *Panduan Guru Pendidikan Jasmani, Olahraga, dan Kesehatan untuk SD/MI
Kelas VI*, Tri Agus Prasetijo dan Alda Indrawan, 2025, ISBN PDF
978-634-00-3177-5. [Salinan buku pemerintah](https://static.sc.cloudapp.web.id/content/pdf/bukuteks/kurikulum21/PJOK_BG_KLS_VI.pdf).
Daftar isi halaman v–vii memuat lima bab; sembilan unit buku 2022 tidak dicampurkan.

| Bab | Pokok bahasan | Modul | Soal biasa | Tambahan bergambar |
| --- | --- | --- | --- | --- |
| 1 | Jago Bergerak: Taktik Juara | 2 | 50 | 20 |
| 2 | Sahabat Lapangan: Serunya Main Adil dan Kompak! | 2 | 50 | 20 |
| 3 | Energi Super Rahasia Tubuh Sehat dan Kuat | 2 | 50 | 20 |
| 4 | Makanan Sehat Bahan Bakar untuk Bergerak | 2 | 50 | 20 |
| 5 | Pahlawan Cilik: Siap Menolong dan Jaga Diri | 2 | 50 | 20 |

## Aturan produksi

Kode internal tetap `PJK`; nama tampilan PJOK. Setiap bab mempunyai
`pjk-bNN-materi.json`, `pjk-bNN.json` (50 soal), dan `pjk-bNNa.json` (20 soal
bergambar). ID baru `PJK-BNN-M01-001`–`070`; dua materi memakai
`PJK-BNN-MOD-01/02`. Semua file didaftarkan dalam `question-packs.json`.
Paket berikutnya bisa ditambah sebagai file baru, misalnya `pjk-b01b.json`,
dengan ID baru dan pendaftaran manifest, tanpa menulis ulang bank lama.

Dua versi materi membahas sepuluh fokus yang sama per bab, dengan penyajian
konsep dan penyajian situasi. Tiap versi memiliki dua ilustrasi di sela teks.
Bank biasa memuat pengenalan konsep, keputusan situasional, alasan, prediksi
risiko, dan perbaikan keputusan. Paket gambar memakai ilustrasi situasi dan
perbandingan dua tindakan; dua pertanyaan pada satu gambar memiliki fokus
pilihan tindakan dan alasan. Teks pada diagram membantu pemahaman, bukan
rekaman atau demonstrasi teknik fisik. Soal, materi, dan SVG merupakan konten
asli EDUYASA, bukan salinan soal buku.

Jumlah: 10 modul, 250 soal biasa, 100 soal bergambar, dan 60 SVG sekitar
165 KiB (169.138 byte). Sepuluh ilustrasi materi dan 50 ilustrasi situasi dipakai
ulang untuk menghemat ruang. Semua dapat diperbesar melalui fitur gambar.

Sesi yang memilih PJOK, MTK, atau SBD mengambil tepat 20 soal total:
5 bergambar + 15 tanpa gambar, termasuk sesi campuran. Soal dan opsi diacak,
tanpa pengulangan ID; tidak ada kuota per bab. Audio tidak diperlukan untuk
kompetensi paket ini. Versi tautan rilis: `20261111-pjk-1`.

## Pemeriksaan dasar kesehatan dan keselamatan

- [WHO, aktivitas usia 5–17](https://www.who.int/europe/news-room/fact-sheets/item/physical-activity): rata-rata sedikitnya 60 menit aktivitas sedang hingga berat per hari sepanjang minggu, bukan kewajiban latihan berat terus-menerus tanpa jeda. Penyesuaian kondisi perlu pendampingan.
- [Kemenkes, Isi Piringku](https://ayosehat.kemkes.go.id/isi-piringku-pedoman-makan-kekinian-orang-indonesia): sekitar setengah piring sayur dan buah, sisanya makanan pokok dan lauk. Tidak menetapkan diet medis atau target berat badan anak.
- [NHS, sprains and strains](https://www.nhs.uk/conditions/sprains-and-strains/): konsep bantuan awal cedera tertentu dibedakan dari penanganan cedera berat. Kompres dingin memerlukan pelindung kulit; balutan dan posisi bantuan memerlukan penilaian orang dewasa yang memahami caranya.
- [British Red Cross, anak tidak responsif](https://www.redcross.org.uk/first-aid/learn-first-aid-for-babies-and-children/unresponsive-not-breathing-child): bantuan perlu segera. Materi EDUYASA **tidak memakai ambang menunggu satu menit** yang muncul pada uraian buku. Siswa memanggil orang dewasa dan bantuan darurat setempat, tidak memberi minum/obat kepada orang tidak sadar, tidak memindahkan atau meluruskan cedera secara paksa, dan tidak melakukan penyelamatan air sendiri.

RICE dibahas sebagai konsep bantuan awal tertentu dengan pendampingan,
bukan petunjuk bagi anak untuk menangani semua cedera sendiri. Praktik
keterampilan, kebugaran, dan pertolongan memerlukan guru; nilai pilihan ganda
tidak membuktikan penguasaan praktik. Nomor darurat tidak diseragamkan ke
nomor luar negeri atau dianggap tersedia sama di semua wilayah.

## Status audit dan pengujian

Audit akademik menyeluruh ditunda atas permintaan pemilik. Pemeriksaan dasar
kesesuaian topik, keselamatan, JSON, ID, kunci, media, dan alur tetap dilakukan.
Audit manual berikutnya perlu meninjau cakupan kompetensi, tingkat tantangan,
variasi situasi/pengecoh, ketunggalan jawaban, bahasa, serta kebutuhan praktik.

Jalankan `node --test tests/quiz-bank.test.cjs`. Hasil sebelum penerbitan:
31 tes Node lulus termasuk regresi MTK/SBD dan 700 percobaan komposisi PJOK;
seluruh 2.430 soal repository memiliki ID unik, empat opsi unik dan kunci yang
cocok. Pengujian browser lokal memeriksa 10 modul, lima kuis penuh, gambar,
pembesaran, sesi campuran, kegagalan paket, dan coba lagi. Pengujian publik
GitHub Pages dilakukan terpisah setelah deployment. Android WebView belum diuji.
