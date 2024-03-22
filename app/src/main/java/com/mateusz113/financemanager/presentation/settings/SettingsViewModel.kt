package com.mateusz113.financemanager.presentation.settings

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mateusz113.financemanager.presentation.common.dialog.radio_buttons_dialog.RadioButtonsDialogInfo
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        sharedPreferences.getString("${userId}Currency", Currency.PLN.name)?.let { code ->
            _state.value = _state.value.copy(
                currentCurrency = Currency.valueOf(code)
            )
        }
        sharedPreferences.getString("${userId}SymbolPlacement", SymbolPlacement.InAppControl.name)
            ?.let { name ->
                _state.value = _state.value.copy(
                    currentSymbolPlacement = SymbolPlacement.valueOf(name)
                )
            }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateDialogState -> {
                _state.value = _state.value.copy(
                    isDialogOpen = event.isOpen
                )
            }

            is SettingsEvent.UpdateDialogInfo<*> -> {
                _state.value = _state.value.copy(
                    dialogInfo = RadioButtonsDialogInfo(
                        label = event.label,
                        currentOption = event.currentOption,
                        optionsLabelsMap = event.optionsLabelsMap,
                        optionsList = event.optionsList
                    )
                )
            }

            is SettingsEvent.UpdateSelectedOption<*> -> {
                when (event.selectedOption) {
                    is Currency -> {
                        updateCurrentCurrency(event.selectedOption)
                    }

                    is SymbolPlacement -> {
                        updateCurrentSymbolPlacement(event.selectedOption)
                    }
                }
            }
        }
    }

    private fun updateCurrentSymbolPlacement(symbolPlacement: SymbolPlacement) {
        _state.value = _state.value.copy(
            currentSymbolPlacement = symbolPlacement
        )
        sharedPreferences.edit().apply {
            this.putString("${userId}SymbolPlacement", symbolPlacement.name)
        }.apply()
    }

    private fun updateCurrentCurrency(currency: Currency) {
        _state.value = _state.value.copy(
            currentCurrency = currency
        )
        sharedPreferences.edit().apply {
            this.putString("${userId}Currency", currency.name)
        }.apply()
    }
}
