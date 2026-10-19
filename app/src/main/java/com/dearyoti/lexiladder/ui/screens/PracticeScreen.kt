package com.dearyoti.lexiladder.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dearyoti.lexiladder.LexiLadderApp
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
    val repo = LexiLadderApp.instance.vocabularyRepository
    val allWords by repo.getWordsByLevel("level_a1").collectAsState(initial = emptyList())
    val dueWords by repo.getWordsDueForReview().collectAsState(initial = emptyList())
    val difficultWords by repo.getDifficultWords().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var selectedMode by remember { mutableStateOf(PracticeMode.ALL) }

    // Active words based on selected mode
    val activeWordPool = when (selectedMode) {
        PracticeMode.ALL -> allWords
        PracticeMode.DUE_REVIEW -> dueWords
        PracticeMode.DIFFICULT -> difficultWords
    }

    // Generate dynamic quiz questions from words
    val questions = remember(activeWordPool, allWords, selectedMode) {
        val targetWords = if (activeWordPool.size >= 4) activeWordPool else allWords
        generateQuizFromWords(targetWords, allWords)
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
            Text("Loading practice questions...", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    if (isSessionComplete) {
        QuizResultView(
            totalQuestions = questions.size,
            correctCount = sessionCorrectCount,
            onRestart = {
                currentIndex = 0
                selectedOption = null
                isSubmitted = false
                sessionCorrectCount = 0
                isSessionComplete = false
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
            text = "Adaptive Practice & Review",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Practice Mode Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val modes = listOf(
                PracticeMode.ALL to "Standard Quiz (${allWords.size})",
                PracticeMode.DUE_REVIEW to "Due Review (${dueWords.size})",
                PracticeMode.DIFFICULT to "Difficult Words (${difficultWords.size})"
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
                        text = "All Caught Up! 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You have no words scheduled for review right now. Great job keeping up with your spaced repetition schedule!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { selectedMode = PracticeMode.ALL }) {
                        Text("Practice All Words")
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
                        text = "No Difficult Words Yet! 🌟",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Words that you miss during practice will automatically appear here for focused reinforcement.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { selectedMode = PracticeMode.ALL }) {
                        Text("Practice All Words")
                    }
                }
            }
            return
        }

        if (questions.isEmpty()) {
            Text("No questions available for this category.", style = MaterialTheme.typography.bodyMedium)
            return
        }

        val currentQuestion = questions[currentIndex]

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${currentIndex + 1} of ${questions.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Score: $sessionCorrectCount",
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
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.titleLarge
                )
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
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor)
            ) {
                Text(
                    text = optionText,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isSubmitted) {
            val isRight = selectedOption == currentQuestion.correctOptionIndex
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isRight)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isRight) "Correct! 🎉" else "Incorrect",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQuestion.explanation,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

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
                Text(text = if (currentIndex < questions.size - 1) "Next Question" else "See Results")
            }
        } else {
            Button(
                onClick = {
                    if (selectedOption != null) {
                        isSubmitted = true
                        val correct = selectedOption == currentQuestion.correctOptionIndex
                        if (correct) sessionCorrectCount++
                        val rating = if (correct) ReviewRating.GOOD else ReviewRating.WRONG
                        scope.launch {
                            repo.recordReviewRating(currentQuestion.wordId, rating)
                        }
                    }
                },
                enabled = selectedOption != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit Answer")
            }
        }
    }
}

@Composable
fun QuizResultView(
    totalQuestions: Int,
    correctCount: Int,
    onRestart: () -> Unit
) {
    val accuracy = if (totalQuestions > 0) (correctCount.toFloat() / totalQuestions.toFloat()) * 100f else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Session Complete! 🏆",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Review intervals have been updated using your spaced repetition scheduler.",
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
                    text = "Score: $correctCount / $totalQuestions",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Accuracy: ${String.format("%.1f", accuracy)}%",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Practice Again")
        }
    }
}

fun generateQuizFromWords(
    words: List<Word>,
    fallbackPool: List<Word> = words
): List<QuizQuestion> {
    if (words.isEmpty() || fallbackPool.size < 4) return emptyList()

    val random = Random(42)
    val questions = mutableListOf<QuizQuestion>()

    words.take(8).forEachIndexed { index, word ->
        val distractorPool = fallbackPool.filter { it.id != word.id }.shuffled(random).take(3)
        val options = (listOf(word.definition) + distractorPool.map { it.definition }).shuffled(random)
        val correctIndex = options.indexOf(word.definition)

        questions.add(
            QuizQuestion(
                id = "q_gen_$index",
                wordId = word.id,
                questionText = "What is the meaning of \"${word.text}\"?",
                options = options,
                correctOptionIndex = correctIndex,
                explanation = "\"${word.text}\" (${word.partOfSpeech}) means: ${word.definition}. Arti: ${word.translation ?: "-"}"
            )
        )
    }

    return questions
}
