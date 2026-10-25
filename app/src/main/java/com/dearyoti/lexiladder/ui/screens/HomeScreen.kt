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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.core.ads.AdBannerView
import com.dearyoti.lexiladder.core.localization.UiLocalizer
import com.dearyoti.lexiladder.core.notification.ContentNotificationHelper
import com.dearyoti.lexiladder.data.content.ContentUpdateResult
import com.dearyoti.lexiladder.data.preferences.UserPreferencesRepository
import com.dearyoti.lexiladder.domain.model.LevelStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit = {},
    onNavigateToPractice: () -> Unit = {},
    onNavigateToMasteryTest: (String) -> Unit = {},
    onNavigateToContentUpdates: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = LexiLadderApp.instance.vocabularyRepository
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val contentUpdateManager = LexiLadderApp.instance.contentUpdateManager

    val levels by repo.getAllLevels().collectAsState(initial = emptyList())
    val unlockedLevels = remember(levels) { levels.filter { it.isUnlocked } }

    val prefs by prefsRepo.userPreferencesFlow.collectAsState(
        initial = com.dearyoti.lexiladder.data.preferences.UserPreferences()
    )
    val lang = prefs.uiLanguage

    val savedLevelId = prefs.selectedLevelId
    val currentLevel = remember(levels, savedLevelId) {
        if (savedLevelId.isNotBlank()) {
            levels.firstOrNull { it.id == savedLevelId && it.isUnlocked }
        } else {
            null
        } ?: levels.firstOrNull { it.isUnlocked && !it.isMastered }
          ?: levels.filter { it.isUnlocked }.maxByOrNull { it.order }
          ?: levels.firstOrNull()
    }
    val activeLevelId = currentLevel?.id ?: "level_a1"
    val words by repo.getWordsByLevel(activeLevelId).collectAsState(initial = emptyList())
    val dueCount by repo.getDueReviewCount().collectAsState(initial = 0)
    val difficultWords by repo.getDifficultWords().collectAsState(initial = emptyList())

    val totalWords = if (words.isNotEmpty()) words.size else (currentLevel?.totalWords ?: 40)
    val levelStudiedCount = currentLevel?.studiedWords ?: 0
    val levelMasteredCount = currentLevel?.masteredWords ?: 0
    val levelProgressFraction = if (totalWords > 0) (levelStudiedCount.toFloat() / totalWords.toFloat()).coerceIn(0f, 1f) else 0f

    // In-App Content Update Popup State
    var showUpdateDialog by remember { mutableStateOf(false) }
    var availableUpdateCount by remember { mutableIntStateOf(0) }

    // Automatic Periodic Check: once every 24 hours (1 day)
    LaunchedEffect(Unit) {
        val currentPrefs = prefsRepo.userPreferencesFlow.first()
        val lastCheck = currentPrefs.lastContentCheckTimestamp
        if (UserPreferencesRepository.isCheckIntervalElapsed(lastCheck)) {
            prefsRepo.updateLastContentCheckTimestamp()
            try {
                val result = contentUpdateManager.checkUpdates(
                    remoteManifestUrl = "https://raw.githubusercontent.com/arton-soul/ladder/master/content/manifest.json"
                )
                if (result is ContentUpdateResult.Success && result.packagesToUpdate.isNotEmpty()) {
                    availableUpdateCount = result.packagesToUpdate.size
                    showUpdateDialog = true
                    ContentNotificationHelper.showUpdateNotification(
                        context = context,
                        packageCount = availableUpdateCount,
                        uiLanguage = lang
                    )
                }
            } catch (_: Exception) {
                // Silently ignore network failures to maintain 100% offline-first reliability
            }
        }
    }

    if (showUpdateDialog) {
        val isId = lang == "id"
        AlertDialog(
            onDismissRequest = {
                showUpdateDialog = false
                scope.launch {
                    prefsRepo.updateLastContentCheckTimestamp()
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (isId) "Pembaruan Konten Tersedia 🚀" else "New Content Update Available 🚀",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isId)
                        "Terdapat $availableUpdateCount pembaruan kosakata atau bahasa baru di GitHub. Unduh sekarang untuk memperkaya materi belajar Anda?"
                    else
                        "Found $availableUpdateCount new vocabulary or language updates on GitHub. Download now to enrich your offline study library?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUpdateDialog = false
                        scope.launch {
                            prefsRepo.updateLastContentCheckTimestamp()
                        }
                        onNavigateToContentUpdates()
                    }
                ) {
                    Text(if (isId) "Perbarui Sekarang" else "Update Now")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showUpdateDialog = false
                        scope.launch {
                            prefsRepo.updateLastContentCheckTimestamp()
                        }
                    }
                ) {
                    Text(if (isId) "Nanti Saja" else "Later")
                }
            }
        )
    }

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
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎯", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text(
                            text = UiLocalizer.getString("home_target_title", lang, prefs.dailyGoalMinutes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Status Pill Badge
                    val isDailyGoalReached = prefs.dailyGoalProgressFraction >= 1f
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDailyGoalReached)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isDailyGoalReached) {
                                Text(
                                    text = "✓",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(
                                text = "${(prefs.dailyGoalProgressFraction * 100).toInt()}% " +
                                        if (lang == "id") "Selesai" else "Done",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDailyGoalReached)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = UiLocalizer.getString("home_target_desc", lang, prefs.dailyGoalMinutes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${prefs.effectiveTodayStudyCount}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = " / ${prefs.dailyTargetReviews} ${if (lang == "id") "kata" else "words"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                            modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { prefs.dailyGoalProgressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
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
                        onClick = {
                            scope.launch {
                                prefsRepo.updateSelectedLevel(lvl.id)
                            }
                        },
                        label = { Text("${lvl.title.split(":").firstOrNull() ?: lvl.title} $badge") }
                    )
                }
            }
        }

        // Active Level Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentLevel?.description ?: "Essential vocabulary for everyday conversations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Modern Dedicated Progress Container
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (lang == "id") "PROGRES MATERI" else "MATERIAL PROGRESS",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$levelStudiedCount",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = " / $totalWords ${if (lang == "id") "kata" else "words"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                                    )
                                }
                            }

                            // Mastered words pill badge
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🏆", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = "$levelMasteredCount ${if (lang == "id") "dikuasai" else "mastered"}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { levelProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${(levelProgressFraction * 100).toInt()}% " +
                                        if (lang == "id") "terselesaikan" else "completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (levelStudiedCount < totalWords) {
                                Text(
                                    text = "${totalWords - levelStudiedCount} " +
                                            if (lang == "id") "kata tersisa" else "words remaining",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                currentLevel?.let { prefsRepo.updateSelectedLevel(it.id) }
                            }
                            onNavigateToLearn()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = UiLocalizer.getString("home_learn_words", lang))
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                currentLevel?.let { prefsRepo.updateSelectedLevel(it.id) }
                            }
                            onNavigateToPractice()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "Practice")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = UiLocalizer.getString("home_quick_quiz", lang))
                    }
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
                        Spacer(modifier = Modifier.width(4.dp))
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
