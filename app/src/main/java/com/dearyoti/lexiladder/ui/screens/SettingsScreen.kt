package com.dearyoti.lexiladder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Color
import com.dearyoti.lexiladder.LexiLadderApp
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onNavigateToContentUpdates: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val prefs by prefsRepo.userPreferencesFlow.collectAsState(
        initial = com.dearyoti.lexiladder.data.preferences.UserPreferences()
    )
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        // Premium Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (prefs.isPremium) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (prefs.isPremium) "✨ LexiLadder Premium Active" else "LexiLadder Free Edition",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (prefs.isPremium) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (prefs.isPremium) "All advertisements are permanently disabled. 100% offline access." else "Remove all banner and session ads with a one-time purchase.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (prefs.isPremium) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNavigateToPremium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (prefs.isPremium) "Manage Premium" else "Upgrade to Premium (Ad-Free)")
                }
            }
        }

        // Explanation Language
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Explanation & Translation Language",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                val languages = listOf("id" to "Bahasa Indonesia", "en" to "English (Direct)")
                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = prefs.explanationLanguage == code,
                            onClick = {
                                scope.launch {
                                    prefsRepo.updateExplanationLanguage(code)
                                }
                            }
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Content & Language Updates Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Content & Language Updates",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Download new vocabulary levels, translations, and UI packages directly from GitHub without needing an APK update.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNavigateToContentUpdates,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.CloudSync, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manage Content Updates")
                }
            }
        }

        // Daily Goal
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Daily Practice Goal",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                val goals = listOf(5, 10, 15)
                goals.forEach { minutes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = prefs.dailyGoalMinutes == minutes,
                            onClick = {
                                scope.launch {
                                    prefsRepo.updateDailyGoal(minutes)
                                }
                            }
                        )
                        Text(
                            text = "$minutes Minutes / day",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Privacy Policy Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Privacy & Data Safety",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "100% offline learning progress. No account required. Learn how your data is handled.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onNavigateToPrivacyPolicy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Policy, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Read Privacy Policy")
                }
            }
        }

        // About Application Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "About LexiLadder",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version: 1.0.0 (Build 1)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Package: com.dearyoti.lexiladder",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Developer: dearyoti",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Architecture: Offline-first Room + Jetpack Compose",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
