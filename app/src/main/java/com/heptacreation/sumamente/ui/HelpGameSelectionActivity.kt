package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.heptacreation.sumamente.R
import androidx.activity.enableEdgeToEdge

class HelpGameSelectionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_game_selection)

        setupClickListeners()
        setupGameNameColors()
        setupAnimations()
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.closeButton).setOnClickListener {
            applyBounceEffect(it) { finish() }
        }
        findViewById<ConstraintLayout>(R.id.btn_numeros_plus).setOnClickListener {
            animateHelpIcon(it.findViewById(R.id.ic_help_numeros_plus))
            applyBounceEffect(it) {
                it.postDelayed({
                    startActivity(Intent(this, HelpTutorialActivityNumeros::class.java))
                }, 700)
            }
        }
        findViewById<ConstraintLayout>(R.id.btn_deci_plus).setOnClickListener {
            animateHelpIcon(it.findViewById(R.id.ic_help_deci_plus))
            applyBounceEffect(it) {
                it.postDelayed({
                    startActivity(Intent(this, HelpTutorialActivityDeciPlus::class.java))
                }, 700)
            }
        }
        findViewById<ConstraintLayout>(R.id.btn_romas).setOnClickListener {
            animateHelpIcon(it.findViewById(R.id.ic_help_romas))
            applyBounceEffect(it) {
                it.postDelayed({
                    startActivity(Intent(this, HelpTutorialActivityRomas::class.java))
                }, 700)
            }
        }
        findViewById<ConstraintLayout>(R.id.btn_alfa_numeros).setOnClickListener {
            animateHelpIcon(it.findViewById(R.id.ic_help_alfa_numeros))
            applyBounceEffect(it) {
                it.postDelayed({
                    startActivity(Intent(this, HelpTutorialActivityAlfaNumeros::class.java))
                }, 700)
            }
        }
        findViewById<ConstraintLayout>(R.id.btn_sumaresta).setOnClickListener {
            animateHelpIcon(it.findViewById(R.id.ic_help_sumaresta))
            applyBounceEffect(it) {
                it.postDelayed({
                    startActivity(Intent(this, HelpTutorialActivitySumaResta::class.java))
                }, 700)
            }
        }
        findViewById<ConstraintLayout>(R.id.btn_mas_plus).setOnClickListener {
            animateHelpIcon(it.findViewById(R.id.ic_help_mas_plus))
            applyBounceEffect(it) {
                it.postDelayed({
                    startActivity(Intent(this, HelpTutorialActivityMasPlus::class.java))
                }, 700)
            }
        }
        findViewById<ConstraintLayout>(R.id.btn_genio_plus).setOnClickListener {
            animateHelpIcon(it.findViewById(R.id.ic_help_genio_plus))
            applyBounceEffect(it) {
                it.postDelayed({
                    startActivity(Intent(this, HelpTutorialActivityGenioPlus::class.java))
                }, 700)
            }
        }
    }

    private fun animateHelpIcon(icon: ImageView) {
        ObjectAnimator.ofFloat(icon, "rotation", 0f, 360f).apply {
            duration = 700
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun setupAnimations() {
        val tvTitle = findViewById<TextView>(R.id.tv_help_title)
        val container = findViewById<LinearLayout>(R.id.layout_help_buttons)

        tvTitle.animate()
            .alpha(1f)
            .setDuration(450)
            .setStartDelay(100)
            .start()

        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            view.translationY = 60f
            val animator = view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(450)
                .setStartDelay(200 + i * 80L)

            if (i == container.childCount - 1) {
                animator.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        startTitleShineAnimation(tvTitle)
                    }
                })
            }
            animator.start()
        }
    }

    private fun startTitleShineAnimation(textView: TextView) {

        textView.post {
            val textWidth = textView.paint.measureText(textView.text.toString())
            val baseColor = textView.currentTextColor
            val shineColor = ContextCompat.getColor(this, R.color.white)

            val shader = LinearGradient(
                -textWidth, 0f, 0f, 0f,
                intArrayOf(baseColor, shineColor, baseColor),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )

            textView.paint.shader = shader
            val matrix = Matrix()

            val animator = ValueAnimator.ofFloat(0f, 2 * textWidth)
            animator.duration = 800
            animator.startDelay = 500
            animator.addUpdateListener {
                val translate = it.animatedValue as Float
                matrix.setTranslate(translate, 0f)
                shader.setLocalMatrix(matrix)
                textView.invalidate()
            }
            animator.addListener(object: AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {

                    textView.paint.shader = null
                }
            })
            animator.start()
        }
    }


    private fun setupGameNameColors() {
        applyAlfaNumerosColor()
        applySumarestaColor()
    }

    private fun applyAlfaNumerosColor() {
        val textView = findViewById<TextView>(R.id.tv_game_name_alfa_numeros)
        val alfaText = getString(R.string.text_alfa)
        val numerosText = getString(R.string.text_numeros)
        val alfaNumerosText = "$alfaText$numerosText"
        val spannable = SpannableString(alfaNumerosText)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)), 0, alfaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)), alfaText.length, alfaNumerosText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable
    }

    private fun applySumarestaColor() {
        val textView = findViewById<TextView>(R.id.tv_game_name_sumaresta)
        val sumaText = getString(R.string.text_suma)
        val restaText = getString(R.string.text_resta)
        val sumarestaText = "$sumaText$restaText"
        val spannable = SpannableString(sumarestaText)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)), 0, sumaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), sumaText.length, sumarestaText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDown = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.9f))
                .with(ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.9f))
            duration = 100
        }
        val scaleUp = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(view, View.SCALE_X, 0.9f, 1f))
                .with(ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.9f, 1f))
            duration = 100
        }
        AnimatorSet().apply {
            playSequentially(scaleDown, scaleUp)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd()
                }
            })
            start()
        }
    }
}
