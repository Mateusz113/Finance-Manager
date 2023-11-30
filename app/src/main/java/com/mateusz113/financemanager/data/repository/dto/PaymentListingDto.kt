package com.mateusz113.financemanager.data.repository.dto

class PaymentListingDto() {
    var id: String? = null
    var title: String? = null
    var amount: Float? = null
    var timestamp: Long? = null
    var category: String? = null

    constructor(
        id: String?,
        title: String?,
        amount: Float?,
        timestamp: Long?,
        category: String?
    ) : this() {
        this.id = id
        this.title = title
        this.amount = amount
        this.timestamp = timestamp
        this.category = category
    }
}