package com.example.sumamente.ui

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
import com.example.sumamente.R
import java.util.Locale
import kotlin.math.max
import kotlin.random.Random
import com.example.sumamente.ui.utils.MusicManager

class SpeedRankingActivity : BaseActivity()  {

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

        loadSpeedRankingData()
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

            rankingItems.clear()
            rankingItems.add(
                SpeedRankingItem(
                    position = 1,
                    username = username,
                    countryCode = countryCode,
                    averageTime = avgTime,
                    isCurrentUser = true
                )
            )
            adapter.notifyItemInserted(0)
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            loadingIndicator.visibility = View.GONE
        }, 700)
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
        if (!isFinishingByBack) {
            MusicManager.pause()
        }
        isFinishingByBack = false
    }


}
