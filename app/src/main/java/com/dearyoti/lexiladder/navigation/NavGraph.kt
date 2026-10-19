package com.dearyoti.lexiladder.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.data.preferences.UserPreferences
import com.dearyoti.lexiladder.ui.screens.HomeScreen
import com.dearyoti.lexiladder.ui.screens.LearnScreen
import com.dearyoti.lexiladder.ui.screens.OnboardingScreen
import com.dearyoti.lexiladder.ui.screens.PracticeScreen
import com.dearyoti.lexiladder.ui.screens.ProgressScreen
import com.dearyoti.lexiladder.ui.screens.SettingsScreen

@Composable
fun MainAppNavigation() {
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val prefs by prefsRepo.userPreferencesFlow.collectAsState(
        initial = UserPreferences(
            uiLanguage = "en",
            explanationLanguage = "id",
            targetLanguage = "en",
            dailyGoalMinutes = 5,
            isDarkMode = false,
            isOnboardingCompleted = false
        )
    )

    if (!prefs.isOnboardingCompleted) {
        OnboardingScreen(onFinished = { /* OnboardingScreen sets preference */ })
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToLearn = {
                        navController.navigate(Screen.Learn.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToPractice = {
                        navController.navigate(Screen.Practice.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Learn.route) {
                LearnScreen()
            }
            composable(Screen.Practice.route) {
                PracticeScreen()
            }
            composable(Screen.Progress.route) {
                ProgressScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
