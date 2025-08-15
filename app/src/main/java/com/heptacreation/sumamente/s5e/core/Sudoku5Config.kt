package com.heptacreation.sumamente.s5e.core

object Sudoku5Config {
    const val BOARD_SIZE = 9
    const val BLOCK_SIZE = 3
    const val MAX_DIGIT = 5
    const val TOTAL_ACTIVE_CELLS = 45

    val DIGITS: IntArray = intArrayOf(1, 2, 3, 4, 5)

    // Patrón de celdas activas dentro de cada bloque 3x3 (forma de cruz 5-celdas)
    val ACTIVE_PATTERN: List<Pair<Int, Int>> = listOf(
        0 to 0,
        0 to 2,
        1 to 1,
        2 to 0,
        2 to 2
    )


    val ROW_ACTIVE_K: IntArray = intArrayOf(6, 3, 6, 6, 3, 6, 6, 3, 6)
    val COL_ACTIVE_K: IntArray = intArrayOf(6, 3, 6, 6, 3, 6, 6, 3, 6)


    val DIFFICULTY_PROFILES: Map<Difficulty, DifficultyProfile> = mapOf(
        Difficulty.PRINCIPIANTE to DifficultyProfile(
            name = "Principiante",
            minClues = 30,
            maxClues = 35,
            techniques = listOf("singles_obvios", "singles_ocultos"),
            ratingRange = 1.0 to 3.0
        ),
        Difficulty.AVANZADO to DifficultyProfile(
            name = "Avanzado",
            minClues = 24,
            maxClues = 29,
            techniques = listOf("singles", "pares_desnudos", "intersecciones"),
            ratingRange = 3.1 to 5.0
        ),
        Difficulty.PRO to DifficultyProfile(
            name = "Pro",
            minClues = 18,
            maxClues = 23,
            techniques = listOf("singles", "pares", "ternas", "pointing"),
            ratingRange = 5.1 to 7.0
        ),
        Difficulty.HIPER to DifficultyProfile(
            name = "Híper",
            minClues = 12,
            maxClues = 17,
            techniques = listOf("todas_tecnicas", "backtracking_minimo"),
            ratingRange = 7.1 to 10.0
        )
    )

    const val DEFAULT_SEED_SALT: String = "Sudoku5::SeedSalt"
}
