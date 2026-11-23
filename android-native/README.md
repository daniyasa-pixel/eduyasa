# EDUYASA Android Native - Tahap 1

Kotlin + Jetpack Compose; tidak menggunakan WebView. Berbasis referensi
`index/EDUYASA - Belajar Menyenangkan.html`,
`HTML/eduyasa_belajar_menyenangkan.html`, `HTML/grade-config.js`
dan `HTML/quiz-bank.js`.

Buka folder `android-native` sebagai project di Android Studio.
Butuh JDK 17, SDK 35, Gradle 8.9 (AGP 8.7.3). Jika Gradle Wrapper belum tersedia,
gunakan Gradle 8.9 lokal di Android Studio atau jalankan
`gradle wrapper --gradle-version 8.9`.

Semua bank soal, ID dan materi tetap pada repo GitHub, tidak ada salinan atau
perubahan JSON di aplikasi. Tampilan/native logic ada di `app/`.
Katalog bab tahap awal adalah snapshot; saat ada perubahan daftar bab
sinkronkan `app/src/main/assets/catalog.json` dari data repo.
Katalog bab Kelas 6 sudah diselaraskan dari chapter_title setiap file soal utama.
Jika judul bab sumber berubah, perbarui katalog tanpa mengubah ID bab/soal.

Belum diverifikasi build pada Android Studio atau perangkat.
Renderer Markdown/LaTeX dan audio telah ditambahkan ke kode; hasil visualnya belum diverifikasi melalui build APK/tes HP.

## Tahap pengembangan berikutnya
- Uji Gradle Sync / build APK dan cek layar HP di Android Studio.
- Renderer native Markdown yang lengkap, tabel, ilustrasi modul, dan matematika LaTeX.
- Audio belajar khusus (non-TTS), suara jawaban, zoom gambar dengan gestur.
- Cache data agar materi dan soal yang pernah diunduh dapat dibuka secara offline.

## Tahap 2 — frontend profil dan pengelompokan semester

**Sistem profil siswa (lokal HP)**
- Di halaman awal pengguna memilih profil siswa, bukan kelas tiap kali belajar.
- Tambah profil: nama + Kelas 3/6, tersimpan dengan SharedPreferences milik aplikasi.
- Ubah nama/kelas, hapus profil dengan konfirmasi; bisa lebih dari satu siswa di HP sama.
- Mengetuk profil langsung membuka pilihan mata pelajaran sesuai kelas tersimpan.
- Nama siswa otomatis terisi dalam formulir laporan WhatsApp.
- Tidak memakai akun, internet, atau JSON GitHub untuk menyimpan profil; data hanya di HP.
- Menghapus data aplikasi/uninstall dapat menghapus profil lokal.

**Pemisahan semester pada daftar bab**
- Konfigurasi hanya di `SemesterPresentation.kt`, bukan perubahan bank soal atau JSON.
- Kelas 3: BIN 1–2 / 3–4, PKN 1–2 / 3–4, MTK 1–3 / 4–5, IPAS 1–4 / 5–8, PAI 1–5 / 6–10, ENG 1–5 / 6–10, PJK 1–4 / 5–8.
- Kelas 6: MTK 1–2 / 3–4, IPAS 1–3 / 4–6, PKN 1–4 / 5–7, ENG 1–3 / 4–6, PAI 1–5 / 6–10, SBD 1–3 / 4–5, PJK 1–3 / 4–5.
- Pembagian yang sudah terkonfirmasi berdasarkan referensi resmi: Kelas 3 PKN, PAI; Kelas 6 PAI.
- Kesebelas mapel lain bertanda **pembagian sementara**, perlu cocokkan LKS sebelum ditetapkan.
- Kelas 3 Seni Rupa dan Kelas 6 Bahasa Indonesia belum dibagi agar tidak memberi semester yang tidak terbukti.
- Aturan pemilihan jumlah bab, ID, dan pengambilan file JSON **tidak diubah**.

