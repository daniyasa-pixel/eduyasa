package id.eduyasa.nativeapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** These are the approved encouragement messages from the original EDUYASA HTML. */
internal object EduEncouragement {
    private val success = listOf(
        "Good Job! 🎉", "Nice Job! 🌟", "Keren! 😎", "Mantap! 👍", "Hebat! 🚀"
    )
    private val retry = listOf(
        "Tetap Semangat! 💪", "Ayo Coba Lagi! 🧐",
        "Belajar dari Kesalahan! 🌱", "Jangan Menyerah! ✊"
    )

    fun choose(correct: Boolean, previous: String): String {
        val messages = (if (correct) success else retry).filter { it != previous }
        return messages.random()
    }
}

/**
 * Non-modal in-app toast. It does not interfere with the selection overlay
 * because it occupies only a small area near the bottom of the screen.
 */
@Composable
fun EduQuizFeedbackToast(
    message: String,
    correct: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message.isNotBlank(),
        enter = fadeIn(tween(180)) + slideInVertically(tween(190)) { it / 2 },
        exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it / 2 },
        modifier = modifier.testTag("quiz-feedback-toast")
    ) {
        Surface(
            color = if (correct) Color(0xFFECFDF5) else Color(0xFFFFF7ED),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp,
                if (correct) Color(0xFF22C55E) else Color(0xFFF59E0B)),
            shadowElevation = 6.dp
        ) {
            Text(
                message,
                color = Color(0xFF1E293B),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
        }
    }
}
