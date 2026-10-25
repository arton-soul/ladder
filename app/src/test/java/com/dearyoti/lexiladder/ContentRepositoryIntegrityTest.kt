package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.data.content.model.ContentManifest
import com.dearyoti.lexiladder.data.content.model.LearningPackage
import com.dearyoti.lexiladder.data.content.model.TranslationPackage
import com.dearyoti.lexiladder.data.content.model.UiCatalogPackage
import com.dearyoti.lexiladder.domain.content.ContentValidator
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ContentRepositoryIntegrityTest {

    private val gson = Gson()
    private val projectRootDir: File = run {
        val currentDir = File(System.getProperty("user.dir") ?: ".")
        if (File(currentDir, "content").exists()) currentDir
        else if (File(currentDir.parentFile, "content").exists()) currentDir.parentFile
        else currentDir
    }
    private val repoContentDir = File(projectRootDir, "content")
    private val assetsContentDir = File(projectRootDir, "app/src/main/assets/content")

    @Test
    fun contentDir_existsAndContainsAllRequiredFiles() {
        assertTrue("content directory must exist", repoContentDir.exists() && repoContentDir.isDirectory)
        val expectedFiles = listOf(
            "manifest.json",
            "learning_a1.json",
            "learning_a2.json",
            "translation_id_a1.json",
            "translation_id_a2.json",
            "ui_id.json"
        )
        for (fileName in expectedFiles) {
            val f = File(repoContentDir, fileName)
            assertTrue("File $fileName must exist in content/", f.exists() && f.length() > 0)
        }
    }

    @Test
    fun manifest_correctlyValidatesAllPackagesChecksumAndSize() {
        val manifestFile = File(repoContentDir, "manifest.json")
        val manifest = gson.fromJson(manifestFile.readText(Charsets.UTF_8), ContentManifest::class.java)

        assertNotNull("Manifest must parse correctly", manifest)
        assertEquals("Manifest schema version must be 1", 1, manifest.manifestSchemaVersion)
        assertEquals("Manifest packages count must be 5", 5, manifest.packages.size)

        for (pkg in manifest.packages) {
            val fileName = pkg.url.substringAfterLast("/")
            val file = File(repoContentDir, fileName)
            assertTrue("Package file $fileName must exist", file.exists())

            val bytes = file.readBytes()
            assertEquals("Byte size of $fileName must match manifest", pkg.sizeBytes, bytes.size.toLong())

            val calculatedHash = ContentValidator.calculateSha256(bytes)
            assertEquals("SHA-256 of $fileName must match manifest", pkg.sha256, calculatedHash)

            assertTrue("Schema version must be supported", ContentValidator.validateSchemaVersion(pkg.schemaVersion))
            assertTrue("Minimum app version must be compatible", ContentValidator.validateAppCompatibility(pkg.minimumAppVersion, currentAppVersion = 2))
        }
    }

    @Test
    fun learningPackages_areValidAndMatchWordCounts() {
        // A1
        val a1File = File(repoContentDir, "learning_a1.json")
        val a1Pkg = gson.fromJson(a1File.readText(Charsets.UTF_8), LearningPackage::class.java)
        val a1Validation = ContentValidator.validateLearningPackage(a1Pkg)
        assertTrue("Learning A1 must be valid", a1Validation.isValid)
        assertEquals("Level A1 must have 150 words", 150, a1Pkg.words.size)
        assertEquals("Level A1 must have 5 units", 5, a1Pkg.units.size)

        // A2
        val a2File = File(repoContentDir, "learning_a2.json")
        val a2Pkg = gson.fromJson(a2File.readText(Charsets.UTF_8), LearningPackage::class.java)
        val a2Validation = ContentValidator.validateLearningPackage(a2Pkg)
        assertTrue("Learning A2 must be valid", a2Validation.isValid)
        assertEquals("Level A2 must have 200 words", 200, a2Pkg.words.size)
        assertEquals("Level A2 must have 7 units", 7, a2Pkg.units.size)
    }

    @Test
    fun translationPackages_areValidAndMatchWordCounts() {
        // Translation A1
        val trA1File = File(repoContentDir, "translation_id_a1.json")
        val trA1Pkg = gson.fromJson(trA1File.readText(Charsets.UTF_8), TranslationPackage::class.java)
        val trA1Validation = ContentValidator.validateTranslationPackage(trA1Pkg)
        assertTrue("Translation A1 must be valid", trA1Validation.isValid)
        assertEquals("Translation A1 must have 150 entries", 150, trA1Pkg.entries.size)

        // Translation A2
        val trA2File = File(repoContentDir, "translation_id_a2.json")
        val trA2Pkg = gson.fromJson(trA2File.readText(Charsets.UTF_8), TranslationPackage::class.java)
        val trA2Validation = ContentValidator.validateTranslationPackage(trA2Pkg)
        assertTrue("Translation A2 must be valid", trA2Validation.isValid)
        assertEquals("Translation A2 must have 200 entries", 200, trA2Pkg.entries.size)
    }

    @Test
    fun uiCatalogPackage_isValidAndContainsStrings() {
        val uiFile = File(repoContentDir, "ui_id.json")
        val uiPkg = gson.fromJson(uiFile.readText(Charsets.UTF_8), UiCatalogPackage::class.java)
        val uiValidation = ContentValidator.validateUiCatalog(uiPkg)
        assertTrue("UI catalog must be valid", uiValidation.isValid)
        assertTrue("UI catalog must contain strings", uiPkg.strings.isNotEmpty())
    }

    @Test
    fun repoAndAssetsContent_areInSync() {
        val filesToCheck = listOf(
            "manifest.json",
            "learning_a1.json",
            "learning_a2.json",
            "translation_id_a1.json",
            "translation_id_a2.json",
            "ui_id.json"
        )
        for (fileName in filesToCheck) {
            val repoFile = File(repoContentDir, fileName)
            val assetFile = File(assetsContentDir, fileName)
            assertTrue("Asset file $fileName must exist", assetFile.exists())
            assertEquals("Asset file $fileName size must match repo content file", repoFile.length(), assetFile.length())
            assertEquals(
                "Asset file $fileName content must match repo content file",
                repoFile.readText(Charsets.UTF_8),
                assetFile.readText(Charsets.UTF_8)
            )
        }
    }
}
