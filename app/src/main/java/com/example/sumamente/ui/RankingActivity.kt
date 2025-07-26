package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import com.example.sumamente.ui.utils.MusicManager
import kotlin.random.Random

class RankingActivity : BaseActivity(), LinkAccountDialogFragment.LinkAccountDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var adapter: RankingAdapter
    private lateinit var rankingItems: MutableList<RankingItem>

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var tvMsgGlobalRanking: TextView

    private var isFinishingByBack = false
    private lateinit var floatingLinkButton: View
    private var isDialogFromFloatingButton = false

    private var pulseAnimator: ValueAnimator? = null
    private var colorAnimator: ValueAnimator? = null

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

        tvMsgGlobalRanking = findViewById(R.id.tvMsgGlobalRanking)
        floatingLinkButton = findViewById(R.id.floating_link_button)

        setupFloatingButtonInteractions()
    }

    private fun setupButtons() {
        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                finish()
            }
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
        floatingLinkButton.visibility = View.GONE

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
                    getString(R.string.msg_need_more_games_global_1, username), getString(R.string.msg_need_more_games_global_2, username),
                    getString(R.string.msg_need_more_games_global_3, username), getString(R.string.msg_need_more_games_global_4, username),
                    getString(R.string.msg_need_more_games_global_5, username), getString(R.string.msg_need_more_games_global_6, username)
                )
                tvMsgGlobalRanking.text = noGamesMsgs.random()
                tvMsgGlobalRanking.visibility = View.VISIBLE
                loadingIndicator.visibility = View.GONE
                return@postDelayed
            }


            handleLinkAccountInvitation()

            if (totalLevels >= MIN_LEVELS_REQUIRED) {

                rankingItems.clear()
                rankingItems.add(
                    RankingItem(
                        position = 1, username = username, countryCode = countryCode,
                        score = score, isCurrentUser = true
                    )
                )
                adapter.notifyItemInserted(0)
                recyclerView.visibility = View.VISIBLE

            } else {

                val levelsRemaining = MIN_LEVELS_REQUIRED - totalLevels
                val infoPluralsWithName = arrayOf(
                    R.plurals.msg_info_global_levels_1, R.plurals.msg_info_global_levels_2, R.plurals.msg_info_global_levels_3,
                    R.plurals.msg_info_global_levels_4, R.plurals.msg_info_global_levels_5, R.plurals.msg_info_global_levels_6
                )
                val infoPluralsWithoutName = arrayOf(
                    R.plurals.msg_info_global_levels_7, R.plurals.msg_info_global_levels_8, R.plurals.msg_info_global_levels_9,
                    R.plurals.msg_info_global_levels_10, R.plurals.msg_info_global_levels_11, R.plurals.msg_info_global_levels_12
                )
                val infoMsgsWithName = infoPluralsWithName.map {
                    resources.getQuantityString(it, totalLevels, username, totalLevels, levelsRemaining)
                }.toTypedArray()
                val infoMsgsWithoutName = infoPluralsWithoutName.map {
                    resources.getQuantityString(it, totalLevels, totalLevels, levelsRemaining)
                }.toTypedArray()
                val motivMsgsWithName = arrayOf(
                    getString(R.string.msg_motiv_global_levels_1, username), getString(R.string.msg_motiv_global_levels_2, username),
                    getString(R.string.msg_motiv_global_levels_3, username), getString(R.string.msg_motiv_global_levels_4, username),
                    getString(R.string.msg_motiv_global_levels_5, username), getString(R.string.msg_motiv_global_levels_6, username)
                )
                val motivMsgsWithoutName = arrayOf(
                    getString(R.string.msg_motiv_global_levels_7), getString(R.string.msg_motiv_global_levels_8),
                    getString(R.string.msg_motiv_global_levels_9), getString(R.string.msg_motiv_global_levels_10),
                    getString(R.string.msg_motiv_global_levels_11), getString(R.string.msg_motiv_global_levels_12)
                )
                val infoWithName = Random.nextBoolean()
                val infoMsg = if (infoWithName) infoMsgsWithName.random() else infoMsgsWithoutName.random()
                val motivMsg = if (infoWithName) motivMsgsWithoutName.random() else motivMsgsWithName.random()
                val combinedMsg = "$infoMsg\n\n$motivMsg"

                tvMsgGlobalRanking.apply {
                    visibility = View.VISIBLE
                    text = combinedMsg
                    textSize = 22f
                    gravity = Gravity.CENTER
                }
            }

            loadingIndicator.visibility = View.GONE
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
            getColor(R.color.blue_primary),
            getColor(R.color.green_accent),
            getColor(R.color.orange),
            getColor(R.color.cyan),
            getColor(R.color.blue_primary)
        ).apply {
            duration = 4000L
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
                            Toast.makeText(this@RankingActivity, getString(R.string.toast_button_dismissed), Toast.LENGTH_SHORT).show()

                            val cooldown = SettingsActivity.COOLDOWN_FLOAT_DISMISS
                            sharedPreferences.edit {
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

    override fun onAcceptLink() {

        sharedPreferences.edit {
            putBoolean(SettingsActivity.ACCOUNT_LINKED, true)
        }
        Toast.makeText(this, getString(R.string.account_linked_success), Toast.LENGTH_LONG).show()

        loadRankingData()
    }

    override fun onNotNow() {
        val cooldown = if (isDialogFromFloatingButton) {
            SettingsActivity.COOLDOWN_FLOAT_DIALOG_DISMISS
        } else {
            SettingsActivity.COOLDOWN_NOT_NOW
        }

        sharedPreferences.edit {
            putBoolean(SettingsActivity.LINK_PROMPT_INTERACTED, true)
            putLong(SettingsActivity.LAST_PROMPT_DISMISSAL_TIMESTAMP, System.currentTimeMillis() + cooldown)
        }
        Toast.makeText(this, getString(R.string.toast_cooldown_long), Toast.LENGTH_SHORT).show()
        isDialogFromFloatingButton = false

        loadRankingData()
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

        loadRankingData()
    }


}

