package com.heptacreation.sumamente.s5e.core

class StepByStepSolver {

    data class Step(
        val cell: Pair<Int, Int>,
        val digit: Int,
        val technique: String,
        val explanation: String
    )

    fun getNextStep(
        puzzle: Map<Pair<Int, Int>, Int>,
        currentState: Map<Pair<Int, Int>, Int>
    ): Step? {
        val board = HashMap<Pair<Int, Int>, Int>(puzzle.size + currentState.size)
        board.putAll(puzzle)
        board.putAll(currentState)

        // Single obvio
        for (cell in Sudoku5Precalc.activePositions) {
            if (cell in board) continue
            val dom = Sudoku5Rules.domainForCell(board, cell)
            if (dom.size == 1) {
                val d = dom[0]
                val exp = "La celda $cell solo admite el dígito $d (todas las otras opciones violan fila/columna/bloque)."
                return Step(cell, d, "single_obvio", exp)
            }
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
                if (count == 1 && unicoCell != null) {
                    val exp = "En el bloque $b, el dígito $d solo puede colocarse en la celda $unicoCell (single oculto de bloque)."
                    return Step(unicoCell, d, "single_oculto", exp)
                }
            }
        }
        return null
    }
}
