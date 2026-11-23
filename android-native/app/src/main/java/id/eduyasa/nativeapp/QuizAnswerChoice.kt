package id.eduyasa.nativeapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Entire option area receives taps from a TRANSPARENT Compose overlay which is
 * placed AFTER the content. Android TextViews for LaTeX never receive taps.
 *
 * Selecting A/B/C/D only updates the outer border; the white background and
 * already-prepared math text never change. Data selection remains in the parent
 * onSelect callback (the same source of truth used by scoring/history).
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
    val shape = RoundedCornerShape(12.dp)
    val borderColor = when {
        correct -> Color(0xFF16A34A)
        wrong -> Color(0xFFDC2626)
        selected -> Color(0xFF2563EB)
        else -> Color(0xFFE5EAF2)
    }
    val touchSource = remember(questionId, index) { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(shape)
            .background(Color.White)
            .border(BorderStroke(2.dp, borderColor), shape)
            .testTag("option-" + "ABCD"[index])
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // This composable has no dependency on selected/checked state.
            QuizAnswerContent(
                questionId = questionId,
                index = index,
                text = text,
                prepared = prepared,
                modifier = Modifier.weight(1f)
            )
            // Correction feedback is shown only AFTER the choice is locked.
            if (correct) {
                Text(" ✓", fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A), fontSize = 19.sp)
            } else if (wrong) {
                Text(" ×", fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626), fontSize = 19.sp)
            }
        }

        // Topmost element captures every tap; no ripple, background tint or
        // scaling, only the border changes when the parent records a selection.
        Box(
            modifier = Modifier
                .matchParentSize()
                .semantics {
                    role = Role.RadioButton
                    this.selected = selected
                    contentDescription = "${"ABCD"[index]}. ${text}"
                }
                .clickable(
                    enabled = !checked,
                    interactionSource = touchSource,
                    indication = null,
                    onClick = onSelect
                )
        )
    }
}

@Composable
private fun QuizAnswerContent(
    questionId: String,
    index: Int,
    text: String,
    prepared: EduPreparedText?,
    modifier: Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text("ABCD"[index].toString() + ". ",
            fontWeight = FontWeight.Bold, fontSize = 14.sp)
        EduAnswerText(text, modifier = Modifier.weight(1f), prepared = prepared)
    }
}
