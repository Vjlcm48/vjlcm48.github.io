package com.heptacreation.sumamente.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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
import com.heptacreation.sumamente.R
import androidx.core.content.edit
import androidx.activity.enableEdgeToEdge

class NotificationsActivity : BaseActivity() {

    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val icon = findViewById<ImageView>(R.id.icon)
        val appName = findViewById<TextView>(R.id.app_name)
        val bellIcon = findViewById<ImageView>(R.id.bell_icon)
        val notificationText = findViewById<TextView>(R.id.notification_text)
        val btnAllow = findViewById<AppCompatButton>(R.id.btn_allow)
        val btnDeny = findViewById<AppCompatButton>(R.id.btn_deny)
        val mediaPlayer = MediaPlayer.create(this, R.raw.clicbotones)


        startEntranceAnimation(icon, appName, bellIcon, notificationText, btnAllow, btnDeny)
        startBellAnimation(bellIcon)

        val buttonClickListener = { button: View ->
            val animation = AnimationUtils.loadAnimation(this, R.anim.button_press)
            button.startAnimation(animation)
            mediaPlayer.start()
            Handler(Looper.getMainLooper()).postDelayed({
                fadeOutScreen()
            }, animation.duration)
        }

        btnAllow.setOnClickListener {
            sharedPreferences.edit { putBoolean(SettingsActivity.NOTIFICATIONS_ENABLED, true) }
            buttonClickListener(btnAllow)
        }

        btnDeny.setOnClickListener {
            sharedPreferences.edit { putBoolean(SettingsActivity.NOTIFICATIONS_ENABLED, false) }
            buttonClickListener(btnDeny)
        }
    }

    private fun startEntranceAnimation(vararg views: View) {
        views.forEachIndexed { index, view ->
            view.translationY = 50f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay((150 * index).toLong())
                .start()
        }
    }

    private fun startBellAnimation(bell: ImageView) {
        ObjectAnimator.ofFloat(bell, "rotation", 0f, 15f, -15f, 10f, -10f, 5f, -5f, 0f).apply {
            duration = 2500
            startDelay = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
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
