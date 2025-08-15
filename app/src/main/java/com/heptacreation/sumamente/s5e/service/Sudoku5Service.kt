package com.heptacreation.sumamente.s5e.service

import com.heptacreation.sumamente.s5e.core.Difficulty
import com.heptacreation.sumamente.s5e.core.PuzzleData


interface Sudoku5Service {

    fun getPuzzle(difficulty: Difficulty, seed: String? = null): PuzzleData

    fun validateMove(
        puzzle: Map<Cell, Int>,
        currentState: Map<Cell, Int>,
        cell: Cell,
        digit: Int
    ): Boolean

    fun hint(
        puzzle: Map<Cell, Int>,
        currentState: Map<Cell, Int>
    ): Pair<Cell, Int>?

    fun solve(puzzle: Map<Cell, Int>): Map<Cell, Int>?

    fun countSolutions(puzzle: Map<Cell, Int>, limit: Int = 2): Int

    fun verifyPuzzle(puzzle: Map<Cell, Int>): ServiceValidation

    fun difficultyBreakdown(puzzle: Map<Cell, Int>): Map<String, Int>

    fun buildGameResult(
        data: PuzzleData,
        mistakes: Int,
        hintsUsed: Int,
        elapsedMillis: Long,
        completed: Boolean
    ): GameResult
}
