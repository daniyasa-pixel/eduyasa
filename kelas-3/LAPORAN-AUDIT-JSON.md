# Audit integritas JSON EDUYASA Kelas 3

Tanggal checkpoint: 12 November 2026. Tujuan audit: **membandingkan hasil konversi JSON dengan sumber Markdown**, bukan mengaudit kebenaran atau mutu akademik isi.

## Konversi batch 1: Seni Rupa (SBD) Bab 1–3

| Bab | Teks | Gambar | Modul | Status kesetiaan JSON | Output |
|---|---:|---:|---:|---|---|
| SBD Bab 1 | 35 | 10 | 2 | **PASS** | `sbd/sbd-b01.json`, `sbd/sbd-b01a.json`, `sbd/sbd-b01-materi.json` |
| SBD Bab 2 | 35 | 10 | 2 | **PASS** | `sbd/sbd-b02.json`, `sbd/sbd-b02a.json`, `sbd/sbd-b02-materi.json` |
| SBD Bab 3 | 35 | 10 | 2 | **PASS** | `sbd/sbd-b03.json`, `sbd/sbd-b03a.json`, `sbd/sbd-b03-materi.json` |
| **Jumlah** | **105** | **30** | **6** | **135 soal PASS** | **9 JSON** |

Hasil pembacaan ulang melalui GitHub: semua 9 file JSON berhasil di-parse. Audit pasangan Markdown–JSON dilakukan untuk **135 ID soal** dan **6 modul**; dibandingkan ID **beserta posisi urutannya**, teks pertanyaan, empat pilihan **dalam urutan aslinya**, kunci jawaban teks penuh, pembahasan, metadata gambar (`src` dan `alt`), judul dan konten lengkap dua modul. 135 ID unik, 0 mismatch. Semua **30 aset SVG yang disebut oleh 30 soal bergambar** diverifikasi ada melalui GitHub. Manifest parsial `sbd/question-packs.json` berisi Bab 1–3 dibuat dan berhasil dibaca sebagai JSON. Tidak ada pengacakan opsi atau ID di file data.

### SHA sumber yang digunakan

| Bab | Soal teks SHA | Soal gambar SHA | Materi SHA |
|---|---|---|---|
| 01 | `665089b8daff013bea9d3649c5023f72afbbb396` | `89ac4dd3d3850290b5dff920f8a4f38477702fc3` | `f0cc943f37f657d1bba4fd2aa88a53e5e44dce09` |
| 02 | `b66fa31006834bb0c844ab246b1b78caa8af1a9b` | `ff9128f6cc9aea660feb6f2faf56f8a6351beeb8` | `b1e7bda50bf8453556459e9ba24bfee63d09a6a9` |
| 03 | `161458c4b78994d217cf67439e2baa797a011114` | `329bf861283a05dc9cbeff41a0d9f3f86459c8129` | `ceefa43a1ecedc0a712d195651c71191946c4e4f` |

Catatan: SHA di atas adalah SHA blob isi file GitHub yang dikonversi. Jika Markdown berubah, JSON bab tersebut harus diregenerasi, lalu dibandingkan ulang.

### Sisa pekerjaan dan batasan

- **Belum dikonversi dalam batch ini:** SBD Bab 4 dan 5 (90 soal), dan paket lain yang belum berformat JSON.
- **Integrasi frontend belum dilakukan:** `HTML/grade-config.js` masih memiliki daftar Kelas 3 yang hanya mengaktifkan BIN, PKN, MTK. Jangan menganggap SBD sudah muncul atau bisa dimainkan sampai konfigurasi ditambahkan, paket lengkap siap, dan diuji di aplikasi.
- Pengujian render gambar di HP/WebView, urutan soal ketika runtime berjalan, dan akses URL GitHub raw dalam WebView **belum dilakukan**. Verifikasi 30 aset berupa keberadaan file pada repo; bukan pengujian browser.
- Audit JSON ini tidak mencakup kualitas akademik, cocoknya ilustrasi dengan jawaban secara pedagogis, ataupun kecocokan bab buku pemerintah, sesuai arahan pengguna.
- Standar umum dan pekerjaan berikutnya lihat `HANDOVER-KONVERSI-JSON.md`.

## Konversi batch 2 — SBD Bab 4–5 (12 November 2026)

Konversi langsung dari Markdown sumber pada branch `main`, tanpa audit ulang isi soal. Telah dibuat **6 file JSON** baru:

| Bab | Bank teks | Bank gambar | Modul | Soal | Hasil audit konversi |
|---|---|---|---|---:|---|
| Bab 4 | `sbd/sbd-b04.json` (35) | `sbd/sbd-b04a.json` (10) | `sbd/sbd-b04-materi.json` (MOD-01, MOD-02) | 45 | PASS |
| Bab 5 | `sbd/sbd-b05.json` (35) | `sbd/sbd-b05a.json` (10) | `sbd/sbd-b05-materi.json` (MOD-01, MOD-02) | 45 | PASS |

**Audit hasil JSON terhadap Markdown:** 90 soal terkonversi, 90 ID unik, semua nomor/urutan identik dengan sumber, pilihan jawaban (termasuk urutan), kunci teks, pembahasan, `image.src`/`image.alt`, dan isi lengkap 4 modul cocok **tanpa mismatch (0)**. Semua 20 referensi SVG tersedia di GitHub. Enam file JSON berhasil di-parse dan diverifikasi ulang setelah ditulis. Ini audit kesetiaan konversi, **bukan audit isi akademik**.

