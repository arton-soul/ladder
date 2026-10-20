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
    }

    private var billingClient: BillingClient? = null

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

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_PREMIUM_REMOVE_ADS)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val product = queryProductDetailsResult.productDetailsList.firstOrNull {
                    it.productId == PRODUCT_PREMIUM_REMOVE_ADS
                }
                _premiumProductDetails.value = product
                Log.d(TAG, "Product details loaded: ${product?.title}")
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

    fun launchPurchaseFlow(activity: Activity): Boolean {
        val client = billingClient
        val productDetails = _premiumProductDetails.value

        if (client == null || !client.isReady || productDetails == null) {
            Log.w(TAG, "Cannot launch purchase flow. BillingClient not ready or product details missing.")
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
        var hasPremium = false
        for (purchase in purchases) {
            if (purchase.products.contains(PRODUCT_PREMIUM_REMOVE_ADS)) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    hasPremium = true
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase)
                    }
                } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                    Log.d(TAG, "Purchase is in PENDING state.")
                    _billingStatusMessage.value = "Purchase is pending approval by Google Play."
                }
            }
        }

        if (hasPremium) {
            coroutineScope.launch {
                userPreferencesRepository.setPremium(true)
                _billingStatusMessage.value = "Premium activated successfully! All ads removed."
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
                val hasPremium = purchases.any {
                    it.products.contains(PRODUCT_PREMIUM_REMOVE_ADS) &&
                            it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                if (hasPremium) {
                    processPurchases(purchases)
                    onComplete(true, "Premium restored successfully! Ads are disabled.")
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
            _billingStatusMessage.value = if (enable) "Simulated Premium Activated!" else "Simulated Premium Deactivated."
        }
    }
}
