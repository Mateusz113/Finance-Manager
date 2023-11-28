package com.mateusz113.financemanager.presentation.auth

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)
