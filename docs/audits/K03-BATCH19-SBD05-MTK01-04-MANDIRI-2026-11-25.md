# Audit Soal Mandiri EDUYASA Kelas 3 — Pengerjaan 19

Tanggal: 25 November 2026 (Asia/Jakarta). Branch main. Baseline: a37bebf7c7cd3e9bb2e5d50513edd2bc72434fa1 (laporan batch 18).

## Cakupan dan hasil

| Bab | Soal audit awal | Revisi teks/data JSON | ID terdampak media saja | Total ID revisi/terdampak | Tidak diubah |
|---|---:|---:|---:|---:|---:|
| SBD B05 | 45 | 45 | 0 | 45 | 0 |
| MTK B01 | 70 | 5 | 14 | 19 | 51 |
| MTK B02 | 70 | 0 | 0 | 0 | 70 |
| MTK B03 | 70 | 3 | 0 | 3 | 67 |
| MTK B04 | 70 | 2 | 13 | 15 | 55 |
| **Total** | **325** | **55** | **27** | **82** | **243** |

Audit awal membaca bank JSON dan Markdown sumber, menelaah isi, kunci, konteks dan kecocokan gambar. 50 SVG diperiksa statis melalui judul, deskripsi, label dan (untuk relevansi) geometri/bentuk. ID dan kuantitas dipertahankan. Revisi hanya ID yang bermasalah; 243 ID tidak diubah atau diaudit ulang. Verifikasi setelah penyimpanan dilakukan pada perubahan, bukan seluruh bank yang telah lulus.

## Perbaikan
- SBD B05 (Eksplorasi Membuat Bentuk 3 Dimensi: Hewan di Sekitarku): 45 ID (001–045) JSON berbeda dari Markdown sumber yang lebih sesuai topik dan SVG. Diselaraskan soal, empat opsi, kunci, pembahasan; 10 alt dicocokkan dengan SVG. Q019 memperjelas empat sayap capung; Q040 memperjelas kondisi perbandingan stabilitas dua model. Seluruh file kandidat divalidasi dan dibaca kembali dari GitHub.
- MTK B01 (Bilangan Cacah sampai 1.000): Q011–015 istilah nilai tempat diperjelas menjadi nilai angka pada tempat tertentu. Judul pada SVG 01–04 tidak lagi mencantumkan bilangan hasil, SVG 08–10 tidak lagi mengungkap besar langkah garis bilangan; ID terkait Q051–058, Q065–070. Semua angka stimulus dan geometri dipertahankan.
- MTK B02 (Kalimat Matematika): 70 butir cocok dengan sumber, jawaban dan data diagram pita; tidak direvisi.
- MTK B03 (Pengukuran Panjang dan Berat): Q010 memperjelas arah pandangan tegak lurus terhadap skala, Q045 menyebut sambungan kayu tanpa tumpang tindih, Q049 menyatakan letak dua ujung benda pada penggaris.
- MTK B04 (Unsur-Unsur Bangun Datar): Q044 memperjelas batas sudut lancip (>0 dan <90 derajat), Q054 meminta nama bangun paling khusus (persegi juga merupakan persegi panjang khusus). Judul SVG 01–05 dan 09–10 tidak lagi mengungkap nama bangun atau hubungan garis; Q051–060 dan Q067–070 terdampak, termasuk Q054 yang sudah dihitung sebagai revisi teks. Label derajat pada SVG sudut dipertahankan karena soal meminta membaca ukuran.
- Semua perubahan Markdown MTK disertai pembaruan source_markdown.sha256 dalam JSON. Hash sumber sebelum dan sesudah diperiksa. Opsi dan ID selain revisi khusus dipertahankan; berkas SVG tetap pada URL semula.

## Verifikasi setelah perubahan
55 ID data direvisi dan diverifikasi dengan kandidat hasil telaah, kunci terdapat pada empat opsi yang berbeda (SBD divalidasi per butir), serta sumber/hash pada MTK. 14 berkas SVG dengan judul yang diperbaiki dibaca kembali dan cocok dengan versi kandidat; mencakup 28 ID terkait, satu juga direvisi pada data (MTK B04 Q054), sehingga total ID unik revisi/terdampak = 82. Tidak ditemukan ketidaksesuaian penyimpanan. Tidak mengulang audit substansi nomor yang lulus awal.

Batas: pengecekan statis GitHub, bukan rendering HP/WebView, pengujian pembaca layar, pengacakan opsi dalam aplikasi, atau penilaian akademik independen dengan buku terbaru. Deskripsi aksesibilitas dan angka yang diperlukan untuk membaca stimulus tetap dipertahankan.

## Progres
Batch 18: 40/54 bab, 1.900/2.855 soal.
Batch 19: 45/54 bab, 2.225/2.855 soal (9 bab, 630 soal tersisa).
Batch 20: Matematika B05 + Bahasa Indonesia B01–04.
Batch 21: PKN B01–04.
