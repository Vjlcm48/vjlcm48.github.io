package com.example.sumamente.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sumamente.R
import java.util.Locale

class FlagSelectionActivity : AppCompatActivity() {

    private lateinit var flagsRecyclerView: RecyclerView
    private lateinit var searchEditText: androidx.appcompat.widget.AppCompatEditText
    private lateinit var currentFlagImageView: ImageView
    private lateinit var currentFlagLabel: androidx.appcompat.widget.AppCompatTextView

    private val countryFlags = listOf(
        "ad", "ae", "af", "ag", "ai", "al", "am", "ao", "ar", "at", "au",
        "az", "ba", "bb", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm",
        "bn", "bo", "br", "bs", "bt", "bw", "by", "bz", "ca", "cf", "ch",
        "cl", "cn", "co", "cr", "cu", "cy", "cz", "de", "dj", "dk", "dm",
        "do", "dz", "ec", "ee", "eg", "es", "et", "fi", "fj", "fr", "ga",
        "gb", "gd", "ge", "gh", "gi", "gr", "gt", "gy", "hk", "hn", "hr",
        "ht", "hu", "id", "ie", "il", "in", "iq", "ir", "is", "it", "jm",
        "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw",
        "kz", "la", "lb", "lc", "lk", "lr", "ls", "lt", "lu", "lv", "ly",
        "ma", "mc", "md", "me", "mg", "mh", "mk", "ml", "mm", "mn", "mo",
        "mr", "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "ne", "ng",
        "ni", "nl", "no", "np", "nr", "nz", "om", "pa", "pe", "ph", "pk",
        "pl", "pt", "py", "qa", "ro", "rs", "ru", "rw", "sa", "sb", "sc",
        "sd", "se", "sg", "si", "sk", "sl", "sm", "sn", "so", "sr", "ss",
        "st", "sv", "sy", "sz", "td", "tg", "th", "tj", "tl", "tm", "tn",
        "to", "tr", "tt", "tv", "tw", "tz", "ua", "ug", "us", "uy", "uz",
        "va", "ve", "vn", "vu", "ws", "ye", "za", "zm", "zw"
    )

