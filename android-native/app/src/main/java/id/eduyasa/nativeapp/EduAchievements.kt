package id.eduyasa.nativeapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Penghargaan dihitung dari data yang benar-benar sudah tersimpan di SQLite.
 * Bintang boleh turun, tetapi yang dirayakan hanya peningkatan.
 */
data class ChapterMilestone(
    val grade: Int,
    val subjectId: String,
    val chapter: Int,
    val oldLevel: Int,
    val newLevel: Int,
    val oldStars: Int,
    val newStars: Int
) {
    val levelUp: Boolean get() = newLevel > oldLevel
    val starUp: Boolean get() = newStars > oldStars
}

fun findNewMilestones(before: LearningStatistics, after: LearningStatistics): List<ChapterMilestone> =
    after.chapters.mapNotNull { current ->
        val old = before.chapters.firstOrNull {
            it.grade == current.grade && it.subjectId == current.subjectId &&
                it.chapter == current.chapter
        }
        val result = ChapterMilestone(
            grade = current.grade,
            subjectId = current.subjectId,
            chapter = current.chapter ?: return@mapNotNull null,
            oldLevel = old?.level ?: 0,
            newLevel = current.level,
            oldStars = old?.stars ?: 0,
            newStars = current.stars
        )
        result.takeIf { it.levelUp || it.starUp }
    }

@Composable
fun AchievementCelebration(
    achievements: List<ChapterMilestone>,
    catalog: Map<Int, List<EduSubject>>
) {
    if (achievements.isEmpty()) return
    var visible by remember(achievements) { mutableStateOf(false) }
    LaunchedEffect(achievements) { visible = true }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)) +
            scaleIn(initialScale = .92f, animationSpec = tween(320)) +
            slideInVertically(initialOffsetY = { it / 5 }, animationSpec = tween(320))
    ) {
        Column(
            Modifier.fillMaxWidth()
                .background(Color(0xFFFFF8E6), RoundedCornerShape(18.dp))
                .padding(16.dp)
                .testTag("achievement-celebration"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✨ PENCAPAIAN BARU! ✨", fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFF92400E),
                textAlign = TextAlign.Center)
            achievements.take(6).forEach { milestone ->
                val subject = catalog[milestone.grade]
                    ?.firstOrNull { it.id == milestone.subjectId }
                val chapterTitle = subject?.chapters?.getOrNull(milestone.chapter - 1).orEmpty()
                Spacer(Modifier.height(11.dp))
                Text((subject?.name ?: milestone.subjectId) + " • Bab " + milestone.chapter,
                    fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), fontSize = 13.sp)
                if (chapterTitle.isNotBlank()) {
                    Text(chapterTitle, fontSize = 11.sp, color = Color(0xFF718096),
                        textAlign = TextAlign.Center)
                }
                if (milestone.levelUp) {
                    Text("🏆 LEVEL " + milestone.newLevel + "!",
                        fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2563EB), modifier = Modifier.testTag("level-up"))
                }
                if (milestone.starUp) {
                    Text("Bintang meningkat: " + milestone.oldStars + " → " +
                        milestone.newStars, fontSize = 12.sp,
                        color = Color(0xFF92400E), modifier = Modifier.testTag("star-up"))
                }
                RewardStars(milestone.newStars)
            }
            if (achievements.size > 6) Text(
                "+ " + (achievements.size - 6) + " bab mendapatkan pencapaian.",
                fontSize = 11.sp, color = Color(0xFF718096))
        }
    }
}

@Composable
private fun RewardStars(total: Int) {
    var revealed by remember(total) { mutableIntStateOf(0) }
    LaunchedEffect(total) {
        revealed = 0
        repeat(total.coerceIn(0, 5)) {
            delay(90)
            revealed++
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(5) { index ->
            val earned = index < revealed
            Text(if (earned) "★" else "☆", fontSize = 26.sp,
                color = if (earned) Color(0xFFE0A221) else Color(0xFF9CA3AF))
        }
    }
}
