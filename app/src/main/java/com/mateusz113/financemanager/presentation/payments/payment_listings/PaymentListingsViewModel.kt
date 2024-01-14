package com.mateusz113.financemanager.presentation.payments.payment_listings

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Currency
import com.mateusz113.financemanager.util.Resource
import com.mateusz113.financemanager.util.SymbolPlacement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PaymentListingsViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var _state = MutableStateFlow(PaymentListingsState())
    val state = _state.asStateFlow()
    private var searchJob: Job? = null

    init {
        getPaymentListings()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateCurrencyDetails()
        updateSymbolPlacementDetails()
    }

    fun onEvent(event: PaymentListingsEvent) {
        when (event) {
            is PaymentListingsEvent.Refresh -> {
                getPaymentListings()
            }

            is PaymentListingsEvent.DeletePayment -> {
                deletePayment(event.id)
            }

            is PaymentListingsEvent.SearchPayment -> {
                _state.value = _state.value.copy(
                    filterSettings = _state.value.filterSettings.copy(query = event.query)
                )
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getPaymentListings()
                }
            }

            is PaymentListingsEvent.UpdateFilterDialogState -> {
                _state.value = _state.value.copy(
                    isFilterDialogOpen = event.isOpen
                )
            }

            is PaymentListingsEvent.UpdateFilterSettings -> {
                _state.value = _state.value.copy(
                    filterSettings = event.filterSettings
                )
                viewModelScope.launch {
                    getPaymentListings()
                }
            }
        }
    }

    private fun deletePayment(id: String?) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        viewModelScope.launch {
            id?.let { idToRemove ->
                repository.removePayment(idToRemove)
                var paymentsNumber = sharedPreferences.getInt("${uid}PaymentsNum", 1)
                sharedPreferences.edit().apply {
                    this.putInt("${uid}PaymentsNum", --paymentsNumber)
                }.apply()
                getPaymentListings()
            }
        }
    }

    private fun getPaymentListings(
        filterSettings: FilterSettings = _state.value.filterSettings
    ) {
        viewModelScope.launch {
            repository.getPaymentListingsWithFilter(filterSettings)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { listings ->
                                _state.value = _state.value.copy(
                                    payments = listings
                                )
                            }
                        }

                        is Resource.Error -> {
                            //TODO: Introduce proper error handling
                            Unit
                        }

                        is Resource.Loading -> {
                            _state.value = _state.value.copy(isLoading = result.isLoading)
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