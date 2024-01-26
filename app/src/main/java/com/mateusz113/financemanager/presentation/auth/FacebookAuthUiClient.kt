package com.mateusz113.financemanager.presentation.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistryOwner
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException

class FacebookAuthUiClient(
    private val context: Context,
    private val activity: ActivityResultRegistryOwner,
    onSignIn: (SignInResult) -> Unit
) : AuthUiClient {
    private val loginManager = LoginManager.getInstance()
    private val callbackManager = CallbackManager.Factory.create()
    private val auth = Firebase.auth

    init {
        loginManager.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Log.d("FACEBOOK_INFO", "onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("FACEBOOK_INFO", "onError: $error")
                }

                override fun onSuccess(result: LoginResult) {
                    Log.d("FACEBOOK_INFO", "onSuccess: $result")
                    CoroutineScope(Dispatchers.Default).launch {
                        val signInResult = signInWithInfo(result.accessToken)
                        onSignIn(signInResult)
                    }
                }
            })
    }

    fun openLoginPage() {
        loginManager.logIn(
            activity,
            callbackManager,
            listOf("public_profile", "email")
        )
    }

    suspend fun signInWithInfo(
        accessInfo: AccessToken
    ): SignInResult {
        val credential = FacebookAuthProvider.getCredential(accessInfo.token)
        return try {
            auth.signInWithCredential(credential).await()
            SignInResult(
                wasSignInSuccessful = true,
                errorMessage = null
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "${e.message}",
                    Toast.LENGTH_LONG,
                ).show()
            }
            SignInResult(
                wasSignInSuccessful = false,
                errorMessage = e.message
            )
        }
    }

    fun unregisterCallback() {
        loginManager.unregisterCallback(callbackManager)
    }

    override suspend fun signOut() {
        try {
            LoginManager.getInstance().logOut()
            auth.signOut()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
            Toast.makeText(
                context,
                "Sign out failed.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}
