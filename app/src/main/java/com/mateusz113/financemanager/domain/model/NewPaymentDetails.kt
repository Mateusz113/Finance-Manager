package com.mateusz113.financemanager.domain.model

import android.net.Uri
import com.mateusz113.financemanager.util.Category
import java.time.LocalDate

data class NewPaymentDetails(
    val title: String,
    val description: String,
    val amount: Float,
    val photoUris: List<Uri>,
    val date: LocalDate,
    val category: Category
)
