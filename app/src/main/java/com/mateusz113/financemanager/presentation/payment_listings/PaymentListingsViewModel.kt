package com.mateusz113.financemanager.presentation.payment_listings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Category
import com.mateusz113.financemanager.util.Resource
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
    private val repository: PaymentRepository
) : ViewModel() {
    private var _state = MutableStateFlow(PaymentListingsState())
    val state = _state.asStateFlow()
    private var searchJob: Job? = null

    init {
        getPaymentListings()
    }

    fun onEvent(event: PaymentListingsEvent) {
        when (event) {
            is PaymentListingsEvent.Refresh -> {
                getPaymentListings()
            }

            is PaymentListingsEvent.AddPayment -> {
                addPayment()
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
        viewModelScope.launch {
            id?.let { idToRemove ->
                repository.removePayment(idToRemove)
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

    private fun addPayment() {
        viewModelScope.launch {
            val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val title =
                (1..Random.nextInt(2, 20)).map { charset[Random.nextInt(charset.lastIndex)] }
                    .joinToString("")
            val description =
                (1..Random.nextInt(20, 100)).map { charset[Random.nextInt(charset.lastIndex)] }
                    .joinToString("")
            val amount = BigDecimal(Random.nextFloat().times(Random.nextInt(1, charset.lastIndex)).toString())
                .setScale(2, RoundingMode.DOWN).toFloat()
            val date = LocalDate.of(
                Random.nextInt(2011, 2023),
                Random.nextInt(1, 12),
                Random.nextInt(1, 25),
            )
            val category = Category.values().random()
            repository.addPayment(
                NewPaymentDetails(
                    title = title,
                    description = description,
                    amount = amount,
                    photoUris = emptyList(),
                    date = date,
                    category = category
                )
            )
        }
    }
}