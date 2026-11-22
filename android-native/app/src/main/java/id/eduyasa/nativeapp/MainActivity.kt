package id.eduyasa.nativeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.net.URLEncoder

private val Blue = Color(0xFF2563EB)
private val DarkBlue = Color(0xFF1E3A8A)
private val Pale = Color(0xFFF4F7FB)
private val Muted = Color(0xFF718096)
private val Line = Color(0xFFE5EAF2)
private val Green = Color(0xFF16A34A)
private val Red = Color(0xFFDC2626)
private enum class Page { HOME, SUBJECTS, CHAPTERS, MATERIAL, PREP, QUIZ, RESULT, STATS, SETTINGS }
private enum class SaveStatus { NONE, SAVING, SAVED, FAILED }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Pale)) {
                EduyasaApp()
            }
        }
    }
}

@Composable
private fun EduyasaApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val catalog = remember { EduyasaRepository.catalog(context.applicationContext) }
    val profileStore = remember { StudentProfileStore(context.applicationContext) }
    val studyHistory = remember { StudyHistoryStore(context.applicationContext) }
    val preferences = remember { context.applicationContext.getSharedPreferences("eduyasa_settings", 0) }
    var defaultQuestionCount by remember {
        mutableIntStateOf(preferences.getInt("quiz_length", 20).takeIf {
            it in LearningGamification.QUIZ_LENGTHS
        } ?: 20)
    }
    var questionCount by remember { mutableIntStateOf(defaultQuestionCount) }
    var audioEnabled by remember { mutableStateOf(preferences.getBoolean("listening_enabled", true)) }
    var drawerOpen by remember { mutableStateOf(false) }
    var pendingDestination by remember { mutableStateOf<Page?>(null) }
    var profiles by remember { mutableStateOf(profileStore.load()) }
    var activeProfileId by remember { mutableStateOf(profileStore.activeProfileId()) }
    val activeProfile = profiles.firstOrNull { it.id == activeProfileId }
    var editProfileOpen by remember { mutableStateOf(false) }
    var profileError by remember { mutableStateOf("") }
    var editingProfile by remember { mutableStateOf<StudentProfile?>(null) }
    LaunchedEffect(Unit) {
        if (profiles.isEmpty()) {
            editingProfile = null
            editProfileOpen = true
        }
    }
    var page by remember { mutableStateOf(Page.HOME) }
    var grade by remember { mutableIntStateOf(0) }
    val selectedSubjects = remember { mutableStateListOf<String>() }
    val chapters = remember { mutableStateMapOf<String, Set<Int>>() }
    var lessonItems by remember { mutableStateOf<List<Lesson>>(emptyList()) }
    var quiz by remember { mutableStateOf<List<EduQuestion>>(emptyList()) }
    var qIndex by remember { mutableIntStateOf(0) }
    var options by remember { mutableStateOf<List<String>>(emptyList()) }
    var selected by remember { mutableStateOf<String?>(null) }
    var checked by remember { mutableStateOf(false) }
    val results = remember { mutableStateMapOf<Int, Boolean>() }
    val answersChosen = remember { mutableStateMapOf<Int, String>() }
    var sessionId by remember { mutableStateOf(UUID.randomUUID().toString()) }
    var saveStatus by remember { mutableStateOf(SaveStatus.NONE) }
    var saveError by remember { mutableStateOf("") }
    var savedRevision by remember { mutableIntStateOf(0) }
    var statistics by remember { mutableStateOf<LearningStatistics?>(null) }
    var statisticsLoading by remember { mutableStateOf(false) }
    var statisticsError by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf<Boolean?>(null) }
    var aboutOpen by remember { mutableStateOf(false) }
    var reportOpen by remember { mutableStateOf(false) }
    var leaveOpen by remember { mutableStateOf(false) }
    var zoomImage by remember { mutableStateOf<String?>(null) }
    val audioEngine = rememberEduAudioPlayer()
    val subjects = catalog[grade].orEmpty()

    fun reset() {
        page = Page.HOME
        grade = 0
        selectedSubjects.clear()
        chapters.clear()
        lessonItems = emptyList()
        quiz = emptyList()
        results.clear()
        answersChosen.clear()
        errorText = ""
        connected = null
        busy = false
        questionCount = defaultQuestionCount
    }
    fun selections(): List<SelectedChapter> = selectedSubjects.flatMap { key ->
        val subject = subjects.firstOrNull { it.id == key }
        if (subject == null) emptyList()
        else chapters[key].orEmpty().sorted().map { SelectedChapter(subject, it) }
    }
    fun resetQuiz() {
        sessionId = UUID.randomUUID().toString()
        saveStatus = SaveStatus.NONE
        saveError = ""
        quiz = quiz.shuffled()
        qIndex = 0
        selected = null
        checked = false
        results.clear()
        answersChosen.clear()
        options = quiz.firstOrNull()?.options?.shuffled().orEmpty()
        page = Page.QUIZ
    }
    fun persistCompletedQuiz() {
        if (saveStatus == SaveStatus.SAVING || saveStatus == SaveStatus.SAVED) return
        val profileId = activeProfileId
        if (profileId.isNullOrBlank() || quiz.size !in LearningGamification.QUIZ_LENGTHS ||
            results.size != quiz.size || answersChosen.size != quiz.size) {
            saveStatus = SaveStatus.FAILED
            saveError = "Riwayat belum bisa disimpan: identitas siswa atau jawaban belum lengkap."
            return
        }
        val snapshot = SavedSession(
            id = sessionId,
            profileId = profileId,
            grade = grade,
            completedAt = System.currentTimeMillis(),
            answers = quiz.mapIndexed { index, q ->
                SavedAnswer(
                    position = index,
                    subjectId = q.subjectId,
                    chapter = q.chapter,
                    questionId = q.id,
                    selectedAnswer = answersChosen[index].orEmpty(),
                    correctAnswer = q.answer,
                    correct = results[index] == true
                )
            }
        )
        saveStatus = SaveStatus.SAVING
        saveError = ""
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    studyHistory.saveCompletedSession(snapshot)
                }
                saveStatus = SaveStatus.SAVED
                savedRevision++
            } catch (e: Exception) {
                saveStatus = SaveStatus.FAILED
                saveError = e.message ?: "Tidak dapat menyimpan hasil di perangkat."
            }
        }
    }
    fun back() {
        when (page) {
            Page.SUBJECTS, Page.STATS, Page.SETTINGS -> page = Page.HOME
            Page.CHAPTERS -> page = Page.SUBJECTS
            Page.MATERIAL -> page = Page.CHAPTERS
            Page.PREP -> page = Page.MATERIAL
            Page.QUIZ, Page.RESULT -> leaveOpen = true
            Page.HOME -> Unit
        }
    }
    fun goTo(target: Page) {
        drawerOpen = false
        if (page == Page.QUIZ || page == Page.RESULT) {
            pendingDestination = target
            leaveOpen = true
            return
        }
        when (target) {
            Page.HOME -> reset()
            Page.SUBJECTS -> {
                if (activeProfile == null) reset() else {
                    grade = activeProfile.grade
                    selectedSubjects.clear()
                    chapters.clear()
                    statistics = null
                    page = Page.SUBJECTS
                }
            }
            Page.STATS -> {
                if (activeProfile == null) reset() else {
                    grade = activeProfile.grade
                    statistics = null
                    page = Page.STATS
                }
            }
            else -> page = target
        }
    }
    BackHandler(enabled = drawerOpen) { drawerOpen = false }
    BackHandler(enabled = page != Page.HOME && !drawerOpen) { back() }
    LaunchedEffect(grade) {
        if (grade == 3 || grade == 6) connected = EduyasaRepository.checkConnection(grade)
    }
    LaunchedEffect(page, qIndex) {
        // Stop listening if student leaves a question or opens another page.
        audioEngine.stop()
    }
    LaunchedEffect(page, activeProfileId, savedRevision) {
        val profileId = activeProfileId
        if (page in setOf(Page.STATS, Page.SUBJECTS, Page.CHAPTERS) &&
            !profileId.isNullOrBlank()) {
            statisticsLoading = true
            statisticsError = ""
            try {
                statistics = withContext(Dispatchers.IO) {
                    studyHistory.statistics(profileId)
                }
            } catch (e: Exception) {
                statistics = null
                statisticsError = e.message ?: "Gagal membaca riwayat belajar."
            } finally {
                statisticsLoading = false
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
    Column(
        Modifier.fillMaxSize().background(Pale)
            .verticalScroll(rememberScrollState())
    ) {
        EduHeader(
            grade = grade,
            home = page == Page.HOME,
            connection = connected,
            onOpenMenu = { drawerOpen = true }
        )
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp)) {
            if (page != Page.HOME && page != Page.RESULT && page != Page.STATS) {
                StepIndicator(when (page) {
                    Page.SUBJECTS -> 1
                    Page.CHAPTERS -> 2
                    Page.MATERIAL -> 3
                    else -> 4
                })
            }
            when (page) {
                Page.HOME -> {
                    Text("⭐ Portal Belajar Interaktif", color = Blue, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("Belajar Jadi Lebih Seru!", fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold, color = DarkBlue)
                    Text("Satu HP bisa digunakan oleh beberapa siswa.",
                        color = Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(24.dp))
                    Title("Siapa yang belajar hari ini?")
                    Text("Pilih nama siswa. Kelas akan terisi otomatis.",
                        color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    if (profiles.isEmpty()) {
                        Panel {
                            Text("Belum ada profil siswa.", fontWeight = FontWeight.Bold)
                            Text("Tambahkan nama dan kelas sekali saja. Profil akan tersimpan di HP ini.",
                                fontSize = 13.sp, color = Muted)
                        }
                    }
                    profiles.forEach { profile ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (activeProfileId == profile.id)
                                    Color(0xFFEFF6FF) else Color.White)
                                .testTag("profile-" + profile.id)
                                .clickable {
                                    activeProfileId = profile.id
                                    profileStore.setActiveProfileId(profile.id)
                                    grade = profile.grade
                                    statistics = null
                                    selectedSubjects.clear()
                                    chapters.clear()
                                    errorText = ""
                                    page = Page.SUBJECTS
                                }.padding(start = 15.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎓", fontSize = 29.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(profile.name, fontWeight = FontWeight.Bold,
                                    color = DarkBlue, fontSize = 16.sp)
                                Text("Kelas " + profile.grade + " SD",
                                    color = Muted, fontSize = 12.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                TextButton(
                                    onClick = {
                                        activeProfileId = profile.id
                                        profileStore.setActiveProfileId(profile.id)
                                        grade = profile.grade
                                        statistics = null
                                        selectedSubjects.clear()
                                        chapters.clear()
                                        page = Page.STATS
                                    },
                                    modifier = Modifier.testTag("stats-profile-" + profile.id)
                                ) { Text("Statistik") }
                                TextButton(
                                    onClick = {
                                        editingProfile = profile
                                        editProfileOpen = true
                                    },
                                    modifier = Modifier.testTag("edit-profile-" + profile.id)
                                ) { Text("Ubah") }
                            }
                            Text("›", color = Blue, fontSize = 25.sp,
                                modifier = Modifier.padding(end = 10.dp))
                        }
                    }
                    if (profileError.isNotBlank()) ErrorMessage(profileError)
                    Primary("＋ Tambah Profil Siswa", "add-profile") {
                        editingProfile = null
                        editProfileOpen = true
                    }
                    Secondary("Tentang EDUYASA", "about-btn") { aboutOpen = true }
                    Spacer(Modifier.height(8.dp))
                    Text("Profil disimpan hanya di perangkat ini.",
                        fontSize = 11.sp, color = Muted,
                        modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
                Page.SUBJECTS -> {
                    Text("Belajar bersama " + (activeProfile?.name ?: "Siswa") +
                        " • Kelas " + grade + " SD", fontSize = 13.sp, color = Blue)
                    OutlinedButton(
                        onClick = { statistics = null; page = Page.STATS },
                        modifier = Modifier.testTag("open-statistics")
                    ) { Text("📊 Lihat Evaluasi Belajar") }
                    Spacer(Modifier.height(6.dp))
                    Title("Pilih Mata Pelajaran")
                    Text("Pilih minimal 1 dan maksimal 2 mata pelajaran.", color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(14.dp))
                    subjects.chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { s ->
                                val picked = s.id in selectedSubjects
                                val enabled = picked || selectedSubjects.size < 2
                                Surface(
                                    modifier = Modifier.weight(1f).heightIn(min = 112.dp)
                                        .testTag("subject-" + s.id)
                                        .clickable(enabled) {
                                            if (picked) {
                                                selectedSubjects.remove(s.id)
                                                chapters.remove(s.id)
                                            } else selectedSubjects.add(s.id)
                                        },
                                    color = if (picked) Color(0xFFEFF6FF) else Color.White,
                                    shape = RoundedCornerShape(15.dp),
                                    border = BorderStroke(2.dp, if (picked) Blue else Line)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(s.icon, fontSize = 24.sp)
                                        Spacer(Modifier.height(7.dp))
                                        Text(s.name, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                            color = if (enabled) Color(0xFF172033) else Muted)
                                        Text(s.chapters.size.toString() + " bab terdaftar",
                                            fontSize = 11.sp, color = Muted)
                                        val assessed = statistics?.chapters.orEmpty().filter {
                                            it.grade == grade && it.subjectId == s.id
                                        }
                                        val rating = LearningGamification.summary(assessed)
                                        Spacer(Modifier.height(5.dp))
                                        if (rating == null) {
                                            Text("Belum dinilai", fontSize = 11.sp, color = Muted)
                                        } else {
                                            LearningStars(rating.stars)
                                            Text(rating.accuracy.toString() + "% • " +
                                                rating.assessedChapters + "/" + s.chapters.size +
                                                " bab" + if (rating.provisional || rating.assessedChapters < s.chapters.size)
                                                    " • Sementara" else "",
                                                fontSize = 10.sp, color = Muted)
                                        }
                                    }
                                }
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                    Primary("NEXT", "btnNext1", selectedSubjects.isNotEmpty()) {
                        page = Page.CHAPTERS
                    }
                    Secondary("KEMBALI", "btnBack1") { back() }
                }
                Page.CHAPTERS -> {
                    Title("Pilih Bab")
                    val total = selectedSubjects.sumOf { chapters[it].orEmpty().size }
                    val allSelected = selectedSubjects.all { chapters[it].orEmpty().isNotEmpty() }
                    val valid = total in 1..4 && allSelected &&
                        (selectedSubjects.size != 2 || selectedSubjects.all { chapters[it].orEmpty().size <= 3 })
                    Panel {
                        selectedSubjects.forEach { key ->
                            val subj = subjects.firstOrNull { it.id == key }
                            if (subj != null) {
                                Text(subj.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(Modifier.height(6.dp))
                                val semester = SemesterPresentation.forSubject(
                                    grade, subj.id, subj.chapters.size
                                )
                                if (semester == null) {
                                    val explanation = SemesterPresentation.pendingReason(grade, subj.id)
                                    if (explanation != null) {
                                        Text(explanation, fontSize = 12.sp,
                                            color = Color(0xFF92400E),
                                            modifier = Modifier.padding(vertical = 8.dp)
                                                .testTag("semester-pending-" + grade + "-" + subj.id))
                                    }
                                }
                                subj.chapters.forEachIndexed { index, label ->
                                    val num = index + 1
                                    if (semester != null &&
                                        (index == 0 || index == semester.lastChapterFirstSemester)) {
                                        SemesterSectionHeader(
                                            number = semester.semesterFor(num),
                                            evidence = semester.evidence,
                                            grade = grade,
                                            subjectId = subj.id
                                        )
                                    }
                                    val isOn = num in chapters[key].orEmpty()
                                    val canPick = isOn || (total < 4 &&
                                        (selectedSubjects.size != 2 || chapters[key].orEmpty().size < 3))
                                    Row(Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(when (semester?.semesterFor(num)) {
                                            1 -> Color(0xFFF3F8FF)
                                            2 -> Color(0xFFFFF9ED)
                                            else -> Color.White
                                        })
                                        .testTag("chapter-" + (if (grade == 3) "K03-" else "") +
                                            key + "-B" + num.toString().padStart(2, '0'))
                                        .clickable(enabled = canPick) {
                                            val updated = chapters[key].orEmpty().toMutableSet()
                                            if (isOn) updated.remove(num) else updated.add(num)
                                            chapters[key] = updated
                                        }
                                        .padding(vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isOn,
                                            enabled = canPick,
                                            onCheckedChange = { yes ->
                                                val updated = chapters[key].orEmpty().toMutableSet()
                                                if (yes) updated.add(num) else updated.remove(num)
                                                chapters[key] = updated
                                            }
                                        )
                                        Column(Modifier.weight(1f)) {
                                            Text("Bab " + num + ": " + label,
                                                fontSize = 13.sp,
                                                color = if (canPick) Color(0xFF172033) else Muted)
                                            val progress = statistics?.chapters?.firstOrNull {
                                                it.grade == grade && it.subjectId == key && it.chapter == num
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            ChapterLevelAndStars(progress, compact = true)
                                        }
                                    }
                                    HorizontalDivider(color = Line)
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                        Text(
                            when {
                                selectedSubjects.size == 2 && !allSelected ->
                                    total.toString() + " bab dipilih. Minimal 1 bab dari tiap mapel."
                                total == 4 -> "Maksimal 4 bab telah tercapai."
                                else -> total.toString() + " dari maksimal 4 bab dipilih."
                            }, color = Muted, fontSize = 12.sp
                        )
                    }
                    Actions(
                        backTag = "btnBack2", nextTag = "btnNext2",
                        nextEnabled = valid && !busy, nextText = if (busy) "MEMUAT..." else "NEXT",
                        onBack = { back() },
                        onNext = {
                            scope.launch {
                                busy = true
                                errorText = ""
                                try {
                                    lessonItems = EduyasaRepository.lessons(grade, selections())
                                    connected = true
                                    page = Page.MATERIAL
                                } catch (e: Exception) {
                                    errorText = e.message ?: "Materi gagal dimuat."
                                    connected = false
                                } finally { busy = false }
                            }
                        }
                    )
                    if (errorText.isNotBlank()) ErrorMessage(errorText)
                }
                Page.MATERIAL -> {
                    Title("Baca Materi")
                    Column(Modifier.testTag("materiContainer")) {
                        lessonItems.forEach { lesson ->
                            Panel {
                                Text(lesson.subjectName + " - Bab " + lesson.chapter,
                                    color = Blue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(lesson.chapterTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                if (lesson.problem != null) {
                                    Spacer(Modifier.height(10.dp))
                                    ErrorMessage(lesson.problem)
                                } else {
                                    Text("Modul Terpilih: " + lesson.moduleTitle + " (Versi " + lesson.version + ")",
                                        color = Muted, fontSize = 11.sp)
                                    Spacer(Modifier.height(12.dp))
                                    EduMaterialContent(lesson.content) { url -> zoomImage = url }
                                }
                            }
                        }
                    }
                    Actions("btnBack3", "btnNext3", true, "NEXT",
                        onBack = { back() }, onNext = { page = Page.PREP })
                }
                Page.PREP -> {
                    Panel {
                        Title("Persiapan Kuis", centered = true)
                        Text("Kelas " + grade + " SD", textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(), color = Blue, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        Text(selectedSubjects.joinToString(", ") { id ->
                            subjects.firstOrNull { it.id == id }?.name ?: id
                        } + "\n" + selections().size + " bab dipilih.\n" +
                            questionCount + " soal acak siap untuk latihan.",
                            modifier = Modifier.fillMaxWidth().testTag("quizPrepInfo"),
                            textAlign = TextAlign.Center, fontSize = 14.sp, lineHeight = 23.sp)
                        Spacer(Modifier.height(14.dp))
                        Text("Jumlah soal:", color = DarkBlue, fontWeight = FontWeight.Bold)
                        QuizLengthChooser(value = questionCount, onSelect = { questionCount = it })
                        if (errorText.isNotBlank()) ErrorMessage(errorText)
                        Spacer(Modifier.height(12.dp))
                        Primary(if (busy) "MEMPERSIAPKAN..." else "MULAI KUIS",
                            "btnStartQuiz", !busy) {
                            scope.launch {
                                busy = true
                                errorText = ""
                                try {
                                    quiz = EduyasaRepository.questions(grade, selections(), questionCount)
                                    connected = true
                                    resetQuiz()
                                } catch (e: Exception) {
                                    errorText = e.message ?: "Kuis gagal dimuat."
                                    connected = false
                                } finally { busy = false }
                            }
                        }
                    }
                    Secondary("KEMBALI", "btnBack4") { back() }
                }
                Page.QUIZ -> {
                    val q = quiz.getOrNull(qIndex)
                    if (q != null) {
                        Panel {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Soal " + (qIndex + 1) + " dari " + quiz.size,
                                    color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(q.subjectName + " - Bab " + q.chapter,
                                    color = Blue, fontSize = 11.sp)
                            }
                            Spacer(Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { (qIndex + if (checked) 1 else 0).toFloat() / quiz.size },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(8.dp)),
                                color = Blue, trackColor = Line
                            )
                            Spacer(Modifier.height(19.dp))
                            EduRichText(q.text, modifier = Modifier.testTag("quizQText"),
                                sizeSp = 17f)
                            Text("ID: " + q.id, color = Muted, fontSize = 10.sp)
                            if (!q.imageUrl.isNullOrBlank()) {
                                Spacer(Modifier.height(13.dp))
                                EduRemoteImage(
                                    source = q.imageUrl,
                                    description = q.imageAlt?.takeIf(String::isNotBlank)
                                        ?: "Gambar pertanyaan",
                                    modifier = Modifier.fillMaxWidth()
                                        .testTag("quizQImage"),
                                    onClick = {
                                        resolveEduMedia(q.imageUrl)?.let { zoomImage = it }
                                    }
                                )
                                Text("Ketuk gambar untuk memperbesar", color = Muted,
                                    fontSize = 11.sp)
                            }
                            if (!q.audioType.isNullOrBlank()) {
                                Spacer(Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = {
                                        audioEngine.play(q.audioType, q.audioText,
                                            q.audioLang, q.audioRate, q.audioUrl)
                                    },
                                    enabled = audioEnabled,
                                    modifier = Modifier.testTag("quizQAudio")
                                ) { Text(q.audioLabel?.takeIf(String::isNotBlank) ?: "🔊 DENGARKAN SUARA") }
                                if (audioEngine.status.isNotBlank()) Text(
                                    audioEngine.status, color = Muted, fontSize = 12.sp,
                                    modifier = Modifier.testTag("listening-status")
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            options.forEachIndexed { i, option ->
                                val isCorrect = checked && option == q.answer
                                val isWrong = checked && selected == option && option != q.answer
                                val isSelected = !checked && selected == option
                                val bg = when {
                                    isCorrect -> Color(0xFFDCFCE7)
                                    isWrong -> Color(0xFFFEE2E2)
                                    isSelected -> Color(0xFFEFF6FF)
                                    else -> Color.White
                                }
                                val border = when {
                                    isCorrect -> Green
                                    isWrong -> Red
                                    isSelected -> Blue
                                    else -> Line
                                }
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                                        .testTag("option-" + "ABCD"[i]).clickable(enabled = !checked) { selected = option },
                                    shape = RoundedCornerShape(12.dp),
                                    color = bg, border = BorderStroke(2.dp, border)
                                ) {
                                    Row(Modifier.fillMaxWidth().padding(15.dp)) {
                                        Text("ABCD"[i].toString() + ". ",
                                            fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        EduRichText(option, sizeSp = 14f,
                                            modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                            if (checked) {
                                val correctLabel = "ABCD".getOrNull(options.indexOf(q.answer)) ?: '?'
                                Spacer(Modifier.height(10.dp))
                                Panel {
                                    Text("Jawaban kamu: " + if (selected == q.answer) "Benar ✅" else "Belum tepat ❌",
                                        fontWeight = FontWeight.Bold)
                                    Text("Jawaban benar: " + correctLabel, color = Green)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Pembahasan:", color = Blue, fontWeight = FontWeight.Bold)
                                    EduRichText(q.discussion, sizeSp = 14f)
                                }
                            }
                            Primary(
                                if (!checked) "KOREKSI JAWABAN"
                                else if (qIndex == quiz.lastIndex) "LIHAT HASIL" else "LANJUT",
                                "btnQuizAction", checked || selected != null
                            ) {
                                if (!checked) {
                                    checked = true
                                    results[qIndex] = selected == q.answer
                                    answersChosen[qIndex] = selected.orEmpty()
                                } else if (qIndex == quiz.lastIndex) {
                                    page = Page.RESULT
                                    persistCompletedQuiz()
                                }
                                else {
                                    qIndex++
                                    selected = null
                                    checked = false
                                    options = quiz[qIndex].options.shuffled()
                                }
                            }
                        }
                    }
                }
                Page.STATS -> {
                    LearningStatisticsScreen(
                        student = activeProfile,
                        statistics = statistics,
                        catalog = catalog,
                        loading = statisticsLoading,
                        error = statisticsError,
                        onRetry = { savedRevision++ },
                        onBack = { back() }
                    )
                }
                Page.RESULT -> {
                    val correct = results.values.count { it }
                    val score = if (quiz.isEmpty()) 0 else (correct * 100f / quiz.size).toInt()
                    Panel {
                        Text("🎉", fontSize = 46.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        Text("Kuis Selesai!", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        Text("Hasil belajar " + (activeProfile?.name ?: "Siswa") + " — Kelas " + grade + " SD",
                            color = Blue, fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        Text("Terima kasih sudah berusaha. Ini hasil belajarmu hari ini.",
                            color = Muted, fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(20.dp))
                        Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(18.dp)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("NILAI AKHIR", color = Blue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(score.toString(), fontSize = 68.sp, fontWeight = FontWeight.ExtraBold,
                                    color = Blue, modifier = Modifier.testTag("resultScore"))
                            }
                        }
                        Spacer(Modifier.height(15.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ResultStat("✅", correct, "Jawaban Benar", Green,
                                Modifier.weight(1f).testTag("resultCorrect"))
                            ResultStat("❌", quiz.size - correct, "Jawaban Salah", Red,
                                Modifier.weight(1f).testTag("resultWrong"))
                        }
                        Spacer(Modifier.height(20.dp))
                        when (saveStatus) {
                            SaveStatus.SAVING -> Text(
                                "⏳ Menyimpan hasil latihan di HP...",
                                color = Muted, fontSize = 13.sp
                            )
                            SaveStatus.SAVED -> Text(
                                "✅ Hasil tersimpan otomatis di perangkat.",
                                color = Green, fontSize = 13.sp,
                                modifier = Modifier.testTag("history-saved")
                            )
                            SaveStatus.FAILED -> {
                                ErrorMessage("Hasil belum tersimpan: " + saveError)
                                Primary("COBA SIMPAN LAGI", "retry-save-history") {
                                    persistCompletedQuiz()
                                }
                            }
                            SaveStatus.NONE -> Unit
                        }
                        Primary("📊 LIHAT EVALUASI BELAJAR", "result-open-statistics",
                            saveStatus == SaveStatus.SAVED) {
                            statistics = null
                            page = Page.STATS
                        }
                        Primary("📩 KIRIM LAPORAN", "btnOpenReport") { reportOpen = true }
                        Secondary("ULANGI KUIS", "btnRetryQuiz") {
                            if (!busy) scope.launch {
                                busy = true
                                errorText = ""
                                try {
                                    quiz = EduyasaRepository.questions(grade, selections(), questionCount)
                                    resetQuiz()
                                } catch (e: Exception) {
                                    errorText = e.message ?: "Gagal menyiapkan kuis ulang."
                                } finally { busy = false }
                            }
                        }
                        if (errorText.isNotBlank()) ErrorMessage(errorText)
                        Secondary("⌂ KEMBALI KE AWAL", "btnBackToHome") { leaveOpen = true }
                        Text("Jangan lupa bagikan hasil belajarmu, ya! 🌟",
                            fontSize = 11.sp, color = Muted, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("EDUYASA © 2026 • Kurikulum Merdeka", fontSize = 11.sp,
            color = Muted, modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
    }

    if (editProfileOpen) StudentProfileEditor(
        existing = editingProfile,
        onDismiss = { editProfileOpen = false },
        onSave = { entry ->
            val updated = if (profiles.any { it.id == entry.id })
                profiles.map { if (it.id == entry.id) entry else it }
                else profiles + entry
            profileStore.save(updated)
            profiles = updated
            editProfileOpen = false
            editingProfile = null
        },
        onDelete = { entry ->
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        studyHistory.deleteProfileHistory(entry.id)
                    }
                    val updated = profiles.filterNot { it.id == entry.id }
                    profileStore.save(updated)
                    profiles = updated
                    if (activeProfileId == entry.id) {
                        activeProfileId = null
                        profileStore.setActiveProfileId(null)
                    }
                    profileError = ""
                } catch (e: Exception) {
                    profileError = "Gagal menghapus riwayat profil: " +
                        (e.message ?: "kesalahan penyimpanan")
                } finally {
                    editProfileOpen = false
                    editingProfile = null
                }
            }
        }
    )

    if (aboutOpen) AlertDialog(
        onDismissRequest = { aboutOpen = false },
        title = { Text("Tentang EDUYASA") },
        text = { Text("EDUYASA adalah platform belajar digital dari Yasa Tech / Yasa CCTV. Aplikasi ini membantu siswa belajar melalui materi dan kuis interaktif.") },
        confirmButton = { TextButton(onClick = { aboutOpen = false }) { Text("TUTUP") } }
    )
    if (leaveOpen) AlertDialog(
        onDismissRequest = { leaveOpen = false },
        title = { Text("Yakin mau kembali ke awal?") },
        text = { Text("Jika kembali sekarang, sesi belajar atau hasil kuis ini akan ditutup.") },
        confirmButton = {
            TextButton(onClick = { leaveOpen = false; reset() },
                modifier = Modifier.testTag("btnConfirmBackHome")) { Text("YA, KEMBALI KE AWAL") }
        },
        dismissButton = {
            TextButton(onClick = { leaveOpen = false },
                modifier = Modifier.testTag("btnCancelBackHome")) { Text("BATAL") }
        }
    )
    if (reportOpen) {
        var student by remember(activeProfileId) { mutableStateOf(activeProfile?.name.orEmpty()) }
        var phone by remember { mutableStateOf("") }
        var reportError by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { reportOpen = false },
            title = { Text("Kirim Laporan Belajar") },
            text = {
                Column {
                    Text("Bagikan hasil belajar ke orang tua atau guru.", color = Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(value = student, onValueChange = { student = it.take(60) },
                        label = { Text("Nama siswa") },
                        modifier = Modifier.fillMaxWidth().testTag("studentName"),
                        singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = phone, onValueChange = {
                        phone = it.filter(Char::isDigit).take(16)
                    }, label = { Text("Nomor WhatsApp (+62)") },
                        modifier = Modifier.fillMaxWidth().testTag("parentPhone"),
                        singleLine = true)
                    Text("Awali nomor dengan 08, misalnya 081234567890.",
                        fontSize = 11.sp, color = Muted)
                    if (reportError.isNotBlank()) ErrorMessage(reportError)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val normalized = normalizePhone(phone)
                    when {
                        student.trim().length < 2 -> reportError = "Nama siswa minimal 2 karakter."
                        normalized == null -> reportError = "Nomor WhatsApp Indonesia belum valid."
                        else -> {
                            val message = buildReport(student.trim(), grade, quiz, results)
                            val link = "https://wa.me/" + normalized + "?text=" +
                                URLEncoder.encode(message, "UTF-8")
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                                reportOpen = false
                                reset()
                            } catch (e: Exception) {
                                reportError = "Tidak dapat membuka WhatsApp atau browser."
                            }
                        }
                    }
                }, modifier = Modifier.testTag("btnSendReport")) { Text("BUKA WHATSAPP & KIRIM") }
            },
            dismissButton = {
                TextButton(onClick = { reportOpen = false }) { Text("KEMBALI KE HASIL") }
            }
        )
    }
    zoomImage?.let { url ->
        var scale by remember(url) { mutableFloatStateOf(1f) }
        AlertDialog(
            onDismissRequest = { zoomImage = null },
            title = { Text("Lihat gambar") },
            text = {
                Column {
                    EduRemoteImage(source = url, description = "Gambar diperbesar",
                        modifier = Modifier.fillMaxWidth().height(350.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { scale = (scale - .5f).coerceAtLeast(1f) }) { Text("−") }
                        OutlinedButton(onClick = { scale = (scale + .5f).coerceAtMost(3f) }) { Text("+") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { zoomImage = null }) { Text("TUTUP") } }
        )
    }
}

@Composable
private fun EduHeader(grade: Int, home: Boolean, connection: Boolean?) {
    Column(Modifier.fillMaxWidth().background(Blue)
        .padding(horizontal = 18.dp, vertical = 20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("EDUYASA", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
                Text(if (home) "BY YASA CCTV" else "Belajar Menyenangkan • Kelas " + grade + " SD",
                    color = Color.White.copy(alpha = .85f), fontSize = 11.sp)
            }
            if (!home) Text(
                when (connection) { true -> "● GitHub Terhubung"; false -> "● Belum Terhubung"; null -> "● Memeriksa GitHub" },
                fontSize = 10.sp, color = Color.White,
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = .17f)).padding(7.dp)
            )
        }
        if (!home) {
            Spacer(Modifier.height(21.dp))
            Text("Halo, Teman Belajar! 👋", color = Color.White, fontSize = 22.sp,
                fontWeight = FontWeight.Bold)
            Text("Siap belajar dan menguji kemampuanmu hari ini?",
                color = Color.White.copy(alpha = .9f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun StepIndicator(active: Int) {
    Row(Modifier.fillMaxWidth().padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf("MAPEL", "BAB", "MATERI", "KUIS").forEachIndexed { index, label ->
            val chosen = index + 1 == active
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(color = if (chosen) Blue else Line,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(26.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text((index + 1).toString(),
                            color = if (chosen) Color.White else Muted, fontSize = 12.sp)
                    }
                }
                Text(label, color = if (chosen) Blue else Muted,
                    fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SemesterSectionHeader(
    number: Int,
    evidence: SemesterEvidence,
    grade: Int,
    subjectId: String
) {
    val tint = if (number == 1) Color(0xFFEFF6FF) else Color(0xFFFFF1D6)
    val foreground = if (number == 1) Color(0xFF1D4ED8) else Color(0xFF92400E)
    Column(
        Modifier.fillMaxWidth()
            .padding(top = 9.dp, bottom = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(tint)
            .testTag("semester-" + grade + "-" + subjectId + "-" + number)
            .padding(horizontal = 13.dp, vertical = 11.dp)
    ) {
        Text("📚 SEMESTER " + number, fontSize = 14.sp,
            fontWeight = FontWeight.Bold, color = foreground)
        Text(
            if (evidence == SemesterEvidence.OFFICIAL) "Mengikuti acuan buku/panduan resmi"
            else "Pembagian sementara — menunggu pencocokan LKS",
            fontSize = 11.sp, color = foreground
        )
    }
}

@Composable private fun Title(text: String, centered: Boolean = false) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold,
        color = Color(0xFF172033), textAlign = if (centered) TextAlign.Center else TextAlign.Start,
        modifier = Modifier.fillMaxWidth().padding(bottom = 11.dp))
}
@Composable private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().padding(bottom = 15.dp)
        .clip(RoundedCornerShape(16.dp)).background(Color.White).padding(17.dp),
        content = content)
}
@Composable private fun Primary(
    label: String, tag: String, enabled: Boolean = true, onClick: () -> Unit
) {
    Button(onClick = onClick, enabled = enabled,
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp).heightIn(min = 52.dp).testTag(tag),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
@Composable private fun Secondary(label: String, tag: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()
        .padding(top = 10.dp).heightIn(min = 52.dp).testTag(tag),
        shape = RoundedCornerShape(12.dp), border = BorderStroke(2.dp, Line)) {
        Text(label, color = Color(0xFF172033), fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
@Composable private fun Actions(
    backTag: String, nextTag: String, nextEnabled: Boolean, nextText: String,
    onBack: () -> Unit, onNext: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.weight(1f)) { Secondary("KEMBALI", backTag, onBack) }
        Box(Modifier.weight(1f)) { Primary(nextText, nextTag, nextEnabled, onNext) }
    }
}
@Composable private fun ErrorMessage(message: String) {
    Text(message, color = Red, fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
}
@Composable private fun ResultStat(icon: String, number: Int, label: String,
                                   color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp))
        .background(Pale).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 22.sp)
        Text(number.toString(), fontSize = 23.sp, color = color, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 10.sp, color = Muted, textAlign = TextAlign.Center)
    }
}

private fun normalizePhone(raw: String): String? {
    val all = raw.filter(Char::isDigit)
    val local = when {
        all.startsWith("62") -> all.drop(2)
        all.startsWith("0") -> all.drop(1)
        else -> all
    }
    return if (Regex("^8[1-9][0-9]{7,11}$").matches(local)) "62" + local else null
}
private fun buildReport(
    name: String, grade: Int, quiz: List<EduQuestion>, results: Map<Int, Boolean>
): String {
    val correct = results.values.count { it }
    val score = if (quiz.isEmpty()) 0 else ((correct.toFloat() * 100 / quiz.size) + .5f).toInt()
    val lines = mutableListOf<String>()
    lines += "📚 *LAPORAN HASIL BELAJAR EDUYASA*"
    lines += ""
    lines += "👧 Nama siswa: " + name
    lines += "🏫 Kelas: " + grade + " SD"
    lines += ""
    lines += "📝 *RINCIAN HASIL KUIS*"
    val groups = quiz.indices.groupBy { quiz[it].subjectName to quiz[it].chapter }
    groups.forEach { (key, indexes) ->
        val benar = indexes.count { results[it] == true }
        val nilai = ((benar * 100f / indexes.size) + .5f).toInt()
        lines += "• " + key.first + " - Bab " + key.second + ": Nilai " + nilai +
            " (" + benar + " benar, " + (indexes.size - benar) + " salah dari " + indexes.size + " soal)"
    }
    lines += ""
    lines += "📊 *HASIL KESELURUHAN*"
    lines += "• Jumlah soal: " + quiz.size
    lines += "• Jawaban benar: " + correct
    lines += "• Jawaban salah: " + (quiz.size - correct)
    lines += "• Nilai akhir: " + score
    lines += ""
    lines += "🎉 Terus semangat belajar dan tingkatkan prestasimu!"
    lines += "Laporan dikirim melalui EDUYASA - Belajar Menyenangkan."
    return lines.joinToString("\n")
}
