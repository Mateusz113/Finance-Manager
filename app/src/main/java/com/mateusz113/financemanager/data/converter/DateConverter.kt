package com.mateusz113.financemanager.data.converter

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object DateConverter {
    fun convertTimestampIntoLocalDate(timestamp: Long): LocalDate {
        val instant = Instant.ofEpochMilli(timestamp)
        return instant.atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun convertLocalDateIntoTimestamp(localDate: LocalDate): Long {
        val instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
        return instant.toEpochMilli()
    }
}
