package com.dearyoti.lexiladder.data.repository

import com.dearyoti.lexiladder.data.local.dao.LevelDao
import com.dearyoti.lexiladder.data.local.dao.MasteryTestDao
import com.dearyoti.lexiladder.data.local.dao.UserProgressDao
import com.dearyoti.lexiladder.data.local.dao.WordDao
import com.dearyoti.lexiladder.data.local.entity.LevelEntity
import com.dearyoti.lexiladder.data.local.entity.MasteryTestAttemptEntity
import com.dearyoti.lexiladder.data.local.entity.UserProgressEntity
import com.dearyoti.lexiladder.data.preferences.UserPreferencesRepository
import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.model.MasteryTestAttempt
import com.dearyoti.lexiladder.domain.model.ReviewRating
import com.dearyoti.lexiladder.domain.model.UserProgress
import com.dearyoti.lexiladder.domain.model.Word
import com.dearyoti.lexiladder.domain.scheduler.SpacedRepetitionScheduler
import com.dearyoti.lexiladder.domain.service.MasteryTestEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class VocabularyRepositoryImpl(
    private val wordDao: WordDao,
    private val userProgressDao: UserProgressDao,
    private val levelDao: LevelDao,
    private val masteryTestDao: MasteryTestDao,
    private val userPreferencesRepository: UserPreferencesRepository? = null
) : VocabularyRepository {

    override fun getAllWords(): Flow<List<Word>> {
        return wordDao.getAllWords().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override fun searchWords(query: String): Flow<List<Word>> {
        return wordDao.searchWords(query).map { list ->
            list.map { it.toDomainModel() }
        }
    }

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
        val prefsFlow = userPreferencesRepository?.userPreferencesFlow
            ?: kotlinx.coroutines.flow.flowOf(com.dearyoti.lexiladder.data.preferences.UserPreferences())
        return combine(
            levelDao.getAllLevels(),
            userProgressDao.getAllProgress(),
            prefsFlow
        ) { levels, _, prefs ->
            levels.map { levelEntity ->
                val isUnlocked = if (levelEntity.id.startsWith("level_exam_")) {
                    prefs.isExamBoosterUnlocked
                } else {
                    levelEntity.isUnlocked
                }
                val total = wordDao.getWordCountByLevel(levelEntity.id)
                val studied = userProgressDao.getStudiedWordCountByLevel(levelEntity.id)
                val mastered = userProgressDao.getMasteredWordCountByLevel(levelEntity.id)
                val hasPassed = masteryTestDao.hasPassedLevel(levelEntity.id)
                val isMastered = levelEntity.isMastered || hasPassed
                val status = MasteryTestEngine.determineLevelStatus(
                    isUnlocked = isUnlocked,
                    isMastered = isMastered,
                    totalWords = total,
                    studiedWords = studied,
                    masteredWords = mastered
                )
                levelEntity.toDomainModel(
                    totalWords = total,
                    studiedWords = studied,
                    masteredWords = mastered,
                    status = status
                ).copy(isUnlocked = isUnlocked)
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

    override fun getWordsDueForReview(currentTime: Long): Flow<List<Word>> {
        return combine(
            wordDao.getAllWords(),
            userProgressDao.getWordsDueForReviewFlow(currentTime)
        ) { words, dueProgress ->
            val dueIds = dueProgress.map { it.wordId }.toSet()
            words.filter { dueIds.contains(it.id) }.map { it.toDomainModel() }
        }
    }

    override fun getDueReviewCount(currentTime: Long): Flow<Int> {
        return userProgressDao.getDueReviewCount(currentTime)
    }

    override fun getDifficultWords(): Flow<List<Word>> {
        return combine(
            wordDao.getAllWords(),
            userProgressDao.getDifficultWords()
        ) { words, diffProgress ->
            val diffIds = diffProgress.map { it.wordId }.toSet()
            words.filter { diffIds.contains(it.id) }.map { it.toDomainModel() }
        }
    }

    override fun getTestAttempts(levelId: String): Flow<List<MasteryTestAttempt>> {
        return masteryTestDao.getAttemptsByLevel(levelId).map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun submitMasteryTest(attempt: MasteryTestAttempt): Boolean {
        masteryTestDao.insertAttempt(MasteryTestAttemptEntity.fromDomainModel(attempt))
        userPreferencesRepository?.recordDailyStudyActivity()
        if (attempt.isPassed) {
            levelDao.setLevelMastered(attempt.levelId, true)
            val currentLevel = levelDao.getLevelById(attempt.levelId)
            if (currentLevel != null) {
                val nextLocked = levelDao.getNextLockedLevel(currentLevel.order)
                if (nextLocked != null) {
                    levelDao.setLevelUnlocked(nextLocked.id, true)
                    return true
                }
            }
        }
        return false
    }

    override suspend fun unlockLevel(levelId: String) {
        levelDao.setLevelUnlocked(levelId, true)
    }

    override suspend fun recordReviewRating(wordId: String, rating: ReviewRating, now: Long) {
        val existingEntity = userProgressDao.getProgressForWord(wordId)
        val currentProgress = existingEntity?.toDomainModel() ?: UserProgress(wordId = wordId)
        val updatedProgress = SpacedRepetitionScheduler.scheduleNextReview(currentProgress, rating, now)
        userProgressDao.saveProgress(UserProgressEntity.fromDomainModel(updatedProgress))
        userPreferencesRepository?.recordDailyStudyActivity()
    }

    override suspend fun recordReviewResult(wordId: String, isCorrect: Boolean) {
        val rating = if (isCorrect) ReviewRating.GOOD else ReviewRating.WRONG
        recordReviewRating(wordId, rating)
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
                        isUnlocked = true,
                        isMastered = false
                    ),
                    LevelEntity(
                        id = "level_a2",
                        title = "Level 2: Elementary",
                        description = "Expanding topics and common routines",
                        order = 2,
                        isUnlocked = false,
                        isMastered = false
                    ),
                    LevelEntity(
                        id = "level_b1",
                        title = "Level 3: Intermediate",
                        description = "Work, travel, and personal experiences",
                        order = 3,
                        isUnlocked = false,
                        isMastered = false
                    )
                )
            )
        }

        // Populate/update built-in vocabulary catalog (600 words: A1 = 150, A2 = 200, B1 = 250)
        wordDao.insertWords(DefaultVocabularyCatalog.allWords)
    }
}
