package com.heptacreation.sumamente.s5e.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.s5e.core.Difficulty
import com.heptacreation.sumamente.s5e.ui.base.BaseActivityS5E

class S5ESelectDifficultyActivity : BaseActivityS5E() {

    companion object {
        const val EXTRA_DIFFICULTY = "extra_difficulty"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s5e_select_difficulty)

        findViewById<TextView>(R.id.title).text = getString(R.string.difficulty_selection_title)

        findViewById<ImageButton>(R.id.btnBeginner).setOnClickListener {
            openGame(Difficulty.PRINCIPIANTE)
        }
        findViewById<ImageButton>(R.id.btnAdvanced).setOnClickListener {
            openGame(Difficulty.AVANZADO)
        }
        findViewById<ImageButton>(R.id.btnPro).setOnClickListener {
            openGame(Difficulty.PRO)
        }
        findViewById<ImageButton>(R.id.btnHyper).setOnClickListener {
            openGame(Difficulty.HIPER)
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun openGame(difficulty: Difficulty) {
        val i = Intent(this, GameActivityS5E::class.java)
        i.putExtra(EXTRA_DIFFICULTY, difficulty.name)
        startActivity(i)
    }
}
