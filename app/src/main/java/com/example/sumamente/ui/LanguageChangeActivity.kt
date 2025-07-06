package com.example.sumamente.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.example.sumamente.R
import java.util.Locale

class LanguageChangeActivity : BaseActivity() {

    private lateinit var languageButtonsContainer: LinearLayout
    private var selectedLanguageCode: String? = null
    private lateinit var loadingOverlay: View
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_change)

        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        languageButtonsContainer = findViewById(R.id.language_buttons_container)

        // Set content descriptions for accessibility
        btnClose.contentDescription = getString(R.string.close) // Using existing string for close
        btnBack.contentDescription = getString(R.string.back)   // Using existing string for back

        btnClose.setOnClickListener { view ->
            applyBounceEffect(view) {
                finish()
            }
        }

        btnBack.setOnClickListener { view ->
            applyBounceEffect(view) {
                finish()
            }
        }

        setupLoadingOverlay()
        setupLanguageButtons()
        highlightSelectedLanguage()
    }

    private fun setupLoadingOverlay() {

        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        loadingOverlay = LayoutInflater.from(this).inflate(R.layout.loading_overlay, rootLayout, false)
        progressBar = loadingOverlay.findViewById(R.id.progress_bar)
        loadingText = loadingOverlay.findViewById(R.id.loading_text)

        rootLayout.addView(loadingOverlay)

        loadingOverlay.visibility = View.GONE
    }

    private fun setupLanguageButtons() {
        val languages = listOf(
            Pair("es", R.string.language_spanish),
            Pair("en", R.string.language_english),
            Pair("pt", R.string.language_portuguese),
            Pair("hi", R.string.language_hindi),
            Pair("fr", R.string.language_french),
            Pair("de", R.string.language_german),
            Pair("id", R.string.language_indonesian),
            Pair("ja", R.string.language_japanese),
            Pair("ko", R.string.language_korean)
        )

        val currentAppLocaleCode = resources.configuration.locales[0].language

        for ((code, nameResId) in languages) {
            val languageButtonView = LayoutInflater.from(this)
                .inflate(R.layout.item_language_button, languageButtonsContainer, false) as LinearLayout

            val flagImageView = languageButtonView.findViewById<ImageView>(R.id.flag_image)
            val languageTextView = languageButtonView.findViewById<TextView>(R.id.language_name)
            val checkImageView = languageButtonView.findViewById<ImageView>(R.id.check_mark)

            val flagResId = when (code) {
                "es" -> R.drawable.es
                "en" -> R.drawable.us
                "pt" -> R.drawable.br
                "hi" -> R.drawable.`in`
                "fr" -> R.drawable.fr
                "de" -> R.drawable.de
                "id" -> R.drawable.id
                "ja" -> R.drawable.jp
                "ko" -> R.drawable.kr
                else -> R.drawable.us // Default flag
            }
            flagImageView.setImageResource(flagResId)
            languageTextView.setText(nameResId)

            flagImageView.contentDescription = getString(R.string.flag_image) // Using existing string for flag_image

            checkImageView.visibility = View.GONE

            if (code == currentAppLocaleCode) {
                selectedLanguageCode = code
            }

            languageButtonView.setOnClickListener { view ->
                applyBounceEffect(view) {
                    selectedLanguageCode = code
                    showLanguageChangeConfirmationDialog(code)
                }
            }
            languageButtonsContainer.addView(languageButtonView)
        }
    }

    private fun highlightSelectedLanguage() {
        // Hide all check marks first
        for (i in 0 until languageButtonsContainer.childCount) {
            val buttonView = languageButtonsContainer.getChildAt(i) as LinearLayout
            buttonView.findViewById<ImageView>(R.id.check_mark).visibility = View.GONE
        }

        val languages = listOf(
            Pair("es", R.string.language_spanish),
            Pair("en", R.string.language_english),
            Pair("pt", R.string.language_portuguese),
            Pair("hi", R.string.language_hindi),
            Pair("fr", R.string.language_french),
            Pair("de", R.string.language_german),
            Pair("id", R.string.language_indonesian),
            Pair("ja", R.string.language_japanese),
            Pair("ko", R.string.language_korean)
        )

        val currentLocaleCode = resources.configuration.locales[0].language
        val selectedIndex = languages.indexOfFirst { it.first == currentLocaleCode }

        if (selectedIndex != -1 && selectedIndex < languageButtonsContainer.childCount) {
            val selectedButtonView = languageButtonsContainer.getChildAt(selectedIndex) as LinearLayout
            selectedButtonView.findViewById<ImageView>(R.id.check_mark).visibility = View.VISIBLE
        }
    }

    private fun showLanguageChangeConfirmationDialog(languageCode: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.difficulty_exit_dialog_title)
            .setMessage(R.string.confirm_change_language_message)
            .setPositiveButton(R.string.btn_accept) { dialog, _ ->
                dialog.dismiss()
                // Start the language change sequence
                startLanguageChangeSequence(languageCode)
            }
            .setNegativeButton(R.string.difficulty_exit_dialog_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startLanguageChangeSequence(languageCode: String) {
        // Step 1: Show check mark for selected language
        showCheckMarkForSelectedLanguage(languageCode)

        // Step 2: Show loading overlay after a brief delay
        Handler(Looper.getMainLooper()).postDelayed({
            showLoadingOverlay()

            // Step 3: Change language after loading duration
            Handler(Looper.getMainLooper()).postDelayed({
                setAppLocale(languageCode)
            }, 1500) // 1.5 seconds loading

        }, 300) // Brief delay to show check mark first
    }

    private fun showCheckMarkForSelectedLanguage(languageCode: String) {
        val languages = listOf("es", "en", "pt", "hi", "fr", "de", "id", "ja", "ko")
        val selectedIndex = languages.indexOf(languageCode)

        if (selectedIndex != -1 && selectedIndex < languageButtonsContainer.childCount) {
            // Hide all check marks first
            for (i in 0 until languageButtonsContainer.childCount) {
                val buttonView = languageButtonsContainer.getChildAt(i) as LinearLayout
                buttonView.findViewById<ImageView>(R.id.check_mark).visibility = View.GONE
            }

            // Show check mark for selected language
            val selectedButtonView = languageButtonsContainer.getChildAt(selectedIndex) as LinearLayout
            val checkMark = selectedButtonView.findViewById<ImageView>(R.id.check_mark)
            checkMark.visibility = View.VISIBLE

            // Optional: Add a small scale animation to the check mark
            checkMark.scaleX = 0f
            checkMark.scaleY = 0f
            checkMark.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start()
        }
    }

    private fun showLoadingOverlay() {
        loadingText.text = getString(R.string.changing_language) // You'll need to add this string
        loadingOverlay.visibility = View.VISIBLE
        loadingOverlay.alpha = 0f

        // Fade in the overlay
        loadingOverlay.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    }

    private fun setAppLocale(languageCode: String) {
        // Determine the display language (following the same logic as LanguageSelectionActivity)
        val appDisplayLanguage = when (languageCode) {
            "es", "en", "fr" -> languageCode
            else -> "en" // Default to English for unsupported languages
        }

        // Save both preferences for consistency
        val preferences = getSharedPreferences("MyPrefers", MODE_PRIVATE)
        preferences.edit {
            putString("selected_language", languageCode)
            putString("app_display_language", appDisplayLanguage)
        }

        // Apply the new locale
        val locale = Locale.Builder().setLanguage(appDisplayLanguage).build()
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)

        // Update configuration (keeping compatibility with older versions)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
        @Suppress("DEPRECATION")
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val intent = Intent(this, LanguageChangeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val options = android.app.ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = android.animation.ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = android.animation.ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = android.animation.ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = android.animation.ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = android.animation.AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
        }

        val scaleUp = android.animation.AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
        }

        val animatorSet = android.animation.AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)

        animatorSet.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }
}
