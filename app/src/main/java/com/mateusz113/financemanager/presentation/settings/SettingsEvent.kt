package com.mateusz113.financemanager.presentation.settings

sealed class SettingsEvent {
    data class UpdateDialogState(val isOpen: Boolean) : SettingsEvent()
    data class UpdateDialogInfo<T>(
        val label: Int,
        val currentOption: T,
        val optionsLabelsMap: Map<T, Int>,
        val optionsList: List<T>
    ) : SettingsEvent()

    data class UpdateSelectedOption<T>(val selectedOption: T) : SettingsEvent()
}