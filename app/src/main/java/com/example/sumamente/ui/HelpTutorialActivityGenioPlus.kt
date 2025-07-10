package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.sumamente.R
import java.util.Locale

class HelpTutorialActivityGenioPlus : BaseActivity()   {

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var tvWelcomeText: TextView
    private lateinit var tvGameName: TextView
    private lateinit var instructionTextView: TextView
    private lateinit var modeDialogContainer: LinearLayout
    private lateinit var lottieHandChrono: LottieAnimationView
    private lateinit var lottieHandAnswer: LottieAnimationView
    private lateinit var circleView: View
    private lateinit var vamosTextView: TextView
    private lateinit var numbersContainer: LinearLayout
    private lateinit var chronometerTextView: TextView

    private lateinit var confettiAnimation: LottieAnimationView
    private lateinit var finalMessageTextView: TextView
    private lateinit var finalTimeTextView: TextView
    private lateinit var finalPointsTextView: TextView
    private lateinit var starImageView: ImageView
    private lateinit var btnPlay: Button
    private lateinit var btnRepeat: Button
    private lateinit var btnClose: ImageView
    private lateinit var checkEscribeRespuesta: ImageView
    private lateinit var progressRing: ProgressRingView
    private lateinit var tvNumber: TextView
    private lateinit var tvStepIntroPartA: TextView
    private lateinit var tvStepIntroPartB: TextView
    private lateinit var tvStepOne: TextView
    private lateinit var tvStepTwo: TextView
    private lateinit var tvStepThreePartOne: TextView
    private lateinit var tvStepThreePartTwo: TextView
    private lateinit var tvResultMessage: TextView
    private lateinit var tvClosingMessage: TextView
    private lateinit var etUserAnswer: EditText

