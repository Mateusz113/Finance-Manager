package com.mateusz113.financemanager.presentation.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R

@Composable
fun <T> MultipleOptionsButtonSpinner(
    options: List<T>,
    selectedItems: List<T>,
    modifier: Modifier,
    selectedOption: (T) -> Unit,
    menuOffset: DpOffset
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
            text = if (selectedItems.isNotEmpty()) {
                selectedItems.joinToString { it.toString() }
            } else {
                "Select categories"
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Icon(
            modifier = Modifier.weight(0.5f),
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = stringResource(id = R.string.category_selection_filter)
        )
        DropdownMenu(
            modifier = Modifier.heightIn(max = 300.dp),
            expanded = expanded,
            onDismissRequest = { expanded = !expanded },
            offset = menuOffset
        ) {
            options.forEach { option ->
                Row {
                    Checkbox(
                        checked = selectedItems.contains(option),
                        onCheckedChange = {
                            selectedOption(option)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = option.toString()) },
                        onClick = {
                            selectedOption(option)
                        })
                }
            }
        }
    }
}