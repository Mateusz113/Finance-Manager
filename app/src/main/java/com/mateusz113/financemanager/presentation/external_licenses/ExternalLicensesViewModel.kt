package com.mateusz113.financemanager.presentation.external_licenses

import androidx.lifecycle.ViewModel
import com.mateusz113.financemanager.domain.enumeration.ExternalLicense
import com.mateusz113.financemanager.domain.enumeration.LicenseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExternalLicensesViewModel @Inject constructor() : ViewModel() {
    private var _state = MutableStateFlow(ExternalLicensesState())
    val state = _state.asStateFlow()

    init {
        _state.update {
            it.copy(externalLicensesMap = LicenseType.values().associateWith { licenseType ->
                ExternalLicense.values()
                    .filter { externalLicense -> externalLicense.licenseType == licenseType }
            })
        }
    }

    fun onEvent(event: ExternalLicensesEvent) {
        when (event) {
            is ExternalLicensesEvent.LicenseDialogStateUpdate -> {
                _state.update {
                    it.copy(isLicenseDialogOpen = event.isOpen)
                }
            }

            is ExternalLicensesEvent.LicenseDialogInfoUpdate -> {
                _state.update {
                    it.copy(licenseText = event.licenseText)
                }
            }
        }
    }
}
