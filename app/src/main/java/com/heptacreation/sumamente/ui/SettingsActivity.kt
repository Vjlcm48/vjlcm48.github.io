package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.MessagesStateManager


class SettingsActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profileSubtitleText: TextView
    private lateinit var linearDeleteAccount: LinearLayout
    private lateinit var btnCloseSettings: ImageView
    private lateinit var switchSound: SwitchCompat
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var adsText: TextView

    private lateinit var itemsContainer: LinearLayout
    private lateinit var linearProfile: LinearLayout
    private lateinit var linearShare: LinearLayout
    private lateinit var linearHelp: LinearLayout
    private lateinit var linearResetProgress: LinearLayout
    private lateinit var linearLanguage: LinearLayout
    private lateinit var linearTheme: LinearLayout
    private lateinit var messagesRedDotSettings: View
    private lateinit var linearMessages: LinearLayout
    private lateinit var notificationReminderOverlay: FrameLayout
    private lateinit var appLogo: ImageView
    private var progressDialog: AlertDialog? = null
    private var isReauthenticatingForDelete = false
    private var hasShownNotificationReminder = false
    private var initialNotificationState = false


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
        setupNotificationReminderLogic()
    }

    // TODO PREMIUM FLAG: cambia aquí la fuente de verdad cuando añadas pasarela de pago o backend
    private val isPremium: Boolean
        get() = sharedPreferences.getBoolean("isPremium", false)

    private fun bindViews() {
        appLogo              = findViewById(R.id.app_logo)
        btnCloseSettings     = findViewById(R.id.btn_close_settings)
        switchSound          = findViewById(R.id.switch_sound)
        switchNotifications  = findViewById(R.id.switch_notifications)
        itemsContainer       = findViewById(R.id.layout_settings_items)
        profileSubtitleText  = findViewById(R.id.profile_subtitle_text)
        adsText              = findViewById(R.id.ads_text)
        linearDeleteAccount  = findViewById(R.id.linear_delete_account)
        messagesRedDotSettings = findViewById(R.id.messages_red_dot_settings)
        linearMessages = findViewById(R.id.linear_messages)
        linearProfile        = findViewById(R.id.linear_profile)
        linearShare          = findViewById(R.id.linear_share)
        linearHelp           = findViewById(R.id.linear_help)
        linearResetProgress  = findViewById(R.id.linear_reset_progress)
        linearLanguage       = findViewById(R.id.linear_language)
        linearTheme          = findViewById(R.id.linear_theme)
        notificationReminderOverlay = findViewById(R.id.notification_reminder_overlay)
    }

    private fun areSystemNotificationsEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {

            NotificationManagerCompat.from(this).areNotificationsEnabled()
        }
    }

    private fun shouldShowNotificationReminder(): Boolean {
        val userAcceptedNotifications = sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, false)
        val systemNotificationsEnabled = areSystemNotificationsEnabled()

        return userAcceptedNotifications && !systemNotificationsEnabled
    }

    private fun incrementSettingsVisitCounter() {
        val currentCount = sharedPreferences.getInt("settings_visit_count", 0)
        val newCount = currentCount + 1
        sharedPreferences.edit { putInt("settings_visit_count", newCount) }
    }

    private fun shouldShowReminderForCurrentVisit(): Boolean {
        val visitCount = sharedPreferences.getInt("settings_visit_count", 0)

        return visitCount <= 19 && visitCount % 2 == 1
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
            sharedPreferences.edit {
                putBoolean(NOTIFICATIONS_ENABLED, isChecked)

                if (!isChecked) {
                    putInt("settings_visit_count", 0)
                }

                if (isChecked && !initialNotificationState) {
                    hasShownNotificationReminder = true
                }
            }
        }
    }

    private fun setupOptionListeners() {

        linearShare.setOnClickListener { view ->
            applyBounceEffect(view) {
                if (isPremium) {
                    shareApp()
                } else {

                    startActivity(Intent(this, EmbajadorActivity::class.java))
                }
            }
        }
        val linearAds = findViewById<LinearLayout>(R.id.linear_ads)
        linearAds.setOnClickListener { view ->
            applyBounceEffect(view) {
                if (isPremium) {
                    showPremiumAdsInfoDialog()
                } else {
                    showRemoveAdsDecisionDialog()
                }
            }
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
        linearMessages.setOnClickListener { view ->
            applyBounceEffect(view) { startActivity(Intent(this, MessagesActivity::class.java)) }
        }
        linearProfile.setOnClickListener { view ->
            applyBounceEffect(view) { openProfileEdit() }
        }
        linearDeleteAccount.setOnClickListener { view ->
            applyBounceEffect(view) { showConfirmDeleteDialog() }
        }
        linearTheme.setOnClickListener { view ->
            applyBounceEffect(view) { showThemeSelectionDialog() }
        }

    }

    private fun showPremiumAdsInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_canje_exitoso_custom, null)

        val tvMensaje = dialogView.findViewById<TextView>(R.id.tv_mensaje_canje)

        tvMensaje.setText(R.string.mensaje_premium_ads)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<ImageView>(R.id.btn_cerrar_dialog)?.setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<AppCompatButton>(R.id.btn_entendido)?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRemoveAdsDecisionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_link_unlink_account, null)

        val tvTitle   = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        val btnLeft   = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
        val btnRight  = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAction)

        tvTitle.setText(R.string.remove_ads_title)
        tvMessage.setText(R.string.remove_ads_subtitle)
        btnLeft.setText(R.string.remove_ads_buy_premium)
        btnRight.setText(R.string.remove_ads_remove_free)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnLeft.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, PremiumPlansActivity::class.java))
        }

        btnRight.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, EmbajadorActivity::class.java))
        }
        dialog.show()
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

    private fun setupNotificationReminderLogic() {

        initialNotificationState = sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED, false)

        incrementSettingsVisitCounter()

        if (initialNotificationState && shouldShowNotificationReminder() && shouldShowReminderForCurrentVisit() && !hasShownNotificationReminder) {
            Handler(Looper.getMainLooper()).postDelayed({
                showNotificationReminder()
            }, 2000)
        }
    }

    private fun showNotificationReminder() {
        if (hasShownNotificationReminder) return

        hasShownNotificationReminder = true

        notificationReminderOverlay.bringToFront()
        notificationReminderOverlay.elevation = 32f
        notificationReminderOverlay.requestLayout()
        notificationReminderOverlay.visibility = View.VISIBLE

        findViewById<View>(R.id.notification_reminder_scrim)?.setOnClickListener {
            hideNotificationReminder()
        }

        findViewById<ImageView>(R.id.notification_reminder_close)?.setOnClickListener {
            hideNotificationReminder()
        }

        findViewById<View>(R.id.notification_reminder_modal_container)?.setOnClickListener { /* consume */ }
    }

    private fun hideNotificationReminder() {
        notificationReminderOverlay.visibility = View.GONE
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
                performAccountDeletion()
            }
            .setNegativeButton(getString(R.string.cancel_button), null)
            .create()

        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.dialog_background_with_border))
        dialog.show()
    }

    private fun showProgressDialog(message: String) {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
        val dialogView = layoutInflater.inflate(R.layout.dialog_progress, null, false)
        dialogView.findViewById<TextView>(R.id.tvProgressMessage).text = message

        progressDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog?.show()
    }

    private fun performAccountDeletion() {
        showProgressDialog(getString(R.string.account_delete_in_progress))

        com.heptacreation.sumamente.ui.utils.DataSyncManager.deleteAccountData(this) { success, error ->
            progressDialog?.dismiss()
            if (success) {
                Toast.makeText(this, getString(R.string.account_deleted_success), Toast.LENGTH_LONG).show()
                val intent = Intent(this, SplashScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finishAffinity()
            } else {
                if (error == getString(R.string.account_delete_error_auth_recent)) {

                    showReauthDialog()
                } else {

                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showReauthDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_account_confirm_title))
            .setMessage(getString(R.string.account_delete_error_auth_recent))
            .setPositiveButton(getString(R.string.btn_accept)) { _, _ ->
                isReauthenticatingForDelete = true
                val webClientId = getString(R.string.default_web_client_id)
                FirebaseAuthManager.startGoogleSignIn(this, webClientId) { _, _ ->

                }
            }
            .setNegativeButton(getString(R.string.cancel_button), null)
            .create().apply {
                window?.setBackgroundDrawable(ContextCompat.getDrawable(this.context, R.drawable.dialog_background_with_border))
                show()
            }
    }

    private fun showThemeSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_theme_selection, null)
        val radioLight = dialogView.findViewById<androidx.appcompat.widget.AppCompatRadioButton>(R.id.radio_theme_light)
        val radioDevice = dialogView.findViewById<androidx.appcompat.widget.AppCompatRadioButton>(R.id.radio_theme_device)
        val radioDark = dialogView.findViewById<androidx.appcompat.widget.AppCompatRadioButton>(R.id.radio_theme_dark)
        val btnClose = dialogView.findViewById<ImageView>(R.id.btn_close_dialog)

        val currentTheme = sharedPreferences.getString("selected_theme", "device")
        when (currentTheme) {
            "light" -> radioLight.isChecked = true
            "dark" -> radioDark.isChecked = true
            else -> radioDevice.isChecked = true
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnClose.setOnClickListener { dialog.dismiss() }

        radioLight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                applyTheme("light")
                dialog.dismiss()
            }
        }

        radioDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                applyTheme("dark")
                dialog.dismiss()
            }
        }

        radioDevice.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                applyTheme("device")
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun applyTheme(theme: String) {
        sharedPreferences.edit { putString("selected_theme", theme) }
        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "device" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

    }

    override fun onResume() {
        super.onResume()
        updateMessagesRedDotSettings()
    }

    private fun updateMessagesRedDotSettings() {
        val visible = MessagesStateManager.hasGlobalRedDot(this)
        messagesRedDotSettings.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isReauthenticatingForDelete) {
            isReauthenticatingForDelete = false
            FirebaseAuthManager.handleSignInResult(this, requestCode, data)

            if (resultCode == RESULT_OK) {

                Handler(Looper.getMainLooper()).postDelayed({
                    performAccountDeletion()
                }, 1000)
            }
        }
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
