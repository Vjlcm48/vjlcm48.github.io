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

        Handler(Looper.getMainLooper()).postDelayed({
            val newItems = listOf(
                RankingItem(1, "MasterMind", "us", 25750, true),
                RankingItem(2, "BrainGamer", "es", 24800, false),
                RankingItem(3, "Intelectual99", "mx", 22340, false),
                RankingItem(4, "GeniusPlayer", "br", 21220, false),
                RankingItem(5, "SmartBrain", "ar", 19800, false),
                RankingItem(6, "MathWizard", "co", 18750, false),
                RankingItem(7, "LogicPro", "pe", 17500, false),
                RankingItem(8, "NumberKing", "cl", 16820, false),
                RankingItem(9, "BrainTrainer", "ca", 16400, false),
                RankingItem(10, "MindMaster", "fr", 15980, false)
            )

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
