package com.mateusz113.financemanager.presentation.settings.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.mateusz113.financemanager.R

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    optionLabel: String,
    currentSetting: String,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .clickable { onClick() }
    ) {
        val (text, icon) = createRefs()
        Column(
            modifier = Modifier
                .constrainAs(text) {
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start)
                }
                .padding(start = 20.dp)
        ) {
            Text(
                text = optionLabel,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            )

            Text(
                text = currentSetting,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            )
        }

        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = stringResource(id = R.string.arrow),
            modifier = Modifier
                .constrainAs(icon) {
                    end.linkTo(parent.end)
                    centerVerticallyTo(parent)
                }
                .padding(end = 20.dp)
                .size(28.dp)
        )
    }
}