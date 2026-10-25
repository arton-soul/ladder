package com.dearyoti.lexiladder.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.core.ads.AdBannerView
import com.dearyoti.lexiladder.core.localization.UiLocalizer
import com.dearyoti.lexiladder.data.preferences.UserPreferences
import com.dearyoti.lexiladder.domain.model.QuizQuestion
import com.dearyoti.lexiladder.domain.model.ReviewRating
import com.dearyoti.lexiladder.domain.model.Word
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class PracticeMode {
    ALL,
    DUE_REVIEW,
    DIFFICULT
}

@Composable
fun PracticeScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val repo = LexiLadderApp.instance.vocabularyRepository
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val adManager = LexiLadderApp.instance.adManager

    val prefs by prefsRepo.userPreferencesFlow.collectAsState(initial = UserPreferences())
    val uiLang = prefs.uiLanguage
    val expLang = prefs.explanationLanguage

    val levels by repo.getAllLevels().collectAsState(initial = emptyList())
    val allWords by repo.getAllWords().collectAsState(initial = emptyList())
    val dueWords by repo.getWordsDueForReview().collectAsState(initial = emptyList())
    val difficultWords by repo.getDifficultWords().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val unlockedLevels = remember(levels) { levels.filter { it.isUnlocked } }
    val defaultLevelId = remember(prefs.selectedLevelId, unlockedLevels) {
        if (unlockedLevels.any { it.id == prefs.selectedLevelId }) prefs.selectedLevelId
        else unlockedLevels.firstOrNull()?.id ?: "level_a1"
    }
    var selectedLevelId by remember(defaultLevelId) { mutableStateOf(defaultLevelId) }
    var selectedMode by remember { mutableStateOf(PracticeMode.ALL) }
    var sessionRound by remember { mutableIntStateOf(0) }

    // Words filtered by selected level (or all unlocked levels)
    val levelFilteredWords = remember(allWords, selectedLevelId, unlockedLevels) {
        if (selectedLevelId == "all") {
            val unlockedIds = unlockedLevels.map { it.id }.toSet()
            if (unlockedIds.isNotEmpty()) allWords.filter { unlockedIds.contains(it.levelId) } else allWords
        } else {
            allWords.filter { it.levelId == selectedLevelId }
        }
    }

    // Active words based on selected mode
    val activeWordPool = remember(selectedMode, levelFilteredWords, dueWords, difficultWords) {
        when (selectedMode) {
            PracticeMode.ALL -> levelFilteredWords
            PracticeMode.DUE_REVIEW -> {
                val dueIds = dueWords.map { it.id }.toSet()
                levelFilteredWords.filter { dueIds.contains(it.id) }
            }
            PracticeMode.DIFFICULT -> {
                val diffIds = difficultWords.map { it.id }.toSet()
                levelFilteredWords.filter { diffIds.contains(it.id) }
            }
        }
    }

    // Generate dynamic quiz questions with fresh randomization every sessionRound
    val questions = remember(activeWordPool, levelFilteredWords, selectedMode, sessionRound, expLang, uiLang) {
        val targetWords = if (activeWordPool.size >= 4) activeWordPool else levelFilteredWords
        val fallbackPool = if (levelFilteredWords.size >= 4) levelFilteredWords else allWords
        generateQuizFromWords(
            words = targetWords,
            fallbackPool = fallbackPool,
            explanationLanguage = expLang,
            uiLanguage = uiLang
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var sessionCorrectCount by remember { mutableIntStateOf(0) }
    var isSessionComplete by remember { mutableStateOf(false) }

    if (allWords.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (uiLang == "id") "Memuat soal latihan..." else "Loading practice questions...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    if (isSessionComplete) {
        QuizResultView(
            totalQuestions = questions.size,
            correctCount = sessionCorrectCount,
            isPremium = prefs.isPremium,
            uiLanguage = uiLang,
            onRestart = {
                val restartAction = {
                    sessionRound++
                    currentIndex = 0
                    selectedOption = null
                    isSubmitted = false
                    sessionCorrectCount = 0
                    isSessionComplete = false
                }
                if (activity != null) {
                    adManager.showInterstitialAd(
                        activity = activity,
                        isPremium = prefs.isPremium,
                        onDismiss = restartAction
                    )
                } else {
                    restartAction()
                }
            }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = UiLocalizer.getString("practice_title", uiLang),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Level Selection Chips (when multiple levels are unlocked)
        if (unlockedLevels.size > 1) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedLevelId == "all",
                        onClick = {
                            selectedLevelId = "all"
                            currentIndex = 0
                            selectedOption = null
                            isSubmitted = false
                            sessionCorrectCount = 0
                            isSessionComplete = false
                        },
                        label = { Text(UiLocalizer.getString("practice_all_levels", uiLang)) }
                    )
                }
                items(unlockedLevels) { lvl ->
                    FilterChip(
                        selected = selectedLevelId == lvl.id,
                        onClick = {
                            selectedLevelId = lvl.id
                            scope.launch {
                                prefsRepo.updateSelectedLevel(lvl.id)
                            }
                            currentIndex = 0
                            selectedOption = null
                            isSubmitted = false
                            sessionCorrectCount = 0
                            isSessionComplete = false
                        },
                        label = { Text(lvl.title.split(":").firstOrNull() ?: lvl.title) }
                    )
                }
            }
        }

        // Practice Mode Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val modes = listOf(
                PracticeMode.ALL to UiLocalizer.getString("practice_standard_quiz", uiLang, levelFilteredWords.size),
                PracticeMode.DUE_REVIEW to UiLocalizer.getString("practice_due_review", uiLang, dueWords.size),
                PracticeMode.DIFFICULT to UiLocalizer.getString("practice_difficult_words", uiLang, difficultWords.size)
            )
            items(modes) { (mode, label) ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = {
                        selectedMode = mode
                        currentIndex = 0
                        selectedOption = null
                        isSubmitted = false
                        sessionCorrectCount = 0
                        isSessionComplete = false
                    },
                    label = { Text(label) }
                )
            }
        }

        if (selectedMode == PracticeMode.DUE_REVIEW && dueWords.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (uiLang == "id") "Semua Sudah Diulang! 🎉" else "All Caught Up! 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiLang == "id")
                            "Tidak ada kata yang jatuh tempo untuk diulang saat ini. Pertahankan konsistensi jadwal Spaced Repetition Anda!"
                        else
                            "You have no words scheduled for review right now. Great job keeping up with your spaced repetition schedule!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { selectedMode = PracticeMode.ALL }) {
                        Text(if (uiLang == "id") "Latihan Semua Kata" else "Practice All Words")
                    }
                }
            }
            return
        }

        if (selectedMode == PracticeMode.DIFFICULT && difficultWords.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (uiLang == "id") "Belum Ada Kata Sulit! 🌟" else "No Difficult Words Yet! 🌟",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiLang == "id")
                            "Kata yang salah dijawab selama kuis akan otomatis tercatat di sini untuk penguatan intensif."
                        else
                            "Words that you miss during practice will automatically appear here for focused reinforcement.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { selectedMode = PracticeMode.ALL }) {
                        Text(if (uiLang == "id") "Latihan Semua Kata" else "Practice All Words")
                    }
                }
            }
            return
        }

        if (questions.isEmpty()) {
            Text(
                if (uiLang == "id") "Tidak ada pertanyaan untuk kategori ini." else "No questions available for this category.",
                style = MaterialTheme.typography.bodyMedium
            )
            return
        }

        val currentQuestion = questions[currentIndex]

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = UiLocalizer.getString("practice_question_counter", uiLang, currentIndex + 1, questions.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = UiLocalizer.getString("practice_score", uiLang, sessionCorrectCount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                val targetWord = allWords.firstOrNull { it.id == currentQuestion.wordId }
                if (targetWord != null) {
                    val tts = LexiLadderApp.instance.ttsManager
                    IconButton(
                        onClick = { tts.speak(targetWord.text) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen to pronunciation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        currentQuestion.options.forEachIndexed { index, optionText ->
            val isSelected = selectedOption == index
            val isCorrect = isSubmitted && index == currentQuestion.correctOptionIndex
            val isWrongSelected = isSubmitted && isSelected && index != currentQuestion.correctOptionIndex

            val containerColor = when {
                isCorrect -> MaterialTheme.colorScheme.primaryContainer
                isWrongSelected -> MaterialTheme.colorScheme.errorContainer
                isSelected -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surface
            }

            OutlinedButton(
                onClick = {
                    if (!isSubmitted) {
                        selectedOption = index
                        isSubmitted = true
                        val correct = index == currentQuestion.correctOptionIndex
                        if (correct) {
                            sessionCorrectCount++
                        }
                        scope.launch {
                            val rating = if (correct) ReviewRating.GOOD else ReviewRating.HARD
                            repo.recordReviewRating(currentQuestion.wordId, rating)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor)
            ) {
                Text(
                    text = optionText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        if (isSubmitted) {
            val isAnswerCorrect = selectedOption == currentQuestion.correctOptionIndex
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAnswerCorrect)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isAnswerCorrect)
                            (if (uiLang == "id") "Benar! 🎉" else "Correct! 🎉")
                        else
                            (if (uiLang == "id") "Belum Tepat 💡" else "Incorrect 💡"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQuestion.explanation,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                                selectedOption = null
                                isSubmitted = false
                            } else {
                                isSessionComplete = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (currentIndex < questions.size - 1)
                                (if (uiLang == "id") "Pertanyaan Berikutnya" else "Next Question")
                            else
                                (if (uiLang == "id") "Lihat Hasil" else "See Results")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizResultView(
    totalQuestions: Int,
    correctCount: Int,
    isPremium: Boolean,
    uiLanguage: String = "en",
    onRestart: () -> Unit
) {
    val accuracy = if (totalQuestions > 0) (correctCount.toFloat() / totalQuestions.toFloat()) * 100f else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = UiLocalizer.getString("practice_complete_title", uiLanguage) + " 🏆",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (accuracy >= 70f)
                UiLocalizer.getString("practice_great_job", uiLanguage)
            else
                UiLocalizer.getString("practice_keep_going", uiLanguage),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${UiLocalizer.getString("practice_score", uiLanguage, correctCount)} / $totalQuestions",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${if (uiLanguage == "id") "Akurasi" else "Accuracy"}: ${String.format("%.1f", accuracy)}%",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(UiLocalizer.getString("practice_practice_again", uiLanguage))
        }

        // Safe Banner Ad at end of session (hidden if Premium)
        AdBannerView(
            isPremium = isPremium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

fun generateQuizFromWords(
    words: List<Word>,
    fallbackPool: List<Word> = words,
    random: Random = Random.Default,
    explanationLanguage: String = "id",
    uiLanguage: String = "en"
): List<QuizQuestion> {
    if (words.isEmpty() || fallbackPool.size < 4) return emptyList()

    val targetWords = words.shuffled(random).take(minOf(10, words.size))
    val questions = mutableListOf<QuizQuestion>()

    targetWords.forEachIndexed { index, word ->
        val otherWords = fallbackPool.filter { it.id != word.id }
        // Cycle between question styles:
        // 0: Word to Definition
        // 1: Definition to Word
        // 2: Fill in the Blank
        // 3: Translation/Immersion Matching
        when (index % 4) {
            0 -> {
                val distractorPool = otherWords.shuffled(random).take(3)
                val options = (listOf(word.definition) + distractorPool.map { it.definition }).shuffled(random)
                val correctIndex = options.indexOf(word.definition)
                val expText = if (explanationLanguage == "id") {
                    "\"${word.text}\" (${word.partOfSpeech}) bermakna: ${word.definition}. Arti: ${word.translation ?: "-"}"
                } else {
                    "\"${word.text}\" (${word.partOfSpeech}) means: ${word.definition}."
                }
                questions.add(
                    QuizQuestion(
                        id = "q_gen_${word.id}_$index",
                        wordId = word.id,
                        questionText = UiLocalizer.getString("practice_what_meaning", uiLanguage, word.text),
                        options = options,
                        correctOptionIndex = correctIndex,
                        explanation = expText
                    )
                )
            }
            1 -> {
                val distractorPool = otherWords.shuffled(random).take(3)
                val options = (listOf(word.text) + distractorPool.map { it.text }).shuffled(random)
                val correctIndex = options.indexOf(word.text)
                val expText = if (explanationLanguage == "id") {
                    "\"${word.text}\" (${word.phonetic ?: ""}): ${word.definition}. Arti: ${word.translation ?: "-"}"
                } else {
                    "\"${word.text}\" (${word.phonetic ?: ""}): ${word.definition}."
                }
                questions.add(
                    QuizQuestion(
                        id = "q_gen_${word.id}_$index",
                        wordId = word.id,
                        questionText = UiLocalizer.getString("practice_which_word", uiLanguage, word.definition),
                        options = options,
                        correctOptionIndex = correctIndex,
                        explanation = expText
                    )
                )
            }
            2 -> {
                val sentence = word.exampleSentence
                val regex = Regex("\\b${Regex.escape(word.text)}\\b", RegexOption.IGNORE_CASE)
                val blankSentence = if (sentence.contains(regex)) {
                    sentence.replace(regex, "______")
                } else {
                    "Complete with \"${word.text}\": $sentence"
                }
                val distractorPool = otherWords.shuffled(random).take(3)
                val options = (listOf(word.text) + distractorPool.map { it.text }).shuffled(random)
                val correctIndex = options.indexOf(word.text)
                val expText = if (explanationLanguage == "id") {
                    "Kalimat lengkap: \"${word.exampleSentence}\". (${word.text} = ${word.translation ?: "-"})"
                } else {
                    "Full sentence: \"${word.exampleSentence}\"."
                }
                questions.add(
                    QuizQuestion(
                        id = "q_gen_${word.id}_$index",
                        wordId = word.id,
                        questionText = UiLocalizer.getString("practice_complete_sentence", uiLanguage, blankSentence),
                        options = options,
                        correctOptionIndex = correctIndex,
                        explanation = expText
                    )
                )
            }
            else -> {
                if (explanationLanguage == "id") {
                    // Indonesian translation to English word
                    val translationText = word.translation ?: word.definition
                    val distractorPool = otherWords.shuffled(random).take(3)
                    val options = (listOf(word.text) + distractorPool.map { it.text }).shuffled(random)
                    val correctIndex = options.indexOf(word.text)
                    questions.add(
                        QuizQuestion(
                            id = "q_gen_${word.id}_$index",
                            wordId = word.id,
                            questionText = UiLocalizer.getString("practice_which_translates", uiLanguage, translationText),
                            options = options,
                            correctOptionIndex = correctIndex,
                            explanation = "\"${word.text}\" bermakna \"$translationText\". (${word.phonetic ?: ""})"
                        )
                    )
                } else {
                    // English Direct Immersion: Context/Definition fitting
                    val distractorPool = otherWords.shuffled(random).take(3)
                    val options = (listOf(word.text) + distractorPool.map { it.text }).shuffled(random)
                    val correctIndex = options.indexOf(word.text)
                    questions.add(
                        QuizQuestion(
                            id = "q_gen_${word.id}_$index",
                            wordId = word.id,
                            questionText = UiLocalizer.getString("practice_which_fits", uiLanguage, word.definition),
                            options = options,
                            correctOptionIndex = correctIndex,
                            explanation = "\"${word.text}\" matches: \"${word.definition}\"."
                        )
                    )
                }
            }
        }
    }

    return questions
}
