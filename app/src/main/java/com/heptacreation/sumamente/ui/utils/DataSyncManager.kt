package com.heptacreation.sumamente.ui.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
import com.google.firebase.messaging.FirebaseMessaging


object DataSyncManager {

    private val auth get() = FirebaseAuth.getInstance()

    private const val SCHEMA_VERSION_PROFILE = 1
    private const val SCHEMA_VERSION_SCORE = 2
    private const val SCHEMA_VERSION_CONDECO = 2


    fun syncDataToCloud(
        context: Context,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        Log.d("DataSyncManager", "=== INICIO syncDataToCloud ===")

        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user == null) {
            Log.d("DataSyncManager", "Usuario no autenticado")
            onResult(false, context.getString(R.string.user_not_authenticated))
            return
        }

        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isFirstTimeUser = prefs.getBoolean("is_first_run", true)
        val hasCompleted12Levels = ScoreManager.hasCompleted12LevelsInAnyGame()
        val totalLevels = ScoreManager.getTotalUniqueLevelsCompletedAllGames()

        Log.d("DataSyncManager", "isFirstTimeUser: $isFirstTimeUser")
        Log.d("DataSyncManager", "hasCompleted12Levels: $hasCompleted12Levels")
        Log.d("DataSyncManager", "totalLevels: $totalLevels")

        if (isFirstTimeUser || hasCompleted12Levels) {
            Log.d("DataSyncManager", "Condición cumplida - ejecutando validación de referidos")

            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    Log.d("DataSyncManager", "Iniciando corrutina de validación")
                    val validated = ReferralManager.checkAndValidateReferral(context)
                    Log.d("DataSyncManager", "Resultado validación: $validated")

                    if (validated) {
                        prefs.edit {
                            putBoolean("is_first_run", false)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DataSyncManager", "Error al validar referido: ${e.message}")
                }
            }
        } else {
            Log.d("DataSyncManager", "Condición NO cumplida - saltando validación de referidos")
        }

        val profileBox = buildProfilePreferencesBox(context)
        val scoreJson = ScoreManager.exportAllDataAsJson(context)
        val condecoJson = CondecoracionTracker.exportAllDataAsJson(context)
        val userName = prefs.getString("savedUserName", "") ?: ""
        val countryCode = prefs.getString("savedCountryCode", "sumamente") ?: "sumamente"
        val activateCanjeNow = false

        if (activateCanjeNow) {
            updateCanjeStatus(true)
        }

        val privateData = hashMapOf(
            "profile_preferences" to profileBox,
            "score_data" to scoreJson,
            "condecoracion_data" to condecoJson
        )

        val data = hashMapOf(
            "username" to userName,
            "countryCode" to countryCode,
            "lastUpdate" to FieldValue.serverTimestamp(),
            "iqPlus" to ScoreManager.lastIqComponentByGame.values.sum(),
            "global_ranking_points" to ScoreManager.getTotalUniqueLevelsCompletedAllGames().toLong(),
            "private" to privateData,
            "score_schema_version" to SCHEMA_VERSION_SCORE,
            "condecoracion_schema_version" to SCHEMA_VERSION_CONDECO,
            "hasInsigniaRIPlus" to (CondecoracionTracker.getInsigniaRIPlus() != null),
            "account_linked" to prefs.getBoolean(SettingsActivity.ACCOUNT_LINKED, false),
            "lastActive" to FieldValue.serverTimestamp()
        )

        Log.d("DataSyncManager", "Sincronizando datos con Firebase")



