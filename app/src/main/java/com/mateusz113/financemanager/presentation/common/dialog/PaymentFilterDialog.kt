package com.mateusz113.financemanager.presentation.common.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import com.mateusz113.financemanager.R
import com.mateusz113.financemanager.domain.model.FilterSettings
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.presentation.common.option_picker.MultipleOptionsButtonSpinner
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PaymentFilterDialog(
    filterSettings: FilterSettings,
    isDialogOpen: Boolean,
    onFilterSettingsUpdate: (FilterSettings) -> Unit,
    onDismiss: () -> Unit
) {
    if (isDialogOpen) {
        var filterSettingsState by remember { mutableStateOf(filterSettings) }
        val formattedStartDate by remember {
            derivedStateOf {
                DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .format(filterSettingsState.startDate)
            }
        }
        val formattedEndDate by remember {
            derivedStateOf {
                DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .format(filterSettingsState.endDate)
            }
        }
        val startDateDialogState = rememberMaterialDialogState()
        val endDateDialogState = rememberMaterialDialogState()

        val minValueInputIsValid by remember {
            derivedStateOf {
                val minIsLowerIfBothNumbers =
                    if (
                        isValidDoubleInput(filterSettingsState.minValue)
                        && isValidDoubleInput(filterSettingsState.maxValue)
                    ) {
                        filterSettingsState.minValue.toDouble() < filterSettingsState.maxValue.toDouble()
                    } else {
                        true
                    }
                (isValidDoubleInput(filterSettingsState.minValue) && minIsLowerIfBothNumbers)
                        || filterSettingsState.minValue.isEmpty()
            }
        }

        val maxValueInputIsValid by remember {
            derivedStateOf {
                val maxIsBiggerIfBothNumbers = if (
                    isValidDoubleInput(filterSettingsState.minValue) && isValidDoubleInput(filterSettingsState.maxValue)
                ) {
                    filterSettingsState.minValue.toDouble() < filterSettingsState.maxValue.toDouble()
                } else {
                    true
                }
                (isValidDoubleInput(filterSettingsState.maxValue) && maxIsBiggerIfBothNumbers)
                        || filterSettingsState.maxValue.isEmpty()
            }
        }

        //Kotlin release date
        val minStartDate = LocalDate.of(2011, Month.JULY, 22)

        val settingsInnerRowModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
            .height(55.dp)

        Dialog(
            onDismissRequest = {
                //Do not update settings when dialog is dismissed
                onDismiss()
            },
        ) {
            Card(
                modifier = Modifier.sizeIn(
                    minWidth = 280.dp,
                    maxWidth = 560.dp
                ),
                shape = RoundedCornerShape(size = 5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                ConstraintLayout {
                    val (label, labelDivider, settings, buttonDivider, buttons) = createRefs()
                    Text(
                        modifier = Modifier
                            .constrainAs(label) {
                                top.linkTo(parent.top)
                                centerHorizontallyTo(parent)
                            }
                            .padding(top = 15.dp),
                        text = stringResource(id = R.string.filter_settings),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Divider(
                        modifier = Modifier
                            .constrainAs(labelDivider) {
                                top.linkTo(label.bottom)
                                centerHorizontallyTo(parent)
                            }
                            .fillMaxWidth(0.9f)
                            .padding(top = 15.dp)
                    )

                    Column(
                        Modifier
                            .constrainAs(settings) {
                                top.linkTo(labelDivider.bottom)
                                start.linkTo(parent.start)
                            }
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                    ) {
                        MultipleOptionsButtonSpinner(
                            options = Category.values().toList(),
                            selectedOptions = filterSettingsState.categories,
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onOptionSelect = { category ->
                                val newCategoriesList: MutableList<Category?>
                                if (filterSettingsState.categories.contains(category)) {
                                    newCategoriesList = filterSettingsState.categories.toMutableList()
                                    newCategoriesList.remove(category)
                                } else {
                                    newCategoriesList = filterSettingsState.categories.toMutableList()
                                    newCategoriesList.add(category)
                                }
                                filterSettingsState = filterSettingsState.copy(categories = newCategoriesList)
                            },
                            menuOffset = DpOffset(0.dp, 8.dp)
                        )
                        Row(
                            modifier = settingsInnerRowModifier
                        ) {
                            OutlinedTextField(
                                label = {
                                    Text(text = stringResource(id = R.string.min_payment_value))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 2.dp)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(5.dp),
                                value = filterSettingsState.minValue,
                                isError = !minValueInputIsValid,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                onValueChange = { minValue ->
                                    filterSettingsState =
                                        filterSettingsState.copy(minValue = minValue)
                                },
                                singleLine = true
                            )

                            OutlinedTextField(
                                label = {
                                    Text(text = stringResource(id = R.string.max_payment_value))
                                },
                                shape = RoundedCornerShape(5.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(start = 2.dp),
                                value = filterSettingsState.maxValue,
                                isError = !maxValueInputIsValid,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                onValueChange = { maxValue ->
                                    filterSettingsState =
                                        filterSettingsState.copy(maxValue = maxValue)
                                },
                                singleLine = true
                            )
                        }
                        Row(
                            modifier = settingsInnerRowModifier
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 2.dp),
                                shape = RoundedCornerShape(5.dp),
                                label = {
                                    Text(text = "From")
                                },
                                readOnly = true,
                                value = formattedStartDate,
                                onValueChange = {},
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.EditCalendar,
                                        contentDescription = stringResource(
                                            id = R.string.start_date_picker
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                startDateDialogState.show()
                                            }
                                            .size(20.dp)
                                    )
                                }
                            )
                            OutlinedTextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 2.dp),
                                shape = RoundedCornerShape(5.dp),
                                label = {
                                    Text(text = "To")
                                },
                                readOnly = true,
                                value = formattedEndDate,
                                onValueChange = {},
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.EditCalendar,
                                        contentDescription = stringResource(
                                            id = R.string.end_date_picker
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                endDateDialogState.show()
                                            }
                                            .size(20.dp)
                                    )
                                }
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .constrainAs(buttonDivider) {
                                top.linkTo(settings.bottom)
                                centerHorizontallyTo(parent)
                            }
                            .fillMaxWidth(0.9f)
                            .padding(top = 10.dp)
                    )

                    ConstraintLayout(
                        modifier = Modifier
                            .constrainAs(buttons) {
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                                top.linkTo(buttonDivider.bottom)
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                    ) {
                        val (applyRef, cancelRef, resetRef) = createRefs()
                        TextButton(
                            modifier = Modifier
                                .padding(5.dp)
                                .constrainAs(applyRef) {
                                    top.linkTo(parent.top)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(parent.bottom)
                                },
                            onClick = onDismiss,
                        ) {
                            Text(
                                text = stringResource(id = R.string.cancel)
                            )
                        }
                        TextButton(
                            modifier = Modifier
                                .padding(5.dp)
                                .constrainAs(cancelRef) {
                                    top.linkTo(parent.top)
                                    end.linkTo(applyRef.start)
                                    bottom.linkTo(parent.bottom)
                                },
                            onClick = {
                                if (minValueInputIsValid && maxValueInputIsValid) {
                                    val decimalFormat = DecimalFormat("#.##")
                                    decimalFormat.decimalFormatSymbols =
                                        DecimalFormatSymbols.getInstance(Locale.ENGLISH)
                                    try {
                                        val formattedMinVale =
                                            if (filterSettingsState.minValue.isNotEmpty()) {
                                                decimalFormat.format(
                                                    filterSettingsState.minValue.toDouble()
                                                )
                                            } else {
                                                filterSettingsState.minValue
                                            }
                                        val formattedMaxValue =
                                            if (filterSettingsState.maxValue.isNotEmpty()) {
                                                decimalFormat.format(
                                                    filterSettingsState.maxValue.toDouble()
                                                )
                                            } else {
                                                filterSettingsState.maxValue
                                            }
                                        filterSettingsState = filterSettingsState.copy(
                                            minValue = formattedMinVale,
                                            maxValue = formattedMaxValue
                                        )
                                        onFilterSettingsUpdate(filterSettingsState)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                        ) {
                            Text(
                                text = stringResource(id = R.string.apply),
                            )
                        }
                        TextButton(
                            modifier = Modifier
                                .padding(5.dp)
                                .constrainAs(resetRef) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    bottom.linkTo(parent.bottom)
                                },
                            onClick = {
                                onFilterSettingsUpdate(FilterSettings())
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.reset_filter),
                            )
                        }

                    }
                }
            }
        }
        com.mateusz113.financemanager.presentation.common.option_picker.DatePicker(
            datePickerState = startDateDialogState,
            date = filterSettingsState.startDate,
            dateValidator = { date ->
                date > minStartDate
                        && date < filterSettingsState.endDate
            },
            onDateChange = { date ->
                filterSettingsState = filterSettingsState.copy(startDate = date)
            }
        )

        com.mateusz113.financemanager.presentation.common.option_picker.DatePicker(
            datePickerState = endDateDialogState,
            date = filterSettingsState.endDate,
            dateValidator = { date ->
                date > filterSettingsState.startDate
                        && date <= LocalDate.now()
            },
            onDateChange = { date ->
                filterSettingsState = filterSettingsState.copy(endDate = date)
            }
        )
    }
}

private fun isValidDoubleInput(s: String): Boolean {
    return try {
        s.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
