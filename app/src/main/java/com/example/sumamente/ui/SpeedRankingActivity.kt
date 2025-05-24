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

        Handler(Looper.getMainLooper()).postDelayed({
            val gameType = intent.getStringExtra(EXTRA_GAME_TYPE) ?: return@postDelayed
            val newItems = generateFakeSpeedData(gameType)

            rankingItems.clear()
            rankingItems.addAll(newItems)

            if (rankingItems.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.notifyItemRangeChanged(0, newItems.size)
            }

            loadingIndicator.visibility = View.GONE
        }, 1500)
    }

    private fun generateFakeSpeedData(gameType: String): List<SpeedRankingItem> {
        val baseTime = when (gameType) {
            SpeedClassificationActivity.GAME_NUMEROS_PLUS -> 1.78f
            SpeedClassificationActivity.GAME_DECI_PLUS -> 2.25f
            SpeedClassificationActivity.GAME_ROMAS -> 2.80f
            SpeedClassificationActivity.GAME_ALFA_NUMEROS -> 3.20f
            SpeedClassificationActivity.GAME_SUMA_RESTA -> 3.65f
            SpeedClassificationActivity.GAME_MAS_PLUS -> 4.10f
            SpeedClassificationActivity.GAME_GENIO_PLUS -> 4.75f
            else -> 3.00f
        }

        val usernames = listOf(
            "SpeedMaster", "QuickBrain", "FastThink", "RapidMind", "SwiftPlayer",
            "TurboGamer", "VelocityKing", "LightningFast", "SpeedDemon", "FlashBrain",
            "RocketPlayer", "BulletTime", "QuickSilver", "FastForward", "SpeedStar",
            "RapidFire", "TurboCharged", "VelocityPro", "SpeedRunner", "QuickStrike"
        )

        val countries = listOf(
            "us", "es", "mx", "br", "ar", "co", "pe", "cl", "ca", "fr",
            "de", "it", "jp", "kr", "cn", "in", "au", "nz", "se", "no"
        )

        return (1..20).map { position ->
            val variance = (Math.random() * 2.0 - 1.0).toFloat()
            val time = baseTime + (variance * baseTime * 0.3f)
            val finalTime = maxOf(0.5f, time)

            SpeedRankingItem(
                position = position,
                username = usernames.random(),
                countryCode = countries.random(),
                averageTime = finalTime,
                isCurrentUser = position == 7
            )
        }.sortedBy { it.averageTime }.mapIndexed { index, item ->
            item.copy(position = index + 1)
        }
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
