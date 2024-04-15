package com.mateusz113.financemanager.presentation.external_licenses

import com.mateusz113.financemanager.domain.enumeration.ExternalLicense
import com.mateusz113.financemanager.domain.enumeration.LicenseType

data class ExternalLicensesState(
    val externalLicensesMap: Map<LicenseType, List<ExternalLicense>> = emptyMap(),
    val licenseText: String = "",
    val isLicenseDialogOpen: Boolean = false
)
