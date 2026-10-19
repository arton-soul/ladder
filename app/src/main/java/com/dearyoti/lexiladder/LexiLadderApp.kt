package com.dearyoti.lexiladder

import android.app.Application
import com.dearyoti.lexiladder.core.ads.AdManager
import com.dearyoti.lexiladder.core.billing.BillingManager
import com.dearyoti.lexiladder.data.content.ContentUpdateManager
import com.dearyoti.lexiladder.data.local.AppDatabase
import com.dearyoti.lexiladder.data.preferences.UserPreferencesRepository
import com.dearyoti.lexiladder.data.repository.VocabularyRepository
import com.dearyoti.lexiladder.data.repository.VocabularyRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LexiLadderApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var database: AppDatabase
        private set

    lateinit var vocabularyRepository: VocabularyRepository
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    lateinit var contentUpdateManager: ContentUpdateManager
        private set

    lateinit var billingManager: BillingManager
        private set

    val adManager: AdManager
        get() = AdManager.getInstance()

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        vocabularyRepository = VocabularyRepositoryImpl(
            wordDao = database.wordDao(),
            userProgressDao = database.userProgressDao(),
            levelDao = database.levelDao(),
            masteryTestDao = database.masteryTestDao()
        )
        userPreferencesRepository = UserPreferencesRepository(this)
        billingManager = BillingManager(
            context = this,
            userPreferencesRepository = userPreferencesRepository,
            coroutineScope = applicationScope
        )
        contentUpdateManager = ContentUpdateManager(
            context = this,
            database = database,
            currentAppVersion = 1
        )

        // Initialize AdMob MobileAds with safe test/prod IDs
        adManager.initialize(this)

        applicationScope.launch {
            vocabularyRepository.seedInitialDataIfEmpty()
        }
    }

    companion object {
        lateinit var instance: LexiLadderApp
            private set
    }
}
