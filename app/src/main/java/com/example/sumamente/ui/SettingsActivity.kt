package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import com.example.sumamente.R

class SettingsActivity : BaseActivity()  {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val SOUND_ENABLED = "sound_enabled"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val ADS_ENABLED = "ads_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        setContentView(R.layout.activity_settings)

        val btnCloseSettings = findViewById<ImageView>(R.id.btn_close_settings)
        val switchSound = findViewById<SwitchCompat>(R.id.switch_sound)
        val switchNotifications = findViewById<SwitchCompat>(R.id.switch_notifications)
        val switchAds = findViewById<SwitchCompat>(R.id.switch_ads)
        val adsText = findViewById<TextView>(R.id.ads_text)

        val linearProfile = findViewById<LinearLayout>(R.id.linear_profile)
        val linearShare = findViewById<LinearLayout>(R.id.linear_share)
        val linearHelp = findViewById<LinearLayout>(R.id.linear_help)
        val linearResetProgress = findViewById<LinearLayout>(R.id.linear_reset_progress)
        val linearLanguage = findViewById<LinearLayout>(R.id.linear_language)

        btnCloseSettings.setOnClickListener { view ->
            applyBounceEffect(view) {
                finish()
            }
        }

        switchSound.isChecked = sharedPreferences.getBoolean(SOUND_ENABLED, true)
        switchSound.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, getString(if (isChecked) R.string.sound_enabled else R.string.sound_disabled), Toast.LENGTH_SHORT).show()
            sharedPreferences.edit { putBoolean(SOUND_ENABLED, isChecked) }
        }


        switchNotifications.isChecked = sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, true)
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, getString(if (isChecked) R.string.notifications_enabled else R.string.notifications_disabled), Toast.LENGTH_SHORT).show()
            sharedPreferences.edit { putBoolean(NOTIFICATIONS_ENABLED, isChecked) }
        }


        switchAds.isChecked = sharedPreferences.getBoolean(ADS_ENABLED, true)
        switchAds.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, getString(if (isChecked) R.string.ads_enabled else R.string.ads_disabled), Toast.LENGTH_SHORT).show()
            sharedPreferences.edit { putBoolean(ADS_ENABLED, isChecked) }
        }



        val colorAnimator = ValueAnimator.ofArgb(
            getColor(R.color.blue_primary),
            getColor(R.color.red_primary)
        ).apply {
            duration = 2000L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                adsText.setTextColor(animator.animatedValue as Int)
            }
        }
        colorAnimator.start()

        linearProfile.setOnClickListener { view ->
            applyBounceEffect(view) {
                val dialog = ProfileEditDialog(this)
                dialog.show()
            }
        }

        linearShare.setOnClickListener { view ->
            applyBounceEffect(view) {
                Toast.makeText(this, getString(R.string.share_with_friends), Toast.LENGTH_SHORT).show()
            }
        }

        linearHelp.setOnClickListener { view ->
            applyBounceEffect(view) {
                startActivity(Intent(this, HelpGameSelectionActivity::class.java))
            }
        }

        linearResetProgress.setOnClickListener { view ->
            applyBounceEffect(view) {
                val intent = Intent(this, ResetProgressActivity::class.java)
                startActivity(intent)
            }
        }

        linearLanguage.setOnClickListener { view ->
            applyBounceEffect(view) {
                val intent = Intent(this, LanguageChangeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)

        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleDownX, scaleDownY)
        animatorSet.playTogether(scaleUpX, scaleUpY)
        animatorSet.playSequentially(scaleDownX, scaleUpX)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }
}
