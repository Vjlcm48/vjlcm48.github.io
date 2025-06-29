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
    private lateinit var btnApexSupremus: LinearLayout
    private lateinit var btnTop10: LinearLayout
    private lateinit var btnLos7Mejores: LinearLayout
    private lateinit var btnClose: ImageView
    private lateinit var btnBack: ImageView
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var misCondecoracionesRedDot: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trofeos)

        instanceRef = WeakReference(this)

        CondecoracionTracker.init(this)

        initViews()
        initMusic()
        setupButtons()

        CondecoracionTracker.verificarYEntregarPines()
        updateMisCondecoracionesRedDot()
    }

    private fun initViews() {
        tvTituloTrofeos = findViewById(R.id.tv_titulo_trofeos)
        btnPin = findViewById(R.id.btn_pin)
        btnCorona = findViewById(R.id.btn_corona)
        btnMedalla = findViewById(R.id.btn_medalla)
        btnTrofeo = findViewById(R.id.btn_trofeo)
        btnMisCondecoraciones = findViewById(R.id.btn_mis_condecoraciones)
        btnApexSupremus = findViewById(R.id.btn_apex_supremus)
        btnTop10 = findViewById(R.id.btn_top_10)
        btnLos7Mejores = findViewById(R.id.btn_los_7_mejores)
        btnClose = findViewById(R.id.btn_close)
        btnBack = findViewById(R.id.btn_back)

        misCondecoracionesRedDot = findViewById(R.id.mis_condecoraciones_red_dot)
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

                stopAndReleaseMusic()

                val intent = Intent(this, MainGameActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        btnBack.setOnClickListener {
            applyBounceEffect(it) {

                stopAndReleaseMusic()

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

        btnTop10.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, Top10Activity::class.java)
                startActivity(intent)
            }
        }

        btnApexSupremus.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, ApexSupremusActivity::class.java)
                startActivity(intent)
            }
        }


        btnMisCondecoraciones.setOnClickListener {
            applyBounceEffect(it) {

                CondecoracionTracker.clearMisCondecoracionesRedDot()
                updateMisCondecoracionesRedDot()

                val intent = Intent(this, MisCondecoracionesActivity::class.java)
                startActivity(intent)
            }
        }

        btnLos7Mejores.setOnClickListener {
            applyBounceEffect(it) {
                val intent = Intent(this, Los7MejoresActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun stopAndReleaseMusic() {
        if (this::mediaPlayer.isInitialized) {
            try {

                try {
                    if (mediaPlayer.isPlaying) mediaPlayer.stop()
                } catch (_: Exception) {}
                try {
                    mediaPlayer.release()
                } catch (_: Exception) {}
            } catch (_: Exception) {}
        }
    }


    override fun onPause() {
        super.onPause()
        stopAndReleaseMusic()
    }


    private fun updateMisCondecoracionesRedDot() {
        if (CondecoracionTracker.shouldShowMisCondecoracionesRedDot()) {
            misCondecoracionesRedDot.visibility = View.VISIBLE
        } else {
            misCondecoracionesRedDot.visibility = View.GONE
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
        stopAndReleaseMusic()
        super.onDestroy()
        instanceRef?.clear()
        instanceRef = null
    }

}
