package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import com.example.sumamente.R
import com.google.android.material.textfield.TextInputLayout

class UsernameSelectionActivity : BaseActivity() {

    private lateinit var sharedPreferences: android.content.SharedPreferences

    private lateinit var icon: ImageView
    private lateinit var instructionText: TextView
    private lateinit var usernameInputLayout: TextInputLayout
    private lateinit var btnAccept: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        setContentView(R.layout.activity_username_selection)

        icon = findViewById(R.id.icon)
        instructionText = findViewById(R.id.instruction_text)
        usernameInputLayout = findViewById(R.id.username_input_layout)
        btnAccept = findViewById(R.id.btn_accept)

        startEntranceAnimation()

        btnAccept.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.button_press)
            btnAccept.startAnimation(animation)

            animation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    validateUsername()
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
        }

    }

    private fun startEntranceAnimation() {
        val duration = 500L
        val delay = 200L

        icon.animate().alpha(1f).setDuration(duration).setStartDelay(0).start()
        instructionText.animate().alpha(1f).translationY(-20f).setDuration(duration).setStartDelay(delay).start()
        usernameInputLayout.animate().alpha(1f).translationY(-20f).setDuration(duration).setStartDelay(delay * 2).start()
        btnAccept.animate().alpha(1f).translationY(-20f).setDuration(duration).setStartDelay(delay * 3).start()
    }

    private fun validateUsername() {

        playClickSound()

        val username = usernameInputLayout.editText?.text.toString().trim()
        usernameInputLayout.error = null
        usernameInputLayout.endIconMode = TextInputLayout.END_ICON_NONE

        if (username.length !in 4..12) {
            usernameInputLayout.error = getString(R.string.error_invalid_username_length)
            playErrorSound()
            shakeView(usernameInputLayout)
            return
        }

        btnAccept.isEnabled = false

        checkUsernameAvailable(username) { available ->
            if (!available) {
                usernameInputLayout.error = getString(R.string.error_username_taken)
                playErrorSound()
                shakeView(usernameInputLayout)
                btnAccept.isEnabled = true
                return@checkUsernameAvailable
            }

                playValidationSound()
                showSuccessCheckmark()

                Handler(Looper.getMainLooper()).postDelayed({
                    usernameInputLayout.isEnabled = false
                    saveUsernameAndProceed(username)
                }, 1500)
        }
    }

    private fun checkUsernameAvailable(username: String, onResult: (Boolean) -> Unit) {

        Handler(Looper.getMainLooper()).postDelayed({
            onResult(username.lowercase() != "admin")
        }, 700)
    }

    private fun showSuccessCheckmark() {

        usernameInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        usernameInputLayout.setEndIconDrawable(R.drawable.ic_check_green)
        usernameInputLayout.endIconContentDescription = getString(R.string.check_description)
        usernameInputLayout.setEndIconTintList(null)
        usernameInputLayout.isEndIconVisible = true

        val endIconView = usernameInputLayout.findViewById<ImageView>(com.google.android.material.R.id.text_input_end_icon)
        endIconView?.let { animateCheck(it) }
    }

    private fun saveUsernameAndProceed(username: String) {
        sharedPreferences.edit {
            putString("savedUserName", username)
            putBoolean("isAccountLinked", false)
        }

        val fadeOutDuration = 400L
        val viewsToFade = listOf(icon, instructionText, usernameInputLayout, btnAccept)
        viewsToFade.forEachIndexed { index, view ->
            view.animate()
                .alpha(0f)
                .translationY(40f)
                .setDuration(fadeOutDuration)
                .setStartDelay((index * 50).toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (index == viewsToFade.lastIndex) {
                            navigateToMainGame()
                        }
                    }
                })
                .start()
        }
    }

    private fun navigateToMainGame() {
        val intent = Intent(this, MainGameActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun shakeView(view: View) {
        view.animate()
            .translationX(-25f)
            .setDuration(50)
            .withEndAction {
                view.animate()
                    .translationX(25f)
                    .setDuration(50)
                    .withEndAction {
                        view.animate().translationX(0f).setDuration(50).start()
                    }.start()
            }.start()
    }

    private fun playClickSound() {
        MediaPlayer.create(this, R.raw.clicbotones).apply {
            start()
            setOnCompletionListener { release() }
        }
    }

    private fun playErrorSound() {
        MediaPlayer.create(this, R.raw.sonidoerror).apply {
            start()
            setOnCompletionListener { release() }
        }
    }

    private fun playValidationSound() {
        MediaPlayer.create(this, R.raw.notificacionpo).apply {
            start()
            setOnCompletionListener { release() }
        }
    }

    private fun animateCheck(view: ImageView) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f
        view.visibility = View.VISIBLE

        val animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f).apply {
            duration = 600
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        val animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f).apply {
            duration = 600
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        val animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f).apply {
            duration = 600
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        AnimatorSet().apply {
            playTogether(animatorX, animatorY, animatorAlpha)
            start()
        }
    }

}
