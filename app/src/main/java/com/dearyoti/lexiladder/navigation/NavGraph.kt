package com.dearyoti.lexiladder.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.data.preferences.UserPreferences
import com.dearyoti.lexiladder.ui.screens.ContentUpdatesScreen
import com.dearyoti.lexiladder.ui.screens.HomeScreen
import com.dearyoti.lexiladder.ui.screens.LearnScreen
import com.dearyoti.lexiladder.ui.screens.MasteryTestScreen
import com.dearyoti.lexiladder.ui.screens.OnboardingScreen
import com.dearyoti.lexiladder.ui.screens.PracticeScreen
import com.dearyoti.lexiladder.ui.screens.PremiumScreen
import com.dearyoti.lexiladder.ui.screens.PrivacyPolicyScreen
import com.dearyoti.lexiladder.ui.screens.ProgressScreen
import com.dearyoti.lexiladder.ui.screens.SettingsScreen

@Composable
fun MainAppNavigation(
    initialRoute: String = Screen.Home.route
) {
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
                    val labelText = com.dearyoti.lexiladder.core.localization.UiLocalizer.getString(screen.titleKey, prefs.uiLanguage)
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = labelText) },
                        label = {
                            Text(
                                text = labelText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = (-0.2).sp
                                ),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        alwaysShowLabel = true,
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
            startDestination = initialRoute,
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
                    },
                    onNavigateToMasteryTest = { levelId ->
                        navController.navigate(Screen.MasteryTest.createRoute(levelId))
                    },
                    onNavigateToContentUpdates = {
                        navController.navigate(Screen.ContentUpdates.route)
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
                ProgressScreen(
                    onNavigateToMasteryTest = { levelId ->
                        navController.navigate(Screen.MasteryTest.createRoute(levelId))
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToContentUpdates = {
                        navController.navigate(Screen.ContentUpdates.route)
                    },
                    onNavigateToPremium = {
                        navController.navigate(Screen.Premium.route)
                    },
                    onNavigateToPrivacyPolicy = {
                        navController.navigate(Screen.PrivacyPolicy.route)
                    }
                )
            }
            composable(Screen.ContentUpdates.route) {
                ContentUpdatesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Premium.route) {
                PremiumScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.MasteryTest.route,
                arguments = listOf(
                    navArgument("levelId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val levelId = backStackEntry.arguments?.getString("levelId") ?: "level_a1"
                MasteryTestScreen(
                    levelId = levelId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
