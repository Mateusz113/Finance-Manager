package com.mateusz113.financemanager.domain.validator

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AccountInfoValidatorTest {
    @Test
    fun `Validate correct email, return true`() {
        val email = "abc@def.ghi"
        assertThat(AccountInfoValidator.emailValidator(email)).isTrue()
    }

    @Test
    fun `Validate email with incorrect format, return false`() {
        val email = "abc"
        assertThat(AccountInfoValidator.emailValidator(email)).isFalse()
    }

    @Test
    fun `Validate empty email, return false`() {
        val email = ""
        assertThat(AccountInfoValidator.emailValidator(email)).isFalse()
    }

    @Test
    fun `Validate correct display name, return true`() {
        val displayName = "display name"
        assertThat(AccountInfoValidator.displayNameValidator(displayName)).isTrue()
    }

    @Test
    fun `Validate empty username, return false`() {
        val displayName = ""
        assertThat(AccountInfoValidator.displayNameValidator(displayName)).isFalse()
    }

    @Test
    fun `Validate too long username, return false`() {
        val displayName =
            "display name,display name,display name,display name,display name,display name"
        assertThat(AccountInfoValidator.displayNameValidator(displayName)).isFalse()
    }

    @Test
    fun `Validate correct password, return true`() {
        val password = "correct password"
        assertThat(AccountInfoValidator.passwordValidator(password)).isTrue()
    }

    @Test
    fun `Validate empty password, return false`() {
        val password = ""
        assertThat(AccountInfoValidator.passwordValidator(password)).isFalse()
    }

    @Test
    fun `Validate too short password, return false`() {
        val password = "pass"
        assertThat(AccountInfoValidator.passwordValidator(password)).isFalse()
    }
}
