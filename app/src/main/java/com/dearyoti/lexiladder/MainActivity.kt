package com.dearyoti.lexiladder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dearyoti.lexiladder.data.preferences.UserPreferences
import com.dearyoti.lexiladder.navigation.MainAppNavigation
import com.dearyoti.lexiladder.theme.LexiLadderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
            val prefs by prefsRepo.userPreferencesFlow.collectAsState(initial = UserPreferences())

            val startDestination = intent?.getStringExtra("startDestination") ?: com.dearyoti.lexiladder.navigation.Screen.Home.route
            LexiLadderTheme(darkTheme = prefs.isDarkMode) {
                MainAppNavigation(initialRoute = startDestination)
            }
        }
    }
}
