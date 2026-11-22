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
Renderer Markdown/LaTeX penuh dan audio non-TTS perlu integrasi lanjutan.

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
