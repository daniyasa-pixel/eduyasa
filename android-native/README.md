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
