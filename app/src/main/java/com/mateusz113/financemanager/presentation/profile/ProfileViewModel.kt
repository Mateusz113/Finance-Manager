package com.mateusz113.financemanager.presentation.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.presentation.auth.AuthUiClient
import com.mateusz113.financemanager.util.Resource
import com.mateusz113.financemanager.util.convertTimestampIntoLocalDate
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
            if (!sharedPreferences.contains("${userData?.uid}JoinDate")) {
                val userId = user?.uid ?: ""
                val userJoinTimestamp = user?.metadata?.creationTimestamp
                if (userJoinTimestamp != null) {
                    val userJoinDate = convertTimestampIntoLocalDate(userJoinTimestamp)
                    updateJoinDateInSharedPrefs(
                        userId = userId,
                        userJoinDate = formatter.format(userJoinDate)
                    )
                } else {
                    updateJoinDateInSharedPrefs(
                        userId = userId,
                        userJoinDate = formatter.format(LocalDate.now())
                    )
                }
            }
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
            if (!sharedPreferences.contains("${userData.uid}PaymentsNum")) {
                updateNumOfPaymentsInSharedPrefs(userData.uid)
            }
            val paymentsNumber = sharedPreferences.getInt("${userData.uid}PaymentsNum", 0)
            _state.value = _state.value.copy(
                paymentsNumber = paymentsNumber
            )
        }
    }


    private fun updateUserInfo() {
        FirebaseAuth.getInstance().currentUser?.let { userData ->
            _state.value = _state.value.copy(
                userId = userData.uid,
                username = userData.displayName,
                email = userData.email,
                profilePictureUrl = userData.photoUrl?.toString()
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
        userJoinDate: String
    ) {
        sharedPreferences.edit().apply {
            this.putString(
                "${userId}JoinDate", userJoinDate
            )
        }.apply()
    }

    suspend fun signOut() {
        _state.value.authUiClient?.signOut()
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