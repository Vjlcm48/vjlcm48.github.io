package com.example.sumamente.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import kotlin.random.Random

class IntegralRankingActivity : BaseActivity()  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var tvMsgIntegralRanking: TextView
    private lateinit var tvProgressIndicator: TextView
    private lateinit var btnBack: ImageView
    private lateinit var rankingListContainer: LinearLayout
    private lateinit var adapter: IntegralRankingAdapter
    private lateinit var rankingItems: MutableList<IntegralRankingItem>
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_integral_ranking)

        ScoreManager.init(this)
        ScoreManager.initPrincipiante(this)
        ScoreManager.initPro(this)
        ScoreManager.initDeciPlus(this)
        ScoreManager.initDeciPlusPrincipiante(this)
        ScoreManager.initDeciPlusPro(this)
        ScoreManager.initRomas(this)
        ScoreManager.initRomasPrincipiante(this)
        ScoreManager.initRomasPro(this)
        ScoreManager.initAlfaNumeros(this)
        ScoreManager.initAlfaNumerosPrincipiante(this)
        ScoreManager.initAlfaNumerosPro(this)
        ScoreManager.initSumaResta(this)
        ScoreManager.initSumaRestaPrincipiante(this)
        ScoreManager.initSumaRestaPro(this)
        ScoreManager.initMasPlus(this)
        ScoreManager.initMasPlusPrincipiante(this)
        ScoreManager.initMasPlusPro(this)
        ScoreManager.initGenioPlus(this)
        ScoreManager.initGenioPlusPrincipiante(this)
        ScoreManager.initGenioPlusPro(this)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        initViews()
        setupButtons()
        setupMusic()
        loadIntegralRankingData()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.integral_ranking_recycler_view)
        loadingIndicator = findViewById(R.id.loading_indicator)
        tvMsgIntegralRanking = findViewById(R.id.tvMsgIntegralRanking)
        tvProgressIndicator = findViewById(R.id.tvProgressIndicator)
        btnBack = findViewById(R.id.btn_back)
        rankingListContainer = findViewById(R.id.rankingListContainer)

        recyclerView.layoutManager = LinearLayoutManager(this)
        rankingItems = mutableListOf()
        adapter = IntegralRankingAdapter(rankingItems)
        recyclerView.adapter = adapter
    }

    private fun setupButtons() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.clasificacion)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.2f, 0.2f)

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            mediaPlayer.start()
        }
    }

    private fun loadIntegralRankingData() {

        loadingIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvMsgIntegralRanking.visibility = View.GONE
        tvProgressIndicator.visibility = View.GONE
        rankingListContainer.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            val rankingsStatus = checkUserRankingsStatus()
            val rankingsCount  = rankingsStatus.count { it }

            loadingIndicator.visibility = View.GONE

            when (rankingsCount) {
                0      -> showNoRankingsMessage()
                in 1..8 -> showProgressMessage(rankingsCount, rankingsStatus)
                9      -> {
                    val integralScore = ScoreManager.getIntegralScore()
                    showIntegralRanking(integralScore)
                }
            }
        }, 800)
    }

    private fun checkUserRankingsStatus(): List<Boolean> {

        ScoreManager.ensurePreferencesInitialized(this)

        return listOf(
            ScoreManager.isUserInRanking("GLOBAL"),
            ScoreManager.isUserInRanking("VEL_NUMEROS"),
            ScoreManager.isUserInRanking("VEL_DECI"),
            ScoreManager.isUserInRanking("VEL_ALFANUM"),
            ScoreManager.isUserInRanking("VEL_ROMAS"),
            ScoreManager.isUserInRanking("VEL_SUMARESTA"),
            ScoreManager.isUserInRanking("VEL_MAS"),
            ScoreManager.isUserInRanking("VEL_GENIOS"),
            ScoreManager.isUserInRanking("IQ_PLUS")
        )
    }

    private fun showNoRankingsMessage() {
        val username = sharedPreferences.getString("savedUserName", getString(R.string.default_username))
            ?: getString(R.string.default_username)

        val infoMessages = resources.getStringArray(R.array.ranking_integral_info_messages)
        val motivMessages = resources.getStringArray(R.array.ranking_integral_motiv_messages)

        val infoMsgIndex = Random.nextInt(infoMessages.size)
        val motivMsgIndex = Random.nextInt(motivMessages.size)

        val infoMsg = String.format(infoMessages[infoMsgIndex], username)
        val motivMsg = motivMessages[motivMsgIndex]

        val combinedMsg = getString(R.string.ranking_integral_combined_message, infoMsg, motivMsg)

        tvMsgIntegralRanking.apply {
            visibility = View.VISIBLE
            text = combinedMsg
            textSize = 22f
            setTextColor(ContextCompat.getColor(this@IntegralRankingActivity, android.R.color.black))
        }
    }

    private fun showProgressMessage(rankingsCount: Int, rankingsStatus: List<Boolean>) {
        val username = sharedPreferences.getString("savedUserName", getString(R.string.default_username))
            ?: getString(R.string.default_username)
        val remaining = 9 - rankingsCount


        val mensajesDeProgreso = listOf(
            R.plurals.ranking_integral_progreso_1,
            R.plurals.ranking_integral_progreso_2,
            R.plurals.ranking_integral_progreso_3,
            R.plurals.ranking_integral_progreso_4,
            R.plurals.ranking_integral_progreso_5,
            R.plurals.ranking_integral_progreso_6,
            R.plurals.ranking_integral_progreso_7,
            R.plurals.ranking_integral_progreso_8,
            R.plurals.ranking_integral_progreso_9,
            R.plurals.ranking_integral_progreso_10,
            R.plurals.ranking_integral_progreso_11,
            R.plurals.ranking_integral_progreso_12
        )


        val idDePluralAleatorio = mensajesDeProgreso.random()


        val progressMsg = resources.getQuantityString(idDePluralAleatorio, rankingsCount, username, rankingsCount, remaining)

        tvProgressIndicator.apply {
            visibility = View.VISIBLE

            text = getString(R.string.ranking_progress_indicator, rankingsCount)
            textSize = 18f
            setTextColor(ContextCompat.getColor(this@IntegralRankingActivity, R.color.blue_primary_darker))
        }

        tvMsgIntegralRanking.apply {
            visibility = View.VISIBLE
            text = progressMsg
            textSize = 20f
            setTextColor(ContextCompat.getColor(this@IntegralRankingActivity, android.R.color.black))
        }

        showRankingsList(rankingsStatus)
    }

    private fun showRankingsList(rankingsStatus: List<Boolean>) {
        rankingListContainer.removeAllViews()
        rankingListContainer.visibility = View.VISIBLE

        val rankingNames = arrayOf(
            getString(R.string.ranking_global_name),
            getString(R.string.ranking_vel_numeros),
            getString(R.string.ranking_vel_deci),
            getString(R.string.ranking_vel_alfanumeros),
            getString(R.string.ranking_vel_romas),
            getString(R.string.ranking_vel_sumaresta),
            getString(R.string.ranking_vel_mas),
            getString(R.string.ranking_vel_genios),
            getString(R.string.ranking_iq)
        )

        val statusApareceMessages = resources.getStringArray(R.array.ranking_estado_aparece_messages)
        val statusNoApareceMessages = resources.getStringArray(R.array.ranking_estado_no_aparece_messages)

        for (i in rankingNames.indices) {
            val itemView = layoutInflater.inflate(R.layout.item_ranking_status, rankingListContainer, false)
            val tvRankingName = itemView.findViewById<TextView>(R.id.tvRankingName)
            val tvRankingStatus = itemView.findViewById<TextView>(R.id.tvRankingStatus)

            tvRankingName.text = rankingNames[i]

            if (rankingsStatus[i]) {
                val statusMsgIndex = Random.nextInt(statusApareceMessages.size)
                val statusMsg = statusApareceMessages[statusMsgIndex]
                tvRankingStatus.text = statusMsg
                tvRankingStatus.setTextColor(ContextCompat.getColor(this, R.color.blue_primary_darker))
            } else {
                val statusMsgIndex = Random.nextInt(statusNoApareceMessages.size)
                val statusMsg = statusNoApareceMessages[statusMsgIndex]
                tvRankingStatus.text = statusMsg
                tvRankingStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            }

            rankingListContainer.addView(itemView)
        }
    }

    private fun showIntegralRanking(integralScore: Double) {
        tvProgressIndicator.visibility = View.GONE
        tvMsgIntegralRanking.visibility = View.GONE
        rankingListContainer.visibility = View.GONE


        val username    = sharedPreferences.getString("savedUserName", getString(R.string.default_username))
            ?: getString(R.string.default_username)
        val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"

        rankingItems.clear()
        rankingItems.add(
            IntegralRankingItem(
                position       = 1,
                username       = username,
                countryCode    = countryCode,
                integralScore  = integralScore,
                isCurrentUser  = true
            )
        )

        adapter.notifyItemInserted(0)
        recyclerView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
