package com.mateusz113.financemanager.presentation.profile

import com.mateusz113.financemanager.presentation.auth.GoogleAuthUiClient

data class ProfileState(
    val googleAuthUiClient: GoogleAuthUiClient? = null,
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val isDeletionConfirmOpen: Boolean = false,
    val isSignOutDialogOpen: Boolean = false,
    val shouldDelete: Boolean = false,
    val shouldSignOut: Boolean = false,
    val userId: String = "",
    val username: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val joinDate: String = "",
    val paymentsNumber: Int = 0
)