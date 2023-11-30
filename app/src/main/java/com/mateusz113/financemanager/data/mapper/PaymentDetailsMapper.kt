package com.mateusz113.financemanager.data.mapper

import com.mateusz113.financemanager.data.repository.dto.PaymentDetailsDto
import com.mateusz113.financemanager.data.repository.dto.PaymentListingDto
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.Category
import com.mateusz113.financemanager.util.convertLocalDateIntoTimestamp
import com.mateusz113.financemanager.util.convertStringToCategory
import com.mateusz113.financemanager.util.convertTimestampIntoLocalDate
import java.time.LocalDate


fun PaymentDetailsDto.toPaymentDetails(): PaymentDetails {
    //Convert timestamp into LocalDate
    val date = timestamp?.let {
        convertTimestampIntoLocalDate(it)
    }

    return PaymentDetails(
        title = title ?: "",
        description = description ?: "",
        amount = amount ?: 0.00f,
        photoUrls = photoUrls ?: emptyList(),
        date = date ?: LocalDate.now(),
        category = convertStringToCategory(category)
    )
}
