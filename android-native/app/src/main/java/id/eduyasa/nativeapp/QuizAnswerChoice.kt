package id.eduyasa.nativeapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Isolated, Material-native touch target. Text preparation is passed in rather
 * than reparsed on every answer selection.
 */
@Composable
fun QuizAnswerChoice(
    questionId: String,
    index: Int,
    text: String,
    prepared: EduPreparedText?,
    selected: Boolean,
    correct: Boolean,
    wrong: Boolean,
    checked: Boolean,
    onSelect: () -> Unit
) {
    val blue = Color(0xFF2563EB)
    val green = Color(0xFF16A34A)
    val red = Color(0xFFDC2626)
    val line = Color(0xFFE5EAF2)
    val background = when {
        correct -> Color(0xFFDCFCE7)
        wrong -> Color(0xFFFEE2E2)
        selected -> Color(0xFFEFF6FF)
        else -> Color.White
    }
    val border = when {
        correct -> green
        wrong -> red
        selected -> blue
        else -> line
    }
    val touchSource = remember(questionId, index) { MutableInteractionSource() }
    val pressed by touchSource.collectIsPressedAsState()
    val animatedBackground by animateColorAsState(
        targetValue = background,
        animationSpec = tween(if (checked) 210 else 100),
        label = "quiz-option-background"
    )
    val animatedBorder by animateColorAsState(
        targetValue = border,
        animationSpec = tween(160),
        label = "quiz-option-border"
    )
    val scale by animateFloatAsState(
        targetValue = when {
            pressed && !checked -> .975f
            correct -> 1.012f
            wrong -> .988f
            else -> 1f
        },
        animationSpec = tween(if (pressed) 70 else 150),
        label = "quiz-option-press"
    )
    Surface(
        onClick = onSelect,
        enabled = !checked,
        interactionSource = touchSource,
        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .testTag("option-" + "ABCD"[index]),
        shape = RoundedCornerShape(12.dp),
        color = animatedBackground,
        border = BorderStroke(2.dp, animatedBorder)
    ) {
        Row(Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("ABCD"[index].toString() + ". ",
                fontWeight = FontWeight.Bold, fontSize = 14.sp)
            EduAnswerText(text, modifier = Modifier.weight(1f), prepared = prepared)
            if (correct) Text(" ✓", fontWeight = FontWeight.Bold,
                color = green, fontSize = 19.sp)
            else if (wrong) Text(" ×", fontWeight = FontWeight.Bold,
                color = red, fontSize = 19.sp)
        }
    }
}
