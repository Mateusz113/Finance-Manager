package com.mateusz113.financemanager.domain.model

import android.net.Uri
import com.mateusz113.financemanager.domain.model.Category
import java.time.LocalDate

data class TestPaymentInformation(
    val id: String,
    val title: String,
    val description: String,
    val amount: Double,
    val date: LocalDate,
    val category: Category,
    val photoUrls: List<String>,
    val photoUris: List<Uri>,
    val deletedPhotos: List<String>
)
