package com.heptacreation.sudoku5

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.EnumMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

enum class Difficulty(val code: String, val displayName: String) {
    PRINCIPIANTE("PRIN", "Principiante"),
    AVANZADO("AVAN", "Avanzado"),
    PRO("PRO", "Pro"),
    HIPER("HIP", "Híper");

    companion object {
        fun fromCode(code: String): Difficulty =
            entries.firstOrNull { it.code == code }
                ?: throw IllegalArgumentException("Código de dificultad inválido: $code")
    }
}

data class DifficultyProfile(
    val name: String,
    val minClues: Int,
    val maxClues: Int,
    val techniques: List<String>,
    val ratingRange: Pair<Double, Double>
)

data class PuzzleData(
    val seed: String,
    val puzzle: Map<Pair<Int, Int>, Int>,
    val solution: Map<Pair<Int, Int>, Int>,
    val difficulty: Difficulty,
    val rating: Double,
    val puzzleHash: String,
    val cluesCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserProgress(
    val seed: String,
    val currentState: Map<Pair<Int, Int>, Int>,
    val mistakes: Int = 0,
    val hintsUsed: Int = 0,
    val timeSpent: Long = 0L,
    val completed: Boolean = false
)

data class UserStats(
    val totalCompleted: Int,
    val completedByDifficulty: Map<Difficulty, Int>,
    val averageTime: Long,
    val totalHintsUsed: Int,
    val lastPlayedAt: Long = 0L
)


class InvalidSeedException(message: String) : RuntimeException(message)
class PuzzleGenerationException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)
class StorageException(message: String) : RuntimeException(message)


object Sudoku5Config {

    const val BOARD_SIZE = 9
    const val BLOCK_SIZE = 3
    const val MAX_DIGIT = 5
    const val TOTAL_ACTIVE_CELLS = 45

    val DIGITS: IntArray = intArrayOf(1, 2, 3, 4, 5)


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


object U {

    fun log(tag: String, msg: String) {
        println("[$tag] $msg")
    }

    inline fun <T> measureMillis(block: () -> T): Pair<T, Long> {
        val start = System.currentTimeMillis()
        val result = block()
        val elapsed = System.currentTimeMillis() - start
        return result to elapsed
    }

    fun normalizeIndex(x: Int, modulo: Int): Int {
        val m = if (modulo <= 0) 1 else modulo
        val nonNeg = x and Int.MAX_VALUE
        return nonNeg % m
    }
}

object Sudoku5Precalc {
    val activePositions: Set<Pair<Int, Int>>
    val blockIndexMap: Map<Pair<Int, Int>, Int>
    val blockCells: Map<Int, List<Pair<Int, Int>>>
    val rowCells: Map<Int, List<Int>>
    val colCells: Map<Int, List<Int>>

    init {
        val actPos = mutableSetOf<Pair<Int, Int>>()
        val bIndex = mutableMapOf<Pair<Int, Int>, Int>()
        val bCells = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()

        for (br in 0 until Sudoku5Config.BLOCK_SIZE) {
            for (bc in 0 until Sudoku5Config.BLOCK_SIZE) {
                val idx = br * Sudoku5Config.BLOCK_SIZE + bc
                val list = mutableListOf<Pair<Int, Int>>()
                for ((dr, dc) in Sudoku5Config.ACTIVE_PATTERN) {
                    val r = br * 3 + dr
                    val c = bc * 3 + dc
                    val cell = r to c
                    actPos.add(cell)
                    bIndex[cell] = idx
                    list.add(cell)
                }
                bCells[idx] = list
            }
        }
        activePositions = actPos
        blockIndexMap = bIndex
        blockCells = bCells

        val rMap = mutableMapOf<Int, MutableList<Int>>()
        val cMap = mutableMapOf<Int, MutableList<Int>>()
        for (r in 0 until Sudoku5Config.BOARD_SIZE) rMap[r] = mutableListOf()
        for (c in 0 until Sudoku5Config.BOARD_SIZE) cMap[c] = mutableListOf()

        for ((r, c) in activePositions) {
            rMap.getValue(r).add(c)
            cMap.getValue(c).add(r)
        }
        rowCells = rMap.mapValues { it.value.sorted() }
        colCells = cMap.mapValues { it.value.sorted() }
    }
}

object Sudoku5Rules {

