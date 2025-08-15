package com.heptacreation.sumamente.s5e.core

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)

object Sudoku5Validator {

    fun validateCompleteSolution(board: Map<Pair<Int, Int>, Int>): ValidationResult {
        val errors = mutableListOf<String>()

        if (board.size != Sudoku5Config.TOTAL_ACTIVE_CELLS) {
            errors.add("El tablero debe tener exactamente ${Sudoku5Config.TOTAL_ACTIVE_CELLS} celdas llenas; tiene ${board.size}")
        }

        for (b in 0 until 9) errors += validateBlock(board, b)
        for (r in 0 until Sudoku5Config.BOARD_SIZE) errors += validateRow(board, r)
        for (c in 0 until Sudoku5Config.BOARD_SIZE) errors += validateColumn(board, c)

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun validateBlock(board: Map<Pair<Int, Int>, Int>, blockIndex: Int): List<String> {
        val errors = mutableListOf<String>()
        val seen = BooleanArray(6)
        val cells = Sudoku5Precalc.blockCells.getValue(blockIndex)

        for ((r, c) in cells) {
            val v = board[r to c]
            when {
                v == null -> errors.add("Bloque $blockIndex: casilla ($r,$c) vacía")
                v !in 1..5 -> errors.add("Bloque $blockIndex: dígito inválido $v en ($r,$c)")
                seen[v] -> errors.add("Bloque $blockIndex: dígito $v repetido")
                else -> seen[v] = true
            }
        }
        for (d in 1..5) if (!seen[d]) errors.add("Bloque $blockIndex: falta el dígito $d")
        return errors
    }

    private fun validateRow(board: Map<Pair<Int, Int>, Int>, r: Int): List<String> {
        val errors = mutableListOf<String>()
        val cols = Sudoku5Precalc.rowCells.getValue(r)
        val k = Sudoku5Config.ROW_ACTIVE_K[r]
        val counts = IntArray(6)
        var filled = 0

        for (c in cols) {
            val v = board[r to c]
            if (v != null) { counts[v]++; filled++ }
        }
        if (filled != k) {
            errors.add("Fila $r: debe tener $k activas, tiene $filled")
            return errors
        }
        return when (k) {
            3 -> {
                for (d in 1..5) if (counts[d] > 1) errors.add("Fila $r (3 activas): dígito $d repetido")
                if (counts.count { it == 1 } != 3) errors.add("Fila $r (3 activas): deben ser 3 dígitos distintos")
                errors
            }
            6 -> {
                if (counts.any { it > 2 }) errors.add("Fila $r (6 activas): ningún dígito puede aparecer >2")
                val twos = counts.count { it == 2 }
                val ones = counts.count { it == 1 }
                if (twos != 1) errors.add("Fila $r (6 activas): debe haber exactamente 1 par (hay $twos)")
                if (ones != 4) errors.add("Fila $r (6 activas): deben ser 4 dígitos únicos (hay $ones)")
                errors
            }
            else -> listOf("Fila $r: k inesperado = $k")
        }
    }

    private fun validateColumn(board: Map<Pair<Int, Int>, Int>, c: Int): List<String> {
        val errors = mutableListOf<String>()
        val rows = Sudoku5Precalc.colCells.getValue(c)
        val k = Sudoku5Config.COL_ACTIVE_K[c]
        val counts = IntArray(6)
        var filled = 0

        for (r in rows) {
            val v = board[r to c]
            if (v != null) { counts[v]++; filled++ }
        }
        if (filled != k) {
            errors.add("Columna $c: debe tener $k activas, tiene $filled")
            return errors
        }
        return when (k) {
            3 -> {
                for (d in 1..5) if (counts[d] > 1) errors.add("Columna $c (3 activas): dígito $d repetido")
                if (counts.count { it == 1 } != 3) errors.add("Columna $c (3 activas): deben ser 3 dígitos distintos")
                errors
            }
            6 -> {
                if (counts.any { it > 2 }) errors.add("Columna $c (6 activas): ningún dígito puede aparecer >2")
                val twos = counts.count { it == 2 }
                val ones = counts.count { it == 1 }
                if (twos != 1) errors.add("Columna $c (6 activas): debe haber exactamente 1 par (hay $twos)")
                if (ones != 4) errors.add("Columna $c (6 activas): deben ser 4 dígitos únicos (hay $ones)")
                errors
            }
            else -> listOf("Columna $c: k inesperado = $k")
        }
    }
}
