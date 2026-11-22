package id.eduyasa.nativeapp

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class EduSubject(
    val id: String,
    val name: String,
    val icon: String,
    val chapters: List<String>,
    val materialsAvailable: Boolean = true
)
data class SelectedChapter(val subject: EduSubject, val number: Int) {
    val chapterId: String get() = subject.id + "-B" + number.toString().padStart(2, '0')
}
data class Lesson(
    val subjectName: String,
    val chapter: Int,
    val chapterTitle: String,
    val moduleTitle: String,
    val version: String,
    val content: String,
    val problem: String? = null
)
data class EduQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val answer: String,
    val discussion: String,
    val subjectId: String,
    val subjectName: String,
    val chapter: Int,
    val imageUrl: String? = null,
    val imageAlt: String? = null,
    val audioType: String? = null,
    val audioText: String? = null,
    val audioLabel: String? = null,
    val audioLang: String? = null,
    val audioRate: Float? = null,
    val audioUrl: String? = null
)
object EduyasaRepository {
    private const val BASE = "https://raw.githubusercontent.com/daniyasa-pixel/eduyasa/main/"
    private val manifest6 = setOf("MTK", "SBD", "PJK")
    private val illustrated6 = setOf("MTK", "SBD", "PJK")
    private val illustrated3 = setOf("BIN", "PKN", "MTK", "SBD", "IPAS", "ENG", "PJK")

    fun catalog(context: Context): Map<Int, List<EduSubject>> {
        val json = JSONObject(context.assets.open("catalog.json").bufferedReader().use { it.readText() })
        return listOf(3, 6).associateWith { grade ->
            val items = json.getJSONArray(grade.toString())
            (0 until items.length()).map { index ->
                val s = items.getJSONObject(index)
                val names = s.getJSONArray("chapters")
                EduSubject(
                    id = s.getString("id"),
                    name = s.getString("name"),
                    icon = s.optString("icon", "📖"),
                    chapters = (0 until names.length()).map { names.getString(it) },
                    materialsAvailable = s.optBoolean("materialsAvailable", true)
                )
            }
        }
    }

    private fun path(grade: Int) = if (grade == 3) "kelas-3/" else ""
    private fun id(grade: Int, subjectId: String, number: Int): String =
        (if (grade == 3) "K03-" else "") + subjectId + "-B" + number.toString().padStart(2, '0')
    private fun stem(s: String, number: Int) =
        s.lowercase() + "/" + s.lowercase() + "-b" + number.toString().padStart(2, '0')

