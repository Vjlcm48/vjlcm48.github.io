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
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import com.example.sumamente.R
import androidx.core.content.ContextCompat

class SettingsActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profileSubtitleText: TextView
    private lateinit var linearDeleteAccount: LinearLayout
    private lateinit var btnCloseSettings: ImageView
    private lateinit var switchSound: SwitchCompat
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var switchAds: SwitchCompat
    private lateinit var adsText: TextView
    private lateinit var itemsContainer: LinearLayout
    private lateinit var linearProfile: LinearLayout
    private lateinit var linearShare: LinearLayout
    private lateinit var linearHelp: LinearLayout
    private lateinit var linearResetProgress: LinearLayout
    private lateinit var linearLanguage: LinearLayout
    private lateinit var appLogo: ImageView

    companion object {
        const val SOUND_ENABLED = "sound_enabled"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val ADS_ENABLED = "ads_enabled"

        const val ACCOUNT_LINKED = "isAccountLinked"
        const val LAST_PROMPT_DISMISSAL_TIMESTAMP = "lastPromptDismissalTimestamp"
        const val LINK_PROMPT_INTERACTED = "linkPromptInteracted"

        const val COOLDOWN_REMIND_LATER = 4 * 24 * 60 * 60 * 1000L // 4 días
        const val COOLDOWN_NOT_NOW = 7 * 24 * 60 * 60 * 1000L // 7 días
        const val COOLDOWN_FLOAT_DISMISS = 2 * 24 * 60 * 60 * 1000L // 2 días (descartar botón)
        const val COOLDOWN_FLOAT_DIALOG_DISMISS = 3 * 24 * 60 * 60 * 1000L // 3 días (más tarde desde el flotante)
        const val COOLDOWN_FLOAT_DIALOG_NOT_NOW = 5 * 24 * 60 * 60 * 1000L // 5 días (no por ahora desde el flotante)


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        setContentView(R.layout.activity_settings)

        bindViews()
        setupSwitchStates()
        setupSwitchListeners()
        setupOptionListeners()
        setupColorAnimationForAds()
        setupProfileSubtitle()
        setupCloseButton()

        appLogo.alpha = 0f
        btnCloseSettings.alpha = 0f
        findViewById<View>(R.id.card_settings).alpha = 0f
        itemsContainer.alpha = 0f

        startEntranceSequence()
    }

    private fun bindViews() {
        appLogo              = findViewById(R.id.app_logo)
        btnCloseSettings     = findViewById(R.id.btn_close_settings)
        switchSound          = findViewById(R.id.switch_sound)
        switchNotifications  = findViewById(R.id.switch_notifications)
        switchAds            = findViewById(R.id.switch_ads)
        adsText              = findViewById(R.id.ads_text)
        itemsContainer       = findViewById(R.id.layout_settings_items)
        profileSubtitleText  = findViewById(R.id.profile_subtitle_text)
        linearDeleteAccount  = findViewById(R.id.linear_delete_account)
        linearProfile        = findViewById(R.id.linear_profile)
        linearShare          = findViewById(R.id.linear_share)
        linearHelp           = findViewById(R.id.linear_help)
        linearResetProgress  = findViewById(R.id.linear_reset_progress)
        linearLanguage       = findViewById(R.id.linear_language)
    }

    private fun startEntranceSequence() {

        appLogo.alpha = 1f
        val animLogo = AnimationUtils.loadAnimation(this, R.anim.logo_zoom_in)
        appLogo.startAnimation(animLogo)

        btnCloseSettings.alpha = 1f
        val animX = AnimationUtils.loadAnimation(this, R.anim.logo_zoom_in)
        btnCloseSettings.startAnimation(animX)

        val card = findViewById<View>(R.id.card_settings)
        card.alpha = 0f // Inicia invisible
        card.postDelayed({
            card.animate()
                .alpha(1f)
                .setDuration(220)
                .withEndAction {
                    animateItems()
                    startLogoPulseAnimation()
                }
                .start()
        }, 100)
    }

    private fun animateItems() {
        itemsContainer.visibility = View.VISIBLE
        itemsContainer.alpha = 1f

        for (i in 0 until itemsContainer.childCount) {
            val v = itemsContainer.getChildAt(i)
            v.alpha = 0f
            v.translationY = 50f
            v.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(350)
                .setStartDelay((i * 60L))
                .start()
        }
    }

    private fun startLogoPulseAnimation() {
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_progress_button)
        appLogo.startAnimation(pulseAnimation)
    }

    private fun setupSwitchStates() {
        switchSound.isChecked         = sharedPreferences.getBoolean(SOUND_ENABLED, true)
        switchNotifications.isChecked = sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, false)
        switchAds.isChecked           = sharedPreferences.getBoolean(ADS_ENABLED, true)
    }

    private fun setupSwitchListeners() {
        switchSound.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                getString(if (isChecked) R.string.sound_enabled else R.string.sound_disabled),
                Toast.LENGTH_SHORT
            ).show()
            sharedPreferences.edit { putBoolean(SOUND_ENABLED, isChecked) }
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                getString(if (isChecked) R.string.notifications_enabled else R.string.notifications_disabled),
                Toast.LENGTH_SHORT
            ).show()
            sharedPreferences.edit { putBoolean(NOTIFICATIONS_ENABLED, isChecked) }
        }

        switchAds.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                getString(if (isChecked) R.string.ads_enabled else R.string.ads_disabled),
                Toast.LENGTH_SHORT
            ).show()
            sharedPreferences.edit { putBoolean(ADS_ENABLED, isChecked) }
        }
    }

    private fun setupOptionListeners() {
        linearShare.setOnClickListener { view ->
            applyBounceEffect(view) { shareApp() }
        }
        linearHelp.setOnClickListener { view ->
            applyBounceEffect(view) { startActivity(Intent(this, HelpGameSelectionActivity::class.java)) }
        }
        linearResetProgress.setOnClickListener { view ->
            applyBounceEffect(view) { startActivity(Intent(this, ResetProgressActivity::class.java)) }
        }
        linearLanguage.setOnClickListener { view ->
            applyBounceEffect(view) { startActivity(Intent(this, LanguageChangeActivity::class.java)) }
        }
        linearProfile.setOnClickListener { view ->
            applyBounceEffect(view) { openProfileEdit() }
        }
        linearDeleteAccount.setOnClickListener { view ->
            applyBounceEffect(view) { showConfirmDeleteDialog() }
        }
    }

    private fun setupColorAnimationForAds() {
        ValueAnimator.ofArgb(
            getColor(R.color.blue_primary),
            getColor(R.color.red_primary)
        ).apply {
            duration = 2000L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animator ->
                adsText.setTextColor(animator.animatedValue as Int)
            }
        }.start()
    }

    private fun setupProfileSubtitle() {
        val isLinked = sharedPreferences.getBoolean(ACCOUNT_LINKED, false)
        profileSubtitleText.text = getString(
            if (isLinked) R.string.profile_subtitle_linked else R.string.profile_subtitle_unlinked
        )
    }

    private fun setupCloseButton() {
        btnCloseSettings.setOnClickListener { view ->
            applyBounceEffect(view) { finish() }
        }
    }

    private fun shareApp() {
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

    private fun openProfileEdit() {
        startActivity(Intent(this, ProfileEditActivity::class.java))
    }

    private fun showConfirmDeleteDialog() {

        val dialog = AlertDialog.Builder(this)
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
            .create()

        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialog_background_with_border))

        dialog.show()
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply { playTogether(scaleDownX, scaleDownY) }
        val scaleUp   = AnimatorSet().apply { playTogether(scaleUpX, scaleUpY) }

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
