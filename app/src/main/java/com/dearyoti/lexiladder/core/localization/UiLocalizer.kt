package com.dearyoti.lexiladder.core.localization

/**
 * Pure Kotlin UI string localizer supporting reactive in-app language switching
 * without requiring app restart or complex Android context recreation.
 *
 * Provides built-in strings for English ("en") and Indonesian ("id") with zero network overhead.
 * Falls back safely to English ("en") for any missing keys or unsupported locales.
 */
object UiLocalizer {

    private val enStrings: Map<String, String> = mapOf(
        // Bottom Navigation
        "nav_home" to "Home",
        "nav_learn" to "Learn",
        "nav_practice" to "Practice",
        "nav_progress" to "Progress",
        "nav_settings" to "Settings",

        // Home Screen
        "home_due_title" to "%d Words Due for Review",
        "home_due_desc" to "Spaced repetition scheduled review to cement your memory.",
        "home_review_btn" to "Review",
        "home_target_title" to "Daily Target: %d Minutes",
        "home_target_desc" to "A %d-minute daily ladder session builds rock-solid memory.",
        "home_words_mastered" to "Words Mastered",
        "home_learn_words" to "Learn Words",
        "home_quick_quiz" to "Quick Quiz Practice",
        "home_take_test" to "Take Level Mastery Test",
        "home_units_header" to "Level %s Units",
        "home_words_count" to "%d words",
        "home_locked_badge" to "Locked",
        "home_available_badge" to "Available",
        "home_mastered_badge" to "Mastered",

        // Learn Screen
        "learn_search_placeholder_id" to "Search word or Indonesian meaning...",
        "learn_search_placeholder_en" to "Search word or English definition...",
        "learn_all_words" to "All Words (%d)",
        "learn_favorites" to "Favorites (%d)",
        "learn_flashcard_title" to "Practice Flashcard Mode",
        "learn_flashcard_desc" to "Tap to flip, test your memory, and rate difficulty",
        "learn_show_meaning" to "Show Meaning",
        "learn_tap_flip" to "💡 Tap card to flip & reveal meaning",
        "learn_rating_hard" to "Hard",
        "learn_rating_good" to "Good",
        "learn_rating_easy" to "Easy",
        "learn_meaning_label" to "Arti: ",
        "learn_definition_label" to "Definition: ",
        "learn_immersion_badge" to "English Direct Immersion",

        // Practice Screen
        "practice_title" to "Adaptive Practice & Review",
        "practice_all_levels" to "All Unlocked Levels",
        "practice_standard_quiz" to "Standard Quiz (%d)",
        "practice_due_review" to "Due Review (%d)",
        "practice_difficult_words" to "Difficult Words (%d)",
        "practice_question_counter" to "Question %d of %d",
        "practice_score" to "Score: %d",
        "practice_what_meaning" to "What is the meaning of \"%s\"?",
        "practice_which_word" to "Which word matches this definition:\n\"%s\"?",
        "practice_complete_sentence" to "Complete the sentence:\n\"%s\"",
        "practice_which_translates" to "Which English word translates to:\n\"%s\"?",
        "practice_which_fits" to "Which word best completes or fits the context:\n\"%s\"?",
        "practice_complete_title" to "Practice Complete!",
        "practice_great_job" to "Great work! Every review strengthens your retention.",
        "practice_keep_going" to "Good effort! Keep practicing to master these words.",
        "practice_practice_again" to "Practice Again",
        "practice_done_btn" to "Done",

        // Progress Screen
        "progress_title" to "Learning Statistics",
        "progress_stat_mastered" to "Mastered",
        "progress_stat_due" to "Due for Review",
        "progress_stat_learning" to "In Learning",
        "progress_stat_difficult" to "Difficult Words",
        "progress_bookmarked" to "Bookmarked Words",
        "progress_roadmap_title" to "Ladder Level Roadmap",
        "progress_study_progress" to "Study Progress (%d/%d words)",
        "progress_retake_test" to "Retake Mastery Test",
        "progress_take_test" to "Take Mastery Test",

        // Settings Screen
        "settings_title" to "Settings",
        "settings_ui_lang_title" to "App Interface Language",
        "settings_ui_lang_desc" to "Choose language for menus, buttons, and navigation text",
        "settings_ui_en" to "English",
        "settings_ui_id" to "Bahasa Indonesia",
        "settings_exp_lang_title" to "Vocabulary Explanation & Translation",
        "settings_exp_lang_desc" to "Choose how vocabulary meanings and quizzes are explained",
        "settings_exp_id" to "Bahasa Indonesia",
        "settings_exp_id_sub" to "Disertai arti terjemahan lokal",
        "settings_exp_en" to "English (Direct Immersion)",
        "settings_exp_en_sub" to "Tanpa terjemahan; fokus 100% definisi Inggris",
        "settings_premium_title" to "LexiLadder Free Edition",
        "settings_premium_desc" to "Remove all banner and session ads with a one-time purchase.",
        "settings_premium_btn" to "Upgrade to Premium (Ad-Free)",
        "settings_premium_active" to "LexiLadder Premium Active",
        "settings_premium_active_desc" to "You have lifetime ad-free access. Thank you for your support!",
        "settings_updates_title" to "Content & Language Updates",
        "settings_updates_desc" to "Download new vocabulary levels, translations, and UI packages directly from GitHub without needing an APK update.",
        "settings_updates_btn" to "Manage Content Updates",
        "settings_daily_goal_title" to "Daily Practice Goal",
        "settings_minutes_per_day" to "%d Minutes / day",
        "settings_privacy_title" to "Privacy & Data Safety",
        "settings_privacy_desc" to "Read our 100% offline-first privacy statement",
        "settings_privacy_btn" to "View Privacy Policy",

        // Mastery Test
        "test_specs_title" to "Test Specifications",
        "test_specs_questions" to "Questions",
        "test_specs_questions_val" to "10 Randomized questions",
        "test_specs_passing" to "Passing Score",
        "test_specs_passing_val" to "80% (8 of 10 correct)",
        "test_specs_format" to "Format",
        "test_specs_format_val" to "Meaning, Translation & Sentence Completion",
        "test_specs_outcome" to "Outcome",
        "test_specs_outcome_val" to "Unlocks next level upon passing",
        "test_start_btn" to "Start Mastery Test",
        "test_retake_btn" to "Retake Mastery Test",
        "test_prev_attempts" to "Previous Attempts (%d)",
        "test_passed_badge" to "PASSED",
        "test_failed_badge" to "NEEDS REVIEW",
        "test_mastered_badge" to "MASTERED"
    )

