package com.heptacreation.sumamente.s5e.core

class Sudoku5Generator(
    private val dedupe: DeduplicationManager = DeduplicationManager()
) {
    private var rng = java.util.Random()

    fun setSeed(seed: String) {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((Sudoku5Config.DEFAULT_SEED_SALT + "::" + seed).toByteArray(Charsets.UTF_8))

        var acc = 0L
        for (i in 0 until 8) acc = (acc shl 8) or (bytes[i].toLong() and 0xFFL)
        rng = java.util.Random(acc)
    }

    fun generateUniqueSolution(): Map<Pair<Int, Int>, Int> {
        val seedLong = rng.nextLong()
        val fast = FastGenerator(seedLong)
        return fast.generateSolution()
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
                    if (hasUniqueSolution(puzzle)) currentClues--
                    else puzzle[cell] = backup
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
                if (hasUniqueSolution(puzzle)) currentClues--
                else puzzle[cell] = backup
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
