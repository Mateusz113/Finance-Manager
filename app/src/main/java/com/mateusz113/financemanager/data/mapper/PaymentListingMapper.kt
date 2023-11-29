package com.mateusz113.financemanager.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.mateusz113.financemanager.data.repository.dto.PaymentListingDto
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.convertTimestampIntoLocalDate
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun PaymentListingDto.toPaymentListing(): PaymentListing {
    val date = timestamp?.let {
        convertTimestampIntoLocalDate(it)
    }
    return PaymentListing(
        id = id ?: "",
        title = title ?: "",
        amount = amount ?: 0.00f,
        date = date ?: LocalDate.now()
    )
}