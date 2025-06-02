package com.example.sumamente.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale
import java.util.regex.Pattern
import kotlin.random.Random

class IQPlusRankingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IQPlusRankingAdapter
    private lateinit var tvMsgIQPlus: TextView
    private lateinit var btnShareRanking: FloatingActionButton
    private lateinit var spinnerFiltro: Spinner
    private lateinit var btnBack: ImageView
    private lateinit var loadingIndicator: ProgressBar

    private val rankingIQPlus: MutableList<IQPlusRankingItem> = mutableListOf()
    private val rankingIQPlusFiltrado: MutableList<IQPlusRankingItem> = mutableListOf()
    private var iqPlusFlash = false
    private val handlerFlash = Handler(Looper.getMainLooper())

    private lateinit var sharedPreferences: android.content.SharedPreferences

    companion object {
        const val TOTAL_COMBOS_REQUIRED = 21
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScoreManager.init(this)
        ScoreManager.initPrincipiante(this)
        ScoreManager.initPro(this)
        ScoreManager.initDeciPlus(this)
        ScoreManager.initDeciPlusPrincipiante(this)
        ScoreManager.initDeciPlusPro(this)
        ScoreManager.initRomas(this)
        ScoreManager.initRomasPrincipiante(this)
        ScoreManager.initRomasPro(this)
        ScoreManager.initAlfaNumeros(this)
        ScoreManager.initAlfaNumerosPrincipiante(this)
        ScoreManager.initAlfaNumerosPro(this)
        ScoreManager.initSumaResta(this)
        ScoreManager.initSumaRestaPrincipiante(this)
        ScoreManager.initSumaRestaPro(this)
        ScoreManager.initMasPlus(this)
        ScoreManager.initMasPlusPrincipiante(this)
        ScoreManager.initMasPlusPro(this)
        ScoreManager.initGenioPlus(this)
        ScoreManager.initGenioPlusPrincipiante(this)
        ScoreManager.initGenioPlusPro(this)

        setContentView(R.layout.activity_iqplus_ranking)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        initViews()
        setupRecyclerView()
        setupShareButton()
        setupBackButton()

        spinnerFiltro.visibility = View.GONE
        loadIQPlusRankingData()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewIQPlus)
        tvMsgIQPlus = findViewById(R.id.tvMsgIQPlus)
        btnShareRanking = findViewById(R.id.btnShareRanking)
        spinnerFiltro = findViewById(R.id.spinnerFiltro)
        btnBack = findViewById(R.id.btnBack)
        loadingIndicator = findViewById(R.id.loadingIndicator)
    }

    private fun setupRecyclerView() {
        adapter = IQPlusRankingAdapter(
            rankingIQPlusFiltrado,
            onIQPlusClick = { mostrarDialogoEstadisticas() }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupShareButton() {
        btnShareRanking.setOnClickListener { compartirRanking() }
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener { finish() }
    }

    private fun loadIQPlusRankingData() {
        loadingIndicator.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvMsgIQPlus.visibility = View.GONE
        btnShareRanking.visibility = View.GONE

        Handler(Looper.getMainLooper()).postDelayed({
            rankingIQPlus.clear()
            rankingIQPlusFiltrado.clear()

            val nombre = getUsuarioActualNombre()
            val pais = getUsuarioActualPais()
            val iqPlus = calcularIQPlus()
            val haCompletado = haJugadoTodosLosCombos()
            val combosCompletados = getCombosCompletados()

            loadingIndicator.visibility = View.GONE

            if (combosCompletados == 0) {
                mostrarMensajeInicial(nombre)
                recyclerView.visibility = View.GONE
                btnShareRanking.visibility = View.GONE
                return@postDelayed
            }

            if (!haCompletado) {
                mostrarMensajesMotivacionales(nombre, iqPlus, combosCompletados)
                recyclerView.visibility = View.GONE
                btnShareRanking.visibility = View.GONE
                return@postDelayed
            } else {
                tvMsgIQPlus.visibility = View.GONE
            }

            val item = IQPlusRankingItem(
                position = 1,
                username = nombre,
                countryCode = pais,
                iqPlus = iqPlus,
                isCurrentUser = true
            )
            rankingIQPlus.add(item)
            rankingIQPlusFiltrado.add(item)

            adapter.notifyItemInserted(0)
            recyclerView.visibility = View.VISIBLE
            btnShareRanking.visibility = View.VISIBLE
        }, 800)
    }

    private fun mostrarMensajeInicial(nombre: String) {
        val startMsgIndex = Random.nextInt(6)
        val startMsg = getString(
            when(startMsgIndex) {
                0 -> R.string.msg_iqplus_start_1
                1 -> R.string.msg_iqplus_start_2
                2 -> R.string.msg_iqplus_start_3
                3 -> R.string.msg_iqplus_start_4
                4 -> R.string.msg_iqplus_start_5
                else -> R.string.msg_iqplus_start_6
            },
            nombre
        )

        tvMsgIQPlus.apply {
            visibility = View.VISIBLE
            text = startMsg
            textSize = 24f
            setTextColor(ContextCompat.getColor(this@IQPlusRankingActivity, android.R.color.black))
        }
    }

    private fun mostrarMensajesMotivacionales(
        nombre: String,
        iqPlus: Double,
        combosCompletados: Int
    ) {
        val combosFaltantes = TOTAL_COMBOS_REQUIRED - combosCompletados
        val infoMsgIndex = Random.nextInt(6)
        val infoMsg = getString(
            when(infoMsgIndex) {
                0 -> R.string.msg_iqplus_info_1
                1 -> R.string.msg_iqplus_info_2
                2 -> R.string.msg_iqplus_info_3
                3 -> R.string.msg_iqplus_info_4
                4 -> R.string.msg_iqplus_info_5
                else -> R.string.msg_iqplus_info_6
            },
            iqPlus
        )

        val levelsInfoMsgIndex = Random.nextInt(6)
        var levelsInfoMsg = getString(
            when(levelsInfoMsgIndex) {
                0 -> R.string.msg_iqplus_levels_info_1
                1 -> R.string.msg_iqplus_levels_info_2
                2 -> R.string.msg_iqplus_levels_info_3
                3 -> R.string.msg_iqplus_levels_info_4
                4 -> R.string.msg_iqplus_levels_info_5
                else -> R.string.msg_iqplus_levels_info_6
            },
            nombre, combosCompletados, combosFaltantes
        )

        val motivMsgIndex = Random.nextInt(6)
        var motivMsg = getString(
            when(motivMsgIndex) {
                0 -> R.string.msg_iqplus_motiv_1
                1 -> R.string.msg_iqplus_motiv_2
                2 -> R.string.msg_iqplus_motiv_3
                3 -> R.string.msg_iqplus_motiv_4
                4 -> R.string.msg_iqplus_motiv_5
                else -> R.string.msg_iqplus_motiv_6
            },
            nombre
        )

        val levelsInfoHasName = levelsInfoMsg.contains(nombre)
        if (levelsInfoHasName) {

            motivMsg = motivMsg.replace(nombre, "").replace(", !", "!").replace(", ¡", "¡")
        } else if (motivMsg.contains(nombre)) {

            levelsInfoMsg = levelsInfoMsg.replace(nombre, "").replace(", !", "!").replace(", ¡", "¡")
        }

        val mensajeFinal = "$infoMsg\n\n$levelsInfoMsg\n\n$motivMsg"

        val spannableText = hacerIQPlusInteractivo(mensajeFinal, iqPlus)

        tvMsgIQPlus.apply {
            visibility = View.VISIBLE
            text = spannableText
            textSize = 24f
            setTextColor(ContextCompat.getColor(this@IQPlusRankingActivity, android.R.color.black))
            movementMethod = LinkMovementMethod.getInstance()
        }
        tvMsgIQPlus.highlightColor = Color.TRANSPARENT

    }

    private fun hacerIQPlusInteractivo(texto: String, iqPlus: Double): SpannableString {
        val spannableString = SpannableString(texto)
        val iqPlusFormateado = String.format(Locale.ROOT, "%.2f", iqPlus)
        val pattern = Pattern.compile("\\b\\d+\\.\\d{2}\\b")
        val matcher = pattern.matcher(texto)

        while (matcher.find()) {
            val numeroEncontrado = texto.substring(matcher.start(), matcher.end())

            if (numeroEncontrado == iqPlusFormateado) {
                val start = matcher.start()
                val end = matcher.end()

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        // 1. Activa el flash (efecto amarillo)
                        iqPlusFlash = true
                        (widget as TextView).text = hacerIQPlusInteractivo(texto, iqPlus)

                        // 2. Espera 150ms y regresa a azul
                        handlerFlash.postDelayed({
                            iqPlusFlash = false
                            widget.text = hacerIQPlusInteractivo(texto, iqPlus)

                            // 3. Espera 80ms más y abre el diálogo
                            handlerFlash.postDelayed({
                                mostrarDialogoEstadisticas()
                            }, 80)
                        }, 150)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false

                        if (iqPlusFlash) {
                            // Efecto flash: amarillo vibrante
                            ds.color = ContextCompat.getColor(this@IQPlusRankingActivity, R.color.yellow_dark)
                            ds.isFakeBoldText = true
                            ds.setShadowLayer(4f, 0f, 0f, Color.DKGRAY)
                        } else {
                            // Normal: azul principal branding
                            ds.color = ContextCompat.getColor(this@IQPlusRankingActivity, R.color.blue_primary)
                            ds.isFakeBoldText = true
                            ds.setShadowLayer(2f, 1f, 1f, Color.GRAY)
                        }
                    }
                }

                spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                // No necesitas otros spans de color/negrita, ya lo maneja el ClickableSpan
            }
        }

        return spannableString
    }


    private fun getCombosCompletados(): Int {
        var combosCompletados = 0
        for (combo in IQPlusCombos.ALL) {
            val maxNivel = ScoreManager.getMaxLevelForCombo(combo.juego, combo.grado)
            if (maxNivel > 0) {
                combosCompletados++
            }
        }
        return combosCompletados
    }


    private fun getUsuarioActualNombre(): String =
        sharedPreferences.getString("savedUserName", getString(R.string.default_username)) ?: getString(R.string.default_username)

    private fun getUsuarioActualPais(): String =
        sharedPreferences.getString("savedCountryCode", "us") ?: "us"

    private fun calcularIQPlus(): Double {
        val precisionGlobal = ScoreManager.getPrecisionGlobal()
        val tiempoPromedio = ScoreManager.getTiempoPromedioGlobal()
        val velocidadGlobal = if (tiempoPromedio > 0.0) 1.0 / tiempoPromedio else 1.0

        var sumaIQ = 0.0
        for (combo in IQPlusCombos.ALL) {
            val maxNivel = ScoreManager.getMaxLevelForCombo(combo.juego, combo.grado)
            val factorCorreccion = obtenerFactorCorreccion(maxNivel)
            sumaIQ += combo.peso * precisionGlobal * velocidadGlobal * factorCorreccion
        }
            return String.format(Locale.US, "%.3f", sumaIQ).toDouble()
    }

    private fun obtenerFactorCorreccion(maxNivel: Int): Double {
        return when (maxNivel) {
            in 1..14 -> 0.80
            in 15..28 -> 0.85
            in 29..42 -> 0.90
            in 43..56 -> 0.95
            in 57..70 -> 1.00
            else -> 0.0
        }
    }

    private fun haJugadoTodosLosCombos(): Boolean {
        return ScoreManager.haJugadoAlMenosUnNivelEnCadaJuegoYGrado()
    }

    private fun mostrarDialogoEstadisticas() {

        val precision = ScoreManager.getPrecisionGlobal()
        val tiempoPromedio = ScoreManager.getTiempoPromedioGlobal()
        val dialog = IQPlusStatsDialogFragment.newInstance(
            precision = precision,
            tiempoPromedio = tiempoPromedio
        )
        dialog.show(supportFragmentManager, "IQPlusStatsDialog")
    }

    private fun compartirRanking() {

        val posicion = 1
        val iqPlus = rankingIQPlusFiltrado.firstOrNull { it.username == getUsuarioActualNombre() }?.iqPlus ?: 0.0
        val mensaje = getString(R.string.share_iqplus_ranking_message, posicion, iqPlus)
        val intent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, mensaje)
            type = "text/plain"
        }
        startActivity(android.content.Intent.createChooser(intent, getString(R.string.share)))
    }
}

