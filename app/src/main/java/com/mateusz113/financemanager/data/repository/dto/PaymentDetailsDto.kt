package com.mateusz113.financemanager.data.repository.dto

class PaymentDetailsDto() {
    var title: String? = null
    var description: String? = null
    var amount: Double? = null
    var photoUrls: List<String>? = null
    var timestamp: Long? = null
    var category: String? = null

    constructor(
        title: String?,
        description: String?,
        amount: Double?,
        photoUrls: List<String>?,
        timestamp: Long?,
        category: String?
    ) : this() {
        this.title = title
        this.description = description
        this.amount = amount
        this.photoUrls = photoUrls
        this.timestamp = timestamp
        this.category = category
    }
}