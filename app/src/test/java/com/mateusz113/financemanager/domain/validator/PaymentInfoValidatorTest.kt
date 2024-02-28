package com.mateusz113.financemanager.domain.validator

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class PaymentInfoValidatorTest {
    @Test
    fun `Validates correct title, returns true`() {
        val title = "Valid title"
        assertThat(PaymentInfoValidator.titleValidator(title)).isTrue()
    }

    @Test
    fun `Validates empty title, returns false`() {
        val title = ""
        assertThat(PaymentInfoValidator.titleValidator(title)).isFalse()
    }

    @Test
    fun `Validates too long title, returns false`() {
        val title = "title".repeat(20)
        assertThat(PaymentInfoValidator.titleValidator(title)).isFalse()
    }

    @Test
    fun `Validates correct description, returns true`() {
        val description = "description"
        assertThat(PaymentInfoValidator.descriptionValidator(description)).isTrue()
    }

    @Test
    fun `Validates empty description, returns false`() {
        val description = ""
        assertThat(PaymentInfoValidator.descriptionValidator(description)).isFalse()
    }

    @Test
    fun `Validates too long description, returns false`() {
        val description = "description".repeat(50)
        assertThat(PaymentInfoValidator.descriptionValidator(description)).isFalse()
    }

    @Test
    fun `Validates correct amount, returns true`() {
        val amount = "21.1263"
        assertThat(PaymentInfoValidator.amountValidator(amount)).isTrue()
    }

    @Test
    fun `Validates too big amount, returns false`() {
        val amount = "211212121212.12"
        assertThat(PaymentInfoValidator.amountValidator(amount)).isFalse()
    }

    @Test
    fun `Validates amount of incorrect format, returns false`() {
        val amount = "not a correct amount"
        assertThat(PaymentInfoValidator.amountValidator(amount)).isFalse()
    }

    @Test
    fun `Validates valid photo quantity, returns true`() {
        assertThat(PaymentInfoValidator.photoQuantityValidator(2, 2)).isTrue()
    }

    @Test
    fun `Validates too many photos, returns false`() {
        assertThat(PaymentInfoValidator.photoQuantityValidator(3, 3)).isFalse()
    }
}