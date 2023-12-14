package com.mateusz113.financemanager.presentation.payment_addition

import android.net.Uri
import com.mateusz113.financemanager.domain.model.Category
import java.time.LocalDate

data class PaymentAdditionState(
    val title: String = "",
    val description: String = "",
    val amount: String = "",
    val newPhotos: MutableList<Uri> = mutableListOf(),
    val uploadedPhotos: List<String> = emptyList(),
    val deletedPhotos: MutableList<String> = mutableListOf(),
    val date: LocalDate = LocalDate.now(),
    val category: Category = Category.Housing,
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastDeletedUriPhoto: Uri? = null,
    val lastDeletedUrlPhoto: String? = null
)