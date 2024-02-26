package com.mateusz113.financemanager.presentation.spendings.spending_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.PaymentListing
import com.mateusz113.financemanager.util.Currency
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun PaymentsChart(
    listingsMap: Map<Category, List<PaymentListing>>,
    currency: Currency,
    onKeyClick: (PieChartData.Slice) -> Unit
) {
    val slicesList = mutableListOf<PieChartData.Slice>()
    val decimalFormat = DecimalFormat("#.##")
    decimalFormat.decimalFormatSymbols =
        DecimalFormatSymbols.getInstance(Locale.ENGLISH)

    Category.values().forEach { category ->
        val value = decimalFormat
            .format(listingsMap[category]
                ?.sumOf { it.amount } ?: 0.0
            )
            .toFloat()
        slicesList.add(
            PieChartData.Slice(
                label = category.name,
                value = value,
                color = ChartColors.getColorFromCategory(LocalContext.current, category)
            )
        )
    }
    val chartData = PieChartData(
        slices = slicesList,
        plotType = PlotType.Donut
    )
    val chartConfig = PieChartConfig(
        isSumVisible = true,
        isClickOnSliceEnabled = false,
        strokeWidth = 150f,
        isAnimationEnable = true,
        animationDuration = 500,
        labelFontSize = MaterialTheme.typography.headlineMedium.fontSize,
        labelColor = MaterialTheme.colorScheme.onBackground,
        chartPadding = 40,
        backgroundColor = MaterialTheme.colorScheme.background,
        sumUnit = currency.symbol ?: currency.name
    )
    Column {
        DonutPieChart(
            modifier = Modifier
                .fillMaxWidth(),
            pieChartData = chartData,
            pieChartConfig = chartConfig
        )
        ChartKeys(
            slices = chartData.slices,
            onKeyClick = { slice ->
                onKeyClick(slice)
            }
        )
    }
}
