package com.heptacreation.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.heptacreation.sumamente.R
import androidx.core.view.isVisible
import androidx.core.graphics.toColorInt
import androidx.core.content.edit

class InstructionsLevelsActivityFocoPlus : BaseActivity() {

    private lateinit var sharedPreferences: android.content.SharedPreferences
    private lateinit var tvGameName: TextView
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvLevelIndicator: TextView
    private lateinit var tvInstructions: TextView
    private lateinit var btnStart: androidx.appcompat.widget.AppCompatButton

    private var selectedLevel = 1
    private var isLevelSelectorExpanded = false
    private lateinit var levelSelectorButton: androidx.appcompat.widget.AppCompatButton
    private lateinit var levelDropdownContainer: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var levelScrollView: androidx.core.widget.NestedScrollView
    private lateinit var levelButtonsContainer: LinearLayout
    private lateinit var dropdownArrowIcon: ImageView
    private lateinit var dropdownGradientView: View
    private var currentDifficulty = DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE
    private var miniblockSubtypes: List<Int> = emptyList()
    private var isFirstTimeUser = false
    private var hasUserMadeSelection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupPreferences()
        hasUserMadeSelection = false
        setContentView(R.layout.activity_instructions_levels_foco)

        initializeViews()
        setupInfoBar()
        createLevelButtons()
        updateLevelIndicator()
        updateInstructions()
        setupClickListeners()

