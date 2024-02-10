package com.mateusz113.financemanager.presentation.auth

data class SignInResult(
    val wasSignInSuccessful: Boolean,
    val errorMessage: String?
)
