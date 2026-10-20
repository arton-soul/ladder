package com.dearyoti.lexiladder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.core.ads.AdBannerView
import com.dearyoti.lexiladder.core.localization.UiLocalizer
import com.dearyoti.lexiladder.domain.model.LevelStatus

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit = {},
    onNavigateToPractice: () -> Unit = {},
    onNavigateToMasteryTest: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val repo = LexiLadderApp.instance.vocabularyRepository
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val levels by repo.getAllLevels().collectAsState(initial = emptyList())
    val unlockedLevels = remember(levels) { levels.filter { it.isUnlocked } }

    var selectedLevelId by remember { mutableStateOf<String?>(null) }
    val currentLevel = remember(levels, selectedLevelId) {
        if (selectedLevelId != null) {
            levels.firstOrNull { it.id == selectedLevelId && it.isUnlocked }
        } else {
            levels.firstOrNull { it.isUnlocked && !it.isMastered }
                ?: levels.filter { it.isUnlocked }.maxByOrNull { it.order }
                ?: levels.firstOrNull()
        }
    }
    val activeLevelId = currentLevel?.id ?: "level_a1"
    val words by repo.getWordsByLevel(activeLevelId).collectAsState(initial = emptyList())
    val dueCount by repo.getDueReviewCount().collectAsState(initial = 0)
    val difficultWords by repo.getDifficultWords().collectAsState(initial = emptyList())
    val prefs by prefsRepo.userPreferencesFlow.collectAsState(
        initial = com.dearyoti.lexiladder.data.preferences.UserPreferences()
    )
    val lang = prefs.uiLanguage

    val totalWords = if (words.isNotEmpty()) words.size else (currentLevel?.totalWords ?: 40)
    val levelMasteredCount = currentLevel?.masteredWords ?: 0
    val progressFraction = if (totalWords > 0) (levelMasteredCount.toFloat() / totalWords.toFloat()).coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Due for review alert card if any words are scheduled for recall today
        if (dueCount > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ " + UiLocalizer.getString("home_due_title", lang, dueCount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = UiLocalizer.getString("home_due_desc", lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Button(
                        onClick = onNavigateToPractice,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text(UiLocalizer.getString("home_review_btn", lang))
                    }
                }
            }
        }

        // Daily target header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = UiLocalizer.getString("home_target_title", lang, prefs.dailyGoalMinutes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${(progressFraction * 100).toInt()}% " + if (lang == "id") "Selesai" else "Done",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = UiLocalizer.getString("home_target_desc", lang, prefs.dailyGoalMinutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Level Roadmap Switcher (when multiple levels are unlocked)
        if (unlockedLevels.size > 1) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(unlockedLevels) { lvl ->
                    val isSelected = currentLevel?.id == lvl.id
                    val badge = if (lvl.isMastered) "🏆" else "🔓"
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLevelId = lvl.id },
                        label = { Text("${lvl.title.split(":").firstOrNull() ?: lvl.title} $badge") }
                    )
                }
            }
        }

        // Active Level Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentLevel?.title ?: "Level 1: Beginner Starter",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    currentLevel?.let { lvl ->
                        HomeStatusBadge(status = lvl.status, uiLanguage = lang)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentLevel?.description ?: "Essential vocabulary for everyday conversations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = UiLocalizer.getString("home_words_mastered", lang),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "$levelMasteredCount / $totalWords " + if (lang == "id") "kata" else "words",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(onClick = onNavigateToLearn) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = UiLocalizer.getString("home_learn_words", lang))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onNavigateToPractice,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "Practice")
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = UiLocalizer.getString("home_quick_quiz", lang))
                }

                // Mastery Test Button
                currentLevel?.let { lvl ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onNavigateToMasteryTest(lvl.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lvl.isMastered)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = if (lvl.isMastered)
                                (if (lang == "id") "Ulangi Ujian Level 🏆" else "Retake Level Mastery Test 🏆")
                            else
                                (if (lang == "id") "Ikuti Ujian Level 🎯" else "Take Level Mastery Test 🎯")
                        )
                    }
                }
            }
        }

        // Dynamic Units Breakdown Card
        val unitsMap = remember(words) {
            words.groupBy { it.unitId ?: "unit_1" }
        }
        if (unitsMap.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val levelShortTitle = currentLevel?.title?.split(":")?.firstOrNull() ?: "Level"
                    Text(
                        text = UiLocalizer.getString("home_units_header", lang, levelShortTitle),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    unitsMap.entries.sortedBy { it.key }.forEachIndexed { idx, entry ->
                        if (idx > 0) Spacer(modifier = Modifier.height(6.dp))
                        val unitTitle = entry.key.replace("_", " ").replaceFirstChar { it.uppercase() }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(unitTitle, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                UiLocalizer.getString("home_words_count", lang, entry.value.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Offline notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "id") "🌐 Akses Penuh Offline" else "🌐 100% Offline Access",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lang == "id")
                        "Semua kosakata dan ujian level disimpan langsung di perangkat Anda. Belajar kapan saja tanpa koneksi internet."
                    else
                        "All vocabulary and mastery tests for unlocked levels run locally on your device. No internet connection required.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Ad Banner (Non-intrusive bottom placement, hidden when Premium)
        AdBannerView(
            isPremium = prefs.isPremium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun HomeStatusBadge(status: LevelStatus, uiLanguage: String = "en") {
    val isId = uiLanguage == "id"
    val (label, bg, textCol) = when (status) {
        LevelStatus.LOCKED -> Triple(if (isId) "Terkunci 🔒" else "Locked 🔒", Color(0xFFEEEEEE), Color(0xFF757575))
        LevelStatus.AVAILABLE -> Triple(if (isId) "Tersedia 🔓" else "Available 🔓", Color(0xFFE3F2FD), Color(0xFF1976D2))
        LevelStatus.IN_PROGRESS -> Triple(if (isId) "Belajar ⏳" else "Learning ⏳", Color(0xFFFFF3E0), Color(0xFFE65100))
        LevelStatus.READY_FOR_TEST -> Triple(if (isId) "Siap Ujian 🎯" else "Ready for Test 🎯", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        LevelStatus.MASTERED -> Triple(if (isId) "Dikuasai 🏆" else "Mastered 🏆", Color(0xFFEDE7F6), Color(0xFF512DA8))
    }

    Box(
        modifier = Modifier
            .background(color = bg, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textCol
        )
    }
}
