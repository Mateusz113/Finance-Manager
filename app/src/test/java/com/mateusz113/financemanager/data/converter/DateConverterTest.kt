package com.mateusz113.financemanager.data.converter

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.properties.Delegates

class DateConverterTest {
    private var timestamp by Delegates.notNull<Long>()
    private lateinit var date: LocalDate

    @Before
    fun setUp() {
        //TIMESTAMP FOR 2024-03-04 00:00:00
        timestamp = 1709589600000
        date = LocalDate.of(2024, 3, 4)
    }

    @Test
    fun `Convert timestamp into date, returns correct date`() {
        assertThat(
            DateConverter
                .convertTimestampIntoLocalDate(timestamp)
        ).isEqualTo(date)
    }

    @Test
    fun `Convert date into timestamp, returns correct timestamp`() {
        assertThat(
            DateConverter
                .convertLocalDateIntoTimestamp(date)
        ).isEqualTo(timestamp)
    }
}
