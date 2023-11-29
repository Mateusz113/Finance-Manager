package com.mateusz113.financemanager.presentation.payment_details

import com.mateusz113.financemanager.domain.model.PaymentDetails

data class PaymentDetailsState(
    var paymentDetails: PaymentDetails? = null,
    var error: String? = null,
    var isLoading: Boolean = false
)