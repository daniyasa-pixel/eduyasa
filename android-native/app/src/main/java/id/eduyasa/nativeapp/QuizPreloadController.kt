package id.eduyasa.nativeapp

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

/**
 * A small, bounded preparation window for quiz UI.
 * It parses and pre-renders Markdown and LaTeX spans off the main thread,
 * then asks Coil to prefetch an image. Actual Android Views stay on the UI thread.
 * Does not access, edit, or persist the question bank.
 */
data class PreparedQuizQuestion(
    val id: String,
    val prompt: EduPreparedText,
    val answerOptions: Map<String, EduPreparedText>,
    val discussion: EduPreparedText
)

class QuizPreloadController(context: Context) {
    private val appContext = context.applicationContext
    val ready = mutableStateMapOf<Int, PreparedQuizQuestion>()
    val errors = mutableStateMapOf<Int, String>()
    private val jobs = mutableMapOf<Int, Job>()
    // Limit rich-math CPU work so preloading never floods slower phones.
    private val renderPermit = Semaphore(permits = 1)
    private var generation = 0

    fun reset() {
        generation++
        jobs.values.forEach { it.cancel() }
        jobs.clear()
        ready.clear()
        errors.clear()
    }

    fun prepareWindow(scope: CoroutineScope, questions: List<EduQuestion>, current: Int) {
        if (current !in questions.indices) return
        val indexes = (current..minOf(current + 2, questions.lastIndex)).toSet()
        jobs.keys.filter { it !in indexes }.toList().forEach { index ->
            jobs.remove(index)?.cancel()
        }
        ready.keys.filter { it < current || it > current + 2 }.toList()
            .forEach { ready.remove(it) }
        errors.keys.filter { it < current || it > current + 2 }.toList()
            .forEach { errors.remove(it) }

        for (index in indexes) {
            if (index in ready || index in errors || index in jobs) continue
            val token = generation
            val q = questions[index]
            jobs[index] = scope.launch {
                try {
                    val prepared = withTimeout(10_000L) {
                        withContext(Dispatchers.Default) {
                            renderPermit.withPermit {
                                PreparedQuizQuestion(
                                    id = q.id,
                                    prompt = prepareRenderedEduText(appContext, q.text, 17f),
                                    answerOptions = q.options.associateWith { option ->
                                        prepareRenderedEduText(appContext, option, 14f)
                                    },
                                    discussion = prepareRenderedEduText(
                                        appContext, q.discussion, 14f
                                    )
                                )
                            }
                        }
                    }
                    if (generation == token) {
                        ready[index] = prepared
                        // Network/image decoding is handled by Coil asynchronously.
                        // Image failure never blocks moving on to the next question.
                        prefetchEduQuizImage(appContext, q.imageUrl)
                    }
                } catch (timeout: TimeoutCancellationException) {
                    if (generation == token) {
                        errors[index] = "Persiapan terlalu lama. Silakan coba lagi."
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    if (generation == token) {
                        errors[index] = error.message ?: "Gagal menyiapkan tampilan soal."
                    }
                } finally {
                    if (generation == token) jobs.remove(index)
                }
            }
        }
    }

    fun retry(
        scope: CoroutineScope,
        questions: List<EduQuestion>,
        target: Int,
        visibleCurrent: Int = target
    ) {
        // Retrying a future question must not evict the question on screen.
        errors.remove(target)
        jobs.remove(target)?.cancel()
        prepareWindow(scope, questions, visibleCurrent)
    }
}
