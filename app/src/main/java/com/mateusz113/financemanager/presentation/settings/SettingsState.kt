package com.mateusz113.financemanager.presentation.settings

import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.common.dialog.radio_buttons_dialog.RadioButtonsDialogInfo
import com.mateusz113.financemanager.domain.enumeration.Currency
import com.mateusz113.financemanager.domain.enumeration.SymbolPlacement

data class SettingsState(
    val isDialogOpen: Boolean = false,
    val dialogInfo: RadioButtonsDialogInfo<*> = RadioButtonsDialogInfo(
        label = R.string.unknown,
        currentOption = Currency.PLN,
        optionsLabelsMap = emptyMap(),
        optionsList = listOf()
    ),
    val currentCurrency: Currency = Currency.PLN,
    val currentSymbolPlacement: SymbolPlacement = SymbolPlacement.InAppControl
)