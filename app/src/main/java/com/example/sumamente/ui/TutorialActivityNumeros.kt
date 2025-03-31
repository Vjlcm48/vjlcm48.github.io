package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.sumamente.R
import java.util.Locale
import androidx.core.content.edit

class TutorialActivityNumeros : AppCompatActivity() {

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
    private lateinit var answerButtonsLayout: GridLayout
    private lateinit var btnAnswer1: Button
    private lateinit var btnAnswer2: Button
    private lateinit var btnAnswer3: Button
    private lateinit var btnAnswer4: Button
    private lateinit var confettiAnimation: LottieAnimationView
    private lateinit var finalMessageTextView: TextView
    private lateinit var finalTimeTextView: TextView
    private lateinit var finalPointsTextView: TextView
    private lateinit var starImageView: ImageView
    private lateinit var btnUnderstood: Button
    private lateinit var btnClose: ImageView
    private lateinit var checkSeleccionSimple: ImageView
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
    private lateinit var btnSkipTutorial: ImageView

    private lateinit var btnPlayPauseTutorial: ImageView
    private lateinit var seekBarTutorial: SeekBar

    private val fixedNumbers = listOf(7, 3, 5, -3, 4, 4, -11)
    private val handler = Handler(Looper.getMainLooper())
    private var backgroundMusicPlayer: MediaPlayer? = null
    private var soundEffectPlayer: MediaPlayer? = null
    private var responseModeDialog: ResponseModeDialog? = null
    private var currentNumberIndex = 0
    private var selectedMode = ResponseMode.SIMPLE_SELECTION

    private var isPaused = false
    private var pendingSequences = mutableListOf<SequenceStep>()
    private var currentProgress = 0
    private var totalTutorialDuration = 0L
    private var isUserInteractingWithSeekBar = false

    private data class SequenceStep(
        val action: () -> Unit,
        val delay: Long,
        val progress: Int,
        val description: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val hasSeenInstructions = prefs.getBoolean("hasSeenInstructionsNumeros", false)
        if (hasSeenInstructions) {
            val intent = Intent(this, LevelsActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_tutorial_numeros)
        initViews()
        setupTutorialControls()
        initSequences()
        startSequence()
        startBackgroundMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        rootLayout = findViewById(R.id.root_instructions_numeros)
        tvWelcomeText = findViewById(R.id.tv_welcome_text)
        tvGameName = findViewById(R.id.tv_game_name)

        tvWelcomeText.text = getString(R.string.instructions_numeros_intro)
        tvGameName.text = getString(R.string.game_numeros_plus)

        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.blue_pressed))

        instructionTextView = findViewById(R.id.tv_instruction)
        modeDialogContainer = findViewById(R.id.layout_dialog_modes)
        lottieHandChrono = findViewById(R.id.lottie_hand_chrono)
        lottieHandAnswer = findViewById(R.id.lottie_hand_answer)

        circleView = findViewById(R.id.circle_view)
        vamosTextView = findViewById(R.id.tv_vamos_instructions)
        numbersContainer = findViewById(R.id.layout_numbers_container)
        chronometerTextView = findViewById(R.id.tv_chronometer_demo)
        answerButtonsLayout = findViewById(R.id.layout_answer_buttons)

        btnAnswer1 = findViewById(R.id.btn_answer_1_demo)
        btnAnswer2 = findViewById(R.id.btn_answer_2_demo)
        btnAnswer3 = findViewById(R.id.btn_answer_3_demo)
        btnAnswer4 = findViewById(R.id.btn_answer_4_demo)
        confettiAnimation = findViewById(R.id.lottie_confetti)
        finalMessageTextView = findViewById(R.id.tv_final_message)
        finalTimeTextView = findViewById(R.id.tv_final_time)
        finalPointsTextView = findViewById(R.id.tv_final_points)
        starImageView = findViewById(R.id.iv_star)
        btnUnderstood = findViewById(R.id.btn_understood)
        btnClose = findViewById(R.id.btn_close_instructions)
        checkSeleccionSimple = findViewById(R.id.check_seleccion_simple)
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
        btnSkipTutorial = findViewById(R.id.btn_skip_tutorial)

