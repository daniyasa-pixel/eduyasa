package id.eduyasa.nativeapp

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin

/**
 * Native Android TextView + Markwon: Markdown and real LaTeX drawable spans.
 * This is NOT WebView. Never remove math delimiters before rendering.
 *
 * JSON EDUYASA uses single-dollar inline math whereas Markwon's LaTeX
 * extension expects double-dollar delimiters. Convert display syntax only.
 */
internal fun prepareMathMarkdown(raw: String): String {
    var text = raw.replace(Regex("""(?<!\$)\$([^\r\n$]+)\$(?!\$)"""), { match ->
        "$" + "$" + match.groupValues[1] + "$" + "$"
    })
    text = text.replace(Regex("""\\\((.*?)\\\)"""), { match ->
        "$" + "$" + match.groupValues[1] + "$" + "$"
    })
    text = text.replace(Regex("""\\\[([\s\S]*?)\\]"""), { match ->
        "$" + "$" + "\n" + match.groupValues[1] + "\n" + "$" + "$"
    })
    return text
}

private object EduImages {
    private var loader: ImageLoader? = null

    @Synchronized
    fun get(context: Context): ImageLoader {
        return loader ?: ImageLoader.Builder(context.applicationContext)
            .components { add(SvgDecoder.Factory()) }
            .build().also { loader = it }
    }
}

internal fun resolveEduMedia(source: String?): String? {
    val clean = source?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    if (clean.startsWith("https://", true)) return clean
    if (clean.startsWith("http://", true)) return null // no cleartext content
    if (clean.startsWith("/") || clean.contains("..") || clean.contains("://")) return null
    return "https://raw.githubusercontent.com/daniyasa-pixel/eduyasa/main/" + clean.removePrefix("./")
}

@Composable
fun EduRemoteImage(
    source: String?,
    description: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val url = resolveEduMedia(source)
    val context = LocalContext.current
    var failed by remember(url) { mutableStateOf(false) }
    if (url == null) {
        Text("Gambar tidak memiliki alamat yang valid. " + description,
            color = Color(0xFFB91C1C), modifier = modifier)
        return
    }
    val request = remember(url) { ImageRequest.Builder(context).data(url).crossfade(true).build() }
    Column(modifier) {
        AsyncImage(
            model = request,
            imageLoader = EduImages.get(context),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 340.dp)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
            onError = { failed = true },
            onSuccess = { failed = false }
        )
        if (failed) {
            Text("Gambar gagal dimuat. " + description,
                color = Color(0xFFB91C1C), style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Options without formatting use pure Compose Text. It avoids nesting a native
 * AndroidView inside a clickable answer and removes unnecessary LaTeX work.
 */
@Composable
fun EduAnswerText(
    content: String, modifier: Modifier = Modifier, sizeSp: Float = 14f
) {
    val isRich = remember(content) {
        content.contains('

private class EduFormattedTextView(context: Context) : TextView(context) {
    var lastMarkdown: String? = null
    var lastSizeSp: Float = -1f
    var lastTextColor: Int = 0
}

/** Rich math and Markdown displayed using an Android-native TextView. */
@Composable
fun EduRichText(
    content: String,
    modifier: Modifier = Modifier,
    sizeSp: Float = 15f,
    color: Int = android.graphics.Color.rgb(23, 32, 51)
) {
    val context = LocalContext.current
    val latexSize = remember(sizeSp, context) {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sizeSp, context.resources.displayMetrics
        )
    }
    val markwon = remember(context, latexSize) {
        Markwon.builder(context.applicationContext)
            .usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(latexSize,
                JLatexMathPlugin.BuilderConfigure { builder -> builder.inlinesEnabled(true) }))
            .usePlugin(TablePlugin.create(context.applicationContext))
            .build()
    }
    val formatted = remember(content) { prepareMathMarkdown(content) }
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            EduFormattedTextView(ctx).apply {
                setLineSpacing(5f, 1.1f)
                // Let the parent Compose choice own the complete touch target.
                setTextIsSelectable(false)
                isClickable = false
                isLongClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
            }
        },
        update = { view ->
            // AndroidView.update runs during unrelated Compose recompositions,
            // for example every time the student taps A/B/C/D. Do not reparse.
            if (view.lastMarkdown != formatted || view.lastSizeSp != sizeSp ||
                view.lastTextColor != color) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
                view.setTextColor(color)
                markwon.setMarkdown(view, formatted)
                view.lastMarkdown = formatted
                view.lastSizeSp = sizeSp
                view.lastTextColor = color
            }
        }
    )
}

private val markdownImage = Regex("""^!\[([^\]]*)]\(([^)\s]+)\)\s*$""")

/**
 * Split illustrations into image cards (Coil + SVG) and render all remaining
 * paragraphs using actual Markdown + LaTeX, including strong text and tables.
 */
@Composable
fun EduMaterialContent(
    markdown: String,
    onImageZoom: (String) -> Unit
) {
    Column(
        Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        val blocks = remember(markdown) {
            val pieces = mutableListOf<Pair<Boolean, String>>()
            val textBuffer = StringBuilder()
            fun flush() {
                val content = textBuffer.toString().trim()
                if (content.isNotEmpty()) pieces += false to content
                textBuffer.clear()
            }
            markdown.lineSequence().forEach { line ->
                val image = markdownImage.matchEntire(line.trim())
                if (image != null) {
                    flush()
                    pieces += true to line.trim()
                } else {
                    textBuffer.appendLine(line)
                }
            }
            flush()
            pieces
        }
        blocks.forEach { (isImage, value) ->
            if (isImage) {
                val match = markdownImage.matchEntire(value)
                val alt = match?.groupValues?.getOrNull(1).orEmpty()
                val src = match?.groupValues?.getOrNull(2)
                val resolved = resolveEduMedia(src)
                EduRemoteImage(src, alt,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                        .testTag("lesson-illustration"),
                    onClick = if (resolved == null) null else ({ onImageZoom(resolved) }))
                if (alt.isNotBlank()) Text(alt,
                    color = Color(0xFF475569), style = MaterialTheme.typography.bodySmall)
            } else {
                EduRichText(value, modifier = Modifier.padding(vertical = 5.dp), sizeSp = 15f)
            }
        }
    }
}
) || content.contains("**") || content.contains("__") ||
            content.contains("\\(") || content.contains("\\[") ||
            content.contains('\u0060') ||
            Regex("""(?<!\w)[*_][^*_\r\n]+[*_](?!\w)""").containsMatchIn(content) ||
            Regex("""(?m)^\s*(#{1,6}\s+|[-*]\s+|\d+\.\s+)""").containsMatchIn(content)
    }
    if (isRich) {
        EduRichText(content, modifier = modifier, sizeSp = sizeSp)
    } else {
        Text(content, modifier = modifier, fontSize = sizeSp.sp, color = Color(0xFF172033))
    }
}

