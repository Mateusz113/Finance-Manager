package com.mateusz113.financemanager.domain.model

import com.mateusz113.financemanager.util.Category
import java.time.LocalDate

data class PaymentDetails(
    val title: String,
    val description: String,
    val amount: Float,
    val photoUrls: List<String>,
    val date: LocalDate,
    val category: Category
)
