package com.mateusz113.financemanager.domain.model

import java.time.LocalDate

data class PaymentListing(
    val id: String,
    val title: String,
    val amount: Double,
    val date: LocalDate,
    val category: Category
)
