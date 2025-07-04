package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sumamente.R

class LevelsActivityPro : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var levelContainer: LinearLayout
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView

    private val levelStrings = arrayOf(
        R.string.level_1, R.string.level_2, R.string.level_3, R.string.level_4, R.string.level_5,
        R.string.level_6, R.string.level_7, R.string.level_8, R.string.level_9, R.string.level_10,
        R.string.level_11, R.string.level_12, R.string.level_13, R.string.level_14, R.string.level_15,
        R.string.level_16, R.string.level_17, R.string.level_18, R.string.level_19, R.string.level_20,
        R.string.level_21, R.string.level_22, R.string.level_23, R.string.level_24, R.string.level_25,
        R.string.level_26, R.string.level_27, R.string.level_28, R.string.level_29, R.string.level_30,
        R.string.level_31, R.string.level_32, R.string.level_33, R.string.level_34, R.string.level_35,
        R.string.level_36, R.string.level_37, R.string.level_38, R.string.level_39, R.string.level_40,
        R.string.level_41, R.string.level_42, R.string.level_43, R.string.level_44, R.string.level_45,
        R.string.level_46, R.string.level_47, R.string.level_48, R.string.level_49, R.string.level_50,
        R.string.level_51, R.string.level_52, R.string.level_53, R.string.level_54, R.string.level_55,
        R.string.level_56, R.string.level_57, R.string.level_58, R.string.level_59, R.string.level_60,
        R.string.level_61, R.string.level_62, R.string.level_63, R.string.level_64, R.string.level_65,
        R.string.level_66, R.string.level_67, R.string.level_68, R.string.level_69, R.string.level_70
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        setContentView(R.layout.activity_levels_pro)

        ScoreManager.initPro(this)

        mediaPlayer = MediaPlayer.create(this, R.raw.clicbotones)


        tvGameName = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore = findViewById(R.id.tv_score)

        val btnClose = findViewById<ImageView>(R.id.btn_close)
        levelContainer = findViewById(R.id.level_container)


        setupInfoBar()

        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, GameSelectionActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        createLevelButtons()
    }

    override fun onResume() {
        super.onResume()
        ScoreManager.initPro(this)
        updateLevelButtons()
        setupInfoBar()
    }

    private fun setupInfoBar() {
        tvGameName.text = getString(R.string.game_numeros_plus)
        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.blue_pressed))

        val difficultyKey = "difficulty_numerosplus"

        val difficultyValue = sharedPreferences.getString(
            difficultyKey,
            DifficultySelectionActivity.DIFFICULTY_PRO
        )

        val difficultyText = when(difficultyValue) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> getString(R.string.difficulty_principiante)
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> getString(R.string.difficulty_avanzado)
            DifficultySelectionActivity.DIFFICULTY_PRO -> getString(R.string.difficulty_pro)
            else -> getString(R.string.difficulty_pro)
        }

        tvDifficulty.text = difficultyText
        tvScore.text = getString(R.string.score_format, ScoreManager.currentScorePro)

        tvDifficulty.setOnClickListener {
            applyBounceEffect(it) {

                val intent = DifficultySelectionActivity.createIntent(
                    this,
                    "NumerosPlus"
                )

                intent.putExtra("FROM_LEVELS", true)

                startActivity(intent)
            }
        }
    }

    private fun createLevelButtons() {
        levelContainer.removeAllViews()
        for (i in levelStrings.indices) {
            val levelLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 8.dpToPx(this@LevelsActivityPro)
                }
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val button = Button(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    48.dpToPx(this@LevelsActivityPro),
                    1f
                ).apply {
                    setMargins(0, 0, 8.dpToPx(this@LevelsActivityPro), 0)
                }
                text = getString(levelStrings[i])
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(ContextCompat.getColor(this@LevelsActivityPro, android.R.color.black))
                gravity = Gravity.CENTER
                setPadding(
                    16.dpToPx(this@LevelsActivityPro),
                    8.dpToPx(this@LevelsActivityPro),
                    16.dpToPx(this@LevelsActivityPro),
                    8.dpToPx(this@LevelsActivityPro)
                )

                if (i < ScoreManager.unlockedLevelsPro && !ScoreManager.isLevelBlockedByFailuresPro(i + 1)) {
                    setBackgroundResource(R.drawable.level_button_background)

                    setOnClickListener {
                        applyBounceEffect(this) {
                            mediaPlayer.start()
                            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                            val storedModeName = prefs.getString("selectedResponseModePro", null)
                            val storedMode = if (storedModeName != null) ResponseMode.valueOf(storedModeName) else null

                            if (storedMode == null) {
                                showResponseModeDialog(i + 1)
                            } else {
                                showInstructions(i + 1, storedMode)
                            }
                        }
                    }
                } else {
                    setBackgroundResource(R.drawable.button_background_locked)
                    setOnClickListener {

                        if (i < ScoreManager.unlockedLevelsPro && ScoreManager.isLevelBlockedByFailuresPro(i + 1)) {
                            Toast.makeText(
                                this@LevelsActivityPro,
                                R.string.level_locked_by_failures,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@LevelsActivityPro,
                                R.string.level_locked_message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        applyBounceEffect(this) {

                        }
                    }
                }
            }

            val lockIcon = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    36.dpToPx(this@LevelsActivityPro),
                    36.dpToPx(this@LevelsActivityPro)
                ).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }
                setImageResource(if (i < ScoreManager.unlockedLevelsPro && !ScoreManager.isLevelBlockedByFailuresPro(i + 1))
                    R.drawable.ic_unlock else R.drawable.ic_lock)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }

            levelLayout.addView(button)
            levelLayout.addView(lockIcon)

            levelContainer.addView(levelLayout)
            Log.d("LevelsActivityPro", "Added button for level ${i + 1}")
        }
    }

    private fun updateLevelButtons() {
        createLevelButtons()
    }

    private fun showInstructions(level: Int, mode: ResponseMode? = null) {
        val intent = Intent(this, InstructionsActivityPro::class.java)
        intent.putExtra("LEVEL", level)
        if (mode != null) {
            intent.putExtra("RESPONSE_MODE", mode.name)
        }
        startActivity(intent)
    }

    private fun showResponseModeDialog(level: Int) {
        val dialog = ResponseModeDialogPro(this)
        dialog.setOnResponseModeSelectedListener(object : ResponseModeDialogPro.OnResponseModeSelectedListener {
            override fun onResponseModeSelected(mode: ResponseMode) {
                showInstructions(level, mode)
            }
        })
        dialog.show()
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
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
