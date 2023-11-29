package com.mateusz113.financemanager.presentation.payment_listings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Category
import com.mateusz113.financemanager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PaymentListingsViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {
    private var _state = MutableStateFlow(PaymentListingsState())
    val state = _state.asStateFlow()

    init {
        getPaymentListings()
    }

    fun onEvent(event: PaymentListingsEvent, id: String? = null) {
        when (event) {
            is PaymentListingsEvent.Refresh -> {
                getPaymentListings()
            }

            is PaymentListingsEvent.AddPayment -> {
                addPayment()
            }

            is PaymentListingsEvent.DeletePayment -> {
                deletePayment(id)
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

    private fun getPaymentListings() {
        viewModelScope.launch {
            repository.getPaymentListings()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("LISTINGS_GET", "ViewModel: List of results received")
                            result.data?.forEach {
                                Log.d("LISTINGS_GET", it.toString())
                            }
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
            repository.addPayment(
                NewPaymentDetails(
                    title = "Title",
                    description = "This is description",
                    amount = 10.99f,
                    photoUris = emptyList(),
                    date = LocalDate.now(),
                    category = Category.Housing
                )
            )
        }
    }
}