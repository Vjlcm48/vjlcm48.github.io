package com.example.sumamente.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import java.util.Locale

class ProgressSummaryActivity : BaseActivity() {

    private lateinit var progressRecyclerView: RecyclerView
    private lateinit var progressAdapter: GameProgressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_summary)
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        initializeScoreManager()
        displayAllProgress()
    }

    private fun initializeScoreManager() {

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
    }

    private fun setupNavigation() {
        val backArrow = findViewById<ImageView>(R.id.iv_back_arrow)
        val closeButton = findViewById<ImageView>(R.id.iv_close_button)
        backArrow.setOnClickListener { finish() }
        closeButton.setOnClickListener {
            val intent = Intent(this, MainGameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun displayAllProgress() {
        displayGlobalSummary()

        val gameProgressList = createGameProgressData()

        progressRecyclerView = findViewById(R.id.rv_progress_summary)
        progressAdapter = GameProgressAdapter(this, gameProgressList)
        progressRecyclerView.adapter = progressAdapter
        progressRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun displayGlobalSummary() {
        val tvGlobalLevels = findViewById<TextView>(R.id.tv_global_levels_completed)
        val tvGlobalProgress = findViewById<TextView>(R.id.tv_global_progress)

        val totalCompleted = ScoreManager.getTotalUniqueLevelsCompletedAllGames()
        val totalLevels = 1470.0
        val percentage = (totalCompleted / totalLevels) * 100
        val percentageString = String.format(Locale.getDefault(), "%.2f", percentage)

        val builder = SpannableStringBuilder()
        val label = getString(R.string.global_levels_label) + " "
        builder.append(label)

        val completedString = totalCompleted.toString()
        val blueSpannable = SpannableString(completedString)
        blueSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.progress_number_color)),
            0,
            completedString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.append(blueSpannable)

        val totalString = "/${totalLevels.toInt()}"
        builder.append(totalString)

        tvGlobalLevels.text = builder
        tvGlobalProgress.text = getString(R.string.global_progress_format, percentageString)
    }

    private fun createGameProgressData(): List<GameProgressItem> {
        return listOf(
            GameProgressItem(
                borderColorRes = R.color.blue_primary_dark,
                totalRowBackgroundRes = R.drawable.button_background,
                gameNameRes = R.string.game_numeros_plus,
                gameNameTextColorRes = android.R.color.white,
                getPrincipianteData = { ScoreManager.getUniqueLevelsPlayedNumerosPlusPrincipiante() },
                getAvanzadoData = { ScoreManager.getUniqueLevelsPlayedNumerosPlusAvanzado() },
                getProData = { ScoreManager.getUniqueLevelsPlayedNumerosPlusPro() }
            ),
            GameProgressItem(
                borderColorRes = R.color.orange_dark,
                totalRowBackgroundRes = R.drawable.button_background_deci,
                gameNameRes = R.string.game_deci_plus,
                gameNameTextColorRes = android.R.color.black,
                getPrincipianteData = { ScoreManager.getUniqueLevelsPlayedDeciPlusPrincipiante() },
                getAvanzadoData = { ScoreManager.getUniqueLevelsPlayedDeciPlusAvanzado() },
                getProData = { ScoreManager.getUniqueLevelsPlayedDeciPlusPro() }
            ),
            GameProgressItem(
                borderColorRes = R.color.green_medium,
                totalRowBackgroundRes = R.drawable.button_background_romas,
                gameNameRes = R.string.game_romas,
                gameNameTextColorRes = android.R.color.black,
                getPrincipianteData = { ScoreManager.getUniqueLevelsPlayedRomasPrincipiante() },
                getAvanzadoData = { ScoreManager.getUniqueLevelsPlayedRomasAvanzado() },
                getProData = { ScoreManager.getUniqueLevelsPlayedRomasPro() }
            ),
            GameProgressItem(
                borderColorRes = R.color.blue_primary_dark,
                totalRowBackgroundRes = R.drawable.button_background_alfa_numeros,
                gameNameSpannable = getAlfaNumerosSpannable(),
                gameNameTextColorRes = android.R.color.black,
                getPrincipianteData = { ScoreManager.getUniqueLevelsPlayedAlfaNumerosPrincipiante() },
                getAvanzadoData = { ScoreManager.getUniqueLevelsPlayedAlfaNumerosAvanzado() },
                getProData = { ScoreManager.getUniqueLevelsPlayedAlfaNumerosPro() }
            ),
            GameProgressItem(
                borderColorRes = R.color.gray,
                totalRowBackgroundRes = R.drawable.button_background_sumaresta,
                gameNameSpannable = getSumaRestaSpannable(),
                gameNameTextColorRes = android.R.color.black,
                getPrincipianteData = { ScoreManager.getUniqueLevelsPlayedSumaRestaPrincipiante() },
                getAvanzadoData = { ScoreManager.getUniqueLevelsPlayedSumaRestaAvanzado() },
                getProData = { ScoreManager.getUniqueLevelsPlayedSumaRestaPro() }
            ),
            GameProgressItem(
                borderColorRes = R.color.red,
                totalRowBackgroundRes = R.drawable.button_background_mas,
                gameNameRes = R.string.game_mas_plus,
                gameNameTextColorRes = android.R.color.white,
                getPrincipianteData = { ScoreManager.getUniqueLevelsPlayedMasPlusPrincipiante() },
                getAvanzadoData = { ScoreManager.getUniqueLevelsPlayedMasPlusAvanzado() },
                getProData = { ScoreManager.getUniqueLevelsPlayedMasPlusPro() }
            ),
            GameProgressItem(
                borderColorRes = R.color.blue_pressed,
                totalRowBackgroundRes = R.drawable.button_background_genio,
                gameNameRes = R.string.game_genio_plus,
                gameNameTextColorRes = R.color.blue_pressed,
                getPrincipianteData = { ScoreManager.getUniqueLevelsPlayedGenioPlusPrincipiante() },
                getAvanzadoData = { ScoreManager.getUniqueLevelsPlayedGenioPlusAvanzado() },
                getProData = { ScoreManager.getUniqueLevelsPlayedGenioPlusPro() }
            )
        )
    }

    private fun getAlfaNumerosSpannable(): SpannableString {
        val alfaText = getString(R.string.text_alfa)
        val numerosText = getString(R.string.text_numeros)
        val spannable = SpannableString("$alfaText$numerosText")
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red_primary)),
            0,
            alfaText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_primary_darker)),
            alfaText.length,
            spannable.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private fun getSumaRestaSpannable(): SpannableString {
        val sumaText = getString(R.string.text_suma)
        val restaText = getString(R.string.text_resta)
        val spannable = SpannableString("$sumaText$restaText")
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_pressed)),
            0,
            sumaText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)),
            sumaText.length,
            spannable.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}
