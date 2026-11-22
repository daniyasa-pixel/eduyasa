package id.eduyasa.nativeapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Riwayat latihan tersimpan di database SQLite privat aplikasi, terpisah dari
 * JSON materi/bank soal GitHub. Semua identitas siswa memakai profile ID lokal.
 */
data class SavedAnswer(
    val position: Int,
    val subjectId: String,
    val chapter: Int,
    val questionId: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val correct: Boolean
)

data class SavedSession(
    val id: String,
    val profileId: String,
    val grade: Int,
    val completedAt: Long,
    val answers: List<SavedAnswer>
)

data class HistoryTopic(val subjectId: String, val chapter: Int)

data class HistorySession(
    val id: String,
    val grade: Int,
    val completedAt: Long,
    val total: Int,
    val correct: Int,
    val topics: List<HistoryTopic> = emptyList()
) {
    val score: Int get() = if (total == 0) 0 else (100 * correct + total / 2) / total
}

data class LearningProgress(
    val grade: Int,
    val subjectId: String,
    val chapter: Int?, // null = ringkasan satu mata pelajaran
    val total: Int,
    val correct: Int,
    val sessions: Int,
    val recentTotal: Int,
    val recentCorrect: Int
) {
    val accuracy: Int get() = LearningGamification.accuracy(correct, total)
    val recentAccuracy: Int get() = LearningGamification.accuracy(recentCorrect, recentTotal)
    val level: Int get() = LearningGamification.level(total)
    val stars: Int get() = if (recentTotal == 0) 0 else LearningGamification.stars(recentAccuracy)
    val isProvisional: Boolean get() = sessions < 2 || recentTotal < LearningGamification.RECENT_SAMPLE

    /**
     * Label bersifat indikator latihan, BUKAN diagnosis penguasaan materi.
     * Minimal 10 jawaban dari dua sesi berbeda untuk klasifikasi yang lebih kuat.
     */
    val evaluation: String get() = when {
        sessions < 2 || total < 10 -> "Data awal — perlu latihan lagi"
        accuracy >= 80 -> "Mulai dipahami"
        accuracy >= 60 -> "Sedang berkembang"
        else -> "Perlu latihan lagi"
    }
}

data class LearningStatistics(
    val totalSessions: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val subjects: List<LearningProgress>,
    val chapters: List<LearningProgress>,
    val recentSessions: List<HistorySession>
) {
    val overallAccuracy: Int get() = if (totalQuestions == 0) 0
        else (correctAnswers * 100 + totalQuestions / 2) / totalQuestions
}

