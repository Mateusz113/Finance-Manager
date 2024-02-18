package com.mateusz113.financemanager.presentation.payments.payment_listings

import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.util.SortingMethod

sealed class PaymentListingsEvent {
    object Refresh : PaymentListingsEvent()
    data class UpdateFilterDialogState(val isOpen: Boolean) : PaymentListingsEvent()
    data class UpdateFilterSettings(val filterSettings: FilterSettings) : PaymentListingsEvent()
    data class UpdateDeleteDialogState(val isOpen: Boolean) : PaymentListingsEvent()
    data class UpdateDeleteDialogInfo(val title: String, val id: String) : PaymentListingsEvent()
    data class UpdateSortingMethod(val sortingMethod: SortingMethod): PaymentListingsEvent()
    data class UpdateSortingDialogState(val isOpen: Boolean): PaymentListingsEvent()
    data class DeletePayment(val id: String) : PaymentListingsEvent()
    data class SearchPayment(val query: String) : PaymentListingsEvent()

}