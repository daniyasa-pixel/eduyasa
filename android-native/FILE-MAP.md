# EDUYASA Native: peta UI, tombol, dan ID

Referensi tetap:
- index/EDUYASA - Belajar Menyenangkan.html
- HTML/eduyasa_belajar_menyenangkan.html
- HTML/grade-config.js dan HTML/quiz-bank.js

Jetpack Compose tidak memakai android:id XML seperti layout Android klasik.
Identitas komponen untuk pengujian diberikan melalui Modifier.testTag().
ID data pertanyaan tidak diubah.

| Referensi HTML | testTag Native | Fungsi |
| --- | --- | --- |
| pilih-kelas, start-learning-btn | class-1 sampai class-6, start-learning-btn | Kelas 3 dan 6 tersedia |
| subjectGrid | subject-MTK, subject-BIN, subject-IPAS, subject-PKN, subject-ENG, subject-PAI, subject-SBD, subject-PJK | Maksimal 2 mapel |
| btnNext1 | btnNext1 | Menuju pilihan bab |
| chapterList | chapter-MTK-B01 atau chapter-K03-MTK-B01, dst | Maksimal 4 bab; minimal 1 bab tiap mapel |
| btnBack2, btnNext2 | btnBack2, btnNext2 | Kembali / baca materi |
| materiContainer | materiContainer | MOD-01/MOD-02 dipilih acak |
| btnBack3, btnNext3 | btnBack3, btnNext3 | Kembali / persiapan kuis |
| quizPrepInfo, btnBack4, btnStartQuiz | quizPrepInfo, btnBack4, btnStartQuiz | Kuis 20 soal acak |
| quizQText, quizOptions | quizQText, option-A, option-B, option-C, option-D | Kunci dibandingkan berdasarkan teks opsi |
| btnQuizAction | btnQuizAction | Koreksi / Lanjut / Lihat Hasil |
| resultScore, resultCorrect, resultWrong | resultScore, resultCorrect, resultWrong | Nilai, jumlah benar dan salah |
| btnOpenReport, reportForm | btnOpenReport, studentName, parentPhone, btnSendReport | Buka WhatsApp dengan laporan terisi |
| hasil / Ulangi Kuis | btnRetryQuiz | Memuat 20 soal baru dari bank sumber |
| btnBackToHome, restartModal | btnBackToHome, btnConfirmBackHome, btnCancelBackHome | Konfirmasi kembali ke awal |

## ID konten, tidak berubah
- Kelas 6: kode bab MTK-B01, soal MTK-B01-M01-001, dst.
- Kelas 3: kode bab K03-MTK-B01, soal K03-MTK-B01-M01-001, dst.
- Pola berkas: mtk/mtk-b01.json, mtk/mtk-b01-materi.json, dan untuk Kelas 3 kelas-3/mtk/...
- Paket tambahan ditentukan question-packs.json, tidak diduplikasi/diubah oleh Android.
- Opsi JSON tetap tanpa label A/B/C/D; label hanya ditambahkan saat ditampilkan.
- Kunci jawaban adalah teks pilihan yang persis sama dengan salah satu dari empat opsi.

## Status implementasi
Sudah ditulis: UI Compose semua langkah; validasi kelas, mapel, bab;
pembaca JSON; manifest paket; acak modul; kuis 20 soal (kuota gambar
bila berlaku); koreksi; hasil; ulangi kuis; pelaporan WhatsApp (+62).

Belum tervalidasi: Gradle Sync, build APK, tes UI HP, renderer LaTeX native,
Markdown tabel lengkap, gambar di konten modul, audio non-TTS, cache offline.
Jangan dianggap siap produksi sebelum pengujian tersebut.

## Tahap 2: profil siswa (Compose UI testTag)
- add-profile: buka form Tambah Profil Siswa (otomatis dibuka saat pertama tanpa profil).
- student-profile-name: masukkan nama siswa.
- profile-grade-3 / profile-grade-6: pilih kelas.
- save-profile, cancel-profile: simpan/batal.
- profile-<UUID>: pilih profil, kelas terisi otomatis.
- edit-profile-<UUID>: ubah nama/kelas.
- delete-profile, confirm-delete-profile: hapus profil dengan konfirmasi.

## Tahap 2: pemisah semester
- semester-<KELAS>-<MAPEL>-1, semester-<KELAS>-<MAPEL>-2: header semester.
- semester-pending-3-SBD dan semester-pending-6-BIN: belum ada pemisah
  karena susunan bab/edisi LKS perlu dicek.
- Bukti: pembagian PKN & PAI Kelas 3 serta PAI Kelas 6 sudah terkonfirmasi,
  sebelas lainnya provisional dengan label peringatan, bukan ketentuan resmi.

## Tahap 3: riwayat dan evaluasi
- stats-profile-<UUID>: buka evaluasi untuk siswa dari halaman pemilihan profil.
- open-statistics: buka evaluasi siswa aktif dari pilihan mata pelajaran.
- result-open-statistics: buka evaluasi setelah hasil kuis tersimpan.
- learning-statistics, stats-back, stats-retry: halaman evaluasi.
- stats-sessions, stats-questions, stats-accuracy: KPI siswa aktif.
- history-saved: konfirmasi penyimpanan otomatis di halaman hasil.
- retry-save-history: tombol ulang menyimpan jika gagal.
- Database SQLite private per profil: study_sessions + study_answers.
- Tidak ada perubahan ID soal, file JSON, backend maupun frontend web.
