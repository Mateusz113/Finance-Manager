package com.mateusz113.financemanager.presentation.payments.payment_listings

import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.Currency

data class PaymentListingsState(
    val payments: List<PaymentListing> = emptyList(),
    val currency: Currency = Currency.PLN,
    val isCurrencyPrefix: Boolean? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFilterDialogOpen: Boolean = false,
    val filterSettings: FilterSettings = FilterSettings()
)
