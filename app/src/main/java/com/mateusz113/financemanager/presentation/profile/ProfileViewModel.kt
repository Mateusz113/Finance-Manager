package com.mateusz113.financemanager.presentation.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.presentation.auth.AuthUiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.UpdateConfirmationDialogState -> {
                _state.value = _state.value.copy(
                    isDeletionConfirmOpen = event.isOpen
                )
            }

            is ProfileEvent.UpdateSignOutDialogState -> {
                _state.value = _state.value.copy(
                    isSignOutDialogOpen = event.isOpen
                )
            }

            is ProfileEvent.UpdateDeleteCondition -> {
                _state.value = _state.value.copy(
                    shouldDelete = event.shouldDelete
                )
            }

            is ProfileEvent.UpdateSignOutCondition -> {
                _state.value = _state.value.copy(
                    shouldSignOut = event.shouldSignOut
                )
            }
        }
    }

    fun updateAuthClient(
        authUiClient: AuthUiClient
    ) {
        if (_state.value.authUiClient == null) {
            _state.value = _state.value.copy(
                authUiClient = authUiClient
            )
            //After the auth client is inserted the user info can be updated
            updateProfileInfo()
        }
    }

    private fun updateProfileInfo() {
        updateUserInfo()
        updateJoinDate()
        updatePaymentNumber()
    }

    private fun updateJoinDate() {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val user = FirebaseAuth.getInstance().currentUser
        user.let { userData ->
            val joinDate =
                sharedPreferences.getString(
                    "${userData?.uid}JoinDate",
                    formatter.format(LocalDate.now())
                )
            _state.value = _state.value.copy(
                joinDate = joinDate!!
            )
        }
    }

    private fun updatePaymentNumber() {
        FirebaseAuth.getInstance().currentUser?.let { userData ->
            val paymentsNumber = sharedPreferences.getInt("${userData.uid}PaymentsNum", 0)
            _state.value = _state.value.copy(
                paymentsNumber = paymentsNumber
            )
        }
    }


    private fun updateUserInfo() {
        FirebaseAuth.getInstance().currentUser?.let { userData ->
            Log.d("EMAIL_CASE", userData.email.toString())
            _state.value = _state.value.copy(
                userId = userData.uid,
                username = userData.displayName,
                email = userData.email,
                profilePictureUrl = userData.photoUrl?.toString()
            )
        }
    }

    suspend fun signOut(
        uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    ) {
        sharedPreferences.edit().apply {
            uid?.let {
                remove("${it}AuthMethod")
            }
        }.apply()
        _state.value.authUiClient?.signOut()
    }

    suspend fun deleteAccount(): Boolean = suspendCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sharedPreferences.edit().apply {
                        this.remove("${_state.value.userId}JoinDate")
                        this.remove("${_state.value.userId}PaymentsNum")
                    }.apply()
                    viewModelScope.launch {
                        async { signOut(user.uid) }.await()
                        continuation.resume(true)
                    }
                }
            }
            ?.addOnFailureListener {
                it.message?.let { message ->
                    _state.value = _state.value.copy(
                        errorMessage = message
                    )
                }
                continuation.resume(false)
            }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "${_state.value.userId}PaymentsNum") {
            updatePaymentNumber()
        }
    }

    override fun onCleared() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onCleared()
    }
}