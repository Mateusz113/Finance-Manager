package com.mateusz113.financemanager.presentation.spendings.spending_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.PaymentListing
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun PaymentsChart(
    listingsMap: Map<Category, List<PaymentListing>>,
    onKeyClick: (PieChartData.Slice) -> Unit
) {
    val slicesList = mutableListOf<PieChartData.Slice>()
    val colorsMap = mapOf(
        Category.Housing to Color.Black,
        Category.Transportation to Color.Blue,
        Category.Groceries to Color.Red,
        Category.Health to Color.Yellow,
        Category.Entertainment to Color.Green,
        Category.Utilities to Color.Gray,
        Category.Personal to Color.Magenta,
        Category.Savings to Color.White,
        Category.Education to Color.LightGray
    )
    val decimalFormat = DecimalFormat("#.##")
    decimalFormat.decimalFormatSymbols =
        DecimalFormatSymbols.getInstance(Locale.ENGLISH)
    Category.values().forEach { category ->
        slicesList.add(
            PieChartData.Slice(
                label = category.name,
                value = decimalFormat
                    .format(listingsMap[category]
                        ?.sumOf { it.amount })
                    .toFloat(),
                color = colorsMap[category] ?: Color.Transparent
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
        backgroundColor = MaterialTheme.colorScheme.background
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