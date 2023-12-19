package com.mateusz113.financemanager.presentation.spendings.spending_details.components

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.domain.model.PaymentListing

@Composable
fun PaymentsChart(
    paymentListings: List<PaymentListing>
) {
    val housingExpenses = paymentListings.filter { it.category == Category.Housing }
    val transportationExpenses = paymentListings.filter { it.category == Category.Transportation }
    val groceryExpenses = paymentListings.filter { it.category == Category.Groceries }
    val healthExpenses = paymentListings.filter { it.category == Category.Health }
    val entertainmentExpenses = paymentListings.filter { it.category == Category.Entertainment }
    val utilExpenses = paymentListings.filter { it.category == Category.Utilities }
    val personalExpenses = paymentListings.filter { it.category == Category.Personal }
    val savingExpenses = paymentListings.filter { it.category == Category.Savings }
    val educationExpenses = paymentListings.filter { it.category == Category.Education }

    val chartData = PieChartData(
        slices = listOf(
            PieChartData.Slice(
                label = stringResource(id = R.string.housing),
                value = housingExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.Black
            ),
            PieChartData.Slice(
                label = stringResource(id = R.string.transportation),
                value = transportationExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.Blue
            ),
            PieChartData.Slice(
                label = stringResource(id = R.string.groceries),
                value = groceryExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.Red
            ),
            PieChartData.Slice(
                label = stringResource(id = R.string.health),
                value = healthExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.Yellow
            ),
            PieChartData.Slice(
                label = stringResource(id = R.string.entertainment),
                value = entertainmentExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.Green
            ),
            PieChartData.Slice(
                label = stringResource(id = R.string.utility),
                value = utilExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.Gray
            ),
            PieChartData.Slice(
                label = stringResource(id = R.string.personal),
                value = personalExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.Magenta
            ),
            PieChartData.Slice(
                label = stringResource(id = R.string.savings),
                value = savingExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.White
            ),
            PieChartData.Slice(
                label = stringResource(id = R.string.education),
                value = educationExpenses.sumOf { it.amount.toDouble() }.toFloat(),
                color = Color.LightGray
            )
        ),
        plotType = PlotType.Donut
    )
    val chartConfig = PieChartConfig(
        isSumVisible = true,
        isClickOnSliceEnabled = true,
        strokeWidth = 150f,
        isAnimationEnable = true,
        animationDuration = 500,
        sliceLabelTextSize = MaterialTheme.typography.titleLarge.fontSize,
        labelFontSize = MaterialTheme.typography.headlineMedium.fontSize,
        labelColor = MaterialTheme.colorScheme.onBackground,
        labelColorType = PieChartConfig.LabelColorType.SLICE_COLOR,
        labelType = PieChartConfig.LabelType.PERCENTAGE,
        labelTypeface = Typeface.DEFAULT,
        chartPadding = 40,
        backgroundColor = MaterialTheme.colorScheme.background,
    )

    Column {
        chartData.slices.forEach { slice ->
            if (slice.value > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(slice.color)
                            .size(30.dp)
                    )
                    Text(text = slice.label)
                }
            }
        }
        DonutPieChart(
            modifier = Modifier
                .fillMaxWidth(),
            pieChartData = chartData,
            pieChartConfig = chartConfig
        )
    }
}