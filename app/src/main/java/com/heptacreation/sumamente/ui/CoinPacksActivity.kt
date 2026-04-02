package com.heptacreation.sumamente.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.heptacreation.sumamente.R

class CoinPacksActivity : BaseActivity() {

    private data class PackViews(
        val card:    CardView,
        val tvCoins: TextView,
        val llBonus: LinearLayout,
        val tvBonus: TextView,
        val tvTotal: TextView,
        val tvPrice: TextView,
        val tvLimit: TextView
    )

    private lateinit var bannerBienvenida: CardView
    private lateinit var btnGoPremium: MaterialButton
    private lateinit var packViews: List<PackViews>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_packs)

        inicializarVistas()
        configurarListeners()
    }

    override fun onResume() {
        super.onResume()
        actualizarUI()
    }

    // ── Inicialización ────────────────────────────────────────────────────

    private fun inicializarVistas() {
        bannerBienvenida = findViewById(R.id.banner_bienvenida)
        btnGoPremium     = findViewById(R.id.btn_go_premium)

        packViews = listOf(
            PackViews(
                card    = findViewById(R.id.card_pack_1),
                tvCoins = findViewById(R.id.tv_coins_1),
                llBonus = findViewById(R.id.ll_bonus_1),
                tvBonus = findViewById(R.id.tv_bonus_1),
                tvTotal = findViewById(R.id.tv_total_1),
                tvPrice = findViewById(R.id.tv_price_1),
                tvLimit = findViewById(R.id.tv_limit_1)
            ),
            PackViews(
                card    = findViewById(R.id.card_pack_2),
                tvCoins = findViewById(R.id.tv_coins_2),
                llBonus = findViewById(R.id.ll_bonus_2),
                tvBonus = findViewById(R.id.tv_bonus_2),
                tvTotal = findViewById(R.id.tv_total_2),
                tvPrice = findViewById(R.id.tv_price_2),
                tvLimit = findViewById(R.id.tv_limit_2)
            ),
            PackViews(
                card    = findViewById(R.id.card_pack_3),
                tvCoins = findViewById(R.id.tv_coins_3),
                llBonus = findViewById(R.id.ll_bonus_3),
                tvBonus = findViewById(R.id.tv_bonus_3),
                tvTotal = findViewById(R.id.tv_total_3),
                tvPrice = findViewById(R.id.tv_price_3),
                tvLimit = findViewById(R.id.tv_limit_3)
            ),
            PackViews(
                card    = findViewById(R.id.card_pack_4),
                tvCoins = findViewById(R.id.tv_coins_4),
                llBonus = findViewById(R.id.ll_bonus_4),
                tvBonus = findViewById(R.id.tv_bonus_4),
                tvTotal = findViewById(R.id.tv_total_4),
                tvPrice = findViewById(R.id.tv_price_4),
                tvLimit = findViewById(R.id.tv_limit_4)
            )
        )
    }

    private fun configurarListeners() {
        findViewById<ImageView>(R.id.btn_close_packs).setOnClickListener { finish() }

        btnGoPremium.setOnClickListener {
            startActivity(Intent(this, PremiumPlansActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finish()
        })
    }

    // ── UI dinámica ───────────────────────────────────────────────────────

    private fun actualizarUI() {
        btnGoPremium.visibility = if (CoinManager.isPremium(this)) View.GONE else View.VISIBLE
        actualizarPacks()
    }

    private fun actualizarPacks() {
        val isFirst = CoinManager.isFirstPurchaseAvailable(this)
        bannerBienvenida.visibility = if (isFirst) View.VISIBLE else View.GONE

        packViews.forEachIndexed { index, views ->
            val pack   = CoinManager.PACKS[index]
            val canBuy = CoinManager.canPurchase(this, pack)

            // Texto de monedas (con badge si aplica)
            val baseLabel = getString(R.string.coins_pack_amount, pack.baseCoins)
            views.tvCoins.text = if (pack.badge.isNotEmpty()) "$baseLabel ${pack.badge}"
            else baseLabel
            views.tvPrice.text = pack.priceLabel

            // Fila de bono (solo si primera compra disponible)
            if (isFirst) {
                views.llBonus.visibility = View.VISIBLE
                views.tvBonus.text = getString(R.string.coins_pack_bonus_badge, pack.bonusCoins)
                views.tvTotal.text = getString(
                    R.string.coins_pack_total_with_bonus,
                    pack.baseCoins + pack.bonusCoins
                )
            } else {
                views.llBonus.visibility = View.GONE
            }

            // Estado habilitado / deshabilitado
            if (canBuy) {
                views.card.alpha       = 1f
                views.card.isClickable = true
                views.tvLimit.visibility = View.GONE
                views.card.setOnClickListener { comprarPack(pack) }
            } else {
                views.card.alpha       = 0.4f
                views.card.isClickable = false
                views.tvLimit.visibility = View.VISIBLE
                views.card.setOnClickListener(null)
            }
        }
    }

    // ── Lógica de compra (modo prueba) ────────────────────────────────────

    private fun comprarPack(pack: CoinManager.CoinPack) {
        val antes   = CoinManager.getBalance(this)
        CoinManager.executePurchase(this, pack)
        val despues = CoinManager.getBalance(this)
        val agregadas = despues - antes

        Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.coins_pack_purchased_msg, agregadas),
            Snackbar.LENGTH_LONG
        ).show()

        actualizarPacks() // revalida qué packs siguen disponibles
    }
}