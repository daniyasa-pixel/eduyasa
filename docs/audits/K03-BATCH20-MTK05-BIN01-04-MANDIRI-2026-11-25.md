# Audit Soal Mandiri EDUYASA Kelas 3 — Pengerjaan 20

Tanggal: 25 November 2026 (Asia/Jakarta). Branch: main.
Baseline: 41f585b621400871554bfd9a758773e95c41ac69 (laporan pengerjaan 19).

## Cakupan dan hasil

| Bab | Diaudit | ID direvisi | Tidak diubah |
|---|---:|---:|---:|
| MTK B05 — Penyajian Data dalam Tabel | 70 | 12 | 58 |
| BIN B01 — Kawan Seiring | 70 | 3 | 67 |
| BIN B02 — Yuk, Berpendapat! | 70 | 6 | 64 |
| BIN B03 — Pribadi yang Hebat | 70 | 1 | 69 |
| BIN B04 — Menyelamatkan Lingkungan | 70 | 11 | 59 |
| **Total** | **350** | **33** | **317** |

Audit awal menelaah isi pertanyaan, kemandirian konteks, opsi, kunci, pembahasan, dan stimulus. Semua 50 SVG yang dirujuk (10 per bab, dua soal per SVG) dibaca secara statis melalui label dan deskripsi. Bank MTK diperbandingkan dengan Markdown (tidak ada perbedaan awal). Bank BIN menggunakan JSON sebagai sumber operasional sesuai struktur paket; berkas soal Markdown dengan pola nama seperti MTK tidak tersedia. Tidak membuat sumber Markdown baru.

## Rincian revisi dan verifikasi

- MTK B05: Q001, 002, 007, 008, 011, 012, 017, 018, 041, 042, 047, 048. Memperjelas apakah angka menunjukkan pilihan siswa atau banyak buku yang dibaca, bukan banyak kegiatan olahraga atau jenis buah. Kunci dan perhitungan tetap. Markdown diselaraskan dan source_markdown.sha256 diperbarui; hash awal dan akhir cocok.
- BIN B01: Q020 urutan sesuai peristiwa eksplisit dalam teks; Q049 dan Q068 menghapus koma sebelum konjungsi sehingga.
- BIN B02: Q041 menghapus asumsi taman dekat rumah; Q044 membedakan alasan dan pertimbangan keselamatan; Q059–060 memperbaiki frasa menjadi membaca buku bergambar, sinkron dengan SVG bin-b02-gambar-05.svg; Q066 dan Q068 menghapus rujukan posisi opsi yang salah dari pembahasan.
- BIN B03: Q064 pembahasan menyebut judul yang benar, bukan posisi pilihan.
- BIN B04: Q020, Q045, Q070 pembahasan menyebut isi jawaban, bukan posisi opsi; Q031–035 memperjelas dahan pohon buah; Q050 memperjelas bahwa pembuka merupakan contoh, bukan fakta cerita yang belum diberikan; Q062 menyebut tindakan Belalang secara eksplisit agar tidak bergantung pada soal sebelumnya; Q063 memperjelas perubahan sikap terkait kepemilikan buah.
- 33 ID diubah; masing-masing kandidat memiliki empat opsi berbeda dan kunci terdapat dalam opsi. File yang direvisi dibaca kembali dari GitHub; setiap objek soal hasil revisi cocok dengan kandidat yang ditelaah. Satu SVG yang direvisi juga dibaca kembali dan cocok. Tidak ada ID atau jumlah soal yang diubah, tidak mengaudit ulang isi 317 soal yang lulus audit awal.

## Batas verifikasi
Pemeriksaan dilakukan pada data dan sumber SVG di GitHub, bukan uji rendering langsung di Android/WebView, pembaca layar, atau uji empiris dengan siswa. Tidak menguji ulang pengacakan jawaban aplikasi maupun kesesuaian independen dengan edisi buku terbaru. Tidak mengubah materi/modul.

## Progres
Pengerjaan 19: 45/54 bab, 2.225/2.855 soal.
Pengerjaan 20: 50/54 bab, 2.575/2.855 soal. Tersisa 4 bab, 280 soal.
Pengerjaan 21: PKN B01–04, tahap terakhir Kelas 3.
