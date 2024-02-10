package com.mateusz113.financemanager.util

class AccountInfoValidator {
    companion object {
        fun emailValidator(
            email: String
        ): Boolean {
            val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            return email.matches(emailRegex) && email.isNotEmpty()
        }

        fun displayNameValidator(
            displayName: String
        ): Boolean {
            return displayName.length < 50 && displayName.isNotEmpty()
        }

        fun passwordValidator(password: String): Boolean {
            return password.length >= 6
        }
    }
}