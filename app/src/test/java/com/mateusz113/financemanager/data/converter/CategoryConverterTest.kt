package com.mateusz113.financemanager.data.converter

import com.google.common.truth.Truth.assertThat
import com.mateusz113.financemanager.domain.model.Category
import org.junit.Before
import org.junit.Test

class CategoryConverterTest {
    private lateinit var category: Category

    @Before
    fun setUp() {
        category = Category.values().random()
    }

    @Test
    fun `Convert category name to the category enum, returns correct enum`() {
        assertThat(CategoryConverter.convertStringToCategory(category.name)).isEqualTo(category)
    }
}
