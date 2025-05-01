package com.example.alquranapp.GoogleSignIn

import android.content.Context
import android.content.Intent
import com.example.alquranapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

object FirebaseUtils {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun initializeGoogleClient(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInClient(context: Context? = null): GoogleSignInClient {
        if (!::googleSignInClient.isInitialized && context != null) {
            initializeGoogleClient(context)
        }
        return googleSignInClient
    }

    fun signOut() {
        auth.signOut()
        if (::googleSignInClient.isInitialized) {
            googleSignInClient.signOut()
        }
    }

    fun getSignInResultFromIntent(data: Intent?) = GoogleSignIn.getSignedInAccountFromIntent(data)

    fun firebaseAuthWithGoogle(idToken: String, onResult: () -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult()
            }
        }
    }
}