package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.airbnb.lottie.LottieAnimationView
import com.example.sumamente.R
import java.util.Locale

class TutorialActivityNumeros : BaseActivity()  {

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
    private lateinit var btnSkipTutorial: ImageView
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
    private var isAlive = true
    private val handler = Handler(Looper.getMainLooper())
    private var backgroundMusicPlayer: MediaPlayer? = null
    private var soundEffectPlayer: MediaPlayer? = null
    private lateinit var sharedPreferences: SharedPreferences

    private var currentNumberIndex = 0

    private val fixedNumbers = listOf(7, 3, 5, -3, 4, 4, -11)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)


        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val hasSeenInstructions = prefs.getBoolean("hasSeenInstructionsNumeros", false)
        if (hasSeenInstructions) {
            startActivity(Intent(this, LevelsActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_tutorial_numeros)
        initViews()
        startSequence()
        startBackgroundMusic()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        rootLayout = findViewById(R.id.root_instructions_numeros)
        tvWelcomeText = findViewById(R.id.tv_welcome_text)
        tvGameName = findViewById(R.id.tv_game_name)
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

        tvWelcomeText.text = getString(R.string.instructions_numeros_intro)
        tvGameName.text = getString(R.string.game_numeros_plus)
        tvGameName.setTextColor(ContextCompat.getColor(this, R.color.blue_pressed))

        btnSkipTutorial.setOnClickListener {
            applyBounceEffect(it) { markTutorialAsSeenAndNavigate() }
        }
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

        handler.postDelayed({ showModesDialog() }, 1000)
    }

    private fun showModesDialog() {
        modeDialogContainer.visibility = View.VISIBLE
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        modeDialogContainer.startAnimation(slideUp)

        ResponseModeDialog(this).apply {
            setOnResponseModeSelectedListener(object : ResponseModeDialog.OnResponseModeSelectedListener {
                override fun onResponseModeSelected(mode: ResponseMode) {
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
                proceedToStepTwo()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }


    private fun simulateHandInfoIcons() {
        val infoSimple = findViewById<ImageButton>(R.id.info_seleccion_simple)
        val infoType = findViewById<ImageButton>(R.id.info_escribe_respuesta)
        val btnSeleccionSimple = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btn_seleccion_simple)

        infoSimple.visibility = View.VISIBLE
        infoType.visibility = View.VISIBLE

        showHandWithFade(infoType, 2000, 2800, 4000) {
            showTooltip(infoType, R.string.write_answer_title, R.string.write_answer_message)
        }

        showHandWithFade(infoSimple, 5800, 6800, 8000) {
            showTooltip(infoSimple, R.string.selection_simple_title, R.string.selection_simple_message)
        }

        showHandWithFade(btnSeleccionSimple, 9800, 10800, 12000) {
            animateCheck(checkSeleccionSimple)
        }

        handler.postDelayed({
            modeDialogContainer.visibility = View.GONE
            checkSeleccionSimple.visibility = View.GONE
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
                    showChronometerAndButtons()
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 5000)
    }


    private fun showChronometerAndButtons() {
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

        handler.postDelayed({
            val fadeOutChronoHand = AnimationUtils.loadAnimation(this, R.anim.activity_fade_out)
            lottieHandChrono.startAnimation(fadeOutChronoHand)

            fadeOutChronoHand.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    lottieHandChrono.visibility = View.GONE

                    handler.postDelayed({
                        showHandWithFade(
                            targetView = btnAnswer2,
                            appearDelay = 0L,
                            clickDelay = 600L,
                            hideDelay = 2000L,
                            onComplete = {
                                highlightCorrectButton(btnAnswer2)
                            }
                        )
                    }, 300)
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }, 2500)
    }

    private fun startChronometerSimulation() {
        val startTime = System.currentTimeMillis()
        val totalDuration = 4370L
        val tick = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < totalDuration) {
                    val seconds = elapsed / 1000.0
                    val formatted = String.format(Locale.getDefault(), "%.2f", seconds)
                    chronometerTextView.text = formatted

                    when {
                        seconds < 3.0 -> chronometerTextView.setTextColor(ContextCompat.getColor(this@TutorialActivityNumeros, R.color.green_medium))
                        seconds < 5.0 -> chronometerTextView.setTextColor(ContextCompat.getColor(this@TutorialActivityNumeros, R.color.orange_dark))
                        else -> chronometerTextView.setTextColor(ContextCompat.getColor(this@TutorialActivityNumeros, R.color.red))
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
        button.setOnClickListener {
            playClickSound()
            button.setBackgroundResource(R.drawable.sombra_correcta)
            val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
            button.startAnimation(pulse)

            handler.postDelayed({
                chronometerTextView.visibility = View.GONE
                answerButtonsLayout.visibility = View.GONE
                showTutorialResultMessage()
            }, 1500)
        }

        button.performClick()
    }

    private fun showTutorialResultMessage() {
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
            finalPointsTextView.text = getString(R.string.formatted_points, 200)
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

    private fun markTutorialAsSeenAndNavigate() {
        stopBackgroundMusic()
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        prefs.edit { putBoolean("hasSeenInstructionsNumeros", true) }

        val intent = Intent(this, DifficultySelectionActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun stopBackgroundMusic() {
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = null
    }

    private fun playClickSound() {
        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            soundEffectPlayer = MediaPlayer.create(this, R.raw.clicbotones)
            soundEffectPlayer?.start()
            soundEffectPlayer?.setOnCompletionListener {
                it.release()
                soundEffectPlayer = null
            }
        }
    }

    private fun playCelebrationSound() {
        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
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
            lottieHandAnswer.animate().alpha(1f).setDuration(200).start()
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

        if (!isAlive || isFinishing || isDestroyed || anchorView.windowToken == null || !anchorView.isAttachedToWindow) return

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

    private fun releaseAllMediaPlayers() {
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = null

        soundEffectPlayer?.stop()
        soundEffectPlayer?.release()
        soundEffectPlayer = null
    }

    private fun startBackgroundMusic() {

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            backgroundMusicPlayer = MediaPlayer.create(this, R.raw.tutorial1)
            backgroundMusicPlayer?.setVolume(0.12f, 0.12f)
            backgroundMusicPlayer?.isLooping = true
            backgroundMusicPlayer?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (backgroundMusicPlayer?.isPlaying == true) {
            backgroundMusicPlayer?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            if (backgroundMusicPlayer?.isPlaying == false) {
                backgroundMusicPlayer?.start()
            }
        }
    }

    override fun onDestroy() {
        isAlive = false
        handler.removeCallbacksAndMessages(null)
        releaseAllMediaPlayers()
        super.onDestroy()
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


