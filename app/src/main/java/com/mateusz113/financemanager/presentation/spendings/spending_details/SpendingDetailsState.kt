package com.mateusz113.financemanager.presentation.spendings.spending_details

import androidx.compose.ui.graphics.Color
import co.yml.charts.ui.piechart.models.PieChartData
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.PaymentListing

data class SpendingDetailsState(
    val paymentListings: List<PaymentListing> = emptyList(),
    val listingsMap: Map<Category, List<PaymentListing>> = emptyMap(),
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