    companion object {
        val countryNameMap = mapOf(
            "ad" to R.string.country_ad,
            "ae" to R.string.country_ae,
            "af" to R.string.country_af,
            "ag" to R.string.country_ag,
            "ai" to R.string.country_ai,
            "al" to R.string.country_al,
            "am" to R.string.country_am,
            "ao" to R.string.country_ao,
            "aq" to R.string.country_aq,
            "ar" to R.string.country_ar,
            "at" to R.string.country_at,
            "au" to R.string.country_au,
            "aw" to R.string.country_aw,
            "ax" to R.string.country_ax,
            "az" to R.string.country_az,
            "ba" to R.string.country_ba,
            "bb" to R.string.country_bb,
            "bd" to R.string.country_bd,
            "be" to R.string.country_be,
            "bf" to R.string.country_bf,
            "bg" to R.string.country_bg,
            "bh" to R.string.country_bh,
            "bi" to R.string.country_bi,
            "bj" to R.string.country_bj,
            "bl" to R.string.country_bl,
            "bm" to R.string.country_bm,
            "bn" to R.string.country_bn,
            "bo" to R.string.country_bo,
            "bq" to R.string.country_bq,
            "br" to R.string.country_br,
            "bs" to R.string.country_bs,
            "bt" to R.string.country_bt,
            "bv" to R.string.country_bv,
            "bw" to R.string.country_bw,
            "by" to R.string.country_by,
            "bz" to R.string.country_bz,
            "ca" to R.string.country_ca,
            "cc" to R.string.country_cc,
            "cd" to R.string.country_cd,
            "cf" to R.string.country_cf,
            "cg" to R.string.country_cg,
            "ch" to R.string.country_ch,
            "ci" to R.string.country_ci,
            "ck" to R.string.country_ck,
            "cl" to R.string.country_cl,
            "cm" to R.string.country_cm,
            "cn" to R.string.country_cn,
            "co" to R.string.country_co,
            "cr" to R.string.country_cr,
            "cu" to R.string.country_cu,
            "cv" to R.string.country_cv,
            "cw" to R.string.country_cw,
            "cx" to R.string.country_cx,
            "cy" to R.string.country_cy,
            "cz" to R.string.country_cz,
            "de" to R.string.country_de,
            "dj" to R.string.country_dj,
            "dk" to R.string.country_dk,
            "dm" to R.string.country_dm,
            "dominican_republic" to R.string.country_dominican_republic,
            "dz" to R.string.country_dz,
            "ec" to R.string.country_ec,
            "ee" to R.string.country_ee,
            "eg" to R.string.country_eg,
            "eh" to R.string.country_eh,
            "er" to R.string.country_er,
            "es" to R.string.country_es,
            "et" to R.string.country_et,
            "fi" to R.string.country_fi,
            "fj" to R.string.country_fj,
            "fk" to R.string.country_fk,
            "fm" to R.string.country_fm,
            "fo" to R.string.country_fo,
            "fr" to R.string.country_fr,
            "ga" to R.string.country_ga,
            "gb" to R.string.country_gb,
            "gd" to R.string.country_gd,
            "ge" to R.string.country_ge,
            "gf" to R.string.country_gf,
            "gg" to R.string.country_gg,
            "gh" to R.string.country_gh,
            "gi" to R.string.country_gi,
            "gl" to R.string.country_gl,
            "gm" to R.string.country_gm,
            "gn" to R.string.country_gn,
            "gp" to R.string.country_gp,
            "gq" to R.string.country_gq,
            "gr" to R.string.country_gr,
            "gs" to R.string.country_gs,
            "gt" to R.string.country_gt,
            "gu" to R.string.country_gu,
            "gw" to R.string.country_gw,
            "gy" to R.string.country_gy,
            "hk" to R.string.country_hk,
            "hm" to R.string.country_hm,
            "hn" to R.string.country_hn,
            "hr" to R.string.country_hr,
            "ht" to R.string.country_ht,
            "hu" to R.string.country_hu,
            "id" to R.string.country_id,
            "ie" to R.string.country_ie,
            "il" to R.string.country_il,
            "im" to R.string.country_im,
            "in" to R.string.country_in,
            "io" to R.string.country_io,
            "iq" to R.string.country_iq,
            "ir" to R.string.country_ir,
            "is" to R.string.country_is,
            "it" to R.string.country_it,
            "je" to R.string.country_je,
            "jm" to R.string.country_jm,
            "jo" to R.string.country_jo,
            "jp" to R.string.country_jp,
            "ke" to R.string.country_ke,
            "kg" to R.string.country_kg,
            "kh" to R.string.country_kh,
            "ki" to R.string.country_ki,
            "km" to R.string.country_km,
            "kn" to R.string.country_kn,
            "kp" to R.string.country_kp,
            "kr" to R.string.country_kr,
            "kw" to R.string.country_kw,
            "ky" to R.string.country_ky,
            "kz" to R.string.country_kz,
            "la" to R.string.country_la,
            "lb" to R.string.country_lb,
            "lc" to R.string.country_lc,
            "li" to R.string.country_li,
            "lk" to R.string.country_lk,
            "lr" to R.string.country_lr,
            "ls" to R.string.country_ls,
            "lt" to R.string.country_lt,
            "lu" to R.string.country_lu,
            "lv" to R.string.country_lv,
            "ly" to R.string.country_ly,
            "ma" to R.string.country_ma,
            "mc" to R.string.country_mc,
            "md" to R.string.country_md,
            "me" to R.string.country_me,
            "mf" to R.string.country_mf,
            "mg" to R.string.country_mg,
            "mh" to R.string.country_mh,
            "mk" to R.string.country_mk,
            "ml" to R.string.country_ml,
            "mm" to R.string.country_mm,
            "mn" to R.string.country_mn,
            "mo" to R.string.country_mo,
            "mp" to R.string.country_mp,
            "mq" to R.string.country_mq,
            "mr" to R.string.country_mr,
            "ms" to R.string.country_ms,
            "mt" to R.string.country_mt,
            "mu" to R.string.country_mu,
            "mv" to R.string.country_mv,
            "mw" to R.string.country_mw,
            "mx" to R.string.country_mx,
            "my" to R.string.country_my,
            "mz" to R.string.country_mz,
            "na" to R.string.country_na,
            "nc" to R.string.country_nc,
            "ne" to R.string.country_ne,
            "nf" to R.string.country_nf,
            "ng" to R.string.country_ng,
            "ni" to R.string.country_ni,
            "nl" to R.string.country_nl,
            "no" to R.string.country_no,
            "np" to R.string.country_np,
            "nr" to R.string.country_nr,
            "nu" to R.string.country_nu,
            "nz" to R.string.country_nz,
            "om" to R.string.country_om,
            "pa" to R.string.country_pa,
            "pe" to R.string.country_pe,
            "pf" to R.string.country_pf,
            "pg" to R.string.country_pg,
            "ph" to R.string.country_ph,
            "pk" to R.string.country_pk,
            "pl" to R.string.country_pl,
            "pt" to R.string.country_pt,
            "py" to R.string.country_py,
            "qa" to R.string.country_qa,
            "re" to R.string.country_re,
            "ro" to R.string.country_ro,
            "rs" to R.string.country_rs,
            "ru" to R.string.country_ru,
            "rw" to R.string.country_rw,
            "sa" to R.string.country_sa,
            "sb" to R.string.country_sb,
            "sc" to R.string.country_sc,
            "sd" to R.string.country_sd,
            "se" to R.string.country_se,
            "sg" to R.string.country_sg,
            "si" to R.string.country_si,
            "sk" to R.string.country_sk,
            "sl" to R.string.country_sl,
            "sm" to R.string.country_sm,
            "sn" to R.string.country_sn,
            "so" to R.string.country_so,
            "sr" to R.string.country_sr,
            "st" to R.string.country_st,
            "sv" to R.string.country_sv,
            "sy" to R.string.country_sy,
            "sz" to R.string.country_sz,
            "td" to R.string.country_td,
            "tg" to R.string.country_tg,
            "th" to R.string.country_th,
            "tj" to R.string.country_tj,
            "tl" to R.string.country_tl,
            "tm" to R.string.country_tm,
            "tn" to R.string.country_tn,
            "tr" to R.string.country_tr,
            "tt" to R.string.country_tt,
            "tw" to R.string.country_tw,
            "tz" to R.string.country_tz,
            "ua" to R.string.country_ua,
            "ug" to R.string.country_ug,
            "us" to R.string.country_us,
            "uy" to R.string.country_uy,
            "uz" to R.string.country_uz,
            "va" to R.string.country_va,
            "ve" to R.string.country_ve,
            "vn" to R.string.country_vn,
            "ye" to R.string.country_ye,
            "za" to R.string.country_za,
            "zm" to R.string.country_zm,
            "zw" to R.string.country_zw
        )
    }

