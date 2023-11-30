package com.mateusz113.financemanager.util

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