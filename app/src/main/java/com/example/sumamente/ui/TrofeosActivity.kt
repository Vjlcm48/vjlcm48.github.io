package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sumamente.R
import java.lang.ref.WeakReference

class TrofeosActivity : AppCompatActivity() {

    companion object {
        private var instanceRef: WeakReference<TrofeosActivity>? = null

        fun finishTrofeosActivity() {
            instanceRef?.get()?.finish()
        }
    }

    private lateinit var tvTituloTrofeos: TextView
    private lateinit var btnPin: LinearLayout
    private lateinit var btnCorona: LinearLayout
    private lateinit var btnMedalla: LinearLayout
    private lateinit var btnTrofeo: LinearLayout
    private lateinit var btnMisCondecoraciones: LinearLayout
    private lateinit var btnClose: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var mediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trofeos)

        instanceRef = WeakReference(this)

        initViews()
        initMusic()
        setupButtons()
    }


    private fun initViews() {
        tvTituloTrofeos = findViewById(R.id.tv_titulo_trofeos)
        btnPin = findViewById(R.id.btn_pin)
        btnCorona = findViewById(R.id.btn_corona)
        btnMedalla = findViewById(R.id.btn_medalla)
        btnTrofeo = findViewById(R.id.btn_trofeo)
        btnMisCondecoraciones = findViewById(R.id.btn_mis_condecoraciones)
        btnClose = findViewById(R.id.btn_close)
        btnBack = findViewById(R.id.btn_back)
    }


    private fun initMusic() {

        mediaPlayer = MediaPlayer.create(this, R.raw.condecoraciones).apply {
            isLooping = true
            start()
        }
    }

    private fun setupButtons() {

        btnClose.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, MainGameActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        btnBack.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, MainGameActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnPin.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, PinesActivity::class.java)

                startActivity(intent)
            }
        }


        btnCorona.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, CoronasActivity::class.java)
                startActivity(intent)
            }
        }

        btnMedalla.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, MedallasActivity::class.java)
                startActivity(intent)
            }
        }

        btnTrofeo.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, TrofeosDetailActivity::class.java)
                startActivity(intent)
            }
        }

        setupPremioButton(btnMisCondecoraciones)
    }

    private fun setupPremioButton(button: LinearLayout) {
        button.setOnClickListener {
            applyBounceEffect(it) {
                Toast.makeText(this, getString(R.string.en_construccion), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply {
            playTogether(scaleDownX, scaleDownY)
        }

        val scaleUp = AnimatorSet().apply {
            playTogether(scaleUpX, scaleUpY)
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
        instanceRef?.clear()
        instanceRef = null
    }


}
