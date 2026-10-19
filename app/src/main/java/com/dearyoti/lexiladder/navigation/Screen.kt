package com.dearyoti.lexiladder.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Learn : Screen("learn", "Learn", Icons.AutoMirrored.Filled.MenuBook)
    object Practice : Screen("practice", "Practice", Icons.Default.FitnessCenter)
    object Progress : Screen("progress", "Progress", Icons.Default.BarChart)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object ContentUpdates : Screen("content_updates", "Content Updates", Icons.Default.CloudSync)
    object Premium : Screen("premium", "LexiLadder Premium", Icons.Default.WorkspacePremium)
    object PrivacyPolicy : Screen("privacy_policy", "Privacy Policy", Icons.Default.Settings)
    object MasteryTest : Screen("mastery_test/{levelId}", "Mastery Test", Icons.Default.WorkspacePremium) {
        fun createRoute(levelId: String): String = "mastery_test/$levelId"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Learn,
    Screen.Practice,
    Screen.Progress,
    Screen.Settings
)