    fun lineOkAfter(
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

        for (b in 0 until 9) {
            errors += validateBlock(board, b)
        }

        for (r in 0 until Sudoku5Config.BOARD_SIZE) {
            errors += validateRow(board, r)
        }

        for (c in 0 until Sudoku5Config.BOARD_SIZE) {
            errors += validateColumn(board, c)
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun validateBlock(
        board: Map<Pair<Int, Int>, Int>,
        blockIndex: Int
    ): List<String> {
        val errors = mutableListOf<String>()
        val seen = BooleanArray(6)

        val cells = Sudoku5Precalc.blockCells.getValue(blockIndex)
        for ((r, c) in cells) {
            val v = board[r to c]
            if (v == null) {
                errors.add("Bloque $blockIndex: casilla ($r,$c) vacía")
            } else if (v !in 1..5) {
                errors.add("Bloque $blockIndex: dígito inválido $v en ($r,$c)")
            } else if (seen[v]) {
                errors.add("Bloque $blockIndex: dígito $v repetido")
            } else {
                seen[v] = true
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
                if (counts.any { it > 2 }) errors.add("Fila $r (6 activas): ningÃºn dígito puede aparecer >2")
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

    fun transformBoard(
        board: Map<Pair<Int, Int>, Int>,
        k: Int
    ): Map<Pair<Int, Int>, Int> {
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
class DeduplicationManager {
    private val puzzleHashes = LinkedHashSet<String>()
    private val solutionHashes = LinkedHashSet<String>()

    fun tryAddPuzzle(board: Map<Pair<Int, Int>, Int>): Boolean {
        val h = Sudoku5Canonicalizer.getCanonicalHash(board)
        return puzzleHashes.add(h)
    }

    fun isPuzzleDuplicate(board: Map<Pair<Int, Int>, Int>): Boolean {
        val h = Sudoku5Canonicalizer.getCanonicalHash(board)
        return h in puzzleHashes
    }

    fun addPuzzleHash(hash: String): Boolean = puzzleHashes.add(hash)
    fun exportPuzzleHashes(): Set<String> = puzzleHashes.toSet()
    fun importPuzzleHashes(hashes: Collection<String>) {
        puzzleHashes.clear()
        puzzleHashes.addAll(hashes)
    }

    fun puzzleCount(): Int = puzzleHashes.size

    fun tryAddSolution(board: Map<Pair<Int, Int>, Int>): Boolean {
        val h = Sudoku5Canonicalizer.getCanonicalHash(board)
        return solutionHashes.add(h)
    }

    fun solutionCount(): Int = solutionHashes.size
}


object CanonicalUtils {
    private val names = arrayOf(
        "id", "rot90", "rot180", "rot270", "reflH", "reflV", "diagP", "diagS"
    )

    fun nameOf(k: Int): String = if (k in 0..7) names[k] else "id"
}

class BoardState {
    val board: MutableMap<Pair<Int, Int>, Int> = HashMap(Sudoku5Config.TOTAL_ACTIVE_CELLS)

    fun isActive(cell: Pair<Int, Int>): Boolean =
        cell in Sudoku5Precalc.activePositions

    fun get(cell: Pair<Int, Int>): Int? = board[cell]

    fun place(cell: Pair<Int, Int>, digit: Int) {
        require(isActive(cell)) { "Celda inactiva: $cell" }
        board[cell] = digit
    }

    fun unplace(cell: Pair<Int, Int>) {
        board.remove(cell)
    }

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

class SearchEngine(
    private val onSolution: (Map<Pair<Int, Int>, Int>) -> Unit = {}
) {
    var solutionsFound: Int = 0
        private set

    fun initializeFirstBlock(state: BoardState) {

        val digits = Sudoku5Config.DIGITS
        val blockIndex = 0 // (0,0)
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
        if (state.board.isEmpty()) {
            initializeFirstBlock(state)
        }
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
        if (cell == null) return
        if (domain.isEmpty()) return

        for (d in domain) {
            if (!state.canPlace(cell, d)) continue
            state.place(cell, d)
            backtrack(state, maxSolutions)
            state.unplace(cell)
            if (solutionsFound >= maxSolutions) return
        }
    }
}

class FastGenerator(
    seed: Long
) {
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
            val tmp = shuffled[i]
            shuffled[i] = shuffled[j]
            shuffled[j] = tmp
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

class Sudoku5Generator(
    private val dedupe: DeduplicationManager = DeduplicationManager()
) {

    private var rng = java.util.Random()

    fun setSeed(seed: String) {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((Sudoku5Config.DEFAULT_SEED_SALT + "::" + seed).toByteArray(Charsets.UTF_8))

        var acc = 0L
        for (i in 0 until 8) {
            acc = (acc shl 8) or (bytes[i].toLong() and 0xFFL)
        }
        rng = java.util.Random(acc)
    }

    fun generateUniqueSolution(): Map<Pair<Int, Int>, Int> {

        val seedLong = rng.nextLong()
        val fast = FastGenerator(seedLong)
        val solution = fast.generateSolution()

        return solution
    }

    fun createPuzzle(
        solution: Map<Pair<Int, Int>, Int>,
        difficulty: Difficulty
    ): Map<Pair<Int, Int>, Int> {
        val profile = Sudoku5Config.DIFFICULTY_PROFILES.getValue(difficulty)
        val targetClues = randomBetween(profile.minClues, profile.maxClues)
        val puzzle = solution.toMutableMap()

        fun isInFirstBlock(cell: Pair<Int, Int>): Boolean =
            Sudoku5Precalc.blockIndexMap.getValue(cell) == 0

        val enforceRot180 = (difficulty == Difficulty.PRO || difficulty == Difficulty.HIPER)

        val candidates = Sudoku5Precalc.activePositions
            .filter { !isInFirstBlock(it) }
            .shuffled(rng)

        fun rot180Of(cell: Pair<Int, Int>): Pair<Int, Int> {
            val (r, c) = cell
            return (8 - r) to (8 - c)
        }

        var currentClues = puzzle.size
        var i = 0
        val maxAttempts = candidates.size * 5

        while (currentClues > targetClues && i < maxAttempts) {
            i++
            if (candidates.isEmpty()) break
            val cell = candidates[(i - 1) % candidates.size]
            if (cell !in puzzle) continue

            if (!enforceRot180) {

                val backup = puzzle.remove(cell)
                if (backup != null) {
                    if (hasUniqueSolution(puzzle)) {
                        currentClues--
                    } else {
                        puzzle[cell] = backup
                    }
                }
            } else {

                val mate = rot180Of(cell)
                if (mate == cell || mate !in Sudoku5Precalc.activePositions) continue
                val backup1 = puzzle.remove(cell)
                val backup2 = puzzle.remove(mate)
                var removedCount = 0
                if (backup1 != null) removedCount++
                if (backup2 != null) removedCount++

                if (removedCount == 0) continue

                if (hasUniqueSolution(puzzle) && (currentClues - removedCount) >= targetClues) {
                    currentClues -= removedCount
                } else {

                    if (backup1 != null) puzzle[cell] = backup1
                    if (backup2 != null) puzzle[mate] = backup2
                }
            }
        }

        if (currentClues > targetClues) {
            val singles = Sudoku5Precalc.activePositions.shuffled(rng)
            for (cell in singles) {
                if (currentClues <= targetClues) break
                val backup = puzzle.remove(cell) ?: continue
                if (hasUniqueSolution(puzzle)) {
                    currentClues--
                } else {
                    puzzle[cell] = backup
                }
            }
        }

        return if (dedupe.tryAddPuzzle(puzzle)) {
            puzzle.toMap()
        } else {

            val extra = puzzle.toMutableMap()

            val pool = extra.keys.shuffled(rng)
            for (cell in pool) {
                val valBackup = extra[cell] ?: continue
                extra.remove(cell)
                if (hasUniqueSolution(extra) && dedupe.tryAddPuzzle(extra)) {
                    return extra.toMap()
                } else {
                    extra[cell] = valBackup
                }
            }

            puzzle.toMap()
        }
    }

    fun generateFromSeed(seed: String, difficulty: Difficulty): PuzzleData {
        setSeed(seed)

        val solution = generateUniqueSolution()

        val puzzle = createPuzzle(solution, difficulty)

        val puzzleHash = Sudoku5Canonicalizer.getCanonicalHash(puzzle)

        val rating = calculateRating(puzzle, difficulty)

        return PuzzleData(
            seed = seed,
            puzzle = puzzle,
            solution = solution,
            difficulty = difficulty,
            rating = rating,
            puzzleHash = puzzleHash,
            cluesCount = puzzle.size
        )
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
        countSolutions(puzzle, limit = 2) == 1

    private fun calculateRating(
        puzzle: Map<Pair<Int, Int>, Int>,
        difficulty: Difficulty
    ): Double {
        val clues = puzzle.size
        val empty = Sudoku5Config.TOTAL_ACTIVE_CELLS - clues
        val base = 10.0 * (empty.toDouble() / Sudoku5Config.TOTAL_ACTIVE_CELLS.toDouble())

        var blocksWithClues = 0
        for (b in 0 until 9) {
            val cells = Sudoku5Precalc.blockCells.getValue(b)
            if (cells.any { it in puzzle }) blocksWithClues++
        }
        var rowsWith = 0
        for (r in 0 until Sudoku5Config.BOARD_SIZE) {
            if (Sudoku5Precalc.rowCells.getValue(r).any { (r to it) in puzzle }) rowsWith++
        }
        var colsWith = 0
        for (c in 0 until Sudoku5Config.BOARD_SIZE) {
            if (Sudoku5Precalc.colCells.getValue(c).any { (it to c) in puzzle }) colsWith++
        }
        val blockFactor = blocksWithClues / 9.0
        val rowFactor = rowsWith / 9.0
        val colFactor = colsWith / 9.0
        val distribution = 0.4 + (blockFactor * 0.2 + rowFactor * 0.2 + colFactor * 0.2)

        val raw = base * distribution

        val (minR, maxR) = Sudoku5Config.DIFFICULTY_PROFILES.getValue(difficulty).ratingRange
        val norm = minR + (raw / 10.0) * (maxR - minR)
        return kotlin.math.round(norm * 10.0) / 10.0
    }

    private fun randomBetween(a: Int, b: Int): Int {
        val lo = minOf(a, b)
        val hi = maxOf(a, b)
        val span = (hi - lo + 1)
        return lo + rng.nextInt(span)
    }
}

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

    fun countSolutions(
        puzzle: Map<Pair<Int, Int>, Int>,
        limit: Int = 2
    ): Int {
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

        for (cell in Sudoku5Precalc.activePositions) {
            if (cell in board) continue
            val dom = Sudoku5Rules.domainForCell(board, cell)
            if (dom.size == 1) return cell to dom[0]
        }

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


        if (board.size < Sudoku5Config.TOTAL_ACTIVE_CELLS) {
            counters["backtracking"] = 1
        }
        return counters
    }
}

object PuzzleVerifier {

    fun verifyPuzzle(puzzle: Map<Pair<Int, Int>, Int>): ValidationResult {
        val errors = mutableListOf<String>()

        for ((cell, v) in puzzle) {
            if (cell !in Sudoku5Precalc.activePositions) {
                errors.add("Pista fuera de máscara: $cell")
            }
            if (v !in 1..5) {
                errors.add("Pista inválida $v en $cell")
            }
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
        if (count != 1) {
            errors.add("El puzzle no tiene solución unica (soluciones=$count)")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun verifySolution(solution: Map<Pair<Int, Int>, Int>): ValidationResult =
        Sudoku5Validator.validateCompleteSolution(solution)

    fun areEquivalentPuzzles(
        p1: Map<Pair<Int, Int>, Int>,
        p2: Map<Pair<Int, Int>, Int>
    ): Boolean = Sudoku5Canonicalizer.areEquivalent(p1, p2)
}

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

        for (cell in Sudoku5Precalc.activePositions) {
            if (cell in board) continue
            val dom = Sudoku5Rules.domainForCell(board, cell)
            if (dom.size == 1) {
                val d = dom[0]
                val exp = "La celda $cell solo admite el dígito $d (todas las otras opciones violan fila/columna/bloque)."
                return Step(cell, d, "single_obvio", exp)
            }
        }

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

class Sudoku5App(
    globalDedupe: DeduplicationManager = DeduplicationManager()
) {
    private val generator = Sudoku5Generator(dedupe = globalDedupe)
    private val solver = Sudoku5Solver()

    private val playedPuzzleHashes: MutableSet<String> = LinkedHashSet()


    private var totalCompleted: Int = 0
    private val completedByDifficulty: MutableMap<Difficulty, Int> = EnumMap(Difficulty::class.java)
    private var totalTimeMs: Long = 0L
    private var totalHints: Int = 0
    private var lastPlayedAt: Long = 0L

    init {
        Difficulty.entries.forEach { completedByDifficulty[it] = 0 }
    }

    fun getNewPuzzle(difficulty: Difficulty, seed: String? = null): PuzzleData {

        val baseSeed = seed ?: "S5-${difficulty.code}-${System.currentTimeMillis()}"
        var attempt = 0
        while (attempt < 20) {
            val curSeed = if (attempt == 0) baseSeed else "$baseSeed-try$attempt"
            val data = generator.generateFromSeed(curSeed, difficulty)
            val pHash = data.puzzleHash

            val prof = Sudoku5Config.DIFFICULTY_PROFILES.getValue(difficulty)
            val inRange = data.cluesCount in prof.minClues..prof.maxClues

            if (inRange && pHash !in playedPuzzleHashes) {
                return data
            }

            if (pHash !in playedPuzzleHashes) {
                return data
            }
            attempt++
        }

        return generator.generateFromSeed("$baseSeed-final", difficulty)
    }

    fun markPuzzleCompleted(puzzle: PuzzleData, timeSpentMs: Long, hintsUsed: Int = 0) {
        playedPuzzleHashes.add(puzzle.puzzleHash)

        totalCompleted += 1
        completedByDifficulty[puzzle.difficulty] = (completedByDifficulty[puzzle.difficulty] ?: 0) + 1
        totalTimeMs += timeSpentMs
        totalHints += hintsUsed
        lastPlayedAt = System.currentTimeMillis()
    }

    fun getUserStats(): UserStats {
        val avg = if (totalCompleted > 0) totalTimeMs / totalCompleted else 0L
        return UserStats(
            totalCompleted = totalCompleted,
            completedByDifficulty = completedByDifficulty.toMap(),
            averageTime = avg,
            totalHintsUsed = totalHints,
            lastPlayedAt = lastPlayedAt
        )
    }

    fun getPlayedHashes(): Set<String> = playedPuzzleHashes.toSet()
}


object DailyChallengeGenerator {

    private val weeklyCycle = arrayOf(
        Difficulty.PRINCIPIANTE,
        Difficulty.AVANZADO,
        Difficulty.PRO,
        Difficulty.HIPER,
        Difficulty.PRINCIPIANTE,
        Difficulty.AVANZADO,
        Difficulty.PRO
    )

    fun generateDailyChallenge(
        date: String,
        app: Sudoku5App,
        fixedDifficulty: Difficulty? = null
    ): PuzzleData {
        val base = (date.hashCode() and Int.MAX_VALUE)
        val dayIndex = base % 7
        val diff = fixedDifficulty ?: weeklyCycle[dayIndex]
        val seed = "DAILY-$date-$diff-$dayIndex"
        return app.getNewPuzzle(diff, seed = seed)
    }
}

object BoardUtils {

    fun boardToString(board: Map<Pair<Int, Int>, Int>): String {
        val sb = StringBuilder()
        sb.appendLine("┌───┬───┬───┦───┬───┬───┦───┬───┬───┐")
        for (r in 0 until Sudoku5Config.BOARD_SIZE) {
            sb.append("│")
            for (c in 0 until Sudoku5Config.BOARD_SIZE) {
                val cell = r to c
                val value = when (cell) {
                    in board -> " ${board[cell]} "
                    in Sudoku5Precalc.activePositions -> " · "
                    else -> "   "
                }
                sb.append(value)
                when (c) {
                    2, 5 -> sb.append("│")
                    8 -> sb.appendLine("│")
                    else -> sb.append("│")
                }
            }
            when (r) {
                2, 5 -> sb.appendLine("├───┼───┼───╫───┼───┼───╫───┼───┼───┤")
                8 -> sb.appendLine("└───┴───┴───╩───┴───┴───╩───┴───┴───┘")
                else -> sb.appendLine("├───┼───┼───╫───┼───┼───╫───┼───┼───┤")
            }
        }
        return sb.toString()
    }

    fun summary(pd: PuzzleData): String =
        "seed=${pd.seed} diff=${pd.difficulty.code} clues=${pd.cluesCount}/45 hash=${pd.puzzleHash} rating=${pd.rating}"
}

fun main() {

    BoardRender.defaultMode = BoardRender.Mode.ASCII
    println("=== Sudoku-5 :: Smoke Test ===")
    val app = Sudoku5App()

    for (d in Difficulty.entries) {
        val p = app.getNewPuzzle(d)
        println("[NEW] ${BoardUtils.summary(p)}")
        println(BoardRender.toString(p.puzzle))

        S5Log.enabled = true
        Sudoku5Benchmark.run(perDiff = 3)

    }

    val dailyDate = "2025-08-13"
    val daily = DailyChallengeGenerator.generateDailyChallenge(dailyDate, app)
    println("[DAILY] ${BoardUtils.summary(daily)}")

    app.markPuzzleCompleted(daily, timeSpentMs = 3_500L, hintsUsed = 1)
    val stats = app.getUserStats()
    println("[STATS] total=${stats.totalCompleted} avgTime=${stats.averageTime}ms totalHints=${stats.totalHintsUsed}")
    println("Hashes jugados: ${app.getPlayedHashes().size}")

    val sharedDedupe = DeduplicationManager()
    val buffer = PuzzleBuffer(sharedDedupe, replenishTarget = 3)
    buffer.start()

    Thread.sleep(300)

    val fromBuffer = buffer.get(Difficulty.PRINCIPIANTE)
    println("[BUFFER] ${BoardUtils.summary(fromBuffer)}  stock=${buffer.sizeByDifficulty()[Difficulty.PRINCIPIANTE]}")

    buffer.stop()

    val svc = Sudoku5Service(
        ServiceConfig(
            useCatalog = true,
            useBuffer = true,
            replenishTarget = 3
        )
    )
    svc.start()
    val p = svc.getPuzzle(Difficulty.AVANZADO)
    println(svc.render(p.puzzle))
    svc.markCompleted(p, timeSpentMs = 4200, hintsUsed = 0)
    println(svc.getUserStats())
    svc.stop()

}

object TestStorage {
    private val rootDir: File = File(System.getProperty("user.home"), ".sudoku5test").apply { mkdirs() }
    private val hashesFile = File(rootDir, "played_hashes.txt")
    private val puzzlesLog = File(rootDir, "puzzles_log.csv")

    fun savePlayedHashes(hashes: Set<String>) {
        val content = hashes.joinToString(separator = "\n", postfix = if (hashes.isNotEmpty()) "\n" else "")
        Files.write(hashesFile.toPath(), content.toByteArray(Charsets.UTF_8),
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
    }

    fun loadPlayedHashes(): Set<String> {
        if (!hashesFile.exists()) return emptySet()
        val lines = Files.readAllLines(hashesFile.toPath(), Charsets.UTF_8)
        return lines.filter { it.isNotBlank() }.toSet()
    }

    fun appendPuzzleSummary(pd: PuzzleData) {
        if (!puzzlesLog.exists()) {

            val header = "timestamp,seed,difficulty,clues,hash,rating\n"
            Files.write(puzzlesLog.toPath(), header.toByteArray(Charsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        }
        val line = "${pd.timestamp},${pd.seed},${pd.difficulty.code},${pd.cluesCount},${pd.puzzleHash},${pd.rating}\n"
        Files.write(puzzlesLog.toPath(), line.toByteArray(Charsets.UTF_8),
            StandardOpenOption.CREATE, StandardOpenOption.APPEND)
    }

    fun readPuzzleSummaries(): List<String> {
        if (!puzzlesLog.exists()) return emptyList()
        return Files.readAllLines(puzzlesLog.toPath(), Charsets.UTF_8)
    }

    fun clearAll() {
        if (hashesFile.exists()) hashesFile.delete()
        if (puzzlesLog.exists()) puzzlesLog.delete()
    }
}

fun Sudoku5App.persistPlayedHashes() {
    TestStorage.savePlayedHashes(this.getPlayedHashes())
}

fun Sudoku5App.loadPlayedHashesIntoMemory() {
    val loaded = TestStorage.loadPlayedHashes()

    for (h in loaded) {

    }
}

data class RunBudget(
    val maxMillis: Long = 5_000L,
    val maxNodes: Long = Long.MAX_VALUE
)

enum class SearchStatus { SOLVED, TIMEOUT, EXHAUSTED, PARTIAL }

class BudgetedSearchEngine(
    private val budget: RunBudget,
    private val rng: java.util.Random = java.util.Random(),
    private val onSolution: (Map<Pair<Int, Int>, Int>) -> Unit = {}
) {
    private var solutionsFound = 0
    private var nodes = 0L
    private var startNs = 0L

    fun initializeFirstBlock(state: BoardState) {
        val digits = Sudoku5Config.DIGITS
        val cells = Sudoku5Precalc.blockCells.getValue(0)
        for (i in cells.indices) {
            val cell = cells[i]
            val d = digits[i]
            require(state.canPlace(cell, d)) { "No se pudo anclar el primer bloque en $cell con $d" }
            state.place(cell, d)
        }
    }

    fun search(maxSolutions: Int = 1, initialState: BoardState? = null): SearchStatus {
        val state = initialState ?: BoardState()
        if (state.board.isEmpty()) initializeFirstBlock(state)

        solutionsFound = 0
        nodes = 0
        startNs = System.nanoTime()

        val status = backtrack(state, maxSolutions)
        return when {
            status != SearchStatus.SOLVED && solutionsFound > 0 -> SearchStatus.SOLVED
            else -> status
        }
    }

    private fun timedOut(): Boolean =
        (System.nanoTime() - startNs) / 1_000_000L > budget.maxMillis

    private fun exhausted(): Boolean = nodes >= budget.maxNodes

    private fun backtrack(state: BoardState, maxSolutions: Int): SearchStatus {
        if (solutionsFound >= maxSolutions) return SearchStatus.SOLVED
        if (timedOut()) return SearchStatus.TIMEOUT
        if (exhausted()) return SearchStatus.EXHAUSTED

        if (state.isComplete()) {
            solutionsFound += 1
            onSolution(state.board.toMap())
            return SearchStatus.SOLVED
        }

        val (cell, domain) = Sudoku5Rules.selectUnassignedCellMRV(state.board)
        if (cell == null) return SearchStatus.PARTIAL
        if (domain.isEmpty()) return SearchStatus.PARTIAL

        val opts = domain.toMutableList()
        for (i in opts.lastIndex downTo 1) {
            val j = rng.nextInt(i + 1)
            val t = opts[i]; opts[i] = opts[j]; opts[j] = t
        }

        for (d in opts) {
            if (timedOut()) return SearchStatus.TIMEOUT
            if (exhausted()) return SearchStatus.EXHAUSTED

            if (!state.canPlace(cell, d)) continue
            nodes++
            state.place(cell, d)
            val st = backtrack(state, maxSolutions)
            when (st) {
                SearchStatus.SOLVED -> { state.unplace(cell); return SearchStatus.SOLVED }
                SearchStatus.TIMEOUT -> { state.unplace(cell); return SearchStatus.TIMEOUT }
                SearchStatus.EXHAUSTED -> { state.unplace(cell); return SearchStatus.EXHAUSTED }
                else -> { /* sigue */ }
            }
            state.unplace(cell)
        }
        return SearchStatus.PARTIAL
    }
}

class FastGeneratorBudgeted(
    private val seed: Long,
    private val budget: RunBudget
) {
    fun generateSolution(): Map<Pair<Int, Int>, Int> {
        val rng = java.util.Random(seed)
        var solved: Map<Pair<Int, Int>, Int>? = null
        val engine = BudgetedSearchEngine(
            budget = budget,
            rng = rng,
            onSolution = { s -> solved = s }
        )
        val state = BoardState()
        engine.initializeFirstBlock(state)
        val status = engine.search(maxSolutions = 1, initialState = state)
        if (status == SearchStatus.SOLVED && solved != null) return solved
        throw PuzzleGenerationException("No se pudo generar soluciÃ³n dentro del presupuesto: $budget (status=$status)")
    }
}

object SafeSolutionFactory {
    fun generate(
        baseSeed: Long,
        attempts: Int = 3,
        budget: RunBudget = RunBudget(maxMillis = 2_000, maxNodes = 200_000)
    ): Map<Pair<Int, Int>, Int> {
        var curSeed = baseSeed
        repeat(attempts) {
            try {
                return FastGeneratorBudgeted(curSeed, budget).generateSolution()
            } catch (_: PuzzleGenerationException) {
                curSeed = curSeed xor (System.nanoTime() and 0xFFFF)
            }
        }

        return FastGenerator(curSeed).generateSolution()
    }
}

class PuzzleBuffer(
    private val dedupe: DeduplicationManager = DeduplicationManager(),
    private val replenishTarget: Int = 5
) {
    private val generator = Sudoku5Generator(dedupe = dedupe)
    private val queues: EnumMap<Difficulty, java.util.ArrayDeque<PuzzleData>> =
        EnumMap<Difficulty, java.util.ArrayDeque<PuzzleData>>(Difficulty::class.java).apply {
            Difficulty.entries.forEach { d ->
                this[d] = java.util.ArrayDeque<PuzzleData>()
            }
        }

    private val exec = Executors.newSingleThreadExecutor { r ->
        Thread(r, "Sudoku5-PuzzleBuffer").apply { isDaemon = true }
    }
    @Volatile private var running = false

    fun start() {
        if (running) return
        running = true
        exec.submit {
            while (running) {
                var didWork = false
                for (d in Difficulty.entries) {
                    val q = queues.getValue(d)
                    if (q.size < replenishTarget) {

                        val seed = "BUF-${d.code}-${System.nanoTime()}"
                        val pd = generator.generateFromSeed(seed, d)

                        if (q.none { it.puzzleHash == pd.puzzleHash }) {
                            q.addLast(pd)
                            didWork = true
                        }
                    }
                }

                if (!didWork) Thread.sleep(25)
            }
        }
    }

    fun stop() {
        running = false
        exec.shutdown()
        exec.awaitTermination(2, TimeUnit.SECONDS)
    }

    fun clear() {
        queues.values.forEach { it.clear() }
    }

    fun sizeByDifficulty(): Map<Difficulty, Int> =
        Difficulty.entries.associateWith { d -> queues.getValue(d).size }


    fun get(difficulty: Difficulty): PuzzleData {
        val q = queues.getValue(difficulty)
        val polled = q.pollFirst()
        return polled ?: run {
            val seed = "ONDEMAND-${difficulty.code}-${System.nanoTime()}"
            generator.generateFromSeed(seed, difficulty)
        }
    }
}

fun Sudoku5App.withBuffer(
    sharedDedupe: DeduplicationManager = DeduplicationManager(),
    replenishTarget: Int = 5
): Pair<Sudoku5App, PuzzleBuffer> {

    val buffer = PuzzleBuffer(sharedDedupe, replenishTarget)
    buffer.start()
    return this to buffer
}

object BoardRender {

    enum class Mode { ASCII, UNICODE }

    var defaultMode: Mode = Mode.UNICODE

    fun toString(board: Map<Pair<Int, Int>, Int>, mode: Mode = defaultMode): String {
        return when (mode) {
            Mode.ASCII -> toAscii(board)
            Mode.UNICODE -> toUnicode(board)
        }
    }

    private fun toAscii(board: Map<Pair<Int, Int>, Int>): String {
        val sb = StringBuilder()
        fun border() = "+---+---+---+---+---+---+---+---+---+\n"

        for (r in 0 until Sudoku5Config.BOARD_SIZE) {
            sb.append(border())
            sb.append('|')
            for (c in 0 until Sudoku5Config.BOARD_SIZE) {
                val cell = r to c
                val value = when {
                    board.containsKey(cell) -> " ${board[cell]} "
                    cell in Sudoku5Precalc.activePositions -> " . "
                    else -> "   "
                }
                sb.append(value)
                sb.append('|')
            }
            sb.append('\n')
        }
        sb.append(border())
        return sb.toString()
    }

    private fun toUnicode(board: Map<Pair<Int, Int>, Int>): String {
        val sb = StringBuilder()
        sb.appendLine("┌───┬───┬───╦───┬───┬───╦───┬───┬───┐")
        for (r in 0 until Sudoku5Config.BOARD_SIZE) {
            sb.append("│")
            for (c in 0 until Sudoku5Config.BOARD_SIZE) {
                val cell = r to c
                val value = when {
                    board.containsKey(cell) -> " ${board[cell]} "
                    cell in Sudoku5Precalc.activePositions -> " · "
                    else -> "   "
                }
                sb.append(value)
                when (c) {
                    2, 5 -> sb.append("│")
                    8 -> sb.appendLine("│")
                    else -> sb.append("│")
                }
            }
            when (r) {
                2, 5 -> sb.appendLine("├───┼───┼───╫───┼───┼───╫───┼───┼───┤")
                8 -> sb.appendLine("└───┴───┴───╩───┴───┴───╩───┴───┴───┘")
                else -> sb.appendLine("├───┼───┼───╫───┼───┼───╫───┼───┼───┤")
            }
        }
        return sb.toString()
    }

    fun print(board: Map<Pair<Int, Int>, Int>, mode: Mode = defaultMode) {
        println(toString(board, mode))
    }
}

class Sudoku5BasicTests {

    @Test(timeout = 15_000)
    fun generate_and_verify_per_difficulty() {
        val dedupe = DeduplicationManager()
        val gen = Sudoku5Generator(dedupe)

        for (d in Difficulty.entries) {
            val seed = "TEST-${d.code}-${System.nanoTime()}"
            val pd = gen.generateFromSeed(seed, d)

            assertTrue(
                "Clues inválidos (0..${Sudoku5Config.TOTAL_ACTIVE_CELLS}) para $d: ${pd.cluesCount}",
                pd.cluesCount in 1..Sudoku5Config.TOTAL_ACTIVE_CELLS
            )
            assertEquals(
                "La solución no tiene el tamaño esperado",
                Sudoku5Config.TOTAL_ACTIVE_CELLS,
                pd.solution.size
            )

            val prof = Sudoku5Config.DIFFICULTY_PROFILES.getValue(d)

            assertTrue(
                "Clues fuera de rango para $d: ${pd.cluesCount} (esperado ${prof.minClues}..${prof.maxClues})",
                pd.cluesCount in prof.minClues..prof.maxClues
            )

            val (minR, maxR) = prof.ratingRange
            assertTrue(
                "Rating fuera de rango para $d: ${"%.2f".format(pd.rating)} (esperado ${"%.1f".format(minR)}..${"%.1f".format(maxR)})",
                pd.rating in minR..maxR
            )

            val vr = PuzzleVerifier.verifyPuzzle(pd.puzzle)
            assertTrue("Puzzle inválido para $d: ${vr.errors}", vr.isValid)

            val solver = Sudoku5Solver()
            val solved = solver.solve(pd.puzzle)
            assertNotNull("Solver no encontró solución para $d", solved)
            assertEquals(
                "La solución del solver no coincide en tamaño",
                pd.solution.size,
                solved!!.size
            )

            val full = Sudoku5Validator.validateCompleteSolution(solved)
            assertTrue("Solución inválida para $d: ${full.errors}", full.isValid)

            val pct = (pd.cluesCount * 100.0 / Sudoku5Config.TOTAL_ACTIVE_CELLS)
            println(
                "[CHECK] $d clues=${pd.cluesCount}/${Sudoku5Config.TOTAL_ACTIVE_CELLS} " +
                        "(~${"%.1f".format(pct)}%) rating=${"%.2f".format(pd.rating)}"
            )
        }
    }

    @Test(timeout = 8_000)
    fun canonical_equivalence_under_D8() {
        val gen = Sudoku5Generator(DeduplicationManager())
        val pd = gen.generateFromSeed("TEST-CANON-${System.nanoTime()}", Difficulty.PRINCIPIANTE)

        val rot180 = Sudoku5Canonicalizer.transformBoard(pd.puzzle, 2)
        val h1 = Sudoku5Canonicalizer.getCanonicalHash(pd.puzzle)
        val h2 = Sudoku5Canonicalizer.getCanonicalHash(rot180)

        assertEquals("La canónica no es estable bajo D8", h1, h2)
        assertTrue(
            "areEquivalent debería ser true para transformaciones D8",
            Sudoku5Canonicalizer.areEquivalent(pd.puzzle, rot180)
        )
    }

    @Test(timeout = 5_000)
    fun daily_challenge_is_deterministic_for_date() {
        val app = Sudoku5App()
        val date = "2025-08-13"
        val p1 = DailyChallengeGenerator.generateDailyChallenge(date, app)
        val app2 = Sudoku5App()
        val p2 = DailyChallengeGenerator.generateDailyChallenge(date, app2)

        assertEquals(p1.puzzleHash, p2.puzzleHash)
    }

    @Test(timeout = 3_000)
    fun render_ascii_and_unicode_should_not_throw() {
        val gen = Sudoku5Generator(DeduplicationManager())
        val pd = gen.generateFromSeed("TEST-RENDER-${System.nanoTime()}", Difficulty.AVANZADO)

        BoardRender.defaultMode = BoardRender.Mode.ASCII
        val ascii = BoardRender.toString(pd.puzzle)
        assertTrue(ascii.contains("+---+"))

        BoardRender.defaultMode = BoardRender.Mode.UNICODE
        val uni = BoardRender.toString(pd.puzzle)
        assertTrue(uni.contains("â•”") || uni.contains("â•‘") || uni.contains("â•š"))
    }
}

object S5Log {
    var enabled: Boolean = false
    fun d(tag: String, msg: String) {
        if (enabled) println("[$tag] $msg")
    }
}

object Profiler {
    inline fun <T> measureMillis(name: String = "", block: () -> T): Pair<T, Long> {
        val t0 = System.nanoTime()
        val result = block()
        val dtMs = ((System.nanoTime() - t0) / 1_000_000.0).roundToLong()
        if (name.isNotEmpty()) S5Log.d("PROFILE", "$name = ${dtMs}ms")
        return result to dtMs
    }
}

data class TimeStats(
    val n: Int,
    val min: Long,
    val p50: Long,
    val mean: Long,
    val p95: Long,
    val max: Long
)

fun computeStats(timesMs: List<Long>): TimeStats {
    require(timesMs.isNotEmpty())
    val n = timesMs.size
    val sorted = timesMs.sorted()
    val min = sorted.first()
    val max = sorted.last()
    val p50 = sorted[(0.5 * (n - 1)).toInt()]
    val p95 = sorted[(0.95 * (n - 1)).toInt()]
    val mean = (timesMs.sum().toDouble() / n).roundToLong()
    return TimeStats(n, min, p50, mean, p95, max)
}

object Sudoku5Benchmark {

    data class Result(
        val byDifficulty: Map<Difficulty, TimeStats>,
        val overall: TimeStats
    )

    fun run(perDiff: Int = 3, seedBase: Long = System.currentTimeMillis()): Result {
        require(perDiff >= 1) { "perDiff debe ser >= 1" }

        val allTimes = mutableListOf<Long>()
        val mapTimes = mutableMapOf<Difficulty, MutableList<Long>>()
        val dedupe = DeduplicationManager()
        val gen = Sudoku5Generator(dedupe)

        for (d in Difficulty.entries) {
            val list = mutableListOf<Long>()
            mapTimes[d] = list

            repeat(perDiff) { i ->
                val seed = "BM-${d.code}-${seedBase + i}"
                val (_, dt) = Profiler.measureMillis("gen:$d#$i") {
                    gen.generateFromSeed(seed, d)
                }
                list += dt
                allTimes += dt
            }
        }

        val byDiffStats = mapTimes.mapValues { (_, times) -> computeStats(times) }
        val overall = computeStats(allTimes)

        printReport(byDiffStats, overall)
        return Result(byDiffStats, overall)
    }

    private fun printReport(byDiff: Map<Difficulty, TimeStats>, overall: TimeStats) {
        println("\n=== Sudoku-5 :: Benchmark de generación ===")
        for (d in Difficulty.entries) {
            val s = byDiff.getValue(d)
            println(
                String.format(
                    "%-12s n=%d  min=%dms  p50=%dms  mean=%dms  p95=%dms  max=%dms",
                    d.name.lowercase().replaceFirstChar { it.uppercase() },
                    s.n, s.min, s.p50, s.mean, s.p95, s.max
                )
            )
        }
        println(
            String.format(
                "TOTAL        n=%d  min=%dms  p50=%dms  mean=%dms  p95=%dms  max=%dms",
                overall.n, overall.min, overall.p50, overall.mean, overall.p95, overall.max
            )
        )
        println("===========================================\n")
    }
}

private fun exportPuzzleLocal(puzzle: Map<Pair<Int, Int>, Int>): String {
    val sb = StringBuilder(81)
    for (r in 0 until Sudoku5Config.BOARD_SIZE) {
        for (c in 0 until Sudoku5Config.BOARD_SIZE) {
            val cell = r to c
            if (cell in Sudoku5Precalc.activePositions) {
                val v = puzzle[cell]
                sb.append(if (v != null) ('0' + v) else '0')
            } else {
                sb.append('-')
            }
        }
    }
    return sb.toString()
}


data class CatalogEntry(
    val seed: String,
    val difficulty: Difficulty,
    val puzzleStr: String,
    val clues: Int,
    val rating: Double,
    val puzzleHash: String,
    val solutionHash: String,
    val timestamp: Long
)

object CatalogCSV {
    private const val HEADER =
        "seed,difficulty,puzzleStr,clues,rating,puzzleHash,solutionHash,timestamp"

    fun write(file: File, entries: List<CatalogEntry>, append: Boolean = false) {
        val writeHeader = !append || !file.exists() || file.length() == 0L
        val sb = StringBuilder()
        if (writeHeader) sb.appendLine(HEADER)
        for (e in entries) {
            sb.append(e.seed).append(',')
                .append(e.difficulty.code).append(',')
                .append(e.puzzleStr).append(',')
                .append(e.clues).append(',')
                .append(e.rating).append(',')
                .append(e.puzzleHash).append(',')
                .append(e.solutionHash).append(',')
                .append(e.timestamp)
                .append('\n')
        }
        Files.write(
            file.toPath(),
            sb.toString().toByteArray(Charsets.UTF_8),
            StandardOpenOption.CREATE,
            if (append && file.exists()) StandardOpenOption.APPEND else StandardOpenOption.TRUNCATE_EXISTING
        )
    }

    fun read(file: File): List<CatalogEntry> {
        if (!file.exists()) return emptyList()
        val lines = Files.readAllLines(file.toPath(), Charsets.UTF_8)
        if (lines.isEmpty()) return emptyList()
        val result = mutableListOf<CatalogEntry>()
        for ((i, raw) in lines.withIndex()) {
            if (i == 0 && raw.startsWith("seed,")) continue // header
            if (raw.isBlank()) continue
            val parts = raw.split(',')
            if (parts.size < 8) continue
            val diff = Difficulty.fromCode(parts[1])
            result += CatalogEntry(
                seed = parts[0],
                difficulty = diff,
                puzzleStr = parts[2],
                clues = parts[3].toInt(),
                rating = parts[4].toDouble(),
                puzzleHash = parts[5],
                solutionHash = parts[6],
                timestamp = parts[7].toLong()
            )
        }
        return result
    }
}


class CatalogManager(
    private val file: File?,
    private val dedupe: DeduplicationManager = DeduplicationManager(),
    private val generator: Sudoku5Generator = Sudoku5Generator(dedupe)
) {
    private val entriesByDiff: EnumMap<Difficulty, java.util.ArrayDeque<CatalogEntry>> =
        EnumMap<Difficulty, java.util.ArrayDeque<CatalogEntry>>(Difficulty::class.java).apply {
            Difficulty.entries.forEach { d -> this[d] = java.util.ArrayDeque() }
        }

    private val seenPuzzleHashes = java.util.HashSet<String>()

    init {

        val loaded = if (file != null && file.exists()) CatalogCSV.read(file) else emptyList()
        for (e in loaded) {

            if (seenPuzzleHashes.add(e.puzzleHash)) {
                entriesByDiff.getValue(e.difficulty).addLast(e)
            }

        }
    }

    fun sizeByDifficulty(): Map<Difficulty, Int> =
        Difficulty.entries.associateWith { d -> entriesByDiff.getValue(d).size }

    fun hasStock(d: Difficulty): Boolean = entriesByDiff.getValue(d).isNotEmpty()

    fun next(
        difficulty: Difficulty,
        fallbackOnDemand: Boolean = true
    ): PuzzleData {
        val q = entriesByDiff.getValue(difficulty)
        val e = q.pollFirst()
        if (e != null) {

            return generator.generateFromSeed(e.seed, difficulty)
        }
        if (fallbackOnDemand) {
            val seed = "CAT-FB-${difficulty.code}-${System.nanoTime()}"
            return generator.generateFromSeed(seed, difficulty)
        }
        throw IllegalStateException("Catálogo vacío para $difficulty y fallback desactivado")
    }


    fun add(pd: PuzzleData, persist: Boolean = true): Boolean {
        val diff = pd.difficulty
        val profile = Sudoku5Config.DIFFICULTY_PROFILES.getValue(diff)
        if (pd.cluesCount !in profile.minClues..profile.maxClues) return false

        val entry = CatalogEntry(
            seed = pd.seed,
            difficulty = diff,
            puzzleStr = exportPuzzleLocal(pd.puzzle),
            clues = pd.cluesCount,
            rating = pd.rating,
            puzzleHash = pd.puzzleHash,
            solutionHash = Sudoku5Canonicalizer.getCanonicalHash(pd.solution),
            timestamp = pd.timestamp
        )

        if (!seenPuzzleHashes.add(entry.puzzleHash)) return false
        entriesByDiff.getValue(diff).addLast(entry)

        if (persist && file != null) {
            CatalogCSV.write(file, listOf(entry), append = true)
        }
        return true
    }
}

object CatalogBuilder {

    data class BuildConfig(
        val perDifficulty: Int = 200,
        val allowSymmetry: Boolean = true,
        val seedBase: Long = System.currentTimeMillis()
    )

    fun build(outFile: File, cfg: BuildConfig = BuildConfig()) {
        val seen = java.util.HashSet<String>()
        val gen = Sudoku5Generator(DeduplicationManager())
        val all = mutableListOf<CatalogEntry>()

        for (d in Difficulty.entries) {
            var accepted = 0
            var attempt = 0
            val profile = Sudoku5Config.DIFFICULTY_PROFILES.getValue(d)

            while (accepted < cfg.perDifficulty && attempt < cfg.perDifficulty * 20) {
                val seed = "CAT-${d.code}-${cfg.seedBase + attempt}"
                val pd = gen.generateFromSeed(seed, d)

                val inRange = pd.cluesCount in profile.minClues..profile.maxClues
                if (inRange) {
                    val e = CatalogEntry(
                        seed = pd.seed,
                        difficulty = d,
                        puzzleStr = exportPuzzleLocal(pd.puzzle),
                        clues = pd.cluesCount,
                        rating = pd.rating,
                        puzzleHash = pd.puzzleHash,
                        solutionHash = Sudoku5Canonicalizer.getCanonicalHash(pd.solution),
                        timestamp = pd.timestamp
                    )
                    if (seen.add(e.puzzleHash)) {
                        all += e
                        accepted++
                    }
                }
                attempt++
            }
        }

        CatalogCSV.write(outFile, all, append = false)
    }
}

data class ServiceConfig(
    val useCatalog: Boolean = true,
    val useBuffer: Boolean = true,
    val replenishTarget: Int = 5,
    val catalogFile: File? = defaultCatalogFile(),
    val fallbackOnDemand: Boolean = true
)

fun defaultCatalogFile(): File =
    File(System.getProperty("user.home"), ".sudoku5test/catalog.csv")


class Sudoku5Service(private val cfg: ServiceConfig = ServiceConfig()) {

    private val sharedDedupe = DeduplicationManager()

    private val app = Sudoku5App(sharedDedupe)

    private val catalogManager: CatalogManager? =
        if (cfg.useCatalog && cfg.catalogFile != null) CatalogManager(cfg.catalogFile, sharedDedupe)
        else null

    private val buffer: PuzzleBuffer? =
        if (cfg.useBuffer) PuzzleBuffer(sharedDedupe, cfg.replenishTarget) else null

    fun start() {
        buffer?.start()
    }

    fun stop() {
        buffer?.stop()
    }

    fun bufferSizes(): Map<Difficulty, Int> =
        buffer?.sizeByDifficulty() ?: Difficulty.entries.associateWith { 0 }

    fun getPuzzle(difficulty: Difficulty): PuzzleData {

        catalogManager?.let { cm ->
            if (cm.hasStock(difficulty)) {
                return cm.next(difficulty, fallbackOnDemand = cfg.fallbackOnDemand)
            }
        }

        buffer?.let { buf ->
            val stock = buf.sizeByDifficulty()[difficulty] ?: 0
            if (stock > 0) return buf.get(difficulty)
        }

        return app.getNewPuzzle(difficulty)
    }

    fun getDailyChallenge(date: String): PuzzleData =
        DailyChallengeGenerator.generateDailyChallenge(date, app)

    fun markCompleted(puzzle: PuzzleData, timeSpentMs: Long, hintsUsed: Int = 0) {
        app.markPuzzleCompleted(puzzle, timeSpentMs, hintsUsed)

        TestStorage.appendPuzzleSummary(puzzle)

    }

    fun getUserStats(): UserStats = app.getUserStats()

    fun render(puzzle: Map<Pair<Int, Int>, Int>): String =
        BoardRender.toString(puzzle)


    fun warmup(minStock: Int = 3, timeoutMs: Long = 3_000L): Boolean {
        val start = System.nanoTime()
        if (buffer == null) return false
        while (true) {
            val ok = Difficulty.entries.toTypedArray().all { d ->
                (buffer.sizeByDifficulty()[d] ?: 0) >= minStock
            }
            if (ok) return true
            val elapsedMs = (System.nanoTime() - start) / 1_000_000L
            if (elapsedMs > timeoutMs) return false
            try { Thread.sleep(25) } catch (_: InterruptedException) { return false }
        }
    }
}

object S5FeatureFlags {

    var forceAsciiRender: Boolean = true

    var enableBenchmarks: Boolean = false

    var preferCatalog: Boolean = true
    var preferBuffer: Boolean = true
    var bufferTarget: Int = 5
}

fun applyS5FeatureFlags() {
    BoardRender.defaultMode =
        if (S5FeatureFlags.forceAsciiRender) BoardRender.Mode.ASCII
        else BoardRender.Mode.UNICODE
    S5Log.enabled = S5FeatureFlags.enableBenchmarks
}


@Suppress("unused", "UNUSED_EXPRESSION")
private object KeepS5LogProfilerBenchmark {

    init {

        Profiler.measureMillis("noop") { 0 }

        Sudoku5Benchmark
        computeStats(listOf(1L, 2L, 3L))
    }
}

@Suppress("unused")
private object KeepBufferCatalogService {

    init {

        val dummyDedupe = DeduplicationManager()
        val dummyBuf = PuzzleBuffer(dummyDedupe, replenishTarget = 1)
        val dummySvc = Sudoku5Service(

            ServiceConfig(
                useCatalog = S5FeatureFlags.preferCatalog,
                useBuffer = S5FeatureFlags.preferBuffer,
                replenishTarget = S5FeatureFlags.bufferTarget,
                catalogFile = defaultCatalogFile()
            )
        )

        if (dummyBuf.sizeByDifficulty().isEmpty()) { /* no-op */ }
        if (dummySvc.bufferSizes().isEmpty()) { /* no-op */ }

    }
}

@Suppress("unused", "UNUSED_EXPRESSION")
private object KeepBudgetedSearchSafeFactory {

    init {

        RunBudget()
        SafeSolutionFactory
        BudgetedSearchEngine(RunBudget()) { _ -> }
        FastGeneratorBudgeted(seed = 0L, budget = RunBudget(maxMillis = 1))
    }
}

@Suppress("unused", "UNUSED_EXPRESSION")
private object KeepRenderTestStorageCatalog {

    init {

        BoardRender.Mode.ASCII
        TestStorage.readPuzzleSummaries()

        CatalogCSV
        CatalogBuilder
        CatalogManager(File(System.getProperty("user.home"), ".sudoku5test/catalog.csv"))
    }
}

