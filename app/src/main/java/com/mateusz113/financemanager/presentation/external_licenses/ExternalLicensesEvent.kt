package com.mateusz113.financemanager.presentation.external_licenses

sealed class ExternalLicensesEvent {
    data class LicenseDialogStateUpdate(val isOpen: Boolean) : ExternalLicensesEvent()
    data class LicenseDialogInfoUpdate(val licenseText: String) : ExternalLicensesEvent()
}
