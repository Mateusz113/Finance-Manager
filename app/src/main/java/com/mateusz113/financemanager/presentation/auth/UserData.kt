package com.mateusz113.financemanager.presentation.auth

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String?
)