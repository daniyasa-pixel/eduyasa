package id.eduyasa.nativeapp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val EvalNavy = Color(0xFF1E3A8A)
private val EvalBlue = Color(0xFF2563EB)
private val EvalGray = Color(0xFF718096)
private val EvalLine = Color(0xFFE5EAF2)

/**
 * Student-specific evaluation navigator.
 * The overview always shows every subject as a single vertical list,
 * including subjects with no attempts. Detail is scoped to one subject.
 *
 * Accuracy = all correct answers / all attempted answers for that subject.
 * Unattempted chapters are never included in that denominator.
 * Chapter level and stars keep their established independent calculations.
 */
@Composable
fun LearningStatisticsScreen(
    student: StudentProfile?,
    statistics: LearningStatistics?,
    catalog: Map<Int, List<EduSubject>>,
    loading: Boolean,
    error: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    var openedSubjectId by remember(student?.id, student?.grade) {
        mutableStateOf<String?>(null)
    }
    BackHandler(enabled = openedSubjectId != null) { openedSubjectId = null }
    val grade = student?.grade ?: 0
    val subjects = catalog[grade].orEmpty()
    val selectedSubject = subjects.firstOrNull { it.id == openedSubjectId }
    val stats = statistics
    val studentName = student?.name ?: "Siswa"

    Column(Modifier.fillMaxWidth().testTag("learning-statistics")) {
        if (selectedSubject == null) {
            Text("📊 Evaluasi Belajar", fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold, color = EvalNavy)
            Text("$studentName • Kelas $grade SD", fontSize = 13.sp, color = EvalGray)
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.testTag("stats-back")) {
                Text("← Kembali")
            }
        } else {
            Text("📊 Evaluasi ${selectedSubject.name}", fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold, color = EvalNavy)
            Text("$studentName • Kelas $grade SD", fontSize = 13.sp, color = EvalGray)
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { openedSubjectId = null },
                modifier = Modifier.testTag("stats-subject-back")
            ) { Text("← Semua mata pelajaran") }
        }

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text("Sedang membaca hasil latihan...", fontSize = 13.sp, color = EvalGray)
            return@Column
        }
        if (error.isNotBlank()) {
            EvalPanel {
                Text("Hasil latihan belum bisa dibuka.", color = EvalNavy)
                Text(error, fontSize = 12.sp, color = EvalGray)
                Button(onClick = onRetry, modifier = Modifier.testTag("stats-retry")) {
                    Text("Coba Lagi")
                }
            }
            return@Column
        }

        if (selectedSubject != null) {
            val summary = stats?.subjects.orEmpty().firstOrNull {
                it.grade == grade && it.subjectId == selectedSubject.id
            }
            val chapters = stats?.chapters.orEmpty().filter {
                it.grade == grade && it.subjectId == selectedSubject.id
            }.associateBy { it.chapter }
            EvalSubjectDetail(selectedSubject, grade, summary, chapters)
            return@Column
        }

        if (stats != null && stats.totalQuestions > 0) {
            EvalPanel {
                Text("Ringkasan latihanmu", color = EvalNavy,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    EvalMetric("Sesi kuis", stats.totalSessions.toString(), "stats-sessions")
                    EvalMetric("Soal dijawab", stats.totalQuestions.toString(), "stats-questions")
                    EvalMetric("Jawaban benar", stats.overallAccuracy.toString() + "%",
                        "stats-accuracy")
                }
            }
        } else {
            EvalPanel {
                Text("Ayo mulai petualangan belajarmu! 🌟",
                    fontWeight = FontWeight.Bold, color = EvalNavy)
                Text("Selesaikan kuis pertamamu. Nanti hasil latihanmu muncul di sini.",
                    color = EvalGray, fontSize = 13.sp)
            }
        }

        Text("Pilih mata pelajaran", fontWeight = FontWeight.Bold, fontSize = 18.sp,
            color = EvalNavy, modifier = Modifier.padding(top = 18.dp, bottom = 5.dp))
        Text("Persen menunjukkan jawaban benar dari soal yang sudah kamu kerjakan.",
            color = EvalGray, fontSize = 12.sp, lineHeight = 18.sp)
        subjects.forEach { subject ->
            val progress = stats?.subjects.orEmpty().firstOrNull {
                it.grade == grade && it.subjectId == subject.id
            }
            Surface(
                onClick = { openedSubjectId = subject.id },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    .testTag("stats-subject-" + subject.id),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EvalLine)
            ) {
                Column(Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(subject.icon, fontSize = 25.sp)
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text(subject.name, color = EvalNavy,
                                fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            if (progress == null || progress.total == 0) {
                                Text("Yuk, coba latihan pertamamu!",
                                    color = EvalGray, fontSize = 12.sp)
                            } else {
                                Text("${progress.correct} dari ${progress.total} jawaban benar",
                                    color = EvalGray, fontSize = 12.sp)
                            }
                        }
                        if (progress != null && progress.total > 0) {
                            Text("${progress.accuracy}%", fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EvalBlue, modifier = Modifier.testTag(
                                    "stats-subject-percent-" + subject.id))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("›", fontSize = 25.sp, color = EvalGray)
                    }
                    if (progress != null && progress.total > 0) {
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress.accuracy / 100f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = EvalBlue, trackColor = EvalLine
                        )
                    }
                }
            }
        }

        if (stats != null && stats.recentSessions.isNotEmpty()) {
            Text("Riwayat 10 latihan terakhir", fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = EvalNavy,
                modifier = Modifier.padding(top = 20.dp))
            stats.recentSessions.forEach { session ->
                EvalPanel {
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(formatEvalDate(session.completedAt),
                                fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("${session.correct} dari ${session.total} benar",
                                color = EvalGray, fontSize = 12.sp)
                        }
                        Text("${session.score}%", color = EvalBlue, fontSize = 21.sp,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EvalSubjectDetail(
    subject: EduSubject,
    grade: Int,
    summary: LearningProgress?,
    chapters: Map<Int?, LearningProgress>
) {
    EvalPanel {
        Text("Ketepatan Jawaban", color = EvalNavy, fontWeight = FontWeight.Bold)
        if (summary == null || summary.total == 0) {
            Text("Yuk, coba latihan pertamamu!", fontSize = 15.sp, color = EvalBlue,
                modifier = Modifier.padding(top = 8.dp))
            Text("Pilih bab yang kamu suka, lalu mulai latihan.",
                color = EvalGray, fontSize = 12.sp)
        } else {
            Text("${summary.accuracy}%", color = EvalBlue, fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.testTag("stats-detail-accuracy"))
            Text("${summary.correct} dari ${summary.total} jawaban benar",
                color = EvalGray, fontSize = 13.sp)
            LinearProgressIndicator(
                progress = { summary.accuracy / 100f },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(8.dp),
                color = EvalBlue, trackColor = EvalLine
            )
            Text("Dihitung dari soal ${subject.name} yang sudah dijawab, " +
                "bukan dari semua bab yang tersedia.",
                color = EvalGray, fontSize = 11.sp, lineHeight = 17.sp,
                modifier = Modifier.padding(top = 9.dp))
        }
    }

    Text("Perkembangan setiap bab", color = EvalNavy,
        fontWeight = FontWeight.Bold, fontSize = 18.sp,
        modifier = Modifier.padding(top = 18.dp, bottom = 3.dp))
    Text("Level = jumlah soal yang dikerjakan. " +
        "Bintang = ketepatan maksimal 20 jawaban terakhir.",
        color = EvalGray, fontSize = 12.sp, lineHeight = 18.sp)

    subject.chapters.forEachIndexed { index, title ->
        val number = index + 1
        val progress = chapters[number]
        EvalPanel {
            Text("Bab $number — $title", color = EvalNavy,
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            if (progress == null || progress.total == 0) {
                Text("Bab ini belum dicoba. Ayo mulai belajar!",
                    color = EvalGray, fontSize = 12.sp)
            } else {
                ChapterLevelAndStars(progress)
                Text("${progress.correct} dari ${progress.total} benar " +
                    "(${progress.accuracy}% dari semua latihan bab ini)",
                    color = EvalGray, fontSize = 12.sp,
                    modifier = Modifier.padding(top = 7.dp))
                Text(evaluationSuggestion(progress), color = EvalGray,
                    fontSize = 11.sp, lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 5.dp))
            }
        }
    }
}

private fun evaluationSuggestion(item: LearningProgress): String {
    val note = when (item.recentAccuracy) {
        in 0..39 -> "Ayo baca lagi materinya dan coba latihan pelan-pelan."
        in 40..59 -> "Terus berlatih supaya makin paham, ya!"
        in 60..74 -> "Bagus, kamu semakin berkembang!"
        in 75..89 -> "Hebat! Pertahankan dan coba soal baru."
        else -> "Keren sekali! Terus asah kemampuanmu."
    }
    return if (item.isProvisional) "Hasil latihan awal. $note" else note
}

@Composable
private fun EvalPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        shape = RoundedCornerShape(16.dp), color = Color.White
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), content = content)
    }
}

@Composable
private fun EvalMetric(label: String, value: String, tag: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold,
            color = EvalBlue, fontSize = 20.sp,
            modifier = Modifier.testTag(tag))
        Text(label, fontSize = 10.sp, color = EvalGray,
            textAlign = TextAlign.Center)
    }
}

private fun formatEvalDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(timestamp))
