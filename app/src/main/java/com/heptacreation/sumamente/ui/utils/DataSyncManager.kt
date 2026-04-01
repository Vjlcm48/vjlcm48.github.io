package com.heptacreation.sumamente.ui.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.heptacreation.sumamente.R
import com.heptacreation.sumamente.ui.CondecoracionTracker
import com.heptacreation.sumamente.ui.GlobalRankingItem
import com.heptacreation.sumamente.ui.IQPlusRankingItem
import com.heptacreation.sumamente.ui.IntegralRankingItem
import com.heptacreation.sumamente.ui.ScoreManager
import com.heptacreation.sumamente.ui.SettingsActivity
import com.heptacreation.sumamente.ui.SpeedRankingItem
import kotlinx.coroutines.launch
import com.google.firebase.firestore.AggregateSource


object DataSyncManager {

    private val auth get() = FirebaseAuth.getInstance()

    private const val SCHEMA_VERSION_PROFILE = 1
    private const val SCHEMA_VERSION_SCORE = 2
    private const val SCHEMA_VERSION_CONDECO = 2


    @android.annotation.SuppressLint("HardwareIds")
    fun syncDataToCloud(
        context: Context,
        validateReferralIfNeeded: Boolean = false,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        Log.d("DataSyncManager", "=== INICIO syncDataToCloud LIGERA ===")

        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user == null) {
            Log.d("DataSyncManager", "Usuario no autenticado")
            onResult(false, context.getString(R.string.user_not_authenticated))
            return
        }

