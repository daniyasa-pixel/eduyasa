# Audit Kemandirian Soal EDUYASA Kelas 3 — Pengerjaan 11

Tanggal 23 November 2026 (Asia/Jakarta). Repo: `daniyasa-pixel/eduyasa`, branch `main`.
Baseline GitHub sebelum audit: `e716baa896a4855100c08d7204735ca70b3780cd`.

## Cakupan dan hasil

| Bab IPAS Kelas 3 | Soal teks | Soal gambar | Total dicek | Butir direvisi |
|---|---:|---:|---:|---:|
| B01 — Keajaiban Tubuhku | 50 | 20 | 70 | 3 |
| B02 — Dahulu, Kini, dan Nanti | 50 | 20 | 70 | 5 |
| B03 — Peduli dan Berbagi | 50 | 20 | 70 | 4 |
| B04 — Siklus Hidup yang Menakjubkan | 50 | 20 | 70 | 4 |
| B05 — Bijak Berbelanja Kebutuhan | 50 | 20 | 70 | 4 |
| **Total** | **250** | **100** | **350** | **20** |

Audit ditujukan pada **setiap pertanyaan yang bisa tampil sendirian saat bank diacak**, bukan sekadar kesetiaan hasil konversi dari sumber Markdown. Konten yang tidak perlu diubah dipertahankan.

## Perbaikan per bab

- **B01, 3 butir (`051–070`):** Q061–Q062 menjelaskan perbandingan pencahayaan tempat membaca A dan B secara lengkap, menghilangkan ketergantungan Q062 pada frasa *“pilihan itu”* dari Q061. Q068 menyebut urutan membersihkan tangan dari tanah sampai siap memegang makanan.
- **B02, 5 butir:** Q004 memperjelas dua perjalanan Paman yang memakai moda berbeda tanpa menyamaratakan perubahan cara bepergian. Q054 menjelaskan perbedaan isi dua catatan taman; Q057–Q058 memasukkan pertentangan tahun pembukaan jembatan dari sumber A dan B; Q062 memuat riwayat moda perjalanan Bibi secara langsung.
- **B03, 4 butir (`060, 067, 068, 070`):** memperjelas data keterampilan mengikat tali sepatu, perubahan pembagian tugas karena anggota keluarga sakit, dan hubungan kerja sama dengan jalur ruangan yang kembali aman.
- **B04, 4 butir (`059–062`):** memperjelas informasi percobaan empat biji kacang hijau di tiga wadah dan perbandingan tanaman di tempat terang/gelap. Soal tidak membutuhkan soal pasangan sebelum/sesudahnya untuk mengetahui percobaan yang sedang dibahas.
- **B05, 4 butir (`059–062`):** pertanyaan mencantumkan harga buku pada dua toko dan data kesalahan total belanja pada struk. Penentuan toko termurah, selisih harga, koreksi penjumlahan, dan sikap kepada penjual dapat dipahami langsung.

## Perubahan file

Untuk setiap perubahan, **bank JSON operasional dan Markdown sumber yang sesuai diperbarui di GitHub**. Bank awal Kelas 3 menggunakan identitas `K03-IPAS-BNN-M01-NNN`; tidak ada ID baru, perubahan kuantitas, atau pengacakan opsi di dalam JSON. Seluruh revisi batch ini hanya menyentuh `text` dan sebagian `discussion`, **bukan** opsi dan kunci.

Berkas JSON yang mengalami perubahan:
- `kelas-3/ipas/ipas-b01a.json` (3 butir);
- `kelas-3/ipas/ipas-b02.json` (1) dan `ipas-b02a.json` (4);
- `kelas-3/ipas/ipas-b03a.json` (4);
- `kelas-3/ipas/ipas-b04a.json` (4);
- `kelas-3/ipas/ipas-b05a.json` (4).

Sumber diselaraskan langsung pada `ipas-b01-soal-bergambar.md`, `ipas-b02-soal-biasa.md`, `ipas-b02-soal-bergambar.md`, `ipas-b03-soal-bergambar.md`, `ipas-b04-soal-bergambar.md`, dan `ipas-b05-soal-bergambar.md`.

## Audit ulang setelah commit

- Semua **350 soal** dibaca kembali dari cabang `main`; 350 ID tetap unik, setiap bab tepat 50 soal teks dan 20 soal gambar.
- Semua soal bertipe PG, berisi empat opsi berbeda, dan `answer` cocok persis dengan salah satu opsi.
- Dibandingkan per ID dengan Markdown terbaru: setiap `text`, urutan keempat opsi, jawaban, dan `discussion` cocok; 100 referensi gambar pada lima bab cocok dengan gambar/alt sumber Markdown, **0 mismatch**.
- Lima pasang MOD-01/MOD-02 tetap tersedia dan berisi materi; tidak perlu ditulis ulang pada audit soal mandiri ini.
- Pengecekan aset media diperluas untuk seluruh IPAS B01–B08: **160/160 soal bergambar** menunjuk nama SVG yang tersedia; delapan contoh SVG diperiksa teks di dalamnya dan dicocokkan dengan stimulus.
- Simulasi gabungan Pengerjaan 11 dan 12: **2.000 sesi** (200 per bab, 20 soal per sesi), 620 ID seluruhnya pernah muncul, tanpa ID ganda dalam sesi dan tanpa kunci hilang saat opsi diacak.

**Batas verifikasi:** analisis struktur, konteks, kesetiaan sumber, serta pemeriksaan sampel SVG bukan pengujian langsung semua gambar yang dirender pada HP; belum menjalankan loader produksi atau mengetes audio/akses jaringan di WebView. Koreksi tidak berarti seluruh soal harus diubah. Audit mutu/kesulitan secara empiris terhadap siswa belum dilakukan.

## Kelanjutan

**Pengerjaan 11 selesai.** Pengerjaan 12 mengaudit IPAS B06–B08 dan PAI B01–B02 secara langsung. Daftar pekerjaan berikutnya menurut rencana adalah Pengerjaan 13: **PAI Kelas 3 Bab 3–7**.
