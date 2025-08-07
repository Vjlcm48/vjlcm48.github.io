package com.example.sumamente.ui.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.example.sumamente.R
import com.example.sumamente.ui.CondecoracionTracker
import com.example.sumamente.ui.IQPlusRankingItem
import com.example.sumamente.ui.ScoreManager
import com.example.sumamente.ui.SettingsActivity
import com.example.sumamente.ui.SpeedRankingItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.example.sumamente.ui.GlobalRankingItem
import com.example.sumamente.ui.IntegralRankingItem

object DataSyncManager {

    private val auth get() = FirebaseAuth.getInstance()

    private const val SCHEMA_VERSION_PROFILE = 1
    private const val SCHEMA_VERSION_SCORE = 2
    private const val SCHEMA_VERSION_CONDECO = 2


    fun syncDataToCloud(
        context: Context,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser
        if (user == null) {
            onResult(false, context.getString(R.string.user_not_authenticated))
            return
        }

        val profileBox = buildProfilePreferencesBox(context)
        val scoreJson = ScoreManager.exportAllDataAsJson(context)
        val condecoJson = CondecoracionTracker.exportAllDataAsJson(context)

        val privateData = hashMapOf(
            "profile_preferences" to profileBox,
            "score_data" to scoreJson,
            "condecoracion_data" to condecoJson
        )

        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("savedUserName", "") ?: ""
        val countryCode = prefs.getString("savedCountryCode", "sumamente") ?: "sumamente"


        val data = hashMapOf(
            "username" to userName,
            "countryCode" to countryCode,
            "lastUpdate" to FieldValue.serverTimestamp(),
            "iqPlus" to (ScoreManager.lastIqComponentByGame["IQ_PLUS_OVERALL"] ?: 0.0),
            "global_ranking_points" to ScoreManager.getTotalUniqueLevelsCompletedAllGames().toLong(),
            "private" to privateData,
            "score_schema_version" to SCHEMA_VERSION_SCORE,
            "condecoracion_schema_version" to SCHEMA_VERSION_CONDECO,
            "hasInsigniaRIPlus" to (CondecoracionTracker.getInsigniaRIPlus() != null)
        )


        firestore.collection("usuarios")
            .document(user.uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.localizedMessage) }
    }

    fun syncDataFromCloud(
        context: Context,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()
        val user = auth.currentUser
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
                        CondecoracionTracker.importAllDataFromJson(context, json)
                    }
                }
                // --- Fin del Cambio ---

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
                        is Double -> putFloat(keyStr, value.toFloat())
                        is Int -> putInt(keyStr, value)
                        is Long -> putLong(keyStr, value)
                        is String -> putString(keyStr, value)
                        is List<*> -> {

                            if (keyStr.contains("completed_levels") || keyStr.contains("unlocked_levels")) {
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
        val isLinked = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            .getBoolean(SettingsActivity.ACCOUNT_LINKED, false)

        Log.d("DataSync", "deleteAccountData - isLinked: $isLinked, user: ${user?.email}")


        if (user == null || !isLinked) {
            clearAllLocalData(context)
            onResult(true, null)
            return
        }


        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("usuarios").document(user.uid).delete()
            .addOnSuccessListener {

                user.delete()
                    .addOnSuccessListener {

                        clearAllLocalData(context)
                        signOutFromGoogle(context)
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
        val data = hashMapOf(
            "profile_preferences" to hashMapOf(
                "savedUserName" to userName,
                "savedCountryCode" to country
            ),
            "iqPlus" to iqPlus,
            "hasInsigniaRIPlus" to (CondecoracionTracker.getInsigniaRIPlus() != null)
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
                    val value = (doc.get("iqPlus") ?: 0.0) as Double
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
        val data = hashMapOf(
            "profile_preferences" to hashMapOf(
                "savedUserName" to userName,
                "savedCountryCode" to country
            ),
            "speed_ranking_$gameType" to hashMapOf(
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
        db.collection("usuarios")
            .whereNotEqualTo("speed_ranking_$gameType", null)
            .orderBy("speed_ranking_$gameType.averageTime")
            .limit(200)
            .get()
            .addOnSuccessListener { result ->
                val rankingList = mutableListOf<SpeedRankingItem>()
                var userPosition = -1
                var userItem: SpeedRankingItem? = null
                var currentPosition = 1
                var previousTime: Double? = null

                for (doc in result) {
                    val name = doc.getString("username") ?: ""
                    val code = doc.getString("countryCode") ?: "us"
                    val timeData = doc.get("speed_ranking_$gameType") as? Map<*, *>
                    val value = (timeData?.get("averageTime") ?: 0.0) as Double
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
                        .whereNotEqualTo("speed_ranking_$gameType", null)
                        .whereLessThan("speed_ranking_$gameType.averageTime", averageTime)
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
                        .addOnFailureListener { callback(rankingList, -1, null) }
                } else {
                    callback(rankingList, userPosition, userItem)
                }
            }
            .addOnFailureListener { callback(emptyList(), -1, null) }
    }

    fun uploadGlobalRankingToFirebase(
        userId: String,
        userName: String,
        country: String,
        totalPoints: Long
    ) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("usuarios").document(userId)
        val data = hashMapOf(
            "profile_preferences" to hashMapOf(
                "savedUserName" to userName,
                "savedCountryCode" to country
            ),
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
            .whereNotEqualTo("global_ranking", null)
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
                    val name = doc.getString("username") ?: ""
                    val code = doc.getString("countryCode") ?: "us"
                    val pointsData = doc.get("global_ranking") as? Map<*, *>
                    val value = ((pointsData?.get("totalPoints") ?: 0L) as Number).toLong()
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
                        .whereNotEqualTo("global_ranking", null)
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
                        .addOnFailureListener { callback(rankingList, -1, null) }
                } else {
                    callback(rankingList, userPosition, userItem)
                }
            }
            .addOnFailureListener { callback(emptyList(), -1, null) }
    }

    fun uploadIntegralRankingToFirebase(
        userId: String,
        userName: String,
        country: String,
        averagePosition: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val userDoc = db.collection("usuarios").document(userId)
        val data = hashMapOf(
            "profile_preferences" to hashMapOf(
                "savedUserName" to userName,
                "savedCountryCode" to country
            ),
            "integral_ranking" to hashMapOf(
                "averagePosition" to averagePosition,
                "eligible" to true,
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
        db.collection("usuarios")
            .whereNotEqualTo("integral_ranking", null)
            .orderBy("integral_ranking.averagePosition")
            .limit(200)
            .get()
            .addOnSuccessListener { result ->
                val rankingList = mutableListOf<IntegralRankingItem>()
                var userPosition = -1
                var userItem: IntegralRankingItem? = null
                var currentPosition = 1
                var previousAverage: Double? = null

                for (doc in result) {
                    val name = doc.getString("username") ?: ""
                    val code = doc.getString("countryCode") ?: "us"
                    val integralData = doc.get("integral_ranking") as? Map<*, *>
                    val value = ((integralData?.get("averagePosition") ?: 0.0) as Number).toDouble()
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
                        .whereNotEqualTo("integral_ranking", null)
                        .whereLessThan("integral_ranking.averagePosition", averagePosition)
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


}
