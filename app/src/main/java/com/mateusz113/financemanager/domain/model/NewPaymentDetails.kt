package com.mateusz113.financemanager.domain.model

import android.net.Uri
import java.time.LocalDate

data class NewPaymentDetails(
    val title: String,
    val description: String,
    val amount: Double,
    val photoUris: List<Uri>,
    val photoUrls: List<String> = emptyList(),
    val deletedPhotos: List<String> = emptyList(),
    val date: LocalDate,
    val category: Category
)
