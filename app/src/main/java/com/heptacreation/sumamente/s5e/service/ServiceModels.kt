package com.heptacreation.sumamente.s5e.service

import com.heptacreation.sumamente.s5e.core.Difficulty
import com.heptacreation.sumamente.s5e.core.PuzzleData

typealias Cell = Pair<Int, Int>

data class UserMove(
    val cell: Cell,
    val digit: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameResult(
    val seed: String,
    val difficulty: Difficulty,
    val cluesCount: Int,
    val rating: Double,
    val timeSpentMillis: Long,
    val mistakes: Int,
    val hintsUsed: Int,
    val completed: Boolean,
    val score: Int
)

data class ServiceValidation(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)


fun fromPuzzleData(
    data: PuzzleData,
    timeSpentMillis: Long,
    mistakes: Int,
    hintsUsed: Int,
    completed: Boolean,
    score: Int
): GameResult {
    return GameResult(
        seed = data.seed,
        difficulty = data.difficulty,
        cluesCount = data.cluesCount,
        rating = data.rating,
        timeSpentMillis = timeSpentMillis,
        mistakes = mistakes,
        hintsUsed = hintsUsed,
        completed = completed,
        score = score
    )
}
