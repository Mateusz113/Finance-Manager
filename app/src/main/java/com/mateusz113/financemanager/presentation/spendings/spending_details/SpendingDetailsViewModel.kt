package com.mateusz113.financemanager.presentation.spendings.spending_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpendingDetailsViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {
    private var _state = MutableStateFlow(SpendingDetailsState())
    val state = _state.asStateFlow()
    private var searchJob: Job? = null

    init {
        getPaymentListings()
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
                                    paymentListings = data,
                                    listingsMap = Category.values().associateWith { category ->
                                        data.filter { it.category == category }
                                    },
                                    error = null
                                )
                            }
                        }

                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                paymentListings = emptyList(),
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
}