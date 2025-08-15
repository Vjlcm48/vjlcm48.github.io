package com.heptacreation.sumamente.s5e.core

class FastGenerator(seed: Long) {
    private val rng = java.util.Random(seed)

    fun generateSolution(): Map<Pair<Int, Int>, Int> {
        val engine = SearchEngine()
        val state = BoardState()
        engine.initializeFirstBlock(state)

        solveRandomized(state)
        require(state.isComplete()) { "No se pudo generar una solución rápida" }
        return state.board.toMap()
    }

    private fun solveRandomized(state: BoardState): Boolean {
        if (state.isComplete()) return true
        val (cell, domain) = Sudoku5Rules.selectUnassignedCellMRV(state.board)
        if (cell == null) return true
        if (domain.isEmpty()) return false

        val shuffled = domain.toMutableList()
        for (i in shuffled.lastIndex downTo 1) {
            val j = rng.nextInt(i + 1)
            val tmp = shuffled[i]; shuffled[i] = shuffled[j]; shuffled[j] = tmp
        }

        for (d in shuffled) {
            if (!state.canPlace(cell, d)) continue
            state.place(cell, d)
            if (solveRandomized(state)) return true
            state.unplace(cell)
        }
        return false
    }
}
