package com.mateusz113.financemanager.util

import android.content.SharedPreferences
import android.util.Log
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SharedPreferencesSetup @Inject constructor(
    private val repository: PaymentRepository
) {
    fun setupSharedPreferences(
        sharedPreferences: SharedPreferences,
        userId: String,
        userJoinDate: LocalDate,
        authMethod: AuthMethod,
    ) {
        Log.d(
            "SHARED_PREFERENCES_SETUP",
            "Setup shared preferences function"
        )
        if (!sharedPreferences.contains("${userId}JoinDate")) {
            updateJoinDateInSharedPrefs(
                sharedPreferences,
                userId,
                userJoinDate
            )
        }
        if (!sharedPreferences.contains("${userId}AuthMethod")) {
            updateAuthMethodInSharedPrefs(
                sharedPreferences,
                userId,
                authMethod
            )
        }
        if (!sharedPreferences.contains("${userId}PaymentsNum")) {
            updatePaymentsNumInSharedPrefs(
                sharedPreferences,
                userId
            )
        }
        if (!sharedPreferences.contains("${userId}Currency")) {
            updateCurrencyInSharedPrefs(
                sharedPreferences,
                userId
            )
        }
        if (!sharedPreferences.contains("${userId}SymbolPlacement")) {
            updateSymbolPlacementInSharedPrefs(
                sharedPreferences,
                userId
            )
        }
    }

    private fun updateJoinDateInSharedPrefs(
        sharedPreferences: SharedPreferences,
        userId: String,
        userJoinDate: LocalDate
    ) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        sharedPreferences.edit().apply {
            this.putString(
                "${userId}JoinDate", formatter.format(userJoinDate)
            )
        }.apply()
    }

    private fun updateAuthMethodInSharedPrefs(
        sharedPreferences: SharedPreferences,
        userId: String,
        authMethod: AuthMethod
    ) {
        sharedPreferences.edit().apply {
            this.putString(
                "${userId}AuthMethod",
                authMethod.name
            )
        }.apply()
    }

    private fun updatePaymentsNumInSharedPrefs(
        sharedPreferences: SharedPreferences,
        userId: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getPaymentListings().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val count = result.data?.size
                        sharedPreferences.edit().apply {
                            this.putInt("${userId}PaymentsNum", count ?: -1)
                        }.apply()
                    }

                    is Resource.Error -> {
                        Log.d(
                            "SHARED_PREFERENCES_SETUP",
                            "Problem occurred when getting the data from remote source about payments num : ${result.message}"
                        )
                    }

                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun updateCurrencyInSharedPrefs(
        sharedPreferences: SharedPreferences,
        userId: String
    ) {
        sharedPreferences.edit().apply {
            this.putString(
                "${userId}Currency",
                Currency.PLN.name
            )
        }.apply()
    }

    private fun updateSymbolPlacementInSharedPrefs(
        sharedPreferences: SharedPreferences,
        userId: String
    ) {
        sharedPreferences.edit().apply {
            this.putString(
                "${userId}SymbolPlacement",
                SymbolPlacement.InAppControl.name
            )
        }.apply()
    }
}