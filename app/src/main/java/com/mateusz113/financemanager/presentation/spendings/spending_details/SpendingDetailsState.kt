package com.mateusz113.financemanager.presentation.spendings.spending_details

import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.PaymentListing

data class SpendingDetailsState(
    val paymentListings: List<PaymentListing> = emptyList(),
    val filterSettings: FilterSettings = FilterSettings(),
    val isLoading: Boolean = false,
    val isDialogOpen: Boolean = false,
    val error: String? = null
)