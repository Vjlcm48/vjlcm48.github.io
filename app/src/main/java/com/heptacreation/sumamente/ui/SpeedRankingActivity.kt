package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R
import java.util.Locale
import kotlin.math.max
import kotlin.random.Random
import com.heptacreation.sumamente.ui.utils.MusicManager
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.edit
import com.heptacreation.sumamente.ui.utils.DataSyncManager
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SpeedRankingActivity : BaseActivity(), LinkAccountDialogFragment.LinkAccountDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var adapter: SpeedRankingAdapter
    private lateinit var rankingItems: MutableList<SpeedRankingItem>

    private lateinit var headerGameButton: ConstraintLayout
    private lateinit var tvHeaderGameName: TextView

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private var isFinishingByBack = false

    private lateinit var floatingLinkButton: View

    private lateinit var btnShareSpeedRanking: FloatingActionButton
    private var isDialogFromFloatingButton = false
    private var pulseAnimator: ValueAnimator? = null
    private var colorAnimator: ValueAnimator? = null

    companion object {
        const val EXTRA_GAME_TYPE = "game_type"
        const val EXTRA_GAME_COLOR = "game_color"
        const val TOTAL_LEVELS_REQUIRED = 36
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_ranking)

        ScoreManager.ensurePreferencesInitialized(this)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        initViews()
        setupButtons()
        setupGameSpecificUI()

        setupFloatingButtonInteractions()

        val gameType = intent.getStringExtra(EXTRA_GAME_TYPE)
        if (gameType != null) {
            when (gameType) {
                SpeedClassificationActivity.GAME_NUMEROS_PLUS -> {
                    ScoreManager.initPrincipiante(this)
                    ScoreManager.init(this)
                    ScoreManager.initPro(this)
                }
                SpeedClassificationActivity.GAME_DECI_PLUS -> {
                    ScoreManager.initDeciPlusPrincipiante(this)
                    ScoreManager.initDeciPlus(this)
                    ScoreManager.initDeciPlusPro(this)
                }
                SpeedClassificationActivity.GAME_ROMAS -> {
                    ScoreManager.initRomasPrincipiante(this)
                    ScoreManager.initRomas(this)
                    ScoreManager.initRomasPro(this)
                }
                SpeedClassificationActivity.GAME_ALFA_NUMEROS -> {
                    ScoreManager.initAlfaNumerosPrincipiante(this)
                    ScoreManager.initAlfaNumeros(this)
                    ScoreManager.initAlfaNumerosPro(this)
                }
                SpeedClassificationActivity.GAME_SUMA_RESTA -> {
                    ScoreManager.initSumaRestaPrincipiante(this)
                    ScoreManager.initSumaResta(this)
                    ScoreManager.initSumaRestaPro(this)
                }
                SpeedClassificationActivity.GAME_MAS_PLUS -> {
                    ScoreManager.initMasPlusPrincipiante(this)
                    ScoreManager.initMasPlus(this)
                    ScoreManager.initMasPlusPro(this)
                }
                SpeedClassificationActivity.GAME_GENIO_PLUS -> {
                    ScoreManager.initGenioPlusPrincipiante(this)
                    ScoreManager.initGenioPlus(this)
                    ScoreManager.initGenioPlusPro(this)
                }
            }
        }

        ensureFreshThen { loadSpeedRankingData() }


        // Inicio del cambio flecha de regresar del celular
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val intent = Intent(this@SpeedRankingActivity, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        // Fin del código de flecha de regresar del celular

    }

    private fun initViews() {
        recyclerView = findViewById(R.id.speed_ranking_recycler_view)
        loadingIndicator = findViewById(R.id.loading_indicator)
        emptyView = findViewById(R.id.empty_view)
        btnBack = findViewById(R.id.btn_back)
        tvTitle = findViewById(R.id.tv_speed_ranking_title)
        rootLayout = findViewById(R.id.root_speed_ranking)

        headerGameButton = findViewById(R.id.header_game_button)
        tvHeaderGameName = findViewById(R.id.tv_header_game_name)

        floatingLinkButton = findViewById(R.id.floating_link_button)

        btnShareSpeedRanking = findViewById(R.id.btnShareSpeedRanking)

        recyclerView.layoutManager = LinearLayoutManager(this)
        rankingItems = mutableListOf()
        adapter = SpeedRankingAdapter(rankingItems)
        recyclerView.adapter = adapter
    }

    private fun setupButtons() {
        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                finish()
            }
        }

        btnShareSpeedRanking.setOnClickListener {
            compartirRankingVelocidad()
        }

        setupShareFabMovable()
    }

    private fun setupGameSpecificUI() {
        val gameType = intent.getStringExtra(EXTRA_GAME_TYPE) ?: return

        rootLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))


        when (gameType) {
            SpeedClassificationActivity.GAME_NUMEROS_PLUS -> {
                headerGameButton.setBackgroundResource(R.drawable.button_background)
                tvHeaderGameName.text = getString(R.string.game_numeros_plus)
                tvHeaderGameName.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            }
            SpeedClassificationActivity.GAME_DECI_PLUS -> {
                headerGameButton.setBackgroundResource(R.drawable.button_background_deci)
                tvHeaderGameName.text = getString(R.string.game_deci_plus)
                tvHeaderGameName.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            }
            SpeedClassificationActivity.GAME_ROMAS -> {
                headerGameButton.setBackgroundResource(R.drawable.button_background_romas)
                tvHeaderGameName.text = getString(R.string.game_romas)
                tvHeaderGameName.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            }
            SpeedClassificationActivity.GAME_ALFA_NUMEROS -> {
                headerGameButton.setBackgroundResource(R.drawable.button_background_alfa_numeros)

                val alfaText = getString(R.string.text_alfa)
                val numerosText = getString(R.string.text_numeros)
                val alfaNumerosText = "$alfaText$numerosText"
                val spannableAlfaNumeros = SpannableString(alfaNumerosText)

                spannableAlfaNumeros.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)),
                    0, alfaText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableAlfaNumeros.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)),
                    alfaText.length, alfaNumerosText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvHeaderGameName.text = spannableAlfaNumeros
            }
            SpeedClassificationActivity.GAME_SUMA_RESTA -> {
                headerGameButton.setBackgroundResource(R.drawable.button_background_sumaresta)

                val sumaText = getString(R.string.text_suma)
                val restaText = getString(R.string.text_resta)
                val sumarestaText = "$sumaText$restaText"
                val spannableSumaresta = SpannableString(sumarestaText)

                spannableSumaresta.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)),
                    0, sumaText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableSumaresta.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
                    sumaText.length, sumarestaText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvHeaderGameName.text = spannableSumaresta
            }
            SpeedClassificationActivity.GAME_MAS_PLUS -> {
                headerGameButton.setBackgroundResource(R.drawable.button_background_mas)
                tvHeaderGameName.text = getString(R.string.game_mas_plus)
                tvHeaderGameName.setTextColor(ContextCompat.getColor(this, R.color.grey_light))
            }
            SpeedClassificationActivity.GAME_GENIO_PLUS -> {
                headerGameButton.setBackgroundResource(R.drawable.button_background_genio)
                tvHeaderGameName.text = getString(R.string.game_genio_plus)
                tvHeaderGameName.setTextColor(ContextCompat.getColor(this, R.color.blue_pressed))
            }
            else -> {
                headerGameButton.setBackgroundResource(R.drawable.button_background)
                tvHeaderGameName.text = ""
            }
        }

        tvTitle.text = getString(R.string.speed_ranking_title)
    }

    private fun loadSpeedRankingData() {
        loadingIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.GONE

        floatingLinkButton.visibility = View.GONE
        btnShareSpeedRanking.visibility = View.GONE

        val tvMsgSpeedRanking = findViewById<TextView>(R.id.tvMsgSpeedRanking)
        tvMsgSpeedRanking.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            val gameType = intent.getStringExtra(EXTRA_GAME_TYPE) ?: return@postDelayed


            val result = when (gameType) {
                SpeedClassificationActivity.GAME_NUMEROS_PLUS -> arrayOf<Any>(
                    ScoreManager.getUniqueLevelsPlayedNumerosPlusPrincipiante(),
                    ScoreManager.getUniqueLevelsPlayedNumerosPlusAvanzado(),
                    ScoreManager.getUniqueLevelsPlayedNumerosPlusPro(),
                    ScoreManager.getTiempoPromedioNumerosPlus(),
                    ScoreManager.isEligibleForSpeedRankingNumerosPlus(),
                    ScoreManager.getMissingLevelsNumerosPlusPrincipiante(),
                    ScoreManager.getMissingLevelsNumerosPlusAvanzado(),
                    ScoreManager.getMissingLevelsNumerosPlusPro()
                )
                SpeedClassificationActivity.GAME_DECI_PLUS -> arrayOf<Any>(
                    ScoreManager.getUniqueLevelsPlayedDeciPlusPrincipiante(),
                    ScoreManager.getUniqueLevelsPlayedDeciPlusAvanzado(),
                    ScoreManager.getUniqueLevelsPlayedDeciPlusPro(),
                    ScoreManager.getTiempoPromedioDeciPlus(),
                    ScoreManager.isEligibleForSpeedRankingDeciPlus(),
                    ScoreManager.getMissingLevelsDeciPlusPrincipiante(),
                    ScoreManager.getMissingLevelsDeciPlusAvanzado(),
                    ScoreManager.getMissingLevelsDeciPlusPro()
                )
                SpeedClassificationActivity.GAME_ROMAS -> arrayOf<Any>(
                    ScoreManager.getUniqueLevelsPlayedRomasPrincipiante(),
                    ScoreManager.getUniqueLevelsPlayedRomasAvanzado(),
                    ScoreManager.getUniqueLevelsPlayedRomasPro(),
                    ScoreManager.getTiempoPromedioRomas(),
                    ScoreManager.isEligibleForSpeedRankingRomas(),
                    ScoreManager.getMissingLevelsRomasPrincipiante(),
                    ScoreManager.getMissingLevelsRomasAvanzado(),
                    ScoreManager.getMissingLevelsRomasPro()
                )
                SpeedClassificationActivity.GAME_ALFA_NUMEROS -> arrayOf<Any>(
                    ScoreManager.getUniqueLevelsPlayedAlfaNumerosPrincipiante(),
                    ScoreManager.getUniqueLevelsPlayedAlfaNumerosAvanzado(),
                    ScoreManager.getUniqueLevelsPlayedAlfaNumerosPro(),
                    ScoreManager.getTiempoPromedioAlfaNumeros(),
                    ScoreManager.isEligibleForSpeedRankingAlfaNumeros(),
                    ScoreManager.getMissingLevelsAlfaNumerosPrincipiante(),
                    ScoreManager.getMissingLevelsAlfaNumerosAvanzado(),
                    ScoreManager.getMissingLevelsAlfaNumerosPro()
                )
                SpeedClassificationActivity.GAME_SUMA_RESTA -> arrayOf<Any>(
                    ScoreManager.getUniqueLevelsPlayedSumaRestaPrincipiante(),
                    ScoreManager.getUniqueLevelsPlayedSumaRestaAvanzado(),
                    ScoreManager.getUniqueLevelsPlayedSumaRestaPro(),
                    ScoreManager.getTiempoPromedioSumaResta(),
                    ScoreManager.isEligibleForSpeedRankingSumaResta(),
                    ScoreManager.getMissingLevelsSumaRestaPrincipiante(),
                    ScoreManager.getMissingLevelsSumaRestaAvanzado(),
                    ScoreManager.getMissingLevelsSumaRestaPro()
                )
                SpeedClassificationActivity.GAME_MAS_PLUS -> arrayOf<Any>(
                    ScoreManager.getUniqueLevelsPlayedMasPlusPrincipiante(),
                    ScoreManager.getUniqueLevelsPlayedMasPlusAvanzado(),
                    ScoreManager.getUniqueLevelsPlayedMasPlusPro(),
                    ScoreManager.getTiempoPromedioMasPlus(),
                    ScoreManager.isEligibleForSpeedRankingMasPlus(),
                    ScoreManager.getMissingLevelsMasPlusPrincipiante(),
                    ScoreManager.getMissingLevelsMasPlusAvanzado(),
                    ScoreManager.getMissingLevelsMasPlusPro()
                )
                SpeedClassificationActivity.GAME_GENIO_PLUS -> arrayOf<Any>(
                    ScoreManager.getUniqueLevelsPlayedGenioPlusPrincipiante(),
                    ScoreManager.getUniqueLevelsPlayedGenioPlusAvanzado(),
                    ScoreManager.getUniqueLevelsPlayedGenioPlusPro(),
                    ScoreManager.getTiempoPromedioGenioPlus(),
                    ScoreManager.isEligibleForSpeedRankingGenioPlus(),
                    ScoreManager.getMissingLevelsGenioPlusPrincipiante(),
                    ScoreManager.getMissingLevelsGenioPlusAvanzado(),
                    ScoreManager.getMissingLevelsGenioPlusPro()
                )
                else -> arrayOf<Any>(0, 0, 0, 0f, false, 0, 0, 0)
            }

            val uniquePrincipiante = result[0] as Int
            val uniqueAvanzado = result[1] as Int
            val uniquePro = result[2] as Int
            val avgTime = (result[3] as Double).toFloat()
            val avgTimeFormatted = formatAvgTime(avgTime)
            val eligibleForRanking = result[4] as Boolean
            val missingPrincipiante = result[5] as Int
            val missingAvanzado = result[6] as Int
            val missingPro = result[7] as Int

            val totalPlayed = max(0, uniquePrincipiante.coerceAtMost(12) + uniqueAvanzado.coerceAtMost(12) + uniquePro.coerceAtMost(12))
            val totalMissing = (TOTAL_LEVELS_REQUIRED - totalPlayed).coerceAtLeast(0)

            val username = sharedPreferences.getString("savedUserName", getString(R.string.default_username)) ?: getString(R.string.default_username)
            val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"

            // ----------- ESCENARIO A: Ningún nivel jugado -----------
            if (uniquePrincipiante == 0 && uniqueAvanzado == 0 && uniquePro == 0) {
                tvMsgSpeedRanking.apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.msg_need_more_games_speed, getGameName(gameType))
                    textSize = 24f // 50% más grande que 16sp
                    gravity = android.view.Gravity.CENTER
                    setTextColor(ContextCompat.getColor(this@SpeedRankingActivity, android.R.color.black))
                }
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                return@postDelayed
            }

            handleLinkAccountInvitation()

            // ----------- ESCENARIO B: Al menos un nivel jugado -----------

            if (!eligibleForRanking) {

                val avgMsgIndex = Random.nextInt(12)
                val infoGroup = when (totalPlayed) {
                    in 1..10 -> 0
                    in 11..20 -> 1
                    in 21..35 -> 2
                    else -> 0
                }
                val infoMsgIndex = Random.nextInt(6) + (infoGroup * 6)
                val motivMsgIndex = Random.nextInt(6) + (infoGroup * 6)

                val avgMsg = getString(
                    when(avgMsgIndex) {
                        0 -> R.string.speed_avg_1
                        1 -> R.string.speed_avg_2
                        2 -> R.string.speed_avg_3
                        3 -> R.string.speed_avg_4
                        4 -> R.string.speed_avg_5
                        5 -> R.string.speed_avg_6
                        6 -> R.string.speed_avg_7
                        7 -> R.string.speed_avg_8
                        8 -> R.string.speed_avg_9
                        9 -> R.string.speed_avg_10
                        10 -> R.string.speed_avg_11
                        else -> R.string.speed_avg_12
                    },
                    avgTimeFormatted
                )

                var infoMsg = getString(
                    when(infoMsgIndex) {
                        0 -> R.string.speed_info_1
                        1 -> R.string.speed_info_2
                        2 -> R.string.speed_info_3
                        3 -> R.string.speed_info_4
                        4 -> R.string.speed_info_5
                        5 -> R.string.speed_info_6
                        6 -> R.string.speed_info_7
                        7 -> R.string.speed_info_8
                        8 -> R.string.speed_info_9
                        9 -> R.string.speed_info_10
                        10 -> R.string.speed_info_11
                        11 -> R.string.speed_info_12
                        12 -> R.string.speed_info_13
                        13 -> R.string.speed_info_14
                        14 -> R.string.speed_info_15
                        15 -> R.string.speed_info_16
                        16 -> R.string.speed_info_17
                        else -> R.string.speed_info_18
                    },
                    "<b>$totalMissing</b>", "<b>$missingPrincipiante</b>", "<b>$missingAvanzado</b>", "<b>$missingPro</b>", "<b>$username</b>"
                )

                var motivMsg = getString(
                    when(motivMsgIndex) {
                        0 -> R.string.speed_motiv_1
                        1 -> R.string.speed_motiv_2
                        2 -> R.string.speed_motiv_3
                        3 -> R.string.speed_motiv_4
                        4 -> R.string.speed_motiv_5
                        5 -> R.string.speed_motiv_6
                        6 -> R.string.speed_motiv_7
                        7 -> R.string.speed_motiv_8
                        8 -> R.string.speed_motiv_9
                        9 -> R.string.speed_motiv_10
                        10 -> R.string.speed_motiv_11
                        11 -> R.string.speed_motiv_12
                        12 -> R.string.speed_motiv_13
                        13 -> R.string.speed_motiv_14
                        14 -> R.string.speed_motiv_15
                        15 -> R.string.speed_motiv_16
                        16 -> R.string.speed_motiv_17
                        else -> R.string.speed_motiv_18
                    },
                    "<b>$username</b>"
                )

                val infoHasName = infoMsg.contains("<b>$username</b>")
                if (infoHasName) {
                    motivMsg = motivMsg.replace("<b>$username</b>", "").replace(", !", "!").replace(", ¡", "¡")
                } else if (motivMsg.contains("<b>$username</b>")) {
                    infoMsg = infoMsg.replace("<b>$username</b>", "").replace(", !", "!").replace(", ¡", "¡")
                }

                tvMsgSpeedRanking.apply {
                    visibility = View.VISIBLE
                    text = android.text.Html.fromHtml(
                        "$avgMsg<br/><br/>$infoMsg<br/><br/>$motivMsg",
                        android.text.Html.FROM_HTML_MODE_LEGACY
                    )
                    setTextColor(ContextCompat.getColor(this@SpeedRankingActivity, android.R.color.black))
                }

                recyclerView.visibility = View.GONE
                emptyView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                return@postDelayed
            } else {
                tvMsgSpeedRanking.visibility = View.GONE
            }

            DataSyncManager.uploadSpeedRankingToFirebase(
                userId = getUserId(),
                userName = username,
                country = countryCode,
                gameType = gameType,
                averageTime = avgTime.toDouble()
            )

            DataSyncManager.getTopSpeedRanking(
                userId = getUserId(),
                userName = username,
                country = countryCode,
                gameType = gameType,
                averageTime = avgTime.toDouble()
            ) { rankingList, userPosition, userItem ->
                rankingItems.clear()
                rankingItems.addAll(rankingList)

                if (userItem != null && userPosition > 200) {
                    rankingItems.add(userItem.copy(position = userPosition))
                }

                @Suppress("NotifyDataSetChanged")
                adapter.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE
                btnShareSpeedRanking.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
            }

        }, 700)
    }

    private fun handleLinkAccountInvitation() {
        val isLinked = sharedPreferences.getBoolean(SettingsActivity.ACCOUNT_LINKED, false)


        if (isLinked || !ScoreManager.hasCompleted12LevelsInAnyGame()) {
            return
        }

        val lastDismissal = sharedPreferences.getLong(SettingsActivity.LAST_PROMPT_DISMISSAL_TIMESTAMP, 0L)


        if (System.currentTimeMillis() < lastDismissal) {
            return
        }

        val promptInteracted = sharedPreferences.getBoolean(SettingsActivity.LINK_PROMPT_INTERACTED, false)
        val isRanked = ScoreManager.isRankedInAtLeastOneGame()

        if (promptInteracted || isRanked) {

            floatingLinkButton.visibility = View.VISIBLE
            centerFloatingButton()
            startFloatingButtonAnimations()
        } else {

            val dialog = LinkAccountDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "LinkAccountDialog")
        }
    }

    private fun startFloatingButtonAnimations() {

        pulseAnimator = ValueAnimator.ofFloat(1.0f, 1.2f).apply {
            duration = 1500L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                val scale = animator.animatedValue as Float
                floatingLinkButton.scaleX = scale
                floatingLinkButton.scaleY = scale
            }
        }

        colorAnimator = ValueAnimator.ofArgb(

            getColor(R.color.blue_secondary),
            getColor(R.color.blue_primary),
            getColor(R.color.blue_primary_dark),
            getColor(R.color.green_accent),
            getColor(R.color.yellow),
            getColor(R.color.red_primary),
            getColor(R.color.yellow),
            getColor(R.color.green_accent),
            getColor(R.color.blue_primary_dark),
            getColor(R.color.blue_primary),
            getColor(R.color.blue_secondary)
        ).apply {
            duration = 7000L
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                val iconView = floatingLinkButton.findViewById<ImageView>(R.id.ic_link)
                iconView?.setColorFilter(animator.animatedValue as Int)
            }
        }

        pulseAnimator?.start()
        colorAnimator?.start()
    }

    private fun stopFloatingButtonAnimations() {
        pulseAnimator?.cancel()
        colorAnimator?.cancel()
        pulseAnimator = null
        colorAnimator = null

        floatingLinkButton.scaleX = 1.0f
        floatingLinkButton.scaleY = 1.0f
        val iconView = floatingLinkButton.findViewById<ImageView>(R.id.ic_link)
        iconView?.clearColorFilter()
    }

    private fun centerFloatingButton() {
        floatingLinkButton.post {
            val screenWidth = resources.displayMetrics.widthPixels
            val buttonWidth = floatingLinkButton.width
            val centerX = (screenWidth - buttonWidth) / 2f
            floatingLinkButton.x = centerX
        }
    }

    private fun setupFloatingButtonInteractions() {
        floatingLinkButton.setOnClickListener {
            stopFloatingButtonAnimations()
            isDialogFromFloatingButton = true
            val dialog = LinkAccountDialogFragment.newInstance()
            dialog.show(supportFragmentManager, "LinkAccountDialog_FromFloat")
        }

        floatingLinkButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0f
            private var initialY = 0f
            private var dX = 0f
            private var dY = 0f
            private var isDragging = false

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        stopFloatingButtonAnimations()
                        initialX = view.x
                        initialY = view.y
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        isDragging = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newX = event.rawX + dX
                        val newY = event.rawY + dY

                        if (kotlin.math.abs(newX - initialX) > 10 || kotlin.math.abs(newY - initialY) > 10) {
                            isDragging = true
                        }

                        val screenWidth = resources.displayMetrics.widthPixels
                        val screenHeight = resources.displayMetrics.heightPixels
                        val buttonWidth = view.width
                        val buttonHeight = view.height

                        val constrainedX = newX.coerceIn(0f, (screenWidth - buttonWidth).toFloat())
                        val constrainedY = newY.coerceIn(0f, (screenHeight - buttonHeight).toFloat())

                        view.animate()
                            .x(constrainedX)
                            .y(constrainedY)
                            .setDuration(0)
                            .start()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val screenHeight = resources.displayMetrics.heightPixels

                        if (view.y > screenHeight * 0.8) {

                            view.visibility = View.GONE
                            Toast.makeText(this@SpeedRankingActivity, getString(R.string.toast_button_dismissed), Toast.LENGTH_SHORT).show()

                            val cooldown = SettingsActivity.COOLDOWN_FLOAT_DISMISS
                            sharedPreferences.edit {
                                putBoolean(SettingsActivity.LINK_PROMPT_INTERACTED, true)
                                putLong(SettingsActivity.LAST_PROMPT_DISMISSAL_TIMESTAMP, System.currentTimeMillis() + cooldown)
                            }
                        } else if (!isDragging) {

                            view.performClick()
                        } else {

                            startFloatingButtonAnimations()
                        }

                        isDragging = false
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun setupShareFabMovable() {
        @SuppressLint("ClickableViewAccessibility")
        btnShareSpeedRanking.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0f
            private var initialY = 0f
            private var dX = 0f
            private var dY = 0f
            private var isDragging = false

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = view.x
                        initialY = view.y
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                        isDragging = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newX = event.rawX + dX
                        val newY = event.rawY + dY

                        if (kotlin.math.abs(newX - initialX) > 10 || kotlin.math.abs(newY - initialY) > 10) {
                            isDragging = true
                        }

                        val screenWidth = resources.displayMetrics.widthPixels
                        val screenHeight = resources.displayMetrics.heightPixels
                        val buttonWidth = view.width
                        val buttonHeight = view.height

                        val constrainedX = newX.coerceIn(0f, (screenWidth - buttonWidth).toFloat())
                        val constrainedY = newY.coerceIn(0f, (screenHeight - buttonHeight).toFloat())

                        view.animate()
                            .x(constrainedX)
                            .y(constrainedY)
                            .setDuration(0)
                            .start()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isDragging) {
                            view.performClick()
                        }
                        isDragging = false
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onAcceptLink() {
        FirebaseAuthManager.startGoogleSignIn(
            this,
            getString(R.string.default_web_client_id)
        ) { success, message ->
            if (success) {
                sharedPreferences.edit {
                    putBoolean(SettingsActivity.ACCOUNT_LINKED, true)
                }

                DataSyncManager.syncDataToCloud(this) { ok, err ->
                    Toast.makeText(
                        this,
                        if (ok) getString(R.string.account_linked_success)
                        else getString(R.string.firebase_link_failed) + (err?.let { ": $it" } ?: ""),
                        Toast.LENGTH_LONG
                    ).show()
                    loadSpeedRankingData()
                }
            } else {
                Toast.makeText(this, message ?: getString(R.string.account_linked_error), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FirebaseAuthManager.handleSignInResult(this, requestCode, data)
    }

    override fun onNotNow() {
        val cooldown = if (isDialogFromFloatingButton) {
            SettingsActivity.COOLDOWN_FLOAT_DIALOG_NOT_NOW
        } else {
            SettingsActivity.COOLDOWN_NOT_NOW
        }

        sharedPreferences.edit {
            putBoolean(SettingsActivity.LINK_PROMPT_INTERACTED, true)
            putLong(SettingsActivity.LAST_PROMPT_DISMISSAL_TIMESTAMP, System.currentTimeMillis() + cooldown)
        }
        Toast.makeText(this, getString(R.string.toast_cooldown_long), Toast.LENGTH_SHORT).show()
        isDialogFromFloatingButton = false
        loadSpeedRankingData()
    }

    override fun onRemindMeLater() {
        val cooldown = if (isDialogFromFloatingButton) {
            SettingsActivity.COOLDOWN_FLOAT_DIALOG_DISMISS
        } else {
            SettingsActivity.COOLDOWN_REMIND_LATER
        }

        sharedPreferences.edit {
            putBoolean(SettingsActivity.LINK_PROMPT_INTERACTED, true)
            putLong(SettingsActivity.LAST_PROMPT_DISMISSAL_TIMESTAMP, System.currentTimeMillis() + cooldown)
        }
        Toast.makeText(this, getString(R.string.toast_cooldown_short), Toast.LENGTH_SHORT).show()
        isDialogFromFloatingButton = false
        loadSpeedRankingData()
    }

    private fun formatAvgTime(timeSeconds: Float): String {
        val totalCentis = (timeSeconds * 100).toInt()
        val seconds = totalCentis / 100
        val centis = totalCentis % 100
        return String.format(Locale.US, "%02d.%02d", seconds, centis)

    }

    private fun getGameName(gameType: String): String {
        return when (gameType) {
            SpeedClassificationActivity.GAME_NUMEROS_PLUS -> getString(R.string.game_numeros_plus)
            SpeedClassificationActivity.GAME_DECI_PLUS -> getString(R.string.game_deci_plus)
            SpeedClassificationActivity.GAME_ROMAS -> getString(R.string.game_romas)
            SpeedClassificationActivity.GAME_ALFA_NUMEROS -> getString(R.string.game_alfa_numeros)
            SpeedClassificationActivity.GAME_SUMA_RESTA -> getString(R.string.game_sumaresta)
            SpeedClassificationActivity.GAME_MAS_PLUS -> getString(R.string.game_mas_plus)
            SpeedClassificationActivity.GAME_GENIO_PLUS -> getString(R.string.game_genio_plus)
            else -> ""
        }
    }

    private fun compartirRankingVelocidad() {
        val gameType = intent.getStringExtra(EXTRA_GAME_TYPE) ?: return
        val gameName = getGameName(gameType)

        val userItem = rankingItems.firstOrNull { it.isCurrentUser }

        if (userItem != null) {
            val posicion = userItem.position
            val tiempoPromedio = userItem.averageTime.toDouble()

            val mensaje = getString(R.string.share_speed_ranking_message, gameName, posicion, tiempoPromedio)
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, mensaje)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }

    override fun onStart() {
        super.onStart()
        val clasificacionSigueViva = ClassificationActivity.instanceRef?.get() != null
        val context = ClassificationActivity.instanceRef?.get()
        val sonidoActivo = context?.let {
            val prefs = it.getSharedPreferences("MyPrefs", MODE_PRIVATE)
            prefs.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        } ?: true

        if (clasificacionSigueViva && sonidoActivo) {
            MusicManager.resume()
        }
    }


    override fun onStop() {
        super.onStop()

        stopFloatingButtonAnimations()

        if (!isFinishingByBack) {
            MusicManager.pause()
        }
        isFinishingByBack = false
    }

    private fun ensureFreshThen(block: () -> Unit) {
        val isLinked = sharedPreferences.getBoolean(SettingsActivity.ACCOUNT_LINKED, false)
        if (!isLinked) { block(); return }
        DataSyncManager.syncDataFromCloud(this) { _, _ -> block() }
    }

    private fun getUserId(): String {
        val user = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        } catch (_: Exception) {
            null
        }
        return user?.uid
            ?: sharedPreferences.getString("anonymous_user_id", null)
            ?: generateAnonymousUserId().also { saveAnonymousUserId(it) }
    }

    private fun generateAnonymousUserId(): String {
        val id = java.util.UUID.randomUUID().toString()
        saveAnonymousUserId(id)
        return id
    }

    private fun saveAnonymousUserId(id: String) {
        sharedPreferences.edit { putString("anonymous_user_id", id) }
    }

}
