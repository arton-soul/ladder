package com.dearyoti.lexiladder.data.content

import android.content.Context
import androidx.room.withTransaction
import com.dearyoti.lexiladder.data.content.model.ContentManifest
import com.dearyoti.lexiladder.data.content.model.ContentPackageMetadata
import com.dearyoti.lexiladder.data.content.model.LearningPackage
import com.dearyoti.lexiladder.data.content.model.TranslationPackage
import com.dearyoti.lexiladder.data.content.model.UiCatalogPackage
import com.dearyoti.lexiladder.data.local.AppDatabase
import com.dearyoti.lexiladder.data.local.entity.InstalledPackageEntity
import com.dearyoti.lexiladder.data.local.entity.LevelEntity
import com.dearyoti.lexiladder.data.local.entity.WordEntity
import com.dearyoti.lexiladder.domain.content.ContentValidator
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

sealed interface ContentUpdateResult {
    data class Success(val packagesToUpdate: List<ContentPackageMetadata>, val manifestVersion: Int) : ContentUpdateResult
    data class UpToDate(val manifestVersion: Int) : ContentUpdateResult
    data class Offline(val message: String, val cachedPackages: List<ContentPackageMetadata>) : ContentUpdateResult
    data class Error(val error: String) : ContentUpdateResult
}

