package com.example.sumamente.ui

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.example.sumamente.R
import com.google.android.material.transition.platform.MaterialSharedAxis
import java.util.Locale

class MainGameActivity : BaseActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var locationManager: LocationManager
    private lateinit var profileText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var trophyContainer: FrameLayout
    private lateinit var trophyRedDot: View
    private var fadeHandler: Handler? = null
    private var fadeRunnable: Runnable? = null
    private var isActivityVisible = false

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val geocoder = Geocoder(this@MainGameActivity, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val countryCode = addresses[0].countryCode.lowercase(Locale.getDefault())
                        val resId = FlagsAdapter.flagResourceMap[countryCode]
                        if (resId != null) {

                            runOnUiThread {
                                val drawable = ContextCompat.getDrawable(this@MainGameActivity, resId)
                                profileText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                            }
                        }
                        sharedPreferences.edit { putString("savedCountryCode", countryCode) }
                    }
                    locationManager.removeUpdates(this)
                }
            } else {
                @Suppress("DEPRECATION")
                try {
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val countryCode = addresses[0].countryCode.lowercase(Locale.getDefault())
                        val resId = FlagsAdapter.flagResourceMap[countryCode]
                        if (resId != null) {
                            val drawable = ContextCompat.getDrawable(this@MainGameActivity, resId)
                            profileText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                        }
                        sharedPreferences.edit { putString("savedCountryCode", countryCode) }
                    }
                } catch (_: Exception) {
                }
                locationManager.removeUpdates(this)
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)

        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        setContentView(R.layout.activity_main_game)

        CondecoracionTracker.init(this)
        scheduleDailyCondecoracionesWork()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        profileText = findViewById(R.id.profile_text)

        val newGameButton = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.new_game_button)
        val homeIcon = findViewById<ImageView>(R.id.home_icon)
        val statisticsIcon = findViewById<ImageView>(R.id.statistics_icon)
        val settingsIcon = findViewById<ImageView>(R.id.settings_icon)
        findViewById<ImageView>(R.id.trophy_icon)
        val calendarIcon = findViewById<ImageView>(R.id.calendar_icon)

        trophyContainer = findViewById(R.id.trophy_container)
        trophyRedDot = findViewById(R.id.trophy_red_dot)


        initializeMediaPlayer()

        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SettingsActivity.SOUND_ENABLED && isActivityVisible) {
                val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
                if (soundEnabled) {
                    startMusic()
                } else {
                    stopMusic()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        newGameButton.setOnClickListener {
            applyBounceEffect(it) {
                fadeOutAndStop()
                val intent = Intent(this, GameSelectionActivity::class.java)
                startActivity(intent)
            }
        }

        homeIcon.setOnClickListener {
            applyBounceEffect(it) {

            }
        }

        statisticsIcon.setOnClickListener {
            applyBounceEffect(it) {
                fadeOutAndStop()
                startActivity(Intent(this, ClassificationActivity::class.java))
            }
        }

        settingsIcon.setOnClickListener {
            applyBounceEffect(it) {
                fadeOutAndStop()
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        profileText.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, ProfileEditActivity::class.java)
                startActivity(intent)
            }
        }

        calendarIcon.setOnClickListener {
            applyBounceEffect(it) {
                fadeOutAndStop()
                startActivity(Intent(this, DesafiosActivity::class.java))
            }
        }

        trophyContainer.setOnClickListener {
            applyBounceEffect(it) {

                fadeOutAndStop()
                val intent = Intent(this, TrofeosActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun initializeMediaPlayer() {

        releaseMediaPlayer()


        mediaPlayer = MediaPlayer.create(this, R.raw.principal).apply {
            isLooping = true
            setVolume(0.2f, 0.2f)
        }


        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            startMusic()
        }
    }

    private fun startMusic() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                try {
                    it.setVolume(0.2f, 0.2f)
                    it.start()
                } catch (_: IllegalStateException) {

                    initializeMediaPlayer()
                }
            }
        }
    }

    private fun stopMusic() {
        cancelFadeOut()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    private fun fadeOutAndStop() {
        cancelFadeOut()

        mediaPlayer?.let { player ->
            if (!player.isPlaying) return

            fadeHandler = Handler(Looper.getMainLooper())
            val fadeOutDuration = 2000L
            val fadeStep = 0.05f
            var currentVolume = 0.2f

            fadeRunnable = object : Runnable {
                override fun run() {
                    if (player.isPlaying) {
                        currentVolume -= fadeStep
                        if (currentVolume > 0) {
                            try {
                                player.setVolume(currentVolume, currentVolume)
                                fadeHandler?.postDelayed(
                                    this,
                                    (fadeOutDuration / (1 / fadeStep)).toLong()
                                )
                            } catch (_: IllegalStateException) {

                            }
                        } else {
                            try {
                                player.pause()
                                player.setVolume(0f, 0f)
                            } catch (_: IllegalStateException) {

                            }
                        }
                    }
                }
            }
            fadeHandler?.post(fadeRunnable!!)
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
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            } catch (_: IllegalStateException) {

            }
        }
        mediaPlayer = null
    }

    private fun updateTrophyRedDot() {
        if (CondecoracionTracker.shouldShowTrophyRedDot()) {
            trophyRedDot.visibility = View.VISIBLE
        } else {
            trophyRedDot.visibility = View.GONE
        }
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

    private fun scheduleDailyCondecoracionesWork() {
        val delayMs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val zoneId = java.time.ZoneId.of("America/Chicago")
            val now = java.time.ZonedDateTime.now(zoneId)
            val next0h = now.withHour(0).withMinute(0).withSecond(0).withNano(0)
                .let { if (now >= it) it.plusDays(1) else it }
            java.time.Duration.between(now, next0h).toMillis()
        } else {
            val chicagoZone = java.util.TimeZone.getTimeZone("America/Chicago")
            val now = java.util.Calendar.getInstance(chicagoZone)
            val nextMidnight = java.util.Calendar.getInstance(chicagoZone).apply {
                add(java.util.Calendar.DAY_OF_MONTH, 1)
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            nextMidnight.timeInMillis - now.timeInMillis
        }

        val work = androidx.work.PeriodicWorkRequest.Builder(
            com.example.sumamente.ui.utils.DailyCondecoracionesWorker::class.java,
            24,
            java.util.concurrent.TimeUnit.HOURS
        )
            .setInitialDelay(delayMs, java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()

        androidx.work.WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "daily_condecoraciones",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                work
            )
    }

    @Suppress("DEPRECATION")
    private fun getLocationAndSetFlag() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }


    override fun onResume() {
        super.onResume()

        isActivityVisible = true

        CondecoracionTracker.verificarYEntregarPines()
        CondecoracionTracker.verificarYActualizarCoronasDeVelocidad(this)
        CondecoracionTracker.verificarYActualizarCondecoracionesTop10(this)
        CondecoracionTracker.verificarYActualizarCondecoracionesIQ7(this)
        CondecoracionTracker.verificarYActualizarCondecoracionesTop5Integral(this)
        CondecoracionTracker.verificarYActualizarInsigniaRIPlus(this)
        updateTrophyRedDot()

        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled) {
            startMusic()
        }

        val savedUsername = sharedPreferences.getString("savedUserName", "Usuario")
        profileText.text = savedUsername

        val savedCountryCode = sharedPreferences.getString("savedCountryCode", null)
        if (savedCountryCode != null) {
            val resId = FlagsAdapter.flagResourceMap[savedCountryCode]
            if (resId != null) {
                val drawable = ContextCompat.getDrawable(this, resId)
                profileText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        } else {

            getLocationAndSetFlag()
        }
    }

    override fun onPause() {
        super.onPause()

        isActivityVisible = false

        stopMusic()
        locationManager.removeUpdates(locationListener)
    }

    override fun onStop() {
        super.onStop()

        stopMusic()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLocationAndSetFlag()
            } else {

                sharedPreferences.edit { putString("savedCountryCode", "sumamente") }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}
