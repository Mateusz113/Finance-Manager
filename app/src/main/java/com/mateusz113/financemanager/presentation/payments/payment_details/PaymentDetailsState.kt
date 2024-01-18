package com.mateusz113.financemanager.presentation.payments.payment_details

import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.util.Currency

data class PaymentDetailsState<T>(
    var paymentDetails: PaymentDetails? = null,
    val currency: Currency = Currency.PLN,
    val isCurrencyPrefix: Boolean? = null,
    var error: String? = null,
    var isLoading: Boolean = false,
    var isPhotoDialogOpen: Boolean = false,
    var dialogPhoto: T? = null
)