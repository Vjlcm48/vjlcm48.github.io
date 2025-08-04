package com.example.sumamente.ui

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.sumamente.R

object FirebaseAuthManager {
    private const val RC_SIGN_IN = 9001

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private var callback: ((success: Boolean, message: String?) -> Unit)? = null

    fun startGoogleSignIn(
        activity: Activity,
        webClientId: String,
        callback: (success: Boolean, message: String?) -> Unit
    ) {
        this.callback = callback


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)
        firebaseAuth = FirebaseAuth.getInstance()


        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignInResult(
        activity: Activity,
        requestCode: Int,
        data: Intent?
    ) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(activity, account)
            } catch (e: ApiException) {
                callback?.invoke(false, activity.getString(R.string.google_signin_failed) + ": ${e.localizedMessage}")
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
}
