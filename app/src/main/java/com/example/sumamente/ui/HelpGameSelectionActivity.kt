package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.sumamente.R

class HelpGameSelectionActivity : BaseActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_game_selection)

        val btnNumerosPlus = findViewById<RelativeLayout>(R.id.btn_numeros_plus)
        val btnDeciPlus = findViewById<RelativeLayout>(R.id.btn_deci_plus)
        val btnRomas = findViewById<RelativeLayout>(R.id.btn_romas)
        val btnAlfaNumeros = findViewById<RelativeLayout>(R.id.btn_alfa_numeros)
        val btnSumaresta = findViewById<RelativeLayout>(R.id.btn_sumaresta)
        val btnMasPlus = findViewById<RelativeLayout>(R.id.btn_mas_plus)
        val btnGenioPlus = findViewById<RelativeLayout>(R.id.btn_genio_plus)
        val closeButton = findViewById<ImageView>(R.id.closeButton)
        val titleTextView = findViewById<TextView>(R.id.tv_help_title)

        titleTextView.text = getString(R.string.descubre_como_jugar)

        val tvGameNameNumerosPlus = btnNumerosPlus.findViewById<TextView>(R.id.tv_game_name_numeros_plus)
        val tvGameNameDeciPlus = btnDeciPlus.findViewById<TextView>(R.id.tv_game_name_deci_plus)
        val tvGameNameRomas = btnRomas.findViewById<TextView>(R.id.tv_game_name_romas)
        val tvGameNameAlfaNumeros = btnAlfaNumeros.findViewById<TextView>(R.id.tv_game_name_alfa_numeros)
        val tvGameNameSumaresta = btnSumaresta.findViewById<TextView>(R.id.tv_game_name_sumaresta)
        val tvGameNameMasPlus = btnMasPlus.findViewById<TextView>(R.id.tv_game_name_mas_plus)
        val tvGameNameGenioPlus = btnGenioPlus.findViewById<TextView>(R.id.tv_game_name_genio_plus)

        tvGameNameNumerosPlus.text = getString(R.string.game_numeros_plus)
        tvGameNameDeciPlus.text = getString(R.string.game_deci_plus)
        tvGameNameRomas.text = getString(R.string.game_romas)
        tvGameNameAlfaNumeros.text = getString(R.string.game_alfa_numeros)
        tvGameNameSumaresta.text = getString(R.string.game_sumaresta)
        tvGameNameMasPlus.text = getString(R.string.game_mas_plus)
        tvGameNameGenioPlus.text = getString(R.string.game_genio_plus)

        applyAlfaNumerosColor(btnAlfaNumeros)
        applySumarestaColor(btnSumaresta)

        btnNumerosPlus.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, HelpTutorialActivityNumeros::class.java))
            }
        }

        btnDeciPlus.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, HelpTutorialActivityDeciPlus::class.java))
            }
        }

        btnRomas.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, HelpTutorialActivityRomas::class.java))
            }
        }

        btnAlfaNumeros.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, HelpTutorialActivityAlfaNumeros::class.java))
            }
        }

        btnSumaresta.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, HelpTutorialActivitySumaResta::class.java))
            }
        }

        btnMasPlus.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, HelpTutorialActivityMasPlus::class.java))
            }
        }

        btnGenioPlus.setOnClickListener {
            applyBounceEffect(it) {
                startActivity(Intent(this, HelpTutorialActivityGenioPlus::class.java))
            }
        }

        closeButton.setOnClickListener {
            applyBounceEffect(it) {
                finish()
            }
        }
    }

    private fun applyAlfaNumerosColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_alfa_numeros)

        val alfaText = getString(R.string.text_alfa)
        val numerosText = getString(R.string.text_numeros)
        val alfaNumerosText = "$alfaText$numerosText"
        val spannableAlfaNumeros = SpannableString(alfaNumerosText)

        spannableAlfaNumeros.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)),
            0, alfaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableAlfaNumeros.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)),
            alfaText.length, alfaNumerosText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableAlfaNumeros
    }

    private fun applySumarestaColor(button: RelativeLayout) {
        val textView = button.findViewById<TextView>(R.id.tv_game_name_sumaresta)

        val sumaText = getString(R.string.text_suma)
        val restaText = getString(R.string.text_resta)
        val sumarestaText = "$sumaText$restaText"
        val spannableSumaresta = SpannableString(sumarestaText)

        spannableSumaresta.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)),
            0, sumaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableSumaresta.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
            sumaText.length, sumarestaText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableSumaresta
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
