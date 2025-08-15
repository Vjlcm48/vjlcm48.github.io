package com.heptacreation.sumamente.s5e.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heptacreation.sumamente.s5e.core.Difficulty
import com.heptacreation.sumamente.s5e.core.PuzzleData
import com.heptacreation.sumamente.s5e.core.Sudoku5Config
import com.heptacreation.sumamente.s5e.service.Cell
import com.heptacreation.sumamente.s5e.service.GameResult
import com.heptacreation.sumamente.s5e.service.Sudoku5Service
import com.heptacreation.sumamente.s5e.service.Sudoku5ServiceImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class GameViewModelS5E(
    private val service: Sudoku5Service = Sudoku5ServiceImpl()
) : ViewModel() {

    data class GameState(
        val loading: Boolean = false,
        val data: PuzzleData? = null,
        val puzzle: Map<Cell, Int> = emptyMap(),
        val current: Map<Cell, Int> = emptyMap(),
        val mistakes: Int = 0,
        val hintsUsed: Int = 0,
        val elapsedMillis: Long = 0L,
        val completed: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state

    private val moveStack = ArrayDeque<Pair<Cell, Int>>()

    private var timerJob: Job? = null
    private var startedAtMs: Long = 0L
    private var pausedAccumulatedMs: Long = 0L
    private var isRunning: Boolean = false

    private var cachedSolution: Map<Cell, Int>? = null


    fun startNewGame(difficulty: Difficulty, seed: String? = null) {
        stopTimer(resetClock = true)
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val data = service.getPuzzle(difficulty, seed)
                cachedSolution = null
                moveStack.clear()
                _state.value = GameState(
                    loading = false,
                    data = data,
                    puzzle = data.puzzle,
                    current = emptyMap(),
                    mistakes = 0,
                    hintsUsed = 0,
                    elapsedMillis = 0L,
                    completed = false,
                    error = null
                )
                startTimer()
            } catch (e: Throwable) {
                _state.value = GameState(error = e.message ?: "Error al generar puzzle")
            }
        }
    }

    fun makeMove(cell: Cell, digit: Int): Boolean {
        val st = _state.value
        if (st.data == null || st.completed) return false
        if (cell in st.puzzle) return false

        val ok = service.validateMove(st.puzzle, st.current, cell, digit)
        if (!ok) {
            _state.value = st.copy(mistakes = st.mistakes + 1)
            return false
        }

        val newCurrent = st.current.toMutableMap().apply { put(cell, digit) }
        moveStack.addLast(cell to digit)


        val done = (st.puzzle.size + newCurrent.size) == Sudoku5Config.TOTAL_ACTIVE_CELLS
        if (done) {

            val solution = cachedSolution ?: service.solve(st.puzzle).also { cachedSolution = it }
            val allMatch = solution != null && solution.all { (k, v) ->
                st.puzzle[k] == v || newCurrent[k] == v
            }
            val completed = allMatch
            if (completed) stopTimer(resetClock = false)

            _state.value = st.copy(
                current = newCurrent,
                completed = completed
            )
        } else {
            _state.value = st.copy(current = newCurrent)
        }
        return true
    }

    fun undo(): Boolean {
        val st = _state.value
        if (st.data == null || st.current.isEmpty()) return false
        val last = moveStack.removeLastOrNull() ?: return false
        val (cell, _) = last
        val newCurrent = st.current.toMutableMap().apply { remove(cell) }
        _state.value = st.copy(current = newCurrent, completed = false)
        if (!isRunning) startTimer()
        return true
    }

    fun useHint(): Boolean {
        val st = _state.value
        if (st.data == null || st.completed) return false
        val hint = service.hint(st.puzzle, st.current) ?: return false
        val (cell, digit) = hint
        val added = makeMove(cell, digit)
        if (added) {
            _state.value = _state.value.copy(hintsUsed = _state.value.hintsUsed + 1)
        }
        return added
    }

    fun giveUpRevealSolution() {
        val st = _state.value
        if (st.data == null) return
        val solution = cachedSolution ?: service.solve(st.puzzle).also { cachedSolution = it }
        if (solution != null) {
            stopTimer(resetClock = false)

            val onlyUserCells = solution.filterKeys { it !in st.puzzle }
            _state.value = st.copy(current = onlyUserCells, completed = false)
        }
    }

    fun clearCell(cell: Cell): Boolean {
        val st = _state.value
        if (st.data == null || st.completed) return false
        if (cell in st.puzzle) return false
        if (cell !in st.current) return false
        val newCurrent = st.current.toMutableMap().apply { remove(cell) }
        _state.value = st.copy(current = newCurrent)
        return true
    }

    fun buildResult(): GameResult? {
        val st = _state.value
        val data = st.data ?: return null
        val elapsed = st.elapsedMillis
        return service.buildGameResult(
            data = data,
            mistakes = st.mistakes,
            hintsUsed = st.hintsUsed,
            elapsedMillis = elapsed,
            completed = st.completed
        )
    }

    private fun startTimer() {
        if (isRunning) return
        isRunning = true
        startedAtMs = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (isRunning) {
                val now = System.currentTimeMillis()
                val elapsed = (now - startedAtMs) + pausedAccumulatedMs
                _state.value = _state.value.copy(elapsedMillis = elapsed)
                delay(1000L)
            }
        }
    }

    private fun stopTimer(resetClock: Boolean) {
        if (isRunning) {
            isRunning = false
            timerJob?.cancel()
            val now = System.currentTimeMillis()
            pausedAccumulatedMs += (now - startedAtMs)
        }
        if (resetClock) {
            pausedAccumulatedMs = 0L
            _state.value = _state.value.copy(elapsedMillis = 0L)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer(resetClock = false)
    }
}
