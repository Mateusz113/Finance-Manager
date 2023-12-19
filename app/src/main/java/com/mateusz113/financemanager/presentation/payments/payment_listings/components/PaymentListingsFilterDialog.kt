package com.mateusz113.financemanager.presentation.payments.payment_listings.components

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
import com.mateusz113.financemanager.presentation.common.MultipleOptionsButtonSpinner
import com.mateusz113.financemanager.domain.model.Category
import com.mateusz113.financemanager.presentation.common.DatePicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PaymentListingsFilterDialog(
    currentFilterSettings: FilterSettings,
    isDialogOpen: Boolean,
    dialogOpen: (Boolean) -> Unit,
    updateFilterSettings: (FilterSettings) -> Unit,
) {
    if (isDialogOpen) {
        var filterSettings by remember { mutableStateOf(currentFilterSettings) }
        val formattedStartDate by remember {
            derivedStateOf {
                DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .format(filterSettings.startDate)
            }
        }
        val formattedEndDate by remember {
            derivedStateOf {
                DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .format(filterSettings.endDate)
            }
        }
        val startDateDialogState = rememberMaterialDialogState()
        val endDateDialogState = rememberMaterialDialogState()

        val minValueInputIsValid by remember {
            derivedStateOf {
                val minIsLowerIfBothNumbers =
                    if (
                        isValidFloatInput(filterSettings.minValue)
                        && isValidFloatInput(filterSettings.maxValue)
                    ) {
                        filterSettings.minValue.toFloat() < filterSettings.maxValue.toFloat()
                    } else {
                        true
                    }
                (isValidFloatInput(filterSettings.minValue) && minIsLowerIfBothNumbers)
                        || filterSettings.minValue.isEmpty()
            }
        }

        val maxValueInputIsValid by remember {
            derivedStateOf {
                val maxIsBiggerIfBothNumbers = if (
                    isValidFloatInput(filterSettings.minValue) && isValidFloatInput(filterSettings.maxValue)
                ) {
                    filterSettings.minValue.toFloat() < filterSettings.maxValue.toFloat()
                } else {
                    true
                }
                (isValidFloatInput(filterSettings.maxValue) && maxIsBiggerIfBothNumbers)
                        || filterSettings.maxValue.isEmpty()
            }
        }

        //Kotlin release date
        val minStartDate = LocalDate.of(2011, Month.JULY, 22)

        val settingsInnerRowModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp)
            .height(50.dp)

        Dialog(
            onDismissRequest = {
                //Do not update settings when dialog is dismissed
                dialogOpen(false)
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
                            selectedItems = filterSettings.categories,
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            selectedOption = { category ->
                                val newCategoriesList: MutableList<Category?>
                                if (filterSettings.categories.contains(category)) {
                                    newCategoriesList = filterSettings.categories.toMutableList()
                                    newCategoriesList.remove(category)
                                } else {
                                    newCategoriesList = filterSettings.categories.toMutableList()
                                    newCategoriesList.add(category)
                                }
                                filterSettings = filterSettings.copy(categories = newCategoriesList)
                            },
                            menuOffset = DpOffset(0.dp, 8.dp)
                        )
                        Row(
                            modifier = settingsInnerRowModifier
                        ) {
                            OutlinedTextField(
                                placeholder = {
                                    Text(text = stringResource(id = R.string.min_payment_value))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 2.dp)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(5.dp),
                                value = filterSettings.minValue,
                                isError = !minValueInputIsValid,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                onValueChange = { minValue ->
                                    filterSettings =
                                        filterSettings.copy(minValue = minValue)
                                },
                                singleLine = true
                            )

                            OutlinedTextField(
                                placeholder = {
                                    Text(text = stringResource(id = R.string.max_payment_value))
                                },
                                shape = RoundedCornerShape(5.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(start = 2.dp),
                                value = filterSettings.maxValue,
                                isError = !maxValueInputIsValid,
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                                onValueChange = { maxValue ->
                                    filterSettings =
                                        filterSettings.copy(maxValue = maxValue)
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
                            onClick = { dialogOpen(false) },
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
                                            if (filterSettings.minValue.isNotEmpty()) {
                                                decimalFormat.format(
                                                    filterSettings.minValue.toFloat()
                                                )
                                            } else {
                                                filterSettings.minValue
                                            }
                                        val formattedMaxValue =
                                            if (filterSettings.maxValue.isNotEmpty()) {
                                                decimalFormat.format(
                                                    filterSettings.maxValue.toFloat()
                                                )
                                            } else {
                                                filterSettings.maxValue
                                            }
                                        filterSettings = filterSettings.copy(
                                            minValue = formattedMinVale,
                                            maxValue = formattedMaxValue
                                        )
                                        updateFilterSettings(filterSettings)
                                        dialogOpen(false)
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
                                updateFilterSettings(FilterSettings())
                                dialogOpen(false)
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
        DatePicker(
            datePickerState = startDateDialogState,
            date = filterSettings.startDate,
            dateValidator = {date ->
                date > minStartDate
                        && date < filterSettings.endDate
            },
            onDateChange = { date ->
                filterSettings = filterSettings.copy(startDate = date)
            }
        )

        DatePicker(
            datePickerState = endDateDialogState,
            date = filterSettings.endDate,
            dateValidator = {date ->
                date > filterSettings.startDate
                        && date <= LocalDate.now()
            },
            onDateChange = { date ->
                filterSettings = filterSettings.copy(endDate = date)
            }
        )
    }
}

private fun isValidFloatInput(s: String): Boolean {
    return try {
        s.toFloat()
        true
    } catch (e: NumberFormatException) {
        false
    }
}