        setupScrollListener()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToGameSelection()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        hasUserMadeSelection = false
        setupInfoBar()
        updateLevelButtons()
    }

    private fun setupPreferences() {
        sharedPreferences = getSharedPreferences("MyPrefsFocoPlus", MODE_PRIVATE)

        currentDifficulty = sharedPreferences.getString("difficulty_focoplus",
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE)
            ?: DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE

        initializeScoreManager(currentDifficulty)

        selectedLevel = getNextAvailableLevel()
        isFirstTimeUser = (getUnlockedLevels() == 1 && !hasCompletedLevel(1) && !hasCompletedLevel(2))
    }

    private fun initializeViews() {
        tvGameName = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore = findViewById(R.id.tv_score)

        tvLevelIndicator = findViewById(R.id.tv_level_indicator)
        tvInstructions = findViewById(R.id.tv_instructions)
        btnStart = findViewById(R.id.btn_start)
        tvGameName = findViewById(R.id.tv_game_name)
        tvDifficulty = findViewById(R.id.tv_difficulty)
        tvScore = findViewById(R.id.tv_score)

        levelSelectorButton = findViewById(R.id.level_selector_button)
        levelDropdownContainer = findViewById(R.id.level_dropdown_container)
        levelScrollView = findViewById(R.id.level_dropdown_scroll)
        levelButtonsContainer = findViewById(R.id.level_buttons_container)
        dropdownArrowIcon = findViewById(R.id.dropdown_arrow_icon)
        dropdownGradientView = findViewById(R.id.dropdown_gradient_view)
    }

    private fun initializeScoreManager(difficulty: String) {
        when (difficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.initFocoPlus(this)
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.initFocoPlusPrincipiante(this)
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.initFocoPlusPro(this)
        }
    }

    private fun setupInfoBar() {
        tvGameName.text = getString(R.string.game_foco_plus)

        val difficultyText = when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> getString(R.string.difficulty_principiante)
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> getString(R.string.difficulty_avanzado)
            DifficultySelectionActivity.DIFFICULTY_PRO -> getString(R.string.difficulty_pro)
            else -> getString(R.string.difficulty_principiante)
        }

        tvDifficulty.text = difficultyText

        val currentScore = getCurrentScore()
        tvScore.text = getString(R.string.score_format, currentScore)
    }

    private fun getCurrentScore(): Int {
        return when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.currentScoreFocoPlus
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.currentScoreFocoPlusPrincipiante
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.currentScoreFocoPlusPro
            else -> ScoreManager.currentScoreFocoPlusPrincipiante
        }
    }

    private fun createLevelButtons() {
        setupLevelSelectorButton()
        setupDropdownLevels()

    }

    private fun setupLevelSelectorButton() {
        levelSelectorButton.text = getString(R.string.select_level_button)
        levelSelectorButton.textSize = 27f
        levelSelectorButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        levelSelectorButton.setOnClickListener {
            applyBounceEffect(it) {
                toggleLevelSelector()
            }
        }
    }

    private fun setupDropdownLevels() {
        levelButtonsContainer.removeAllViews()

        for (level in 1..10) {
            addDropdownLevelButton(level, isUnlocked = level <= getUnlockedLevels())
        }

        val unlockedLevels = getUnlockedLevels()

        if (unlockedLevels >= 11) {
            val maxVisibleLevel = minOf(unlockedLevels, 415)
            for (level in 11..maxVisibleLevel) {
                addDropdownLevelButton(level, isUnlocked = true)
            }
        }

        if (unlockedLevels < 415) {
            val suspensiveButtonsNeeded = when {
                unlockedLevels < 411 -> 5
                unlockedLevels == 411 -> 4
                unlockedLevels == 412 -> 3
                unlockedLevels == 413 -> 2
                else -> 1
            }

            repeat(suspensiveButtonsNeeded) {
                addSuspensiveButton()
            }
        }

        for (level in 416..420) {
            addDropdownLevelButton(level, isUnlocked = false)
        }
    }

    private fun addDropdownLevelButton(level: Int, isUnlocked: Boolean) {
        val levelLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12.dpToPx(this@InstructionsLevelsActivityFocoPlus))
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val button = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                84.dpToPx(this@InstructionsLevelsActivityFocoPlus),
                1f
            ).apply {
                setMargins(0, 0, 12.dpToPx(this@InstructionsLevelsActivityFocoPlus), 0)
            }

            text = getString(R.string.level_prefix, level)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            textSize = 27f
            typeface = androidx.core.content.res.ResourcesCompat.getFont(this@InstructionsLevelsActivityFocoPlus, R.font.gochi_hand)

            if (isUnlocked) {
                setBackgroundResource(R.drawable.level_button_background)
                setTextColor(ContextCompat.getColor(this@InstructionsLevelsActivityFocoPlus, android.R.color.white))

                setOnClickListener {
                    applyBounceEffect(this) {
                        selectLevel(level)
                        collapseLevelSelector()
                    }
                }
            } else {
                setBackgroundResource(R.drawable.button_background_locked)
                setTextColor(ContextCompat.getColor(this@InstructionsLevelsActivityFocoPlus, R.color.text_color_adaptive))

                setOnClickListener {
                    showLockedLevelMessage()
                }
            }
        }

        val lockIcon = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                36.dpToPx(this@InstructionsLevelsActivityFocoPlus),
                36.dpToPx(this@InstructionsLevelsActivityFocoPlus)
            )
            setImageResource(if (isUnlocked) R.drawable.ic_unlock else R.drawable.ic_lock)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        levelLayout.addView(button)
        levelLayout.addView(lockIcon)
        levelButtonsContainer.addView(levelLayout)
    }

    private fun addSuspensiveButton() {
        val levelLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 12.dpToPx(this@InstructionsLevelsActivityFocoPlus))
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val button = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                84.dpToPx(this@InstructionsLevelsActivityFocoPlus),
                1f
            ).apply {
                setMargins(0, 0, 12.dpToPx(this@InstructionsLevelsActivityFocoPlus), 0)
            }

            text = "..."
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
            textSize = 27f
            typeface = androidx.core.content.res.ResourcesCompat.getFont(this@InstructionsLevelsActivityFocoPlus, R.font.gochi_hand)
            setBackgroundResource(R.drawable.button_background_locked)
            setTextColor(ContextCompat.getColor(this@InstructionsLevelsActivityFocoPlus, R.color.text_color_adaptive))

            setOnClickListener {
                showLockedLevelMessage()
            }
        }

        val lockIcon = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                36.dpToPx(this@InstructionsLevelsActivityFocoPlus),
                36.dpToPx(this@InstructionsLevelsActivityFocoPlus)
            )
            setImageResource(R.drawable.ic_lock)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        levelLayout.addView(button)
        levelLayout.addView(lockIcon)
        levelButtonsContainer.addView(levelLayout)
    }

    private fun toggleLevelSelector() {
        if (isLevelSelectorExpanded) {
            collapseLevelSelector()
        } else {
            expandLevelSelector()
        }
    }

    private fun expandLevelSelector() {
        isLevelSelectorExpanded = true

        levelDropdownContainer.visibility = View.VISIBLE
        levelDropdownContainer.alpha = 0f
        levelDropdownContainer.translationY = -50f

        dropdownArrowIcon.visibility = View.VISIBLE
        dropdownGradientView.visibility = View.VISIBLE

        levelDropdownContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()

        dropdownArrowIcon.alpha = 0f
        dropdownGradientView.alpha = 0f
        dropdownArrowIcon.animate().alpha(1f).setDuration(400).setStartDelay(200).start()
        dropdownGradientView.animate().alpha(1f).setDuration(400).setStartDelay(200).start()
    }

    private fun collapseLevelSelector() {
        isLevelSelectorExpanded = false

        levelDropdownContainer.animate()
            .alpha(0f)
            .translationY(-50f)
            .setDuration(250)
            .setInterpolator(android.view.animation.AccelerateInterpolator())
            .withEndAction {
                levelDropdownContainer.visibility = View.GONE
                dropdownArrowIcon.visibility = View.GONE
                dropdownGradientView.visibility = View.GONE
            }
            .start()
    }

    private fun updateSelectedLevelDisplay() {
        levelSelectorButton.text = getString(R.string.level_prefix, selectedLevel)
        levelSelectorButton.textSize = 27f
        levelSelectorButton.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    }

    private fun selectLevel(level: Int) {
        selectedLevel = level
        hasUserMadeSelection = true
        val completedLevels = (1..420).count { hasCompletedLevel(it) }
        if (completedLevels == 0) {
            generateMiniblockSubtypesIfNeeded(level)
        }
        updateLevelIndicator()
        updateInstructions()
        updateSelectedLevelDisplay()
        btnStart.isEnabled = true

        updateLevelButtons()
    }

    private fun setupScrollListener() {
        levelScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 50 && dropdownArrowIcon.isVisible) {
                dropdownArrowIcon.animate().alpha(0f).setDuration(200).start()
                dropdownGradientView.animate().alpha(0.7f).setDuration(200).start()
            } else if (scrollY <= 50 && dropdownArrowIcon.isVisible) {
                dropdownArrowIcon.animate().alpha(1f).setDuration(200).start()
                dropdownGradientView.animate().alpha(1f).setDuration(200).start()
            }
        }
    }

    private fun updateLevelButtons() {
        setupDropdownLevels()
    }

    private fun updateLevelIndicator() {
        val completedLevels = (1..420).count { hasCompletedLevel(it) }

        val displayLevel = if (completedLevels == 0 && miniblockSubtypes.isEmpty()) {
            0
        } else if (hasUserMadeSelection) {
            selectedLevel
        } else {
            selectedLevel
        }

        val levelText = if (displayLevel == 0) {
            "NIVEL ${getString(R.string._0)} / 420"
        } else {
            getString(R.string.level_indicator_foco, displayLevel)
        }

        val spannableString = android.text.SpannableString(levelText)

        if (displayLevel == 0) {
            val zeroText = getString(R.string._0)
            val startIndex = levelText.indexOf(zeroText)
            if (startIndex != -1) {
                spannableString.setSpan(
                    android.text.style.ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)),
                    startIndex,
                    startIndex + zeroText.length,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
            val levelNumber = displayLevel.toString()
            val startIndex = levelText.indexOf(levelNumber)
            if (startIndex != -1) {
                spannableString.setSpan(
                    android.text.style.ForegroundColorSpan("#4391F8".toColorInt()),
                    startIndex,
                    startIndex + levelNumber.length,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        tvLevelIndicator.text = spannableString
    }

    private fun updateInstructions() {
        val completedLevels = (1..420).count { hasCompletedLevel(it) }

        if (completedLevels == 0 && miniblockSubtypes.isEmpty()) {
            tvInstructions.text = getString(R.string.select_level_button)
            tvInstructions.setTextColor(ContextCompat.getColor(this, R.color.red_primary))
            tvInstructions.gravity = android.view.Gravity.CENTER
            return
        }

        tvInstructions.setTextColor(ContextCompat.getColor(this, R.color.text_color_adaptive))
        tvInstructions.gravity = android.view.Gravity.CENTER

        val subtype = getSubtypeForLevel(selectedLevel)
        tvInstructions.text = getInstructionsForSubtype(subtype)
    }

    private fun getSubtypeForLevel(level: Int): Int {
        val miniblockIndex = (level - 1) / 14
        val positionInMiniblock = (level - 1) % 14

        val miniblockKey = "focoplus_miniblock_${currentDifficulty}_$miniblockIndex"
        val savedSubtypes = sharedPreferences.getString(miniblockKey, null)
        
        fun buildPoolFor(miniIdx: Int): List<Int> {
            val isOddMiniblock = ((miniIdx + 1) % 2 != 0)
            val pool = (1..14).toMutableList()
            if (isOddMiniblock) {
                pool.remove(11)
                if (!pool.contains(15)) pool.add(15)
            } else {
                pool.remove(15)
                if (!pool.contains(11)) pool.add(11)
            }
            return pool
        }

        if (savedSubtypes != null) {
            val subtypesList = savedSubtypes.split(",").mapNotNull { it.toIntOrNull() }
            return subtypesList.getOrElse(positionInMiniblock) {

                val pool = buildPoolFor(miniblockIndex)
                pool.getOrElse(positionInMiniblock) { ((level - 1) % 14) + 1 }
            }
        }

        if (miniblockSubtypes.isNotEmpty()) {
            return miniblockSubtypes.getOrElse(positionInMiniblock) {
                val pool = buildPoolFor(miniblockIndex)
                pool.getOrElse(positionInMiniblock) { ((level - 1) % 14) + 1 }
            }
        }

        val pool = buildPoolFor(miniblockIndex)
        return pool.getOrElse(positionInMiniblock) { ((level - 1) % 14) + 1 }
    }


    private fun getInstructionsForSubtype(subtype: Int): String {
        return when (subtype) {
            1, 2, 4, 5, 7, 9 -> getString(R.string.instruction_game_terms_28)
            3, 6, 8, 10 -> getString(R.string.instruction_game_terms_14)
            11 -> getString(R.string.instruction_game_roman_numerals) + "\n\n" +
                    getString(R.string.instruction_game_terms_14)
            12 -> getString(R.string.instruction_game_letters_value) + "\n\n" +
                    getString(R.string.instruction_game_terms_14)
            13 -> getString(R.string.instruction_game_terms_28) + "\n\n" +
                    getString(R.string.instruction_game_roman_numerals) + "\n\n" +
                    getString(R.string.instruction_game_letters_value)
            14 -> getString(R.string.instruction_game_figures_28)
            15 -> getString(R.string.instruction_game_time_addition) + "\n\n" +
                    getString(R.string.instruction_game_terms_14)
            else -> getString(R.string.instruction_game_terms_28)
        }
    }

    private fun getNextAvailableLevel(): Int {
        val unlockedLevels = getUnlockedLevels()
        for (level in 1..unlockedLevels) {
            if (!hasCompletedLevel(level)) {
                return level
            }
        }
        return 1
    }

    private fun getUnlockedLevels(): Int {
        return when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.unlockedLevelsFocoPlus
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.unlockedLevelsFocoPlusPrincipiante
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.unlockedLevelsFocoPlusPro
            else -> ScoreManager.unlockedLevelsFocoPlusPrincipiante
        }
    }

    private fun hasCompletedLevel(level: Int): Boolean {
        return when (currentDifficulty) {
            DifficultySelectionActivity.DIFFICULTY_AVANZADO -> ScoreManager.hasCompletedLevelFocoPlus(level)
            DifficultySelectionActivity.DIFFICULTY_PRINCIPIANTE -> ScoreManager.hasCompletedLevelFocoPlusPrincipiante(level)
            DifficultySelectionActivity.DIFFICULTY_PRO -> ScoreManager.hasCompletedLevelFocoPlusPro(level)
            else -> ScoreManager.hasCompletedLevelFocoPlusPrincipiante(level)
        }
    }

    private fun showLockedLevelMessage() {
        Toast.makeText(this, R.string.level_locked_message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToGameSelection() {
        val intent = Intent(this, GameSelectionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.btn_close).setOnClickListener {
            applyBounceEffect(it) { navigateToGameSelection() }
        }

        tvDifficulty.setOnClickListener {
            applyBounceEffect(it) { navigateToDifficultySelection() }
        }

        btnStart.setOnClickListener {
            applyBounceEffect(it) { startGame() }
        }
    }

    private fun navigateToDifficultySelection() {
        val intent = DifficultySelectionActivity.createIntent(this, "FocoPlus")
        startActivity(intent)
    }

    private fun startGame() {
        val completedLevels = (1..420).count { hasCompletedLevel(it) }
        if (completedLevels == 0) {
            saveMiniblockSubtypes(selectedLevel)
        }

        if (isFirstTimeUser && selectedLevel == 2) {
            isFirstTimeUser = false
        }

        val intent = Intent(this@InstructionsLevelsActivityFocoPlus, GameActivityFocoPlus::class.java)
        intent.putExtra("LEVEL", selectedLevel)
        intent.putExtra("DIFFICULTY", currentDifficulty)

        ensureMiniblockShuffleAndAttachToIntent(intent, selectedLevel, currentDifficulty)

        val subtype = getSubtypeForLevel(selectedLevel)
        intent.putExtra("SUBTYPE", subtype)

        startActivity(intent)
    }

    private fun ensureMiniblockShuffleAndAttachToIntent(
        intent: Intent,
        level: Int,
        difficulty: String
    ) {
        val miniblockIndex = (level - 1) / 14

        val prefs = getSharedPreferences("MyPrefsFocoPlus", MODE_PRIVATE)
        val key = "focoplus_miniblock_${difficulty}_${miniblockIndex}"
        val existing = prefs.getString(key, null)

        val order: IntArray = if (existing.isNullOrBlank()) {

            val isOddMiniblock = ((miniblockIndex + 1) % 2 != 0)

            val subtypesPool = (1..14).toMutableList()

            if (isOddMiniblock) {
                subtypesPool.remove(11)
                subtypesPool.add(15)
            } else {

                subtypesPool.remove(15)
            }

            val newOrder = subtypesPool.shuffled().toIntArray()
            prefs.edit { putString(key, newOrder.joinToString(",")) }
            newOrder
        } else {

            val list = existing.split(",").mapNotNull { it.toIntOrNull() }
            if (list.size == 14) {
                list.toIntArray()
            } else {
                val isOddMiniblock = ((miniblockIndex + 1) % 2 != 0)

                val subtypesPool = (1..14).toMutableList()

                if (isOddMiniblock) {

                    subtypesPool.remove(11)
                    subtypesPool.add(15)
                } else {
                    subtypesPool.remove(15)
                }

                val newOrder = subtypesPool.shuffled().toIntArray()
                prefs.edit { putString(key, newOrder.joinToString(",")) }
                newOrder
            }
        }

        intent.putExtra("FOCO_SHUFFLE_ORDER", order)
        intent.putExtra("FOCO_SHUFFLE_MINIBLOCK_INDEX", miniblockIndex)
        intent.putExtra("FOCO_SHUFFLE_DIFFICULTY", difficulty)
    }

    private fun Int.dpToPx(context: android.content.Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun generateMiniblockSubtypesIfNeeded(level: Int) {
        val miniblockIndex = (level - 1) / 14
        val miniblockKey = "focoplus_miniblock_${currentDifficulty}_$miniblockIndex"

        val savedSubtypes = sharedPreferences.getString(miniblockKey, null)

        miniblockSubtypes = if (savedSubtypes != null) {
            savedSubtypes.split(",").map { it.toInt() }
        } else {

            val isOddMiniblock = ((miniblockIndex + 1) % 2 != 0)
            val subtypesPool = (1..14).toMutableList()

            if (isOddMiniblock) {
                subtypesPool.remove(11)
                subtypesPool.add(15)
            } else {
                subtypesPool.remove(15)
            }

            subtypesPool.shuffled()
        }
    }

    private fun saveMiniblockSubtypes(level: Int) {
        val miniblockIndex = (level - 1) / 14
        val miniblockKey = "focoplus_miniblock_${currentDifficulty}_$miniblockIndex"

        val savedSubtypes = sharedPreferences.getString(miniblockKey, null)

        if (savedSubtypes == null && miniblockSubtypes.isNotEmpty()) {
            val subtypesString = miniblockSubtypes.joinToString(",")
            sharedPreferences.edit { putString(miniblockKey, subtypesString) }
        }
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