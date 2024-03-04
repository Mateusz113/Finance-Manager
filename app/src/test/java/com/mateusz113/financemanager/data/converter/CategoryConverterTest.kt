package com.mateusz113.financemanager.data.converter

import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.domain.model.Category
import org.junit.Before
import org.junit.Test

class CategoryConverterTest {
    private lateinit var categoryName: String

    @Before
    fun setUp() {
        categoryName = Category.values().random().toString()
    }

    @Test
    fun `Convert category name to the category enum, returns correct enum`() {
        assertThat(CategoryConverter.convertStringToCategory(categoryName)).isEquivalentAccordingToCompareTo(
            Category.Education
        )
    }
}
