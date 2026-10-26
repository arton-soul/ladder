package com.dearyoti.lexiladder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.core.ads.AdBannerView
import com.dearyoti.lexiladder.core.localization.UiLocalizer
import com.dearyoti.lexiladder.data.preferences.UserPreferences
import com.dearyoti.lexiladder.domain.model.LevelStatus
import com.dearyoti.lexiladder.domain.service.MasteryTestEngine
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun ProgressScreen(
    onNavigateToMasteryTest: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val repo = LexiLadderApp.instance.vocabularyRepository
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val prefs by prefsRepo.userPreferencesFlow.collectAsState(initial = UserPreferences())
    val uiLang = prefs.uiLanguage

    val levels by repo.getAllLevels().collectAsState(initial = emptyList())
    val masteredCount by repo.getMasteredWordCount().collectAsState(initial = 0)
    val favorites by repo.getFavorites().collectAsState(initial = emptyList())
    val allWords by repo.getAllWords().collectAsState(initial = emptyList())
    val dueCount by repo.getDueReviewCount().collectAsState(initial = 0)
    val difficultWords by repo.getDifficultWords().collectAsState(initial = emptyList())

    val unlockedLevelIds = remember(levels) { levels.filter { it.isUnlocked }.map { it.id }.toSet() }
    val unlockedWords = remember(allWords, unlockedLevelIds) {
        if (unlockedLevelIds.isNotEmpty()) allWords.filter { unlockedLevelIds.contains(it.levelId) }
        else allWords
    }
    val totalWords = if (unlockedWords.isNotEmpty()) unlockedWords.size else 40
    val learningCount = (totalWords - masteredCount).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = UiLocalizer.getString("progress_title", uiLang),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Daily Streak & Activity Calendar
        DailyStreakCard(
            effectiveStreak = prefs.effectiveStreak,
            bestStreak = prefs.bestStreak,
            isStudiedToday = prefs.isStudiedToday,
            activeDates = prefs.activeDates,
            uiLanguage = uiLang
        )

        // Summary Cards - Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = UiLocalizer.getString("progress_stat_mastered", uiLang),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "$masteredCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = UiLocalizer.getString("progress_stat_due", uiLang),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "$dueCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Summary Cards - Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = UiLocalizer.getString("progress_stat_learning", uiLang),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "$learningCount",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = UiLocalizer.getString("progress_stat_difficult", uiLang),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "${difficultWords.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bookmarked Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("★ " + UiLocalizer.getString("progress_bookmarked", uiLang), style = MaterialTheme.typography.titleMedium)
                Text(
                    "${favorites.size} " + if (uiLang == "id") "kata" else "words",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = UiLocalizer.getString("progress_roadmap_title", uiLang),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        levels.forEach { level ->
            val fraction = if (level.totalWords > 0) {
                (level.studiedWords.toFloat() / level.totalWords.toFloat()).coerceIn(0f, 1f)
            } else 0f

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
                            text = level.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        ProgressLevelBadge(status = level.status, uiLanguage = uiLang)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = level.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (level.isUnlocked) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = UiLocalizer.getString("progress_study_progress", uiLang, level.studiedWords, level.totalWords),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "${(fraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        val isEligible = MasteryTestEngine.isTestEligible(level)
                        val req80 = MasteryTestEngine.calculateRequiredStudyCount(level.totalWords)
                        Button(
                            onClick = { onNavigateToMasteryTest(level.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    level.isMastered -> MaterialTheme.colorScheme.secondary
                                    isEligible -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = when {
                                    level.isMastered -> MaterialTheme.colorScheme.onSecondary
                                    isEligible -> MaterialTheme.colorScheme.onPrimary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        ) {
                            Icon(
                                imageVector = if (!isEligible && !level.isMastered) Icons.Default.Lock else Icons.Default.WorkspacePremium,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(
                                text = when {
                                    level.isMastered -> if (uiLang == "id") "Ulangi Ujian Level 🏆" else "Retake Mastery Test 🏆"
                                    isEligible -> if (uiLang == "id") "Ikuti Ujian Level (Lulus ${level.requiredPassingScore}%) 🎯" else "Take Mastery Test (Pass ${level.requiredPassingScore}%) 🎯"
                                    else -> if (uiLang == "id") "Ujian Terkunci (Pelajari 80% Materi: ${level.studiedWords}/$req80) 🔒" else "Exam Locked (Study 80% Material: ${level.studiedWords}/$req80) 🔒"
                                }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiLang == "id")
                                "🔒 Terkunci — Lulus Ujian Level ${level.order - 1} untuk membuka."
                            else
                                "🔒 Locked — Pass Level ${level.order - 1} Mastery Test to unlock.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
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
private fun ProgressLevelBadge(status: LevelStatus, uiLanguage: String = "en") {
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

@Composable
private fun DailyStreakCard(
    effectiveStreak: Int,
    bestStreak: Int,
    isStudiedToday: Boolean,
    activeDates: Set<String>,
    uiLanguage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Flame + Streak count, and Best streak chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (effectiveStreak > 0) "🔥" else "⚡",
                        fontSize = 28.sp
                    )
                    Column {
                        Text(
                            text = UiLocalizer.getString("progress_streak_title", uiLanguage),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (effectiveStreak > 0) {
                                UiLocalizer.getString("progress_streak_count", uiLanguage, effectiveStreak)
                            } else {
                                UiLocalizer.getString("progress_streak_zero", uiLanguage)
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (effectiveStreak > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Best streak badge
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = UiLocalizer.getString("progress_streak_best", uiLanguage, bestStreak),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 7-Day Rolling Activity Visualizer
            val today = remember { LocalDate.now() }
            val past7Days = remember(today) {
                (6 downTo 0).map { today.minusDays(it.toLong()) }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                past7Days.forEach { date ->
                    val isDayActive = activeDates.contains(date.toString())
                    val isDateToday = date == today
                    val dayLabel = getShortDayName(date.dayOfWeek, uiLanguage)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDateToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isDateToday) FontWeight.Bold else FontWeight.Normal
                        )

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isDayActive -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                )
                                .then(
                                    if (isDateToday) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    } else {
                                        Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDayActive) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${date.dayOfMonth}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Status feedback message
            Text(
                text = if (isStudiedToday) {
                    UiLocalizer.getString("progress_streak_done_today", uiLanguage)
                } else {
                    UiLocalizer.getString("progress_streak_pending_today", uiLanguage)
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (isStudiedToday) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun getShortDayName(dayOfWeek: DayOfWeek, uiLanguage: String): String {
    return if (uiLanguage == "id") {
        when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Sen"
            DayOfWeek.TUESDAY -> "Sel"
            DayOfWeek.WEDNESDAY -> "Rab"
            DayOfWeek.THURSDAY -> "Kam"
            DayOfWeek.FRIDAY -> "Jum"
            DayOfWeek.SATURDAY -> "Sab"
            DayOfWeek.SUNDAY -> "Min"
        }
    } else {
        when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Mon"
            DayOfWeek.TUESDAY -> "Tue"
            DayOfWeek.WEDNESDAY -> "Wed"
            DayOfWeek.THURSDAY -> "Thu"
            DayOfWeek.FRIDAY -> "Fri"
            DayOfWeek.SATURDAY -> "Sat"
            DayOfWeek.SUNDAY -> "Sun"
        }
    }
}
