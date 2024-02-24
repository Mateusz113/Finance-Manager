package com.mateusz113.financemanager.presentation.external_licenses

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExternalLicensesViewModel @Inject constructor() : ViewModel() {
    private var _state = MutableStateFlow(ExternalLicensesState())
    val state = _state.asStateFlow()

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
