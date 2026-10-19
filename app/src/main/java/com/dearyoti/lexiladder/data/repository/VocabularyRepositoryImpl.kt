package com.dearyoti.lexiladder.data.repository

import com.dearyoti.lexiladder.data.local.dao.LevelDao
import com.dearyoti.lexiladder.data.local.dao.UserProgressDao
import com.dearyoti.lexiladder.data.local.dao.WordDao
import com.dearyoti.lexiladder.data.local.entity.LevelEntity
import com.dearyoti.lexiladder.data.local.entity.UserProgressEntity
import com.dearyoti.lexiladder.data.local.entity.WordEntity
import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.model.UserProgress
import com.dearyoti.lexiladder.domain.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class VocabularyRepositoryImpl(
    private val wordDao: WordDao,
    private val userProgressDao: UserProgressDao,
    private val levelDao: LevelDao
) : VocabularyRepository {

    override fun getWordsByLevel(levelId: String): Flow<List<Word>> {
        return wordDao.getWordsByLevel(levelId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getWordsByUnit(unitId: String): Flow<List<Word>> {
        return wordDao.getWordsByUnit(unitId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun getAllLevels(): Flow<List<Level>> {
        return levelDao.getAllLevels().map { levels ->
            levels.map { levelEntity ->
                val total = wordDao.getWordCountByLevel(levelEntity.id)
                val mastered = userProgressDao.getMasteredWordCountByLevel(levelEntity.id)
                levelEntity.toDomainModel(totalWords = total, masteredWords = mastered)
            }
        }
    }

    override fun getMasteredWordCount(): Flow<Int> {
        return userProgressDao.getMasteredWordCount()
    }

    override fun getFavorites(): Flow<List<Word>> {
        return combine(
            wordDao.getAllWords(),
            userProgressDao.getFavoriteWords()
        ) { words, favorites ->
            val favIds = favorites.map { it.wordId }.toSet()
            words.filter { favIds.contains(it.id) }.map { it.toDomainModel() }
        }
    }

    override suspend fun getWordById(wordId: String): Word? {
        return wordDao.getWordById(wordId)?.toDomainModel()
    }

    override suspend fun getProgressForWord(wordId: String): UserProgress? {
        return userProgressDao.getProgressForWord(wordId)?.toDomainModel()
    }

    override suspend fun recordReviewResult(wordId: String, isCorrect: Boolean) {
        val existing = userProgressDao.getProgressForWord(wordId) ?: UserProgressEntity(wordId = wordId)
        val now = System.currentTimeMillis()

        val updatedCorrect = if (isCorrect) existing.correctCount + 1 else existing.correctCount
        val updatedIncorrect = if (!isCorrect) existing.incorrectCount + 1 else existing.incorrectCount
        val updatedReviews = existing.reviewCount + 1
        val isMastered = updatedCorrect >= 3 && (updatedCorrect.toFloat() / updatedReviews.toFloat()) >= 0.75f

        // Interval interval spaced repetition (ms)
        val nextIntervalMs = if (isCorrect) {
            when (updatedCorrect) {
                1 -> 24 * 60 * 60 * 1000L      // 1 day
                2 -> 3 * 24 * 60 * 60 * 1000L  // 3 days
                else -> 7 * 24 * 60 * 60 * 1000L // 7 days
            }
        } else {
            4 * 60 * 60 * 1000L // 4 hours
        }

        val updated = existing.copy(
            reviewCount = updatedReviews,
            correctCount = updatedCorrect,
            incorrectCount = updatedIncorrect,
            isMastered = isMastered,
            lastReviewedAt = now,
            nextReviewAt = now + nextIntervalMs
        )
        userProgressDao.saveProgress(updated)
    }

    override suspend fun toggleFavorite(wordId: String) {
        val existing = userProgressDao.getProgressForWord(wordId) ?: UserProgressEntity(wordId = wordId)
        val updated = existing.copy(isFavorite = !existing.isFavorite)
        userProgressDao.saveProgress(updated)
    }

    override suspend fun seedInitialDataIfEmpty() {
        val levels = levelDao.getAllLevels().firstOrNull()
        if (levels.isNullOrEmpty()) {
            levelDao.insertLevels(
                listOf(
                    LevelEntity(
                        id = "level_a1",
                        title = "Level 1: Beginner Starter",
                        description = "Essential vocabulary for everyday conversations",
                        order = 1,
                        isUnlocked = true
                    ),
                    LevelEntity(
                        id = "level_a2",
                        title = "Level 2: Elementary",
                        description = "Expanding topics and common routines",
                        order = 2,
                        isUnlocked = false
                    ),
                    LevelEntity(
                        id = "level_b1",
                        title = "Level 3: Intermediate",
                        description = "Work, travel, and personal experiences",
                        order = 3,
                        isUnlocked = false
                    )
                )
            )

            wordDao.insertWords(
                listOf(
                    WordEntity(
                        id = "w_a1_001",
                        text = "Achieve",
                        partOfSpeech = "verb",
                        definition = "To successfully complete something with effort or skill",
                        exampleSentence = "She worked hard to achieve her goal of becoming a doctor.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/əˈtʃiːv/",
                        translation = "Mencapai / Meraih"
                    ),
                    WordEntity(
                        id = "w_a1_002",
                        text = "Brief",
                        partOfSpeech = "adjective",
                        definition = "Lasting only a short period of time",
                        exampleSentence = "We had a brief meeting before the project started.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/briːf/",
                        translation = "Singkat / Ringkas"
                    ),
                    WordEntity(
                        id = "w_a1_003",
                        text = "Courage",
                        partOfSpeech = "noun",
                        definition = "The ability to do something that frightens someone",
                        exampleSentence = "It took a lot of courage to speak in front of the crowd.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/ˈkʌr.ɪdʒ/",
                        translation = "Keberanian"
                    ),
                    WordEntity(
                        id = "w_a1_004",
                        text = "Diligent",
                        partOfSpeech = "adjective",
                        definition = "Having or showing care and conscientiousness in one's work",
                        exampleSentence = "The diligent student studied vocabulary every evening.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/ˈdɪl.ə.dʒənt/",
                        translation = "Rajin / Tekun"
                    ),
                    WordEntity(
                        id = "w_a1_005",
                        text = "Explore",
                        partOfSpeech = "verb",
                        definition = "To travel around a new place to learn about it",
                        exampleSentence = "They decided to explore the ancient city on foot.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/ɪkˈsplɔːr/",
                        translation = "Menjelajahi / Mengeksplorasi"
                    )
                )
            )
        }
    }
}
