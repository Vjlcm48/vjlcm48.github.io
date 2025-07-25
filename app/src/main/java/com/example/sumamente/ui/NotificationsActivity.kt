package com.example.sumamente.ui

import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.sumamente.R

class NotificationsActivity : BaseActivity()  {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        findViewById<ImageView>(R.id.icon)
        val appName = findViewById<TextView>(R.id.app_name)
        val btnAllow = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_allow)
        val btnDeny = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_deny)
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

        val buttonClickListener = { button: Button ->
            val animation = AnimationUtils.loadAnimation(this, R.anim.button_press)
            button.startAnimation(animation)
            mediaPlayer.start()
            Handler(Looper.getMainLooper()).postDelayed({
                fadeOutScreen()
            }, animation.duration)
        }

        btnAllow.setOnClickListener {
            buttonClickListener(btnAllow)
        }

        btnDeny.setOnClickListener {
            buttonClickListener(btnDeny)
        }
    }

    private fun fadeOutScreen() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.animate().alpha(0f).setDuration(500L).withEndAction {
            startNextActivity()
        }.start()
    }

    private fun startNextActivity() {
        val intent = Intent(this, UsernameSelectionActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }
}