        firestore.collection("usuarios")
            .document(user.uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("DataSyncManager", "Sincronización exitosa")

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    FirebaseMessaging.getInstance().token
                        .addOnSuccessListener { tk ->
                            FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(uid)
                                .set(mapOf("fcmToken" to tk), SetOptions.merge())
                                .addOnSuccessListener { Log.d("DataSyncManager", "fcmToken (post-sync) guardado con merge") }
                                .addOnFailureListener { e -> Log.e("DataSyncManager", "Error guardando fcmToken post-sync", e) }
                        }
                        .addOnFailureListener { e -> Log.e("DataSyncManager", "No se pudo obtener token FCM post-sync", e) }
                }

                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("DataSyncManager", "Error en sincronización: ${e.localizedMessage}")
                onResult(false, e.localizedMessage)
            }
    }

    fun syncLightweightToCloud(context: Context) {
        val user = auth.currentUser ?: return
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("savedUserName", "") ?: ""
        val countryCode = prefs.getString("savedCountryCode", "sumamente") ?: "sumamente"

        val data = hashMapOf(
            "username" to userName,
            "countryCode" to countryCode,
            "lastActive" to FieldValue.serverTimestamp()
        )

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(user.uid)
            .set(data, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.e("DataSyncManager", "Error en sync liviano: ${e.localizedMessage}")
            }
    }

    fun syncDataFromCloud(
        context: Context,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        if (user == null) {
            onResult(false, context.getString(R.string.user_not_authenticated))
            return
        }

        firestore.collection("usuarios")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onResult(true, null)
                    return@addOnSuccessListener
                }

                val privateData = doc.get("private") as? Map<*, *>

                val canjeEnabled = doc.getBoolean("canje_enabled") ?: false
                if (canjeEnabled) {
                    prefs.edit { putBoolean("canje_enabled", true) }
                }

                if (privateData != null) {
                    // 1) Perfil
                    (privateData["profile_preferences"] as? Map<*, *>)?.let {
                        applyProfilePreferencesBox(context, it)
                    }
                    // 2) Progreso
                    (privateData["score_data"] as? String)?.let { json ->
                        ScoreManager.importAllDataFromJson(context, json)
                    }
                    // 3) Condecoraciones
                    (privateData["condecoracion_data"] as? String)?.let { json ->
                        CondecoracionTracker.init(context)
                        CondecoracionTracker.importAllDataFromJson(context, json)
                    }
                }

                onResult(true, null)
            }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
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
            "MyPrefsSumaResta", "MyPrefsMasPlus", "MyPrefsGenioPlus",

            "ScorePrefs", "ScorePrefsPrincipiante", "ScorePrefsPro",
            "ScorePrefsDeciPlus", "ScorePrefsDeciPlusPrincipiante", "ScorePrefsDeciPlusPro",
            "ScorePrefsRomas", "ScorePrefsRomasPrincipiante", "ScorePrefsRomasPro",
            "ScorePrefsAlfaNumeros", "ScorePrefsAlfaNumerosPrincipiante", "ScorePrefsAlfaNumerosPro",
            "ScorePrefsSumaResta", "ScorePrefsSumaRestaPrincipiante", "ScorePrefsSumaRestaPro",
            "ScorePrefsMasPlus", "ScorePrefsMasPlusPrincipiante", "ScorePrefsMasPlusPro",
            "ScorePrefsGenioPlus", "ScorePrefsGenioPlusPrincipiante", "ScorePrefsGenioPlusPro"
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
        val linked = raw[SettingsActivity.ACCOUNT_LINKED] as? Boolean ?: true

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
                val cloudProgress = doc.getString("score_data")
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
            .addOnFailureListener { firestoreException ->

                onResult(false, context.getString(R.string.account_deleted_error_firestore))
            }
    }

    private fun clearAllLocalData(context: Context) {

        val allPrefsNames = listOf(
            "MyPrefs",
            "MyPrefsDeciPlus", "MyPrefsRomas", "MyPrefsAlfaNumeros",
            "MyPrefsSumaResta", "MyPrefsMasPlus", "MyPrefsGenioPlus",
            "ScorePrefs", "ScorePrefsPrincipiante", "ScorePrefsPro",
            "ScorePrefsDeciPlus", "ScorePrefsDeciPlusPrincipiante", "ScorePrefsDeciPlusPro",
            "ScorePrefsRomas", "ScorePrefsRomasPrincipiante", "ScorePrefsRomasPro",
            "ScorePrefsAlfaNumeros", "ScorePrefsAlfaNumerosPrincipiante", "ScorePrefsAlfaNumerosPro",
            "ScorePrefsSumaResta", "ScorePrefsSumaRestaPrincipiante", "ScorePrefsSumaRestaPro",
            "ScorePrefsMasPlus", "ScorePrefsMasPlusPrincipiante", "ScorePrefsMasPlusPro",
            "ScorePrefsGenioPlus", "ScorePrefsGenioPlusPrincipiante", "ScorePrefsGenioPlusPro",
            "CondecoracionPrefs"
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
        val userDoc = db.collection("usuarios").document(userId)

        userDoc.set(
            hashMapOf(
                "profile_preferences" to hashMapOf(
                    "savedUserName" to userName,
                    "savedCountryCode" to country
                )
            ),
            SetOptions.merge()
        )

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
        db.collection("usuarios")
            .orderBy("iqPlus", com.google.firebase.firestore.Query.Direction.DESCENDING)
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
                    db.collection("usuarios")
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
        val userDoc = db.collection("usuarios").document(userId)

        userDoc.set(
            hashMapOf(
                "profile_preferences" to hashMapOf(
                    "savedUserName" to userName,
                    "savedCountryCode" to country
                )
            ),
            SetOptions.merge()
        )

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
            userDoc.update(mapOf(fieldName to FieldValue.delete()))
            return
        }

        val data = hashMapOf(
            fieldName to hashMapOf(
                "averageTime" to averageTime,
                "updated_at" to FieldValue.serverTimestamp()
            ),
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
        callback: (List<SpeedRankingItem>, Int, SpeedRankingItem?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val fieldBase = "speed_ranking_$gameType"
        val fieldAvg = "$fieldBase.averageTime"

        db.collection("usuarios")
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

                if (userPosition == -1) {

                    db.collection("usuarios")
                        .whereLessThan(fieldAvg, averageTime)
                        .get()
                        .addOnSuccessListener { betterUsers ->
                            userPosition = betterUsers.size() + 1
                            userItem = SpeedRankingItem(
                                position = userPosition,
                                username = userName,
                                countryCode = country,
                                averageTime = averageTime.toFloat(),
                                isCurrentUser = true,
                                hasInsigniaRIPlus = (CondecoracionTracker.getInsigniaRIPlus() != null)
                            )
                            callback(rankingList, userPosition, userItem)
                        }
                        .addOnFailureListener {
                            callback(rankingList, -1, null)
                        }
                } else {
                    callback(rankingList, userPosition, userItem)
                }
            }
            .addOnFailureListener {
                callback(emptyList(), -1, null)
            }
    }

    fun uploadGlobalRankingToFirebase(
        userId: String,
        userName: String,
        country: String,
        totalPoints: Long
    ) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("usuarios").document(userId)

        userDoc.set(
            hashMapOf(
                "profile_preferences" to hashMapOf(
                    "savedUserName" to userName,
                    "savedCountryCode" to country
                )
            ),
            SetOptions.merge()
        )

        if (!ScoreManager.isUserInRanking("GLOBAL")) {
            userDoc.update(mapOf("global_ranking" to FieldValue.delete()))
            return
        }

        val data = hashMapOf(
            "global_ranking" to hashMapOf(
                "totalPoints" to totalPoints,
                "updated_at" to FieldValue.serverTimestamp()
            ),
            "hasInsigniaRIPlus" to (CondecoracionTracker.getInsigniaRIPlus() != null)
        )
        userDoc.set(data, SetOptions.merge())
    }

    fun getTopGlobalRanking(
        userId: String,
        userName: String,
        country: String,
        totalPoints: Long,
        callback: (List<GlobalRankingItem>, Int, GlobalRankingItem?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .orderBy("global_ranking.totalPoints", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(200)
            .get()
            .addOnSuccessListener { result ->
                val rankingList = mutableListOf<GlobalRankingItem>()
                var userPosition = -1
                var userItem: GlobalRankingItem? = null
                var currentPosition = 1
                var previousPoints: Long? = null

                for (doc in result) {
                    val pointsData = doc.get("global_ranking") as? Map<*, *>
                    val value = ((pointsData?.get("totalPoints") ?: 0L) as Number).toLong()

                    if (value <= 0L) continue

                    val name = doc.getString("username") ?: ""
                    val code = doc.getString("countryCode") ?: "us"
                    val thisUserId = doc.id
                    val isCurrent = thisUserId == userId
                    val hasInsignia = doc.getBoolean("hasInsigniaRIPlus") ?: false

                    if (previousPoints != null && value < previousPoints) {
                        currentPosition++
                    }

                    val item = GlobalRankingItem(
                        position = currentPosition,
                        username = name,
                        countryCode = code,
                        totalPoints = value,
                        isCurrentUser = isCurrent,
                        hasInsigniaRIPlus = hasInsignia
                    )
                    rankingList.add(item)

                    if (isCurrent) {
                        userPosition = currentPosition
                        userItem = item
                    }

                    previousPoints = value
                }

                if (userPosition == -1) {

                    db.collection("usuarios")
                        .whereGreaterThan("global_ranking.totalPoints", totalPoints)
                        .get()
                        .addOnSuccessListener { betterUsers ->
                            userPosition = betterUsers.size() + 1
                            userItem = GlobalRankingItem(
                                position = userPosition,
                                username = userName,
                                countryCode = country,
                                totalPoints = totalPoints,
                                isCurrentUser = true,
                                hasInsigniaRIPlus = (CondecoracionTracker.getInsigniaRIPlus() != null)
                            )
                            callback(rankingList, userPosition, userItem)
                        }
                        .addOnFailureListener {
                            callback(rankingList, -1, null)
                        }
                } else {
                    callback(rankingList, userPosition, userItem)
                }
            }
            .addOnFailureListener {
                callback(emptyList(), -1, null)
            }
    }

    fun uploadIntegralRankingToFirebase(
        userId: String,
        userName: String,
        country: String,
        averagePosition: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("usuarios").document(userId)

        userDoc.set(
            hashMapOf(
                "profile_preferences" to hashMapOf(
                    "savedUserName" to userName,
                    "savedCountryCode" to country
                )
            ),
            SetOptions.merge()
        )

        val required = listOf(
            "GLOBAL","VEL_NUMEROS","VEL_DECI","VEL_ALFANUM","VEL_ROMAS",
            "VEL_SUMARESTA","VEL_MAS","VEL_GENIOS","IQ_PLUS"
        )
        val eligible = required.all { tag -> ScoreManager.isUserInRanking(tag) }

        val validAvg = averagePosition.isFinite() && averagePosition >= 1.0 && averagePosition <= 1_000_000.0

        if (!eligible || !validAvg) {

            userDoc.update(mapOf("integral_ranking" to FieldValue.delete()))
            return
        }

        val rounded = kotlin.math.round(averagePosition * 1000.0) / 1000.0

        val data = hashMapOf(
            "integral_ranking" to hashMapOf(
                "averagePosition" to rounded,
                "updated_at" to FieldValue.serverTimestamp()
            ),
            "hasInsigniaRIPlus" to (CondecoracionTracker.getInsigniaRIPlus() != null)
        )
        userDoc.set(data, SetOptions.merge())
    }

    fun getTopIntegralRanking(
        userId: String,
        userName: String,
        country: String,
        averagePosition: Double,
        callback: (List<IntegralRankingItem>, Int, IntegralRankingItem?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val fieldBase = "integral_ranking"
        val fieldAvg = "$fieldBase.averagePosition"

        db.collection("usuarios")
            .orderBy(fieldAvg)
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
                    val value = (integralData?.get("averagePosition") as? Number)?.toDouble() ?: Double.POSITIVE_INFINITY


                    if (!value.isFinite() || value <= 0.0) continue

                    val name = doc.getString("username") ?: ""
                    val code = doc.getString("countryCode") ?: "us"
                    val thisUserId = doc.id
                    val isCurrent = thisUserId == userId
                    val hasInsignia = doc.getBoolean("hasInsigniaRIPlus") ?: false


                    if (previousAverage != null && value > previousAverage) {
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

                    db.collection("usuarios")
                        .whereLessThan(fieldAvg, averagePosition)
                        .get()
                        .addOnSuccessListener { betterUsers ->
                            userPosition = betterUsers.size() + 1
                            userItem = IntegralRankingItem(
                                position = userPosition,
                                username = userName,
                                countryCode = country,
                                integralScore = averagePosition,
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
