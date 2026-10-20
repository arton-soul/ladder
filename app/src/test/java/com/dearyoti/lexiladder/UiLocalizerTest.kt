package com.dearyoti.lexiladder

import com.dearyoti.lexiladder.core.localization.UiLocalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UiLocalizerTest {

    @Test
    fun testEnglishNavigationStrings() {
        assertEquals("Home", UiLocalizer.getString("nav_home", "en"))
        assertEquals("Learn", UiLocalizer.getString("nav_learn", "en"))
        assertEquals("Practice", UiLocalizer.getString("nav_practice", "en"))
        assertEquals("Progress", UiLocalizer.getString("nav_progress", "en"))
        assertEquals("Settings", UiLocalizer.getString("nav_settings", "en"))
    }

    @Test
    fun testIndonesianNavigationStrings() {
        assertEquals("Beranda", UiLocalizer.getString("nav_home", "id"))
        assertEquals("Belajar", UiLocalizer.getString("nav_learn", "id"))
        assertEquals("Latihan", UiLocalizer.getString("nav_practice", "id"))
        assertEquals("Progres", UiLocalizer.getString("nav_progress", "id"))
        assertEquals("Pengaturan", UiLocalizer.getString("nav_settings", "id"))
    }

    @Test
    fun testStringWithFormatArguments() {
        val enDue = UiLocalizer.getString("home_due_title", "en", 5)
        assertEquals("5 Words Due for Review", enDue)

        val idDue = UiLocalizer.getString("home_due_title", "id", 5)
        assertEquals("5 Kata Perlu Diulang", idDue)
    }

    @Test
    fun testSettingsLanguageTitles() {
        assertEquals("App Interface Language", UiLocalizer.getString("settings_ui_lang_title", "en"))
        assertEquals("Bahasa Antarmuka Aplikasi", UiLocalizer.getString("settings_ui_lang_title", "id"))

        assertEquals("Vocabulary Explanation & Translation", UiLocalizer.getString("settings_exp_lang_title", "en"))
        assertEquals("Bahasa Penjelasan & Terjemahan Materi", UiLocalizer.getString("settings_exp_lang_title", "id"))
    }

    @Test
    fun testFallbackToEnglishWhenLocaleUnknown() {
        val unknownLocale = UiLocalizer.getString("nav_home", "fr")
        assertEquals("Home", unknownLocale)
    }

    @Test
    fun testFallbackWhenKeyNotFound() {
        val missing = UiLocalizer.getString("some_unregistered_key", "id")
        assertEquals("some_unregistered_key", missing)
    }
}
