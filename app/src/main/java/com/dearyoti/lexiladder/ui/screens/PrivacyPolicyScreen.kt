package com.dearyoti.lexiladder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "LexiLadder Privacy Policy",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Last Updated: October 2026",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            // Section 1: Overview & Offline-first
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Overview & Offline Data Storage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "LexiLadder is designed with an offline-first architecture. We do NOT require user account creation, registration, email addresses, or phone numbers to use the app.\n\nAll your learning data—including vocabulary progress, mastery test scores, spaced-repetition schedules, bookmarks, and settings—is stored 100% locally on your device in private SQLite/Room databases and DataStore. None of your personal study records are uploaded to our servers.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Section 2: Advertising (AdMob)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Advertising & Google AdMob",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "In the free version of LexiLadder, advertisements (banners, interstitials between sessions, and optional rewarded ads) are served via Google AdMob (Google Mobile Ads SDK).\n\nGoogle AdMob may collect and process device identifiers (such as Android Advertising ID), coarse location, and diagnostic information to deliver relevant ads and prevent fraudulent traffic.\n\nStrict Learning Policy: Advertisements are NEVER displayed during active quiz questions, answers, or mastery tests. You can completely remove all ads at any time by upgrading to LexiLadder Premium.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Section 3: Google Play Billing
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. In-App Purchases & Billing",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All in-app purchases (e.g., LexiLadder Premium / Ad-Free) are processed securely through the Google Play In-App Billing system. LexiLadder does NOT have access to, collect, or store your credit card numbers, billing addresses, or banking details.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Section 4: Content Updates
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Remote Content Updates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "When you check for content updates, LexiLadder makes an HTTPS request to public GitHub repository releases to retrieve static JSON content catalogs. No personal or device data is transmitted during this check.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Section 5: Data Deletion & Contact
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5. Data Retention & Contact",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can delete all your progress and local data at any time by going to Android Settings > Apps > LexiLadder > Storage > Clear Data, or by uninstalling the application.\n\nIf you have any questions or feedback regarding this Privacy Policy, please contact:\nDeveloper: dearyoti\nEmail: made.sudi@gmail.com",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
