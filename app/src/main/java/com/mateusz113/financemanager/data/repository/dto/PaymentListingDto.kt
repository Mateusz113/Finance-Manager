package com.mateusz113.financemanager.data.repository.dto

class PaymentListingDto() {
    var id: String? = null
    var title: String? = null
    var amount: Float? = null
    var timestamp: Long? = null

    constructor(
        id: String?,
        title: String?,
        amount: Float?,
        timestamp: Long?
    ) : this() {
        this.id = id
        this.title = title
        this.amount = amount
        this.timestamp = timestamp
    }
}