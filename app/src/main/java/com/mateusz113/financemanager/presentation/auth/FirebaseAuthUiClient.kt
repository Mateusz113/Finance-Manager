package com.mateusz113.financemanager.presentation.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthUiClient(
    private val context: Context
) : AuthUiClient {
    suspend fun register(
        displayName: String,
        email: String,
        password: String,
    ): SignInResult = suspendCoroutine { continuation ->
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("FIREBASE_AUTH", "Register successful")
                val user = auth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user?.updateProfile(profileUpdates)
                    ?.addOnSuccessListener {
                        Log.d("FIREBASE_AUTH", "Successful display name update")
                    }
                    ?.addOnFailureListener {
                        Log.d(
                            "FIREBASE_AUTH",
                            "Error updating display name: ${it.message}"
                        )
                    }
                continuation.resume(SignInResult(true, null))
            }
            .addOnFailureListener {
                Log.d("FIREBASE_AUTH", "Register failed: ${it.message}")
                Toast.makeText(
                    context,
                    it.message ?: "Registration failed",
                    Toast.LENGTH_SHORT
                ).show()
                continuation.resume(SignInResult(false, it.message))
            }
    }

    suspend fun signIn(
        email: String,
        password: String
    ): SignInResult = suspendCoroutine { continuation ->
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("FIREBASE_AUTH", "Sign in successful")
                continuation.resume(SignInResult(true, null))
            }
            .addOnFailureListener {
                Log.d("FIREBASE_AUTH", "Sign in failed: ${it.message}")
                Toast.makeText(
                    context,
                    it.message ?: "Sign in failed",
                    Toast.LENGTH_SHORT
                ).show()
                continuation.resume(SignInResult(false, it.message))
            }
    }

    override suspend fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}
