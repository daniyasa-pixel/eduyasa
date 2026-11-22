package id.eduyasa.nativeapp

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Lima efek suara sintetis orisinal tanpa aset eksternal/izin internet.
 * Pemutarannya tidak memblokir UI dan mengikuti volume perangkat.
 * Seluruh not pendek, tidak ada musik latar ataupun lagu komersial.
 */
enum class EduSfx(val displayName: String, val description: String) {
    CORRECT("Jawaban benar", "Ding ceria"),
    WRONG("Jawaban belum tepat", "Tok lembut"),
    STAR("Bintang meningkat", "Kilau bintang"),
    LEVEL("Naik level", "Kemenangan singkat"),
    FINISH("Kuis selesai", "Penutup lembut")
}

class EduSoundEffects {
    private data class Note(val frequency: Double, val startMs: Int, val durationMs: Int, val volume: Double)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    @Volatile private var closed = false

    fun play(effect: EduSfx) {
        if (closed) return
        scope.launch {
            val (notes, durationMs) = when (effect) {
                EduSfx.CORRECT -> Pair(listOf(
                    Note(659.25, 0, 110, .17), Note(880.0, 95, 155, .18)), 270)
                EduSfx.WRONG -> Pair(listOf(
                    Note(349.23, 0, 105, .11), Note(293.66, 95, 125, .09)), 240)
                EduSfx.STAR -> Pair(listOf(
                    Note(659.25, 0, 110, .13), Note(880.0, 85, 130, .13),
                    Note(1174.66, 180, 210, .15)), 420)
                EduSfx.LEVEL -> Pair(listOf(
                    Note(523.25, 0, 155, .12), Note(659.25, 115, 150, .13),
                    Note(783.99, 225, 150, .13), Note(1046.5, 330, 280, .16)), 640)
                EduSfx.FINISH -> Pair(listOf(
                    Note(523.25, 0, 145, .10), Note(659.25, 115, 180, .11),
                    Note(783.99, 245, 245, .12)), 510)
            }
            val sampleRate = 24_000
            val samples = ShortArray(durationMs * sampleRate / 1000) { index ->
                val absoluteMs = index * 1000.0 / sampleRate
                var mixed = 0.0
                notes.forEach { note ->
                    val elapsed = (absoluteMs - note.startMs) / 1000.0
                    val duration = note.durationMs / 1000.0
                    if (elapsed >= 0.0 && elapsed < duration) {
                        val attack = min(1.0, elapsed / .015)
                        val release = min(1.0, (duration - elapsed) / .060)
                        val envelope = min(attack, release)
                        mixed += sin(2 * PI * note.frequency * elapsed) * note.volume * envelope
                    }
                }
                (mixed.coerceIn(-.72, .72) * Short.MAX_VALUE).roundToInt().toShort()
            }
            var track: AudioTrack? = null
            try {
                val bufferSize = max(samples.size * 2, AudioTrack.getMinBufferSize(
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT))
                if (bufferSize <= 0 || closed) return@launch
                track = AudioTrack.Builder()
                    .setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                    .setAudioFormat(AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build())
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(bufferSize)
                    .build()
                if (track.state != AudioTrack.STATE_INITIALIZED) return@launch
                if (track.write(samples, 0, samples.size) != samples.size) return@launch
                if (!closed) {
                    track.play()
                    delay(durationMs.toLong() + 100L)
                }
            } catch (_: Exception) {
                // Ketiadaan audio/izin perangkat tidak boleh menghentikan kuis.
            } finally {
                runCatching { track?.stop() }
                runCatching { track?.release() }
            }
        }
    }

    fun close() {
        closed = true
        scope.cancel()
    }
}
