package com.mateusz113.financemanager.data.mapper

import com.mateusz113.financemanager.data.repository.dto.PaymentDetailsDto
import com.mateusz113.financemanager.data.repository.dto.PaymentListingDto
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.util.convertLocalDateIntoTimestamp

fun NewPaymentDetails.toPaymentListingDto(): PaymentListingDto {
    return PaymentListingDto(
        id = null,
        title = title,
        amount = amount,
        timestamp = convertLocalDateIntoTimestamp(date),
        category = category.name
    )
}

fun NewPaymentDetails.toPaymentDetailsDto(): PaymentDetailsDto {
    return PaymentDetailsDto(
        title = title,
        description = description,
        amount = amount,
        photoUrls = emptyList(),
        timestamp = convertLocalDateIntoTimestamp(date),
        category = category.name
    )
}
