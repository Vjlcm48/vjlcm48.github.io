package com.example.sumamente.ui

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.example.sumamente.R
import com.example.sumamente.ui.utils.MusicManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class DesafiosActivity : BaseActivity() {

    private lateinit var tvAppName: TextView
    private lateinit var tvMensajeDesafios: TextView
    private lateinit var tvMesActual: TextView
    private lateinit var gridCalendario: GridLayout
    private lateinit var btnEntendido: Button
    private lateinit var btnClose: ImageView
    private lateinit var btnBack: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desafios)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        initViews()
        startBackgroundMusic()
        animateAppName()
        showWelcomeMessage()
        setupCalendar()
        setupButtons()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MusicManager.stop()
                returnToMainGame()
            }
        })

        startEntryAnimations()
    }

    private fun initViews() {
        tvAppName = findViewById(R.id.tv_app_name)
        tvMensajeDesafios = findViewById(R.id.tv_mensaje_desafios)
        tvMesActual = findViewById(R.id.tv_mes_actual)
        gridCalendario = findViewById(R.id.grid_calendario)
        btnEntendido = findViewById(R.id.btn_entendido)
        btnClose = findViewById(R.id.btn_close)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun startBackgroundMusic() {
        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            MusicManager.play(this, R.raw.desafios, looping = true, volume = 0.2f)
        }
    }

    private fun animateAppName() {
        val colorAnimator = ValueAnimator.ofArgb(
            getColor(R.color.blue_primary),
            getColor(R.color.red_primary)
        ).apply {
            duration = 2000L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                tvAppName.setTextColor(animator.animatedValue as Int)
            }
        }
        colorAnimator.start()
    }

    private fun startEntryAnimations() {
        val viewsToAnimate = listOf(
            findViewById(R.id.btn_back),
            findViewById(R.id.btn_close),
            findViewById(R.id.icon_desafios),
            findViewById(R.id.tv_app_name),
            findViewById(R.id.card_calendario),
            findViewById<View>(R.id.btn_entendido)
        )

        viewsToAnimate.forEachIndexed { index, view ->
            view.translationY = 50f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(100 + (index * 100L))
                .start()
        }
    }

    private fun showWelcomeMessage() {
        tvMensajeDesafios.visibility = View.VISIBLE
        tvMensajeDesafios.alpha = 0f
        tvMensajeDesafios.scaleX = 0.7f
        tvMensajeDesafios.scaleY = 0.7f

        val translateY = ObjectAnimator.ofFloat(tvMensajeDesafios, "translationY", 100f, 0f)
        translateY.duration = 500

        val scaleX = ObjectAnimator.ofFloat(tvMensajeDesafios, "scaleX", 0.7f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(tvMensajeDesafios, "scaleY", 0.7f, 1.1f, 1f)
        val alpha = ObjectAnimator.ofFloat(tvMensajeDesafios, "alpha", 0f, 1f)

        val bounceAnimator = AnimatorSet()
        bounceAnimator.playTogether(scaleX, scaleY, alpha)
        bounceAnimator.duration = 800

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(translateY, bounceAnimator)
        animatorSet.start()
    }

    private fun setupCalendar() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
        val currentLocale = resources.configuration.locales.get(0)
        val monthFormat = SimpleDateFormat("MMMM yyyy", currentLocale)
        tvMesActual.text = monthFormat.format(calendar.time).capitalize(currentLocale)

        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)

        var startOffset = firstDayOfWeek - Calendar.MONDAY
        if (startOffset < 0) startOffset += 7

        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        gridCalendario.removeAllViews()

        for (i in 1..42) {
            val dayView = TextView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(4, 4, 4, 4)
                }
                gravity = Gravity.CENTER
                setPadding(8, 16, 8, 16)
                textSize = 16f
            }

            val dayNumber = i - startOffset
            if (dayNumber in 1..daysInMonth) {
                dayView.text = dayNumber.toString()

                if (dayNumber == currentDay) {
                    dayView.background = ContextCompat.getDrawable(this, R.drawable.button_background)
                    dayView.setTextColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    val dayOfWeek = (i % 7)
                    when (dayOfWeek) {
                        6 -> dayView.setTextColor(ContextCompat.getColor(this, R.color.blue_primary))
                        0 -> dayView.setTextColor(ContextCompat.getColor(this, R.color.red_primary))
                        else -> dayView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    }
                }
            } else {
                dayView.text = ""
            }
            gridCalendario.addView(dayView)
        }
    }

    private fun setupButtons() {
        btnEntendido.setOnClickListener {
            applyBounceEffect(it) {
                MusicManager.stop()
                returnToMainGame()
            }
        }

        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                MusicManager.stop()
                returnToMainGame()
            }
        }

        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                MusicManager.stop()
                returnToMainGame()
            }
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
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }

    private fun returnToMainGame() {
        val intent = Intent(this, MainGameActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    override fun onPause() {
        super.onPause()
        MusicManager.pause()
    }

    override fun onResume() {
        super.onResume()
        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled) {
            MusicManager.resume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun String.capitalize(locale: Locale): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }
}
