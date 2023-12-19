package com.mateusz113.financemanager.presentation.payments.payment_addition

import android.net.Uri
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.presentation.payments.payment_details.PaymentDetailsEvent
import java.time.LocalDate

sealed class PaymentAdditionEvent {
    data class ChangeTitle(val title: String) : PaymentAdditionEvent()
    data class ChangeDescription(val description: String) : PaymentAdditionEvent()
    data class ChangeAmount(val amount: String) : PaymentAdditionEvent()
    data class ChangeCategory(val category: Category) : PaymentAdditionEvent()
    data class ChangeDate(val date: LocalDate): PaymentAdditionEvent()
    data class AddNewPhoto(val photoUri: Uri) : PaymentAdditionEvent()
    data class RemovePhoto(val photoUri: Uri) : PaymentAdditionEvent()
    data class RemoveUploadedPhoto(val photoUrl: String) : PaymentAdditionEvent()
    data class RestoreDeletedPhoto<T>(val photo: T) : PaymentAdditionEvent()
    data class UpdateDialogState(val isOpen: Boolean): PaymentAdditionEvent()
    data class UpdateDialogPhoto(val photo: Any): PaymentAdditionEvent()
    object AdditionConfirm : PaymentAdditionEvent()
}