package com.mateusz113.financemanager.presentation.spendings.spending_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import co.yml.charts.ui.piechart.models.PieChartData
import com.mateusz113.financemanager.R

@Composable
fun ChartKey(
    modifier: Modifier,
    slice: PieChartData.Slice
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (colorBox, text, arrowIcon) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(colorBox) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(25.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(slice.color)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(5.dp)
                )
        )
        Text(
            modifier = Modifier
                .constrainAs(text) {
                    start.linkTo(colorBox.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    centerVerticallyTo(parent)
                }
                .padding(start = 4.dp),
            text = slice.label
        )
        Icon(
            modifier = Modifier
                .size(20.dp)
                .constrainAs(arrowIcon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    centerVerticallyTo(parent)
                },
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = stringResource(id = R.string.arrow),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}