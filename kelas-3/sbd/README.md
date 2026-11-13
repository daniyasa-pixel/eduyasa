# Seni Budaya — Seni Rupa Kelas 3 (EDUYASA)

Lima bab internal EDUYASA (judul internal, bukan klaim nama bab resmi):
1. Menemukan Keindahan di Sekitar Kita
2. Belajar dari Karya Orang Lain
3. Ideku, Gagasanku
4. Karya yang Mewakili Diriku
5. Aku Seniman Cilik

Bank sumber berjumlah **225 soal**: lima bab × 45 soal (35 teks dan 10 bergambar). ID soal memakai `K03-SBD-B##-M01-001` s.d. `045`; modul memakai `K03-SBD-B##-MOD-01/02`.

## Status audit isi 13 November 2026

- **Bab 1 selesai:** diselaraskan dengan Bab I *Seni Rupa di Sekitarku*.
- **Bab 2 selesai:** diselaraskan dengan Bab II *Bicara Tentang Karya*.
- **Bab 3 selesai:** diselaraskan dengan Bab III *Berkreasi dengan Warna*.
- Setiap bab selesai memuat 45 soal, 10 SVG, dan dua modul yang ditulis ulang.
- **Bab 4–5 belum diperiksa ulang** dalam putaran audit isi ini.
- Kumulatif Seni Rupa: **135/225**; sisa **90 soal**.
- Konversi JSON, aktivasi aplikasi, uji siswa, dan uji HP/WebView belum termasuk hasil ini.

Bab 3 kini mencakup gambar cerita, penafsiran visual, pengamatan lingkungan, uji alat/bahan, cetak stensil, eksplorasi warna, bingkai, keselamatan gunting/krayon, privasi foto keluarga, presentasi proses, dan apresiasi. Tiga puluh ilustrasi Bab 1–3 menjadi stimulus fungsional; enam modul memiliki lingkup setara dalam dua pendekatan per bab.

## Aturan tahap JSON

Sumber Bab 1–3 berubah dan harus dikonversi ulang. Regenerasi paket soal teks, soal bergambar, dan materi untuk setiap bab: `sbd-b01*.json`, `sbd-b02*.json`, dan `sbd-b03*.json` sesuai nama file yang dirinci dalam `../HANDOVER-KONVERSI-JSON.md`. Bandingkan ID, urutan opsi, kunci, pembahasan, media, dan isi modul 1:1. Jika Markdown berubah lagi, audit perubahan satu kali; bila perlu, revisi sumber dahulu lalu konversi ulang.
