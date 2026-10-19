package com.dearyoti.lexiladder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.domain.model.QuizQuestion
import kotlinx.coroutines.launch

@Composable
fun PracticeScreen(
    modifier: Modifier = Modifier
) {
    val repo = LexiLadderApp.instance.vocabularyRepository
    val scope = rememberCoroutineScope()

    // Sample adaptive quiz questions generated from initial vocabulary
    val questions = remember {
        listOf(
            QuizQuestion(
                id = "q1",
                wordId = "w_a1_001",
                questionText = "What is the meaning of \"Achieve\"?",
                options = listOf(
                    "To successfully complete something with effort",
                    "To travel around a new place",
                    "To sleep for a long time",
                    "To lose something valuable"
                ),
                correctOptionIndex = 0,
                explanation = "\"Achieve\" means to successfully finish or accomplish something."
            ),
            QuizQuestion(
                id = "q2",
                wordId = "w_a1_002",
                questionText = "Which word means \"lasting only a short period of time\"?",
                options = listOf(
                    "Diligent",
                    "Courage",
                    "Brief",
                    "Explore"
                ),
                correctOptionIndex = 2,
                explanation = "\"Brief\" means short in duration or concise."
            ),
            QuizQuestion(
                id = "q3",
                wordId = "w_a1_004",
                questionText = "A student who studies conscientiously every night can be described as:",
                options = listOf(
                    "Careless",
                    "Diligent",
                    "Brief",
                    "Frightened"
                ),
                correctOptionIndex = 1,
                explanation = "\"Diligent\" means hardworking and persistent."
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Adaptive Practice",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Question ${currentIndex + 1} of ${questions.size}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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

        Spacer(modifier = Modifier.height(8.dp))

        if (isSubmitted) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedOption == currentQuestion.correctOptionIndex)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedOption == currentQuestion.correctOptionIndex) "Correct! 🎉" else "Not quite right!",
                        style = MaterialTheme.typography.titleMedium
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
                    isSubmitted = false
                    selectedOption = null
                    currentIndex = (currentIndex + 1) % questions.size
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (currentIndex < questions.size - 1) "Next Question" else "Practice Again")
            }
        } else {
            Button(
                onClick = {
                    if (selectedOption != null) {
                        isSubmitted = true
                        val correct = selectedOption == currentQuestion.correctOptionIndex
                        scope.launch {
                            repo.recordReviewResult(currentQuestion.wordId, correct)
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