class StudyHistoryStore(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "eduyasa_study_history.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE study_sessions (
                id TEXT PRIMARY KEY NOT NULL,
                profile_id TEXT NOT NULL,
                grade INTEGER NOT NULL,
                completed_at INTEGER NOT NULL,
                total_questions INTEGER NOT NULL,
                correct_questions INTEGER NOT NULL
            )""".trimIndent()
        )
        db.execSQL(
            """CREATE TABLE study_answers (
                session_id TEXT NOT NULL,
                position INTEGER NOT NULL,
                subject_id TEXT NOT NULL,
                chapter_number INTEGER NOT NULL,
                question_id TEXT NOT NULL,
                selected_answer TEXT NOT NULL,
                correct_answer TEXT NOT NULL,
                is_correct INTEGER NOT NULL,
                PRIMARY KEY (session_id, position),
                FOREIGN KEY (session_id) REFERENCES study_sessions(id) ON DELETE CASCADE
            )""".trimIndent()
        )
        db.execSQL("CREATE INDEX ix_sessions_profile_time ON study_sessions(profile_id, completed_at DESC)")
        db.execSQL("CREATE INDEX ix_answers_subject_chapter ON study_answers(subject_id, chapter_number)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Versi 1 adalah skema awal. Migrasi berikutnya wajib mempertahankan riwayat.
        if (oldVersion != newVersion) error("Belum ada migrasi riwayat untuk versi database ini.")
    }

    /**
     * Transaksi tunggal: sesi 15/20/25 jawaban tersimpan bersama atau batal.
     * ID session unik mencegah sebuah kuis dihitung dua kali pada retry penyimpanan.
     * @return true jika sesi baru tersimpan, false jika ID sesi sudah ada.
     */
    fun saveCompletedSession(session: SavedSession): Boolean {
        require(session.profileId.isNotBlank())
        require(session.id.isNotBlank())
        require(session.grade in setOf(3, 6))
        require(session.answers.size in LearningGamification.QUIZ_LENGTHS) {
            "Kuis harus berisi 15, 20, atau 25 jawaban."
        }
        require(session.answers.map { it.position }.toSet() ==
            session.answers.indices.toSet()) { "Posisi jawaban harus lengkap dan unik." }
        require(session.answers.all { it.questionId.isNotBlank() })
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val values = ContentValues().apply {
                put("id", session.id)
                put("profile_id", session.profileId)
                put("grade", session.grade)
                put("completed_at", session.completedAt)
                put("total_questions", session.answers.size)
                put("correct_questions", session.answers.count { it.correct })
            }
            val rowId = db.insertWithOnConflict(
                "study_sessions", null, values, SQLiteDatabase.CONFLICT_IGNORE
            )
            if (rowId == -1L) {
                db.setTransactionSuccessful()
                false
            } else {
                session.answers.forEach { a ->
                    val answer = ContentValues().apply {
                        put("session_id", session.id)
                        put("position", a.position)
                        put("subject_id", a.subjectId)
                        put("chapter_number", a.chapter)
                        put("question_id", a.questionId)
                        put("selected_answer", a.selectedAnswer)
                        put("correct_answer", a.correctAnswer)
                        put("is_correct", if (a.correct) 1 else 0)
                    }
                    if (db.insertOrThrow("study_answers", null, answer) == -1L) {
                        error("Gagal menyimpan jawaban soal ke-" + a.position)
                    }
                }
                db.setTransactionSuccessful()
                true
            }
        } finally {
            db.endTransaction()
        }
    }

    /**
     * Riwayat dan evaluasi hanya untuk siswa ini; profil lain tetap terpisah.
     * Materi dan nama profil dibaca dari sumber lain, bukan disalin ke tabel soal.
     */
    fun statistics(profileId: String): LearningStatistics {
        require(profileId.isNotBlank())
        val db = readableDatabase
        val sessions = mutableListOf<HistorySession>()
        db.rawQuery(
            """SELECT id, grade, completed_at, total_questions, correct_questions
               FROM study_sessions WHERE profile_id = ?
               ORDER BY completed_at DESC, id DESC""".trimIndent(),
            arrayOf(profileId)
        ).use { cursor ->
            while (cursor.moveToNext()) {
                sessions += HistorySession(
                    cursor.getString(0), cursor.getInt(1), cursor.getLong(2),
                    cursor.getInt(3), cursor.getInt(4)
                )
            }
        }

        data class Entry(val sessionId: String, val grade: Int, val subject: String,
                         val chapter: Int, val correct: Boolean)
        val answers = mutableListOf<Entry>()
        db.rawQuery(
            """SELECT a.session_id, s.grade, a.subject_id, a.chapter_number, a.is_correct
               FROM study_answers a
               INNER JOIN study_sessions s ON s.id = a.session_id
               WHERE s.profile_id = ?
               ORDER BY s.completed_at DESC, s.id DESC, a.position DESC""".trimIndent(),
            arrayOf(profileId)
        ).use { cursor ->
            while (cursor.moveToNext()) {
                answers += Entry(
                    cursor.getString(0), cursor.getInt(1), cursor.getString(2),
                    cursor.getInt(3), cursor.getInt(4) == 1
                )
            }
        }
        fun stats(group: List<Entry>, chapter: Int?): LearningProgress {
            val sample = group.first()
            val latest = group.take(LearningGamification.RECENT_SAMPLE)
            return LearningProgress(
                grade = sample.grade, subjectId = sample.subject, chapter = chapter,
                total = group.size, correct = group.count { it.correct },
                sessions = group.map { it.sessionId }.distinct().size,
                recentTotal = latest.size, recentCorrect = latest.count { it.correct }
            )
        }
        val subjects = answers.groupBy { it.grade to it.subject }
            .values.map { stats(it, null) }
            .sortedWith(compareBy<LearningProgress> { it.grade }.thenBy { it.subjectId })
        val chapters = answers.groupBy { Triple(it.grade, it.subject, it.chapter) }
            .values.map { stats(it, it.first().chapter) }
            .sortedWith(compareBy<LearningProgress> { it.grade }
                .thenBy { it.subjectId }.thenBy { it.chapter ?: 0 })
        return LearningStatistics(
            totalSessions = sessions.size,
            totalQuestions = sessions.sumOf { it.total },
            correctAnswers = sessions.sumOf { it.correct },
            subjects = subjects,
            chapters = chapters,
            recentSessions = sessions.take(10).map { session ->
                session.copy(topics = answers.asSequence()
                    .filter { it.sessionId == session.id }
                    .map { HistoryTopic(it.subject, it.chapter) }
                    .distinct()
                    .toList())
            }
        )
    }

    /**
     * Bila profil dihapus secara sadar, riwayat miliknya juga dihapus;
     * tidak mempengaruhi siswa lainnya.
     */
    fun deleteProfileHistory(profileId: String) {
        writableDatabase.delete("study_sessions", "profile_id = ?", arrayOf(profileId))
    }
}
