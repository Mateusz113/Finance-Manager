package com.mateusz113.financemanager.presentation.payments.payment_details

sealed class PaymentDetailsEvent {
    object Refresh: PaymentDetailsEvent()
    data class UpdateDialogState(val isOpen: Boolean): PaymentDetailsEvent()
    data class UpdateDialogPhoto(val photo: String): PaymentDetailsEvent()
}