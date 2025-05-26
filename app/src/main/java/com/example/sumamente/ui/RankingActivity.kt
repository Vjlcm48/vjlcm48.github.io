package com.example.sumamente.ui

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class RankingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var adapter: RankingAdapter
    private lateinit var rankingItems: MutableList<RankingItem>
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

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

    private fun loadRankingData() {
        loadingIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.GONE
        findViewById<TextView>(R.id.tvMsgGlobalRanking).visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({

            val minGamesRequired = 20
            val minGamesTypesRequired = 2


            val totalGames = ScoreManager.totalGamesGlobal
            var juegosDistintos = 0
            if (ScoreManager.totalGamesNumerosPlus > 0) juegosDistintos++
            if (ScoreManager.totalGamesDeciPlus > 0) juegosDistintos++
            if (ScoreManager.totalGamesRomas > 0) juegosDistintos++
            if (ScoreManager.totalGamesAlfaNumeros > 0) juegosDistintos++
            if (ScoreManager.totalGamesSumaResta > 0) juegosDistintos++
            if (ScoreManager.totalGamesMasPlus > 0) juegosDistintos++
            if (ScoreManager.totalGamesGenioPlus > 0) juegosDistintos++


            val totalScoreGlobal =
                ScoreManager.currentScore + ScoreManager.currentScorePrincipiante + ScoreManager.currentScorePro +
                        ScoreManager.currentScoreDeciPlus + ScoreManager.currentScoreDeciPlusPrincipiante + ScoreManager.currentScoreDeciPlusPro +
                        ScoreManager.currentScoreRomas + ScoreManager.currentScoreRomasPrincipiante + ScoreManager.currentScoreRomasPro +
                        ScoreManager.currentScoreAlfaNumeros + ScoreManager.currentScoreAlfaNumerosPrincipiante + ScoreManager.currentScoreAlfaNumerosPro +
                        ScoreManager.currentScoreSumaResta + ScoreManager.currentScoreSumaRestaPrincipiante + ScoreManager.currentScoreSumaRestaPro +
                        ScoreManager.currentScoreMasPlus + ScoreManager.currentScoreMasPlusPrincipiante + ScoreManager.currentScoreMasPlusPro +
                        ScoreManager.currentScoreGenioPlus + ScoreManager.currentScoreGenioPlusPrincipiante + ScoreManager.currentScoreGenioPlusPro


            val username = sharedPreferences.getString("savedUserName", "Tú") ?: "Tú"
            val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"

            if (totalGames < minGamesRequired || juegosDistintos < minGamesTypesRequired) {
                val juegosFaltantes = minGamesTypesRequired - juegosDistintos
                val partidasFaltantes = minGamesRequired - totalGames
                val msg = getString(R.string.msg_need_more_games_global)
                val progressMsg = getString(R.string.msg_progress_remaining_global,
                    maxOf(0, partidasFaltantes), maxOf(0, juegosFaltantes))

                findViewById<TextView>(R.id.tvMsgGlobalRanking).apply {
                    visibility = View.VISIBLE
                    text = getString(R.string.msg_need_more_games_combined, msg, progressMsg)

                }
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                return@postDelayed
            } else {
                findViewById<TextView>(R.id.tvMsgGlobalRanking).visibility = View.GONE
            }

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
                    score = totalScoreGlobal,
                    isCurrentUser = true
                )
            )
            adapter.notifyItemInserted(0)

            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            loadingIndicator.visibility = View.GONE
        }, 800)
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
