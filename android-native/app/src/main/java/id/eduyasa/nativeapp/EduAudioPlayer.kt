package id.eduyasa.nativeapp

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import java.util.Locale

/**
 * Supports existing EDUYASA listening format:
 *   {"type":"tts","lang":"en-US","text":"I like rice.","rate":0.85}
 * Also supports future {"type":"file","url":"https://.../clip.mp3"}.
 * No media files are bundled and no quiz JSON is rewritten.
 */
class EduAudioPlayer(context: Context) {
    private val appContext = context.applicationContext
    var status by mutableStateOf("Menyiapkan suara...")
        private set
    private var ttsReady = false
    private var media: MediaPlayer? = null
    private val speech = TextToSpeech(appContext) { code ->
        ttsReady = code == TextToSpeech.SUCCESS
        status = if (ttsReady) "" else "Mesin pembaca suara tidak tersedia di HP ini."
    }

    fun play(
        type: String?,
        text: String?,
        lang: String?,
        rate: Float?,
        fileUrl: String?
    ) {
        stop()
        when (type?.lowercase()) {
            "tts" -> {
                if (text.isNullOrBlank()) {
                    status = "Teks listening tidak tersedia."
                    return
                }
                if (!ttsReady) {
                    status = "Suara HP belum siap. Periksa pengaturan Text-to-Speech."
                    return
                }
                val locale = Locale.forLanguageTag(lang?.takeIf(String::isNotBlank) ?: "en-US")
                val availability = speech.setLanguage(locale)
                if (availability == TextToSpeech.LANG_NOT_SUPPORTED ||
                    availability == TextToSpeech.LANG_MISSING_DATA) {
                    status = "Bahasa " + locale.displayName +
                        " belum tersedia. Pasang data suara lewat pengaturan HP."
                    return
                }
                speech.setSpeechRate((rate ?: .85f).coerceIn(.5f, 1.5f))
                val outcome = speech.speak(
                    text, TextToSpeech.QUEUE_FLUSH, null, "eduyasa-listening"
                )
                status = if (outcome == TextToSpeech.SUCCESS) "🔊 Sedang memutar listening"
                    else "Gagal memutar listening. Coba lagi."
            }
            "file", "audio", "url" -> {
                val url = resolveEduMedia(fileUrl)
                if (url == null) {
                    status = "Alamat file audio tidak valid atau tidak tersedia."
                    return
                }
                try {
                    val player = MediaPlayer()
                    media = player
                    player.setDataSource(url)
                    player.setOnPreparedListener {
                        status = "🔊 Sedang memutar audio"
                        it.start()
                    }
                    player.setOnCompletionListener {
                        status = ""
                        it.reset()
                        it.release()
                        if (media === it) media = null
                    }
                    player.setOnErrorListener { mp, _, _ ->
                        status = "Audio gagal diputar. Periksa koneksi internet."
                        if (media === mp) media = null
                        mp.release()
                        true
                    }
                    status = "Memuat file audio..."
                    player.prepareAsync()
                } catch (e: Exception) {
                    media?.release()
                    media = null
                    status = "Audio gagal dimuat: " + (e.localizedMessage ?: "kesalahan jaringan")
                }
            }
            else -> status = "Format audio belum didukung."
        }
    }

    fun stop() {
        if (ttsReady) speech.stop()
        runCatching { media?.reset(); media?.release() }
        media = null
        status = ""
    }

    fun release() {
        stop()
        speech.shutdown()
    }
}

@Composable
fun rememberEduAudioPlayer(): EduAudioPlayer {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(context) { EduAudioPlayer(context) }
    DisposableEffect(player) { onDispose { player.release() } }
    return player
}
