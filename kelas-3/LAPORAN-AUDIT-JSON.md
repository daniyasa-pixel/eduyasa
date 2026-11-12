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

## Konversi batch 4 — Bahasa Inggris Unit 1–10 (12 November 2026)

Sepuluh unit Bahasa Inggris telah dikonversi langsung dari sumber Markdown, **500 soal** dengan komposisi per unit **35 teks + 10 bergambar + 5 listening**, serta **20 modul** (MOD-01 dan MOD-02 per unit). Dibuat **40 JSON**: `eng-bNN.json` untuk teks, `eng-bNNa.json` untuk bergambar, `eng-bNNl.json` untuk listening, `eng-bNN-materi.json` untuk materi; seluruhnya berada di `kelas-3/eng/`. Manifest `kelas-3/eng/question-packs.json` memuat 10 entri bab dan masing-masing tiga bank soal, commit `21e477ed96da574ea7e6aa71415ae616bda3ecf9`.

**Audit JSON setelah konversi:** seluruh 10 unit dibaca kembali dari GitHub bersama keempat file sumber Markdown masing-masing; 50 nomor 001–050 per unit tepat urut, 4 opsi tiap soal sama persis dan dalam urutan yang sama, jawaban teks penuh dan pembahasan cocok, metadata gambar `src/alt` cocok dengan pemetaan path sumber, metadata `audio` identik dengan TTS sumber (`type`, `lang`, `text`, `rate`). Transkrip produksi **tidak dimasukkan** sebagai teks pertanyaan maupun properti produksi pada JSON; `audio.text` tetap disimpan untuk pembaca TTS. Konten MOD-01 dan MOD-02 cocok setelah hanya memetakan URL gambar Markdown relatif ke GitHub raw absolut. **500 soal / 20 modul, 0 mismatch** pada pemeriksaan per-butir/per-modul, **tanpa audit ulang akademik**.

| Unit | Bank soal | Jumlah | Modul | Audit |
|---|---|---:|---:|---|
| 01 | `eng-b01.json`, `eng-b01a.json`, `eng-b01l.json` | 50 | `eng-b01-materi.json` (2) | PASS |
| 02 | `eng-b02.json`, `eng-b02a.json`, `eng-b02l.json` | 50 | `eng-b02-materi.json` (2) | PASS |
| 03 | `eng-b03.json`, `eng-b03a.json`, `eng-b03l.json` | 50 | `eng-b03-materi.json` (2) | PASS |
| 04 | `eng-b04.json`, `eng-b04a.json`, `eng-b04l.json` | 50 | `eng-b04-materi.json` (2) | PASS |
| 05 | `eng-b05.json`, `eng-b05a.json`, `eng-b05l.json` | 50 | `eng-b05-materi.json` (2) | PASS |
| 06 | `eng-b06.json`, `eng-b06a.json`, `eng-b06l.json` | 50 | `eng-b06-materi.json` (2) | PASS |
| 07 | `eng-b07.json`, `eng-b07a.json`, `eng-b07l.json` | 50 | `eng-b07-materi.json` (2) | PASS |
| 08 | `eng-b08.json`, `eng-b08a.json`, `eng-b08l.json` | 50 | `eng-b08-materi.json` (2) | PASS |
| 09 | `eng-b09.json`, `eng-b09a.json`, `eng-b09l.json` | 50 | `eng-b09-materi.json` (2) | PASS |
| 10 | `eng-b10.json`, `eng-b10a.json`, `eng-b10l.json` | 50 | `eng-b10-materi.json` (2) | PASS |

