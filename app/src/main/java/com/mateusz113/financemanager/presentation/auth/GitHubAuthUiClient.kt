package com.mateusz113.financemanager.presentation.auth

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GitHubAuthUiClient(
    private val firebaseAuth: FirebaseAuth,
    private val activity: Activity
) : AuthUiClient {
    private val tag = "GITHUB_INFO"
    fun signIn(
        onSignInComplete: (SignInResult) -> Unit
    ) {
        val pendingSignInTask = firebaseAuth.pendingAuthResult
        if (pendingSignInTask != null) {
            pendingSignInTask
                .addOnSuccessListener {
                    Log.d(tag, "Pending sign in task successfully completed")
                    if (it.user != null) {
                        onSignInComplete(
                            SignInResult(
                                wasSignInSuccessful = true,
                                errorMessage = null
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    Log.d(tag, "Logging failed: ${it.message}")
                    onSignInComplete(
                        SignInResult(
                            wasSignInSuccessful = false,
                            errorMessage = it.message
                        )
                    )
                }
        } else {
            firebaseAuth
                .startActivityForSignInWithProvider(activity, buildProvider())
                .addOnSuccessListener {
                    Log.d(tag, "Logging success")
                    if (it.user != null) {
                        onSignInComplete(
                            SignInResult(
                                wasSignInSuccessful = true,
                                errorMessage = null
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    Log.d(tag, "Logging failure: ${it.message}")
                    onSignInComplete(
                        SignInResult(
                            wasSignInSuccessful = false,
                            errorMessage = it.message
                        )
                    )
                }
        }
    }

    private fun buildProvider(): OAuthProvider {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.addCustomParameter("login", "your-email.com")
        return provider.build()
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }
}
