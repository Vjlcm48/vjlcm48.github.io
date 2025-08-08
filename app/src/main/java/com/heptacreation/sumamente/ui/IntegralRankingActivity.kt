package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.DataSyncManager
import com.heptacreation.sumamente.ui.utils.MusicManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random


class IntegralRankingActivity : BaseActivity(), LinkAccountDialogFragment.LinkAccountDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var tvMsgIntegralRanking: TextView
    private lateinit var tvProgressIndicator: TextView
    private lateinit var btnBack: ImageView
    private lateinit var rankingListContainer: LinearLayout
    private lateinit var adapter: IntegralRankingAdapter
    private lateinit var rankingItems: MutableList<IntegralRankingItem>
    private lateinit var btnShareIntegralRanking: FloatingActionButton

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var isFinishingByBack = false
    private lateinit var floatingLinkButton: View
    private var isDialogFromFloatingButton = false
    private var pulseAnimator: ValueAnimator? = null
    private var colorAnimator: ValueAnimator? = null

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
        setupShareButton()
        setupShareFabMovable()
        ensureFreshThen { loadIntegralRankingData() }

        // Inicio del cambio flecha de regresar del celular
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val intent = Intent(this@IntegralRankingActivity, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        // Fin del código de flecha de regresar del celular

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
        floatingLinkButton = findViewById(R.id.floating_link_button)

        btnShareIntegralRanking = findViewById(R.id.btnShareIntegralRanking)

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

    private fun setupShareButton() {
        btnShareIntegralRanking.setOnClickListener { compartirIntegralRanking() }
    }

    private fun setupShareFabMovable() {
        @SuppressLint("ClickableViewAccessibility")
        btnShareIntegralRanking.setOnTouchListener(object : View.OnTouchListener {
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

    private fun compartirIntegralRanking() {
        val currentUser = rankingItems.firstOrNull { it.isCurrentUser }
        if (currentUser != null) {
            val mensaje = getString(
                R.string.share_integral_ranking_message,
                currentUser.integralScore,
                currentUser.position
            )
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, mensaje)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }
    }

    private fun isUserEligibleForIntegralRanking(): Boolean {
        val rankingsStatus = checkUserRankingsStatus()
        return rankingsStatus.all { it } // Debe estar en TODOS los 9 rankings
    }

    private fun calculateAveragePosition(): Double {

        val positions = listOf(
            ScoreManager.getUserPositionInRanking("GLOBAL"),
            ScoreManager.getUserPositionInRanking("VEL_NUMEROS"),
            ScoreManager.getUserPositionInRanking("VEL_DECI"),
            ScoreManager.getUserPositionInRanking("VEL_ALFANUM"),
            ScoreManager.getUserPositionInRanking("VEL_ROMAS"),
            ScoreManager.getUserPositionInRanking("VEL_SUMARESTA"),
            ScoreManager.getUserPositionInRanking("VEL_MAS"),
            ScoreManager.getUserPositionInRanking("VEL_GENIOS"),
            ScoreManager.getUserPositionInRanking("IQ_PLUS")
        )

        return positions.average()
    }

    private fun loadIntegralRankingData() {

        loadingIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvMsgIntegralRanking.visibility = View.GONE
        tvProgressIndicator.visibility = View.GONE
        rankingListContainer.visibility = View.GONE
        floatingLinkButton.visibility = View.GONE
        btnShareIntegralRanking.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            val rankingsStatus = checkUserRankingsStatus()
            val rankingsCount  = rankingsStatus.count { it }

            loadingIndicator.visibility = View.GONE
            handleLinkAccountInvitation()

            when (rankingsCount) {
                0      -> showNoRankingsMessage()
                in 1..8 -> showProgressMessage(rankingsCount, rankingsStatus)
                9      -> {

                    calculateAveragePosition()
                    showIntegralRanking()
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

    private fun showIntegralRanking() {
        tvProgressIndicator.visibility = View.GONE
        tvMsgIntegralRanking.visibility = View.GONE
        rankingListContainer.visibility = View.GONE

        val username    = sharedPreferences.getString("savedUserName", getString(R.string.default_username))
            ?: getString(R.string.default_username)
        val countryCode = sharedPreferences.getString("savedCountryCode", "us") ?: "us"

        if (!isUserEligibleForIntegralRanking()) {

            return
        }

        val averagePosition = calculateAveragePosition()

        // Subir datos a Firebase y obtener ranking real
        DataSyncManager.uploadIntegralRankingToFirebase(
            userId = getUserId(),
            userName = username,
            country = countryCode,
            averagePosition = averagePosition
        )

        DataSyncManager.getTopIntegralRanking(
            userId = getUserId(),
            userName = username,
            country = countryCode,
            averagePosition = averagePosition
        ) { rankingList, userPosition, userItem ->
            rankingItems.clear()
            rankingItems.addAll(rankingList)


            if (userItem != null && userPosition > 200) {
                rankingItems.add(userItem.copy(position = userPosition))
            }
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
            recyclerView.visibility = View.VISIBLE
            btnShareIntegralRanking.visibility = View.VISIBLE
        }



        btnShareIntegralRanking.visibility = View.VISIBLE
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
                            Toast.makeText(this@IntegralRankingActivity, getString(R.string.toast_button_dismissed), Toast.LENGTH_SHORT).show()

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
                    loadIntegralRankingData()
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
        loadIntegralRankingData()
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
        loadIntegralRankingData()
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



