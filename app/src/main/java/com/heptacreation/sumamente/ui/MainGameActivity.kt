package com.heptacreation.sumamente.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.DailyCondecoracionesWorker
import com.heptacreation.sumamente.ui.utils.DataSyncManager
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.Locale
import java.util.concurrent.TimeUnit
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import com.heptacreation.sumamente.ui.utils.MessagesStateManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainGameActivity : BaseActivity() {

    private companion object {
        const val PREFS_NAME = "MyPrefs"
        const val PERMISSION_REQUEST_LOCATION = 1
        const val MUSIC_VOLUME = 0.2f
        const val FADE_OUT_DURATION = 2000L
        const val FADE_STEP = 0.05f
        const val BOUNCE_SCALE_FACTOR = 0.9f
        const val BOUNCE_DURATION = 50L
        const val WORK_TAG = "daily_condecoraciones"
        const val WORK_INTERVAL_HOURS = 24L
        const val POST_RESUME_DEFER_MS = 2500L

        private const val CONDECORACIONES_INTERVAL_MS = 120_000L // 2 minutos
        private var lastCondecoracionesCheckMs = 0L
    }

    private var mediaPlayer: MediaPlayer? = null
    private var fadeHandler: Handler? = null
    private var fadeRunnable: Runnable? = null
    private var didCloudRestore = false

    private lateinit var locationManager: LocationManager
    private val locationListener = createLocationListener()
    private lateinit var profileText: TextView
    private lateinit var trophyContainer: FrameLayout
    private lateinit var trophyRedDot: View
    private lateinit var messagesRedDot: View

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var tvSumaMenteTitle: TextView
    private lateinit var settingsIcon: FrameLayout
    private lateinit var homeButton: TextView
    private lateinit var calendarButton: TextView
    private lateinit var statisticsButton: TextView
    private lateinit var welcomeOverlay: FrameLayout
    private var isActivityVisible = false
    private val postResumeHandler = Handler(Looper.getMainLooper())
    private var postResumeWork: Runnable? = null
    private var backgroundCondecoracionesThread: Thread? = null
    private var backgroundSyncThread: Thread? = null
    private var isNavigating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        configurarTransiciones()
        super.onCreate(savedInstanceState)

        inicializarPreferencias()
        setContentView(R.layout.activity_main_game)
        inicializarComponentes()

        //welcomeOverlay.bringToFront()

        configurarListeners()
        mostrarBienvenidaSiCorresponde()

        ajustarIconosNavegacion()

        configurarBackPressedCallback()
        inicializarCondecoraciones()
        inicializarMusica()
        aplicarAnimacionDeColor(tvSumaMenteTitle)

        if (sharedPreferences.getBoolean(SettingsActivity.ACCOUNT_LINKED, false) && !didCloudRestore) {
            DataSyncManager.syncDataFromCloud(this) { ok, _ ->
                if (ok) didCloudRestore = true
            }
        }

        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "check_firebase_setup")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }


    private fun configurarTransiciones() {
        window.exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    }

    private fun inicializarPreferencias() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        configurarPreferenceListener()
    }

    private fun configurarPreferenceListener() {
        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SettingsActivity.SOUND_ENABLED && isActivityVisible) {
                actualizarEstadoMusica()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun inicializarComponentes() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        profileText = findViewById(R.id.profile_text)
        trophyContainer = findViewById(R.id.trophy_container)
        trophyRedDot = findViewById(R.id.trophy_red_dot)
        tvSumaMenteTitle = findViewById(R.id.tv_sumamente_title)
        settingsIcon = findViewById(R.id.settings_icon)
        messagesRedDot = findViewById(R.id.messages_red_dot)
        homeButton = findViewById(R.id.home_button)
        calendarButton = findViewById(R.id.calendar_button)
        statisticsButton = findViewById(R.id.statistics_button)
        welcomeOverlay = findViewById(R.id.welcome_overlay)

    }

    private fun configurarListeners() {
        configurarBotonNewGame()
        configurarBotonesNavegacion()
        configurarBotonPerfil()
        configurarBotonTrofeos()
    }

    private fun configurarBotonNewGame() {
        findViewById<AppCompatButton>(R.id.new_game_button).setOnClickListener {
            aplicarEfectoConAccion(it) {
                navegarA(GameSelectionActivity::class.java)
            }
        }
    }

    private fun configurarBotonesNavegacion() {

        findViewById<FrameLayout>(R.id.settings_icon).setOnClickListener {
            aplicarEfectoConAccion(it) {
                navegarA(SettingsActivity::class.java)
            }
        }

        findViewById<TextView>(R.id.home_button).setOnClickListener {
            aplicarEfectoBounce(it)
        }

        findViewById<TextView>(R.id.statistics_button).setOnClickListener {
            aplicarEfectoConAccion(it) {
                navegarA(ClassificationActivity::class.java)
            }
        }

        findViewById<TextView>(R.id.calendar_button).setOnClickListener {
            aplicarEfectoConAccion(it) {
                navegarA(DesafiosActivity::class.java)
            }
        }

    }

    private fun configurarBotonPerfil() {
        profileText.setOnClickListener {
            aplicarEfectoBounce(it) {
                startActivity(Intent(this, ProfileEditActivity::class.java))
            }
        }
    }

    private fun configurarBotonTrofeos() {
        trophyContainer.setOnClickListener {
            aplicarEfectoConAccion(it) {
                navegarA(TrofeosActivity::class.java)
            }
        }
    }

    private fun configurarBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (::welcomeOverlay.isInitialized && welcomeOverlay.isVisible) {
                    ocultarBienvenida()
                    return
                }

            }
        })
    }


    private fun ajustarIconosNavegacion() {

        val iconoReferencia = settingsIcon

        iconoReferencia.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                iconoReferencia.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val anchoIcono = iconoReferencia.width
                val altoIcono = iconoReferencia.height

                val botonesNavegacion = listOf(homeButton, calendarButton, statisticsButton)


                for (boton in botonesNavegacion) {
                    val icono = boton.compoundDrawables[1]

                    icono?.setBounds(0, 0, anchoIcono, altoIcono)

                    boton.setCompoundDrawables(
                        boton.compoundDrawables[0],
                        icono,
                        boton.compoundDrawables[2],
                        boton.compoundDrawables[3]
                    )
                }
            }
        })
    }

    private fun inicializarCondecoraciones() {
        scheduleDailyCondecoracionesWork()
        backgroundSyncThread?.interrupt()
        backgroundSyncThread = Thread {
            try {
                ScoreManager.init(this)
                CondecoracionTracker.init(this)
                runOnUiThread {
                    if (!isFinishing && !isDestroyed) {
                        updateTrophyRedDot()
                        updateMessagesRedDot()
                    }
                }
            } catch (_: Exception) { }
        }.apply { start() }
    }

    private fun inicializarMusica() {
        initializeMediaPlayer()
    }

    private fun navegarA(activityClass: Class<*>) {
        if (isNavigating) return
        isNavigating = true
        fadeOutAndStop()
        startActivity(Intent(this, activityClass))
    }

    private fun aplicarEfectoConAccion(view: View, accion: () -> Unit) {
        applyBounceEffect(view, accion)
    }

    private fun aplicarEfectoBounce(view: View, accion: (() -> Unit)? = null) {
        applyBounceEffect(view) {
            accion?.invoke()
        }
    }

    private fun initializeMediaPlayer() {
        releaseMediaPlayer()

        mediaPlayer = MediaPlayer.create(this, R.raw.principal).apply {
            isLooping = true
            setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
        }

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            startMusic()
        }
    }

    private fun actualizarEstadoMusica() {
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled) {
            startMusic()
        } else {
            stopMusic()
        }
    }

    private fun startMusic() {
        mediaPlayer?.let { player ->
            if (!player.isPlaying) {
                try {
                    player.setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
                    player.start()
                } catch (_: IllegalStateException) {
                    initializeMediaPlayer()
                }
            }
        }
    }

    private fun stopMusic() {
        cancelFadeOut()
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
            }
        }
    }

    private fun fadeOutAndStop() {
        cancelFadeOut()

        mediaPlayer?.let { player ->
            if (!player.isPlaying) return

            fadeHandler = Handler(Looper.getMainLooper())
            var currentVolume = MUSIC_VOLUME

            fadeRunnable = Runnable {
                if (player.isPlaying) {
                    currentVolume -= FADE_STEP
                    if (currentVolume > 0) {
                        aplicarVolumen(player, currentVolume)
                    } else {
                        pausarYResetearVolumen(player)
                    }
                }
            }
            fadeHandler?.post(fadeRunnable!!)
        }
    }

    private fun aplicarVolumen(player: MediaPlayer, volume: Float) {
        try {
            player.setVolume(volume, volume)
            fadeHandler?.postDelayed(
                fadeRunnable!!,
                (FADE_OUT_DURATION / (1 / FADE_STEP)).toLong()
            )
        } catch (_: IllegalStateException) {

        }
    }

    private fun pausarYResetearVolumen(player: MediaPlayer) {
        try {
            player.pause()
            player.setVolume(0f, 0f)
        } catch (_: IllegalStateException) {

        }
    }

    private fun cancelFadeOut() {
        fadeRunnable?.let {
            fadeHandler?.removeCallbacks(it)
        }
        fadeHandler = null
        fadeRunnable = null
    }

    private fun releaseMediaPlayer() {
        cancelFadeOut()
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.reset()
                player.release()
            } catch (_: IllegalStateException) {

            }
        }
        mediaPlayer = null
    }

    private fun createLocationListener() = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            procesarUbicacion(location)
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun procesarUbicacion(location: Location) {
        val geocoder = Geocoder(this@MainGameActivity, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            procesarUbicacionModerno(geocoder, location)
        } else {
            procesarUbicacionLegacy(geocoder, location)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun procesarUbicacionModerno(geocoder: Geocoder, location: Location) {
        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
            if (addresses.isNotEmpty()) {
                procesarDireccion(addresses[0].countryCode)
            }
            locationManager.removeUpdates(locationListener)
        }
    }

    @Suppress("DEPRECATION")
    private fun procesarUbicacionLegacy(geocoder: Geocoder, location: Location) {
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                procesarDireccion(addresses[0].countryCode)
            }
        } catch (_: Exception) {

        }
        locationManager.removeUpdates(locationListener)
    }

    private fun procesarDireccion(countryCode: String) {
        val codigoPais = countryCode.lowercase(Locale.getDefault())
        actualizarBandera(codigoPais)
        guardarCodigoPais(codigoPais)
    }

    private fun actualizarBandera(codigoPais: String) {
        FlagsAdapter.flagResourceMap[codigoPais]?.let { resId ->
            val drawable = ContextCompat.getDrawable(this@MainGameActivity, resId)
            runOnUiThread {
                profileText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        }
    }

    private fun guardarCodigoPais(codigoPais: String) {
        sharedPreferences.edit {
            putString("savedCountryCode", codigoPais)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @Suppress("DEPRECATION")
    private fun getLocationAndSetFlag() {
        if (tienePermisoUbicacion()) {
            solicitarActualizacionUbicacion()
        } else {
            solicitarPermisoUbicacion()
        }
    }

    private fun tienePermisoUbicacion(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun solicitarActualizacionUbicacion() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    private fun solicitarPermisoUbicacion() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_LOCATION
        )
    }

    private fun updateTrophyRedDot() {
        trophyRedDot.visibility = if (CondecoracionTracker.shouldShowTrophyRedDot()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateMessagesRedDot() {
        val visibleInicial = MessagesStateManager.hasGlobalRedDot(this)
        messagesRedDot.visibility = if (visibleInicial) View.VISIBLE else View.GONE

        MessagesStateManager.ensureActivationByThresholds(this) {
            if (!isFinishing && !isDestroyed) {
                val visibleActualizado = MessagesStateManager.hasGlobalRedDot(this)
                messagesRedDot.visibility = if (visibleActualizado) View.VISIBLE else View.GONE
            }
        }
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val animatorSet = crearAnimacionBounce(view)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }

    private fun crearAnimacionBounce(view: View): AnimatorSet {
        val scaleDown = crearAnimacionEscala(view, 1f, BOUNCE_SCALE_FACTOR)
        val scaleUp = crearAnimacionEscala(view, BOUNCE_SCALE_FACTOR, 1f)

        return AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
        }
    }

    private fun crearAnimacionEscala(view: View, desde: Float, hasta: Float): AnimatorSet {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", desde, hasta).setDuration(BOUNCE_DURATION)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", desde, hasta).setDuration(BOUNCE_DURATION)

        return AnimatorSet().apply {
            playTogether(scaleX, scaleY)
        }
    }

    private fun aplicarAnimacionDeColor(textView: TextView) {
        val colorAnimator = android.animation.ValueAnimator.ofArgb(

            ContextCompat.getColor(this, R.color.blue_primary_darker),
            ContextCompat.getColor(this, R.color.blue_primary),
            ContextCompat.getColor(this, R.color.grey_dark),
            ContextCompat.getColor(this, R.color.red_primary)
        ).apply {

            duration = 8000L

            repeatMode = android.animation.ValueAnimator.REVERSE
            repeatCount = android.animation.ValueAnimator.INFINITE
            addUpdateListener { animator ->
                textView.setTextColor(animator.animatedValue as Int)
            }
        }
        colorAnimator.start()
    }

    private fun scheduleDailyCondecoracionesWork() {
        val workManager = WorkManager.getInstance(this)
        workManager.getWorkInfosForUniqueWorkLiveData(WORK_TAG)
            .observe(this) { infos ->
                val yaExisteActivo = infos?.any {
                    it.state == androidx.work.WorkInfo.State.ENQUEUED ||
                            it.state == androidx.work.WorkInfo.State.RUNNING
                } ?: false
                if (!yaExisteActivo) {
                    val delayMs = calcularDelayHastaMedianoche()
                    val work = crearTrabajoPeriodicoCondecoraciones(delayMs)
                    workManager.enqueueUniquePeriodicWork(
                        WORK_TAG,
                        ExistingPeriodicWorkPolicy.KEEP,
                        work
                    )
                }
            }
    }

    private fun calcularDelayHastaMedianoche(): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            calcularDelayModerno()
        } else {
            calcularDelayLegacy()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcularDelayModerno(): Long {
        val zoneId = java.time.ZoneId.of("America/Chicago")
        val now = java.time.ZonedDateTime.now(zoneId)
        val next0h = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
            .let { if (now >= it) it.plusDays(1) else it }
        return java.time.Duration.between(now, next0h).toMillis()
    }

    private fun calcularDelayLegacy(): Long {
        val chicagoZone = java.util.TimeZone.getTimeZone("America/Chicago")
        val now = java.util.Calendar.getInstance(chicagoZone)
        val nextMidnight = java.util.Calendar.getInstance(chicagoZone).apply {
            add(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        return nextMidnight.timeInMillis - now.timeInMillis
    }

    private fun crearTrabajoPeriodicoCondecoraciones(delayMs: Long): PeriodicWorkRequest {
        return PeriodicWorkRequest.Builder(
            DailyCondecoracionesWorker::class.java,
            WORK_INTERVAL_HOURS,
            TimeUnit.HOURS
        )
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .build()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onResume() {
        super.onResume()
        isActivityVisible = true
        isNavigating = false

        actualizarEstadoMusica()
        actualizarPerfil()
        updateTrophyRedDot()

        postResumeWork?.let { postResumeHandler.removeCallbacks(it) }
        postResumeWork = Runnable {
            if (!isActivityVisible || isFinishing || isDestroyed) return@Runnable

            val nowCond = System.currentTimeMillis()
            if (nowCond - lastCondecoracionesCheckMs > CONDECORACIONES_INTERVAL_MS) {
                lastCondecoracionesCheckMs = nowCond
                backgroundCondecoracionesThread?.interrupt()
                backgroundCondecoracionesThread = Thread {
                    try {
                        actualizarCondecoraciones()
                        runOnUiThread {
                            if (!isFinishing && !isDestroyed && isActivityVisible) {
                                updateTrophyRedDot()
                                updateMessagesRedDot()
                            }
                        }
                    } catch (_: Exception) { }
                }.apply { start() }
            }
        }

        postResumeHandler.postDelayed(postResumeWork!!, POST_RESUME_DEFER_MS)
    }

    private fun actualizarCondecoraciones() {
        CondecoracionTracker.verificarYEntregarPines()
        CondecoracionTracker.verificarYActualizarCoronasDeVelocidad(this)
        CondecoracionTracker.verificarYActualizarCondecoracionesTop10(this)
        CondecoracionTracker.verificarYActualizarCondecoracionesIQ7(this)
        CondecoracionTracker.verificarYActualizarCondecoracionesTop5Integral(this)
        CondecoracionTracker.verificarYActualizarInsigniaRIPlus(this)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun actualizarPerfil() {
        actualizarNombreUsuario()
        actualizarBanderaPais()
    }

    private fun actualizarNombreUsuario() {
        val savedUsername = sharedPreferences.getString("savedUserName", null)

        if (!savedUsername.isNullOrBlank()) {
            profileText.text = savedUsername
            return
        }

        profileText.text = getString(R.string.default_username)

        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val usernameFirestore = document.getString("username")

                if (!usernameFirestore.isNullOrBlank()) {
                    sharedPreferences.edit {
                        putString("savedUserName", usernameFirestore)
                    }

                    if (!isFinishing && !isDestroyed) {
                        profileText.text = usernameFirestore
                    }
                }
            }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun actualizarBanderaPais() {
        val savedCountryCode = sharedPreferences.getString("savedCountryCode", null)
        if (savedCountryCode != null) {
            mostrarBanderaGuardada(savedCountryCode)
        } else {
            getLocationAndSetFlag()
        }
    }

    private fun mostrarBanderaGuardada(countryCode: String) {
        FlagsAdapter.flagResourceMap[countryCode]?.let { resId ->
            val drawable = ContextCompat.getDrawable(this, resId)
            profileText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        }
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
        postResumeWork?.let { postResumeHandler.removeCallbacks(it) }
        backgroundCondecoracionesThread?.interrupt()
        backgroundCondecoracionesThread = null
        backgroundSyncThread?.interrupt()
        backgroundSyncThread = null
        stopMusic()
        locationManager.removeUpdates(locationListener)
    }

    override fun onStop() {
        super.onStop()
        stopMusic()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            procesarResultadoPermisoUbicacion(grantResults)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun procesarResultadoPermisoUbicacion(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndSetFlag()
        } else {
            establecerPaisPorDefecto()
        }
    }

    private fun establecerPaisPorDefecto() {
        sharedPreferences.edit {
            putString("savedCountryCode", "sumamente")
        }
    }

    private fun mostrarBienvenidaSiCorresponde() {
        val yaMostrado = sharedPreferences.getBoolean("welcome_shown", false)
        if (yaMostrado) return

        sharedPreferences.edit { putBoolean("welcome_shown", true) }

        welcomeOverlay.bringToFront()
        welcomeOverlay.elevation = 32f
        welcomeOverlay.requestLayout()

        welcomeOverlay.visibility = View.VISIBLE

        findViewById<View>(R.id.welcome_scrim)?.setOnClickListener {
            ocultarBienvenida()
        }

        findViewById<ImageView>(R.id.welcome_close)?.setOnClickListener {
            ocultarBienvenida()
        }

        findViewById<View>(R.id.welcome_modal_container)?.setOnClickListener { /* consume */ }
    }

    private fun ocultarBienvenida() {

        welcomeOverlay.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
