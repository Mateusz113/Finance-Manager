package com.mateusz113.financemanager.domain.model

import java.time.LocalDate
import java.time.Month

data class FilterSettings(
    val query: String = "",
    val categories: MutableList<Category?> = mutableListOf(),
    val minValue: String = "",
    val maxValue: String = "",
    val startDate: LocalDate = LocalDate.of(2011, Month.JULY, 22),
    val endDate: LocalDate = LocalDate.now()
)
