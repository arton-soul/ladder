package com.dearyoti.lexiladder.core.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.dearyoti.lexiladder.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingManager(
    private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        const val PRODUCT_PREMIUM_REMOVE_ADS = "lexiladder_premium_remove_ads"
        const val PRODUCT_EXAM_BOOSTER = "lexiladder_exam_booster_pack"
        const val PRODUCT_ALL_ACCESS_LIFETIME = "lexiladder_all_access_lifetime"

        val ALL_PRODUCT_IDS = listOf(
            PRODUCT_ALL_ACCESS_LIFETIME,
            PRODUCT_EXAM_BOOSTER,
            PRODUCT_PREMIUM_REMOVE_ADS
        )
    }

    private var billingClient: BillingClient? = null

    private val _productsDetails = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productsDetails: StateFlow<Map<String, ProductDetails>> = _productsDetails.asStateFlow()

    private val _premiumProductDetails = MutableStateFlow<ProductDetails?>(null)
    val premiumProductDetails: StateFlow<ProductDetails?> = _premiumProductDetails.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    init {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        try {
            val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()

            billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases(pendingPurchasesParams)
                .build()

            startBillingConnection()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing BillingClient: ${e.message}")
        }
    }

    fun startBillingConnection() {
        val client = billingClient ?: return
        if (client.isReady) {
            queryProductDetails()
            queryPurchases()
            return
        }

        _isConnecting.value = true
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                _isConnecting.value = false
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "BillingClient connected successfully.")
                    queryProductDetails()
                    queryPurchases()
                } else {
                    Log.w(TAG, "Billing setup failed with code: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    _billingStatusMessage.value = "Billing service unavailable: ${billingResult.debugMessage}"
                }
            }

            override fun onBillingServiceDisconnected() {
                _isConnecting.value = false
                Log.w(TAG, "Billing service disconnected. Will retry on next request.")
            }
        })
    }

    fun queryProductDetails() {
        val client = billingClient
        if (client == null || !client.isReady) {
            startBillingConnection()
            return
        }

        val productList = ALL_PRODUCT_IDS.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val detailsMap = queryProductDetailsResult.productDetailsList.associateBy { it.productId }
                _productsDetails.value = detailsMap
                _premiumProductDetails.value = detailsMap[PRODUCT_PREMIUM_REMOVE_ADS]
                Log.d(TAG, "Product details loaded for products: ${detailsMap.keys}")
            } else {
                Log.w(TAG, "queryProductDetails failed: ${billingResult.debugMessage}")
            }
        }
    }

    fun queryPurchases() {
        val client = billingClient
        if (client == null || !client.isReady) {
            return
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        client.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            } else {
                Log.w(TAG, "queryPurchases failed: ${billingResult.debugMessage}")
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productId: String = PRODUCT_ALL_ACCESS_LIFETIME): Boolean {
        val client = billingClient
        val productDetails = _productsDetails.value[productId]
            ?: if (productId == PRODUCT_PREMIUM_REMOVE_ADS) _premiumProductDetails.value else null

        if (client == null || !client.isReady || productDetails == null) {
            Log.w(TAG, "Cannot launch purchase flow for $productId. BillingClient not ready or product details missing.")
            _billingStatusMessage.value = "Google Play Billing is not ready. Please check internet connection."
            startBillingConnection()
            return false
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val result = client.launchBillingFlow(activity, billingFlowParams)
        return result.responseCode == BillingClient.BillingResponseCode.OK
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases)
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Purchase cancelled by user.")
            _billingStatusMessage.value = "Purchase cancelled."
        } else {
            Log.w(TAG, "onPurchasesUpdated error: ${billingResult.debugMessage} (code: ${billingResult.responseCode})")
            _billingStatusMessage.value = "Purchase error: ${billingResult.debugMessage}"
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        var hasRemoveAds = false
        var hasExamBooster = false
        var hasAllAccess = false

        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
                if (purchase.products.contains(PRODUCT_ALL_ACCESS_LIFETIME)) {
                    hasAllAccess = true
                }
                if (purchase.products.contains(PRODUCT_EXAM_BOOSTER)) {
                    hasExamBooster = true
                }
                if (purchase.products.contains(PRODUCT_PREMIUM_REMOVE_ADS)) {
                    hasRemoveAds = true
                }
            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Log.d(TAG, "Purchase is in PENDING state.")
                _billingStatusMessage.value = "Purchase is pending approval by Google Play."
            }
        }

        coroutineScope.launch {
            if (hasAllAccess) {
                userPreferencesRepository.setAllAccessLifetime(true)
                _billingStatusMessage.value = "All-Access Lifetime activated! All features unlocked."
            } else {
                if (hasExamBooster) {
                    userPreferencesRepository.setExamBooster(true)
                }
                if (hasRemoveAds) {
                    userPreferencesRepository.setPremium(true)
                }
                if (hasExamBooster && hasRemoveAds) {
                    _billingStatusMessage.value = "Exam Booster & Ad-Free activated!"
                } else if (hasExamBooster) {
                    _billingStatusMessage.value = "Exam Booster Pack activated!"
                } else if (hasRemoveAds) {
                    _billingStatusMessage.value = "Premium ad-free activated!"
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val client = billingClient ?: return
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        client.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully.")
            } else {
                Log.w(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Restore purchases triggered by user action in Settings / Premium screen.
     */
    fun restorePurchases(onComplete: (Boolean, String) -> Unit) {
        val client = billingClient
        if (client == null || !client.isReady) {
            onComplete(false, "Billing service not connected. Please check internet connection.")
            startBillingConnection()
            return
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        client.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val purchasedProducts = purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                    .flatMap { it.products }
                    .toSet()

                val foundProducts = ALL_PRODUCT_IDS.filter { it in purchasedProducts }
                if (foundProducts.isNotEmpty()) {
                    processPurchases(purchases)
                    val summary = when {
                        PRODUCT_ALL_ACCESS_LIFETIME in foundProducts -> "All-Access Lifetime restored successfully!"
                        PRODUCT_EXAM_BOOSTER in foundProducts && PRODUCT_PREMIUM_REMOVE_ADS in foundProducts -> "Exam Booster and Ad-Free restored successfully!"
                        PRODUCT_EXAM_BOOSTER in foundProducts -> "Exam Booster Pack restored successfully!"
                        PRODUCT_PREMIUM_REMOVE_ADS in foundProducts -> "Ad-Free Premium restored successfully!"
                        else -> "Purchases restored successfully!"
                    }
                    onComplete(true, summary)
                } else {
                    onComplete(false, "No previous purchases found for this Google account.")
                }
            } else {
                onComplete(false, "Restore failed: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Testing / QA helper to simulate premium purchase toggle without live Play Console billing track.
     */
    fun simulatePremiumPurchase(enable: Boolean) {
        coroutineScope.launch {
            userPreferencesRepository.setPremium(enable)
            _billingStatusMessage.value = if (enable) "Simulated Ad-Free Activated!" else "Simulated Ad-Free Deactivated."
        }
    }

    fun simulateExamBoosterPurchase(enable: Boolean) {
        coroutineScope.launch {
            userPreferencesRepository.setExamBooster(enable)
            _billingStatusMessage.value = if (enable) "Simulated Exam Booster Activated!" else "Simulated Exam Booster Deactivated."
        }
    }

    fun simulateAllAccessPurchase(enable: Boolean) {
        coroutineScope.launch {
            userPreferencesRepository.setAllAccessLifetime(enable)
            if (!enable) {
                userPreferencesRepository.setPremium(false)
                userPreferencesRepository.setExamBooster(false)
            }
            _billingStatusMessage.value = if (enable) "Simulated All-Access Lifetime Activated!" else "Simulated All-Access Reset."
        }
    }
}
