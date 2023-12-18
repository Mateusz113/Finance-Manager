package com.mateusz113.financemanager.presentation.payment_addition.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.presentation.common.DatePicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun PaymentAdditionDatePicker(
    modifier: Modifier = Modifier,
    date: LocalDate,
    dateText: String,
    dateChange: (LocalDate) -> Unit
) {
    val datePickerState = rememberMaterialDialogState()

    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.date),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            modifier = Modifier
                .width(160.dp)
                .padding(start = 10.dp),
            shape = RoundedCornerShape(5.dp),
            readOnly = true,
            value = dateText,
            onValueChange = {},
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.EditCalendar,
                    contentDescription = stringResource(
                        id = R.string.date_picker
                    ),
                    modifier = Modifier
                        .clickable {
                            datePickerState.show()
                        }
                        .size(20.dp)
                )
            }
        )
    }
    DatePicker(
        datePickerState = datePickerState,
        date = date,
        dateValidator = { validDate ->
            validDate <= LocalDate.now()
        },
        onDateChange = { newDate ->
            dateChange(newDate)
        }
    )
}