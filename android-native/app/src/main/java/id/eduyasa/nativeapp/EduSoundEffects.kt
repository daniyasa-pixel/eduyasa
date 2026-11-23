package id.eduyasa.nativeapp

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Game feedback uses a preloaded SoundPool (low latency) on MEDIA/GAME audio
 * instead of allocating a new AudioTrack for every tap. On many Android
 * devices the separate system-sonification stream can be muted while media
 * volume is audible; SoundPool follows the normal media volume.
 *
 * Six short original synthesized cues; no external audio, permissions or JSON.
 */
enum class EduSfx(val displayName: String, val description: String) {
    TAP("Sentuh pilihan A/B/C/D", "Klik lembut ketika memilih jawaban"),
    CORRECT("Jawaban benar", "Ding ceria"),
    WRONG("Jawaban belum tepat", "Nada lembut"),
    STAR("Bintang meningkat", "Kilau bintang"),
    LEVEL("Naik level", "Nada kemenangan singkat"),
    FINISH("Kuis selesai", "Nada penutup")
}

class EduSoundEffects(context: Context) {
    private data class Note(
        val hz: Double,
        val startMs: Int,
        val lengthMs: Int,
        val level: Double
    )
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val pool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    private val loadedIds = ConcurrentHashMap.newKeySet<Int>()
    private val sampleIds = ConcurrentHashMap<EduSfx, Int>()
    // Audible fallback while SoundPool is loading, or if an audio sample fails.
    private val backupTone = runCatching {
        ToneGenerator(AudioManager.STREAM_MUSIC, 85)
    }.getOrNull()
    @Volatile private var closed = false

    init {
        pool.setOnLoadCompleteListener { _, id, status ->
            if (closed) return@setOnLoadCompleteListener
            if (status == 0) {
                loadedIds.add(id)
            } else {
                Log.w("EduyasaSFX", "Gagal memuat suara: id=$id status=$status")
            }
        }
        // Generate reusable WAV files once in app-private cache, then load all
        // samples long before the child reaches the quiz.
        scope.launch {
            EduSfx.values().forEach { type ->
                if (closed) return@launch
                try {
                    val file = File(appContext.cacheDir, "eduyasa_sfx_v4_${type.name}.wav")
                    if (!file.exists() || file.length() <= 44L) {
                        file.writeBytes(generateWav(type))
                    }
                    if (!closed) {
                        val id = pool.load(file.absolutePath, 1)
                        if (id != 0) sampleIds[type] = id
                        else Log.w("EduyasaSFX", "SoundPool tidak memuat ${type.name}")
                    }
                } catch (e: Exception) {
                    Log.w("EduyasaSFX", "Gagal menyiapkan ${type.name}", e)
                }
            }
        }
    }

    fun play(type: EduSfx) {
        if (closed) return
        val id = sampleIds[type]
        if (id != null && loadedIds.contains(id)) {
            val stream = pool.play(id, 1f, 1f, 1, 0, 1f)
            if (stream != 0) return
            Log.w("EduyasaSFX", "Gagal memainkan ${type.name}, gunakan fallback")
        }
        // Do not queue a delayed sound after the child has moved on.
        // ToneGenerator also uses STREAM_MUSIC, never the muted system-click stream.
        val tone = when (type) {
            EduSfx.TAP -> ToneGenerator.TONE_PROP_BEEP
            EduSfx.CORRECT, EduSfx.STAR, EduSfx.LEVEL ->
                ToneGenerator.TONE_PROP_ACK
            EduSfx.WRONG -> ToneGenerator.TONE_PROP_NACK
            EduSfx.FINISH -> ToneGenerator.TONE_PROP_BEEP2
        }
        runCatching { backupTone?.startTone(tone, 115) }
            .onFailure { Log.w("EduyasaSFX", "Fallback audio gagal", it) }
    }

    private fun generateWav(type: EduSfx): ByteArray {
        val (notes, durationMs) = when (type) {
            EduSfx.TAP -> listOf(Note(820.0, 0, 52, .27)) to 75
            EduSfx.CORRECT -> listOf(
                Note(659.25, 0, 140, .32),
                Note(880.0, 110, 210, .35)
            ) to 340
            EduSfx.WRONG -> listOf(
                Note(330.0, 0, 140, .25),
                Note(260.0, 125, 185, .24)
            ) to 325
            EduSfx.STAR -> listOf(
                Note(659.25, 0, 130, .26),
                Note(880.0, 100, 160, .28),
                Note(1174.66, 215, 220, .32)
            ) to 450
            EduSfx.LEVEL -> listOf(
                Note(523.25, 0, 175, .29),
                Note(659.25, 140, 160, .29),
                Note(783.99, 265, 170, .31),
                Note(1046.50, 390, 265, .34)
            ) to 670
            EduSfx.FINISH -> listOf(
                Note(523.25, 0, 150, .27),
                Note(659.25, 130, 180, .28),
                Note(783.99, 270, 270, .31)
            ) to 560
        }

        val sampleRate = 44_100
        val count = durationMs * sampleRate / 1000
        val bytes = count * 2
        val wav = ByteBuffer.allocate(44 + bytes).order(ByteOrder.LITTLE_ENDIAN)
        wav.put("RIFF".toByteArray(Charsets.US_ASCII))
        wav.putInt(36 + bytes)
        wav.put("WAVE".toByteArray(Charsets.US_ASCII))
        wav.put("fmt ".toByteArray(Charsets.US_ASCII))
        wav.putInt(16)
        wav.putShort(1.toShort()) // PCM
        wav.putShort(1.toShort()) // mono
        wav.putInt(sampleRate)
        wav.putInt(sampleRate * 2)
        wav.putShort(2.toShort())
        wav.putShort(16.toShort())
        wav.put("data".toByteArray(Charsets.US_ASCII))
        wav.putInt(bytes)
        for (i in 0 until count) {
            val atMs = i * 1000.0 / sampleRate
            var sample = 0.0
            for (note in notes) {
                val elapsed = atMs - note.startMs
                if (elapsed >= 0.0 && elapsed < note.lengthMs) {
                    val envelope = min(
                        min(1.0, elapsed / 12.0),
                        min(1.0, (note.lengthMs - elapsed) / 65.0)
                    ).coerceAtLeast(0.0)
                    sample += sin(2 * PI * note.hz * elapsed / 1000.0) *
                        note.level * envelope
                }
            }
            wav.putShort((sample.coerceIn(-0.8, 0.8) *
                Short.MAX_VALUE).roundToInt().toShort())
        }
        return wav.array()
    }

    fun close() {
        closed = true
        scope.cancel()
        pool.release()
        runCatching { backupTone?.release() }
    }
}
