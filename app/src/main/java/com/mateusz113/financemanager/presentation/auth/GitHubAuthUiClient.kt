package com.mateusz113.financemanager.presentation.auth

import android.app.Activity
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth

class GitHubAuthUiClient(
    private val activity: Activity
) : AuthUiClient {
    private val tag = "GITHUB_INFO"
    private val auth = FirebaseAuth.getInstance()
    fun signIn(
        onSignInComplete: (SignInResult) -> Unit
    ) {
        val pendingSignInTask = auth.pendingAuthResult
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
            auth
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
        provider.addCustomParameter("login", "")
        return provider.build()
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
    }
}