**QA belum dilaksanakan**: Gradle Sync, build APK, tes navigasi di perangkat,
tes profil setelah aplikasi ditutup, pengelompokan UI, dan formulir WhatsApp.

## Tahap 3 — penyimpanan lokal dan evaluasi otomatis

- Selesai mengoreksi soal terakhir (15, 20, atau 25 pilihan), aplikasi otomatis menyimpan **satu sesi kuis** dan semua jawabannya ke database SQLite privat di HP. Pengguna tidak harus mengirim laporan WhatsApp.
- Disimpan: ID profil lokal, kelas, waktu selesai, ID sesi, ID soal, mapel, bab, jawaban yang dipilih, kunci jawaban, dan benar/salah. JSON GitHub tidak diubah.
- Penyimpanan memakai satu transaksi (atomik) dan ID sesi unik, sehingga menekan duplikasi hasil saat tombol simpan ulang digunakan.
- Jika penyimpanan gagal, halaman hasil memberi peringatan dan tombol **COBA SIMPAN LAGI**.
- Dari layar nama siswa atau daftar mata pelajaran, tombol **Statistik / Lihat Evaluasi Belajar** membuka ringkasan dan riwayat khusus siswa yang dipilih.
- Statistik: jumlah sesi, total jawaban, ketepatan keseluruhan, ringkasan per mapel, rincian per bab, dan 10 sesi terbaru.
- Interpretasi awal: minimal 10 jawaban dari 2 sesi kuis untuk menilai ketepatan (80%+ mulai dipahami, 60–79% berkembang, di bawah 60% latihan ulang). Di bawah syarat tersebut status tetap **data awal**.
- Statistik adalah indikator latihan, **bukan** kesimpulan pasti bahwa siswa sudah menguasai materi; jumlah soal, cakupan bab dan pengulangan soal dapat memengaruhi hasil.
- Data antarprofil terpisah. Jika profil dihapus setelah konfirmasi, riwayat latihan profil tersebut turut dihapus; riwayat profil lainnya aman.
- Riwayat berada **hanya di HP tersebut**; belum ada sinkronisasi cloud atau pemindahan data antarperangkat. Uninstall atau hapus data aplikasi bisa menghapusnya.
- Belum ada tes Gradle/compile/APK/HP. Pengujian riwayat dan statistik perlu dilakukan sebelum dipakai siswa.

### Kode
- `StudyHistory.kt`: SQLite dan agregasi statistik
- `LearningStatisticsScreen.kt`: UI evaluasi dan histori
- `MainActivity.kt`: penyimpanan kuis dan navigasi statistik

## Tahap 4 — matematika, gambar, dan listening Native

**Implementasi di branch `android` (belum build APK / tes HP):**
- `EduRichContent.kt`: Markdown native Android TextView + Markwon 4.6.2; mode LaTeX asli (bukan sekadar menghapus tanda `$`), didukung plugin inline parser dan LaTeX. Sumber EDUYASA memakai `$...$`; UI mengonversi pembatas tampilannya menjadi `$$...$$` tanpa menyentuh isi JSON.
- Materi menampilkan heading, tulisan tebal, paragraf, daftar dan tabel sederhana, gambar materi yang sebelumnya hanya placeholder.
- `EduRemoteImage` memakai Coil SVG decoder 2.7.0; menerima gambar PNG/JPG/SVG HTTPS dari GitHub, menampilkan pesan jika gagal, memakai alt text dari JSON, dan zoom ilustrasi.
- `EduAudioPlayer.kt` membaca `audio.type=tts`, `audio.lang` (contoh `en-US`), `audio.rate` (contoh `0.85`), serta `audio.text` dari bank soal Kelas 3; mendukung file audio HTTPS jika dipakai nanti. TTS berhenti saat siswa pindah soal/halaman.
- `EduyasaRepository.kt` mempertahankan metadata audio/alternatif gambar; ID soal dan isi JSON tetap sama. Jumlah soal kini dapat dipilih 15, 20, atau 25 di aplikasi Android.
- Semua perubahan pada **folder Android di branch `android`**, bukan `main`, bukan JSON/HTML.
- Dependensi pihak ketiga: Markwon (Apache-2.0), renderer transitive JLaTeXMath-Android (perlu pemeriksaan kepatuhan lisensi sebelum distribusi APK/komersialisasi).

