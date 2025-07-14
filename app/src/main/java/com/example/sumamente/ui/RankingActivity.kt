package com.example.sumamente.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import kotlin.random.Random

class RankingActivity : BaseActivity()  {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var adapter: RankingAdapter
    private lateinit var rankingItems: MutableList<RankingItem>
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var tvMsgGlobalRanking: TextView

    companion object {
        const val MIN_LEVELS_REQUIRED = 36
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScoreManager.ensurePreferencesInitialized(this)
        setContentView(R.layout.activity_ranking)

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
        loadRankingData()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.ranking_recycler_view)
        loadingIndicator = findViewById(R.id.loading_indicator)
        emptyView = findViewById(R.id.empty_view)
        btnBack = findViewById(R.id.btn_back)
        tvMsgGlobalRanking = findViewById(R.id.tvMsgGlobalRanking)

        recyclerView.layoutManager = LinearLayoutManager(this)
        rankingItems = mutableListOf()
        adapter = RankingAdapter(rankingItems)
        recyclerView.adapter = adapter
    }

    private fun setupMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.clasificacion)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.2f, 0.2f)

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            mediaPlayer.start()
        }
    }

    private fun setupButtons() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getTotalLevelsPlayed(): Int {
        return ScoreManager.getTotalUniqueLevelsCompletedAllGames()
    }

    private fun loadRankingData() {
        loadingIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.GONE
        tvMsgGlobalRanking.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({

            val totalLevels = getTotalLevelsPlayed()
            val username = sharedPreferences.getString("savedUserName", getString(R.string.default_username)) ?: getString(R.string.default_username)
            val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"
            val score = ScoreManager.currentScore + ScoreManager.currentScorePrincipiante + ScoreManager.currentScorePro +
                    ScoreManager.currentScoreDeciPlus + ScoreManager.currentScoreDeciPlusPrincipiante + ScoreManager.currentScoreDeciPlusPro +
                    ScoreManager.currentScoreRomas + ScoreManager.currentScoreRomasPrincipiante + ScoreManager.currentScoreRomasPro +
                    ScoreManager.currentScoreAlfaNumeros + ScoreManager.currentScoreAlfaNumerosPrincipiante + ScoreManager.currentScoreAlfaNumerosPro +
                    ScoreManager.currentScoreSumaResta + ScoreManager.currentScoreSumaRestaPrincipiante + ScoreManager.currentScoreSumaRestaPro +
                    ScoreManager.currentScoreMasPlus + ScoreManager.currentScoreMasPlusPrincipiante + ScoreManager.currentScoreMasPlusPro +
                    ScoreManager.currentScoreGenioPlus + ScoreManager.currentScoreGenioPlusPrincipiante + ScoreManager.currentScoreGenioPlusPro

            if (totalLevels == 0) {

                val noGamesMsgs = arrayOf(
                    getString(R.string.msg_need_more_games_global_1, username),
                    getString(R.string.msg_need_more_games_global_2, username),
                    getString(R.string.msg_need_more_games_global_3, username),
                    getString(R.string.msg_need_more_games_global_4, username),
                    getString(R.string.msg_need_more_games_global_5, username),
                    getString(R.string.msg_need_more_games_global_6, username)
                )
                val msg = noGamesMsgs[Random.nextInt(noGamesMsgs.size)]
                tvMsgGlobalRanking.apply {
                    visibility = View.VISIBLE
                    text = msg
                }
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                return@postDelayed
            }

            if (totalLevels in 1 until MIN_LEVELS_REQUIRED) {

                val levelsRemaining = MIN_LEVELS_REQUIRED - totalLevels

                val infoPluralsWithName = arrayOf(
                    R.plurals.msg_info_global_levels_1,
                    R.plurals.msg_info_global_levels_2,
                    R.plurals.msg_info_global_levels_3,
                    R.plurals.msg_info_global_levels_4,
                    R.plurals.msg_info_global_levels_5,
                    R.plurals.msg_info_global_levels_6
                )
                val infoPluralsWithoutName = arrayOf(
                    R.plurals.msg_info_global_levels_7,
                    R.plurals.msg_info_global_levels_8,
                    R.plurals.msg_info_global_levels_9,
                    R.plurals.msg_info_global_levels_10,
                    R.plurals.msg_info_global_levels_11,
                    R.plurals.msg_info_global_levels_12
                )

                val infoMsgsWithName = infoPluralsWithName.map {
                    resources.getQuantityString(it, totalLevels, username, totalLevels, levelsRemaining)
                }.toTypedArray()
                val infoMsgsWithoutName = infoPluralsWithoutName.map {
                    resources.getQuantityString(it, totalLevels, totalLevels, levelsRemaining)
                }.toTypedArray()


                val motivMsgsWithName = arrayOf(
                    getString(R.string.msg_motiv_global_levels_1, username),
                    getString(R.string.msg_motiv_global_levels_2, username),
                    getString(R.string.msg_motiv_global_levels_3, username),
                    getString(R.string.msg_motiv_global_levels_4, username),
                    getString(R.string.msg_motiv_global_levels_5, username),
                    getString(R.string.msg_motiv_global_levels_6, username)
                )
                val motivMsgsWithoutName = arrayOf(
                    getString(R.string.msg_motiv_global_levels_7),
                    getString(R.string.msg_motiv_global_levels_8),
                    getString(R.string.msg_motiv_global_levels_9),
                    getString(R.string.msg_motiv_global_levels_10),
                    getString(R.string.msg_motiv_global_levels_11),
                    getString(R.string.msg_motiv_global_levels_12)
                )

                val infoWithName = Random.nextBoolean()
                val infoMsg = if (infoWithName)
                    infoMsgsWithName[Random.nextInt(infoMsgsWithName.size)]
                else
                    infoMsgsWithoutName[Random.nextInt(infoMsgsWithoutName.size)]

                val motivMsg = if (infoWithName)
                    motivMsgsWithoutName[Random.nextInt(motivMsgsWithoutName.size)]
                else
                    motivMsgsWithName[Random.nextInt(motivMsgsWithName.size)]

                val combinedMsg = "$infoMsg\n\n$motivMsg"

                tvMsgGlobalRanking.apply {
                    visibility = View.VISIBLE
                    text = combinedMsg
                    textSize = 24f
                    gravity = Gravity.CENTER
                }
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                return@postDelayed
            }

            tvMsgGlobalRanking.visibility = View.GONE

            val oldSize = rankingItems.size
            rankingItems.clear()
            if (oldSize > 0) {
                adapter.notifyItemRangeRemoved(0, oldSize)
            }
            rankingItems.add(
                RankingItem(
                    position = 1,
                    username = username,
                    countryCode = countryCode,
                    score = score,
                    isCurrentUser = true
                )
            )
            adapter.notifyItemInserted(0)

            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            loadingIndicator.visibility = View.GONE
        }, 700)
    }

    override fun onResume() {
        super.onResume()
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        loadRankingData()
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

