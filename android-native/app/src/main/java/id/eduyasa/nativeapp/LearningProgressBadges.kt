package id.eduyasa.nativeapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Gold = Color(0xFFE0A221)
private val Slate = Color(0xFF718096)
private val Indigo = Color(0xFF2563EB)

@Composable
fun LearningStars(stars: Int, modifier: Modifier = Modifier) {
    val value = stars.coerceIn(0, 5)
    Row(modifier.testTag("learning-stars"), verticalAlignment = Alignment.CenterVertically) {
        Text("★".repeat(value), color = Gold, fontSize = 17.sp, letterSpacing = 1.sp)
        Text("☆".repeat(5 - value), color = Slate, fontSize = 17.sp, letterSpacing = 1.sp)
    }
}

@Composable
fun ChapterLevelAndStars(progress: LearningProgress?, compact: Boolean = false) {
    if (progress == null || progress.total == 0) {
        Text("Belum mulai • Belum dinilai", color = Slate, fontSize = 11.sp)
        return
    }
    Column(Modifier.fillMaxWidth().testTag("chapter-progress-" + progress.subjectId + "-" + progress.chapter)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Level " + progress.level + " • " + progress.total + " soal diselesaikan",
                color = Indigo, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            LearningStars(progress.stars)
        }
        if (!compact) {
            LinearProgressIndicator(
                progress = { progress.let { LearningGamification.levelProgress(it.total) } / 20f },
                modifier = Modifier.fillMaxWidth().height(5.dp).padding(top = 1.dp),
                color = Indigo,
                trackColor = Color(0xFFE5EAF2)
            )
            Text(progress.recentAccuracy.toString() + "% benar (20 jawaban terakhir maks.)" +
                if (progress.isProvisional) " • Sementara" else "",
                color = Slate, fontSize = 11.sp)
        } else {
            Text(progress.recentAccuracy.toString() + "% benar" +
                if (progress.isProvisional) " • Sementara" else "",
                color = Slate, fontSize = 10.sp)
        }
    }
}
