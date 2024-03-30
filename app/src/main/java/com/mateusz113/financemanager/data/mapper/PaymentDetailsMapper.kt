package com.mateusz113.financemanager.data.mapper

import com.mateusz113.financemanager.data.converter.CategoryConverter
import com.mateusz113.financemanager.data.converter.DateConverter
import com.mateusz113.financemanager.data.repository.dto.PaymentDetailsDto
import com.mateusz113.financemanager.domain.model.PaymentDetails
import java.time.LocalDate


fun PaymentDetailsDto.toPaymentDetails(): PaymentDetails {
    //Convert timestamp into LocalDate
    val date = timestamp?.let {
        DateConverter.convertTimestampIntoLocalDate(it)
    }

    return PaymentDetails(
        title = title ?: "",
        description = description ?: "",
        amount = amount ?: 0.00,
        photoUrls = photoUrls ?: emptyList(),
        date = date ?: LocalDate.now(),
        category = CategoryConverter.convertStringToCategory(category)
    )
}
