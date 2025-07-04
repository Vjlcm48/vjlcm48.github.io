package com.example.sumamente.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.sumamente.R

class UsernameSelectionActivity : AppCompatActivity() {

    private val occupiedUsernames = listOf("victor121$", "jose2376#", "maestro333$$", "r_marcano40")
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        setContentView(R.layout.activity_username_selection)


        findViewById<ImageView>(R.id.icon)
        val appName = findViewById<TextView>(R.id.app_name)
        val usernameInput = findViewById<EditText>(R.id.username_input)
        val btnAccept = findViewById<Button>(R.id.btn_accept)
        val checkIcon = findViewById<ImageView>(R.id.check_icon)
        val errorMessage = findViewById<TextView>(R.id.error_message)
        val mediaPlayer = MediaPlayer.create(this, R.raw.clicbotones)

        val colorAnimator = ValueAnimator.ofArgb(
            getColor(R.color.blue_primary),
            getColor(R.color.red_primary)
        ).apply {
            duration = 2000L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                appName.setTextColor(animator.animatedValue as Int)
            }
        }
        colorAnimator.start()

        btnAccept.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            errorMessage.visibility = View.GONE
            if (username.length in 4..12) {
                mediaPlayer.start()
                val animation = AnimationUtils.loadAnimation(this, R.anim.button_press)
                btnAccept.startAnimation(animation)
                Handler(Looper.getMainLooper()).postDelayed({
                    checkUsernameAvailability(username, checkIcon, errorMessage)
                }, animation.duration)
            } else {
                showErrorMessage(errorMessage, getString(R.string.error_invalid_username_length))
            }
        }
    }

    private fun checkUsernameAvailability(username: String, checkIcon: ImageView, errorMessage: TextView) {
        val normalizedUsername = username.lowercase()
        if (occupiedUsernames.map { it.lowercase() }.contains(normalizedUsername)) {
            showErrorMessage(errorMessage, getString(R.string.error_username_taken))
            val mediaPlayerError = MediaPlayer.create(this, R.raw.sonidoerror)
            mediaPlayerError.start()
            mediaPlayerError.setOnCompletionListener { mediaPlayerError.release() }
        } else {
            errorMessage.visibility = View.GONE
            checkIcon.visibility = View.VISIBLE

            sharedPreferences.edit { putString("savedUserName", username) }

            animateCheck(checkIcon)

            val mediaPlayerCheck = MediaPlayer.create(this, R.raw.notificacionpo)
            mediaPlayerCheck.start()
            mediaPlayerCheck.setOnCompletionListener { mediaPlayerCheck.release() }

            Handler(Looper.getMainLooper()).postDelayed({
                navigateToMainGame()
            }, 600)
        }
    }

    private fun navigateToMainGame() {
        val intent = Intent(this, MainGameActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun animateCheck(view: ImageView) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f
        view.visibility = View.VISIBLE

        val animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(animatorX, animatorY, animatorAlpha)
            start()
        }
    }

    private fun showErrorMessage(errorMessage: TextView, message: String) {
        errorMessage.text = message
        errorMessage.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        val mediaPlayer = MediaPlayer.create(this, R.raw.clicbotones)
        mediaPlayer.release()
    }
}
