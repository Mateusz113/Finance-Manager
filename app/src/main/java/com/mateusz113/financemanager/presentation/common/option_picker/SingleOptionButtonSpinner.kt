package com.mateusz113.financemanager.presentation.common.option_picker

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.util.TestTags

@Composable
fun <T> SingleOptionButtonSpinner(
    modifier: Modifier = Modifier,
    menuOffset: DpOffset = DpOffset(0.dp, 0.dp),
    selectedOption: T,
    options: List<T>,
    onOptionSelect: (T) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    OutlinedButton(
        modifier = modifier,
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(5.dp)
    ) {
        Text(
            modifier = Modifier.weight(3f),
            text = selectedOption.toString(),
            textAlign = TextAlign.Center
        )
        Icon(
            modifier = Modifier.weight(0.5f),
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = stringResource(id = R.string.option_selection_dropdown)
        )
        DropdownMenu(
            modifier = Modifier
                .heightIn(max = 300.dp)
                .testTag(TestTags.DROPDOWN_MENU),
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
            offset = menuOffset
        ) {
            options.forEach { option ->
                Row {
                    DropdownMenuItem(
                        text = { Text(text = option.toString()) },
                        onClick = {
                            onOptionSelect(option)
                            expanded = false
                        })
                }
            }
        }
    }
}
