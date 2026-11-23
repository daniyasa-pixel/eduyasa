package id.eduyasa.nativeapp

import android.content.Context
import android.os.Trace
import android.text.Spanned
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
import androidx.compose.ui.unit.Dp
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
import kotlin.math.roundToInt

/**
 * Native Android TextView + Markwon: Markdown and real LaTeX drawable spans.
 * This is NOT WebView. Never remove math delimiters before rendering.
 *
 * JSON EDUYASA uses single-dollar inline math whereas Markwon's LaTeX
 * extension expects double-dollar delimiters. Convert display syntax only.
 */
private val inlineMathDollars = Regex("""(?<!\$)\$([^\r\n$]+)\$(?!\$)""")
private val inlineMathParentheses = Regex("""\\\((.*?)\\\)""")
private val blockMathBrackets = Regex("""\\\[([\s\S]*?)\\]""")

internal fun prepareMathMarkdown(raw: String): String {
    var text = raw.replace(inlineMathDollars, { match ->
        "$" + "$" + match.groupValues[1] + "$" + "$"
    })
    text = text.replace(inlineMathParentheses, { match ->
        "$" + "$" + match.groupValues[1] + "$" + "$"
    })
    text = text.replace(blockMathBrackets, { match ->
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

/**
 * Best-effort image warmup into Coil's existing memory/disk cache.
 * It deliberately never blocks question readiness.
 */
internal fun prefetchEduQuizImage(context: Context, source: String?) {
    val url = resolveEduMedia(source) ?: return
    runCatching {
        val request = ImageRequest.Builder(context.applicationContext).data(url).build()
        EduImages.get(context).enqueue(request)
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
    onClick: (() -> Unit)? = null,
    fixedHeight: Dp? = null
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
            modifier = (if (fixedHeight == null)
                Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 340.dp)
                else Modifier.fillMaxWidth().height(fixedHeight))
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
 * Plain quiz options use Compose Text; formatted text keeps Android-native
 * Markwon and LaTeX rendering. No HTML/WebView is involved.
 */
private val inlineEmphasis = Regex("""(?<!\w)[*_][^*_\r\n]+[*_](?!\w)""")
private val markdownBlock = Regex("""(?m)^\s*(#{1,6}\s+|[-*]\s+|\d+\.\s+)""")

data class EduPreparedText(
    val raw: String,
    val formatted: String,
    val rich: Boolean,
    val rendered: Spanned? = null
)

/** Pure string preparation safe on Dispatchers.Default (no View creation). */
fun prepareEduText(content: String): EduPreparedText {
    val rich = content.contains('$') || content.contains("**") ||
        content.contains("__") || content.contains("\\(") ||
        content.contains("\\[") || content.contains('\u0060') ||
        inlineEmphasis.containsMatchIn(content) ||
        markdownBlock.containsMatchIn(content)
    return EduPreparedText(
        raw = content,
        formatted = if (rich) prepareMathMarkdown(content) else content,
        rich = rich
    )
}

/**
 * The costly Markwon Markdown+LaTeX parse/render phase runs in the quiz
 * preloader on Dispatchers.Default. Views are still created on the UI thread.
 * Only the current and next two questions keep their resulting Spanned objects.
 */
fun prepareRenderedEduText(
    context: Context,
    content: String,
    sizeSp: Float
): EduPreparedText {
    val basic = prepareEduText(content)
    if (!basic.rich) return basic
    val px = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, sizeSp, context.resources.displayMetrics
    )
    Trace.beginSection("EDUYASA.PreRenderRichText")
    return try {
        basic.copy(
            rendered = EduMarkwonPool.renderer(context, px).toMarkdown(basic.formatted)
        )
    } finally {
        Trace.endSection()
    }
}

@Composable
fun EduAnswerText(
    content: String,
    modifier: Modifier = Modifier,
    sizeSp: Float = 14f,
    prepared: EduPreparedText? = null
) {
    val value = remember(content, prepared) {
        prepared?.takeIf { it.raw == content } ?: prepareEduText(content)
    }
    if (value.rich) {
        EduRichText(
            content, modifier = modifier, sizeSp = sizeSp,
            preparedMarkdown = value.formatted,
            preparedSpanned = value.rendered
        )
    } else {
        Text(content, modifier = modifier, fontSize = sizeSp.sp,
            color = Color(0xFF172033))
    }
}

private class EduFormattedTextView(context: Context) : TextView(context) {
    var lastMarkdown: String? = null
    var lastSizeSp: Float = -1f
    var lastTextColor: Int = 0
    var lastSpanned: Spanned? = null
}

/**
 * Building Markwon and its LaTeX plugin for every option is expensive.
 * Reuse at most a few immutable renderers per font size in this process.
 */
private object EduMarkwonPool {
    private val instances = mutableMapOf<Int, Markwon>()

    @Synchronized
    fun renderer(context: Context, latexSize: Float): Markwon {
        val key = latexSize.roundToInt()
        return instances.getOrPut(key) {
            Markwon.builder(context.applicationContext)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(latexSize,
                    JLatexMathPlugin.BuilderConfigure { builder ->
                        builder.inlinesEnabled(true)
                    }))
                .usePlugin(TablePlugin.create(context.applicationContext))
                .build()
        }
    }
}

/** Markdown and LaTeX rendered by native Android TextView. */
@Composable
fun EduRichText(
    content: String,
    modifier: Modifier = Modifier,
    sizeSp: Float = 15f,
    color: Int = android.graphics.Color.rgb(23, 32, 51),
    preparedMarkdown: String? = null,
    preparedSpanned: Spanned? = null
) {
    val context = LocalContext.current
    val latexSize = remember(sizeSp, context) {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sizeSp, context.resources.displayMetrics
        )
    }
    val markwon = remember(context, latexSize) {
        EduMarkwonPool.renderer(context, latexSize)
    }
    val formatted = remember(content, preparedMarkdown) {
        preparedMarkdown ?: prepareMathMarkdown(content)
    }
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            EduFormattedTextView(ctx).apply {
                setLineSpacing(5f, 1.1f)
                setTextIsSelectable(false)
                isClickable = false
                isLongClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
            }
        },
        update = { view ->
            // Selecting another answer must not reparse unchanged Markdown.
            if (view.lastMarkdown != formatted || view.lastSizeSp != sizeSp ||
                view.lastTextColor != color || view.lastSpanned !== preparedSpanned) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp)
                view.setTextColor(color)
                Trace.beginSection("EDUYASA.RenderRichText")
                try {
                    if (preparedSpanned != null)
                        markwon.setParsedMarkdown(view, preparedSpanned)
                    else markwon.setMarkdown(view, formatted)
                } finally {
                    Trace.endSection()
                }
                view.lastMarkdown = formatted
                view.lastSpanned = preparedSpanned
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
