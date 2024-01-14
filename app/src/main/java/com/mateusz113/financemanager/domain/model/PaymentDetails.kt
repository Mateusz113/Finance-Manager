package com.mateusz113.financemanager.domain.model

import java.time.LocalDate

data class PaymentDetails(
    val title: String,
    val description: String,
    val amount: Double,
    val photoUrls: List<String>,
    val date: LocalDate,
    val category: Category
)