**QA wajib di Android Studio sebelum menyatakan selesai:**
1. Gradle Sync / dependency download / build APK; perlu Gradle Wrapper atau Gradle lokal sesuai README.
2. Kelas 6 MTK Bab 1, tampilkan soal dengan `$\\frac{1}{4}$` dan opsi dengan `$\\frac{3}{4}$`; pastikan bentuk pecahan nyata dan pembahasan rapi.
3. Kelas 3 MTK Bab 1 bergambar: cek gambar `.svg` sumber GitHub, zoom (+/−), ukuran gambar, gagal jaringan.
4. Kelas 3 BIN Bab 1 materi: lihat ilustrasi `.svg`, Markdown heading & tebal; tautan dan daftar tidak kehilangan konten.
5. Kelas 3 ENG Bab 1 listening: jika soal TTS terpilih, tes lafal bahasa Inggris, `en-US`, kecepatan 0.85, tombol play ulang, perpindahan soal dan HP tanpa mesin TTS.
6. Uji tiga pilihan kuis (15, 20, 25 soal), perhitungan skor, penyimpanan SQLite, statistik, profil, semester, dan laporan WA.
7. Tes tidak ada crash saat koneksi internet mati atau media tidak berhasil dimuat.

**Catatan:** Kode sudah ditulis dan pemeriksaan statis dilakukan. Keberhasilan tampilan rumus/audio/SVG di HP belum terkonfirmasi karena belum ada build serta uji fisik Android Studio.

## Tahap 5 — Level, Bintang, kuis fleksibel, dan menu samping kanan

**Kode sudah masuk branch `android`, belum di-build/diuji di HP.**

- **Bab:** Level 0 jika belum ada latihan; jika sudah 1–20 jawaban Level 1, 21–40 Level 2, dan seterusnya. Semua jawaban dari kuis campuran dihitung ke bab asal; level tidak turun.
- **Bintang bab:** berdasarkan ketepatan maksimal **20 jawaban terbaru** pada bab tersebut: 0–19%=0, 20–39%=1, 40–59%=2, 60–74%=3, 75–89%=4, 90–100%=5. Bintang bisa naik/turun; hasil dari satu sesi atau sampel kecil ditandai sementara.
- **Mata pelajaran/semester:** penilaian bintang dihitung dari **rata-rata persentase** bab yang sudah dikerjakan, tanpa menghitung bab belum dipelajari sebagai nol. Ringkasan belum lengkap ditandai sementara. Pemetaan semester yang belum didukung LKS tetap tidak ditampilkan sebagai fakta.
- **Kuis:** sebelum mulai pilih 15, 20, atau 25 soal (default 20); bank soal tetap acak, tanpa mengubah JSON. Untuk mapel bergambar, kuota gambar proporsional menjadi 4/5/6 soal.
- **Riwayat lokal SQLite:** menerima ketiga ukuran kuis dengan transaksi atomik; tidak ada perubahan versi tabel atau migrasi data. Perhitungan per bab memakai seluruh jawaban untuk Level dan sampel 20 jawaban terbaru untuk Bintang.
- **UI:** kartu mata pelajaran menampilkan bintang dan cakupan bab; daftar bab menampilkan Level serta Bintang; halaman Evaluasi berisi ringkasan, saran bab prioritas, bintang semester, detail tiap bab, serta **hanya 10 sesi latihan terakhir** dengan mapel/bab.
- **Navigation Drawer:** tombol ☰ di kanan atas, panel masuk dari kanan, menu Beranda/Mulai Belajar/Evaluasi Belajar/Kelola Profil/Pengaturan/Tentang. Meninggalkan kuis lewat menu memerlukan konfirmasi.
- **Pengaturan berfungsi:** jumlah soal bawaan dan aktif/nonaktif audio listening, disimpan di SharedPreferences. Tema, ukuran huruf, dan backup belum diimplementasikan; UI tidak menampilkan toggle palsu.
- **Lokasi kode:** `LearningGamification.kt`, `LearningProgressBadges.kt`, `StudyHistory.kt`, `LearningStatisticsScreen.kt`, `MainActivity.kt`, `EduyasaRepository.kt`, dan dependency Compose Animation.
- **Larangan:** jangan mengubah JSON, bank soal, ID, modul, atau `main` saat memperbaiki frontend Android.

