package com.dearyoti.lexiladder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dearyoti.lexiladder.navigation.MainAppNavigation
import com.dearyoti.lexiladder.theme.LexiLadderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LexiLadderTheme {
                MainAppNavigation()
            }
        }
    }
}
