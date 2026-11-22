package id.eduyasa.nativeapp

import kotlin.math.roundToInt

/**
 * Gamifikasi belajar Android. Tidak mengubah maupun menulis bank soal JSON.
 * Level = pengalaman dari seluruh jawaban per bab (tidak pernah turun).
 * Bintang = ketepatan maksimal 20 jawaban terakhir per bab (dapat berubah).
 */
object LearningGamification {
    const val LEVEL_STEP = 20
    const val RECENT_SAMPLE = 20
    val QUIZ_LENGTHS = listOf(15, 20, 25)

    fun level(totalAnswers: Int): Int =
        if (totalAnswers <= 0) 0 else (totalAnswers - 1) / LEVEL_STEP + 1

    fun levelProgress(totalAnswers: Int): Int =
        if (totalAnswers <= 0) 0 else (totalAnswers - 1) % LEVEL_STEP + 1

    fun stars(accuracyPercent: Int): Int = when (accuracyPercent.coerceIn(0, 100)) {
        in 0..19 -> 0
        in 20..39 -> 1
        in 40..59 -> 2
        in 60..74 -> 3
        in 75..89 -> 4
        else -> 5
    }

    fun accuracy(correct: Int, total: Int): Int =
        if (total <= 0) 0 else ((correct * 100.0) / total).roundToInt()

    /** Setiap bab yang sudah dijawab punya satu suara pada ringkasan semester/mapel. */
    fun summary(chapters: List<LearningProgress>): Summary? {
        val assessed = chapters.filter { it.recentTotal > 0 }
        if (assessed.isEmpty()) return null
        val accuracy = assessed.map { it.recentAccuracy }.average().roundToInt()
        return Summary(
            accuracy = accuracy,
            stars = stars(accuracy),
            assessedChapters = assessed.size,
            provisional = assessed.any { it.isProvisional }
        )
    }

    data class Summary(
        val accuracy: Int,
        val stars: Int,
        val assessedChapters: Int,
        val provisional: Boolean
    )
}
