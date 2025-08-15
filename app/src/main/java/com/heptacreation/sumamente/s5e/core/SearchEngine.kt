package com.heptacreation.sumamente.s5e.core

class SearchEngine(
    private val onSolution: (Map<Pair<Int, Int>, Int>) -> Unit = {}
) {
    var solutionsFound: Int = 0
        private set

    fun initializeFirstBlock(state: BoardState) {
        val digits = Sudoku5Config.DIGITS
        val blockIndex = 0 // bloque (0,0)
        val cells = Sudoku5Precalc.blockCells.getValue(blockIndex)
        require(cells.size == 5)
        for (i in cells.indices) {
            val cell = cells[i]
            val d = digits[i]
            require(state.canPlace(cell, d)) { "No se pudo anclar el primer bloque en $cell con $d" }
            state.place(cell, d)
        }
    }

    fun search(maxSolutions: Int = Int.MAX_VALUE, initialState: BoardState? = null): Int {
        solutionsFound = 0
        val state = initialState ?: BoardState()
        if (state.board.isEmpty()) initializeFirstBlock(state)
        backtrack(state, maxSolutions)
        return solutionsFound
    }

    private fun backtrack(state: BoardState, maxSolutions: Int) {
        if (solutionsFound >= maxSolutions) return
        if (state.isComplete()) {
            solutionsFound += 1
            onSolution(state.board.toMap())
            return
        }

        val (cell, domain) = Sudoku5Rules.selectUnassignedCellMRV(state.board)
        if (cell == null || domain.isEmpty()) return

        for (d in domain) {
            if (!state.canPlace(cell, d)) continue
            state.place(cell, d)
            backtrack(state, maxSolutions)
            state.unplace(cell)
            if (solutionsFound >= maxSolutions) return
        }
    }
}