    private fun readJson(relativePath: String): JSONObject {
        require(!relativePath.startsWith("/") && !relativePath.contains("..")) { "Lokasi data tidak valid." }
        val connection = URL(BASE + relativePath).openConnection() as HttpURLConnection
        connection.connectTimeout = 15000
        connection.readTimeout = 20000
        connection.setRequestProperty("Accept", "application/json")
        connection.useCaches = false
        try {
            if (connection.responseCode !in 200..299)
                error("Gagal mengambil " + relativePath + " (HTTP " + connection.responseCode + ")")
            return JSONObject(connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() })
        } finally {
            connection.disconnect()
        }
    }

    suspend fun checkConnection(grade: Int): Boolean = withContext(Dispatchers.IO) {
        runCatching { readJson(path(grade) + "mtk/mtk-b01.json") }.isSuccess
    }

    suspend fun lessons(grade: Int, chosen: List<SelectedChapter>): List<Lesson> =
        withContext(Dispatchers.IO) {
            chosen.map { c ->
                if (!c.subject.materialsAvailable) {
                    Lesson(c.subject.name, c.number, c.subject.chapters[c.number - 1],
                        "", "", "", "Materi bab ini belum tersedia. Kamu tetap bisa lanjut ke kuis.")
                } else {
                    try {
                        val data = readJson(path(grade) + stem(c.subject.id, c.number) + "-materi.json")
                        if (data.has("grade") && data.optInt("grade") != grade)
                            error("Kelas materi tidak sesuai.")
                        if (grade == 3 && data.optString("chapter_id") != id(grade, c.subject.id, c.number))
                            error("ID bab materi tidak sesuai.")
                        val modules = data.optJSONArray("modules") ?: data.optJSONArray("modul")
                            ?: error("Dua versi materi tidak ditemukan.")
                        if (modules.length() == 0) error("Materi masih kosong.")
                        val m = modules.getJSONObject((0 until modules.length()).random())
                        Lesson(c.subject.name, c.number,
                            data.optString("chapter_title", c.subject.chapters[c.number - 1]),
                            m.optString("title", m.optString("judul", "Materi Belajar")),
                            m.optString("version", m.optString("versi", "-")),
                            m.optString("content", "Konten belum tersedia."))
                    } catch (e: Exception) {
                        Lesson(c.subject.name, c.number, c.subject.chapters[c.number - 1],
                            "", "", "", "Gagal memuat materi: " + (e.message ?: "koneksi tidak tersedia"))
                    }
                }
            }
        }

    suspend fun questions(grade: Int, chosen: List<SelectedChapter>): List<EduQuestion> =
        withContext(Dispatchers.IO) {
            val manifests = mutableMapOf<String, JSONObject>()
            val manifestSubjects = if (grade == 3) chosen.map { it.subject.id }.toSet() else manifest6
            for (subjectId in chosen.map { it.subject.id }.distinct().filter { it in manifestSubjects }) {
                val manifest = readJson(path(grade) + subjectId.lowercase() + "/question-packs.json")
                if (manifest.optString("subject_id") != subjectId ||
                    (manifest.has("grade") && manifest.optInt("grade") != grade))
                    error("Daftar paket " + subjectId + " tidak sesuai.")
                manifests[subjectId] = manifest.getJSONObject("chapters")
            }
            val seenIds = mutableSetOf<String>()
            val bank = mutableListOf<EduQuestion>()
            for (c in chosen) {
                val chapterId = id(grade, c.subject.id, c.number)
                val stem = stem(c.subject.id, c.number)
                val manifest = manifests[c.subject.id]
                val packs = manifest?.optJSONArray(chapterId)
                val locations = if (manifest == null) listOf(stem + ".json")
                    else if (packs == null || packs.length() == 0) error("Paket " + chapterId + " belum tersedia.")
                    else (0 until packs.length()).map { packs.getString(it) }
                for (location in locations) {
                    if (manifest != null && !Regex("^" + Regex.escape(stem) + "[a-z]*\\.json$").matches(location))
                        error("Nama paket " + chapterId + " tidak sesuai.")
                    val data = readJson(path(grade) + location)
                    if (data.has("grade") && data.optInt("grade") != grade)
                        error("Data soal tidak sesuai dengan Kelas " + grade)
                    val chapters = data.optJSONArray("chapters")
                    val rows = mutableListOf<JSONObject>()
                    if (chapters != null) for (i in 0 until chapters.length()) {
                        val chapter = chapters.getJSONObject(i)
                        if (chapter.optString("chapter_id") != chapterId)
                            error("ID bab tidak sesuai: " + chapterId)
                        addQuestions(rows, chapter.optJSONArray("questions"))
                    }
                    addQuestions(rows, data.optJSONArray("bank_soal"))
                    if (rows.isEmpty()) error("Paket " + location + " kosong.")
                    rows.forEach { raw ->
                        val q = normalize(raw, c)
                        if (!seenIds.add(q.id)) error("ID soal berulang: " + q.id)
                        bank.add(q)
                    }
                }
            }
            val mustIllustrate = chosen.any {
                it.subject.id in (if (grade == 3) illustrated3 else illustrated6)
            }
            if (!mustIllustrate) {
                if (bank.size < 20) error("Bank soal kurang dari 20 soal.")
                bank.shuffled().take(20)
            } else {
                val illustrated = bank.filter { !it.imageUrl.isNullOrBlank() }
                val plain = bank.filter { it.imageUrl.isNullOrBlank() }
                if (illustrated.size < 5 || plain.size < 15)
                    error("Bank soal perlu minimal 5 soal bergambar dan 15 soal biasa.")
                (illustrated.shuffled().take(5) + plain.shuffled().take(15)).shuffled()
            }
        }

    private fun addQuestions(out: MutableList<JSONObject>, arr: JSONArray?) {
        if (arr != null) for (i in 0 until arr.length()) out += arr.getJSONObject(i)
    }

    private fun normalize(raw: JSONObject, c: SelectedChapter): EduQuestion {
        val text = listOf("text", "question", "pertanyaan")
            .firstNotNullOfOrNull { raw.optString(it).takeIf(String::isNotBlank) }
            ?: error("Teks soal " + raw.optString("id") + " kosong.")
        val source = raw.optJSONArray("options") ?: raw.optJSONArray("pilihan")
            ?: error("Opsi soal tidak ada.")
        val options = (0 until source.length()).map { i ->
            val item = source.get(i)
            val answer = if (item is JSONObject) item.optString("text") else item.toString()
            answer.replace(Regex("^[A-D]\\.\\s*", RegexOption.IGNORE_CASE), "").trim()
        }
        val value = raw.opt("answer") ?: raw.opt("jawaban")
        val rawAnswer = value?.toString()?.trim().orEmpty()
        val stripped = rawAnswer.replace(Regex("^[A-D]\\.\\s*", RegexOption.IGNORE_CASE), "")
        val correct = if (stripped in options) stripped
            else if (stripped.length == 1 && stripped[0] in 'A'..'D') options.getOrNull(stripped[0] - 'A') ?: ""
            else if (value is Number) options.getOrNull(value.toInt()) ?: ""
            else stripped
        if (options.size != 4 || options.any(String::isBlank) ||
            options.toSet().size != 4 || correct !in options)
            error("Empat opsi atau kunci soal tidak valid: " + raw.optString("id"))
        val image = when (val v = raw.opt("image")) {
            is String -> v.takeIf(String::isNotBlank)
            is JSONObject -> v.optString("src").takeIf(String::isNotBlank)
            else -> null
        }
        val audio = raw.optJSONObject("audio")
        return EduQuestion(
            id = raw.optString("id").takeIf(String::isNotBlank)
                ?: error("ID soal tidak tersedia."),
            text = text, options = options, answer = correct,
            discussion = raw.optString("discussion",
                raw.optString("explanation", raw.optString("pembahasan", "Tidak ada pembahasan."))),
            subjectId = c.subject.id,
            subjectName = c.subject.name,
            chapter = c.number,
            imageUrl = image,
            imageAlt = raw.optJSONObject("image")?.optString("alt"),
            audioType = audio?.optString("type"),
            audioText = audio?.optString("text"),
            audioLabel = audio?.optString("label"),
            audioLang = audio?.optString("lang", "en-US"),
            audioRate = audio?.optDouble("rate", 0.85)?.toFloat(),
            audioUrl = audio?.optString("url")?.takeIf(String::isNotBlank)
                ?: audio?.optString("src")?.takeIf(String::isNotBlank)
        )
    }
}
