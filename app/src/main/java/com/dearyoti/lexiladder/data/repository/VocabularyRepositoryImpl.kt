package com.dearyoti.lexiladder.data.repository

import com.dearyoti.lexiladder.data.local.dao.LevelDao
import com.dearyoti.lexiladder.data.local.dao.UserProgressDao
import com.dearyoti.lexiladder.data.local.dao.WordDao
import com.dearyoti.lexiladder.data.local.entity.LevelEntity
import com.dearyoti.lexiladder.data.local.entity.UserProgressEntity
import com.dearyoti.lexiladder.data.local.entity.WordEntity
import com.dearyoti.lexiladder.domain.model.Level
import com.dearyoti.lexiladder.domain.model.ReviewRating
import com.dearyoti.lexiladder.domain.model.UserProgress
import com.dearyoti.lexiladder.domain.model.Word
import com.dearyoti.lexiladder.domain.scheduler.SpacedRepetitionScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class VocabularyRepositoryImpl(
    private val wordDao: WordDao,
    private val userProgressDao: UserProgressDao,
    private val levelDao: LevelDao
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

    override suspend fun recordReviewRating(wordId: String, rating: ReviewRating, now: Long) {
        val existingEntity = userProgressDao.getProgressForWord(wordId)
        val currentProgress = existingEntity?.toDomainModel() ?: UserProgress(wordId = wordId)
        val updatedProgress = SpacedRepetitionScheduler.scheduleNextReview(currentProgress, rating, now)
        userProgressDao.saveProgress(UserProgressEntity.fromDomainModel(updatedProgress))
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
                    // Unit 1: Mindset & Action (10 words)
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
                    ),
                    WordEntity(
                        id = "w_a1_006",
                        text = "Focus",
                        partOfSpeech = "verb",
                        definition = "To pay particular attention to something",
                        exampleSentence = "Please focus on your pronunciation during practice.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/ˈfoʊ.kəs/",
                        translation = "Fokus / Memusatkan perhatian"
                    ),
                    WordEntity(
                        id = "w_a1_007",
                        text = "Growth",
                        partOfSpeech = "noun",
                        definition = "The process of developing or maturing",
                        exampleSentence = "Daily practice leads to steady growth in language skills.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/ɡroʊθ/",
                        translation = "Pertumbuhan / Perkembangan"
                    ),
                    WordEntity(
                        id = "w_a1_008",
                        text = "Habit",
                        partOfSpeech = "noun",
                        definition = "A settled or regular tendency or practice",
                        exampleSentence = "Reading five minutes a day is an excellent habit.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/ˈhæb.ɪt/",
                        translation = "Kebiasaan"
                    ),
                    WordEntity(
                        id = "w_a1_009",
                        text = "Inspire",
                        partOfSpeech = "verb",
                        definition = "To fill someone with the urge or ability to do something creative",
                        exampleSentence = "Her dedication inspired all the other students in the room.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/ɪnˈspaɪr/",
                        translation = "Menginspirasi / Mengilhami"
                    ),
                    WordEntity(
                        id = "w_a1_010",
                        text = "Journey",
                        partOfSpeech = "noun",
                        definition = "An act of traveling from one place to another",
                        exampleSentence = "Learning a language is an exciting long-term journey.",
                        levelId = "level_a1",
                        unitId = "unit_1",
                        phonetic = "/ˈdʒɜːr.ni/",
                        translation = "Perjalanan"
                    ),
                    // Unit 2: Life & Surroundings (10 words)
                    WordEntity(
                        id = "w_a1_011",
                        text = "Knowledge",
                        partOfSpeech = "noun",
                        definition = "Facts, information, and skills acquired through experience or education",
                        exampleSentence = "Books are an endless source of knowledge and wisdom.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˈnɑː.lɪdʒ/",
                        translation = "Pengetahuan"
                    ),
                    WordEntity(
                        id = "w_a1_012",
                        text = "Listen",
                        partOfSpeech = "verb",
                        definition = "To give attention to sound or action",
                        exampleSentence = "Listen carefully to native speakers to improve accent.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˈlɪs.ən/",
                        translation = "Mendengarkan"
                    ),
                    WordEntity(
                        id = "w_a1_013",
                        text = "Memory",
                        partOfSpeech = "noun",
                        definition = "The faculty by which the mind stores and remembers information",
                        exampleSentence = "Flashcards strengthen your long-term memory for words.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˈmem.ər.i/",
                        translation = "Ingatan / Memori"
                    ),
                    WordEntity(
                        id = "w_a1_014",
                        text = "Nature",
                        partOfSpeech = "noun",
                        definition = "The physical world collective, including plants and animals",
                        exampleSentence = "Taking a walk in nature refreshes the tired mind.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˈneɪ.tʃər/",
                        translation = "Alam / Alam sekitar"
                    ),
                    WordEntity(
                        id = "w_a1_015",
                        text = "Opportunity",
                        partOfSpeech = "noun",
                        definition = "A set of circumstances that makes it possible to do something",
                        exampleSentence = "Do not miss the opportunity to practice English today.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˌɑː.pɚˈtuː.nə.t̬i/",
                        translation = "Peluang / Kesempatan"
                    ),
                    WordEntity(
                        id = "w_a1_016",
                        text = "Patience",
                        partOfSpeech = "noun",
                        definition = "The capacity to accept delay or suffering without getting angry",
                        exampleSentence = "Mastering vocabulary requires consistency and patience.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˈpeɪ.ʃəns/",
                        translation = "Kesabaran"
                    ),
                    WordEntity(
                        id = "w_a1_017",
                        text = "Quiet",
                        partOfSpeech = "adjective",
                        definition = "Making little or no noise",
                        exampleSentence = "Find a quiet room so you can study without distractions.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˈkwaɪ.ət/",
                        translation = "Tenang / Sunyi"
                    ),
                    WordEntity(
                        id = "w_a1_018",
                        text = "Reliable",
                        partOfSpeech = "adjective",
                        definition = "Consistently good in quality or performance; able to be trusted",
                        exampleSentence = "A good dictionary is a reliable tool for learners.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/rɪˈlaɪ.ə.bəl/",
                        translation = "Dapat diandalkan"
                    ),
                    WordEntity(
                        id = "w_a1_019",
                        text = "Simple",
                        partOfSpeech = "adjective",
                        definition = "Easily understood or done; presenting no difficulty",
                        exampleSentence = "Start with simple sentences before writing complex essays.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˈsɪm.pəl/",
                        translation = "Sederhana / Mudah"
                    ),
                    WordEntity(
                        id = "w_a1_020",
                        text = "Treasure",
                        partOfSpeech = "noun",
                        definition = "A quantity of precious items or a prized possession",
                        exampleSentence = "Every new word you learn is a treasure for your communication.",
                        levelId = "level_a1",
                        unitId = "unit_2",
                        phonetic = "/ˈtreʒ.ɚ/",
                        translation = "Harta berharga / Harta karun"
                    )
                )
            )
        }
    }
}
