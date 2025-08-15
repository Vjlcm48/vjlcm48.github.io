package com.heptacreation.sumamente.s5e.core

class Sudoku5Solver {

    fun solve(puzzle: Map<Pair<Int, Int>, Int>): Map<Pair<Int, Int>, Int>? {
        val engine = SearchEngine()
        val state = BoardState()

        for ((cell, v) in puzzle) {
            if (!state.canPlace(cell, v)) return null
            state.place(cell, v)
        }
        engine.search(maxSolutions = 1, initialState = state)
        return if (state.isComplete()) state.board.toMap() else null
    }

    fun countSolutions(puzzle: Map<Pair<Int, Int>, Int>, limit: Int = 2): Int {
        var count = 0
        val engine = SearchEngine { _ -> count++ }
        val state = BoardState()
        for ((cell, v) in puzzle) {
            if (!state.canPlace(cell, v)) return limit
            state.place(cell, v)
        }
        engine.search(maxSolutions = limit, initialState = state)
        return count
    }

    fun hasUniqueSolution(puzzle: Map<Pair<Int, Int>, Int>): Boolean =
        countSolutions(puzzle, 2) == 1

    fun validateMove(
        puzzle: Map<Pair<Int, Int>, Int>,
        currentState: Map<Pair<Int, Int>, Int>,
        cell: Pair<Int, Int>,
        digit: Int
    ): Boolean {
        val given = puzzle[cell]
        if (given != null) return given == digit

        val board = HashMap<Pair<Int, Int>, Int>(puzzle.size + currentState.size)
        board.putAll(puzzle)
        for ((k, v) in currentState) if (k != cell) board[k] = v

        return Sudoku5Rules.isValidPlacement(board, cell, digit)
    }

    fun generateHint(
        puzzle: Map<Pair<Int, Int>, Int>,
        currentState: Map<Pair<Int, Int>, Int>
    ): Pair<Pair<Int, Int>, Int>? {
        val board = HashMap<Pair<Int, Int>, Int>(puzzle.size + currentState.size)
        board.putAll(puzzle)
        board.putAll(currentState)

        // Single obvio
        for (cell in Sudoku5Precalc.activePositions) {
            if (cell in board) continue
            val dom = Sudoku5Rules.domainForCell(board, cell)
            if (dom.size == 1) return cell to dom[0]
        }

        // Single oculto en bloque
        for (b in 0 until 9) {
            val cells = Sudoku5Precalc.blockCells.getValue(b)
            val candidatesPorCelda = mutableMapOf<Pair<Int, Int>, List<Int>>()
            for (cell in cells) {
                if (cell in board) continue
                val dom = Sudoku5Rules.domainForCell(board, cell)
                if (dom.isNotEmpty()) candidatesPorCelda[cell] = dom
            }
            for (d in 1..5) {
                var unicoCell: Pair<Int, Int>? = null
                var count = 0
                for ((cell, dom) in candidatesPorCelda) {
                    if (d in dom) {
                        count++
                        if (count > 1) break
                        unicoCell = cell
                    }
                }
                if (count == 1 && unicoCell != null) return unicoCell to d
            }
        }

        val solved = solve(puzzle) ?: return null
        for (cell in Sudoku5Precalc.activePositions) {
            if (cell !in board) {
                val d = solved[cell] ?: continue
                return cell to d
            }
        }
        return null
    }

    fun analyzeDifficulty(puzzle: Map<Pair<Int, Int>, Int>): Map<String, Int> {
        val counters = mutableMapOf(
            "single_obvio" to 0,
            "single_oculto" to 0,
            "backtracking" to 0
        )
        val board = HashMap<Pair<Int, Int>, Int>(puzzle)
        var progress: Boolean

        fun singleObvio(): Boolean {
            for (cell in Sudoku5Precalc.activePositions) {
                if (cell in board) continue
                val dom = Sudoku5Rules.domainForCell(board, cell)
                if (dom.size == 1) {
                    board[cell] = dom[0]
                    counters["single_obvio"] = counters.getValue("single_obvio") + 1
                    return true
                }
            }
            return false
        }

        fun singleOculto(): Boolean {
            for (b in 0 until 9) {
                val cells = Sudoku5Precalc.blockCells.getValue(b)
                val candidatesPorCelda = mutableMapOf<Pair<Int, Int>, List<Int>>()
                for (cell in cells) {
                    if (cell in board) continue
                    val dom = Sudoku5Rules.domainForCell(board, cell)
                    if (dom.isNotEmpty()) candidatesPorCelda[cell] = dom
                }
                for (d in 1..5) {
                    var unicoCell: Pair<Int, Int>? = null
                    var count = 0
                    for ((cell, dom) in candidatesPorCelda) {
                        if (d in dom) {
                            count++
                            if (count > 1) break
                            unicoCell = cell
                        }
                    }
                    if (count == 1 && unicoCell != null) {
                        board[unicoCell] = d
                        counters["single_oculto"] = counters.getValue("single_oculto") + 1
                        return true
                    }
                }
            }
            return false
        }

        do {
            progress = false
            if (singleObvio()) { progress = true; continue }
            if (singleOculto()) { progress = true; continue }
        } while (progress)

        if (board.size < Sudoku5Config.TOTAL_ACTIVE_CELLS) counters["backtracking"] = 1
        return counters
    }
}
