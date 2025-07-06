package com.example.sumamente.ui

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.example.sumamente.R
import java.util.Locale
import android.os.Handler
import android.os.Looper

class LanguageSelectionActivity : BaseActivity()  {

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedLanguageCode: String = "es"
    private val languageButtons = mutableListOf<LinearLayout>()
    private val checkMarks = mutableListOf<ImageView>()

    private val supportedLanguages = listOf(
        LanguageItem("es", R.string.language_spanish, R.drawable.es),
        LanguageItem("en", R.string.language_english, R.drawable.us),
        LanguageItem("pt", R.string.language_portuguese, R.drawable.br),
        LanguageItem("hi", R.string.language_hindi, R.drawable.`in`),
        LanguageItem("fr", R.string.language_french, R.drawable.fr),
        LanguageItem("de", R.string.language_german, R.drawable.de),
        LanguageItem("id", R.string.language_indonesian, R.drawable.id),
        LanguageItem("ja", R.string.language_japanese, R.drawable.jp),
        LanguageItem("ko", R.string.language_korean, R.drawable.kr)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("MyPrefers", MODE_PRIVATE)

        detectAndSetInitialLanguage()

        setContentView(R.layout.activity_language_selection)

        setupLanguageButtons()
        highlightSelectedLanguage()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun detectAndSetInitialLanguage() {

        val deviceLocale =
            resources.configuration.locales[0]

        val deviceLanguageCode = deviceLocale.language.lowercase()

        val isSupported = supportedLanguages.any { it.code == deviceLanguageCode }

        if (isSupported) {

            selectedLanguageCode = deviceLanguageCode

            if (deviceLanguageCode == "en" || deviceLanguageCode == "es" || deviceLanguageCode == "fr") {
                setAppLanguage(deviceLanguageCode)
            } else {
                setAppLanguage("en")
            }
        } else {

            selectedLanguageCode = "es"
            setAppLanguage("es")
        }

        sharedPreferences.edit {
            putString("selected_language", selectedLanguageCode)
                .putBoolean("language_selected", false)
        }
    }

    private fun setAppLanguage(languageCode: String) {
        val locale = Locale.Builder().setLanguage(languageCode).build()
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        createConfigurationContext(config)

        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupLanguageButtons() {
        val container = findViewById<LinearLayout>(R.id.language_buttons_container)

        supportedLanguages.forEachIndexed { index, languageItem ->
            val buttonLayout = layoutInflater.inflate(R.layout.item_language_button, container, false) as LinearLayout

            val flagImageView = buttonLayout.findViewById<ImageView>(R.id.flag_image)
            flagImageView.setImageResource(languageItem.flagRes)

            val languageTextView = buttonLayout.findViewById<TextView>(R.id.language_name)
            languageTextView.setText(languageItem.nameRes)

            val checkMark = buttonLayout.findViewById<ImageView>(R.id.check_mark)
            checkMark.visibility = View.GONE

            buttonLayout.setBackgroundResource(R.drawable.premio_boton_background)

            buttonLayout.setOnClickListener { view ->
                applyBounceEffect(view) {
                    selectLanguage(languageItem.code, index)
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

    private fun selectLanguage(languageCode: String, index: Int) {

        selectedLanguageCode = languageCode

        highlightSelectedLanguage()

        val appDisplayLanguage = if (languageCode == "es" || languageCode == "en" || languageCode == "fr") {
            languageCode
        } else {
            "en"
        }

        setAppLanguage(appDisplayLanguage)

        sharedPreferences.edit {
            putString("selected_language", languageCode)
                .putString("app_display_language", appDisplayLanguage)
                .putBoolean("language_selected", true)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            proceedToMainApp()
        }, 1000)
    }

    private fun highlightSelectedLanguage() {

        checkMarks.forEach { it.visibility = View.GONE }

        val selectedIndex = supportedLanguages.indexOfFirst { it.code == selectedLanguageCode }
        if (selectedIndex != -1) {
            checkMarks[selectedIndex].visibility = View.VISIBLE
        }
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

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val hasSelectedLanguage = sharedPreferences.getBoolean("language_selected", false)

            if (hasSelectedLanguage) {
                val intent = Intent(this@LanguageSelectionActivity, TransitionActivity::class.java)
                val options = ActivityOptions.makeCustomAnimation(this@LanguageSelectionActivity, android.R.anim.fade_in, android.R.anim.fade_out)
                startActivity(intent, options.toBundle())
                finish()
            } else {
                showLanguageRequiredDialog()
            }
        }
    }

    private fun showLanguageRequiredDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.choose_language)
            .setMessage(R.string.language_selection_required_message)
            .setPositiveButton(R.string.btn_accept) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setBackgroundResource(R.drawable.button_background)
        }

        dialog.show()
    }

    private fun proceedToMainApp() {
        val intent = Intent(this, TransitionActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }

    data class LanguageItem(
        val code: String,
        val nameRes: Int,
        val flagRes: Int
    )
}
