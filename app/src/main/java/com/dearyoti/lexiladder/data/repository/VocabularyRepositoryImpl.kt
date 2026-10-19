package com.dearyoti.lexiladder.data.repository

import com.dearyoti.lexiladder.data.local.dao.LevelDao
import com.dearyoti.lexiladder.data.local.dao.MasteryTestDao
import com.dearyoti.lexiladder.data.local.dao.UserProgressDao
import com.dearyoti.lexiladder.data.local.dao.WordDao
import com.dearyoti.lexiladder.data.local.entity.LevelEntity
import com.dearyoti.lexiladder.data.local.entity.MasteryTestAttemptEntity
import com.dearyoti.lexiladder.data.local.entity.UserProgressEntity
import com.dearyoti.lexiladder.data.local.entity.WordEntity
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
    private val masteryTestDao: MasteryTestDao
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
                val studied = userProgressDao.getStudiedWordCountByLevel(levelEntity.id)
                val mastered = userProgressDao.getMasteredWordCountByLevel(levelEntity.id)
                val hasPassed = masteryTestDao.hasPassedLevel(levelEntity.id)
                val isMastered = levelEntity.isMastered || hasPassed
                val status = MasteryTestEngine.determineLevelStatus(
                    isUnlocked = levelEntity.isUnlocked,
                    isMastered = isMastered,
                    totalWords = total,
                    studiedWords = studied
                )
                levelEntity.toDomainModel(
                    totalWords = total,
                    studiedWords = studied,
                    masteredWords = mastered,
                    status = status
                )
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

        wordDao.insertWords(
                listOf(
                    // Level 1 - Unit 1: Mindset & Action (10 words)
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
                    // Level 1 - Unit 2: Life & Surroundings (10 words)
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
                    ),
                    // Level 1 - Unit 3: Daily Routine & Health (10 words)
                    WordEntity(
                        id = "w_a1_021",
                        text = "Breakfast",
                        partOfSpeech = "noun",
                        definition = "The first meal of the day eaten after waking up",
                        exampleSentence = "Eating a balanced breakfast gives you energy for morning study.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/ˈbrek.fəst/",
                        translation = "Sarapan"
                    ),
                    WordEntity(
                        id = "w_a1_022",
                        text = "Prepare",
                        partOfSpeech = "verb",
                        definition = "To make something ready for use or consideration",
                        exampleSentence = "She helped her brother prepare for the English exam.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/prɪˈper/",
                        translation = "Mempersiapkan / Menyiapkan"
                    ),
                    WordEntity(
                        id = "w_a1_023",
                        text = "Fresh",
                        partOfSpeech = "adjective",
                        definition = "Recently made, produced, or harvested; not stale",
                        exampleSentence = "Drink fresh water and take short breaks while studying.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/freʃ/",
                        translation = "Segar"
                    ),
                    WordEntity(
                        id = "w_a1_024",
                        text = "Healthy",
                        partOfSpeech = "adjective",
                        definition = "Indicating or promoting good physical or mental health",
                        exampleSentence = "A healthy routine includes adequate sleep and daily exercise.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/ˈhel.θi/",
                        translation = "Sehat"
                    ),
                    WordEntity(
                        id = "w_a1_025",
                        text = "Exercise",
                        partOfSpeech = "noun",
                        definition = "Physical or mental activity that enhances fitness or skill",
                        exampleSentence = "Regular exercise improves concentration and brain health.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/ˈek.sɚ.saɪz/",
                        translation = "Latihan / Olahraga"
                    ),
                    WordEntity(
                        id = "w_a1_026",
                        text = "Relax",
                        partOfSpeech = "verb",
                        definition = "To rest from work or anxiety",
                        exampleSentence = "Listen to gentle music to relax after a busy day.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/rɪˈlæks/",
                        translation = "Bersantai / Rileks"
                    ),
                    WordEntity(
                        id = "w_a1_027",
                        text = "Clean",
                        partOfSpeech = "adjective",
                        definition = "Free from dirt, marks, or unwanted matter",
                        exampleSentence = "Keep your study desk clean to help you stay organized.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/kliːn/",
                        translation = "Bersih"
                    ),
                    WordEntity(
                        id = "w_a1_028",
                        text = "Market",
                        partOfSpeech = "noun",
                        definition = "A regular gathering of people for buying and selling provisions",
                        exampleSentence = "We bought fruits and vegetables at the local market.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/ˈmɑːr.kɪt/",
                        translation = "Pasar"
                    ),
                    WordEntity(
                        id = "w_a1_029",
                        text = "Delicious",
                        partOfSpeech = "adjective",
                        definition = "Highly pleasing to the sense of taste",
                        exampleSentence = "The host served a delicious dinner to the guests.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/dɪˈlɪʃ.əs/",
                        translation = "Lezat / Enak"
                    ),
                    WordEntity(
                        id = "w_a1_030",
                        text = "Routine",
                        partOfSpeech = "noun",
                        definition = "A regular sequence of actions or tasks",
                        exampleSentence = "A consistent morning routine helps establish productive habits.",
                        levelId = "level_a1",
                        unitId = "unit_3",
                        phonetic = "/ruːˈtiːn/",
                        translation = "Rutinitas"
                    ),
                    // Level 1 - Unit 4: Travel & Social (10 words)
                    WordEntity(
                        id = "w_a1_031",
                        text = "Friendly",
                        partOfSpeech = "adjective",
                        definition = "Kind, pleasant, or affectionate toward others",
                        exampleSentence = "The local residents were extremely friendly to tourists.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/ˈfrend.li/",
                        translation = "Ramah / Bersahabat"
                    ),
                    WordEntity(
                        id = "w_a1_032",
                        text = "Welcome",
                        partOfSpeech = "verb",
                        definition = "To greet someone arriving with pleasure or hospitality",
                        exampleSentence = "The school director gave a speech to welcome new students.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/ˈwel.kəm/",
                        translation = "Menyambut"
                    ),
                    WordEntity(
                        id = "w_a1_033",
                        text = "Direction",
                        partOfSpeech = "noun",
                        definition = "A course along which someone or something moves",
                        exampleSentence = "Excuse me, could you give me direction to the nearest train station?",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/daɪˈrek.ʃən/",
                        translation = "Arah / Petunjuk arah"
                    ),
                    WordEntity(
                        id = "w_a1_034",
                        text = "Ticket",
                        partOfSpeech = "noun",
                        definition = "A printed slip entitling the holder to travel or enter an event",
                        exampleSentence = "Remember to show your boarding ticket at the departure gate.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/ˈtɪk.ɪt/",
                        translation = "Tiket / Karcis"
                    ),
                    WordEntity(
                        id = "w_a1_035",
                        text = "Station",
                        partOfSpeech = "noun",
                        definition = "A regular stopping place along a route where passengers board",
                        exampleSentence = "Meet me at the central station entrance at ten o'clock.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/ˈsteɪ.ʃən/",
                        translation = "Stasiun"
                    ),
                    WordEntity(
                        id = "w_a1_036",
                        text = "Neighbor",
                        partOfSpeech = "noun",
                        definition = "A person living near or next door to you",
                        exampleSentence = "Our kind neighbor watered our plants while we were on vacation.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/ˈneɪ.bɚ/",
                        translation = "Tetangga"
                    ),
                    WordEntity(
                        id = "w_a1_037",
                        text = "Gather",
                        partOfSpeech = "verb",
                        definition = "To come together or collect into one group",
                        exampleSentence = "Friends gather at the cafe every weekend to speak English.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/ˈɡæð.ɚ/",
                        translation = "Berkumpul"
                    ),
                    WordEntity(
                        id = "w_a1_038",
                        text = "Share",
                        partOfSpeech = "verb",
                        definition = "To give a portion of something to another person",
                        exampleSentence = "It is polite to share your notes with classmates who missed school.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/ʃer/",
                        translation = "Berbagi"
                    ),
                    WordEntity(
                        id = "w_a1_039",
                        text = "Smile",
                        partOfSpeech = "noun",
                        definition = "A pleased, kind, or amused facial expression",
                        exampleSentence = "A warm smile breaks cultural barriers anywhere in the world.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/smaɪl/",
                        translation = "Senyuman"
                    ),
                    WordEntity(
                        id = "w_a1_040",
                        text = "Together",
                        partOfSpeech = "adverb",
                        definition = "With or in proximity to another person or people",
                        exampleSentence = "We can achieve much more when we work together.",
                        levelId = "level_a1",
                        unitId = "unit_4",
                        phonetic = "/təˈɡeð.ɚ/",
                        translation = "Bersama-sama"
                    ),
                    // Level 2 - Unit 1: Everyday Communication (10 words)
                    WordEntity(
                        id = "w_a2_001",
                        text = "Adapt",
                        partOfSpeech = "verb",
                        definition = "To adjust to new conditions",
                        exampleSentence = "Living abroad helps you adapt to different lifestyles.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/əˈdæpt/",
                        translation = "Menyesuaikan diri / Beradaptasi"
                    ),
                    WordEntity(
                        id = "w_a2_002",
                        text = "Balance",
                        partOfSpeech = "noun",
                        definition = "A situation in which different elements are equal",
                        exampleSentence = "Work-life balance is essential for health.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/ˈbæl.əns/",
                        translation = "Keseimbangan"
                    ),
                    WordEntity(
                        id = "w_a2_003",
                        text = "Celebrate",
                        partOfSpeech = "verb",
                        definition = "To honor a special event or achievement",
                        exampleSentence = "We gathered with friends to celebrate her graduation.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/ˈsel.ə.breɪt/",
                        translation = "Merayakan"
                    ),
                    WordEntity(
                        id = "w_a2_004",
                        text = "Decide",
                        partOfSpeech = "verb",
                        definition = "To make a choice from a number of alternatives",
                        exampleSentence = "Take your time to decide which path to follow.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/dɪˈsaɪd/",
                        translation = "Memutuskan / Menentukan"
                    ),
                    WordEntity(
                        id = "w_a2_005",
                        text = "Efficient",
                        partOfSpeech = "adjective",
                        definition = "Achieving maximum productivity with minimum wasted effort",
                        exampleSentence = "An efficient schedule saves hours every week.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/ɪˈfɪʃ.ənt/",
                        translation = "Efisien / Berdaya guna"
                    ),
                    WordEntity(
                        id = "w_a2_006",
                        text = "Frequent",
                        partOfSpeech = "adjective",
                        definition = "Occurring or done on many occasions with short intervals",
                        exampleSentence = "Frequent repetition builds lasting fluency.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/ˈfriː.kwənt/",
                        translation = "Sering / Kerap"
                    ),
                    WordEntity(
                        id = "w_a2_007",
                        text = "Gratitude",
                        partOfSpeech = "noun",
                        definition = "The quality of being thankful",
                        exampleSentence = "Expressing gratitude brightens someone's day.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/ˈɡræt̬.ə.tuːd/",
                        translation = "Rasa syukur / Terima kasih"
                    ),
                    WordEntity(
                        id = "w_a2_008",
                        text = "Harmony",
                        partOfSpeech = "noun",
                        definition = "Agreement or concord",
                        exampleSentence = "The team worked in perfect harmony to complete the project.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/ˈhɑːr.mə.ni/",
                        translation = "Keharmonisan / Keselarasan"
                    ),
                    WordEntity(
                        id = "w_a2_009",
                        text = "Impact",
                        partOfSpeech = "noun",
                        definition = "A marked effect or influence",
                        exampleSentence = "Small consistent actions create a big positive impact.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/ˈɪm.pækt/",
                        translation = "Dampak / Pengaruh"
                    ),
                    WordEntity(
                        id = "w_a2_010",
                        text = "Joyful",
                        partOfSpeech = "adjective",
                        definition = "Feeling, expressing, or causing great pleasure and happiness",
                        exampleSentence = "The family shared a joyful meal together.",
                        levelId = "level_a2",
                        unitId = "unit_1",
                        phonetic = "/ˈdʒɔɪ.fəl/",
                        translation = "Gembira / Penuh sukacita"
                    ),
                    // Level 2 - Unit 2: Work & Ambition (10 words)
                    WordEntity(
                        id = "w_a2_011",
                        text = "Career",
                        partOfSpeech = "noun",
                        definition = "An occupation undertaken for a significant period of a person's life",
                        exampleSentence = "Learning English opens doors to exciting career opportunities.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/kəˈrɪr/",
                        translation = "Karier / Pekerjaan"
                    ),
                    WordEntity(
                        id = "w_a2_012",
                        text = "Project",
                        partOfSpeech = "noun",
                        definition = "An individual or collaborative enterprise carefully planned",
                        exampleSentence = "Our team delivered the software project ahead of schedule.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/ˈprɑː.dʒekt/",
                        translation = "Proyek / Rencana kerja"
                    ),
                    WordEntity(
                        id = "w_a2_013",
                        text = "Solution",
                        partOfSpeech = "noun",
                        definition = "A means of solving a problem or dealing with a difficult situation",
                        exampleSentence = "They brainstormed creative solutions to the transportation issue.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/səˈluː.ʃən/",
                        translation = "Solusi / Pemecahan masalah"
                    ),
                    WordEntity(
                        id = "w_a2_014",
                        text = "Challenge",
                        partOfSpeech = "noun",
                        definition = "A task or situation that tests someone's abilities",
                        exampleSentence = "She embraced the language challenge with enthusiasm and courage.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/ˈtʃæl.ɪndʒ/",
                        translation = "Tantangan"
                    ),
                    WordEntity(
                        id = "w_a2_015",
                        text = "Progress",
                        partOfSpeech = "noun",
                        definition = "Forward or onward movement toward a destination or goal",
                        exampleSentence = "Tracking your daily words provides visual proof of your progress.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/ˈprɑː.ɡres/",
                        translation = "Kemajuan / Progres"
                    ),
                    WordEntity(
                        id = "w_a2_016",
                        text = "Colleague",
                        partOfSpeech = "noun",
                        definition = "A person with whom one works in a profession or business",
                        exampleSentence = "He collaborated with a senior colleague to analyze market data.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/ˈkɑː.liːɡ/",
                        translation = "Rekan kerja"
                    ),
                    WordEntity(
                        id = "w_a2_017",
                        text = "Schedule",
                        partOfSpeech = "noun",
                        definition = "A plan that lists tasks and the times at which they will happen",
                        exampleSentence = "Check the conference schedule before booking your travel tickets.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/ˈskedʒ.uːl/",
                        translation = "Jadwal"
                    ),
                    WordEntity(
                        id = "w_a2_018",
                        text = "Succeed",
                        partOfSpeech = "verb",
                        definition = "To achieve the desired aim or result",
                        exampleSentence = "With consistent persistence, you will succeed in learning English.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/səkˈsiːd/",
                        translation = "Berhasil / Sukses"
                    ),
                    WordEntity(
                        id = "w_a2_019",
                        text = "Negotiate",
                        partOfSpeech = "verb",
                        definition = "To obtain or bring about by discussion",
                        exampleSentence = "The partners met to negotiate fair terms for the contract.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/nəˈɡoʊ.ʃi.eɪt/",
                        translation = "Berdialog / Bernegosiasi"
                    ),
                    WordEntity(
                        id = "w_a2_020",
                        text = "Creative",
                        partOfSpeech = "adjective",
                        definition = "Relating to or involving the imagination or original ideas",
                        exampleSentence = "She used creative illustrations to explain difficult grammar rules.",
                        levelId = "level_a2",
                        unitId = "unit_2",
                        phonetic = "/kriˈeɪ.t̬ɪv/",
                        translation = "Kreatif"
                    ),

                    // Level 2 - Unit 3: Society & Environment (10 words)
                    WordEntity(
                        id = "w_a2_021",
                        text = "Community",
                        partOfSpeech = "noun",
                        definition = "A group of people living in the same place or having shared characteristics",
                        exampleSentence = "The local community organized a weekly conversation club.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/kəˈmjuː.nə.t̬i/",
                        translation = "Komunitas / Masyarakat"
                    ),
                    WordEntity(
                        id = "w_a2_022",
                        text = "Culture",
                        partOfSpeech = "noun",
                        definition = "The customs, arts, social institutions, and achievements of a nation",
                        exampleSentence = "Traveling allows you to appreciate the richness of foreign culture.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/ˈkʌl.tʃɚ/",
                        translation = "Budaya / Kebudayaan"
                    ),
                    WordEntity(
                        id = "w_a2_023",
                        text = "Environment",
                        partOfSpeech = "noun",
                        definition = "The natural world, as a whole or in a particular geographical area",
                        exampleSentence = "We must take concrete steps to protect our natural environment.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/ɪnˈvaɪ.rən.mənt/",
                        translation = "Lingkungan hidup"
                    ),
                    WordEntity(
                        id = "w_a2_024",
                        text = "Protect",
                        partOfSpeech = "verb",
                        definition = "To keep safe from harm or injury",
                        exampleSentence = "Conservation laws protect endangered forest species.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/prəˈtekt/",
                        translation = "Melindungi"
                    ),
                    WordEntity(
                        id = "w_a2_025",
                        text = "Support",
                        partOfSpeech = "verb",
                        definition = "To give assistance, comfort, or financial help to someone",
                        exampleSentence = "Family members always support each other during tough times.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/səˈpɔːrt/",
                        translation = "Mendukung / Menopang"
                    ),
                    WordEntity(
                        id = "w_a2_026",
                        text = "Volunteer",
                        partOfSpeech = "verb",
                        definition = "To freely offer to do something without being paid",
                        exampleSentence = "Many university students volunteer at the literacy foundation.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/ˌvɑː.lənˈtɪr/",
                        translation = "Menjadi sukarelawan"
                    ),
                    WordEntity(
                        id = "w_a2_027",
                        text = "Global",
                        partOfSpeech = "adjective",
                        definition = "Relating to the whole world; worldwide",
                        exampleSentence = "English has become the global bridge language for business.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/ˈɡloʊ.bəl/",
                        translation = "Global / Seluruh dunia"
                    ),
                    WordEntity(
                        id = "w_a2_028",
                        text = "Opinion",
                        partOfSpeech = "noun",
                        definition = "A view or judgment formed about something, not necessarily based on fact",
                        exampleSentence = "Respecting everyone's opinion creates an open discussion atmosphere.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/əˈpɪn.jən/",
                        translation = "Pendapat / Opini"
                    ),
                    WordEntity(
                        id = "w_a2_029",
                        text = "Improve",
                        partOfSpeech = "verb",
                        definition = "To make or become better in quality",
                        exampleSentence = "Daily reading will noticeably improve your writing confidence.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/ɪmˈpruːv/",
                        translation = "Meningkatkan / Memperbaiki"
                    ),
                    WordEntity(
                        id = "w_a2_030",
                        text = "Respect",
                        partOfSpeech = "noun",
                        definition = "A feeling of deep admiration for someone elicited by their qualities",
                        exampleSentence = "Mutual respect is the cornerstone of every healthy friendship.",
                        levelId = "level_a2",
                        unitId = "unit_3",
                        phonetic = "/rɪˈspekt/",
                        translation = "Rasa hormat / Menghargai"
                    ),

                    // ==================== LEVEL 3 (20 WORDS) ====================
                    // Level 3 - Unit 1: Professional Growth (10 words)
                    WordEntity(
                        id = "w_b1_001",
                        text = "Leadership",
                        partOfSpeech = "noun",
                        definition = "The action of leading a group of people or an organization",
                        exampleSentence = "Effective leadership inspires confidence and collaboration.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/ˈliː.dɚ.ʃɪp/",
                        translation = "Kepemimpinan"
                    ),
                    WordEntity(
                        id = "w_b1_002",
                        text = "Strategy",
                        partOfSpeech = "noun",
                        definition = "A plan of action designed to achieve a long-term goal",
                        exampleSentence = "The company developed a new marketing strategy for digital platforms.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/ˈstræt̬.ə.dʒi/",
                        translation = "Strategi"
                    ),
                    WordEntity(
                        id = "w_b1_003",
                        text = "Innovation",
                        partOfSpeech = "noun",
                        definition = "The action or process of introducing new methods, ideas, or products",
                        exampleSentence = "Technological innovation has transformed modern education.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/ˌɪn.əˈveɪ.ʃən/",
                        translation = "Inovasi"
                    ),
                    WordEntity(
                        id = "w_b1_004",
                        text = "Analyze",
                        partOfSpeech = "verb",
                        definition = "To examine methodically and in detail the structure of something",
                        exampleSentence = "Researchers analyze user feedback to improve mobile software.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/ˈæn.əl.aɪz/",
                        translation = "Menganalisis"
                    ),
                    WordEntity(
                        id = "w_b1_005",
                        text = "Implement",
                        partOfSpeech = "verb",
                        definition = "To put a decision, plan, or agreement into effect",
                        exampleSentence = "The school decided to implement a bilingual curriculum.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/ˈɪm.plə.ment/",
                        translation = "Menerapkan / Mengimplementasikan"
                    ),
                    WordEntity(
                        id = "w_b1_006",
                        text = "Network",
                        partOfSpeech = "noun",
                        definition = "A group of interconnected people or things",
                        exampleSentence = "Attending seminars helps you build a strong professional network.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/ˈnet.wɝːk/",
                        translation = "Jaringan / Relasi"
                    ),
                    WordEntity(
                        id = "w_b1_007",
                        text = "Prioritize",
                        partOfSpeech = "verb",
                        definition = "To designate or treat something as more important than other things",
                        exampleSentence = "Learn to prioritize urgent tasks to avoid burnout.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/praɪˈɔːr.ə.taɪz/",
                        translation = "Memprioritaskan"
                    ),
                    WordEntity(
                        id = "w_b1_008",
                        text = "Adaptability",
                        partOfSpeech = "noun",
                        definition = "The quality of being able to adjust to new conditions",
                        exampleSentence = "Adaptability is one of the most valued traits in modern workplaces.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/əˌdæp.təˈbɪl.ə.t̬i/",
                        translation = "Kemampuan beradaptasi"
                    ),
                    WordEntity(
                        id = "w_b1_009",
                        text = "Vision",
                        partOfSpeech = "noun",
                        definition = "The ability to think about or plan the future with imagination or wisdom",
                        exampleSentence = "A clear vision helps teams stay focused during difficult projects.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/ˈvɪʒ.ən/",
                        translation = "Visi / Pandangan masa depan"
                    ),
                    WordEntity(
                        id = "w_b1_010",
                        text = "Resourceful",
                        partOfSpeech = "adjective",
                        definition = "Having the ability to find quick and clever ways to overcome difficulties",
                        exampleSentence = "The resourceful engineer fixed the machine using simple tools.",
                        levelId = "level_b1",
                        unitId = "unit_1",
                        phonetic = "/rɪˈzɔːr.sfəl/",
                        translation = "Banyak akal / Tanggap"
                    ),

                    // Level 3 - Unit 2: Culture & Philosophy (10 words)
                    WordEntity(
                        id = "w_b1_011",
                        text = "Perspective",
                        partOfSpeech = "noun",
                        definition = "A particular attitude toward or way of regarding something; a point of view",
                        exampleSentence = "Learning a second language gives you a fresh perspective on the world.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/pɚˈspek.tɪv/",
                        translation = "Perspektif / Sudut pandang"
                    ),
                    WordEntity(
                        id = "w_b1_012",
                        text = "Empathy",
                        partOfSpeech = "noun",
                        definition = "The ability to understand and share the feelings of another",
                        exampleSentence = "Practicing empathy helps resolve misunderstandings between friends.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/ˈem.pə.θi/",
                        translation = "Empati"
                    ),
                    WordEntity(
                        id = "w_b1_013",
                        text = "Integrity",
                        partOfSpeech = "noun",
                        definition = "The quality of being honest and having strong moral principles",
                        exampleSentence = "Leaders with high integrity earn the enduring trust of their team.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/ɪnˈteɡ.rə.t̬i/",
                        translation = "Integritas / Kejujuran moral"
                    ),
                    WordEntity(
                        id = "w_b1_014",
                        text = "Diverse",
                        partOfSpeech = "adjective",
                        definition = "Showing a great deal of variety; very different",
                        exampleSentence = "The international conference brought together people with diverse backgrounds.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/daɪˈvɝːs/",
                        translation = "Beragam / Majemuk"
                    ),
                    WordEntity(
                        id = "w_b1_015",
                        text = "Authentic",
                        partOfSpeech = "adjective",
                        definition = "Of undisputed origin; genuine",
                        exampleSentence = "Her authentic speaking style immediately connected with the audience.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/ɑːˈθen.t̬ɪk/",
                        translation = "Otentik / Asli"
                    ),
                    WordEntity(
                        id = "w_b1_016",
                        text = "Resilience",
                        partOfSpeech = "noun",
                        definition = "The capacity to recover quickly from difficulties; toughness",
                        exampleSentence = "Mental resilience enables people to bounce back from setbacks.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/rɪˈzɪl.jəns/",
                        translation = "Ketangguhan / Daya lenting"
                    ),
                    WordEntity(
                        id = "w_b1_017",
                        text = "Dialogue",
                        partOfSpeech = "noun",
                        definition = "A conversation between two or more people to exchange views",
                        exampleSentence = "Constructive dialogue is essential for peaceful community relations.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/ˈdaɪ.ə.lɑːɡ/",
                        translation = "Dialog / Percakapan"
                    ),
                    WordEntity(
                        id = "w_b1_018",
                        text = "Expression",
                        partOfSpeech = "noun",
                        definition = "The process of making known one's thoughts or feelings",
                        exampleSentence = "Music and poetry are universal forms of human expression.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/ɪkˈspreʃ.ən/",
                        translation = "Ungkapan / Ekspresi"
                    ),
                    WordEntity(
                        id = "w_b1_019",
                        text = "Influence",
                        partOfSpeech = "noun",
                        definition = "The capacity to have an effect on the character or behavior of someone",
                        exampleSentence = "Good books exert a lasting positive influence on young minds.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/ˈɪn.flu.əns/",
                        translation = "Pengaruh"
                    ),
                    WordEntity(
                        id = "w_b1_020",
                        text = "Wisdom",
                        partOfSpeech = "noun",
                        definition = "The quality of having experience, knowledge, and good judgment",
                        exampleSentence = "True wisdom comes from reflecting honestly on life experiences.",
                        levelId = "level_b1",
                        unitId = "unit_2",
                        phonetic = "/ˈwɪz.dəm/",
                        translation = "Kebijaksanaan"
                    )
                )
            )
    }
}