        try {
            val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            val userName = prefs.getString("savedUserName", "") ?: ""
            val countryCode = prefs.getString("savedCountryCode", "sumamente") ?: "sumamente"
            val hasInsigniaRIPlus = (CondecoracionTracker.getInsigniaRIPlus() != null)
            val welcomeMissCounter = prefs.getInt("welcomeMissCounter", 0)

            val totalScoreAllGames = ScoreManager.getTotalScoreAllGames()
            val iqPlusValue = ScoreManager.lastIqComponentByGame.values.sum()
            val globalRankingPoints = ScoreManager.getTotalUniqueLevelsCompletedAllGames().toLong()
            val accountLinked = prefs.getBoolean(SettingsActivity.ACCOUNT_LINKED, false)

            val lightUserData = hashMapOf(
                "username" to userName,
                "countryCode" to countryCode,
                "hasInsigniaRIPlus" to hasInsigniaRIPlus,
                "iqPlus" to iqPlusValue,
                "global_ranking_points" to globalRankingPoints,
                "account_linked" to accountLinked,
                "welcomeMissCounter" to welcomeMissCounter,
                "insignia_ri_plus_vista" to prefs.getBoolean("insignia_ri_plus_vista", false),
                "lastUpdate" to FieldValue.serverTimestamp()
            )

            val globalRankingPublicData = hashMapOf(
                "username" to userName,
                "countryCode" to countryCode,
                "totalPoints" to totalScoreAllGames,
                "hasInsigniaRIPlus" to hasInsigniaRIPlus,
                "updated_at" to FieldValue.serverTimestamp()
            )

            firestore.collection("usuarios")
                .document(user.uid)
                .set(lightUserData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("DataSyncManager", "Documento ligero usuarios/${user.uid} sincronizado correctamente")

                    firestore.collection("rankings_global")
                        .document(user.uid)
                        .set(globalRankingPublicData, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("DataSyncManager", "Documento rankings_global/${user.uid} sincronizado correctamente")

                            uploadIQPlusToFirebase(
                                userId = user.uid,
                                userName = userName,
                                country = countryCode,
                                iqPlus = iqPlusValue
                            )

                            val avgNumeros = if (ScoreManager.totalGamesNumerosPlusExitos > 0)
                                ScoreManager.totalTimeNumerosPlusExitos / ScoreManager.totalGamesNumerosPlusExitos
                            else Double.POSITIVE_INFINITY

                            val avgDeci = if (ScoreManager.totalGamesDeciPlusExitos > 0)
                                ScoreManager.totalTimeDeciPlusExitos / ScoreManager.totalGamesDeciPlusExitos
                            else Double.POSITIVE_INFINITY

                            val avgRomas = if (ScoreManager.totalGamesRomasExitos > 0)
                                ScoreManager.totalTimeRomasExitos / ScoreManager.totalGamesRomasExitos
                            else Double.POSITIVE_INFINITY

                            val avgAlfa = if (ScoreManager.totalGamesAlfaNumerosExitos > 0)
                                ScoreManager.totalTimeAlfaNumerosExitos / ScoreManager.totalGamesAlfaNumerosExitos
                            else Double.POSITIVE_INFINITY

                            val avgSumaResta = if (ScoreManager.totalGamesSumaRestaExitos > 0)
                                ScoreManager.totalTimeSumaRestaExitos / ScoreManager.totalGamesSumaRestaExitos
                            else Double.POSITIVE_INFINITY

                            val avgMas = if (ScoreManager.totalGamesMasPlusExitos > 0)
                                ScoreManager.totalTimeMasPlusExitos / ScoreManager.totalGamesMasPlusExitos
                            else Double.POSITIVE_INFINITY

                            val avgGenios = if (ScoreManager.totalGamesGenioPlusExitos > 0)
                                ScoreManager.totalTimeGenioPlusExitos / ScoreManager.totalGamesGenioPlusExitos
                            else Double.POSITIVE_INFINITY

                            uploadSpeedRankingToFirebase(user.uid, userName, countryCode, "NumerosPlus", avgNumeros)
                            uploadSpeedRankingToFirebase(user.uid, userName, countryCode, "DeciPlus", avgDeci)
                            uploadSpeedRankingToFirebase(user.uid, userName, countryCode, "Romas", avgRomas)
                            uploadSpeedRankingToFirebase(user.uid, userName, countryCode, "AlfaNumeros", avgAlfa)
                            uploadSpeedRankingToFirebase(user.uid, userName, countryCode, "SumaResta", avgSumaResta)
                            uploadSpeedRankingToFirebase(user.uid, userName, countryCode, "MasPlus", avgMas)
                            uploadSpeedRankingToFirebase(user.uid, userName, countryCode, "GenioPlus", avgGenios)

                            uploadIntegralRankingToFirebase(
                                userId = user.uid,
                                userName = userName,
                                country = countryCode,
                                integralScore = ScoreManager.calculateIntegralScore()
                            )

                            // Validación puntual de referido solo cuando esta sync viene del flujo de LevelResult
                            if (validateReferralIfNeeded) {
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                                    try {
                                        ReferralManager.checkAndValidateReferral(context)
                                    } catch (e: Exception) {
                                        Log.e("DataSyncManager", "Error validando referido desde sync ligera: ${e.message}", e)
                                    }
                                }
                            }

                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "DataSyncManager",
                                "Error escribiendo rankings_global/${user.uid}: ${e.localizedMessage}",
                                e
                            )
                            onResult(false, e.localizedMessage ?: "Error escribiendo ranking global")
                        }
                }
                .addOnFailureListener { e ->
                    Log.e(
                        "DataSyncManager",
                        "Error escribiendo usuarios/${user.uid} en sync ligera: ${e.localizedMessage}",
                        e
                    )
                    onResult(false, e.localizedMessage)
                }

        } catch (e: Exception) {
            Log.e("DataSyncManager", "EXCEPCIÓN syncDataToCloud ligera: ${e.message}", e)
            onResult(false, e.message)
        }
    }

    fun syncHeavyDataToCloud(
        context: Context,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        Log.d("DataSyncManager", "=== INICIO syncHeavyDataToCloud ===")

        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user == null) {
            Log.d("DataSyncManager", "Usuario no autenticado en syncHeavyDataToCloud")
            onResult(false, context.getString(R.string.user_not_authenticated))
            return
        }

        try {
            val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            val profileBox = buildProfilePreferencesBox(context)
            val scoreJson = ScoreManager.exportAllDataAsJson(context)
            val condecoJson = CondecoracionTracker.exportAllDataAsJson(context)

            val privateData = hashMapOf(
                "profile_preferences" to profileBox,
                "score_data" to scoreJson,
                "condecoracion_data" to condecoJson
            )

            val heavyData = hashMapOf(
                "private" to privateData,
                "score_schema_version" to SCHEMA_VERSION_SCORE,
                "condecoracion_schema_version" to SCHEMA_VERSION_CONDECO,
                "account_linked" to prefs.getBoolean(SettingsActivity.ACCOUNT_LINKED, false),
                "lastUpdate" to FieldValue.serverTimestamp()
            )

            firestore.collection("usuarios")
                .document(user.uid)
                .set(heavyData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("DataSyncManager", "syncHeavyDataToCloud OK usuarios/${user.uid}")
                    onResult(true, null)
                }
                .addOnFailureListener { e ->
                    Log.e(
                        "DataSyncManager",
                        "syncHeavyDataToCloud ERROR usuarios/${user.uid}: ${e.localizedMessage}",
                        e
                    )
                    onResult(false, e.localizedMessage)
                }

        } catch (e: Exception) {
            Log.e("DataSyncManager", "EXCEPCIÓN syncHeavyDataToCloud: ${e.message}", e)
            onResult(false, e.message)
        }
    }

    fun syncDataFromCloud(
        context: Context,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        Log.d("RankingDebug", "syncDataFromCloud - INICIO")
        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (user == null) {
            Log.d("RankingDebug", "syncDataFromCloud - user==null → onResult(false)")
            onResult(false, context.getString(R.string.user_not_authenticated))
            return
        }

        Log.d("RankingDebug", "syncDataFromCloud - consultando Firestore uid=${user.uid}")
        firestore.collection("usuarios")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                try {
                    Log.d("RankingDebug", "syncDataFromCloud - Firestore respondió. doc.exists=${doc.exists()}")
                    if (!doc.exists()) {
                        Log.d("RankingDebug", "syncDataFromCloud - doc no existe → onResult(true)")
                        onResult(true, null)
                        return@addOnSuccessListener
                    }

                    val privateData = doc.get("private") as? Map<*, *>
                    Log.d("RankingDebug", "syncDataFromCloud - privateData=${if (privateData != null) "presente" else "null"}")

                    val canjeEnabled = doc.getBoolean("canje_enabled") ?: false
                    if (canjeEnabled) {
                        prefs.edit { putBoolean("canje_enabled", true) }
                    }

                    val insigniaVista = doc.getBoolean("insignia_ri_plus_vista") ?: false
                    if (insigniaVista) {
                        prefs.edit { putBoolean("insignia_ri_plus_vista", true) }
                    }

                    if (privateData != null) {
                        (privateData["profile_preferences"] as? Map<*, *>)?.let {
                            applyProfilePreferencesBox(context, it)
                        }
                        (privateData["score_data"] as? String)?.let { json ->
                            Log.d("RankingDebug", "syncDataFromCloud - importando score_data")
                            ScoreManager.importAllDataFromJson(context, json)
                        }
                        (privateData["condecoracion_data"] as? String)?.let { json ->
                            CondecoracionTracker.init(context)
                            CondecoracionTracker.importAllDataFromJson(context, json)
                        }
                    }

                    val currentSavedName = prefs.getString("savedUserName", null)
                    if (currentSavedName.isNullOrBlank()) {
                        val topLevelUsername = doc.getString("username")
                        if (!topLevelUsername.isNullOrBlank()) {
                            prefs.edit { putString("savedUserName", topLevelUsername) }
                            Log.d("DataSync", "Fallback: savedUserName restaurado desde username principal: $topLevelUsername")
                        }
                    }

                    Log.d("RankingDebug", "syncDataFromCloud - todo ok → onResult(true)")
                    onResult(true, null)

                } catch (e: Exception) {
                    Log.e("RankingDebug", "syncDataFromCloud - EXCEPCION: ${e.message}", e)
                    onResult(false, e.message)
                }
            }
            .addOnFailureListener { e ->
                Log.d("RankingDebug", "syncDataFromCloud - FALLO Firestore: ${e.localizedMessage}")
                onResult(false, e.localizedMessage)
            }
    }

    private fun buildProfilePreferencesBox(context: Context): Map<String, Any> {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val map = hashMapOf<String, Any>(
            "schema_version" to SCHEMA_VERSION_PROFILE,
            "updated_at" to System.currentTimeMillis(),
            "savedUserName" to (prefs.getString("savedUserName", "") ?: ""),
            "savedCountryCode" to (prefs.getString("savedCountryCode", "sumamente") ?: "sumamente"),
            "selected_language" to (prefs.getString("selected_language", "en") ?: "en"),

            SettingsActivity.SOUND_ENABLED to prefs.getBoolean(SettingsActivity.SOUND_ENABLED, true),
            SettingsActivity.NOTIFICATIONS_ENABLED to prefs.getBoolean(SettingsActivity.NOTIFICATIONS_ENABLED, true),
            SettingsActivity.ADS_ENABLED to prefs.getBoolean(SettingsActivity.ADS_ENABLED, true),
            SettingsActivity.ACCOUNT_LINKED to prefs.getBoolean(SettingsActivity.ACCOUNT_LINKED, false)
        )

        val allPrefsNames = listOf(

            "MyPrefs", "MyPrefsDeciPlus", "MyPrefsRomas", "MyPrefsAlfaNumeros",
            "MyPrefsSumaResta", "MyPrefsMasPlus", "MyPrefsGenioPlus", "MyPrefsFocoPlus",

            "ScorePrefs", "ScorePrefsPrincipiante", "ScorePrefsPro",
            "ScorePrefsDeciPlus", "ScorePrefsDeciPlusPrincipiante", "ScorePrefsDeciPlusPro",
            "ScorePrefsRomas", "ScorePrefsRomasPrincipiante", "ScorePrefsRomasPro",
            "ScorePrefsAlfaNumeros", "ScorePrefsAlfaNumerosPrincipiante", "ScorePrefsAlfaNumerosPro",
            "ScorePrefsSumaResta", "ScorePrefsSumaRestaPrincipiante", "ScorePrefsSumaRestaPro",
            "ScorePrefsMasPlus", "ScorePrefsMasPlusPrincipiante", "ScorePrefsMasPlusPro",
            "ScorePrefsGenioPlus", "ScorePrefsGenioPlusPrincipiante", "ScorePrefsGenioPlusPro",
            "ScorePrefsFocoPlus", "ScorePrefsFocoPlusPrincipiante", "ScorePrefsFocoPlusPro"
        )

        val allGamePrefs = mutableMapOf<String, Map<String, Any>>()

        allPrefsNames.forEach { prefsName ->
            val gamePrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            val prefMap = mutableMapOf<String, Any>()

            gamePrefs.all.forEach { (key, value) ->
                when (value) {
                    is Set<*> -> {

                        prefMap[key] = value.toList()
                    }
                    is Boolean -> prefMap[key] = value
                    is Int -> prefMap[key] = value
                    is Long -> prefMap[key] = value
                    is Float -> prefMap[key] = value
                    is String -> prefMap[key] = value
                    else -> prefMap[key] = value?.toString() ?: ""
                }
            }

            if (prefMap.isNotEmpty()) {
                allGamePrefs[prefsName] = prefMap
            }
        }

        map["game_preferences"] = allGamePrefs
        return map
    }

    private fun applyProfilePreferencesBox(context: Context, raw: Map<*, *>) {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val username = raw["savedUserName"] as? String
        Log.d("DataSync", "Restaurando savedUserName: $username")

        val country = raw["savedCountryCode"] as? String
        val language = raw["selected_language"] as? String
        val sound = raw[SettingsActivity.SOUND_ENABLED] as? Boolean
        val notif = raw[SettingsActivity.NOTIFICATIONS_ENABLED] as? Boolean
        val ads = raw[SettingsActivity.ADS_ENABLED] as? Boolean
        val linked = raw[SettingsActivity.ACCOUNT_LINKED] as? Boolean ?: false

        prefs.edit {
            username?.let { putString("savedUserName", it) }
            country?.let { putString("savedCountryCode", it) }
            language?.let { putString("selected_language", it) }
            sound?.let { putBoolean(SettingsActivity.SOUND_ENABLED, it) }
            notif?.let { putBoolean(SettingsActivity.NOTIFICATIONS_ENABLED, it) }
            ads?.let { putBoolean(SettingsActivity.ADS_ENABLED, it) }
            putBoolean(SettingsActivity.ACCOUNT_LINKED, linked)
        }

        fun shouldBeIntKey(key: String): Boolean =
            key.startsWith("current_score") ||
                    key.contains("unlocked_levels") ||
                    key.startsWith("total_games") ||
                    key.startsWith("consecutive_failures") ||
                    key.contains("consecutive_failures:")

        fun shouldBeFloatKey(key: String): Boolean =
            key.startsWith("total_time")

        fun shouldBeStringSetKey(key: String): Boolean =
            key.contains("completed_levels")

        val allGamePrefs = raw["game_preferences"] as? Map<*, *>
        allGamePrefs?.forEach { (prefsName, prefsData) ->
            val gamePrefs = context.getSharedPreferences(prefsName as String, Context.MODE_PRIVATE)

            gamePrefs.edit {
                clear()

                (prefsData as? Map<*, *>)?.forEach { (key, value) ->
                    val keyStr = key as String

                    when (value) {
                        is Boolean -> putBoolean(keyStr, value)
                        is Float -> putFloat(keyStr, value)
                        is Double -> {
                            if (shouldBeFloatKey(keyStr)) {
                                putFloat(keyStr, value.toFloat())
                            } else {
                                putString(keyStr, value.toString())
                            }
                        }
                        is Long -> {
                            if (shouldBeIntKey(keyStr)) {
                                putInt(keyStr, value.toInt())
                            } else {
                                putLong(keyStr, value)
                            }
                        }
                        is Int -> putInt(keyStr, value)
                        is String -> putString(keyStr, value)
                        is List<*> -> {
                            if (shouldBeStringSetKey(keyStr)) {
                                val stringSet = value.filterIsInstance<String>().toSet()
                                putStringSet(keyStr, stringSet)
                            } else {
                                putString(keyStr, value.toString())
                            }
                        }
                        else -> {
                            putString(keyStr, value?.toString() ?: "")
                        }
                    }
                }
            }
        }
    }

    fun getLocalAndCloudProgress(
        context: Context,
        callback: (localProgress: String?, cloudProgress: String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        val localProgress = ScoreManager.exportAllDataAsJson(context)

        if (user == null) {
            callback(localProgress, null)
            return
        }

        firestore.collection("usuarios")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val privateData = doc.get("private") as? Map<*, *>
                val cloudProgress = privateData?.get("score_data") as? String
                callback(localProgress, cloudProgress)
            }
            .addOnFailureListener {
                callback(localProgress, null)
            }
    }

    fun deleteAccountData(context: Context, onResult: (success: Boolean, error: String?) -> Unit) {
        val user = auth.currentUser

        if (user == null) {
            clearAllLocalData(context)
            onResult(true, null)
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val sharedPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPrefs.getString("savedUserName", null)
        val batch = firestore.batch()

        if (username != null) {
            batch.delete(firestore.collection("usernames").document(username))
        }

        batch.delete(firestore.collection("usuarios").document(user.uid))
        batch.delete(firestore.collection("rankings_global").document(user.uid))
        batch.delete(firestore.collection("rankings_iqplus").document(user.uid))
        batch.delete(firestore.collection("rankings_speed").document(user.uid))
        batch.delete(firestore.collection("rankings_integral").document(user.uid))

        batch.commit()
            .addOnSuccessListener {

                user.delete()
                    .addOnSuccessListener {
                        clearAllLocalData(context)

                        if (!user.isAnonymous) {
                            signOutFromGoogle(context)
                        }
                        onResult(true, null)
                    }
                    .addOnFailureListener { authException ->

                        val errorMessage = if (authException is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                            context.getString(R.string.account_delete_error_auth_recent)
                        } else {
                            context.getString(R.string.account_deleted_error_auth)
                        }
                        onResult(false, errorMessage)
                    }
            }
            .addOnFailureListener { _ ->

                onResult(false, context.getString(R.string.account_deleted_error_firestore))
            }
    }

    fun clearAllLocalData(context: Context) {

        val allPrefsNames = listOf(
            "MyPrefs",
            "MyPrefsDeciPlus", "MyPrefsRomas", "MyPrefsAlfaNumeros",
            "MyPrefsSumaResta", "MyPrefsMasPlus", "MyPrefsGenioPlus", "MyPrefsFocoPlus",

            "ScorePrefs", "ScorePrefsPrincipiante", "ScorePrefsPro",
            "ScorePrefsDeciPlus", "ScorePrefsDeciPlusPrincipiante", "ScorePrefsDeciPlusPro",
            "ScorePrefsRomas", "ScorePrefsRomasPrincipiante", "ScorePrefsRomasPro",
            "ScorePrefsAlfaNumeros", "ScorePrefsAlfaNumerosPrincipiante", "ScorePrefsAlfaNumerosPro",
            "ScorePrefsSumaResta", "ScorePrefsSumaRestaPrincipiante", "ScorePrefsSumaRestaPro",
            "ScorePrefsMasPlus", "ScorePrefsMasPlusPrincipiante", "ScorePrefsMasPlusPro",
            "ScorePrefsGenioPlus", "ScorePrefsGenioPlusPrincipiante", "ScorePrefsGenioPlusPro",
            "ScorePrefsFocoPlus", "ScorePrefsFocoPlusPrincipiante", "ScorePrefsFocoPlusPro",

            "CondecoracionPrefs",
            "AuthPrefs"
        )

        allPrefsNames.forEach { prefsName ->
            context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                .edit { clear() }
        }
    }

    private fun signOutFromGoogle(context: Context) {
        auth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(context, gso).apply {
            signOut()
            revokeAccess()
        }
    }

    fun uploadIQPlusToFirebase(
        userId: String,
        userName: String,
        country: String,
        iqPlus: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("rankings_iqplus").document(userId)


        if (!ScoreManager.isEligibleIQPlusRanking() || !iqPlus.isFinite()) {
            userDoc.update(mapOf("iqPlus" to FieldValue.delete()))
            return
        }

        val data = hashMapOf(
            "iqPlus" to iqPlus,
            "hasInsigniaRIPlus" to (CondecoracionTracker.getInsigniaRIPlus() != null),
            "username" to userName,
            "countryCode" to country
        )
        userDoc.set(data, SetOptions.merge())
    }

    fun getTopIQPlusRanking(
        userId: String,
        userName: String,
        country: String,
        iqPlus: Double,
        callback: (List<IQPlusRankingItem>, Int, IQPlusRankingItem?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        db.collection("rankings_iqplus")
            .orderBy("iqPlus", Query.Direction.DESCENDING)
            .limit(200)
            .get()
            .addOnSuccessListener { result ->
                val rankingList = mutableListOf<IQPlusRankingItem>()
                var userPosition = -1
                var userItem: IQPlusRankingItem? = null
                var currentPosition = 1
                var previousIQPlus: Double? = null

                for (doc in result) {
                    val name = doc.getString("username") ?: ""
                    val code = doc.getString("countryCode") ?: "us"
                    val value = (doc.get("iqPlus") as? Number)?.toDouble() ?: 0.0
                    val thisUserId = doc.id
                    val isCurrent = thisUserId == userId
                    val hasInsignia = doc.getBoolean("hasInsigniaRIPlus") ?: false

                    if (previousIQPlus != null && value < previousIQPlus) {
                        currentPosition++
                    }

                    val item = IQPlusRankingItem(
                        position = currentPosition,
                        username = name,
                        countryCode = code,
                        iqPlus = value,
                        isCurrentUser = isCurrent,
                        hasInsigniaRIPlus = hasInsignia
                    )
                    rankingList.add(item)
                    if (isCurrent) {
                        userPosition = currentPosition
                        userItem = item
                    }
                    previousIQPlus = value
                }

                // Si el usuario NO está en el top 200, busca su posición real
                if (userPosition == -1) {
                    db.collection("rankings_iqplus")
                        .whereGreaterThan("iqPlus", iqPlus)
                        .get()
                        .addOnSuccessListener { others ->
                            userPosition = others.size() + 1
                            userItem = IQPlusRankingItem(
                                position = userPosition,
                                username = userName,
                                countryCode = country,
                                iqPlus = iqPlus,
                                isCurrentUser = true,
                                hasInsigniaRIPlus = (CondecoracionTracker.getInsigniaRIPlus() != null)
                            )
                            callback(rankingList, userPosition, userItem)
                        }
                        .addOnFailureListener { callback(rankingList, -1, null) }
                } else {
                    callback(rankingList, userPosition, userItem)
                }
            }
            .addOnFailureListener { callback(emptyList(), -1, null) }
    }

    fun uploadSpeedRankingToFirebase(
        userId: String,
        userName: String,
        country: String,
        gameType: String,
        averageTime: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("rankings_speed").document(userId)


        val fieldName = "speed_ranking_$gameType"

        val isEligible = when (gameType) {
            "NumerosPlus"  -> ScoreManager.isEligibleForSpeedRankingNumerosPlus()
            "DeciPlus"     -> ScoreManager.isEligibleForSpeedRankingDeciPlus()
            "Romas"        -> ScoreManager.isEligibleForSpeedRankingRomas()
            "AlfaNumeros"  -> ScoreManager.isEligibleForSpeedRankingAlfaNumeros()
            "SumaResta"    -> ScoreManager.isEligibleForSpeedRankingSumaResta()
            "MasPlus"      -> ScoreManager.isEligibleForSpeedRankingMasPlus()
            "GenioPlus"    -> ScoreManager.isEligibleForSpeedRankingGenioPlus()
            else -> false
        }

        if (!isEligible || !averageTime.isFinite()) {
            userDoc.set(mapOf(fieldName to FieldValue.delete()), SetOptions.merge())
            return
        }

        val data = hashMapOf(
            fieldName to hashMapOf(
                "averageTime" to averageTime,
                "updated_at" to FieldValue.serverTimestamp()
            ),
            "username" to userName,
            "countryCode" to country,
            "hasInsigniaRIPlus" to (CondecoracionTracker.getInsigniaRIPlus() != null)
        )
        userDoc.set(data, SetOptions.merge())
    }

    fun getTopSpeedRanking(
        userId: String,
        userName: String,
        country: String,
        gameType: String,
        averageTime: Double,
        callback: (List<SpeedRankingItem>, Int, SpeedRankingItem?, Int) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val fieldBase = "speed_ranking_$gameType"
        val fieldAvg = "$fieldBase.averageTime"

        db.collection("rankings_speed")
            .orderBy(fieldAvg)
            .limit(200)
            .get()
            .addOnSuccessListener { result ->
                val rankingList = mutableListOf<SpeedRankingItem>()
                var userPosition = -1
                var userItem: SpeedRankingItem? = null
                var currentPosition = 1
                var previousTime: Double? = null

                for (doc in result) {
                    val timeData = doc.get(fieldBase) as? Map<*, *>
                    val value = (timeData?.get("averageTime") as? Number)?.toDouble() ?: Double.POSITIVE_INFINITY

                    if (!value.isFinite() || value <= 0.0) continue

                    val name = doc.getString("username") ?: ""
                    val code = doc.getString("countryCode") ?: "us"
                    val thisUserId = doc.id
                    val isCurrent = thisUserId == userId
                    val hasInsignia = doc.getBoolean("hasInsigniaRIPlus") ?: false

                    if (previousTime != null && value > previousTime) {
                        currentPosition++
                    }

                    val item = SpeedRankingItem(
                        position = currentPosition,
                        username = name,
                        countryCode = code,
                        averageTime = value.toFloat(),
                        isCurrentUser = isCurrent,
                        hasInsigniaRIPlus = hasInsignia
                    )
                    rankingList.add(item)

                    if (isCurrent) {
                        userPosition = currentPosition
                        userItem = item
                    }

                    previousTime = value
                }

                fun fetchCountThen(pos: Int, item: SpeedRankingItem?) {
                    db.collection("rankings_speed")
                        .orderBy(fieldAvg)
                        .count()
                        .get(AggregateSource.SERVER)
                        .addOnSuccessListener { countSnapshot ->
                            callback(rankingList, pos, item, countSnapshot.count.toInt())
                        }
                        .addOnFailureListener {
                            callback(rankingList, pos, item, 0)
                        }
                }

                if (userPosition == -1) {
                    db.collection("rankings_speed")
                        .whereLessThan(fieldAvg, averageTime)
                        .get()
                        .addOnSuccessListener { betterUsers ->
                            val pos = betterUsers.size() + 1
                            val item = SpeedRankingItem(
                                position = pos,
                                username = userName,
                                countryCode = country,
                                averageTime = averageTime.toFloat(),
                                isCurrentUser = true,
                                hasInsigniaRIPlus = (CondecoracionTracker.getInsigniaRIPlus() != null)
                            )
                            fetchCountThen(pos, item)
                        }
                        .addOnFailureListener {
                            callback(rankingList, -1, null, 0)
                        }
                } else {
                    fetchCountThen(userPosition, userItem)
                }
            }
            .addOnFailureListener {
                callback(emptyList(), -1, null, 0)
            }
    }

    fun getTopGlobalRanking(
        userId: String,
        userName: String,
        country: String,
        totalPoints: Long,
        callback: (List<GlobalRankingItem>, Int, GlobalRankingItem?, Int) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        Log.d("RankingDebug", "getTopGlobalRanking - consultando top 200 por totalPoints")

        db.collection("rankings_global")
            .orderBy("totalPoints", Query.Direction.DESCENDING)
            .limit(200)
            .get()
            .addOnSuccessListener { result ->
                val rankingList = mutableListOf<GlobalRankingItem>()
                var userPosition = -1
                var userItem: GlobalRankingItem? = null
                var currentPosition = 1
                var previousPoints: Long? = null

                for (doc in result) {
                    val name = doc.getString("username")?.trim().orEmpty()
                    if (name.isBlank()) continue

                    val code = doc.getString("countryCode") ?: ""
                    val points = (doc.get("totalPoints") as? Number)?.toLong() ?: 0L
                    val thisUserId = doc.id
                    val isCurrent = thisUserId == userId
                    val hasInsignia = doc.getBoolean("hasInsigniaRIPlus") ?: false

                    if (previousPoints != null && points < previousPoints) {
                        currentPosition++
                    }

                    val item = GlobalRankingItem(
                        position = currentPosition,
                        username = name,
                        countryCode = code,
                        totalPoints = points,
                        isCurrentUser = isCurrent,
                        hasInsigniaRIPlus = hasInsignia
                    )
                    rankingList.add(item)

                    if (isCurrent) {
                        userPosition = currentPosition
                        userItem = item
                    }
                    previousPoints = points
                }

                if (userPosition == -1) {
                    db.collection("rankings_global")
                        .whereGreaterThan("totalPoints", totalPoints)
                        .get()
                        .addOnSuccessListener { others ->
                            val pos = others.size() + 1
                            val item = GlobalRankingItem(
                                position = pos,
                                username = userName,
                                countryCode = country,
                                totalPoints = totalPoints,
                                isCurrentUser = true,
                                hasInsigniaRIPlus = (CondecoracionTracker.getInsigniaRIPlus() != null)
                            )
                            db.collection("rankings_global")
                                .count()
                                .get(AggregateSource.SERVER)
                                .addOnSuccessListener { countSnapshot ->
                                    callback(rankingList, pos, item, countSnapshot.count.toInt())
                                }
                                .addOnFailureListener {
                                    callback(rankingList, pos, item, 0)
                                }
                        }
                        .addOnFailureListener { callback(rankingList, -1, null, 0) }
                } else {
                    db.collection("rankings_global")
                        .count()
                        .get(AggregateSource.SERVER)
                        .addOnSuccessListener { countSnapshot ->
                            callback(rankingList, userPosition, userItem, countSnapshot.count.toInt())
                        }
                        .addOnFailureListener {
                            callback(rankingList, userPosition, userItem, 0)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("RankingDebug", "getTopGlobalRanking FAILURE error=${e.message}", e)
                callback(emptyList(), -1, null, 0)
            }
    }

    fun uploadIntegralRankingToFirebase(
        userId: String,
        userName: String,
        country: String,
        integralScore: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("rankings_integral").document(userId)

        val required = listOf(
            "GLOBAL","VEL_NUMEROS","VEL_DECI","VEL_ALFANUM","VEL_ROMAS",
            "VEL_SUMARESTA","VEL_MAS","VEL_GENIOS","IQ_PLUS"
        )
        val eligible = required.all { tag -> ScoreManager.isUserInRanking(tag) }

        val validScore = integralScore.isFinite() && integralScore >= 0.0 && integralScore <= 100.0

        if (!eligible || !validScore) {

            userDoc.set(mapOf("integral_ranking" to FieldValue.delete()), SetOptions.merge())
            return
        }

        val rounded = kotlin.math.round(integralScore * 1000.0) / 1000.0

        val data = hashMapOf(
            "integral_ranking" to hashMapOf(
                "integralScore" to rounded,
                "updated_at" to FieldValue.serverTimestamp()
            ),
            "username" to userName,
            "countryCode" to country,
            "hasInsigniaRIPlus" to (CondecoracionTracker.getInsigniaRIPlus() != null)
        )
        userDoc.set(data, SetOptions.merge())
    }

    fun getTopIntegralRanking(
        userId: String,
        userName: String,
        country: String,
        integralScore: Double,
        callback: (List<IntegralRankingItem>, Int, IntegralRankingItem?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val fieldBase = "integral_ranking"
        val fieldAvg = "$fieldBase.integralScore"

        db.collection("rankings_integral")
            .orderBy(fieldAvg, Query.Direction.DESCENDING)
            .limit(200)
            .get()
            .addOnSuccessListener { result ->
                val rankingList = mutableListOf<IntegralRankingItem>()
                var userPosition = -1
                var userItem: IntegralRankingItem? = null
                var currentPosition = 1
                var previousAverage: Double? = null

                for (doc in result) {
                    val integralData = doc.get(fieldBase) as? Map<*, *>
                    val value = (integralData?.get("integralScore") as? Number)?.toDouble() ?: -1.0

                    if (!value.isFinite() || value < 0.0) continue

                    val name = doc.getString("username") ?: ""
                    val code = doc.getString("countryCode") ?: "us"
                    val thisUserId = doc.id
                    val isCurrent = thisUserId == userId
                    val hasInsignia = doc.getBoolean("hasInsigniaRIPlus") ?: false


                    if (previousAverage != null && value < previousAverage) {
                        currentPosition++
                    }

                    val item = IntegralRankingItem(
                        position = currentPosition,
                        username = name,
                        countryCode = code,
                        integralScore = value,
                        isCurrentUser = isCurrent,
                        hasInsigniaRIPlus = hasInsignia
                    )
                    rankingList.add(item)

                    if (isCurrent) {
                        userPosition = currentPosition
                        userItem = item
                    }

                    previousAverage = value
                }

                if (userPosition == -1) {

                    db.collection("rankings_integral")
                        .whereGreaterThan(fieldAvg, integralScore)
                        .get()
                        .addOnSuccessListener { betterUsers ->
                            userPosition = betterUsers.size() + 1
                            userItem = IntegralRankingItem(
                                position = userPosition,
                                username = userName,
                                countryCode = country,
                                integralScore = integralScore,
                                isCurrentUser = true,
                                hasInsigniaRIPlus = (CondecoracionTracker.getInsigniaRIPlus() != null)
                            )
                            callback(rankingList, userPosition, userItem)
                        }
                        .addOnFailureListener { callback(rankingList, -1, null) }
                } else {
                    callback(rankingList, userPosition, userItem)
                }
            }
            .addOnFailureListener { callback(emptyList(), -1, null) }
    }

    fun updateCanjeStatus(status: Boolean) {
        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser ?: return

        firestore.collection("usuarios")
            .document(user.uid)
            .set(
                hashMapOf(
                    "canje_enabled" to status
                ),
                SetOptions.merge()
            )
    }


}
