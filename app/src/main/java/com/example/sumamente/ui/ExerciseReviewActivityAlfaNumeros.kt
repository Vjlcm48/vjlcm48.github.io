package com.example.sumamente.ui

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.sumamente.R

class ExerciseReviewActivityAlfaNumeros : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var elementsGridLayout: GridLayout
    private lateinit var correctAnswerTextView: TextView
    private lateinit var userResponsesTextView: TextView
    private lateinit var understoodButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_review_alfanumeros)

        titleTextView = findViewById(R.id.title_textview)
        elementsGridLayout = findViewById(R.id.numbers_grid)
        correctAnswerTextView = findViewById(R.id.correct_answer_textview)
        userResponsesTextView = findViewById(R.id.user_responses_textview)
        understoodButton = findViewById(R.id.understood_button)

        val elementList = intent.getStringArrayExtra("ELEMENT_LIST") ?: arrayOf()
        val correctAnswer = intent.getIntExtra("CORRECT_ANSWER", 0)
        val userResponses = intent.getIntArrayExtra("USER_RESPONSES") ?: intArrayOf()
        val excludedIndex = intent.getIntExtra("EXCLUDED_INDEX", -1)
        intent.getIntExtra("LEVEL", 1)

        setupUI(elementList, correctAnswer, userResponses, excludedIndex)
    }

    private fun setupUI(
        elementList: Array<String>, correctAnswer: Int, userResponses: IntArray,
        excludedIndex: Int
    ) {
        titleTextView.text = getString(R.string.exercise_title)

        displayElements(elementList, excludedIndex)
        correctAnswerTextView.text = getString(R.string.correct_answer_format, correctAnswer)

        val responseText = getString(R.string.user_responses_format, userResponses[0], userResponses[1])
        userResponsesTextView.text = responseText

        applyTouchAnimation(understoodButton)
        understoodButton.setOnClickListener {
            finish()
        }
    }

    private fun displayElements(elementList: Array<String>, excludedIndex: Int) {
        elementsGridLayout.removeAllViews()

        val columns = 3
        elementsGridLayout.columnCount = columns
        val rows = (elementList.size + columns - 1) / columns
        elementsGridLayout.rowCount = rows

        for (i in elementList.indices) {
            val element = elementList[i]
            val textView = TextView(this)
            textView.textSize = 24f

            val spannableString = SpannableStringBuilder(element)

            if (element.startsWith("-")) {
                spannableString.setSpan(
                    ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.red, null)),
                    0,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannableString.setSpan(
                    ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.black, null)),
                    0,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (i > 0 && element == elementList[i - 1]) {
                spannableString.setSpan(
                    ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.yellow, null)),
                    0,
                    spannableString.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            textView.text = spannableString
            textView.gravity = android.view.Gravity.CENTER

            if (i == excludedIndex) {
                textView.paintFlags = textView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            }

            val params = GridLayout.LayoutParams()
            params.width = GridLayout.LayoutParams.WRAP_CONTENT
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.setMargins(16, 16, 16, 16)
            params.columnSpec = GridLayout.spec(i % columns)
            params.rowSpec = GridLayout.spec(i / columns)

            elementsGridLayout.addView(textView, params)
        }
    }

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
