package com.mateusz113.financemanager.data.mapper

import com.mateusz113.financemanager.data.repository.dto.PaymentListingDto
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.convertStringToCategory
import com.mateusz113.financemanager.util.convertTimestampIntoLocalDate
import java.time.LocalDate

fun PaymentListingDto.toPaymentListing(): PaymentListing {
    val date = timestamp?.let {
        convertTimestampIntoLocalDate(it)
    }
    return PaymentListing(
        id = id ?: "",
        title = title ?: "",
        amount = amount ?: 0.00,
        date = date ?: LocalDate.now(),
        category = convertStringToCategory(category)
    )
}