    private val fixedNumbers = listOf("12/6", "6", "√9", "-4/2", "3", "3", "-8")
    private val handler = Handler(Looper.getMainLooper())
    private var backgroundMusicPlayer: MediaPlayer? = null
    private var soundEffectPlayer: MediaPlayer? = null
    private var currentNumberIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_tutorial_genio_plus)
        initViews()
        startSequence()
        startBackgroundMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        rootLayout = findViewById(R.id.root_instructions_numeros)
        tvWelcomeText = findViewById(R.id.tv_welcome_text)
        tvGameName = findViewById(R.id.tv_game_name)

        tvWelcomeText.text = getString(R.string.instructions_numeros_intro)
        tvGameName.text = getString(R.string.game_genio_plus)

        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.blue_primary_darker))

        instructionTextView = findViewById(R.id.tv_instruction)
        modeDialogContainer = findViewById(R.id.layout_dialog_modes)
        lottieHandChrono = findViewById(R.id.lottie_hand_chrono)
        lottieHandAnswer = findViewById(R.id.lottie_hand_answer)

        circleView = findViewById(R.id.circle_view)
        vamosTextView = findViewById(R.id.tv_vamos_instructions)
        numbersContainer = findViewById(R.id.layout_numbers_container)
        chronometerTextView = findViewById(R.id.tv_chronometer_demo)

        confettiAnimation = findViewById(R.id.lottie_confetti)
        finalMessageTextView = findViewById(R.id.tv_final_message)
        finalTimeTextView = findViewById(R.id.tv_final_time)
        finalPointsTextView = findViewById(R.id.tv_final_points)
        starImageView = findViewById(R.id.iv_star)
        btnPlay = findViewById(R.id.btn_understood)
        btnRepeat = findViewById(R.id.btn_repeat)
        btnClose = findViewById(R.id.btn_close_instructions)
        checkEscribeRespuesta = findViewById(R.id.check_escribe_respuesta)
        progressRing = findViewById(R.id.progress_ring)
        tvNumber = findViewById(R.id.tv_number)

        tvStepIntroPartA = findViewById(R.id.tv_step_intro_part_a)
        tvStepIntroPartB = findViewById(R.id.tv_step_intro_part_b)
        tvStepOne = findViewById(R.id.tv_step_one)
        tvStepTwo = findViewById(R.id.tv_step_two)
        tvStepThreePartOne = findViewById(R.id.tv_step_three_part_one)
        tvStepThreePartTwo = findViewById(R.id.tv_step_three_part_two)
        tvResultMessage = findViewById(R.id.tv_result_message)
        tvClosingMessage = findViewById(R.id.tv_closing_message)
        etUserAnswer = findViewById(R.id.et_user_answer)
    }

    private fun positionCheckRelativeTo(
        targetView: View,
        checkView: ImageView,
        offsetX: Float = 0f,
        offsetY: Float = 0f
    ) {
        val targetLocation = IntArray(2)
        targetView.getLocationOnScreen(targetLocation)

        val rootLocation = IntArray(2)
        val root = findViewById<View>(R.id.root_instructions_numeros)
        root.getLocationOnScreen(rootLocation)

        val relativeX = (targetLocation[0] - rootLocation[0]).toFloat()
        val relativeY = (targetLocation[1] - rootLocation[1]).toFloat()

        checkView.x = relativeX + offsetX
        checkView.y = relativeY + offsetY

        checkView.visibility = View.VISIBLE
    }

    private fun startSequence() {
        showWelcomeAndIntroMessages()
    }

    private fun showWelcomeAndIntroMessages() {
        tvWelcomeText.visibility = View.VISIBLE
        val bounceInWelcome = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvWelcomeText.startAnimation(bounceInWelcome)

        handler.postDelayed({
            tvGameName.visibility = View.VISIBLE
            val bounceInGameName = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
            tvGameName.startAnimation(bounceInGameName)
        }, 2500)

        handler.postDelayed({
            tvStepIntroPartA.visibility = View.VISIBLE
            tvStepIntroPartB.visibility = View.VISIBLE

            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.activity_fade_in)
            tvStepIntroPartA.startAnimation(fadeIn)
            tvStepIntroPartB.startAnimation(fadeIn)
        }, 4500)

        handler.postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvStepIntroPartA.startAnimation(fadeOut)
            tvStepIntroPartB.startAnimation(fadeOut)

            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvWelcomeText.visibility = View.GONE
                    tvGameName.visibility = View.GONE
                    tvStepIntroPartA.visibility = View.GONE
                    tvStepIntroPartB.visibility = View.GONE
                    showStepOne()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 7500)
    }

    private fun showStepOne() {
        tvStepOne.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvStepOne.startAnimation(bounceIn)

        handler.postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvStepOne.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvStepOne.visibility = View.GONE
                    showInstructionToChooseMode()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 3000)
    }

    private fun showInstructionToChooseMode() {
        instructionTextView.visibility = View.VISIBLE
        instructionTextView.text = getString(R.string.escoge_tu_modo_de_respuesta)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.activity_fade_in)
        instructionTextView.startAnimation(fadeIn)

        handler.postDelayed({
            showModesDialog()
        }, 1000)
    }

    private fun showModesDialog() {
        modeDialogContainer.visibility = View.VISIBLE
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        modeDialogContainer.startAnimation(slideUp)

        ResponseModeDialogGenioPlus(this).apply {
            setOnResponseModeSelectedListener(object :
                ResponseModeDialogGenioPlus.OnResponseModeSelectedListenerGenioPlus {
                override fun onResponseModeSelected(mode: ResponseModeGenioPlus) {
                    hideModesDialogGenioPlus()
                }
            })
        }

        simulateHandInfoIcons()
    }

    private fun hideModesDialogGenioPlus() {
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
        modeDialogContainer.startAnimation(fadeOut)
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                modeDialogContainer.visibility = View.GONE
                instructionTextView.visibility = View.GONE
                proceedToStepTwo()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun simulateHandInfoIcons() {
        val infoSimple = findViewById<ImageButton>(R.id.info_seleccion_simple)
        val infoType = findViewById<ImageButton>(R.id.info_escribe_respuesta)
        val btnEscribeRespuesta = findViewById<Button>(R.id.btn_escribe_respuesta)

        infoSimple.visibility = View.VISIBLE
        infoType.visibility = View.VISIBLE

        showHandWithFade(
            targetView = infoType,
            appearDelay = 2000,
            clickDelay = 2800,
            hideDelay = 4000,
            onComplete = {
                showTooltip(infoType, R.string.write_answer_title, R.string.write_answer_message)
            }
        )

        showHandWithFade(
            targetView = infoSimple,
            appearDelay = 5800,
            clickDelay = 6800,
            hideDelay = 8000,
            onComplete = {
                showTooltip(infoSimple, R.string.selection_simple_title, R.string.selection_simple_message)
            }
        )

        showHandWithFade(
            targetView = btnEscribeRespuesta,
            appearDelay = 9800,
            clickDelay = 10800,
            hideDelay = 12000,
            onComplete = {
                val dp = resources.displayMetrics.density
                infoType.post {
                    positionCheckRelativeTo(
                        targetView = infoType,
                        checkView = checkEscribeRespuesta,
                        offsetX = 8f * dp,
                        offsetY = 40f * dp
                    )
                }
                animateCheck(checkEscribeRespuesta)
            }
        )

        handler.postDelayed({
            modeDialogContainer.visibility = View.GONE
            checkEscribeRespuesta.visibility = View.GONE
            instructionTextView.visibility = View.GONE
            proceedToStepTwo()
        }, 13700)
    }

    private fun proceedToStepTwo() {
        tvStepTwo.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvStepTwo.startAnimation(bounceIn)

        handler.postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvStepTwo.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvStepTwo.visibility = View.GONE
                    proceedToCircleAnimation()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 4000)
    }

    private fun proceedToCircleAnimation() {
        circleView.visibility = View.VISIBLE
        circleView.scaleX = 0f
        circleView.scaleY = 0f

        val circleAnimator = ObjectAnimator.ofPropertyValuesHolder(
            circleView,
            PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
        ).setDuration(500)

        progressRing.scaleX = 0f
        progressRing.scaleY = 0f
        progressRing.visibility = View.VISIBLE

        val ringAnimator = ObjectAnimator.ofPropertyValuesHolder(
            progressRing,
            PropertyValuesHolder.ofFloat("scaleX", 0f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0f, 1f)
        ).setDuration(500)

        vamosTextView.visibility = View.VISIBLE
        vamosTextView.alpha = 0f
        val vamosAppear = ObjectAnimator.ofFloat(vamosTextView, "alpha", 0f, 1f).apply {
            duration = 1000
        }

        val circleAndRingSet = AnimatorSet().apply {
            playTogether(circleAnimator, ringAnimator)
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(circleAndRingSet, vamosAppear)
        animatorSet.start()

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                handler.postDelayed({
                    vamosTextView.visibility = View.GONE
                    showNumbersSequence()
                }, 1000)
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun showNumbersSequence() {
        numbersContainer.visibility = View.VISIBLE
        currentNumberIndex = 0
        startRingProgress()
        displayNextNumber()
    }

    private fun startRingProgress() {
        val totalDuration = 12600L
        progressRing.startProgressAnimation(totalDuration)
    }

    private fun displayNextNumber() {
        if (currentNumberIndex >= fixedNumbers.size) {
            hideCircle()
            return
        }
        val number = fixedNumbers[currentNumberIndex]
        currentNumberIndex++

        tvNumber.apply {
            textSize = 127.5f
            text = number
            visibility = View.VISIBLE

            when {
                number.startsWith("-") -> {
                    tvNumber.setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                number == "3" && currentNumberIndex == 6 -> {
                    tvNumber.setTextColor(ContextCompat.getColor(context, R.color.yellow_dark))
                }
                else -> {
                    tvNumber.setTextColor(ContextCompat.getColor(context, R.color.black))
                }
            }

            val bounce = AnimationUtils.loadAnimation(this@HelpTutorialActivityGenioPlus, R.anim.bounce_in)
            startAnimation(bounce)
        }

        handler.postDelayed({
            displayNextNumber()
        }, 1800)
    }

    private fun hideCircle() {
        val circleScaleDown = ObjectAnimator.ofPropertyValuesHolder(
            circleView,
            PropertyValuesHolder.ofFloat("scaleX", 1f, 0f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 0f)
        ).setDuration(500)

        circleScaleDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                circleView.visibility = View.GONE
                numbersContainer.visibility = View.GONE
                showStepThree()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        circleScaleDown.start()
    }

    private fun showStepThree() {
        tvStepThreePartOne.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvStepThreePartOne.startAnimation(bounceIn)

        handler.postDelayed({
            tvStepThreePartTwo.visibility = View.VISIBLE
            val bounceInPartTwo = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
            tvStepThreePartTwo.startAnimation(bounceInPartTwo)
        }, 2500)

        handler.postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvStepThreePartOne.startAnimation(fadeOut)
            tvStepThreePartTwo.startAnimation(fadeOut)

            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvStepThreePartOne.visibility = View.GONE
                    tvStepThreePartTwo.visibility = View.GONE
                    showChronometerAndInput()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 5000)
    }

    private fun showChronometerAndInput() {
        chronometerTextView.visibility = View.VISIBLE
        chronometerTextView.text = getString(R.string.default_chronometer)

        val startTime = System.currentTimeMillis()
        val answerInputLayout = findViewById<LinearLayout>(R.id.answer_input_layout)
        val etUserAnswer = findViewById<EditText>(R.id.et_user_answer)
        val btnSendAnswer = findViewById<Button>(R.id.btn_send_answer)
        val answerValue = "7"

        answerInputLayout.visibility = View.VISIBLE
        etUserAnswer.visibility = View.VISIBLE
        btnSendAnswer.visibility = View.VISIBLE

        etUserAnswer.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etUserAnswer, InputMethodManager.SHOW_IMPLICIT)

        lottieHandChrono.visibility = View.VISIBLE
        lottieHandChrono.setAnimation("handchrono_animation.json")
        lottieHandChrono.playAnimation()

        handler.postDelayed({
            val fadeOutChronoHand = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            lottieHandChrono.startAnimation(fadeOutChronoHand)

            fadeOutChronoHand.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    lottieHandChrono.visibility = View.GONE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 2500)

        val tick = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                val seconds = elapsed / 1000.0
                if (seconds < 4.37) {
                    val formatted = String.format(Locale.getDefault(), "%.2f", seconds)
                    chronometerTextView.text = formatted

                    when {
                        seconds < 3.0 -> {
                            chronometerTextView.setTextColor(
                                ContextCompat.getColor(this@HelpTutorialActivityGenioPlus, R.color.green_medium)
                            )
                        }
                        seconds < 4.36 -> {
                            chronometerTextView.setTextColor(
                                ContextCompat.getColor(this@HelpTutorialActivityGenioPlus, R.color.orange_dark)
                            )
                        }
                        else -> {
                            chronometerTextView.setTextColor(
                                ContextCompat.getColor(this@HelpTutorialActivityGenioPlus, R.color.red)
                            )
                        }
                    }
                    handler.postDelayed(this, 100)
                } else {
                    chronometerTextView.text = getString(R.string.chronometer_value, "4.37")
                }
            }
        }
        handler.post(tick)

        handler.postDelayed({
            applyBounceEffect(etUserAnswer){
                simulateKeyboardPress(answerValue, etUserAnswer)
            }
        }, 1500)

        handler.postDelayed({
            showHandPressSend(btnSendAnswer) {
                playClickSound()
            }
        }, 2070)   // 3.37 seg

        handler.postDelayed({
            chronometerTextView.visibility = View.GONE
            answerInputLayout.visibility = View.GONE
            etUserAnswer.background = ContextCompat.getDrawable(this, R.drawable.sombra_correcta)

            showTutorialResultMessage()
        }, 5000)
    }

    private fun hideSoftKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = currentFocus
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
        }
    }

    @Suppress("SameParameterValue")
    private fun simulateKeyboardPress(number: String, etUserAnswer: EditText) {
        applyBounceEffect(etUserAnswer) {
            val currentText = etUserAnswer.text.toString()
            val newText = String.format("%s%s", currentText, number)
            etUserAnswer.setText(newText)
        }
    }

    private fun showHandPressSend(targetView: View, onComplete: () -> Unit) {
        showHandWithFade(
            targetView = targetView,
            appearDelay = 0L,
            clickDelay = 600L,
            hideDelay = 2500L,
            onComplete = {
                hideSoftKeyboard()
                etUserAnswer.clearFocus()
                onComplete()
            }
        )
    }

    private fun showTutorialResultMessage() {
        hideSoftKeyboard()
        tvResultMessage.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvResultMessage.startAnimation(bounceIn)

        handler.postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvResultMessage.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvResultMessage.visibility = View.GONE
                    showCelebration()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 3000)
    }

    private fun showCelebration() {
        confettiAnimation.visibility = View.VISIBLE
        confettiAnimation.setAnimation("confetti_animation.json")
        confettiAnimation.repeatCount = 0
        confettiAnimation.playAnimation()

        playCelebrationSound()

        finalMessageTextView.visibility = View.VISIBLE
        finalMessageTextView.text = getString(R.string.perfecto)

        handler.postDelayed({
            finalTimeTextView.visibility = View.VISIBLE
            finalTimeTextView.text = getString(R.string.formatted_time, "4.37")
        }, 1000)

        handler.postDelayed({
            finalPointsTextView.visibility = View.VISIBLE
            finalPointsTextView.text = getString(R.string.formatted_points, 700)
            starImageView.visibility = View.VISIBLE
        }, 2000)

        handler.postDelayed({
            confettiAnimation.visibility = View.GONE
            finalMessageTextView.visibility = View.GONE
            finalTimeTextView.visibility = View.GONE
            finalPointsTextView.visibility = View.GONE
            starImageView.visibility = View.GONE

            showFinalClosingMessage()
        }, 4000)
    }

    private fun showFinalClosingMessage() {
        tvClosingMessage.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvClosingMessage.startAnimation(bounceIn)

        handler.postDelayed({
            btnPlay.visibility = View.VISIBLE
            btnRepeat.visibility = View.VISIBLE
            btnClose.visibility = View.VISIBLE

            btnPlay.setOnClickListener {
                applyBounceEffect(it) {
                    navigateToGameSelection()
                }
            }

            btnRepeat.setOnClickListener {
                applyBounceEffect(it) {
                    repeatTutorial()
                }
            }

            btnClose.setOnClickListener {
                applyBounceEffect(it) {
                    navigateToGameSelection()
                }
            }
        }, 2000)

        handler.postDelayed({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvClosingMessage.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvClosingMessage.visibility = View.GONE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 4000)
    }

    private fun navigateToGameSelection() {
        stopBackgroundMusic()
        val intent = Intent(this, GameSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun repeatTutorial() {
        stopBackgroundMusic()
        val intent = Intent(this, HelpTutorialActivityGenioPlus::class.java)
        startActivity(intent)
        finish()
    }

    private fun showHandWithFade(
        targetView: View,
        appearDelay: Long,
        clickDelay: Long,
        hideDelay: Long,
        onComplete: (() -> Unit)? = null
    ) {
        handler.postDelayed({
            lottieHandAnswer.alpha = 0f
            lottieHandAnswer.visibility = View.VISIBLE

            showHandOnViewFor(targetView = targetView)
            lottieHandAnswer.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }, appearDelay)

        handler.postDelayed({
            playClickSound()
        }, clickDelay)

        handler.postDelayed({
            lottieHandAnswer.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    hideHand()
                    onComplete?.invoke()
                }
                .start()
        }, hideDelay)
    }

    private fun showHandOnViewFor(

        targetView: View,
        onAnimationComplete: (() -> Unit)? = null,
        adjustX: Float = 27f,
        adjustY: Float = -1f
    ) {
        val coords = IntArray(2)
        targetView.getLocationOnScreen(coords)

        targetView.post {
            val xCenter = coords[0] + targetView.width / 2f
            val yCenter = coords[1] + targetView.height / 2f

            lottieHandAnswer.x = xCenter - (lottieHandAnswer.width / 2f) + adjustX
            lottieHandAnswer.y = yCenter - (lottieHandAnswer.height / 2f) + adjustY
            lottieHandAnswer.visibility = View.VISIBLE
            lottieHandAnswer.bringToFront()
            lottieHandAnswer.setAnimation("handanswer_animation.json")
            lottieHandAnswer.playAnimation()

            handler.postDelayed({
                lottieHandAnswer.visibility = View.VISIBLE
                onAnimationComplete?.invoke()
            }, 2000)
        }
    }

    private fun hideHand() {
        lottieHandAnswer.visibility = View.GONE
    }

    private fun showTooltip(anchorView: View, titleResId: Int, messageResId: Int) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.dialog_tooltip, rootLayout, false)
        val titleTextView = popupView.findViewById<TextView>(R.id.dialog_title)
        val messageTextView = popupView.findViewById<TextView>(R.id.dialog_message)
        val closeButton = popupView.findViewById<ImageView>(R.id.close_button)

        titleTextView.text = getString(titleResId)
        messageTextView.text = getString(messageResId)

        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAsDropDown(anchorView, 0, -anchorView.height)

        handler.postDelayed({ popupWindow.dismiss() }, 2200)

        closeButton.setOnClickListener { popupWindow.dismiss() }
    }

    private fun animateCheck(view: ImageView) {
        view.alpha = 0f
        view.scaleX = 0f
        view.scaleY = 0f
        view.visibility = View.VISIBLE

        val animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        val animatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f).apply {
            duration = 600
            interpolator = AccelerateDecelerateInterpolator()
        }

        AnimatorSet().apply {
            playTogether(animatorX, animatorY, animatorAlpha)
            start()
        }
    }

    private fun playClickSound() {
        soundEffectPlayer = MediaPlayer.create(this, R.raw.clicbotones)
        soundEffectPlayer?.start()
        soundEffectPlayer?.setOnCompletionListener {
            it.release()
            soundEffectPlayer = null
        }
    }

    private fun playCelebrationSound() {
        soundEffectPlayer = MediaPlayer.create(this, R.raw.trompeta)
        soundEffectPlayer?.apply {
            setVolume(0.1f, 0.1f)
            start()
            setOnCompletionListener {
                it.release()
                soundEffectPlayer = null
            }
        }
    }

    private fun releaseAllMediaPlayers() {
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = null
        soundEffectPlayer?.stop()
        soundEffectPlayer?.release()
        soundEffectPlayer = null
    }

    private fun startBackgroundMusic() {
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.tutorial6)
        backgroundMusicPlayer?.setVolume(0.12f, 0.12f)
        backgroundMusicPlayer?.isLooping = true
        backgroundMusicPlayer?.start()
    }

    private fun stopBackgroundMusic() {
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseAllMediaPlayers()
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