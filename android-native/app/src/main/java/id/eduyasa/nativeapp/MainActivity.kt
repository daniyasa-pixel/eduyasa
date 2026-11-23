package id.eduyasa.nativeapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EduyasaApp() {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
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
    var soundEffectsEnabled by remember {
        mutableStateOf(preferences.getBoolean("sfx_enabled", true))
    }
    val soundChoices = remember {
        mutableStateMapOf<EduSfx, Boolean>().apply {
            EduSfx.values().forEach { put(it,
                preferences.getBoolean("sfx_" + it.name, true)) }
        }
    }
    val soundEffects = remember { EduSoundEffects() }
    DisposableEffect(soundEffects) { onDispose { soundEffects.close() } }
    fun playEffect(effect: EduSfx) {
        if (soundEffectsEnabled && soundChoices[effect] != false) soundEffects.play(effect)
    }
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
    var recentAchievements by remember {
        mutableStateOf<List<ChapterMilestone>>(emptyList())
    }
    var statistics by remember { mutableStateOf<LearningStatistics?>(null) }
    var statisticsLoading by remember { mutableStateOf(false) }
    var statisticsError by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf<Boolean?>(null) }
    var aboutOpen by remember { mutableStateOf(false) }
    var reportError by remember { mutableStateOf("") }
    var reportBusy by remember { mutableStateOf(false) }
    var leaveOpen by remember { mutableStateOf(false) }
    var zoomImage by remember { mutableStateOf<String?>(null) }
    val quizScroll = rememberScrollState()
    val quizPreloader = remember { QuizPreloadController(context.applicationContext) }
    var waitingForNext by remember { mutableStateOf(false) }
    DisposableEffect(quizPreloader) {
        onDispose { quizPreloader.reset() }
    }
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
        recentAchievements = emptyList()
        waitingForNext = false
        quizPreloader.reset()
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
        recentAchievements = emptyList()
        reportError = ""
        waitingForNext = false
        quizPreloader.reset()
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
                val (created, achieved) = withContext(Dispatchers.IO) {
                    val before = studyHistory.statistics(profileId)
                    val inserted = studyHistory.saveCompletedSession(snapshot)
                    val improvement = if (inserted)
                        findNewMilestones(before, studyHistory.statistics(profileId))
                    else emptyList()
                    inserted to improvement
                }
                recentAchievements = achieved
                saveStatus = SaveStatus.SAVED
                savedRevision++
                if (created) {
                    val effect = when {
                        achieved.any { it.levelUp } -> EduSfx.LEVEL
                        achieved.any { it.starUp } -> EduSfx.STAR
                        else -> EduSfx.FINISH
                    }
                    playEffect(effect)
                }
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
    LaunchedEffect(page, sessionId, qIndex) {
        // A question is only composed once its next-screen presentation text
        // has been prepared; preparation never runs on the main UI thread.
        audioEngine.stop()
        quizScroll.scrollTo(0)
        if (page == Page.QUIZ) {
            quizPreloader.prepareWindow(scope, quiz, qIndex)
        }
    }
    LaunchedEffect(page, sessionId, qIndex, waitingForNext) {
        if (page == Page.QUIZ && waitingForNext && qIndex < quiz.lastIndex) {
            val nextIndex = qIndex + 1
            // Observe readiness without making the entire app recompose whenever
            // background preparation finishes for an upcoming question.
            snapshotFlow {
                quizPreloader.ready[nextIndex]?.id == quiz[nextIndex].id
            }.first { it }
            waitingForNext = false
            qIndex = nextIndex
            selected = null
            checked = false
            options = quiz[nextIndex].options.shuffled()
        }
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
        // Keep the application header fixed. Only the content below can scroll.
        // This prevents the title/menu from moving up and down during study.
        Column(Modifier.fillMaxSize().background(Pale)) {
            EduHeader(
                grade = grade,
                home = page == Page.HOME,
                connection = connected,
                onOpenMenu = { drawerOpen = true }
            )
            Column(
                Modifier.weight(1f).fillMaxWidth()
                    .verticalScroll(quizScroll)
                    .testTag("page-scroll-content")
            ) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp)) {
            if (page !in setOf(Page.HOME, Page.RESULT, Page.STATS, Page.SETTINGS)) {
                StepIndicator(when (page) {
                    Page.SUBJECTS -> 1
                    Page.CHAPTERS -> 2
                    Page.MATERIAL -> 3
                    else -> 4
                })
            }
            when (page) {
                Page.HOME -> {
                    Text("⭐ Portal Belajar Interaktif", color = Blue,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text("Belajar Jadi Lebih Seru!", fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold, color = DarkBlue)
                    Spacer(Modifier.height(22.dp))
                    Text("Siapa yang belajar hari ini?", fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, color = DarkBlue)
                    Text("Pilih nama untuk melanjutkan belajar.",
                        color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(14.dp))

                    if (profiles.isEmpty()) {
                        Panel {
                            Text("Belum ada profil siswa.", fontWeight = FontWeight.Bold,
                                color = DarkBlue)
                            Text("Buat profil pertama untuk mulai belajar.",
                                color = Muted, fontSize = 13.sp)
                            Spacer(Modifier.height(8.dp))
                            Primary("BUAT PROFIL SISWA", "add-first-profile") {
                                editingProfile = null
                                editProfileOpen = true
                            }
                        }
                    } else {
                        profiles.forEach { profile ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .testTag("profile-" + profile.id)
                                    .combinedClickable(
                                        onClick = {
                                            activeProfileId = profile.id
                                            profileStore.setActiveProfileId(profile.id)
                                            grade = profile.grade
                                            statistics = null
                                            selectedSubjects.clear()
                                            chapters.clear()
                                            errorText = ""
                                            page = Page.SUBJECTS
                                        },
                                        onLongClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            editingProfile = profile
                                            editProfileOpen = true
                                        }
                                    )
                                    .padding(horizontal = 16.dp, vertical = 17.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎓", fontSize = 25.sp)
                                }
                                Spacer(Modifier.width(13.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(profile.name, fontWeight = FontWeight.Bold,
                                        color = DarkBlue, fontSize = 18.sp)
                                    Text("Kelas " + profile.grade + " SD",
                                        color = Muted, fontSize = 12.sp)
                                }
                                Text("›", color = Blue, fontSize = 28.sp)
                            }
                        }
                        if (profileError.isNotBlank()) ErrorMessage(profileError)
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End) {
                            TextButton(
                                onClick = {
                                    editingProfile = null
                                    editProfileOpen = true
                                },
                                modifier = Modifier.testTag("add-profile")
                            ) {
                                Text("＋ Tambah profil", color = Muted,
                                    fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("EDUYASA • Belajar sesuai kelasmu",
                        color = Muted, fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth())
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
                    val preparedQuestion = quizPreloader.ready[qIndex]
                    val preparationFailure = quizPreloader.errors[qIndex]
                    if (q != null && (preparedQuestion == null || preparedQuestion.id != q.id)) {
                        Panel {
                            Text("Soal " + (qIndex + 1) + " dari " + quiz.size,
                                fontWeight = FontWeight.Bold, color = DarkBlue)
                            Spacer(Modifier.height(15.dp))
                            if (preparationFailure != null) {
                                ErrorMessage(preparationFailure)
                                Primary("COBA LAGI", "quiz-preparation-retry") {
                                    quizPreloader.retry(scope, quiz, qIndex)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.testTag("quiz-current-loading")) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp), strokeWidth = 2.dp
                                    )
                                    Text("Menyiapkan soal...", color = Muted, fontSize = 13.sp)
                                }
                            }
                        }
                    } else if (q != null && preparedQuestion != null) {
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
                            EduAnswerText(q.text, modifier = Modifier.testTag("quizQText"),
                                sizeSp = 17f, prepared = preparedQuestion.prompt)
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
                                    },
                                    fixedHeight = 210.dp
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
                                QuizAnswerChoice(
                                    questionId = q.id,
                                    index = i,
                                    text = option,
                                    prepared = preparedQuestion.answerOptions[option],
                                    selected = !checked && selected == option,
                                    correct = checked && option == q.answer,
                                    wrong = checked && selected == option && option != q.answer,
                                    checked = checked,
                                    onSelect = { selected = option }
                                )
                            }
                            // Always reserve the same space. Showing the selected label
                            // must not push the next control down after the first tap.
                            Text(
                                when {
                                    checked -> "Jawaban sudah dikoreksi"
                                    selected == null -> "Ketuk satu pilihan jawaban"
                                    else -> "Pilihan kamu: " +
                                        ("ABCD".getOrNull(options.indexOf(selected)) ?: "?")
                                },
                                color = if (selected != null && !checked) Blue else Muted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .testTag("answer-selected-indicator")
                                    .height(24.dp)
                            )
                            AnimatedVisibility(
                                visible = checked,
                                enter = fadeIn(animationSpec = tween(220)) +
                                    expandVertically(animationSpec = tween(250))
                            ) {
                                Column {
                                    val correctLabel = "ABCD"
                                        .getOrNull(options.indexOf(q.answer)) ?: '?'
                                    Spacer(Modifier.height(10.dp))
                                    Panel {
                                        Text("Jawaban kamu: " +
                                            (if (selected == q.answer) "Benar ✅" else "Belum tepat ❌"),
                                            fontWeight = FontWeight.Bold)
                                        Text("Jawaban benar: " + correctLabel, color = Green)
                                        Spacer(Modifier.height(8.dp))
                                        Text("Pembahasan:", color = Blue,
                                            fontWeight = FontWeight.Bold)
                                        EduAnswerText(q.discussion, sizeSp = 14f,
                                            prepared = preparedQuestion.discussion)
                                    }
                                }
                            }
                            if (checked && waitingForNext) {
                                Spacer(Modifier.height(8.dp))
                                val nextFailure = quizPreloader.errors[qIndex + 1]
                                if (nextFailure != null) {
                                    ErrorMessage("Persiapan soal berikutnya gagal: " + nextFailure)
                                    Primary("COBA LAGI", "quiz-next-retry") {
                                        quizPreloader.retry(scope, quiz, qIndex + 1, qIndex)
                                    }
                                } else {
                                    Row(Modifier.fillMaxWidth()
                                            .testTag("quiz-next-loading")
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp), strokeWidth = 2.dp
                                        )
                                        Text("Menyiapkan soal berikutnya...",
                                            color = Muted, fontSize = 13.sp)
                                    }
                                }
                            }
                            Primary(
                                if (!checked) "KOREKSI JAWABAN"
                                else if (qIndex == quiz.lastIndex) "LIHAT HASIL" else "LANJUT",
                                "btnQuizAction",
                                (checked || selected != null) && !waitingForNext
                            ) {
                                if (!checked) {
                                    checked = true
                                    val isRight = selected == q.answer
                                    results[qIndex] = isRight
                                    answersChosen[qIndex] = selected.orEmpty()
                                    playEffect(if (isRight) EduSfx.CORRECT else EduSfx.WRONG)
                                } else if (qIndex == quiz.lastIndex) {
                                    page = Page.RESULT
                                    persistCompletedQuiz()
                                }
                                else {
                                    val nextIndex = qIndex + 1
                                    val nextReady = quizPreloader.ready[nextIndex]
                                    if (nextReady != null &&
                                        nextReady.id == quiz[nextIndex].id) {
                                        qIndex = nextIndex
                                        selected = null
                                        checked = false
                                        options = quiz[qIndex].options.shuffled()
                                    } else {
                                        waitingForNext = true
                                        quizPreloader.prepareWindow(scope, quiz, qIndex)
                                    }
                                }
                            }
                        }
                    }
                }
                Page.SETTINGS -> {
                    Title("⚙️ Pengaturan EDUYASA")
                    Panel {
                        Text("Nomor WhatsApp Laporan", fontWeight = FontWeight.Bold,
                            color = DarkBlue)
                        Text("Disimpan sekali untuk masing-masing siswa dan bisa diubah kapan saja.",
                            fontSize = 12.sp, color = Muted)
                        if (activeProfile == null) {
                            Text("Pilih profil siswa di Beranda terlebih dahulu.",
                                fontSize = 13.sp, color = Muted)
                        } else {
                            Spacer(Modifier.height(7.dp))
                            Text(activeProfile.name + " • " +
                                (toLocalIndonesiaWhatsapp(activeProfile.reportPhone)
                                    .ifBlank { "Belum diatur" }),
                                fontSize = 14.sp, color = DarkBlue)
                            TextButton(
                                onClick = {
                                    editingProfile = activeProfile
                                    editProfileOpen = true
                                },
                                modifier = Modifier.testTag("settings-report-phone")
                            ) { Text("UBAH NOMOR WA") }
                        }
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        Text("Kuis", fontWeight = FontWeight.Bold, color = DarkBlue)
                        Spacer(Modifier.height(8.dp))
                        Text("Jumlah soal bawaan", fontSize = 13.sp, color = Muted)
                        QuizLengthChooser(defaultQuestionCount) { amount ->
                            defaultQuestionCount = amount
                            questionCount = amount
                            preferences.edit().putInt("quiz_length", amount).apply()
                        }
                        Spacer(Modifier.height(15.dp))
                        HorizontalDivider()
                        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text("Audio Listening", fontWeight = FontWeight.SemiBold)
                                Text("Aktifkan suara pada soal yang memiliki audio.",
                                    color = Muted, fontSize = 12.sp)
                            }
                            Switch(checked = audioEnabled, onCheckedChange = { enabled ->
                                audioEnabled = enabled
                                if (!enabled) audioEngine.stop()
                                preferences.edit().putBoolean("listening_enabled", enabled).apply()
                            }, modifier = Modifier.testTag("setting-listening"))
                        }
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) {
                                Text("Efek Suara Game", fontWeight = FontWeight.Bold,
                                    color = DarkBlue)
                                Text("Terpisah dari Audio Listening. Suara pendek tanpa musik.",
                                    fontSize = 11.sp, color = Muted)
                            }
                            Switch(
                                checked = soundEffectsEnabled,
                                onCheckedChange = { enabled ->
                                    soundEffectsEnabled = enabled
                                    preferences.edit().putBoolean("sfx_enabled", enabled).apply()
                                },
                                modifier = Modifier.testTag("setting-sfx-master")
                            )
                        }
                        EduSfx.values().forEach { effect ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = soundChoices[effect] != false,
                                    enabled = soundEffectsEnabled,
                                    onCheckedChange = { enabled ->
                                        soundChoices[effect] = enabled
                                        preferences.edit()
                                            .putBoolean("sfx_" + effect.name, enabled).apply()
                                    },
                                    modifier = Modifier.testTag("setting-sfx-" + effect.name)
                                )
                                Column(Modifier.weight(1f)) {
                                    Text(effect.displayName, fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold)
                                    Text(effect.description, fontSize = 11.sp, color = Muted)
                                }
                                TextButton(onClick = { playEffect(effect) },
                                    enabled = soundEffectsEnabled && soundChoices[effect] != false) {
                                    Text("▶", fontSize = 19.sp)
                                }
                            }
                        }
                        HorizontalDivider()
                        Spacer(Modifier.height(12.dp))
                        Text("Data dan tampilan", fontWeight = FontWeight.Bold, color = DarkBlue)
                        Text("Riwayat dan perkembangan tersimpan khusus di HP ini. " +
                            "Ukuran teks, tema, serta cadangan data belum tersedia pada versi ini.",
                            fontSize = 12.sp, color = Muted, lineHeight = 19.sp)
                    }
                    Secondary("KEMBALI", "settings-back") { back() }
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
                        Text(
                            if (saveStatus == SaveStatus.SAVED && recentAchievements.isNotEmpty())
                                "🎉" else "📘",
                            fontSize = 46.sp, modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
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
                        if (saveStatus == SaveStatus.SAVED && recentAchievements.isNotEmpty()) {
                            AchievementCelebration(recentAchievements, catalog)
                            Spacer(Modifier.height(14.dp))
                        }
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
                        Primary(
                            "📩 KIRIM LAPORAN", "btnOpenReport",
                            saveStatus == SaveStatus.SAVED && !reportBusy
                        ) {
                            val profile = activeProfile
                            val number = profile?.let {
                                normalizeIndonesiaWhatsapp(it.reportPhone)
                            }
                            when {
                                profile == null ->
                                    reportError = "Pilih profil siswa sebelum mengirim laporan."
                                number == null -> {
                                    reportError = "Atur nomor WhatsApp laporan pada profil siswa dahulu."
                                    editingProfile = profile
                                    editProfileOpen = true
                                }
                                else -> {
                                    reportError = ""
                                    reportBusy = true
                                    val capturedQuiz = quiz.toList()
                                    val capturedResults = results.toMap()
                                    val capturedGrade = grade
                                    scope.launch {
                                        try {
                                            val latest = withContext(Dispatchers.IO) {
                                                studyHistory.statistics(profile.id)
                                            }
                                            val message = buildReport(
                                                profile.name, capturedGrade, capturedQuiz,
                                                capturedResults, latest, catalog
                                            )
                                            val link = "https://wa.me/" + number + "?text=" +
                                                URLEncoder.encode(message, "UTF-8")
                                            context.startActivity(
                                                Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                            )
                                        } catch (e: Exception) {
                                            reportError = "Tidak dapat membuka WhatsApp atau " +
                                                "menyiapkan laporan: " +
                                                (e.message ?: "coba lagi.")
                                        } finally {
                                            reportBusy = false
                                        }
                                    }
                                }
                            }
                        }
                        if (reportBusy) Text("Menyiapkan laporan...", fontSize = 12.sp,
                            color = Muted)
                        if (reportError.isNotBlank()) ErrorMessage(reportError)
                        Text("WhatsApp akan dibuka dengan laporan siap dikirim. " +
                            "Tekan Kirim di WhatsApp untuk menyampaikannya.",
                            fontSize = 11.sp, color = Muted)
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
            } // Scrollable body
        } // Fixed header container

    AnimatedVisibility(
        visible = drawerOpen,
        enter = fadeIn(animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(180))
    ) {
        Box(Modifier.fillMaxSize()
            .background(Color.Black.copy(alpha = .35f))
            .clickable { drawerOpen = false }
            .testTag("drawer-scrim"))
    }
    AnimatedVisibility(
        visible = drawerOpen,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it }),
        modifier = Modifier.align(Alignment.CenterEnd)
    ) {
        Column(
            Modifier.fillMaxHeight().width(306.dp)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
                .testTag("right-navigation-drawer")
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Menu EDUYASA", fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold, color = DarkBlue)
                TextButton(onClick = { drawerOpen = false }) { Text("✕") }
            }
            Text(activeProfile?.name ?: "Belum memilih siswa",
                fontWeight = FontWeight.Bold, color = Blue)
            Text(if (activeProfile == null) "Pilih profil untuk belajar"
                else "Kelas " + activeProfile.grade + " SD", color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(18.dp))
            listOf(
                Triple("⌂", "Beranda", Page.HOME),
                Triple("📖", "Mulai Belajar", Page.SUBJECTS),
                Triple("📊", "Evaluasi Belajar", Page.STATS),
                Triple("👥", "Kelola Profil Siswa", Page.HOME),
                Triple("⚙", "Pengaturan", Page.SETTINGS)
            ).forEach { (symbol, label, destination) ->
                TextButton(
                    onClick = { goTo(destination) },
                    modifier = Modifier.fillMaxWidth().testTag("drawer-" + destination.name + "-" + label)
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(symbol, fontSize = 19.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(label, color = DarkBlue, fontSize = 14.sp)
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
            TextButton(onClick = {
                drawerOpen = false
                aboutOpen = true
            }, modifier = Modifier.fillMaxWidth()) {
                Text("ℹ  Tentang EDUYASA", color = DarkBlue)
            }
            Text("Riwayat Latihan ada di Evaluasi Belajar (10 sesi terakhir).",
                color = Muted, fontSize = 11.sp, lineHeight = 17.sp,
                modifier = Modifier.padding(top = 18.dp))
        }
    }
    } // End root layout box

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
        onDismissRequest = {
            leaveOpen = false
            pendingDestination = null
        },
        title = { Text("Yakin mau kembali ke awal?") },
        text = { Text("Jika kembali sekarang, sesi belajar atau hasil kuis ini akan ditutup.") },
        confirmButton = {
            TextButton(onClick = {
                val destination = pendingDestination
                leaveOpen = false
                pendingDestination = null
                reset()
                if (destination != null) goTo(destination)
            },
                modifier = Modifier.testTag("btnConfirmBackHome")) { Text("YA, KEMBALI KE AWAL") }
        },
        dismissButton = {
            TextButton(onClick = {
                leaveOpen = false
                pendingDestination = null
            }, modifier = Modifier.testTag("btnCancelBackHome")) { Text("BATAL") }
        }
    )
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
private fun EduHeader(
    grade: Int, home: Boolean, connection: Boolean?, onOpenMenu: () -> Unit
) {
    Column(Modifier.fillMaxWidth().background(Blue)
        .padding(horizontal = 18.dp, vertical = 20.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("EDUYASA", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
                Text(if (home) "BY YASA CCTV" else "Belajar Menyenangkan • Kelas " + grade + " SD",
                    color = Color.White.copy(alpha = .85f), fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!home) Text(
                    when (connection) { true -> "● GitHub Terhubung"; false -> "● Belum Terhubung"; null -> "● Memeriksa GitHub" },
                    fontSize = 10.sp, color = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = .17f)).padding(7.dp)
                )
                TextButton(onClick = onOpenMenu, modifier = Modifier.testTag("open-right-drawer")) {
                    Text("☰", color = Color.White, fontSize = 29.sp)
                }
            }
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
private fun QuizLengthChooser(value: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 9.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LearningGamification.QUIZ_LENGTHS.forEach { amount ->
            val chosen = amount == value
            OutlinedButton(
                onClick = { onSelect(amount) },
                modifier = Modifier.weight(1f).testTag("quiz-length-" + amount),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (chosen) Color(0xFFEFF6FF) else Color.White
                ),
                border = BorderStroke(if (chosen) 2.dp else 1.dp, if (chosen) Blue else Line),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Text(amount.toString() + " Soal", fontSize = 12.sp,
                    fontWeight = if (chosen) FontWeight.Bold else FontWeight.Normal,
                    color = if (chosen) Blue else Muted)
            }
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

private fun buildReport(
    name: String, grade: Int, quiz: List<EduQuestion>, results: Map<Int, Boolean>,
    statistics: LearningStatistics, catalog: Map<Int, List<EduSubject>>
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
    lines += "📈 *PERKEMBANGAN BELAJAR TERSIMPAN*"
    lines += "• Total sesi latihan: " + statistics.totalSessions
    lines += "• Total soal dijawab: " + statistics.totalQuestions
    lines += "• Ketepatan seluruh latihan: " + statistics.overallAccuracy + "%"
    val subjectProgress = statistics.subjects.filter { it.grade == grade }
    subjectProgress.forEach { progress ->
        val subject = catalog[grade]?.firstOrNull { it.id == progress.subjectId }
        val chapters = statistics.chapters.filter {
            it.grade == grade && it.subjectId == progress.subjectId
        }
        val summary = LearningGamification.summary(chapters)
        if (summary != null) {
            val totalChapters = subject?.chapters?.size ?: summary.assessedChapters
            val provisional = summary.provisional ||
                summary.assessedChapters < totalChapters
            lines += "• " + (subject?.name ?: progress.subjectId) + ": " +
                summary.accuracy + "% ketepatan latihan, " +
                summary.assessedChapters + "/" + totalChapters + " bab dicoba, " +
                summary.stars + "/5 bintang" +
                if (provisional) " (sementara)" else ""
        }
    }
    val practiced = quiz.map { it.subjectId to it.chapter }.distinct()
    if (practiced.isNotEmpty()) {
        lines += ""
        lines += "🏅 *LEVEL DAN BINTANG BAB YANG BARU DIKERJAKAN*"
        practiced.forEach { (subjectId, chapter) ->
            val progress = statistics.chapters.firstOrNull {
                it.grade == grade && it.subjectId == subjectId && it.chapter == chapter
            }
            if (progress != null) {
                val name = catalog[grade]?.firstOrNull { it.id == subjectId }?.name ?: subjectId
                lines += "• " + name + " Bab " + chapter + ": Level " +
                    progress.level + ", " + progress.stars + "/5 bintang" +
                    " (" + progress.recentAccuracy + "% ketepatan terbaru)" +
                    if (progress.isProvisional) " (sementara)" else ""
            }
        }
    }
    lines += ""
    lines += "Catatan: Persentase menunjukkan ketepatan pada soal yang sudah dikerjakan, " +
        "bukan persentase penguasaan seluruh materi. Bab yang belum dikerjakan " +
        "tidak dihitung sebagai nilai nol."
    lines += ""
    lines += "🎉 Terus semangat belajar dan tingkatkan prestasimu!"
    lines += "Laporan dikirim melalui EDUYASA - Belajar Menyenangkan."
    return lines.joinToString("\n")
}
