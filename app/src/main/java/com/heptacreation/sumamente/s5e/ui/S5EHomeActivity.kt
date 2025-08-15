package com.heptacreation.sumamente.s5e.ui

import android.content.Intent
import android.os.Bundle
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.s5e.ui.base.BaseActivityS5E

class S5EHomeActivity : BaseActivityS5E() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_s5e_home)

        findViewById<android.widget.ImageButton>(R.id.btnNewGame).setOnClickListener {
            startActivity(Intent(this, S5ESelectDifficultyActivity::class.java))
        }
    }
}
