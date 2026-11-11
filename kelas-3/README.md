# EDUYASA — Kelas 3

Data Kelas 3 berada di folder ini. Data Kelas 6 tetap memakai folder mapel di akar
repository. Keduanya berada pada branch `main`; branch berbeda dengan folder.

Mata pelajaran yang sudah dibuat pada tahap uji coba ini: **Bahasa Indonesia**,
empat bab berdasarkan buku edisi revisi 2025. Daftar kerja delapan mapel/50 bab
dari riset awal belum berarti seluruh kontennya sudah tersedia atau diaudit.

- [Paket Bahasa Indonesia dan laporan pemeriksaan](bin/README.md)
- [Konfigurasi kelas](../HTML/grade-config.js)
- [Ketentuan konten yang berlaku](../docs/CONTENT_GUIDE.md)

Buka aplikasi dengan `?kelas=3`, atau pilih Kelas 3 di halaman awal. URL aplikasi
lama tanpa parameter kelas tetap membaca Kelas 6. Kelas 1, 2, 4, dan 5 masih
ditandai belum tersedia; penambahan mapel berikutnya dilakukan setelah data
tersedia dan konfigurasi kelas diperbarui.

## Penamaan

Contoh bank dasar: `kelas-3/bin/bin-b01.json`.
Paket tambahan bergambar: `kelas-3/bin/bin-b01a.json`.
Dua versi materi: `kelas-3/bin/bin-b01-materi.json`.
ID bab: `K03-BIN-B01`; ID soal: `K03-BIN-B01-M01-001` hingga `070`.
ID modul: `K03-BIN-B01-MOD-01` dan `MOD-02`.

`subject_id` tetap `BIN`; lokasi data dan awalan ID membedakan kelas. Manifest
`bin/question-packs.json` menggunakan jalur relatif terhadap `kelas-3/`.
Jangan menyalin bank Kelas 6 ke sini atau menambahkan nama kelas pada ID lama.
