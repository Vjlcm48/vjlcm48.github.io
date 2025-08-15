package com.heptacreation.sumamente.s5e.ui

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.setPadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.s5e.core.Difficulty
import com.heptacreation.sumamente.s5e.core.Sudoku5Config
import com.heptacreation.sumamente.s5e.core.Sudoku5Precalc
import com.heptacreation.sumamente.s5e.ui.S5ESelectDifficultyActivity.Companion.EXTRA_DIFFICULTY
import com.heptacreation.sumamente.s5e.ui.base.BaseActivityS5E
import com.heptacreation.sumamente.s5e.viewmodel.GameViewModelS5E
import kotlinx.coroutines.launch

class GameActivityS5E : BaseActivityS5E() {

    private val vm: GameViewModelS5E by viewModels()
    private lateinit var grid: GridLayout
    private lateinit var timer: TextView
    private lateinit var mistakes: TextView

    private var selectedCell: Pair<Int, Int>? = null
    private val cellViews = mutableMapOf<Pair<Int, Int>, TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s5e_game)

        grid = findViewById(R.id.boardGrid)
        timer = findViewById(R.id.tvTimer)
        mistakes = findViewById(R.id.tvMistakes)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<ImageButton>(R.id.btnUndo).setOnClickListener { vm.undo() }
        findViewById<ImageButton>(R.id.btnGiveUp).setOnClickListener { vm.giveUpRevealSolution() }
        findViewById<ImageButton>(R.id.btnSolution).setOnClickListener { vm.giveUpRevealSolution() }
        findViewById<ImageButton>(R.id.btnCheck).setOnClickListener {

        }


        val ids = listOf(R.id.key1, R.id.key2, R.id.key3, R.id.key4, R.id.key5)
        ids.forEachIndexed { index, id ->
            findViewById<TextView>(id).setOnClickListener { onKeypadPressed(index + 1) }
        }


        buildBoardGrid()


        val diffName = intent.getStringExtra(EXTRA_DIFFICULTY)
        val diff = diffName?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() } ?: Difficulty.PRINCIPIANTE
        vm.startNewGame(diff, seed = null)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { st ->
                    // Timer y errores
                    timer.text = formatElapsed(st.elapsedMillis)
                    mistakes.text = getString(R.string.excelente)
                    // Render celdas
                    renderBoard(st.puzzle, st.current)
                }
            }
        }
    }

    private fun formatElapsed(ms: Long): String {
        val s = ms / 1000
        val m = s / 60
        val r = s % 60
        return String.format(java.util.Locale.getDefault(), "%02d:%02d", m, r)

    }

    private fun buildBoardGrid() {
        grid.columnCount = 9
        grid.rowCount = 9
        grid.removeAllViews()
        val size = resources.displayMetrics.widthPixels
        val cellSide = (size * 0.10).toInt()
        val lp = GridLayout.LayoutParams().apply {
            width = cellSide
            height = cellSide
            setMargins(2, 2, 2, 2)
        }

        for (r in 0 until Sudoku5Config.BOARD_SIZE) {
            for (c in 0 until Sudoku5Config.BOARD_SIZE) {
                val tv = TextView(this).apply {
                    layoutParams = GridLayout.LayoutParams(lp)
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                    gravity = android.view.Gravity.CENTER
                    setPadding(0)
                }
                val cell = r to c
                styleCellAsInactive(tv)
                if (cell in Sudoku5Precalc.activePositions) {
                    styleCellAsEditable(tv)
                    tv.setOnClickListener {
                        selectedCell = cell
                        highlightSelection()
                    }
                } else {
                    tv.isClickable = false
                }
                grid.addView(tv)
                cellViews[cell] = tv
            }
        }
    }

    private fun styleCellAsInactive(tv: TextView) {
        tv.text = ""
        tv.background = shapeRect(android.R.color.transparent, 0f, android.R.color.transparent)
    }


    private fun styleCellAsEditable(tv: TextView) {
        tv.setTextColor(getColor(R.color.s5e_onSurface))
        tv.background = shapeRect(R.color.s5e_surface, 6f, R.color.s5e_outline)
    }

    private fun styleCellAsFixed(tv: TextView) {
        tv.setTextColor(getColor(R.color.s5e_onPrimaryContainer))
        tv.background = shapeRect(R.color.s5e_green_100, 6f, R.color.s5e_outline)
    }

    private fun styleCellAsSelected(tv: TextView) {
        tv.background = shapeRect(R.color.s5e_green_50, 6f, R.color.s5e_primary)
    }

    private fun shapeRect(fillColorRes: Int, radiusDp: Float, strokeColorRes: Int): GradientDrawable {
        val d = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = resources.displayMetrics.density * radiusDp
        }

        if (fillColorRes == android.R.color.transparent) {
            d.setColor(android.graphics.Color.TRANSPARENT)
        } else {
            d.setColor(getColor(fillColorRes))
        }

        if (strokeColorRes != android.R.color.transparent) {
            val strokePx = (1 * resources.displayMetrics.density).toInt()
            d.setStroke(strokePx, getColor(strokeColorRes))
        }

        return d
    }

    private fun highlightSelection() {

        val sel = selectedCell
        cellViews.forEach { (cell, tv) ->
            if (cell !in Sudoku5Precalc.activePositions) return@forEach
            val isFixed = vm.state.value.puzzle.containsKey(cell)
            when {
                sel == cell -> styleCellAsSelected(tv)
                isFixed     -> styleCellAsFixed(tv)
                else        -> styleCellAsEditable(tv)
            }
        }
        renderBoard(vm.state.value.puzzle, vm.state.value.current)
    }

    private fun renderBoard(
        puzzle: Map<Pair<Int, Int>, Int>,
        current: Map<Pair<Int, Int>, Int>
    ) {
        for ((cell, tv) in cellViews) {
            if (cell !in Sudoku5Precalc.activePositions) {
                tv.text = ""
                continue
            }
            val value = puzzle[cell] ?: current[cell]
            when {
                puzzle.containsKey(cell) -> styleCellAsFixed(tv)
                selectedCell == cell      -> styleCellAsSelected(tv)
                else                      -> styleCellAsEditable(tv)
            }
            tv.text = value?.toString() ?: ""
        }
    }

    private fun onKeypadPressed(digit: Int) {
        val cell = selectedCell ?: return

        if (vm.state.value.puzzle.containsKey(cell)) return
        vm.makeMove(cell, digit)
    }
}