    private lateinit var adapter: FlagsAdapter
    private lateinit var sharedPreferences: android.content.SharedPreferences
    private var currentCountryCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        currentCountryCode = sharedPreferences.getString("savedCountryCode", null)
        setContentView(R.layout.activity_flag_selection)



        flagsRecyclerView = findViewById(R.id.flagsRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)
        currentFlagImageView = findViewById(R.id.currentFlagImageView)
        currentFlagLabel = findViewById(R.id.currentFlagLabel)

        currentFlagLabel.text = getString(R.string.current_flag)
        currentCountryCode?.let { code ->
            val flagResId = FlagsAdapter.flagResourceMap[code]
            if (flagResId != null) {
                currentFlagImageView.setImageResource(flagResId)
            }
        }

        val spanCount = 4
        flagsRecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        adapter = FlagsAdapter(countryFlags, this::onFlagSelected, this)
        flagsRecyclerView.adapter = adapter

        searchEditText.addTextChangedListener { text ->
            val filteredFlags = countryFlags.filter { code ->
                getCountryName(code).contains(text.toString(), ignoreCase = true)
            }
            adapter.updateFlags(filteredFlags)
        }

        findViewById<View>(R.id.closeButton).setOnClickListener {
            applyBounceEffect(it) {
                finish()
            }
        }
    }

    private fun onFlagSelected(countryCode: String, itemView: View) {
        applyBounceEffect(itemView) {
            showConfirmationDialog(countryCode)
        }
    }

    private fun showConfirmationDialog(newCountryCode: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.confirmation)
        builder.setMessage(R.string.confirm_change_flag)
        builder.setPositiveButton(getString(R.string.yes).uppercase(Locale.getDefault())) { dialog, _ ->
            sharedPreferences.edit { putString("savedCountryCode", newCountryCode) }
            currentCountryCode = newCountryCode

            val inputField = findViewById<EditText>(R.id.searchEditText)
            inputField?.animate()?.alpha(0f)?.setDuration(300)?.withEndAction {
                inputField.text?.clear()
                inputField.alpha = 1f
            }

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputField?.windowToken, 0)

            currentFlagImageView.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(500)
                .withEndAction {

                    val flagResId = FlagsAdapter.flagResourceMap[newCountryCode]
                    if (flagResId != null) {
                        currentFlagImageView.setImageResource(flagResId)
                    }

                    currentFlagImageView.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(500)
                        .withEndAction {

                            val rootView = findViewById<ViewGroup>(android.R.id.content)
                            val inflater = LayoutInflater.from(this)
                            val popupView = inflater.inflate(R.layout.popup_country_name, rootView, false)
                            val popupText = popupView.findViewById<TextView>(R.id.popup_text)
                            popupText.text = getCountryName(newCountryCode)

                            popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                            val popupWidth = popupView.measuredWidth
                            val popupHeight = popupView.measuredHeight

                            val location = IntArray(2)
                            currentFlagImageView.getLocationOnScreen(location)
                            val additionalSpacing = popupText.paint.measureText("    ")
                            val x = location[0] + currentFlagImageView.width + additionalSpacing.toInt()
                            val y = location[1] + (currentFlagImageView.height / 2) - (popupHeight / 2)

                            val popupWindow = PopupWindow(
                                popupView,
                                popupWidth,
                                popupHeight,
                                true
                            )

                            popupWindow.showAtLocation(currentFlagImageView, Gravity.NO_GRAVITY, x, y)

                            Handler(Looper.getMainLooper()).postDelayed({
                                popupView.animate()
                                    .alpha(0f)
                                    .scaleX(0f)
                                    .scaleY(0f)
                                    .setDuration(500)
                                    .withEndAction {
                                        popupWindow.dismiss()
                                    }
                                    .start()
                            }, 3000)
                        }
                        .start()
                }
                .start()

            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no).uppercase(Locale.getDefault())) { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()

        alertDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        positiveButton.setTypeface(null, Typeface.BOLD)
        negativeButton.setTypeface(null, Typeface.BOLD)

        applyBounceEffect(positiveButton) {}
        applyBounceEffect(negativeButton) {}
    }

    private fun getCountryName(countryCode: String): String {
        val resourceId = countryNameMap[countryCode]
        return if (resourceId != null) getString(resourceId) else countryCode.uppercase(Locale.ROOT)
    }

    private fun applyBounceEffect(view: View, onAnimationEnd: () -> Unit) {
        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f).setDuration(50)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f).setDuration(50)
        val scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).setDuration(50)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).setDuration(50)

        val scaleDown = AnimatorSet().apply { playTogether(scaleDownX, scaleDownY) }
        val scaleUp = AnimatorSet().apply { playTogether(scaleUpX, scaleUpY) }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDown, scaleUp)

        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd()
            }
        })

        animatorSet.start()
    }
}