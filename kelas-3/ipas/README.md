# IPAS Kelas 3 — EDUYASA

Acuan: Buku Siswa IPAS SD/MI Kelas III **Edisi Revisi 2025**, Kemendikdasmen (Aldilla Kusumawardhani dkk.). Jangan campur dengan urutan bab edisi 2022.

1. Keajaiban Tubuhku
2. Dahulu, Kini, dan Nanti
3. Peduli dan Berbagi
4. Siklus Hidup yang Menakjubkan
5. Bijak Berbelanja Kebutuhan
6. Energi, sang Pemberi Kekuatan
7. Jejak Penjelajah
8. Rahasia Tiga Wujud Zat

## Aturan produksi
- Per bab: dua modul setara dan berbeda pendekatan, **masing-masing bergambar**; 50 soal tanpa gambar + 20 soal bergambar, masing-masing 4 opsi, kunci dan pembahasan.
- Gunakan PNG terkompresi untuk visual rinci dan SVG untuk diagram sederhana; seluruh gambar harus punya teks alternatif dan tidak boleh mengungkap jawaban secara keliru.
- Format kerja Markdown; JSON dikerjakan Codex setelah audit.
- ID: `K03-IPAS-B01-MOD-01`, `K03-IPAS-B01-M01-001` dan seterusnya; ID unik lintas bab.
- Audit kebenaran akademik, ketunggalan jawaban, kecocokan gambar dan penjelasan sebelum status selesai.

**Status putaran revisi isi dan media:** Delapan bab/560 soal diperiksa ulang;
546 butir diubah dan 14 dipertahankan sesudah diperiksa. Seluruh 16 modul
memakai ID unik, semua rujukan ilustrasi tersedia, 96 SVG valid XML.
Setiap bab diuji render lebar 390 piksel, batas teks, dan ditinjau visual;
notasi matematika baru Bab 5, 6, 8 juga diuji MathJax.

Putaran IPAS selesai; keseluruhan lima mapel **BELUM SELESAI**. Belum ada uji
kesulitan empiris siswa, konversi JSON, atau aktivasi paket aplikasi. Lanjut PAI
sesuai [laporan progres](../PROGRES-REVISI-1945.md).

## Checkpoint JSON — 12 November 2026

**Konversi seluruh Bab 1–8 selesai:** 8 bank soal teks `ipas-bNN.json`, 8 bank bergambar `ipas-bNNa.json`, dan 8 file materi `ipas-bNN-materi.json` (dua modul per bab). Total **560 soal, 16 modul, 24 JSON**. Manifest `question-packs.json` tersedia untuk semua delapan bab. Audit integritas hasil JSON vs Markdown per posisi/ID/opsi/kunci/pembahasan/gambar/teks modul: **0 mismatch**; bukan audit ulang isi akademik. Aturan pra-konversi yang tertulis pada bagian produksi di atas adalah catatan historis, digantikan oleh `../HANDOVER-KONVERSI-JSON.md`. **Belum diaktifkan pada menu Kelas 3** dan belum diuji pada HP/WebView.