**QA wajib setelah sinkron ke Android Studio:**
1. Gradle Sync + build + run di HP, perbaiki error Compose/Kotlin bila ada.
2. Pilih 15, 20, dan 25 soal; selesai dan cek simpan otomatis, peringkat bintang, serta nilai WA.
3. Cek level setelah 20 dan 21 soal khusus satu bab; kuis 2 bab mencatat jumlah soal masing-masing dengan benar.
4. Ulang bab yang sama, cek bintang dapat naik/turun sementara Level tidak turun.
5. Periksa penilaian sementara, bab belum dinilai, bintang mapel, dan semester tanpa pemetaan resmi.
6. Pastikan menu muncul **dari kanan**, tidak kehilangan data tanpa konfirmasi, dan pengaturan tersimpan setelah aplikasi ditutup.
7. Uji perpindahan 2 profil, riwayat terpisah, dan batas 10 hasil terbaru.

## Tahap 6 — UI animasi dan efek suara permainan

**Semua kode di branch `android`; tetap belum diverifikasi build APK / pengujian HP.**

- Kuis: kartu jawaban berubah warna hijau/merah dengan transisi halus ±240–260 ms, centang/silang, dan perubahan skala kecil. Panel pembahasan tampil dengan animasi `fadeIn + expandVertically` sekitar 250 ms.
- Pencapaian setelah kuis: `findNewMilestones()` membandingkan Level dan Bintang per bab **sebelum** dan **sesudah** sesi baru benar-benar berhasil disimpan di SQLite. Saat meningkat, layar hasil menampilkan kartu `PENCAPAIAN BARU`, Level Up, dan bintang menyala bergiliran. Jika tidak ada peningkatan, hasil tampil normal tanpa animasi kemenangan.
- Suara: `EduSoundEffects.kt` membuat lima efek pendek orisinal via `AudioTrack` (benar, salah, kenaikan bintang, kenaikan level, selesai kuis). Tidak perlu aset audio eksternal, jaringan, maupun perubahan bank soal. Efek jawaban diputar saat `KOREKSI JAWABAN`, sedangkan efek hasil dibunyikan sesudah penyimpanan berhasil, mengutamakan Level Up > Star Up > penutup biasa.
- Pengaturan: sakelar induk SFX, lima centang terpisah, tombol ▶ untuk mendengar contoh; semuanya disimpan dalam SharedPreferences. `Audio Listening` Bahasa Inggris terpisah.
- Navigasi: overlay gelap Navigation Drawer memiliki transisi pudar; panel tetap masuk dari kanan.
- Komponen: `EduAchievements.kt` untuk perhitungan milestone dan animasi hasil; `EduSoundEffects.kt` untuk synthesizer SFX; `MainActivity.kt` sebagai penghubung UI dan preferensi.

