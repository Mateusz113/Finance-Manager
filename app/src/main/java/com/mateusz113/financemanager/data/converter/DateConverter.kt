package com.mateusz113.financemanager.data.converter

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object DateConverter {
    fun convertTimestampIntoLocalDate(
        timestamp: Long,
        timeZone: ZoneId = ZoneId.systemDefault()
    ): LocalDate {
        val instant = Instant.ofEpochMilli(timestamp)
        return instant.atZone(timeZone).toLocalDate()
    }

    fun convertLocalDateIntoTimestamp(
        localDate: LocalDate,
        timeZone: ZoneId = ZoneId.systemDefault()
    ): Long {
        val instant = localDate.atStartOfDay().atZone(timeZone).toInstant()
        return instant.toEpochMilli()
    }
}