        btnPlayPauseTutorial = findViewById(R.id.btn_play_pause_tutorial)
        seekBarTutorial = findViewById(R.id.seek_bar_tutorial)

        btnSkipTutorial.setOnClickListener {
            applyBounceEffect(it) {
                markTutorialAsSeenAndNavigate()
            }
        }
    }

    private fun setupTutorialControls() {

        btnPlayPauseTutorial.setOnClickListener {
            applyBounceEffect(it) {
                togglePausePlay()
            }
        }

        seekBarTutorial.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserInteractingWithSeekBar = true
                if (!isPaused) {

                    pauseTutorial(updateUI = false)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserInteractingWithSeekBar = false

                jumpToProgress(currentProgress)

                if (!isPaused) {
                    resumeTutorial()
                }
            }
        })
    }

    private fun initSequences() {

        val durations = listOf(
            7500L,
            3000L,
            12000L,
            4000L,
            3000L,
            12600L,
            5000L,
            4370L,
            3000L,
            4000L,
            6000L
        )

        totalTutorialDuration = durations.sum()
    }

    private fun startSequence() {
        clearPendingSequences()
        showWelcomeAndIntroMessages()
    }

    private fun togglePausePlay() {
        if (isPaused) {
            resumeTutorial()
        } else {
            pauseTutorial()
        }
    }

    private fun pauseTutorial(updateUI: Boolean = true) {
        isPaused = true
        if (updateUI) {
            btnPlayPauseTutorial.setImageResource(R.drawable.ic_play_circle)
            btnPlayPauseTutorial.contentDescription = getString(R.string.play_tutorial)
        }
    }

    private fun resumeTutorial() {
        isPaused = false
        btnPlayPauseTutorial.setImageResource(R.drawable.ic_pause_circle)
        btnPlayPauseTutorial.contentDescription = getString(R.string.pause_tutorial)

        if (pendingSequences.isNotEmpty()) {
            val nextStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(nextStep)
        }
    }

    private fun jumpToProgress(progress: Int) {

        clearPendingSequences()

        hideAllViews()


        when {
            progress < 10 -> {
                showWelcomeAndIntroMessages()
            }
            progress < 15 -> {
                showStepOne()
            }
            progress < 30 -> {
                showInstructionToChooseMode()
            }
            progress < 35 -> { // 30-35%: Paso 2
                proceedToStepTwo()
            }
            progress < 50 -> {
                proceedToCircleAnimation()
            }
            progress < 60 -> {
                showStepThree()
            }
            progress < 75 -> {
                showChronometerAndButtons()
            }
            progress < 80 -> {
                showTutorialResultMessage()
            }
            progress < 90 -> {
                showCelebration()
            }
            else -> {
                showFinalClosingMessage()
            }
        }

        currentProgress = progress
        seekBarTutorial.progress = currentProgress
    }

    private fun hideAllViews() {

        tvWelcomeText.visibility = View.GONE
        tvGameName.visibility = View.GONE
        tvStepIntroPartA.visibility = View.GONE
        tvStepIntroPartB.visibility = View.GONE
        tvStepOne.visibility = View.GONE
        instructionTextView.visibility = View.GONE
        modeDialogContainer.visibility = View.GONE
        lottieHandChrono.visibility = View.GONE
        lottieHandAnswer.visibility = View.GONE
        circleView.visibility = View.GONE
        vamosTextView.visibility = View.GONE
        numbersContainer.visibility = View.GONE
        tvStepTwo.visibility = View.GONE
        tvStepThreePartOne.visibility = View.GONE
        tvStepThreePartTwo.visibility = View.GONE
        chronometerTextView.visibility = View.GONE
        answerButtonsLayout.visibility = View.GONE
        btnAnswer1.visibility = View.GONE
        btnAnswer2.visibility = View.GONE
        btnAnswer3.visibility = View.GONE
        btnAnswer4.visibility = View.GONE
        checkSeleccionSimple.visibility = View.GONE
        tvResultMessage.visibility = View.GONE
        confettiAnimation.visibility = View.GONE
        finalMessageTextView.visibility = View.GONE
        finalTimeTextView.visibility = View.GONE
        finalPointsTextView.visibility = View.GONE
        starImageView.visibility = View.GONE
        tvClosingMessage.visibility = View.GONE
        btnUnderstood.visibility = View.GONE
        btnClose.visibility = View.GONE
    }

    private fun clearPendingSequences() {

        handler.removeCallbacksAndMessages(null)
        pendingSequences.clear()
    }

    private fun scheduleTutorialStep(step: SequenceStep) {
        updateProgress(step.progress)

        handler.postDelayed({

            if (isPaused) {
                pendingSequences.add(0, step)
            } else {
                step.action()

                if (pendingSequences.isNotEmpty()) {
                    val nextStep = pendingSequences.removeAt(0)
                    scheduleTutorialStep(nextStep)
                }
            }
        }, step.delay)
    }

    private fun updateProgress(progress: Int) {
        if (!isUserInteractingWithSeekBar) {
            currentProgress = progress
            seekBarTutorial.progress = progress
        }
    }

    private fun addTutorialStep(action: () -> Unit, delay: Long, progress: Int, description: String) {
        val step = SequenceStep(action, delay, progress, description)
        pendingSequences.add(step)
    }

    private fun showWelcomeAndIntroMessages() {

        updateProgress(0)

        tvWelcomeText.visibility = View.VISIBLE
        val bounceInWelcome = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvWelcomeText.startAnimation(bounceInWelcome)

        addTutorialStep({
            tvGameName.visibility = View.VISIBLE
            val bounceInGameName = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
            tvGameName.startAnimation(bounceInGameName)
        }, 2500, 5, "Mostrar nombre del juego")

        addTutorialStep({
            tvStepIntroPartA.visibility = View.VISIBLE
            tvStepIntroPartB.visibility = View.VISIBLE

            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.activity_fade_in)
            tvStepIntroPartA.startAnimation(fadeIn)
            tvStepIntroPartB.startAnimation(fadeIn)
        }, 2000, 10, "Mostrar introducción paso A y B")

        addTutorialStep({
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


                    if (!isPaused) {
                        showStepOne()
                    } else {

                        pendingSequences.add(0, SequenceStep({ showStepOne() }, 0, 15, "Mostrar paso 1"))
                    }
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 3000, 15, "Ocultar introducción")


            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun showStepOne() {
        updateProgress(15)

        tvStepOne.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvStepOne.startAnimation(bounceIn)

        addTutorialStep({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvStepOne.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvStepOne.visibility = View.GONE
                    if (!isPaused) {
                        showInstructionToChooseMode()
                    } else {
                        pendingSequences.add(0, SequenceStep({ showInstructionToChooseMode() }, 0, 20, "Mostrar selección de modo"))
                    }
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 3000, 20, "Finalizar paso 1")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun showInstructionToChooseMode() {
        updateProgress(20)

        instructionTextView.visibility = View.VISIBLE
        instructionTextView.text = getString(R.string.escoge_tu_modo_de_respuesta)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.activity_fade_in)
        instructionTextView.startAnimation(fadeIn)

        addTutorialStep({ showModesDialog() }, 1000, 22, "Mostrar diálogo de modos")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun showModesDialog() {
        modeDialogContainer.visibility = View.VISIBLE
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        modeDialogContainer.startAnimation(slideUp)

        responseModeDialog = ResponseModeDialog(this).apply {
            setOnResponseModeSelectedListener(object :
                ResponseModeDialog.OnResponseModeSelectedListener {
                override fun onResponseModeSelected(mode: ResponseMode) {
                    selectedMode = mode
                    hideModesDialog()
                }
            })
        }

        simulateHandInfoIcons()
    }

    private fun hideModesDialog() {
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
        modeDialogContainer.startAnimation(fadeOut)
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                modeDialogContainer.visibility = View.GONE
                instructionTextView.visibility = View.GONE
                if (!isPaused) {
                    proceedToStepTwo()
                } else {
                    pendingSequences.add(0, SequenceStep({ proceedToStepTwo() }, 0, 30, "Proceder al paso 2"))
                }
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun simulateHandInfoIcons() {
        updateProgress(25)

        val infoSimple = findViewById<ImageButton>(R.id.info_seleccion_simple)
        val infoType = findViewById<ImageButton>(R.id.info_escribe_respuesta)
        val btnSeleccionSimple = findViewById<Button>(R.id.btn_seleccion_simple)

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
            targetView = btnSeleccionSimple,
            appearDelay = 9800,
            clickDelay = 10800,
            hideDelay = 12000,
            onComplete = {
                animateCheck(checkSeleccionSimple)
            }
        )

        addTutorialStep({
            modeDialogContainer.visibility = View.GONE
            checkSeleccionSimple.visibility = View.GONE
            instructionTextView.visibility = View.GONE
            if (!isPaused) {
                proceedToStepTwo()
            } else {
                pendingSequences.add(0, SequenceStep({ proceedToStepTwo() }, 0, 30, "Proceder al paso 2"))
            }
        }, 13700, 30, "Finalizar selección de modo")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun proceedToStepTwo() {
        updateProgress(30)

        tvStepTwo.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvStepTwo.startAnimation(bounceIn)

        addTutorialStep({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvStepTwo.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvStepTwo.visibility = View.GONE
                    if (!isPaused) {
                        proceedToCircleAnimation()
                    } else {
                        pendingSequences.add(0, SequenceStep({ proceedToCircleAnimation() }, 0, 35, "Mostrar animación del círculo"))
                    }
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 4000, 35, "Finalizar paso 2")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun proceedToCircleAnimation() {
        updateProgress(35)

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
                addTutorialStep({
                    vamosTextView.visibility = View.GONE
                    if (!isPaused) {
                        showNumbersSequence()
                    } else {
                        pendingSequences.add(0, SequenceStep({ showNumbersSequence() }, 0, 40, "Mostrar secuencia de números"))
                    }
                }, 1000, 40, "Preparar secuencia de números")

                    val step = pendingSequences.removeAt(0)
                    scheduleTutorialStep(step)

            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun showNumbersSequence() {
        updateProgress(40)

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
        if (isPaused) {
            pendingSequences.add(0, SequenceStep({ displayNextNumber() }, 0, 45, "Continuar secuencia de números"))
            return
        }

        if (currentNumberIndex >= fixedNumbers.size) {
            hideCircle()
            return
        }

        val number = fixedNumbers[currentNumberIndex]
        currentNumberIndex++


        val progressValue = 40 + (currentNumberIndex * 10 / fixedNumbers.size)
        updateProgress(progressValue)

        tvNumber.apply {
            textSize = 150f
            text = String.format(Locale.getDefault(), "%d", number)
            visibility = View.VISIBLE

            when {
                number < 0 -> setTextColor(ContextCompat.getColor(context, R.color.red))
                number == 4 && currentNumberIndex == 5 -> setTextColor(ContextCompat.getColor(context, R.color.black))
                number == 4 && currentNumberIndex == 6 -> setTextColor(ContextCompat.getColor(context, R.color.yellow))
                else -> setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            val bounce = AnimationUtils.loadAnimation(this@TutorialActivityNumeros, R.anim.bounce_in)
            startAnimation(bounce)
        }

        addTutorialStep({ displayNextNumber() }, 1800, progressValue, "Mostrar siguiente número")

            val step = pendingSequences.removeAt(0)
            scheduleTutorialStep(step)

    }

    private fun hideCircle() {
        updateProgress(50)

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
                if (!isPaused) {
                    showStepThree()
                } else {
                    pendingSequences.add(0, SequenceStep({ showStepThree() }, 0, 50, "Mostrar paso 3"))
                }
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        circleScaleDown.start()
    }

    private fun showStepThree() {
        updateProgress(50)

        tvStepThreePartOne.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvStepThreePartOne.startAnimation(bounceIn)

        addTutorialStep({
            tvStepThreePartTwo.visibility = View.VISIBLE
            val bounceInPartTwo = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
            tvStepThreePartTwo.startAnimation(bounceInPartTwo)
        }, 2500, 55, "Mostrar segunda parte del paso 3")

        addTutorialStep({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvStepThreePartOne.startAnimation(fadeOut)
            tvStepThreePartTwo.startAnimation(fadeOut)

            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvStepThreePartOne.visibility = View.GONE
                    tvStepThreePartTwo.visibility = View.GONE
                    if (!isPaused) {
                        showChronometerAndButtons()
                    } else {
                        pendingSequences.add(0, SequenceStep({ showChronometerAndButtons() }, 0, 60, "Mostrar cronómetro y botones"))
                    }
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 2500, 60, "Finalizar paso 3")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun showChronometerAndButtons() {
        updateProgress(60)

        chronometerTextView.visibility = View.VISIBLE
        chronometerTextView.text = getString(R.string.default_chronometer)
        startChronometerSimulation()

        answerButtonsLayout.visibility = View.VISIBLE
        btnAnswer1.visibility = View.VISIBLE
        btnAnswer2.visibility = View.VISIBLE
        btnAnswer3.visibility = View.VISIBLE
        btnAnswer4.visibility = View.VISIBLE

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.activity_fade_in)
        answerButtonsLayout.startAnimation(fadeIn)

        lottieHandChrono.visibility = View.VISIBLE
        lottieHandChrono.setAnimation("handchrono_animation.json")
        lottieHandChrono.playAnimation()

        addTutorialStep({
            val fadeOutChronoHand = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            lottieHandChrono.startAnimation(fadeOutChronoHand)

            fadeOutChronoHand.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    lottieHandChrono.visibility = View.GONE

                    addTutorialStep({
                        showHandWithFade(
                            targetView = btnAnswer2,
                            appearDelay = 0L,
                            clickDelay = 600L,
                            hideDelay = 2000L,
                            onComplete = {
                                highlightCorrectButton(btnAnswer2)
                            }
                        )
                    }, 300, 70, "Mostrar mano para seleccionar respuesta")

                    if (!isPaused) {
                        val step = pendingSequences.removeAt(0)
                        scheduleTutorialStep(step)
                    }
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 2500, 65, "Mano en cronómetro")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun startChronometerSimulation() {
        updateProgress(65)

        val startTime = System.currentTimeMillis()
        val totalDuration = 4370L
        val tick = object : Runnable {
            override fun run() {
                if (isPaused) {

                    pendingSequences.add(0, SequenceStep({ startChronometerSimulation() }, 0, 65, "Reanudar cronómetro"))
                    return
                }

                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < totalDuration) {
                    val seconds = elapsed / 1000.0
                    val formatted = String.format(Locale.getDefault(), "%.2f", seconds)
                    chronometerTextView.text = formatted

                    when {
                        seconds < 3.0 -> {
                            chronometerTextView.setTextColor(
                                ContextCompat.getColor(
                                    this@TutorialActivityNumeros,
                                    R.color.green_medium
                                )
                            )
                        }
                        seconds < 5.0 -> {
                            chronometerTextView.setTextColor(
                                ContextCompat.getColor(
                                    this@TutorialActivityNumeros,
                                    R.color.orange_dark
                                )
                            )
                        }
                        else -> {
                            chronometerTextView.setTextColor(
                                ContextCompat.getColor(
                                    this@TutorialActivityNumeros,
                                    R.color.red
                                )
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
    }

    private fun highlightCorrectButton(button: Button) {
        updateProgress(70)

        button.setOnClickListener {
            playClickSound()
            button.setBackgroundResource(R.drawable.sombra_correcta)
            val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
            button.startAnimation(pulse)

            addTutorialStep({
                chronometerTextView.visibility = View.GONE
                answerButtonsLayout.visibility = View.GONE
                if (!isPaused) {
                    showTutorialResultMessage()
                } else {
                    pendingSequences.add(0, SequenceStep({ showTutorialResultMessage() }, 0, 75, "Mostrar mensaje de resultado"))
                }
            }, 1500, 75, "Finalizar selección de respuesta")

                val step = pendingSequences.removeAt(0)
                scheduleTutorialStep(step)

        }

        button.performClick()
    }

    private fun showTutorialResultMessage() {
        updateProgress(75)

        tvResultMessage.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvResultMessage.startAnimation(bounceIn)

        addTutorialStep({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvResultMessage.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvResultMessage.visibility = View.GONE
                    if (!isPaused) {
                        showCelebration()
                    } else {
                        pendingSequences.add(0, SequenceStep({ showCelebration() }, 0, 80, "Mostrar celebración"))
                    }
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 3000, 80, "Finalizar mensaje de resultado")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun showCelebration() {
        updateProgress(80)

        confettiAnimation.visibility = View.VISIBLE
        confettiAnimation.setAnimation("confetti_animation.json")
        confettiAnimation.repeatCount = 0
        confettiAnimation.playAnimation()

        playCelebrationSound()

        finalMessageTextView.visibility = View.VISIBLE
        finalMessageTextView.text = getString(R.string.perfecto)

        addTutorialStep({
            finalTimeTextView.visibility = View.VISIBLE
            finalTimeTextView.text = getString(R.string.formatted_time, "4.37")
        }, 1000, 85, "Mostrar tiempo final")

        addTutorialStep({
            finalPointsTextView.visibility = View.VISIBLE
            finalPointsTextView.text = getString(R.string.formatted_points, 200)
            starImageView.visibility = View.VISIBLE
        }, 1000, 90, "Mostrar puntos finales")

        addTutorialStep({
            confettiAnimation.visibility = View.GONE
            finalMessageTextView.visibility = View.GONE
            finalTimeTextView.visibility = View.GONE
            finalPointsTextView.visibility = View.GONE
            starImageView.visibility = View.GONE

            if (!isPaused) {
                showFinalClosingMessage()
            } else {
                pendingSequences.add(0, SequenceStep({ showFinalClosingMessage() }, 0, 95, "Mostrar mensaje final"))
            }
        }, 2000, 95, "Finalizar celebración")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun showFinalClosingMessage() {
        updateProgress(95)

        tvClosingMessage.visibility = View.VISIBLE
        val bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in)
        tvClosingMessage.startAnimation(bounceIn)

        addTutorialStep({
            btnUnderstood.visibility = View.VISIBLE
            btnClose.visibility = View.VISIBLE

            btnUnderstood.setOnClickListener {
                applyBounceEffect(it) {
                    markTutorialAsSeenAndNavigate()
                }
            }
            btnClose.setOnClickListener {
                applyBounceEffect(it) {
                    markTutorialAsSeenAndNavigate()
                }
            }
        }, 2000, 100, "Mostrar botones finales")

        addTutorialStep({
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            tvClosingMessage.startAnimation(fadeOut)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    tvClosingMessage.visibility = View.GONE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 2000, 100, "Finalizar mensaje de cierre")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun markTutorialAsSeenAndNavigate() {
        stopBackgroundMusic()
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        prefs.edit { putBoolean("hasSeenInstructionsNumeros", true) }
        val intent = Intent(this, DifficultySelectionActivity::class.java)
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
        addTutorialStep({
            if (isPaused) {
                pendingSequences.add(0, SequenceStep({
                    showHandWithFade(targetView, appearDelay, clickDelay, hideDelay, onComplete)
                }, 0, currentProgress, "Reanudar animación de mano"))
                return@addTutorialStep
            }

            lottieHandAnswer.alpha = 0f
            lottieHandAnswer.visibility = View.VISIBLE
            showHandOnViewFor(duration = 2000, targetView = targetView)
            lottieHandAnswer.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }, appearDelay, currentProgress, "Mostrar mano")

        addTutorialStep({
            if (!isPaused) {
                playClickSound()
            }
        }, clickDelay - appearDelay, currentProgress, "Sonido de click")

        addTutorialStep({
            if (isPaused) {
                pendingSequences.add(0, SequenceStep({
                    lottieHandAnswer.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            hideHand()
                            onComplete?.invoke()
                        }
                        .start()
                }, 0, currentProgress, "Completar animación de mano"))
                return@addTutorialStep
            }

            lottieHandAnswer.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    hideHand()
                    onComplete?.invoke()
                }
                .start()
        }, hideDelay - clickDelay, currentProgress, "Ocultar mano")

            val firstStep = pendingSequences.removeAt(0)
            scheduleTutorialStep(firstStep)

    }

    private fun showHandOnViewFor(
        duration: Long = 2000,
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
            }, duration)
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
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.tutorial1)
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
        clearPendingSequences()
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

