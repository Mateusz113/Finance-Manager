package com.mateusz113.financemanager.data.mapper

import com.mateusz113.financemanager.data.converter.CategoryConverter
import com.mateusz113.financemanager.data.converter.DateConverter
import com.mateusz113.financemanager.data.repository.dto.PaymentListingDto
import com.mateusz113.financemanager.domain.model.PaymentListing
import java.time.LocalDate

fun PaymentListingDto.toPaymentListing(): PaymentListing {
    val date = timestamp?.let {
        DateConverter.convertTimestampIntoLocalDate(it)
    }
    return PaymentListing(
        id = id ?: "",
        title = title ?: "",
        amount = amount ?: 0.00,
        date = date ?: LocalDate.now(),
        category = CategoryConverter.convertStringToCategory(category)
    )
}
