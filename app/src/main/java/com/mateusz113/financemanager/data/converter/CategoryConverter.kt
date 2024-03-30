package com.mateusz113.financemanager.data.converter

import com.mateusz113.financemanager.domain.model.Category

object CategoryConverter {
    fun convertStringToCategory(
        s: String?,
        defaultReturnValue: Category = Category.Housing
    ): Category {
        return s?.let {
            try {
                Category.valueOf(it)
            } catch (e: IllegalArgumentException) {
                defaultReturnValue
            }
        } ?: defaultReturnValue
    }
}
