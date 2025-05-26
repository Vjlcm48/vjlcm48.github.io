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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class SpeedRankingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var adapter: SpeedRankingAdapter
    private lateinit var rankingItems: MutableList<SpeedRankingItem>
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: android.content.SharedPreferences

    companion object {
        const val EXTRA_GAME_TYPE = "game_type"
        const val EXTRA_GAME_COLOR = "game_color"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_ranking)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        initViews()
        setupButtons()
        setupMusic()
        setupGameSpecificUI()

        loadSpeedRankingData()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.speed_ranking_recycler_view)
        loadingIndicator = findViewById(R.id.loading_indicator)
        emptyView = findViewById(R.id.empty_view)
        btnBack = findViewById(R.id.btn_back)
        tvTitle = findViewById(R.id.tv_speed_ranking_title)
        rootLayout = findViewById(R.id.root_speed_ranking)

        recyclerView.layoutManager = LinearLayoutManager(this)
        rankingItems = mutableListOf()
        adapter = SpeedRankingAdapter(rankingItems)
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

    private fun setupGameSpecificUI() {
        val gameType = intent.getStringExtra(EXTRA_GAME_TYPE) ?: return
        val gameColor = intent.getIntExtra(EXTRA_GAME_COLOR, R.color.blue_primary)

        rootLayout.setBackgroundColor(ContextCompat.getColor(this, gameColor))

        val gameTitle = when (gameType) {
            SpeedClassificationActivity.GAME_NUMEROS_PLUS -> getString(R.string.game_numeros_plus)
            SpeedClassificationActivity.GAME_DECI_PLUS -> getString(R.string.game_deci_plus)
            SpeedClassificationActivity.GAME_ROMAS -> getString(R.string.game_romas)
            SpeedClassificationActivity.GAME_ALFA_NUMEROS -> getString(R.string.game_alfa_numeros)
            SpeedClassificationActivity.GAME_SUMA_RESTA -> getString(R.string.game_sumaresta)
            SpeedClassificationActivity.GAME_MAS_PLUS -> getString(R.string.game_mas_plus)
            SpeedClassificationActivity.GAME_GENIO_PLUS -> getString(R.string.game_genio_plus)
            else -> getString(R.string.speed_ranking_title)
        }

        tvTitle.text = getString(R.string.speed_ranking_game_title, gameTitle, getString(R.string.speed_ranking_title))
    }

    private fun loadSpeedRankingData() {
        loadingIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.GONE
        findViewById<TextView>(R.id.tvMsgSpeedRanking).visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            val gameType = intent.getStringExtra(EXTRA_GAME_TYPE) ?: return@postDelayed

            val (totalGames, partPrincipiante, partAvanzado, partPro, avgTime) = when (gameType) {
                SpeedClassificationActivity.GAME_NUMEROS_PLUS -> arrayOf(
                    ScoreManager.totalGamesNumerosPlusPrincipiante +
                            ScoreManager.totalGamesNumerosPlusAvanzado +
                            ScoreManager.totalGamesNumerosPlusPro,
                    ScoreManager.totalGamesNumerosPlusPrincipiante,
                    ScoreManager.totalGamesNumerosPlusAvanzado,
                    ScoreManager.totalGamesNumerosPlusPro,
                    ScoreManager.getTiempoPromedioNumerosPlus().toFloat()
                )
                SpeedClassificationActivity.GAME_DECI_PLUS -> arrayOf(
                    ScoreManager.totalGamesDeciPlusPrincipiante +
                            ScoreManager.totalGamesDeciPlusAvanzado +
                            ScoreManager.totalGamesDeciPlusPro,
                    ScoreManager.totalGamesDeciPlusPrincipiante,
                    ScoreManager.totalGamesDeciPlusAvanzado,
                    ScoreManager.totalGamesDeciPlusPro,
                    ScoreManager.getTiempoPromedioDeciPlus().toFloat()
                )
                SpeedClassificationActivity.GAME_ROMAS -> arrayOf(
                    ScoreManager.totalGamesRomasPrincipiante +
                            ScoreManager.totalGamesRomasAvanzado +
                            ScoreManager.totalGamesRomasPro,
                    ScoreManager.totalGamesRomasPrincipiante,
                    ScoreManager.totalGamesRomasAvanzado,
                    ScoreManager.totalGamesRomasPro,
                    ScoreManager.getTiempoPromedioRomas().toFloat()
                )
                SpeedClassificationActivity.GAME_ALFA_NUMEROS -> arrayOf(
                    ScoreManager.totalGamesAlfaNumerosPrincipiante +
                            ScoreManager.totalGamesAlfaNumerosAvanzado +
                            ScoreManager.totalGamesAlfaNumerosPro,
                    ScoreManager.totalGamesAlfaNumerosPrincipiante,
                    ScoreManager.totalGamesAlfaNumerosAvanzado,
                    ScoreManager.totalGamesAlfaNumerosPro,
                    ScoreManager.getTiempoPromedioAlfaNumeros().toFloat()
                )
                SpeedClassificationActivity.GAME_SUMA_RESTA -> arrayOf(
                    ScoreManager.totalGamesSumaRestaPrincipiante +
                            ScoreManager.totalGamesSumaRestaAvanzado +
                            ScoreManager.totalGamesSumaRestaPro,
                    ScoreManager.totalGamesSumaRestaPrincipiante,
                    ScoreManager.totalGamesSumaRestaAvanzado,
                    ScoreManager.totalGamesSumaRestaPro,
                    ScoreManager.getTiempoPromedioSumaResta().toFloat()
                )
                SpeedClassificationActivity.GAME_MAS_PLUS -> arrayOf(
                    ScoreManager.totalGamesMasPlusPrincipiante +
                            ScoreManager.totalGamesMasPlusAvanzado +
                            ScoreManager.totalGamesMasPlusPro,
                    ScoreManager.totalGamesMasPlusPrincipiante,
                    ScoreManager.totalGamesMasPlusAvanzado,
                    ScoreManager.totalGamesMasPlusPro,
                    ScoreManager.getTiempoPromedioMasPlus().toFloat()
                )
                SpeedClassificationActivity.GAME_GENIO_PLUS -> arrayOf(
                    ScoreManager.totalGamesGenioPlusPrincipiante +
                            ScoreManager.totalGamesGenioPlusAvanzado +
                            ScoreManager.totalGamesGenioPlusPro,
                    ScoreManager.totalGamesGenioPlusPrincipiante,
                    ScoreManager.totalGamesGenioPlusAvanzado,
                    ScoreManager.totalGamesGenioPlusPro,
                    ScoreManager.getTiempoPromedioGenioPlus().toFloat()
                )
                else -> arrayOf(0, 0, 0, 0, 0f)
            }

            val total = totalGames as Int
            val principiante = partPrincipiante as Int
            val avanzado = partAvanzado as Int
            val pro = partPro as Int
            val avg = avgTime as Float

            val minTotalGamesRequired = 30
            val minGamesEachMode = 5

            val username = sharedPreferences.getString("savedUserName", "Tú") ?: "Tú"
            val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"

            if (
                total < minTotalGamesRequired ||
                principiante < minGamesEachMode ||
                avanzado < minGamesEachMode ||
                pro < minGamesEachMode
            ) {
                val msg = getString(R.string.msg_need_more_games_speed, tvTitle.text.toString())
                val progressMsg = getString(
                    R.string.msg_progress_remaining_speed_3modes,
                    maxOf(0, minTotalGamesRequired - total),
                    maxOf(0, minGamesEachMode - principiante),
                    maxOf(0, minGamesEachMode - avanzado),
                    maxOf(0, minGamesEachMode - pro)
                )
                val combinedMsg = getString(R.string.msg_speed_ranking_combined, msg, progressMsg)
                findViewById<TextView>(R.id.tvMsgSpeedRanking).apply {
                    visibility = View.VISIBLE
                    text = combinedMsg
                }

                recyclerView.visibility = View.GONE
                emptyView.visibility = View.GONE
                loadingIndicator.visibility = View.GONE
                return@postDelayed
            } else {
                findViewById<TextView>(R.id.tvMsgSpeedRanking).visibility = View.GONE
            }

            rankingItems.clear()
            rankingItems.add(
                SpeedRankingItem(
                    position = 1,
                    username = username,
                    countryCode = countryCode,
                    averageTime = avg,
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