class ContentUpdateManager(
    private val context: Context,
    private val database: AppDatabase,
    private val gson: Gson = Gson(),
    private val currentAppVersion: Int = 1
) {

    private val installedPackageDao = database.installedPackageDao()
    private val wordDao = database.wordDao()
    private val levelDao = database.levelDao()

    fun getInstalledPackages(): Flow<List<InstalledPackageEntity>> {
        return installedPackageDao.getAllInstalledPackages()
    }

    suspend fun seedBundledPackagesIfEmpty() = withContext(Dispatchers.IO) {
        val existing = installedPackageDao.getAllInstalledPackages().firstOrNull()
        if (existing.isNullOrEmpty()) {
            try {
                val manifest = readBundledManifest()
                for (pkg in manifest.packages) {
                    installedPackageDao.insertOrUpdatePackage(
                        InstalledPackageEntity(
                            packageId = pkg.packageId,
                            type = pkg.type,
                            locale = pkg.locale,
                            contentVersion = pkg.contentVersion,
                            schemaVersion = pkg.schemaVersion,
                            installedAt = System.currentTimeMillis(),
                            sha256 = pkg.sha256
                        )
                    )
                }
            } catch (_: Exception) {
                // Ignore bundled read failure
            }
        }
    }

    suspend fun checkUpdates(remoteManifestUrl: String? = null): ContentUpdateResult = withContext(Dispatchers.IO) {
        seedBundledPackagesIfEmpty()
        val manifest: ContentManifest = try {
            if (!remoteManifestUrl.isNullOrBlank()) {
                fetchRemoteManifest(remoteManifestUrl)
            } else {
                readBundledManifest()
            }
        } catch (e: Exception) {
            // Network failure fallback to bundled manifest
            try {
                val bundled = readBundledManifest()
                return@withContext ContentUpdateResult.Offline(
                    message = "Network unavailable: Using local verified content (${e.message ?: "Offline"})",
                    cachedPackages = bundled.packages
                )
            } catch (bundleError: Exception) {
                return@withContext ContentUpdateResult.Error("Failed to read manifest: ${bundleError.message}")
            }
        }

        val packagesToUpdate = mutableListOf<ContentPackageMetadata>()

        for (pkg in manifest.packages) {
            // Reject packages with incompatible minimumAppVersion
            if (!ContentValidator.validateAppCompatibility(pkg.minimumAppVersion, currentAppVersion)) {
                continue
            }

            val installed = installedPackageDao.getInstalledPackage(pkg.packageId)
            if (installed == null || pkg.contentVersion > installed.contentVersion) {
                packagesToUpdate.add(pkg)
            }
        }

        if (packagesToUpdate.isNotEmpty()) {
            ContentUpdateResult.Success(packagesToUpdate, manifest.manifestVersion)
        } else {
            ContentUpdateResult.UpToDate(manifest.manifestVersion)
        }
    }

    suspend fun downloadAndApplyPackage(
        pkg: ContentPackageMetadata,
        overrideBytes: ByteArray? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 1. Minimum App Version check
            if (!ContentValidator.validateAppCompatibility(pkg.minimumAppVersion, currentAppVersion)) {
                return@withContext Result.failure(
                    IllegalArgumentException("Package requires app version ${pkg.minimumAppVersion} but running version $currentAppVersion")
                )
            }

            // 2. Fetch raw bytes
            val bytes = overrideBytes ?: if (pkg.url.startsWith("http://") || pkg.url.startsWith("https://")) {
                downloadBytes(pkg.url)
            } else {
                readAssetBytes(pkg.url)
            }

            // 3. Validate size
            val sizeValidation = ContentValidator.validateSize(bytes, pkg.sizeBytes)
            if (!sizeValidation.isValid) {
                return@withContext Result.failure(
                    IllegalArgumentException((sizeValidation as com.dearyoti.lexiladder.domain.content.ContentValidationResult.Invalid).reason)
                )
            }

            // 4. Validate SHA-256 Checksum
            if (!ContentValidator.validateChecksum(bytes, pkg.sha256)) {
                val actual = ContentValidator.calculateSha256(bytes)
                return@withContext Result.failure(
                    IllegalArgumentException("SHA-256 checksum mismatch. Expected: ${pkg.sha256}, Actual: $actual")
                )
            }

            // 5. Parse and validate schema
            val jsonString = String(bytes, Charsets.UTF_8)
            when (pkg.type) {
                "learning-package" -> {
                    val learningPkg = gson.fromJson(jsonString, LearningPackage::class.java)
                    val validation = ContentValidator.validateLearningPackage(learningPkg)
                    if (!validation.isValid) {
                        return@withContext Result.failure(
                            IllegalArgumentException((validation as com.dearyoti.lexiladder.domain.content.ContentValidationResult.Invalid).reason)
                        )
                    }
                    importLearningPackageAtomic(learningPkg, pkg)
                }
                "translation-package" -> {
                    val transPkg = gson.fromJson(jsonString, TranslationPackage::class.java)
                    val validation = ContentValidator.validateTranslationPackage(transPkg)
                    if (!validation.isValid) {
                        return@withContext Result.failure(
                            IllegalArgumentException((validation as com.dearyoti.lexiladder.domain.content.ContentValidationResult.Invalid).reason)
                        )
                    }
                    importTranslationPackageAtomic(transPkg, pkg)
                }
                "ui-catalog" -> {
                    val uiPkg = gson.fromJson(jsonString, UiCatalogPackage::class.java)
                    val validation = ContentValidator.validateUiCatalog(uiPkg)
                    if (!validation.isValid) {
                        return@withContext Result.failure(
                            IllegalArgumentException((validation as com.dearyoti.lexiladder.domain.content.ContentValidationResult.Invalid).reason)
                        )
                    }
                    importUiCatalogAtomic(uiPkg, pkg)
                }
                else -> {
                    return@withContext Result.failure(IllegalArgumentException("Unknown package type: ${pkg.type}"))
                }
            }

            Result.success("Successfully applied package ${pkg.packageId} (v${pkg.contentVersion})")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun importLearningPackageAtomic(
        learningPkg: LearningPackage,
        meta: ContentPackageMetadata
    ) {
        database.withTransaction {
            // Check or insert level
            val existingLevel = levelDao.getLevelById(learningPkg.levelId)
            if (existingLevel == null) {
                levelDao.insertLevels(
                    listOf(
                        LevelEntity(
                            id = learningPkg.levelId,
                            title = learningPkg.levelTitle ?: "Level ${learningPkg.levelId}",
                            description = learningPkg.levelDescription ?: "",
                            order = learningPkg.levelOrder ?: 1,
                            isUnlocked = learningPkg.levelOrder == 1
                        )
                    )
                )
            }

            // Insert/update words without touching user progress
            val wordsToInsert = learningPkg.words.map { w ->
                WordEntity(
                    id = w.wordId,
                    text = w.term,
                    partOfSpeech = w.partOfSpeech,
                    definition = w.definition,
                    exampleSentence = w.example,
                    levelId = learningPkg.levelId,
                    unitId = w.unitId,
                    phonetic = w.phonetic,
                    translation = w.translation
                )
            }
            wordDao.insertWords(wordsToInsert)

            // Record installed package
            installedPackageDao.insertOrUpdatePackage(
                InstalledPackageEntity(
                    packageId = meta.packageId,
                    type = meta.type,
                    locale = meta.locale,
                    contentVersion = meta.contentVersion,
                    schemaVersion = meta.schemaVersion,
                    installedAt = System.currentTimeMillis(),
                    sha256 = meta.sha256
                )
            )
        }
    }

    private suspend fun importTranslationPackageAtomic(
        transPkg: TranslationPackage,
        meta: ContentPackageMetadata
    ) {
        database.withTransaction {
            for (entry in transPkg.entries) {
                val existingWord = wordDao.getWordById(entry.wordId)
                if (existingWord != null) {
                    val updated = existingWord.copy(
                        translation = entry.translation,
                        definition = entry.definition ?: existingWord.definition
                    )
                    wordDao.insertWords(listOf(updated))
                }
            }

            installedPackageDao.insertOrUpdatePackage(
                InstalledPackageEntity(
                    packageId = meta.packageId,
                    type = meta.type,
                    locale = meta.locale,
                    contentVersion = meta.contentVersion,
                    schemaVersion = meta.schemaVersion,
                    installedAt = System.currentTimeMillis(),
                    sha256 = meta.sha256
                )
            )
        }
    }

    private suspend fun importUiCatalogAtomic(
        uiPkg: UiCatalogPackage,
        meta: ContentPackageMetadata
    ) {
        database.withTransaction {
            installedPackageDao.insertOrUpdatePackage(
                InstalledPackageEntity(
                    packageId = meta.packageId,
                    type = meta.type,
                    locale = meta.locale,
                    contentVersion = meta.contentVersion,
                    schemaVersion = meta.schemaVersion,
                    installedAt = System.currentTimeMillis(),
                    sha256 = meta.sha256
                )
            )
        }
    }

    fun readBundledManifest(): ContentManifest {
        val inputStream = context.assets.open("content/manifest.json")
        InputStreamReader(inputStream).use { reader ->
            return gson.fromJson(reader, ContentManifest::class.java)
        }
    }

    private fun readAssetBytes(assetPath: String): ByteArray {
        val cleanPath = assetPath.removePrefix("file:///android_asset/").removePrefix("assets/")
        return context.assets.open(cleanPath).use { it.readBytes() }
    }

    private fun fetchRemoteManifest(urlStr: String): ContentManifest {
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.requestMethod = "GET"

        conn.inputStream.use { stream ->
            InputStreamReader(stream).use { reader ->
                return gson.fromJson(reader, ContentManifest::class.java)
            }
        }
    }

    private fun downloadBytes(urlStr: String): ByteArray {
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        conn.requestMethod = "GET"

        return conn.inputStream.use { it.readBytes() }
    }
}
