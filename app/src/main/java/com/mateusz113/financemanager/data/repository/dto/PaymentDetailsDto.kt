package com.mateusz113.financemanager.data.repository.dto

import com.mateusz113.financemanager.util.Category

data class PaymentDetailsDto(
    val title: String?,
    val description: String?,
    val amount: Float?,
    val photoUrls: List<String>?,
    val timestamp: Long?,
    val category: String?
)