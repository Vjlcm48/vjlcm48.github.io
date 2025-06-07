package com.example.sumamente.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sumamente.R
import com.google.android.material.transition.platform.MaterialSharedAxis
import java.util.Locale
import androidx.core.content.edit

class MainGameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var locationManager: LocationManager
    private lateinit var profileText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    private lateinit var trophyContainer: FrameLayout
    private lateinit var trophyRedDot: View

    override fun onCreate(savedInstanceState: Bundle?) {

        window.exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)

        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main_game)

        CondecoracionTracker.init(this)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        profileText = findViewById(R.id.profile_text)

        val newGameButton = findViewById<Button>(R.id.new_game_button)
        val homeIcon = findViewById<ImageView>(R.id.home_icon)
        val statisticsIcon = findViewById<ImageView>(R.id.statistics_icon)
        val settingsIcon = findViewById<ImageView>(R.id.settings_icon)
        findViewById<ImageView>(R.id.trophy_icon)
        val calendarIcon = findViewById<ImageView>(R.id.calendar_icon)

        trophyContainer = findViewById(R.id.trophy_container)
        trophyRedDot = findViewById(R.id.trophy_red_dot)

        mediaPlayer = MediaPlayer.create(this, R.raw.fondomusicals1)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.2f, 0.2f)

        if (sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)) {
            mediaPlayer.start()
        }

        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == SettingsActivity.SOUND_ENABLED) {
                val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
                if (soundEnabled) {
                    mediaPlayer.start()
                } else {
                    mediaPlayer.pause()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        val savedCountryCode = sharedPreferences.getString("savedCountryCode", null)
        val savedUsername = sharedPreferences.getString("savedUserName", "Usuario")
        profileText.text = savedUsername

        if (savedCountryCode != null) {
            val resId = countryFlagMap[savedCountryCode]
            if (resId != null) {
                val drawable = ContextCompat.getDrawable(this, resId)
                profileText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                getLocationAndSetFlag()
            }
        }

        newGameButton.setOnClickListener {
            applyBounceEffect(it) {
                mediaPlayer.fadeOut()
                val intent = Intent(this, GameSelectionActivity::class.java)
                startActivity(intent)
            }
        }

        homeIcon.setOnClickListener {
            applyBounceEffect(it) {
                mediaPlayer.fadeOut()
                startActivity(Intent(this, MainGameActivity::class.java))
            }
        }

        statisticsIcon.setOnClickListener {
            applyBounceEffect(it) {
                mediaPlayer.fadeOut()
                startActivity(Intent(this, ClassificationActivity::class.java))
            }
        }

        settingsIcon.setOnClickListener {
            applyBounceEffect(it) {
                mediaPlayer.fadeOut()
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }

        profileText.setOnClickListener {
            applyBounceEffect(it) {
                val profileEditDialog = ProfileEditDialog(this@MainGameActivity)
                profileEditDialog.show()
            }
        }

        calendarIcon.setOnClickListener {
            applyBounceEffect(it) {
                mediaPlayer.fadeOut()
                startActivity(Intent(this, DesafiosActivity::class.java))
            }
        }

        trophyContainer.setOnClickListener {
            applyBounceEffect(it) {

                CondecoracionTracker.clearTrophyRedDot()
                updateTrophyRedDot()

                mediaPlayer.fadeOut()
                val intent = Intent(this, TrofeosActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun updateTrophyRedDot() {
        if (CondecoracionTracker.shouldShowTrophyRedDot()) {
            trophyRedDot.visibility = View.VISIBLE
        } else {
            trophyRedDot.visibility = View.GONE
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

    @Suppress("DEPRECATION")
    private fun getLocationAndSetFlag() {
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val geocoder = Geocoder(this@MainGameActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val countryCode = addresses[0].countryCode.lowercase(Locale.getDefault())

                    val resId = countryFlagMap[countryCode]
                    if (resId != null) {
                        val drawable = ContextCompat.getDrawable(this@MainGameActivity, resId)
                        profileText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                    }

                    sharedPreferences.edit { putString("savedCountryCode", countryCode) }
                }
                locationManager.removeUpdates(this)
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
        }
    }

    private fun MediaPlayer.fadeOut() {
        val fadeOutDuration = 2000L
        val fadeStep = 0.05f
        val fadeHandler = Handler(Looper.getMainLooper())
        var currentVolume = 0.2f

        val fadeRunnable = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    currentVolume -= fadeStep
                    if (currentVolume > 0) {
                        setVolume(currentVolume, currentVolume)
                        fadeHandler.postDelayed(
                            this,
                            (fadeOutDuration / (1 / fadeStep)).toLong()
                        )
                    } else {
                        pause()
                        setVolume(0f, 0f)
                    }
                }
            }
        }
        fadeHandler.post(fadeRunnable)
    }

    override fun onResume() {
        super.onResume()

        CondecoracionTracker.verificarYEntregarPines()
        updateTrophyRedDot()

        val soundEnabled = sharedPreferences.getBoolean(SettingsActivity.SOUND_ENABLED, true)
        if (soundEnabled && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        val savedCountryCode = sharedPreferences.getString("savedCountryCode", null)
        val savedUsername = sharedPreferences.getString("savedUserName", "Usuario")
        profileText.text = savedUsername

        if (savedCountryCode != null) {
            val resId = countryFlagMap[savedCountryCode]
            if (resId != null) {
                val drawable = ContextCompat.getDrawable(this, resId)
                profileText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocationAndSetFlag()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
}

val countryFlagMap = mapOf(
            "ad" to R.drawable.ad,
            "ae" to R.drawable.ae,
            "af" to R.drawable.af,
            "ag" to R.drawable.ag,
            "ai" to R.drawable.ai,
            "al" to R.drawable.al,
            "am" to R.drawable.am,
            "ao" to R.drawable.ao,
            "aq" to R.drawable.aq,
            "ar" to R.drawable.ar,
            "at" to R.drawable.at,
            "au" to R.drawable.au,
            "aw" to R.drawable.aw,
            "ax" to R.drawable.ax,
            "az" to R.drawable.az,
            "ba" to R.drawable.ba,
            "bb" to R.drawable.bb,
            "bd" to R.drawable.bd,
            "be" to R.drawable.be,
            "bf" to R.drawable.bf,
            "bg" to R.drawable.bg,
            "bh" to R.drawable.bh,
            "bi" to R.drawable.bi,
            "bj" to R.drawable.bj,
            "bl" to R.drawable.bl,
            "bm" to R.drawable.bm,
            "bn" to R.drawable.bn,
            "bo" to R.drawable.bo,
            "bq" to R.drawable.bq,
            "br" to R.drawable.br,
            "bs" to R.drawable.bs,
            "bt" to R.drawable.bt,
            "bv" to R.drawable.bv,
            "bw" to R.drawable.bw,
            "by" to R.drawable.by,
            "bz" to R.drawable.bz,
            "ca" to R.drawable.ca,
            "cc" to R.drawable.cc,
            "cd" to R.drawable.cd,
            "cf" to R.drawable.cf,
            "cg" to R.drawable.cg,
            "ch" to R.drawable.ch,
            "ci" to R.drawable.ci,
            "ck" to R.drawable.ck,
            "cl" to R.drawable.cl,
            "cm" to R.drawable.cm,
            "cn" to R.drawable.cn,
            "co" to R.drawable.co,
            "cr" to R.drawable.cr,
            "cu" to R.drawable.cu,
            "cv" to R.drawable.cv,
            "cw" to R.drawable.cw,
            "cx" to R.drawable.cx,
            "cy" to R.drawable.cy,
            "cz" to R.drawable.cz,
            "de" to R.drawable.de,
            "dj" to R.drawable.dj,
            "dk" to R.drawable.dk,
            "dm" to R.drawable.dm,
            "dominican_republic" to R.drawable.dominican_republic,
            "dz" to R.drawable.dz,
            "ec" to R.drawable.ec,
            "ee" to R.drawable.ee,
            "eg" to R.drawable.eg,
            "eh" to R.drawable.eh,
            "er" to R.drawable.er,
            "es" to R.drawable.es,
            "et" to R.drawable.et,
            "fi" to R.drawable.fi,
            "fj" to R.drawable.fj,
            "fk" to R.drawable.fk,
            "fm" to R.drawable.fm,
            "fo" to R.drawable.fo,
            "fr" to R.drawable.fr,
            "ga" to R.drawable.ga,
            "gb" to R.drawable.gb,
            "gb_eng" to R.drawable.gb_eng,
            "gb_nir" to R.drawable.gb_nir,
            "gb_sct" to R.drawable.gb_sct,
            "gb_wls" to R.drawable.gb_wls,
            "gd" to R.drawable.gd,
            "ge" to R.drawable.ge,
            "gf" to R.drawable.gf,
            "gg" to R.drawable.gg,
            "gh" to R.drawable.gh,
            "gi" to R.drawable.gi,
            "gl" to R.drawable.gl,
            "gm" to R.drawable.gm,
            "gn" to R.drawable.gn,
            "gp" to R.drawable.gp,
            "gq" to R.drawable.gq,
            "gr" to R.drawable.gr,
            "gs" to R.drawable.gs,
            "gt" to R.drawable.gt,
            "gu" to R.drawable.gu,
            "gw" to R.drawable.gw,
            "gy" to R.drawable.gy,
            "hk" to R.drawable.hk,
            "hm" to R.drawable.hm,
            "hn" to R.drawable.hn,
            "hr" to R.drawable.hr,
            "ht" to R.drawable.ht,
            "hu" to R.drawable.hu,
            "id" to R.drawable.id,
            "ie" to R.drawable.ie,
            "il" to R.drawable.il,
            "im" to R.drawable.im,
            "in" to R.drawable.`in`,
            "io" to R.drawable.io,
            "iq" to R.drawable.iq,
            "ir" to R.drawable.ir,
            "is" to R.drawable.`is`,
            "it" to R.drawable.it,
            "je" to R.drawable.je,
            "jm" to R.drawable.jm,
            "jo" to R.drawable.jo,
            "jp" to R.drawable.jp,
            "ke" to R.drawable.ke,
            "kg" to R.drawable.kg,
            "kh" to R.drawable.kh,
            "ki" to R.drawable.ki,
            "km" to R.drawable.km,
            "kn" to R.drawable.kn,
            "kp" to R.drawable.kp,
            "kr" to R.drawable.kr,
            "kw" to R.drawable.kw,
            "ky" to R.drawable.ky,
            "kz" to R.drawable.kz,
            "la" to R.drawable.la,
            "lb" to R.drawable.lb,
            "lc" to R.drawable.lc,
            "li" to R.drawable.li,
            "lk" to R.drawable.lk,
            "lr" to R.drawable.lr,
            "ls" to R.drawable.ls,
            "lt" to R.drawable.lt,
            "lu" to R.drawable.lu,
            "lv" to R.drawable.lv,
            "ly" to R.drawable.ly,
            "ma" to R.drawable.ma,
            "mc" to R.drawable.mc,
            "md" to R.drawable.md,
            "me" to R.drawable.me,
            "mf" to R.drawable.mf,
            "mg" to R.drawable.mg,
            "mh" to R.drawable.mh,
            "mk" to R.drawable.mk,
            "ml" to R.drawable.ml,
            "mm" to R.drawable.mm,
            "mn" to R.drawable.mn,
            "mo" to R.drawable.mo,
            "mp" to R.drawable.mp,
            "mq" to R.drawable.mq,
            "mr" to R.drawable.mr,
            "ms" to R.drawable.ms,
            "mt" to R.drawable.mt,
            "mu" to R.drawable.mu,
            "mv" to R.drawable.mv,
            "mw" to R.drawable.mw,
            "mx" to R.drawable.mx,
            "my" to R.drawable.my,
            "mz" to R.drawable.mz,
            "na" to R.drawable.na,
            "nc" to R.drawable.nc,
            "ne" to R.drawable.ne,
            "nf" to R.drawable.nf,
            "ng" to R.drawable.ng,
            "ni" to R.drawable.ni,
            "nl" to R.drawable.nl,
            "no" to R.drawable.no,
            "np" to R.drawable.np,
            "nr" to R.drawable.nr,
            "nu" to R.drawable.nu,
            "nz" to R.drawable.nz,
            "om" to R.drawable.om,
            "pa" to R.drawable.pa,
            "pe" to R.drawable.pe,
            "pf" to R.drawable.pf,
            "pg" to R.drawable.pg,
            "ph" to R.drawable.ph,
            "pk" to R.drawable.pk,
            "pl" to R.drawable.pl,
            "pm" to R.drawable.pm,
            "pn" to R.drawable.pn,
            "pr" to R.drawable.pr,
            "ps" to R.drawable.ps,
            "pt" to R.drawable.pt,
            "pw" to R.drawable.pw,
            "py" to R.drawable.py,
            "qa" to R.drawable.qa,
            "re" to R.drawable.re,
            "ro" to R.drawable.ro,
            "rs" to R.drawable.rs,
            "ru" to R.drawable.ru,
            "rw" to R.drawable.rw,
            "sa" to R.drawable.sa,
            "sb" to R.drawable.sb,
            "sc" to R.drawable.sc,
            "sd" to R.drawable.sd,
            "se" to R.drawable.se,
            "sg" to R.drawable.sg,
            "sh" to R.drawable.sh,
            "si" to R.drawable.si,
            "sj" to R.drawable.sj,
            "sk" to R.drawable.sk,
            "sl" to R.drawable.sl,
            "sm" to R.drawable.sm,
            "sn" to R.drawable.sn,
            "so" to R.drawable.so,
            "sr" to R.drawable.sr,
            "ss" to R.drawable.ss,
            "st" to R.drawable.st,
            "sv" to R.drawable.sv,
            "sx" to R.drawable.sx,
            "sy" to R.drawable.sy,
            "sz" to R.drawable.sz,
            "tc" to R.drawable.tc,
            "td" to R.drawable.td,
            "tf" to R.drawable.tf,
            "tg" to R.drawable.tg,
            "th" to R.drawable.th,
            "tj" to R.drawable.tj,
            "tk" to R.drawable.tk,
            "tl" to R.drawable.tl,
            "tm" to R.drawable.tm,
            "tn" to R.drawable.tn,
            "to" to R.drawable.to,
            "tr" to R.drawable.tr,
            "tt" to R.drawable.tt,
            "tv" to R.drawable.tv,
            "tw" to R.drawable.tw,
            "tz" to R.drawable.tz,
            "ua" to R.drawable.ua,
            "ug" to R.drawable.ug,
            "um" to R.drawable.um,
            "us" to R.drawable.us,
            "uy" to R.drawable.uy,
            "uz" to R.drawable.uz,
            "va" to R.drawable.va,
            "vc" to R.drawable.vc,
            "ve" to R.drawable.ve,
            "vg" to R.drawable.vg,
            "vi" to R.drawable.vi,
            "vn" to R.drawable.vn,
            "vu" to R.drawable.vu,
            "wf" to R.drawable.wf,
            "ws" to R.drawable.ws,
            "ye" to R.drawable.ye,
            "yt" to R.drawable.yt,
            "za" to R.drawable.za,
            "zm" to R.drawable.zm,
            "zw" to R.drawable.zw
        )
