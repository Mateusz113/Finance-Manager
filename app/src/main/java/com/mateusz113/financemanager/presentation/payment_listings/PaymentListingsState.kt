package com.mateusz113.financemanager.presentation.payment_listings

import com.mateusz113.financemanager.domain.model.PaymentListing

data class PaymentListingsState(
    val payments: List<PaymentListing> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)
