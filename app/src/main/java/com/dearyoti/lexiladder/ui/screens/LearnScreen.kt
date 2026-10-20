package com.dearyoti.lexiladder.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.core.localization.UiLocalizer
import com.dearyoti.lexiladder.data.preferences.UserPreferences
import com.dearyoti.lexiladder.domain.model.ReviewRating
import com.dearyoti.lexiladder.domain.model.Word
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    modifier: Modifier = Modifier
) {
    val repo = LexiLadderApp.instance.vocabularyRepository
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val prefs by prefsRepo.userPreferencesFlow.collectAsState(initial = UserPreferences())

    val uiLang = prefs.uiLanguage
    val expLang = prefs.explanationLanguage

    val levels by repo.getAllLevels().collectAsState(initial = emptyList())
    val unlockedLevels = remember(levels) { levels.filter { it.isUnlocked } }
    var selectedLevelId by remember { mutableStateOf("level_a1") }

    val activeLevelId = remember(selectedLevelId, unlockedLevels) {
        if (unlockedLevels.any { it.id == selectedLevelId }) selectedLevelId
        else unlockedLevels.firstOrNull()?.id ?: "level_a1"
    }

    val words by repo.getWordsByLevel(activeLevelId).collectAsState(initial = emptyList())
    val favorites by repo.getFavorites().collectAsState(initial = emptyList())
    val favIds = favorites.map { it.id }.toSet()
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }
    var isFlashcardMode by remember { mutableStateOf(false) }

    // Dynamic available units for the active level
    val availableUnits = remember(words) {
        words.mapNotNull { it.unitId }.distinct().sorted()
    }

    // Filtered words
    val filteredWords = remember(words, searchQuery, selectedFilter, favIds) {
        words.filter { word ->
            val matchesQuery = searchQuery.isBlank() ||
                    word.text.contains(searchQuery, ignoreCase = true) ||
                    (word.translation?.contains(searchQuery, ignoreCase = true) == true) ||
                    word.definition.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "all" -> true
                "favorites" -> favIds.contains(word.id)
                else -> word.unitId == selectedFilter
            }

            matchesQuery && matchesFilter
        }
    }

    if (isFlashcardMode && filteredWords.isNotEmpty()) {
        FlashcardSessionView(
            words = filteredWords,
            uiLanguage = uiLang,
            explanationLanguage = expLang,
            onClose = { isFlashcardMode = false },
            onWordReviewed = { wordId, rating ->
                scope.launch {
                    repo.recordReviewRating(wordId, rating)
                }
            }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Level Selector Chips (when multiple levels are unlocked)
        if (unlockedLevels.size > 1) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(unlockedLevels) { lvl ->
                    FilterChip(
                        selected = activeLevelId == lvl.id,
                        onClick = {
                            selectedLevelId = lvl.id
                            selectedFilter = "all"
                        },
                        label = {
                            Text(lvl.title.split(":").firstOrNull() ?: lvl.title)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Search bar
        val searchPlaceholder = if (expLang == "en") {
            UiLocalizer.getString("learn_search_placeholder_en", uiLang)
        } else {
            UiLocalizer.getString("learn_search_placeholder_id", uiLang)
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(searchPlaceholder) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic Unit & Bookmarks Filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == "all",
                    onClick = { selectedFilter = "all" },
                    label = { Text(UiLocalizer.getString("learn_all_words", uiLang, words.size)) }
                )
            }
            items(availableUnits) { unitId ->
                val unitWordCount = words.count { it.unitId == unitId }
                val unitLabel = unitId.replace("_", " ").replaceFirstChar { it.uppercase() }
                FilterChip(
                    selected = selectedFilter == unitId,
                    onClick = { selectedFilter = unitId },
                    label = { Text("$unitLabel ($unitWordCount)") }
                )
            }
            item {
                val favCountInLevel = words.count { favIds.contains(it.id) }
                FilterChip(
                    selected = selectedFilter == "favorites",
                    onClick = { selectedFilter = "favorites" },
                    label = { Text("★ " + UiLocalizer.getString("learn_favorites", uiLang, favCountInLevel)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Flashcard practice button banner
        if (filteredWords.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isFlashcardMode = true },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = "Flashcards",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.padding(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = UiLocalizer.getString("learn_flashcard_title", uiLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = UiLocalizer.getString("learn_flashcard_desc", uiLang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Vocabulary word cards list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredWords, key = { it.id }) { word ->
                WordCard(
                    word = word,
                    isFavorite = favIds.contains(word.id),
                    uiLanguage = uiLang,
                    explanationLanguage = expLang,
                    onToggleFavorite = {
                        scope.launch {
                            repo.toggleFavorite(word.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FlashcardSessionView(
    words: List<Word>,
    uiLanguage: String,
    explanationLanguage: String,
    onClose: () -> Unit,
    onWordReviewed: (String, ReviewRating) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val currentWord = words[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close flashcards")
            }
            Text(
                text = "${currentIndex + 1} / ${words.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(24.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Flip Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { isFlipped = !isFlipped },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFlipped)
                    MaterialTheme.colorScheme.surfaceVariant
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val tts = LexiLadderApp.instance.ttsManager

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isFlipped) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currentWord.text,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { tts.speak(currentWord.text) }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Pronounce word",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        currentWord.phonetic?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentWord.partOfSpeech.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = UiLocalizer.getString("learn_tap_flip", uiLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        // Back of card
                        if (explanationLanguage == "id") {
                            currentWord.translation?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentWord.definition,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            // Direct English Immersion: emphasize definition
                            Text(
                                text = UiLocalizer.getString("learn_definition_label", uiLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentWord.definition,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "\"${currentWord.exampleSentence}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { tts.speak(currentWord.exampleSentence) }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Pronounce sentence",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Self-evaluation buttons
        AnimatedVisibility(visible = isFlipped) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        onWordReviewed(currentWord.id, ReviewRating.HARD)
                        isFlipped = false
                        currentIndex = (currentIndex + 1) % words.size
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = UiLocalizer.getString("learn_rating_hard", uiLanguage))
                }

                Button(
                    onClick = {
                        onWordReviewed(currentWord.id, ReviewRating.GOOD)
                        isFlipped = false
                        currentIndex = (currentIndex + 1) % words.size
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = UiLocalizer.getString("learn_rating_good", uiLanguage))
                }

                Button(
                    onClick = {
                        onWordReviewed(currentWord.id, ReviewRating.EASY)
                        isFlipped = false
                        currentIndex = (currentIndex + 1) % words.size
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = UiLocalizer.getString("learn_rating_easy", uiLanguage))
                }
            }
        }

        AnimatedVisibility(visible = !isFlipped) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (currentIndex > 0) currentIndex--
                    },
                    enabled = currentIndex > 0
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                }

                Button(onClick = { isFlipped = true }) {
                    Text(UiLocalizer.getString("learn_show_meaning", uiLanguage))
                }

                IconButton(
                    onClick = {
                        if (currentIndex < words.size - 1) currentIndex++
                    },
                    enabled = currentIndex < words.size - 1
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Next")
                }
            }
        }
    }
}

@Composable
fun WordCard(
    word: Word,
    isFavorite: Boolean,
    uiLanguage: String = "en",
    explanationLanguage: String = "id",
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tts = LexiLadderApp.instance.ttsManager

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = word.text,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    word.phonetic?.let {
                        Text(
                            text = "  $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { tts.speak(word.text) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Pronounce word",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val unitDisplay = word.unitId?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "Unit 1"
                Text(
                    text = "${word.partOfSpeech.replaceFirstChar { it.uppercase() }} • $unitDisplay",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (explanationLanguage == "en") {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("Immersion", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = word.definition,
                style = MaterialTheme.typography.bodyLarge
            )

            // Show translation only when explanation language is NOT English immersion
            if (explanationLanguage == "id") {
                word.translation?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${UiLocalizer.getString("learn_meaning_label", uiLanguage)}$it",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "\"${word.exampleSentence}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { tts.speak(word.exampleSentence) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Pronounce example sentence",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
