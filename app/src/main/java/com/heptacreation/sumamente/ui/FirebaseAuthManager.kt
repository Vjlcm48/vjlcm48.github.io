package com.heptacreation.sumamente.ui

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.heptacreation.sumamente.R

object FirebaseAuthManager {
    private const val RC_SIGN_IN = 9001
    private const val RC_CHECK_PROGRESS = 9002

    private lateinit var firebaseAuth: FirebaseAuth
    private var webClientId: String? = null

    private var callback: ((success: Boolean, message: String?) -> Unit)? = null
    private var checkProgressCallback: ((hasProgress: Boolean, account: GoogleSignInAccount?) -> Unit)? = null

    fun checkProgressOnly(
        activity: Activity,
        webClientId: String,
        callback: (hasProgress: Boolean, account: GoogleSignInAccount?) -> Unit
    ) {
        this.checkProgressCallback = callback
        this.webClientId = webClientId

        val googleSignInClient = getGoogleSignInClient(activity, webClientId)
        firebaseAuth = FirebaseAuth.getInstance()

        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_CHECK_PROGRESS)
    }

    fun startGoogleSignIn(
        activity: Activity,
        webClientId: String,
        callback: (success: Boolean, message: String?) -> Unit
    ) {
        this.callback = callback
        this.webClientId = webClientId

        val googleSignInClient = getGoogleSignInClient(activity, webClientId)
        firebaseAuth = FirebaseAuth.getInstance()

        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignInResult(
        activity: Activity,
        requestCode: Int,
        data: Intent?
    ) {
        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(activity, account)
                } catch (e: ApiException) {
                    callback?.invoke(false, activity.getString(R.string.google_signin_failed) + ": ${e.localizedMessage}")
                }
            }
            RC_CHECK_PROGRESS -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    checkProgressInFirestore(activity, account)
                } catch (_: ApiException) {
                    checkProgressCallback?.invoke(false, null)
                }
            }
        }
    }

    private fun checkProgressInFirestore(
        activity: Activity,
        acct: GoogleSignInAccount?
    ) {
        if (acct == null) {
            checkProgressCallback?.invoke(false, null)
            return
        }

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        val tempAuth = FirebaseAuth.getInstance()

        tempAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = tempAuth.currentUser
                    if (user != null) {
                        FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val hasData = document.exists() &&
                                        (document.getString("score_data") != null ||
                                                document.get("profile_preferences") != null)

                                tempAuth.signOut()

                                webClientId?.let {
                                    getGoogleSignInClient(activity, it).signOut()
                                }

                                checkProgressCallback?.invoke(hasData, if (hasData) acct else null)
                            }
                            .addOnFailureListener {
                                tempAuth.signOut()
                                webClientId?.let {
                                    getGoogleSignInClient(activity, it).signOut()
                                }
                                checkProgressCallback?.invoke(false, null)
                            }
                    } else {
                        checkProgressCallback?.invoke(false, null)
                    }
                } else {
                    checkProgressCallback?.invoke(false, null)
                }
            }
    }

    private fun firebaseAuthWithGoogle(
        activity: Activity,
        acct: GoogleSignInAccount?
    ) {
        if (acct == null) {
            callback?.invoke(false, activity.getString(R.string.invalid_google_account))
            return
        }
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    callback?.invoke(true, null)
                } else {
                    callback?.invoke(false, activity.getString(R.string.firebase_link_failed))
                }
            }
    }


    private fun getGoogleSignInClient(activity: Activity, webClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }
}