**SHA file Markdown acuan sebelum konversi** (urut: teks, gambar, listening, materi):
- B01: `96053270acaac362cad6b40ba06bf946225bd39a` · `13024e7e8fcd044bdd3779fbf8b32b1d57c1950d` · `339fefff8b77a839e5cd8a8fae9b29f5d362ead0` · `9c8d93a7f62576e637fc490540c995d089222e68`
- B02: `069e07e15bd6f31d87a7d2fc713a11cf8a181f05` · `91ac0102dbb2f22bb8b7c1b3b463935665991d3e` · `0e8cd6e727795bc780557ade71e47be9eb7b6e9e` · `8cb0cd20a2c9f7aed726dd0bea55be819476b5a8`
- B03: `d44251cf3528cce8ac4db2aa23cacdb54f566394` · `841e731d3886a0f55f4f62e1c314faf627fef91b` · `8a84d4edc16dbd59fafb3927c90a0bbb8f5d1383` · `442d0bf1688ab3ec136b1443364876c7d7290646`
- B04: `ad137287778aee715e46885c754759d1d6132c29` · `5b7acf79258aac1e828374e0a7b9dc1e0c60db64` · `29e672e0adca6560dea73e677a3bfab7c7031858` · `392062286e38b4d345807f2efd28265a6e635991`
- B05: `335add63e4749807c14dc5031c439b86112867c8` · `59f9129a40523b7bb17bddb41f02d3fce5a8326b` · `be654e871822514915b13746788d1f1fa3b84961` · `ec06227857e668dcaafa9ead661a0d129efe5470`
- B06: `58c5e2d2795d7177f08f81fa9e9a541dee314f65` · `5b7ab7a8f023065b20896a355f70f8f563049108` · `af5b7c9c14432830133ca012efa3ded5a561635c` · `85b271ba720341e7e3630a27258431b91e8c1f1a`
- B07: `b8472a23bed71e8f72aae0a24d905ef9947e18c0` · `53f15f9f7ea04d10136ffd70f3814260b9cb4ffb` · `e7676a59f6f9221cfb61e401c81e57695304e45f` · `3b5f62be1ffb82c030e3b9694875f8b7a18352c8`
- B08: `3d40b88bc962ee42144d2c876f957d0b725b64e9` · `951b635687b475399f0bc2ce016f2d8fc9752414` · `11ece08a240c2ceeb38f56884545625a73f22ad6` · `7ecb43dbd664f044e1e5336b0db80892685802a6`
- B09: `536bc5bf8a894c6397509974eebb7854a4eb6419` · `240bc615299b9435be75062165ee23a1d550518a` · `309740997c38f7c76f8debd29587ebc56ee44a8e` · `82e56ad9400b9c09da14b46d79dc7ad24575c321`
- B10: `2cc0270dcaf1ba1cc060baefa284406abbe947b3` · `c962285e0e5b2f60488a3d5f9b88d8585f1e30b4` · `cfba22ea44594f780b5fbe1bc4eb0d4cc4832a9e` · `c016f495cfc84f92966509c3758cb01b3b599a73`

**Batas pengujian:** Keberadaan/akses semua SVG melalui browser dan kualitas pelafalan TTS belum diuji. Nama bank listening `eng-bNNl.json` perlu disinkronkan dengan loader frontend saat aktivasi karena ini nama keluaran tambahan. `HTML/grade-config.js` belum mengaktifkan Inggris di menu Kelas 3. Modul memiliki URL gambar absolut dari sumber relatif; tidak ada perubahan narasi. Jangan menganggap paket siap rilis HP sebelum uji WebView dan manifest.

**Akumulasi batch baru (SBD + PAI + ENG):** **1.025 soal** dan **50 modul** dikonversi, dengan **75 JSON bank/materi** (15 + 20 + 40) dan tiga manifest. Sisa dari target 1.945 pada lima mapel: **920 soal**, yaitu IPAS 560 dan PJOK 360. Angka tidak menghitung mata pelajaran Kelas 3 (BIN, PKN, MTK) yang sudah punya JSON lebih dahulu.

## Konversi batch 5 — IPAS Bab 1–8 (12 November 2026)

**Selesai:** delapan bab IPAS dengan **560 soal (400 teks + 160 bergambar)**, **16 modul dua versi** dikonversi menjadi **24 file JSON** (8 bank teks `ipas-bNN.json`, 8 bank gambar `ipas-bNNa.json`, 8 modul `ipas-bNN-materi.json`). Manifest baru `kelas-3/ipas/question-packs.json` memetakan `K03-IPAS-B01`–`K03-IPAS-B08` ke kedua bank masing-masing (commit `e4691c716f47649820e8c510069bb4d67f67cfb9`).

