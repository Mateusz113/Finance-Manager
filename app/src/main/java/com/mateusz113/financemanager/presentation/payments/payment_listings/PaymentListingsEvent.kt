package com.mateusz113.financemanager.presentation.payments.payment_listings

import com.mateusz113.financemanager.domain.model.FilterSettings

sealed class PaymentListingsEvent {
    object Refresh : PaymentListingsEvent()
    object AddPayment : PaymentListingsEvent()
    data class UpdateFilterDialogState(val isOpen: Boolean) : PaymentListingsEvent()
    data class UpdateFilterSettings(val filterSettings: FilterSettings) : PaymentListingsEvent()
    data class DeletePayment(val id: String) : PaymentListingsEvent()
    data class SearchPayment(val query: String) : PaymentListingsEvent()

}