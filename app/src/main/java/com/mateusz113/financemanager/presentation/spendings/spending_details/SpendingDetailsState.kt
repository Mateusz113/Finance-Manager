package com.mateusz113.financemanager.presentation.spendings.spending_details

import androidx.compose.ui.graphics.Color
import co.yml.charts.ui.piechart.models.PieChartData
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.Currency

data class SpendingDetailsState(
    val listingsMap: Map<Category, List<PaymentListing>> = emptyMap(),
    val currency: Currency = Currency.PLN,
    val isCurrencyPrefix: Boolean? = null,
    val filterSettings: FilterSettings = FilterSettings(),
    val isLoading: Boolean = false,
    val isFilterDialogOpen: Boolean = false,
    val isKeyDialogOpen: Boolean = false,
    val currentSlice: PieChartData.Slice = PieChartData.Slice(
        label = Category.Housing.name,
        value = 0f,
        color = Color.Black
    ),
    val error: String? = null
)