package com.mateusz113.financemanager.presentation.payments.payment_details

import com.mateusz113.financemanager.domain.model.PaymentDetails

data class PaymentDetailsState<T>(
    var paymentDetails: PaymentDetails? = null,
    var error: String? = null,
    var isLoading: Boolean = false,
    var isRefreshing: Boolean = false,
    var isPhotoDialogOpen: Boolean = false,
    var dialogPhoto: T? = null
)