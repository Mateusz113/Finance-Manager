package com.mateusz113.financemanager.presentation.spendings.spending_details

import com.mateusz113.financemanager.domain.model.FilterSettings

sealed class SpendingDetailsEvent {
    object Refresh : SpendingDetailsEvent()
    data class UpdateDialogState(val isOpen: Boolean) : SpendingDetailsEvent()
    data class UpdateFilterSettings(val filterSettings: FilterSettings) : SpendingDetailsEvent()
    data class SearchForPayment(val query: String) : SpendingDetailsEvent()
}