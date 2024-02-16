package com.mateusz113.financemanager.util

class PaymentInfoValidator {
    companion object {
        fun titleValidator(
            title: String
        ): Boolean {
            return title.length < 50 && title.isNotEmpty()
        }

        fun descriptionValidator(
            description: String
        ): Boolean {
            return description.length < 500 && description.isNotEmpty()
        }

        fun amountValidator(
            amount: String
        ): Boolean {
            return amount.isNotEmpty() &&
                    amount.split(".").first().length < 7 &&
                    try {
                        amount.toDouble()
                        true
                    } catch (e: NumberFormatException) {
                        false
                    }
        }

        fun photoQuantityValidator(
            newPhotosAmount: Int,
            uploadedPhotosAmount: Int
        ): Boolean {
            return newPhotosAmount + uploadedPhotosAmount <= 5
        }
    }
}