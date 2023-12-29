package com.mateusz113.financemanager.presentation.profile

sealed class ProfileEvent {
    data class UpdateConfirmationDialogState(val isOpen: Boolean) : ProfileEvent()
    data class UpdateSignOutDialogState(val isOpen: Boolean) : ProfileEvent()
    data class UpdateDeleteCondition(val shouldDelete: Boolean) : ProfileEvent()
    data class UpdateSignOutCondition(val shouldSignOut: Boolean) : ProfileEvent()
}