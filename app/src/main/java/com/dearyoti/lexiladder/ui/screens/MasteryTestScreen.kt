package com.dearyoti.lexiladder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.core.localization.UiLocalizer
import com.dearyoti.lexiladder.data.preferences.UserPreferences
import com.dearyoti.lexiladder.domain.model.LevelStatus
import com.dearyoti.lexiladder.domain.model.MasteryTestAttempt
import com.dearyoti.lexiladder.domain.model.MasteryTestQuestion
import com.dearyoti.lexiladder.domain.model.MasteryTestResult
import com.dearyoti.lexiladder.domain.model.QuestionType
import com.dearyoti.lexiladder.domain.service.MasteryTestEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private enum class TestStage {
    INTRO,
    IN_TEST,
    RESULT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasteryTestScreen(
    levelId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repo = LexiLadderApp.instance.vocabularyRepository
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val prefs by prefsRepo.userPreferencesFlow.collectAsState(initial = UserPreferences())
    val uiLang = prefs.uiLanguage

    val scope = rememberCoroutineScope()

    val levels by repo.getAllLevels().collectAsState(initial = emptyList())
    val words by repo.getWordsByLevel(levelId).collectAsState(initial = emptyList())
    val testAttempts by repo.getTestAttempts(levelId).collectAsState(initial = emptyList())

    val currentLevel = levels.firstOrNull { it.id == levelId }

    var stage by remember { mutableStateOf(TestStage.INTRO) }
    var questions by remember { mutableStateOf<List<MasteryTestQuestion>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<String, String>() }
    var testResult by remember { mutableStateOf<MasteryTestResult?>(null) }
    var nextLevelUnlocked by remember { mutableStateOf(false) }

    fun startNewTest() {
        val targetCount = MasteryTestEngine.DEFAULT_QUESTION_COUNT
        val count = if (words.size >= targetCount) targetCount else words.size.coerceAtLeast(3)
        questions = MasteryTestEngine.generateQuestions(
            words = words,
            questionCount = count
        )
        currentIndex = 0
        userAnswers.clear()
        testResult = null
        nextLevelUnlocked = false
        stage = TestStage.IN_TEST
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentLevel?.title ?: "Mastery Test",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (stage == TestStage.IN_TEST) {
                            stage = TestStage.INTRO
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (stage) {
                TestStage.INTRO -> {
                    MasteryTestIntroView(
                        level = currentLevel,
                        wordsCount = words.size,
                        attempts = testAttempts,
                        uiLanguage = uiLang,
                        onStartTest = { startNewTest() }
                    )
                }
                TestStage.IN_TEST -> {
                    if (questions.isNotEmpty()) {
                        MasteryTestActiveView(
                            questions = questions,
                            currentIndex = currentIndex,
                            userAnswers = userAnswers,
                            uiLanguage = uiLang,
                            onSelectAnswer = { qId, ans -> userAnswers[qId] = ans },
                            onNext = { if (currentIndex < questions.size - 1) currentIndex++ },
                            onPrevious = { if (currentIndex > 0) currentIndex-- },
                            onSubmit = {
                                val result = MasteryTestEngine.evaluateTest(
                                    questions = questions,
                                    answers = userAnswers,
                                    passingPercentage = currentLevel?.requiredPassingScore ?: 80
                                )
                                testResult = result

                                val attempt = MasteryTestAttempt(
                                    id = UUID.randomUUID().toString(),
                                    levelId = levelId,
                                    scorePercentage = result.scorePercentage,
                                    totalQuestions = result.totalQuestions,
                                    correctCount = result.correctCount,
                                    passingPercentage = result.passingPercentage,
                                    isPassed = result.isPassed
                                )

                                scope.launch {
                                    val unlocked = repo.submitMasteryTest(attempt)
                                    nextLevelUnlocked = unlocked
                                    stage = TestStage.RESULT
                                }
                            }
                        )
                    }
                }
                TestStage.RESULT -> {
                    testResult?.let { res ->
                        MasteryTestResultView(
                            result = res,
                            nextLevelUnlocked = nextLevelUnlocked,
                            uiLanguage = uiLang,
                            onRetakeTest = { startNewTest() },
                            onDone = { onNavigateBack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MasteryTestIntroView(
    level: com.dearyoti.lexiladder.domain.model.Level?,
    wordsCount: Int,
    attempts: List<MasteryTestAttempt>,
    uiLanguage: String = "en",
    onStartTest: () -> Unit
) {
    val isLocked = level?.isUnlocked != true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (level?.isMastered == true)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = if (isLocked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = level?.title ?: "Mastery Test",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = level?.description ?: "Demonstrate your vocabulary mastery to unlock the next level.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                StatusChip(status = level?.status ?: LevelStatus.LOCKED, uiLanguage = uiLanguage)
            }
        }

        // Test Rules Card
        val isId = uiLanguage == "id"
        val targetCount = MasteryTestEngine.DEFAULT_QUESTION_COUNT
        val actualCount = if (wordsCount >= targetCount) targetCount else wordsCount.coerceAtLeast(3)
        val passingScore = level?.requiredPassingScore ?: MasteryTestEngine.DEFAULT_PASSING_PERCENTAGE
        val minCorrect = kotlin.math.ceil(actualCount * (passingScore / 100.0)).toInt()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = UiLocalizer.getString("test_specs_title", uiLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                RuleItem(
                    label = UiLocalizer.getString("test_specs_questions", uiLanguage),
                    value = if (isId) "$actualCount Soal Teracak (Komprehensif)" else "$actualCount Randomized Questions (Comprehensive)"
                )
                RuleItem(
                    label = UiLocalizer.getString("test_specs_passing", uiLanguage),
                    value = if (isId) "$passingScore% (Minimal $minCorrect dari $actualCount benar)" else "$passingScore% (Minimum $minCorrect of $actualCount correct)"
                )
                RuleItem(
                    label = UiLocalizer.getString("test_specs_format", uiLanguage),
                    value = UiLocalizer.getString("test_specs_format_val", uiLanguage)
                )
                RuleItem(
                    label = if (uiLanguage == "id") "Prasyarat" else "Prerequisite",
                    value = if (isLocked)
                        (if (uiLanguage == "id") "Selesaikan level sebelumnya" else "Prerequisite level must be completed")
                    else
                        (if (uiLanguage == "id") "Siap diikuti" else "Ready to take")
                )
                RuleItem(
                    label = UiLocalizer.getString("test_specs_outcome", uiLanguage),
                    value = UiLocalizer.getString("test_specs_outcome_val", uiLanguage)
                )
            }
        }

        // Action Button
        Button(
            onClick = onStartTest,
            enabled = !isLocked,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = when {
                    isLocked -> if (uiLanguage == "id") "Level Terkunci" else "Level Locked"
                    level?.isMastered == true -> UiLocalizer.getString("test_retake_btn", uiLanguage)
                    else -> UiLocalizer.getString("test_start_btn", uiLanguage)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Previous Attempts History
        if (attempts.isNotEmpty()) {
            Text(
                text = UiLocalizer.getString("test_prev_attempts", uiLanguage, attempts.size),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

            attempts.forEach { attempt ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (attempt.isPassed)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (attempt.isPassed)
                                    UiLocalizer.getString("test_passed_badge", uiLanguage)
                                else
                                    UiLocalizer.getString("test_failed_badge", uiLanguage),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (attempt.isPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = dateFormat.format(Date(attempt.timestamp)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${attempt.scorePercentage}% (${attempt.correctCount}/${attempt.totalQuestions})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: LevelStatus, uiLanguage: String = "en") {
    val isId = uiLanguage == "id"
    val (label, bg, textCol) = when (status) {
        LevelStatus.LOCKED -> Triple(if (isId) "TERKUNCI 🔒" else "LOCKED 🔒", Color(0xFFEEEEEE), Color(0xFF757575))
        LevelStatus.AVAILABLE -> Triple(if (isId) "TERSEDIA 🔓" else "AVAILABLE 🔓", Color(0xFFE3F2FD), Color(0xFF1976D2))
        LevelStatus.IN_PROGRESS -> Triple(if (isId) "BELAJAR ⏳" else "IN PROGRESS ⏳", Color(0xFFFFF3E0), Color(0xFFE65100))
        LevelStatus.READY_FOR_TEST -> Triple(if (isId) "SIAP UJIAN 🎯" else "READY FOR TEST 🎯", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        LevelStatus.MASTERED -> Triple(if (isId) "DIKUASAI 🏆" else "MASTERED 🏆", Color(0xFFEDE7F6), Color(0xFF512DA8))
    }

    Box(
        modifier = Modifier
            .background(color = bg, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textCol
        )
    }
}

@Composable
private fun RuleItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MasteryTestActiveView(
    questions: List<MasteryTestQuestion>,
    currentIndex: Int,
    userAnswers: Map<String, String>,
    uiLanguage: String = "en",
    onSelectAnswer: (String, String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSubmit: () -> Unit
) {
    val currentQuestion = questions[currentIndex]
    val selectedAnswer = userAnswers[currentQuestion.id]
    val progress = (currentIndex + 1).toFloat() / questions.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = UiLocalizer.getString("practice_question_counter", uiLanguage, currentIndex + 1, questions.size),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            val answeredCount = userAnswers.size
            Text(
                text = if (uiLanguage == "id") "$answeredCount dijawab" else "$answeredCount answered",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        // Question Type Tag
        val typeBadge = when (currentQuestion.type) {
            QuestionType.WORD_TO_TRANSLATION -> if (uiLanguage == "id") "Arti Kosakata" else "Vocabulary Meaning"
            QuestionType.TRANSLATION_TO_WORD -> if (uiLanguage == "id") "Pemilihan Kata Bahasa Inggris" else "English Word Selection"
            QuestionType.FILL_IN_BLANK -> if (uiLanguage == "id") "Melengkapi Kalimat" else "Sentence Context Completion"
        }
        SuggestionChip(
            onClick = {},
            label = { Text(typeBadge, style = MaterialTheme.typography.labelSmall) }
        )

        // Question Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                currentQuestion.promptDetail?.let { prompt ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Multiple Choice Options
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentQuestion.options.forEach { option ->
                val isSelected = selectedAnswer == option
                val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectAnswer(currentQuestion.id, option) }
                        .border(width = if (isSelected) 2.dp else 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .border(width = 2.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = currentIndex > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (uiLanguage == "id") "Sebelumnya" else "Previous")
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (currentIndex < questions.size - 1) {
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (uiLanguage == "id") "Berikutnya" else "Next")
                }
            } else {
                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (uiLanguage == "id") "Kirim Jawaban" else "Submit Test")
                }
            }
        }
    }
}

@Composable
private fun MasteryTestResultView(
    result: MasteryTestResult,
    nextLevelUnlocked: Boolean,
    uiLanguage: String = "en",
    onRetakeTest: () -> Unit,
    onDone: () -> Unit
) {
    val isId = uiLanguage == "id"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Result Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (result.isPassed)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (result.isPassed) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = if (result.isPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (result.isPassed)
                        (if (isId) "Tingkat Dikuasai! 🎉" else "Level Mastered! 🎉")
                    else
                        (if (isId) "Terus Berlatih! 💪" else "Keep Practicing! 💪"),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${result.scorePercentage}%",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (result.isPassed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Text(
                    text = if (isId)
                        "${result.correctCount} dari ${result.totalQuestions} soal terjawab benar"
                    else
                        "${result.correctCount} of ${result.totalQuestions} questions correct",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (result.isPassed)
                        (if (isId) "Selamat! Anda melampaui batas kelulusan ${result.passingPercentage}%." else "Congratulations! You exceeded the ${result.passingPercentage}% requirement.")
                    else
                        (if (isId) "Nilai Anda ${result.scorePercentage}%. Batas kelulusan adalah ${result.passingPercentage}%." else "You scored ${result.scorePercentage}%. Passing score requires ${result.passingPercentage}%."),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Unlocked Next Level Banner
        if (nextLevelUnlocked) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isId) "Level Berikutnya Terbuka! 🚀" else "Next Level Unlocked! 🚀",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = if (isId) "Level berikutnya pada tangga belajar Anda kini siap dipelajari." else "The next level in your ladder is now available to learn.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }

        // Review Missed Questions
        if (result.incorrectQuestions.isNotEmpty()) {
            Text(
                text = if (isId) "Soal yang Perlu Ditinjau (${result.incorrectQuestions.size})" else "Questions to Review (${result.incorrectQuestions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            result.incorrectQuestions.forEach { q ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = q.targetWord.text,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = (if (isId) "Jawaban Benar: " else "Correct Answer: ") + q.correctAnswer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        q.targetWord.translation?.let {
                            Text(text = "${if (isId) "Arti" else "Meaning"}: $it", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = "\"${q.targetWord.exampleSentence}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action Buttons
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isId) "Kembali ke Peta Level" else "Back to Level Roadmap",
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = onRetakeTest,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isId) "Ulangi Ujian Level" else "Retake Mastery Test",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
