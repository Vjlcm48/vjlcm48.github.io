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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import com.example.sumamente.R

class SettingsActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profileSubtitleText: TextView
    private lateinit var linearDeleteAccount: LinearLayout

    companion object {
        const val SOUND_ENABLED = "sound_enabled"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val ADS_ENABLED = "ads_enabled"

        const val ACCOUNT_LINKED = "isAccountLinked"
        const val LAST_PROMPT_DISMISSAL_TIMESTAMP = "lastPromptDismissalTimestamp"
        const val LINK_PROMPT_INTERACTED = "linkPromptInteracted"

        const val COOLDOWN_REMIND_LATER = 60 * 1000L
        const val COOLDOWN_NOT_NOW = 120 * 1000L
        const val COOLDOWN_FLOAT_DISMISS = 30 * 1000L
        const val COOLDOWN_FLOAT_DIALOG_DISMISS = 90 * 1000L
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

        profileSubtitleText = findViewById(R.id.profile_subtitle_text)
        linearDeleteAccount = findViewById(R.id.linear_delete_account)

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

        switchNotifications.isChecked = sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, false)
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

        linearShare.setOnClickListener { view ->
            applyBounceEffect(view) {

                val messageTemplate = getString(R.string.share_message)
                val chooserTitle = getString(R.string.share_chooser_title)
                val appPackageName = packageName
                val playStoreLink = "https://play.google.com/store/apps/details?id=$appPackageName"
                val shareMessage = "$messageTemplate$playStoreLink"

                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, chooserTitle)
                startActivity(shareIntent)
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

        updateProfileOption()

        linearProfile.setOnClickListener { view ->
            applyBounceEffect(view) {
                handleProfileClick()
            }
        }

        linearDeleteAccount.setOnClickListener { view ->
            applyBounceEffect(view) {
                showConfirmDeleteDialog()
            }
        }
    }

    private fun updateProfileOption() {
        val isLinked = sharedPreferences.getBoolean(ACCOUNT_LINKED, false)
        if (isLinked) {
            profileSubtitleText.text = getString(R.string.profile_subtitle_linked)
        } else {
            profileSubtitleText.text = getString(R.string.profile_subtitle_unlinked)
        }
    }

    private fun handleProfileClick() {

        startActivity(Intent(this, ProfileEditActivity::class.java))
    }

    private fun showConfirmDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_account_confirm_title))
            .setMessage(getString(R.string.delete_account_confirm_message))
            .setPositiveButton(getString(R.string.delete_button)) { _, _ ->

                ScoreManager.resetAllProgress(this)
                sharedPreferences.edit { clear() }

                Toast.makeText(this, getString(R.string.account_deleted_success), Toast.LENGTH_LONG).show()

                val intent = Intent(this, SplashScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()

            }
            .setNegativeButton(getString(R.string.cancel_button), null)
            .show()
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

        val animatorSet = AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd()
                }
            })
        }
        animatorSet.start()
    }
}
