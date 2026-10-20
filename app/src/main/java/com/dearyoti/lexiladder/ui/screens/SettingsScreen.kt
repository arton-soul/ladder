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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.core.localization.UiLocalizer
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
    val lang = prefs.uiLanguage

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = UiLocalizer.getString("settings_title", lang),
            style = MaterialTheme.typography.headlineMedium
        )

        // 1. App Interface Language Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = UiLocalizer.getString("settings_ui_lang_title", lang),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = UiLocalizer.getString("settings_ui_lang_desc", lang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                val uiLanguages = listOf(
                    "en" to UiLocalizer.getString("settings_ui_en", lang),
                    "id" to UiLocalizer.getString("settings_ui_id", lang)
                )

                uiLanguages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = prefs.uiLanguage == code,
                            onClick = {
                                scope.launch {
                                    prefsRepo.updateUiLanguage(code)
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

        // 2. Vocabulary Explanation & Translation Language Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = UiLocalizer.getString("settings_exp_lang_title", lang),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = UiLocalizer.getString("settings_exp_lang_desc", lang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                val explanationLanguages = listOf(
                    Triple("id", UiLocalizer.getString("settings_exp_id", lang), UiLocalizer.getString("settings_exp_id_sub", lang)),
                    Triple("en", UiLocalizer.getString("settings_exp_en", lang), UiLocalizer.getString("settings_exp_en_sub", lang))
                )

                explanationLanguages.forEach { (code, title, desc) ->
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
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        // 3. Premium Status Card
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
                            text = if (prefs.isPremium) "✨ " + UiLocalizer.getString("settings_premium_active", lang) else UiLocalizer.getString("settings_premium_title", lang),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (prefs.isPremium) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (prefs.isPremium) UiLocalizer.getString("settings_premium_active_desc", lang) else UiLocalizer.getString("settings_premium_desc", lang),
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
                    Text(if (prefs.isPremium) "Manage Premium" else UiLocalizer.getString("settings_premium_btn", lang))
                }
            }
        }

        // 4. Content & Language Updates Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = UiLocalizer.getString("settings_updates_title", lang),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = UiLocalizer.getString("settings_updates_desc", lang),
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
                    Text(UiLocalizer.getString("settings_updates_btn", lang))
                }
            }
        }

        // 5. Daily Goal
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = UiLocalizer.getString("settings_daily_goal_title", lang),
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
                            text = UiLocalizer.getString("settings_minutes_per_day", lang, minutes),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // 6. Dark Mode Card
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lang == "id") "Tema Gelap" else "Dark Theme",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (lang == "id") "Aktifkan mode gelap agar nyaman belajar di malam hari" else "Enable dark mode for comfortable night studying",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = prefs.isDarkMode,
                    onCheckedChange = { enabled ->
                        scope.launch {
                            prefsRepo.setDarkMode(enabled)
                        }
                    }
                )
            }
        }

        // 7. Privacy Policy Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = UiLocalizer.getString("settings_privacy_title", lang),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = UiLocalizer.getString("settings_privacy_desc", lang),
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
                    Text(UiLocalizer.getString("settings_privacy_btn", lang))
                }
            }
        }

        // 8. About Application Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == "id") "Tentang LexiLadder" else "About LexiLadder",
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
