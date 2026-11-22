package id.eduyasa.nativeapp

import androidx.compose.foundation.background
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
        Text(
            (student?.name ?: "Siswa") + " • Riwayat tersimpan di HP ini",
            fontSize = 13.sp, color = Gray
        )
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
                Text("Belum ada riwayat kuis", fontWeight = FontWeight.Bold)
                Text("Selesaikan kuis 20 soal pertama untuk mulai melihat statistik per bab dan mata pelajaran.",
                    color = Gray, fontSize = 13.sp, lineHeight = 20.sp)
            }
            return@Column
        }

        val stats = statistics
        StatsPanel {
            Text("Ringkasan perkembangan", fontWeight = FontWeight.Bold, color = Navy)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Metric("Sesi Kuis", stats.totalSessions.toString(), "stats-sessions")
                Metric("Soal Dijawab", stats.totalQuestions.toString(), "stats-questions")
                Metric("Ketepatan", stats.overallAccuracy.toString() + "%", "stats-accuracy")
            }
        }

        Text("Mata pelajaran", fontWeight = FontWeight.Bold, fontSize = 18.sp,
            color = Navy, modifier = Modifier.padding(top = 14.dp, bottom = 5.dp))
        Text("Urutkan perhatian dari bab yang perlu latihan ulang. Nilai adalah ketepatan jawaban, bukan jaminan penguasaan materi.",
            color = Gray, fontSize = 12.sp, lineHeight = 18.sp)

        stats.subjects.sortedWith(
            compareBy<LearningProgress> {
                when {
                    it.sessions < 2 || it.total < 10 -> 2
                    it.accuracy < 60 -> 0
                    it.accuracy < 80 -> 1
                    else -> 3
                }
            }.thenBy { it.accuracy }
        ).forEach { subject ->
            val info = catalog[subject.grade]?.firstOrNull { it.id == subject.subjectId }
            val name = info?.name ?: subject.subjectId
            StatsPanel {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(info?.icon ?: "📚", fontSize = 23.sp)
                    Text("Kelas " + subject.grade, color = Gray, fontSize = 11.sp)
                }
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy)
                Text(subject.evaluation,
                    fontSize = 12.sp, color = when {
                        subject.sessions < 2 || subject.total < 10 -> Gray
                        subject.accuracy >= 80 -> Color(0xFF15803D)
                        subject.accuracy < 60 -> Color(0xFFB91C1C)
                        else -> Color(0xFFB45309)
                    }, fontWeight = FontWeight.Bold)
                ProgressBar(subject.accuracy)
                Text(subject.accuracy.toString() + "% benar • " +
                    subject.correct + "/" + subject.total + " jawaban • " +
                    subject.sessions + " sesi kuis", fontSize = 12.sp, color = Gray)

                val chapters = stats.chapters.filter {
                    it.grade == subject.grade && it.subjectId == subject.subjectId
                }.sortedBy { it.chapter }
                if (chapters.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 13.dp))
                    Text("Rincian per bab", fontWeight = FontWeight.SemiBold, color = Navy)
                    chapters.forEach { item ->
                        val title = info?.chapters?.getOrNull((item.chapter ?: 1) - 1)
                            ?.removePrefix("Bab " + item.chapter + ": ") ?: ""
                        Text("Bab " + item.chapter + if (title.isNotBlank()) " — " + title else "",
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 11.dp))
                        ProgressBar(item.accuracy)
                        Text(item.accuracy.toString() + "% • " + item.evaluation +
                            " • " + item.total + " soal, " + item.sessions + " sesi",
                            fontSize = 11.sp, color = Gray)
                    }
                }
            }
        }

        Text("10 sesi terakhir", fontWeight = FontWeight.Bold, fontSize = 18.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp), color = Navy)
        stats.recentSessions.forEach { session ->
            StatsPanel {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(formatSessionDate(session.completedAt), fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold)
                        Text("Kelas " + session.grade + " • " + session.correct +
                            " dari " + session.total + " benar", color = Gray, fontSize = 12.sp)
                    }
                    Text(session.score.toString(), color = Blue,
                        fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        StatsPanel {
            Text("Cara membaca statistik", fontWeight = FontWeight.Bold, color = Navy)
            Text(
                "Minimal 10 jawaban dari 2 sesi berbeda diperlukan sebelum memberi indikasi " +
                "mulai dipahami (80%+), sedang berkembang (60–79%), atau perlu latihan lagi " +
                "(di bawah 60%). Data sedikit tetap ditandai sebagai data awal. " +
                "Pengulangan soal bisa memengaruhi persentase; evaluasi ini hanyalah panduan latihan.",
                fontSize = 12.sp, color = Gray, lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun StatsPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
    ) {
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

@Composable
private fun ProgressBar(value: Int) {
    LinearProgressIndicator(
        progress = { (value.coerceIn(0, 100)) / 100f },
        modifier = Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 4.dp)
            .height(7.dp),
        color = Blue, trackColor = Background
    )
}

private fun formatSessionDate(timestamp: Long): String =
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(timestamp))
