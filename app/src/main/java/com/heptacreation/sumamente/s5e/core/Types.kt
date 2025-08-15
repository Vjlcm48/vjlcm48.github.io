package com.heptacreation.sumamente.s5e.core

enum class Difficulty(val code: String, val displayName: String) {
    PRINCIPIANTE("PRIN", "Principiante"),
    AVANZADO("AVAN", "Avanzado"),
    PRO("PRO", "Pro"),
    HIPER("HIP", "Híper");

    companion object {
        fun fromCode(code: String): Difficulty =
            entries.firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("Código de dificultad inválido: $code")
    }
}

data class DifficultyProfile(
    val name: String,
    val minClues: Int,
    val maxClues: Int,
    val techniques: List<String>,
    val ratingRange: Pair<Double, Double>
)

data class PuzzleData(
    val seed: String,
    val puzzle: Map<Pair<Int, Int>, Int>,
    val solution: Map<Pair<Int, Int>, Int>,
    val difficulty: Difficulty,
    val rating: Double,
    val puzzleHash: String,
    val cluesCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserProgress(
    val seed: String,
    val currentState: Map<Pair<Int, Int>, Int>,
    val mistakes: Int = 0,
    val hintsUsed: Int = 0,
    val timeSpent: Long = 0L,
    val completed: Boolean = false
)

data class UserStats(
    val totalCompleted: Int,
    val completedByDifficulty: Map<Difficulty, Int>,
    val averageTime: Long,
    val totalHintsUsed: Int,
    val lastPlayedAt: Long = 0L
)

class InvalidSeedException(message: String) : RuntimeException(message)
class PuzzleGenerationException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)
class StorageException(message: String) : RuntimeException(message)
