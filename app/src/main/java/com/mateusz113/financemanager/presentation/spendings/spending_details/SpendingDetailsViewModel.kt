package com.mateusz113.financemanager.presentation.spendings.spending_details

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.util.Resource
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpendingDetailsViewModel @Inject constructor(
    private val repository: PaymentRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var _state = MutableStateFlow(SpendingDetailsState())
    val state = _state.asStateFlow()
    private var searchJob: Job? = null

    init {
        getPaymentListings()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updateCurrencyDetails()
        updateSymbolPlacementDetails()
    }

    fun onEvent(event: SpendingDetailsEvent) {
        when (event) {
            is SpendingDetailsEvent.UpdateFilterDialogState -> {
                _state.value = _state.value.copy(
                    isFilterDialogOpen = event.isOpen
                )
            }

            is SpendingDetailsEvent.UpdateFilterSettings -> {
                _state.value = _state.value.copy(
                    filterSettings = event.filterSettings
                )
                getPaymentListings()
            }

            is SpendingDetailsEvent.UpdateSliceDialogState -> {
                _state.value = _state.value.copy(
                    isKeyDialogOpen = event.isOpen
                )
            }

            is SpendingDetailsEvent.UpdateCurrentSlice -> {
                _state.value = _state.value.copy(
                    currentSlice = event.slice
                )
            }

            is SpendingDetailsEvent.SearchForPayment -> {
                _state.value = _state.value.copy(
                    filterSettings = _state.value.filterSettings.copy(query = event.query)
                )
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getPaymentListings()
                }
            }

            is SpendingDetailsEvent.Refresh -> {
                getPaymentListings()
            }
        }
    }

    private fun getPaymentListings() {
        viewModelScope.launch {
            repository
                .getPaymentListingsWithFilter(_state.value.filterSettings)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _state.value = _state.value.copy(
                                    listingsMap = Category.values().associateWith { category ->
                                        data.filter { it.category == category }
                                    },
                                    error = null
                                )
                            }
                        }

                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                listingsMap = emptyMap(),
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