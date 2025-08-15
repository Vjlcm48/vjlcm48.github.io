package com.heptacreation.sumamente.s5e.core

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
