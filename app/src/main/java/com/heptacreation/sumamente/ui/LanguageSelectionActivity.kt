package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.utils.DataSyncManager
import com.google.android.material.card.MaterialCardView
import java.util.Locale
import com.google.android.material.button.MaterialButton

class LanguageSelectionActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val languageButtons = mutableListOf<LinearLayout>()
    private val checkMarks = mutableListOf<ImageView>()
    private val supportedLanguages by lazy { LanguageManager.getOrderedLanguages(this) }
    private lateinit var scrollView: ScrollView
    private lateinit var arrowIcon: ImageView
    private lateinit var gradientView: View
    private lateinit var webClientId: String
    private var restoreProgressDialog: AlertDialog? = null
    private var restoringLanguage: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val suggestedLanguageCode = loadAndSetInitialLanguageState()

        webClientId = getString(R.string.default_web_client_id)

        setContentView(R.layout.activity_language_selection)

        findViewById<MaterialButton>(R.id.btn_restore_progress).setOnClickListener {
            showRestoreProgressDialog()
        }

        scrollView = findViewById(R.id.language_scroll_view)
        arrowIcon = findViewById(R.id.arrow_icon)
        gradientView = findViewById(R.id.gradient_view)

        setupLanguageButtons(suggestedLanguageCode)
        startAnimations()
        setupScrollListener()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun loadAndSetInitialLanguageState(): String {
        val isFirstRun = sharedPreferences.getBoolean("is_first_run", true)
        val detectedLanguage: String

        if (isFirstRun) {
            val deviceLanguageCode = resources.configuration.locales[0].language.lowercase(Locale.getDefault())
            val languageIsSupported = LanguageManager.defaultLanguages.any { it.code.equals(deviceLanguageCode, ignoreCase = true) }
            detectedLanguage = if (languageIsSupported) deviceLanguageCode else "en"

            LanguageManager.saveNewLanguageOrder(this, detectedLanguage)

            sharedPreferences.edit {
                putString("selected_language", detectedLanguage)
                putBoolean("is_first_run", false)
            }
        } else {
            detectedLanguage = sharedPreferences.getString("selected_language", "en") ?: "en"
        }
        return detectedLanguage
    }

    private fun setupLanguageButtons(suggestedLanguageCode: String) {
        val container = findViewById<LinearLayout>(R.id.language_buttons_container)
        container.removeAllViews()
        languageButtons.clear()
        checkMarks.clear()

        supportedLanguages.forEachIndexed { index, languageItem ->
            val buttonLayout = LayoutInflater.from(this).inflate(R.layout.item_language_button, container, false) as LinearLayout

            val flagImageView = buttonLayout.findViewById<ImageView>(R.id.flag_image)
            flagImageView.setImageResource(languageItem.flagRes)
            val languageTextView = buttonLayout.findViewById<TextView>(R.id.language_name)
            languageTextView.setText(languageItem.nameRes)
            val checkMark = buttonLayout.findViewById<ImageView>(R.id.check_mark)
            checkMark.visibility = View.INVISIBLE

            buttonLayout.setBackgroundResource(R.drawable.language_button_background_selector)
            buttonLayout.isActivated = languageItem.code.equals(suggestedLanguageCode, ignoreCase = true)

            buttonLayout.setOnClickListener { view ->
                applyBounceEffect(view) {
                    selectLanguage(languageItem.code)
                }
            }

            languageButtons.add(buttonLayout)
            checkMarks.add(checkMark)
            container.addView(buttonLayout)

            if (index < supportedLanguages.size - 1) {
                val layoutParams = buttonLayout.layoutParams as LinearLayout.LayoutParams
                layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.language_button_margin)
                buttonLayout.layoutParams = layoutParams
            }
        }
    }

    private fun startAnimations() {
        val logo = findViewById<ImageView>(R.id.app_logo)
        val card = findViewById<MaterialCardView>(R.id.card_view)
        val logoZoomIn = AnimationUtils.loadAnimation(this, R.anim.logo_zoom_in)

        logo.startAnimation(logoZoomIn)
        logo.alpha = 1f

        logoZoomIn.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}

            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                card.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .setStartDelay(0)
                    .start()

                languageButtons.forEachIndexed { index, button ->
                    button.alpha = 0f
                    button.translationY = 150f
                    button.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(500)
                        .setStartDelay(200 + (80 * index).toLong())
                        .start()
                }
                val bounceAnimation = AnimationUtils.loadAnimation(this@LanguageSelectionActivity, R.anim.bounce_arrow)
                arrowIcon.startAnimation(bounceAnimation)
                arrowIcon.animate().alpha(1f).setDuration(400).setStartDelay(800).start()
                gradientView.animate().alpha(1f).setDuration(400).setStartDelay(800).start()
            }

            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })


        languageButtons.forEachIndexed { index, button ->
            button.alpha = 0f
            button.translationY = 150f
            button.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(500 + (80 * index).toLong())
                .start()
        }


        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_arrow)
        arrowIcon.startAnimation(bounceAnimation)
        arrowIcon.animate().alpha(1f).setDuration(400).setStartDelay(800).start()
        gradientView.animate().alpha(1f).setDuration(400).setStartDelay(800).start()
    }

    private fun setupScrollListener() {
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 50 && arrowIcon.isVisible) {
                arrowIcon.clearAnimation()
                arrowIcon.animate().alpha(0f).setDuration(200).withEndAction {
                    arrowIcon.visibility = View.GONE
                    gradientView.visibility = View.GONE
                }.start()
            }
        }
    }

    private fun selectLanguage(languageCode: String) {
        languageButtons.forEach { it.isActivated = false }
        checkMarks.forEach { it.visibility = View.INVISIBLE }

        val selectedIndex = supportedLanguages.indexOfFirst { it.code.equals(languageCode, ignoreCase = true) }
        if (selectedIndex != -1) {
            animateCheck(checkMarks[selectedIndex])
        }

        setAppLanguage(languageCode)
        LanguageManager.saveNewLanguageOrder(this, languageCode)
        sharedPreferences.edit {
            putString("selected_language", languageCode)
            putBoolean("language_selected", true)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            proceedToMainApp()
        }, 700)
    }

    private fun setAppLanguage(languageCode: String) {
        val locale = Locale.Builder().setLanguage(languageCode).build()
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun proceedToMainApp() {
        val intent = Intent(this, TransitionActivity::class.java)
        intent.putExtra("SOURCE", "LanguageSelection")
        val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = android.animation.ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = android.animation.ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = android.animation.ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = android.animation.ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)
        val scaleDown = android.animation.AnimatorSet().apply { playTogether(scaleDownX, scaleDownY) }
        val scaleUp = android.animation.AnimatorSet().apply { playTogether(scaleUpX, scaleUpY) }
        android.animation.AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd()
                }
            })
            start()
        }
    }

    private fun animateCheck(view: ImageView) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.alpha = 0f
        view.visibility = View.VISIBLE

        val animatorX = android.animation.ObjectAnimator.ofFloat(view, "scaleX", 1f).apply {
            duration = 600
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        val animatorY = android.animation.ObjectAnimator.ofFloat(view, "scaleY", 1f).apply {
            duration = 600
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        val animatorAlpha = android.animation.ObjectAnimator.ofFloat(view, "alpha", 1f).apply {
            duration = 600
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        android.animation.AnimatorSet().apply {
            playTogether(animatorX, animatorY, animatorAlpha)
            start()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!sharedPreferences.getBoolean("language_selected", false)) {
                showLanguageRequiredDialog()
            } else {
                finish()
            }
        }
    }

    private fun showLanguageRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)
            .setMessage(R.string.language_selection_required_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun showRestoreProgressDialog() {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_restore_progress, null, false)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        restoreProgressDialog = dialog

        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
        val btnAction = dialogView.findViewById<MaterialButton>(R.id.btnAction)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnAction.setOnClickListener {
            dialog.dismiss()

            startRestoreProgress()
        }

        dialog.show()
    }

    private fun showRestoreSuccessDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_restore_success, null, false)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        restoreProgressDialog = dialog

        val tvSuccess = dialogView.findViewById<TextView>(R.id.tvRestoreSuccess)
        tvSuccess.text = getString(R.string.restore_success_title)
        tvSuccess.alpha = 0f

        val appNameText = dialogView.findViewById<TextView>(R.id.appName)
        appNameText.text = getString(R.string.app_name)

        dialog.setOnShowListener {
            tvSuccess.animate().alpha(1f).setDuration(600).start()
        }

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            proceedToMainApp()
        }, 3000)
    }

    private fun startRestoreProgress() {

        FirebaseAuthManager.checkProgressOnly(
            this,
            webClientId
        ) { hasProgress, account ->
            if (hasProgress && account != null) {

                proceedWithRestore()
            } else {

                showRestoreFailureDialogAndReturn()
            }
        }
    }

    private fun proceedWithRestore() {

        FirebaseAuthManager.startGoogleSignIn(
            this,
            webClientId
        ) { success, message ->
            if (success) {
                DataSyncManager.syncDataFromCloud(this) { ok, error ->
                    if (ok) {
                        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        val languageCode = prefs.getString("selected_language", "en") ?: "en"
                        restoringLanguage = languageCode
                        setAppLanguage(languageCode)

                        prefs.edit {
                            putBoolean(SettingsActivity.ACCOUNT_LINKED, true)
                        }

                        showRestoreSuccessDialog()
                    } else {

                        showErrorDialog(
                            getString(R.string.restore_error_title),
                            error ?: getString(R.string.restore_error_message)
                        )
                    }
                }
            } else {

                showErrorDialog(
                    getString(R.string.restore_error_title),
                    message ?: getString(R.string.restore_error_login)
                )
            }
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun showRestoreFailureDialogAndReturn() {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_restore_failure, null, false)

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }

        }, 3500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FirebaseAuthManager.handleSignInResult(this, requestCode, data)
    }

    data class LanguageItem(
        val code: String,
        val nameRes: Int,
        val flagRes: Int
    )
}