**QA sebelum dirilis:**
1. Lakukan Gradle Sync/compile di Android Studio, jalankan di perangkat nyata, dan perbaiki error jika ditemukan.
2. Uji audio saat volume sistem kecil, mode senyap, headset, dan Audio Listening sedang aktif; pastikan gagal memutar suara tidak membuat aplikasi crash.
3. Centang/matikan satu per satu SFX dan sakelar induk, tutup lalu buka ulang aplikasi; verifikasi pilihan tidak berubah.
4. Jawab benar dan salah, pastikan warna/animasi dan suara sesuai pilihan tanpa menghambat tombol Lanjut.
5. Selesaikan kuis pertama, ulang kuis dengan nilai berbeda, pastikan perayaan muncul **hanya saat Level/Bintang meningkat**, dan Level tidak turun.
6. Uji penyimpanan gagal lalu retry, dan cek tidak ada perayaan ganda pada sesi yang sama.
7. Uji animasi di HP kelas menengah dan pengaturan aksesibilitas pengurangan animasi; durasi serta tampilan bisa disesuaikan dari hasil uji.

**Tidak berubah:** file soal/materi JSON, identitas soal, HTML, serta branch `main`.

## Perbaikan respons sentuh pilihan A/B/C/D (23 November 2026)

Laporan pengujian: pada awal kuis tombol terasa normal, setelah sekitar soal 10–15
pilihan A/B/C/D kadang terasa tidak responsif lalu merespons kembali. Belum ada
profiling/perangkat untuk memastikan akar masalah. Audit kode menemukan:
- Sebelumnya `EduRichText` selalu menjalankan `markwon.setMarkdown()` di
  `AndroidView.update`, termasuk saat hanya mengubah pilihan jawaban.
  Parsing Markdown/LaTeX berulang pada UI thread berpotensi menyebabkan lag.
- Sebelumnya pilihan menggunakan `Surface` dengan `Modifier.clickable` saja,
  sedangkan teks anak menggunakan Android `TextView` yang ditanam di Compose.
- Tidak ada penanda pilihan teks yang langsung menunjukkan tap sudah tercatat.

Perbaikan kode di branch `android`:
1. `EduAnswerText`: teks soal/jawaban biasa menggunakan `Text` Compose
   (tanpa `AndroidView`); rumus/Markdown tetap memakai Markwon untuk menjaga LaTeX.
2. `EduRichText` hanya menjalankan `setMarkdown` jika teks, ukuran atau
   warna berubah, dan TextView dibuat non-clickable/non-focusable agar area
   pilihan dikuasai tombol induknya.
3. Pilihan memakai `Surface(onClick)` Material3 untuk efek ripple instan,
   animasi ditekan singkat, dan animasi warna lebih cepat ketika memilih.
4. Teks `Pilihan kamu: A/B/C/D` muncul saat memilih dan sebelum koreksi.
5. Posisi scroll kembali ke atas setiap perpindahan soal.

**Masih wajib pengujian** pada HP/perangkat pengguna:
- Aktifkan Opsi Pengembang > Tampilkan ketukan (Show taps).
- Uji 25 soal berturut-turut, termasuk soal dengan rumus, gambar dan listening,
  dengan keempat pilihan ditekan bergantian sebelum menekan Koreksi.
- Pastikan titik ketuk terlihat, ada ripple serta label `Pilihan kamu` berubah
  segera; bila titik muncul tetapi aplikasi tidak bereaksi, rekam layar dan
  tangkap Logcat (filter `Choreographer`, `Skipped frames`, `AndroidRuntime`,
  dan `InputDispatcher`) untuk menemukan hambatan sebenarnya.
- Bandingkan dengan/ tanpa SFX, pada HP yang sama, dan uji setelah soal 10–15.
- Jika setelah patch tetap lambat, perlu profiler CPU/main thread dan memori
  melalui Android Studio; jangan anggap penyebab sudah pasti tanpa pengukuran.

Tidak ada perubahan pada JSON, HTML, modul atau ID soal.

## Tahap 8 — Smart Preload dan respons kuis (23 November 2026)

**Kode ditulis di branch `android`, belum dibuild/diuji pada HP.**
Perubahan dibatasi pada frontend Android Native; JSON/ID soal, materi, HTML, dan `main` tetap utuh.

