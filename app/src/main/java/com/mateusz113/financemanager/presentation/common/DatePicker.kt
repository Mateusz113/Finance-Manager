package com.mateusz113.financemanager.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.time.LocalDate

@Composable
fun DatePicker(
    datePickerState: MaterialDialogState,
    date: LocalDate,
    dateValidator: (LocalDate) -> Boolean,
    onDateChange: (LocalDate) -> Unit
) {
    MaterialDialog(
        dialogState = datePickerState,
        backgroundColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
        buttons = {
            positiveButton(
                text = stringResource(id = R.string.apply),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            negativeButton(
                text = stringResource(id = R.string.cancel),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        this.datepicker(
            initialDate = date,
            title = stringResource(id = R.string.pick_end_date),
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = MaterialTheme.colorScheme.secondary,
                headerTextColor = MaterialTheme.colorScheme.onSecondary,
                calendarHeaderTextColor = MaterialTheme.colorScheme.onBackground,
                dateActiveBackgroundColor = MaterialTheme.colorScheme.secondary,
                dateActiveTextColor = MaterialTheme.colorScheme.onSecondary,
                dateInactiveBackgroundColor = MaterialTheme.colorScheme.background,
                dateInactiveTextColor = MaterialTheme.colorScheme.onBackground
            ),
            allowedDateValidator = { date ->
                dateValidator(date)
            },
            onDateChange = { selectedDate ->
                onDateChange(selectedDate)
            }
        )
    }
}