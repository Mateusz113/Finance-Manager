package com.mateusz113.financemanager.data.mapper

import com.mateusz113.financemanager.domain.model.TestPaymentInformation
import com.mateusz113.financemanager.domain.model.NewPaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentDetails
import com.mateusz113.financemanager.domain.model.PaymentListing

fun TestPaymentInformation.toPaymentListing(): PaymentListing {
    return PaymentListing(
        id, title, amount, date, category
    )
}

fun TestPaymentInformation.toPaymentDetails(): PaymentDetails {
    return PaymentDetails(
        title, description, amount, photoUrls, date, category
    )
}

fun TestPaymentInformation.toNewPaymentDetails(): NewPaymentDetails {
    return NewPaymentDetails(
        title, description, amount, photoUris, photoUrls, deletedPhotos, date, category
    )
}