- `QuizPreloadController.kt`: ketika soal ke-N aktif, menyiapkan teks soal, empat opsi, dan pembahasan soal N, N+1, N+2 dengan coroutine `Dispatchers.Default`, tidak di UI thread. Hanya tiga soal pada window memori; soal di belakang dibuang. Membatalkan pekerjaan usang saat kuis diulang atau ditutup. Menggunakan `mutableStateMapOf` untuk memberi tahu UI jika siap/gagal.
- Pada awal kuis atau jika ada soal aktif belum siap: indikator `Menyiapkan soal...` tampil di halaman. Setelah siap, isi soal langsung muncul.
- Menekan `LANJUT`: jika soal selanjutnya siap, pindah segera; jika belum, tombol dinonaktifkan sementara dan indikator `Menyiapkan soal berikutnya...` muncul. `snapshotFlow` memonitor kesiapan dan otomatis pindah begitu siap tanpa tap kedua. Jika persiapan gagal/melewati batas 10 detik, tampil `Coba Lagi`; retry tidak menghilangkan soal aktif. Tidak memakai Toast Android.
- `EduRichContent.kt`: teks biasa memakai Compose Text; pemrosesan sintaks matematika/Markdown dilakukan lebih dulu pada background dan disimpan bersama hasil persiapan; seluruh bank soal JSON tidak berubah. Render Markwon dibuat ulang hanya ketika teks/ukuran/warna berubah; instance Markwon/LaTeX dipakai ulang berdasar ukuran teks. Regex matematika juga dipakai ulang.
- Coil menerima permintaan prefetch gambar soal secara asynchronous dengan loader/cache yang sama; **gambar gagal dimuat tidak menahan perpindahan soal**. Ruang gambar pada kuis setinggi 210 dp agar layout tidak meloncat saat gambar selesai dimuat. Decoder gambar dan pembuatan View aktual tetap berjalan menggunakan mekanisme Android/Coil yang tepat, bukan membuat View di thread background.
- `QuizAnswerChoice.kt`: tombol A/B/C/D dipisahkan dari fungsi layar besar; tombol Material3 tetap punya ripple, skala tekan, dan warna umpan balik yang responsif. Text yang sudah dipersiapkan diteruskan ke komponen.
- Marker `EDUYASA.RenderRichText` ditambahkan melalui `android.os.Trace` supaya Android Studio System Trace dapat menunjukkan biaya render Markwon dalam sesi panjang.
- Page Pengaturan tidak lagi keliru menampilkan indikator langkah belajar.

**Uji wajib setelah pull di laptop:**
1. Sync/Build/Run APK di Android Studio; masih belum diverifikasi compile Kotlin/Compose.
2. Kerjakan 25 soal beruntun pada beberapa mapel, termasuk rumus Matematika, SVG/gambar, dan Listening.
3. Tekan pilihan A/B/C/D bergantian, cek ripple, indikator pilihan, dan kelancaran dari soal awal sampai soal terakhir.
4. Tekan LANJUT segera; jika preloader belum selesai, indikator inline muncul lalu pindah otomatis; tidak ada double-tap.
5. Simulasikan kondisi perangkat lambat (CPU/memori): bila persiapan gagal, Coba Lagi berfungsi tanpa menghapus soal yang sedang dijawab.
6. Bandingkan System Trace, jank/long frames, dan penggunaan RAM dengan sesi sebelumnya; cari rentang EDUYASA.RenderRichText jika ada.
7. Uji tombol Kembali, menu samping, kuis ulang, variasi 15/20/25 soal, SFX, status jawaban, hasil, Level/Bintang, dan riwayat tetap bekerja.

**Batasan teknis yang harus jujur:** Smart Preload melakukan praolah teks/rumus dan permintaan pemuatan gambar sebelum tampilan dibuat. Android TextView dan komposisi Compose baru dibuat pada UI thread ketika dibutuhkan. Ini lebih aman daripada membuat View Android di background, tetapi tidak menjamin semua frame bebas jank; optimasi lanjutan harus mengikuti hasil pengujian di HP. Perubahan LazyColumn untuk halaman materi/evaluasi panjang belum dilakukan dalam tahap ini karena perlu pengujian layout terpisah.
