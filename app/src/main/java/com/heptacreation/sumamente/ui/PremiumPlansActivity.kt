package com.heptacreation.sumamente.ui

import android.animation.ValueAnimator
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsResult
import com.heptacreation.sumamente.R


class PremiumPlansActivity : BaseActivity() {

    private lateinit var badgePopular: TextView
    private lateinit var cardMonthly: RelativeLayout
    private lateinit var cardQuarterly: RelativeLayout
    private lateinit var cardSemi: RelativeLayout
    private lateinit var cardLifetime: RelativeLayout
    private lateinit var tvPriceMonthly: TextView
    private lateinit var tvPriceQuarterly: TextView
    private lateinit var tvPriceSemi: TextView
    private lateinit var tvPriceLifetime: TextView
    private lateinit var tvTitle: TextView
    private lateinit var billingClient: BillingClient
    private enum class Plan { MONTHLY, QUARTERLY, SEMI, LIFETIME }
    private var selectedPlan: Plan = Plan.QUARTERLY
    private var isBillingReady: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        setContentView(R.layout.activity_premium_plans)


        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {

                        handlePurchase(purchase)
                    }
                }
            }
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                isBillingReady = billingResult.responseCode == BillingClient.BillingResponseCode.OK
            }

            override fun onBillingServiceDisconnected() {
                isBillingReady = false
            }
        })

        tvTitle = findViewById(R.id.tv_title)
        findViewById<View>(R.id.btn_close).setOnClickListener {
            finish()
        }

        cardMonthly = findViewById(R.id.card_monthly)
        cardQuarterly = findViewById(R.id.card_quarterly)
        cardSemi = findViewById(R.id.card_semi)
        cardLifetime = findViewById(R.id.card_lifetime)

        tvPriceMonthly = findViewById(R.id.tv_price_monthly)
        tvPriceQuarterly = findViewById(R.id.tv_price_quarterly)
        tvPriceSemi = findViewById(R.id.tv_price_semi)
        tvPriceLifetime = findViewById(R.id.tv_price_lifetime)


        tvPriceMonthly.text = getString(
            R.string.premium_plan_monthly_price,
            getString(R.string.premium_price_monthly)
        )
        tvPriceQuarterly.text = getString(
            R.string.premium_plan_quarterly_price,
            getString(R.string.premium_price_quarterly)
        )
        tvPriceSemi.text = getString(
            R.string.premium_plan_semi_price,
            getString(R.string.premium_price_semi)
        )
        tvPriceLifetime.text = getString(
            R.string.premium_plan_lifetime_price,
            getString(R.string.premium_price_lifetime)
        )

        badgePopular = findViewById(R.id.tv_badge_popular)

        selectedPlan = Plan.QUARTERLY
        applySelectionUI(selectedPlan)


        findViewById<TextView>(R.id.btn_continue_monthly).setOnClickListener {
            onPlanChosen(Plan.MONTHLY)
        }
        findViewById<TextView>(R.id.btn_continue_quarterly).setOnClickListener {
            onPlanChosen(Plan.QUARTERLY)
        }
        findViewById<TextView>(R.id.btn_continue_semi).setOnClickListener {
            onPlanChosen(Plan.SEMI)
        }
        findViewById<TextView>(R.id.btn_continue_lifetime).setOnClickListener {
            onPlanChosen(Plan.LIFETIME)
        }

        cardMonthly.setOnClickListener { applySelectionUI(Plan.MONTHLY) }
        cardQuarterly.setOnClickListener { applySelectionUI(Plan.QUARTERLY) }
        cardSemi.setOnClickListener { applySelectionUI(Plan.SEMI) }
        cardLifetime.setOnClickListener { applySelectionUI(Plan.LIFETIME) }


        tvTitle.apply {
            translationY = 50f
            animate().alpha(1f).translationY(0f).setDuration(450L).setStartDelay(70L).start()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finish()
        })
    }

    override fun onResume() {
        super.onResume()
        startShineLoop(badgePopular)
    }

    override fun onPause() {
        super.onPause()

        if (::badgePopular.isInitialized) badgePopular.paint.shader = null
    }

    private fun onPlanChosen(plan: Plan) {
        val productId = when (plan) {
            Plan.MONTHLY -> "premium_monthly"
            Plan.QUARTERLY -> "premium_quarterly"
            Plan.SEMI -> "premium_semi"
            Plan.LIFETIME -> "premium_lifetime"
        }

        val productType = if (plan == Plan.LIFETIME) {
            BillingClient.ProductType.INAPP
        } else {
            BillingClient.ProductType.SUBS
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult: BillingResult, result: QueryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetailsList = result.productDetailsList
                if (productDetailsList.isNotEmpty()) {
                    val productDetails = productDetailsList.first()

                    if (plan == Plan.LIFETIME && productDetails.oneTimePurchaseOfferDetails != null) {

                        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()

                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(listOf(productDetailsParams))
                            .build()

                        billingClient.launchBillingFlow(this, billingFlowParams)
                    } else {

                        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
                        offerToken?.let { token ->
                            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(token)
                                .build()

                            val billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(listOf(productDetailsParams))
                                .build()

                            billingClient.launchBillingFlow(this, billingFlowParams)
                        }
                    }
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

            getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit { putBoolean("isPremium", true) }


            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(params) { /* opcional: log del resultado */ }
            }
        }
    }

    private fun applySelectionUI(plan: Plan) {
        selectedPlan = plan
    }

    private fun startShineLoop(textView: TextView) {
        textView.post {
            val text = textView.text?.toString() ?: return@post
            val w = textView.paint.measureText(text)
            val base = textView.currentTextColor
            val shine = ContextCompat.getColor(this, R.color.white)

            val shader = LinearGradient(
                -w, 0f, 0f, 0f,
                intArrayOf(base, shine, base),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            textView.paint.shader = shader
            val matrix = Matrix()

            ValueAnimator.ofFloat(0f, 2 * w).apply {
                duration = 1400
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART
                addUpdateListener {
                    val t = it.animatedValue as Float
                    matrix.setTranslate(t, 0f)
                    shader.setLocalMatrix(matrix)
                    textView.invalidate()
                }
                start()
            }
        }
    }
}