object IQPlusCombos {
    val ALL = listOf(
        // NÚMEROS+
        Combo("NumerosPlus", "Principiante", 4.0),
        Combo("NumerosPlus", "Avanzado", 7.0),
        Combo("NumerosPlus", "Pro", 10.0),
        // DECI+
        Combo("DeciPlus", "Principiante", 5.0),
        Combo("DeciPlus", "Avanzado", 8.0),
        Combo("DeciPlus", "Pro", 11.0),
        // ROMAS
        Combo("Romas", "Principiante", 6.0),
        Combo("Romas", "Avanzado", 9.0),
        Combo("Romas", "Pro", 12.0),
        // ALFANUMEROS
        Combo("AlfaNumeros", "Principiante", 7.0),
        Combo("AlfaNumeros", "Avanzado", 10.0),
        Combo("AlfaNumeros", "Pro", 13.0),
        // SUMARESTA
        Combo("SumaResta", "Principiante", 8.0),
        Combo("SumaResta", "Avanzado", 11.0),
        Combo("SumaResta", "Pro", 14.0),
        // MAS+
        Combo("MasPlus", "Principiante", 9.0),
        Combo("MasPlus", "Avanzado", 12.0),
        Combo("MasPlus", "Pro", 15.0),
        // GENIO+
        Combo("GenioPlus", "Principiante", 10.0),
        Combo("GenioPlus", "Avanzado", 13.0),
        Combo("GenioPlus", "Pro", 16.0)
    )
    data class Combo(val juego: String, val grado: String, val peso: Double)
}


