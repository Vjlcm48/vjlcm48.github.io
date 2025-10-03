package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.content.Intent
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.heptacreation.sumamente.R

class LanguageChangeActivity : BaseActivity() {

    private lateinit var languageButtonsContainer: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var arrowIcon: ImageView
    private lateinit var gradientView: View

    private val languageButtons = mutableListOf<LinearLayout>()
    private val checkMarks = mutableListOf<ImageView>()

    private val supportedLanguages by lazy { LanguageManager.getOrderedLanguages(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        setContentView(R.layout.activity_language_change)

        val btnClose = findViewById<ImageView>(R.id.btn_close)
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        findViewById<TextView>(R.id.title_choose_language)
        languageButtonsContainer = findViewById(R.id.language_buttons_container)
        scrollView = findViewById(R.id.language_scroll_view)
        arrowIcon = findViewById(R.id.arrow_icon)
        gradientView = findViewById(R.id.gradient_view)

        btnClose.contentDescription = getString(R.string.close)
        btnBack.contentDescription = getString(R.string.back)

        btnClose.setOnClickListener { view -> applyBounceEffect(view) { finish() } }
        btnBack.setOnClickListener { view -> applyBounceEffect(view) { finish() } }

        setupLanguageButtons()
        highlightSelectedLanguage()
        startAnimations()
        setupScrollListener()
    }

    private fun setupLanguageButtons() {
        languageButtonsContainer.removeAllViews()
        languageButtons.clear()
        checkMarks.clear()

        supportedLanguages.forEachIndexed { index, languageItem ->
            val languageButtonView = LayoutInflater.from(this)
                .inflate(R.layout.item_language_button, languageButtonsContainer, false) as LinearLayout

            val flagImageView = languageButtonView.findViewById<ImageView>(R.id.flag_image)
            val languageTextView = languageButtonView.findViewById<TextView>(R.id.language_name)
            val checkImageView = languageButtonView.findViewById<ImageView>(R.id.check_mark)

            flagImageView.setImageResource(languageItem.flagRes)
            flagImageView.contentDescription = getString(R.string.flag_image)
            languageTextView.setText(languageItem.nameRes)
            checkImageView.visibility = View.GONE

            languageButtonView.alpha = 0f
            languageButtonView.translationY = 150f

            languageButtonView.setOnClickListener { view ->
                applyBounceEffect(view) {
                    if (getCurrentLocaleCode() != languageItem.code) {
                        showLanguageChangeConfirmationDialog(languageItem.code)
                    }
                }
            }

            languageButtons.add(languageButtonView)
            checkMarks.add(checkImageView)
            languageButtonsContainer.addView(languageButtonView)

            if (index < supportedLanguages.size - 1) {
                val layoutParams = languageButtonView.layoutParams as LinearLayout.LayoutParams
                layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.language_button_margin)
                languageButtonView.layoutParams = layoutParams
            }
        }
    }

    private fun getCurrentLocaleCode(): String {
        return resources.configuration.locales[0].language
    }



    private fun highlightSelectedLanguage(onAnimationEnd: () -> Unit = {}) {
        checkMarks.forEach { it.visibility = View.GONE }
        val currentLocaleCode = getCurrentLocaleCode()
        val selectedIndex = supportedLanguages.indexOfFirst { it.code == currentLocaleCode }
        if (selectedIndex != -1) {
            animateCheck(checkMarks[selectedIndex], onAnimationEnd)
        } else {
            onAnimationEnd()
        }
    }

    private fun showLanguageChangeConfirmationDialog(languageCode: String) {

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.difficulty_exit_dialog_title)
            .setMessage(R.string.confirm_change_language_message)
            .setPositiveButton(R.string.btn_accept) { dialog, _ ->
                dialog.dismiss()
                setAppLocale(languageCode)
            }
            .setNegativeButton(R.string.difficulty_exit_dialog_negative) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(this, R.drawable.dialog_background_with_border)
        )

        dialog.show()
    }

    private fun setAppLocale(languageCode: String) {
        LanguageManager.saveNewLanguageOrder(this, languageCode)
        val appDisplayLanguage = languageCode
        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        preferences.edit {
            putString("selected_language", languageCode)
            putString("app_display_language", appDisplayLanguage)
            apply()
        }

        // setAppLanguage(languageCode)

        checkMarks.forEach { it.visibility = View.GONE }

        Handler(Looper.getMainLooper()).postDelayed({

            val selectedIndex = supportedLanguages.indexOfFirst { it.code == languageCode }
            if (selectedIndex != -1) {
                animateCheck(checkMarks[selectedIndex]) {

                    val intent = Intent(this, MainGameActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
                    startActivity(intent, options.toBundle())
                    finishAffinity()
                }
            } else {

                val intent = Intent(this, MainGameActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                val options = ActivityOptions.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
                startActivity(intent, options.toBundle())
                finishAffinity()
            }
        }, 350)
    }


    private fun startAnimations() {
        val logo = findViewById<ImageView>(R.id.app_logo)
        val card = findViewById<View>(R.id.card_view)
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
                    button.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(500)
                        .setStartDelay(200 + (80 * index).toLong())
                        .start()
                }
                val bounceAnimation = AnimationUtils.loadAnimation(this@LanguageChangeActivity, R.anim.bounce_arrow)
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
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })
        animatorSet.start()
    }

    private fun animateCheck(view: ImageView, onEndAction: () -> Unit = {}) {
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
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {

                    onEndAction()
                }
            })
            start()
        }
    }

}
