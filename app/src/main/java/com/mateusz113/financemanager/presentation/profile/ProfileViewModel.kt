package com.mateusz113.financemanager.presentation.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.presentation.auth.GoogleAuthUiClient
import com.mateusz113.financemanager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val sharedPreferences: SharedPreferences,
    private val repository: PaymentRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

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

    fun updateAuthClient(googleAuthUiClient: GoogleAuthUiClient) {
        _state.value = _state.value.copy(
            googleAuthUiClient = googleAuthUiClient
        )
        updateUserInfo()
    }

    private fun updateUserInfo() {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        _state.value.googleAuthUiClient?.getSignedInUser()?.let { userData ->
            if (!sharedPreferences.contains("${userData.userId}JoinDate")) {
                updateJoinDateInSharedPrefs(
                    userId = userData.userId,
                    defaultDate = formatter.format(LocalDate.now())
                )
            }
            if (!sharedPreferences.contains("${userData.userId}PaymentsNum")) {
                updateNumOfPaymentsInSharedPrefs(userData.userId)
            }
            val paymentsNumber = sharedPreferences.getInt("${userData.userId}PaymentsNum", 0)
            val joinDate =
                sharedPreferences.getString("${userData.userId}JoinDate", "22/07/2011")

            _state.value = _state.value.copy(
                userId = userData.userId,
                username = userData.username,
                email = userData.email,
                profilePictureUrl = userData.profilePictureUrl,
                joinDate = joinDate ?: formatter.format(LocalDate.now()),
                paymentsNumber = paymentsNumber
            )
        }

    }

    private fun updateNumOfPaymentsInSharedPrefs(userId: String) {
        viewModelScope.launch {
            repository.getPaymentListings().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val count = result.data?.size
                        sharedPreferences.edit().apply {
                            this.putInt("${userId}PaymentsNum", count ?: -1)
                        }.apply()
                    }

                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun updateJoinDateInSharedPrefs(
        userId: String,
        defaultDate: String = "22/07/2011"
    ) {
        sharedPreferences.edit().apply {
            this.putString(
                "${userId}JoinDate", defaultDate
            )
        }.apply()
    }
    suspend fun signOut() {
        _state.value.googleAuthUiClient?.signOut()
    }

    suspend fun deleteAccount(): Boolean = suspendCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().signOut()
                    sharedPreferences.edit().apply {
                        this.remove("${_state.value.userId}JoinDate")
                        this.remove("${_state.value.userId}PaymentsNum")
                    }.apply()
                    viewModelScope.launch {
                        signOut()
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
}