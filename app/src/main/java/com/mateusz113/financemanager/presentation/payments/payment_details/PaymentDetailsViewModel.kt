package com.mateusz113.financemanager.presentation.payments.payment_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mateusz113.financemanager.domain.repository.PaymentRepository
import com.mateusz113.financemanager.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PaymentRepository
) : ViewModel() {
    private var _state = MutableStateFlow(PaymentDetailsState<Any>())
    val state = _state.asStateFlow()

    init {
        getPaymentDetails()
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
}