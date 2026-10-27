package com.dearyoti.lexiladder.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dearyoti.lexiladder.BuildConfig
import com.dearyoti.lexiladder.LexiLadderApp
import com.dearyoti.lexiladder.core.billing.BillingManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val prefsRepo = LexiLadderApp.instance.userPreferencesRepository
    val billingManager = LexiLadderApp.instance.billingManager

    val prefs by prefsRepo.userPreferencesFlow.collectAsState(
        initial = com.dearyoti.lexiladder.data.preferences.UserPreferences()
    )
    val productsDetails by billingManager.productsDetails.collectAsState()
    val isConnecting by billingManager.isConnecting.collectAsState()
    val statusMessage by billingManager.billingStatusMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val allAccessPrice = productsDetails[BillingManager.PRODUCT_ALL_ACCESS_LIFETIME]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "$5.99"
    val examBoosterPrice = productsDetails[BillingManager.PRODUCT_EXAM_BOOSTER]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "$2.99"
    val removeAdsPrice = productsDetails[BillingManager.PRODUCT_PREMIUM_REMOVE_ADS]
        ?.oneTimePurchaseOfferDetails?.formattedPrice ?: "$1.99"

    fun initiatePurchase(productId: String) {
        if (activity != null) {
            val launched = billingManager.launchPurchaseFlow(activity, productId)
            if (!launched) {
                scope.launch {
                    snackbarHostState.showSnackbar("Unable to open Google Play Store. Check internet connection.")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Premium & Upgrades") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gold Header Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    modifier = Modifier.size(46.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Upgrade LexiLadder",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Supercharge your English vocabulary with specialized exam modules or enjoy a completely ad-free experience forever.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Tier 1: All-Access Lifetime (PRO)
            PurchaseTierCard(
                title = "All-Access Lifetime (PRO)",
                badge = "⭐ BEST VALUE • ONE-TIME",
                price = allAccessPrice,
                description = "Unlock every current and future premium feature with a single lifetime purchase.",
                features = listOf(
                    "Includes Exam Booster Pack (TOEFL / IELTS)",
                    "100% Ad-Free learning forever across all screens",
                    "All future vocabulary, grammar & test expansions",
                    "Full offline access & permanent lifetime ownership"
                ),
                isOwned = prefs.hasAllAccessLifetime,
                isHighlighted = true,
                isConnecting = isConnecting,
                onPurchaseClick = { initiatePurchase(BillingManager.PRODUCT_ALL_ACCESS_LIFETIME) }
            )

            // Tier 2: Exam Booster Pack
            PurchaseTierCard(
                title = "Exam Booster Pack",
                badge = "TOEFL & IELTS",
                price = examBoosterPrice,
                description = "Specialized academic vocabulary and simulated tests designed for exam achievers.",
                features = listOf(
                    "570 Academic Word List (AWL) core vocabulary",
                    "High-scoring collocations & phrasal verbs",
                    "Timed diagnostic practice tests with band estimate",
                    "One-time purchase with permanent offline access"
                ),
                isOwned = prefs.isExamBoosterUnlocked,
                isHighlighted = false,
                isConnecting = isConnecting,
                onPurchaseClick = { initiatePurchase(BillingManager.PRODUCT_EXAM_BOOSTER) }
            )

            // Tier 3: Remove Ads Only
            PurchaseTierCard(
                title = "Ad-Free Experience",
                badge = "LIFETIME",
                price = removeAdsPrice,
                description = "Remove all banners and interstitial ads permanently for focused, peaceful learning.",
                features = listOf(
                    "Zero banners in Home, Progress, and Settings",
                    "Zero interstitial interruptions after lessons",
                    "Faster, cleaner app experience offline & online"
                ),
                isOwned = prefs.isAdFree,
                isHighlighted = false,
                isConnecting = isConnecting,
                onPurchaseClick = { initiatePurchase(BillingManager.PRODUCT_PREMIUM_REMOVE_ADS) }
            )

            // Restore Purchases Button
            OutlinedButton(
                onClick = {
                    billingManager.restorePurchases { success, message ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Restore, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore Purchases")
            }

            // QA / Debug Simulation Mode
            if (BuildConfig.DEBUG) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🛠️ QA / Testing Controls (Debug Only)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Simulate Google Play purchase state for each product tier:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { billingManager.simulateAllAccessPurchase(true) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sim All-Access", style = MaterialTheme.typography.bodySmall)
                            }
                            Button(
                                onClick = { billingManager.simulateExamBoosterPurchase(true) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sim Booster", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { billingManager.simulatePremiumPurchase(true) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sim No-Ads", style = MaterialTheme.typography.bodySmall)
                            }
                            OutlinedButton(
                                onClick = { billingManager.simulateAllAccessPurchase(false) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reset All", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            statusMessage?.let { msg ->
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PurchaseTierCard(
    title: String,
    badge: String?,
    price: String,
    description: String,
    features: List<String>,
    isOwned: Boolean,
    isHighlighted: Boolean,
    isConnecting: Boolean,
    onPurchaseClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwned) {
                Color(0xFFE8F5E9)
            } else if (isHighlighted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isOwned) {
            BorderStroke(1.5.dp, Color(0xFF4CAF50))
        } else if (isHighlighted) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighlighted) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (badge != null && !isOwned) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = null
                    )
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                features.forEach { feature ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (isOwned) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (isOwned) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Unlocked & Active",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
            } else {
                Button(
                    onClick = onPurchaseClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHighlighted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                ) {
                    if (isConnecting) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Get for $price",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
