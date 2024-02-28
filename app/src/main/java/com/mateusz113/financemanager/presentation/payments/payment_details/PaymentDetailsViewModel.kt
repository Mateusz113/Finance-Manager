package com.mateusz113.financemanager.presentation.payments.payment_details

import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.util.Resource
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences,
    private val repository: PaymentRepository
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var _state = MutableStateFlow(PaymentDetailsState<Any>())
    val state = _state.asStateFlow()

    init {
        getPaymentDetails()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateCurrencyDetails()
        updateSymbolPlacementDetails()
    }

    fun onEvent(event: PaymentDetailsEvent) {
        when (event) {
            is PaymentDetailsEvent.Refresh -> {
                getPaymentDetails()
            }

            is PaymentDetailsEvent.UpdateDialogState -> {
                _state.value = _state.value.copy(
                    isPhotoDialogOpen = event.isOpen
                )
            }

            is PaymentDetailsEvent.UpdateDialogPhoto -> {
                _state.value = _state.value.copy(
                    dialogPhoto = event.photo
                )
            }
        }
    }

    private fun getPaymentDetails() {
        viewModelScope.launch {
            val id = savedStateHandle.get<String>("id") ?: return@launch
            repository.getPaymentDetails(id).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            paymentDetails = result.data,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            paymentDetails = null,
                            error = result.message
                        )
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                }
            }
        }
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "${FirebaseAuth.getInstance().currentUser?.uid}Currency") {
            updateCurrencyDetails()
        }
        if (key == "${FirebaseAuth.getInstance().currentUser?.uid}SymbolPlacement") {
            updateSymbolPlacementDetails()
        }
    }

    private fun updateSymbolPlacementDetails() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val symbolPlacement =
            if (uid != null) {
                SymbolPlacement.valueOf(
                    sharedPreferences.getString(
                        "${uid}SymbolPlacement",
                        SymbolPlacement.InAppControl.name
                    )!!
                )
            } else {
                SymbolPlacement.InAppControl
            }
        val isCurrencyPrefix =
            when (symbolPlacement) {
                SymbolPlacement.InAppControl -> {
                    null
                }

                SymbolPlacement.Suffix -> {
                    false
                }

                SymbolPlacement.Prefix -> {
                    true
                }
            }
        _state.value = _state.value.copy(
            isCurrencyPrefix = isCurrencyPrefix
        )
    }

    private fun updateCurrencyDetails() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val currentCurrency =
            if (uid != null) {
                Currency.valueOf(sharedPreferences.getString("${uid}Currency", Currency.PLN.name)!!)
            } else {
                Currency.PLN
            }
        _state.value = _state.value.copy(
            currency = currentCurrency,
        )
    }

    override fun onCleared() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onCleared()
    }
}