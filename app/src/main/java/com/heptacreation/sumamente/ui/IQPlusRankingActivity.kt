package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heptacreation.sumamente.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale
import kotlin.random.Random
import com.heptacreation.sumamente.ui.utils.DataSyncManager
import androidx.activity.enableEdgeToEdge


import com.heptacreation.sumamente.ui.utils.MusicManager

class IQPlusRankingActivity : BaseActivity(), LinkAccountDialogFragment.LinkAccountDialogListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IQPlusRankingAdapter
    private lateinit var tvMsgIQPlus: TextView
    private lateinit var btnShareRanking: FloatingActionButton
    private lateinit var spinnerFiltro: Spinner
    private lateinit var btnBack: ImageView
    private lateinit var loadingIndicator: ProgressBar

    private val rankingIQPlus: MutableList<IQPlusRankingItem> = mutableListOf()
    private val rankingIQPlusFiltrado: MutableList<IQPlusRankingItem> = mutableListOf()
    private var iqPlusFlash = false
    private val handlerFlash = Handler(Looper.getMainLooper())

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var isFinishingByBack = false

    private lateinit var floatingLinkButton: View
    private var isDialogFromFloatingButton = false
    private var pulseAnimator: ValueAnimator? = null
    private var colorAnimator: ValueAnimator? = null

    companion object {
        const val TOTAL_COMBOS_REQUIRED = 21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        ScoreManager.ensurePreferencesInitialized(this)
        setContentView(R.layout.activity_iqplus_ranking)

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
        setupRecyclerView()
        setupShareButton()
        setupShareFabMovable()
        setupBackButton()
        setupFloatingButtonInteractions()

        spinnerFiltro.visibility = View.GONE
        ensureFreshThen { loadIQPlusRankingData() }

        // Inicio del cambio flecha de regresar del celular
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val intent = Intent(this@IQPlusRankingActivity, MainGameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        // Fin del código de flecha de regresar del celular

    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewIQPlus)
        tvMsgIQPlus = findViewById(R.id.tvMsgIQPlus)
        btnShareRanking = findViewById(R.id.btnShareRanking)
        spinnerFiltro = findViewById(R.id.spinnerFiltro)
        btnBack = findViewById(R.id.btnBack)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        floatingLinkButton = findViewById(R.id.floating_link_button)
    }

    private fun setupRecyclerView() {
        adapter = IQPlusRankingAdapter(
            rankingIQPlusFiltrado,
            onIQPlusClick = { mostrarDialogoEstadisticas() }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupShareButton() {
        btnShareRanking.setOnClickListener { compartirRanking() }
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                isFinishingByBack = true
                finish()
            }
        }
    }

    private fun loadIQPlusRankingData() {
        loadingIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvMsgIQPlus.visibility = View.GONE
        btnShareRanking.visibility = View.GONE
        floatingLinkButton.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            rankingIQPlus.clear()
            rankingIQPlusFiltrado.clear()

            val nombre = getUsuarioActualNombre()
            val pais = getUsuarioActualPais()
            val iqPlus = calcularIQPlus()
            val haCompletado = haJugadoTodosLosCombos()
            val combosCompletados = getCombosCompletados()

            loadingIndicator.visibility = View.GONE

            if (combosCompletados == 0) {
                mostrarMensajeInicial(nombre)
                recyclerView.visibility = View.GONE
                btnShareRanking.visibility = View.GONE
                return@postDelayed
            }

            handleLinkAccountInvitation()

            if (!haCompletado) {
                mostrarMensajesMotivacionales(nombre, iqPlus, combosCompletados)
                recyclerView.visibility = View.GONE
                btnShareRanking.visibility = View.GONE
                return@postDelayed
            } else {
                tvMsgIQPlus.visibility = View.GONE
            }

            DataSyncManager.uploadIQPlusToFirebase(
                userId = getUserId(),
                userName = nombre,
                country = pais,
                iqPlus = iqPlus
            )

            DataSyncManager.getTopIQPlusRanking(
                userId = getUserId(),
                userName = nombre,
                country = pais,
                iqPlus = iqPlus
            ) { rankingList, userPosition, userItem ->
                rankingIQPlus.clear()
                rankingIQPlusFiltrado.clear()
                rankingIQPlus.addAll(rankingList)
                rankingIQPlusFiltrado.addAll(rankingList)

                if (userItem != null && userPosition > 200) {
                    rankingIQPlusFiltrado.add(userItem.copy(position = userPosition))
                }
                @Suppress("NotifyDataSetChanged")
                adapter.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE
                btnShareRanking.visibility = View.VISIBLE
            }
        }, 800)
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
                            Toast.makeText(this@IQPlusRankingActivity, getString(R.string.toast_button_dismissed), Toast.LENGTH_SHORT).show()

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
        btnShareRanking.setOnTouchListener(object : View.OnTouchListener {
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

    private fun mostrarMensajeInicial(nombre: String) {
        val startMsgIndex = Random.nextInt(6)
        val startMsg = getString(
            when(startMsgIndex) {
                0 -> R.string.msg_iqplus_start_1
                1 -> R.string.msg_iqplus_start_2
                2 -> R.string.msg_iqplus_start_3
                3 -> R.string.msg_iqplus_start_4
                4 -> R.string.msg_iqplus_start_5
                else -> R.string.msg_iqplus_start_6
            },
            nombre
        )

        tvMsgIQPlus.apply {
            visibility = View.VISIBLE
            text = startMsg
            textSize = 24f
            setTextColor(getColorFromAttr(this@IQPlusRankingActivity, R.attr.colorOnBackground))
        }
    }

    private fun mostrarMensajesMotivacionales(
        nombre: String,
        iqPlus: Double,
        combosCompletados: Int
    ) {
        val combosFaltantes = TOTAL_COMBOS_REQUIRED - combosCompletados
        val infoMsgIndex = Random.nextInt(6)
        val infoMsg = getString(
            when(infoMsgIndex) {
                0 -> R.string.msg_iqplus_info_1
                1 -> R.string.msg_iqplus_info_2
                2 -> R.string.msg_iqplus_info_3
                3 -> R.string.msg_iqplus_info_4
                4 -> R.string.msg_iqplus_info_5
                else -> R.string.msg_iqplus_info_6
            },
            iqPlus
        )

        val pluralsIds = arrayOf(
            R.plurals.msg_iqplus_levels_info_1,
            R.plurals.msg_iqplus_levels_info_2,
            R.plurals.msg_iqplus_levels_info_3,
            R.plurals.msg_iqplus_levels_info_4,
            R.plurals.msg_iqplus_levels_info_5,
            R.plurals.msg_iqplus_levels_info_6
        )
        val levelsInfoMsgIndex = Random.nextInt(pluralsIds.size)
        var levelsInfoMsg = resources.getQuantityString(
            pluralsIds[levelsInfoMsgIndex],
            combosCompletados,
            nombre, combosCompletados, combosFaltantes
        )

        val motivMsgIndex = Random.nextInt(6)
        var motivMsg = getString(
            when(motivMsgIndex) {
                0 -> R.string.msg_iqplus_motiv_1
                1 -> R.string.msg_iqplus_motiv_2
                2 -> R.string.msg_iqplus_motiv_3
                3 -> R.string.msg_iqplus_motiv_4
                4 -> R.string.msg_iqplus_motiv_5
                else -> R.string.msg_iqplus_motiv_6
            },
            nombre
        )

        val levelsInfoHasName = levelsInfoMsg.contains(nombre)
        if (levelsInfoHasName) {

            motivMsg = motivMsg.replace(nombre, "").replace(", !", "!").replace(", ¡", "¡")
        } else if (motivMsg.contains(nombre)) {

            levelsInfoMsg = levelsInfoMsg.replace(nombre, "").replace(", !", "!").replace(", ¡", "¡")
        }

        val mensajeFinal = "$infoMsg\n\n$levelsInfoMsg\n\n$motivMsg"
        val spannableText = hacerIQPlusInteractivo(mensajeFinal, iqPlus)

        tvMsgIQPlus.apply {
            visibility = View.VISIBLE
            text = spannableText
            textSize = 22f
            setTextColor(getColorFromAttr(this@IQPlusRankingActivity, R.attr.colorOnBackground))
            movementMethod = LinkMovementMethod.getInstance()
            gravity = Gravity.CENTER
        }
        tvMsgIQPlus.highlightColor = Color.TRANSPARENT
    }

    private fun hacerIQPlusInteractivo(texto: String, iqPlus: Double): SpannableString {
        val spannableString = SpannableString(texto)
        val iqPlusComoTexto = String.format(Locale.getDefault(), "%.2f", iqPlus)

        val start = texto.indexOf(iqPlusComoTexto)

        if (start != -1) {
            val end = start + iqPlusComoTexto.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    iqPlusFlash = true
                    (widget as TextView).text = hacerIQPlusInteractivo(texto, iqPlus)

                    handlerFlash.postDelayed({
                        iqPlusFlash = false
                        widget.text = hacerIQPlusInteractivo(texto, iqPlus)

                        handlerFlash.postDelayed({
                            mostrarDialogoEstadisticas()
                        }, 80)
                    }, 150)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false

                    if (iqPlusFlash) {
                        ds.color = ContextCompat.getColor(this@IQPlusRankingActivity, R.color.yellow_dark)
                        ds.isFakeBoldText = true
                        ds.setShadowLayer(4f, 0f, 0f, Color.DKGRAY)
                    } else {
                        ds.color = ContextCompat.getColor(this@IQPlusRankingActivity, R.color.blue_primary)
                        ds.isFakeBoldText = true
                        ds.setShadowLayer(2f, 1f, 1f, Color.GRAY)
                    }
                }
            }
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    private fun getCombosCompletados(): Int {
        var combosCompletados = 0
        for (combo in IQPlusCombos.ALL) {
            val maxNivel = ScoreManager.getMaxLevelForCombo(combo.juego, combo.grado)
            if (maxNivel > 0) {
                combosCompletados++
            }
        }
        return combosCompletados
    }

    private fun getUsuarioActualNombre(): String =
        sharedPreferences.getString("savedUserName", getString(R.string.default_username)) ?: getString(R.string.default_username)

    private fun getUsuarioActualPais(): String =
        sharedPreferences.getString("savedCountryCode", "us") ?: "us"

    private fun calcularIQPlus(): Double =
        ScoreManager.lastIqComponentByGame.values.sum()

    private fun haJugadoTodosLosCombos(): Boolean {
        return ScoreManager.haJugadoAlMenosUnNivelEnCadaJuegoYGrado()
    }

    private fun mostrarDialogoEstadisticas() {
        val precision = ScoreManager.getPrecisionGlobal()
        val tiempoPromedio = ScoreManager.getTiempoPromedioGlobal()
        val dialog = IQPlusStatsDialogFragment.newInstance(
            precision = precision,
            tiempoPromedio = tiempoPromedio
        )
        dialog.show(supportFragmentManager, "IQPlusStatsDialog")
    }

    private fun compartirRanking() {
        val posicion = 1
        val iqPlus = rankingIQPlusFiltrado.firstOrNull { it.username == getUsuarioActualNombre() }?.iqPlus ?: 0.0
        val mensaje = getString(R.string.share_iqplus_ranking_message, posicion, iqPlus)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, mensaje)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }

    private fun getColorFromAttr(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
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
                    loadIQPlusRankingData()
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

        loadIQPlusRankingData()
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

        loadIQPlusRankingData()
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

object IQPlusCombos {
    val ALL = listOf(
        // NÚMEROS+
        Combo("NumerosPlus", "Principiante", 2.0),
        Combo("NumerosPlus", "Avanzado", 5.0),
        Combo("NumerosPlus", "Pro", 8.0),
        // DECI+
        Combo("DeciPlus", "Principiante", 4.0),
        Combo("DeciPlus", "Avanzado", 7.0),
        Combo("DeciPlus", "Pro", 10.0),
        // ROMAS
        Combo("Romas", "Principiante", 6.0),
        Combo("Romas", "Avanzado", 9.0),
        Combo("Romas", "Pro", 12.0),
        // ALFANUMEROS
        Combo("AlfaNumeros", "Principiante", 7.0),
        Combo("AlfaNumeros", "Avanzado", 10.0),
        Combo("AlfaNumeros", "Pro", 13.0),
        // SUMARESTA
        Combo("SumaResta", "Principiante", 8.0),
        Combo("SumaResta", "Avanzado", 11.0),
        Combo("SumaResta", "Pro", 14.0),
        // MAS+
        Combo("MasPlus", "Principiante", 10.0),
        Combo("MasPlus", "Avanzado", 13.0),
        Combo("MasPlus", "Pro", 16.0),
        // GENIO+
        Combo("GenioPlus", "Principiante", 12.0),
        Combo("GenioPlus", "Avanzado", 15.0),
        Combo("GenioPlus", "Pro", 18.0)
    )
    data class Combo(val juego: String, val grado: String, val peso: Double)
}