**SHA Markdown acuan:**

| Bab | Teks | Bergambar | Materi |
|---|---|---|---|
| B04 | `808324bdeccbbb5c905dc5ae6e453418d89664e7` | `b0d606cb9b48a312b8a96df54845efab21ba14e9` | `88c65393f0d0367cc89b110aa4f677c1ccb5b8a7` |
| B05 | `cb0c3ba843049650a59806b8ccae7073e5612fd3` | `35a203c4e1d2e4b56a99d0b7361f03e93e86b818` | `7aae37f249eeec5764d67d07002a2b91393eb12d` |

Manifest `sbd/question-packs.json` telah diperluas hingga **B01–B05** dalam commit `6b63761833a2420e251d6863a2a5e57070ae2558`.

**Status kumulatif Seni Rupa:** Bab 1–5 telah dikonversi: **225 soal** (175 teks + 50 bergambar), **10 modul**, **15 file JSON** plus satu manifest. Belum berarti aktif di menu aplikasi: `HTML/grade-config.js` belum diubah dan pemuatan dalam browser/WebView belum diuji. Integrasi frontend dapat dilakukan setelah uji kompatibilitas data dan manifest. **PJOK, IPAS, PAI, ENG** tidak dihitung selesai konversi hanya karena sumber Markdown-nya tersedia.

## Konversi batch 3 — PAI Bab 1–10 (12 November 2026)

Sebanyak **10 bab PAI × 30 soal = 300 soal** dan **10 bab × 2 modul = 20 modul** telah dikonversi dari Markdown ke **20 file JSON** (10 bank soal dan 10 file modul) pada `kelas-3/pai/`. Manifest `kelas-3/pai/question-packs.json` dibuat, memuat pemetaan `K03-PAI-B01` hingga `K03-PAI-B10` ke bank JSON masing-masing. **Tidak ada perubahan isi akademik atau perbaikan sumber Markdown dalam batch ini.**

Hasil audit kesetiaan hasil konversi dibandingkan langsung dengan Markdown sumber terbaru (dibaca ulang dari GitHub setelah commit): seluruh **300 ID** berurutan 001–030 per bab, 4 opsi per soal dengan urutan asli, teks kunci lengkap dan pembahasan, judul bab, dan seluruh **20 konten modul** cocok; **0 mismatch** pada pemeriksaan JS per-ID/per-modul. JSON dapat di-parse; setiap bab 30 soal dan 2 modul. Pertahankan teks Arab/harakat dan konten HTML bahasa Arab persis seperti sumber, termasuk dalam pengujian rendering berikutnya.

| Bab | Soal | Modul | Status | SHA Markdown soal | SHA Markdown materi |
|---|---:|---:|---|---|---|
| B01 | 30 | 2 | PASS | `ad20b0b02dd0f619d8e636e7c3d3e877f2bc4b5f` | `cdede0519cea96814a2e2ae9829b31367e71537d` |
| B02 | 30 | 2 | PASS | `74337cc03d35f9bb9915fafe3a7a147a5c4e5c8f` | `4be1d30fd474b26dace4b5d0436bcd50d568ded8` |
| B03 | 30 | 2 | PASS | `88c1fec96c37e10c7f7ef44a4ec1e99241ee6d2c` | `467112f32d02125beaffd20333ab7155432541d6` |
| B04 | 30 | 2 | PASS | `72402231702bf8b10fa026bdd088bb009d630ba2` | `f79dc8952f526f0cea0fa7a49568ef9a83898d1a` |
| B05 | 30 | 2 | PASS | `00635f32086e74671d9efc8c5d15aeaf12821806` | `454ce72396659623996aee7226c44f557fa29674` |
| B06 | 30 | 2 | PASS | `fe8e665af5bc4f80e9728f0bbbd591cf7f139ff7` | `f4b3f423e1f65b225d4a5b88554273d39e39870d` |
| B07 | 30 | 2 | PASS | `2cfd54b33dfdf93ed4fd38bad07a1812354b7937` | `38065ada77b6c1281ea7dc34547e29de1b052e1c` |
| B08 | 30 | 2 | PASS | `50c04da2067a3d42593a3f4630956945f9564311` | `811c5d5dff1a59e7a08dbe641e62cb0b5c05e8f6` |
| B09 | 30 | 2 | PASS | `c617c023371e2d4fd796a91a7c6b52e7785e5d1b` | `a6952cc72297320586e7c6eb9bb83c3fb0b0dfd0` |
| B10 | 30 | 2 | PASS | `2595d6296a08bbb7b7ab876b77c4db09c64832a9` | `0eceaabff1e5f1abb16691e3eeb5a48f9162ecae` |

**Total akumulatif hasil konversi dalam laporan ini:** SBD 225 soal/10 modul + PAI 300 soal/20 modul = **525 soal, 30 modul, 35 file JSON bank dan materi** (SBD 15 + PAI 20) dan dua manifest. Angka ini tidak termasuk BIN, PKN, MTK yang sudah lama berada dalam aplikasi.

**Batas status:** PAI belum diaktifkan di `HTML/grade-config.js` atau menu Kelas 3; uji di HP/WebView, rendering teks Arab/RTL, dan uji praktik modul belum dilakukan. Jangan menganggap konversi JSON sama dengan integrasi atau rilis; tetap tidak perlu audit ulang isi akademik pada tahap teknis ini. Jika sumber Markdown berganti SHA, konversi ulang dan cocokkan seluruh JSON terkait.
