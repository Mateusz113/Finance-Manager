package com.mateusz113.financemanager.domain.model

import com.mateusz113.financemanager.util.Category
import java.time.LocalDate

data class PaymentListing(
    val id: String,
    val title: String,
    val amount: Float,
    val date: LocalDate,
    val category: Category
)
