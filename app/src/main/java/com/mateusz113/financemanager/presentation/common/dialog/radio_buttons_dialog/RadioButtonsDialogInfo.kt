package com.mateusz113.financemanager.presentation.common.dialog.radio_buttons_dialog

data class RadioButtonsDialogInfo<T>(
    val label: Int,
    val currentOption: T,
    val optionsLabelsMap: Map<out T, Int>,
    val optionsList: List<T>
)
