package com.mateusz113.financemanager.presentation.payment_listings

sealed class PaymentListingsEvent {
    object Refresh : PaymentListingsEvent()
    object AddPayment : PaymentListingsEvent()
    object DeletePayment: PaymentListingsEvent()
}