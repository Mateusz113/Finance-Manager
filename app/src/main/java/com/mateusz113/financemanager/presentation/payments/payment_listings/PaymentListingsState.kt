package com.mateusz113.financemanager.presentation.payments.payment_listings

import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.PaymentListing

data class PaymentListingsState(
    val payments: List<PaymentListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFilterDialogOpen: Boolean = false,
    val filterSettings: FilterSettings = FilterSettings()
)
