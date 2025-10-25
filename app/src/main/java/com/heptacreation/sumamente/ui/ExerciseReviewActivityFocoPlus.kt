package com.heptacreation.sumamente.ui

import android.graphics.Typeface
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.heptacreation.sumamente.R

class ExerciseReviewActivityFocoPlus : BaseActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var tableContainer: LinearLayout
    private lateinit var understoodButton: TextView
    private lateinit var scrollView: ScrollView

    private var exercisesShown: Array<String>? = null
    private var userResponses: Array<String>? = null
    private var correctAnswers: Array<String>? = null
    private var subtype: Int = 1
    private var currentLevel: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_exercise_review_foco_plus)

        titleTextView = findViewById(R.id.title_textview)
        tableContainer = findViewById(R.id.table_container)
        understoodButton = findViewById(R.id.understood_button)
        scrollView = findViewById(R.id.scroll_view)

        currentLevel = intent.getIntExtra("LEVEL", 1)
        exercisesShown = intent.getStringArrayExtra("EXERCISES_SHOWN")
        userResponses = intent.getStringArrayExtra("USER_RESPONSES")
        correctAnswers = intent.getStringArrayExtra("CORRECT_ANSWERS")
        subtype = intent.getIntExtra("SUBTYPE", 1)

        setupUI()
    }

    private fun setupUI() {
        titleTextView.text = getString(R.string.level_indicator_foco, currentLevel)

        buildTable()

        applyTouchAnimation(understoodButton)
        understoodButton.setOnClickListener {
            finish()
        }
    }

    private fun buildTable() {
        tableContainer.removeAllViews()

        addHeaderRow()

        val exercises = exercisesShown ?: return
        val responses = userResponses ?: return
        val corrects = correctAnswers ?: return

        for (i in exercises.indices) {
            if (i < responses.size && i < corrects.size) {
                addDataRow(exercises[i], corrects[i], responses[i])
            }
        }
    }

    private fun addHeaderRow() {
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(ContextCompat.getColor(this@ExerciseReviewActivityFocoPlus, R.color.blue_primary))
        }

        headerRow.addView(createHeaderCell(getString(R.string.exercise_shown), 0.35f))
        headerRow.addView(createHeaderCell(getString(R.string.correct_answer_column), 0.25f))
        headerRow.addView(createHeaderCell(getString(R.string.user_answer_column), 0.25f))
        headerRow.addView(createHeaderCell(getString(R.string.result_column), 0.15f))

        tableContainer.addView(headerRow)
    }

    private fun createHeaderCell(text: String, weight: Float): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
            this.text = text
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(this@ExerciseReviewActivityFocoPlus, R.color.white))
            setTypeface(null, Typeface.BOLD)
            textSize = 14f
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
    }

    private fun addDataRow(exercise: String, correctAnswer: String, userResponse: String) {
        val dataRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }

        dataRow.addView(createExerciseCell(exercise))
        dataRow.addView(createAnswerCell(correctAnswer))
        dataRow.addView(createUserResponseCell(userResponse))
        dataRow.addView(createResultCell(correctAnswer, userResponse))

        tableContainer.addView(dataRow)

        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
            )
            setBackgroundColor(ContextCompat.getColor(this@ExerciseReviewActivityFocoPlus, R.color.grey_light))
        }
        tableContainer.addView(divider)
    }

    private fun createExerciseCell(exercise: String): View {
        if (subtype == 14 && exercise.startsWith("FIG_")) {
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, dp(60), 0.35f)
                scaleType = ImageView.ScaleType.FIT_CENTER
                setPadding(dp(4), dp(4), dp(4), dp(4))
            }
            setBoardFigureImage(imageView, exercise)
            return imageView
        } else {
            return TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.35f)
                text = exercise
                gravity = Gravity.CENTER
                textSize = 16f
                setPadding(dp(4), dp(4), dp(4), dp(4))
                setTextColor(ContextCompat.getColor(this@ExerciseReviewActivityFocoPlus, R.color.black))
            }
        }
    }

    private fun createAnswerCell(correctAnswer: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f)

            // Si es una figura, mostrar indicador
            if (correctAnswer.startsWith("FIG_")) {
                text = "●●"
                gravity = Gravity.CENTER
            } else {
                text = correctAnswer
                gravity = Gravity.CENTER
            }

            textSize = 16f
            setPadding(dp(4), dp(4), dp(4), dp(4))
            setTextColor(ContextCompat.getColor(this@ExerciseReviewActivityFocoPlus, R.color.green_medium))
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun createUserResponseCell(userResponse: String): TextView {
        return TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f)

            if (userResponse.startsWith("FIG_")) {
                text = "●●"
                gravity = Gravity.CENTER
            } else {
                text = userResponse
                gravity = Gravity.CENTER
            }

            textSize = 16f
            setPadding(dp(4), dp(4), dp(4), dp(4))
            setTextColor(ContextCompat.getColor(this@ExerciseReviewActivityFocoPlus, R.color.black))
        }
    }

    private fun createResultCell(correctAnswer: String, userResponse: String): ImageView {
        return ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, dp(32), 0.15f).apply {
                gravity = Gravity.CENTER
            }
            val isCorrect = correctAnswer == userResponse
            setImageResource(if (isCorrect) R.drawable.ic_check_green else R.drawable.ic_x_white_on_red)
            setPadding(dp(4), dp(4), dp(4), dp(4))
        }
    }

    private fun setBoardFigureImage(target: ImageView, figureId: String) {
        val parts = figureId.split("_")
        if (parts.size < 6) {
            target.setImageDrawable(null)
            return
        }
        val base = if (parts.getOrNull(1) == "NONE") 2 else 2
        try {
            val aIcon = parts[base].toInt()
            val bIcon = parts[base + 1].toInt()
            val aColor = parts[base + 2].toInt()
            val bColor = parts[base + 3].toInt()
            val layer = makePairDrawable(aIcon, aColor, bIcon, bColor)
            target.setImageDrawable(layer)
        } catch (_: Exception) {
            target.setImageDrawable(null)
        }
    }

    private fun makePairDrawable(aIcon: Int, aColorRes: Int, bIcon: Int, bColorRes: Int): LayerDrawable? {
        val d1 = AppCompatResources.getDrawable(this, aIcon)?.mutate() ?: return null
        val d2 = AppCompatResources.getDrawable(this, bIcon)?.mutate() ?: return null

        DrawableCompat.setTint(d1, ContextCompat.getColor(this, aColorRes))
        DrawableCompat.setTint(d2, ContextCompat.getColor(this, bColorRes))

        val layer = LayerDrawable(arrayOf(d1, d2))

        val sizeDp = 50
        val gapFraction = 0.37f

        val size = dp(sizeDp)
        d1.setBounds(0, 0, size, size)
        d2.setBounds(0, 0, size, size)

        val inset = (size * gapFraction).toInt()
        layer.setLayerInset(0, 0, 0, inset, 0)
        layer.setLayerInset(1, inset, 0, 0, 0)

        return layer
    }

    private fun dp(value: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics
    ).toInt()

    private fun applyTouchAnimation(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.90f).scaleY(0.90f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    if (event.action == MotionEvent.ACTION_UP) {
                        v.performClick()
                    }
                }
            }
            true
        }
    }
}