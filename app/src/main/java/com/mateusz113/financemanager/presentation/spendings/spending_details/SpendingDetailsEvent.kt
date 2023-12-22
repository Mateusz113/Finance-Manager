package com.mateusz113.financemanager.presentation.spendings.spending_details

import co.yml.charts.ui.piechart.models.PieChartData
import com.mateusz113.financemanager.domain.model.FilterSettings

sealed class SpendingDetailsEvent {
    object Refresh : SpendingDetailsEvent()
    data class UpdateFilterDialogState(val isOpen: Boolean) : SpendingDetailsEvent()
    data class UpdateSliceDialogState(val isOpen: Boolean): SpendingDetailsEvent()
    data class UpdateCurrentSlice(val slice: PieChartData.Slice): SpendingDetailsEvent()
    data class UpdateFilterSettings(val filterSettings: FilterSettings) : SpendingDetailsEvent()
    data class SearchForPayment(val query: String) : SpendingDetailsEvent()
}