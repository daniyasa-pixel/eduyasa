package id.eduyasa.nativeapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

private val Navy = Color(0xFF1E3A8A)
private val Blue = Color(0xFF2563EB)
private val Gray = Color(0xFF718096)
private val Background = Color(0xFFF4F7FB)

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
    Column(Modifier.fillMaxWidth().testTag("learning-statistics")) {
        Text("📊 Evaluasi Belajar", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
        Text((student?.name ?: "Siswa") + " • Riwayat tersimpan di HP ini",
            fontSize = 13.sp, color = Gray)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.testTag("stats-back")) {
            Text("← Kembali")
        }
        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text("Membaca hasil latihan...", color = Gray, fontSize = 13.sp)
            return@Column
        }
        if (error.isNotBlank()) {
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(10.dp))
            Button(onClick = onRetry, modifier = Modifier.testTag("stats-retry")) {
                Text("Coba Lagi")
            }
            return@Column
        }
        if (statistics == null || statistics.totalSessions == 0) {
            StatsPanel {
                Text("Belum ada riwayat latihan", fontWeight = FontWeight.Bold)
                Text("Selesaikan kuis 15, 20, atau 25 soal untuk mulai mendapatkan Level, Bintang, dan analisis.",
                    color = Gray, fontSize = 13.sp, lineHeight = 20.sp)
            }
            return@Column
        }

        val stats = statistics
        val grade = student?.grade
        val currentChapters = stats.chapters.filter { grade == null || it.grade == grade }
        val currentSubjects = stats.subjects.filter { grade == null || it.grade == grade }

        StatsPanel {
            Text("Ringkasan perkembangan", fontWeight = FontWeight.Bold, color = Navy)
            Spacer(Modifier.height(9.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Metric("Sesi Kuis", stats.totalSessions.toString(), "stats-sessions")
                Metric("Soal Dijawab", stats.totalQuestions.toString(), "stats-questions")
                Metric("Ketepatan Total", stats.overallAccuracy.toString() + "%", "stats-accuracy")
            }
            Spacer(Modifier.height(12.dp))
            Text("Level menunjukkan banyaknya soal yang sudah diselesaikan pada tiap bab. " +
                "Bintang menunjukkan ketepatan maksimal 20 jawaban terbaru per bab dan bisa berubah.",
                fontSize = 12.sp, color = Gray, lineHeight = 19.sp)
        }

        if (currentChapters.isNotEmpty()) {
            val weakest = currentChapters.minByOrNull { it.recentAccuracy }
            val strongest = currentChapters.maxByOrNull { it.recentAccuracy }
            StatsPanel {
                Text("💡 Analisis dan Saran Belajar", fontWeight = FontWeight.Bold, color = Navy)
                Spacer(Modifier.height(6.dp))
                if (weakest != null) {
                    val subject = catalog[weakest.grade]?.firstOrNull { it.id == weakest.subjectId }
                    val name = subject?.name ?: weakest.subjectId
                    val title = subject?.chapters?.getOrNull((weakest.chapter ?: 1) - 1).orEmpty()
                    Text("Prioritas: " + name + " • Bab " + weakest.chapter +
                        if (title.isNotBlank()) " — " + title else "",
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Navy)
                    Text(recommendation(weakest), fontSize = 12.sp, color = Gray,
                        lineHeight = 19.sp, modifier = Modifier.padding(top = 4.dp))
                }
                if (strongest != null && strongest != weakest && strongest.recentAccuracy >= 75) {
                    val info = catalog[strongest.grade]?.firstOrNull { it.id == strongest.subjectId }
                    Text("Kekuatan: " + (info?.name ?: strongest.subjectId) +
                        " Bab " + strongest.chapter + " (" + strongest.recentAccuracy +
                        "% ketepatan terbaru).", color = Color(0xFF15803D),
                        fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }

        Text("Mata pelajaran dan bab", fontWeight = FontWeight.Bold, fontSize = 18.sp,
            color = Navy, modifier = Modifier.padding(top = 15.dp, bottom = 5.dp))
        Text("Bintang mata pelajaran dan semester memakai rata-rata persentase dari bab yang sudah dikerjakan. " +
            "Bab belum dikerjakan tidak dihitung sebagai nol.",
            fontSize = 12.sp, color = Gray, lineHeight = 18.sp)
        currentSubjects.forEach { subject ->
            val info = catalog[subject.grade]?.firstOrNull { it.id == subject.subjectId }
            val allChapters = currentChapters.filter {
                it.subjectId == subject.subjectId && it.grade == subject.grade
            }.sortedBy { it.chapter }
            val subjectRating = LearningGamification.summary(allChapters)
            StatsPanel {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text((info?.icon ?: "📚") + " " + (info?.name ?: subject.subjectId),
                        fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Navy,
                        modifier = Modifier.weight(1f))
                    Text("Kelas " + subject.grade, color = Gray, fontSize = 11.sp)
                }
                if (subjectRating != null) {
                    Spacer(Modifier.height(7.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        LearningStars(subjectRating.stars,
                            Modifier.testTag("subject-stars-" + subject.subjectId))
                        Text(subjectRating.accuracy.toString() + "%", color = Blue,
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(subjectRating.assessedChapters.toString() + "/" +
                        (info?.chapters?.size ?: subjectRating.assessedChapters) +
                        " bab dinilai" +
                        if (subjectRating.provisional ||
                            subjectRating.assessedChapters < (info?.chapters?.size ?: 0))
                            " • Sementara" else "",
                        color = Gray, fontSize = 11.sp)
                }
                val semester = SemesterPresentation.forSubject(
                    subject.grade, subject.subjectId, info?.chapters?.size ?: 0
                )
                if (semester != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    (1..2).forEach { number ->
                        val group = allChapters.filter {
                            it.chapter != null && semester.semesterFor(it.chapter) == number
                        }
                        val rating = LearningGamification.summary(group)
                        val available = if (number == 1) semester.lastChapterFirstSemester
                            else (info?.chapters?.size ?: 0) - semester.lastChapterFirstSemester
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Semester " + number, fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold, color = Navy)
                            if (rating == null) Text("Belum dinilai", fontSize = 11.sp, color = Gray)
                            else LearningStars(rating.stars)
                        }
                        Text(if (rating == null) "0/" + available + " bab"
                            else rating.accuracy.toString() + "% • " +
                                rating.assessedChapters + "/" + available + " bab" +
                                if (rating.provisional || rating.assessedChapters < available ||
                                    semester.evidence == SemesterEvidence.PROVISIONAL)
                                    " • Sementara" else "",
                            fontSize = 11.sp, color = Gray, modifier = Modifier.padding(bottom = 7.dp))
                    }
                    if (semester.evidence == SemesterEvidence.PROVISIONAL) {
                        Text("Pembagian semester masih menunggu pencocokan LKS.",
                            color = Gray, fontSize = 11.sp)
                    }
                } else {
                    SemesterPresentation.pendingReason(subject.grade, subject.subjectId)?.let {
                        Text(it, color = Gray, fontSize = 11.sp,
                            modifier = Modifier.padding(top = 9.dp))
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 10.dp, bottom = 7.dp))
                Text("Rincian per bab", fontWeight = FontWeight.SemiBold, color = Navy)
                allChapters.forEach { item ->
                    val title = info?.chapters?.getOrNull((item.chapter ?: 1) - 1).orEmpty()
                    Text("Bab " + item.chapter + if (title.isNotBlank()) " — " + title else "",
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 11.dp, bottom = 4.dp))
                    ChapterLevelAndStars(item)
                    Text(recommendation(item), fontSize = 11.sp, color = Gray,
                        lineHeight = 17.sp, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }

        Text("Riwayat Latihan • 10 Terakhir", fontWeight = FontWeight.Bold, fontSize = 18.sp,
            modifier = Modifier.padding(top = 17.dp, bottom = 5.dp), color = Navy)
        stats.recentSessions.forEach { session ->
            StatsPanel {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(formatSessionDate(session.completedAt), fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold)
                        Text("Kelas " + session.grade + " • " + session.correct +
                            "/" + session.total + " benar", color = Gray, fontSize = 12.sp)
                        val topics = session.topics.take(4).joinToString(" • ") { topic ->
                            val subjectName = catalog[session.grade]
                                ?.firstOrNull { it.id == topic.subjectId }?.name ?: topic.subjectId
                            subjectName + " B" + topic.chapter
                        }
                        if (topics.isNotBlank()) Text(topics,
                            color = Gray, fontSize = 11.sp, lineHeight = 17.sp)
                    }
                    Text(session.score.toString(), color = Blue,
                        fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        StatsPanel {
            Text("Cara membaca bintang", fontWeight = FontWeight.Bold, color = Navy)
            Text("0–19%: 0 bintang • 20–39%: 1 • 40–59%: 2 • " +
                "60–74%: 3 • 75–89%: 4 • 90–100%: 5. " +
                "Bintang dapat muncul sejak latihan pertama. " +
                "Jika data masih sedikit atau baru satu sesi, hasil ditandai Sementara. " +
                "Analisis ini adalah panduan latihan, bukan diagnosis kemampuan siswa.",
                fontSize = 12.sp, color = Gray, lineHeight = 19.sp)
        }
    }
}

private fun recommendation(item: LearningProgress): String {
    val note = when (item.recentAccuracy) {
        in 0..39 -> "Coba baca ulang materi dan kerjakan soal secara perlahan."
        in 40..59 -> "Perkuat pemahaman bab ini lewat latihan ulang."
        in 60..74 -> "Kemampuan mulai berkembang, terus berlatih."
        in 75..89 -> "Hasil sudah baik. Pertahankan dengan latihan berkala."
        else -> "Hasil sangat baik. Coba soal acak berikutnya agar semakin mantap."
    }
    return if (item.isProvisional) "Penilaian sementara. " + note else note
}

@Composable
private fun StatsPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White,
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), content = content)
    }
}

@Composable
private fun Metric(label: String, value: String, tag: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, color = Blue, fontSize = 22.sp,
            modifier = Modifier.testTag(tag))
        Text(label, fontSize = 10.sp, color = Gray, textAlign = TextAlign.Center)
    }
}

private fun formatSessionDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(timestamp))
