package com.mateusz113.financemanager.data.converter

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import kotlin.properties.Delegates

class DateConverterTest {
    //TIMESTAMP FOR 2024-01-01 00:00:00 GMT
    private val timestamp: Long = 1704067200000
    private val date: LocalDate = LocalDate.of(2024, 1, 1)

    @Test
    fun `Convert timestamp into date, returns correct date`() {
        assertThat(
            DateConverter
                .convertTimestampIntoLocalDate(
                    timestamp,
                    ZoneId.of("GMT")
                )
        ).isEqualTo(date)
    }

    @Test
    fun `Convert date into timestamp, returns correct timestamp`() {
        assertThat(
            DateConverter
                .convertLocalDateIntoTimestamp(
                    date,
                    ZoneId.of("GMT")
                )
        ).isEqualTo(timestamp)
    }
}
