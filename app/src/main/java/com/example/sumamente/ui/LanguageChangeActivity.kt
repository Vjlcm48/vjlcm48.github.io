package com.example.sumamente.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.example.sumamente.R

class LanguageChangeActivity : BaseActivity() {

    private lateinit var languageButtonsContainer: LinearLayout

    // private var selectedLanguageCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_change)

        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        languageButtonsContainer = findViewById(R.id.language_buttons_container)

        btnClose.contentDescription = getString(R.string.close)
        btnBack.contentDescription = getString(R.string.back)

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

        setupLanguageButtons()
        highlightSelectedLanguage()
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
                else -> R.drawable.us
            }
            flagImageView.setImageResource(flagResId)
            languageTextView.setText(nameResId)

            flagImageView.contentDescription = getString(R.string.flag_image)

            checkImageView.visibility = View.GONE

            languageButtonView.setOnClickListener { view ->
                applyBounceEffect(view) {
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

                setAppLocale(languageCode)
            }
            .setNegativeButton(R.string.difficulty_exit_dialog_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setAppLocale(languageCode: String) {

        val appDisplayLanguage = when (languageCode) {
            "es", "en", "fr", "pt" -> languageCode
            else -> "en"
        }

        val preferences = getSharedPreferences("MyPrefers", MODE_PRIVATE)
        preferences.edit {
            putString("selected_language", languageCode)
            putString("app_display_language", appDisplayLanguage)
            apply()
        }

        val intent = Intent(this, MainGameActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val options = android.app.ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        startActivity(intent, options.toBundle())
        finishAffinity()
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