private class EduFormattedTextView(context: Context) : TextView(context) {
    var lastMarkdown: String? = null
    var lastSizeSp: Float = -1f
    var lastTextColor: Int = 0
}

/** Rich math and Markdown displayed using an Android-native TextView. */
@Composable
fun EduRichText(
    content: String,
    modifier: Modifier = Modifier,
    sizeSp: Float = 15f,
    color: Int = android.graphics.Color.rgb(23, 32, 51)
) {
    val context = LocalContext.current
    val latexSize = remember(sizeSp, context) {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sizeSp, context.resources.displayMetrics
        )
    }
    val markwon = remember(context, latexSize) {
        Markwon.builder(context.applicationContext)
            .usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(latexSize,
                JLatexMathPlugin.BuilderConfigure { builder -> builder.inlinesEnabled(true) }))
            .usePlugin(TablePlugin.create(context.applicationContext))
            .build()
    }
    val formatted = remember(content) { prepareMathMarkdown(content) }
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            EduFormattedTextView(ctx).apply {
                setLineSpacing(5f, 1.1f)
                // Let the parent Compose choice own the complete touch target.
                setTextIsSelectable(false)
                isClickable = false
                isLongClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
            }
        },
        update = { view ->
            // AndroidView.update runs during unrelated Compose recompositions,
            // for example every time the student taps A/B/C/D. Do not reparse.
            if (view.lastMarkdown != formatted || view.lastSizeSp != sizeSp ||
                view.lastTextColor != color) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
                view.setTextColor(color)
                markwon.setMarkdown(view, formatted)
                view.lastMarkdown = formatted
                view.lastSizeSp = sizeSp
                view.lastTextColor = color
            }
        }
    )
}

private val markdownImage = Regex("""^!\[([^\]]*)]\(([^)\s]+)\)\s*$""")

/**
 * Split illustrations into image cards (Coil + SVG) and render all remaining
 * paragraphs using actual Markdown + LaTeX, including strong text and tables.
 */
@Composable
fun EduMaterialContent(
    markdown: String,
    onImageZoom: (String) -> Unit
) {
    Column(
        Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        val blocks = remember(markdown) {
            val pieces = mutableListOf<Pair<Boolean, String>>()
            val textBuffer = StringBuilder()
            fun flush() {
                val content = textBuffer.toString().trim()
                if (content.isNotEmpty()) pieces += false to content
                textBuffer.clear()
            }
            markdown.lineSequence().forEach { line ->
                val image = markdownImage.matchEntire(line.trim())
                if (image != null) {
                    flush()
                    pieces += true to line.trim()
                } else {
                    textBuffer.appendLine(line)
                }
            }
            flush()
            pieces
        }
        blocks.forEach { (isImage, value) ->
            if (isImage) {
                val match = markdownImage.matchEntire(value)
                val alt = match?.groupValues?.getOrNull(1).orEmpty()
                val src = match?.groupValues?.getOrNull(2)
                val resolved = resolveEduMedia(src)
                EduRemoteImage(src, alt,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                        .testTag("lesson-illustration"),
                    onClick = if (resolved == null) null else ({ onImageZoom(resolved) }))
                if (alt.isNotBlank()) Text(alt,
                    color = Color(0xFF475569), style = MaterialTheme.typography.bodySmall)
            } else {
                EduRichText(value, modifier = Modifier.padding(vertical = 5.dp), sizeSp = 15f)
            }
        }
    }
}
