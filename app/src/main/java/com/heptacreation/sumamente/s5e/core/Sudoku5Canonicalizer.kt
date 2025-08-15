package com.heptacreation.sumamente.s5e.core

object Sudoku5Canonicalizer {

    fun transformCoordinate(r: Int, c: Int, k: Int): Pair<Int, Int> = when (k) {
        0 -> r to c
        1 -> c to (8 - r)
        2 -> (8 - r) to (8 - c)
        3 -> (8 - c) to r
        4 -> (8 - r) to c
        5 -> r to (8 - c)
        6 -> c to r
        7 -> (8 - c) to (8 - r)
        else -> r to c
    }

    fun transformBoard(board: Map<Pair<Int, Int>, Int>, k: Int): Map<Pair<Int, Int>, Int> {
        if (k == 0) return board
        val out = HashMap<Pair<Int, Int>, Int>(board.size)
        for ((cell, v) in board) {
            val (r, c) = cell
            val (r2, c2) = transformCoordinate(r, c, k)
            out[r2 to c2] = v
        }
        return out
    }

    fun boardToString(board: Map<Pair<Int, Int>, Int>): String {
        val sb = StringBuilder(81)
        for (r in 0 until Sudoku5Config.BOARD_SIZE) {
            for (c in 0 until Sudoku5Config.BOARD_SIZE) {
                val cell = r to c
                if (cell in Sudoku5Precalc.activePositions) {
                    val v = board[cell]
                    sb.append(if (v == null) '0' else ('0'.code + v).toChar())
                } else {
                    sb.append('.')
                }
            }
        }
        return sb.toString()
    }

    fun getCanonicalString(board: Map<Pair<Int, Int>, Int>): String {
        var best: String? = null
        for (k in 0..7) {
            val transformed = transformBoard(board, k)
            val s = boardToString(transformed)
            if (best == null || s < best) best = s
        }
        return best!!
    }

    fun getCanonicalHash(board: Map<Pair<Int, Int>, Int>): String {
        val s = getCanonicalString(board)
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val digest = md.digest(s.toByteArray(Charsets.UTF_8))

        val take = 16
        val sb = StringBuilder(take * 2)
        for (i in 0 until take) {
            val b = digest[i].toInt() and 0xFF
            sb.append("0123456789abcdef"[b ushr 4])
            sb.append("0123456789abcdef"[b and 0x0F])
        }
        return sb.toString()
    }

    fun areEquivalent(a: Map<Pair<Int, Int>, Int>, b: Map<Pair<Int, Int>, Int>): Boolean {
        return getCanonicalString(a) == getCanonicalString(b)
    }
}

object CanonicalUtils {
    private val names = arrayOf("id", "rot90", "rot180", "rot270", "reflH", "reflV", "diagP", "diagS")
    fun nameOf(k: Int): String = if (k in 0..7) names[k] else "id"
}
