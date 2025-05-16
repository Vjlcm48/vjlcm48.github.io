package com.example.sumamente.ui

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R

class MisCondecoracionesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateTextView: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnClose: ImageView
    private lateinit var adapter: CondecoracionesAdapter
    private val condecoraciones = mutableListOf<Condecoracion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_condecoraciones)

        initViews()
        setupButtons()
        loadCondecoraciones()
        setupRecyclerView()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recycler_view_condecoraciones)
        emptyStateTextView = findViewById(R.id.tv_empty_state)
        btnBack = findViewById(R.id.btn_back)
        btnClose = findViewById(R.id.btn_close)
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
                finish()
            }
        }
    }

    private fun loadCondecoraciones() {

        condecoraciones.clear()

        condecoraciones.add(
            Condecoracion(
                tipo = TipoCondecoracion.PIN,
                nombre = "INVICTUS",
                descripcion = getString(R.string.logro_pin_invictus),
                imagen = R.drawable.ic_pin_invictus
            )
        )


        condecoraciones.add(
            Condecoracion(
                tipo = TipoCondecoracion.MEDALLA,
                nombre = "INITIUM",
                descripcion = getString(R.string.logro_medalla_initium),
                imagen = R.drawable.ic_medalla_initium_cintas
            )
        )


        condecoraciones.add(
            Condecoracion(
                tipo = TipoCondecoracion.TROFEO,
                nombre = "GRADUS",
                descripcion = getString(R.string.logro_trofeo_gradus),
                imagen = R.drawable.ic_trofeo_gradus
            )
        )

        if (condecoraciones.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = CondecoracionesAdapter(condecoraciones) { condecoracion ->

            mostrarImagenAmpliada(condecoracion)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun mostrarImagenAmpliada(condecoracion: Condecoracion) {
        val dialog = ImagenAmpliadaDialog(
            this,
            condecoracion.nombre,
            condecoracion.imagen
        )
        dialog.show()
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
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }
}
