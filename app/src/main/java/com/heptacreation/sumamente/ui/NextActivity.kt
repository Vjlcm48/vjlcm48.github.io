package com.heptacreation.sumamente.ui

import android.os.Bundle
import com.heptacreation.sumamente.R
import androidx.activity.enableEdgeToEdge

class NextActivity : BaseActivity()  {


    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

        setContentView(R.layout.activity_next)
    }
}
