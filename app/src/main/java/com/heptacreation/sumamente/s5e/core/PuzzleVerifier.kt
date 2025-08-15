package com.heptacreation.sumamente.s5e.core

object PuzzleVerifier {

    fun verifyPuzzle(puzzle: Map<Pair<Int, Int>, Int>): ValidationResult {
        val errors = mutableListOf<String>()


        for ((cell, v) in puzzle) {
            if (cell !in Sudoku5Precalc.activePositions) errors.add("Pista fuera de máscara: $cell")
            if (v !in 1..5) errors.add("Pista inválida $v en $cell")
        }
        if (errors.isNotEmpty()) return ValidationResult(false, errors)


        val board = HashMap<Pair<Int, Int>, Int>()
        for ((cell, v) in puzzle) {
            if (!Sudoku5Rules.isValidPlacement(board, cell, v)) {
                errors.add("Pista inconsistente: $v en $cell")
            } else {
                board[cell] = v
            }
        }
        if (errors.isNotEmpty()) return ValidationResult(false, errors)


        val solver = Sudoku5Solver()
        val count = solver.countSolutions(puzzle, 2)
        if (count != 1) errors.add("El puzzle no tiene solución única (soluciones=$count)")

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun verifySolution(solution: Map<Pair<Int, Int>, Int>): ValidationResult =
        Sudoku5Validator.validateCompleteSolution(solution)

    fun areEquivalentPuzzles(
        p1: Map<Pair<Int, Int>, Int>,
        p2: Map<Pair<Int, Int>, Int>
    ): Boolean = Sudoku5Canonicalizer.areEquivalent(p1, p2)
}