**Audit setelah konversi:** delapan bab dibaca ulang dari GitHub dengan enam file tiap bab (3 Markdown + 3 JSON). Audit JS per-posisi soal dan modul membandingkan ID dan urutan sumber (001–050 teks, 051–070 gambar), 4 pilihan dalam urutan identik, teks pertanyaan, kunci teks penuh, pembahasan, `image.src` dan `image.alt`; konten lengkap kedua modul dibandingkan setelah substitusi tautan gambar relatif Markdown ke URL GitHub raw absolut. **560 soal + 16 modul PASS, 0 mismatch**, seluruh JSON dapat di-parse. Tidak dilakukan audit ulang mutu akademik atau pemeriksaan empiris siswa.

| Bab | Soal teks | Soal bergambar | Modul | Audit JSON | SHA sumber Markdown (teks · gambar · materi) |
|---|---:|---:|---:|---|---|
| B01 | 50 | 20 | 2 | PASS | `9141d6dc7dcf63e63b87ef5e12383c73647fcddd` · `7e18371134eb5ae351b4455a9f9498b82573fed0` · `fa8b1023078d4f0c80c562a1a24df023cf15333c` |
| B02 | 50 | 20 | 2 | PASS | `fc4e222ed300c125b817f0f54f6de285cb68c727` · `dfbb3e586e5f4bdcdb7673ba8f798fea3cdb58de` · `88eaea96f9f177cfe50c31a84fd181e3fa6031b8` |
| B03 | 50 | 20 | 2 | PASS | `ef61ffa8a069bfd98faafcefecaf3f45174db164` · `e8fcf330688be30991be9141edc7777c7f465c5e` · `9e09a7a9851d9bcb7f03aa72e2fe5cc6ce7818c3` |
| B04 | 50 | 20 | 2 | PASS | `04fd8a899ccf83dc0b7d7b8b21a24500c3238333` · `0ae4982b3cd5434ee33372b6377c5e76d6d41cfd` · `3d1832781072528712684c6b29ef3ffcf0ecf935` |
| B05 | 50 | 20 | 2 | PASS | `c8e9ed3c42d963dedb28cbc016a6df0e81748c0d` · `fb03c8bc4c2e7f9408d8f782868cad83ee826601` · `2c9b2c21406903f749a9c2e4200099c85aec2a79` |
| B06 | 50 | 20 | 2 | PASS | `765d8920c17a4d0a8fd9497fb8239d1ac8174917` · `a8f1810a9d09ba0ca1c8e723f03cd8cbce9aaa3b` · `6e6b94bb27b33350a3ececbf7d5fe2c606567e46` |
| B07 | 50 | 20 | 2 | PASS | `4fe803d82a1642489a53bb0b43fbab8546a9d3ac` · `1b8ae4b55a4e2290dd06336aacfe0df2bb9012e8` · `8ea6204d4a9b87fec44595d490340ba4006f0e80` |
| B08 | 50 | 20 | 2 | PASS | `df48ffa1b84398c65ee1032a522e4dae8d441c80` · `90d11e5add8a160842837679941ab58f0f77c76e` · `9ebe6daa6fd8044a81fe7e231b654c3e656fa675` |

**Batas audit:** Validasi ini memeriksa kecocokan path media terhadap sumber; bukan pemeriksaan pemuatan URL SVG dari jaringan pada HP. SVG pernah diperiksa pada fase produksi sumber, tetapi belum diuji ulang dalam browser pascakonversi. Pemetaan modul mengganti hanya URL `assets/...` menjadi URL GitHub raw untuk akses dari JSON. Tidak menyunting teks pengajaran, LaTeX, atau sumber Markdown. `HTML/grade-config.js` tidak diubah, sehingga menu IPAS belum aktif. Belum ada uji WebView.

**Akumulasi fase konversi:** SBD 225 + PAI 300 + ENG 500 + IPAS 560 = **1.585 dari 1.945 soal**, termasuk **66 modul**. Total **99 JSON bank/materi**, dan 4 manifest. Masih tersisa **PJOK Kelas 3: 360 soal** (8 bab). PJOK belum memiliki modul pada sumber sehingga harus diberi status `NOT_AVAILABLE_IN_SOURCE`, bukan menciptakan modul.
