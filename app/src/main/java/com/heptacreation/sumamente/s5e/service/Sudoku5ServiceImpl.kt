package com.heptacreation.sumamente.s5e.service

import com.heptacreation.sumamente.s5e.core.Difficulty
import com.heptacreation.sumamente.s5e.core.PuzzleVerifier
import com.heptacreation.sumamente.s5e.core.Sudoku5Generator
import com.heptacreation.sumamente.s5e.core.Sudoku5Solver

class Sudoku5ServiceImpl : Sudoku5Service {

    private val generator = Sudoku5Generator()
    private val solver = Sudoku5Solver()

    override fun getPuzzle(difficulty: Difficulty, seed: String?): com.heptacreation.sumamente.s5e.core.PuzzleData {
        val useSeed = seed ?: System.currentTimeMillis().toString()
        return generator.generateFromSeed(useSeed, difficulty)
    }

    override fun validateMove(
        puzzle: Map<Cell, Int>,
        currentState: Map<Cell, Int>,
        cell: Cell,
        digit: Int
    ): Boolean {
        return solver.validateMove(puzzle, currentState, cell, digit)
    }

    override fun hint(
        puzzle: Map<Cell, Int>,
        currentState: Map<Cell, Int>
    ): Pair<Cell, Int>? {
        return solver.generateHint(puzzle, currentState)
    }

    override fun solve(puzzle: Map<Cell, Int>): Map<Cell, Int>? {
        return solver.solve(puzzle)
    }

    override fun countSolutions(puzzle: Map<Cell, Int>, limit: Int): Int {
        return solver.countSolutions(puzzle, limit)
    }

    override fun verifyPuzzle(puzzle: Map<Cell, Int>): ServiceValidation {
        val vr = PuzzleVerifier.verifyPuzzle(puzzle)
        return ServiceValidation(vr.isValid, vr.errors)
    }

    override fun difficultyBreakdown(puzzle: Map<Cell, Int>): Map<String, Int> {
        return solver.analyzeDifficulty(puzzle)
    }

    override fun buildGameResult(
        data: com.heptacreation.sumamente.s5e.core.PuzzleData,
        mistakes: Int,
        hintsUsed: Int,
        elapsedMillis: Long,
        completed: Boolean
    ): GameResult {

        val base = when (data.difficulty) {
            Difficulty.PRINCIPIANTE -> 100
            Difficulty.AVANZADO     -> 200
            Difficulty.PRO          -> 300
            Difficulty.HIPER        -> 400
        }

        val timePenalty     = (elapsedMillis / 1000L / 15L).toInt()
        val mistakePenalty  = mistakes * 5
        val hintPenalty     = hintsUsed * 8
        val completionBonus = if (completed) 50 else 0

        val raw   = base + completionBonus - timePenalty - mistakePenalty - hintPenalty
        val score = raw.coerceAtLeast(0)

        return fromPuzzleData(
            data = data,
            timeSpentMillis = elapsedMillis,
            mistakes = mistakes,
            hintsUsed = hintsUsed,
            completed = completed,
            score = score
        )
    }
}
