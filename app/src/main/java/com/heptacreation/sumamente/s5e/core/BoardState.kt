package com.heptacreation.sumamente.s5e.core

class BoardState {
    val board: MutableMap<Pair<Int, Int>, Int> = HashMap(Sudoku5Config.TOTAL_ACTIVE_CELLS)

    fun isActive(cell: Pair<Int, Int>): Boolean = cell in Sudoku5Precalc.activePositions
    fun get(cell: Pair<Int, Int>): Int? = board[cell]

    fun place(cell: Pair<Int, Int>, digit: Int) {
        require(isActive(cell)) { "Celda inactiva: $cell" }
        board[cell] = digit
    }

    fun unplace(cell: Pair<Int, Int>) { board.remove(cell) }

    fun canPlace(cell: Pair<Int, Int>, digit: Int): Boolean {
        if (!isActive(cell)) return false
        if (board.containsKey(cell)) return false
        return Sudoku5Rules.isValidPlacement(board, cell, digit)
    }

    fun isComplete(): Boolean = board.size == Sudoku5Config.TOTAL_ACTIVE_CELLS

    fun copyShallow(): BoardState {
        val b = BoardState()
        b.board.putAll(this.board)
        return b
    }
}