    private val idStrings: Map<String, String> = mapOf(
        // Bottom Navigation
        "nav_home" to "Beranda",
        "nav_learn" to "Belajar",
        "nav_practice" to "Latihan",
        "nav_progress" to "Progres",
        "nav_settings" to "Pengaturan",

        // Home Screen
        "home_due_title" to "%d Kata Perlu Diulang",
        "home_due_desc" to "Jadwal tinjauan berkala (Spaced Repetition) untuk memperkuat daya ingat kosakata Anda.",
        "home_review_btn" to "Tinjau",
        "home_target_title" to "Target Harian: %d Menit",
        "home_target_desc" to "Sesi tangga kosakata %d menit setiap hari membangun memori yang kokoh.",
        "home_words_mastered" to "Kata Dikuasai",
        "home_learn_words" to "Mulai Belajar",
        "home_quick_quiz" to "Latihan Kuis Cepat",
        "home_take_test" to "Ikuti Ujian Level",
        "home_units_header" to "Daftar Unit Level %s",
        "home_words_count" to "%d kata",
        "home_locked_badge" to "Terkunci",
        "home_available_badge" to "Tersedia",
        "home_mastered_badge" to "Dikuasai",

        // Learn Screen
        "learn_search_placeholder_id" to "Cari kata atau arti bahasa Indonesia...",
        "learn_search_placeholder_en" to "Cari kata atau definisi Inggris...",
        "learn_all_words" to "Semua Kata (%d)",
        "learn_favorites" to "Favorit (%d)",
        "learn_flashcard_title" to "Mode Kartu Kilat (Flashcard)",
        "learn_flashcard_desc" to "Ketuk kartu untuk melihat arti, uji ingatan, dan beri nilai kesulitan",
        "learn_show_meaning" to "Buka Arti",
        "learn_tap_flip" to "💡 Ketuk kartu untuk melihat arti kata",
        "learn_rating_hard" to "Sulit",
        "learn_rating_good" to "Cukup",
        "learn_rating_easy" to "Mudah",
        "learn_meaning_label" to "Arti: ",
        "learn_definition_label" to "Definisi: ",
        "learn_immersion_badge" to "English Direct Immersion",

        // Practice Screen
        "practice_title" to "Latihan & Kuis Adaptif",
        "practice_all_levels" to "Semua Level Terbuka",
        "practice_standard_quiz" to "Kuis Standar (%d)",
        "practice_due_review" to "Perlu Diulang (%d)",
        "practice_difficult_words" to "Kata Sulit (%d)",
        "practice_question_counter" to "Pertanyaan %d dari %d",
        "practice_score" to "Skor: %d",
        "practice_what_meaning" to "Apa arti dari \"%s\"?",
        "practice_which_word" to "Kata manakah yang sesuai dengan definisi:\n\"%s\"?",
        "practice_complete_sentence" to "Lengkapi kalimat berikut:\n\"%s\"",
        "practice_which_translates" to "Kata bahasa Inggris manakah yang bermakna:\n\"%s\"?",
        "practice_which_fits" to "Kata manakah yang paling tepat melengkapi konteks:\n\"%s\"?",
        "practice_complete_title" to "Latihan Selesai!",
        "practice_great_job" to "Kerja luar biasa! Setiap latihan memperkuat retensi kosakata Anda.",
        "practice_keep_going" to "Usaha bagus! Teruslah berlatih untuk menguasai kata-kata ini.",
        "practice_practice_again" to "Latihan Lagi",
        "practice_done_btn" to "Selesai",

        // Progress Screen
        "progress_title" to "Statistik Belajar",
        "progress_stat_mastered" to "Dikuasai",
        "progress_stat_due" to "Perlu Diulang",
        "progress_stat_learning" to "Sedang Belajar",
        "progress_stat_difficult" to "Kata Sulit",
        "progress_bookmarked" to "Kata Disimpan",
        "progress_roadmap_title" to "Peta Kemajuan Level",
        "progress_study_progress" to "Progres Belajar (%d/%d kata)",
        "progress_retake_test" to "Ulangi Ujian Level",
        "progress_take_test" to "Ikuti Ujian Level",

        // Settings Screen
        "settings_title" to "Pengaturan",
        "settings_ui_lang_title" to "Bahasa Antarmuka Aplikasi",
        "settings_ui_lang_desc" to "Pilih bahasa untuk menu, tombol, dan navigasi aplikasi",
        "settings_ui_en" to "English",
        "settings_ui_id" to "Bahasa Indonesia",
        "settings_exp_lang_title" to "Bahasa Penjelasan & Terjemahan Materi",
        "settings_exp_lang_desc" to "Pilih format penjelasan kosakata dan soal kuis",
        "settings_exp_id" to "Bahasa Indonesia",
        "settings_exp_id_sub" to "Disertai arti terjemahan lokal",
        "settings_exp_en" to "English (Direct Immersion)",
        "settings_exp_en_sub" to "Tanpa terjemahan; fokus 100% definisi Inggris",
        "settings_premium_title" to "LexiLadder Edisi Gratis",
        "settings_premium_desc" to "Hapus semua iklan banner dan sesi dengan pembelian satu kali saja.",
        "settings_premium_btn" to "Tingkatkan ke Premium (Bebas Iklan)",
        "settings_premium_active" to "LexiLadder Premium Aktif",
        "settings_premium_active_desc" to "Anda memiliki akses seumur hidup bebas iklan. Terima kasih atas dukungan Anda!",
        "settings_updates_title" to "Pembaruan Konten & Bahasa",
        "settings_updates_desc" to "Unduh level kosakata baru, terjemahan, dan paket UI langsung dari GitHub tanpa perlu pembaruan APK.",
        "settings_updates_btn" to "Kelola Pembaruan Konten",
        "settings_daily_goal_title" to "Target Latihan Harian",
        "settings_minutes_per_day" to "%d Menit / hari",
        "settings_privacy_title" to "Privasi & Keamanan Data",
        "settings_privacy_desc" to "Baca pernyataan privasi 100% offline-first kami",
        "settings_privacy_btn" to "Lihat Kebijakan Privasi",

        // Mastery Test
        "test_specs_title" to "Spesifikasi Ujian",
        "test_specs_questions" to "Jumlah Soal",
        "test_specs_questions_val" to "10 Soal Teracak",
        "test_specs_passing" to "Nilai Kelulusan",
        "test_specs_passing_val" to "80% (8 dari 10 benar)",
        "test_specs_format" to "Format",
        "test_specs_format_val" to "Arti, Terjemahan & Melengkapi Kalimat",
        "test_specs_outcome" to "Hasil",
        "test_specs_outcome_val" to "Membuka level berikutnya jika lulus",
        "test_start_btn" to "Mulai Ujian Level",
        "test_retake_btn" to "Ulangi Ujian Level",
        "test_prev_attempts" to "Percobaan Sebelumnya (%d)",
        "test_passed_badge" to "LULUS",
        "test_failed_badge" to "BELUM LULUS",
        "test_mastered_badge" to "DIKUASAI"
    )

    /**
     * Resolves a localized string by key and locale.
     * If the string has format specifiers (%s, %d), [args] will be formatted into it.
     */
    fun getString(key: String, locale: String = "en", vararg args: Any): String {
        val targetMap = when (locale.lowercase()) {
            "id", "in" -> idStrings
            else -> enStrings
        }
        val template = targetMap[key] ?: enStrings[key] ?: key
        return if (args.isNotEmpty()) {
            try {
                String.format(template, *args)
            } catch (_: Exception) {
                template
            }
        } else {
            template
        }
    }
}
