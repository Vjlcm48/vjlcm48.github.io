package com.heptacreation.sumamente.s5e.core

object Sudoku5Rules {

    private fun lineOkAfter(
        board: Map<Pair<Int, Int>, Int>,
        isRow: Boolean,
        idx: Int,
        pos: Int,
        digit: Int
    ): Boolean {
        val k = if (isRow) Sudoku5Config.ROW_ACTIVE_K[idx] else Sudoku5Config.COL_ACTIVE_K[idx]
        val counts = IntArray(6) // 1..5
        var filled = 0

        if (isRow) {
            val cols = Sudoku5Precalc.rowCells.getValue(idx)
            for (c in cols) {
                val v = if (c == pos) digit else board[idx to c]
                if (v != null) { counts[v]++; filled++ }
            }
        } else {
            val rows = Sudoku5Precalc.colCells.getValue(idx)
            for (r in rows) {
                val v = if (r == pos) digit else board[r to idx]
                if (v != null) { counts[v]++; filled++ }
            }
        }

        if (filled > k) return false
        if (counts.any { it > 2 }) return false

        if (k == 3) {

            return !counts.any { it > 1 }
        }


        val t2 = counts.count { it == 2 }
        if (t2 > 1) return false
        val t1 = counts.count { it == 1 }
        val t0 = 5 - t1 - t2
        val remaining = k - filled
        if (remaining < 0) return false

        if (remaining == 0) {
            return t2 == 1 && t1 == 4
        }

        return if (t2 == 1) {

            val needSingles = 4 - t1
            remaining == needSingles && remaining <= t0
        } else {

            val canPromoteExisting =
                (t1 >= 1) && (remaining - 1) <= t0 && ((t1 - 1) + (remaining - 1) <= 4)

            val canCreateNewPair =
                (t0 >= 1) && (remaining >= 2) &&
                        ((remaining - 2) <= (t0 - 1)) && (t1 + (remaining - 2) <= 4)

            canPromoteExisting || canCreateNewPair
        }
    }

    fun isValidPlacement(
        board: Map<Pair<Int, Int>, Int>,
        cell: Pair<Int, Int>,
        digit: Int
    ): Boolean {
        val (r, c) = cell


        val bidx = Sudoku5Precalc.blockIndexMap.getValue(cell)
        for ((rr, cc) in Sudoku5Precalc.blockCells.getValue(bidx)) {
            if ((rr != r || cc != c) && board[rr to cc] == digit) return false
        }

        // Reglas de fila/columna con k = 3 o 6
        if (!lineOkAfter(board, true, r, c, digit)) return false
        if (!lineOkAfter(board, false, c, r, digit)) return false

        return true
    }

    fun domainForCell(
        board: Map<Pair<Int, Int>, Int>,
        cell: Pair<Int, Int>
    ): List<Int> {
        if (cell !in Sudoku5Precalc.activePositions) return emptyList()
        if (cell in board) return emptyList()
        val (r, c) = cell
        val bidx = Sudoku5Precalc.blockIndexMap.getValue(cell)

        val dom = ArrayList<Int>(5)
        for (d in Sudoku5Config.DIGITS) {
            var ok = true
            for ((rr, cc) in Sudoku5Precalc.blockCells.getValue(bidx)) {
                if (board[rr to cc] == d) { ok = false; break }
            }
            if (!ok) continue

            if (!lineOkAfter(board, true, r, c, d)) continue
            if (!lineOkAfter(board, false, c, r, d)) continue

            dom.add(d)
        }
        return dom
    }

    fun selectUnassignedCellMRV(
        board: Map<Pair<Int, Int>, Int>
    ): Pair<Pair<Int, Int>?, List<Int>> {
        var bestCell: Pair<Int, Int>? = null
        var bestDom: List<Int> = emptyList()

        for (cell in Sudoku5Precalc.activePositions) {
            if (cell in board) continue
            val dom = domainForCell(board, cell)
            if (dom.isEmpty()) return cell to dom
            if (bestCell == null || dom.size < bestDom.size) {
                bestCell = cell
                bestDom = dom
                if (bestDom.size == 1) break
            }
        }
        return bestCell to bestDom
    }
}
