package com.heptacreation.sumamente.s5e.core

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
