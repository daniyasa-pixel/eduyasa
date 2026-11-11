# Listening Bahasa Inggris Kelas 3 — Web Speech API

**Keputusan terbaru:** MP3 tidak digunakan. Semua soal listening memakai `speechSynthesis` dari browser/perangkat, bahasa `en-US`, laju awal `0.85`.

- Audio disimpan sebagai data `{"type":"tts","lang":"en-US","text":"...","rate":0.85}`.
- Teks audio hanya dipakai oleh engine TTS; **jangan tampilkan transkrip ke siswa sebelum menjawab**.
- Tombol dengarkan harus dapat ditekan ulang.
- Jika `speechSynthesis` tidak ada atau voice Inggris tidak tersedia, tampilkan pesan yang jelas; jangan diam atau menganggap suara berhasil.
- Pengujian browser Android, Chrome desktop, dan Android WebView masih diperlukan.

Lihat `../web-speech.js` untuk fungsi playback dan status suara.
