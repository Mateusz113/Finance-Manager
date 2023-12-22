package com.mateusz113.financemanager.presentation.spendings.spending_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.yml.charts.ui.piechart.models.PieChartData

@Composable
fun ChartKeys(
    slices: List<PieChartData.Slice>,
    onKeyClick: (PieChartData.Slice) -> Unit
) {
    val slicesWithPositiveValue = slices.filter { it.value > 0 }
    val keyModifier = Modifier.width(150.dp)
    for (index in 0..slicesWithPositiveValue.lastIndex step 2) {
        if (index != slicesWithPositiveValue.lastIndex) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChartKey(
                    modifier = keyModifier
                        .clickable { onKeyClick(slicesWithPositiveValue[index]) },
                    slice = slicesWithPositiveValue[index]
                )
                ChartKey(
                    modifier = keyModifier
                        .clickable { onKeyClick(slicesWithPositiveValue[index + 1]) },
                    slice = slicesWithPositiveValue[index + 1]
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChartKey(
                    modifier = keyModifier
                        .clickable { onKeyClick(slicesWithPositiveValue[index]) },
                    slice = slicesWithPositiveValue[index]
                )
                Spacer(modifier = keyModifier)
            }
        }
    }
}